package com.newmaa.othtech.common.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.entity.EntitySuicideDrone;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Uses the shared, already-normalized 8.75-block model without a second scale transform. */
@SideOnly(Side.CLIENT)
public class RenderSuicideDrone extends Render {

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (entity.isInvisible()) return;
        EntitySuicideDrone drone = (EntitySuicideDrone) entity;
        float facing = entity.prevRotationYaw
            + MathHelper.wrapAngleTo180_float(entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
        float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        int matrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            // The shared preview model points toward -Z; Minecraft yaw zero points toward +Z.
            GL11.glRotatef(180F - facing, 0F, 1F, 0F);
            GL11.glRotatef(-pitch, 1F, 0F, 0F);
            GL11.glRotatef(-drone.getBankAngle(partialTicks), 0F, 0F, 1F);
            RenderDrone.render(drone.getVariant());
        } finally {
            GL11.glPopMatrix();
            GL11.glMatrixMode(matrixMode);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null; // RenderDrone binds the variant's texture and restores GL state itself.
    }
}
