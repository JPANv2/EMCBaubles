package jpan.emcbaubles.packets;

import java.util.UUID;
import java.util.function.Supplier;

import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.capabilities.IPedestalPlacerWorldCapabilty;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class PedestalPlacerSetPacket {
	public final BlockPos pos;
	public final UUID playerID;

	public PedestalPlacerSetPacket(BlockPos pos, UUID playerID) {
		this.pos = pos;
		this.playerID = playerID;
	}

	public static void encode(PedestalPlacerSetPacket msg, PacketBuffer buf) {
		buf.writeVarInt(msg.pos.getX());
		buf.writeVarInt(msg.pos.getY());
		buf.writeVarInt(msg.pos.getZ());
		buf.writeLongLE(msg.playerID.getLeastSignificantBits());
		buf.writeLongLE(msg.playerID.getMostSignificantBits());
	}

	public static PedestalPlacerSetPacket decode(PacketBuffer buf) {
		int x = buf.readVarInt();
		int y = buf.readVarInt();
		int z = buf.readVarInt();
		long least = buf.readLongLE();
		long most = buf.readLongLE();
		return new PedestalPlacerSetPacket(new BlockPos(x,y,z), new UUID(most,least));
	}

	public static class Handler {

		public static void handle(final PedestalPlacerSetPacket pkt, Supplier<NetworkEvent.Context> ctx) {
			ctx.get().enqueueWork(() -> {
				IPedestalPlacerWorldCapabilty cap = ctx.get().getSender().getEntityWorld().getCapability(EMCBaubles.PEDESTAL_PLACER_CAPABILITY).orElse(null);
				if(cap == null) return;
				UUID id = cap.getPlacer(pkt.pos);
				if(id != null && id.equals(pkt.playerID))
					return;
				cap.setPlacer(pkt.pos, pkt.playerID);
			});
			ctx.get().setPacketHandled(true);
		}
	}
}
