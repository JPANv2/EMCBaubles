package jpan.emcbaubles.items;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CurioCapableEquipItem {
	public void curioEquip(@Nonnull ItemStack stack, World world, @Nonnull Entity entity);
	public void curioUnequip(@Nonnull ItemStack stack, World world, @Nonnull Entity entity);
}
