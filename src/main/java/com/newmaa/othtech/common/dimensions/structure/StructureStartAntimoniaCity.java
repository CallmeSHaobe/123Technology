package com.newmaa.othtech.common.dimensions.structure;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureStartAntimoniaCity extends StructureStart {

    public StructureStartAntimoniaCity() {}

    public StructureStartAntimoniaCity(World par1World, Random par2Random, int par3, int par4, int par5) {
        super(par3, par4);

        final ArrayList<StructureCityPieceWeightAntimonia> var6 = StructureCityPiecesAntimonia
            .getStructureVillageWeightedPieceList(par2Random, par5);
        final StructureStartPiecesAntimoniaCity var7 = new StructureStartPiecesAntimoniaCity(
            par1World.getWorldChunkManager(),
            0,
            par2Random,
            (par3 << 4) + 2,
            (par4 << 4) + 2,
            var6,
            par5);
        this.components.add(var7);
        var7.buildComponent(var7, this.components, par2Random);
        final ArrayList<Object> var8 = var7.field_74930_j;
        final ArrayList<Object> var9 = var7.field_74932_i;
        int var10;

        while (!var8.isEmpty() || !var9.isEmpty()) {
            StructureComponent var11;

            if (var8.isEmpty()) {
                var10 = par2Random.nextInt(var9.size());
                var11 = (StructureComponent) var9.remove(var10);
            } else {
                var10 = par2Random.nextInt(var8.size());
                var11 = (StructureComponent) var8.remove(var10);
            }
            var11.buildComponent(var7, this.components, par2Random);
        }

        this.updateBoundingBox();
        var10 = 0;
        for (StructureComponent var12 : this.components) {
            if (!(var12 instanceof StructureComponentCityRoadPiece)) {
                ++var10;
            }
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    @Override
    public boolean isSizeableStructure() {
        return true;
    }
}
