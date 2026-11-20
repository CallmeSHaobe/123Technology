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

    private static final ItemSuicideDrone[] REGISTERED_VARIANTS = new ItemSuicideDrone[VARIANT_COUNT + 1];

    private final int variant;
    private final float explosionSize;
    private final float entityDamage;
    private final float shockwaveRange;
    private final String[] tooltipLines;

    public ItemSuicideDrone(String name, int variant, float explosionSize, float entityDamage, float shockwaveRange,
        String... tooltipLines) {
        if (!isValidVariant(variant)) {
            throw new IllegalArgumentException("Unknown drone variant: " + variant);
        }
        validateBlastValue("Explosion size", explosionSize, 0.1F, 64F);
        validateBlastValue("Entity damage", entityDamage, 0F, 100000F);
        validateBlastValue("Shockwave range", shockwaveRange, 0F, 256F);
        if (REGISTERED_VARIANTS[variant] != null) {
            throw new IllegalArgumentException("Drone variant already registered: " + variant);
        }
        this.variant = variant;
        this.explosionSize = explosionSize;
        this.entityDamage = entityDamage;
        this.shockwaveRange = shockwaveRange;
        this.tooltipLines = tooltipLines == null ? new String[0] : tooltipLines.clone();
        setUnlocalizedName(name);
        setTextureName("minecraft:fireworks");
        setCreativeTab(CreativeTabsLoader.tabothtech);
        setMaxStackSize(64);
        REGISTERED_VARIANTS[variant] = this;
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

    private static void validateBlastValue(String name, float value, float minimum, float maximum) {
        if (!Float.isFinite(value) || value < minimum || value > maximum) {
            throw new IllegalArgumentException(
                name + " must be finite and in [" + minimum + ", " + maximum + "]: " + value);
        }
    }

    public float getExplosionSize() {
        return explosionSize;
    }

    public float getEntityDamage() {
        return entityDamage;
    }

    public float getShockwaveRange() {
        return shockwaveRange;
    }

    /** Used to fill missing blast settings in older saves and spawn data. */
    public static ItemSuicideDrone getRegisteredVariant(int variant) {
        if (!isValidVariant(variant) || REGISTERED_VARIANTS[variant] == null) {
            throw new IllegalStateException("Drone variant has no registered item: " + variant);
        }
        return REGISTERED_VARIANTS[variant];
    }

    public int getVariant() {
        return variant;
    }
}
