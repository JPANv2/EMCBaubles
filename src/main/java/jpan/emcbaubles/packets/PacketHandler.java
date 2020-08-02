package jpan.emcbaubles.packets;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import jpan.emcbaubles.EMCBaubles;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

		private static final String PROTOCOL_VERSION = Integer.toString(1);
		private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder
				.named(new ResourceLocation(EMCBaubles.ModID, "main_channel"))
				.clientAcceptedVersions(PROTOCOL_VERSION::equals)
				.serverAcceptedVersions(PROTOCOL_VERSION::equals)
				.networkProtocolVersion(() -> PROTOCOL_VERSION)
				.simpleChannel();
		private static int index;

		public static void register() {
			registerMessage(PedestalPlacerSetPacket.class, PedestalPlacerSetPacket::encode, PedestalPlacerSetPacket::decode,PedestalPlacerSetPacket.Handler::handle);
			registerMessage(PedestalSyncPacket.class, PedestalSyncPacket::encode, PedestalSyncPacket::decode,PedestalSyncPacket.Handler::handle);
		} 

		private static <MSG> void registerMessage(Class<MSG> type, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder,
				BiConsumer<MSG, Supplier<Context>> consumer) {
			HANDLER.registerMessage(index++, type, encoder, decoder, consumer);
		}

		/**
		 * Sends a packet to the server.<br> Must be called Client side.
		 */
		public static void sendToServer(Object msg) {
			HANDLER.sendToServer(msg);
		}

		/**
		 * Send a packet to a specific player.<br> Must be called Server side.
		 */
		public static void sendTo(Object msg, ServerPlayerEntity player) {
			if (!(player instanceof FakePlayer)) {
				HANDLER.sendTo(msg, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
			}
		}
	
}
