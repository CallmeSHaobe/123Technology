package com.newmaa.othtech.common.item.itemArmor;

import java.util.List;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemHelmTallHatLaodeng extends ItemArmor {

    @SideOnly(Side.CLIENT)
    private IIcon helmIcon;

    public ItemHelmTallHatLaodeng(ArmorMaterial material, int armorType, String name) {
        super(material, 0, 0);
        this.setUnlocalizedName("TallHatLaodeng");
        this.setCreativeTab(CreativeTabsLoader.tabothtech);

    }

    @Override
    public int getItemEnchantability() {
        return 123123;
    }

    @Override
    public boolean isItemTool(ItemStack p_77616_1_) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
        return null;

    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return super.getArmorTexture(stack, entity, slot, type);
    }

    @Override
    public void addInformation(final ItemStack stack, final EntityPlayer aPlayer, final List list, final boolean bool) {
        list.add(EnumChatFormatting.RED + "铁证如山");
        super.addInformation(stack, aPlayer, list, bool);
    }

}
