package jpan.emcbaubles.eventhandler;

import jpan.emcbaubles.items.baubles.CollectorNecklace;
import jpan.emcbaubles.items.baubles.EMCPickupBelt;
import jpan.emcbaubles.items.baubles.EMCRefiller;
import jpan.emcbaubles.items.baubles.PowerFlowerCharm;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "emcbaubles",
   value = {Dist.CLIENT}
)
public class TooltipEventHandler {
   @SubscribeEvent
   public static void tTipEvent(ItemTooltipEvent event) {
      ItemStack current = event.getItemStack();
      Item currentItem = current.getItem();
      if (currentItem instanceof CollectorNecklace collectorNecklace) {
         event.getToolTip()
            .add(
               TextComponentUtil.smartTranslate("pe.emc.maxgenrate_tooltip", new Object[0])
                  .append(
                     TextComponentUtil.build(new Object[]{ChatFormatting.DARK_PURPLE, " " + Constants.EMC_FORMATTER.format(collectorNecklace.emcPerTick)})
                  )
                  .append(TextComponentUtil.build(new Object[]{ChatFormatting.RESET, ""}))
            );
      }

      if (currentItem instanceof PowerFlowerCharm powerFlowerCharm) {
         event.getToolTip()
            .add(
               TextComponentUtil.smartTranslate("pe.emc.maxgenrate_tooltip", new Object[0])
                  .append(TextComponentUtil.build(new Object[]{ChatFormatting.DARK_PURPLE, " " + Constants.EMC_FORMATTER.format(powerFlowerCharm.emcPerTick)}))
                  .append(TextComponentUtil.build(new Object[]{ChatFormatting.RESET, ""}))
            );
      }

      if (currentItem instanceof EMCPickupBelt) {
         event.getToolTip().add(TextComponentUtil.smartTranslate("emcb.belt.tooltip", new Object[0]));
      }

      if (currentItem instanceof EMCRefiller) {
         event.getToolTip().add(TextComponentUtil.smartTranslate("emcb.refill.tooltip", new Object[0]));
      }
   }
}
