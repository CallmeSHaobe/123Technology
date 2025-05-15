package com.newmaa.othtech.common.blocks.antimonia;

import static com.newmaa.othtech.common.creativetab.CreativeTabsLoader.tabothtech;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAntimoniaDirt extends Block {

    public BlockAntimoniaDirt() {
        super(Material.rock);
        this.setBlockName("AntimoniaDirt");
        this.setHardness(3.0F);
        this.setStepSound(Block.soundTypeGrass);
        this.setHarvestLevel("shovel", 5);
        this.setBlockTextureName("123technology:antimonia/blockAntirt");
    }

    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return tabothtech;
    }

    public boolean isTerraformable(World world, int x, int y, int z) {
        return true;
    }
}
