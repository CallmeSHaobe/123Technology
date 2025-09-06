package com.newmaa.othtech.common.dimensions.structure.ross123b;

import java.util.Random;

import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureStart;

import cpw.mods.fml.common.FMLLog;

public class MapGenBuildingRoss123b extends MapGenStructure {

    private static boolean initialized;

    public MapGenBuildingRoss123b() {
        super();
    }

    @Override
    public String func_143025_a() {
        return "Ross123Buildings";
    }

    static {
        try {
            MapGenBuildingRoss123b.initiateStructures();
        } catch (final Throwable e) {

        }
    }

    public static void initiateStructures() throws Throwable {
        if (!MapGenBuildingRoss123b.initialized) {
            MapGenStructureIO.registerStructure(StructureStartBuildingRoss123b.class, "BuildingRoss");
            MapGenStructureIO.func_143031_a(StructureComponentBuildingRoss123b.class, "_BuildingRoss");
            MapGenStructureIO.func_143031_a(StructureComponentRoadRoss123b.class, "RoadRoss");
        }

        MapGenBuildingRoss123b.initialized = true;
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ) {
        int spacingChunks = 3;
        if ((chunkX % spacingChunks == 0) && (chunkZ % spacingChunks == 0)) {
            Random rand = new Random(worldObj.getSeed() + (chunkX * 341873128712L) + (chunkZ * 132897987541L));
            return rand.nextFloat() < 0.8F;
        }
        return false;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ) {
        FMLLog.info("Generating Ross123b City at x" + chunkX * 16 + " z" + chunkZ * 16);
        return new StructureStartBuildingRoss123b(this.worldObj, this.rand, chunkX, chunkZ);
    }

}
