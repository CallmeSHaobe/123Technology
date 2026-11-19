package com.newmaa.othtech.common.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.util.ItemRenderUtil;

import codechicken.lib.render.TextureUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GTMod;
import gregtech.api.interfaces.IGT_ItemWithMaterialRenderer;
import gregtech.common.render.items.GeneratedMaterialRenderer;

@SideOnly(Side.CLIENT)
public class OTEPsionicMaterialRenderer extends GeneratedMaterialRenderer {

    private static final int HALO_COPIES = 18;

    @Override
    protected void renderRegularItem(ItemRenderType type, ItemStack stack, IIcon icon, boolean modulateColor) {
        if (type != ItemRenderType.INVENTORY || icon == null
            || !(stack.getItem() instanceof IGT_ItemWithMaterialRenderer item)) {
            super.renderRegularItem(type, stack, icon, modulateColor);
            return;
        }

        float ticks = ticks();
        short[] rgba = item.getRGBa(stack);
        float pulse = 0.88F + 0.12F * (float) Math.sin(ticks * 0.07F);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT);
        GL11.glPushMatrix();
        try {
            TextureUtils.bindAtlas(item.getSpriteNumber());
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            // Draw the same body as GT, but with a few offset violet copies behind it. Keeping every
            // pass in the inventory's own 16x16 coordinate system prevents detached item models.
            for (int i = 0; i < HALO_COPIES; i++) {
                double angle = Math.PI * 2.0D * i / HALO_COPIES;
                float radius = 0.72F + 0.30F * (float) Math.sin(ticks * 0.045F + i * 0.8F);
                GL11.glPushMatrix();
                GL11.glTranslatef((float) Math.cos(angle) * radius, (float) Math.sin(angle) * radius, 0.0F);
                GL11.glColor4f(0.62F, 0.24F, 1.0F, 0.11F);
                ItemRenderUtil.renderItem(ItemRenderType.INVENTORY, icon);
                GL11.glPopMatrix();
            }

            drawWisps(ticks);
            if (modulateColor) {
                GL11.glColor4f(rgba[0] / 255.0F * pulse, rgba[1] / 255.0F * pulse, rgba[2] / 255.0F, 1.0F);
            } else {
                GL11.glColor4f(0.78F * pulse, 0.52F * pulse, 1.0F, 1.0F);
            }
            ItemRenderUtil.renderItem(ItemRenderType.INVENTORY, icon);
        } finally {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static void drawWisps(float ticks) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        Tessellator tessellator = Tessellator.instance;
        for (int i = 0; i < 14; i++) {
            double angle = ticks * 0.019D + i * Math.PI * 2.0D / 14.0D;
            double radius = 7.4D + 1.25D * Math.sin(ticks * 0.03D + i * 0.9D);
            float x = (float) (8.0D + Math.cos(angle) * radius);
            float y = (float) (8.0D + Math.sin(angle) * radius);
            float size = 0.30F + 0.22F * (float) Math.sin(ticks * 0.055F + i * 1.7F);
            float alpha = 0.20F + 0.12F * (float) Math.sin(ticks * 0.07F + i);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA_F(0.58F, 0.18F, 1.0F, Math.max(0.08F, alpha));
            tessellator.addVertex(x - size, y - size, 0.0D);
            tessellator.addVertex(x - size, y + size, 0.0D);
            tessellator.addVertex(x + size, y + size, 0.0D);
            tessellator.addVertex(x + size, y - size, 0.0D);
            tessellator.draw();
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static float ticks() {
        return GTMod.clientProxy()
            .getAnimationRenderTicks();
    }
}
