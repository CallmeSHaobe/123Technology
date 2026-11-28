package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gtPlusPlus.core.material.Particle;

public class RecipesCyclotronRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        final RecipeMap<?> CYC = RecipeMaps.cyclotronRecipes;
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Sensor_UXV.get(0),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(64, Particle.getBaseParticle(Particle.PROTON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(64, Particle.getBaseParticle(Particle.NEUTRON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(64, Particle.getBaseParticle(Particle.ELECTRON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(64, Particle.getBaseParticle(Particle.ELECTRON_NEUTRINO))
                    : null))
            .itemOutputs(GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 1, 26))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.TranscendentMetal.mStandardMoltenFluid)),
                    9216))
            .outputChances(1230)
            .duration(128 * 20)
            .eut(TierEU.UXV)
            .addTo(CYC);

    }
}
