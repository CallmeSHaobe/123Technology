package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GT_Values;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GT_ModHandler;

public class Freezer implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid colder = FluidRegistry.getFluid("cryotheum");
        Fluid water = FluidRegistry.getFluid("water");

        final IRecipeMap VaF = RecipeMaps.vacuumFreezerRecipes;
        GT_Values.RA.stdBuilder()
            .itemInputs(GT_ModHandler.getModItem("123Technology", "ingotHotDog", 1))
            .itemOutputs(GT_ModHandler.getModItem("123Technology", "ingotDog", 1))
            .fluidInputs(new FluidStack(colder, 1919))
            .fluidOutputs(new FluidStack(water, 1919))
            .noOptimize()
            .duration(2000)
            .eut(123123123)
            .addTo(VaF);
    }
}