package com.newmaa.othtech.common.dimensions.structure.ross123b;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureStart;

public class StructureStartBuildingRoss123b extends StructureStart {

    public StructureStartBuildingRoss123b() {}

    public StructureStartBuildingRoss123b(World world, Random rand, int chunkX, int chunkZ) {
        super(chunkX, chunkZ);
        buildStructure(world, rand, chunkX, chunkZ);
    }

    private void buildStructure(World world, Random rand, int chunkX, int chunkZ) {
        int baseX = chunkX * 16;
        int baseZ = chunkZ * 16;
        int buildingWidth = 24;
        int buildingLength = 24;
        int buildingHeight = 200;
        int roadWidth = 5;
        int groundY = 1;

        StructureComponentBuildingRoss123b building = new StructureComponentBuildingRoss123b(
            baseX,
            groundY,
            baseZ,
            buildingWidth,
            buildingLength,
            buildingHeight);
        this.components.add(building);
        /*
         * StructureComponentRoadRoss123b road = new StructureComponentRoadRoss123b(baseX, 20, baseZ, buildingWidth,
         * buildingLength, roadWidth);
         * this.components.add(road);
         */

        this.updateBoundingBox();
    }
}
