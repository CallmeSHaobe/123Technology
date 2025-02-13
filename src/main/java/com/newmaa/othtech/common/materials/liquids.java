package com.newmaa.othtech.common.materials;

import static bartworks.util.BWUtil.subscriptNumbers;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TextureSet;

public class liquids implements Runnable {

    private static final int offsetID_01 = 12300;
    public static final Werkstoff.GenerationFeatures gf = new Werkstoff.GenerationFeatures();
    public static final Werkstoff Galaxy = new Werkstoff(
        new short[] { 18, 80, 161 },
        "FluidGalaxy",
        subscriptNumbers("☆"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 3,
        TextureSet.SET_FLUID);
    public static final Werkstoff Void = new Werkstoff(
        new short[] { 166, 212, 223 },
        "FluidVoid",
        subscriptNumbers("Vi"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 4,
        TextureSet.SET_FLUID);
    public static final Werkstoff Stars = new Werkstoff(
        new short[] { 221, 226, 188 },
        "MoltenStar",
        subscriptNumbers("St"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 5,
        TextureSet.SET_FLUID);
    public static final Werkstoff LCL = new Werkstoff(
        new short[] { 250, 111, 42 },
        "L.C.L",
        subscriptNumbers(""),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 6,
        TextureSet.SET_FLUID);
    public static final Werkstoff ALS = new Werkstoff(
        new short[] { 0, 255, 255 },
        "A.L.S",
        subscriptNumbers(""),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 7,
        TextureSet.SET_FLUID);
    public static final Werkstoff FUELA = new Werkstoff(
        new short[] { 154, 249, 72 },
        "FUELA",
        subscriptNumbers("化学能的尽头"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 8,
        TextureSet.SET_FLUID);
    public static final Werkstoff FUELA_DE = new Werkstoff(
        new short[] { 116, 182, 59 },
        "FUELA_DE",
        subscriptNumbers("他能干什么？"),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 9,
        TextureSet.SET_FLUID);
    public static final Werkstoff PromoterZPM = new Werkstoff(
        new short[] { 247, 22, 22 },
        "PromoterZPM",
        subscriptNumbers(""),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 10,
        TextureSet.SET_FLUID);
    public static final Werkstoff PromoterUEV = new Werkstoff(
        new short[] { 236, 242, 255 },
        "PromoterUEV",
        subscriptNumbers(""),
        new Werkstoff.Stats(),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().disable().addCells(),
        offsetID_01 + 11,
        TextureSet.SET_FLUID);

    @Override
    public void run() {
        for (var prefix : OrePrefixes.values()) {
            gf.addPrefix(prefix);
        }
        gf.removePrefix(OrePrefixes.ore);
    }
}
