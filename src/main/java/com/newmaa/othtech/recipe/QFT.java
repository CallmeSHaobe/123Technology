package com.newmaa.othtech.recipe;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.enums.GT_Values;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GT_ModHandler;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.item.chemistry.GenericChem;
import gtPlusPlus.core.util.minecraft.ItemUtils;

public class QFT implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid songyou = FluidRegistry.getFluid("pineoil");
        Fluid Water = FluidRegistry.getFluid("water");
        Fluid nijiang = FluidRegistry.getFluid("mud.red.slurry");
        Fluid laoda = FluidRegistry.getFluid("molten.helicopter");

        final IRecipeMap qft = GTPPRecipeMaps.quantumForceTransformerRecipes;

        // ISA
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 0, 31027),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 0, 31028),
                ItemUtils.getSimpleStack(GenericChem.mPlatinumGroupCatalyst, 0))
            .fluidInputs(new FluidStack(songyou, 123), new FluidStack(laoda, 24))
            .itemOutputs(
                GT_ModHandler.getModItem("miscutils", "itemDustRhenium", 16),
                GT_ModHandler.getModItem("miscutils", "itemDustThallium", 16),
                GT_ModHandler.getModItem("miscutils", "itemDustSelenium", 16),
                GT_ModHandler.getModItem("miscutils", "itemDustGermanium", 16),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 2044),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 2073))
            .fluidOutputs(new FluidStack(Water, 123123), new FluidStack(nijiang, 1919810))
            .outputChances(1667, 1667, 1667, 1667, 1667, 1667)
            .noOptimize()
            .specialValue(2)
            .eut(123123123)
            .duration(123)
            .addTo(qft);

    }
}
