package jpan.emcbaubles.items;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.eventhandler.PedestalPlacersWorldSaveData;
import jpan.emcbaubles.items.baubles.CollectorNecklace;
import jpan.emcbaubles.items.baubles.EMCPickupBelt;
import jpan.emcbaubles.items.baubles.EMCRefiller;
import jpan.emcbaubles.items.baubles.PowerFlowerCharm;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.IForgeRegistry;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

@Mod.EventBusSubscriber(modid = EMCBaubles.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemList {
	
	public static final ItemGroup cTab = new ItemGroup(EMCBaubles.ModID) {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(collector_necklaces[0]);
		}
	};
	public static Item[] matters;
	public static Block[] matterBlocks;
	public static Block[] compressedMatterBlocks;
	public static CollectorNecklace[] collector_necklaces;
	public static EMCPickupBelt emc_belt;
	public static EMCRefiller emc_refiller;
	public static BlockReplacementWand emc_build_wand;
	public static PowerFlowerCharm[] flower_charms;
	private static boolean __init = false;
	
	public static final ResourceLocation braceletSlotTexture = new ResourceLocation(EMCBaubles.ModID, "curios/empty_bracelet");
	public static final ResourceLocation pinSlotTexture = new ResourceLocation(EMCBaubles.ModID, "curios/empty_pin");
	
	public static void initCurios() {
		InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("pin"));
		InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("bracelet"));
		InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_ICON, () -> new Tuple<>("pin",pinSlotTexture));
		InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_ICON, () -> new Tuple<>("bracelet",braceletSlotTexture));
	}
	
	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void stitchTextures(TextureStitchEvent.Pre evt) {

		if (evt.getMap().getTextureLocation() == PlayerContainer.LOCATION_BLOCKS_TEXTURE) {

			evt.addSprite(braceletSlotTexture);
			evt.addSprite(pinSlotTexture);

		}
	}
	
	
	
	public static void init() {
		collector_necklaces = new CollectorNecklace[16];
		flower_charms = new PowerFlowerCharm[16];
		matters = new Item[16];
		matterBlocks = new Block[16];
		compressedMatterBlocks = new Block[16];
		int i = 0;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "clay_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "clay_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_clay_matter_block");
		collector_necklaces[i] = new CollectorNecklace(4, 1);
		flower_charms[i] = new PowerFlowerCharm(102, 1);
		i++;
		
		matters[i] = ObjHandler.darkMatter;
		matterBlocks[i] = ObjHandler.dmBlock;
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_dark_matter_block");
		collector_necklaces[i] = new CollectorNecklace(12, 2);
		flower_charms[i] = new PowerFlowerCharm(306, 2);
		i++;
		
		matters[i] = ObjHandler.redMatter;
		matterBlocks[i] = ObjHandler.rmBlock;
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_red_matter_block");
		collector_necklaces[i] = new CollectorNecklace(40, 3);
		flower_charms[i] = new PowerFlowerCharm(1020, 3);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "magenta_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "magenta_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_magenta_matter_block");
		collector_necklaces[i] = new CollectorNecklace(160, 4);
		flower_charms[i] = new PowerFlowerCharm(4080, 4);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "pink_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "pink_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_pink_matter_block");
		collector_necklaces[i] = new CollectorNecklace(640, 5);
		flower_charms[i] = new PowerFlowerCharm(16020, 5);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "purple_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "purple_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_purple_matter_block");
		collector_necklaces[i] = new CollectorNecklace(2560, 6);
		flower_charms[i] = new PowerFlowerCharm(68580, 6);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "violet_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "violet_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_violet_matter_block");
		collector_necklaces[i] = new CollectorNecklace(10240, 7);
		flower_charms[i] = new PowerFlowerCharm(296820, 7);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "blue_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "blue_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_blue_matter_block");
		collector_necklaces[i] = new CollectorNecklace(40960, 8);
		flower_charms[i] = new PowerFlowerCharm(1187280, 8);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "cyan_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "cyan_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_cyan_matter_block");
		collector_necklaces[i] = new CollectorNecklace(163840, 9);
		flower_charms[i] = new PowerFlowerCharm(4749120, 9);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "green_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "green_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_green_matter_block");
		collector_necklaces[i] = new CollectorNecklace(655360, 10);
		flower_charms[i] = new PowerFlowerCharm(18996480, 10);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "lime_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "lime_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_lime_matter_block");
		collector_necklaces[i] = new CollectorNecklace(2621440, 11);
		flower_charms[i] = new PowerFlowerCharm(75985920, 11);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "yellow_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "yellow_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_yellow_matter_block");
		collector_necklaces[i] = new CollectorNecklace(10485760, 12);
		flower_charms[i] = new PowerFlowerCharm(303953680, 12);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "orange_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "orange_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_orange_matter_block");
		collector_necklaces[i] = new CollectorNecklace(41943040, 13);
		flower_charms[i] = new PowerFlowerCharm(1215774720L, 13);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "white_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "white_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_white_matter_block");
		collector_necklaces[i] = new CollectorNecklace(167772160, 14);
		flower_charms[i] = new PowerFlowerCharm(4863098880L, 14);
		i++;
		
		matters[i] = new Item(new Item.Properties().group(ItemList.cTab)).setRegistryName(EMCBaubles.ModID, "fading_matter");
		matterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "fading_matter_block");
		compressedMatterBlocks[i] = new Block(Block.Properties.create(Material.IRON).hardnessAndResistance(0.5F, 1.5F)).setRegistryName(EMCBaubles.ModID, "compressed_fading_matter_block");
		collector_necklaces[i] = new CollectorNecklace(671088640, 15);
		flower_charms[i] = new PowerFlowerCharm(19452395520L, 15);
		i++;
		
		matters[i] = null;
		matterBlocks[i] = null;
		compressedMatterBlocks[i] = null;
		collector_necklaces[i] = new CollectorNecklace(1000000000L,16);
		flower_charms[i] = new PowerFlowerCharm(48000000000000L, 16);
		
		emc_belt = new EMCPickupBelt();
		emc_refiller = new EMCRefiller();
		emc_build_wand = new BlockReplacementWand();
		__init = true;
	}
	
	public static EMCPickupBelt getEMCBelt() {
		if(!__init) {
			init();
		}
		return emc_belt;
	}
	
	public static EMCRefiller getEMCRefiller() {
		if(!__init) {
			init();
		}
		return emc_refiller;
	}
	
	public static BlockReplacementWand getEMCBuildWand() {
		if(!__init) {
			init();
		}
		return emc_build_wand;
	}
	
	public static Item getMatter(int i) {
		if(!__init) {
			init();
		}
		if (i < 0 || i > matters.length)
			return null;
		
		return matters[i];
	}
	
	public static Block getMatterBlock(int i) {
		if(!__init) {
			init();
		}
		if (i < 0 || i > matterBlocks.length)
			return null;
		
		return matterBlocks[i];
	}
	
	public static Block getCompressedMatterBlock(int i) {
		if(!__init) {
			init();
		}
		if (i < 0 || i > compressedMatterBlocks.length)
			return null;
		
		return compressedMatterBlocks[i];
	}
	
	public static CollectorNecklace getCollectorNeckalce(int i) {
		if(!__init) {
			init();
		}
		if (i < 0 || i > collector_necklaces.length)
			return null;
		
		return collector_necklaces[i];
	}

	public static Item getPowerFlowerCharm(int i) {
		if(!__init) {
			init();
		}
		if (i < 0 || i > flower_charms.length)
			return null;
		
		return flower_charms[i];
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> evt) {
		IForgeRegistry<Block> r = evt.getRegistry();
		if(!__init) {
			init();
		}
		for(int i = 0; i < matterBlocks.length; i++) {
			if(i > 0 && i < 3) {
				Block b = getCompressedMatterBlock(i);
				if(b != null)
					r.register(b);
			}else {
				Block b = getCompressedMatterBlock(i);
				if(b != null)
					r.register(b);
				b = getMatterBlock(i);
				if(b != null)
					r.register(b);
			}
		}
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> evt) {
		IForgeRegistry<Item> r = evt.getRegistry();
		if(!__init) {
			init();
		}
		for(int i = 0; i < matters.length; i++) {
			if(i != 1  && i != 2) {
				Item itm = getMatter(i);
				if(itm != null)
					r.register(itm);
				Block b = getMatterBlock(i);
				if(b != null)
					r.register(new BlockItem(b, new Item.Properties().group(ItemList.cTab)).setRegistryName(b.getRegistryName()));
			}
			Block b = getCompressedMatterBlock(i);
			if(b != null)
				r.register(new BlockItem(b, new Item.Properties().group(ItemList.cTab)).setRegistryName(b.getRegistryName()));
			Item itm = getCollectorNeckalce(i);
			if(itm != null)
				r.register(itm);
			itm = getPowerFlowerCharm(i);
			if(itm != null)
				r.register(itm);
		}
		r.register(getEMCBelt());
		r.register(getEMCRefiller());
		r.register(getEMCBuildWand());
		
	}
	
	
}
