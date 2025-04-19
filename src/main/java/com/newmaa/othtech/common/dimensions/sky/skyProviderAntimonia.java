package com.newmaa.othtech.common.dimensions.sky;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.dimensions.AntimoniaResources;

import galaxyspace.core.render.sky.SkyProviderBase;

public class skyProviderAntimonia extends SkyProviderBase implements AntimoniaResources {

    public skyProviderAntimonia() {}

    @Override
    protected void setup() {
        this.sunRGBA[0] = 0.5882353F;
        this.sunRGBA[1] = 0.8352941F;
        this.sunRGBA[2] = 1.0F;
        this.sunRGBA[3] = 0.4F;
        this.sunRadius = 3.3;
        this.innerFlareRadius = 10.0;
        this.outerFlareRadius = 30.0;
    }

    @Override
    protected ResourceLocation getSunTexture() {
        return AntimoniaStar;
    }

    @Override
    protected void renderCelestialBodies(float partialTicks, WorldClient world, Minecraft mc) {
        GL11.glPushMatrix();
        GL11.glScalef(0.6F, 0.6F, 0.6F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        this.drawTexture(AntimoniaPlanet, 200.0);
        GL11.glPopMatrix();
    }

}
