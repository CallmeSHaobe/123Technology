package com.newmaa.othtech.recipe;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_RecipeBuilder;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import net.minecraftforge.fluids.Fluid;

public class EBF implements IRecipePool {

    @Override
    public void loadRecipes() {
        final IRecipeMap EBF = RecipeMaps.blastFurnaceRecipes;
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_Utility.getIntegratedCircuit(1),
                GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 1)
                )
            .itemOutputs(GT_ModHandler.getModItem("123Technology", "ingotHotDog", 1))
            .specialValue(-123123)
            .noOptimize()
            .duration(114514)
            .eut(TierEU.UXV)
            .addTo(EBF);

    }
}
