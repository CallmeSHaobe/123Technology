package com.newmaa.othtech.utils;

import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.BOLD;
import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.RESET;
import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.UNDERLINE;
import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.chain;
import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.text;

import java.util.function.Supplier;

public final class AuthorAnimation {

    // ── Rainbow animation constants ──────────────────────────────────────────
    /** Minecraft § colour-codes that form the rainbow sequence (red → pink). */
    private static final String[] RAINBOW_CODES = { "§c", // red
        "§6", // orange
        "§e", // yellow
        "§a", // green
        "§b", // cyan
        "§9", // blue
        "§5", // purple
        "§d", // pink
    };
    /** Author name whose characters are individually animated. */
    private static final String AUTHOR = "czqwq";
    /** Static label rendered before the animated name. */
    private static final String PREFIX = "§b§lAuthor:§r ";
    /** Colour of the non-spotlight characters. */
    private static final String IDLE_COLOR = "§7";
    /** Emphasis of the spotlight character (bold + underline = "bounced"). */
    private static final String BOUNCE_STYLE = BOLD + UNDERLINE;
    /** Milliseconds each character holds the spotlight before advancing. */
    private static final long CHAR_PERIOD_MS = 400;
    /** Milliseconds each rainbow hue is displayed while a character is spotlit. */
    private static final long COLOR_PERIOD_MS = 90;

    private AuthorAnimation() {}

    /**
     * A live tooltip supplier — register it on the controller's ItemStack with
     * {@code AnimatedTooltipHandler.addItemTooltip(stack, supplier)} at machine
     * registration time.
     *
     * @return supplier producing a new frame per evaluation
     */
    public static Supplier<String> getTooltipSupplier() {
        return chain(text(PREFIX), () -> buildFrame(System.currentTimeMillis()));
    }

    /**
     * @return an animated frame for the current time.
     */
    public static String getFrame() {
        return getTooltipSupplier().get();
    }

    /**
     * Computes the animated frame at the given instant.
     *
     * @param nowMs wall-clock time in milliseconds
     * @return a single line string with § colour codes
     */
    public static String getFrame(long nowMs) {
        return PREFIX + buildFrame(nowMs);
    }

    private static String buildFrame(long nowMs) {
        int activeIdx = (int) ((nowMs / CHAR_PERIOD_MS) % AUTHOR.length());
        int colorIdx = (int) ((nowMs / COLOR_PERIOD_MS) % RAINBOW_CODES.length);
        StringBuilder sb = new StringBuilder(AUTHOR.length() * 4);
        for (int i = 0; i < AUTHOR.length(); i++) {
            if (i == activeIdx) {
                sb.append(RAINBOW_CODES[colorIdx])
                    .append(BOUNCE_STYLE)
                    .append(AUTHOR.charAt(i))
                    .append(RESET);
            } else {
                sb.append(IDLE_COLOR)
                    .append(AUTHOR.charAt(i));
            }
        }
        return sb.toString();
    }
}
