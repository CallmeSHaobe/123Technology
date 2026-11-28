package com.newmaa.othtech.recipe;

import static gregtech.api.util.GTRecipeConstants.QFT_CATALYST;
import static gregtech.api.util.GTRecipeConstants.QFT_FOCUS_TIER;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IRecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.common.items.MetaGeneratedItem01;
import gtPlusPlus.core.item.base.BaseItemComponent.ComponentTypes;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;

public class RecipesQFTRecipes implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid songyou = FluidRegistry.getFluid("pineoil");
        Fluid Water = FluidRegistry.getFluid(FluidRegistry.WATER.getName());
        Fluid nijiang = FluidRegistry.getFluid("mud.red.slurry");
        Fluid laoda = FluidRegistry.getFluid(RecipeFluids.gtppMoltenName(MaterialsAlloy.HELICOPTER));

        final IRecipeMap qft = RecipeMaps.quantumForceTransformerRecipes;

        // ISA
        GTValues.RA.stdBuilder()
            .metadata(QFT_CATALYST, GregtechItemList.PlatinumGroupCatalyst.get(0))
            .metadata(QFT_FOCUS_TIER, 2)
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.Controller_IsaMill.ID),
                RecipeStacks.exact(0, ItemList.FlotationCell))
            .fluidInputs(new FluidStack(songyou, 123), new FluidStack(laoda, 24))
            .itemOutputs(
                RecipeStacks.gtppComponent(16, MaterialsElements.getInstance().RHENIUM, ComponentTypes.DUST),
                RecipeStacks.gtppComponent(16, MaterialsElements.getInstance().THALLIUM, ComponentTypes.DUST),
                RecipeStacks.gtppComponent(16, MaterialsElements.getInstance().SELENIUM, ComponentTypes.DUST),
                RecipeStacks.gtppComponent(16, MaterialsElements.getInstance().GERMANIUM, ComponentTypes.DUST),
                RecipeStacks.generated(16, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Strontium),
                RecipeStacks.generated(16, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Dysprosium))
            .fluidOutputs(new FluidStack(Water, 123123), new FluidStack(nijiang, 1919810))
            .outputChances(1667, 1667, 1667, 1667, 1667, 1667)
            .eut(123123123)
            .duration(123)
            .addTo(qft);
        // Steel leaf & IronWood
        GTValues.RA.stdBuilder()
            .metadata(QFT_CATALYST, GregtechItemList.PlatinumGroupCatalyst.get(0))
            .metadata(QFT_FOCUS_TIER, 1)
            .itemInputs(
                Materials.Iron.getDust(64),
                Materials.Steel.getDust(64),
                Materials.Wood.getDust(64),
                new ItemStack(Blocks.leaves, 64))
            .itemOutputs(Materials.IronWood.getDust(64), Materials.Steeleaf.getDust(64))
            .eut(123123123)
            .duration(114)
            .addTo(qft);

    }
}
