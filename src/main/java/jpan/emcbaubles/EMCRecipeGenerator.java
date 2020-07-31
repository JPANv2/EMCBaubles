package jpan.emcbaubles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class EMCRecipeGenerator {

	public static String decompressShapeless = "{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\",\r\n" + 
			"    \"count\": zcnt\r\n" + 
			"  },\r\n" + 
			"  \"ingredients\": [\r\n" + 
			"    {\r\n" + 
			"      \"item\": \"zsource\"\r\n" + 
			"    }\r\n" + 
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shapeless\"\r\n" + 
			"}";
	
	public static String compress4Shaped="{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\"\r\n" + 
			"  },\r\n" + 
			"  \"pattern\": [\r\n" + 
			"    \"XX\",\r\n" + 
			"    \"XX\"\r\n" + 
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
			"  \"key\": {\r\n" + 
			"    \"X\": {\r\n" + 
			"      \"item\": \"zsource\"\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"}";
	
	public static String compress9Shaped="{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\"\r\n" + 
			"  },\r\n" + 
			"  \"pattern\": [\r\n" + 
			"    \"XXX\",\r\n" +
			"    \"XXX\",\r\n" +
			"    \"XXX\"\r\n" + 
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
			"  \"key\": {\r\n" + 
			"    \"X\": {\r\n" + 
			"      \"item\": \"zsource\"\r\n" + 
			"    }\r\n" + 
			"  }\r\n" + 
			"}";
	public static String baseNecklaceShaped="{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\"\r\n" + 
			"  },\r\n" + 
			"  \"pattern\": [\r\n" + 
			"    \"XXX\",\r\n" +
			"    \"XYX\",\r\n" +
			"    \"XXX\"\r\n" + 
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
			"  \"key\": {\r\n" + 
			"    \"X\": {\r\n" + 
			"      \"item\": \"minecraft:string\"\r\n" + 
			"    },\r\n" +
			" 	\"Y\": {\r\n" + 
				"      \"item\": \"zsource\"\r\n" + 
				"    }\r\n" +
			"  }\r\n" + 
			"}";
	
	public static String clayMatterShaped="{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\"\r\n" + 
			"  },\r\n" + 
			"  \"pattern\": [\r\n" + 
			"    \"XXX\",\r\n" +
			"    \"XYX\",\r\n" +
			"    \"XXX\"\r\n" + 
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
			"  \"key\": {\r\n" + 
			"    \"X\": {\r\n" + 
			"      \"item\": \"minecraft:clay\"\r\n" + 
			"    },\r\n" +
			" 	\"Y\": {\r\n" + 
				"      \"item\": \"zsource\"\r\n" + 
				"    }\r\n" +
			"  }\r\n" + 
			"}";
	public static String matterShaped1="{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\"\r\n" + 
			"  },\r\n" + 
			"  \"pattern\": [\r\n" + 
			"    \"XYX\",\r\n" +
			"    \"XYX\",\r\n" +
			"    \"XYX\"\r\n" + 
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
			"  \"key\": {\r\n" + 
			"    \"X\": {\r\n" + 
			"      \"item\": \"projecte:aeternalis_fuel\"\r\n" + 
			"    },\r\n" +
			" 	\"Y\": {\r\n" + 
				"      \"item\": \"zsource\"\r\n" + 
				"    }\r\n" +
			"  }\r\n" + 
			"}";
	public static String matterShaped2="{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\"\r\n" + 
			"  },\r\n" + 
			"  \"pattern\": [\r\n" + 
			"    \"XXX\",\r\n" +
			"    \"YYY\",\r\n" +
			"    \"XXX\"\r\n" + 
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
			"  \"key\": {\r\n" + 
			"    \"X\": {\r\n" + 
			"      \"item\": \"projecte:aeternalis_fuel\"\r\n" + 
			"    },\r\n" +
			" 	\"Y\": {\r\n" + 
				"      \"item\": \"zsource\"\r\n" + 
				"    }\r\n" +
			"  }\r\n" + 
			"}";
	
	public static String advancedNecklaceShaped="{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\"\r\n" + 
			"  },\r\n" + 
			"  \"pattern\": [\r\n" + 
			"    \"X\",\r\n" +
			"    \"Y\"\r\n" + 
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
			"  \"key\": {\r\n" + 
			"    \"X\": {\r\n" + 
			"      \"item\": \"zmatter\"\r\n" + 
			"    },\r\n" +
			" 	\"Y\": {\r\n" + 
				"      \"item\": \"zneck\"\r\n" + 
				"    }\r\n" +
			"  }\r\n" + 
			"}";
	
	public static String charmShaped="{\r\n" + 
			"  \"result\": {\r\n" + 
			"    \"item\": \"zitm\"\r\n" + 
			"  },\r\n" + 
			"  \"pattern\": [\r\n" + 
			"    \"ZX \",\r\n" +
			"    \" Y \",\r\n" +
			"    \" XZ\"\r\n" +
			"  ],\r\n" + 
			"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
			"  \"key\": {\r\n" + 
			"    \"X\": {\r\n" + 
			"      \"item\": \"zmatter\"\r\n" + 
			"    },\r\n" +
			" 	\"Y\": {\r\n" + 
				"      \"item\": \"zcharm\"\r\n" + 
				"    },\r\n" +
				" 	\"Z\": {\r\n" + 
				"      \"item\": \"minecraft:gold_ingot\"\r\n" + 
				"    }\r\n" +
			"  }\r\n" +
			"}";
	public static String blockLoot = "{\r\n" + 
			"  \"type\": \"minecraft:block\",\r\n" + 
			"  \"pools\": [\r\n" + 
			"    {\r\n" + 
			"      \"name\": \"main\",\r\n" + 
			"      \"rolls\": 1,\r\n" + 
			"      \"entries\": [\r\n" + 
			"        {\r\n" + 
			"          \"type\": \"minecraft:item\",\r\n" + 
			"          \"conditions\": [\r\n" + 
			"            {\r\n" + 
			"              \"condition\": \"minecraft:survives_explosion\"\r\n" + 
			"            }\r\n" + 
			"          ],\r\n" + 
			"          \"name\": \"zblock\"\r\n" + 
			"        }\r\n" + 
			"      ]\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			"}";
	public static String[] mattersNames = {
			"clay_matter",
			"dark_matter",
			"red_matter",
			"magenta_matter",
			"pink_matter",
			"purple_matter",
			"violet_matter",
			"blue_matter",
			"cyan_matter",
			"green_matter",
			"lime_matter",
			"yellow_matter",
			"orange_matter",
			"white_matter",
			"fading_matter"
	};
	
	public static String[] matters = {
			"emcbaubles:clay_matter",
			"projecte:dark_matter",
			"projecte:red_matter",
			"emcbaubles:magenta_matter",
			"emcbaubles:pink_matter",
			"emcbaubles:purple_matter",
			"emcbaubles:violet_matter",
			"emcbaubles:blue_matter",
			"emcbaubles:cyan_matter",
			"emcbaubles:green_matter",
			"emcbaubles:lime_matter",
			"emcbaubles:yellow_matter",
			"emcbaubles:orange_matter",
			"emcbaubles:white_matter",
			"emcbaubles:fading_matter"
	};
	public static String[] matterBlocks = {
			"emcbaubles:clay_matter_block",
			"projecte:dark_matter_block",
			"projecte:red_matter_block",
			"emcbaubles:magenta_matter_block",
			"emcbaubles:pink_matter_block",
			"emcbaubles:purple_matter_block",
			"emcbaubles:violet_matter_block",
			"emcbaubles:blue_matter_block",
			"emcbaubles:cyan_matter_block",
			"emcbaubles:green_matter_block",
			"emcbaubles:lime_matter_block",
			"emcbaubles:yellow_matter_block",
			"emcbaubles:orange_matter_block",
			"emcbaubles:white_matter_block",
			"emcbaubles:fading_matter_block"
	};
	public static String[] cmatterBlocks = {
			"emcbaubles:compressed_clay_matter_block",
			"emcbaubles:compressed_dark_matter_block",
			"emcbaubles:compressed_red_matter_block",
			"emcbaubles:compressed_magenta_matter_block",
			"emcbaubles:compressed_pink_matter_block",
			"emcbaubles:compressed_purple_matter_block",
			"emcbaubles:compressed_violet_matter_block",
			"emcbaubles:compressed_blue_matter_block",
			"emcbaubles:compressed_cyan_matter_block",
			"emcbaubles:compressed_green_matter_block",
			"emcbaubles:compressed_lime_matter_block",
			"emcbaubles:compressed_yellow_matter_block",
			"emcbaubles:compressed_orange_matter_block",
			"emcbaubles:compressed_white_matter_block",
			"emcbaubles:compressed_fading_matter_block"
	};
	
	public static void main(String[] args) throws Exception {
		String shapeless = "c:\\minecraft\\"+ EMCBaubles.ModID+"\\recipes\\shapeless\\";
		String shaped = "c:\\minecraft\\"+ EMCBaubles.ModID+"\\recipes\\shaped\\";
		String block_loot_tables = "c:\\minecraft\\"+ EMCBaubles.ModID+"\\loot_tables\\blocks\\";
		
		try {
		makeDecompressRecipes(shapeless);
		makeCompressRecipes(shaped);
		makeNecklaceRecipes(shaped);
		makeCharmRecipes(shaped);
		makeMatterRecipes(shaped);
		makeOtherRecipes(shaped);
		
		makeBlockLootTables(block_loot_tables);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	

	private static void makeDecompressRecipes(String shapeless) throws IOException {
		File dir = new File(shapeless);
		if(!dir.exists())
			dir.mkdirs();
		for(int i = 0; i < matters.length; i++) {
			File f = new File(dir,"decompress_" + mattersNames[i] + "_block.json");
			f.createNewFile();
			FileWriter fwr = new FileWriter(f);
			String recipe = decompressShapeless.replaceAll("zitm", matterBlocks[i]).replaceAll("zcnt", "9").replaceAll("zsource", cmatterBlocks[i]); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
			f = new File(dir,"decompress_" + mattersNames[i] + ".json");
			f.createNewFile();
			fwr = new FileWriter(f);
			recipe = decompressShapeless.replaceAll("zitm", matters[i]).replaceAll("zcnt", "4").replaceAll("zsource", matterBlocks[i]); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
		}
	}
	private static void makeCompressRecipes(String shaped) throws IOException {
		File dir = new File(shaped);
		if(!dir.exists())
			dir.mkdirs();
		for(int i = 0; i < matters.length; i++) {
			File f = new File(dir,"compress_" + mattersNames[i] + "_block.json");
			f.createNewFile();
			FileWriter fwr = new FileWriter(f);
			String recipe = compress9Shaped.replaceAll("zsource", matterBlocks[i]).replaceAll("zcnt", "1").replaceAll("zitm", cmatterBlocks[i]); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
			f = new File(dir,"compress_" + mattersNames[i] + ".json");
			f.createNewFile();
			fwr = new FileWriter(f);
			recipe = compress4Shaped.replaceAll("zsource", matters[i]).replaceAll("zcnt", "1").replaceAll("zitm", matterBlocks[i]); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
		}
	}
	private static void makeNecklaceRecipes(String shaped) throws IOException {
		File dir = new File(shaped);
		if(!dir.exists())
			dir.mkdirs();
		for(int i = 1; i< 4; i++) {
			File f = new File(dir,"make_base_necklace_"+ i+ ".json");
			f.createNewFile();
			FileWriter fwr = new FileWriter(f);
			String recipe = baseNecklaceShaped.replaceAll("zsource", "projecte:collector_mk" + i).replaceAll("zitm","emcbaubles:collector_mk"+i+"_necklace"); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
			f = new File(dir,"unmake_base_necklace_"+ i+ ".json");
			f.createNewFile();
			fwr = new FileWriter(f);
			recipe = decompressShapeless.replaceAll("zitm", "projecte:collector_mk" + i).replaceAll("zcnt", "1").replaceAll("zsource","emcbaubles:collector_mk"+i+"_necklace"); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
		}
	
		for(int i = 1; i < matters.length; i++) {
			File f = new File(dir,"make_advanced_necklace_mk" + (i+1) + ".json");
			f.createNewFile();
			FileWriter fwr = new FileWriter(f);
			String recipe = advancedNecklaceShaped.replaceAll("zmatter", matters[i]).replaceAll("zneck" , "emcbaubles:collector_mk"+i+"_necklace").replaceAll("zcnt", "1").replaceAll("zitm", "emcbaubles:collector_mk"+(i+1)+"_necklace"); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
		}
	}
	
	private static void makeCharmRecipes(String shaped) throws IOException {
		File dir = new File(shaped);
		if(!dir.exists())
			dir.mkdirs();
		File f = new File(dir,"make_charm_mk1.json");
		f.createNewFile();
		FileWriter fwr = new FileWriter(f);
		String recipe = charmShaped.replaceAll("zmatter", cmatterBlocks[0]).replaceAll("zcharm" , "projecte:condenser_mk1").replaceAll("zcnt", "1").replaceAll("zitm", "emcbaubles:flower_mk1_charm"); 
		fwr.write(recipe);
		fwr.flush();
		fwr.close();
		for(int i = 1; i < matters.length; i++) {
			f = new File(dir,"make_charm_mk" + (i+1) + ".json");
			f.createNewFile();
			fwr = new FileWriter(f);
			recipe = charmShaped.replaceAll("zmatter", cmatterBlocks[i]).replaceAll("zcharm" , "emcbaubles:flower_mk"+i+"_charm").replaceAll("zcnt", "1").replaceAll("zitm", "emcbaubles:flower_mk"+(i+1)+"_charm"); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
		}
	}
	
	private static void makeMatterRecipes(String shaped) throws IOException {
		File dir = new File(shaped);
		if(!dir.exists())
			dir.mkdirs();
		File f = new File(dir,"make_clay_matter.json");
		f.createNewFile();
		FileWriter fwr = new FileWriter(f);
		String recipe = clayMatterShaped.replaceAll("zsource", "projecte:collector_mk1").replaceAll("zcnt", "1").replaceAll("zitm", matters[0]); 
		fwr.write(recipe);
		fwr.flush();
		fwr.close();
		for(int i = 1; i < matters.length; i++) {
			f = new File(dir,"make_" + mattersNames[i] + "_1.json");
			f.createNewFile();
			fwr = new FileWriter(f);
			recipe = matterShaped1.replaceAll("zsource", matters[i-1]).replaceAll("zcnt", "1").replaceAll("zitm", matters[i]); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
			
			f = new File(dir,"make_" + mattersNames[i] + "_2.json");
			f.createNewFile();
			fwr = new FileWriter(f);
			recipe = matterShaped2.replaceAll("zsource", matters[i-1]).replaceAll("zcnt", "1").replaceAll("zitm", matters[i]); 
			fwr.write(recipe);
			fwr.flush();
			fwr.close();
		}
	}
	private static void makeOtherRecipes(String shaped) throws IOException {
		File dir = new File(shaped);
		if(!dir.exists())
			dir.mkdirs();
		File f = new File(dir,"make_emc_filler.json");
		f.createNewFile();
		FileWriter fwr = new FileWriter(f);
		fwr.write("{\r\n" + 
				"  \"result\": {\r\n" + 
				"    \"item\": \"emcbaubles:emc_refiller\"\r\n" + 
				"  },\r\n" + 
				"  \"pattern\": [\r\n" + 
				"    \" X \",\r\n" +
				"    \"Z Z\",\r\n" +
				"    \" Z \"\r\n" +
				"  ],\r\n" + 
				"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
				"  \"key\": {\r\n" + 
				"    \"X\": {\r\n" + 
				"      \"item\": \"projecte:transmutation_table\"\r\n" + 
				"    },\r\n" +
					" 	\"Z\": {\r\n" + 
					"      \"item\": \"minecraft:gold_ingot\"\r\n" + 
					"    }\r\n" +
				"  }\r\n" +
				"  }\r\n" + 
				"}");
		fwr.flush();
		fwr.close();
		f = new File(dir,"make_emc_belt.json");
		f.createNewFile();
		fwr = new FileWriter(f);
		fwr.write("{\r\n" + 
				"  \"result\": {\r\n" + 
				"    \"item\": \"emcbaubles:emc_belt\"\r\n" + 
				"  },\r\n" + 
				"  \"pattern\": [\r\n" + 
				"    \"ZXZ\"\r\n" +
				"  ],\r\n" + 
				"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
				"  \"key\": {\r\n" + 
				"    \"X\": {\r\n" + 
				"      \"item\": \""+ matters[2]+"\"\r\n" + 
				"    },\r\n" +
					" 	\"Z\": {\r\n" + 
					"      \"item\": \"minecraft:leather\"\r\n" + 
					"    }\r\n" +
				"  }\r\n" +
				"  }\r\n" + 
				"}");
		fwr.flush();
		fwr.close();
		f = new File(dir,"make_emc_build_wand.json");
		f.createNewFile();
		fwr = new FileWriter(f);
		fwr.write(
		"{\r\n" + 
		"  \"result\": {\r\n" + 
		"    \"item\": \"emcbaubles:emc_build_wand\"\r\n" + 
		"  },\r\n" + 
		"  \"pattern\": [\r\n" + 
		"    \" XZ\",\r\n" + 
		"    \"XX \",\r\n" + 
		"    \"Y  \"\r\n" + 
		"  ],\r\n" + 
		"  \"type\": \"minecraft:crafting_shaped\",\r\n" + 
		"  \"key\": {\r\n" + 
		"    \"X\": {\r\n" + 
		"      \"item\": \"projecte:red_matter_block\"\r\n" + 
		"    },\r\n" + 
		"    \"Y\": {\r\n" + 
		"      \"item\": \"projecte:transmutation_tablet\"\r\n" + 
		"    },\r\n" + 
		"    \"Z\": {\r\n" + 
		"      \"item\": \"emcbaubles:compressed_red_matter_block\"\r\n" + 
		"    }\r\n" + 
		"  }\r\n" + 
		"}");
		fwr.flush();
		fwr.close();
	}
	
	private static void makeBlockLootTables(String block_loot_tables) throws IOException {
		File dir = new File(block_loot_tables);
		if(!dir.exists())
			dir.mkdirs();
		for(int i = 0; i < cmatterBlocks.length; i++) {
			File f = new File(dir,cmatterBlocks[i].substring(11)+".json");
			f.createNewFile();
			FileWriter fwr = new FileWriter(f);
			String loot = blockLoot.replaceAll("zblock", cmatterBlocks[i]);
			fwr.write(loot);
			fwr.flush();
			fwr.close();
			if(i >2 || i == 0) {
				f = new File(dir,matterBlocks[i].substring(11)+".json");
				f.createNewFile();
				fwr = new FileWriter(f);
				loot = blockLoot.replaceAll("zblock", matterBlocks[i]);
				fwr.write(loot);
				fwr.flush();
				fwr.close();	
			}
		}
		
	}
}
