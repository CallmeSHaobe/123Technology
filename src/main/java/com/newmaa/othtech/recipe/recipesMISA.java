package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.Utils.RecipeBuilder;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.Materials;
import gregtech.api.util.GTModHandler;

public class recipesMISA implements IRecipePool {

    public void loadRecipes() {
        // Pyrope
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(Materials.Magnesium.getDust(1), 16 * 110),
                setStackSize(Materials.Manganese.getDust(1), 16 * 70),
                setStackSize(Materials.Borax.getDust(1), 16 * 60),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustRhenium", 1), 16 * 20))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockores", 1, 835), 8 * 256),
                setStackSize(Materials.Carbon.getDust(1), 40),
                Materials.Sulfur.getDust(80),
                Materials.Sodium.getDust(20))
            .fluidInputs(FluidRegistry.getFluidStack("bioethanol", 20000), FluidRegistry.getFluidStack("steam", 4000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("water", 32000),
                FluidRegistry.getFluidStack("mud.red.slurry", 32000))
            .specialValue(1)
            .eut(30720)
            .duration(400 * 20)
            .addTo(Recipemaps.MISA);
    }
}
