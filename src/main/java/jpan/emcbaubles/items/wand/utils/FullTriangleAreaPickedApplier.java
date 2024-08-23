package jpan.emcbaubles.items.wand.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class FullTriangleAreaPickedApplier implements AreaPickedApplier {
   @Override
   public boolean needsThirdPoint() {
      return true;
   }

   @Override
   public boolean allowsThirdPoint() {
      return true;
   }

   @Override
   public List<BlockPos> applyRadius(List<BlockPos> pos, boolean isRadius) {
      return pos;
   }

   @Override
   public List<BlockPos> getAreaPoints(ItemStack wand, UseOnContext ctx, List<BlockPos> toUse) {
      return toUse.size() < 3 ? null : drawTriangle(toUse.get(0), toUse.get(1), toUse.get(2));
   }

   // $VF: One or more variable merging failures!
   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static List<BlockPos> drawTriangle(BlockPos pp1, BlockPos pp2, BlockPos pp3) {
      List<BlockPos> ans = new ArrayList<>();
      ans.addAll(drawLine(pp1, pp2));
      ans.addAll(drawLine(pp2, pp3));
      ans.addAll(drawLine(pp3, pp1));
      List<BlockPos> trueAns = new ArrayList<>();

      for (BlockPos bp : ans) {
         if (!containsPos(trueAns, bp)) {
            trueAns.add(bp);
         }
      }

      ans = trueAns;
      trueAns = new ArrayList<>();

      for (BlockPos bpx : ans) {
         trueAns.add(bpx);

         for (BlockPos bp2 : ans) {
            if (!containsPos(trueAns, bp2)) {
               trueAns.addAll(drawLine(bpx, bp2));
            }
         }
      }

      List<BlockPos> var12 = new ArrayList();

      /*
      for (BlockPos bpx : var10_1) {
         if (!containsPos(var12, bpx)) {
            var12.add(bpx);
         }
      }*/

      return var12;
   }

   private static boolean containsPos(List<BlockPos> trueAns, BlockPos bp) {
      for (BlockPos bp2 : trueAns) {
         if (bp.getX() == bp2.getX() && bp.getY() == bp2.getY() && bp.getZ() == bp2.getZ()) {
            return true;
         }
      }

      return false;
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
