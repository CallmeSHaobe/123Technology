package com.newmaa.othtech.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeBuilder;

import crazypants.enderio.EnderIO;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public class recipesEIO implements IRecipePool {

    @Override
    public void loadRecipes() {
        final RecipeMap<?> EIO = Recipemaps.EIO;

        final ItemStack villagerTag = GTModHandler.getModItem(EnderIO.MODID, "itemSoulVessel", 1);
        NBTTagCompound Villager = new NBTTagCompound();
        Villager.setString("id", "Villager");
        villagerTag.setTagCompound(Villager);

        final ItemStack EnderManTag = GTModHandler.getModItem(EnderIO.MODID, "itemSoulVessel", 1);
        NBTTagCompound EnderMan = new NBTTagCompound();
        EnderMan.setString("id", "Enderman");
        EnderManTag.setTagCompound(EnderMan);

        final ItemStack WitschTag = GTModHandler.getModItem(EnderIO.MODID, "itemSoulVessel", 1);
        NBTTagCompound Witsch = new NBTTagCompound();
        Witsch.setString("id", "Witch");
        WitschTag.setTagCompound(Witsch);

        final ItemStack ZombieTag = GTModHandler.getModItem(EnderIO.MODID, "itemSoulVessel", 1);
        NBTTagCompound Zombie = new NBTTagCompound();
        Zombie.setString("id", "Zombie");
        ZombieTag.setTagCompound(Zombie);

        // Villager Crystals
        GTRecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), Materials.Emerald.getGems(1), villagerTag)
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 9))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                Materials.Emerald.getGems(1),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 0, 9))
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 9))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // Zombie Crystals
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 1),
                ZombieTag)
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 2))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 1),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 0, 2))
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 2))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // EnderMAn Crystals
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 3),
                EnderManTag)
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 4))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 3),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 0, 4))
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 4))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // Ender Crystals
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 6),
                EnderManTag)
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 8))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 6),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 0, 8))
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 8))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 5),
                EnderManTag)
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 13))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 5),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 0, 13))
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 1, 13))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // Witsch
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 3),
                WitschTag)
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 4))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 3),
                GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 0, 4))
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 4))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // //BlinderFakeRecipes
        // GTRecipeBuilder.builder()
        // .itemInputs(
        // GTModHandler.getModItem(EnderIO.MODID, "itemSoulVessel", 1).setStackDisplayName("任意灵魂瓶"),
        // GTModHandler.getModItem(EnderIO.MODID, "itemBrokenSpawner", 1).setStackDisplayName("任意刷怪笼")
        // )
        // .itemOutputs(
        // GTModHandler.getModItem(EnderIO.MODID, "itemBrokenSpawner", 1).setStackDisplayName("绑定的刷怪笼"),
        // GTModHandler.getModItem(EnderIO.MODID, "itemSoulVessel", 1)
        // )
        // .eut(480)
        // .duration(10 * 20)
        // .addTo(EIO);

        // SkullAssembler
        RecipeBuilder.builder()
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 0))
            .itemInputs(
                new ItemStack(Items.skull, 1),
                Materials.EnergeticAlloy.getIngots(2),
                Materials.Silicon.getPlates(2),
                GTModHandler.getModItem(EnderIO.MODID, "itemBasicCapacitor", 1))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        RecipeBuilder.builder()
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 1))
            .itemInputs(
                new ItemStack(Items.skull, 1),
                Materials.Soularium.getIngots(2),
                Materials.Silicon.getPlates(2),
                Materials.Redstone.getDust(1))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        RecipeBuilder.builder()
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "blockEndermanSkull", 1, 2))
            .itemInputs(
                GTModHandler.getModItem(EnderIO.MODID, "itemBasicCapacitor", 1),
                Materials.Soularium.getIngots(2),
                new ItemStack(Items.potionitem, 2),
                GTModHandler.getModItem(EnderIO.MODID, "blockEndermanSkull", 1))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        RecipeBuilder.builder()
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 3))
            .itemInputs(
                Materials.VibrantAlloy.getIngots(1),
                Materials.Soularium.getIngots(2),
                Materials.Silicon.getPlates(2),
                GTModHandler.getModItem(EnderIO.MODID, "blockEndermanSkull", 1))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        RecipeBuilder.builder()
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 5))
            .itemInputs(
                new ItemStack(Items.skull, 1),
                Materials.Soularium.getIngots(2),
                Materials.Silicon.getPlates(1),
                new ItemStack(Items.rotten_flesh, 2))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        RecipeBuilder.builder()
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemFrankenSkull", 1, 6))
            .itemInputs(
                Materials.EnergeticAlloy.getIngots(1),
                Materials.Soularium.getIngots(2),
                Materials.Silicon.getPlates(1),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 2, 5))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        RecipeBuilder.builder()
            .itemOutputs(GTModHandler.getModItem(EnderIO.MODID, "itemBasicCapacitor", 1, 6))
            .itemInputs(
                new ItemStack(Items.golden_apple, 1, 1),
                Materials.Soularium.getIngots(2),
                GTModHandler.getModItem(EnderIO.MODID, "itemMaterial", 2, 15),
                GTModHandler.getModItem(EnderIO.MODID, "itemBasicCapacitor", 1, 3))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);
    }
}
