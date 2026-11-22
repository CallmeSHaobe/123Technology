package com.newmaa.othtech.common.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.entity.EntityLonginus;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderLonginus extends Render {

    private static final float IMPACT_ADVANCE = 1F;
    private static final float SURFACE_INSET = 0.2F;

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        float flightYaw = interpolateAngle(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
        float flightPitch = interpolateAngle(entity.prevRotationPitch, entity.rotationPitch, partialTicks);
        GL11.glPushMatrix();
        try {
            ForgeDirection face = entity instanceof EntityLonginus spear ? spear.getEmbeddedFace()
                : ForgeDirection.UNKNOWN;
            // Seat the visible tip into the collision surface even at grazing angles.
            GL11.glTranslated(
                x - face.offsetX * SURFACE_INSET,
                y - face.offsetY * SURFACE_INSET,
                z - face.offsetZ * SURFACE_INSET);
            // EntityThrowable measures yaw from +Z and pitch upwards. Turn the model's +Y tip onto that axis.
            GL11.glRotatef(flightYaw, 0F, 1F, 0F);
            GL11.glRotatef(90F - flightPitch, 1F, 0F, 0F);
            if (face == ForgeDirection.UNKNOWN && entity instanceof EntityLonginus spear && spear.hasImpacted()) {
                GL11.glTranslatef(0F, IMPACT_ADVANCE, 0F);
            }
            LonginusModel.render(LonginusModel.WORLD_LENGTH, LonginusModel.TIP_Y);
        } finally {
            GL11.glPopMatrix();
        }
    }

    private static float interpolateAngle(float previous, float current, float partialTicks) {
        return previous + MathHelper.wrapAngleTo180_float(current - previous) * partialTicks;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return LonginusModel.TEXTURE;
    }
}
