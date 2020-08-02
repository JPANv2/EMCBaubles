package jpan.emcbaubles.capabilities;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import jpan.emcbaubles.packets.PacketHandler;
import jpan.emcbaubles.packets.PedestalPlacerSetPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PedestalPlacerWorldCapabilty implements IPedestalPlacerWorldCapabilty{

	World world;
	
	HashMap<BlockPos, UUID> placers = new HashMap<>();
	
	@Override
	public World getWorld() {
		
		return world;
	}
	
	@Override
	public void setWorld(World toSet) {
		world = toSet;
		
	}
	
	@Override
	public Set<BlockPos> getBlocks() {
		
		return placers.keySet();
	}

	@Override
	public UUID getPlacer(BlockPos pos) {
		if(!placers.containsKey(pos))
			return new UUID(0,0);
		return placers.get(pos);
	}

	@Override
	public void setPlacer(BlockPos pos, UUID id) {
		if(id == null || id.equals(new UUID(0,0)))
			removePlacer(pos);
		placers.put(pos, id);
		sendToAll(pos, id);
	}

	@Override
	public void removePlacer(BlockPos pos) {
		if(placers.containsKey(pos))
			placers.remove(pos);
		
		sendToAll(pos, new UUID(0,0));
	}
	
	private void sendToAll(BlockPos pos, UUID id) {
		PacketHandler.sendToServer(new PedestalPlacerSetPacket(pos,id));
		for(PlayerEntity p : world.getPlayers()) {
			if(p instanceof ServerPlayerEntity)
			PacketHandler.sendTo(new PedestalPlacerSetPacket(pos,id), (ServerPlayerEntity)p);
		}
	}

}
