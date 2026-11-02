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
 * 迪普西科调试币（银色版）。
 *
 * <p>
 * 只当「装配线配方图」的 NEI 页签图标用，和金色那枚（凝聚态 / 机体升级）区分开；
 * 刻意**不**加附魔光效，页签上不闪。
 * </p>
 */
public class ItemDeepSeikoTokenSilver extends Item {

    public ItemDeepSeikoTokenSilver() {
        super();
        this.setUnlocalizedName("DeepSeikoTokenSilver");
        this.setTextureName("123technology:DeepSeikoTokenSilver");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        toolTip.add(EnumChatFormatting.GRAY + translateToLocal("tt.ds.token.silver"));
    }
}
