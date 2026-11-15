package com.newmaa.othtech.common.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.entity.EntityPositronShot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderPositronShot extends Render {

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        EntityPositronShot shot = (EntityPositronShot) entity;
        double sx = shot.getOriginX() - entity.posX + x;
        double sy = shot.getOriginY() - entity.posY + y;
        double sz = shot.getOriginZ() - entity.posZ + z;
        double dx = x - sx;
        double dy = y - sy;
        double dz = z - sz;
        double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length < 0.01D) return;
        dx /= length;
        dy /= length;
        dz /= length;

        RenderManager manager = RenderManager.instance;
        double vx = manager.viewerPosX - (shot.getOriginX() + x + RenderManager.renderPosX) * 0.5D;
        double vy = manager.viewerPosY - (shot.getOriginY() + y + RenderManager.renderPosY) * 0.5D;
        double vz = manager.viewerPosZ - (shot.getOriginZ() + z + RenderManager.renderPosZ) * 0.5D;
        double px = dy * vz - dz * vy;
        double py = dz * vx - dx * vz;
        double pz = dx * vy - dy * vx;
        double perpendicularLength = Math.sqrt(px * px + py * py + pz * pz);
        if (perpendicularLength < 0.001D) {
            px = -dz;
            py = 0D;
            pz = dx;
            perpendicularLength = Math.sqrt(px * px + pz * pz);
            if (perpendicularLength < 0.001D) {
                px = 1D;
                perpendicularLength = 1D;
            }
        }
        px /= perpendicularLength;
        py /= perpendicularLength;
        pz /= perpendicularLength;
        double qx = dy * pz - dz * py;
        double qy = dz * px - dx * pz;
        double qz = dx * py - dy * px;

        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_CURRENT_BIT);
        try {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawBand(sx, sy, sz, x, y, z, px, py, pz, 1.5D, 255, 194, 32, 105);
            drawBand(sx, sy, sz, x, y, z, qx, qy, qz, 1.2D, 255, 194, 32, 95);
            drawBand(sx, sy, sz, x, y, z, px, py, pz, 0.7D, 255, 248, 108, 235);
            drawBand(sx, sy, sz, x, y, z, qx, qy, qz, 0.6D, 255, 248, 108, 215);
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static void drawBand(double sx, double sy, double sz, double ex, double ey, double ez, double px, double py,
        double pz, double halfWidth, int red, int green, int blue, int alpha) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(red, green, blue, alpha);
        tessellator.addVertex(sx + px * halfWidth, sy + py * halfWidth, sz + pz * halfWidth);
        tessellator.addVertex(sx - px * halfWidth, sy - py * halfWidth, sz - pz * halfWidth);
        tessellator.addVertex(ex - px * halfWidth, ey - py * halfWidth, ez - pz * halfWidth);
        tessellator.addVertex(ex + px * halfWidth, ey + py * halfWidth, ez + pz * halfWidth);
        tessellator.draw();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null;
    }
}
