package com.newmaa.othtech.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import com.newmaa.othtech.machine.single.OTESpeakingStone;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

/** Draws labels for loaded GT speaking stones supplied by the client; does not discover or load tiles. */
public final class SpeakingStoneDisplay {

    private static final String NO_PROGRAMME = "ote.speaking_stone.now_playing_unavailable";
    private static final String NOTES = "\u266A \u266B \u266A";
    private static final int TITLE_WIDTH = 140;
    private static final float LABEL_SCALE = 0.0125F;

    private SpeakingStoneDisplay() {}

    /** Invoke once per RenderWorldLastEvent with the visible or nearby loaded GT speaking stones. */
    public static void render(RenderWorldLastEvent event, Iterable<? extends OTESpeakingStone> stones) {
        Minecraft mc = Minecraft.getMinecraft();
        Entity viewer = mc.renderViewEntity;
        if (viewer == null || mc.theWorld == null || mc.gameSettings.hideGUI || mc.fontRenderer == null) return;

        float partial = event.partialTicks;
        double cameraX = viewer.lastTickPosX + (viewer.posX - viewer.lastTickPosX) * partial;
        double cameraY = viewer.lastTickPosY + (viewer.posY - viewer.lastTickPosY) * partial;
        double cameraZ = viewer.lastTickPosZ + (viewer.posZ - viewer.lastTickPosZ) * partial;
        RenderManager manager = RenderManager.instance;
        FontRenderer font = mc.fontRenderer;

        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_TEXTURE_BIT);
        try {
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthMask(false);

            for (OTESpeakingStone stone : stones) {
                if (stone == null) continue;
                IGregTechTileEntity base = stone.getBaseMetaTileEntity();
                if (!(base instanceof TileEntity) || ((TileEntity) base).isInvalid()
                    || base.getWorld() != mc.theWorld
                    || !base.isActive()) continue;
                double x = base.getXCoord() + 0.5D - cameraX;
                double y = base.getYCoord() + 0.5D - cameraY;
                double z = base.getZCoord() + 0.5D - cameraZ;
                double radius = stone.getBroadcastRadius();
                if (radius <= 0 || x * x + y * y + z * z >= radius * radius) continue;

                String title = title(stone.getNowPlaying(), font);
                float bob = 0.025F * (float) Math.sin((mc.theWorld.getTotalWorldTime() % 48 + partial) * Math.PI / 24);
                drawLabel(manager, font, x, y + 0.78D + bob, z, title);
            }
        } finally {
            GL11.glPopAttrib();
        }
    }

    private static String title(String raw, FontRenderer font) {
        String text = raw == null ? ""
            : StringUtils.stripControlCodes(raw)
                .replaceAll("[\\p{Z}\\s]+", " ")
                .replaceAll("\\p{Cc}", "")
                .trim();
        if (text.isEmpty()) text = StatCollector.translateToLocal(NO_PROGRAMME);
        if (font.getStringWidth(text) <= TITLE_WIDTH) return text;
        String suffix = "...";
        String clipped = font.trimStringToWidth(text, TITLE_WIDTH - font.getStringWidth(suffix));
        if (!clipped.isEmpty() && Character.isHighSurrogate(clipped.charAt(clipped.length() - 1))) {
            clipped = clipped.substring(0, clipped.length() - 1);
        }
        return clipped.trim() + suffix;
    }

    private static void drawLabel(RenderManager manager, FontRenderer font, double x, double y, double z,
        String title) {
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            GL11.glRotatef(-manager.playerViewY, 0F, 1F, 0F);
            GL11.glRotatef(manager.playerViewX, 1F, 0F, 0F);
            GL11.glScalef(-LABEL_SCALE, -LABEL_SCALE, LABEL_SCALE);
            font.drawStringWithShadow(NOTES, -font.getStringWidth(NOTES) / 2, -font.FONT_HEIGHT - 2, 0xFFFFD36A);
            font.drawStringWithShadow(title, -font.getStringWidth(title) / 2, 0, 0xFFFFFFFF);
        } finally {
            GL11.glPopMatrix();
        }
    }
}
