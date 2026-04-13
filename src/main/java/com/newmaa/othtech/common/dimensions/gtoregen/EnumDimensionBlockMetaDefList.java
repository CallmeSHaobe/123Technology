package com.newmaa.othtech.common.dimensions.gtoregen;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;

import org.jetbrains.annotations.NotNull;

import com.newmaa.othtech.common.blocks.antimonia.AntimoniaBlocks;

import galacticgreg.api.ModDBMDef;

public enum EnumDimensionBlockMetaDefList {

    Antimonia(new ModDBMDef(AntimoniaBlocks.antimoniaBlockStone)),
    Ross123b(new ModDBMDef(Blocks.quartz_block));

    public final @NotNull List<ModDBMDef> DBMDefList;

    EnumDimensionBlockMetaDefList(ModDBMDef... DBMDefArray) {
        DBMDefList = Arrays.asList(DBMDefArray);
    }
}
