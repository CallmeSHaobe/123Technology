package com.newmaa.othtech.common.dimensions.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class StructureCityPiecesAntimonia {

    public static ArrayList<StructureCityPieceWeightAntimonia> getStructureVillageWeightedPieceList(Random par0Random,
        int par1) {
        final ArrayList<StructureCityPieceWeightAntimonia> var2 = new ArrayList<>();
        var2.add(
            new StructureCityPieceWeightAntimonia(
                StructureComponentCitySolar.class,
                2,
                MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, (int) (5 + par1 * 1.5))));
        var2.add(
            new StructureCityPieceWeightAntimonia(
                StructureComponentCityBuilding.class,
                4,
                MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 5 + par1 * 5)));
        var2.add(
            new StructureCityPieceWeightAntimonia(
                StructureComponentCityBuildingBlack.class,
                4,
                MathHelper.getRandomIntegerInRange(par0Random, 2 + par1, 5 + par1 * 5)));

        final Iterator<StructureCityPieceWeightAntimonia> var3 = var2.iterator();

        while (var3.hasNext()) {
            if (var3.next().villagePiecesLimit == 0) {
                var3.remove();
            }
        }

        return var2;
    }

    private static int func_75079_a(ArrayList<StructureCityPieceWeightAntimonia> par0List) {
        boolean var1 = false;
        int var2 = 0;
        StructureCityPieceWeightAntimonia var4;

        for (final Iterator<StructureCityPieceWeightAntimonia> var3 = par0List.iterator(); var3
            .hasNext(); var2 += var4.villagePieceWeight) {
            var4 = var3.next();

            if (var4.villagePiecesLimit > 0 && var4.villagePiecesSpawned < var4.villagePiecesLimit) {
                var1 = true;
            }
        }

        return var1 ? var2 : -1;
    }

    private static StructureComponentCity func_75083_a(StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece,
        StructureCityPieceWeightAntimonia par1StructureVillagePieceWeight, List<StructureComponent> par2List,
        Random par3Random, int par4, int par5, int par6, int par7, int par8) {
        final Class<?> var9 = par1StructureVillagePieceWeight.villagePieceClass;
        Object var10 = null;

        if (var9 == StructureComponentCitySolar.class) {
            var10 = StructureComponentCitySolar
                .func_74900_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        } else if (var9 == StructureComponentCityBuilding.class) {
            var10 = StructureComponentCityBuilding
                .func_74921_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        } else if (var9 == StructureComponentCityBuildingBlack.class) {
            var10 = StructureComponentCityBuildingBlack
                .func_74921_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        }
        // } else if (var9 == StructureComponentVillageHouse.class) {
        // var10 = StructureComponentVillageHouse
        // .func_74921_a(par0ComponentVillageStartPiece, par2List, par3Random, par4, par5, par6, par7, par8);
        // }

        return (StructureComponentCity) var10;
    }

    private static StructureComponentCity getNextVillageComponent(
        StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece, List<StructureComponent> par1List,
        Random par2Random, int par3, int par4, int par5, int par6, int par7) {
        final int var8 = StructureCityPiecesAntimonia
            .func_75079_a(par0ComponentVillageStartPiece.structureVillageWeightedPieceList);

        if (var8 <= 0) {
            return null;
        }
        int var9 = 0;

        while (var9 < 5) {
            ++var9;
            int var10 = par2Random.nextInt(var8);
            for (StructureCityPieceWeightAntimonia var12 : par0ComponentVillageStartPiece.structureVillageWeightedPieceList) {
                var10 -= var12.villagePieceWeight;

                if (var10 < 0) {
                    if (!var12.canSpawnMoreVillagePiecesOfType(par7)
                        || var12 == par0ComponentVillageStartPiece.structVillagePieceWeight
                            && par0ComponentVillageStartPiece.structureVillageWeightedPieceList.size() > 1) {
                        break;
                    }

                    final StructureComponentCity var13 = StructureCityPiecesAntimonia.func_75083_a(
                        par0ComponentVillageStartPiece,
                        var12,
                        par1List,
                        par2Random,
                        par3,
                        par4,
                        par5,
                        par6,
                        par7);

                    if (var13 != null) {
                        ++var12.villagePiecesSpawned;
                        par0ComponentVillageStartPiece.structVillagePieceWeight = var12;

                        if (!var12.canSpawnMoreVillagePieces()) {
                            par0ComponentVillageStartPiece.structureVillageWeightedPieceList.remove(var12);
                        }

                        return var13;
                    }
                }
            }
        }

        final StructureBoundingBox var14 = StructureComponentCityLights
            .func_74904_a(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6);

        if (var14 != null) {
            return new StructureComponentCityLights(par0ComponentVillageStartPiece, par7, par2Random, var14, par6);
        }
        return null;
    }

    /**
     * attempts to find a next Structure Component to be spawned, private Village function
     */
    private static StructureComponent getNextVillageStructureComponent(
        StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece, List<StructureComponent> par1List,
        Random par2Random, int par3, int par4, int par5, int par6, int par7) {
        if (par7 > 50 || Math.abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX) > 112
            || Math.abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ) > 112) {
            return null;
        }
        final StructureComponentCity var8 = StructureCityPiecesAntimonia.getNextVillageComponent(
            par0ComponentVillageStartPiece,
            par1List,
            par2Random,
            par3,
            par4,
            par5,
            par6,
            par7 + 1);

        if (var8 != null) {
            par1List.add(var8);
            par0ComponentVillageStartPiece.field_74932_i.add(var8);
            return var8;
        }

        return null;
    }

    private static StructureComponent getNextComponentVillagePath(
        StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece, List<StructureComponent> par1List,
        Random par2Random, int par3, int par4, int par5, int par6, int par7) {
        if (par7 > 3 + par0ComponentVillageStartPiece.terrainType
            || Math.abs(par3 - par0ComponentVillageStartPiece.getBoundingBox().minX) > 112 * 8
            || Math.abs(par5 - par0ComponentVillageStartPiece.getBoundingBox().minZ) > 112 * 8) {
            return null;
        }
        final StructureBoundingBox var8 = StructureComponentCityPathGen
            .func_74933_a(par0ComponentVillageStartPiece, par1List, par2Random, par3, par4, par5, par6);

        if (var8 != null && var8.minY > 10) {
            final StructureComponentCityPathGen var9 = new StructureComponentCityPathGen(
                par0ComponentVillageStartPiece,
                par7,
                par2Random,
                var8,
                par6);

            par1List.add(var9);
            par0ComponentVillageStartPiece.field_74930_j.add(var9);
            return var9;
        }

        return null;
    }

    /**
     * attempts to find a next Structure Component to be spawned
     */
    static StructureComponent getNextStructureComponent(
        StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece, List<StructureComponent> par1List,
        Random par2Random, int par3, int par4, int par5, int par6, int par7) {
        return StructureCityPiecesAntimonia.getNextVillageStructureComponent(
            par0ComponentVillageStartPiece,
            par1List,
            par2Random,
            par3,
            par4,
            par5,
            par6,
            par7);
    }

    static StructureComponent getNextStructureComponentVillagePath(
        StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece, List<StructureComponent> par1List,
        Random par2Random, int par3, int par4, int par5, int par6, int par7) {
        return StructureCityPiecesAntimonia.getNextComponentVillagePath(
            par0ComponentVillageStartPiece,
            par1List,
            par2Random,
            par3,
            par4,
            par5,
            par6,
            par7);
    }
}
