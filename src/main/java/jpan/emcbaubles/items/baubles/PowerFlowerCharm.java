package jpan.emcbaubles.items.baubles;

import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.utils.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.items.CurioCapableTickItem;
import jpan.emcbaubles.items.CurioEquippedItemCapacity;
import jpan.emcbaubles.items.ItemList;

public class PowerFlowerCharm extends Item implements CurioCapableTickItem {

	public final int tier;
	public long emcPerTick;
	public static long milisecondTime = -1;
	public static int ticks = 0;
	
	private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();

	public PowerFlowerCharm(long EMCPerTick, int tier) {
		super(new Item.Properties().maxStackSize(1).group(ItemList.cTab));
		this.setRegistryName(new ResourceLocation(EMCBaubles.ModID, "flower_mk" + tier + "_charm"));
		this.tier = tier;
		this.emcPerTick = EMCPerTick;
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

	public void curioTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int par4, boolean par5) {
		if (world.isRemote || !(entity instanceof PlayerEntity)) {
			return;
		}
		ticks++;
		if(ticks < 20)
			return;
		ticks = 0;
		/*if(System.currentTimeMillis() < milisecondTime + 1000) {
			return;
		}
		milisecondTime = System.currentTimeMillis();*/
		PlayerEntity player = (PlayerEntity) entity;
		IKnowledgeProvider provider = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null).orElse(null);
		if (provider != null) {
			provider.setEmc(provider.getEmc().add(BigInteger.valueOf(emcPerTick)));
		}
		if(player instanceof ServerPlayerEntity)
			provider.sync((ServerPlayerEntity)player);
	}

}
