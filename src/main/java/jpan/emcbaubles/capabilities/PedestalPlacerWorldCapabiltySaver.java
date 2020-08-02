package jpan.emcbaubles.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

public class PedestalPlacerWorldCapabiltySaver implements  Capability.IStorage<IPedestalPlacerWorldCapabilty>{

	@Override
	public INBT writeNBT(Capability<IPedestalPlacerWorldCapabilty> capability, IPedestalPlacerWorldCapabilty instance,
			Direction side) {
		CompoundNBT ans = new CompoundNBT();
		for(BlockPos b: instance.getBlocks()) {
			/*LogManager.getLogger(EMCBaubles.ModID).info("Write: " + b.getX()+"," + b.getY()+","+ b.getZ());
			LogManager.getLogger(EMCBaubles.ModID).info("ID: "+  EMCBaubles.pedestalPlacers.get(b));*/
			ans.putUniqueId("" + b.getX()+"," + b.getY()+","+ b.getZ()+",", instance.getPlacer(b));
		}
		return ans;
	}

	@Override
	public void readNBT(Capability<IPedestalPlacerWorldCapabilty> capability, IPedestalPlacerWorldCapabilty instance,
			Direction side, INBT nbt) {
		if(!(nbt instanceof CompoundNBT))
			return;
		CompoundNBT nbt2 = (CompoundNBT)nbt;
		CompoundNBT ans;
		if(nbt2.contains("pedestalPlacersNBT")) {
			ans = (CompoundNBT) nbt2.get("pedestalPlacersNBT");
		}else {
			ans = nbt2;
		}
		for(String key: ans.keySet()) {
			//LogManager.getLogger(EMCBaubles.ModID).info("Read: "+ key);
			String[] i = key.split(",");
			BlockPos b = new BlockPos(Integer.parseInt(i[0]),Integer.parseInt(i[1]),Integer.parseInt(i[2]));
			String trueKey = "" + b.getX()+"," + b.getY()+","+ b.getZ()+",";
			//LogManager.getLogger(EMCBaubles.ModID).info("ID: "+ ans.getUniqueId(trueKey));
			instance.setPlacer(b, ans.getUniqueId(trueKey));
		}
	}

}
