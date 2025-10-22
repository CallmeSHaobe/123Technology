package com.newmaa.othtech.recipe;


import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RecipesEXH implements IRecipePool {

    public static void generateEnhancedEXHRecipes() {
        Collection<GTRecipe> recipes = RecipeMaps.vacuumFreezerRecipes.getAllRecipes();

        for (GTRecipe recipe : recipes) {
            if (recipe == null || recipe.mFakeRecipe || recipe.mHidden) continue;

            int newDuration = recipe.mDuration * 4;
            int newEUt = 123;

            GTRecipeBuilder builder = GTRecipeBuilder.builder()
                .duration(newDuration)
                .eut(newEUt)
                .metadata(GTRecipeConstants.CLEANROOM, true)
                .specialValue(1001);

            List<ItemStack> inputItems = new ArrayList<>();
            if (recipe.mInputs != null && recipe.mInputs.length > 0) {
                inputItems.addAll(Arrays.asList(recipe.mInputs));
            }

            List<FluidStack> inputFluids = new ArrayList<>();
            if (recipe.mFluidInputs != null) {
                inputFluids.addAll(Arrays.asList(recipe.mFluidInputs));
            }

            long waterAmount = Math.max((long) recipe.mEUt * recipe.mDuration / 1000, 2);
            int safeWaterAmount = (int) Math.min(waterAmount, Integer.MAX_VALUE);
            FluidStack waterInput = FluidRegistry.getFluidStack("water", safeWaterAmount);
            if (waterInput != null) inputFluids.add(waterInput);

            builder.fluidInputs(inputFluids.toArray(new FluidStack[0]));

            long waterCountInNewRecipe = inputFluids.stream()
                .filter(Objects::nonNull)
                .filter(
                    fs -> fs.getFluid() != null && "water".equals(
                        fs.getFluid()
                            .getName()))
                .count();

            if (waterCountInNewRecipe >= 2) {
                ItemStack circuit = GTUtility.getIntegratedCircuit(1);
                if (circuit != null) inputItems.add(circuit);
            }

            if (recipe.mOutputs != null && recipe.mOutputs.length > 0) {
                builder.itemOutputs(recipe.mOutputs);
            }

            List<FluidStack> outputFluids = new ArrayList<>();
            if (recipe.mFluidOutputs != null) {
                outputFluids.addAll(Arrays.asList(recipe.mFluidOutputs));
            }

            long steamAmount = Math.max((long) recipe.mEUt * recipe.mDuration / 2000, 1);
            int safeSteamAmount = (int) Math.min(steamAmount, Integer.MAX_VALUE);
            FluidStack steamOutput = FluidRegistry.getFluidStack("densesupercriticalsteam", safeSteamAmount);
            if (steamOutput != null) outputFluids.add(steamOutput);

            builder.fluidOutputs(outputFluids.toArray(new FluidStack[0]));

            if (!inputItems.isEmpty()) {
                builder.itemInputs(inputItems.toArray(new ItemStack[0]));
            }

            builder.addTo(Recipemaps.EXH);
        }
    }

    @Override
    public void loadRecipes() {
        generateEnhancedEXHRecipes();
    }
}
