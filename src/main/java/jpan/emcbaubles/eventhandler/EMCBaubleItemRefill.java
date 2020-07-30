package jpan.emcbaubles.eventhandler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
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
/*
	@SubscribeEvent
	public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {

		// LogManager.getLogger(JPANsMiscMod.ModID).log(Level.INFO, "Picking up item "+
		// event.getItem().getItem().getDisplayName());
		ItemStack itemToCheck = event.getItem();
		if ((event.getEntityLiving() instanceof PlayerEntity)
				&& event.getEntityLiving().getTags().contains("emc_refill_items"))
			if (event.getResultStack().getCount() > 0
					|| event.getResultStack().getItem().equals(itemToCheck.getItem())) {
				if (event.getResultStack().getMaxStackSize() > event.getResultStack().getCount()) {

					PlayerEntity player = (PlayerEntity) (event.getEntityLiving());
					IKnowledgeProvider provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null)
							.orElse(null);
					if (provider == null)
						return;
					ItemStack itmCopy;
					if (event.getResultStack().getCount() > 0) {
						itmCopy = event.getResultStack().copy();
					} else {
						itmCopy = itemToCheck.copy();
						itmCopy.setCount(itmCopy.getCount() - 1);
					}
					for(int i = 0; i< nonRefillable.size(); i++) {
						if(itmCopy.getItem().getRegistryName().equals(ResourceLocation.create(nonRefillable.get(i),':')))
							return;
					}
					if (provider.hasKnowledge(itmCopy)) {
						while (provider.getEmc().compareTo(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))) > 0
								&& itmCopy.getCount() < itemToCheck.getMaxStackSize()) {
							provider.setEmc(
									provider.getEmc().subtract(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))));
							itmCopy.setCount(itmCopy.getCount() + 1);
						}
						event.setResultStack(itmCopy);
						if(player instanceof ServerPlayerEntity)
							provider.sync((ServerPlayerEntity)player);
						return;
					}
				}
			}
	}*/
}