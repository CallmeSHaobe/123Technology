package com.newmaa.othtech.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.materials.OTHEntangled;

import codechicken.nei.api.API;
import codechicken.nei.api.IFluidRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.util.GTUtility;
import gregtech.client.handler.CondensateAnimationTickHandler;
import gregtech.common.items.ItemFluidDisplay;
import gregtech.common.render.FluidDisplayStackRenderer;

@SideOnly(Side.CLIENT)
public class OTEEntangledFluidRenderer extends FluidDisplayStackRenderer {

    private static final OTEEntangledFluidRenderer RENDERER = new OTEEntangledFluidRenderer();
    private static final float OVERLAY_ALPHA = 0.9F;
    private static final float FRAMES = 64.0F;

    public static void register() {
        MinecraftForgeClient.registerItemRenderer(ItemList.Display_Fluid.getItem(), RENDERER);
        for (OTHEntangled type : OTHEntangled.getAll()) {
            final Fluid fluid = type.getEntangledFluid();
            if (fluid == null) continue;
            API.registerFluidRenderer(fluid, new EntangledNeiRenderer(fluid));
        }
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        super.renderItem(type, item, data);

        if (type != ItemRenderType.INVENTORY || item == null || !(item.getItem() instanceof ItemFluidDisplay)) return;

        final Fluid fluid = FluidRegistry.getFluid(item.getItemDamage());
        if (!OTHEntangled.isEntangled(fluid)) return;

        drawCondensateOverlay();
    }

    private static void drawCondensateOverlay() {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(CondensateAnimationTickHandler.texture);

        final float vMin = CondensateAnimationTickHandler.currentFrame / FRAMES;
        final float vMax = (CondensateAnimationTickHandler.currentFrame + 1) / FRAMES;
        final Tessellator tess = Tessellator.instance;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        tess.startDrawingQuads();
        tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, OVERLAY_ALPHA);
        tess.addVertexWithUV(0.0D, 16.0D, 0.0D, 0.0D, vMax);
        tess.addVertexWithUV(16.0D, 16.0D, 0.0D, 1.0D, vMax);
        tess.addVertexWithUV(16.0D, 0.0D, 0.0D, 1.0D, vMin);
        tess.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, vMin);
        tess.draw();

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static final class EntangledNeiRenderer implements IFluidRenderer {

        private final ItemStack displayStack;

        private EntangledNeiRenderer(Fluid fluid) {
            this.displayStack = GTUtility.getFluidDisplayStack(new FluidStack(fluid, 0), false, true);
        }

        @Override
        public void renderFluid(Fluid fluid, int x, int y) {
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(TextureMap.locationBlocksTexture);

            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0.0F);
            RENDERER.renderItem(ItemRenderType.INVENTORY, displayStack);
            GL11.glPopMatrix();
        }
    }
}
