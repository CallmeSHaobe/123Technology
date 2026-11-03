package com.newmaa.othtech.common.render;

import static gregtech.api.enums.Mods.GalaxySpace;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.blocks.oterender.OTERocketRender;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RocketRenderOTH extends TileEntitySpecialRenderer {

    private static final ResourceLocation ROCKETTEXTURES = new ResourceLocation(
        "galaxyspace",
        "textures/model/tier8rocket.png");
    private static final IModelCustom ROCKET = GalaxySpace.isModLoaded()
        ? AdvancedModelLoader.loadModel(new ResourceLocation("galaxyspace", "models/tier8rocket.obj"))
        : null;

    public RocketRenderOTH() {
        ClientRegistry.bindTileEntitySpecialRenderer(OTERocketRender.class, this);
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float timeSinceLastTick) {
        if (ROCKET == null || !(tile instanceof OTERocketRender rocket)) return;
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);

        switch (rocket.getBlockMetadata()) {
            case 2:
                GL11.glRotated(180, 0, 1, 0);
                break;
            case 4:
                GL11.glRotated(90, 0, 1, 0);
                break;
            case 5:
                GL11.glRotated(-90, 0, 1, 0);
                break;
            default:
                break;
        }
        GL11.glRotated(rocket.Rotation, 0, 1, 0);

        renderStar(rocket.size);
        GL11.glPopMatrix();
    }

    private void renderStar(double size) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.bindTexture(ROCKETTEXTURES);
        GL11.glScaled(size, size, size);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
        ROCKET.renderAll();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

}
