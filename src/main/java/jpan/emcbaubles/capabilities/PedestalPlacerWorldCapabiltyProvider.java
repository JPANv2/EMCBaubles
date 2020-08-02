package jpan.emcbaubles.capabilities;

import javax.annotation.Nonnull;

import jpan.emcbaubles.EMCBaubles;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PedestalPlacerWorldCapabiltyProvider implements ICapabilitySerializable<CompoundNBT>{
	
	private final IPedestalPlacerWorldCapabilty impl = new PedestalPlacerWorldCapabilty();
	private final LazyOptional<IPedestalPlacerWorldCapabilty> inst = LazyOptional.of(
			() -> impl);
	
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, Direction side) {
			  if (cap == EMCBaubles.PEDESTAL_PLACER_CAPABILITY)
		        {
		            return inst.cast();
		        }

		        return LazyOptional.empty();
		}

		@Override
		public CompoundNBT serializeNBT() {
			return (CompoundNBT)(EMCBaubles.PEDESTAL_PLACER_CAPABILITY.writeNBT(inst.orElse(null), null));
		}

		@Override
		public void deserializeNBT(CompoundNBT nbt) {
			EMCBaubles.PEDESTAL_PLACER_CAPABILITY.readNBT(inst.orElse(null), null, nbt);
		}
}
