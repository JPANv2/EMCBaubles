package jpan.emcbaubles;

import jpan.emcbaubles.config.EMCBaublesModConfig;
import jpan.emcbaubles.config.ServerConfig;
import jpan.emcbaubles.items.ItemList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

@Mod("emcbaubles")
@EventBusSubscriber(
   bus = Bus.FORGE,
   modid = "emcbaubles"
)
public class EMCBaubles {
   public static final String ModID = "emcbaubles";
   public static final String ModName = "EMC Baubles";
   public static ModContainer _instance;
   public static ServerConfig serverConfig;
   public static ForgeConfigSpec serverConfigSpec;

   public EMCBaubles() {
      _instance = ModLoadingContext.get().getActiveContainer();
      MinecraftForge.EVENT_BUS.register(this);
      MinecraftForge.EVENT_BUS.register(ItemList._instance);
      IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
      ItemList.CONTAINER_TYPES.register(modEventBus);
      ItemList.ITEM_TYPES.register(modEventBus);
      modEventBus.addListener(this::imcQueue);
      DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {modEventBus.addListener(ItemList._instance::registerScreens);});
      modEventBus.addListener(ItemList._instance::register);
      modEventBus.addListener(ItemList::onCreativeModeTabRegister);
      Pair<ServerConfig, ForgeConfigSpec> serverConfiguration = new Builder().configure(ServerConfig::new);
      serverConfig = (ServerConfig)serverConfiguration.getLeft();
      serverConfigSpec = (ForgeConfigSpec)serverConfiguration.getRight();
      modEventBus.addListener(this::commonSetup);
      _instance.addConfig(new EMCBaublesModConfig(Type.SERVER, serverConfigSpec, _instance));
   }

   private void imcQueue(InterModEnqueueEvent event) {
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }
}
