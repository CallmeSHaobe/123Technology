package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.materials;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;

public class recipesVacuumFreezerRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid colder = FluidRegistry.getFluid("cryotheum");
        Fluid water = FluidRegistry.getFluid("water");

        final IRecipeMap VaF = RecipeMaps.vacuumFreezerRecipes;
        GTValues.RA.stdBuilder()
            .itemInputs(OTHItemList.ingotHotDog.get(1))
            .itemOutputs(materials.IrOsSm.get(OrePrefixes.ingot, 1))
            .fluidInputs(new FluidStack(colder, 1919))
            .fluidOutputs(new FluidStack(water, 1919))
            .noOptimize()
            .duration(2000)
            .eut(123123123)
            .addTo(VaF);
    }
}
