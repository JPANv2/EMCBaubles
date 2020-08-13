package jpan.emcbaubles;

import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import jpan.emcbaubles.capabilities.IPedestalPlacerWorldCapabilty;
import jpan.emcbaubles.capabilities.PedestalPlacerWorldCapabilty;
import jpan.emcbaubles.capabilities.PedestalPlacerWorldCapabiltyProvider;
import jpan.emcbaubles.capabilities.PedestalPlacerWorldCapabiltySaver;
import jpan.emcbaubles.config.EMCBaublesModConfig;
import jpan.emcbaubles.config.ServerConfig;
import jpan.emcbaubles.items.ItemList;
import jpan.emcbaubles.packets.PacketHandler;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("emcbaubles")
@EventBusSubscriber(bus = Bus.FORGE, modid = EMCBaubles.ModID)
public class EMCBaubles {

	
	public static final String ModID = "emcbaubles";
	public static final String ModName = "EMC Baubles";
	
	public static ModContainer _instance;
	
	public static ServerConfig serverConfig;
	public static ForgeConfigSpec serverConfigSpec;
	
	public EMCBaubles() {
		_instance = ModLoadingContext.get().getActiveContainer();
		MinecraftForge.EVENT_BUS.register(this);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcQueue);
		Pair<ServerConfig, ForgeConfigSpec> serverConfiguration = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
		serverConfig = serverConfiguration.getLeft();
		serverConfigSpec = serverConfiguration.getRight();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		_instance.addConfig(new EMCBaublesModConfig(Type.SERVER, serverConfigSpec, _instance));
	}
	
	private void imcQueue(InterModEnqueueEvent event) {
		ItemList.initCurios();
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(IPedestalPlacerWorldCapabilty.class, new PedestalPlacerWorldCapabiltySaver(), PedestalPlacerWorldCapabilty::new);
		DeferredWorkQueue.runLater(() -> {
			PacketHandler.register();
		});
	}
	
	@CapabilityInject(IPedestalPlacerWorldCapabilty.class)
	public static Capability<IPedestalPlacerWorldCapabilty> PEDESTAL_PLACER_CAPABILITY = null;

	@SubscribeEvent
    public void attachCap(AttachCapabilitiesEvent<World> event)
    {	
		if(event.getObject().dimension.getType() ==  DimensionType.OVERWORLD) {
			event.addCapability(new ResourceLocation(EMCBaubles.ModID, "pedestal_placers"), new PedestalPlacerWorldCapabiltyProvider());
			LogManager.getLogger().info("Attatched World Pedestal Player capability.");
		}
	}
	
}
