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
    private AuthorAnimation() {}

    private static final String[] RAINBOW_CODES = { "§c", // red
        "§6", // orange
        "§e", // yellow
        "§a", // green
        "§b", // cyan
        "§9", // blue
        "§5", // purple
        "§d", // pink
    };
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

    /**
     * A live tooltip supplier — register it on the controller's ItemStack with
     * {@code AnimatedTooltipHandler.addItemTooltip(stack, supplier)} at machine
     * registration time.
     *
     * @return supplier producing a new frame per evaluation
     */
    public static Supplier<String> getTooltipSupplier(String author) {
        return chain(text(PREFIX), () -> buildFrame(System.currentTimeMillis(), author));
    }

    private static String buildFrame(long nowMs, String author) {
        int activeIdx = (int) ((nowMs / CHAR_PERIOD_MS) % author.length());
        int colorIdx = (int) ((nowMs / COLOR_PERIOD_MS) % RAINBOW_CODES.length);
        StringBuilder sb = new StringBuilder(author.length() * 4);
        for (int i = 0; i < author.length(); i++) {
            if (i == activeIdx) {
                sb.append(RAINBOW_CODES[colorIdx])
                    .append(BOUNCE_STYLE)
                    .append(author.charAt(i))
                    .append(RESET);
            } else {
                sb.append(IDLE_COLOR)
                    .append(author.charAt(i));
            }
        }
        return sb.toString();
    }
}
