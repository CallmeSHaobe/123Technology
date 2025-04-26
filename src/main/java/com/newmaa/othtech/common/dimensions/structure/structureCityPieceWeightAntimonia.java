package com.newmaa.othtech.common.dimensions.structure;

public class structureCityPieceWeightAntimonia {

    public Class<? extends structureComponentCity> villagePieceClass;
    public final int villagePieceWeight;
    public int villagePiecesSpawned;
    public int villagePiecesLimit;

    public structureCityPieceWeightAntimonia(Class<? extends structureComponentCity> par1Class, int par2, int par3) {
        this.villagePieceClass = par1Class;
        this.villagePieceWeight = par2;
        this.villagePiecesLimit = (int) (par3 / 0.2D);
    }

    public boolean canSpawnMoreVillagePiecesOfType(int par1) {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
    }

    public boolean canSpawnMoreVillagePieces() {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
    }
}
