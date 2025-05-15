package com.newmaa.othtech.common.dimensions.structure;

public class StructureCityPieceWeightAntimonia {

    public Class<? extends StructureComponentCity> villagePieceClass;
    public final int villagePieceWeight;
    public int villagePiecesSpawned;
    public int villagePiecesLimit;

    public StructureCityPieceWeightAntimonia(Class<? extends StructureComponentCity> villagePieceClass,
        int villagePieceWeight, int villagePiecesLimitBase) {
        this.villagePieceClass = villagePieceClass;
        this.villagePieceWeight = villagePieceWeight;
        this.villagePiecesLimit = (int) (villagePiecesLimitBase / 0.2D);
    }

    public boolean canSpawnMoreVillagePiecesOfType(int par1) {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
    }

    public boolean canSpawnMoreVillagePieces() {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
    }
}
