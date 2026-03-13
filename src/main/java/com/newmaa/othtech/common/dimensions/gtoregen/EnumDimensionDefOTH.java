package com.newmaa.othtech.common.dimensions.gtoregen;

import galacticgreg.api.Enums;
import galacticgreg.api.ModDimensionDef;

public enum EnumDimensionDefOTH {

    ROSS123B(new ModDimensionDef(
        EnumDimensionDefOTH.DimNames.ROSS123B,
        "newmaa.othtech.common.dimensions.ross123b.ChunkProviderRoss123b",
        Enums.DimensionType.Planet,
        EnumDimensionBlockMetaDefList.Ross123b.DBMDefList)),
    ANTIMONIA(new ModDimensionDef(
        EnumDimensionDefOTH.DimNames.ANTIMONIA,
        "newmaa.othtech.common.dimensions.ross123b.ChunkProviderAntimonia",
        Enums.DimensionType.Planet,
        EnumDimensionBlockMetaDefList.Antimonia.DBMDefList));

    public static class DimNames {

        public static final String ANTIMONIA = "Antimonia";
        public static final String ROSS123B = "Ross123b";

    }

    public final ModDimensionDef enumDimensionDefOTH;

    EnumDimensionDefOTH(ModDimensionDef modDimDef) {
        this.enumDimensionDefOTH = modDimDef;
    }
}
