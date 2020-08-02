package jpan.emcbaubles.items.baubles;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.capabilities.IPedestalPlacerWorldCapabilty;
import jpan.emcbaubles.items.CurioCapableTickItem;
import jpan.emcbaubles.items.CurioEquippedItemCapacity;
import jpan.emcbaubles.items.ItemList;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.item.IPedestalItem;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.capability.PedestalItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.integration.IntegrationHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;

public class CollectorNecklace extends Item implements CurioCapableTickItem, IPedestalItem{

	public final int tier;
	public long emcPerTick;
	public static long milisecondTime = -1;
	public static int ticks = 0;
	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();
	
	public CollectorNecklace(long EMCPerTick, int tier){
		super(new Item.Properties().maxStackSize(1).group(ItemList.cTab));
		this.setRegistryName(new ResourceLocation(EMCBaubles.ModID, "collector_mk" + tier+"_necklace"));
		this.tier = tier;
		this.emcPerTick = EMCPerTick;
		addItemCapability(IntegrationHelper.CURIO_MODID, () -> CurioEquippedItemCapacity::new);
		addItemCapability("projecte", () -> PedestalItemCapabilityWrapper::new);
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

	public void curioTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int par4, boolean par5) {
		if (world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}
		if(ticks != 0)
			return;
		/*
		if(System.currentTimeMillis() < milisecondTime + 1000) {
			return;
		}
		milisecondTime = System.currentTimeMillis();*/
		PlayerEntity player = (PlayerEntity) entity;
		IKnowledgeProvider provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).orElse(null);
		if(provider != null) {
			provider.setEmc(provider.getEmc().add(BigInteger.valueOf(emcPerTick)));
		}
		if(player instanceof ServerPlayerEntity)
			provider.sync((ServerPlayerEntity)player);
	}

	@Override
	public List<ITextComponent> getPedestalDescription() {
		List<ITextComponent> list = new ArrayList<>();
		list.add(new StringTextComponent("Provides to the player that placed it on the pedestal " +emcPerTick+ "EMC/s").applyTextStyle(TextFormatting.BLUE));
		return list;
	}

	@Override
	public void updateInPedestal(World arg0, BlockPos arg1) {
		IPedestalPlacerWorldCapabilty cap = arg0.getCapability(EMCBaubles.PEDESTAL_PLACER_CAPABILITY).orElse(null);
		if(cap == null) return;
		
		UUID id = cap.getPlacer(arg1);
		if(id == null)
			return;
		PlayerEntity pe = arg0.getPlayerByUuid(id);
		TileEntity te = arg0.getTileEntity(arg1);
		if(te == null || !(te instanceof DMPedestalTile))
			return;
		curioTick(((DMPedestalTile)te).getInventory().getStackInSlot(0), arg0,pe,0,true);
	}

}
