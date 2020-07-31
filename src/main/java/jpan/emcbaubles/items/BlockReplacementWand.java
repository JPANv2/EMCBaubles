package jpan.emcbaubles.items;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.Nonnull;

import jpan.emcbaubles.EMCBaubles;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockReplacementWand extends ItemMode {

	public BlockReplacementWand() {
		super(new Item.Properties().maxStackSize(1).group(ItemList.cTab), 0,
				new String[] { "Fill Mode", "Replace-only Mode" });
		this.setRegistryName(EMCBaubles.ModID, "emc_build_wand");
	}

	@Override
	public boolean canHarvestBlock(BlockState bs) {
		return true;
	}
	
	@Nonnull
	@Override
	public ActionResultType onItemUse(ItemUseContext ctx) {
		if(ctx.getWorld().isRemote)
			return ActionResultType.PASS;
		ItemStack item = ctx.getItem();
		if (!item.hasTag())
			item.getOrCreateTag();	
		
		if(ctx.getPlayer().isCrouching()) {
			if (item.hasTag() && item.getTag().contains("startPos")) {
				item.getTag().remove("startPos");
				ctx.getPlayer().sendMessage(new StringTextComponent("Cleared stored block."));
			}
			return ActionResultType.SUCCESS;
		}
		
		if (item.getTag().contains("startPos")) {
				IKnowledgeProvider provider = ctx.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)
						.orElse(null);
				if (provider == null)
					return ActionResultType.FAIL;

				BlockPos startPos = stringToBlockPos(item.getTag().getString("startPos"));
				BigInteger totalEMCEarned = BigInteger.valueOf(0);
				long totalBlocksReplace = 0;
				long totalBlocksFill = 0;
				long totalBlocksReplaced = 0;
				long totalBlocksFilled = 0;
				if (!ctx.getWorld().getBlockState(ctx.getPos()).getBlock()
						.equals(ctx.getWorld().getBlockState(startPos).getBlock())) {
					ctx.getPlayer()
							.sendMessage(new StringTextComponent("Stored block at position (" + ctx.getPos().getX()
									+ "," + ctx.getPos().getY() + "," + ctx.getPos().getZ() + ") and at " + "( "
									+ item.getTag().getString("startPos") + ") are not the same!"));
					return ActionResultType.FAIL;
				}
				if (!EMCHelper.doesItemHaveEmc(ctx.getWorld().getBlockState(ctx.getPos()).getBlock())) {
					ctx.getPlayer().sendMessage(new StringTextComponent("Stored block at position ("
							+ ctx.getPos().getX() + "," + ctx.getPos().getY() + "," + ctx.getPos().getZ() + ")."));
					return ActionResultType.FAIL;
				}
				int startX = Math.min(startPos.getX(), ctx.getPos().getX());
				int startY = Math.min(startPos.getY(), ctx.getPos().getY());
				int startZ = Math.min(startPos.getZ(), ctx.getPos().getZ());
				int endX = Math.max(startPos.getX(), ctx.getPos().getX());
				int endY = Math.max(startPos.getY(), ctx.getPos().getY());
				int endZ = Math.max(startPos.getZ(), ctx.getPos().getZ());
				
				for (int y = startY; y <= endY; y++) {
					for (int x = startX; x <= endX; x++) {
						for (int z = startZ; z <= endZ; z++) {
							totalBlocksFill++;
							BlockPos cur = new BlockPos(x, y, z);
							BlockState st = ctx.getWorld().getBlockState(cur);
							if (st != null && st.getBlock() != null && !ctx.getWorld().isAirBlock(cur)) {
								totalBlocksReplace++;
								List<ItemStack> drops = Block.getDrops(st, (ServerWorld) ctx.getWorld(), cur,
										ctx.getWorld().getTileEntity(cur));
								for (ItemStack d : drops) {
									if (EMCHelper.doesItemHaveEmc(d)) {
										totalEMCEarned = totalEMCEarned
												.add(BigInteger.valueOf(EMCHelper.getEmcValue(d)));
									}
								}
							}
						}
					}
				}
				if (getMode(item) == 0) {
					BigInteger totalEMC = BigInteger
							.valueOf(EMCHelper.getEmcValue(ctx.getWorld().getBlockState(ctx.getPos()).getBlock()))
							.multiply(BigInteger.valueOf(totalBlocksFill));
					totalEMC = totalEMC.subtract(totalEMCEarned);
					totalEMC = provider.getEmc().subtract(totalEMC);
					if (totalEMC.signum() < 0) {
						ctx.getPlayer()
								.sendMessage(new StringTextComponent(
										"You don't have enough EMC to do this operation. You would need "
												+ totalEMC.abs().toString() + " more EMC to do it."));
						return ActionResultType.FAIL;
					}
					for (int y = startY; y <= endY; y++) {
						for (int x = startX; x <= endX; x++) {
							for (int z = startZ; z <= endZ; z++) {
								BlockPos cur = new BlockPos(x, y, z);
								BlockState st = ctx.getWorld().getBlockState(cur);
								ctx.getWorld().setBlockState(cur, ctx.getWorld().getBlockState(startPos));
								totalBlocksFilled++;
							}
						}
					}
					totalEMC = BigInteger
							.valueOf(EMCHelper.getEmcValue(ctx.getWorld().getBlockState(ctx.getPos()).getBlock()))
							.multiply(BigInteger.valueOf(totalBlocksFilled));
					totalEMC = totalEMC.subtract(totalEMCEarned);
					BigInteger totalEMC2 = provider.getEmc().subtract(totalEMC);
					provider.setEmc(totalEMC2);
					provider.sync((ServerPlayerEntity) ctx.getPlayer());
					ctx.getPlayer()
					.sendMessage(new StringTextComponent("Filled and replaced " + totalBlocksFilled + " blocks for a total of " + (totalEMC.signum() < 0 ? (totalEMC.abs().toString()+ " EMC earned.") : (totalEMC.toString()+ " EMC spent."))));
					if(totalBlocksReplaced < totalBlocksReplace) {
						ctx.getPlayer()
						.sendMessage(new StringTextComponent("Failed to replace " + (totalBlocksFill - totalBlocksFilled) + "blocks."));
					}
					item.getTag().remove("startPos");
					return ActionResultType.SUCCESS;

				} else {
					BigInteger totalEMC = BigInteger
							.valueOf(EMCHelper.getEmcValue(ctx.getWorld().getBlockState(ctx.getPos()).getBlock()))
							.multiply(BigInteger.valueOf(totalBlocksReplace));
					totalEMC = totalEMC.subtract(totalEMCEarned);
					totalEMC = provider.getEmc().subtract(totalEMC);
					if (totalEMC.signum() < 0) {
						ctx.getPlayer()
								.sendMessage(new StringTextComponent(
										"You don't have enough EMC to do this operation. You would need "
												+ totalEMC.abs().toString() + " more EMC to do it."));
						return ActionResultType.FAIL;
					}
					for (int y = startY; y <= endY; y++) {
						for (int x = startX; x <= endX; x++) {
							for (int z = startZ; z <= endZ; z++) {
								BlockPos cur = new BlockPos(x, y, z);
								BlockState st = ctx.getWorld().getBlockState(cur);
								if (st != null && st.getBlock() != null && !ctx.getWorld().isAirBlock(cur)) {
									ctx.getWorld().setBlockState(cur, ctx.getWorld().getBlockState(startPos));
									totalBlocksReplaced++;
									
								}
							}
						}
					}
					totalEMC = BigInteger
							.valueOf(EMCHelper.getEmcValue(ctx.getWorld().getBlockState(ctx.getPos()).getBlock()))
							.multiply(BigInteger.valueOf(totalBlocksReplaced));
					totalEMC = totalEMC.subtract(totalEMCEarned);
					BigInteger totalEMC2 = provider.getEmc().subtract(totalEMC);
					provider.setEmc(totalEMC2);
					provider.sync((ServerPlayerEntity) ctx.getPlayer());
					ctx.getPlayer()
					.sendMessage(new StringTextComponent("Replaced " + totalBlocksReplaced + " blocks for a total of " + (totalEMC.signum() < 0 ? (totalEMC.abs().toString()+ " EMC earned.") : (totalEMC.toString()+ " EMC spent."))));
					if(totalBlocksReplaced < totalBlocksReplace) {
						ctx.getPlayer()
						.sendMessage(new StringTextComponent("Failed to replace " + (totalBlocksReplace - totalBlocksReplaced) + "blocks."));
					}
					item.getTag().remove("startPos");
					return ActionResultType.SUCCESS;
				}
			} else {
				item.getTag().putString("startPos",
						"" + ctx.getPos().getX() + "," + ctx.getPos().getY() + "," + ctx.getPos().getZ());
				ctx.getPlayer().sendMessage(new StringTextComponent("Stored block at position (" + ctx.getPos().getX()
						+ "," + ctx.getPos().getY() + "," + ctx.getPos().getZ() + ")."));
				return ActionResultType.SUCCESS;
			}
	}

	private BlockPos stringToBlockPos(String string) {
		String[] posS = string.split(",");
		return new BlockPos(Integer.parseInt(posS[0]), Integer.parseInt(posS[1]), Integer.parseInt(posS[2]));
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		boolean ans = super.changeMode(player, stack, hand);
		if (stack.hasTag() && stack.getTag().contains("startPos")) {
			stack.getTag().remove("startPos");
			player.sendMessage(new StringTextComponent("Cleared stored block."));
		}
		return ans;
	}

}
