package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Mods;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtnhlanth.common.register.WerkstoffMaterialPool;

public class RecipesMixerRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid Ne = FluidRegistry.getFluid(RecipeFluids.werkstoffName(WerkstoffLoader.Neon));
        Fluid I = FluidRegistry.getFluid(
            Mods.GTNHLanthanides.isModLoaded() ? RecipeFluids.werkstoffName(WerkstoffMaterialPool.Iodine) : "iodine");
        Fluid Shit = FluidRegistry.getFluid(RecipeFluids.gtppMoltenName(MaterialsAlloy.ARCANITE));

        final RecipeMap<?> LMixer = RecipeMaps.mixerNonCellRecipes;
        GTValues.RA.stdBuilder()
            .fluidInputs(new FluidStack(Ne, 1000), new FluidStack(I, 1000))
            .fluidOutputs(new FluidStack(Shit, 144))
            .itemOutputs(OTHItemList.dustLookNEIM.get(1))

            .duration(200)
            .eut(6144)
            .addTo(LMixer);
        GTValues.RA.stdBuilder()
            .itemOutputs(
                (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded() ? RecipeStacks.exact(
                    5,
                    WerkstoffLoader.items.get(OrePrefixes.dust),
                    (Mods.GoodGenerator.isModLoaded() ? GGMaterial.lumiium.getmID() : 10101)) : null))
            .itemInputs(
                GTUtility.getIntegratedCircuit(17),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 20),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 30),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 2),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 40),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 10),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 5))

            .duration(400)
            .eut(123123)
            .addTo(LMixer);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded() ? RecipeStacks.exact(
                        1,
                        WerkstoffLoader.items.get(OrePrefixes.dust),
                        (Mods.GoodGenerator.isModLoaded() ? GGMaterial.marM200.getmID() : 10096)) : null),
                    18),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded() ? RecipeStacks.exact(
                        1,
                        WerkstoffLoader.items.get(OrePrefixes.dust),
                        (Mods.GoodGenerator.isModLoaded() ? GGMaterial.marCeM200.getmID() : 10097)) : null),
                    19))
            .fluidInputs(
                FluidRegistry.getFluidStack(
                    Mods.GoodGenerator.isModLoaded() ? RecipeFluids.werkstoffMoltenName(GGMaterial.lithiumChloride)
                        : "molten.lithium chloride",
                    144))

            .duration(30 * 20)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(LMixer);
    }
}
