package com.newmaa.othtech.recipe;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gtPlusPlus.api.objects.Logger;
import gtPlusPlus.core.recipe.common.CI;
import gtPlusPlus.core.util.minecraft.ItemUtils;
import gtPlusPlus.core.util.recipe.GTRecipeUtils;

public class recipesEXH implements IRecipePool {

    int mEut = 0;
    // public void EBFRecipesCheck() {
    // for (GTRecipe recipe : RecipeMaps.vacuumFreezerRecipes.getAllRecipes()) {
    // ItemStack mIp;
    // ItemStack mOp;
    // this.mEut = recipe.mEUt;
    // addExtremeHeatExchangerRecipe(
    // recipe.mInputs,
    // recipe.mOutputs,
    // FluidRegistry.getFluidStack("water", mEut / 1000 / 2 / 2),
    // FluidRegistry.getFluidStack("densesupercriticalsteam", mEut / 1000 / 2)
    // );
    // }
    // }
    private static ItemStack mEmptyCell;
    private static final ArrayList<ItemStack> mItemsToIgnore = new ArrayList<>();
    private static boolean mInit = false;

    private static void init() {
        if (!mInit) {
            mInit = true;
            mItemsToIgnore.add(
                ItemUtils.simpleMetaStack(
                    CI.emptyCells(1)
                        .getItem(),
                    8,
                    1));
        }
    }

    private static boolean doesItemMatchIgnoringStackSize(ItemStack a, ItemStack b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.getItem() == b.getItem()) {
            return a.getItemDamage() == b.getItemDamage();
        }
        return false;
    }

    private static boolean isEmptyCell(ItemStack aCell) {
        if (aCell == null) {
            return false;
        }
        if (mEmptyCell == null) {
            mEmptyCell = CI.emptyCells(1);
        }
        if (mEmptyCell != null) {
            ItemStack aTempStack = mEmptyCell.copy();
            aTempStack.stackSize = aCell.stackSize;
            return GTUtility.areStacksEqual(aTempStack, aCell);
        }
        return false;
    }

    private static synchronized FluidStack getFluidFromItemStack(final ItemStack ingot) {
        if (ingot == null) {
            return null;
        }
        return GTUtility.getFluidForFilledItem(ingot, true);
    }

    public static synchronized int generateEXHRecipes(RecipeMap<?> aInputs, RecipeMap<?> aOutputs) {
        init();
        int aRecipesHandled = 0;
        int aInvalidRecipesToConvert = 0;
        int aOriginalCount = aInputs.getAllRecipes()
            .size();
        ArrayList<GTRecipe> deDuplicationInputArray = new ArrayList<>();

        recipe: for (GTRecipe x : aInputs.getAllRecipes()) {
            if (x != null && x.mInputs != null && x.mOutputs != null) {

                ItemStack[] aInputItems = x.mInputs.clone();
                ItemStack[] aOutputItems = x.mOutputs.clone();

                ArrayList<ItemStack> aInputItemsMap = new ArrayList<>();
                ArrayList<ItemStack> aOutputItemsMap = new ArrayList<>();

                // Iterate Inputs, Convert valid items into fluids
                for (ItemStack aInputStack : aInputItems) {
                    FluidStack aFoundFluid = getFluidFromItemStack(aInputStack);
                    if (aFoundFluid == null) {
                        for (ItemStack aBadStack : mItemsToIgnore) {
                            if (doesItemMatchIgnoringStackSize(aInputStack, aBadStack)) {
                                continue recipe; // Skip this recipe entirely if we find an item we don't like
                            }
                        }
                        if (!isEmptyCell(aInputStack)) {
                            aInputItemsMap.add(aInputStack);
                        }
                    }
                }
                // Iterate Outputs, Convert valid items into fluids
                for (ItemStack aOutputStack : aOutputItems) {
                    FluidStack aFoundFluid = getFluidFromItemStack(aOutputStack);
                    if (aFoundFluid == null) {
                        for (ItemStack aBadStack : mItemsToIgnore) {
                            if (doesItemMatchIgnoringStackSize(aOutputStack, aBadStack)) {
                                continue recipe; // Skip this recipe entirely if we find an item we don't like
                            }
                        }
                        if (!isEmptyCell(aOutputStack)) {
                            aOutputItemsMap.add(aOutputStack);
                        }
                    }
                }

                // Make some new Arrays
                ItemStack[] aNewItemInputs = new ItemStack[aInputItemsMap.size()];
                ItemStack[] aNewItemOutputs = new ItemStack[aOutputItemsMap.size()];

                // Add AutoMap contents to Arrays
                for (int i = 0; i < aInputItemsMap.size(); i++) {
                    aNewItemInputs[i] = aInputItemsMap.get(i);
                }
                for (int i = 0; i < aOutputItemsMap.size(); i++) {
                    aNewItemOutputs[i] = aOutputItemsMap.get(i);
                }

                if (!ItemUtils.checkForInvalidItems(aNewItemInputs, aNewItemOutputs)) {
                    aInvalidRecipesToConvert++;
                    continue; // Skip this recipe entirely if we find an item we don't like
                }
                GTRecipe aNewRecipe = new GTRecipe(
                    false,
                    aNewItemInputs,
                    aNewItemOutputs,
                    x.mSpecialItems,
                    new int[] { 8888 },
                    new FluidStack[] { FluidRegistry
                        .getFluidStack("water", (int) Math.max((double) (x.mEUt * x.mDuration) / 1000 / 2, 2)) },
                    new FluidStack[] { FluidRegistry.getFluidStack(
                        "densesupercriticalsteam",
                        (int) Math.max((double) (x.mEUt * x.mDuration) / 1000 / 2 / 2, 1)) },
                    x.mDuration,
                    0,
                    x.mSpecialValue);
                aNewRecipe.owners = new ArrayList<>(x.owners);

                // add all recipes to an intermediate array
                deDuplicationInputArray.add(aNewRecipe);

                aRecipesHandled++;
            } else {
                aInvalidRecipesToConvert++;
            }
        }
        // cast arraylist of input to a regular array and pass it to a duplicate recipe remover.
        List<GTRecipe> deDuplicationOutputArray = GTRecipeUtils
            .removeDuplicates(deDuplicationInputArray, aOutputs.unlocalizedName);
        // add each recipe from the above output to the intended recipe map
        for (GTRecipe recipe : deDuplicationOutputArray) {
            aOutputs.add(recipe);
        }
        Logger.INFO("Generated Recipes for " + aOutputs.unlocalizedName);
        Logger.INFO("Original Map contains " + aOriginalCount + " recipes.");
        Logger.INFO("Output Map contains " + aRecipesHandled + " recipes.");
        Logger.INFO("There were " + aInvalidRecipesToConvert + " invalid recipes.");
        return aRecipesHandled;
    }

    @Override
    public void loadRecipes() {
        // EBFRecipesCheck();
    }
}
