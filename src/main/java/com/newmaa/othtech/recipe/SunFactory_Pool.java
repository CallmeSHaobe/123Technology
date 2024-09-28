package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.Utils.recipes.RecipeBuilder;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;

public class SunFactory_Pool implements IRecipePool {

    @Override
    public void loadRecipes() {
        RecipeMap<?> PCB = Recipemaps.SF1;
        RecipeMap<?> SMD = Recipemaps.SF2;
        RecipeMap<?> NMD = Recipemaps.SF3;
        RecipeMap<?> UNI = Recipemaps.SF4;
        Fluid sb = FluidRegistry.getFluid("molten.mutatedlivingsolder");
        ItemStack radox = GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979);
        // PCB
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 180),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 6),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 3),
                setStackSize(radox, 1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32748), 128))
            .noOptimize()
            .duration(4 * 20)
            .eut(TierEU.RECIPE_UEV)
            .addTo(PCB);
    }
}
