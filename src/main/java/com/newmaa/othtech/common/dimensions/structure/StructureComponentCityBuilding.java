package com.newmaa.othtech.common.dimensions.structure;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import crazypants.enderio.EnderIO;

public class StructureComponentCityBuilding extends StructureComponentCity {

    private int averageGroundLevel = -1;

    public StructureComponentCityBuilding() {}

    public StructureComponentCityBuilding(StructureStartPiecesAntimoniaCity par1ComponentVillageStartPiece, int par2,
        Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5) {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
    }

    public static StructureComponentCityBuilding func_74921_a(
        StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece, List<StructureComponent> par1List,
        Random par2Random, int par3, int par4, int par5, int par6, int par7) {
        final StructureBoundingBox var8 = StructureBoundingBox
            .getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 17, 9, 17, par6);
        return StructureComponent.findIntersecting(par1List, var8) == null
            ? new StructureComponentCityBuilding(par0ComponentVillageStartPiece, par7, par2Random, var8, par6)
            : null;
    }

    @Override
    protected void func_143012_a(NBTTagCompound nbt) {
        super.func_143012_a(nbt);

        nbt.setInteger("AvgGroundLevel", this.averageGroundLevel);
    }

    @Override
    protected void func_143011_b(NBTTagCompound nbt) {
        super.func_143011_b(nbt);

        this.averageGroundLevel = nbt.getInteger("AvgGroundLevel");
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    @Override
    public boolean addComponentParts(World par1World, Random par2Random,
        StructureBoundingBox par3StructureBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

            if (this.averageGroundLevel < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 9 - 1, 0);
        }

        this.fillWithAir(par1World, par3StructureBoundingBox, 3, 0, 3, 13, 9, 13);
        this.fillWithAir(par1World, par3StructureBoundingBox, 5, 0, 2, 11, 9, 14);
        this.fillWithAir(par1World, par3StructureBoundingBox, 2, 0, 5, 14, 9, 11);

        for (int i = 3; i <= 13; i++) {
            for (int j = 3; j <= 13; j++) {
                this.placeBlockAtCurrentPosition(
                    par1World,
                    Block.getBlockFromName("Botania:quartzTypeLavender"),
                    0,
                    i,
                    0,
                    j,
                    par3StructureBoundingBox);
            }
        }

        for (int i = 5; i <= 11; i++) {
            for (int j = 2; j <= 14; j++) {
                this.placeBlockAtCurrentPosition(
                    par1World,
                    Block.getBlockFromName("Botania:quartzTypeLavender"),
                    0,
                    i,
                    0,
                    j,
                    par3StructureBoundingBox);
            }
        }

        for (int i = 2; i <= 14; i++) {
            for (int j = 5; j <= 11; j++) {
                this.placeBlockAtCurrentPosition(
                    par1World,
                    Block.getBlockFromName("Botania:quartzTypeLavender"),
                    0,
                    i,
                    0,
                    j,
                    par3StructureBoundingBox);
            }
        }

        int yLevel = 0;

        for (yLevel = -8; yLevel < 40; yLevel++) {
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                4,
                yLevel,
                2,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                3,
                yLevel,
                2,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                2,
                yLevel,
                3,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                2,
                yLevel,
                4,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                5,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                6,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                7,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                yLevel <= 1 ? Block.getBlockFromName("Botania:quartzTypeLavender") : EnderIO.blockFusedQuartz,
                0,
                1,
                yLevel,
                8,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                9,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                10,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                11,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                2,
                yLevel,
                12,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                2,
                yLevel,
                13,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                3,
                yLevel,
                14,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                4,
                yLevel,
                14,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                5,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                6,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                7,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                yLevel <= 1 ? Block.getBlockFromName("Botania:quartzTypeLavender") : EnderIO.blockFusedQuartz,
                0,
                8,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                9,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                10,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                11,
                yLevel,
                15,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                12,
                yLevel,
                14,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                13,
                yLevel,
                14,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                14,
                yLevel,
                13,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                14,
                yLevel,
                12,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                11,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                10,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                9,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                yLevel <= 1 ? Block.getBlockFromName("Botania:quartzTypeLavender") : EnderIO.blockFusedQuartz,
                0,
                15,
                yLevel,
                8,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                7,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                6,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                5,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                14,
                yLevel,
                4,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                14,
                yLevel,
                3,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                13,
                yLevel,
                2,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                12,
                yLevel,
                2,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                11,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                10,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                9,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                yLevel <= 1 ? Block.getBlockFromName("Botania:quartzTypeLavender") : EnderIO.blockFusedQuartz,
                0,
                8,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                7,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                6,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                5,
                yLevel,
                1,
                par3StructureBoundingBox);
        }
        for (yLevel = -8 + 40; yLevel < 40 + 40; yLevel++) {
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                4,
                yLevel,
                2,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                3,
                yLevel,
                2,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                2,
                yLevel,
                3,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                2,
                yLevel,
                4,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                5,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                6,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                7,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                yLevel <= 1 ? Block.getBlockFromName("Botania:quartzTypeLavender") : EnderIO.blockFusedQuartz,
                0,
                1,
                yLevel,
                8,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                9,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                10,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                1,
                yLevel,
                11,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                2,
                yLevel,
                12,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                2,
                yLevel,
                13,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                3,
                yLevel,
                14,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                4,
                yLevel,
                14,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                5,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                6,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                7,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                yLevel <= 1 ? Block.getBlockFromName("Botania:quartzTypeLavender") : EnderIO.blockFusedQuartz,
                0,
                8,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                9,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                10,
                yLevel,
                15,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                11,
                yLevel,
                15,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                12,
                yLevel,
                14,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                13,
                yLevel,
                14,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                14,
                yLevel,
                13,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                14,
                yLevel,
                12,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                11,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                10,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                9,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                yLevel <= 1 ? Block.getBlockFromName("Botania:quartzTypeLavender") : EnderIO.blockFusedQuartz,
                0,
                15,
                yLevel,
                8,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                7,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                6,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                15,
                yLevel,
                5,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                14,
                yLevel,
                4,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                14,
                yLevel,
                3,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                13,
                yLevel,
                2,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                12,
                yLevel,
                2,
                par3StructureBoundingBox);

            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                11,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                10,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                9,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                yLevel <= 1 ? Block.getBlockFromName("Botania:quartzTypeLavender") : EnderIO.blockFusedQuartz,
                0,
                8,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                7,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                6,
                yLevel,
                1,
                par3StructureBoundingBox);
            this.placeBlockAtCurrentPosition(
                par1World,
                Block.getBlockFromName("Botania:quartzTypeLavender"),
                0,
                5,
                yLevel,
                1,
                par3StructureBoundingBox);

        }
        for (int i = 3; i <= 13; i++) {
            for (int j = 3; j <= 13; j++) {
                this.placeBlockAtCurrentPosition(
                    par1World,
                    Block.getBlockFromName("Botania:quartzTypeLavender"),
                    0,
                    i,
                    80,
                    j,
                    par3StructureBoundingBox);
            }
        }

        for (int i = 5; i <= 11; i++) {
            for (int j = 2; j <= 14; j++) {
                this.placeBlockAtCurrentPosition(
                    par1World,
                    Block.getBlockFromName("Botania:quartzTypeLavender"),
                    0,
                    i,
                    80,
                    j,
                    par3StructureBoundingBox);
            }
        }

        for (int i = 2; i <= 14; i++) {
            for (int j = 5; j <= 11; j++) {
                this.placeBlockAtCurrentPosition(
                    par1World,
                    Block.getBlockFromName("Botania:quartzTypeLavender"),
                    0,
                    i,
                    80,
                    j,
                    par3StructureBoundingBox);
            }
        }
        this.spawnVillagers(par1World, par3StructureBoundingBox, 6, 5, 6, 4);
        return true;
    }
}
