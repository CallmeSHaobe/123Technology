package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.Utils.RecipeBuilder;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class recipesSunFactoryEnqing implements IRecipePool {

    @Override
    public void loadRecipes() {
        RecipeMap<?> PCB = Recipemaps.SF1;
        RecipeMap<?> SMD = Recipemaps.SF2;
        RecipeMap<?> NMD = Recipemaps.SF3;
        RecipeMap<?> UNI = Recipemaps.SF4;
        Fluid sb = FluidRegistry.getFluid("molten.mutatedlivingsolder");
        Fluid water = FluidRegistry.getFluid("grade8purifiedwater");
        Fluid water2 = FluidRegistry.getFluid("water");
        // PCB
        // T3
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 180),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 6),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 3),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32748), 128))
            .noOptimize()
            .duration(4 * 20 * 64)
            .eut(TierEU.RECIPE_EV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 180),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 6),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 3),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32106), 2048))
            .noOptimize()
            .duration(4 * 20 * 64)
            .eut(TierEU.RECIPE_EV)
            .addTo(PCB);
        // T4
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 120),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrum, 1), 120),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 15),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 15),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 1))
            .fluidInputs(new FluidStack(sb, 54))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32756), 128))
            .noOptimize()
            .duration(6 * 20 * 64)
            .eut(TierEU.RECIPE_IV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 120),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Electrum, 1), 120),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 15),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 15),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 1))
            .fluidInputs(new FluidStack(sb, 54))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32102), 2048))
            .noOptimize()
            .duration(6 * 20 * 64)
            .eut(TierEU.RECIPE_IV)
            .addTo(PCB);
        // T5
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 156),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnergeticAlloy, 1), 156),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 39),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 18),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 2))
            .fluidInputs(new FluidStack(sb, 72))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32754), 128))
            .noOptimize()
            .duration(8 * 20 * 64)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 156),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnergeticAlloy, 1), 156),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 39),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 18),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 2))
            .fluidInputs(new FluidStack(sb, 72))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32103), 2048))
            .noOptimize()
            .duration(8 * 20 * 64)
            .eut(TierEU.RECIPE_LuV)
            .addTo(PCB);
        // T6
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1), 194),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 194),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 99),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 24),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 2))
            .fluidInputs(new FluidStack(sb, 90))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32753), 128))
            .noOptimize()
            .duration(10 * 20 * 64)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Palladium, 1), 194),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 194),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 99),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 24),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 2))
            .fluidInputs(new FluidStack(sb, 90))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32104), 2048))
            .noOptimize()
            .duration(10 * 20 * 64)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(PCB);
        // T7
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnrichedHolmium, 1), 204),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NiobiumTitanium, 1), 204),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32073), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 35),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 32),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 3))
            .fluidInputs(new FluidStack(sb, 108))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32750), 128))
            .noOptimize()
            .duration(12 * 20 * 64)
            .eut(TierEU.RECIPE_UV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnrichedHolmium, 1), 204),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NiobiumTitanium, 1), 204),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32073), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 35),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 32),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 3))
            .fluidInputs(new FluidStack(sb, 108))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32105), 2048))
            .noOptimize()
            .duration(12 * 20 * 64)
            .eut(TierEU.RECIPE_UV)
            .addTo(PCB);
        // T8
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Longasssuperconductornameforuvwire, 1),
                    300),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 300),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32076), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 38),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 4))
            .fluidInputs(new FluidStack(sb, 126))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32746), 128))
            .noOptimize()
            .duration(14 * 20 * 64)
            .eut(TierEU.RECIPE_UHV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Longasssuperconductornameforuvwire, 1),
                    300),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 300),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32076), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 38),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 4))
            .fluidInputs(new FluidStack(sb, 126))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32107), 2048))
            .noOptimize()
            .duration(14 * 20 * 64)
            .eut(TierEU.RECIPE_UHV)
            .addTo(PCB);
        // T9
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10106), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfinityCatalyst, 1), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.MysteriousCrystal, 1), 200),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustChromaticGlass", 1), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 60),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 45),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 7))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32704), 128))
            .noOptimize()
            .duration(16 * 20 * 64)
            .eut(TierEU.RECIPE_UEV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10106), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfinityCatalyst, 1), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.MysteriousCrystal, 1), 200),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustChromaticGlass", 1), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 60),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 45),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 22979), 7))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32728), 2048))
            .noOptimize()
            .duration(16 * 20 * 64)
            .eut(TierEU.RECIPE_UEV)
            .addTo(PCB);
        // SMD
        // Yellow
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plastic, 1), 217),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tantalum, 1), 58),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.PolyvinylChloride, 1), 21),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 1), 12),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 1), 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 1), 4),
                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32741), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32742), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32743), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32744), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32745), 64))
            .noOptimize()
            .duration(20 * 64)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(SMD);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Plastic, 1), 217),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tantalum, 1), 58),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.PolyvinylChloride, 1), 21),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iridium, 1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 1), 12),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Arsenic, 1), 4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neodymium, 1), 4),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32011), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32016), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32018), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32020), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32182), 1024))
            .noOptimize()
            .duration(20 * 64)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(SMD);
        // Red
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Polybenzimidazole, 1), 272),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.TungstenSteel, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Graphene, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NiobiumTitanium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.HSSG, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.HSSS, 1), 8),
                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(new FluidStack(sb, 288))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32737), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32738), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32739), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32740), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32707), 64))
            .noOptimize()
            .duration(20 * 64)
            .eut(TierEU.RECIPE_UHV)
            .addTo(SMD);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Polybenzimidazole, 1), 272),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.TungstenSteel, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Graphene, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NiobiumTitanium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.HSSG, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.HSSS, 1), 8),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 288))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32024), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32025), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32026), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32027), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32183), 1024))
            .noOptimize()
            .duration(20 * 64)
            .eut(TierEU.RECIPE_UHV)
            .addTo(SMD);
        // Purple -1
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 83),
                setStackSize(
                    GTOreDictUnificator.get(
                        OrePrefixes.dust,
                        Materials.Tetraindiumditindibariumtitaniumheptacoppertetrakaidekaoxid,
                        1),
                    72),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 32),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10105), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 31),
                setStackSize(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tetranaquadahdiindiumhexaplatiumosminid, 1),
                    28),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 16),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10102), 8),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustPikyonium64B", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustArceusAlloy2B", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustCinobiteA243", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustLafiumCompound", 1), 8),
                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(FluidRegistry.getFluidStack("molten.spacetime", 72))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32706), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32708), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32709), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32710), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32711), 64))
            .noOptimize()
            .duration(20 * 64)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SMD);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 83),
                setStackSize(
                    GTOreDictUnificator.get(
                        OrePrefixes.dust,
                        Materials.Tetraindiumditindibariumtitaniumheptacoppertetrakaidekaoxid,
                        1),
                    72),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 32),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10105), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 31),
                setStackSize(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tetranaquadahdiindiumhexaplatiumosminid, 1),
                    28),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 16),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10102), 8),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustPikyonium64B", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustArceusAlloy2B", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustCinobiteA243", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustLafiumCompound", 1), 8),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(FluidRegistry.getFluidStack("molten.spacetime", 72))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32178), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32179), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32180), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32181), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32184), 1024))
            .noOptimize()
            .duration(20 * 64)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SMD);
        // Purple -2
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 83),
                setStackSize(
                    GTOreDictUnificator.get(
                        OrePrefixes.dust,
                        Materials.Tetraindiumditindibariumtitaniumheptacoppertetrakaidekaoxid,
                        1),
                    72),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 32),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10105), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 31),
                setStackSize(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tetranaquadahdiindiumhexaplatiumosminid, 1),
                    28),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 16),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10102), 8),
                setStackSize(GTModHandler.getModItem("miscutils", "itemIngotPikyonium64B", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemIngotArceusAlloy2B", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemIngotCinobiteA243", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemIngotLafiumCompound", 1), 8),
                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.spacetime", 72),
                FluidRegistry.getFluidStack("molten.eternity", 72))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32706), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32708), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32709), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32710), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32711), 64))
            .noOptimize()
            .duration(20 * 64)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SMD);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 83),
                setStackSize(
                    GTOreDictUnificator.get(
                        OrePrefixes.dust,
                        Materials.Tetraindiumditindibariumtitaniumheptacoppertetrakaidekaoxid,
                        1),
                    72),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 32),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10105), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 31),
                setStackSize(
                    GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tetranaquadahdiindiumhexaplatiumosminid, 1),
                    28),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 16),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedingot", 1, 10102), 8),
                setStackSize(GTModHandler.getModItem("miscutils", "itemIngotPikyonium64B", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemIngotArceusAlloy2B", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemIngotCinobiteA243", 1), 16),
                setStackSize(GTModHandler.getModItem("miscutils", "itemIngotLafiumCompound", 1), 8),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.spacetime", 72),
                FluidRegistry.getFluidStack("molten.eternity", 72))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32178), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32179), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32180), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32181), 1024),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32184), 1024))
            .noOptimize()
            .duration(20 * 64)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SMD);
        // NMD
        // Power IC
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconSG, 1), 576))
            .fluidInputs(new FluidStack(water, 1440))
            .fluidOutputs(new FluidStack(water2, 1440))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32050), 1024))
            .noOptimize()
            .duration(60 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(NMD);
        // Power IC Adv
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(2),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconSG, 1), 576),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048))
            .fluidInputs(new FluidStack(water, 2880))
            .fluidOutputs(new FluidStack(water2, 2880))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32052), 1024))
            .noOptimize()
            .duration(60 * 64 * 20)
            .eut(TierEU.LuV)
            .addTo(NMD);
        // Power IC SuperHigh
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(3),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconSG, 1), 576),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 10240),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 1), 4096))
            .fluidInputs(new FluidStack(water, 4320))
            .fluidOutputs(new FluidStack(water2, 4320))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32058), 1024))
            .noOptimize()
            .duration(60 * 64 * 20)
            .eut(TierEU.ZPM)
            .addTo(NMD);
        // Power IC Pm
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SiliconSG, 1), 576),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 65536),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 10240))
            .fluidInputs(new FluidStack(water, 5760))
            .fluidOutputs(new FluidStack(water2, 5760))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32162), 1024))
            .noOptimize()
            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(NMD);
        // UNI
        // Crystal Processing Unit
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32718), 64))
            .noOptimize()
            .duration(123 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Olivine, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32718), 64))
            .noOptimize()
            .duration(123 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(UNI);
        // Crystal SoC
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Olivine, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(2))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32717), 64))
            .noOptimize()
            .duration(123 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(2))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32717), 64))
            .noOptimize()
            .duration(123 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(UNI);
        // Wetware SoC
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Olivine, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32073), 5),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32076), 5),
                GTUtility.getIntegratedCircuit(3))
            .fluidInputs(new FluidStack(sb, 288))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32700), 64))
            .noOptimize()
            .duration(240 * 64 * 20)
            .eut(TierEU.UHV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32073), 5),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32076), 5),
                GTUtility.getIntegratedCircuit(3))
            .fluidInputs(new FluidStack(sb, 288))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32700), 64))
            .noOptimize()
            .duration(240 * 64 * 20)
            .eut(TierEU.UHV)
            .addTo(UNI);
        // Adv Crystal
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Olivine, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(4))
            .fluidInputs(new FluidStack(sb, 576))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32716), 64))
            .noOptimize()
            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(4))
            .fluidInputs(new FluidStack(sb, 576))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32716), 64))
            .noOptimize()
            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(UNI);
        // Bio SoC
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustHypogen", 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.block, Materials.SiliconSG, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.GalliumArsenide, 1), 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 1), 4),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32076), 96),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(FluidRegistry.getFluidStack("ic2uumatter", 576000))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32699), 64))
            .noOptimize()
            .duration(15 * 64 * 20)
            .eut(TierEU.UIV)
            .addTo(UNI);
        // Optically RAM
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 4608),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1), 4608),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15470), 2048),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32039), 432),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32047), 432),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32041), 432),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 4581), 4),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32417), 64),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32724), 256),
                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.spacetime", 2880),
                FluidRegistry.getFluidStack("molten.infinity", 4320),
                FluidRegistry.getFluidStack("molten.superconductorumvbase", 2880),
                new FluidStack(sb, 5760))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32725), 1024))
            .noOptimize()
            .duration(10 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 4608),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1), 4608),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15470), 2048),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32039), 432),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32047), 432),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32041), 432),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 4581), 4),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32417), 64),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32724), 256),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.spacetime", 2880),
                FluidRegistry.getFluidStack("molten.infinity", 4320),
                FluidRegistry.getFluidStack("molten.superconductorumvbase", 2880),
                new FluidStack(sb, 5760))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32701), 64))
            .noOptimize()
            .duration(10 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        // Optically CPU Shell
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32704), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 1024),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustBotmium", 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahAlloy, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VibrantAlloy, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 144),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 40),

                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32727), 1024))
            .noOptimize()
            .duration(10 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32704), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 1024),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustBotmium", 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahAlloy, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VibrantAlloy, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 144),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 40),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32702), 64))
            .noOptimize()
            .duration(5 * 64 * 20)
            .eut(TierEU.UIV)
            .addTo(UNI);
        // Optically CPU
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32724), 768),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32727), 768),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15470), 768),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 2606), 128),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 128),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10112), 128),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustCelestialTungsten", 1), 128),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10110), 128),

                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(new FluidStack(sb, 1440))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32726), 1024))
            .noOptimize()
            .duration(20 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32724), 768),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32727), 768),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15470), 768),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 2606), 128),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 128),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10112), 128),
                setStackSize(GTModHandler.getModItem("miscutils", "itemDustCelestialTungsten", 1), 128),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 10110), 128),

                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 1440))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 32703), 64))
            .noOptimize()
            .duration(20 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        // Optically Die
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.ElectrumFlux, 1), 9216),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1), 2560),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.block, Materials.SiliconSG, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.GalliumArsenide, 1), 128),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 288))
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32724), 1024))
            .noOptimize()
            .duration(5 * 64 * 20)
            .eut(TierEU.UXV)
            .addTo(UNI);
        // Item Processing Unit IV
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.GarnetYellow, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Emerald, 1), 1024),
                GTUtility.getIntegratedCircuit(24))
            .itemOutputs(
                setStackSize(
                    GTModHandler.getModItem("dreamcraft", "item.EngineeringProcessorItemAdvEmeraldCore", 1),
                    1024))
            .noOptimize()
            .duration(114 * 20)
            .eut(TierEU.IV)
            .addTo(UNI);
        // Gravity Engine
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steel, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lead, 1), 36),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), 24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silver, 1), 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Data, 1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Obsidian, 1), 16),
                setStackSize(
                    GTOreDictUnificator.get(
                        OrePrefixes.dust,
                        Materials.Tetraindiumditindibariumtitaniumheptacoppertetrakaidekaoxid,
                        1),
                    12),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.StainlessSteel, 1), 9 * 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 1), 8 * 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1), 8 * 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 8 * 64),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32600), 4 * 64),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GTModHandler.getModItem("GraviSuite", "itemSimpleItem", 1, 3), 64))
            .fluidInputs(
                FluidRegistry.getFluidStack("helium", 288 * 64),
                FluidRegistry.getFluidStack("molten.solderingalloy", 288 * 64),
                FluidRegistry.getFluidStack("molten.callistoice", 288 * 64)

            )
            .noOptimize()
            .duration(123 * 20)
            .eut(TierEU.EV)
            .addTo(UNI);
        // shit
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Boron, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bismuth, 1), 3 * 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 35), 64),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 3), 64),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedItem0", 1, 3), 64))
            .fluidInputs(
                FluidRegistry.getFluidStack("oxygen", 144 * 64),
                FluidRegistry.getFluidStack("molten.solderingalloy", 144 * 64),
                FluidRegistry.getFluidStack("hydrogen", 144 * 64)

            )
            .noOptimize()
            .duration(20 * 20)
            .eut(TierEU.LuV)
            .addTo(UNI);
        // HighTemp Carbon Web
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tantalum, 1), 16),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 11000), 4),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GTModHandler.getModItem("GalaxySpace", "item.DysonSwarmParts", 1, 3), 10))
            .fluidInputs(Materials.Helium.getPlasma(80)

            )
            .noOptimize()
            .duration(100 * 20)
            .eut(TierEU.LuV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tantalum, 1), 16),
                setStackSize(GTModHandler.getModItem("bartworks", "gt.bwMetaGenerateddust", 1, 11002), 4),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GTModHandler.getModItem("GalaxySpace", "item.DysonSwarmParts", 1, 3), 10))
            .fluidInputs(Materials.Helium.getPlasma(80)

            )
            .noOptimize()
            .duration(100 * 20)
            .eut(TierEU.LuV)
            .addTo(UNI);
        // Iridium Adv Plate
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 17588), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1), 1024),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 2304), FluidRegistry.getFluidStack("molten.infinity", 144))
            .itemOutputs(setStackSize(GTModHandler.getModItem("IC2", "itemPartIridium", 1), 2048))
            .noOptimize()
            .duration(100 * 20)
            .eut(TierEU.UHV)
            .addTo(UNI);
        // Inf Chest
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTModHandler.getModItem("avaritiaddons", "InfinityChest", 1), 256))
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 256 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 144 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 144 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1), 144 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Bio, 1), 90 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Superconductor, 1), 48 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 1), 54 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CosmicNeutronium, 1), 54 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 1), 54 * 256),
                setStackSize(ItemList.Field_Generator_UV.get(1), 18 * 256),
                setStackSize(ItemList.Conveyor_Module_UEV.get(1), 18 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.StainlessSteel, 1), 18 * 256),
                setStackSize(new ItemStack(Blocks.chest, 1), 18 * 256),
                setStackSize(GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 1), 256),
                GTModHandler.getModItem("dreamcraft", "item.EngineeringProcessorItemAdvEmeraldCore", 1))
            .noOptimize()
            .duration(256 * 20 * 5)
            .eut(TierEU.UV)
            .addTo(UNI);

    }
}
