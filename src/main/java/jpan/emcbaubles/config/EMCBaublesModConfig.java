package jpan.emcbaubles.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.nio.file.Path;
import java.util.function.Function;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.loading.FMLPaths;

public class EMCBaublesModConfig extends ModConfig {
   private static final EMCBaublesModConfig.EMCBaublesConfigFileTypeHandler TOML = new EMCBaublesModConfig.EMCBaublesConfigFileTypeHandler();

   public EMCBaublesModConfig(Type type, ForgeConfigSpec spec, ModContainer activeContainer) {
      super(type, spec, activeContainer);
   }

   public EMCBaublesModConfig(Type type, ForgeConfigSpec spec, ModContainer activeContainer, String filename) {
      super(type, spec, activeContainer, filename);
   }

   public ConfigFileTypeHandler getHandler() {
      return TOML;
   }

   private static class EMCBaublesConfigFileTypeHandler extends ConfigFileTypeHandler {
      private static Path getPath(Path configBasePath) {
         return configBasePath.endsWith("serverconfig") ? FMLPaths.CONFIGDIR.get() : configBasePath;
      }

      public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
         return super.reader(getPath(configBasePath));
      }

      public void unload(Path configBasePath, ModConfig config) {
         super.unload(getPath(configBasePath), config);
      }
   }
}
