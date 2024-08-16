package com.newmaa.othtech.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.github.technus.tectech.recipe.TT_recipeAdder;

import goodgenerator.api.recipe.GoodGeneratorRecipeMaps;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.util.GT_ModHandler;

public class MAXs implements IRecipePool {

    @Override
    public void loadRecipes() {

        Fluid UEVsc = FluidRegistry.getFluid("molten.superconductoruevbase");
        Fluid UIVsc = FluidRegistry.getFluid("molten.superconductoruivbase");
        Fluid UMVsc = FluidRegistry.getFluid("molten.superconductorumvbase");
        Fluid quark = FluidRegistry.getFluid("quarkgluonplasma");

        final IRecipeMap comass = GoodGeneratorRecipeMaps.componentAssemblyLineRecipes;

        TT_recipeAdder.addResearchableAssemblylineRecipe(
            ItemList.Electric_Motor_UXV.get(1),
            (int) TierEU.UHV,
            2048,
            (int) TierEU.UMV,
            123,
            new ItemStack[] { GT_ModHandler.getModItem("eternalsingularity", "combined_singularity", 1),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 22139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.01", 8, 28139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 32, 25139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 19143),
                GT_ModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4139),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4585),
                GT_ModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4586), },
            new FluidStack[] { new FluidStack(quark, 16000), new FluidStack(UEVsc, 5760), new FluidStack(UIVsc, 5760),
                new FluidStack(UMVsc, 5760) },
            ItemList.Electric_Motor_MAX.get(1),
            2460,
            512000000);
    }
}
