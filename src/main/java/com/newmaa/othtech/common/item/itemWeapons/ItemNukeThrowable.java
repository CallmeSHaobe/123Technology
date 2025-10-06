package com.newmaa.othtech.common.item.itemWeapons;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.entity.EntityNukeThrowable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNukeThrowable extends Item {

    public ItemNukeThrowable() {
        super();

        this.setUnlocalizedName("itemNukeThrowable");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:itemNukeThrowable");

    }

    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        if (!playerIn.capabilities.isCreativeMode) {
            --itemStackIn.stackSize;
        }
        if (!worldIn.isRemote) {
            worldIn.spawnEntityInWorld(new EntityNukeThrowable(worldIn, playerIn));
        }
        return itemStackIn;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add(translateToLocal("tt.690.0"));
        toolTip.add(translateToLocal("tt.690.1"));

    }

}
