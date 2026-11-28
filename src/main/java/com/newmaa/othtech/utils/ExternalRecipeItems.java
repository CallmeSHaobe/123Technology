package com.newmaa.othtech.utils;

import net.minecraft.item.ItemStack;

import com.glodblock.github.loader.ItemAndBlockHolder;

import appeng.api.AEApi;
import crazypants.enderio.EnderIO;
import crazypants.enderio.material.Material;
import gregtech.api.enums.Mods;

/** Guarded links to compile-time mods that are not required by the OTH FML manifest. */
public final class ExternalRecipeItems {

    private ExternalRecipeItems() {}

    public enum AeItem {
        SINGULARITY_CELL,
        UNIVERSE_CELL,
        SINGULARITY,
        SINGULARITY_CRAFTING_STORAGE
    }

    public enum AeFluidItem {
        SINGULARITY_CELL,
        UNIVERSE_CELL,
        WALRUS
    }

    public enum EnderItem {
        BASIC_CAPACITOR,
        FRANKEN_SKULL,
        ENDERMAN_SKULL,
        BROKEN_SPAWNER,
        SOUL_VESSEL
    }

    public enum EnderMaterial {
        PULSATING_CRYSTAL,
        VIBRANT_CRYSTAL,
        ENDER_CRYSTAL,
        ATTRACTOR_CRYSTAL,
        PRECIENT_CRYSTAL,
        VIBRANT_POWDER
    }

    public static ItemStack aeItem(long amount, AeItem item) {
        return Mods.AppliedEnergistics2.isModLoaded() ? Ae2Link.stack(amount, item) : null;
    }

    public static ItemStack aeFluidItem(long amount, AeFluidItem item) {
        return Mods.AE2FluidCraft.isModLoaded() ? Ae2FcLink.stack(amount, item) : null;
    }

    public static ItemStack enderIo(long amount, EnderItem item, int meta) {
        return Mods.EnderIO.isModLoaded() ? EnderIoLink.stack(amount, item, meta) : null;
    }

    public static ItemStack enderMaterial(long amount, EnderMaterial material) {
        return Mods.EnderIO.isModLoaded() ? EnderIoLink.material(amount, material) : null;
    }

    // The nested classes keep the optional-mod bytecode off the absent-mod path.
    private static final class Ae2Link {

        private static ItemStack stack(long amount, AeItem item) {
            var definitions = AEApi.instance()
                .definitions();
            // maybeStack requires a positive count; exact preserves catalyst 0 and GT's original clamp.
            var stack = switch (item) {
                case SINGULARITY_CELL -> definitions.items()
                    .cellSingularity()
                    .maybeStack(1);
                case UNIVERSE_CELL -> definitions.items()
                    .cellUniverse()
                    .maybeStack(1);
                case SINGULARITY -> definitions.materials()
                    .singularity()
                    .maybeStack(1);
                case SINGULARITY_CRAFTING_STORAGE -> definitions.blocks()
                    .craftingStorageSingularity()
                    .maybeStack(1);
            };
            return RecipeStacks.exact(amount, stack.orNull());
        }
    }

    private static final class Ae2FcLink {

        private static ItemStack stack(long amount, AeFluidItem item) {
            return switch (item) {
                case SINGULARITY_CELL -> RecipeStacks.exact(amount, ItemAndBlockHolder.SINGULARITY_CELL, 0);
                case UNIVERSE_CELL -> RecipeStacks.exact(amount, ItemAndBlockHolder.ARTIFICIAL_UNIVERSE_CELL, 0);
                case WALRUS -> RecipeStacks.exact(amount, ItemAndBlockHolder.WALRUS, 0);
            };
        }
    }

    private static final class EnderIoLink {

        private static ItemStack stack(long amount, EnderItem item, int meta) {
            return switch (item) {
                case BASIC_CAPACITOR -> RecipeStacks.exact(amount, EnderIO.itemBasicCapacitor, meta);
                case FRANKEN_SKULL -> RecipeStacks.exact(amount, EnderIO.itemFrankenSkull, meta);
                case ENDERMAN_SKULL -> RecipeStacks.exact(amount, EnderIO.blockEndermanSkull, meta);
                case BROKEN_SPAWNER -> RecipeStacks.exact(amount, EnderIO.itemBrokenSpawner, meta);
                case SOUL_VESSEL -> RecipeStacks.exact(amount, EnderIO.itemSoulVessel, meta);
            };
        }

        private static ItemStack material(long amount, EnderMaterial material) {
            Material variant = switch (material) {
                case PULSATING_CRYSTAL -> Material.PULSATING_CYSTAL;
                case VIBRANT_CRYSTAL -> Material.VIBRANT_CYSTAL;
                case ENDER_CRYSTAL -> Material.ENDER_CRYSTAL;
                case ATTRACTOR_CRYSTAL -> Material.ATTRACTOR_CRYSTAL;
                case PRECIENT_CRYSTAL -> Material.PRECIENT_CRYSTAL;
                case VIBRANT_POWDER -> Material.VIBRANT_POWDER;
            };
            return RecipeStacks.exact(amount, EnderIO.itemMaterial, variant.ordinal());
        }
    }
}
