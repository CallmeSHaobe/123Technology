package com.newmaa.othtech.common.dimensions.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.dimensions.ResourcesDimensions;

import galaxyspace.core.render.sky.SkyProviderBase;

/**
 * 锑星天空盒: 纯黑视界 + 同心发光吸积盘。
 * 所有几何都用四边形/三角形拼, 不压扁、不做前后半圈拆分, 避免裁剪出怪三角形和"穿透"感。
 */
public class SkyProviderAntimonia extends SkyProviderBase implements ResourcesDimensions {

    private static final float CORE_RADIUS = 100.0F;
    private static final float PLANET_RADIUS = 200.0F;
    private static final int SEGMENTS = 128;
    /** 天体平面 */
    private static final float Y_PLANE = -100.0F;

    // 白热 → 橙 → 暗红 → 透明
    private static final float[] DISK_1 = { 1.00F, 0.95F, 0.86F, 0.45F };
    private static final float[] DISK_2 = { 1.00F, 0.74F, 0.40F, 0.26F };
    private static final float[] DISK_3 = { 0.95F, 0.45F, 0.16F, 0.10F };
    private static final float[] DISK_4 = { 0.55F, 0.16F, 0.08F, 0.00F };
    private static final float[] RING_INNER = { 0.92F, 0.98F, 1.00F, 0.30F };
    private static final float[] RING_OUTER = { 0.70F, 0.85F, 1.00F, 0.00F };

    public SkyProviderAntimonia() {}

    @Override
    protected ResourceLocation getSunTexture() {
        return White;
    }

    /** 这颗恒星本身就是黑洞, 关掉基类那个白色太阳圆盘和它的光斑 */
    @Override
    protected void renderSun(float partialTicks, WorldClient world, Minecraft mc) {}

    /** 那几个银河贴图在基类里缺纹理状态会变成白方块, 直接不画 */
    @Override
    protected void renderGalaxies() {}

    @Override
    protected void renderCelestialBodies(float partialTicks, WorldClient world, Minecraft mc) {
        float time = world.getTotalWorldTime() + partialTicks;

        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glPushMatrix();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDepthMask(false);

        GL11.glRotatef(-180.0F, 100.0F, 1.0F, 0.0F);
        GL11.glRotatef(40.0F, 1.0F, 0.0F, 0.0F);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        // 纯黑视界 → 吸积盘 → 光子环, 全部同心同平面
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawCore();

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawAccretionDisk(time);
        drawPhotonRing(time);

        GL11.glPopMatrix();

        // 行星: 同样用三角形扇区拼, 避免方块
        GL11.glPushMatrix();
        GL11.glScalef(0.6F, 0.6F, 0.6F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawPlanetDisc();
        GL11.glPopMatrix();

        GL11.glPopAttrib();
    }

    /** 视界本体: 纯黑圆盘, 用显式三角形拼, 不用 TRIANGLE_FAN */
    private void drawCore() {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_TRIANGLES);
        tess.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
        for (int i = 0; i < SEGMENTS; i++) {
            double a0 = Math.toRadians(360.0D * i / SEGMENTS);
            double a1 = Math.toRadians(360.0D * (i + 1) / SEGMENTS);
            tess.setColorRGBA_F(0.0F, 0.0F, 0.0F, 1.0F);
            tess.addVertex(0.0D, Y_PLANE, 0.0D);
            tess.addVertex(Math.cos(a0) * CORE_RADIUS, Y_PLANE, Math.sin(a0) * CORE_RADIUS);
            tess.addVertex(Math.cos(a1) * CORE_RADIUS, Y_PLANE, Math.sin(a1) * CORE_RADIUS);
        }
        tess.draw();
    }

    /** 行星圆盘: 一边亮一边暗 */
    private void drawPlanetDisc() {
        Tessellator tess = Tessellator.instance;
        tess.startDrawing(GL11.GL_TRIANGLES);
        for (int i = 0; i < SEGMENTS; i++) {
            double a0 = Math.toRadians(360.0D * i / SEGMENTS);
            double a1 = Math.toRadians(360.0D * (i + 1) / SEGMENTS);
            tess.setColorRGBA_F(0.72F, 0.78F, 0.82F, 1.0F);
            tess.addVertex(0.0D, Y_PLANE, 0.0D);
            tintPlanet(tess, a0);
            tess.addVertex(Math.cos(a0) * PLANET_RADIUS, Y_PLANE, Math.sin(a0) * PLANET_RADIUS);
            tintPlanet(tess, a1);
            tess.addVertex(Math.cos(a1) * PLANET_RADIUS, Y_PLANE, Math.sin(a1) * PLANET_RADIUS);
        }
        tess.draw();
    }

    private void tintPlanet(Tessellator tess, double rad) {
        float lit = 0.45F + 0.55F * (float) Math.max(0.0D, Math.cos(rad) * 0.9D + 0.35D);
        tess.setColorRGBA_F(0.46F * lit, 0.50F * lit, 0.56F * lit, 1.0F);
    }

    /** 吸积盘: 一直铺到 3.6R 的同心环带 */
    private void drawAccretionDisk(float time) {
        float breathe = 0.90F + 0.10F * (float) Math.sin(time * 0.01D);
        drawBand(CORE_RADIUS * 1.00F, CORE_RADIUS * 1.55F, DISK_1, DISK_2, breathe);
        drawBand(CORE_RADIUS * 1.55F, CORE_RADIUS * 2.50F, DISK_2, DISK_3, breathe);
        drawBand(CORE_RADIUS * 2.50F, CORE_RADIUS * 3.60F, DISK_3, DISK_4, breathe);
    }

    /** 光子环: 贴着视界的一圈很淡的冷白光 */
    private void drawPhotonRing(float time) {
        float pulse = 0.80F + 0.20F * (float) Math.sin(time * 0.05F);
        drawBand(CORE_RADIUS * 1.00F, CORE_RADIUS * 1.10F, RING_INNER, RING_OUTER, pulse);
    }

    /** 同心环带: 每个扇区一个四边形, 顶点色从 innerR 渐变到 outerR */
    private void drawBand(float innerR, float outerR, float[] inner, float[] outer, float alphaScale) {
        Tessellator tess = Tessellator.instance;
        tess.startDrawingQuads();
        for (int i = 0; i < SEGMENTS; i++) {
            double a0 = Math.toRadians(360.0D * i / SEGMENTS);
            double a1 = Math.toRadians(360.0D * (i + 1) / SEGMENTS);
            double cos0 = Math.cos(a0), sin0 = Math.sin(a0);
            double cos1 = Math.cos(a1), sin1 = Math.sin(a1);

            tess.setColorRGBA_F(outer[0], outer[1], outer[2], outer[3] * alphaScale);
            tess.addVertex(cos0 * outerR, Y_PLANE, sin0 * outerR);
            tess.setColorRGBA_F(outer[0], outer[1], outer[2], outer[3] * alphaScale);
            tess.addVertex(cos1 * outerR, Y_PLANE, sin1 * outerR);
            tess.setColorRGBA_F(inner[0], inner[1], inner[2], inner[3] * alphaScale);
            tess.addVertex(cos1 * innerR, Y_PLANE, sin1 * innerR);
            tess.setColorRGBA_F(inner[0], inner[1], inner[2], inner[3] * alphaScale);
            tess.addVertex(cos0 * innerR, Y_PLANE, sin0 * innerR);
        }
        tess.draw();
    }
}
