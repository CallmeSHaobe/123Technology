package com.newmaa.othtech.recipe;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.ExternalRecipeItems;
import com.newmaa.othtech.utils.ExternalRecipeItems.EnderMaterial;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTUtility;

public class RecipesEIO implements IRecipePool {

    @Override
    public void loadRecipes() {
        final RecipeMap<?> EIO = Recipemaps.EIO;

        final ItemStack villagerTag = ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.SOUL_VESSEL, 0);
        NBTTagCompound Villager = new NBTTagCompound();
        Villager.setString("id", "Villager");
        villagerTag.setTagCompound(Villager);

        final ItemStack EnderManTag = ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.SOUL_VESSEL, 0);
        NBTTagCompound EnderMan = new NBTTagCompound();
        EnderMan.setString("id", "Enderman");
        EnderManTag.setTagCompound(EnderMan);

        final ItemStack WitschTag = ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.SOUL_VESSEL, 0);
        NBTTagCompound Witsch = new NBTTagCompound();
        Witsch.setString("id", "Witch");
        WitschTag.setTagCompound(Witsch);

        final ItemStack ZombieTag = ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.SOUL_VESSEL, 0);
        NBTTagCompound Zombie = new NBTTagCompound();
        Zombie.setString("id", "Zombie");
        ZombieTag.setTagCompound(Zombie);

        // Villager Crystals
        GTRecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(1), Materials.Emerald.getGems(1), villagerTag)
            .itemOutputs(ExternalRecipeItems.enderMaterial(1, EnderMaterial.ATTRACTOR_CRYSTAL))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                Materials.Emerald.getGems(1),
                ExternalRecipeItems.enderMaterial(0, EnderMaterial.ATTRACTOR_CRYSTAL))
            .itemOutputs(ExternalRecipeItems.enderMaterial(1, EnderMaterial.ATTRACTOR_CRYSTAL))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // Zombie Crystals
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 1),
                ZombieTag)
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 2))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 1),
                ExternalRecipeItems.enderIo(0, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 2))
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 2))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // EnderMAn Crystals
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 3),
                EnderManTag)
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 4))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 3),
                ExternalRecipeItems.enderIo(0, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 4))
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 4))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // Ender Crystals
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                ExternalRecipeItems.enderMaterial(1, EnderMaterial.VIBRANT_CRYSTAL),
                EnderManTag)
            .itemOutputs(ExternalRecipeItems.enderMaterial(1, EnderMaterial.ENDER_CRYSTAL))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                ExternalRecipeItems.enderMaterial(1, EnderMaterial.VIBRANT_CRYSTAL),
                ExternalRecipeItems.enderMaterial(0, EnderMaterial.ENDER_CRYSTAL))
            .itemOutputs(ExternalRecipeItems.enderMaterial(1, EnderMaterial.ENDER_CRYSTAL))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                ExternalRecipeItems.enderMaterial(1, EnderMaterial.PULSATING_CRYSTAL),
                EnderManTag)
            .itemOutputs(ExternalRecipeItems.enderMaterial(1, EnderMaterial.PRECIENT_CRYSTAL))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                ExternalRecipeItems.enderMaterial(1, EnderMaterial.PULSATING_CRYSTAL),
                ExternalRecipeItems.enderMaterial(0, EnderMaterial.PRECIENT_CRYSTAL))
            .itemOutputs(ExternalRecipeItems.enderMaterial(1, EnderMaterial.PRECIENT_CRYSTAL))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        // Witsch
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 3),
                WitschTag)
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 4))
            .eut(480)
            .nbtSensitive()
            .duration(60 * 20)
            .addTo(EIO);
        GTRecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 3),
                ExternalRecipeItems.enderIo(0, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 4))
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 4))
            .eut(480)
            .nbtSensitive()
            .duration(80 * 20)
            .addTo(EIO);

        GTRecipeBuilder.builder()
            .itemInputs(
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.SOUL_VESSEL, 0)
                    .setStackDisplayName(StatCollector.translateToLocal("oth.recipe.eio.anySoulVial")),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.BROKEN_SPAWNER, 0)
                    .setStackDisplayName(StatCollector.translateToLocal("oth.recipe.eio.anySpawner")))
            .itemOutputs(
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.BROKEN_SPAWNER, 0)
                    .setStackDisplayName(StatCollector.translateToLocal("oth.recipe.eio.boundSpawner")),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.SOUL_VESSEL, 0))
            .eut(114)
            .duration(100)
            .fake()
            .addTo(EIO);

        // SkullAssembler
        GTValues.RA.stdBuilder()
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 0))
            .itemInputsUnsafe(
                new ItemStack(Items.skull, 1),
                Materials.EnergeticAlloy.getIngots(2),
                Materials.Silicon.getPlates(2),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.BASIC_CAPACITOR, 0))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        GTValues.RA.stdBuilder()
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 1))
            .itemInputsUnsafe(
                new ItemStack(Items.skull, 1),
                Materials.Soularium.getIngots(2),
                Materials.Silicon.getPlates(2),
                Materials.Redstone.getDust(1))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        GTValues.RA.stdBuilder()
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.ENDERMAN_SKULL, 2))
            .itemInputsUnsafe(
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.BASIC_CAPACITOR, 0),
                Materials.Soularium.getIngots(2),
                new ItemStack(Items.potionitem, 2),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.ENDERMAN_SKULL, 0))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        GTValues.RA.stdBuilder()
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 3))
            .itemInputsUnsafe(
                Materials.VibrantAlloy.getIngots(1),
                Materials.Soularium.getIngots(2),
                Materials.Silicon.getPlates(2),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.ENDERMAN_SKULL, 0))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        GTValues.RA.stdBuilder()
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 5))
            .itemInputsUnsafe(
                new ItemStack(Items.skull, 1),
                Materials.Soularium.getIngots(2),
                Materials.Silicon.getPlates(1),
                new ItemStack(Items.rotten_flesh, 2))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        GTValues.RA.stdBuilder()
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.FRANKEN_SKULL, 6))
            .itemInputsUnsafe(
                Materials.EnergeticAlloy.getIngots(1),
                Materials.Soularium.getIngots(2),
                Materials.Silicon.getPlates(1),
                ExternalRecipeItems.enderMaterial(2, EnderMaterial.PULSATING_CRYSTAL))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);

        GTValues.RA.stdBuilder()
            .itemOutputs(ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.BASIC_CAPACITOR, 6))
            .itemInputsUnsafe(
                new ItemStack(Items.golden_apple, 1, 1),
                Materials.Soularium.getIngots(2),
                ExternalRecipeItems.enderMaterial(2, EnderMaterial.VIBRANT_POWDER),
                ExternalRecipeItems.enderIo(1, ExternalRecipeItems.EnderItem.BASIC_CAPACITOR, 3))
            .eut(120)
            .duration(60 * 20)
            .addTo(EIO);
    }
}
