package jpan.emcbaubles.items;

import moze_intel.projecte.capability.BasicItemCapability;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import top.theillusivec4.curios.api.capability.CuriosCapability;
import top.theillusivec4.curios.api.capability.ICurio;

public class CurioEquippedItemCapacity extends BasicItemCapability<ICurio> implements ICurio {

		@Override
		public Capability<ICurio> getCapability() {
			return CuriosCapability.ITEM;
		}

		@Override
		public void onCurioTick(String identifier, int index, LivingEntity living) {
			if(getStack().getItem() instanceof CurioCapableTickItem)
				((CurioCapableTickItem)(getStack().getItem())).curioTick(getStack(),living.getEntityWorld(), living, index, false);
		}
		
		@Override 
		public void onEquipped(String identifier, LivingEntity living) {
			if(getStack().getItem() instanceof CurioCapableEquipItem)
				((CurioCapableEquipItem)(getStack().getItem())).curioEquip(getStack(),living.getEntityWorld(), living);
		}
		
		@Override
		public void onUnequipped(String identifier, LivingEntity living) {
			if(getStack().getItem() instanceof CurioCapableEquipItem)
				((CurioCapableEquipItem)(getStack().getItem())).curioUnequip(getStack(),living.getEntityWorld(), living);
		}
}
