package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;

public class RecipesCyclotronRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        final RecipeMap<?> CYC = GTPPRecipeMaps.cyclotronRecipes;
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Sensor_UXV.get(0),
                GTModHandler.getModItem("miscutils", "particleBase", 64, 18),
                GTModHandler.getModItem("miscutils", "particleBase", 64, 19),
                GTModHandler.getModItem("miscutils", "particleBase", 64, 7),
                GTModHandler.getModItem("miscutils", "particleBase", 64, 8))
            .itemOutputs(GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 1, 26))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.transcendentmetal"), 9216))
            .outputChances(1230)

            .duration(128 * 20)
            .eut(TierEU.UXV)
            .addTo(CYC);

    }
}
