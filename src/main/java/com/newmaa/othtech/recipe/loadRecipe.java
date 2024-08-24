package com.newmaa.othtech.recipe;

import com.github.technus.tectech.recipe.TecTechRecipeMaps;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
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

public class loadRecipe implements IRecipePool {

    @Override
    public void loadRecipes()

    {

        Fluid water = FluidRegistry.getFluid("water");
        Fluid Ewater = FluidRegistry.getFluid("grade8purifiedwater");
        Fluid songyou = FluidRegistry.getFluid("pineoil");
        Fluid white = FluidRegistry.getFluid("molten.whitedwarfmatter");
        Fluid cuihuaji = FluidRegistry.getFluid("exciteddtsc");
        final RecipeMap<?> Nan = RecipeMaps.nanoForgeRecipes;


        // dustIrOsSm assembler
        // GT_Values.RA.stdBuilder()
        // .metadata(RESEARCH_ITEM, ItemList.Field_Generator_EV.get(1))
        // .metadata(RESEARCH_TIME, 123 * MINUTES)
        // .itemInputs(
        // GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
        // GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1),
        // GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 1))
        // .fluidInputs(new FluidStack(water, 123123))
        // .itemOutputs(GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 1))
        // .noOptimize()
        // .eut(123123)
        // .duration(8100)
        // .addTo(GT_RecipeConstants.AssemblyLine);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Field_Generator_EV.get(1),
            (int) TierEU.UV,
            114,
            (int) TierEU.UV,
            16,
            new ItemStack[] { GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 2),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3),
                GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 3) },
            new FluidStack[] { new FluidStack(water, 123123), new FluidStack(water, 123123),
                new FluidStack(water, 123123), new FluidStack(water, 123123) },
            GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 1),
            114514,
            1919810);
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 31027),
            (int) 409600000,
            32000,
            (int) 123123123,
            514,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 1004),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 13532),
                GT_ModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 32),
                GT_ModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 32),
                GT_ModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 32),
                GT_ModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 32),
                GT_ModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 32),
                GT_ModHandler.getModItem("eternalsingularity", "eternal_singularity", 64),
                ItemList.Field_Generator_UMV.get(64), GT_ModHandler.getModItem("miscutils", "milledMonazite", 64),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2083),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2092),
                GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 64),
                GT_ModHandler.getModItem("123Technology", "LookNEIdust", 64) },
            new FluidStack[] { new FluidStack(white, 4608), new FluidStack(cuihuaji, 123123),
                new FluidStack(Ewater, 4096000), new FluidStack(songyou, 32000000) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 20000),
            105000,
            123123123);
        // IsaBee
        GT_Values.RA.stdBuilder()
            .itemInputs(
                GT_ModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32140),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31027),
                GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31028),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139))
            .itemOutputs(GT_ModHandler.getModItem("123Technology", "IsaBee", 1))
            .fluidInputs(new FluidStack(songyou, 256000))
            .noOptimize()
            .duration(500 * 20)
            .eut(Integer.MAX_VALUE)
            .specialValue(3)
            .addTo(Nan);
        // Mega QFT
        TT_recipeAdder.addResearchableAssemblylineRecipe(
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 31151),
            512000000,
            4096,
            123123123,
            256,
            new ItemStack[] { GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 64, 31151),
                GT_ModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 64, 10),
                GT_ModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 64, 14),
                GT_ModHandler.getModItem("dreamcraft", "item.QuantumCircuit", 64),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417),
                GT_ModHandler.getModItem("dreamcraft", "item.MysteriousCrystalLens", 64),
                GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 64),
                GT_ModHandler.getModItem("123Technology", "LookNEIdust", 64),
                GT_ModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GT_ModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GT_ModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
                GT_ModHandler.getModItem("miscutils", "MU-metaitem.01", 64, 32105),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32429),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32428),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32427),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4581), },
            new FluidStack[] { new FluidStack(FluidRegistry.getFluidID("molten.spacetime"), 36000000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 256000),
                new FluidStack(FluidRegistry.getFluidID("plasma.neptunium"), 256000),
                new FluidStack(FluidRegistry.getFluidID("plasma.fermium"), 256000) },
            GT_ModHandler.getModItem("gregtech", "gt.blockmachines", 1, 20005),
            1234 * 20,
            123123123);

    }
}
