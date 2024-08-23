package jpan.emcbaubles.items;

import java.util.List;
import jpan.emcbaubles.PacketHandler;
import jpan.emcbaubles.items.baubles.CollectorNecklace;
import jpan.emcbaubles.items.baubles.EMCPickupBelt;
import jpan.emcbaubles.items.baubles.EMCRefiller;
import jpan.emcbaubles.items.baubles.PowerFlowerCharm;
import jpan.emcbaubles.items.gui.WandContainerMenu;
import jpan.emcbaubles.items.gui.WandSelectGui;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.registration.impl.ItemDeferredRegister;
import moze_intel.projecte.gameObjs.registration.impl.ItemRegistryObject;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries.Keys;

@EventBusSubscriber(
   modid = "emcbaubles",
   bus = Bus.MOD
)
public class ItemList {
   public static final ContainerTypeDeferredRegister CONTAINER_TYPES = new ContainerTypeDeferredRegister("emcbaubles");
   public static final ContainerTypeRegistryObject<WandContainerMenu> WAND_CONTAINER = CONTAINER_TYPES.register(
      "wand_select_screen", WandContainerMenu::fromNetwork
   );
   public static final ItemDeferredRegister ITEM_TYPES = new ItemDeferredRegister("emcbaubles");
   public static final ItemRegistryObject<BlockReplacementWand> WAND_ITEM = ITEM_TYPES.register("emc_build_wand", BlockReplacementWand::new);
   public static final ItemRegistryObject<EMCPickupBelt> BELT_ITEM = ITEM_TYPES.register("emc_belt", EMCPickupBelt::new);
   public static final ItemRegistryObject<EMCRefiller> BRACELET_ITEM = ITEM_TYPES.register("emc_refiller", EMCRefiller::new);
   public static final ItemList _instance = new ItemList();
   public static String[] names = new String[]{
      "clay", "dark", "red", "magenta", "pink", "purple", "violet", "blue", "cyan", "green", "lime", "yellow", "orange", "white", "fading"
   };
   public static long[] neck_power = new long[]{
      4L, 12L, 40L, 160L, 640L, 2560L, 10240L, 40960L, 163840L, 655360L, 2621440L, 10485760L, 41943040L, 167772160L, 671088640L, 2000000000L
   };
   public static long[] flower_power = new long[]{
      102L,
      306L,
      1020L,
      4080L,
      16020L,
      10240L,
      68580L,
      296820L,
      1187280L,
      4749120L,
      18996480L,
      75985920L,
      303953680L,
      1215774720L,
      4863098880L,
      19452395520L,
      48000000000000L
   };
   public static final ResourceLocation braceletSlotTexture = new ResourceLocation("emcbaubles", "curios/empty_bracelet");
   public static final ResourceLocation pinSlotTexture = new ResourceLocation("emcbaubles", "curios/empty_pin");

   public static boolean isStringItem(String s, ItemStack itm) {
      ResourceLocation rl = new ResourceLocation(s);
      TagKey<Item> tag = ItemTags.create(rl);
      TagKey<Block> tagB = BlockTags.create(rl);
      return itm.is(tag) ? true : itm.is((Item)ForgeRegistries.ITEMS.getValue(rl));
   }

   public static boolean isStringBlock(String s, BlockState bl) {
      ResourceLocation rl = new ResourceLocation(s);
      TagKey<Block> tagB = BlockTags.create(rl);
      return bl.is(tagB) ? true : bl.is((Block)ForgeRegistries.BLOCKS.getValue(rl));
   }

   public static boolean isBlockInList(List<? extends String> list, BlockState toApply) {
      for (String s : list) {
         if (isStringBlock(s, toApply)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isItemInList(List<? extends String> list, ItemStack toApply) {
      for (String s : list) {
         if (isStringItem(s, toApply)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isTool(ItemStack itm) {
      if (itm.getItem().canBeDepleted()) {
         return true;
      } else if (itm.getItem() instanceof TieredItem) {
         return true;
      } else if (itm.getItem() instanceof FishingRodItem) {
         return true;
      } else {
         return itm.getItem() instanceof FlintAndSteelItem ? true : itm.getItem() instanceof ShearsItem;
      }
   }

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   public void registerScreens(FMLClientSetupEvent event) {
      event.enqueueWork(() -> registerScreen(WAND_CONTAINER, WandSelectGui::new));
   }
   
   @OnlyIn(Dist.CLIENT)
   private static <C extends AbstractContainerMenu, U extends Screen & MenuAccess<C>> void registerScreen(
      ContainerTypeRegistryObject<C> type, ScreenConstructor<C, U> factory
   ) {
      MenuScreens.register((MenuType)type.get(), factory);
   }

   @SubscribeEvent
   public void register(RegisterEvent event) {
      event.register(Keys.BLOCKS, helper -> {
         for (int i = 0; i < 15; i++) {
            if (i != 1 && i != 2) {
               ResourceLocation var10001 = new ResourceLocation("emcbaubles", names[i] + "_matter_block");
               Properties.of();
               helper.register(var10001, new Block(Properties.copy(Blocks.IRON_BLOCK).strength(0.5F, 1.5F)));
            }

            ResourceLocation var2 = new ResourceLocation("emcbaubles", "compressed_" + names[i] + "_matter_block");
            Properties.of();
            helper.register(var2, new Block(Properties.copy(Blocks.IRON_BLOCK).strength(0.5F, 1.5F)));
         }
      });
      event.register(
         Keys.ITEMS,
         helper -> {
            for (int i = 0; i < 15; i++) {
               if (i != 1 && i != 2) {
                  helper.register(new ResourceLocation("emcbaubles", names[i] + "_matter"), new Item(new net.minecraft.world.item.Item.Properties()));
                  helper.register(
                     new ResourceLocation("emcbaubles", names[i] + "_matter_block"),
                     new BlockItem(
                        (Block)((Holder)ForgeRegistries.BLOCKS.getHolder(new ResourceLocation("emcbaubles", names[i] + "_matter_block")).get()).get(),
                        new net.minecraft.world.item.Item.Properties()
                     )
                  );
               }

               helper.register(
                  new ResourceLocation("emcbaubles", "compressed_" + names[i] + "_matter_block"),
                  new BlockItem(
                     (Block)((Holder)ForgeRegistries.BLOCKS.getHolder(new ResourceLocation("emcbaubles", "compressed_" + names[i] + "_matter_block")).get())
                        .get(),
                     new net.minecraft.world.item.Item.Properties()
                  )
               );
               helper.register(new ResourceLocation("emcbaubles", "collector_mk" + (i + 1) + "_necklace"), new CollectorNecklace(neck_power[i], i + 1));
               helper.register(new ResourceLocation("emcbaubles", "flower_mk" + (i + 1) + "_charm"), new PowerFlowerCharm(flower_power[i], i + 1));
            }

            helper.register(new ResourceLocation("emcbaubles", "collector_mk16_necklace"), new CollectorNecklace(neck_power[15], 16));
            helper.register(new ResourceLocation("emcbaubles", "flower_mk16_charm"), new PowerFlowerCharm(flower_power[15], 16));
         }
      );
   }

   public static void onCreativeModeTabRegister(RegisterEvent event) {
      event.register(
         Registries.CREATIVE_MODE_TAB,
         helper -> helper.register(
               "emcbaublesctab",
               CreativeModeTab.builder()
                  .icon(() -> new ItemStack(WAND_ITEM.asItem()))
                  .title(Component.literal("EMC Baubles"))
                  .withLabelColor(16777215)
                  .displayItems((params, output) -> {
                     for (int i = 0; i < 15; i++) {
                        if (i != 1 && i != 2) {
                           output.accept((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("emcbaubles", names[i] + "_matter")));
                           output.accept((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("emcbaubles", names[i] + "_matter_block")));
                        }

                        output.accept(
                           (ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("emcbaubles", "compressed_" + names[i] + "_matter_block"))
                        );
                        output.accept((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("emcbaubles", "collector_mk" + (i + 1) + "_necklace")));
                        output.accept((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("emcbaubles", "flower_mk" + (i + 1) + "_charm")));
                     }

                     output.accept((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("emcbaubles", "collector_mk16_necklace")));
                     output.accept((ItemLike)ForgeRegistries.ITEMS.getValue(new ResourceLocation("emcbaubles", "flower_mk16_charm")));
                     output.accept(WAND_ITEM);
                     output.accept(BELT_ITEM);
                     output.accept(BRACELET_ITEM);
                  })
                  .build()
            )
      );
   }

   static {
      PacketHandler.INSTANCE
         .registerMessage(
            1,
            PacketHandler.WandNBTExchange.class,
            PacketHandler.WandNBTExchange::encode,
            PacketHandler.WandNBTExchange::decode,
            PacketHandler.WandNBTExchange::handler
         );
   }
}
