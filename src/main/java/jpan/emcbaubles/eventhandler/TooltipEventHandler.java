package jpan.emcbaubles.eventhandler;

import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.items.ItemList;
import jpan.emcbaubles.items.baubles.CollectorNecklace;
import jpan.emcbaubles.items.baubles.PowerFlowerCharm;
import moze_intel.projecte.utils.Constants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EMCBaubles.ModID , value = Dist.CLIENT)
public class TooltipEventHandler {

	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event) {

		ItemStack current = event.getItemStack();
		Item currentItem = current.getItem();
		/**
		 * Collector ToolTips
		 */

		if (currentItem instanceof CollectorNecklace) {

			event.getToolTip().add(new TranslationTextComponent("pe.emc.maxgenrate_tooltip")
					.applyTextStyle(TextFormatting.DARK_PURPLE).appendText(" ")
					.appendSibling(new StringTextComponent(
							Constants.EMC_FORMATTER.format((long) (((CollectorNecklace) currentItem).emcPerTick)))
									.applyTextStyle(TextFormatting.RESET)));

		}

		if (currentItem instanceof PowerFlowerCharm) {

			event.getToolTip().add(new TranslationTextComponent("pe.emc.maxgenrate_tooltip")
					.applyTextStyle(TextFormatting.DARK_PURPLE).appendText(" ")
					.appendSibling(new StringTextComponent(
							Constants.EMC_FORMATTER.format((long) (((PowerFlowerCharm) currentItem).emcPerTick)))
									.applyTextStyle(TextFormatting.RESET)));

		}

		if (currentItem == ItemList.getEMCBelt()) {
			event.getToolTip()
					.add(new StringTextComponent("Learns and converts to EMC picked up items with EMC value."));
		}

		if (currentItem == ItemList.getEMCRefiller()) {
			event.getToolTip().add(
					new StringTextComponent("Automatically refills your hand when you use an item or break a tool."));
		}
	}

}
