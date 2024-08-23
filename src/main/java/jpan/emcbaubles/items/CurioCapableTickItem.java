package jpan.emcbaubles.items;

import javax.annotation.Nonnull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface CurioCapableTickItem {
   void curioTick(@Nonnull ItemStack var1, Level var2, @Nonnull Entity var3, int var4, boolean var5);
}
