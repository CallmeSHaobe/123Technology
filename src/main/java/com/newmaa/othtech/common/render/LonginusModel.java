package com.newmaa.othtech.common.render;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
final class LonginusModel {

    // The OBJ is centered on X/Z. Its shaft runs along Y, with the fork at the positive end.
    static final float TIP_Y = 6.672779F;
    static final float CENTER_Y = (TIP_Y + 0.124467F) * 0.5F;
    static final float GRIP_Y = 3.05F;
    static final float WORLD_LENGTH = 9.6F;
    private static final float LENGTH = TIP_Y - 0.124467F;
    private static final ResourceLocation OBJ = new ResourceLocation("123technology", "models/longinus.obj");
    static final ResourceLocation TEXTURE = new ResourceLocation("123technology", "models/error_block_0.png");
    private static IModelCustom model;

    private LonginusModel() {}

    static void render(float length, float pivotY) {
        if (model == null) model = AdvancedModelLoader.loadModel(OBJ);

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_CURRENT_BIT | GL11.GL_TEXTURE_BIT);
        GL11.glPushMatrix();
        try {
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_NORMALIZE);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            // Forge's OBJ loader ignores the material library. Bind the supplied atlas directly.
            Minecraft.getMinecraft()
                .getTextureManager()
                .bindTexture(TEXTURE);
            float scale = length / LENGTH;
            GL11.glScalef(scale, scale, scale);
            GL11.glTranslatef(0F, -pivotY, 0F);
            model.renderAll();
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }
}
