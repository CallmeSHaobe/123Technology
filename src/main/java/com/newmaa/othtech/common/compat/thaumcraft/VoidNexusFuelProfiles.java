package com.newmaa.othtech.common.compat.thaumcraft;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import thaumcraft.api.aspects.Aspect;

/**
 * Stable aspect metadata used by the Void Nexus Matrix.
 *
 * <p>
 * Thaumcraft stores the recursion graph in {@link Aspect#getComponents()}, but some
 * addon aspects are registered with incomplete metadata. The explicit level table
 * keeps the fuel balance deterministic across GTNH mod-list changes.
 * </p>
 */
public final class VoidNexusFuelProfiles {

    public static final int PRIMAL_COUNT = 6;

    /** Nine fuel-grade multipliers from the machine specification. */
    public static final double[] HEAT_MULTIPLIERS = { 1.00D, 1.06D, 1.12D, 1.19D, 1.26D, 1.34D, 1.42D, 1.51D, 1.60D };

    private static final double BASE_HEAT = 32_000.0D;
    private static final double SPECIAL_HEAT = 51_200.0D;
    private static final Map<String, Integer> ASPECT_LEVELS;

    static {
        Map<String, Integer> levels = new HashMap<>();
        addLevel(levels, 1, "perditio", "aqua", "ignis", "terra", "ordo", "aer");
        addLevel(
            levels,
            2,
            "lux",
            "vacuos",
            "potentia",
            "vitreus",
            "motus",
            "permutatio",
            "venenum",
            "gelum",
            "victus",
            "tempestas");
        addLevel(
            levels,
            3,
            "praecantatio",
            "radio",
            "sano",
            "iter",
            "herba",
            "fames",
            "volatus",
            "tenebrae",
            "bestia",
            "mortuus",
            "limus",
            "primordium",
            "vinculum",
            "metallum",
            "tempus");
        addLevel(
            levels,
            4,
            "magneto",
            "corpus",
            "infernus",
            "auram",
            "astrum",
            "spiritus",
            "alienis",
            "vitium",
            "exanimis",
            "arbor",
            "gula",
            "caelum",
            "superbia");
        addLevel(levels, 5, "sensus", "luxuria", "desidia", "cognitio");
        addLevel(levels, 6, "humanus", "aequalitas", "vesania", "strontio", "invidia");
        addLevel(levels, 7, "perfodio", "instrumentum", "messis", "lucrum", "gloria");
        addLevel(levels, 8, "nebrisum", "meto", "machina", "terminus", "telum", "pannus", "tutamen", "fabrico");
        addLevel(levels, 9, "electrum", "ira", "tabernus");
        ASPECT_LEVELS = Collections.unmodifiableMap(levels);
    }

    private VoidNexusFuelProfiles() {}

    public static int getLevel(Aspect aspect) {
        if (aspect == null) return 0;
        String tag = normalize(aspect.getTag());
        Integer known = ASPECT_LEVELS.get(tag);
        if (known != null) return known;
        return Math.max(1, Math.min(9, recursiveLevel(aspect, 1)));
    }

    public static double getHeat(Aspect aspect) {
        String tag = normalize(aspect == null ? null : aspect.getTag());
        if ("gloria".equals(tag) || "terminus".equals(tag)) return SPECIAL_HEAT;
        int level = Math.max(1, Math.min(9, getLevel(aspect)));
        return BASE_HEAT * HEAT_MULTIPLIERS[level - 1];
    }

    /**
     * Converts an aspect into its six-primal phase vector.
     *
     * <p>
     * Components are reduced recursively and each level splits its contribution
     * equally. Unknown/custom aspects fall back to an even six-phase vector so that
     * addon aspects remain burnable instead of being silently ignored.
     * </p>
     */
    public static double[] getPhaseVector(Aspect aspect) {
        double[] result = new double[PRIMAL_COUNT];
        if (aspect == null) return result;
        reduce(aspect, 1.0D, result, 0);
        normalizeFallback(result);
        return result;
    }

    public static int getPrimalIndex(Aspect aspect) {
        if (aspect == Aspect.AIR) return 5;
        if (aspect == Aspect.WATER) return 1;
        if (aspect == Aspect.FIRE) return 2;
        if (aspect == Aspect.EARTH) return 3;
        if (aspect == Aspect.ORDER) return 4;
        if (aspect == Aspect.ENTROPY) return 0;
        return -1;
    }

    public static Aspect getPrimal(int index) {
        switch (index) {
            case 0:
                return Aspect.ENTROPY;
            case 1:
                return Aspect.WATER;
            case 2:
                return Aspect.FIRE;
            case 3:
                return Aspect.EARTH;
            case 4:
                return Aspect.ORDER;
            case 5:
                return Aspect.AIR;
            default:
                return null;
        }
    }

    private static int recursiveLevel(Aspect aspect, int depth) {
        Aspect[] components = aspect.getComponents();
        if (components == null || components.length != 2 || depth >= 12) return depth;
        int left = recursiveLevel(components[0], depth + 1);
        int right = recursiveLevel(components[1], depth + 1);
        return Math.max(left, right);
    }

    private static void reduce(Aspect aspect, double weight, double[] out, int depth) {
        if (aspect == null || weight <= 0.0D || depth >= 12) return;
        int directIndex = getPrimalIndex(aspect);
        if (directIndex >= 0) {
            out[directIndex] += weight;
            return;
        }

        Aspect[] components = aspect.getComponents();
        if (components == null || components.length != 2 || components[0] == null || components[1] == null) {
            return;
        }
        reduce(components[0], weight * 0.5D, out, depth + 1);
        reduce(components[1], weight * 0.5D, out, depth + 1);
    }

    private static void normalizeFallback(double[] vector) {
        double sum = 0.0D;
        for (double value : vector) sum += value;
        if (sum > 1.0E-12D) {
            for (int i = 0; i < vector.length; i++) vector[i] /= sum;
            return;
        }
        for (int i = 0; i < vector.length; i++) vector[i] = 1.0D / PRIMAL_COUNT;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT);
    }

    private static void addLevel(Map<String, Integer> levels, int level, String... tags) {
        for (String tag : tags) levels.put(tag, level);
    }
}
