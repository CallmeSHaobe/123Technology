package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class RecipesEpicCokeOvenFake implements IRecipePool {

    public void loadRecipes() {
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 1024))
            .duration(0)
            .eut(0)
            .addTo(Recipemaps.COC);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(new ItemStack(Items.diamond, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 1024))
            .duration(0)
            .eut(0)
            .addTo(Recipemaps.COC);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 16))
            .duration(0)
            .eut(0)
            .addTo(Recipemaps.COC);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(new ItemStack(Items.coal, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 16))
            .duration(0)
            .eut(0)
            .addTo(Recipemaps.COC);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(new ItemStack(Blocks.dirt, 1))
            .itemOutputs(setStackSize(GTModHandler.getModItem("Railcraft", "fuel.coke", 1), 1))
            .duration(0)
            .eut(0)
            .addTo(Recipemaps.COC);

    }
}
