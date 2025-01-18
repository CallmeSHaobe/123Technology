package com.newmaa.othtech.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.newmaa.othtech.common.IRegistry;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.item.itemBlock.itemModelAyanami;

import cpw.mods.fml.common.registry.GameRegistry;

public class modelAyanami extends BaseBlockContainer implements IRegistry<modelAyanami> {

    public modelAyanami() {
        super(Material.iron);
        this.setHardness(2.0f);
        this.setResistance(10.0F);

        this.setBlockName("modelAyanami");
    }

    @Override
    public modelAyanami register() {
        GameRegistry.registerBlock(this, itemModelAyanami.class, "modelAyanami");
        GameRegistry.registerTileEntity(tileEntityModelAyanami.class, "modelAyanami");
        setCreativeTab(CreativeTabsLoader.tabothtech);
        return this;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new tileEntityModelAyanami();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack itemstack) {
        int l = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

        if (l == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
        }

        if (l == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5, 2);
        }

        if (l == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
        }

        if (l == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
        }
    }
}
