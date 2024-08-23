package jpan.emcbaubles;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkEvent.Context;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
   private static final String PROTOCOL_VERSION = "1";
   public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation("emcbaubles", "net"), () -> "1", "1"::equals, "1"::equals);

   public static class WandNBTExchange {
      ItemStack wand;

      public WandNBTExchange(ItemStack toSend) {
         this.wand = toSend;
      }

      public static void encode(PacketHandler.WandNBTExchange wb, FriendlyByteBuf buf) {
         CompoundTag sv = wb.wand.save(new CompoundTag());
         buf.writeNbt(sv);
      }

      public static PacketHandler.WandNBTExchange decode(FriendlyByteBuf buff) {
         return new PacketHandler.WandNBTExchange(ItemStack.of(buff.readNbt()));
      }

      public static void handler(PacketHandler.WandNBTExchange wb, Supplier<Context> ctx) {
         ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            sender.getInventory().setItem(sender.getInventory().selected, wb.wand);
         });
         ctx.get().setPacketHandled(true);
      }
   }
}
