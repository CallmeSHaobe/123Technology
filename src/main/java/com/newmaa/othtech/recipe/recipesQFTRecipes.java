package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GTValues;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GTModHandler;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.item.chemistry.GenericChem;
import gtPlusPlus.core.util.minecraft.ItemUtils;

public class recipesQFTRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid songyou = FluidRegistry.getFluid("pineoil");
        Fluid Water = FluidRegistry.getFluid("water");
        Fluid nijiang = FluidRegistry.getFluid("mud.red.slurry");
        Fluid laoda = FluidRegistry.getFluid("molten.helicopter");

        final IRecipeMap qft = GTPPRecipeMaps.quantumForceTransformerRecipes;

        // ISA
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 31027),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 31028),
                ItemUtils.getSimpleStack(GenericChem.mPlatinumGroupCatalyst, 0))
            .fluidInputs(new FluidStack(songyou, 123), new FluidStack(laoda, 24))
            .itemOutputs(
                GTModHandler.getModItem("miscutils", "itemDustRhenium", 16),
                GTModHandler.getModItem("miscutils", "itemDustThallium", 16),
                GTModHandler.getModItem("miscutils", "itemDustSelenium", 16),
                GTModHandler.getModItem("miscutils", "itemDustGermanium", 16),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 2044),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 2073))
            .fluidOutputs(new FluidStack(Water, 123123), new FluidStack(nijiang, 1919810))
            .outputChances(1667, 1667, 1667, 1667, 1667, 1667)
            .noOptimize()
            .specialValue(2)
            .eut(123123123)
            .duration(123)
            .addTo(qft);

    }
}
