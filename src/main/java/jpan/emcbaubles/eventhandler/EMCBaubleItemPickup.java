package jpan.emcbaubles.eventhandler;

import java.math.BigInteger;

import jpan.emcbaubles.EMCBaubles;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
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
		//LogManager.getLogger(JPANsMiscMod.ModID).log(Level.INFO, "Picking up item "+ event.getItem().getItem().getDisplayName());
		ItemStack itemToCheck = event.getItem().getItem();
		if((event.getEntityLiving() instanceof PlayerEntity) &&
				event.getEntityLiving().getTags().contains("auto_emc_convert") &&
				EMCHelper.doesItemHaveEmc(itemToCheck)){
			if(itemToCheck.getItem() == ObjHandler.transmutationTablet || itemToCheck.getItem() == getTransmutationTable().getItem())
				return;
			long emc = EMCHelper.getEmcValue(itemToCheck);
			if (emc <= 0)
				return;
			PlayerEntity player = (PlayerEntity)(event.getEntityLiving());
			IKnowledgeProvider provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).orElse(null);
			if(provider == null) 
				return;
			handleKnowledge(provider, player, itemToCheck);
			while(itemToCheck.getCount() > 0){
				if(!addEmc(provider, player, emc)) {
					break;
				}
				itemToCheck.setCount(itemToCheck.getCount() - 1);
			}
			if(player instanceof ServerPlayerEntity)
				provider.sync((ServerPlayerEntity)player);
			return;
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
