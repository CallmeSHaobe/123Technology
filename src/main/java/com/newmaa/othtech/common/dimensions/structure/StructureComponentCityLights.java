package com.newmaa.othtech.common.dimensions.structure;

import static gregtech.api.enums.Mods.IndustrialCraft2;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class StructureComponentCityLights extends StructureComponentCity {

    private int averageGroundLevel = -1;

    public StructureComponentCityLights() {}

    public StructureComponentCityLights(StructureStartPiecesAntimoniaCity par1ComponentVillageStartPiece, int par2,
        Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5) {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
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

    public static StructureBoundingBox func_74904_a(StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece,
        List<StructureComponent> par1List, Random par2Random, int par3, int par4, int par5, int par6) {
        final StructureBoundingBox var7 = StructureBoundingBox
            .getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 3, 4, 2, par6);
        return StructureComponent.findIntersecting(par1List, var7) != null ? null : var7;
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

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4 - 1, 0);
        }

        this.fillWithBlocks(par1World, par3StructureBoundingBox, 0, 0, 0, 2, 3, 1, Blocks.air, Blocks.air, false);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            0,
            0,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            1,
            0,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            2,
            0,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            3,
            0,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(
            par1World,
            Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron"),
            0,
            1,
            4,
            0,
            par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.stone_slab, 0, 1, 5, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.stone_slab, 0, 0, 5, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.stone_slab, 0, 1, 5, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.stone_slab, 0, 2, 5, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.stone_slab, 0, 1, 5, -1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.glowstone, 0, 0, 4, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.glowstone, 0, 1, 4, 1, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.glowstone, 0, 2, 4, 0, par3StructureBoundingBox);
        this.placeBlockAtCurrentPosition(par1World, Blocks.glowstone, 0, 1, 4, -1, par3StructureBoundingBox);
        return true;
    }
}
