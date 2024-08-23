package jpan.emcbaubles.items;

import moze_intel.projecte.capability.BasicItemCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CurioEquippedItemCapacity extends BasicItemCapability<ICurio> implements ICurio {
   public Capability<ICurio> getCapability() {
      return CuriosCapability.ITEM;
   }

   public ItemStack getStack() {
      return super.getStack();
   }

   public void curioTick(String identifier, int index, LivingEntity living) {
      if (this.getStack().getItem() instanceof CurioCapableTickItem curioCapableTickItem) {
         curioCapableTickItem.curioTick(this.getStack(), living.level(), living, index, false);
      }
   }

   public void onEquip(String identifier, int index, LivingEntity living) {
      if (this.getStack().getItem() instanceof CurioCapableEquipItem curioCapableEquipItem) {
         curioCapableEquipItem.curioEquip(this.getStack(), living.level(), living);
      }
   }

   public void onUnequip(String identifier, int index, LivingEntity living) {
      if (this.getStack().getItem() instanceof CurioCapableEquipItem curioCapableEquipItem) {
         curioCapableEquipItem.curioUnequip(this.getStack(), living.level(), living);
      }
   }
}
