package com.newmaa.othtech.common.blocks.antimonia;

import static com.newmaa.othtech.common.creativetab.CreativeTabsLoader.tabothtech;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockAntimoniaSand extends Block {

    public BlockAntimoniaSand() {
        super(Material.sand);
        setBlockName("AntimoniaSand");
        setCreativeTab(tabothtech);
        setHardness(0.5F);
        setStepSound(Block.soundTypeSand);
        setHarvestLevel("shovel", 0);
        setBlockTextureName("123technology:antimonia/blockAntisand");
    }
}
