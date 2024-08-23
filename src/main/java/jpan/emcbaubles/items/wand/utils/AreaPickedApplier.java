package jpan.emcbaubles.items.wand.utils;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import jpan.emcbaubles.items.BlockReplacementWand;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface AreaPickedApplier {
   default List<BlockPos> getWandBasePositions(ItemStack wand, UseOnContext ctx) {
      if (!wand.getOrCreateTag().contains("block1_pos")) {
         sendMessage(ctx.getPlayer(), TextComponentUtil.translate("emcb.wand.no_block1", new Object[0]));
         return null;
      } else {
         BlockPos b1 = new BlockPos(
            wand.getOrCreateTag().getIntArray("block1_pos")[0], wand.getOrCreateTag().getIntArray("block1_pos")[1], wand.getOrCreateTag().getIntArray("block1_pos")[2]
         );
         if (!wand.getOrCreateTag().contains("block2_pos")) {
            sendMessage(ctx.getPlayer(), TextComponentUtil.translate("emcb.wand.no_block2", new Object[0]));
            return null;
         } else {
            BlockPos b2 = new BlockPos(
               wand.getOrCreateTag().getIntArray("block2_pos")[0], wand.getOrCreateTag().getIntArray("block2_pos")[1], wand.getOrCreateTag().getIntArray("block2_pos")[2]
            );
            ArrayList<BlockPos> ans = new ArrayList<>();
            ans.add(b1);
            ans.add(b2);
            if (this.needsThirdPoint()) {
               if (!wand.getOrCreateTag().contains("block3_pos")) {
                  sendMessage(ctx.getPlayer(), TextComponentUtil.translate("emcb.wand.no_block3", new Object[0]));
                  return null;
               }

               BlockPos b3 = new BlockPos(
                  wand.getOrCreateTag().getIntArray("block3_pos")[0], wand.getOrCreateTag().getIntArray("block3_pos")[1], wand.getOrCreateTag().getIntArray("block3_pos")[2]
               );
               ans.add(b3);
            } else if (this.allowsThirdPoint() && wand.getOrCreateTag().contains("block3_pos")) {
               BlockPos b3 = new BlockPos(
                  wand.getOrCreateTag().getIntArray("block3_pos")[0], wand.getOrCreateTag().getIntArray("block3_pos")[1], wand.getOrCreateTag().getIntArray("block3_pos")[2]
               );
               ans.add(b3);
            }

            return this.applyRadius(ans, ((BlockReplacementWand)wand.getItem()).getUseRadius(wand));
         }
      }
   }

   boolean needsThirdPoint();

   boolean allowsThirdPoint();

   default List<BlockPos> applyRadius(List<BlockPos> pos, boolean isRadius) {
      if (!isRadius) {
         return pos;
      } else {
         ArrayList<BlockPos> ans = new ArrayList<>();
         Vec3i vector = new Vec3i(pos.get(0).getX(), pos.get(0).getY(), pos.get(0).getZ());
         Vec3i vector2 = new Vec3i(pos.get(1).getX(), pos.get(1).getY(), pos.get(1).getZ());
         Vec3i offset = vector2.subtract(vector);
         offset = offset.multiply(-1);
         vector = vector.offset(offset);
         LogUtils.getLogger().debug("p2:" + pos.get(1));
         LogUtils.getLogger().debug("p1:" + vector);
         ans.add(new BlockPos(vector));
         ans.add(pos.get(1));
         return ans;
      }
   }

   default AreaEvaluation applyBlockFill(ItemStack wand, UseOnContext ctx) {
      if (wand == null) {
         sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.no_wand", new Object[0]));
         return null;
      } else if (wand.getOrCreateTag().contains("picked_block")) {
         BlockPos p1 = new BlockPos(
            wand.getOrCreateTag().getIntArray("picked_block")[0], wand.getOrCreateTag().getIntArray("picked_block")[1], wand.getOrCreateTag().getIntArray("picked_block")[2]
         );
         return this.applyBlockFillAndOrReplace(wand, ctx.getLevel().getBlockState(p1), ctx, true, false);
      } else {
         if (wand.getOrCreateTag().contains("picked_item")) {
            ItemStack picked = ItemStack.of(wand.getOrCreateTag().getCompound("picked_item"));
            if (picked.getItem() instanceof BlockItem) {
               BlockState bs = (BlockState)((BlockItem)picked.getItem()).getBlock().getStateDefinition().getPossibleStates().stream().findFirst().orElse(null);
               if (bs != null) {
                  return this.applyBlockFillAndOrReplace(wand, bs, ctx, true, false);
               }

               sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.invalid_pick", new Object[0]));
               return null;
            }
         }

         sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.nothing_picked", new Object[0]));
         return null;
      }
   }

   default AreaEvaluation applyBlockFillAndReplace(ItemStack wand, UseOnContext ctx) {
      if (wand == null) {
         sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.no_wand", new Object[0]));
         return null;
      } else if (wand.getOrCreateTag().contains("picked_block")) {
         BlockPos p1 = new BlockPos(
            wand.getOrCreateTag().getIntArray("picked_block")[0], wand.getOrCreateTag().getIntArray("picked_block")[1], wand.getOrCreateTag().getIntArray("picked_block")[2]
         );
         return this.applyBlockFillAndOrReplace(wand, ctx.getLevel().getBlockState(p1), ctx, true, true);
      } else {
         if (wand.getOrCreateTag().contains("picked_item")) {
            ItemStack picked = ItemStack.of(wand.getOrCreateTag().getCompound("picked_item"));
            if (picked.getItem() instanceof BlockItem) {
               BlockState bs = (BlockState)((BlockItem)picked.getItem()).getBlock().getStateDefinition().getPossibleStates().stream().findFirst().orElse(null);
               if (bs != null) {
                  return this.applyBlockFillAndOrReplace(wand, bs, ctx, true, true);
               }

               sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.invalid_pick", new Object[0]));
               return null;
            }
         }

         sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.nothing_picked", new Object[0]));
         return null;
      }
   }

   default AreaEvaluation applyErase(ItemStack wand, UseOnContext ctx) {
      if (wand == null) {
         sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.no_wand", new Object[0]));
         return null;
      } else {
         return this.applyBlockFillAndOrReplace(wand, Blocks.AIR.defaultBlockState(), ctx, true, true);
      }
   }

   default AreaEvaluation applyBlockReplace(ItemStack wand, UseOnContext ctx) {
      if (wand == null) {
         return null;
      } else if (wand.getOrCreateTag().contains("picked_block")) {
         BlockPos p1 = new BlockPos(
            wand.getOrCreateTag().getIntArray("picked_block")[0], wand.getOrCreateTag().getIntArray("picked_block")[1], wand.getOrCreateTag().getIntArray("picked_block")[2]
         );
         return this.applyBlockFillAndOrReplace(wand, ctx.getLevel().getBlockState(p1), ctx, false, true);
      } else {
         if (wand.getOrCreateTag().contains("picked_item")) {
            ItemStack picked = ItemStack.of(wand.getOrCreateTag().getCompound("picked_item"));
            if (picked.getItem() instanceof BlockItem) {
               BlockState bs = (BlockState)((BlockItem)picked.getItem()).getBlock().getStateDefinition().getPossibleStates().stream().findFirst().orElse(null);
               if (bs != null) {
                  return this.applyBlockFillAndOrReplace(wand, bs, ctx, false, true);
               }

               sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.invalid_pick", new Object[0]));
               return null;
            }
         }

         sendMessage(ctx.getPlayer(), TextComponentUtil.smartTranslate("emcb.wand.nothing_picked", new Object[0]));
         return null;
      }
   }

   default AreaEvaluation applyBlockFillAndOrReplace(ItemStack wand, BlockState toApply, UseOnContext ctx, boolean doesFill, boolean doesReplace) {
      List<BlockPos> toUse = this.getWandBasePositions(wand, ctx);
      if (toUse == null) {
         return null;
      } else {
         Level world = ctx.getLevel();
         Player p = ctx.getPlayer();
         toUse = this.getAreaPoints(wand, ctx, toUse);
         if (toApply.is(Blocks.AIR)) {
            long var10000 = 0L;
         } else {
            EMCHelper.getEmcValue(toApply.getBlock());
         }

         IKnowledgeProvider provider = (IKnowledgeProvider)p.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).orElse(null);
         if (provider == null) {
            return null;
         } else {
            AreaEvaluation eval = new AreaEvaluation();
            eval.evaluateArea(toApply, toUse, ctx);
            if (doesFill && doesReplace) {
               if (provider.getEmc().compareTo(eval.totalEMCFill.add(eval.totalEMCReplace)) < 0) {
                  sendMessage(
                     ctx.getPlayer(),
                     TextComponentUtil.translate(
                        "emcb.wand.not_enough_emc", new Object[]{eval.totalEMCFill.add(eval.totalEMCReplace).subtract(provider.getEmc()).toString()}
                     )
                  );
                  return null;
               }

               for (BlockPos p1 : eval.blocksFilled) {
                  ctx.getLevel().setBlockAndUpdate(p1, toApply);
               }

               for (BlockPos p1 : eval.blocksReplaced) {
                  ctx.getLevel().setBlockAndUpdate(p1, toApply);
               }

               for (ItemStack itm : eval.irreplaceableDrops) {
                  p.spawnAtLocation(itm);
               }
            } else if (doesFill) {
               if (provider.getEmc().compareTo(eval.totalEMCFill) < 0) {
                  sendMessage(
                     ctx.getPlayer(),
                     TextComponentUtil.translate("emcb.wand.not_enough_emc", new Object[]{eval.totalEMCFill.subtract(provider.getEmc()).toString()})
                  );
                  return null;
               }

               for (BlockPos p1 : eval.blocksFilled) {
                  ctx.getLevel().setBlockAndUpdate(p1, toApply);
               }
            } else if (doesReplace) {
               if (provider.getEmc().compareTo(eval.totalEMCReplace) < 0) {
                  sendMessage(
                     ctx.getPlayer(),
                     TextComponentUtil.translate("emcb.wand.not_enough_emc", new Object[]{eval.totalEMCReplace.subtract(provider.getEmc()).toString()})
                  );
                  return null;
               }

               for (BlockPos p1 : eval.blocksReplaced) {
                  ctx.getLevel().setBlockAndUpdate(p1, toApply);
               }

               for (ItemStack itm : eval.irreplaceableDrops) {
                  p.spawnAtLocation(itm);
               }
            }

            return eval;
         }
      }
   }

   List<BlockPos> getAreaPoints(ItemStack var1, UseOnContext var2, List<BlockPos> var3);

   default AreaEvaluation applyPickedApplier(ItemStack wand, UseOnContext ctx) {
      if (!wand.getOrCreateTag().contains("picked_item") && !wand.getOrCreateTag().contains("picked_block")) {
         sendMessage(ctx.getPlayer(), TextComponentUtil.translate("emcb.wand.nothing_picked", new Object[0]));
         return null;
      } else {
         ItemStack toApply;
         if (wand.getOrCreateTag().contains("picked_item")) {
            toApply = ItemStack.of(wand.getOrCreateTag().getCompound("picked_item"));
         } else {
            BlockPos p1 = new BlockPos(
               wand.getOrCreateTag().getIntArray("picked_block")[0], wand.getOrCreateTag().getIntArray("picked_block")[1], wand.getOrCreateTag().getIntArray("picked_block")[2]
            );
            toApply = new ItemStack(ctx.getLevel().getBlockState(p1).getBlock());
         }

         List<BlockPos> toUse = this.getWandBasePositions(wand, ctx);
         if (toUse == null) {
            return null;
         } else {
            toUse = this.getAreaPoints(wand, ctx, toUse);
            AreaEvaluation eval = new AreaEvaluation();
            eval.evaluateAreaForPickedItem(toApply, toUse, ctx);
            return eval;
         }
      }
   }

   static void sendMessage(Player player, Component text) {
      player.sendSystemMessage(text);
   }
}
