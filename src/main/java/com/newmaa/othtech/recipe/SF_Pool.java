package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.Utils.recipes.RecipeBuilder;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;

public class SF_Pool implements IRecipePool {

    public void loadRecipes() {
        RecipeMap<?> SF = Recipemaps.TSSF;
        // -----Of Fe
        // Fe50C
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 256000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 5120),
                GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1), 256000))
            .fluidInputs(FluidRegistry.getFluidStack("oxygen", 25600))
            .noOptimize()
            .specialValue(3200)
            .eut(7680)
            .duration(4000 * 20)
            .addTo(SF);
        // ele Fe
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                GT_Utility.getIntegratedCircuit(15))
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.ConductiveIron, 1), 64000 * 9))
            .noOptimize()
            .specialValue(3000)
            .eut(480)
            .duration(512 * 120 * 20 * 9)
            .addTo(SF);
        // pul Fe
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.EnderPearl, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                GT_Utility.getIntegratedCircuit(15))
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.PulsatingIron, 1), 64000 * 9))
            .noOptimize()
            .specialValue(4400)
            .eut(480)
            .duration(512 * 160 * 20 * 9)
            .addTo(SF);
        // Fe Mix
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 64000),
                GT_Utility.getIntegratedCircuit(3))
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.NickelZincFerrite, 1), 64000))
            .noOptimize()
            .specialValue(3000)
            .eut(480)
            .duration(512 * 30 * 20)
            .addTo(SF);
        // Invar
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000),
                GT_Utility.getIntegratedCircuit(1))
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Invar, 1), 64000 * 3))
            .noOptimize()
            .specialValue(1000)
            .eut(480)
            .duration(512 * 10 * 20 * 3)
            .addTo(SF);
        // Eglin
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 23),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 12),
                GT_Utility.getIntegratedCircuit(6))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotEglinSteel", 1), 64000 * 48))
            .noOptimize()
            .specialValue(1600)
            .eut(480)
            .duration(512 * 45 * 20 * 48)
            .addTo(SF);
        // Kant
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000),
                GT_Utility.getIntegratedCircuit(1))
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Kanthal, 1), 64000 * 3))
            .noOptimize()
            .specialValue(3600)
            .eut(1920)
            .duration(512 * 60 * 20 * 3)
            .addTo(SF);
        // Stainless
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 6),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000),
                GT_Utility.getIntegratedCircuit(2))
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.StainlessSteel, 1), 64000 * 9))
            .noOptimize()
            .specialValue(3400)
            .eut(1920)
            .duration(512 * 85 * 20 * 9)
            .addTo(SF);
        // DS
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 23),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 9),
                GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotIncoloyDS", 1), 64000 * 50))
            .noOptimize()
            .specialValue(5400)
            .eut(1920)
            .duration(512 * 37 * 20 * 50)
            .addTo(SF);
        // 020
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 10),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 5),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 9),
                GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotIncoloy020", 1), 64000 * 25))
            .noOptimize()
            .specialValue(5400)
            .eut(1920)
            .duration(512 * 37 * 20 * 25)
            .addTo(SF);
        // MA956
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 16),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 5),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Yttrium, 1), 64000),
                GT_Utility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotIncoloyMA956", 1), 64000 * 25))
            .noOptimize()
            .specialValue(6300)
            .eut(7680)
            .duration(512 * 50 * 20 * 25)
            .addTo(SF);
        // HastW
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 31),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 12),
                GT_Utility.getIntegratedCircuit(5))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotHastelloyW", 1), 64000 * 50))
            .noOptimize()
            .specialValue(5400)
            .eut(1920)
            .duration(512 * 20 * 20 * 50)
            .addTo(SF);
        // HastX
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 11),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 24),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 4),
                GT_Utility.getIntegratedCircuit(6))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotHastelloyX", 1), 64000 * 50))
            .noOptimize()
            .specialValue(5400)
            .eut(1920)
            .duration(512 * 20 * 20 * 50)
            .addTo(SF);
        // EnergeticSilver
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 309),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 15 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 5 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000 * 25),
                GT_Utility.getIntegratedCircuit(17))
            .itemOutputs(
                setStackSize(
                    GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.EnergeticSilver, 1),
                    64000 * 27 * 25))
            .noOptimize()
            .specialValue(3600)
            .duration(512 * 160 * 20 * 27 * 25)
            .eut(480)
            .addTo(SF);
        // EnergeticAlloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 84),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 234),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 15 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 5 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000 * 25),
                GT_Utility.getIntegratedCircuit(17))
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.EnergeticAlloy, 1), 64000 * 27 * 25))
            .noOptimize()
            .specialValue(3600)
            .duration(512 * 160 * 20 * 27 * 25)
            .eut(480)
            .addTo(SF);
        // VividAlloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 27 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.EnderEye, 1), 64000 * 27 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 309),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 15 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 5 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000 * 25),
                GT_Utility.getIntegratedCircuit(18))
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.VividAlloy, 1), 64000 * 81 * 25))
            .noOptimize()
            .specialValue(3600)
            .duration(512 * 100 * 20 * 81 * 25)
            .eut(480)
            .addTo(SF);
        // VibrantAlloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 27 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.EnderEye, 1), 64000 * 27 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 84),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 234),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 15 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 5 * 9),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000 * 25),
                GT_Utility.getIntegratedCircuit(18))
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.VibrantAlloy, 1), 64000 * 81 * 25))
            .noOptimize()
            .specialValue(3600)
            .duration(512 * 100 * 20 * 81 * 25)
            .eut(480)
            .addTo(SF);
        // Liuming
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10101), 5 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(19),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1), 20 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 20 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1), 30 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 2 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 8 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1), 40 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 10 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 5 * 64000))
            .noOptimize()
            .duration(512 * 36 * 20 * 5)
            .specialValue(10800)
            .eut(122880)
            .addTo(SF);
        // -----Of Fe50C
        // highDur
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 92), 297 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(19),
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 64), 18 * 64000),
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 36), 18 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 24 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 84 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 84 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 12 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 12 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 9 * 64000))
            .noOptimize()
            .duration(512 * 450 * 20 * 297)
            .specialValue(9900)
            .eut(30720)
            .addTo(SF);
        // HardSteel
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 96), 72 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(20),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 59 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 2 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 3 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 6 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000))
            .noOptimize()
            .duration(512 * 450 * 20 * 72)
            .specialValue(2000)
            .eut(7680)
            .addTo(SF);
        // MAR200
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10096), 59 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(21),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 5 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 2 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 10 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 18 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 1), 2 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 13 * 64000))
            .noOptimize()
            .duration(512 * 5 * 20 * 59)
            .specialValue(6300)
            .eut(30720)
            .addTo(SF);
        // MARCe
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10097), 1121 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(19),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 5 * 64000 * 59),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1), 64000 * 59),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 2 * 64000 * 59),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 9 * 64000 * 59),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 10 * 64000 * 59),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 18 * 64000 * 59),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 1), 2 * 64000 * 59),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 13 * 64000 * 59))
            .noOptimize()
            .duration(512 * 95 * 20 * 1121)
            .specialValue(5400)
            .eut(TierEU.UV)
            .addTo(SF);
        // TungS
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.TungstenSteel, 1), 2 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(1),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000))
            .noOptimize()
            .duration(512 * 50 * 20 * 2)
            .specialValue(5400)
            .eut(TierEU.IV)
            .addTo(SF);
        // BlackSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.BlackSteel, 1), 25 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(14),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 5 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 15 * 64000))
            .noOptimize()
            .duration(512 * 60 * 20 * 25)
            .specialValue(2400)
            .eut(TierEU.HV)
            .addTo(SF);
        // Damascus
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.DamascusSteel, 1), 18 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(18),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 6 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000))
            .noOptimize()
            .duration(512 * 60 * 20 * 18)
            .specialValue(2400)
            .eut(TierEU.HV)
            .addTo(SF);
        // RedSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.RedSteel, 1), 40 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(18),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 4 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 4 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 10 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.BlackSteel, 1), 20 * 64000))
            .noOptimize()
            .duration(512 * 65 * 20 * 40)
            .specialValue(2400)
            .eut(TierEU.HV)
            .addTo(SF);
        // BlueSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.BlueSteel, 1), 160 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(18),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 19 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 5 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 16 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 40 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.BlackSteel, 1), 80 * 64000))
            .noOptimize()
            .duration(512 * 70 * 20 * 160)
            .specialValue(2400)
            .eut(TierEU.HV)
            .addTo(SF);
        // DarkSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.DarkSteel, 1), 9 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(14),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 4 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1), 3 * 64000))
            .noOptimize()
            .duration(512 * 100 * 20 * 9)
            .specialValue(3000)
            .eut(TierEU.HV)
            .addTo(SF);
        // EleSteel
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.ElectricalSteel, 1), 3 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000))
            .noOptimize()
            .duration(512 * 60 * 20 * 3)
            .specialValue(3000)
            .eut(TierEU.HV)
            .addTo(SF);
        // VanSteel
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.VanadiumSteel, 1), 9 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 7 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000))
            .noOptimize()
            .duration(512 * 60 * 20 * 9)
            .specialValue(3000)
            .eut(TierEU.HV)
            .addTo(SF);
        // EnderSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.EndSteel, 1), 27 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(16),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 4 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1), 3 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 1), 9 * 64000))
            .noOptimize()
            .duration(512 * 20 * 20 * 27)
            .specialValue(5120)
            .eut(TierEU.IV)
            .addTo(SF);
        // Alumite
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Alumite, 1), 9 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 5 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 2 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1), 2 * 64000))
            .noOptimize()
            .duration(512 * 20 * 20 * 9)
            .specialValue(1000)
            .eut(TierEU.HV)
            .addTo(SF);
        // Melodic
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.MelodicAlloy, 1), 81 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(18),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 1), 27 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.EnderEye, 1), 27 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 4 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1), 3 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 1), 9 * 64000))
            .noOptimize()
            .duration(512 * 50 * 20 * 81)
            .specialValue(6400)
            .eut(TierEU.IV)
            .addTo(SF);
        // Ma 250
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 16),
                GT_Utility.getIntegratedCircuit(9))
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotMaragingSteel250", 1), 64000 * 24))
            .noOptimize()
            .specialValue(3200)
            .eut(480)
            .duration(512 * 25 * 20 * 24)
            .addTo(SF);
        // Ma 350
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 16),
                GT_Utility.getIntegratedCircuit(5))
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotMaragingSteel350", 1), 64000 * 24))
            .noOptimize()
            .specialValue(3200)
            .eut(480)
            .duration(512 * 20 * 20 * 24)
            .addTo(SF);
        // Ma 300
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 16),
                GT_Utility.getIntegratedCircuit(5))
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotMaragingSteel300", 1), 64000 * 24))
            .noOptimize()
            .specialValue(3200)
            .eut(480)
            .duration(512 * 20 * 20 * 24)
            .addTo(SF);
        // WaterTightSteel
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 12),
                GT_Utility.getIntegratedCircuit(7))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotWatertightSteel", 1), 64000 * 20))
            .noOptimize()
            .specialValue(3200)
            .eut(480)
            .duration(512 * 25 * 20 * 24)
            .addTo(SF);
        // Zeron - 100
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 13),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 10),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 20),
                GT_Utility.getIntegratedCircuit(7))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotZeron100", 1), 64000 * 50))
            .noOptimize()
            .specialValue(7200)
            .eut(122880)
            .duration(512 * 360 * 20 * 50)
            .addTo(SF);
        // Titan Steel
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1), 64000 * 20),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 21),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 21),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 18),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 64000 * 20),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 64000 * 20),
                GT_Utility.getIntegratedCircuit(7))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotTitansteel", 1), 64000 * 120))
            .noOptimize()
            .specialValue(10000)
            .eut(TierEU.UHV)
            .duration(512 * 500 * 20 * 120)
            .addTo(SF);
        // EightSteel
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 30), 64000 * 240),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 105),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 120),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 8),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 8),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 90),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 1), 64000 * 85),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 64000 * 185),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 64000 * 185),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 1), 64000 * 85),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1), 64000 * 160),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedOrder, 1), 64000 * 60),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 40),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 24),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 105),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Thaumium, 1), 64000 * 500),
                GT_Utility.getIntegratedCircuit(22))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotOctiron", 1), 64000 * 2000))
            .noOptimize()
            .specialValue(10000)
            .eut(TierEU.UHV)
            .duration(512 * 104 * 20 * 2000)
            .addTo(SF);
        // HSSG
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.HSSG, 1), 18 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(18),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 2 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 2 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 4 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 5 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 5 * 64000))
            .noOptimize()
            .duration(512 * 36 * 20 * 18)
            .specialValue(6400)
            .eut(TierEU.IV)
            .addTo(SF);
        // HSSE
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.HSSE, 1), 81 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(19),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 6 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 6 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 12 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 15 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 15 * 64000))
            .noOptimize()
            .duration(512 * 56 * 20 * 81)
            .specialValue(7200)
            .eut(TierEU.IV)
            .addTo(SF);
        // HSSS
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.HSSS, 1), 81 * 64000))
            .itemInputs(
                GT_Utility.getIntegratedCircuit(19),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 18 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 9 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 6 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 6 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 12 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 15 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 15 * 64000))
            .noOptimize()
            .duration(512 * 67 * 20 * 81)
            .specialValue(7200)
            .eut(TierEU.IV)
            .addTo(SF);
        // EnrichedNq
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 39), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 1), 64000 * 8),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 64000 * 5),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 64000 * 2),
                GT_Utility.getIntegratedCircuit(5))
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10110), 64000 * 18))
            .noOptimize()
            .specialValue(12000)
            .eut(TierEU.UIV)
            .duration(512 * 54 * 20 * 18)
            .addTo(SF);
        // Goushi Alloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 64000),
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 64), 64000),
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 78), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 64000),
                GT_Utility.getIntegratedCircuit(5))
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10109), 64000 * 6))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UIV)
            .duration(512 * 54 * 20 * 6)
            .addTo(SF);
        // Ao SHI
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 30), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedOrder, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1), 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 1), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 1), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 64000 * 4),
                GT_Utility.getIntegratedCircuit(7))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotArcanite", 1), 64000 * 10))
            .noOptimize()
            .specialValue(6200)
            .eut(TierEU.LuV)
            .duration(512 * 300 * 20 * 10)
            .addTo(SF);
        // Pika 64B
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 1440),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 1440),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 1995),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 900),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 64000 * 1440),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 64000 * 225),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 1), 64000 * 1440),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 267),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 1725),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1), 64000 * 2160),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 3063),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 1), 64000 * 2880),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 225),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Ytterbium, 1), 64000 * 720),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 1), 64000 * 960),
                GT_Utility.getIntegratedCircuit(21))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotPikyonium64B", 1), 64000 * 20880))
            .noOptimize()
            .specialValue(10000)
            .eut(TierEU.UV)
            .duration(512 * 104 * 20 * 2000)
            .addTo(SF);
        // Quantum
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 10),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1), 64000 * 20),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 1), 64000 * 20),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 6),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 1), 64000 * 5),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 6400 * 5),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 21),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 64000 * 5),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000 * 12),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 1), 64000 * 5),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 21),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 1), 64000 * 20),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 1), 64000 * 20),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 10),
                setStackSize(GT_ModHandler.getModItem("miscutils", "itemDustGermanium", 1), 64000 * 20),
                GT_Utility.getIntegratedCircuit(21))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotQuantum", 1), 64000 * 200))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UIV)
            .duration(512 * 625 * 20 * 200)
            .addTo(SF);
        // A243
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 32),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 32),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 320),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 150),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 64000 * 75),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 6400 * 225),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1), 64000 * 100),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 600),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 208),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 48),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 160),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 1), 64000 * 350),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 1), 64000 * 250),
                setStackSize(GT_ModHandler.getModItem("Thaumcraft", "ItemResource", 1, 3), 64000 * 100),
                GT_Utility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotCinobiteA243", 1), 64000 * 2650))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UV)
            .duration(512 * 79 * 20 * 2650)
            .addTo(SF);
        // 2B
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 88),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 24),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 4),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 64000 * 6),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 6400 * 18),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 8),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 16),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 1), 64000 * 72),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Strontium, 1), 64000 * 24),
                GT_Utility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotCinobiteA243", 1), 64000 * 264))
            .noOptimize()
            .specialValue(9000)
            .eut(TierEU.UV)
            .duration(512 * 438 * 20 * 264)
            .addTo(SF);
        // Guangsu
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1), 360 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 360 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1), 540 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 36 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 184 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1), 720 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 180 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 90 * 64000),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 64000 * 20),
                GT_Utility.getIntegratedCircuit(20))
            .itemOutputs(
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10105), 150 * 64000))
            .noOptimize()
            .specialValue(9000)
            .eut(TierEU.ZPM)
            .duration(512 * 20 * 20 * 150)
            .addTo(SF);
        // Lufeien
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 16),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 50),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 160),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 75),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 1), 64000 * 50),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 1), 6400 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 25),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 8),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Yttrium, 1), 64000 * 8),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 8),
                GT_Utility.getIntegratedCircuit(15))
            .fluidInputs(FluidRegistry.getFluidStack("argon", 64000 * 25000))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotLafiumCompound", 1), 64000 * 450))
            .noOptimize()
            .specialValue(9000)
            .eut(TierEU.ZPM)
            .duration(512 * 375 * 20 * 450)
            .addTo(SF);
        // Botem
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GT_ModHandler.getModItem("miscutils", "itemDustThallium", 1), 64000 * 15),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 3),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 2),
                setStackSize(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 64000 * 30),
                setStackSize(GT_ModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 64), 30 * 64000),
                GT_Utility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GT_ModHandler.getModItem("miscutils", "itemIngotBotmium", 1), 64000 * 80))
            .noOptimize()
            .specialValue(1000)
            .eut(TierEU.UHV)
            .duration(512 * 500 * 20 * 80)
            .addTo(SF);
        // -----
    }
}
