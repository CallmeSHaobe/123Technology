package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.Utils.RecipeBuilder;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class recipesEpicCokeOvenFake implements IRecipePool {

    public void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 1024))
            .addTo(Recipemaps.COC);
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.diamond, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 1024))
            .addTo(Recipemaps.COC);
        RecipeBuilder.builder()
            .itemInputs(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 16))
            .addTo(Recipemaps.COC);
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Items.coal, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 16))
            .addTo(Recipemaps.COC);
        RecipeBuilder.builder()
            .itemInputs(new ItemStack(Blocks.dirt, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 1))
            .addTo(Recipemaps.COC);

    }
}
