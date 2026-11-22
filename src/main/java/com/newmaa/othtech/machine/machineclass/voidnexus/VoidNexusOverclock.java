package com.newmaa.othtech.machine.machineclass.voidnexus;

import gregtech.api.util.OverclockCalculator;

/**
 * Overclock presets for the Void Nexus psionic modules.
 *
 * <p>
 * These presets are fed into GTNH's standard {@link OverclockCalculator}; they do not
 * reimplement voltage-tier, amperage, rounding or one-tick clamping behaviour.
 * </p>
 */
public enum VoidNexusOverclock {

    /** Standard GTNH overclock: 4x EU and half duration per overclock. */
    LOSSY(2),
    /** GTNH perfect overclock: 4x EU and quarter duration per overclock. */
    LOSSLESS(4),
    /** Void Nexus extreme overclock: 4x EU and one eighth duration per overclock. */
    EXTREME(8);

    public final int durationDivisor;

    VoidNexusOverclock(int durationDivisor) {
        this.durationDivisor = durationDivisor;
    }

    /** Creates the same calculator configuration used by GTNH multiblocks. */
    public OverclockCalculator calculator(long recipeEU, long machineVoltage, long amperage, int duration,
        int parallel) {
        return new OverclockCalculator().setRecipeEUt(recipeEU)
            .setEUt(machineVoltage)
            .setAmperage(Math.max(1L, amperage))
            .setDuration(Math.max(1, duration))
            .setParallel(Math.max(1, parallel))
            .setEUtIncreasePerOC(4.0D)
            .setDurationDecreasePerOC(durationDivisor)
            .setAmperageOC(true)
            .setUnlimitedTierSkips();
    }

    public OverclockCalculator calculate(long recipeEU, long machineVoltage, long amperage, int duration,
        int parallel) {
        return calculator(recipeEU, machineVoltage, amperage, duration, parallel).calculate();
    }
}
