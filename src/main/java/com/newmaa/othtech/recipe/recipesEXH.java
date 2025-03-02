package com.newmaa.othtech.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.recipemap.Recipemaps;

public class recipesEXH implements IRecipePool {

    int mEut = 0;
    // public void EBFRecipesCheck() {
    // for (GTRecipe recipe : RecipeMaps.vacuumFreezerRecipes.getAllRecipes()) {
    // ItemStack mIp;
    // ItemStack mOp;
    // this.mEut = recipe.mEUt;
    // addExtremeHeatExchangerRecipe(
    // recipe.mInputs,
    // recipe.mOutputs,
    // FluidRegistry.getFluidStack("water", mEut / 1000 / 2 / 2),
    // FluidRegistry.getFluidStack("densesupercriticalsteam", mEut / 1000 / 2)
    // );
    // }
    // }

    @Deprecated
    public void addExtremeHeatExchangerRecipe(ItemStack inputItems, ItemStack outputItems, FluidStack inputFluid,
        FluidStack outputFluid) {
        Recipemaps.EXH.addRecipe(
            new FrostExtremeHeatExchangerRecipe(
                new ItemStack[] { inputItems },
                new ItemStack[] { outputItems },
                new FluidStack[] { inputFluid },
                new FluidStack[] { outputFluid }));
    }

    @Override
    public void loadRecipes() {
        // EBFRecipesCheck();
    }
}
