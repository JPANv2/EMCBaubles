package jpan.emcbaubles;



import jpan.emcbaubles.items.ItemList;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("emcbaubles")
public class EMCBaubles {

	
	public static final String ModID = "emcbaubles";
	public static final String ModName = "EMC Baubles";
	
	public static ModContainer _instance;
	
	/*Blocks and tiles*/
	
	public EMCBaubles() {
		_instance = ModLoadingContext.get().getActiveContainer();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::imcQueue);
	}
	
	private void imcQueue(InterModEnqueueEvent event) {
		ItemList.initCurios();
	}
}
