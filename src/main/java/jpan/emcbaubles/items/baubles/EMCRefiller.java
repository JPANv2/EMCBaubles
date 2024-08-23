package jpan.emcbaubles.items.baubles;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import jpan.emcbaubles.items.CurioCapableEquipItem;
import jpan.emcbaubles.items.CurioCapableTickItem;
import jpan.emcbaubles.items.CurioEquippedItemCapacity;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;

public class EMCRefiller extends Item implements CurioCapableEquipItem, CurioCapableTickItem {
   public String name = "emc_refiller";
   private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();

   public EMCRefiller() {
      super(new Properties().stacksTo(1));
      this.addItemCapability("curios", () -> CurioEquippedItemCapacity::new);
   }

   protected void addItemCapability(String modid, Supplier<Supplier<ItemCapability<?>>> capabilitySupplier) {
      if (ModList.get().isLoaded(modid)) {
         this.supportedCapabilities.add(capabilitySupplier.get());
      }
   }

   public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
      return (ICapabilityProvider)(this.supportedCapabilities.isEmpty()
         ? super.initCapabilities(stack, nbt)
         : new ItemCapabilityWrapper(stack, this.supportedCapabilities));
   }

   @Override
   public void curioEquip(ItemStack stack, Level world, Entity entity) {
      if (!world.isClientSide && entity instanceof Player player) {
         if (!player.getTags().contains("emc_refill_items")) {
            player.addTag("emc_refill_items");
         }
      }
   }

   @Override
   public void curioUnequip(ItemStack stack, Level world, Entity entity) {
      if (!world.isClientSide && entity instanceof Player player) {
         if (player.getTags().contains("emc_refill_items")) {
            player.removeTag("emc_refill_items");
         }
      }
   }

   @Override
   public void curioTick(ItemStack stack, Level world, Entity entity, int par4, boolean par5) {
   }
}
