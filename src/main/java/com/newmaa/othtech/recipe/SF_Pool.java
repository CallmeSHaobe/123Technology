package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.Utils.recipes.RecipeBuilder;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;

public class SF_Pool implements IRecipePool {

    public void loadRecipes() {
        ItemStack Fe = GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 2032);
        RecipeMap<?> SF = Recipemaps.TSSF;

        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(Fe, 256000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 5120),
                GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1), 256000))
            .fluidInputs(FluidRegistry.getFluidStack("oxygen", 25600))
            .noOptimize()
            .specialValue(3200)
            .eut(7680)
            .duration(4000 * 20)
            .addTo(SF);

    }
}
