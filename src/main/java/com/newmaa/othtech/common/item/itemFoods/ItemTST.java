package com.newmaa.othtech.common.item.itemFoods;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemTST extends ItemFood {

    public ItemTST() {
        super(4096, 10F, true);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.damageBoost.id, 114514, 64, 1);
        this.setUnlocalizedName("itemTST");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:TST");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {

        toolTip.add(translateToLocal("tt.tst.0"));

    }
}
