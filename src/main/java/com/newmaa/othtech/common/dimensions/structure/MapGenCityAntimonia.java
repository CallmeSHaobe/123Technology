package com.newmaa.othtech.common.dimensions.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;

import com.newmaa.othtech.common.dimensions.biome.BiomeGenBaseAntimonia;

import cpw.mods.fml.common.FMLLog;

public class MapGenCityAntimonia extends MapGenStructure {

    public static List<BiomeGenBase> villageSpawnBiomes = Arrays.asList(BiomeGenBaseAntimonia.antimonia);
    private final int terrainType;
    private static boolean initialized;

    static {
        try {
            MapGenCityAntimonia.initiateStructures();
        } catch (final Throwable e) {

        }
    }

    public static void initiateStructures() throws Throwable {
        if (!MapGenCityAntimonia.initialized) {
            MapGenStructureIO.registerStructure(StructureStartAntimoniaCity.class, "CityAntimonia");
            MapGenStructureIO.func_143031_a(StructureComponentCitySolar.class, "CitySolar");
            MapGenStructureIO.func_143031_a(StructureComponentCityBuildingBlack.class, "CityHouse2");
            MapGenStructureIO.func_143031_a(StructureComponentCityBuilding.class, "CityHouse");
            MapGenStructureIO.func_143031_a(StructureComponentCityRoadPiece.class, "CityRoadPiece");
            MapGenStructureIO.func_143031_a(StructureComponentCityPathGen.class, "CityPath");
            MapGenStructureIO.func_143031_a(StructureComponentCityLights.class, "LightsAntimonia");
            MapGenStructureIO.func_143031_a(StructureStartPiecesAntimoniaCity.class, "CenterCity");
        }

        MapGenCityAntimonia.initialized = true;
    }

    public MapGenCityAntimonia() {
        this.terrainType = 0;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int i, int j) {
        final byte numChunks = 4;
        final byte offsetChunks = 2;
        final int oldi = i;
        final int oldj = j;

        if (i < 0) {
            i -= numChunks - 1;
        }

        if (j < 0) {
            j -= numChunks - 1;
        }

        int randX = i / numChunks;
        int randZ = j / numChunks;
        final Random var7 = this.worldObj.setRandomSeed(i, j, 10387312);
        randX *= numChunks;
        randZ *= numChunks;
        randX += var7.nextInt(numChunks - offsetChunks);
        randZ += var7.nextInt(numChunks - offsetChunks);

        return oldi == randX && oldj == randZ;
    }

    @Override
    protected StructureStart getStructureStart(int par1, int par2) {
        FMLLog.info("Generating Antimonia City at x" + par1 * 16 + " z" + par2 * 16);
        return new StructureStartAntimoniaCity(this.worldObj, this.rand, par1, par2, this.terrainType);
    }

    @Override
    public String func_143025_a() {
        return "AntimoniaCity";
    }
}
