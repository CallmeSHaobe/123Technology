package com.newmaa.othtech.common.item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class boardCasimir extends Item {
    public boardCasimir() {
        super();

        this.setUnlocalizedName("boardCasimir");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:boardCasimir");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
                               final boolean advancedToolTips) {

        toolTip.add("§a一块能够植入卡西米尔鳍片的板材，几乎不会与外界发生任何交互，时空的终点？");
    }
}
