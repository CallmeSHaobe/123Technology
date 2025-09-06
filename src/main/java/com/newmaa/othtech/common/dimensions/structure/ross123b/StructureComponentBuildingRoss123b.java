package com.newmaa.othtech.common.dimensions.structure.ross123b;

import static net.minecraft.init.Blocks.stained_glass;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class StructureComponentBuildingRoss123b extends StructureComponent {

    private int width, length, height;

    public StructureComponentBuildingRoss123b() {}

    @Override
    protected void func_143012_a(NBTTagCompound p_143012_1_) {}

    @Override
    protected void func_143011_b(NBTTagCompound p_143011_1_) {}

    public StructureComponentBuildingRoss123b(int x, int y, int z, int width, int length, int height) {
        this.width = width;
        this.length = length;
        this.height = height;
        this.boundingBox = new StructureBoundingBox(x, y, z, x + width, y + height, z + length);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox box) {
        int floorHeight = 4; // 每层楼层高

        for (int x = boundingBox.minX; x < boundingBox.maxX; x++) {
            for (int y = boundingBox.minY; y < boundingBox.maxY; y++) {
                for (int z = boundingBox.minZ; z < boundingBox.maxZ; z++) {

                    boolean isWallX = (x == boundingBox.minX || x == boundingBox.maxX - 1);
                    boolean isWallZ = (z == boundingBox.minZ || z == boundingBox.maxZ - 1);
                    boolean isWall = isWallX || isWallZ;

                    // 楼层相对高度
                    int layerY = (y - boundingBox.minY) % floorHeight;

                    if (isWall) {
                        // 窗户：每层的第 1~3 格高度
                        if (layerY >= 1 && layerY <= 3) {
                            if (isWallX) {
                                int dz = (z - boundingBox.minZ) % 5;
                                if (dz == 0 || dz == 1) {
                                    world.setBlock(x, y, z, stained_glass, 0, 2);
                                    continue;
                                }
                            }
                            if (isWallZ) {
                                int dx = (x - boundingBox.minX) % 5;
                                if (dx == 0 || dx == 1) {
                                    world.setBlock(x, y, z, stained_glass, 0, 2);
                                    continue;
                                }
                            }
                        }
                        // 默认墙体：随机石砖
                        int type = getRandomBrickType(rand);
                        world.setBlock(x, y, z, Blocks.stonebrick, type, 2);
                    } else {
                        // 楼层地板
                        if (layerY == 0) {
                            world.setBlock(x, y, z, Blocks.stone_slab, 0, 2);
                        } else {
                            // 楼层空间
                            world.setBlock(x, y, z, Blocks.air, 0, 2);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 随机返回石砖类型元数据
     * 0 = 普通石砖
     * 1 = 苔石砖
     * 2 = 碎裂石砖
     * 3 = 雕纹石砖
     */
    private int getRandomBrickType(Random rand) {
        int roll = rand.nextInt(100);
        if (roll < 70) return 0; // 普通
        if (roll < 85) return 1; // 苔石
        if (roll < 95) return 2; // 碎裂
        return 3; // 雕纹
    }
}
