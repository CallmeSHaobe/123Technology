package com.newmaa.othtech.common.dimensions.structure.antimonia;

import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static net.minecraft.init.Blocks.air;
import static net.minecraft.init.Blocks.quartz_block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import com.newmaa.othtech.common.blocks.fluids.AntimoniaFluids;

public class StructureComponentCityCenter extends StructureComponentCity {

    private int averageGroundLevel = -1;

    public StructureComponentCityCenter() {}

    public StructureComponentCityCenter(StructureStartPiecesAntimoniaCity par1ComponentVillageStartPiece, int par2,
        Random par3Random, int par4, int par5) {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par3Random.nextInt(4);

        switch (this.coordBaseMode) {
            case 0:
            case 2:
                this.boundingBox = new StructureBoundingBox(par4, 64, par5, par4 + 6 - 1, 78, par5 + 6 - 1);
                break;
            default:
                this.boundingBox = new StructureBoundingBox(par4, 64, par5, par4 + 6 - 1, 78, par5 + 6 - 1);
        }
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

    @Override
    public void buildComponent(StructureComponent par1StructureComponent, List<StructureComponent> par2List,
        Random par3Random) {
        StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
            (StructureStartPiecesAntimoniaCity) par1StructureComponent,
            par2List,
            par3Random,
            this.boundingBox.minX - 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ + 1,
            1,
            this.getComponentType());
        StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
            (StructureStartPiecesAntimoniaCity) par1StructureComponent,
            par2List,
            par3Random,
            this.boundingBox.maxX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ + 1,
            3,
            this.getComponentType());
        StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
            (StructureStartPiecesAntimoniaCity) par1StructureComponent,
            par2List,
            par3Random,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.minZ - 1,
            2,
            this.getComponentType());
        StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
            (StructureStartPiecesAntimoniaCity) par1StructureComponent,
            par2List,
            par3Random,
            this.boundingBox.minX + 1,
            this.boundingBox.maxY - 4,
            this.boundingBox.maxZ + 1,
            0,
            this.getComponentType());
    }

    @Override
    public boolean addComponentParts(World par1World, Random par2Random,
        StructureBoundingBox par3StructureBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

            if (this.averageGroundLevel < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 3, 0);
        }

        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            0,
            1,
            4,
            12,
            4,
            sBlockCasings8,
            0,
            AntimoniaFluids.BlockAquaRegia,
            0,
            false);
        this.placeBlockAtCurrentPosition(par1World, air, 0, 2, 12, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, air, 0, 3, 12, 2, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, air, 0, 2, 12, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, air, 0, 3, 12, 3, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            13,
            1,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            14,
            1,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            4,
            13,
            1,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            4,
            14,
            1,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            13,
            4,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            14,
            4,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            4,
            13,
            4,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            4,
            14,
            4,
            par3StructureBoundingBox);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            15,
            1,
            4,
            15,
            4,
            sBlockCasings8,
            0,
            sBlockCasings8,
            0,
            false);

        for (int var4 = 0; var4 <= 5; ++var4) {
            for (int var5 = 0; var5 <= 5; ++var5) {
                if (var5 == 0 || var5 == 5 || var4 == 0 || var4 == 5) {
                    this.placeBlockAtCurrentPosition(
                        par1World,
                        quartz_block,
                        11,
                        var5,
                        11,
                        var4,
                        par3StructureBoundingBox);
                    this.clearCurrentPositionBlocksUpwards(par1World, var5, 12, var4, par3StructureBoundingBox);
                }
            }
        }

        return true;
    }
}
