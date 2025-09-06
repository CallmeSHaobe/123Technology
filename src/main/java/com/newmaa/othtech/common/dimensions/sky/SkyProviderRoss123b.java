package com.newmaa.othtech.common.dimensions.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.dimensions.ResourcesDimensions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SkyProviderRoss123b extends IRenderHandler implements ResourcesDimensions {

    // TODO 憋憋了。 不会修呀兄弟。
    public SkyProviderRoss123b() {}

    @Override
    public void render(float partialTicks, WorldClient world, Minecraft mc) {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();

        // 自发光
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

        // 永远在前面
        GL11.glDepthMask(false);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        // 黑洞参数
        float radiusInner = 30F; // 核心半径
        float radiusOuter = 60F; // 光晕半径
        float y = 150F; // 固定高度，玩家视线之上
        int slices = 64;

        // 外圈旋转
        float time = (world.getTotalWorldTime() + partialTicks) % 3600;
        float rotation = time * 0.5F;

        // ===== 外圈渐变光晕 =====
        GL11.glPushMatrix();
        GL11.glTranslatef(0f, y, 0f); // 世界坐标固定，不随玩家移动
        GL11.glRotatef(rotation, 0f, 1f, 0f);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glColor4f(0f, 0f, 0f, 1f); // 中心黑
        GL11.glVertex3f(0f, 0f, 0f);

        for (int i = 0; i <= slices; i++) {
            double angle = 2 * Math.PI * i / slices;
            float dx = (float) Math.cos(angle);
            float dz = (float) Math.sin(angle);

            // 渐变到透明外圈
            GL11.glColor4f(0f, 0f, 0f, 0f);
            GL11.glVertex3f(dx * radiusOuter, 0f, dz * radiusOuter);
        }
        GL11.glEnd();
        GL11.glPopMatrix();

        // ===== 黑洞核心 =====
        GL11.glPushMatrix();
        GL11.glTranslatef(0f, y, 0f); // 固定位置
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glColor4f(0f, 0f, 0f, 1f);
        GL11.glVertex3f(0f, 0f, 0f);

        for (int i = 0; i <= slices; i++) {
            double angle = 2 * Math.PI * i / slices;
            float dx = (float) Math.cos(angle);
            float dz = (float) Math.sin(angle);
            GL11.glVertex3f(dx * radiusInner, 0f, dz * radiusInner);
        }
        GL11.glEnd();
        GL11.glPopMatrix();

        // ===== 恢复状态 =====
        GL11.glDepthMask(true); // 重新开启深度写入
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }
}
