package com.newmaa.othtech.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.github.technus.tectech.recipe.TT_recipeAdder;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;

public class RecipesFromTST implements IRecipePool {
    // Compatible with TST added structure blocks
    // 添加了一些TST添加的结构方块的配方

    @Override
    public void loadRecipes() {
        final RecipeMap<?> Assembler = RecipeMaps.assemblerRecipes;
        // 终极分子机械方块
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 1, 4),
                ItemList.Field_Generator_UIV.get(2),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 23581),
                GT_ModHandler.getModItem("miscutils", "itemFoilHypogen", 64),
                GT_ModHandler.getModItem("miscutils", "itemScrewCelestialTungsten", 64),
                GT_ModHandler.getModItem("miscutils", "itemRingCelestialTungsten", 64))
            .itemOutputs(GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 1, 12))
            .noOptimize()
            .eut(TierEU.UMV)
            .duration(300 * 20)
            .addTo(Assembler);
        // 时空扭曲机械方块
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 1, 12),
            (int) 81920,
            256,
            (int) 2097152,
            64,
            new ItemStack[] { GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 4, 8),
                ItemList.Field_Generator_UMV.get(1), GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 8, 7),
                GT_ModHandler.getModItem("dreamcraft", "item.HighEnergyFlowCircuit", 4),
                ItemList.Field_Generator_UEV.get(24), GT_ModHandler.getModItem("gregtech", "gt.blockcasings2", 24, 9),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings2", 24, 5),
                GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Optical, 24),
                GT_OreDictUnificator.get(OrePrefixes.plateDense, Materials.CosmicNeutronium, 64),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 14),
                GT_ModHandler.getModItem("miscutils", "particleBase", 2, 14),
                GT_OreDictUnificator.get(OrePrefixes.plateDense, Materials.CosmicNeutronium, 64),
                GT_OreDictUnificator.get(OrePrefixes.plateDense, Materials.CosmicNeutronium, 64),
                GT_OreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 64),
                GT_OreDictUnificator.get(OrePrefixes.plateDense, Materials.Neutronium, 64),
                GT_OreDictUnificator.get(OrePrefixes.plateDense, Materials.CosmicNeutronium, 64)

            },
            new FluidStack[] { FluidRegistry.getFluidStack("molten.mutatedlivingsolder", 147456),
                FluidRegistry.getFluidStack("ic2uumatter", 64000),
                FluidRegistry.getFluidStack("molten.osmiridium", 36864),
                FluidRegistry.getFluidStack("molten.superconductoruevbase", 4608) },
            GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 1, 9),
            512 * 20,
            1966080

        );
        // 传输机械方块
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 1, 9),
            (int) 163840,
            256,
            (int) TierEU.UEV,
            64,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 16, 4684),
                GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 4, 12),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings", 4, 14),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings", 4, 13),
                GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 16), ItemList.Field_Generator_UMV.get(8),
                ItemList.Emitter_UMV.get(6), GT_ModHandler.getModItem("dreamcraft", "item.PikoCircuit", 8),
                ItemList.Field_Generator_UEV.get(24), GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 28581),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 23581),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 22581),

            },
            new FluidStack[] { FluidRegistry.getFluidStack("molten.mutatedlivingsolder", 18432),
                FluidRegistry.getFluidStack("ic2uumatter", 64000),
                FluidRegistry.getFluidStack("molten.superconductorumvbase", 18432),
                FluidRegistry.getFluidStack("exciteddtec", 16000) },
            GT_ModHandler.getModItem("tectech", "gt.blockcasingsTT", 1, 10),
            512 * 20,
            (int) TierEU.UMV);

    }

}
