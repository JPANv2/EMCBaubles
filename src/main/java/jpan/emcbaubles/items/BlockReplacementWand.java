package jpan.emcbaubles.items;

import com.mojang.logging.LogUtils;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import jpan.emcbaubles.items.gui.WandContainerMenu;
import jpan.emcbaubles.items.wand.utils.AreaEvaluation;
import jpan.emcbaubles.items.wand.utils.AreaPickedApplier;
import jpan.emcbaubles.items.wand.utils.FullCircleAreaPickedApplier;
import jpan.emcbaubles.items.wand.utils.FullSquareAreaPickedApplier;
import jpan.emcbaubles.items.wand.utils.FullTriangleAreaPickedApplier;
import jpan.emcbaubles.items.wand.utils.LineAreaPickedApplier;
import jpan.emcbaubles.items.wand.utils.OpenCircleAreaPickedApplier;
import jpan.emcbaubles.items.wand.utils.OpenSquareAreaPickedApplier;
import jpan.emcbaubles.items.wand.utils.OpenTriangleAreaPickedApplier;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import moze_intel.projecte.capability.BasicItemCapability;
import moze_intel.projecte.capability.ModeChangerItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.items.IBarHelper;
import moze_intel.projecte.gameObjs.items.IItemMode;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.ILangEntry;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockReplacementWand extends ItemPE implements IItemMode, IItemCharge, IBarHelper {
   public static ArrayList<AreaPickedApplier> appliedShapes = new ArrayList<>();
   private final ILangEntry[] modes = new ILangEntry[]{
      new BlockReplacementWand.SimpleKey("emcb.wand.mode.0.name"),
      new BlockReplacementWand.SimpleKey("emcb.wand.mode.1.name"),
      new BlockReplacementWand.SimpleKey("emcb.wand.mode.2.name"),
      new BlockReplacementWand.SimpleKey("emcb.wand.mode.3.name"),
      new BlockReplacementWand.SimpleKey("emcb.wand.mode.4.name"),
      new BlockReplacementWand.SimpleKey("emcb.wand.mode.5.name")
   };

   public BlockReplacementWand() {
      super(new Properties().stacksTo(1));
      this.addItemCapability(BlockReplacementWand.WandItemCapabilityWrapper::new);
      this.addItemCapability(ModeChangerItemCapabilityWrapper::new);
   }

   public ILangEntry[] getModeLangEntries() {
      return this.modes;
   }

   public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltips, @NotNull TooltipFlag flags) {
      super.appendHoverText(stack, level, tooltips, flags);
      tooltips.add(this.getToolTip(stack));
   }

   public boolean isBarVisible(@NotNull ItemStack stack) {
      return true;
   }

   public float getWidthForBar(ItemStack stack) {
      return 1.0F - this.getChargePercent(stack);
   }

   public int getBarWidth(@NotNull ItemStack stack) {
      return this.getScaledBarWidth(stack);
   }

   public int getNumCharges(@NotNull ItemStack stack) {
      return stack.getOrCreateTag().contains("wand_mode") && (stack.getTag().getInt("wand_mode") & 4) == 4 ? 3 : 2;
   }

   public byte getMode(@NotNull ItemStack stack) {
      return stack.getOrCreateTag().contains("wand_mode") ? (byte)(stack.getTag().getInt("wand_mode") >> 3 & 7) : 0;
   }

   public byte getShape(@NotNull ItemStack stack) {
      return stack.getOrCreateTag().contains("wand_mode") ? (byte)(stack.getTag().getInt("wand_mode") >> 6 & 7) : 0;
   }

   public boolean getUseThird(@NotNull ItemStack stack) {
      return stack.getOrCreateTag().contains("wand_mode") ? (stack.getTag().getInt("wand_mode") & 4) == 4 : false;
   }

   public boolean getUseRadius(@NotNull ItemStack stack) {
      return stack.getOrCreateTag().contains("wand_mode") ? (stack.getTag().getInt("wand_mode") & 2) == 2 : false;
   }

   public boolean changeMode(@NotNull Player player, @NotNull ItemStack stack, InteractionHand hand) {
      int mode = this.getMode(stack);
      mode = (this.getMode(stack) + 1) % this.modes.length;
      if (!stack.getOrCreateTag().contains("wand_mode")) {
         stack.getOrCreateTag().putInt("wand_mode", 1 + (mode << 3));
      } else {
         stack.getOrCreateTag()
            .putInt("wand_mode", (stack.getOrCreateTag().getInt("wand_mode") & 7) + (mode << 3) + (stack.getOrCreateTag().getInt("wand_mode") >> 6 << 6));
      }

      player.sendSystemMessage(this.getModeSwitchEntry().translate(new Object[]{this.getModeLangEntry(stack)}));
      return true;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return stack.isEnchanted() && stack.getAllEnchantments().containsKey(enchantment)
         ? false
         : enchantment.equals(Enchantments.BLOCK_FORTUNE) && !stack.getAllEnchantments().containsKey(enchantment);
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      for (Enchantment e : EnchantmentHelper.getEnchantments(book).keySet()) {
         if (!this.canApplyAtEnchantingTable(stack, e)) {
            return false;
         }
      }

      return true;
   }

   public boolean m_8120_(ItemStack stack) {
      return false;
   }

   public int getEnchantmentValue(ItemStack stack) {
      return 30;
   }

   public int m_142159_(@NotNull ItemStack stack) {
      return this.getNumCharges(stack) == this.getCharge(stack) ? -1 : this.getColorForBar(stack);
   }

   public int getNextPointToDefine(ItemStack itm, boolean ignoreThird) {
      if (!itm.getOrCreateTag().contains("block1_pos")) {
         return 1;
      } else if (!itm.getTag().contains("block2_pos")) {
         return 2;
      } else if (!ignoreThird && this.getUseThird(itm)) {
         return !itm.getTag().contains("block3_pos") ? 3 : -1;
      } else {
         return -1;
      }
   }

   public void setNextPoint(ItemStack itm, BlockPos pos, boolean ignoreThird) {
      int pnt = this.getNextPointToDefine(itm, ignoreThird);
      int[] posA = new int[]{pos.getX(), pos.getY(), pos.getZ()};
      switch (pnt) {
         case 1:
            itm.getOrCreateTag().putIntArray("block1_pos", posA);
            return;
         case 2:
            itm.getOrCreateTag().putIntArray("block2_pos", posA);
            return;
         case 3:
            itm.getOrCreateTag().putIntArray("block3_pos", posA);
            return;
      }
   }

   public void resetAllPoints(ItemStack itm) {
      if (itm.getOrCreateTag().contains("block1_pos")) {
         itm.getOrCreateTag().remove("block1_pos");
      }

      if (itm.getOrCreateTag().contains("block2_pos")) {
         itm.getOrCreateTag().remove("block2_pos");
      }

      if (itm.getOrCreateTag().contains("block3_pos")) {
         itm.getOrCreateTag().remove("block3_pos");
      }
   }

   public BlockPos getPointForIndex(ItemStack itm, int idx) {
      switch (idx) {
         case 1:
            if (itm.getOrCreateTag().contains("block1_pos")) {
               return new BlockPos(
                  itm.getOrCreateTag().getIntArray("block1_pos")[0], itm.getOrCreateTag().getIntArray("block1_pos")[1], itm.getOrCreateTag().getIntArray("block1_pos")[2]
               );
            }

            return null;
         case 2:
            if (itm.getOrCreateTag().contains("block2_pos")) {
               return new BlockPos(
                  itm.getOrCreateTag().getIntArray("block2_pos")[0], itm.getOrCreateTag().getIntArray("block2_pos")[1], itm.getOrCreateTag().getIntArray("block2_pos")[2]
               );
            }

            return null;
         case 3:
            if (itm.getOrCreateTag().contains("block3_pos")) {
               return new BlockPos(
                  itm.getOrCreateTag().getIntArray("block3_pos")[0], itm.getOrCreateTag().getIntArray("block3_pos")[1], itm.getOrCreateTag().getIntArray("block3_pos")[2]
               );
            }

            return null;
         default:
            return null;
      }
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
      return InteractionResultHolder.success(player.getItemInHand(hand));
   }

   @Nonnull
   public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
      if (ctx.getLevel().isClientSide) {
         return InteractionResult.PASS;
      } else if (this.getMode(stack) == 0) {
         this.pickThisBlock(stack, ctx);
         return InteractionResult.SUCCESS;
      } else {
         IKnowledgeProvider provider = (IKnowledgeProvider)ctx.getPlayer().getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, null).orElse(null);
         if (provider == null) {
            return InteractionResult.FAIL;
         } else if (appliedShapes.size() > this.getShape(stack)) {
            int pnt = this.getNextPointToDefine(stack, !appliedShapes.get(this.getShape(stack)).allowsThirdPoint());
            if (pnt == -1) {
               switch (this.getMode(stack)) {
                  case 1: {
                     AreaEvaluation eval = appliedShapes.get(this.getShape(stack))
                        .applyBlockFillAndOrReplace(stack, Blocks.AIR.defaultBlockState(), ctx, true, true);
                     provider.setEmc(provider.getEmc().subtract(eval.totalEMCFill));
                     provider.setEmc(provider.getEmc().subtract(eval.totalEMCReplace));
                     this.sendEMCChangeMessage(
                        ctx.getPlayer(), this.getMode(stack), eval.totalEMCFill.add(eval.totalEMCReplace), eval.blocksReplaced.size() + ""
                     );
                     break;
                  }
                  case 2: {
                     AreaEvaluation eval = appliedShapes.get(this.getShape(stack)).applyBlockFill(stack, ctx);
                     provider.setEmc(provider.getEmc().subtract(eval.totalEMCFill));
                     this.sendEMCChangeMessage(ctx.getPlayer(), this.getMode(stack), eval.totalEMCFill, eval.blocksFilled.size() + "");
                     break;
                  }
                  case 3: {
                     AreaEvaluation eval = appliedShapes.get(this.getShape(stack)).applyBlockReplace(stack, ctx);
                     provider.setEmc(provider.getEmc().subtract(eval.totalEMCReplace));
                     this.sendEMCChangeMessage(ctx.getPlayer(), this.getMode(stack), eval.totalEMCReplace, eval.blocksReplaced.size() + "");
                     break;
                  }
                  case 4: {
                     AreaEvaluation eval = appliedShapes.get(this.getShape(stack)).applyBlockFillAndReplace(stack, ctx);
                     provider.setEmc(provider.getEmc().subtract(eval.totalEMCFill));
                     provider.setEmc(provider.getEmc().subtract(eval.totalEMCReplace));
                     this.sendEMCChangeMessage(
                        ctx.getPlayer(),
                        this.getMode(stack),
                        eval.totalEMCFill.add(eval.totalEMCReplace),
                        eval.blocksFilled.size() + "",
                        eval.blocksReplaced.size() + ""
                     );
                     break;
                  }
                  case 5: {
                     AreaEvaluation eval = appliedShapes.get(this.getShape(stack)).applyPickedApplier(stack, ctx);
                     this.sendEMCChangeMessage(
                        ctx.getPlayer(),
                        this.getMode(stack),
                        eval.totalEMCReplace,
                        eval.irreplaceableDrops.get(0).getDisplayName().getString(),
                        eval.blocksReplaced.size() + ""
                     );
                     break;
                  }
                  default:
                     return InteractionResult.PASS;
               }

               this.resetAllPoints(stack);
               return InteractionResult.SUCCESS;
            } else {
               int next = this.getNextPointToDefine(stack, !appliedShapes.get(this.getShape(stack)).allowsThirdPoint());
               this.setNextPoint(stack, ctx.getClickedPos(), !appliedShapes.get(this.getShape(stack)).allowsThirdPoint());
               AreaPickedApplier.sendMessage(
                  ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.start.selected." + pnt, new Object[]{this.getPointForIndex(stack, pnt)})
               );
               if (this.getNextPointToDefine(stack, !appliedShapes.get(this.getShape(stack)).allowsThirdPoint()) == -1) {
                  AreaPickedApplier.sendMessage(
                     ctx.getPlayer(),
                     TextComponentUtil.smartTranslate(
                        "emcb.wand.start.selected.keeps",
                        new Object[]{TextComponentUtil.smartTranslate(this.modes[this.getMode(stack)].getTranslationKey(), new Object[0])}
                     )
                  );
               }

               return InteractionResult.SUCCESS;
            }
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   private void sendEMCChangeMessage(Player player, int msgNum, BigInteger total, String str1, String str2) {
      if (total.equals(BigInteger.ZERO)) {
         AreaPickedApplier.sendMessage(player, TextComponentUtil.smartTranslate("emcb.wand.mode." + msgNum + ".result.neutral", new Object[]{str1, str2}));
      } else if (total.signum() < 0) {
         AreaPickedApplier.sendMessage(
            player, TextComponentUtil.smartTranslate("emcb.wand.mode." + msgNum + ".result.gain", new Object[]{str1, str2, total.negate().toString()})
         );
      } else {
         AreaPickedApplier.sendMessage(
            player, TextComponentUtil.smartTranslate("emcb.wand.mode." + msgNum + ".result.loss", new Object[]{str1, str2, total.toString()})
         );
      }
   }

   private void sendEMCChangeMessage(Player player, int msgNum, BigInteger total, String str1) {
      if (total.equals(BigInteger.ZERO)) {
         AreaPickedApplier.sendMessage(player, TextComponentUtil.smartTranslate("emcb.wand.mode." + msgNum + ".result.neutral", new Object[]{str1}));
      } else if (total.signum() < 0) {
         AreaPickedApplier.sendMessage(
            player, TextComponentUtil.smartTranslate("emcb.wand.mode." + msgNum + ".result.gain", new Object[]{str1, total.negate().toString()})
         );
      } else {
         AreaPickedApplier.sendMessage(
            player, TextComponentUtil.smartTranslate("emcb.wand.mode." + msgNum + ".result.loss", new Object[]{str1, total.toString()})
         );
      }
   }

   public void pickThisBlock(ItemStack stack, UseOnContext ctx) {
      BlockState picked = ctx.getLevel().getBlockState(ctx.getClickedPos());
      if (picked != null && EMCHelper.doesItemHaveEmc(picked.getBlock())) {
         stack.getOrCreateTag().putIntArray("picked_block", new int[]{ctx.getClickedPos().getX(), ctx.getClickedPos().getY(), ctx.getClickedPos().getZ()});
         IKnowledgeProvider provider = (IKnowledgeProvider)ctx.getPlayer().getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, null).orElse(null);
         if (provider != null) {
            if (provider.addKnowledge(new ItemStack(picked.getBlock()))) {
               AreaPickedApplier.sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.learned.block", new Object[]{picked.getBlock().getName()}));
            } else {
               AreaPickedApplier.sendMessage(
                  ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.selected.block", new Object[]{picked.getBlock().getName()})
               );
            }
         }
      } else {
         AreaPickedApplier.sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.cannot_learn.block", new Object[]{picked.getBlock().getName()}));
      }
   }

   static {
      appliedShapes.add(new FullSquareAreaPickedApplier());
      appliedShapes.add(new OpenSquareAreaPickedApplier());
      appliedShapes.add(new FullCircleAreaPickedApplier());
      appliedShapes.add(new OpenCircleAreaPickedApplier());
      appliedShapes.add(new FullTriangleAreaPickedApplier());
      appliedShapes.add(new OpenTriangleAreaPickedApplier());
      appliedShapes.add(new LineAreaPickedApplier());
   }

   static class SimpleKey implements ILangEntry {
      String key;

      public SimpleKey(String k) {
         this.key = k;
      }

      public String getTranslationKey() {
         return this.key;
      }
   }

   public static class WandItemCapabilityWrapper extends BasicItemCapability<IItemCharge> implements IItemCharge {
      public Capability<IItemCharge> getCapability() {
         return PECapabilities.CHARGE_ITEM_CAPABILITY;
      }

      public boolean changeCharge(@NotNull Player player, @NotNull ItemStack stack, @Nullable InteractionHand hand) {
         LogUtils.getLogger().debug("Changing charge ->" + player.level().isClientSide);
         if (!player.level().isClientSide) {
            NetworkHooks.openScreen(
               (ServerPlayer)player, new BlockReplacementWand.WandItemCapabilityWrapper.ContainerProvider(player.getItemInHand(hand), hand), buf -> {
                  buf.writeEnum(hand);
                  buf.writeByte(player.getInventory().selected);
                  buf.writeBoolean(false);
               }
            );
            return true;
         } else {
            return true;
         }
      }

      public int getNumCharges(@NotNull ItemStack stack) {
         return stack.getOrCreateTag().contains("wand_mode") && (stack.getTag().getInt("wand_mode") & 4) == 4 ? 3 : 2;
      }

      public int getCharge(@NotNull ItemStack stack) {
         int tot = stack.getOrCreateTag().contains("block1_pos") ? 1 : 0;
         if (stack.getOrCreateTag().contains("block2_pos")) {
            tot++;
         }

         if (stack.getOrCreateTag().contains("wand_mode") && (stack.getTag().getInt("wand_mode") & 4) == 4 && stack.getOrCreateTag().contains("block3_pos")) {
            tot++;
         }

         return tot;
      }

      private class ContainerProvider implements MenuProvider {
         private final ItemStack stack;
         private final InteractionHand hand;

         private ContainerProvider(ItemStack stack, InteractionHand hand) {
            this.stack = stack;
            this.hand = hand;
         }

         @NotNull
         public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
            return new WandContainerMenu(windowId, playerInventory, this.hand, new ItemStackHandler(5), playerInventory.selected, false);
         }

         @NotNull
         public Component getDisplayName() {
            return this.stack.getDisplayName();
         }
      }
   }
}
