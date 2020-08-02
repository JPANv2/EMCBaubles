package jpan.emcbaubles;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import jpan.emcbaubles.config.EMCBaublesModConfig;
import jpan.emcbaubles.config.ServerConfig;
import jpan.emcbaubles.items.ItemList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("emcbaubles")
public class EMCBaubles {

	
	public static final String ModID = "emcbaubles";
	public static final String ModName = "EMC Baubles";
	
	public static ModContainer _instance;
	
	public static HashMap<BlockPos, UUID> pedestalPlacers = new HashMap<>();
	/*Blocks and tiles*/
	
	public static ServerConfig serverConfig;
	public static ForgeConfigSpec serverConfigSpec;
	
	public EMCBaubles() {
		_instance = ModLoadingContext.get().getActiveContainer();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcQueue);
		Pair<ServerConfig, ForgeConfigSpec> serverConfiguration = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
		serverConfig = serverConfiguration.getLeft();
		serverConfigSpec = serverConfiguration.getRight();
		
		_instance.addConfig(new EMCBaublesModConfig(Type.SERVER, serverConfigSpec, _instance));
	}
	
	private void imcQueue(InterModEnqueueEvent event) {
		ItemList.initCurios();
	}
}
