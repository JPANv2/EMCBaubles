package jpan.emcbaubles.items.wand.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class FullSquareAreaPickedApplier implements AreaPickedApplier {
   @Override
   public boolean needsThirdPoint() {
      return false;
   }

   @Override
   public boolean allowsThirdPoint() {
      return false;
   }

   @Override
   public List<BlockPos> getAreaPoints(ItemStack wand, UseOnContext ctx, List<BlockPos> toUse) {
      return toUse.size() < 2 ? null : drawSquare(toUse.get(0), toUse.get(1));
   }

   public static List<BlockPos> drawSquare(BlockPos pp1, BlockPos pp2) {
      List<BlockPos> trueAns = new ArrayList<>();
      int startX = Math.min(pp1.getX(), pp2.getX());
      int startY = Math.min(pp1.getY(), pp2.getY());
      int startZ = Math.min(pp1.getZ(), pp2.getZ());
      int endX = Math.max(pp1.getX(), pp2.getX());
      int endY = Math.max(pp1.getY(), pp2.getY());
      int endZ = Math.max(pp1.getZ(), pp2.getZ());

      for (int y = startY; y <= endY; y++) {
         for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
               BlockPos cur = new BlockPos(x, y, z);
               trueAns.add(cur);
            }
         }
      }

      return trueAns;
   }

   public static List<BlockPos> drawLine(BlockPos pp1, BlockPos pp2) {
      List<BlockPos> ans = new ArrayList<>();
      ans.add(pp1);
      int dx = Math.abs(pp2.getX() - pp1.getX());
      int dy = Math.abs(pp2.getY() - pp1.getY());
      int dz = Math.abs(pp2.getZ() - pp1.getZ());
      int xs;
      if (pp2.getX() > pp1.getX()) {
         xs = 1;
      } else {
         xs = -1;
      }

      int ys;
      if (pp2.getY() > pp1.getY()) {
         ys = 1;
      } else {
         ys = -1;
      }

      int zs;
      if (pp2.getZ() > pp1.getZ()) {
         zs = 1;
      } else {
         zs = -1;
      }

      int x1 = pp1.getX();
      int x2 = pp2.getX();
      int y1 = pp1.getY();
      int y2 = pp2.getY();
      int z1 = pp1.getZ();
      int z2 = pp2.getZ();
      if (dx >= dy && dx >= dz) {
         int p1 = 2 * dy - dx;
         int p2 = 2 * dz - dx;

         while (x1 != x2) {
            x1 += xs;
            if (p1 >= 0) {
               y1 += ys;
               p1 -= 2 * dx;
            }

            if (p2 >= 0) {
               z1 += zs;
               p2 -= 2 * dx;
            }

            p1 += 2 * dy;
            p2 += 2 * dz;
            ans.add(new BlockPos(x1, y1, z1));
         }
      } else if (dy >= dx && dy >= dz) {
         int p1 = 2 * dx - dy;
         int p2 = 2 * dz - dy;

         while (y1 != y2) {
            y1 += ys;
            if (p1 >= 0) {
               x1 += xs;
               p1 -= 2 * dy;
            }

            if (p2 >= 0) {
               z1 += zs;
               p2 -= 2 * dy;
            }

            p1 += 2 * dx;
            p2 += 2 * dz;
            ans.add(new BlockPos(x1, y1, z1));
         }
      } else {
         int p1 = 2 * dy - dz;
         int p2 = 2 * dx - dz;

         while (z1 != z2) {
            z1 += zs;
            if (p1 >= 0) {
               y1 += ys;
               p1 -= 2 * dz;
            }

            if (p2 >= 0) {
               x1 += xs;
               p2 -= 2 * dz;
            }

            p1 += 2 * dy;
            p2 += 2 * dx;
            ans.add(new BlockPos(x1, y1, z1));
         }
      }

      return ans;
   }
}
