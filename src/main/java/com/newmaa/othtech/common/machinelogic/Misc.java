package com.newmaa.othtech.common.machinelogic;

import static gregtech.api.util.GTModHandler.getModItem;
import static tectech.thing.CustomItemList.astralArrayFabricator;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import gregtech.api.enums.Mods;

public class Misc {

    // thanks for TST
    public static ItemStack ASTRAL_ARRAY_FABRICATOR;
    public static Item PickaxeOfTheCore;
    public static Item BoundPickaxe;
    public static Item TerraShatterer;

    public static void initStatics() {
        ASTRAL_ARRAY_FABRICATOR = astralArrayFabricator.get(1);

        ItemStack pickaxeOfTheCore = getModItem(Mods.Thaumcraft.ID, "ItemPickaxeElemental", 1);
        if (pickaxeOfTheCore != null) {
            PickaxeOfTheCore = pickaxeOfTheCore.getItem();
        } else {
            PickaxeOfTheCore = Items.iron_pickaxe;
        }

        ItemStack boundPickaxe = getModItem(Mods.BloodMagic.ID, "boundPickaxe", 1);
        if (boundPickaxe != null) {
            BoundPickaxe = boundPickaxe.getItem();
        } else {
            BoundPickaxe = Items.golden_pickaxe;
        }

        ItemStack terraShatterer = getModItem(Mods.Botania.ID, "terraPick", 1);
        if (terraShatterer != null) {
            TerraShatterer = terraShatterer.getItem();
        } else {
            TerraShatterer = Items.diamond_pickaxe;
        }
    }
}
