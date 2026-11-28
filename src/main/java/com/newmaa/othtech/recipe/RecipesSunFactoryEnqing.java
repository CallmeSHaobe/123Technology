package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Config.ENQING_MULTI;
import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.ItemList.Circuit_Chip_CPU;
import static gregtech.api.enums.ItemList.Circuit_Chip_HPIC;
import static gregtech.api.enums.ItemList.Circuit_Chip_ILC;
import static gregtech.api.enums.ItemList.Circuit_Chip_LPIC;
import static gregtech.api.enums.ItemList.Circuit_Chip_NAND;
import static gregtech.api.enums.ItemList.Circuit_Chip_NOR;
import static gregtech.api.enums.ItemList.Circuit_Chip_NPIC;
import static gregtech.api.enums.ItemList.Circuit_Chip_NanoCPU;
import static gregtech.api.enums.ItemList.Circuit_Chip_PIC;
import static gregtech.api.enums.ItemList.Circuit_Chip_PPIC;
import static gregtech.api.enums.ItemList.Circuit_Chip_QPIC;
import static gregtech.api.enums.ItemList.Circuit_Chip_QuantumCPU;
import static gregtech.api.enums.ItemList.Circuit_Chip_Ram;
import static gregtech.api.enums.ItemList.Circuit_Chip_Simple_SoC;
import static gregtech.api.enums.ItemList.Circuit_Chip_SoC;
import static gregtech.api.enums.ItemList.Circuit_Chip_SoC2;
import static gregtech.api.enums.ItemList.Circuit_Chip_UHPIC;
import static gregtech.api.enums.ItemList.Circuit_Chip_ULPIC;
import static gregtech.api.enums.ItemList.Circuit_Silicon_Wafer;
import static gregtech.api.enums.ItemList.Circuit_Silicon_Wafer2;
import static gregtech.api.enums.ItemList.Circuit_Silicon_Wafer3;
import static gregtech.api.enums.ItemList.Circuit_Silicon_Wafer6;
import static gregtech.api.enums.ItemList.Circuit_Silicon_Wafer7;
import static gregtech.api.enums.ItemList.Circuit_Wafer_Bioware;
import static gregtech.api.enums.ItemList.Circuit_Wafer_CPU;
import static gregtech.api.enums.ItemList.Circuit_Wafer_HPIC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_ILC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_LPIC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_NAND;
import static gregtech.api.enums.ItemList.Circuit_Wafer_NOR;
import static gregtech.api.enums.ItemList.Circuit_Wafer_NPIC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_NanoCPU;
import static gregtech.api.enums.ItemList.Circuit_Wafer_PIC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_PPIC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_QPIC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_QuantumCPU;
import static gregtech.api.enums.ItemList.Circuit_Wafer_Ram;
import static gregtech.api.enums.ItemList.Circuit_Wafer_Simple_SoC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_SoC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_SoC2;
import static gregtech.api.enums.ItemList.Circuit_Wafer_UHPIC;
import static gregtech.api.enums.ItemList.Circuit_Wafer_ULPIC;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.enums.TierEU.RECIPE_UEV;
import static gregtech.api.enums.TierEU.RECIPE_UHV;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.item.NHItemList;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.ModsEnum;
import com.newmaa.othtech.utils.RecipeBuilder;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import bartworks.system.material.WerkstoffLoader;
import goodgenerator.items.GGMaterial;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.common.items.MetaGeneratedItem01;
import gregtech.common.items.MetaGeneratedItem03;
import gtPlusPlus.core.item.base.BaseItemComponent.ComponentTypes;
import gtPlusPlus.core.material.MaterialMisc;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements.STANDALONE;
import gtPlusPlus.core.material.Particle;
import gtnhlanth.common.item.MaskList;
import gtnhlanth.common.register.LanthItemList;
import gtnhlanth.common.register.WerkstoffMaterialPool;
import ic2.core.init.InternalName;

public class RecipesSunFactoryEnqing implements IRecipePool {

    @Override
    public void loadRecipes() {
        final int a = Math.round(ENQING_MULTI);
        RecipeMap<?> PCB = Recipemaps.SF1;
        RecipeMap<?> SMD = Recipemaps.SF2;
        RecipeMap<?> NMD = Recipemaps.SF3;
        RecipeMap<?> UNI = Recipemaps.SF4;
        Fluid sb = FluidRegistry.getFluid(RecipeFluids.gtppMoltenName(MaterialMisc.MUTATED_LIVING_SOLDER));
        Fluid water = FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Grade8PurifiedWater.mFluid));
        Fluid water2 = FluidRegistry.getFluid(FluidRegistry.WATER.getName());
        // PCB
        // T3
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 180),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 6),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 3),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_PlasticCircuitBoards2)
                        : null),
                    128 * a))

            .duration(4 * 20 * 64)
            .eut(TierEU.RECIPE_EV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Copper, 1), 180),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 6),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 3),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Board_Plastic_Advanced), 2048 * a))

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
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    1))
            .fluidInputs(new FluidStack(sb, 54))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_AdvancedCircuitBoards)
                        : null),
                    128 * a))

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
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    1))
            .fluidInputs(new FluidStack(sb, 54))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Board_Epoxy_Advanced), 2048 * a))

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
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    2))
            .fluidInputs(new FluidStack(sb, 72))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_MoreAdvancedCircuitBoards)
                        : null),
                    128 * a))

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
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    2))
            .fluidInputs(new FluidStack(sb, 72))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Board_Fiberglass_Advanced), 2048 * a))

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
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    2))
            .fluidInputs(new FluidStack(sb, 90))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_EliteCircuitBoards)
                        : null),
                    128 * a))

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
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    2))
            .fluidInputs(new FluidStack(sb, 90))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Board_Multifiberglass_Elite), 2048 * a))

            .duration(10 * 20 * 64)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(PCB);
        // T7
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnrichedHolmium, 1), 204),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NiobiumTitanium, 1), 204),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Stemcell), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 35),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 32),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    3))
            .fluidInputs(new FluidStack(sb, 108))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_ExtremeWetwareLifesupportCircuitBoards)
                        : null),
                    128 * a))

            .duration(12 * 20 * 64)
            .eut(TierEU.RECIPE_UV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.EnrichedHolmium, 1), 204),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NiobiumTitanium, 1), 204),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Stemcell), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 35),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 32),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    3))
            .fluidInputs(new FluidStack(sb, 108))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Board_Wetware_Extreme), 2048 * a))

            .duration(12 * 20 * 64)
            .eut(TierEU.RECIPE_UV)
            .addTo(PCB);
        // T8
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SuperconductorUVBase, 1), 300),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 300),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Biocell), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 38),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    4))
            .fluidInputs(new FluidStack(sb, 126))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_UltraBioMutatedCircuitBoards)
                        : null),
                    128 * a))

            .duration(14 * 20 * 64)
            .eut(RECIPE_UHV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SuperconductorUVBase, 1), 300),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 300),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Biocell), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 59),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 38),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    4))
            .fluidInputs(new FluidStack(sb, 126))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Board_Bio_Ultra), 2048 * a))

            .duration(14 * 20 * 64)
            .eut(RECIPE_UHV)
            .addTo(PCB);
        // T9
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(23),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.tairitsu.getmID()
                                : 10106))
                        : null),
                    352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfinityCatalyst, 1), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.MysteriousCrystal, 1), 200),
                setStackSize(RecipeStacks.gtppComponent(1, STANDALONE.CHRONOMATIC_GLASS, ComponentTypes.DUST), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 60),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 45),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    7))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalCircuitBoards)
                        : null),
                    128 * a))

            .duration(16 * 20 * 64)
            .eut(TierEU.RECIPE_UEV)
            .addTo(PCB);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.tairitsu.getmID()
                                : 10106))
                        : null),
                    352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.InfinityCatalyst, 1), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.MysteriousCrystal, 1), 200),
                setStackSize(RecipeStacks.gtppComponent(1, STANDALONE.CHRONOMATIC_GLASS, ComponentTypes.DUST), 352),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.Iron, 1), 60),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1), 45),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plateDense, Materials.RadoxPolymer),
                    7))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Board_Optical), 2048 * a))

            .duration(16 * 20 * 64)
            .eut(TierEU.RECIPE_UEV)
            .addTo(PCB);
        // TST PCB
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(24),
                Materials.CosmicNeutronium.getDust(32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SuperconductorUIVBase, 1), 176),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.SuperconductorUMVBase, 1), 176))
            .fluidInputs(Materials.SpaceTime.getMolten(32 * 667))
            .itemOutputs(
                setStackSize(GTModHandler.getModItem(ModsEnum.TwistSpaceTechnology.ID, "MetaItem01", 1, 30), 2048 * a))

            .duration(22 * 20 * 32)
            .eut(TierEU.RECIPE_UEV)
            .addTo(PCB);
        // SMD
        // Yellow
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Polyethylene, 1), 217),
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
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_SMDCapacitors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_SMDTransistors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_SMDDiodes)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_SMDInductors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_SMDResistors)
                        : null),
                    64 * a))

            .duration(20 * 64)
            .eut(TierEU.RECIPE_ZPM)
            .addTo(SMD);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Polyethylene, 1), 217),
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
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_ResistorSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_DiodeSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_TransistorSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_CapacitorSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_InductorSMD), 1024 * a))

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
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_AdvancedSMDCapacitors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_AdvancedSMDTransistors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_AdvancedSMDDiodes)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_AdvancedSMDResistors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_AdvancedSMDInductors)
                        : null),
                    64 * a))

            .duration(20 * 64)
            .eut(RECIPE_UHV)
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
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_ResistorASMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_DiodeASMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_TransistorASMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_CapacitorASMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_InductorASMD), 1024 * a))

            .duration(20 * 64)
            .eut(RECIPE_UHV)
            .addTo(SMD);
        // Purple -1
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 83),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CosmicNeutronium, 1), 72),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 32),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.hikarium.getmID()
                                : 10105))
                        : null),
                    32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 31),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 1), 28),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 16),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.artheriumSn.getmID()
                                : 10102))
                        : null),
                    8),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.PIKYONIUM, ComponentTypes.DUST), 16),
                setStackSize(
                    RecipeStacks.gtppComponent(1, MaterialsAlloy.TRINIUM_REINFORCED_STEEL, ComponentTypes.DUST),
                    16),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.CINOBITE, ComponentTypes.DUST), 16),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.LAFIUM, ComponentTypes.DUST), 8),
                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(Materials.SpaceTime.getMolten(72))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDInductors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDCapacitors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDTransistors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDDiodes)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDResistors)
                        : null),
                    64 * a))

            .duration(20 * 64)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SMD);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 83),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CosmicNeutronium, 1), 72),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 32),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.hikarium.getmID()
                                : 10105))
                        : null),
                    32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 31),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 1), 28),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 16),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.artheriumSn.getmID()
                                : 10102))
                        : null),
                    8),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.PIKYONIUM, ComponentTypes.DUST), 16),
                setStackSize(
                    RecipeStacks.gtppComponent(1, MaterialsAlloy.TRINIUM_REINFORCED_STEEL, ComponentTypes.DUST),
                    16),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.CINOBITE, ComponentTypes.DUST), 16),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.LAFIUM, ComponentTypes.DUST), 8),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(Materials.SpaceTime.getMolten(72))
            .itemOutputs(
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_ResistorXSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_DiodeXSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_TransistorXSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_CapacitorXSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_InductorXSMD), 1024 * a))

            .duration(20 * 64)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SMD);
        // Purple -2
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 83),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CosmicNeutronium, 1), 72),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 32),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.ingot),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.hikarium.getmID()
                                : 10105))
                        : null),
                    32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 31),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 1), 28),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 16),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.ingot),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.artheriumSn.getmID()
                                : 10102))
                        : null),
                    8),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.PIKYONIUM, ComponentTypes.INGOT), 16),
                setStackSize(
                    RecipeStacks.gtppComponent(1, MaterialsAlloy.TRINIUM_REINFORCED_STEEL, ComponentTypes.INGOT),
                    16),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.CINOBITE, ComponentTypes.INGOT), 16),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.LAFIUM, ComponentTypes.INGOT), 8),
                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(Materials.SpaceTime.getMolten(72), Materials.Eternity.getMolten(72))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDInductors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDCapacitors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDTransistors)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDDiodes)
                        : null),
                    64 * a),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalSMDResistors)
                        : null),
                    64 * a))

            .duration(20 * 64)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SMD);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 83),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CosmicNeutronium, 1), 72),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.BlackPlutonium, 1), 32),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.ingot),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.hikarium.getmID()
                                : 10105))
                        : null),
                    32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Osmium, 1), 31),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 1), 28),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 16),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.ingot),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.artheriumSn.getmID()
                                : 10102))
                        : null),
                    8),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.PIKYONIUM, ComponentTypes.INGOT), 16),
                setStackSize(
                    RecipeStacks.gtppComponent(1, MaterialsAlloy.TRINIUM_REINFORCED_STEEL, ComponentTypes.INGOT),
                    16),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.CINOBITE, ComponentTypes.INGOT), 16),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.LAFIUM, ComponentTypes.INGOT), 8),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(Materials.SpaceTime.getMolten(72), Materials.Eternity.getMolten(72))
            .itemOutputs(
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_ResistorXSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_DiodeXSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_TransistorXSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_CapacitorXSMD), 1024 * a),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Parts_InductorXSMD), 1024 * a))

            .duration(20 * 64)
            .eut(TierEU.RECIPE_UIV)
            .addTo(SMD);
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                Materials.SiliconSG.getDust(16),
                RecipeStacks.exact(64, ItemList.Circuit_Silicon_Ingot))
            .itemOutputs(setStackSize(ItemList.Circuit_Silicon_Wafer.get(1), 64 * 16 * a))
            .duration(40 * 64 * 20)
            .eut(30)
            .addTo(NMD);
        // P
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                Materials.SiliconSG.getDust(32),
                RecipeStacks.exact(64, ItemList.Circuit_Silicon_Ingot2))
            .itemOutputs(setStackSize(ItemList.Circuit_Silicon_Wafer2.get(1), 64 * 32 * a))
            .duration(40 * 64 * 20)
            .eut(120)
            .addTo(NMD);
        // Nq
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                Materials.SiliconSG.getDust(64),
                RecipeStacks.exact(64, ItemList.Circuit_Silicon_Ingot3))
            .itemOutputs(setStackSize(Circuit_Silicon_Wafer3.get(1), 64 * 64 * a))
            .duration(40 * 64 * 20)
            .eut(480)
            .addTo(NMD);
        // Eu
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                setStackSize(Materials.SiliconSG.getDust(1), 128),
                RecipeStacks.exact(64, ItemList.Circuit_Silicon_Ingot4))
            .itemOutputs(setStackSize(ItemList.Circuit_Silicon_Wafer4.get(1), 64 * 96 * a))
            .duration(40 * 64 * 20)
            .eut(1920)
            .addTo(NMD);
        // Am
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                setStackSize(Materials.SiliconSG.getDust(1), 256),
                RecipeStacks.exact(64, ItemList.Circuit_Silicon_Ingot5))
            .itemOutputs(setStackSize(ItemList.Circuit_Silicon_Wafer5.get(1), 64 * 128 * a))
            .duration(40 * 64 * 20)
            .eut(7680)
            .addTo(NMD);
        // Optical
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                RecipeStacks.exact(64, ItemList.Circuit_Silicon_Ingot6))
            .fluidInputs(new FluidStack(water, 64000))
            .itemOutputs(setStackSize(ItemList.Circuit_Silicon_Wafer6.get(1), 64 * 24 * a))
            .duration(40 * 64 * 20)
            .eut(7680)
            .addTo(NMD);

        // Circuit Wafers
        // CPU
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_CPU.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.CPU), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(2))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_CPU.get(1), 1024 * 8 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.CPU), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(2))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);
        // NAND
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_NAND.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.NAND), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_NAND.get(1), 1024 * 32 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.NAND), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);
        // NOR
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_NOR.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.NOR), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_NOR.get(1), 1024 * 16 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.NOR), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);
        // RAM
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_Ram.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.RAM), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_Ram.get(1), 1024 * 32 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.RAM), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);
        // Logic
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_ILC.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.ILC), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_ILC.get(1), 1024 * 8 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.ILC), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);
        // SoC
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_SoC.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.SOC), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_SoC.get(1), 1024 * 6 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.SOC), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);
        // SSoC
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_Simple_SoC.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.SSOC), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_Simple_SoC.get(1), 1024 * 6 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.SSOC), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);
        // ASoC
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_SoC2.get(1024 * 6 * a), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.ASOC), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_SoC2.get(1024 * 6 * a), 1024 * 6 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.ASOC), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(100 * 20)
            .eut(1920)
            .addTo(NMD);
        // Nano CPU
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_NanoCPU.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.NCPU), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(60 * 20)
            .eut(1920)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_NanoCPU.get(1), 1024 * 8 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.NCPU), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(60 * 20)
            .eut(1920)
            .addTo(NMD);
        // Q CPU
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_QuantumCPU.get(1), 1024 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.QBIT), 0)
                    : null),
                GTUtility.getIntegratedCircuit(1),
                Circuit_Silicon_Wafer6.get(4))
            .duration(120 * 20)
            .eut(7680)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Chip_QuantumCPU.get(1), 1024 * 4 * a))
            .itemInputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                    ? RecipeStacks.exact(0, LanthItemList.maskMap.get(MaskList.QBIT), 0)
                    : null),
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(24),
                Circuit_Silicon_Wafer6.get(4))
            .duration(120 * 20)
            .eut(7680)
            .addTo(NMD);
        // BIO
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Wafer_Bioware.get(1), 1024 * a))
            .fluidInputs(Materials.BioMediumSterilized.getFluid(8000 * 1024))
            .itemInputs(
                GTUtility.getIntegratedCircuit(14),
                setStackSize(Circuit_Silicon_Wafer7.get(1), 256),
                setStackSize(RecipeStacks.gtppComponent(1, STANDALONE.HYPOGEN, ComponentTypes.DUST), 1024))
            .duration(60 * 20)
            .eut(RECIPE_UHV)
            .addTo(NMD);
        // Optical Enhanced
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(Circuit_Silicon_Wafer7.get(1), 512 * a))
            .itemInputs(
                OTHItemList.dustIrOsSmM.get(0),
                setStackSize(Circuit_Silicon_Wafer6.get(1), 1024),
                setStackSize(Materials.Glowstone.getNanite(1), 1024))
            .fluidInputs(
                Materials.Tin.getPlasma(1000 * 1024),
                FluidRegistry.getFluidStack(RecipeFluids.werkstoffName(WerkstoffLoader.Oganesson), 4000 * 1024))
            .eut(RECIPE_UEV)
            .duration(5 * 64 * 20)
            .addTo(NMD);

        // POWER IC
        // UL Power IC
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(2), setStackSize(Circuit_Silicon_Wafer2.get(1), 128))
            .itemOutputs(setStackSize(Circuit_Wafer_ULPIC.get(1), 1024 * a))
            .duration(10 * 20)
            .eut(120)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(18),
                setStackSize(Circuit_Silicon_Wafer2.get(1), 128))
            .itemOutputs(setStackSize(Circuit_Chip_ULPIC.get(1), 2048 * a))
            .duration(10 * 20)
            .eut(120)
            .addTo(NMD);
        // L Power IC
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(3), setStackSize(Circuit_Silicon_Wafer.get(1), 256))
            .itemOutputs(setStackSize(Circuit_Wafer_LPIC.get(1), 1024 * a))
            .duration(10 * 20)
            .eut(480)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(17),
                setStackSize(Circuit_Silicon_Wafer.get(1), 256))
            .itemOutputs(setStackSize(Circuit_Chip_LPIC.get(1), 2048 * a))
            .duration(10 * 20)
            .eut(480)
            .addTo(NMD);
        // Power IC
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4), setStackSize(Circuit_Silicon_Wafer3.get(1), 256))
            .fluidInputs(new FluidStack(water, 1440))
            .fluidOutputs(new FluidStack(water2, 1440))
            .itemOutputs(setStackSize(Circuit_Wafer_PIC.get(1), 1024 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(NMD);
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(4), setStackSize(Circuit_Silicon_Wafer6.get(1), 4))
            .fluidOutputs(new FluidStack(water2, 1440))
            .itemOutputs(setStackSize(Circuit_Wafer_PIC.get(1), 1024 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(16),
                setStackSize(Circuit_Silicon_Wafer3.get(1), 256))
            .fluidInputs(new FluidStack(water, 1440))
            .fluidOutputs(new FluidStack(water2, 1440))
            .itemOutputs(setStackSize(Circuit_Chip_PIC.get(1), 2048 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(NMD);
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(16),
                setStackSize(Circuit_Silicon_Wafer6.get(1), 4))
            .fluidOutputs(new FluidStack(water2, 1440))
            .itemOutputs(setStackSize(Circuit_Chip_PIC.get(1), 2048 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(NMD);
        // Power IC High
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(5),
                setStackSize(Circuit_Silicon_Wafer3.get(1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048))
            .fluidInputs(new FluidStack(water, 2880))
            .fluidOutputs(new FluidStack(water2, 2880))
            .itemOutputs(setStackSize(Circuit_Wafer_HPIC.get(1), 1024 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.LuV)
            .addTo(NMD);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(5),
                setStackSize(Circuit_Silicon_Wafer6.get(1), 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048))
            .fluidOutputs(new FluidStack(water2, 2880))
            .itemOutputs(setStackSize(Circuit_Wafer_HPIC.get(1), 1024 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.LuV)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(23),
                setStackSize(Circuit_Silicon_Wafer3.get(1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048))
            .fluidInputs(new FluidStack(water, 2880))
            .fluidOutputs(new FluidStack(water2, 2880))
            .itemOutputs(setStackSize(Circuit_Chip_HPIC.get(1), 2048 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.LuV)
            .addTo(NMD);
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(23),
                setStackSize(Circuit_Silicon_Wafer6.get(1), 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048))
            .fluidOutputs(new FluidStack(water2, 2880))
            .itemOutputs(setStackSize(Circuit_Chip_HPIC.get(1), 2048 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.LuV)
            .addTo(NMD);
        // Power IC SuperHigh
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                setStackSize(Circuit_Silicon_Wafer3.get(1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 10240),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 1), 4096))
            .fluidInputs(new FluidStack(water, 4320))
            .fluidOutputs(new FluidStack(water2, 4320))
            .itemOutputs(setStackSize(Circuit_Wafer_UHPIC.get(1), 1024 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.ZPM)
            .addTo(NMD);
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(6),
                setStackSize(Circuit_Silicon_Wafer6.get(1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 10240),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 1), 4096))
            .fluidOutputs(new FluidStack(water2, 4320))
            .itemOutputs(setStackSize(Circuit_Wafer_UHPIC.get(1), 1024 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.ZPM)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(22),
                setStackSize(Circuit_Silicon_Wafer3.get(1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 10240),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 1), 4096))
            .fluidInputs(new FluidStack(water, 4320))
            .fluidOutputs(new FluidStack(water2, 4320))
            .itemOutputs(setStackSize(Circuit_Chip_UHPIC.get(1), 2048 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.ZPM)
            .addTo(NMD);
        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(22),
                setStackSize(Circuit_Silicon_Wafer6.get(1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 10240),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VanadiumGallium, 1), 2048),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 1), 4096))
            .fluidOutputs(new FluidStack(water2, 4320))
            .itemOutputs(setStackSize(Circuit_Chip_UHPIC.get(1), 2048 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.ZPM)
            .addTo(NMD);
        // Power IC Pm
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                setStackSize(Circuit_Silicon_Wafer7.get(1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 65536),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 10240))
            .fluidOutputs(new FluidStack(water2, 5760))
            .itemOutputs(setStackSize(Circuit_Wafer_PPIC.get(1), 1024 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(21),
                setStackSize(Circuit_Silicon_Wafer7.get(1), 32),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.IndiumGalliumPhosphide, 1), 65536),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 10240))
            .fluidOutputs(new FluidStack(water2, 5760))
            .itemOutputs(setStackSize(Circuit_Chip_PPIC.get(1), 2048 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(NMD);
        // Nano Power IC
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(8), setStackSize(Circuit_Silicon_Wafer6.get(1), 32))
            .itemOutputs(setStackSize(Circuit_Wafer_NPIC.get(1), 1024 * a))
            .duration(10 * 20)
            .eut(120)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(20),
                setStackSize(Circuit_Silicon_Wafer6.get(1), 32))
            .itemOutputs(setStackSize(Circuit_Chip_NPIC.get(1), 2048 * a))
            .duration(10 * 20)
            .eut(120)
            .addTo(NMD);
        // Quantum Power ic
        RecipeBuilder.builder()
            .itemInputs(GTUtility.getIntegratedCircuit(9), setStackSize(Circuit_Silicon_Wafer6.get(1), 32))
            .itemOutputs(setStackSize(Circuit_Wafer_QPIC.get(1), 1024 * a))
            .duration(10 * 20)
            .eut(120)
            .addTo(NMD);

        RecipeBuilder.builder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.Shape_Slicer_Flat),
                GTUtility.getIntegratedCircuit(19),
                setStackSize(Circuit_Silicon_Wafer6.get(1), 32))
            .itemOutputs(setStackSize(Circuit_Chip_QPIC.get(1), 2048 * a))
            .duration(10 * 20)
            .eut(120)
            .addTo(NMD);
        // Adv Crystal
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Olivine, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(4))
            .fluidInputs(new FluidStack(sb, 576))
            .itemOutputs(setStackSize(ItemList.Circuit_Chip_CrystalSoC2.get(1), 64 * 16 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(NMD);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(4))
            .fluidInputs(new FluidStack(sb, 576))
            .itemOutputs(setStackSize(ItemList.Circuit_Chip_CrystalSoC2.get(1), 64 * 16 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(NMD);
        // 阿米功率IC晶圆 -> 仄米功率IC晶圆（定制配方）
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(ItemList.Circuit_Wafer_APIC.get(1), 4096),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.SpaceTime, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Creon, 1), 123),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Mellion, 1), 123),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                        ? RecipeStacks.exact(1, Particle.getBaseParticle(Particle.GRAVITON))
                        : null),
                    999))
            .itemOutputs(setStackSize(ItemList.Circuit_Wafer_ZPIC.get(1), 4096 * a))
            .duration(999 * 20)
            .eut(125_829_120)
            .addTo(NMD);
        // UNI
        // Crystal Processing Unit
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_CrystalProcessingUnits)
                        : null),
                    64 * a))

            .duration(123 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Olivine, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(new FluidStack(sb, 36))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_CrystalProcessingUnits)
                        : null),
                    64 * a))

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
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_CrystalSoCs)
                        : null),
                    64 * a))

            .duration(123 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(2))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_CrystalSoCs)
                        : null),
                    64 * a))

            .duration(123 * 64 * 20)
            .eut(TierEU.EV)
            .addTo(UNI);
        // Wetware SoC
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Olivine, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Stemcell), 5),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Biocell), 5),
                GTUtility.getIntegratedCircuit(3))
            .fluidInputs(new FluidStack(sb, 288))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_LivingCrystalChips)
                        : null),
                    64 * a))

            .duration(240 * 64 * 20)
            .eut(TierEU.UHV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Stemcell), 5),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Biocell), 5),
                GTUtility.getIntegratedCircuit(3))
            .fluidInputs(new FluidStack(sb, 288))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_LivingCrystalChips)
                        : null),
                    64 * a))

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
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_RawAdvancedCrystalChips)
                        : null),
                    64 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Emerald, 1), 1024),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Europium, 1), 144),
                GTUtility.getIntegratedCircuit(4))
            .fluidInputs(new FluidStack(sb, 576))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_RawAdvancedCrystalChips)
                        : null),
                    64 * a))

            .duration(60 * 64 * 20)
            .eut(TierEU.UV)
            .addTo(UNI);
        // Bio SoC
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(RecipeStacks.gtppComponent(1, STANDALONE.HYPOGEN, ComponentTypes.DUST), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.block, Materials.SiliconSG, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.GalliumArsenide, 1), 8),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 1), 4),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Biocell), 96),
                GTUtility.getIntegratedCircuit(1))
            .fluidInputs(FluidRegistry.getFluidStack(RecipeFluids.ic2FluidName(InternalName.fluidUuMatter), 576000))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_LivingBioChips)
                        : null),
                    64 * a))

            .duration(15 * 64 * 20)
            .eut(TierEU.UIV)
            .addTo(UNI);
        // Optically RAM
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 4608),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1), 4608),
                setStackSize(
                    GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, MetaTileEntityIDs.OpticalFiberCable.ID),
                    2048),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Ram), 432),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_SoC), 432),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_NAND), 432),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.TranscendentMetal),
                    4),
                setStackSize(RecipeStacks.exact(1, ItemList.EnergisedTesseract), 64),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Optical), 256),
                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(
                Materials.SpaceTime.getMolten(2880),
                Materials.Infinity.getMolten(4320),
                Materials.SuperconductorUMVBase.getMolten(2880),
                new FluidStack(sb, 5760))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Optically_Compatible_Memory), 1024 * a))

            .duration(10 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Sunnarium, 1), 4608),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1), 4608),
                setStackSize(
                    GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, MetaTileEntityIDs.OpticalFiberCable.ID),
                    2048),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Ram), 432),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_SoC), 432),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_NAND), 432),
                setStackSize(
                    RecipeStacks
                        .generated(1, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.TranscendentMetal),
                    4),
                setStackSize(RecipeStacks.exact(1, ItemList.EnergisedTesseract), 64),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Optical), 256),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(
                Materials.SpaceTime.getMolten(2880),
                Materials.Infinity.getMolten(4320),
                Materials.SuperconductorUMVBase.getMolten(2880),
                new FluidStack(sb, 5760))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticallyCompatibleMemories)
                        : null),
                    64 * a))

            .duration(10 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        // Optically CPU Shell
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalCircuitBoards)
                        : null),
                    64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 1024),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.BOTMIUM, ComponentTypes.DUST), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahAlloy, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VibrantAlloy, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 144),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 40),

                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Optical_Cpu_Containment_Housing), 1024 * a))

            .duration(10 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalCircuitBoards)
                        : null),
                    64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 1024),
                setStackSize(RecipeStacks.gtppComponent(1, MaterialsAlloy.BOTMIUM, ComponentTypes.DUST), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahAlloy, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.VibrantAlloy, 1), 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), 144),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Zinc, 1), 40),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 144))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticalCPUContainmentHousings)
                        : null),
                    64 * a))

            .duration(5 * 64 * 20)
            .eut(TierEU.UIV)
            .addTo(UNI);
        // Optically CPU
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Optical), 768),
                setStackSize(RecipeStacks.exact(1, ItemList.Optical_Cpu_Containment_Housing), 768),
                setStackSize(
                    GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, MetaTileEntityIDs.OpticalFiberCable.ID),
                    768),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 2606), 128),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 128),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.shirabon.getmID()
                                : 10112))
                        : null),
                    128),
                setStackSize(RecipeStacks.gtppComponent(1, STANDALONE.CELESTIAL_TUNGSTEN, ComponentTypes.DUST), 128),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                                ? GGMaterial.enrichedNaquadahAlloy.getmID()
                                : 10110))
                        : null),
                    128),

                GTUtility.getIntegratedCircuit(23))
            .fluidInputs(new FluidStack(sb, 1440))
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Optically_Perfected_CPU), 1024 * a))

            .duration(20 * 64 * 20)
            .eut(TierEU.UMV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Optical), 768),
                setStackSize(RecipeStacks.exact(1, ItemList.Optical_Cpu_Containment_Housing), 768),
                setStackSize(
                    GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, MetaTileEntityIDs.OpticalFiberCable.ID),
                    768),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 2606), 128),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tritanium, 1), 128),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded() ? GGMaterial.shirabon.getmID()
                                : 10112))
                        : null),
                    128),
                setStackSize(RecipeStacks.gtppComponent(1, STANDALONE.CELESTIAL_TUNGSTEN, ComponentTypes.DUST), 128),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                                ? GGMaterial.enrichedNaquadahAlloy.getmID()
                                : 10110))
                        : null),
                    128),

                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 1440))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.Wrap_OpticallyPerfectedCPUs)
                        : null),
                    64 * a))

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
            .itemOutputs(setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_Optical), 1024 * a))

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
                setStackSize(RecipeStacks.exact(a, NHItemList.EngineeringProcessorItemAdvEmeraldCore.item, 0), 1024))

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
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Obsidian, 1), 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CosmicNeutronium, 1), 12),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.StainlessSteel, 1), 9 * 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Lapis, 1), 8 * 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1), 8 * 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Gold, 1), 8 * 64),
                setStackSize(RecipeStacks.exact(1, ItemList.Electric_Motor_LV), 4 * 64),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(setStackSize(GTModHandler.getModItem("GraviSuite", "itemSimpleItem", 1, 3), 64 * a))
            .fluidInputs(
                Materials.Helium.getGas(288 * 64),
                Materials.SolderingAlloy.getMolten(288 * 64),
                Materials.CallistoIce.getMolten(288 * 64)

            )

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
                setStackSize(RecipeStacks.werkstoff(1, OrePrefixes.dust, WerkstoffLoader.Prasiolite), 64),
                setStackSize(RecipeStacks.werkstoff(1, OrePrefixes.dust, WerkstoffLoader.Zirconium), 64),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.ImprintBoard)
                        : null),
                    64 * a))
            .fluidInputs(
                Materials.Oxygen.getGas(144 * 64),
                Materials.SolderingAlloy.getMolten(144 * 64),
                Materials.Hydrogen.getGas(144 * 64)

            )

            .duration(20 * 20)
            .eut(TierEU.LuV)
            .addTo(UNI);
        // HighTemp Carbon Web
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tantalum, 1), 16),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                                ? WerkstoffMaterialPool.Hafnium.getmID()
                                : 11000))
                        : null),
                    4),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHIntergalactic.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.UHTResistantMesh)
                        : null),
                    10 * a))
            .fluidInputs(Materials.Helium.getPlasma(80)

            )

            .duration(100 * 20)
            .eut(TierEU.LuV)
            .addTo(UNI);
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 40),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1), 20),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Tantalum, 1), 16),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.BartWorks.isModLoaded()
                        ? RecipeStacks.exact(
                            1,
                            WerkstoffLoader.items.get(OrePrefixes.dust),
                            (gregtech.api.enums.Mods.GTNHLanthanides.isModLoaded()
                                ? WerkstoffMaterialPool.Hafnia.getmID()
                                : 11002))
                        : null),
                    4),
                GTUtility.getIntegratedCircuit(20))
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHIntergalactic.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.UHTResistantMesh)
                        : null),
                    10 * a))
            .fluidInputs(Materials.Helium.getPlasma(80)

            )

            .duration(100 * 20)
            .eut(TierEU.LuV)
            .addTo(UNI);
        // Iridium Adv Plate
        RecipeBuilder.builder()
            .itemInputs(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem01.INSTANCE, OrePrefixes.plate, Materials.SpaceTime),
                    64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 1), 64),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1), 1024),
                GTUtility.getIntegratedCircuit(24))
            .fluidInputs(new FluidStack(sb, 2304), Materials.Infinity.getMolten(144))
            .itemOutputs(setStackSize(GTModHandler.getModItem("IC2", "itemPartIridium", 1), 2048 * a))

            .duration(100 * 20)
            .eut(TierEU.UHV)
            .addTo(UNI);
        // Inf Chest
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTModHandler.getModItem("avaritiaddons", "InfinityChest", 1), 256 * a))
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Silicon, 1), 256 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Bedrockium, 1), 144 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 1), 144 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1), 144 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 1), 90 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 1), 48 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Americium, 1), 54 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.CosmicNeutronium, 1), 54 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 1), 54 * 256),
                setStackSize(ItemList.Field_Generator_UV.get(1), 18 * 256),
                setStackSize(ItemList.Conveyor_Module_UEV.get(1), 18 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.StainlessSteel, 1), 18 * 256),
                setStackSize(new ItemStack(Blocks.chest, 1), 18 * 256),
                setStackSize(GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 1), 256),
                RecipeStacks.exact(1, NHItemList.EngineeringProcessorItemAdvEmeraldCore.item, 0))

            .duration(256 * 20 * 5)
            .eut(TierEU.UV)
            .addTo(UNI);
        // Solar Panel (UV)
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(ItemList.Cover_SolarPanel_UV.get(1), 256 * a))
            .itemInputs(
                GTUtility.getIntegratedCircuit(16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Steeleaf, 1), 4 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.FierySteel, 1), 4 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Infinity, 1), 8 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1), 8 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Naquadria, 1), 32 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 1), 6 * 256),
                setStackSize(OTHItemList.dustIrOsSmM.get(64), 16 * 256),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.wireGt02, Materials.SuperconductorUHV, 1), 26 * 256),
                setStackSize(GTOreDictUnificator.get("waferPhotonicallyPrepared", 1), 4 * 256))
            .fluidInputs(
                Materials.Sunnarium.getMolten(9216 * 256),
                Materials.Epoxid.getMolten(1152 * 256),
                Materials.Polybenzimidazole.getMolten(1152 * 256),
                Materials.Polytetrafluoroethylene.getMolten(1152 * 256),
                Materials.SolderingAlloy.getMolten(128123 * 256),
                Materials.Neutronium.getMolten(9216 * 256))

            .duration(256 * 20 * 8)
            .eut(TierEU.UMV)
            .addTo(UNI);
        // Dyson Sphere module
        RecipeBuilder.builder()
            .itemOutputs(
                setStackSize(
                    (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHIntergalactic.isModLoaded()
                        ? RecipeStacks.exact(1, ItemList.DysonSwarmModule)
                        : null),
                    256 * a))
            .itemInputs(
                GTUtility.getIntegratedCircuit(7),
                ItemList.Cover_SolarPanel_UV.get(4),
                setStackSize(ItemList.Sensor_UEV.get(1), 4),
                setStackSize(ItemList.Emitter_UEV.get(1), 4),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHIntergalactic.isModLoaded()
                    ? RecipeStacks.exact(16, ItemList.UHTResistantMesh)
                    : null),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UEV, 1), 4 * 16),
                setStackSize(OTHItemList.dustIrOsSmM.get(64), 16 * 16),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.wireGt01, Materials.SuperconductorUMV, 1), 10 * 16))
            .fluidInputs(new FluidStack(sb, 123123))
            .duration(256 * 10 * 8)
            .eut(TierEU.UMV)
            .addTo(UNI);

    }
}
