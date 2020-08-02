package jpan.emcbaubles.capabilities;

import java.util.Set;
import java.util.UUID;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPedestalPlacerWorldCapabilty {
	
	public World getWorld();
	
	public void setWorld(World toSet);
	
	public Set<BlockPos> getBlocks();
	
	public UUID getPlacer(BlockPos pos);
	
	public void setPlacer(BlockPos pos, UUID id);
	
	public void removePlacer(BlockPos pos);
}
