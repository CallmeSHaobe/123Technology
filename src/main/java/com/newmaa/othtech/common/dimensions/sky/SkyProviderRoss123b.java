package com.newmaa.othtech.common.dimensions.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.dimensions.ResourcesDimensions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import galaxyspace.core.render.sky.SkyProviderBase;

@SideOnly(Side.CLIENT)
public class SkyProviderRoss123b extends SkyProviderBase implements ResourcesDimensions {

    // FIXME 憋憋了。 不会修呀兄弟。
    // OMG 有GPT？！
    // 也许GPT修好了.
    public SkyProviderRoss123b() {}

    @Override
    protected ResourceLocation getSunTexture() {
        return White;
    }

    @Override
    public void renderCelestialBodies(float partialTicks, WorldClient world, Minecraft mc) {

        // 让贴图始终在正上方

        // === 黑洞核心 ===
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 100.0F, 0.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // 放大/旋转调整位置（和星球类似）
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glRotatef(-180.0F, 100.0F, 1.0F, 0.0F);
        GL11.glRotatef(40.0F, 1.0F, 0.0F, 0.0F);

        // 确保贴图在最前面
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        // 绘制核心贴图
        this.drawTexture(AntimoniaStar, 120.0);

        GL11.glPopMatrix();

        // === 外圈光晕（炫酷特效） ===
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        float rotation = (world.getTotalWorldTime() + partialTicks) % 3600 * 0.5f;
        GL11.glRotatef(rotation, 0f, 0f, 1f);

        int slices = 64;
        float radius = 200F;

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glColor4f(0f, 0f, 0f, 0.6f);
        GL11.glVertex3f(0f, 0f, 0f);

        for (int i = 0; i <= slices; i++) {
            double angle = 2 * Math.PI * i / slices;
            float dx = (float) Math.cos(angle);
            float dy = (float) Math.sin(angle);

            GL11.glColor4f(0.5f, 0.2f, 1f, 0f); // 紫色渐变透明
            GL11.glVertex3f(dx * radius, dy * radius, 0f);
        }
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }
}
