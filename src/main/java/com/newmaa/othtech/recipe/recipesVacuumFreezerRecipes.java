package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;

public class recipesVacuumFreezerRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid colder = FluidRegistry.getFluid("cryotheum");
        Fluid water = FluidRegistry.getFluid("water");

        final IRecipeMap VaF = RecipeMaps.vacuumFreezerRecipes;
        GTValues.RA.stdBuilder()
            .itemInputs(GTModHandler.getModItem("123Technology", "ingotHotDog", 1))
            .itemOutputs(GTModHandler.getModItem("123Technology", "MetaItemOTH", 1, 3))
            .fluidInputs(new FluidStack(colder, 1919))
            .fluidOutputs(new FluidStack(water, 1919))
            .noOptimize()
            .duration(2000)
            .eut(123123123)
            .addTo(VaF);
    }
}
