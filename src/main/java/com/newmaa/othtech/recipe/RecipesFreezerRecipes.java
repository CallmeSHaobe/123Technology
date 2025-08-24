package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.BWMaterials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class RecipesFreezerRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid colder = FluidRegistry.getFluid("cryotheum");
        Fluid water = FluidRegistry.getFluid("water");

        final IRecipeMap VaF = RecipeMaps.vacuumFreezerRecipes;
        GTValues.RA.stdBuilder()
            .itemInputs(OTHItemList.ingotHotDog.get(1))
            .itemOutputs(BWMaterials.IrOsSm.get(OrePrefixes.ingot, 1))
            .fluidInputs(new FluidStack(colder, 100))
            .fluidOutputs(new FluidStack(water, 100))

            .duration(2000)
            .eut(TierEU.RECIPE_UEV)
            .addTo(VaF);
        GTValues.RA.stdBuilder()
            .itemInputs(OTHItemList.ingotHotDog.get(1))
            .itemOutputs(BWMaterials.IrOsSm.get(OrePrefixes.ingot, 1))
            .fluidInputs(new FluidStack(colder, 100))
            .fluidOutputs(new FluidStack(water, 100))

            .duration(2000)
            .eut(TierEU.RECIPE_UEV)
            .addTo(GTPPRecipeMaps.advancedFreezerRecipes);
    }
}
