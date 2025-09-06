package com.newmaa.othtech.common.dimensions.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.dimensions.ResourcesDimensions;

import galaxyspace.core.render.sky.SkyProviderBase;

public class SkyProviderAntimonia extends SkyProviderBase implements ResourcesDimensions {

    public SkyProviderAntimonia() {}

    @Override
    protected ResourceLocation getSunTexture() {
        return White;
    }

    @Override
    protected void renderCelestialBodies(float partialTicks, WorldClient world, Minecraft mc) {
        // 启用混合
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glRotatef(-180.0F, 100.0F, 1.0F, 0.0F);
        GL11.glRotatef(40.0F, 1.0F, 0.0F, 0.0F);
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        this.drawTexture(AntimoniaStar, 100.0);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        GL11.glScalef(0.6F, 0.6F, 0.6F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        this.drawTexture(AntimoniaPlanet, 200.0);
        GL11.glPopMatrix();
    }

}
