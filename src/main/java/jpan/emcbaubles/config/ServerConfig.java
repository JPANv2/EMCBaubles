package jpan.emcbaubles.config;

import java.util.ArrayList;
import java.util.List;

import moze_intel.projecte.PECore;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public final class ServerConfig {

	public final BuildWandConfig wand;
	public final BeltConfig belt;

	public ServerConfig(ForgeConfigSpec.Builder builder) {
		
		wand = new BuildWandConfig(builder);
		belt = new BeltConfig(builder);
	
	}

	public static class BuildWandConfig {

		public final BooleanValue only_one_select;
		public final BooleanValue select_end;
		public final ConfigValue<List<? extends String>> start_blacklist;
		public final ConfigValue<List<? extends String>> replace_blacklist;

		private BuildWandConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Build Wand Settings");
			
			/*only_one_select = builder
					.comment(new TranslationTextComponent("emcb.config.wand.only_one_select.desc").getString())
					.translation("emcb.config.wand.only_one_select")
					.define("only_one_select", false);
			select_end = builder
					.comment(new TranslationTextComponent("emcb.config.wand.select_end.desc").getString())
					.translation("emcb.config.wand.select_end")
					.define("select_end", false);
			List<String> defaultBlacklist = new ArrayList<String>();
			defaultBlacklist.add("minecraft:chest");
			start_blacklist = builder
					.comment(new TranslationTextComponent("emcb.config.wand.blacklist.start.desc").getString())
					.translation("emcb.config.wand.blacklist.start")
					.define("start_blacklist", defaultBlacklist);
			defaultBlacklist = new ArrayList<String>();
			defaultBlacklist.add("minecraft:chest");
			replace_blacklist = builder
					.comment(new TranslationTextComponent("emcb.config.wand.blacklist.replace.desc").getString())
					.translation("emcb.config.wand.blacklist.replace")
					.define("replace_blacklist", defaultBlacklist);*/
			
			only_one_select = builder
					.comment(new TranslationTextComponent("If you don't need to have the start block and the end block to be the same before actually replacing/filling the blocks. Default false.").getString())
					.translation("Start and end Blocks not the same")
					.define("only_one_select", false);
			select_end = builder
					.comment("If this option is selected, together with not requiring same start/end blocks, the block that will be copied will be the last one selected. Default false, meaning the copied block will be the starting one.")
					.translation("End Block is the one copied")
					.define("select_end", false);
			List<String> defaultBlacklist = new ArrayList<String>();
			defaultBlacklist.add("minecraft:chest");
			start_blacklist = builder
					.comment("Add here the resource location name (ie: \"minecraft:dirt\") of the blocks that will not be allowed as start block. (Note: These blocks will still be replaced with the replace feature. If you need to block that from happening, use the \"Non-replaceable block list\").")
					.translation("Block selection Blacklist")
					.define("start_blacklist", defaultBlacklist);
			defaultBlacklist = new ArrayList<String>();
			defaultBlacklist.add("minecraft:chest");
			replace_blacklist = builder
					.comment("Add here the resource location name (ie: \"minecraft:dirt\") of the blocks that will not be replaced by the replace function. (Note: Blocks defined here will not be considered invalid for copying. For that use \"Block selection Blacklist\").")
					.translation("Non-replaceable Blocks")
					.define("replace_blacklist", defaultBlacklist);
			
			builder.pop();
		}
	}
	
	public static class BeltConfig {

		public final BooleanValue fill_first;
		public final BooleanValue ignore_tools;
		public final ConfigValue<List<? extends String>> blacklist;
		public final ConfigValue<List<? extends String>> true_blacklist;
		
		private BeltConfig(ForgeConfigSpec.Builder builder) {
			builder.push("Belt Settings");
			
			/*fill_first = builder
					.comment(new TranslationTextComponent("emcb.config.belt.fill_first.desc").getString())
					.translation("emcb.config.belt.fill_first")
					.define("fill_first", true);
			ignore_tools = builder
					.comment(new TranslationTextComponent("emcb.config.belt.ignore_tools.desc").getString())
					.translation("emcb.config.belt.ignore_tools")
					.define("ignore_tools", true);
			List<String> defaultBlacklist = new ArrayList<String>();
			defaultBlacklist.add(PECore.MODID + ":transmutation_table");
			blacklist = builder
					.comment(new TranslationTextComponent("emcb.config.belt.blacklist.desc").getString())
					.translation("emcb.config.belt.blacklist")
					.define("blacklist", defaultBlacklist);
			defaultBlacklist = new ArrayList<String>();
			defaultBlacklist.add(PECore.MODID + ":transmutation_tablet");
			true_blacklist = builder
					.comment(new TranslationTextComponent("emcb.config.belt.true_blacklist.desc").getString())
					.translation("emcb.config.belt.true_blacklist")
					.define("true_blacklist", defaultBlacklist);
					*/
			
			fill_first = builder
					.comment("If the belt will try to place the item in the inventory if you already have a compatible stack in there.")
					.translation("Fill inventory stack first")
					.define("fill_first", true);
			ignore_tools = builder
					.comment("If the belt will try to place the item in the inventory if it's a tool.")
					.translation("Ignore Tools")
					.define("ignore_tools", true);
			List<String> defaultBlacklist = new ArrayList<String>();
			defaultBlacklist.add(PECore.MODID + ":transmutation_table");
			blacklist = builder
					.comment("Add here the resource location name (ie: \"minecraft:dirt\") of the items that will not be destroyed into EMC by the belt, if the inventory is not full.")
					.translation("Items to avoid destroying")
					.define("blacklist", defaultBlacklist);
			defaultBlacklist = new ArrayList<String>();
			defaultBlacklist.add(PECore.MODID + ":transmutation_tablet");
			true_blacklist = builder
					.comment("Add here the resource location name (ie: \"minecraft:dirt\") of the items that will not be destroyed into EMC by the belt, ever, even if it means not picking them up.")
					.translation("Items to not destroy")
					.define("true_blacklist", defaultBlacklist);
			
			builder.pop();
		}
	}
}
