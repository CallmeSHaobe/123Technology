package com.newmaa.othtech.common.render;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.ToIntFunction;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import com.newmaa.othtech.common.entity.EntityAyanami;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Draws Rei’s current speech above her in world space, with normal depth testing. */
@SideOnly(Side.CLIENT)
public final class AyanamiSpeechBubble {

    private static final int NORMAL_TEXT_WIDTH = 180;
    private static final int MAX_TEXT_WIDTH = 360;
    private static final int MAX_LINES = 26;
    private static final int PADDING_X = 6;
    private static final int PADDING_Y = 4;
    private static final int TAIL_HEIGHT = 6;
    private static final float PIXEL_SIZE = 0.010F;
    private static final int BORDER = 0xFF292735;
    private static final int BACKGROUND = 0xFFFFFBF4;
    private static final int TEXT = 0xFF252331;

    private static FontRenderer lastFont;
    private static boolean lastUnicode;
    private static String lastReply;
    private static Layout lastLayout;

    private AyanamiSpeechBubble() {}

    public static void render(EntityAyanami npc, String reply, double renderX, double renderY, double renderZ) {
        if (reply == null || reply.isEmpty() || !npc.isEntityAlive() || npc.isInvisible()) return;
        RenderManager camera = RenderManager.instance;
        FontRenderer font = camera.getFontRenderer();
        if (camera.livingPlayer == null || font == null) return;
        Layout layout = layout(font, reply);
        if (layout.lines.isEmpty()) return;

        int advance = font.FONT_HEIGHT + 2;
        int width = layout.width + 2 * PADDING_X;
        int height = 2 * PADDING_Y + (layout.lines.size() - 1) * advance + font.FONT_HEIGHT;
        int left = -width / 2;
        int right = left + width;
        int bottom = -TAIL_HEIGHT;
        int top = bottom - height;
        // At full size even a fairly short sentence remains legible. Only unusually large replies shrink.
        float scale = Math.min(PIXEL_SIZE, Math.min(2.8F / width, 1.9F / -top));
        boolean frontCamera = camera.options != null && camera.options.thirdPersonView == 2;
        int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);

        GL11.glPushMatrix();
        GL11.glPushAttrib(
            GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT
                | GL11.GL_DEPTH_BUFFER_BIT
                | GL11.GL_CURRENT_BIT
                | GL11.GL_TEXTURE_BIT);
        try {
            GL11.glTranslated(renderX, renderY + npc.height + 0.13, renderZ);
            GL11.glRotatef(-camera.playerViewY, 0, 1, 0);
            GL11.glRotatef(frontCamera ? -camera.playerViewX : camera.playerViewX, 1, 0, 0);
            // After the sign flip, a positive local Z offset layers text in front of the bubble.
            GL11.glScalef(-scale, -scale, -scale);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glDepthMask(true);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            color(BORDER);
            rectangle(left, top, right, bottom, 0);
            triangle(-4, bottom, 4, bottom, 0, 0, 0);
            color(BACKGROUND);
            rectangle(left + 1, top + 1, right - 1, bottom - 1, 0.125F);
            triangle(-2.5F, bottom - 1, 2.5F, bottom - 1, 0, -1.5F, 0.125F);

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glDepthMask(false);
            GL11.glTranslatef(0, 0, 0.25F);
            for (int i = 0; i < layout.lines.size(); i++) {
                font.drawString(layout.lines.get(i), left + PADDING_X, top + PADDING_Y + i * advance, TEXT);
            }
        } finally {
            GL11.glPopAttrib();
            OpenGlHelper.setActiveTexture(activeTexture);
            GL11.glPopMatrix();
        }
    }

    private static Layout layout(FontRenderer font, String reply) {
        boolean unicode = font.getUnicodeFlag();
        if (font != lastFont || unicode != lastUnicode || !reply.equals(lastReply)) {
            int targetWidth = Math
                .min(MAX_TEXT_WIDTH, Math.max(NORMAL_TEXT_WIDTH, (font.getStringWidth(reply) + 16) / 17));
            List<String> lines = wrap(reply, font::getStringWidth, targetWidth, MAX_LINES);
            int width = 0;
            for (String line : lines) width = Math.max(width, font.getStringWidth(line));
            lastLayout = new Layout(lines, width);
            lastFont = font;
            lastUnicode = unicode;
            lastReply = reply;
        }
        return lastLayout;
    }

    /** Unicode line breaks work for Latin words, CJK text and long words without spaces. */
    static List<String> wrap(String reply, ToIntFunction<String> measure, int maxWidth, int maxLines) {
        List<String> lines = new ArrayList<>();
        BreakIterator breaks = BreakIterator.getLineInstance(Locale.ROOT);
        BreakIterator characters = BreakIterator.getCharacterInstance(Locale.ROOT);
        // Prevent Minecraft formatting directives from changing colour or font metrics in a reply.
        String clean = reply.replace("\u00a7", "")
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .trim();
        if (clean.isEmpty()) return lines;
        for (String raw : clean.split("\n", -1)) {
            String paragraph = raw.trim();
            if (paragraph.isEmpty()) {
                if (!lines.isEmpty() && !lines.get(lines.size() - 1)
                    .isEmpty()) lines.add("");
                continue;
            }
            breaks.setText(paragraph);
            characters.setText(paragraph);
            int start = 0;
            while (start < paragraph.length()) {
                if (lines.size() >= maxLines) {
                    String tail = lines.get(maxLines - 1);
                    characters.setText(tail);
                    int end = tail.length();
                    while (end > 0 && measure.applyAsInt(tail.substring(0, end) + "\u2026") > maxWidth) {
                        end = characters.preceding(end);
                    }
                    lines.set(
                        maxLines - 1,
                        tail.substring(0, end)
                            .trim() + "\u2026");
                    return lines;
                }
                int end = start;
                for (int next = breaks.following(start); next != BreakIterator.DONE; next = breaks.next()) {
                    if (measure.applyAsInt(
                        paragraph.substring(start, next)
                            .trim())
                        > maxWidth) break;
                    end = next;
                }
                if (end == start) {
                    for (int next = characters.following(start); next != BreakIterator.DONE; next = characters.next()) {
                        if (measure.applyAsInt(paragraph.substring(start, next)) > maxWidth) break;
                        end = next;
                    }
                    if (end == start) end = characters.following(start);
                }
                lines.add(
                    paragraph.substring(start, end)
                        .trim());
                start = end;
                while (start < paragraph.length() && Character.isWhitespace(paragraph.charAt(start))) start++;
            }
        }
        while (!lines.isEmpty() && lines.get(lines.size() - 1)
            .isEmpty()) lines.remove(lines.size() - 1);
        return lines;
    }

    private static void color(int argb) {
        GL11.glColor4ub((byte) (argb >> 16), (byte) (argb >> 8), (byte) argb, (byte) (argb >> 24));
    }

    private static void rectangle(float left, float top, float right, float bottom, float z) {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex3f(left, top, z);
        GL11.glVertex3f(left, bottom, z);
        GL11.glVertex3f(right, bottom, z);
        GL11.glVertex3f(right, top, z);
        GL11.glEnd();
    }

    private static void triangle(float x1, float y1, float x2, float y2, float x3, float y3, float z) {
        GL11.glBegin(GL11.GL_TRIANGLES);
        GL11.glVertex3f(x1, y1, z);
        GL11.glVertex3f(x2, y2, z);
        GL11.glVertex3f(x3, y3, z);
        GL11.glEnd();
    }

    private static final class Layout {

        private final List<String> lines;
        private final int width;

        private Layout(List<String> lines, int width) {
            this.lines = lines;
            this.width = width;
        }
    }
}
