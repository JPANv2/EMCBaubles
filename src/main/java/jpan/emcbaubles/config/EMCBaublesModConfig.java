package jpan.emcbaubles.config;

import java.nio.file.Path;
import java.util.function.Function;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class EMCBaublesModConfig extends ModConfig {

	private static final EMCBaublesConfigFileTypeHandler TOML = new EMCBaublesConfigFileTypeHandler();
	
	public EMCBaublesModConfig(Type type, ForgeConfigSpec spec, ModContainer activeContainer) {
		super(type, spec, activeContainer);
	}
	
	public EMCBaublesModConfig(Type type, ForgeConfigSpec spec, ModContainer activeContainer, String filename) {
		super(type, spec, activeContainer, filename);
	}
	
	@Override
	public ConfigFileTypeHandler getHandler() {
		return TOML;
	}

	private static class EMCBaublesConfigFileTypeHandler extends ConfigFileTypeHandler {

		private static Path getPath(Path configBasePath) {
			//Intercept server config path reading for ProjectE configs and reroute it to the normal config directory
			if (configBasePath.endsWith("serverconfig")) {
				return FMLPaths.CONFIGDIR.get();
			}
			return configBasePath;
		}

		@Override
		public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
			return super.reader(getPath(configBasePath));
		}

		@Override
		public void unload(Path configBasePath, ModConfig config) {
			super.unload(getPath(configBasePath), config);
		}
	}

}
