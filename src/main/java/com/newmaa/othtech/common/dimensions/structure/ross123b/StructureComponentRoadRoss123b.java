package com.newmaa.othtech.common.dimensions.structure.ross123b;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class StructureComponentRoadRoss123b extends StructureComponent {

    private int buildingWidth, buildingLength, roadWidth;

    @Override
    protected void func_143012_a(NBTTagCompound p_143012_1_) {

    }

    @Override
    protected void func_143011_b(NBTTagCompound p_143011_1_) {

    }

    public StructureComponentRoadRoss123b(int x, int y, int z, int buildingWidth, int buildingLength, int roadWidth) {
        this.buildingWidth = buildingWidth;
        this.buildingLength = buildingLength;
        this.roadWidth = roadWidth;
        this.boundingBox = new StructureBoundingBox(
            x - roadWidth,
            y,
            z - roadWidth,
            x + buildingWidth + roadWidth,
            y + 1,
            z + buildingLength + roadWidth);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox box) {
        for (int x = box.minX; x < box.maxX; x++) {
            for (int z = box.minZ; z < box.maxZ; z++) {
                int y = box.minY;
                world.setBlock(x, y, z, Blocks.stone);
            }
        }
        return true;
    }
}
