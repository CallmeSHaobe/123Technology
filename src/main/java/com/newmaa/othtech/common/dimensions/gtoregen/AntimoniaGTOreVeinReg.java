package com.newmaa.othtech.common.dimensions.gtoregen;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;

import java.util.Collections;

import gregtech.api.enums.Materials;
import gregtech.api.enums.StoneType;
import gregtech.api.interfaces.IOreMaterial;
import gregtech.common.OreMixBuilder;
import gregtech.common.WorldgenGTOreLayer;
import gtneioreplugin.util.DimensionHelper;

public final class AntimoniaGTOreVeinReg {

    private static void registerVein(String name, IOreMaterial material, int weight, int minY, int maxY, int density,
        int size) {
        new WorldgenGTOreLayer(
            new OreMixBuilder().name(name)
                .enableInDim(ANTIMONIA_NAME)
                .heightRange(minY, maxY)
                .weight(weight)
                .density(density)
                .size(size)
                .primary(material)
                .secondary(material)
                .inBetween(material)
                .sporadic(material));
    }

    public static void register() {
        // ZMY矿脉
        DimensionHelper.register(
            "planet.Antimonia",
            ANTIMONIA_NAME,
            "Antimonia",
            "sb",
            "gtnop.tier.4",
            Collections.singletonList(StoneType.Stone));

        registerVein("ore.Antimonia.zmy.sb", Materials.Antimony, 110, 20, 100, 5, 28);

        registerVein("ore.Antimonia.zmy.b", Materials.Boron, 110, 20, 100, 5, 28);

        registerVein("ore.Antimonia.zmy.s", Materials.Sulfur, 110, 20, 100, 5, 28);
    }
}
