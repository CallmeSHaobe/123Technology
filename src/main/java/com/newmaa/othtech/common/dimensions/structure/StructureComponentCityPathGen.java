package com.newmaa.othtech.common.dimensions.structure;

import static gregtech.api.GregTechAPI.sBlockConcretes;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;

public class StructureComponentCityPathGen extends StructureComponentCityRoadPiece {

    private int averageGroundLevel;

    public StructureComponentCityPathGen() {}

    public StructureComponentCityPathGen(StructureStartPiecesAntimoniaCity par1ComponentVillageStartPiece, int par2,
        Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5) {
        super(par1ComponentVillageStartPiece, par2);
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
        this.averageGroundLevel = Math.max(par4StructureBoundingBox.getXSize(), par4StructureBoundingBox.getZSize());
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
        boolean var4 = false;
        int var5;
        StructureComponent var6;

        for (var5 = par3Random.nextInt(5); var5 < this.averageGroundLevel - 8; var5 += 2 + par3Random.nextInt(5)) {
            var6 = this.getNextComponentNN(
                (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                par2List,
                par3Random,
                0,
                var5);

            if (var6 != null) {
                var5 += Math.max(
                    var6.getBoundingBox()
                        .getXSize(),
                    var6.getBoundingBox()
                        .getZSize());
                var4 = true;
            }
        }

        for (var5 = par3Random.nextInt(5); var5 < this.averageGroundLevel - 8; var5 += 2 + par3Random.nextInt(5)) {
            var6 = this.getNextComponentPP(
                (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                par2List,
                par3Random,
                0,
                var5);

            if (var6 != null) {
                var5 += Math.max(
                    var6.getBoundingBox()
                        .getXSize(),
                    var6.getBoundingBox()
                        .getZSize());
                var4 = true;
            }
        }

        if (var4 && par3Random.nextInt(3) > 0) {
            switch (this.coordBaseMode) {
                case 0:
                    StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
                        (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                        par2List,
                        par3Random,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY,
                        this.boundingBox.maxZ - 2,
                        1,
                        this.getComponentType());
                    break;
                case 1:
                    StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
                        (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                        par2List,
                        par3Random,
                        this.boundingBox.minX,
                        this.boundingBox.minY,
                        this.boundingBox.minZ - 1,
                        2,
                        this.getComponentType());
                    break;
                case 2:
                    StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
                        (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                        par2List,
                        par3Random,
                        this.boundingBox.minX - 1,
                        this.boundingBox.minY,
                        this.boundingBox.minZ,
                        1,
                        this.getComponentType());
                    break;
                case 3:
                    StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
                        (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                        par2List,
                        par3Random,
                        this.boundingBox.maxX - 2,
                        this.boundingBox.minY,
                        this.boundingBox.minZ - 1,
                        2,
                        this.getComponentType());
            }
        }

        if (var4 && par3Random.nextInt(3) > 0) {
            switch (this.coordBaseMode) {
                case 0:
                    StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
                        (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                        par2List,
                        par3Random,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY,
                        this.boundingBox.maxZ - 2,
                        3,
                        this.getComponentType());
                    break;
                case 1:
                    StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
                        (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                        par2List,
                        par3Random,
                        this.boundingBox.minX,
                        this.boundingBox.minY,
                        this.boundingBox.maxZ + 1,
                        0,
                        this.getComponentType());
                    break;
                case 2:
                    StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
                        (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                        par2List,
                        par3Random,
                        this.boundingBox.maxX + 1,
                        this.boundingBox.minY,
                        this.boundingBox.minZ,
                        3,
                        this.getComponentType());
                    break;
                case 3:
                    StructureCityPiecesAntimonia.getNextStructureComponentVillagePath(
                        (StructureStartPiecesAntimoniaCity) par1StructureComponent,
                        par2List,
                        par3Random,
                        this.boundingBox.maxX - 2,
                        this.boundingBox.minY,
                        this.boundingBox.maxZ + 1,
                        0,
                        this.getComponentType());
            }
        }
    }

    public static StructureBoundingBox func_74933_a(StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece,
        List<StructureComponent> par1List, Random par2Random, int par3, int par4, int par5, int par6) {
        for (int var7 = 7 * MathHelper.getRandomIntegerInRange(par2Random, 3, 5); var7 >= 7; var7 -= 7) {
            final StructureBoundingBox var8 = StructureBoundingBox
                .getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 5, 3, var7, par6);

            if (StructureComponent.findIntersecting(par1List, var8) == null) {
                return var8;
            }
        }

        return null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    @Override
    public boolean addComponentParts(World par1World, Random par2Random,
        StructureBoundingBox par3StructureBoundingBox) {
        final Block var4 = sBlockConcretes;

        for (int var5 = this.boundingBox.minX; var5 <= this.boundingBox.maxX; ++var5) {
            for (int var6 = this.boundingBox.minZ; var6 <= this.boundingBox.maxZ; ++var6) {
                if (par3StructureBoundingBox.isVecInside(var5, 64, var6)
                    && (par1World.getBlock(var5, par1World.getTopSolidOrLiquidBlock(var5, var6) - 1, var6)
                        == AntimoniaBlocks.antimoniaBlockGrass
                        && par1World.getBlockMetadata(var5, par1World.getTopSolidOrLiquidBlock(var5, var6) - 1, var6)
                            == 0
                        || Blocks.air
                            == par1World.getBlock(var5, par1World.getTopSolidOrLiquidBlock(var5, var6) - 1, var6))) {
                    final int var7 = par1World.getTopSolidOrLiquidBlock(var5, var6) - 1;
                    par1World.setBlock(var5, var7, var6, var4, 15, 3);
                }
            }
        }

        return true;
    }
}
