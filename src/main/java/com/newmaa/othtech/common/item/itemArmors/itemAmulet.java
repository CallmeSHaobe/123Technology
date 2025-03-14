package com.newmaa.othtech.common.item.itemArmors;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class itemAmulet extends Item {

    public itemAmulet() {
        setMaxStackSize(1);
        setMaxDamage(1);
        setCreativeTab(CreativeTabsLoader.tabothtech);
        setUnlocalizedName("itemAmulet");
        setTextureName("123technology:itemAmulet");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add("佩戴在头盔位， 炸死老登");

    }

    @Override
    public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) {
        return armorType == 0; // 0对应头盔槽位
    }

}
