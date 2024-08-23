package jpan.emcbaubles.eventhandler;

import java.math.BigInteger;
import java.util.List;
import jpan.emcbaubles.EMCBaubles;
import jpan.emcbaubles.items.ItemList;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "emcbaubles"
)
public class EMCBaubleItemPickup {
   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onPlayerPickUpItemWithEMC(EntityItemPickupEvent event) {
      boolean destroyIfFull = true;
      if (event.getEntity() instanceof Player) {
         Player player = event.getEntity();
         if (player.getTags().contains("auto_emc_convert")) {
            ItemStack itemToCheck = event.getItem().getItem();
            long emc = EMCHelper.getEmcSellValue(itemToCheck);
            if (emc > 0L) {
               for (String s : (List<String>)(EMCBaubles.serverConfig.belt.blacklist.get())) {
                  if (ItemList.isStringItem(s, itemToCheck)) {
                     destroyIfFull = false;
                  }
               }

               for (String sx : (List<String>)(EMCBaubles.serverConfig.belt.true_blacklist.get())) {
                  if (ItemList.isStringItem(sx, itemToCheck)) {
                     return;
                  }
               }

               if (!(Boolean)EMCBaubles.serverConfig.belt.ignore_tools.get() || !ItemList.isTool(itemToCheck)) {
                  if ((Boolean)EMCBaubles.serverConfig.belt.fill_first.get() && player.getInventory().contains(itemToCheck)) {
                     placeInInventoryIfSpace(player.getInventory(), itemToCheck);
                     if (itemToCheck.getCount() <= 0) {
                        player.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                     }
                  } else if (!destroyIfFull) {
                     player.getInventory().add(itemToCheck);
                  } else {
                     IKnowledgeProvider provider = (IKnowledgeProvider)player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, null).orElse(null);
                     if (provider != null) {
                        handleKnowledge(provider, player, itemToCheck);
                        addEmc(provider, player, (double)(emc * (long)itemToCheck.getCount()));
                        itemToCheck.setCount(0);
                        if (player instanceof ServerPlayer serverPlayer) {
                           provider.sync(serverPlayer);
                        }

                        player.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean placeInInventoryIfSpace(Inventory inventory, ItemStack itemToCheck) {
      ItemStack itm = itemToCheck.copy();
      itm.setCount(1);

      for (int slot = inventory.getSlotWithRemainingSpace(itemToCheck); slot > 0 && slot < inventory.getContainerSize(); slot = inventory.getSlotWithRemainingSpace(itemToCheck)) {
         inventory.getItem(slot).setCount(inventory.getItem(slot).getCount() + 1);
         itemToCheck.setCount(itemToCheck.getCount() - 1);
         if (itemToCheck.getCount() <= 0) {
            return true;
         }
      }

      return false;
   }

   public static void handleKnowledge(IKnowledgeProvider provider, Player player, ItemStack itm) {
      ItemStack stack = itm.copy();
      if (stack.getCount() > 1) {
         stack.setCount(1);
      }

      if (stack.isDamaged()) {
         stack.setDamageValue(0);
      }

      if (!provider.hasKnowledge(stack)) {
         provider.addKnowledge(stack);
      }
   }

   public static boolean addEmc(IKnowledgeProvider provider, Player player, double value) {
      provider.setEmc(provider.getEmc().add(BigInteger.valueOf(Math.round(value))));
      if (player instanceof ServerPlayer serverPlayer) {
         PlayerHelper.updateScore(serverPlayer, PlayerHelper.SCOREBOARD_EMC, provider.getEmc());
      }

      return true;
   }
}
