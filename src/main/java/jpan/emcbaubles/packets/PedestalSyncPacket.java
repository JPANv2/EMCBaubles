package jpan.emcbaubles.packets;

import java.util.function.Supplier;

import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.capabilities.IPedestalPlacerWorldCapabilty;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class PedestalSyncPacket {

		public PedestalSyncPacket() {
			
		}

		public static void encode(PedestalSyncPacket msg, PacketBuffer buf) {
			
		}

		public static PedestalSyncPacket decode(PacketBuffer buf) {
			return new PedestalSyncPacket();
		}

		public static class Handler {

			public static void handle(final PedestalSyncPacket pkt, Supplier<NetworkEvent.Context> ctx) {
				ctx.get().enqueueWork(() -> {
					IPedestalPlacerWorldCapabilty cap = ctx.get().getSender().getEntityWorld().getCapability(EMCBaubles.PEDESTAL_PLACER_CAPABILITY).orElse(null);
					if(cap == null) return;
					for(BlockPos key: cap.getBlocks()) {
						PacketHandler.sendTo(new PedestalPlacerSetPacket(key, cap.getPlacer(key)), ctx.get().getSender());
					}
				});
				ctx.get().setPacketHandled(true);
			}
		}
	
}
