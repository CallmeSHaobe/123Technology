package com.newmaa.othtech.common.blocks.fluids;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FluidLCL extends BlockFluidClassic {

    @SideOnly(Side.CLIENT)
    protected IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    protected IIcon flowingIcon;

    public FluidLCL(Fluid fluid, Material material) {
        super(fluid, material);
    }

    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTabToDisplayOn() {
        return null;
    }

    public IIcon getIcon(int side, int meta) {
        return side != 0 && side != 1 ? this.flowingIcon : this.stillIcon;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.stillIcon = register.registerIcon("123technology:antimonia/stillLCL");
        this.flowingIcon = register.registerIcon("123technology:antimonia/flowingLCL");
    }

    @Override
    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
        return !world.getBlock(x, y, z)
            .getMaterial()
            .isLiquid() && super.canDisplace(world, x, y, z);
    }

    @Override
    public boolean displaceIfPossible(World world, int x, int y, int z) {
        return !world.getBlock(x, y, z)
            .getMaterial()
            .isLiquid() && super.displaceIfPossible(world, x, y, z);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        super.onEntityCollidedWithBlock(worldIn, x, y, z, entityIn);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        return;
    }

    @Override
    protected boolean canFlowInto(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    protected void flowIntoBlock(World world, int x, int y, int z, int meta) {
        return;
    }
}
