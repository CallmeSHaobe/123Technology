package com.newmaa.othtech.common.dimensions.gtoregen;

import static com.newmaa.othtech.common.dimensions.gtoregen.EnumDimensionDefOTH.DimNames.ANTIMONIA;
import static galacticgreg.api.enums.DimensionDef.*;

import galacticgreg.WorldgenOreLayerSpace;
import gregtech.api.enums.Materials;
import gregtech.common.OreMixBuilder;
import gregtech.common.WorldgenGTOreLayer;

public enum EnumGTOreMixes {

    Antimony(new OreMixBuilder().name("ore.mix.antimony")
        .heightRange(0, 90)
        .weight(30)
        .density(10)
        .size(40)
        .enableInDim(ANTIMONIA)
        .primary(Materials.Antimony)
        .secondary(Materials.Niobium)
        .inBetween(Materials.Sulfur)
        .sporadic(Materials.Boron));

    public final OreMixBuilder oreMixBuilder;

    EnumGTOreMixes(OreMixBuilder oreMixBuilder) {
        this.oreMixBuilder = oreMixBuilder;
    }

    public WorldgenGTOreLayer addGTOreLayer() {
        return new WorldgenGTOreLayer(this.oreMixBuilder);
    }

    public WorldgenOreLayerSpace addGaGregOreLayer() {
        return new WorldgenOreLayerSpace(this.oreMixBuilder);
    }
}
