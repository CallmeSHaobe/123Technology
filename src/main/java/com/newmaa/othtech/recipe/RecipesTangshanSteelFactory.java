package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class RecipesTangshanSteelFactory implements IRecipePool {

    public void loadRecipes() {
        RecipeMap<?> SF = Recipemaps.TSSF;
        // -----Of Fe
        // Fe50C
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 256000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 5120),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1), 256000))
            .fluidInputs(FluidRegistry.getFluidStack("oxygen", 25600))
            .noOptimize()
            .specialValue(3200)
            .eut(7680)
            .duration(4000 * 20)
            .addTo(SF);
        // ele Fe
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                GTUtility.getIntegratedCircuit(15))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.ConductiveIron, 1), 64000 * 9))
            .noOptimize()
            .specialValue(3000)
            .eut(480)
            .duration(512 * 120 * 20 * 9)
            .addTo(SF);
        // pul Fe
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnderPearl, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                GTUtility.getIntegratedCircuit(15))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.PulsatingIron, 1), 64000 * 9))
            .noOptimize()
            .specialValue(4400)
            .eut(480)
            .duration(512 * 160 * 20 * 9)
            .addTo(SF);
        // Fe Mix
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 64000 * 4),
                GTUtility.getIntegratedCircuit(3))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.NickelZincFerrite, 1), 64000))
            .fluidInputs(Materials.Oxygen.getGas(8 * 64000))
            .noOptimize()
            .specialValue(3000)
            .eut(480)
            .duration(512 * 30 * 20)
            .addTo(SF);
        // Invar
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Invar, 1), 64000 * 3))
            .noOptimize()
            .specialValue(1000)
            .eut(480)
            .duration(512 * 10 * 20 * 3)
            .addTo(SF);
        // Eglin
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 12),
                GTUtility.getIntegratedCircuit(6))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotEglinSteel", 1), 64000 * 48))
            .noOptimize()
            .specialValue(1600)
            .eut(480)
            .duration(512 * 45 * 20 * 48)
            .addTo(SF);
        // Kant
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000),
                GTUtility.getIntegratedCircuit(1))
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Kanthal, 1), 64000 * 3))
            .noOptimize()
            .specialValue(3600)
            .eut(1920)
            .duration(512 * 60 * 20 * 3)
            .addTo(SF);
        // Stainless
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 6),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000),
                GTUtility.getIntegratedCircuit(2))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.StainlessSteel, 1), 64000 * 9))
            .noOptimize()
            .specialValue(3400)
            .eut(1920)
            .duration(512 * 85 * 20 * 9)
            .addTo(SF);
        // DS
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 9),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotIncoloyDS", 1), 64000 * 50))
            .noOptimize()
            .specialValue(5400)
            .eut(1920)
            .duration(512 * 37 * 20 * 50)
            .addTo(SF);
        // 020
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 10),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 5),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 9),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotIncoloy020", 1), 64000 * 25))
            .noOptimize()
            .specialValue(5400)
            .eut(1920)
            .duration(512 * 37 * 20 * 25)
            .addTo(SF);
        // MA956
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 5),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Yttrium, 1), 64000),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotIncoloyMA956", 1), 64000 * 25))
            .noOptimize()
            .specialValue(6300)
            .eut(7680)
            .duration(512 * 50 * 20 * 25)
            .addTo(SF);
        // HastW
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 31),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 12),
                GTUtility.getIntegratedCircuit(5))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotHastelloyW", 1), 64000 * 50))
            .noOptimize()
            .specialValue(5400)
            .eut(1920)
            .duration(512 * 20 * 20 * 50)
            .addTo(SF);
        // HastX
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 11),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 4),
                GTUtility.getIntegratedCircuit(6))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotHastelloyX", 1), 64000 * 50))
            .noOptimize()
            .specialValue(5400)
            .eut(1920)
            .duration(512 * 20 * 20 * 50)
            .addTo(SF);
        // EnergeticSilver
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 309),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 15 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 5 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000 * 25),
                GTUtility.getIntegratedCircuit(9))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.EnergeticSilver, 1), 64000 * 27 * 25))
            .noOptimize()
            .specialValue(3600)
            .duration(512 * 160 * 20 * 27 * 25)
            .eut(480)
            .addTo(SF);
        // EnergeticAlloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 84),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 234),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 15 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 5 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000 * 25),
                GTUtility.getIntegratedCircuit(17))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.EnergeticAlloy, 1), 64000 * 27 * 25))
            .noOptimize()
            .specialValue(3600)
            .duration(512 * 160 * 20 * 27 * 25)
            .eut(480)
            .addTo(SF);
        // VividAlloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 27 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnderEye, 1), 64000 * 27 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 309),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 15 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 5 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000 * 25),
                GTUtility.getIntegratedCircuit(23))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.VividAlloy, 1), 64000 * 81 * 25))
            .noOptimize()
            .specialValue(3600)
            .duration(512 * 100 * 20 * 81 * 25)
            .eut(480)
            .addTo(SF);
        // VibrantAlloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 27 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnderEye, 1), 64000 * 27 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 3 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 84),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 234),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 15 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 5 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000 * 25),
                GTUtility.getIntegratedCircuit(18))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.VibrantAlloy, 1), 64000 * 81 * 25))
            .noOptimize()
            .specialValue(3600)
            .duration(512 * 100 * 20 * 81 * 25)
            .eut(480)
            .addTo(SF);
        // Liuming
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10101), 5 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(19),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1), 20 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 20 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1), 30 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 2 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 8 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1), 40 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 10 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 5 * 64000))
            .noOptimize()
            .duration(512 * 36 * 20 * 5)
            .specialValue(10800)
            .eut(122880)
            .addTo(SF);
        // -----Of Fe50C
        // highDur
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 92), 297 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(11),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 64), 18 * 64000),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 36), 18 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 24 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 84 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 84 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 12 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 12 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 9 * 64000))
            .noOptimize()
            .duration(512 * 450 * 20 * 297)
            .specialValue(9900)
            .eut(30720)
            .addTo(SF);
        // HardSteel
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 96), 72 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 59 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 2 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 3 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 6 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000))
            .noOptimize()
            .duration(512 * 450 * 20 * 72)
            .specialValue(2000)
            .eut(7680)
            .addTo(SF);
        // MAR200
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10096), 59 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(21),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 5 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 2 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 10 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 18 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 1), 2 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 13 * 64000))
            .noOptimize()
            .duration(512 * 5 * 20 * 59)
            .specialValue(6300)
            .eut(30720)
            .addTo(SF);
        // MARCe
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10097), 1121 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(19),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 5 * 64000 * 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1), 64000 * 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 2 * 64000 * 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 9 * 64000 * 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 10 * 64000 * 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 18 * 64000 * 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 1), 2 * 64000 * 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 13 * 64000 * 59))
            .noOptimize()
            .duration(512 * 95 * 20 * 1121)
            .specialValue(5400)
            .eut(TierEU.UV)
            .addTo(SF);
        // TungS
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.TungstenSteel, 1), 2 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000))
            .noOptimize()
            .duration(512 * 50 * 20 * 2)
            .specialValue(5400)
            .eut(TierEU.IV)
            .addTo(SF);
        // BlackSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.BlackSteel, 1), 25 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(14),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 5 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 15 * 64000))
            .noOptimize()
            .duration(512 * 60 * 20 * 25)
            .specialValue(2400)
            .eut(TierEU.HV)
            .addTo(SF);
        // Damascus
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.DamascusSteel, 1), 18 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(18),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 6 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000))
            .noOptimize()
            .duration(512 * 60 * 20 * 18)
            .specialValue(2400)
            .eut(TierEU.HV)
            .addTo(SF);
        // RedSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.RedSteel, 1), 40 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(18),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 4 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 4 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 10 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackSteel, 1), 20 * 64000))
            .noOptimize()
            .duration(512 * 65 * 20 * 40)
            .specialValue(2400)
            .eut(TierEU.HV)
            .addTo(SF);
        // BlueSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.BlueSteel, 1), 160 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(18),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 19 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 5 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 16 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 40 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackSteel, 1), 80 * 64000))
            .noOptimize()
            .duration(512 * 70 * 20 * 160)
            .specialValue(2400)
            .eut(TierEU.HV)
            .addTo(SF);
        // DarkSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.DarkSteel, 1), 9 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(14),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 4 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1), 3 * 64000))
            .noOptimize()
            .duration(512 * 100 * 20 * 9)
            .specialValue(3000)
            .eut(TierEU.HV)
            .addTo(SF);
        // EleSteel
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.ElectricalSteel, 1), 3 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 64000))
            .noOptimize()
            .duration(512 * 60 * 20 * 3)
            .specialValue(3000)
            .eut(TierEU.HV)
            .addTo(SF);
        // VanSteel
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.VanadiumSteel, 1), 9 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 7 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000))
            .noOptimize()
            .duration(512 * 60 * 20 * 9)
            .specialValue(3000)
            .eut(TierEU.HV)
            .addTo(SF);
        // EnderSteel
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.EndSteel, 1), 27 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 4 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1), 3 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 1), 9 * 64000))
            .noOptimize()
            .duration(512 * 20 * 20 * 27)
            .specialValue(5120)
            .eut(TierEU.IV)
            .addTo(SF);
        // Alumite
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Alumite, 1), 9 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 5 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 2 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1), 2 * 64000))
            .noOptimize()
            .duration(512 * 20 * 20 * 9)
            .specialValue(1000)
            .eut(TierEU.HV)
            .addTo(SF);
        // Melodic
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.MelodicAlloy, 1), 81 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(18),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Oriharukon, 1), 27 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnderEye, 1), 27 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1), 4 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1), 3 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 1), 9 * 64000))
            .noOptimize()
            .duration(512 * 50 * 20 * 81)
            .specialValue(6400)
            .eut(TierEU.IV)
            .addTo(SF);
        // Ma 250
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 16),
                GTUtility.getIntegratedCircuit(9))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotMaragingSteel250", 1), 64000 * 24))
            .noOptimize()
            .specialValue(3200)
            .eut(480)
            .duration(512 * 25 * 20 * 24)
            .addTo(SF);
        // Ma 350
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 16),
                GTUtility.getIntegratedCircuit(5))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotMaragingSteel350", 1), 64000 * 24))
            .noOptimize()
            .specialValue(3200)
            .eut(480)
            .duration(512 * 20 * 20 * 24)
            .addTo(SF);
        // Ma 300
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 16),
                GTUtility.getIntegratedCircuit(5))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotMaragingSteel300", 1), 64000 * 24))
            .noOptimize()
            .specialValue(3200)
            .eut(480)
            .duration(512 * 20 * 20 * 24)
            .addTo(SF);
        // WaterTightSteel
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 12),
                GTUtility.getIntegratedCircuit(7))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotWatertightSteel", 1), 64000 * 20))
            .noOptimize()
            .specialValue(3200)
            .eut(480)
            .duration(512 * 25 * 20 * 24)
            .addTo(SF);
        // Zeron - 100
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 13),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 10),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 20),
                GTUtility.getIntegratedCircuit(7))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotZeron100", 1), 64000 * 50))
            .noOptimize()
            .specialValue(7200)
            .eut(122880)
            .duration(512 * 360 * 20 * 50)
            .addTo(SF);
        // Titan Steel
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1), 64000 * 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 21),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 21),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 18),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 64000 * 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 64000 * 20),
                GTUtility.getIntegratedCircuit(7))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotTitansteel", 1), 64000 * 120))
            .noOptimize()
            .specialValue(10000)
            .eut(TierEU.UHV)
            .duration(512 * 500 * 20 * 120)
            .addTo(SF);
        // EightSteel
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 30), 64000 * 240),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 105),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 120),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 64000 * 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 64000 * 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 90),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 1), 64000 * 85),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 64000 * 185),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 64000 * 185),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 1), 64000 * 85),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1), 64000 * 160),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedOrder, 1), 64000 * 60),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 105),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thaumium, 1), 64000 * 500),
                GTUtility.getIntegratedCircuit(22))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotOctiron", 1), 64000 * 2000))
            .noOptimize()
            .specialValue(10000)
            .eut(TierEU.UHV)
            .duration(512 * 104 * 20 * 2000)
            .addTo(SF);
        // HSSG
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.HSSG, 1), 18 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(18),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 2 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 2 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 4 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 5 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 5 * 64000))
            .noOptimize()
            .duration(512 * 36 * 20 * 18)
            .specialValue(6400)
            .eut(TierEU.IV)
            .addTo(SF);
        // HSSE
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.HSSE, 1), 81 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(19),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 6 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 6 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 12 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 15 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 15 * 64000))
            .noOptimize()
            .duration(512 * 56 * 20 * 81)
            .specialValue(7200)
            .eut(TierEU.IV)
            .addTo(SF);
        // HSSS
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, Materials.HSSS, 1), 81 * 64000))
            .itemInputs(
                GTUtility.getIntegratedCircuit(19),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 18 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 9 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Vanadium, 1), 6 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 6 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 12 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 15 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 15 * 64000))
            .noOptimize()
            .duration(512 * 67 * 20 * 81)
            .specialValue(7200)
            .eut(TierEU.IV)
            .addTo(SF);
        // EnrichedNq
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 39), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 1), 64000 * 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 64000 * 5),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 64000 * 2),
                GTUtility.getIntegratedCircuit(5))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10110), 64000 * 18))
            .noOptimize()
            .specialValue(12000)
            .eut(TierEU.UIV)
            .duration(512 * 54 * 20 * 18)
            .addTo(SF);
        // Goushi Alloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 64000),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 64), 64000),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 78), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 64000),
                GTUtility.getIntegratedCircuit(5))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10109), 64000 * 6))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UIV)
            .duration(512 * 54 * 20 * 6)
            .addTo(SF);
        // Ao SHI
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 30), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedOrder, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1), 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 64000 * 4),
                GTUtility.getIntegratedCircuit(7))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotArcanite", 1), 64000 * 10))
            .noOptimize()
            .specialValue(6200)
            .eut(TierEU.LuV)
            .duration(512 * 300 * 20 * 10)
            .addTo(SF);
        // Pika 64B
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 1440),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 1440),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 1995),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 900),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 64000 * 1440),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 64000 * 225),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Antimony, 1), 64000 * 1440),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 267),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 1725),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cerium, 1), 64000 * 2160),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 3063),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 1), 64000 * 2880),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 225),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Ytterbium, 1), 64000 * 720),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 1), 64000 * 960),
                GTUtility.getIntegratedCircuit(21))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotPikyonium64B", 1), 64000 * 20880))
            .noOptimize()
            .specialValue(10000)
            .eut(TierEU.UV)
            .duration(512 * 104 * 20 * 2000)
            .addTo(SF);
        // Quantum
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 64000 * 10),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1), 64000 * 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 1), 64000 * 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 6),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedAir, 1), 64000 * 5),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 6400 * 5),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 21),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 64000 * 5),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000 * 12),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedWater, 1), 64000 * 5),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 21),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 1), 64000 * 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 1), 64000 * 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 10),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustGermanium", 1), 64000 * 20),
                GTUtility.getIntegratedCircuit(21))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotQuantum", 1), 64000 * 200))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UIV)
            .duration(512 * 625 * 20 * 200)
            .addTo(SF);
        // A243
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 320),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 150),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 64000 * 75),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 6400 * 225),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1), 64000 * 100),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 600),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 208),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 48),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 160),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 1), 64000 * 350),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gadolinium, 1), 64000 * 250),
                setStackSize(GTModHandler.getModItem("Thaumcraft", "ItemResource", 1, 3), 64000 * 100),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotCinobiteA243", 1), 64000 * 2650))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UV)
            .duration(512 * 79 * 20 * 2650)
            .addTo(SF);
        // Guangsu
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1), 360 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 360 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1), 540 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 36 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 184 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1), 720 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 180 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 90 * 64000),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 64000 * 20),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10105), 150 * 64000))
            .noOptimize()
            .specialValue(9000)
            .eut(TierEU.ZPM)
            .duration(512 * 20 * 20 * 150)
            .addTo(SF);
        // Lufeien
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 50),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 160),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 75),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 1), 64000 * 50),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Samarium, 1), 6400 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 25),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Yttrium, 1), 64000 * 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 8),
                GTUtility.getIntegratedCircuit(15))
            .fluidInputs(FluidRegistry.getFluidStack("argon", 64000 * 25000))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotLafiumCompound", 1), 64000 * 450))
            .noOptimize()
            .specialValue(9000)
            .eut(TierEU.ZPM)
            .duration(512 * 375 * 20 * 450)
            .addTo(SF);
        // Botem
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustThallium", 1), 64000 * 15),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 64000 * 30),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 64), 30 * 64000),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotBotmium", 1), 64000 * 80))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UHV)
            .duration(512 * 500 * 20 * 80)
            .addTo(SF);
        // Abyssal Alloy
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustGermanium", 1), 64000 * 180),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 450),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 108),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tin, 1), 64000 * 225),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Chrome, 1), 64000 * 460),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000 * 100),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 64000 * 1176),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 820),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64000 * 675),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 64000 * 450),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Yttrium, 1), 64000 * 36),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 11012), 180 * 64000),
                GTUtility.getIntegratedCircuit(14))
            .fluidInputs(FluidRegistry.getFluidStack("radon", Integer.MAX_VALUE))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotAbyssalAlloy", 1), 64000 * 5040))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UEV)
            .duration(512 * 562 * 20 * 373)
            .addTo(SF);
        // Black Titanium
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 64000 * 55),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lanthanum, 1), 64000 * 12),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 6),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Manganese, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Phosphorus, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Niobium, 1), 64000 * 2),
                GTUtility.getIntegratedCircuit(13))
            .fluidInputs(FluidRegistry.getFluidStack("argon", 64000 * 5000))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotBlackTitanium", 1), 64000 * 100))
            .noOptimize()
            .specialValue(11000)
            .eut(TierEU.UHV)
            .duration(512 * 480 * 20 * 100)
            .addTo(SF);
        // 2B
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Molybdenum, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 64000 * 56),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 64000 * 24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 64000 * 2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 64000 * 3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 64000 * 9),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Cobalt, 1), 64000 * 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 64000 * 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Trinium, 1), 64000 * 36),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Strontium, 1), 64000 * 12),
                GTUtility.getIntegratedCircuit(8))
            .itemOutputs(setStackSize(GTModHandler.getModItem("miscutils", "itemIngotArceusAlloy2B", 1), 64000 * 132))
            .noOptimize()
            .specialValue(9000)
            .eut(TierEU.UV)
            .duration(512 * 438 * 20 * 132)
            .addTo(SF);
        // Mellion
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Rubidium, 1), 64000 * 11),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 64000 * 11),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.FierySteel, 1), 64000 * 7),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Firestone, 1), 64000 * 13),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10022), 64000 * 13),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10023), 64000 * 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 1), 3200),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Calcium, 1), 3200),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustCelestialTungsten", 1), 3200))
            .fluidInputs(
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 64000 * 10000),
                FluidRegistry.getFluidStack("plasma.fermium", 448000))
            .itemOutputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, MaterialsUEVplus.Mellion, 1), 64000 * 63),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.ingot, MaterialsUEVplus.Creon, 1), 64000 * 63))
            .specialValue(13500)
            .eut(TierEU.MAX)
            .duration(80 * 512 * 20 * 63)
            .addTo(SF);
        // Six-Phased Copper
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(
                    GTOreDictUnificator.get(OrePrefixes.ingot, MaterialsUEVplus.SixPhasedCopper, 1),
                    64000 * 72))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("Avaritia", "Singularity", 1, 5), 64000 * 8),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustCelestialTungsten", 1), 64000 * 72),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustAstralTitanium", 1), 64000 * 288),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustHypogen", 1), 64000 * 36),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustChromaticGlass", 1), 64000 * 576),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustRhugnor", 1), 64000 * 18),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, MaterialsUEVplus.Mellion, 1), 64000 * 72))
            .eut(TierEU.RECIPE_UXV)
            .duration(60 * 72 * 20 * 512)
            .specialValue(13000)
            .addTo(SF);
        // Molten Halkonite
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEntropy, 1), 1000 * 180),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tungsten, 1), 1000 * 829),
                setStackSize(Materials.Tartarite.getDust(1), 1000 * 2160),
                setStackSize(Materials.Vanadium.getDust(1), 1000 * 240),
                setStackSize(Materials.BlackPlutonium.getDust(1), 1000 * 80),
                setStackSize(MaterialsUEVplus.TranscendentMetal.getDust(1), 1000 * 2160),
                setStackSize(Materials.Naquadria.getDust(1), 1000 * 560),
                setStackSize(Materials.Bedrockium.getDust(1), 1000 * 320),
                setStackSize(Materials.Infinity.getDust(1), 1000 * 1080),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 1000 * 509),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Titanium, 1), 1000 * 162),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedFire, 1), 1000 * 180),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfusedEarth, 1), 1000 * 180),
                GTUtility.getIntegratedCircuit(11))
            .fluidInputs(FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 1000 * 1080000))
            .fluidOutputs(MaterialsUEVplus.MoltenProtoHalkoniteBase.getFluid(1244160000))
            .noOptimize()
            .specialValue(10000)
            .eut(TierEU.UEV)
            .duration(1024 * 1000 * 20 * 60)
            .addTo(SF);
    }
}
