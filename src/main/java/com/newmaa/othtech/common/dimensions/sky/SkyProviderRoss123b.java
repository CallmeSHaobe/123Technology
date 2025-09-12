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

    @Override
    public void renderCelestialBodies(float partialTicks, WorldClient world, Minecraft mc) {

    }

    /**
     * 渲染锑洞. Render the AntimoniaHole. From Chat-GPT
     */
    @Override
    public void renderSun(float partialTicks, WorldClient world, Minecraft mc) {
        Tessellator tess = Tessellator.instance;

        // =====================
        // 1. 黑洞核心贴图
        // =====================
        this.drawTexture(AntimoniaStar, 120); // 核心贴图

        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);

        // 自发光混合模式
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        // 核心始终在正上方
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);

        float size = 80.0F; // 核心大小
        double u0 = 0.0, u1 = 1.0, v0 = 0.0, v1 = 1.0;

        tess.startDrawingQuads();
        tess.addVertexWithUV(-size, size, -100.0, u0, v0);
        tess.addVertexWithUV(size, size, -100.0, u1, v0);
        tess.addVertexWithUV(size, -size, -100.0, u1, v1);
        tess.addVertexWithUV(-size, -size, -100.0, u0, v1);
        tess.draw();

        // =====================
        // 2. 多层动态光晕
        // =====================
        GL11.glDisable(GL11.GL_TEXTURE_2D); // 光晕纯色
        int haloSegments = 64;
        float time = world.getTotalWorldTime() + partialTicks;

        // 定义光晕半径和基础颜色
        float[] baseHaloSizes = { size * 1.5F, size * 2.0F, size * 2.5F };
        float[][] haloColors = { { 1.0F, 0.2F, 0.2F, 0.6F }, // 红
            { 0.2F, 1.0F, 0.2F, 0.4F }, // 绿
            { 0.2F, 0.2F, 1.0F, 0.3F } // 蓝
        };

        for (int h = 0; h < baseHaloSizes.length; h++) {
            float haloRadius = baseHaloSizes[h];

            // 脉动效果
            float pulse = 1.0F + 0.05F * (float) Math.sin(time * 0.1 + h);
            float animatedRadius = haloRadius * pulse;

            // 旋转角度
            float rotationAngle = (time * 0.5F + h * 20) % 360;
            double cosRot = Math.cos(Math.toRadians(rotationAngle));
            double sinRot = Math.sin(Math.toRadians(rotationAngle));

            tess.startDrawing(GL11.GL_TRIANGLE_FAN);

            // 中心透明
            tess.setColorRGBA_F(haloColors[h][0], haloColors[h][1], haloColors[h][2], 0.0F);
            tess.addVertex(0.0, 0.0, -101.0);

            // 外圈渐变
            tess.setColorRGBA_F(haloColors[h][0], haloColors[h][1], haloColors[h][2], haloColors[h][3]);

            for (int i = 0; i <= haloSegments; i++) {
                double angle = 2 * Math.PI * i / haloSegments;
                double dx = Math.cos(angle) * animatedRadius;
                double dy = Math.sin(angle) * animatedRadius;

                // 应用旋转
                double rx = dx * cosRot - dy * sinRot;
                double ry = dx * sinRot + dy * cosRot;

                tess.addVertex(rx, ry, -101.0);
            }

            tess.draw();
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // =====================
        // 3. 恢复状态
        // =====================
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
