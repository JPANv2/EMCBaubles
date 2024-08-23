package jpan.emcbaubles.items.wand.utils;

import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.items.ItemList;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AreaEvaluation {
   public BigInteger totalEMCFill;
   public BigInteger totalEMCReplace;
   public List<BlockPos> blocksFilled = new ArrayList<>();
   public List<BlockPos> blocksReplaced = new ArrayList<>();
   public List<BlockPos> blacklisted = new ArrayList<>();
   public boolean invalid = false;
   public List<ItemStack> irreplaceableDrops = new ArrayList<>();

   public AreaEvaluation() {
      this.totalEMCFill = BigInteger.ZERO;
      this.totalEMCReplace = BigInteger.ZERO;
      this.blocksFilled = new ArrayList<>();
      this.blocksReplaced = new ArrayList<>();
      this.blacklisted = new ArrayList<>();
      this.irreplaceableDrops.clear();
   }

   public void evaluateArea(BlockState toApply, List<BlockPos> toUse, UseOnContext ctx) {
      AreaEvaluation eval = this;
      long blockEMC = toApply.is(Blocks.AIR) ? 0L : EMCHelper.getEmcValue(toApply.getBlock());

      for (BlockPos bp : toUse) {
         if (ctx.getLevel().getBlockState(bp).isAir()) {
            eval.totalEMCFill = eval.totalEMCFill.add(BigInteger.valueOf(blockEMC));
            eval.blocksFilled.add(bp);
         } else if (ItemList.isBlockInList((List<? extends String>)EMCBaubles.serverConfig.wand.replace_blacklist.get(), toApply)) {
            this.blacklisted.add(bp);
         } else {
            for (ItemStack itm : Block.getDrops(
               ctx.getLevel().getBlockState(bp), (ServerLevel)ctx.getLevel(), bp, ctx.getLevel().getBlockEntity(bp), ctx.getPlayer(), ctx.getPlayer().getInventory().getSelected()
            )) {
               if (EMCHelper.getEmcSellValue(itm) > 0L) {
                  eval.totalEMCReplace = eval.totalEMCReplace
                     .subtract(BigInteger.valueOf(EMCHelper.getEmcSellValue(itm)).multiply(BigInteger.valueOf((long)itm.getCount())));
               } else {
                  eval.irreplaceableDrops.add(itm);
               }
            }

            eval.totalEMCReplace = eval.totalEMCReplace.add(BigInteger.valueOf(blockEMC));
            eval.blocksReplaced.add(bp);
         }
      }
   }

   public void evaluateAreaForPickedItem(ItemStack toApply, List<BlockPos> toUse, UseOnContext ctx) {
      AreaEvaluation eval = this;
      long blockEMC = EMCHelper.getEmcValue(toApply);
      IKnowledgeProvider provider = (IKnowledgeProvider)ctx.getPlayer().getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).orElse(null);
      if (provider == null) {
         this.invalid = true;
      } else {
         long usedUpEMC = 0L;
         this.irreplaceableDrops.add(toApply);
         ItemStack wand = ctx.getPlayer().getItemInHand(ctx.getHand());

         for (BlockPos bp : toUse) {
            ItemStack afterApplied = toApply.copy();
            ctx.getPlayer().setItemInHand(ctx.getHand(), afterApplied);
            UseOnContext useCtx = new UseOnContext(ctx.getPlayer(), ctx.getHand(), new BlockHitResult(ctx.getClickLocation(), ctx.getClickedFace(), bp, ctx.isInside()));
            afterApplied.getItem().useOn(useCtx);
            if (ItemStack.isSameItemSameTags(toApply, ctx.getPlayer().getItemInHand(ctx.getHand()))) {
               afterApplied.use(ctx.getLevel(), ctx.getPlayer(), ctx.getHand());
            }

            if (ItemStack.isSameItemSameTags(toApply, ctx.getPlayer().getItemInHand(ctx.getHand()))) {
               afterApplied.getItem().onItemUseFirst(ctx.getPlayer().getItemInHand(ctx.getHand()), useCtx);
            }

            if (!ItemStack.isSameItemSameTags(toApply, ctx.getPlayer().getItemInHand(ctx.getHand()))) {
               LogUtils.getLogger().debug("Applied " + blockEMC + " - " + EMCHelper.getEmcValue(ctx.getPlayer().getItemInHand(ctx.getHand())));
               usedUpEMC = blockEMC - EMCHelper.getEmcValue(ctx.getPlayer().getItemInHand(ctx.getHand()));
               eval.totalEMCReplace = eval.totalEMCReplace.add(BigInteger.valueOf(usedUpEMC));
               this.blocksReplaced.add(bp);
               if (provider.getEmc().compareTo(eval.totalEMCReplace) <= 0) {
                  provider.setEmc(BigInteger.ZERO);
                  ctx.getPlayer().setItemInHand(ctx.getHand(), wand);
                  return;
               }
            }
         }

         if (provider.getEmc().compareTo(eval.totalEMCReplace) > 0) {
            provider.setEmc(provider.getEmc().subtract(eval.totalEMCReplace));
            ctx.getPlayer().setItemInHand(ctx.getHand(), wand);
         } else {
            ctx.getPlayer().setItemInHand(ctx.getHand(), wand);
         }
      }
   }
}
