package jpan.emcbaubles.items.gui;

import java.math.BigInteger;
import jpan.emcbaubles.items.ItemList;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.gameObjs.container.PEHandContainer;
import moze_intel.projecte.gameObjs.container.slots.HotBarSlot;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.gameObjs.container.slots.MainInventorySlot;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class WandContainerMenu extends PEHandContainer {
   public static final byte MODE_PICK = 0;
   public static final byte MODE_FILL = 2;
   public static final byte MODE_ERASE = 1;
   public static final byte MODE_REPLACE = 3;
   public static final byte MODE_USE = 4;
   public static final byte SHAPE_SQUARE = 1;
   public static final byte SHAPE_SQUARE_FILLED = 0;
   public static final byte SHAPE_TRIANGLE = 5;
   public static final byte SHAPE_TRIANGLE_FILLED = 4;
   public static final byte SHAPE_CIRCLE = 2;
   public static final byte SHAPE_CIRCLE_FILLED = 3;
   public static final byte SHAPE_LINE = 6;
   public byte mode;
   public byte shape;
   public boolean isRadius;
   public boolean selectsThird;
   public BlockState block1;
   public BlockPos block1_pos;
   public BlockState block2;
   public BlockPos block2_pos;
   public BlockState block3;
   public BlockPos block3_pos;
   public BlockState picked;
   public BlockPos picked_pos;
   private final boolean immutable;
   public Player owner;
   public ItemStack wand;
   public InteractionHand hand;

   public static WandContainerMenu fromNetwork(int windowId, Inventory playerInv, FriendlyByteBuf buf) {
      return new WandContainerMenu(
         windowId, playerInv, (InteractionHand)buf.readEnum(InteractionHand.class), new ItemStackHandler(104), buf.readByte(), buf.readBoolean()
      );
   }

   public WandContainerMenu(int windowId, Inventory playerInv, InteractionHand hand, IItemHandlerModifiable invBag, int selected, boolean immutable) {
      super(ItemList.WAND_CONTAINER, windowId, playerInv, hand, selected);
      this.owner = playerInv.player;
      this.wand = this.owner.getItemInHand(hand);
      this.hand = hand;
      this.immutable = immutable;
      this.addSlot(new InventoryContainerSlot(invBag, 0, 169, 126));
      this.addSlot(new InventoryContainerSlot(invBag, 1, 51, 21));
      this.addSlot(new InventoryContainerSlot(invBag, 2, 51, 57));
      this.addSlot(new InventoryContainerSlot(invBag, 3, 51, 92));
      this.addPlayerInventory(48, 152);
      this.loadFromItemNBT(playerInv.player, playerInv.player.level(), playerInv.getItem(selected));
   }

   protected MainInventorySlot createMainInventorySlot(@NotNull Inventory inv, int index, int x, int y) {
      return this.immutable ? new MainInventorySlot(inv, index, x, y) {
         public boolean mayPickup(@NotNull Player player) {
            return false;
         }

         public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
         }
      } : super.createMainInventorySlot(inv, index, x, y);
   }

   protected HotBarSlot createHotBarSlot(@NotNull Inventory inv, int index, int x, int y) {
      return this.immutable ? new HotBarSlot(inv, index, x, y) {
         public boolean mayPickup(@NotNull Player player) {
            return false;
         }

         public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
         }
      } : super.createHotBarSlot(inv, index, x, y);
   }

   @NotNull
   public ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
      this.setItemOnPickSlot(player.getInventory().getItem(slotIndex));
      return ItemStack.EMPTY;
   }

   protected void setItemOnPickSlot(ItemStack pickSlot) {
      ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).set(pickSlot.copy());
      ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem().setCount(1);
      ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).setChanged();
      this.saveToItemNBT(this.wand);
      this.broadcastChanges();
   }

   protected void clearPickupItem() {
      ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).set(ItemStack.EMPTY);
      ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).setChanged();
      if (this.picked != null) {
         this.picked = null;
         this.picked_pos = null;
      }

      this.saveToItemNBT(this.wand);
      this.broadcastChanges();
   }

   public void clicked(int slotId, int dragType, @NotNull ClickType clickType, @NotNull Player player) {
      this.wand = player.getItemInHand(this.hand);
      this.loadFromItemNBT(player, player.level(), this.wand);
      if (slotId != 0) {
         if (slotId <= 0 || slotId >= 4) {
            if (!this.immutable) {
               super.clicked(slotId, dragType, clickType, player);
            }
         }
      } else if (((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem() != null
         && !((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem().isEmpty()
         && !((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem().is(Items.AIR)) {
         if (this.getCarried().isEmpty()
            || ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem().getItem().equals(this.getCarried().getItem())
               && this.getCarried().getCount() < ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem().getMaxStackSize()) {
            ItemStack itm = ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem().copy();
            itm.setCount(1);
            long emc = EMCHelper.getEmcValue(itm);
            IKnowledgeProvider provider = (IKnowledgeProvider)player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY, null).orElse(null);
            if (provider != null) {
               int maxCount = player.isShiftKeyDown() ? itm.getMaxStackSize() - this.getCarried().getCount() : 1;
               if (provider.getEmc().compareTo(BigInteger.valueOf(emc)) >= 0) {
                  do {
                     provider.setEmc(provider.getEmc().subtract(BigInteger.valueOf(emc)));
                     if (this.getCarried().isEmpty()) {
                        this.setCarried(itm);
                     } else {
                        this.getCarried().setCount(this.getCarried().getCount() + 1);
                     }
                  } while (--maxCount > 0 && provider.getEmc().compareTo(BigInteger.valueOf(emc)) >= 0);
               }
            }
         } else {
            ItemStack itm = this.getCarried().copy();
            itm.setCount(1);
            long emc = EMCHelper.getEmcValue(itm);
            if (emc > 0L) {
               this.setItemOnPickSlot(this.getCarried());
            }
         }
      } else {
         ItemStack itm = this.getCarried().copy();
         itm.setCount(1);
         long emc = EMCHelper.getEmcValue(itm);
         if (emc > 0L) {
            this.setItemOnPickSlot(this.getCarried());
         }
      }
   }

   protected int getStatus() {
      return 1 + (this.isRadius ? 2 : 0) + (this.selectsThird ? 4 : 0) + ((this.mode & 7) << 3) + ((this.shape & 7) << 6);
   }

   protected void setStatus(int status) {
      status >>= 1;
      this.isRadius = (status & 1) == 1;
      status >>= 1;
      this.selectsThird = (status & 1) == 1;
      status >>= 1;
      this.mode = (byte)(status & 7);
      status >>= 3;
      this.shape = (byte)(status & 7);
   }

   public WandContainerMenu loadFromItemNBT(Player p, Level world, ItemStack itm) {
      if (!itm.getOrCreateTag().contains("wand_mode")) {
         return this.resetWand();
      } else {
         int status = itm.getTag().getInt("wand_mode");
         if (status == 0) {
            return this.resetWand();
         } else {
            status >>= 1;
            this.isRadius = (status & 1) == 1;
            status >>= 1;
            this.selectsThird = (status & 1) == 1;
            status >>= 1;
            this.mode = (byte)(status & 7);
            status >>= 3;
            this.shape = (byte)(status & 7);
            if (itm.getTag().contains("block1_pos")) {
               int[] bPos = itm.getTag().getIntArray("block1_pos");
               this.block1_pos = new BlockPos(bPos[0], bPos[1], bPos[2]);
               this.block1 = world.getBlockState(this.block1_pos);
            } else {
               this.block1_pos = null;
               this.block1 = null;
            }

            if (itm.getTag().contains("block2_pos")) {
               int[] bPos = itm.getTag().getIntArray("block2_pos");
               this.block2_pos = new BlockPos(bPos[0], bPos[1], bPos[2]);
               this.block2 = world.getBlockState(this.block2_pos);
            } else {
               this.block2_pos = null;
               this.block2 = null;
            }

            if (itm.getTag().contains("block3_pos") && this.selectsThird) {
               int[] bPos = itm.getTag().getIntArray("block3_pos");
               this.block3_pos = new BlockPos(bPos[0], bPos[1], bPos[2]);
               this.block3 = world.getBlockState(this.block3_pos);
            } else {
               this.block3_pos = null;
               this.block3 = null;
            }

            if (itm.getTag().contains("picked_block")) {
               int[] bPos = itm.getTag().getIntArray("picked_block");
               this.picked_pos = new BlockPos(bPos[0], bPos[1], bPos[2]);
               this.picked = world.getBlockState(this.picked_pos);
               ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).set(new ItemStack(this.picked.getBlock(), 1));
            } else if (itm.getTag().contains("picked_item")) {
               this.picked = null;
               ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).set(ItemStack.of((CompoundTag)itm.getTag().get("picked_item")));
            }

            this.updateBlocksByPos();
            return this;
         }
      }
   }

   public void saveToItemNBT(ItemStack itm) {
      int status = 1 + (this.isRadius ? 2 : 0) + (this.selectsThird ? 4 : 0) + ((this.mode & 7) << 3) + ((this.shape & 7) << 6);
      itm.getOrCreateTag().putInt("wand_mode", status);
      if (this.picked != null) {
         itm.getTag().putIntArray("picked_block", new int[]{this.picked_pos.getX(), this.picked_pos.getY(), this.picked_pos.getZ()});
      } else if (itm.getTag().contains("picked_block")) {
         itm.getTag().remove("picked_block");
      }

      if (((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem() != null) {
         itm.getTag().put("picked_item", ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).getItem().save(new CompoundTag()));
      } else if (itm.getTag().contains("picked_item")) {
         itm.getTag().remove("picked_item");
      }

      if (this.block1_pos != null) {
         itm.getTag().putIntArray("block1_pos", new int[]{this.block1_pos.getX(), this.block1_pos.getY(), this.block1_pos.getZ()});
      } else if (itm.getTag().contains("block1_pos")) {
         itm.getTag().remove("block1_pos");
      }

      if (this.block2_pos != null) {
         itm.getTag().putIntArray("block2_pos", new int[]{this.block2_pos.getX(), this.block2_pos.getY(), this.block2_pos.getZ()});
      } else if (itm.getTag().contains("block2_pos")) {
         itm.getTag().remove("block2_pos");
      }

      if (this.selectsThird && this.block3_pos != null) {
         itm.getTag().putIntArray("block3_pos", new int[]{this.block3_pos.getX(), this.block3_pos.getY(), this.block3_pos.getZ()});
      } else if (itm.getTag().contains("block3_pos")) {
         itm.getTag().remove("block3_pos");
      }
   }

   public WandContainerMenu resetWand() {
      this.mode = 0;
      this.shape = 0;
      this.isRadius = false;
      this.selectsThird = false;
      this.block1 = null;
      this.block1_pos = null;
      this.block2 = null;
      this.block2_pos = null;
      this.block3 = null;
      this.block3_pos = null;
      this.picked = null;
      ((InventoryContainerSlot)this.inventoryContainerSlots.get(0)).set(ItemStack.EMPTY);
      return this;
   }

   public void updateBlocksByPos() {
      if (this.block1_pos != null) {
         this.block1 = this.owner.level().getBlockState(this.block1_pos);
         this.getSlot(1).set(this.block1.getCloneItemStack(null, this.owner.level(), this.block1_pos, this.owner));
      } else {
         this.block1 = null;
         this.getSlot(1).set(ItemStack.EMPTY);
      }

      if (this.block2_pos != null) {
         this.block2 = this.owner.level().getBlockState(this.block2_pos);
         this.getSlot(2).set(this.block2.getCloneItemStack(null, this.owner.level(), this.block2_pos, this.owner));
      } else {
         this.block2 = null;
         this.getSlot(2).set(ItemStack.EMPTY);
      }

      if (this.block3_pos != null && this.selectsThird) {
         this.block3 = this.owner.level().getBlockState(this.block3_pos);
         this.getSlot(3).set(this.block3.getCloneItemStack(null, this.owner.level(), this.block3_pos, this.owner));
      } else {
         this.block3 = null;
         this.getSlot(3).set(ItemStack.EMPTY);
      }

      this.saveToItemNBT(this.wand);
   }

   public BlockPos getBlockPos(int slotNum) {
      switch (slotNum) {
         case 1:
            return this.block1_pos;
         case 2:
            return this.block2_pos;
         case 3:
            return this.block3_pos;
         default:
            return null;
      }
   }

   public void setBlockPos(int slotNum, BlockPos ps) {
      switch (slotNum) {
         case 1:
            this.block1_pos = ps;
            break;
         case 2:
            this.block2_pos = ps;
            break;
         case 3:
            this.block3_pos = ps;
      }

      this.updateBlocksByPos();
   }

   public void clearBlock(int slotNum) {
      switch (slotNum) {
         case 1:
            this.block1_pos = null;
            break;
         case 2:
            this.block2_pos = null;
            break;
         case 3:
            this.block3_pos = null;
      }

      this.updateBlocksByPos();
   }
}
