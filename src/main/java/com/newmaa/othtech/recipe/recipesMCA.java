package com.newmaa.othtech.recipe;

import static bartworks.API.recipe.BartWorksRecipeMaps.circuitAssemblyLineRecipes;
import static com.newmaa.othtech.common.recipemap.Recipemaps.MCA;
import static com.newmaa.othtech.utils.Utils.setStackSize;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import bartworks.system.material.CircuitGeneration.BWMetaItems;
import bartworks.util.BWUtil;
import bartworks.util.Pair;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;

public class recipesMCA {

    public static final ArrayListMultimap<NBTTagCompound, GTRecipe> recipeTagMap = ArrayListMultimap.create();
    public static final HashBiMap<Short, ItemList> circuitIIconRefs = HashBiMap.create(20);
    public static final HashSet<ItemStack> blacklistSet = new HashSet<>();
    private static final HashSet<GTRecipe> ORIGINAL_CAL_RECIPES = new HashSet<>();
    private static final HashSet<GTRecipe> MODIFIED_CAL_RECIPES = new HashSet<>();
    private static final List<Pair<ItemStack, ItemStack>> circuitPartsToReplace = Collections.unmodifiableList(
        Arrays.asList(
            new Pair<>(ItemList.Circuit_Parts_Resistor.get(1), ItemList.Circuit_Parts_ResistorSMD.get(1)),
            new Pair<>(ItemList.Circuit_Parts_Diode.get(1), ItemList.Circuit_Parts_DiodeSMD.get(1)),
            new Pair<>(ItemList.Circuit_Parts_Transistor.get(1), ItemList.Circuit_Parts_TransistorSMD.get(1)),
            new Pair<>(ItemList.Circuit_Parts_Capacitor.get(1), ItemList.Circuit_Parts_CapacitorSMD.get(1)),
            new Pair<>(ItemList.Circuit_Parts_Coil.get(1), ItemList.Circuit_Parts_InductorSMD.get(1))));

    private static void reAddOriginalRecipes() {
        circuitAssemblyLineRecipes.getBackend()
            .removeRecipes(MODIFIED_CAL_RECIPES);
        ORIGINAL_CAL_RECIPES.forEach(circuitAssemblyLineRecipes::add);
        ORIGINAL_CAL_RECIPES.clear();
        MODIFIED_CAL_RECIPES.clear();
    }

    private static void rebuildCircuitAssemblerMap(HashSet<GTRecipe> toRem, HashSet<GTRecipe> toAdd) {
        reAddOriginalRecipes();
        circuitAssemblyLineRecipes.getAllRecipes()
            .forEach(e -> handleCircuitRecipeRebuilding(e, toRem, toAdd));
    }

    private static void handleCircuitRecipeRebuilding(GTRecipe circuitRecipe, HashSet<GTRecipe> toRem,
        HashSet<GTRecipe> toAdd) {
        ItemStack[] outputs = circuitRecipe.mOutputs;
        boolean isOrePass = isCircuitOreDict(outputs[0]);
        String unlocalizedName = outputs[0].getUnlocalizedName();
        if (isOrePass || unlocalizedName.contains("Circuit") || unlocalizedName.contains("circuit")) {

            recipeTagMap.put(getTagFromStack(outputs[0]), circuitRecipe.copy());

            Fluid solderIndalloy = FluidRegistry.getFluid("molten.indalloy140") != null
                ? FluidRegistry.getFluid("molten.indalloy140")
                : FluidRegistry.getFluid("molten.solderingalloy");

            Fluid solderUEV = FluidRegistry.getFluid("molten.mutatedlivingsolder") != null
                ? FluidRegistry.getFluid("molten.mutatedlivingsolder")
                : FluidRegistry.getFluid("molten.solderingalloy");

            if (circuitRecipe.mFluidInputs[0].isFluidEqual(Materials.SolderingAlloy.getMolten(0))
                || circuitRecipe.mFluidInputs[0].isFluidEqual(new FluidStack(solderIndalloy, 0))
                || circuitRecipe.mFluidInputs[0].isFluidEqual(new FluidStack(solderUEV, 0))) {
                GTRecipe newRecipe = reBuildRecipe(circuitRecipe);
                if (newRecipe != null) MCA.addRecipe(newRecipe);
            } else if (circuitRecipe.mEUt > TierEU.IV) toRem.add(circuitRecipe);
        }
    }

    private static void replaceCircuits(BiMap<ItemList, Short> inversed, GTRecipe original, ItemStack[] in, int index) {
        for (ItemList il : inversed.keySet()) {
            if (GTUtility.areStacksEqual(il.get(1), replaceCircuitParts(original.mInputs[index]))) {
                in[index] = BWMetaItems.getCircuitParts()
                    .getStack(inversed.get(il), original.mInputs[index].stackSize);
            }
        }
    }

    private static ItemStack replaceCircuitParts(ItemStack stack) {
        for (Pair<ItemStack, ItemStack> pair : circuitPartsToReplace) {
            if (GTUtility.areStacksEqual(pair.getKey(), stack)) {
                ItemStack newStack = pair.getValue();
                newStack.stackSize = stack.stackSize;
                return newStack;
            }
        }
        return stack;
    }

    public static GTRecipe reBuildRecipe(GTRecipe original) {
        ItemStack[] in = new ItemStack[6];
        BiMap<ItemList, Short> inversed = circuitIIconRefs.inverse();
        for (int i = 0; i < 6; i++) {
            try {
                replaceCircuits(inversed, original, in, i);
                replaceComponents(in, original, i);
            } catch (ArrayIndexOutOfBoundsException e) {
                break;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        if (checkForBlacklistedComponents(in)) {
            return null;
        }

        return new GTRecipe(
            false,
            in,
            new ItemStack[] { getOutputMultiplied(original) },
            null,
            null,
            original.mFluidInputs,
            null,
            original.mDuration,
            original.mEUt,
            0);
    }

    private static boolean checkForBlacklistedComponents(ItemStack[] itemStacks) {
        for (ItemStack is : itemStacks) {
            for (ItemStack is2 : blacklistSet) {
                if (GTUtility.areStacksEqual(is, is2)) return true;
            }
        }
        return false;
    }

    private static ItemStack getOutputMultiplied(GTRecipe original) {
        ItemStack out = original.copy()
            .getOutput(0);
        return out;
    }

    public static NBTTagCompound getTagFromStack(ItemStack stack) {
        if (GTUtility.isStackValid(stack)) return setStackSize(stack.copy(), 1).writeToNBT(new NBTTagCompound());
        return new NBTTagCompound();
    }

    private static void exchangeRecipesInList(HashSet<GTRecipe> toRem, HashSet<GTRecipe> toAdd) {
        toAdd.forEach(circuitAssemblyLineRecipes::add);
        circuitAssemblyLineRecipes.getBackend()
            .removeRecipes(toRem);
        ORIGINAL_CAL_RECIPES.addAll(toRem);
        MODIFIED_CAL_RECIPES.addAll(toAdd);
    }

    private static boolean isCircuitOreDict(ItemStack item) {
        return BWUtil.isTieredCircuit(item) || BWUtil.getOreNames(item)
            .stream()
            .anyMatch(s -> "circuitPrimitiveArray".equals(s));
    }

    private static void deleteCALRecipesAndTags() {
        MCA.getBackend()
            .clearRecipes();
        recipeTagMap.clear();
    }

    private static void replaceComponents(ItemStack[] in, GTRecipe original, int index)
        throws ArrayIndexOutOfBoundsException {
        if (original.mInputs[index] != null && in[index] == null) {
            // big wires
            if (BWUtil.checkStackAndPrefix(original.mInputs[index])
                && GTOreDictUnificator.getAssociation(original.mInputs[index]).mPrefix == OrePrefixes.wireGt01) {
                in[index] = GTOreDictUnificator.get(
                    OrePrefixes.wireGt16,
                    GTOreDictUnificator.getAssociation(original.mInputs[index]).mMaterial.mMaterial,
                    original.mInputs[index].stackSize);
                // fine wires
            } else if (BWUtil.checkStackAndPrefix(original.mInputs[index])
                && GTOreDictUnificator.getAssociation(original.mInputs[index]).mPrefix == OrePrefixes.wireFine) {
                    in[index] = GTOreDictUnificator.get(
                        OrePrefixes.wireGt04,
                        GTOreDictUnificator.getAssociation(original.mInputs[index]).mMaterial.mMaterial,
                        original.mInputs[index].stackSize);
                    if (in[index] == null) {
                        in[index] = GTOreDictUnificator.get(
                            OrePrefixes.wireFine,
                            GTOreDictUnificator.getAssociation(original.mInputs[index]).mMaterial.mMaterial,
                            original.mInputs[index].stackSize * 16);
                    }
                    // other components
                } else {
                    in[index] = original.mInputs[index].copy();
                    in[index].stackSize *= 16;
                    if (in[index].stackSize > in[index].getItem()
                        .getItemStackLimit() || in[index].stackSize > in[index].getMaxStackSize())
                        in[index].stackSize = in[index].getMaxStackSize();
                }
        }
    }

    public void loadRecipes() {
        HashSet<GTRecipe> toRem = new HashSet<>();
        HashSet<GTRecipe> toAdd = new HashSet<>();
        deleteCALRecipesAndTags();
        rebuildCircuitAssemblerMap(toRem, toAdd);
        exchangeRecipesInList(toRem, toAdd);
    }
}
