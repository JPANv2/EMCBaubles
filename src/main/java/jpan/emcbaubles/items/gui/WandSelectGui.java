package jpan.emcbaubles.items.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import jpan.emcbaubles.PacketHandler;
import jpan.emcbaubles.items.BlockReplacementWand;
import moze_intel.projecte.gameObjs.gui.PEContainerScreen;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class WandSelectGui extends PEContainerScreen<WandContainerMenu> {
   private static final ResourceLocation bg = new ResourceLocation("emcbaubles", "textures/gui/build_wand_gui_bg.png");
   private static final ResourceLocation buttons = new ResourceLocation("emcbaubles", "textures/gui/build_wand_gui_buttons.png");
   private WandContainerMenu container;
   WandSelectGui.Button exit;
   WandSelectGui.Button confirm;
   WandSelectGui.Button radius;
   WandSelectGui.Button toggleThird;
   WandSelectGui.Button clearHeld;
   WandSelectGui.ButtonCollection shapes;
   WandSelectGui.ButtonCollection modes;
   WandSelectGui.BlockButtonCollection block1;
   WandSelectGui.BlockButtonCollection block2;
   WandSelectGui.BlockButtonCollection block3;
   List<WandSelectGui.Button> children;

   public WandSelectGui(WandContainerMenu container, Inventory invPlayer, Component title) {
      super(container, invPlayer, TextComponentUtil.smartTranslate("emcb.wand.gui.title", new Object[0]));
      this.container = container;
      this.imageWidth = 255;
      this.imageHeight = 230;
      this.children = new ArrayList<>();
      this.exit = new WandSelectGui.Button(this, 230, 5, 0.8333333F, 1.0F, 0.8333333F, 1.0F, false, x -> {
         x.parent.onClose();
         return x;
      });
      this.children.add(this.exit);
      this.confirm = new WandSelectGui.Button(
         this,
         220,
         135,
         0.8333333F,
         1.0F,
         0.5F,
         0.6666667F,
         false,
         x -> {
            int pnt = ((BlockReplacementWand)x.parent.container.wand.getItem())
               .getNextPointToDefine(
                  x.parent.container.owner.getItemInHand(x.parent.container.hand),
                  !BlockReplacementWand.appliedShapes
                     .get(((BlockReplacementWand)x.parent.container.wand.getItem()).getShape(x.parent.container.owner.getItemInHand(x.parent.container.hand)))
                     .allowsThirdPoint()
               );
            LogUtils.getLogger().debug("pnt = " + pnt);
            if (pnt == -1) {
               UseOnContext useCtx = new UseOnContext(
                  x.parent.container.owner,
                  x.parent.container.hand,
                  new BlockHitResult(
                     new Vec3(
                        (double)x.parent.container.block1_pos.getX(),
                        (double)x.parent.container.block1_pos.getY(),
                        (double)x.parent.container.block1_pos.getZ()
                     ),
                     Direction.DOWN,
                     x.parent.container.block1_pos,
                     false
                  )
               );
               x.parent.container.wand.onItemUseFirst(useCtx);
            }

            x.parent.onClose();
            return x;
         }
      );
      this.children.add(this.confirm);
      this.shapes = new WandSelectGui.ButtonCollection(
         this,
         new WandSelectGui.Button(this, 190, 20, 0.5F, 0.6666667F, 0.16666667F, 0.33333334F, false, null),
         new WandSelectGui.Button(this, 210, 20, 0.5F, 0.6666667F, 0.0F, 0.16666667F, false, null),
         new WandSelectGui.Button(this, 190, 40, 0.5F, 0.6666667F, 0.5F, 0.6666667F, false, null),
         new WandSelectGui.Button(this, 210, 40, 0.5F, 0.6666667F, 0.33333334F, 0.5F, false, null),
         new WandSelectGui.Button(this, 190, 60, 0.5F, 0.6666667F, 0.8333333F, 1.0F, false, null),
         new WandSelectGui.Button(this, 210, 60, 0.5F, 0.6666667F, 0.6666667F, 0.8333333F, false, null),
         new WandSelectGui.Button(this, 190, 80, 0.16666667F, 0.33333334F, 0.8333333F, 1.0F, false, null)
      );
      this.shapes.buttons.get(container.shape).toggled = true;
      this.shapes.toggledNum = container.shape;
      this.shapes.funcToRun = x -> {
         x.parent.container.shape = (byte)((WandSelectGui.ButtonCollection)x).toggledNum;
         return x;
      };
      this.children.add(this.shapes);
      this.modes = new WandSelectGui.ButtonCollection(
         this,
         new WandSelectGui.Button(this, 5, 124, 0.16666667F, 0.33333334F, 0.0F, 0.16666667F, false, null),
         new WandSelectGui.Button(this, 25, 124, 0.16666667F, 0.33333334F, 0.16666667F, 0.33333334F, false, null),
         new WandSelectGui.Button(this, 45, 124, 0.16666667F, 0.33333334F, 0.33333334F, 0.5F, false, null),
         new WandSelectGui.Button(this, 65, 124, 0.16666667F, 0.33333334F, 0.5F, 0.6666667F, false, null),
         new WandSelectGui.Button(this, 85, 124, 0.8333333F, 1.0F, 0.6666667F, 0.8333333F, false, null),
         new WandSelectGui.Button(this, 105, 124, 0.16666667F, 0.33333334F, 0.6666667F, 0.8333333F, false, null)
      );
      this.modes.buttons.get(container.mode).toggled = true;
      this.modes.toggledNum = container.mode;
      this.modes.funcToRun = x -> {
         x.parent.container.mode = (byte)((WandSelectGui.ButtonCollection)x).toggledNum;
         return x;
      };
      this.children.add(this.modes);
      this.radius = new WandSelectGui.Button(this, 210, 100, 0.8333333F, 1.0F, 0.0F, 0.16666667F, !container.isRadius, x -> {
         x.parent.container.isRadius = !x.toggled;
         return x;
      });
      this.children.add(this.radius);
      this.toggleThird = new WandSelectGui.Button(this, 10, 90, 0.8333333F, 1.0F, 0.16666667F, 0.33333334F, !container.selectsThird, x -> {
         x.parent.container.selectsThird = !x.toggled;
         return x;
      });
      this.children.add(this.toggleThird);
      this.clearHeld = new WandSelectGui.Button(this, 190, 124, 0.6666667F, 0.8333333F, 0.8333333F, 1.0F, false, x -> {
         x.parent.container.clearPickupItem();
         x.toggled = !x.toggled;
         return x;
      });
      this.children.add(this.clearHeld);
      this.block1 = new WandSelectGui.BlockButtonCollection(this, 1, 51, 21);
      this.block2 = new WandSelectGui.BlockButtonCollection(this, 2, 51, 57);
      this.block3 = new WandSelectGui.BlockButtonCollection(this, 3, 51, 92);
      this.children.add(this.block1);
      this.children.add(this.block2);
      this.children.add(this.block3);
   }

   protected void renderBg(GuiGraphics graphics, float layer, int x, int y) {
      this.container.loadFromItemNBT(this.container.owner, this.container.owner.level(), this.container.wand);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, bg);
      innerBlit(
         graphics.pose().last().pose(),
         this.leftPos,
         this.leftPos + this.imageWidth,
         this.topPos,
         this.topPos + this.imageHeight,
         0,
         0.0F,
         1.0F,
         0.0F,
         1.0F
      );
      graphics.drawString(this.font, this.title, this.leftPos + 5, 5 + this.topPos, 4210752, false);
      this.RenderLabelsAndButtons(graphics, this.leftPos, this.topPos);
   }

   protected void renderLabels(GuiGraphics graphics, int x, int y) {
   }

   private void RenderLabelsAndButtons(GuiGraphics graphics, int x, int y) {
      graphics.drawString(this.font, TextComponentUtil.smartTranslate("emcb.wand.gui.shapes", new Object[0]), x + 190, y + 10, 4210752, false);
      graphics.drawString(this.font, TextComponentUtil.smartTranslate("emcb.wand.gui.picked", new Object[0]), x + 170, y + 115, 4210752, false);

      for (WandSelectGui.Button b : this.children) {
         b.render(graphics);
      }
   }

   public boolean mouseClicked(double x, double y, int mouseButton) {
      for (WandSelectGui.Button b : this.children) {
         if (b.isHovering((int)x, (int)y)) {
            b.toggle((int)x, (int)y);
            return true;
         }
      }

      return super.mouseClicked(x, y, mouseButton);
   }

   public void onClose() {
      this.container.saveToItemNBT(this.container.wand);
      super.onClose();
   }

   private static void innerBlit(
      Matrix4f matrix, int startX, int endX, int startY, int endY, int layer, float startTextureX, float endTextureX, float startTextureY, float endTextureY
   ) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
      bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferbuilder.vertex(matrix, (float)startX, (float)endY, (float)layer).uv(startTextureX, endTextureY).endVertex();
      bufferbuilder.vertex(matrix, (float)endX, (float)endY, (float)layer).uv(endTextureX, endTextureY).endVertex();
      bufferbuilder.vertex(matrix, (float)endX, (float)startY, (float)layer).uv(endTextureX, startTextureY).endVertex();
      bufferbuilder.vertex(matrix, (float)startX, (float)startY, (float)layer).uv(startTextureX, startTextureY).endVertex();
      BufferUploader.drawWithShader(bufferbuilder.end());
   }

   private class BlockButtonCollection extends WandSelectGui.ButtonCollection {
      public static final int REMOVE_BUTTON = 0;
      public static final int X_ADD_BUTTON = 1;
      public static final int X_REMOVE_BUTTON = 2;
      public static final int Y_ADD_BUTTON = 3;
      public static final int Y_REMOVE_BUTTON = 4;
      public static final int Y_MAX_BUTTON = 5;
      public static final int Y_MIN_BUTTON = 6;
      public static final int Z_ADD_BUTTON = 7;
      public static final int Z_REMOVE_BUTTON = 8;
      private int slotNum;

      public BlockButtonCollection(WandSelectGui par, int slotNum, int topX, int topY) {
         super(par);
         this.topPos = topY;
         this.leftPos = topX;
         this.slotNum = slotNum;
         this.buttons
            .add(WandSelectGui.this.new Button(this.parent, this.leftPos - 22, this.topPos - 2, 0.6666667F, 0.8333333F, 0.8333333F, 1.0F, false, x -> {
               x.parent.container.clearBlock(slotNum);
               x.toggled = false;
               return x;
            }));
         this.buttons
            .add(WandSelectGui.this.new MiniButton(this.parent, this.leftPos + 20, this.topPos - 10, 0.6666667F, 0.7083334F, 0.33333334F, 0.375F, false, x -> {
               BlockPos p1 = x.parent.container.getBlockPos(slotNum);
               if (p1 != null) {
                  x.parent.container.setBlockPos(slotNum, p1.offset(1, 0, 0));
               }

               x.toggled = false;
               return x;
            }));
         this.buttons
            .add(
               WandSelectGui.this.new MiniButton(
                  this.parent, this.leftPos + 20, this.topPos - 5, 0.6666667F, 0.7083334F, 0.4166667F, 0.45833334F, false, x -> {
                     BlockPos p1 = x.parent.container.getBlockPos(slotNum);
                     if (p1 != null) {
                        x.parent.container.setBlockPos(slotNum, p1.offset(-1, 0, 0));
                     }

                     x.toggled = false;
                     return x;
                  }
               )
            );
         this.buttons
            .add(WandSelectGui.this.new MiniButton(this.parent, this.leftPos + 20, this.topPos + 1, 0.6666667F, 0.7083334F, 0.33333334F, 0.375F, false, x -> {
               BlockPos p1 = x.parent.container.getBlockPos(slotNum);
               if (p1 != null) {
                  x.parent.container.setBlockPos(slotNum, p1.offset(0, 1, 0));
               }

               x.toggled = false;
               return x;
            }));
         this.buttons
            .add(
               WandSelectGui.this.new MiniButton(
                  this.parent, this.leftPos + 20, this.topPos + 6, 0.6666667F, 0.7083334F, 0.4166667F, 0.45833334F, false, x -> {
                     BlockPos p1 = x.parent.container.getBlockPos(slotNum);
                     if (p1 != null) {
                        x.parent.container.setBlockPos(slotNum, p1.offset(0, -1, 0));
                     }

                     x.toggled = false;
                     return x;
                  }
               )
            );
         this.buttons
            .add(WandSelectGui.this.new MiniButton(this.parent, this.leftPos + 20, this.topPos + 12, 0.6666667F, 0.7083334F, 0.33333334F, 0.375F, false, x -> {
               BlockPos p1 = x.parent.container.getBlockPos(slotNum);
               if (p1 != null) {
                  x.parent.container.setBlockPos(slotNum, p1.offset(0, 0, 1));
               }

               x.toggled = false;
               return x;
            }));
         this.buttons
            .add(
               WandSelectGui.this.new MiniButton(
                  this.parent, this.leftPos + 20, this.topPos + 17, 0.6666667F, 0.7083334F, 0.4166667F, 0.45833334F, false, x -> {
                     BlockPos p1 = x.parent.container.getBlockPos(slotNum);
                     if (p1 != null) {
                        x.parent.container.setBlockPos(slotNum, p1.offset(0, 0, -1));
                     }

                     x.toggled = false;
                     return x;
                  }
               )
            );
         this.buttons
            .add(WandSelectGui.this.new MiniButton(this.parent, this.leftPos + 26, this.topPos + 1, 0.75F, 0.7916667F, 0.33333334F, 0.375F, false, x -> {
               BlockPos p1 = x.parent.container.getBlockPos(slotNum);
               if (p1 != null) {
                  x.parent.container.setBlockPos(slotNum, new BlockPos(p1.getX(), x.parent.container.owner.level().getHeight(), p1.getZ()));
               }

               x.toggled = false;
               return x;
            }));
         this.buttons
            .add(WandSelectGui.this.new MiniButton(this.parent, this.leftPos + 26, this.topPos + 6, 0.75F, 0.7916667F, 0.4166667F, 0.45833334F, false, x -> {
               BlockPos p1 = x.parent.container.getBlockPos(slotNum);
               if (p1 != null) {
                  x.parent.container.setBlockPos(slotNum, new BlockPos(p1.getX(), x.parent.container.owner.level().getMinBuildHeight(), p1.getZ()));
               }

               x.toggled = false;
               return x;
            }));
      }

      @Override
      public Boolean toggle(int x, int y) {
         boolean oneToggle = false;

         for (WandSelectGui.Button b : this.buttons) {
            if (b.isHovering(x, y)) {
               b.toggle(x, y);
               oneToggle = true;
            }
         }

         if (oneToggle) {
            WandSelectGui.this.container.saveToItemNBT(WandSelectGui.this.container.wand);
            PacketHandler.INSTANCE.sendToServer(new PacketHandler.WandNBTExchange(WandSelectGui.this.container.wand));
            return true;
         } else {
            return null;
         }
      }

      @Override
      public void render(GuiGraphics graphics) {
         BlockPos p1 = this.parent.container.getBlockPos(this.slotNum);
         if (p1 == null) {
            graphics.drawString(WandSelectGui.this.font, Component.literal("X: ???"), this.absolutePosX() + 32, this.absolutePosY() - 10, 4210752, false);
            graphics.drawString(WandSelectGui.this.font, Component.literal("Y: ???"), this.absolutePosX() + 32, this.absolutePosY() + 1, 4210752, false);
            graphics.drawString(WandSelectGui.this.font, Component.literal("Z: ???"), this.absolutePosX() + 32, this.absolutePosY() + 12, 4210752, false);
         } else {
            graphics.drawString(
               WandSelectGui.this.font, Component.literal("X: " + p1.getX()), this.absolutePosX() + 32, this.absolutePosY() - 10, 4210752, false
            );
            graphics.drawString(
               WandSelectGui.this.font, Component.literal("Y: " + p1.getY()), this.absolutePosX() + 32, this.absolutePosY() + 1, 4210752, false
            );
            graphics.drawString(
               WandSelectGui.this.font, Component.literal("Z: " + p1.getZ()), this.absolutePosX() + 32, this.absolutePosY() + 12, 4210752, false
            );
         }

         for (WandSelectGui.Button b : this.buttons) {
            b.render(graphics);
         }
      }
   }

   private class Button {
      WandSelectGui parent;
      int leftPos;
      int topPos;
      float xStart;
      float xEnd;
      float yStart;
      float yEnd;
      boolean toggled;
      Function<WandSelectGui.Button, WandSelectGui.Button> funcToRun;

      protected Button() {
      }

      public Button(
         WandSelectGui gui,
         int leftPos,
         int topPos,
         float xStart,
         float xEnd,
         float yStart,
         float yEnd,
         boolean toggled,
         Function<WandSelectGui.Button, WandSelectGui.Button> funcToRun
      ) {
         this.parent = gui;
         this.leftPos = leftPos;
         this.topPos = topPos;
         this.xStart = xStart;
         this.xEnd = xEnd;
         this.yStart = yStart;
         this.yEnd = yEnd;
         this.toggled = toggled;
         this.funcToRun = funcToRun;
      }

      public boolean isHovering(int x, int y) {
         return x > this.absolutePosX() && x < this.absolutePosX() + 20 && y > this.absolutePosY() && y < this.absolutePosY() + 20;
      }

      public Boolean toggle(int x, int y) {
         if (this.isHovering(x, y)) {
            this.toggled = !this.toggled;
            if (this.funcToRun != null) {
               this.funcToRun.apply(this);
               WandSelectGui.this.container.saveToItemNBT(WandSelectGui.this.container.wand);
               PacketHandler.INSTANCE.sendToServer(new PacketHandler.WandNBTExchange(WandSelectGui.this.container.wand));
            }

            return this.toggled;
         } else {
            return null;
         }
      }

      public int absolutePosX() {
         return this.parent.leftPos + this.leftPos;
      }

      public int absolutePosY() {
         return this.parent.topPos + this.topPos;
      }

      public void render(GuiGraphics graphics) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, WandSelectGui.buttons);
         if (this.toggled) {
            WandSelectGui.innerBlit(
               graphics.pose().last().pose(),
               this.absolutePosX(),
               this.absolutePosX() + 20,
               this.absolutePosY(),
               this.absolutePosY() + 20,
               0,
               this.xStart - 0.16666667F,
               this.xEnd - 0.16666667F,
               this.yStart,
               this.yEnd
            );
         } else {
            WandSelectGui.innerBlit(
               graphics.pose().last().pose(),
               this.absolutePosX(),
               this.absolutePosX() + 20,
               this.absolutePosY(),
               this.absolutePosY() + 20,
               0,
               this.xStart,
               this.xEnd,
               this.yStart,
               this.yEnd
            );
         }
      }
   }

   private class ButtonCollection extends WandSelectGui.Button {
      List<WandSelectGui.Button> buttons = new ArrayList<>();
      int toggledNum = -1;

      public ButtonCollection(WandSelectGui par, WandSelectGui.Button... buttons) {
         this.parent = par;
         int i = 0;

         for (WandSelectGui.Button b : buttons) {
            this.buttons.add(b);
            if (b.toggled) {
               this.toggledNum = i;
            }

            i++;
         }
      }

      @Override
      public boolean isHovering(int x, int y) {
         for (WandSelectGui.Button b : this.buttons) {
            if (b.isHovering(x, y)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public Boolean toggle(int x, int y) {
         int i = 0;

         for (WandSelectGui.Button b : this.buttons) {
            if (b.isHovering(x, y)) {
               break;
            }

            i++;
         }

         if (i >= this.buttons.size()) {
            return null;
         } else if (this.buttons.get(i).toggled) {
            return true;
         } else {
            for (WandSelectGui.Button b : this.buttons) {
               b.toggled = false;
            }

            this.buttons.get(i).toggled = true;
            this.toggledNum = i;
            if (this.funcToRun != null) {
               this.funcToRun.apply(this);
               WandSelectGui.this.container.saveToItemNBT(WandSelectGui.this.container.wand);
               PacketHandler.INSTANCE.sendToServer(new PacketHandler.WandNBTExchange(WandSelectGui.this.container.wand));
            }

            return true;
         }
      }

      @Override
      public void render(GuiGraphics matrix) {
         for (WandSelectGui.Button b : this.buttons) {
            b.render(matrix);
         }
      }
   }

   private class MiniButton extends WandSelectGui.Button {
      public MiniButton(
         WandSelectGui gui,
         int leftPos,
         int topPos,
         float xStart,
         float xEnd,
         float yStart,
         float yEnd,
         boolean toggled,
         Function<WandSelectGui.Button, WandSelectGui.Button> funcToRun
      ) {
         super(gui, leftPos, topPos, xStart, xEnd, yStart, yEnd, toggled, funcToRun);
      }

      @Override
      public boolean isHovering(int x, int y) {
         return x > this.absolutePosX() && x < this.absolutePosX() + 5 && y > this.absolutePosY() && y < this.absolutePosY() + 5;
      }

      @Override
      public void render(GuiGraphics graphics) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, WandSelectGui.buttons);
         WandSelectGui.innerBlit(
            graphics.pose().last().pose(),
            this.absolutePosX(),
            this.absolutePosX() + 5,
            this.absolutePosY(),
            this.absolutePosY() + 5,
            0,
            this.xStart,
            this.xEnd,
            this.yStart,
            this.yEnd
         );
      }
   }
}
