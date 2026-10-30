package com.newmaa.othtech.common.item.itemMisc;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * 时空冷却核心：迪普西科「装配线」模式的产物，整台机器的冷却系统最终压进这一颗核心里。
 *
 * <p>
 * 配方在 {@code Recipemaps.DeepSeikoAL}（装配线模式）：10 种冰/冷却材料 + 4 种冷却流体，
 * 2,000,000,000 EU/t。NEI 里能看到完整清单。
 * </p>
 */
public class ItemSpacetimeCoolingCore extends Item {

    public ItemSpacetimeCoolingCore() {
        super();
        this.setUnlocalizedName("SpacetimeCoolingCore");
        this.setTextureName("123technology:SpacetimeCoolingCore");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setMaxStackSize(16);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add(EnumChatFormatting.AQUA + translateToLocal("tt.ds.core.0"));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack, int pass) {
        return true;
    }
}
