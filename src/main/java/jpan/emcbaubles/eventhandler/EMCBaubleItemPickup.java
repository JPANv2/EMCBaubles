package jpan.emcbaubles.eventhandler;

import java.math.BigInteger;

import jpan.emcbaubles.EMCBaubles;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EMCBaubles.ModID)
public class EMCBaubleItemPickup {

	private static ItemStack transmutation_table = null;
	public static ItemStack getTransmutationTable(){
		if(transmutation_table == null){
			transmutation_table = new ItemStack(ObjHandler.transmuteStone);
		}
		return transmutation_table;
	}
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerPickUpItemWithEMC(EntityItemPickupEvent event){
		if(!(event.getEntityLiving() instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity)(event.getEntityLiving());
		if(!player.getTags().contains("auto_emc_convert"))
			return;
		
		ItemStack itemToCheck = event.getItem().getItem();
		long emc = EMCHelper.getEmcSellValue(itemToCheck);
		if (emc <= 0)
			return;
		if(EMCBaubles.serverConfig.belt.true_blacklist.get().contains(itemToCheck.getItem().getRegistryName().toString())) 
			return;
		
		if(EMCBaubles.serverConfig.belt.blacklist.get().contains(itemToCheck.getItem().getRegistryName().toString())) {
			if(player.inventory.getFirstEmptyStack() != -1)
				return;
		}
			
		if(EMCBaubles.serverConfig.belt.ignore_tools.get() && !itemToCheck.getToolTypes().isEmpty()) {
			if(player.inventory.getFirstEmptyStack() != -1)
					return;
		}
		int maxAvailable = 0;
		if(EMCBaubles.serverConfig.belt.blacklist.get().contains(itemToCheck.getItem().getRegistryName().toString())||
				(EMCBaubles.serverConfig.belt.fill_first.get() && itemToCheck.getMaxStackSize() > 1)) {
			if(player.inventory.hasItemStack(itemToCheck)) {
				 maxAvailable = itemToCheck.getMaxStackSize() - (player.inventory.count(itemToCheck.getItem()) % itemToCheck.getMaxStackSize());
				if(maxAvailable == itemToCheck.getMaxStackSize())
					maxAvailable = 0;
				if(itemToCheck.getCount() <= maxAvailable) {
					return;
				}
			}
		}
		IKnowledgeProvider provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).orElse(null);
		if(provider == null) 
			return;
		
		handleKnowledge(provider, player, itemToCheck);
		boolean added = false;
		while(itemToCheck.getCount() > maxAvailable){
			if(!addEmc(provider, player, emc)) {
				break;
			}
			added = true;
			itemToCheck.setCount(itemToCheck.getCount() - 1);
		}
		if(player instanceof ServerPlayerEntity)
			provider.sync((ServerPlayerEntity)player);
		if(added /*&& itemToCheck.getCount() == 0*/) {
			player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0f, 1.0f);
		}
	}
	
	public static void handleKnowledge(IKnowledgeProvider provider, PlayerEntity player, ItemStack itm)
	{
		ItemStack stack = itm.copy();
		if (stack.getCount() > 1)
		{
			stack.setCount(1);
		}
		
		if (ItemHelper.isDamageable(stack))
		{
			stack.setDamage(0);
		}
		
		if (!provider.hasKnowledge(stack))
		{
			provider.addKnowledge(stack);
		}
		
	}
	
	public static boolean addEmc(IKnowledgeProvider provider, PlayerEntity player, double value)
	{
		provider.setEmc(provider.getEmc().add(BigInteger.valueOf( Math.round(value))));
		if (!player.getEntityWorld().isRemote && player instanceof ServerPlayerEntity)
		{
			PlayerHelper.updateScore((ServerPlayerEntity)player, PlayerHelper.SCOREBOARD_EMC, provider.getEmc());
		}
		return true;
	}
}
