package com.newmaa.othtech.common.item;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class finCasimir extends Item {
    public finCasimir() {
        super();

        this.setUnlocalizedName("finCasimir");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:finCasimir");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
                               final boolean advancedToolTips) {

        toolTip.add("§a一对对细小达到时空最基本单位的鳍片，能从真空量子海洋中汲取源源不断的能量。*留意真空衰变！");
    }
}
