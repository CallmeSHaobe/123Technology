package com.newmaa.othtech.utils;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;

import bartworks.system.material.Werkstoff;
import bartworks.system.material.WerkstoffLoader;
import gregtech.api.GregTechAPI;
import gregtech.api.casing.ICasing;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.items.MetaGeneratedItemX32;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.item.base.BaseItemComponent.ComponentTypes;
import gtPlusPlus.core.material.Material;

/** Builds exact recipe stacks without going through ore dictionary unification. */
public final class RecipeStacks {

    private RecipeStacks() {}

    /** Keeps the amount normalization of GTModHandler.getModItem, including zero-size catalysts. */
    public static ItemStack exact(long amount, ItemStack template) {
        // Forge's lookup supplies an invalid (negative-size) template for negative amounts.
        int count = (int) amount;
        return GregTechAPI.sPreloadStarted && count >= 0 ? GTUtility.copyAmount(count, template) : null;
    }

    /** Uses a registration to identify the item and meta, not any NBT on the registration's template. */
    public static ItemStack exact(long amount, ItemList entry) {
        return fromRegistration(amount, entry.getInternalStack_unsafe());
    }

    public static ItemStack exact(long amount, OTHItemList entry) {
        return fromRegistration(amount, entry.getInternalStack_unsafe());
    }

    private static ItemStack fromRegistration(long amount, ItemStack template) {
        return template == null ? null : exact(amount, template.getItem(), template.getItemDamage());
    }

    public static ItemStack exact(long amount, Item item, int meta) {
        return item == null ? null : exact(amount, new ItemStack(item, 1, meta));
    }

    public static ItemStack exact(long amount, Block block, int meta) {
        return block == null ? null : exact(amount, Item.getItemFromBlock(block), meta);
    }

    public static ItemStack generated(long amount, MetaGeneratedItemX32 item, OrePrefixes prefix, Materials material) {
        if (item == null || !GregTechAPI.sPreloadStarted) return null;
        // The form table is item-specific: scan its public prefix accessor, without ore dictionary lookup.
        for (int slot = 0; slot < 32; slot++) {
            int meta = slot * 1000 + material.getId();
            if (item.getOrePrefix(meta) == prefix) return exact(amount, item, meta);
        }
        return null;
    }

    public static ItemStack werkstoff(long amount, OrePrefixes prefix, Werkstoff material) {
        // Use the material's raw BW form, not Werkstoff.get's ore dictionary fallback.
        return exact(amount, WerkstoffLoader.items.get(prefix), material.getmID());
    }

    // BaseItemComponent registers this name. Looking it up avoids selecting an ore dictionary substitute.
    private static String gtppComponentName(Material material, ComponentTypes component) {
        return "item" + component.getComponent() + material.getUnlocalizedName();
    }

    public static ItemStack gtppComponent(long amount, Material material, ComponentTypes component) {
        return GTModHandler.getModItem(Mods.GTPlusPlus.ID, gtppComponentName(material, component), amount);
    }

    public static ItemStack gtppComponent(long amount, Material material, ComponentTypes component, int meta) {
        return GTModHandler.getModItem(Mods.GTPlusPlus.ID, gtppComponentName(material, component), amount, meta);
    }

    /** For recipes that directly constructed a stack, keep the requested count (even above 64). */
    public static ItemStack casing(int amount, ICasing casing) {
        return new ItemStack(casing.getBlock(), amount, casing.getBlockMeta());
    }
}
