package jpan.emcbaubles.eventhandler;

import jpan.emcbaubles.EMCBaubles;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;

public class PedestalPlacersWorldSaveData extends WorldSavedData {

	public PedestalPlacersWorldSaveData() {
		super("pedestalPlacers");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void read(CompoundNBT nbt) {
		CompoundNBT ans;
		if(nbt.contains("pedestalPlacersNBT")) {
			ans = (CompoundNBT) nbt.get("pedestalPlacersNBT");
		}else {
			ans = nbt;
		}
		for(String key: ans.keySet()) {
			//LogManager.getLogger(EMCBaubles.ModID).info("Read: "+ key);
			String[] i = key.split(",");
			BlockPos b = new BlockPos(Integer.parseInt(i[0]),Integer.parseInt(i[1]),Integer.parseInt(i[2]));
			String trueKey = "" + b.getX()+"," + b.getY()+","+ b.getZ()+",";
			//LogManager.getLogger(EMCBaubles.ModID).info("ID: "+ ans.getUniqueId(trueKey));
			EMCBaubles.pedestalPlacers.put(b, ans.getUniqueId(trueKey));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT ans = new CompoundNBT();
		for(BlockPos b: EMCBaubles.pedestalPlacers.keySet()) {
			/*LogManager.getLogger(EMCBaubles.ModID).info("Write: " + b.getX()+"," + b.getY()+","+ b.getZ());
			LogManager.getLogger(EMCBaubles.ModID).info("ID: "+  EMCBaubles.pedestalPlacers.get(b));*/
			ans.putUniqueId("" + b.getX()+"," + b.getY()+","+ b.getZ()+",", EMCBaubles.pedestalPlacers.get(b));
		}
		if(compound == null)
			return ans;
		compound.put("pedestalPlacersNBT", ans);
		return compound;
	}

}
