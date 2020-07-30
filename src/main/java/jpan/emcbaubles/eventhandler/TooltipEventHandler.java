package jpan.emcbaubles.eventhandler;

import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.items.ItemList;
import jpan.emcbaubles.items.baubles.CollectorNecklace;
import jpan.emcbaubles.items.baubles.PowerFlowerCharm;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.utils.Constants;
import net.minecraft.client.audio.SoundHandler.Loader;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EMCBaubles.ModID)
public class TooltipEventHandler {

	@SubscribeEvent
	public static void tTipEvent(ItemTooltipEvent event)
	{
		
				ItemStack current = event.getItemStack();
				Item currentItem = current.getItem();
				/**
				 * Collector ToolTips
				 */
				String unit = I18n.format("pe.emc.name");
				String rate = I18n.format("pe.emc.rate");

				if (currentItem instanceof CollectorNecklace)
				{
					/*if(Loader.isModLoaded("projectex")) {
						event.getToolTip().add(TextFormatting.DARK_PURPLE
								+ String.format(I18n.format("pe.emc.maxgenrate_tooltip")
								+ TextFormatting.BLUE + " " + EMCFormat.INSTANCE_IGNORE_SHIFT.format((long)(((CollectorNecklace)currentItem).emcPerTick)) + " " + rate));
					}else {*/
						event.getToolTip().add(new TranslationTextComponent("pe.emc.maxgenrate_tooltip").applyTextStyle(TextFormatting.DARK_PURPLE).appendText(" ")
								.appendSibling(new StringTextComponent(Constants.EMC_FORMATTER.format((long)(((CollectorNecklace)currentItem).emcPerTick))).applyTextStyle(TextFormatting.RESET)));
		
					//}
				}
				
				if (currentItem instanceof PowerFlowerCharm)
				{
					/*if(Loader.isModLoaded("projectex")) {
						event.getToolTip().add(TextFormatting.DARK_PURPLE
								+ String.format(I18n.format("pe.emc.maxgenrate_tooltip")
								+ TextFormatting.BLUE + " " + EMCFormat.INSTANCE_IGNORE_SHIFT.format((long)(((PowerFlowerCharm)currentItem).emcPerTick))+ " " + rate));
					}else {*/
					event.getToolTip().add(new TranslationTextComponent("pe.emc.maxgenrate_tooltip").applyTextStyle(TextFormatting.DARK_PURPLE).appendText(" ")
							.appendSibling(new StringTextComponent(Constants.EMC_FORMATTER.format((long)(((PowerFlowerCharm)currentItem).emcPerTick))).applyTextStyle(TextFormatting.RESET)));
					//}
				}
				
				if (currentItem == ItemList.getEMCBelt()){
					event.getToolTip().add(new StringTextComponent("Learns and converts to EMC picked up items with EMC value."));
				}
				
				if (currentItem == ItemList.getEMCRefiller()){
					event.getToolTip().add(new StringTextComponent("Automatically refills your hand when you use an item or break a tool."));
				}
		}
	
	
}
