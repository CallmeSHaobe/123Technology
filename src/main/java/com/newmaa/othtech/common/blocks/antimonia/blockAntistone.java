package com.newmaa.othtech.common.blocks.antimonia;

import static com.newmaa.othtech.common.creativetab.CreativeTabsLoader.tabothtech;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class blockAntistone extends Block {

    public blockAntistone() {
        super(Material.rock);
        this.setBlockName("AntimoniaStone");
        this.setHardness(300.0F);
        this.setStepSound(Block.soundTypeStone);
        this.setHarvestLevel("pickaxe", 5);
        this.setBlockTextureName("123technology:antimonia/blockAntistone");
    }

    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return tabothtech;
    }

    public boolean isTerraformable(World world, int x, int y, int z) {
        return true;
    }
}
