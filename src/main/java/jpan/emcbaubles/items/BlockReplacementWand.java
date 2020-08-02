package jpan.emcbaubles.items;

import java.math.BigInteger;
import java.util.List;

import javax.annotation.Nonnull;

import jpan.emcbaubles.EMCBaubles;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.gameObjs.items.ItemMode;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class BlockReplacementWand extends ItemMode {

	public BlockReplacementWand() {
		super(new Item.Properties().maxStackSize(1).group(ItemList.cTab), 0,
				new String[] {"emcb.wand.mode.0.name","emcb.wand.mode.1.name", "emcb.wand.mode.2.name", "emcb.wand.mode.3.name", "emcb.wand.mode.4.name" });
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
		switch (getMode(item)) {
			case 0:
				return fillOrReplaceMode(ctx);
			case 1:
				return fillMode(ctx);
			case 2:
				return replaceMode(ctx);
			case 3:
				return eraseMode(ctx);
			case 4:
			default:
				return PickerMode(ctx);
		}
	}
	
	protected boolean clearStartMode(ItemUseContext ctx) {
		if(ctx.getPlayer().isCrouching()) {
			if (ctx.getItem().getOrCreateTag().contains("startPos")) {
				ctx.getItem().getTag().remove("startPos");
				ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.start.clear"));
				return true;
			}
		}
		return false;
	}
	
	protected ActionResultType setStartMode(ItemUseContext ctx, boolean allowNoValueSelect) {
		if(ctx.getItem().getOrCreateTag().contains("startPos"))
			return ActionResultType.PASS;
		
		if(ctx.getWorld().getBlockState(ctx.getPos()).has(BlockStateProperties.BED_PART) ||
		   ctx.getWorld().getBlockState(ctx.getPos()).has(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.block.disallowed.double"));
			return ActionResultType.FAIL;
		}
		Block start = ctx.getWorld().getBlockState(ctx.getPos()).getBlock();
		if( allowNoValueSelect || EMCHelper.doesItemHaveEmc(start)) {
			if(EMCBaubles.serverConfig.wand.start_blacklist.get().contains(start.getRegistryName().toString())) {
				ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.block.disallowed.blacklist.start"));
				return ActionResultType.FAIL;
			}
			ctx.getItem().getOrCreateTag().putString("startPos",
					"" + ctx.getPos().getX() + "," + ctx.getPos().getY() + "," + ctx.getPos().getZ());
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.start.selected", "(" + ctx.getPos().getX()
					+ "," + ctx.getPos().getY() + "," + ctx.getPos().getZ() + ")"));
			return ActionResultType.SUCCESS;
		}else {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.block.disallowed.value"));
			return ActionResultType.FAIL;
		}
	}
	
	protected AreaEvaluation evaluateArea(ItemUseContext ctx, BlockState blockToReplace) {
		BlockPos startPos = stringToBlockPos(ctx.getItem().getOrCreateTag().getString("startPos"));
		if(startPos == null) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent( "emcb.wand.no_start_block"));
			return null;
		}
		AreaEvaluation ans = new AreaEvaluation();
		
		int startX = Math.min(startPos.getX(), ctx.getPos().getX());
		int startY = Math.min(startPos.getY(), ctx.getPos().getY());
		int startZ = Math.min(startPos.getZ(), ctx.getPos().getZ());
		int endX = Math.max(startPos.getX(), ctx.getPos().getX());
		int endY = Math.max(startPos.getY(), ctx.getPos().getY());
		int endZ = Math.max(startPos.getZ(), ctx.getPos().getZ());
		
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				for (int z = startZ; z <= endZ; z++) {
					
					BlockPos cur = new BlockPos(x, y, z);
					BlockState st = ctx.getWorld().getBlockState(cur);
					if (st != null && st.getBlock() != null && !ctx.getWorld().isAirBlock(cur)) {
						if(EMCBaubles.serverConfig.wand.replace_blacklist.get().contains(st.getBlock().getRegistryName().toString())) {
							ans.blacklisted++;
						}else {
							if(!st.getBlock().equals(blockToReplace.getBlock())) {
								ans.totalBlocksReplaced++;
								List<ItemStack> drops = Block.getDrops(st, (ServerWorld) ctx.getWorld(), cur,
										ctx.getWorld().getTileEntity(cur));
								for (ItemStack d : drops) {
									if (EMCHelper.doesItemHaveEmc(d)) {
										ans.totalEMC = ans.totalEMC
												.add(BigInteger.valueOf(EMCHelper.getEmcSellValue(d)));
									}
								}
							}
						}
					}else {
						ans.totalBlocksFilled++;
					}
				}
			}
		}
		return ans;
	}

	
	
	public AreaEvaluation fillAndReplace(ItemUseContext ctx, BlockState toPlace, boolean fill, boolean replace) {
		BlockPos startPos = stringToBlockPos(ctx.getItem().getOrCreateTag().getString("startPos"));
		if(startPos == null) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent( "emcb.wand.no_start_block"));
			return null;
		}
		AreaEvaluation ans = new AreaEvaluation();
		
		int startX = Math.min(startPos.getX(), ctx.getPos().getX());
		int startY = Math.min(startPos.getY(), ctx.getPos().getY());
		int startZ = Math.min(startPos.getZ(), ctx.getPos().getZ());
		int endX = Math.max(startPos.getX(), ctx.getPos().getX());
		int endY = Math.max(startPos.getY(), ctx.getPos().getY());
		int endZ = Math.max(startPos.getZ(), ctx.getPos().getZ());
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				for (int z = startZ; z <= endZ; z++) {
					BlockPos cur = new BlockPos(x, y, z);
					BlockState st = ctx.getWorld().getBlockState(cur);
					if (st != null && st.getBlock() != null && !ctx.getWorld().isAirBlock(cur)) {
						if (replace) {
							if(EMCBaubles.serverConfig.wand.replace_blacklist.get().contains(st.getBlock().getRegistryName().toString())) {
								ans.blacklisted++;
							}else {
								if(!st.getBlock().equals(toPlace.getBlock())) {
									ans.totalBlocksReplaced++;
								}
								ctx.getWorld().setBlockState(cur,toPlace);
							}
						}
					}else {
						if(fill) {
							ans.totalBlocksFilled++;
							ctx.getWorld().setBlockState(cur,toPlace);
						}
					}
				}
			}
		}
		return ans;
	}
	
	protected ActionResultType fillOrReplaceMode(ItemUseContext ctx) {
		if(clearStartMode(ctx)) {
			return ActionResultType.SUCCESS;
		}
		ActionResultType setStart = setStartMode(ctx, false); 
		if(setStart != ActionResultType.PASS) {
			return setStart;
		}
		BlockPos startPos = stringToBlockPos(ctx.getItem().getOrCreateTag().getString("startPos"));
		if(startPos == null)
			return ActionResultType.FAIL;
		BlockState startBlock = ctx.getWorld().getBlockState(startPos);
		BlockState endBlock = ctx.getWorld().getBlockState(ctx.getPos());
		if(!EMCBaubles.serverConfig.wand.only_one_select.get() && !startBlock.getBlock().equals(endBlock.getBlock())) {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.blocks_not_equal"));
			return ActionResultType.FAIL;
		}
		startBlock = EMCBaubles.serverConfig.wand.select_end.get() ? endBlock: startBlock;
		
		IKnowledgeProvider provider = ctx.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)
				.orElse(null);
		if (provider == null)
			return ActionResultType.FAIL;
		
		AreaEvaluation eval = evaluateArea(ctx, startBlock);
		if(eval == null)
			return ActionResultType.FAIL;
		
		
		if(startPos == null)
			return ActionResultType.FAIL;
		
		BigInteger totalEMC = BigInteger
				.valueOf(EMCHelper.getEmcValue(startBlock.getBlock()))
				.multiply(BigInteger.valueOf(eval.totalBlocksFilled + eval.totalBlocksReplaced));
		totalEMC = totalEMC.subtract(eval.totalEMC);
		totalEMC = provider.getEmc().subtract(totalEMC);
		if (totalEMC.signum() < 0) {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.not_enough_emc", totalEMC.abs().toString()));
			return ActionResultType.FAIL;
		}
		
		AreaEvaluation results = fillAndReplace(ctx, startBlock, true, true);
		
		totalEMC = BigInteger
				.valueOf(EMCHelper.getEmcValue(startBlock.getBlock()))
				.multiply(BigInteger.valueOf(results.totalBlocksFilled + results.totalBlocksReplaced));
		totalEMC = totalEMC.subtract(eval.totalEMC);
		BigInteger totalEMC2 = provider.getEmc().subtract(totalEMC);
		provider.setEmc(totalEMC2);
		provider.sync((ServerPlayerEntity) ctx.getPlayer());
		if(totalEMC.signum() == 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.0.result.neutral", results.totalBlocksFilled, results.totalBlocksReplaced));
		}else if(totalEMC.signum() > 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.0.result.loss", results.totalBlocksFilled, results.totalBlocksReplaced, totalEMC.abs().toString()));
		}else {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.0.result.gain", results.totalBlocksFilled, results.totalBlocksReplaced, totalEMC.abs().toString()));
		}
		if(results.blacklisted > 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent( "emcb.wand.not_replaced_count", results.blacklisted));
		}
		ctx.getItem().getTag().remove("startPos");
		return ActionResultType.SUCCESS;
	}
	
	protected ActionResultType fillMode(ItemUseContext ctx) {
		if(clearStartMode(ctx)) {
			return ActionResultType.SUCCESS;
		}
		ActionResultType setStart = setStartMode(ctx, false); 
		if(setStart != ActionResultType.PASS) {
			return setStart;
		}
		
		BlockPos startPos = stringToBlockPos(ctx.getItem().getOrCreateTag().getString("startPos"));
		if(startPos == null)
			return ActionResultType.FAIL;
		BlockState startBlock = ctx.getWorld().getBlockState(startPos);
		BlockState endBlock = ctx.getWorld().getBlockState(ctx.getPos());
		if(!EMCBaubles.serverConfig.wand.only_one_select.get() && !startBlock.getBlock().equals(endBlock.getBlock())) {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.blocks_not_equal"));
			return ActionResultType.FAIL;
		}
		startBlock = EMCBaubles.serverConfig.wand.select_end.get() ? endBlock: startBlock;
		
		IKnowledgeProvider provider = ctx.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)
				.orElse(null);
		if (provider == null)
			return ActionResultType.FAIL;
		
		AreaEvaluation eval = evaluateArea(ctx, startBlock);
		if(eval == null)
			return ActionResultType.FAIL;
		
		eval.totalEMC = BigInteger.ZERO; //Fill mode does not replace blocks, therefore does not receive EMC
		
		BigInteger totalEMC = BigInteger
				.valueOf(EMCHelper.getEmcValue(ctx.getWorld().getBlockState(ctx.getPos()).getBlock()))
				.multiply(BigInteger.valueOf(eval.totalBlocksFilled));
		totalEMC = totalEMC.subtract(eval.totalEMC);
		totalEMC = provider.getEmc().subtract(totalEMC);
		if (totalEMC.signum() < 0) {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.not_enough_emc", totalEMC.abs().toString()));
			return ActionResultType.FAIL;
		}
		
		AreaEvaluation results = fillAndReplace(ctx, startBlock, true, false);
		
		totalEMC = BigInteger
				.valueOf(EMCHelper.getEmcValue(ctx.getWorld().getBlockState(ctx.getPos()).getBlock()))
				.multiply(BigInteger.valueOf(results.totalBlocksFilled));
		totalEMC = totalEMC.subtract(eval.totalEMC);
		BigInteger totalEMC2 = provider.getEmc().subtract(totalEMC);
		provider.setEmc(totalEMC2);
		provider.sync((ServerPlayerEntity) ctx.getPlayer());
		if(totalEMC.signum() == 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.1.result.neutral", results.totalBlocksFilled));
		}else if(totalEMC.signum() > 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.1.result.loss", results.totalBlocksFilled, totalEMC.abs().toString()));
		}else {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.1.result.gain", results.totalBlocksFilled, totalEMC.abs().toString()));
		}
		if(results.blacklisted > 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent( "emcb.wand.not_replaced_count", results.blacklisted));
		}
		ctx.getItem().getTag().remove("startPos");
		return ActionResultType.SUCCESS;
	}
	
	protected ActionResultType replaceMode(ItemUseContext ctx) {
		if(clearStartMode(ctx)) {
			return ActionResultType.SUCCESS;
		}
		ActionResultType setStart = setStartMode(ctx, false); 
		if(setStart != ActionResultType.PASS) {
			return setStart;
		}
		
		BlockPos startPos = stringToBlockPos(ctx.getItem().getOrCreateTag().getString("startPos"));
		if(startPos == null)
			return ActionResultType.FAIL;
		BlockState startBlock = ctx.getWorld().getBlockState(startPos);
		BlockState endBlock = ctx.getWorld().getBlockState(ctx.getPos());
		if(!EMCBaubles.serverConfig.wand.only_one_select.get() && !startBlock.getBlock().equals(endBlock.getBlock())) {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.blocks_not_equal"));
			return ActionResultType.FAIL;
		}
		startBlock = EMCBaubles.serverConfig.wand.select_end.get() ? endBlock: startBlock;
		
		IKnowledgeProvider provider = ctx.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)
				.orElse(null);
		if (provider == null)
			return ActionResultType.FAIL;
		
		AreaEvaluation eval = evaluateArea(ctx, startBlock);
		if(eval == null)
			return ActionResultType.FAIL;
		
	
		
		BigInteger totalEMC = BigInteger
				.valueOf(EMCHelper.getEmcValue(startBlock.getBlock()))
				.multiply(BigInteger.valueOf(eval.totalBlocksReplaced));
		totalEMC = totalEMC.subtract(eval.totalEMC);
		totalEMC = provider.getEmc().subtract(totalEMC);
		if (totalEMC.signum() < 0) {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.not_enough_emc", totalEMC.abs().toString()));
			return ActionResultType.FAIL;
		}
		
		AreaEvaluation results = fillAndReplace(ctx, startBlock, false, true);
		
		totalEMC = BigInteger
				.valueOf(EMCHelper.getEmcValue(startBlock.getBlock()))
				.multiply(BigInteger.valueOf(results.totalBlocksReplaced));
		totalEMC = totalEMC.subtract(eval.totalEMC);
		BigInteger totalEMC2 = provider.getEmc().subtract(totalEMC);
		provider.setEmc(totalEMC2);
		provider.sync((ServerPlayerEntity) ctx.getPlayer());
		if(totalEMC.signum() == 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.2.result.neutral", results.totalBlocksReplaced));
		}else if(totalEMC.signum() > 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.2.result.loss", results.totalBlocksReplaced, totalEMC.abs().toString()));
		}else {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.2.result.gain", results.totalBlocksReplaced, totalEMC.abs().toString()));
		}
		if(results.blacklisted > 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent( "emcb.wand.not_replaced_count", results.blacklisted));
		}
		ctx.getItem().getTag().remove("startPos");
		return ActionResultType.SUCCESS;
	}
	
	protected ActionResultType eraseMode(ItemUseContext ctx) {
		if(clearStartMode(ctx)) {
			return ActionResultType.SUCCESS;
		}
		ActionResultType setStart = setStartMode(ctx, true); 
		if(setStart != ActionResultType.PASS) {
			return setStart;
		}
		
		IKnowledgeProvider provider = ctx.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)
				.orElse(null);
		if (provider == null)
			return ActionResultType.FAIL;
		
		AreaEvaluation eval = evaluateArea(ctx, Blocks.AIR.getDefaultState());
		if(eval == null)
			return ActionResultType.FAIL;
		
		BlockPos startPos = stringToBlockPos(ctx.getItem().getOrCreateTag().getString("startPos"));
		if(startPos == null)
			return ActionResultType.FAIL;
		
		AreaEvaluation results = fillAndReplace(ctx, Blocks.AIR.getDefaultState(), false, true);
		
		BigInteger totalEMC = BigInteger.ZERO;
		totalEMC = totalEMC.subtract(eval.totalEMC);
		BigInteger totalEMC2 = provider.getEmc().subtract(totalEMC);
		provider.setEmc(totalEMC2);
		provider.sync((ServerPlayerEntity) ctx.getPlayer());
		if(totalEMC.signum() == 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.3.result.neutral", results.totalBlocksReplaced));
		}else if(totalEMC.signum() > 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.3.result.loss", results.totalBlocksReplaced, totalEMC.abs().toString()));
		}else {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent("emcb.wand.mode.3.result.gain", results.totalBlocksReplaced, totalEMC.abs().toString()));
		}
		if(results.blacklisted > 0) {
			ctx.getPlayer()
			.sendMessage(new TranslationTextComponent( "emcb.wand.not_replaced_count", results.blacklisted));
		}
		ctx.getItem().getTag().remove("startPos");
		return ActionResultType.SUCCESS;
	}
	
	protected ActionResultType PickerMode(ItemUseContext ctx) {
		Block b = ctx.getWorld().getBlockState(ctx.getPos()).getBlock();
		if (!EMCHelper.doesItemHaveEmc(b)) {
			ctx.getPlayer().sendMessage( new TranslationTextComponent( "emcb.wand.block.disallowed.value"));
			return ActionResultType.FAIL;
		}
		Hand empty = Hand.OFF_HAND;
		if(ctx.getPlayer().getHeldItemMainhand().isEmpty()) {
			empty = Hand.MAIN_HAND;
		}else if(!ctx.getPlayer().getHeldItemOffhand().isEmpty()){
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.wand.need_empty_hand"));
			return ActionResultType.FAIL;
		}
		IKnowledgeProvider provider = ctx.getPlayer().getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)
				.orElse(null);
		if (provider == null)
			return ActionResultType.FAIL;
		ItemStack picked = new ItemStack(b);
		picked.setCount(1);
		if(!provider.hasKnowledge(picked)) {
			provider.addKnowledge(picked);
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.learned.block"));
		}
		if(provider.getEmc().compareTo(BigInteger.valueOf(EMCHelper.getEmcValue(picked))) >= 0) {
			ctx.getPlayer().setHeldItem(empty, picked);
			provider.setEmc(provider.getEmc().subtract(BigInteger.valueOf(EMCHelper.getEmcValue(picked))));
			provider.sync((ServerPlayerEntity) ctx.getPlayer());
			return ActionResultType.SUCCESS;
		}else {
			ctx.getPlayer().sendMessage(new TranslationTextComponent("emcb.cannot.afford.block"));
			return ActionResultType.FAIL;
		}
	}
	
	private BlockPos stringToBlockPos(String string) {
		if(string == null || string.isEmpty())
			return null;
		String[] posS = string.split(",");
		if (posS.length < 3)
			return null;
		
		return new BlockPos(Integer.parseInt(posS[0]), Integer.parseInt(posS[1]), Integer.parseInt(posS[2]));
	}

	@Override
	public boolean changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, Hand hand) {
		boolean ans = super.changeMode(player, stack, hand);
		if (stack.hasTag() && stack.getTag().contains("startPos")) {
			stack.getTag().remove("startPos");
			player.sendMessage(new TranslationTextComponent("emcb.wand.start.clear"));
		}
		return ans;
	}

	public static class AreaEvaluation{
		public BigInteger totalEMC = BigInteger.ZERO;
		public long totalBlocksFilled = 0;
		public long totalBlocksReplaced = 0;
		public long blacklisted = 0;
	}
	
}
