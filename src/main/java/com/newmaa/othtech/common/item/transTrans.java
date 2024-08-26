package com.newmaa.othtech.common.item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class transTrans extends Item {
    public transTrans() {
        super();

        this.setUnlocalizedName("transTrans");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:transTrans");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
                               final boolean advancedToolTips) {

        toolTip.add("§a突破任意封装空间内距离与时间的限制，并使得超维度通信变得可能");
}
}
