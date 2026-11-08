package com.newmaa.othtech.common.render;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import codechicken.lib.render.TextureUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GTMod;
import gregtech.api.interfaces.IGT_ItemWithMaterialRenderer;
import gregtech.common.render.items.GeneratedMaterialRenderer;

@SideOnly(Side.CLIENT)
public class OTEGunmuObservableRenderer extends GeneratedMaterialRenderer {

    public static float ROTATION_SPEED = 3.5F;
    public static float FADE_SPEED = 0.07F;
    public static float FLICKER_RATE = 0.6F;
    public static int FLICKER_PERIOD = 47;
    public static int FLICKER_LENGTH = 4;
    public static float FLICKER_MIN = 0.18F;
    public static float MIN_VISIBILITY = 0.08F;
    public static boolean SCANLINE = true;
    public static float SCAN_SPEED = 0.55F;
    public static float SCAN_ALPHA = 0.40F;

    private static final float ICON_SIZE = 16.0F;

    @Override
    public void renderItem(ItemRenderType type, ItemStack aStack, Object... data) {
        super.renderItem(type, aStack, data);
        if (SCANLINE && type == ItemRenderType.INVENTORY) drawScanline(ticks());
    }

    @Override
    protected void renderRegularItem(ItemRenderType type, ItemStack aStack, IIcon icon, boolean shouldModulateColor) {
        if (icon == null || !(aStack.getItem() instanceof IGT_ItemWithMaterialRenderer aItem)) {
            super.renderRegularItem(type, aStack, icon, shouldModulateColor);
            return;
        }

        final float ticks = ticks();

        GL11.glPushMatrix();
        applyRotation(type, ticks);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        TextureUtils.bindAtlas(aItem.getSpriteNumber());
        color(aItem, aStack, shouldModulateColor, visibility(ticks));
        drawIcon2D(type, icon);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderContainedFluid(ItemRenderType type, FluidStack fluidStack, IIcon fluidIcon) {
        if (fluidStack == null || fluidIcon == null || fluidStack.getFluid() == null) return;

        final float ticks = ticks();
        final Fluid fluid = fluidStack.getFluid();
        final int color = fluid.getColor(fluidStack);

        GL11.glPushMatrix();
        applyRotation(type, ticks);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        TextureUtils.bindAtlas(fluid.getSpriteNumber());
        GL11.glColor4f(
            (color >> 16 & 0xFF) / 255.0F,
            (color >> 8 & 0xFF) / 255.0F,
            (color & 0xFF) / 255.0F,
            visibility(ticks));
        GL11.glDepthFunc(GL11.GL_EQUAL);

        if (type == ItemRenderType.INVENTORY) GL11.glScalef(16.0F, 16.0F, 32.0F);

        ItemRenderer.renderItemIn2D(
            Tessellator.instance,
            fluidIcon.getMaxU(),
            fluidIcon.getMinV(),
            fluidIcon.getMinU(),
            fluidIcon.getMaxV(),
            fluidIcon.getIconWidth(),
            fluidIcon.getIconHeight(),
            0.0625F);

        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void renderItemOverlay(ItemRenderType type, IIcon overlay) {
        if (overlay == null) return;

        final float ticks = ticks();

        GL11.glPushMatrix();
        applyRotation(type, ticks);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, visibility(ticks));
        drawIcon2D(type, overlay);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static void applyRotation(ItemRenderType type, float ticks) {
        if (type == ItemRenderType.INVENTORY) {
            GL11.glTranslatef(8.0F, 8.0F, 0.0F);
        } else {
            GL11.glTranslatef(0.5F, 0.5F, 0.0F);
        }

        GL11.glRotatef((ticks * ROTATION_SPEED) % 360.0F, 0.3F, 0.5F, 0.2F);
        GL11.glRotatef(180.0F, 0.5F, 0.0F, 0.0F);

        if (type == ItemRenderType.INVENTORY) {
            GL11.glTranslatef(-8.0F, -8.0F, 0.0F);
        } else {
            GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
        }

        GL11.glTranslatef(0.0F, 0.0F, 0.03125F);
    }

    private static void drawIcon2D(ItemRenderType type, IIcon icon) {
        final boolean flip = type == ItemRenderType.INVENTORY;

        if (flip) GL11.glScalef(16.0F, 16.0F, 32.0F);

        ItemRenderer.renderItemIn2D(
            Tessellator.instance,
            flip ? icon.getMinU() : icon.getMaxU(),
            flip ? icon.getMinV() : icon.getMaxV(),
            flip ? icon.getMaxU() : icon.getMinU(),
            flip ? icon.getMaxV() : icon.getMinV(),
            icon.getIconWidth(),
            icon.getIconHeight(),
            0.0625F);
    }

    private static void color(IGT_ItemWithMaterialRenderer aItem, ItemStack aStack, boolean shouldModulateColor,
        float alpha) {
        if (!shouldModulateColor) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
            return;
        }

        final short[] rgba = aItem.getRGBa(aStack);
        GL11.glColor4f(rgba[0] / 255.0F, rgba[1] / 255.0F, rgba[2] / 255.0F, alpha);
    }

    private static float visibility(float ticks) {
        final float fade = 0.55F + 0.45F * (float) Math.sin(ticks * FADE_SPEED);
        final boolean flicker = FLICKER_PERIOD > 0 && ((int) (ticks * FLICKER_RATE)) % FLICKER_PERIOD < FLICKER_LENGTH;
        final float value = flicker ? fade * FLICKER_MIN : fade;
        return Math.max(MIN_VISIBILITY, Math.min(1.0F, value));
    }

    private static void drawScanline(float ticks) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        final float sweep = (ticks * SCAN_SPEED) % 22.0F - 3.0F;
        final float visibility = visibility(ticks);
        final int bands = 5;
        final Tessellator tess = Tessellator.instance;

        for (int i = 0; i < bands; i++) {
            final float alpha = SCAN_ALPHA * visibility * (1.0F - i / (float) bands);
            final float y0 = sweep - i * 0.6F;
            tess.startDrawingQuads();
            tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, alpha);
            tess.addVertexWithUV(0.0D, y0, 0.0D, 0.0D, 0.0D);
            tess.addVertexWithUV(0.0D, y0 + 1.6F, 0.0D, 0.0D, 0.0D);
            tess.addVertexWithUV(ICON_SIZE, y0 + 1.6F, 0.0D, 0.0D, 0.0D);
            tess.addVertexWithUV(ICON_SIZE, y0, 0.0D, 0.0D, 0.0D);
            tess.draw();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    private static float ticks() {
        return GTMod.clientProxy()
            .getAnimationRenderTicks();
    }
}
