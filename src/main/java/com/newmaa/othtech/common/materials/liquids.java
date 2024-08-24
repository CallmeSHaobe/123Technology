package com.newmaa.othtech.common.materials;

import static com.github.bartimaeusnek.bartworks.util.BW_Util.subscriptNumbers;

import com.github.bartimaeusnek.bartworks.system.material.Werkstoff;
import com.github.bartimaeusnek.bartworks.util.Pair;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TextureSet;

public class liquids implements Runnable {

    private static final int offsetID_01 = 123123;
    public static final Werkstoff.GenerationFeatures gf = new Werkstoff.GenerationFeatures();

    public static final Werkstoff MagmatterA = new Werkstoff(
        new short[] { 169, 169, 169 }, // DarkGray
        "磁流体浆液",
        subscriptNumbers("M*"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 1,
        TextureSet.SET_FLUID);
    public static final Werkstoff MagmatterB = new Werkstoff(
        new short[] { 169, 169, 169 }, // DarkGray
        "磁单极子流体",
        subscriptNumbers("M%"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 2,
        TextureSet.SET_FLUID,
        new Pair<>(MagmatterA, 1),
        new Pair<>(Materials.Stone, 2));
    public static final Werkstoff Galaxy = new Werkstoff(
        new short[] { 18, 80, 161 },
        "Galaxy",
        subscriptNumbers("☆"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 3,
        TextureSet.SET_FLUID);
    public static final Werkstoff Void = new Werkstoff(
        new short[] { 166, 212, 223 },
        "虚空熵流",
        subscriptNumbers("Vi"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().addCells()
            .addCells(),
        offsetID_01 + 4,
        TextureSet.SET_FLUID);
    public static final Werkstoff Stars = new Werkstoff(
        new short[] { 221, 226, 188 },
        "星尘凝融",
        subscriptNumbers("St"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().addCells()
            .addCells(),
        offsetID_01 + 5,
        TextureSet.SET_FLUID);

    @Override
    public void run() {
        for (var prefix : OrePrefixes.values()) {
            gf.addPrefix(prefix);
        }
        gf.removePrefix(OrePrefixes.ore);
    }
}
