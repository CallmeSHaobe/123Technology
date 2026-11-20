package com.newmaa.othtech.common.item.itemArmor;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.items.equipment.ItemArmorElytra;
import ic2.api.item.ElectricItem;
import ic2.api.item.ICustomDamageItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import ic2.api.util.Keys;

public class ItemPoweredElytra extends ItemArmorElytra implements ISpecialElectricItem, ICustomDamageItem {

    public static final String REGISTRY_NAME = "itemPoweredElytra";
    public static final double CAPACITY = 5_000_000_000D;
    private static final double TICKS_PER_SECOND = 20D;
    private static final double MAX_SPEED_PER_TICK = 80D / TICKS_PER_SECOND;
    private static final double ACCELERATION_PER_TICK = 0.32D;
    private static final double EU_PER_SECOND_PER_METER_PER_SECOND = 100_000D;

    @SideOnly(Side.CLIENT)
    private IIcon brokenIcon;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        brokenIcon = register.registerIcon("123technology:" + REGISTRY_NAME + "Broken");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return damage >= getMaxDamage() ? brokenIcon : super.getIconFromDamage(damage);
    }

    public ItemPoweredElytra() {
        setUnlocalizedName(REGISTRY_NAME);
        setTextureName("123technology:" + REGISTRY_NAME);
        setCreativeTab(CreativeTabsLoader.tabothtech);
    }

    /** Applies thrust to the velocity already calculated by Et Futurum for this movement. */
    public static boolean boost(EntityPlayer player) {
        if (!(player instanceof IElytraPlayer elytraPlayer) || !elytraPlayer.etfu$isElytraFlying()
            || !player.isClientWorld()
            || player.onGround
            || player.capabilities.isFlying
            || player.isInWater()
            || player.handleLavaMovement()
            || Keys.instance == null
            || !Keys.instance.isForwardKeyDown(player)) return false;

        ItemStack stack = getElytra(player);
        if (stack == null || !(stack.getItem() instanceof ItemPoweredElytra) || isBroken(stack)) return false;

        double speed = speed(player.motionX, player.motionY, player.motionZ);
        if (speed >= MAX_SPEED_PER_TICK) return false;

        Vec3 look = player.getLookVec();
        double x = player.motionX + look.xCoord * ACCELERATION_PER_TICK;
        double y = player.motionY + look.yCoord * ACCELERATION_PER_TICK;
        double z = player.motionZ + look.zCoord * ACCELERATION_PER_TICK;
        double boostedSpeed = speed(x, y, z);
        if (boostedSpeed <= speed) return false;

        double cost = Math.min(boostedSpeed, MAX_SPEED_PER_TICK) * EU_PER_SECOND_PER_METER_PER_SECOND;
        // Predict velocity locally. Only the server changes the charge, which the inventory then synchronizes.
        if (!player.capabilities.isCreativeMode && !ElectricItem.manager.canUse(stack, cost)) return false;
        if (!player.worldObj.isRemote && !player.capabilities.isCreativeMode
            && ElectricItem.manager.discharge(stack, cost, Integer.MAX_VALUE, true, false, false) < cost) return false;

        double scale = Math.min(1D, MAX_SPEED_PER_TICK / boostedSpeed);
        player.motionX = x * scale;
        player.motionY = y * scale;
        player.motionZ = z * scale;
        return true;
    }

    private static double speed(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public Item getChargedItem(ItemStack stack) {
        return this;
    }

    @Override
    public Item getEmptyItem(ItemStack stack) {
        return this;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return CAPACITY;
    }

    @Override
    public int getTier(ItemStack stack) {
        return 8;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return 2_000_000D;
    }

    @Override
    public IElectricItemManager getManager(ItemStack stack) {
        return ElectricItem.rawManager;
    }

    // Keep IC2's charge bar writes separate from the durability used by Et Futurum's flight checks.
    @Override
    public int getCustomDamage(ItemStack stack) {
        return stack.getItemDamage();
    }

    @Override
    public int getMaxCustomDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public void setCustomDamage(ItemStack stack, int damage) {}

    @Override
    public boolean applyCustomDamage(ItemStack stack, int damage, EntityLivingBase entity) {
        if (entity == null) return false;
        stack.damageItem(damage, entity);
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(
            EnumChatFormatting.AQUA + translateToLocal("tt.positron.charge")
                + ": "
                + String.format("%,.0f / %,.0f EU", ElectricItem.manager.getCharge(stack), CAPACITY));
        tooltip.add(EnumChatFormatting.GRAY + translateToLocal("tt.poweredElytra.boost"));
        tooltip.add(EnumChatFormatting.GRAY + translateToLocal("tt.poweredElytra.cost"));
    }
}
