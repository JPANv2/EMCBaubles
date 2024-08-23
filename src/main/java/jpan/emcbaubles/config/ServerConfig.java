package jpan.emcbaubles.config;

import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public final class ServerConfig {
   public final ServerConfig.BuildWandConfig wand;
   public final ServerConfig.BeltConfig belt;

   public ServerConfig(Builder builder) {
      this.wand = new ServerConfig.BuildWandConfig(builder);
      this.belt = new ServerConfig.BeltConfig(builder);
   }

   public static class BeltConfig {
      public final BooleanValue fill_first;
      public final BooleanValue ignore_tools;
      public final ConfigValue<List<? extends String>> blacklist;
      public final ConfigValue<List<? extends String>> true_blacklist;

      private BeltConfig(Builder builder) {
         builder.push("Belt Settings");
         this.fill_first = builder.comment("If the belt will try to place the item in the inventory if you already have a compatible stack in there.")
            .translation("Fill inventory stack first")
            .define("fill_first", true);
         this.ignore_tools = builder.comment("If the belt will try to place the item in the inventory if it's a tool.")
            .translation("Ignore Tools")
            .define("ignore_tools", true);
         List<String> defaultBlacklist = new ArrayList<>();
         defaultBlacklist.add("projecte:transmutation_table");
         this.blacklist = builder.comment(
               "Add here the resource location name (ie: \"minecraft:dirt\") of the items that will not be destroyed into EMC by the belt, if the inventory is not full."
            )
            .translation("Items to avoid destroying")
            .define("blacklist", defaultBlacklist);
         defaultBlacklist = new ArrayList<>();
         defaultBlacklist.add("projecte:transmutation_tablet");
         this.true_blacklist = builder.comment(
               "Add here the resource location name (ie: \"minecraft:dirt\") of the items that will not be destroyed into EMC by the belt, ever, even if it means not picking them up."
            )
            .translation("Items to not destroy")
            .define("true_blacklist", defaultBlacklist);
         builder.pop();
      }
   }

   public static class BuildWandConfig {
      public final BooleanValue only_one_select;
      public final BooleanValue select_end;
      public final BooleanValue drop_non_emc_items;
      public final ConfigValue<List<? extends String>> start_blacklist;
      public final ConfigValue<List<? extends String>> replace_blacklist;

      private BuildWandConfig(Builder builder) {
         builder.push("Build Wand Settings");
         this.only_one_select = builder.comment(
               "If you don't need to have the start block and the end block to be the same before actually replacing/filling the blocks. Default false."
            )
            .translation("Start and end Blocks not the same")
            .define("only_one_select", false);
         this.select_end = builder.comment(
               "If this option is selected, together with not requiring same start/end blocks, the block that will be copied will be the last one selected. Default false, meaning the copied block will be the starting one."
            )
            .translation("End Block is the one copied")
            .define("select_end", false);
         this.drop_non_emc_items = builder.comment(
               "When erasing blocks, via either replace, fill and replace or erase, items that have no EMC (IE: Iron Ore) will be dropped on the floor next to the player."
            )
            .translation("Drop destroyed non-EMC Items")
            .define("drop_non_emc_items", true);
         List<String> defaultBlacklist = new ArrayList<>();
         defaultBlacklist.add("minecraft:chest");
         this.start_blacklist = builder.comment(
               "Add here the resource location name (ie: \"minecraft:dirt\") of the blocks that will not be allowed as start block. (Note: These blocks will still be replaced with the replace feature. If you need to block that from happening, use the \"Non-replaceable block list\")."
            )
            .translation("Block selection Blacklist")
            .define("start_blacklist", defaultBlacklist);
         defaultBlacklist = new ArrayList<>();
         defaultBlacklist.add("minecraft:bedrock");
         defaultBlacklist.add("minecraft:end_portal_frame");
         defaultBlacklist.add("minecraft:chest");
         this.replace_blacklist = builder.comment(
               "Add here the resource location name (ie: \"minecraft:dirt\") of the blocks that will not be replaced by the replace function. (Note: Blocks defined here will not be considered invalid for copying. For that use \"Block selection Blacklist\")."
            )
            .translation("Non-replaceable Blocks")
            .define("replace_blacklist", defaultBlacklist);
         builder.pop();
      }
   }
}
