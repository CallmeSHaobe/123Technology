package com.newmaa.othtech.common.blocks;

import java.util.Random;

/** Client-side timing for the NPC's cosmetic blink; never advances during rendering. */
public final class AyanamiBlink {

    private static final int MIN_INTERVAL = 40;
    private static final int MAX_INTERVAL = 70;
    private static final int DURATION = 5;

    private int remaining;
    private int age = DURATION;

    public void tick(Random random) {
        if (age < DURATION) age++;
        if (remaining == 0) remaining = nextInterval(random);
        if (--remaining == 0) {
            age = 0;
            remaining = nextInterval(random);
        }
    }

    public float closure(float partialTick) {
        float elapsed = age + partialTick;
        if (elapsed >= DURATION) return 0;
        float phase = Math.min(1, Math.min(elapsed / 1.5F, (DURATION - elapsed) / 2.5F));
        return phase * phase * (3 - 2 * phase);
    }

    private static int nextInterval(Random random) {
        return MIN_INTERVAL + random.nextInt(MAX_INTERVAL - MIN_INTERVAL + 1);
    }
}
