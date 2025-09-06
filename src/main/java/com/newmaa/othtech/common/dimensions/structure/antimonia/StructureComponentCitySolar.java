package com.newmaa.othtech.common.dimensions.structure.antimonia;

import static advsolar.common.AdvancedSolarPanel.blockAdvSolarPanel;
import static com.dreammaster.block.BlockList.TungstensteelPlatedReinforcedStone;
import static micdoodle8.mods.galacticraft.core.blocks.GCBlocks.sealableBlock;
import static net.minecraft.init.Blocks.*;

import java.util.List;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class StructureComponentCitySolar extends StructureComponentCity {

    private int averageGroundLevel = -1;

    public StructureComponentCitySolar() {}

    public StructureComponentCitySolar(StructureStartPiecesAntimoniaCity par1ComponentVillageStartPiece, int par2,
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

    public static StructureComponentCitySolar func_74900_a(
        StructureStartPiecesAntimoniaCity par0ComponentVillageStartPiece, List<StructureComponent> par1List,
        Random par2Random, int par3, int par4, int par5, int par6, int par7) {
        final StructureBoundingBox var8 = StructureBoundingBox
            .getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 13, 4, 9, par6);
        return StructureComponent.findIntersecting(par1List, var8) == null
            ? new StructureComponentCitySolar(par0ComponentVillageStartPiece, par7, par2Random, var8, par6)
            : null;
    }

    @Override
    public boolean addComponentParts(World par1World, Random par2Random,
        StructureBoundingBox par3StructureBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(par1World, par3StructureBoundingBox);

            if (this.averageGroundLevel < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 7 - 1, 0);
        }

        this.fillWithMetadataBlocks(par1World, par3StructureBoundingBox, 0, 1, 0, 12, 4, 8, air, 0, air, 0, false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            0,
            1,
            2,
            0,
            7,
            blockAdvSolarPanel,
            3,
            blockAdvSolarPanel,
            3,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            4,
            0,
            1,
            5,
            0,
            7,
            blockAdvSolarPanel,
            3,
            blockAdvSolarPanel,
            3,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            7,
            0,
            1,
            8,
            0,
            7,
            blockAdvSolarPanel,
            3,
            blockAdvSolarPanel,
            3,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            10,
            0,
            1,
            11,
            0,
            7,
            blockAdvSolarPanel,
            3,
            blockAdvSolarPanel,
            3,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            0,
            0,
            0,
            0,
            0,
            8,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            6,
            0,
            0,
            6,
            0,
            8,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            12,
            0,
            0,
            12,
            0,
            8,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            0,
            0,
            11,
            0,
            0,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            1,
            0,
            8,
            11,
            0,
            8,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            TungstensteelPlatedReinforcedStone.getBlock(),
            0,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            3,
            0,
            1,
            3,
            0,
            7,
            sealableBlock,
            5,
            sealableBlock,
            5,
            false);
        this.fillWithMetadataBlocks(
            par1World,
            par3StructureBoundingBox,
            9,
            0,
            1,
            9,
            0,
            7,
            sealableBlock,
            5,
            sealableBlock,
            5,
            false);
        return true;
    }
}
