package com.newmaa.othtech.common.blocks.oterender;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRocketRender extends Block {

    public BlockRocketRender() {
        super(Material.iron);
        this.setHardness(-1.0f);
        this.setResistance(114514.0F);
        this.setBlockName("rockerrender");
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean canRenderInPass(int a) {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new OTERocketRender();
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        return new ArrayList<>();
    }
    /*
     * @Override
     * public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack) {
     * int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
     * if (l == 0) {
     * world.setBlockMetadataWithNotify(x, y, z, 2, 2);
     * }
     * if (l == 1) {
     * world.setBlockMetadataWithNotify(x, y, z, 5, 2);
     * }
     * if (l == 2) {
     * world.setBlockMetadataWithNotify(x, y, z, 3, 2);
     * }
     * if (l == 3) {
     * world.setBlockMetadataWithNotify(x, y, z, 4, 2);
     * }
     * }
     */
}
