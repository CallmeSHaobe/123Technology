package com.newmaa.othtech.machine.gui;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;

public final class OTHGuiBranding {

    private static final String DOMAIN = "123technology";
    private static final String LOGO_PATH = "items/MetaItem/0";
    private static final String GLYPH_PATH = "gui/drone/glyph_";
    private static final String[] GLYPHS = { "1", "2", "3", "", "t", "e", "c" };
    private static final UITexture LOGO = UITexture.fullImage(DOMAIN, LOGO_PATH);
    private static final IDrawable[] GLYPH_TEXTURES = new IDrawable[GLYPHS.length];

    static {
        for (int i = 0; i < GLYPHS.length; i++) {
            GLYPH_TEXTURES[i] = GLYPHS[i].isEmpty() ? IDrawable.EMPTY
                : UITexture.fullImage(DOMAIN, GLYPH_PATH + GLYPHS[i]);
        }
    }

    private OTHGuiBranding() {}

    public static IDrawable.DrawableWidget logo() {
        return new IDrawable.DrawableWidget(LOGO).size(18);
    }

    public static Flow glyphColumn(int glyphSize) {
        Flow column = Flow.column()
            .size(glyphSize, glyphSize * GLYPHS.length);
        for (IDrawable glyph : GLYPH_TEXTURES) {
            column.child(new IDrawable.DrawableWidget(glyph).size(glyphSize));
        }
        return column;
    }

    public static void addLegacyTecTechBranding(ModularWindow.Builder builder) {
        // The legacy terminal's scrollable starts at x=10; keep the glyphs wholly in its six-pixel left gutter.
        int glyphSize = 6;
        for (int i = 0; i < GLYPHS.length; i++) {
            if (GLYPHS[i].isEmpty()) continue;
            builder.widget(
                new DrawableWidget()
                    .setDrawable(
                        com.gtnewhorizons.modularui.api.drawable.UITexture.fullImage(DOMAIN, GLYPH_PATH + GLYPHS[i]))
                    .setSize(glyphSize, glyphSize)
                    .setPos(4, 36 + i * glyphSize));
        }
        builder.widget(
            new DrawableWidget()
                .setDrawable(com.gtnewhorizons.modularui.api.drawable.UITexture.fullImage(DOMAIN, LOGO_PATH))
                .setSize(18, 18)
                .setPos(173, 74));
    }
}
