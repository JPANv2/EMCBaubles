package jpan.emcbaubles.eventhandler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "emcbaubles"
)
public class EMCBaubleItemRefill {
   private static ItemStack transmutation_table = null;
   public static List<String> nonRefillable = new ArrayList<>();

   @SubscribeEvent
   public static void onItemBreak(PlayerDestroyItemEvent event) {
      ItemStack itemToCheck = event.getOriginal();
      ItemStack itmCopy = itemToCheck.copy();
      if (itmCopy.isDamageableItem()) {
         itmCopy.setDamageValue(0);
      }

      itmCopy.setCount(1);
      Player p = event.getEntity();
      if (p.getTags().contains("emc_refill_items") && EMCHelper.doesItemHaveEmc(itmCopy)) {
         IKnowledgeProvider provider = (IKnowledgeProvider)p.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, null).orElse(null);
         if (provider == null) {
            return;
         }

         if (provider.hasKnowledge(itmCopy) && provider.getEmc().compareTo(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))) > 0) {
            provider.setEmc(provider.getEmc().subtract(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))));
            if (event.getOriginal().isDamageableItem()) {
               event.getOriginal().setDamageValue(0);
            }

            event.getOriginal().setCount(1);

            while (
               event.getOriginal().getMaxStackSize() > event.getOriginal().getCount()
                  && provider.getEmc().compareTo(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))) > 0
            ) {
               provider.setEmc(provider.getEmc().subtract(BigInteger.valueOf(EMCHelper.getEmcValue(itmCopy))));
               event.getOriginal().setCount(1 + event.getOriginal().getCount());
            }

            p.setItemInHand(event.getHand(), event.getOriginal());
            if (p instanceof ServerPlayer serverPlayer) {
               provider.sync(serverPlayer);
            }
         }
      }
   }
}
