package com.newmaa.othtech.common.item.weapons;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.common.entity.entityNukeThrowable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemNukeThrowable extends Item {

    public itemNukeThrowable() {
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
            worldIn.spawnEntityInWorld(new entityNukeThrowable(worldIn, playerIn));
        }
        return itemStackIn;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("§z§l一发摧毁一个航母战斗群");
        toolTip.add("§4警告：投掷者后果自负 将造成大范围爆炸");

    }

}
