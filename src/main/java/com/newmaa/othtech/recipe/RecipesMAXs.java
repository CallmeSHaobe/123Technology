package com.newmaa.othtech.recipe;

import static com.dreammaster.item.NHItemList.StargateFramePart;
import static com.newmaa.othtech.recipe.RecipesCircuit.getAstralArray;
import static com.newmaa.othtech.recipe.RecipesComponentAssemblyLineRecipes.getNanites;
import static com.newmaa.othtech.utils.Utils.getEternalSingularity;
import static com.newmaa.othtech.utils.Utils.hasNullItem;
import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.TierEU.RECIPE_MAX;
import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.common.materials.BWMaterials;
import com.newmaa.othtech.common.materials.OTHEntangled;
import com.newmaa.othtech.common.materials.OTHMaterials;
import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.CondensateType;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.NaniteTier;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import tectech.recipe.TecTechRecipeMaps;

public class RecipesMAXs implements IRecipePool {

    private static final int BEC_SECONDS = 50;
    private static final int BEC_TIME_MULTIPLIER = 1000;
    private static final int BEC_CASING_SECONDS = 325000;
    private static final NaniteTier PART_NANITE = NaniteTier.Eternity;
    private static final NaniteTier HATCH_NANITE = NaniteTier.MagMatter;

    public static ItemStack getMM(int amount, OrePrefixes orePrefixes) {
        return GTOreDictUnificator.get(orePrefixes, Materials.MagMatter, amount);
    }

    public static ItemStack getGM(int amount, OrePrefixes orePrefixes) {
        return GTOreDictUnificator.get(orePrefixes, OTHMaterials.Gunmu, amount);
    }

    public static ItemStack getMC(int amount, OrePrefixes orePrefixes) {
        return GTOreDictUnificator.get(orePrefixes, Materials.WhiteDwarfMatter, amount);
    }

    public static ItemStack getUN(int amount, OrePrefixes orePrefixes) {
        return GTOreDictUnificator.get(orePrefixes, Materials.Universium, amount);
    }

    public static ItemStack getET(int amount, OrePrefixes orePrefixes) {
        return GTOreDictUnificator.get(orePrefixes, Materials.Eternity, amount);
    }

    @Override
    public void loadRecipes() {
        OTHEntangled.registerRecipes();
        loadStandaloneRecipes();
        loadBecParts();
        loadBecHatches();
    }

    private void loadStandaloneRecipes() {
        final RecipeMap<?> Assembler = RecipeMaps.assemblerRecipes;
        final RecipeMap<?> Tower = RecipeMaps.distillationTowerRecipes;
        RecipeBuilder.builder()
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.stick, Materials.MHDCSM, 1),
                BWMaterials.Gunmu.get(OrePrefixes.wireFine, 16))
            .itemOutputs(ItemList.MAX_Coil.get(1))
            .eut(RECIPE_MAX)
            .duration(10 * SECONDS)
            .addTo(Assembler);

        GTValues.RA.stdBuilder()
            .itemInputs(
                BWMaterials.Gunmu.get(OrePrefixes.plate, 8),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.MHDCSM, 8),
                GTUtility.getIntegratedCircuit(8))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockcasingsNH", 1, 14))
            .duration(50)
            .eut(TierEU.LV)
            .addTo(Assembler);

        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 11234))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockcasingsNH", 1, 14),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 2, 2611))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.radoxpoly"), 576))
            .duration(50)
            .eut(TierEU.UXV)
            .addTo(Assembler);

        GTValues.RA.stdBuilder()
            .itemOutputs(GTOreDictUnificator.get(OrePrefixes.wrapCircuit, Materials.MAX, 1))
            .itemInputs(
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 16),
                GTUtility.getIntegratedCircuit(16))
            .fluidInputs(FluidRegistry.getFluidStack("molten.solderingalloy", 72))
            .duration(30 * 20)
            .eut(TierEU.LV)
            .addTo(Assembler);

        GTValues.RA.stdBuilder()
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("molten.universium"), 24 * 144))
            .fluidOutputs(
                BWLiquids.Galaxy.getFluidOrGas(48 * 144),
                new FluidStack(FluidRegistry.getFluidID("molten.quantum"), 16 * 144))
            .duration(123 * 20)
            .requiresLowGravity()
            .eut(512000000)
            .addTo(Tower);
    }

    private void loadBecParts() {
        addPartBec(
            ItemList.Electric_Motor_MAX.get(1),
            new Object[] { getEternalSingularity(1), getGM(18, OrePrefixes.stickLong), getGM(12, OrePrefixes.ring),
                getGM(48, OrePrefixes.round), getGM(64, OrePrefixes.wireFine), getGM(64, OrePrefixes.wireFine),
                getUN(64, OrePrefixes.wireFine), getUN(64, OrePrefixes.wireFine), getET(64, OrePrefixes.wireFine),
                getET(64, OrePrefixes.wireFine), getMM(64, OrePrefixes.wireFine), getMM(64, OrePrefixes.wireFine),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockcasings5", 1, 13), 2),
                getNanites(1, Materials.Universium), getNanites(4, Materials.WhiteDwarfMatter),
                getNanites(4, Materials.BlackDwarfMatter) });

        addPartBec(
            ItemList.Electric_Pump_MAX.get(1),
            new Object[] { ItemList.Electric_Motor_MAX.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, OTHMaterials.Gunmu, 4),
                GTOreDictUnificator.get(OrePrefixes.screw, OTHMaterials.Gunmu, 16),
                GTOreDictUnificator.get(OrePrefixes.ring, OTHMaterials.Gunmu, 64),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.Universium, 64),
                GTOreDictUnificator.get(OrePrefixes.ring, Materials.MHDCSM, 64),
                GTOreDictUnificator.get(OrePrefixes.rotor, OTHMaterials.Gunmu, 8),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Universium, 8),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.MHDCSM, 8),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 2, 13),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.RadoxPolymer, 8) });

        addPartBec(
            ItemList.Electric_Piston_MAX.get(1),
            new Object[] { ItemList.Electric_Motor_MAX.get(1),
                GTOreDictUnificator.get(OrePrefixes.plate, OTHMaterials.Gunmu, 6),
                GTOreDictUnificator.get(OrePrefixes.ring, OTHMaterials.Gunmu, 8),
                GTOreDictUnificator.get(OrePrefixes.round, OTHMaterials.Gunmu, 64),
                GTOreDictUnificator.get(OrePrefixes.stick, OTHMaterials.Gunmu, 8),
                GTOreDictUnificator.get(OrePrefixes.gearGt, OTHMaterials.Gunmu, 4),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.gearGt, Materials.Eternity, 4),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, OTHMaterials.Gunmu, 8),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Universium, 8),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Eternity, 8),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 4, 13),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.RadoxPolymer, 8) });

        addPartBec(
            ItemList.Conveyor_Module_MAX.get(1),
            new Object[] { ItemList.Electric_Motor_MAX.get(2),
                GTOreDictUnificator.get(OrePrefixes.plate, OTHMaterials.Gunmu, 4),
                GTOreDictUnificator.get(OrePrefixes.ring, OTHMaterials.Gunmu, 16),
                GTOreDictUnificator.get(OrePrefixes.ring, OTHMaterials.Gunmu, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 2),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 4, 13),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Kevlar, 16),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.BlackDwarfMatter, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.RadoxPolymer, 32),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.WhiteDwarfMatter, 4),
                GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.RadoxPolymer, 64),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 2) });

        addPartBec(
            ItemList.Robot_Arm_MAX.get(1),
            new Object[] { GTOreDictUnificator.get(OrePrefixes.stick, OTHMaterials.Gunmu, 8),
                GTOreDictUnificator.get(OrePrefixes.gearGt, OTHMaterials.Gunmu, 8),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, OTHMaterials.Gunmu, 16),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 2),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UXV, 4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 8), ItemList.Electric_Motor_MAX.get(2),
                ItemList.Electric_Piston_MAX.get(1), new Object[] { OrePrefixes.circuit.get(Materials.MAX), 2 },
                new Object[] { OrePrefixes.circuit.get(Materials.UXV), 4 },
                new Object[] { OrePrefixes.circuit.get(Materials.UMV), 8 },
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 4, 13),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 2) });

        addPartBec(
            ItemList.Sensor_MAX.get(1),
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, OTHMaterials.Gunmu, 4),
                ItemList.Electric_Motor_MAX.get(1), GTOreDictUnificator.get(OrePrefixes.plate, OTHMaterials.Gunmu, 16),
                OTHItemList.glassSingularityM.get(4), GTModHandler.getModItem("dreamcraft", "ChromaticLens", 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 4),
                GTOreDictUnificator.get(OrePrefixes.foil, OTHMaterials.Gunmu, 64),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Universium, 64),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Eternity, 64),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.MHDCSM, 64),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 2) },
            CondensateType.Space.getEntangled(448 * INGOTS));

        addPartBec(
            ItemList.Emitter_MAX.get(1),
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, OTHMaterials.Gunmu, 4),
                ItemList.Electric_Motor_MAX.get(1), GTOreDictUnificator.get(OrePrefixes.stick, OTHMaterials.Gunmu, 16),
                OTHItemList.glassSingularityM.get(4), GTModHandler.getModItem("dreamcraft", "ChromaticLens", 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 4),
                GTOreDictUnificator.get(OrePrefixes.foil, OTHMaterials.Gunmu, 64),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Universium, 64),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.Eternity, 64),
                GTOreDictUnificator.get(OrePrefixes.foil, Materials.MHDCSM, 64),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 8, 13),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 1),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 2),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 2) },
            CondensateType.Time.getEntangled(448 * INGOTS));

        addPartBec(
            ItemList.Field_Generator_MAX.get(1),
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, OTHMaterials.Gunmu, 4), getAstralArray(1),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.MagMatter, 12),
                OTHItemList.machineSingularityM.get(4), OTHItemList.glassSingularityM.get(4),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 4), ItemList.Emitter_MAX.get(4),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MAX, 4),
                GTOreDictUnificator.get(OrePrefixes.wireFine, OTHMaterials.Gunmu, 64),
                GTOreDictUnificator.get(OrePrefixes.wireFine, OTHMaterials.Gunmu, 64),
                GTOreDictUnificator.get(OrePrefixes.wireFine, OTHMaterials.Gunmu, 64),
                GTOreDictUnificator.get(OrePrefixes.wireFine, OTHMaterials.Gunmu, 64),
                GTModHandler.getModItem("gregtech", "gt.blockcasings5", 16, 13),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.Universium, 4),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.WhiteDwarfMatter, 8),
                GTOreDictUnificator.get(OrePrefixes.nanite, Materials.BlackDwarfMatter, 8) },
            CondensateType.Eternity.getEntangled(448 * INGOTS));
    }

    private void loadBecHatches() {
        addHatchBec(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23522),
            new Object[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 11234),
                GTModHandler.getModItem("GoodGenerator", "compactFusionCoil", 64, 4),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 15300),
                GTModHandler.getModItem("tectech", "gt.blockcasingsTT", 64), getMM(64, OrePrefixes.wireFine),
                getMC(64, OrePrefixes.plateDense),
                new Object[] { OrePrefixes.circuit.get(Materials.TranscendentMetal), 64 },
                ItemList.EnergisedTesseract.get(64), GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 23524) });

        addHatchBec(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23523),
            new Object[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 13106),
                GTModHandler.getModItem("kekztech", "kekztech_lapotronicenergyunit_block", 64),
                GTModHandler.getModItem("kekztech", "kekztech_lapotronicenergyunit_block", 64, 10),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15497),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 15465),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 64, 2611),
                GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.6", 16, 1),
                GTModHandler.getModItem("gregtech", "gt.blockcasings", 1, 15),
                GTModHandler.getModItem("tectech", "gt.blockcasingsTT", 64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32165),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32417) });

        addHatchBec(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23524),
            new Object[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 286),
                ItemList.Electric_Pump_MAX.get(64), ItemList.Sensor_MAX.get(64),
                GTOreDictUnificator.get(OrePrefixes.lens, Materials.Diamond, 64),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 16, 2611), GTUtility.getIntegratedCircuit(7) });

        addHatchBec(
            GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 286),
            new Object[] { GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 11234),
                GTModHandler.getModItem("GoodGenerator", "compactFusionCoil", 16, 2),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                GTModHandler.getModItem("tectech", "gt.blockcasingsTT", 16), getUN(64, OrePrefixes.wireFine),
                getMC(4, OrePrefixes.plateDense), new Object[] { OrePrefixes.circuit.get(Materials.MAX), 16 },
                getNanites(4, Materials.Eternity), ItemList.Circuit_Chip_YPIC.get(2), ItemList.MAX_Coil.get(4),
                ItemList.Electric_Pump_MAX.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1), ItemList.Reactor_Coolant_Sp_6.get(1),
                ItemList.Reactor_Coolant_Sp_6.get(1) });

        addHatchBec(
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 13),
            new Object[] { GTOreDictUnificator.get(OrePrefixes.frameGt, OTHMaterials.Gunmu, 1),
                StargateFramePart.get(1), GTOreDictUnificator.get(OrePrefixes.plateDense, Materials.MHDCSM, 12),
                GTOreDictUnificator.get(OrePrefixes.plateDense, OTHMaterials.Gunmu, 12), ItemList.Robot_Arm_MAX.get(8),
                ItemList.Electric_Piston_MAX.get(10), ItemList.Electric_Motor_MAX.get(16),
                GTOreDictUnificator.get(OrePrefixes.gearGt, OTHMaterials.Gunmu, 4),
                GTOreDictUnificator.get(OrePrefixes.gearGtSmall, OTHMaterials.Gunmu, 16), getAstralArray(1),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockcasings5", 1, 13), 64),
                OTHItemList.machineSingularityM.get(12), getNanites(64, Materials.Universium),
                getNanites(64, Materials.Eternity) },
            BEC_CASING_SECONDS);
    }

    private static void addPartBec(ItemStack output, Object[] inputs) {
        addBec(
            output,
            inputs,
            partCondensate(OTHEntangled.SuperconductorUIV.getEntangled(5760)),
            PART_NANITE,
            TierEU.RECIPE_UXV);
    }

    private static void addPartBec(ItemStack output, Object[] inputs, FluidStack third) {
        addBec(output, inputs, partCondensate(third), PART_NANITE, TierEU.RECIPE_UXV);
    }

    private static void addHatchBec(ItemStack output, Object[] inputs) {
        addBec(output, inputs, hatchCondensate(), HATCH_NANITE, RECIPE_MAX);
    }

    private static void addHatchBec(ItemStack output, Object[] inputs, int seconds) {
        addBec(output, inputs, hatchCondensate(), HATCH_NANITE, RECIPE_MAX, seconds);
    }

    private static void addBec(ItemStack output, Object[] inputs, FluidStack[] condensates, NaniteTier tier, long eut) {
        addBec(output, inputs, condensates, tier, eut, BEC_SECONDS * BEC_TIME_MULTIPLIER);
    }

    private static void addBec(ItemStack output, Object[] inputs, FluidStack[] condensates, NaniteTier tier, long eut,
        int seconds) {
        if (output == null) {
            OTHTechnology.LOG.error("Skipping BEC recipe with null output");
            return;
        }
        if (inputs == null || hasNullItem(inputs)) {
            final StringBuilder detail = new StringBuilder();
            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i] == null) {
                    detail.append(" [")
                        .append(i)
                        .append("]=null");
                } else if (inputs[i] instanceof ItemStack stack) {
                    if (stack.getItem() == null) detail.append(" [")
                        .append(i)
                        .append("]=invalid-item");
                } else if (inputs[i] instanceof Object[]group) {
                    if (hasNullItem(group)) detail.append(" [")
                        .append(i)
                        .append("]=null-in-group");
                }
            }
            OTHTechnology.LOG
                .error("Skipping BEC recipe for {} due to null inputs:{}", output.getDisplayName(), detail);
            return;
        }
        if (condensates == null || condensates.length == 0) {
            OTHTechnology.LOG.error("Skipping BEC recipe for {} due to empty condensates", output.getDisplayName());
            return;
        }
        for (int i = 0; i < condensates.length; i++) {
            final FluidStack condensate = condensates[i];
            if (condensate == null || condensate.getFluid() == null) {
                OTHTechnology.LOG
                    .error("Skipping BEC recipe for {} due to null condensate [{}]", output.getDisplayName(), i);
                return;
            }
        }

        final ItemStack[] stacks = flatten(inputs);
        if (stacks.length == 0) {
            OTHTechnology.LOG.error("Skipping BEC recipe for {} due to empty inputs", output.getDisplayName());
            return;
        }

        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(stacks)
            .fluidInputs(condensates)
            .itemOutputs(output)
            .metadata(GTRecipeConstants.NANITE_TIERS, nanites(stacks.length, tier))
            .duration(seconds * SECONDS)
            .eut(eut)
            .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
    }

    private static FluidStack[] partCondensate(FluidStack third) {
        return new FluidStack[] { OTHEntangled.Void.getEntangled(16000), OTHEntangled.Stars.getEntangled(2304), third,
            OTHEntangled.SuperconductorUMV.getEntangled(5760) };
    }

    private static FluidStack[] hatchCondensate() {
        return new FluidStack[] { CondensateType.MagMatter.getEntangled(128 * INGOTS),
            CondensateType.Eternity.getEntangled(128 * INGOTS), CondensateType.Universium.getEntangled(64 * INGOTS),
            CondensateType.Space.getEntangled(64 * INGOTS) };
    }

    private static NaniteTier[] nanites(int length, NaniteTier tier) {
        final NaniteTier[] tiers = new NaniteTier[Math.max(0, length)];
        Arrays.fill(tiers, tier);
        return tiers;
    }

    private static ItemStack[] flatten(Object[] inputs) {
        final List<ItemStack> stacks = new ArrayList<>();
        for (Object input : inputs) {
            final ItemStack stack = toStack(input);
            if (stack != null) {
                stacks.add(stack);
            }
        }
        return stacks.toArray(new ItemStack[0]);
    }

    private static ItemStack toStack(Object input) {
        if (input instanceof ItemStack stack) {
            return stack;
        }
        if (!(input instanceof Object[]group)) {
            return null;
        }

        ItemStack base = null;
        int amount = 0;
        for (Object element : group) {
            if (element instanceof ItemStack stack && base == null) {
                base = stack;
            } else if (element instanceof Number number) {
                amount = number.intValue();
            }
        }

        if (base == null) {
            return null;
        }
        return amount > 0 ? GTUtility.copyAmountUnsafe(amount, base) : base;
    }
}
