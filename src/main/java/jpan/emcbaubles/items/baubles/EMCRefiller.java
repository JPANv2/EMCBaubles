package jpan.emcbaubles.items.baubles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.items.CurioCapableEquipItem;
import jpan.emcbaubles.items.CurioCapableTickItem;
import jpan.emcbaubles.items.CurioEquippedItemCapacity;
import jpan.emcbaubles.items.ItemList;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;

public class EMCRefiller extends Item implements CurioCapableEquipItem, CurioCapableTickItem {

	public String name = "emc_refiller";
	
	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	
	public EMCRefiller(){
		super(new Item.Properties().maxStackSize(1).group(ItemList.cTab));
		this.setRegistryName(new ResourceLocation(EMCBaubles.ModID, name));
		addItemCapability(IntegrationHelper.CURIO_MODID, () -> CurioEquippedItemCapacity::new);
	}
	
	protected void addItemCapability(String modid, Supplier<Supplier<ItemCapability<?>>> capabilitySupplier) {
		if (ModList.get().isLoaded(modid)) {
			supportedCapabilities.add(capabilitySupplier.get());
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt) {
		if (supportedCapabilities.isEmpty()) {
			return super.initCapabilities(stack, nbt);
		}
		return new ItemCapabilityWrapper(stack, supportedCapabilities);
	}

	@Override
	public void curioEquip(ItemStack stack, World world, Entity entity) {
		if (world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;
		if(!player.getTags().contains("emc_refill_items")){
			player.addTag("emc_refill_items");
		}
	}

	@Override
	public void curioUnequip(ItemStack stack, World world, Entity entity) {
		if (world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;
		if(player.getTags().contains("emc_refill_items")){
			player.removeTag("emc_refill_items");
		}
	}
	@Override
	public void curioTick(ItemStack stack, World world, Entity entity, int par4, boolean par5) {
	/*	if (world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}
		PlayerEntity player = (PlayerEntity) entity;
		if(!player.getTags().contains("auto_emc_convert")){
			player.addTag("auto_emc_convert");
		}*/
	}
}
