package com.newmaa.othtech.common.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.util.ItemRenderUtil;

import codechicken.lib.render.TextureUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GTMod;
import gregtech.api.interfaces.IGT_ItemWithMaterialRenderer;
import gregtech.common.render.items.GeneratedMaterialRenderer;

@SideOnly(Side.CLIENT)
public class OTEGunmuUnobservableRenderer extends GeneratedMaterialRenderer {

    public static int FLICKER_PERIOD = 53;
    public static int FLICKER_LENGTH = 6;
    public static float FLASH_ALPHA = 0.92F;
    public static float GHOST_ALPHA = 0.0F;
    public static float STROBE_SPEED = 3.1F;
    public static float MIN_DRAW_ALPHA = 0.02F;
    public static int BLUR_COPIES = 3;
    public static float BLUR_SPREAD = 1.2F;
    public static float BLUR_RATIO = 0.20F;
    public static float VEIL_RATIO = 0.26F;
    public static float DRIFT_SPEED = 0.045F;

    private static final float ICON_SIZE = 16.0F;

    @Override
    protected void renderRegularItem(ItemRenderType type, ItemStack aStack, IIcon icon, boolean shouldModulateColor) {
        if (icon == null || !(aStack.getItem() instanceof IGT_ItemWithMaterialRenderer aItem)) {
            super.renderRegularItem(type, aStack, icon, shouldModulateColor);
            return;
        }

        final float ticks = ticks();
        final float alpha = alpha(ticks);
        if (alpha <= MIN_DRAW_ALPHA) return;

        final short[] rgba = aItem.getRGBa(aStack);
        final float r = shouldModulateColor ? rgba[0] / 255.0F : 1.0F;
        final float g = shouldModulateColor ? rgba[1] / 255.0F : 1.0F;
        final float b = shouldModulateColor ? rgba[2] / 255.0F : 1.0F;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (type != ItemRenderType.INVENTORY) {
            GL11.glColor4f(r, g, b, alpha);
            ItemRenderUtil.renderItem(type, icon);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha * VEIL_RATIO);
            ItemRenderUtil.renderItem(type, icon);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }

        TextureUtils.bindAtlas(aItem.getSpriteNumber());
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawCopy(icon, drift(0, ticks), r, g, b, alpha);
        for (int i = 1; i <= BLUR_COPIES; i++) {
            drawCopy(icon, drift(i, ticks), r, g, b, alpha * BLUR_RATIO / i);
        }
        drawVeil(alpha);

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderContainedFluid(ItemRenderType type, FluidStack fluidStack, IIcon fluidIcon) {
        if (fluidStack == null || fluidIcon == null || fluidStack.getFluid() == null) return;

        final float ticks = ticks();
        final float alpha = alpha(ticks);
        if (alpha <= MIN_DRAW_ALPHA) return;

        final Fluid fluid = fluidStack.getFluid();
        final int color = fluid.getColor(fluidStack);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
        TextureUtils.bindAtlas(fluid.getSpriteNumber());
        GL11.glDepthFunc(GL11.GL_EQUAL);
        ItemRenderUtil.renderItem(type, fluidIcon);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderItemOverlay(ItemRenderType type, IIcon overlay) {
        if (overlay == null) return;

        final float ticks = ticks();
        final float alpha = alpha(ticks);
        if (alpha <= MIN_DRAW_ALPHA) return;

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (type != ItemRenderType.INVENTORY) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            ItemRenderUtil.renderItem(type, overlay);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }

        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawCopy(overlay, drift(0, ticks), 1.0F, 1.0F, 1.0F, alpha);
        for (int i = 1; i <= BLUR_COPIES; i++) {
            drawCopy(overlay, drift(i, ticks), 1.0F, 1.0F, 1.0F, alpha * BLUR_RATIO / i);
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static float alpha(float ticks) {
        final int period = Math.max(1, FLICKER_PERIOD);
        final int phase = (int) ticks % period;

        if (phase >= FLICKER_LENGTH || FLICKER_LENGTH <= 0) return GHOST_ALPHA;

        final float progress = phase / (float) FLICKER_LENGTH;
        final float envelope = (float) Math.sin(Math.PI * progress);
        final float strobe = 0.55F + 0.45F * (float) Math.sin(ticks * STROBE_SPEED);

        return Math.min(1.0F, FLASH_ALPHA * envelope * strobe);
    }

    private static void drawCopy(IIcon icon, float offset, float r, float g, float b, float a) {
        final Tessellator tess = Tessellator.instance;

        tess.startDrawingQuads();
        tess.setColorRGBA_F(r, g, b, a);
        tess.addVertexWithUV(offset, 0.0D, 0.0D, icon.getMinU(), icon.getMinV());
        tess.addVertexWithUV(offset, ICON_SIZE, 0.0D, icon.getMinU(), icon.getMaxV());
        tess.addVertexWithUV(offset + ICON_SIZE, ICON_SIZE, 0.0D, icon.getMaxU(), icon.getMaxV());
        tess.addVertexWithUV(offset + ICON_SIZE, 0.0D, 0.0D, icon.getMaxU(), icon.getMinV());
        tess.draw();
    }

    private static void drawVeil(float alpha) {
        final float veil = alpha * VEIL_RATIO;
        final Tessellator tess = Tessellator.instance;

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        tess.startDrawingQuads();
        tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, veil);
        tess.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        tess.addVertexWithUV(0.0D, ICON_SIZE * 0.5F, 0.0D, 0.0D, 0.0D);
        tess.addVertexWithUV(ICON_SIZE, ICON_SIZE * 0.5F, 0.0D, 0.0D, 0.0D);
        tess.addVertexWithUV(ICON_SIZE, 0.0D, 0.0D, 0.0D, 0.0D);
        tess.draw();

        tess.startDrawingQuads();
        tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, veil * 0.55F);
        tess.addVertexWithUV(0.0D, ICON_SIZE * 0.5F, 0.0D, 0.0D, 0.0D);
        tess.addVertexWithUV(0.0D, ICON_SIZE, 0.0D, 0.0D, 0.0D);
        tess.addVertexWithUV(ICON_SIZE, ICON_SIZE, 0.0D, 0.0D, 0.0D);
        tess.addVertexWithUV(ICON_SIZE, ICON_SIZE * 0.5F, 0.0D, 0.0D, 0.0D);
        tess.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static float drift(int index, float ticks) {
        final float phase = index * 2.1F;
        final float scale = index == 0 ? BLUR_SPREAD * 0.2F : BLUR_SPREAD;
        return (float) Math.sin(ticks * DRIFT_SPEED + phase) * scale;
    }

    private static float ticks() {
        return GTMod.clientProxy()
            .getAnimationRenderTicks();
    }
}
