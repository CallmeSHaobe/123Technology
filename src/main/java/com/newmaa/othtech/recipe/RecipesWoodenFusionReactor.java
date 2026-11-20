package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;

public class RecipesWoodenFusionReactor implements IRecipePool {

    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("steam", 1000))
            .fluidOutputs(Materials.Hydrogen.getPlasma(1))
            .eut(0)
            .duration(20 * 20)
            .addTo(Recipemaps.WFR);
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("ic2superheatedsteam", 1000))
            .fluidOutputs(Materials.Hydrogen.getPlasma(10))
            .eut(0)
            .duration(20 * 20)
            .addTo(Recipemaps.WFR);
        GTValues.RA.stdBuilder()
            .fluidInputs(FluidRegistry.getFluidStack("densesupercriticalsteam", 1000))
            .fluidOutputs(Materials.Hydrogen.getPlasma(100))
            .eut(0)
            .duration(20 * 20)
            .addTo(Recipemaps.WFR);
    }
}
