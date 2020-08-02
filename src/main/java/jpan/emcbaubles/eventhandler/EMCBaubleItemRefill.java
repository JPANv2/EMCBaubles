package jpan.emcbaubles.eventhandler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.capabilities.IPedestalPlacerWorldCapabilty;
import jpan.emcbaubles.items.baubles.CollectorNecklace;
import jpan.emcbaubles.items.baubles.PowerFlowerCharm;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EMCBaubles.ModID)
public class EMCBaubleItemRefill {

	private static ItemStack transmutation_table = null;

	public static ItemStack getTransmutationTable() {
		if (transmutation_table == null) {
			transmutation_table = new ItemStack(ObjHandler.transmuteStone);
		}
		return transmutation_table;
	}

	public static List<String> nonRefillable = new ArrayList<>();
	
	@SubscribeEvent
	public static void onItemBreak(PlayerDestroyItemEvent event) {

		// LogManager.getLogger(JPANsMiscMod.ModID).log(Level.INFO, "Picking up item "+
		// event.getItem().getItem().getDisplayName());
		ItemStack itemToCheck = event.getOriginal();
		ItemStack itmCopy = itemToCheck.copy();
		if(itmCopy.isDamageable())
			itmCopy.setDamage(0);
		itmCopy.setCount(1);
		if ((event.getEntityLiving() instanceof PlayerEntity)
				&& event.getEntityLiving().getTags().contains("emc_refill_items")
				&& EMCHelper.doesItemHaveEmc(itmCopy)) {

			PlayerEntity player = (PlayerEntity) (event.getEntityLiving());
			IKnowledgeProvider provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).orElse(null);
			if (provider == null)
				return;
			if (provider.hasKnowledge(itmCopy)
					&& provider.getEmc().compareTo(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))) > 0) {
				provider.setEmc(provider.getEmc().subtract(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))));
				if(event.getOriginal().isDamageable())
					event.getOriginal().setDamage(0);
				event.getOriginal().setCount(1);
				while(event.getOriginal().getMaxStackSize() > event.getOriginal().getCount()&&
						provider.getEmc().compareTo(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))) > 0) {
					provider.setEmc(provider.getEmc().subtract(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))));
					event.getOriginal().setCount(1+event.getOriginal().getCount());
				}
				player.setHeldItem(event.getHand(), event.getOriginal()); 
				if(player instanceof ServerPlayerEntity)
					provider.sync((ServerPlayerEntity)player);
				
				return;
			}
		}
	}
	
	@SubscribeEvent
	public static void onPedestalActivated(PlayerInteractEvent.RightClickBlock event) {
		if(event.getPlayer() == null)
			return;
		World world =event.getPlayer().getEntityWorld();
		TileEntity te = world.getTileEntity(event.getPos());
		//Block block = world.getBlockState(event.getPos()).getBlock();
		if(te == null || !(te instanceof DMPedestalTile)) return;
		DMPedestalTile tile = (DMPedestalTile)te;
		ItemStack item = tile.getInventory().getStackInSlot(0);
		ItemStack stack = event.getPlayer().getHeldItem(event.getHand());
		if (!stack.isEmpty() && item.isEmpty()) {
			if(stack.getItem() instanceof CollectorNecklace || stack.getItem() instanceof PowerFlowerCharm) {
				IPedestalPlacerWorldCapabilty cap = world.getCapability(EMCBaubles.PEDESTAL_PLACER_CAPABILITY).orElse(null);
				if(cap == null) return;
				if(cap.getWorld() != world)
					cap.setWorld(world);
				cap.setPlacer(event.getPos(), event.getPlayer().getUniqueID());
				
			}
		}
	}
	
	@SubscribeEvent 
	public static void onWorldTick(TickEvent.WorldTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			CollectorNecklace.ticks = (CollectorNecklace.ticks + 1) %20;
			PowerFlowerCharm.ticks = (PowerFlowerCharm.ticks +1) % 20;
			IPedestalPlacerWorldCapabilty cap = event.world.getCapability(EMCBaubles.PEDESTAL_PLACER_CAPABILITY).orElse(null);
			if(cap == null) return;
			if(cap.getWorld() != event.world)
				cap.setWorld(event.world);
			HashSet<BlockPos> keys = new HashSet<BlockPos>();
			keys.addAll(cap.getBlocks());
			for(BlockPos key: keys) {
				TileEntity te = event.world.getTileEntity(key);
				if(te == null || !(te instanceof DMPedestalTile)) {
					cap.removePlacer(key);
				}
			}
		}
	}
}