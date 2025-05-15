package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.util.GTRecipeConstants.DEFC_CASING_TIER;
import static kubatech.loaders.DEFCRecipes.fusionCraftingRecipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.utils.modsEnum;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeBuilder;

public class RecipesDE implements IRecipePool {

    @Override
    public void loadRecipes() {
        // Dragon Egg
        GTRecipeBuilder.builder()
            .itemInputs(
                new ItemStack(Blocks.dragon_egg, 0),
                new ItemStack(Items.egg, 1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Enderium, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnderEye, 32))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("biohmediumsterilized"), 4000))
            .itemOutputs(new ItemStack(Blocks.dragon_egg, 1))
            .eut(TierEU.RECIPE_UV)
            .duration(1280)
            .metadata(DEFC_CASING_TIER, 1)
            .addTo(fusionCraftingRecipes);
        // Ancient Dragon Egg
        GTRecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(String.valueOf(modsEnum.NHUtilities), "AncientDragonEgg", 0),
                new ItemStack(Items.egg, 8),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.DraconiumAwakened, 4))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("biohmediumsterilized"), 2000))
            .itemOutputs(GTModHandler.getModItem(String.valueOf(modsEnum.NHUtilities), "AncientDragonEgg", 1))
            .eut(TierEU.RECIPE_UV)
            .duration(2560)
            .metadata(DEFC_CASING_TIER, 1)
            .addTo(fusionCraftingRecipes);
        // Chaos Dragon Egg
        GTRecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem(String.valueOf(modsEnum.NHUtilities), "ChaosDragonEgg", 0),
                new ItemStack(Items.egg, 16),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 4))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("biohmediumsterilized"), 1000))
            .itemOutputs(GTModHandler.getModItem(String.valueOf(modsEnum.NHUtilities), "ChaosDragonEgg", 1))
            .eut(TierEU.RECIPE_UV)
            .duration(5120)
            .metadata(DEFC_CASING_TIER, 1)
            .addTo(fusionCraftingRecipes);
        // DANji
        GTRecipeBuilder.builder()
            .itemInputsUnsafe(
                setStackSize(new ItemStack(Items.egg), 1919810),
                ItemList.Field_Generator_UXV.get(16),
                ItemList.Field_Generator_UMV.get(16),
                ItemList.Field_Generator_UIV.get(16),
                ItemList.Field_Generator_UEV.get(16),
                ItemList.Field_Generator_UHV.get(16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 1), 262144),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 1), 114514))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 25011))
            .eut(123123)
            .duration(123123 * 20)
            .metadata(DEFC_CASING_TIER, 4)
            .addTo(fusionCraftingRecipes);

    }
}
