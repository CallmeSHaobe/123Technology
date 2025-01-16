package com.newmaa.othtech.common.materials;

import static bartworks.util.BWUtil.subscriptNumbers;

import bartworks.system.material.Werkstoff;
import bartworks.util.Pair;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TextureSet;

public class liquids implements Runnable {

    private static final int offsetID_01 = 123123;
    public static final Werkstoff.GenerationFeatures gf = new Werkstoff.GenerationFeatures();

    public static final Werkstoff MagmatterA = new Werkstoff(
        new short[] { 169, 169, 169 }, // DarkGray
        "FluidMagnetorheological",
        subscriptNumbers("M*"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 1,
        TextureSet.SET_FLUID);
    public static final Werkstoff MagmatterB = new Werkstoff(
        new short[] { 169, 169, 169 }, // DarkGray
        "FluidMagneticmonopole",
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
        "FluidGalaxy",
        subscriptNumbers("☆"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().disable()
            .addCells(),
        offsetID_01 + 3,
        TextureSet.SET_FLUID);
    public static final Werkstoff Void = new Werkstoff(
        new short[] { 166, 212, 223 },
        "FluidVoid",
        subscriptNumbers("Vi"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().addCells()
            .addCells(),
        offsetID_01 + 4,
        TextureSet.SET_FLUID);
    public static final Werkstoff Stars = new Werkstoff(
        new short[] { 221, 226, 188 },
        "MoltenStar",
        subscriptNumbers("St"),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().addCells()
            .addCells(),
        offsetID_01 + 5,
        TextureSet.SET_FLUID);
    public static final Werkstoff LCL = new Werkstoff(
        new short[] { 250, 111, 42 },
        "L.C.L",
        subscriptNumbers(""),
        new Werkstoff.Stats(),
        Werkstoff.Types.ELEMENT,
        new Werkstoff.GenerationFeatures().addCells()
            .addCells(),
        offsetID_01 + 6,
        TextureSet.SET_FLUID);

    @Override
    public void run() {
        for (var prefix : OrePrefixes.values()) {
            gf.addPrefix(prefix);
        }
        gf.removePrefix(OrePrefixes.ore);
    }
}
