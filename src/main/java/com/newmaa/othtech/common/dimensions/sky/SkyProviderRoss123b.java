package com.newmaa.othtech.common.dimensions.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.dimensions.ResourcesDimensions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import galaxyspace.core.render.sky.SkyProviderBase;

/**
 * OMG 有GPT?!
 * 也许GPT修好了.
 * biebie.
 */

@SideOnly(Side.CLIENT)
public class SkyProviderRoss123b extends SkyProviderBase implements ResourcesDimensions {

    public SkyProviderRoss123b() {}

    @Override
    protected void drawTexture(ResourceLocation texture, double radius) {
        return;
    }

    /** 天空里黑之月所在的深度平面。 */
    private static final double MOON_Z = -100.0D;
    /** 画圆盘的细分数。 */
    private static final int MOON_SEGMENTS = 96;

    /**
     * 黑之月: 悬在天空一侧的巨大黑盘, 边缘一圈血色光环 —— 第三次冲击后挂在天上的那颗。
     */
    @Override
    public void renderCelestialBodies(float partialTicks, WorldClient world, Minecraft mc) {
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // 先清深度再关深度写入, 否则 glClear 会被深度掩码挡掉
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glDepthMask(false);
        GL11.glShadeModel(GL11.GL_SMOOTH);

        // 固定悬在固定方位, 不随昼夜转动(死掉的月亮不该再走)
        GL11.glRotatef(18.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(64.0F, 0.0F, 1.0F, 0.0F);

        float radius = 78.0F;
        drawDisc(radius, 0.01F, 0.0F, 0.0F);
        drawRing(radius, radius * 1.06F, 1.0F, 0.08F, 0.05F, 0.95F, 0.80F);
        drawRing(radius * 1.06F, radius * 1.75F, 0.75F, 0.03F, 0.02F, 0.0F, 0.55F);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static void drawDisc(float radius, float red, float green, float blue) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        tessellator.setColorRGBA_F(red, green, blue, 1.0F);
        tessellator.addVertex(0.0D, 0.0D, MOON_Z);
        for (int i = 0; i <= MOON_SEGMENTS; i++) {
            double angle = Math.PI * 2.0D * i / MOON_SEGMENTS;
            tessellator.setColorRGBA_F(red, green, blue, 1.0F);
            tessellator.addVertex(Math.cos(angle) * radius, Math.sin(angle) * radius, MOON_Z);
        }
        tessellator.draw();
    }

    private static void drawRing(float inner, float outer, float red, float green, float blue, float alphaOuter,
        float alphaInner) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        for (int i = 0; i < MOON_SEGMENTS; i++) {
            double angle0 = Math.PI * 2.0D * i / MOON_SEGMENTS;
            double angle1 = Math.PI * 2.0D * (i + 1) / MOON_SEGMENTS;
            float cos0 = (float) Math.cos(angle0);
            float sin0 = (float) Math.sin(angle0);
            float cos1 = (float) Math.cos(angle1);
            float sin1 = (float) Math.sin(angle1);

            tessellator.setColorRGBA_F(red, green, blue, alphaOuter);
            tessellator.addVertex(cos0 * outer, sin0 * outer, MOON_Z);
            tessellator.addVertex(cos1 * outer, sin1 * outer, MOON_Z);
            tessellator.setColorRGBA_F(red, green, blue, alphaInner);
            tessellator.addVertex(cos1 * inner, sin1 * inner, MOON_Z);
            tessellator.addVertex(cos0 * inner, sin0 * inner, MOON_Z);
        }
        tessellator.draw();
    }

    /**
     * 原来的"锑洞"三层脉动光晕已经去掉(天上那几条彩色弧带就是它)。
     * <p>
     * 天空现在只剩底色渐变 + 星点 + {@link #renderCelestialBodies} 里的黑之月。
     */
    @Override
    public void renderSun(float partialTicks, WorldClient world, Minecraft mc) {}
}
