package com.newmaa.othtech.common.materials;

import static bartworks.util.BWUtil.subscriptNumbers;

import bartworks.system.material.Werkstoff;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TextureSet;

public class BWMaterials implements Runnable {

    private static final int offsetID_01 = 12300;
    public static final Werkstoff.GenerationFeatures gf = new Werkstoff.GenerationFeatures();
    public static final Werkstoff IrOsSm = new Werkstoff(
        new short[] { 36, 168, 255 },
        "IrOsSm",
        subscriptNumbers("IrOsSm"),
        new Werkstoff.Stats().setMeltingPoint(114514),
        Werkstoff.Types.MIXTURE,
        new Werkstoff.GenerationFeatures().onlyDust()
            .addMolten()
            .addMetalItems()
            .addCraftingMetalWorkingItems()
            .addMetaSolidifierRecipes()
            .addMetalCraftingSolidifierRecipes(),
        offsetID_01 + 20,
        TextureSet.SET_SHINY);

    @Override
    public void run() {
        for (var prefix : OrePrefixes.values()) {
            gf.addPrefix(prefix);
        }
        gf.removePrefix(OrePrefixes.ore);
    }
}
