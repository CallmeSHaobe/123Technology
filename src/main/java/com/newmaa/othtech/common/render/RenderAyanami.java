package com.newmaa.othtech.common.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.common.blocks.OTEModelAyanami;

public class RenderAyanami extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity te, double posX, double posY, double posZ, float partialTickTime) {
        OTEModelAyanami statue = (OTEModelAyanami) te;
        bindTexture(AyanamiModelRenderer.TEXTURE);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(posX + 0.5, posY + 0.1, posZ + 0.5);
            int orientation = te.getBlockMetadata();
            if (orientation == 4) {
                GL11.glRotatef(90, 0, 1, 0);
            } else if (orientation == 5) {
                GL11.glRotatef(-90, 0, 1, 0);
            } else if (orientation == 3) {
                GL11.glRotatef(180, 0, 1, 0);
            }
            if (statue.getWorldObj() == null) {
                AyanamiModelRenderer.renderStanding();
            } else {
                long elapsed = statue.getWorldObj()
                    .getTotalWorldTime() - statue.getActionStartTick();
                AyanamiModelRenderer.render(statue.getAction(), elapsed, partialTickTime, 0, 0, 0, null);
            }
        } finally {
            GL11.glPopMatrix();
        }
    }
}
