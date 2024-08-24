package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_ModHandler;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class Cyclotron implements IRecipePool {

    @Override
    public void loadRecipes() {
        final RecipeMap<?> CYC = GTPPRecipeMaps.cyclotronRecipes;
        GT_Values.RA.stdBuilder()
            .itemInputs(
                ItemList.Sensor_UXV.get(0),
                GT_ModHandler.getModItem("miscutils", "particleBase", 64, 18),
                GT_ModHandler.getModItem("miscutils", "particleBase", 64, 19),
                GT_ModHandler.getModItem("miscutils", "particleBase", 64, 7),
                GT_ModHandler.getModItem("miscutils", "particleBase", 64, 8))
            .itemOutputs(GT_ModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 1, 26))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.transcendentmetal"), 9216))
            .outputChances(1230)
            .noOptimize()
            .duration(128 * 20)
            .eut(TierEU.UXV)
            .addTo(CYC);

    }
}
