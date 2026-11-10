package com.newmaa.othtech.common.item.itemFoods;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemYellowPeach extends ItemFood {

    /** 饥饿值回复量 */
    public static final int HUNGER = 12;
    /** 饱食度系数 */
    public static final float SATURATION = 0.6F;
    /** 速度 V：amplifier 4 = 五级 */
    public static final int SPEED_AMPLIFIER = 4;
    /** 30 秒 = 600 tick */
    public static final int EFFECT_DURATION = 30 * 20;

    public ItemYellowPeach() {
        super(HUNGER, SATURATION, false);
        this.setAlwaysEdible();
        this.setPotionEffect(Potion.moveSpeed.id, EFFECT_DURATION, SPEED_AMPLIFIER, 1.0F);
        this.setUnlocalizedName("YellowPeach");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setTextureName("123technology:YellowPeach");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List toolTip,
        final boolean advancedToolTips) {
        toolTip.add(EnumChatFormatting.YELLOW + translateToLocal("tt.yellowPeach"));
        toolTip.add(EnumChatFormatting.AQUA + translateToLocal("tt.yellowPeach.effect"));
    }
}
