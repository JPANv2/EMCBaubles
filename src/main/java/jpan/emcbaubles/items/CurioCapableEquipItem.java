package jpan.emcbaubles.items;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface CurioCapableEquipItem {
   void curioEquip(@Nonnull ItemStack var1, Level var2, @Nonnull Entity var3);

   void curioUnequip(@Nonnull ItemStack var1, Level var2, @Nonnull Entity var3);
}
