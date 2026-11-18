package com.newmaa.othtech.common.item.itemMisc;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/** Model variants are shared by the item renderer and the launch platform's synchronized state. */
public class ItemSuicideDrone extends Item {

    public static final int PIG = 1;
    public static final int ANGELICA = 2;
    public static final int NUCLEAR = 3;
    public static final int NVIDIA = 4;
    public static final int VARIANT_COUNT = 4;

    public static boolean isValidVariant(int variant) {
        return variant >= PIG && variant <= VARIANT_COUNT;
    }

    private static final float[] REGISTERED_POWERS = new float[VARIANT_COUNT + 1];

    private final int variant;
    private final float explosionPower;
    private final String[] tooltipLines;

    public ItemSuicideDrone(String name, int variant, float explosionPower, String... tooltipLines) {
        if (!isValidVariant(variant)) {
            throw new IllegalArgumentException("Unknown drone variant: " + variant);
        }
        if (!Float.isFinite(explosionPower) || explosionPower <= 0F) {
            throw new IllegalArgumentException("Drone explosion power must be finite and positive: " + explosionPower);
        }
        if (REGISTERED_POWERS[variant] != 0F) {
            throw new IllegalArgumentException("Drone variant already registered: " + variant);
        }
        this.variant = variant;
        this.explosionPower = explosionPower;
        this.tooltipLines = tooltipLines == null ? new String[0] : tooltipLines.clone();
        REGISTERED_POWERS[variant] = explosionPower;
        setUnlocalizedName(name);
        setTextureName("minecraft:fireworks");
        setCreativeTab(CreativeTabsLoader.tabothtech);
        setMaxStackSize(64);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        for (String line : tooltipLines) {
            if (line != null) tooltip.add(StatCollector.translateToLocal(line));
        }
    }

    public float getExplosionPower() {
        return explosionPower;
    }

    /** Used only when old or malformed entity data has no valid saved power. */
    public static float getRegisteredExplosionPower(int variant) {
        if (!isValidVariant(variant) || REGISTERED_POWERS[variant] == 0F) {
            throw new IllegalStateException("Drone variant has no registered explosion power: " + variant);
        }
        return REGISTERED_POWERS[variant];
    }

    public int getVariant() {
        return variant;
    }
}
