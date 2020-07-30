package jpan.emcbaubles.items;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CurioCapableTickItem {
	public void curioTick(@Nonnull ItemStack stack, World world, @Nonnull Entity entity, int par4, boolean par5);
}
