package com.newmaa.othtech.common.blocks.antimonia;

import static com.newmaa.othtech.common.creativetab.CreativeTabsLoader.tabothtech;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAntimoniaGrass extends Block {

    public BlockAntimoniaGrass() {
        super(Material.rock);
        this.setBlockName("AntimoniaGrass");
        this.setHardness(100.0F);
        this.setStepSound(Block.soundTypeGravel);
        this.setHarvestLevel("shovel", 5);
        this.setBlockTextureName("123technology:antimonia/blockAntimorass");
    }

    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return tabothtech;
    }

    public boolean isTerraformable(World world, int x, int y, int z) {
        return true;
    }
}
