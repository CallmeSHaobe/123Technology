package com.newmaa.othtech.common.dimensions.gtoregen;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ROSS123B_NAME;

import gregtech.api.enums.Materials;
import gregtech.common.OreMixBuilder;

public final class Ross123bGTOreVeinReg {

    private Ross123bGTOreVeinReg() {}

    public static void registerAll() {
        AntimoniaGTOreVeinReg.register(veinIron());
    }

    private static OreMixBuilder veinIron() {
        return new OreMixBuilder().name("otht.vein.ross.fe")
            .enableInDim(ROSS123B_NAME)
            .heightRange(2, 60)
            .weight(40)
            .density(2)
            .size(12)
            .primary(Materials.Carbon)
            .secondary(Materials.Carbon)
            .inBetween(Materials.Carbon)
            .sporadic(Materials.Stone);
    }
}
