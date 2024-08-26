package com.newmaa.othtech.common.item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class boardTrans extends Item {
    public boardTrans() {
        super();

        this.setUnlocalizedName("boardTrans");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:boardTrans");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
                               final boolean advancedToolTips) {

        toolTip.add("§a一块容纳过去、当下、未来，包含无穷多可能得微缩空间");

    }
}
