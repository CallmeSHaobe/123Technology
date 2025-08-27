package com.newmaa.othtech.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.newmaa.othtech.common.recipemap.Recipemaps;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipeBuilder;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.ItemList;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.objects.Logger;
import gtPlusPlus.core.util.recipe.GTRecipeUtils;

public class RecipesEXH implements IRecipePool {

    static Collection<GTRecipe> recipes = RecipeMaps.vacuumFreezerRecipes.getAllRecipes();

    public static void convertBlastToEXH() {
        for (GTRecipe recipe : recipes) {
            if (recipe == null || recipe.mFakeRecipe || recipe.mHidden) continue;

            int newDuration = recipe.mDuration * 4;
            int eut = recipe.mEUt;

            GTRecipeBuilder builder = GTRecipeBuilder.builder()
                .duration(newDuration)
                .eut(eut);

            if (recipe.mInputs != null && recipe.mInputs.length > 0) {
                builder.itemInputs(recipe.mInputs);
            }

            if (recipe.mOutputs != null && recipe.mOutputs.length > 0) {
                builder.itemOutputs(recipe.mOutputs);
            }

            List<FluidStack> inputFluids = new ArrayList<>();
            if (recipe.mFluidInputs != null) {
                inputFluids.addAll(Arrays.asList(recipe.mFluidInputs));
            }

            long waterAmount = Math.max((long) eut * newDuration / 2000, 2);
            int safeWaterAmount = (int) Math.min(waterAmount, Integer.MAX_VALUE);

            FluidStack waterInput = FluidRegistry.getFluidStack("water", safeWaterAmount);
            if (waterInput != null) inputFluids.add(waterInput);

            builder.fluidInputs(inputFluids.toArray(new FluidStack[0]));

            List<FluidStack> outputFluids = new ArrayList<>();
            if (recipe.mFluidOutputs != null) {
                outputFluids.addAll(Arrays.asList(recipe.mFluidOutputs));
            }

            long steamAmount = Math.max((long) eut * newDuration / 4000, 1);
            int safeSteamAmount = (int) Math.min(steamAmount, Integer.MAX_VALUE);

            FluidStack steamOutput = FluidRegistry.getFluidStack("densesupercriticalsteam", safeSteamAmount);
            if (steamOutput != null) outputFluids.add(steamOutput);

            builder.fluidOutputs(outputFluids.toArray(new FluidStack[0]));

            builder.addTo(Recipemaps.EXH);
        }

    }

    public static void enhanceEXHRecipes() {
        Collection<GTRecipe> exhRecipes = Recipemaps.EXH.getAllRecipes();

        for (GTRecipe recipe : exhRecipes) {
            if (recipe == null || recipe.mFakeRecipe || recipe.mHidden) continue;

            recipe.mHidden = true;

            GTRecipeBuilder builder = GTRecipeBuilder.builder()
                .duration(recipe.mDuration * 4)
                .eut(recipe.mEUt / 2);

            if (recipe.mOutputs != null && recipe.mOutputs.length > 0) {
                List<ItemStack> newOutputs = new ArrayList<>();
                for (ItemStack output : recipe.mOutputs) {
                    if (output != null) {
                        ItemStack doubled = output.copy();
                        newOutputs.add(doubled);
                    }
                }
                builder.itemOutputs(newOutputs.toArray(new ItemStack[0]));
            }

            if (recipe.mInputs != null && recipe.mInputs.length > 0) {
                builder.itemInputs(recipe.mInputs);
            }

            if (recipe.mFluidInputs != null && recipe.mFluidInputs.length > 0) {
                builder.fluidInputs(recipe.mFluidInputs);
            }

            if (recipe.mFluidOutputs != null && recipe.mFluidOutputs.length > 0) {
                builder.fluidOutputs(recipe.mFluidOutputs);
            }

            builder.metadata(GTRecipeConstants.CLEANROOM, true)
                .specialValue(1001);

            builder.addTo(Recipemaps.EXH);
        }
    }


    @Override
    public void loadRecipes() {
        convertBlastToEXH();
        enhanceEXHRecipes();
    }
}
