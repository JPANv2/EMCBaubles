package jpan.emcbaubles.items.baubles;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import jpan.emcbaubles.items.CurioCapableTickItem;
import jpan.emcbaubles.items.CurioEquippedItemCapacity;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.capability.ItemCapability;
import moze_intel.projecte.capability.ItemCapabilityWrapper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.ModList;

public class CollectorNecklace extends Item implements CurioCapableTickItem {
   public final int tier;
   public long emcPerTick;
   private final List<Supplier<ItemCapability<?>>> supportedCapabilities = new ArrayList<>();

   public CollectorNecklace(long EMCPerTick, int tier) {
      super(new Properties().stacksTo(1));
      this.tier = tier;
      this.emcPerTick = EMCPerTick;
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
   public void curioTick(@Nonnull ItemStack stack, Level world, @Nonnull Entity entity, int par4, boolean par5) {
      if (!world.isClientSide && entity instanceof Player) {
         int ticks = stack.getOrCreateTag().getInt("ticks");
         ticks = (ticks + 1) % 20;
         stack.getOrCreateTag().putInt("ticks", ticks);
         if (ticks == 0) {
            Player player = (Player)entity;
            IKnowledgeProvider provider = (IKnowledgeProvider)player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, (Direction)null).orElse(null);
            if (provider != null) {
               provider.setEmc(provider.getEmc().add(BigInteger.valueOf(this.emcPerTick)));
            }

            if (player instanceof ServerPlayer serverPlayer) {
               provider.sync(serverPlayer);
            }
         }
      }
   }
}
