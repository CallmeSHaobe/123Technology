package com.newmaa.othtech.machine.machineclass.voidnexus;

import java.util.Collection;
import java.util.Locale;

import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusFuelProfiles;

import thaumcraft.api.aspects.Aspect;

/**
 * Side-effect-free generator arithmetic for the Void Nexus Matrix.
 *
 * <p>
 * Amounts are expressed in essentia units. One standard round lasts five seconds;
 * all supplied fuels are burned in parallel. The machine layer is responsible for
 * item/fluid consumption, wand charge, research gates and actual EU emission.
 * </p>
 */
public final class VoidNexusCalculator {

    private static final double REFERENCE_HEAT = 40_320.0D;
    private static final int GAUSSIAN_INTEGRATION_INTERVALS = 2_048;
    private static final double GAUSSIAN_INTEGRATION_BOUND = 0.1D;
    private static final double GAUSSIAN_INTEGRATION_DENOMINATOR = gaussianWindowIntegral(0.0D);
    private static final double[] STRUCTURE_DECAY_MULTIPLIERS = createStructureDecayMultipliers();
    public static final int BURN_TICKS = 100;

    private VoidNexusCalculator() {}

    public enum Stage {

        UHV(0),
        UEV(1),
        UIV(2),
        UMV(3),
        UXV(4);

        public final int index;

        Stage(int index) {
            this.index = index;
        }

        public static Stage fromCasingTier(int casingTier) {
            switch (Math.max(0, Math.min(4, casingTier))) {
                case 1:
                    return UEV;
                case 2:
                    return UIV;
                case 3:
                    return UMV;
                case 4:
                    return UXV;
                default:
                    return UHV;
            }
        }
    }

    public static final class Fuel {

        public final Aspect aspect;
        public final long amount;
        public final double heat;
        public final double[] phase;

        public Fuel(Aspect aspect, long amount) {
            this.aspect = aspect;
            this.amount = amount;
            this.heat = VoidNexusFuelProfiles.getHeat(aspect);
            this.phase = VoidNexusFuelProfiles.getPhaseVector(aspect);
        }
    }

    public static final class Config {

        public Stage stage = Stage.UHV;
        public int structureTier = 1;
        public boolean improvedCommunication;
        public boolean endlessWandSystem;
        public boolean bloodRuneSaver;
        public boolean dimensionalBeyond;
        public boolean voidLinkTheory;
        public boolean moonNest;
        public boolean hungryTool;
        public boolean lifeWeave;
        public boolean worldDevourer;
        public boolean psionicHarmony;
        public boolean moonContract;
        public boolean toolContract;
        public boolean weaveContract;
        public boolean devourerContract;
        public boolean enteredVoid;
        public boolean materialInputMode;
        public boolean alienVisitor;
        public boolean goldenDream;
        public boolean endlessTide;
        public boolean unshakenMountain;
        public boolean hollowPoint;
        public boolean emptyThrone;
        public boolean darkRiver;
        public boolean reforgedVoid;
        public boolean cycleBonus;
        public boolean starArray;
        public boolean manifestedPsionics;
        public boolean bloodSupply;
        public int burnTicks = BURN_TICKS;
        public long virtualPotential;
    }

    public static final class Result {

        public long power;
        public double rawThermalEnergy;
        public double averageHeat;
        public double deviation;
        public double qualityMultiplier;
        public double phaseMultiplier;
        public double gloriaMultiplier;
        public double efficiencyMultiplier;
        public double ordinaryMultiplier;
        public double specialMultiplier;
        public long totalEssentia;

        public boolean isBalanced() {
            return totalEssentia > 0L && deviation <= 1.0E-9D;
        }
    }

    public static Result calculate(Stage stage, Collection<Fuel> inputs, Config config) {
        Config safeConfig = config == null ? new Config() : config;
        Result result = new Result();
        if (inputs == null || inputs.isEmpty()) return result;

        double rawThermal = 0.0D;
        double gloriaThermal = 0.0D;
        long totalEssentia = 0L;
        double[] phaseEnergy = new double[VoidNexusFuelProfiles.PRIMAL_COUNT];

        for (Fuel input : inputs) {
            if (input == null || input.aspect == null || input.amount <= 0L) continue;
            double amount = input.amount;
            double heat = adjustedHeat(input.aspect, input.heat, safeConfig);
            double thermal = amount * heat;
            rawThermal += thermal;
            totalEssentia = saturatingAdd(totalEssentia, input.amount);
            if ("gloria".equalsIgnoreCase(input.aspect.getTag())) gloriaThermal += thermal;
            for (int i = 0; i < phaseEnergy.length; i++) phaseEnergy[i] += thermal * input.phase[i];
        }

        if (rawThermal <= 0.0D || totalEssentia <= 0L) return result;

        result.rawThermalEnergy = rawThermal;
        result.totalEssentia = totalEssentia;
        result.averageHeat = rawThermal / totalEssentia;
        result.deviation = safeConfig.starArray ? 0.0D : deviation(phaseEnergy, rawThermal);
        result.qualityMultiplier = 1.0D + 0.10D * Math.log(Math.max(0.01D, result.averageHeat / REFERENCE_HEAT));
        result.phaseMultiplier = phaseMultiplier(safeConfig, result.deviation);
        result.gloriaMultiplier = gloriaMultiplier(rawThermal, gloriaThermal, safeConfig.goldenDream);
        result.efficiencyMultiplier = efficiencyMultiplier(safeConfig);
        result.ordinaryMultiplier = ordinaryMultiplier(safeConfig);
        result.specialMultiplier = specialMultiplier(safeConfig);
        int structureTier = Math.max(1, Math.min(STRUCTURE_DECAY_MULTIPLIERS.length, safeConfig.structureTier));

        double power = rawThermal / Math.max(1, safeConfig.burnTicks);
        power *= result.qualityMultiplier;
        power *= result.phaseMultiplier;
        power *= result.gloriaMultiplier;
        power *= result.efficiencyMultiplier;
        power *= result.ordinaryMultiplier;
        power *= result.specialMultiplier;
        power *= STRUCTURE_DECAY_MULTIPLIERS[structureTier - 1];
        power *= structureUpgradeMultiplier(structureTier, safeConfig);

        // Material input applies an output penalty in addition to the separate burn-efficiency penalty below.
        if (safeConfig.materialInputMode) power *= 0.20D;
        if (safeConfig.bloodSupply) power *= 1.15D;
        if (safeConfig.starArray) power *= 360.0D;
        result.power = toSaturatedLong(power);
        return result;
    }

    public static long getBalancePotentialIntervalTicks() {
        return 20L * 900L;
    }

    public static long getContinuousPotentialIntervalTicks() {
        return 20L * 7_200L;
    }

    public static long getDarkRiverIntervalTicks() {
        return 20L * 360L;
    }

    public static long getPermanentWarpIntervalTicks() {
        return 20L * 60L;
    }

    public static long getWarpCheckIntervalTicks() {
        return 20L * 3_600L;
    }

    public static double getVirtualPotentialMultiplier(long points) {
        long value = Math.max(0L, points);
        double result = 1.0D;
        result += 0.05D * Math.min(value, 20L);
        result += 0.01D * Math.min(Math.max(value - 20L, 0L), 80L);
        result += 0.002D * Math.max(value - 100L, 0L);
        return result;
    }

    public static double getVirtualPotentialSpeedPercent(long points) {
        long value = Math.max(0L, points);
        double result = 0.0D;
        result += 5.0D * Math.min(value, 20L);
        result += Math.min(Math.max(value - 20L, 0L), 80L);
        result += 0.2D * Math.max(value - 100L, 0L);
        return result;
    }

    public static long suggestedMaximum(Stage stage) {
        switch (stage) {
            case UHV:
                return 25_260_551_724L;
            case UEV:
                return 123_772_661_760L;
            case UIV:
                return 2_000_000_000_000L;
            case UMV:
                return 562_949_951_324_160L;
            case UXV:
                return 21_138_317_123_677_440L;
            default:
                return Long.MAX_VALUE;
        }
    }

    private static double adjustedHeat(Aspect aspect, double heat, Config config) {
        String tag = aspect.getTag()
            .toLowerCase(Locale.ROOT);
        double result = heat;
        if (config.moonNest && isOneOf(tag, "tempus", "spiritus", "astrum")) result *= 2.0D;
        if (config.hungryTool && isOneOf(tag, "instrumentum", "fabrico", "machina")) result *= 2.0D;
        if (config.lifeWeave && isOneOf(tag, "victus", "cognitio", "mortuus")) result *= 2.0D;
        if (config.worldDevourer && isOneOf(tag, "gula", "superbia", "vitium")) result *= 2.0D;
        if (config.goldenDream && "terminus".equals(tag)) result *= 4.0D;
        return result;
    }

    private static double deviation(double[] phaseEnergy, double total) {
        if (total <= 0.0D) return 0.0D;
        double sumSquares = 0.0D;
        for (double value : phaseEnergy) {
            double proportion = value / total;
            sumSquares += proportion * proportion;
        }
        double normalized = (sumSquares - 1.0D / VoidNexusFuelProfiles.PRIMAL_COUNT) / (5.0D / 6.0D);
        return Math.max(0.0D, Math.min(1.0D, normalized));
    }

    private static double phaseMultiplier(Config config, double deviation) {
        double maximumBonus = config.psionicHarmony ? 1.0D
            : config.dimensionalBeyond || config.voidLinkTheory ? 0.8D : 0.2D;
        return 1.0D + maximumBonus * (1.0D - deviation);
    }

    private static double gloriaMultiplier(double totalThermal, double gloriaThermal, boolean goldenDream) {
        if (totalThermal <= 0.0D || gloriaThermal <= 0.0D) return 1.0D;
        double share = gloriaThermal / totalThermal;
        double cap = goldenDream ? 4.0D : 2.0D;
        return 1.0D + Math.min(cap, 35.0D * share / (share + 0.10D));
    }

    private static double efficiencyMultiplier(Config config) {
        double efficiency = 1.0D;
        if (config.improvedCommunication) efficiency += 0.05D;
        if (config.dimensionalBeyond) efficiency += 0.10D;
        if (config.psionicHarmony) efficiency += 0.50D;
        // This is the burn-efficiency penalty. The final-output penalty is applied separately in calculate().
        if (config.materialInputMode) efficiency *= 0.20D;
        return efficiency;
    }

    private static double ordinaryMultiplier(Config config) {
        double bonus = 0.0D;
        if (config.improvedCommunication) bonus += 0.10D;
        if (config.moonNest) bonus += 0.10D;
        if (config.hungryTool) bonus += 0.10D;
        if (config.lifeWeave) bonus += 0.10D;
        if (config.worldDevourer) bonus += 0.10D;
        if (config.psionicHarmony) bonus += 0.25D;
        if (config.moonContract) bonus += 0.15D;
        if (config.toolContract) bonus += 0.15D;
        if (config.weaveContract) bonus += 0.15D;
        if (config.devourerContract) bonus += 0.15D;
        if (config.enteredVoid) bonus += 0.20D;
        if (config.alienVisitor) bonus += 0.20D;
        if (config.goldenDream) bonus += 0.20D;
        if (config.endlessTide) bonus += 0.20D;
        if (config.unshakenMountain) bonus += 0.20D;
        if (config.hollowPoint) bonus += 0.20D;
        if (config.emptyThrone) bonus += 0.20D;
        if (config.darkRiver) bonus += 0.50D;
        return 1.0D + bonus;
    }

    private static double specialMultiplier(Config config) {
        double multiplier = 1.0D;
        double stabilizerMultiplier = 1.0D;
        if (config.manifestedPsionics) stabilizerMultiplier = config.enteredVoid ? 2.5D : 2.0D;
        // The stabilizer cannot be produced before UIV, so tiers 1 and 2 receive the same x2 as a hidden baseline.
        if (config.structureTier <= 2) stabilizerMultiplier = Math.max(2.0D, stabilizerMultiplier);
        multiplier *= stabilizerMultiplier;
        if (config.alienVisitor) multiplier *= getVirtualPotentialMultiplier(config.virtualPotential);
        if (config.cycleBonus) multiplier *= 5.0D;
        return multiplier;
    }

    private static double[] createStructureDecayMultipliers() {
        double[] multipliers = new double[5];
        for (int structureTier = 1; structureTier <= multipliers.length; structureTier++) {
            double exponent = -0.6D * square(structureTier - 1);
            exponent += 0.7D * gaussianWindow(structureTier, 1);
            exponent -= 1.1D * gaussianWindow(structureTier, 2);
            exponent -= 0.2D * gaussianWindow(structureTier, 3);
            multipliers[structureTier - 1] = Math.exp(exponent);
        }
        return multipliers;
    }

    private static double gaussianWindow(int structureTier, int window) {
        return gaussianWindowIntegral(structureTier - window) / GAUSSIAN_INTEGRATION_DENOMINATOR;
    }

    private static double gaussianWindowIntegral(double center) {
        int intervals = GAUSSIAN_INTEGRATION_INTERVALS;
        double lower = -GAUSSIAN_INTEGRATION_BOUND;
        double upper = GAUSSIAN_INTEGRATION_BOUND;
        double step = (upper - lower) / intervals;
        double sum = gaussianKernel(center - lower) + gaussianKernel(center - upper);
        for (int i = 1; i < intervals; i++) {
            double x = lower + step * i;
            sum += (i & 1) == 0 ? 2.0D * gaussianKernel(center - x) : 4.0D * gaussianKernel(center - x);
        }
        return sum * step / 3.0D;
    }

    private static double gaussianKernel(double value) {
        return Math.exp(-square(value) / 0.1D);
    }

    private static double structureUpgradeMultiplier(int structureTier, Config config) {
        int tierOffset = structureTier - 1;
        double multiplier = 1.0D;
        if (config.endlessWandSystem) multiplier *= Math.exp(1.0D);
        if (config.improvedCommunication) multiplier *= Math.exp(1.5D * tierOffset);
        if (config.bloodRuneSaver) multiplier *= Math.exp(1.6D * tierOffset);
        return multiplier;
    }

    private static double square(double value) {
        return value * value;
    }

    private static boolean isOneOf(String value, String... options) {
        for (String option : options) {
            if (option.equals(value)) return true;
        }
        return false;
    }

    private static long saturatingAdd(long left, long right) {
        if (right > 0L && left > Long.MAX_VALUE - right) return Long.MAX_VALUE;
        return left + right;
    }

    private static long toSaturatedLong(double value) {
        if (!Double.isFinite(value) || value <= 0.0D) return 0L;
        if (value >= Long.MAX_VALUE) return Long.MAX_VALUE;
        return (long) Math.floor(value);
    }
}
