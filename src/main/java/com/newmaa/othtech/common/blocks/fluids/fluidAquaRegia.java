package com.newmaa.othtech.common.blocks.fluids;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class fluidAquaRegia extends BlockFluidClassic {

    @SideOnly(Side.CLIENT)
    protected IIcon stillIcon;
    @SideOnly(Side.CLIENT)
    protected IIcon flowingIcon;

    public fluidAquaRegia(Fluid fluid, Material material) {
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
        this.stillIcon = register.registerIcon("123technology:antimonia/stillAqua");
        this.flowingIcon = register.registerIcon("123technology:antimonia/flowingAqua");
    }

    public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
        return !world.getBlock(x, y, z)
            .getMaterial()
            .isLiquid() && super.canDisplace(world, x, y, z);
    }

    public boolean displaceIfPossible(World world, int x, int y, int z) {
        return !world.getBlock(x, y, z)
            .getMaterial()
            .isLiquid() && super.displaceIfPossible(world, x, y, z);
    }

    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        {
            entityIn.attackEntityFrom(new DamageSource("Acid"), 400.0F);
        }
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }
}
