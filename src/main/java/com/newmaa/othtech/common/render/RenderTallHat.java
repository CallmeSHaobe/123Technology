package com.newmaa.othtech.common.render;

import static mcp.mobius.waila.overlay.OverlayConfig.scale;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.blocks.OTEModelTallHat;

public class RenderTallHat extends TileEntitySpecialRenderer {

    ResourceLocation texture;
    ResourceLocation objModelLocation;
    IModelCustom model;

    public RenderTallHat() {
        texture = new ResourceLocation("123technology", "models/tallHatLaodengTexture.png");
        objModelLocation = new ResourceLocation("123technology", "models/TallHatLaodeng.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double posX, double posY, double posZ, float partialTickTime) {
        OTEModelTallHat te2 = (OTEModelTallHat) te;
        bindTexture(texture);
        GL11.glPushMatrix();
        GL11.glTranslated(posX + 0.5, posY + 0.1, posZ + 0.5);
        int orientation = te.getBlockMetadata();
        if (orientation == 4) {
            GL11.glRotatef(90, 0, 0.2F, 0);
        } else if (orientation == 5) {
            GL11.glRotatef(-90, 0, 0.2F, 0);
        } else if (orientation == 3) {
            GL11.glRotatef(180, 0, 0.2F, 0);
        }
        GL11.glScalef(scale, scale, scale);
        GL11.glPushMatrix();
        model.renderAll();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
}
