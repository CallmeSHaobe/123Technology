package com.newmaa.othtech.common.item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class itemEnqing extends Item {
    public itemEnqing() {
        super();

        this.setUnlocalizedName("itemEnqing");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:itemEnqing");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
                               final boolean advancedToolTips) {

        toolTip.add("§e§l眼里有光....");
        toolTip.add("§e§l为§4§l恩情工厂§e§l提供激光源支持");

    }
}
