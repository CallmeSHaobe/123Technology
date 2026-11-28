package com.newmaa.othtech.recipe;

import static bartworks.system.material.WerkstoffLoader.Ruridit;
import static com.newmaa.othtech.common.recipemap.Recipemaps.NASA;
import static com.newmaa.othtech.utils.Utils.isStackValid;
import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.Mods.GalacticraftAmunRa;
import static gregtech.api.enums.Mods.GalacticraftCore;
import static gregtech.api.enums.Mods.GalacticraftMars;
import static gregtech.api.enums.Mods.GalaxySpace;
import static gregtech.api.enums.Mods.NewHorizonsCoreMod;
import static gregtech.api.enums.TierEU.RECIPE_EV;
import static gregtech.api.enums.TierEU.RECIPE_LV;
import static gregtech.api.enums.TierEU.RECIPE_LuV;
import static gregtech.api.enums.TierEU.RECIPE_ZPM;
import static gregtech.api.util.GTRecipeBuilder.INGOTS;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.item.NHItemList;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;
import com.newmaa.othtech.utils.Utils;

import galaxyspace.core.recipe.RocketRecipes;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipe.RecipeAssemblyLine;
import gregtech.api.util.GTRecipeBuilder;
import gtPlusPlus.core.material.MaterialsAlloy;
import ic2.core.IC2;
import micdoodle8.mods.galacticraft.api.recipe.INasaWorkbenchRecipe;

public class RecipesNASA implements IRecipePool {

    private static final int ROCKET_DURATION = 100;
    private static final int ROCKET_EUT = 100;
    private static final int PLATING_BATCH_SIZE = 64;
    private static final int BULK_PLATING_DURATION = 15 * SECONDS * 32;

    @Override
    public void loadRecipes() {

        ItemStack schematic = RecipeStacks.exact(0, NHItemList.SchematicsTier1.item, 0);
        if (!isStackValid(schematic)) return;

        ItemStack computer = GTModHandler.getModItem(GalaxySpace.ID, "item.RocketControlComputer", 1, 1);

        // Tier 1 rocket: one stack per ingredient to avoid inflating GT's recipe index.
        ItemStack rocket = GTModHandler.getModItem(GalacticraftCore.ID, "item.spaceship", 1, 0);
        ItemStack[] rocketInputs = { GTModHandler.getModItem(GalacticraftCore.ID, "item.noseCone", 1),
            GTModHandler.getModItem(GalacticraftCore.ID, "item.heavyPlating", 8),
            GTModHandler.getModItem(GalaxySpace.ID, "item.ModuleLander", 1), computer,
            GTModHandler.getModItem(GalaxySpace.ID, "item.ModuleSmallFuelCanister", 2),
            GTModHandler.getModItem(GalacticraftCore.ID, "item.rocketFins", 4),
            GTModHandler.getModItem(GalacticraftCore.ID, "item.engine", 1) };
        if (isStackValid(rocket) && Arrays.stream(rocketInputs)
            .allMatch(Utils::isStackValid)) {
            OTHTechnology.LOG.info("NASA: registering tier 1 rocket");
            GTRecipeBuilder.builder()
                .itemInputs(rocketInputs)
                .itemOutputs(rocket)
                .special(schematic)
                .duration(ROCKET_DURATION)
                .eut(ROCKET_EUT)
                .addTo(NASA);
        }
        if (GalaxySpace.isModLoaded()) registerHigherTierRockets();

        // Mining drone.
        ItemStack drone = (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTNHIntergalactic.isModLoaded()
            ? RecipeStacks.exact(1, ItemList.MiningDroneLV)
            : null);
        ItemStack[] droneInputs = { ItemList.Sensor_LV.get(1),
            GTOreDictUnificator.get(OrePrefixes.toolHeadDrill, Materials.Titanium, 1), ItemList.Robot_Arm_LV.get(1),
            GTModHandler.getModItem(GalacticraftCore.ID, "item.heavyPlating", 14), computer,
            ItemList.Field_Generator_LV.get(1), GTModHandler.getModItem(GalacticraftCore.ID, "item.engine", 2) };
        if (isStackValid(drone) && Arrays.stream(droneInputs)
            .allMatch(Utils::isStackValid)) {
            OTHTechnology.LOG.info("NASA: registering mining drone");
            GTRecipeBuilder.builder()
                .itemInputs(droneInputs)
                .itemOutputs(drone)
                .special(schematic)
                .duration(100)
                .eut(100)
                .addTo(NASA);
        }

        // OTH Suicide Drone
        GTRecipeBuilder.builder()
            .itemOutputs(OTHItemList.DroneLaunchPlatform.get(1))
            .itemInputs(
                ItemList.Hull_IV.get(1),
                ItemList.Sensor_EV.get(1),
                ItemList.Emitter_EV.get(1),
                ItemList.Cover_SolarPanel_MV.get(1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 2))
            .fluidInputs(Materials.Iridium.getMolten(144))
            .special(RecipeStacks.exact(0, NHItemList.SchematicsTier3.item, 0))
            .duration(10 * SECONDS)
            .eut(RECIPE_EV)
            .addTo(NASA);
        GTRecipeBuilder.builder()
            .itemOutputs(OTHItemList.SuicideDronePig.get(1))
            .special(RecipeStacks.exact(0, NHItemList.SchematicsTier1.item, 0))
            .itemInputs(
                OTHItemList.SuicideDronePig.get(0),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Steel, 4),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.LV, 1),
                ItemList.Sensor_LV.get(1),
                ItemList.Emitter_LV.get(1),
                new ItemStack(Items.porkchop, 1),
                new ItemStack(Items.gunpowder, 2))
            .eut(RECIPE_LV)
            .duration(50)
            .addTo(NASA);
        GTRecipeBuilder.builder()
            .itemOutputs(OTHItemList.SuicideDroneAngelica.get(1))
            .special(RecipeStacks.exact(0, NHItemList.SchematicsTier3.item, 0))
            .itemInputs(
                OTHItemList.SuicideDroneAngelica.get(0),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.RedstoneAlloy, 4),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Steel, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.MV, 1),
                ItemList.Sensor_MV.get(1),
                ItemList.Emitter_MV.get(1),
                new ItemStack(Blocks.tnt, 2))
            .eut(RECIPE_EV)
            .duration(50)
            .addTo(NASA);
        GTRecipeBuilder.builder()
            .itemOutputs(OTHItemList.SuicideDroneNvidia.get(1))
            .special(RecipeStacks.exact(0, NHItemList.SchematicsTier5.item, 0))
            .itemInputs(
                OTHItemList.SuicideDroneNvidia.get(0),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.NaquadahAlloy, 4),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.DamascusSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.StainlessSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.EV, 1),
                ItemList.Sensor_EV.get(1),
                ItemList.Emitter_EV.get(1),
                OTHItemList.NukeThrowable.get(1))
            .eut(RECIPE_LuV)
            .duration(50)
            .addTo(NASA);
        GTRecipeBuilder.builder()
            .itemOutputs(OTHItemList.SuicideDroneNuclear.get(1))
            .special(RecipeStacks.exact(0, NHItemList.SchematicsTier8.item, 0))
            .itemInputs(
                OTHItemList.SuicideDroneNuclear.get(0),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 4),
                GTOreDictUnificator.get(OrePrefixes.rotor, Materials.Iridium, 1),
                GTOreDictUnificator.get(OrePrefixes.stickLong, Materials.TungstenSteel, 1),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.ZPM, 3),
                ItemList.Sensor_LuV.get(1),
                ItemList.Emitter_LuV.get(1),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GoodGenerator.isModLoaded()
                    ? RecipeStacks.exact(1, Loaders.advancedRadiationProtectionPlate, 0)
                    : null),
                ItemList.Cover_AdvancedRedstoneReceiver.get(1),
                GTModHandler.getModItem(IC2.MODID, "blockGenerator", 1, 5),
                ItemList.RodNaquadah32.get(2))
            .fluidInputs(Materials.Polybenzimidazole.getMolten(288))
            .eut(RECIPE_ZPM)
            .duration(50)
            .addTo(NASA);

        // Heavy plating.
        ItemStack plating = GTModHandler.getModItem(GalacticraftCore.ID, "item.heavyPlating", 1);
        Fluid solder = FluidRegistry.getFluid(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140));
        if (isStackValid(plating) && solder != null) {
            OTHTechnology.LOG.info("NASA: registering heavy plating");
            GTRecipeBuilder.builder()
                .itemInputsUnsafe(
                    setStackSize(Materials.Bronze.getPlates(1), 144),
                    setStackSize(Materials.Aluminium.getPlates(1), 144),
                    setStackSize(Materials.Steel.getPlates(1), 144))
                .itemOutputs(new ItemStack(plating.getItem(), 64))
                .fluidInputs(new FluidStack(solder, 144))
                .special(schematic)
                .duration(240)
                .eut(1920)
                .addTo(NASA);
        }

        registerHigherTierDrones();
        registerBulkPlatingRecipes(plating);
    }

    private static void registerHigherTierDrones() {
        ItemList[] drones = { ItemList.MiningDroneMV, ItemList.MiningDroneHV, ItemList.MiningDroneEV,
            ItemList.MiningDroneIV, ItemList.MiningDroneLuV, ItemList.MiningDroneZPM, ItemList.MiningDroneUV,
            ItemList.MiningDroneUHV, ItemList.MiningDroneUEV, ItemList.MiningDroneUIV, ItemList.MiningDroneUMV };
        // The assembly line stores expanded alternatives, not the original circuit ore-dictionary key.
        Materials[] assemblyLineCircuits = { Materials.LuV, Materials.ZPM, Materials.ZPM, Materials.UV, Materials.UHV,
            Materials.UEV, Materials.UIV, Materials.UMV, Materials.UXV };
        for (int i = 0; i < drones.length; i++) {
            ItemList drone = drones[i];
            if (!drone.hasBeenSet()) continue;
            ItemStack output = drone.get(1);
            ItemStack schematic = GTModHandler
                .getModItem(NewHorizonsCoreMod.ID, "SchematicsTier" + Math.min(i + 2, 8), 0, 0);
            if (!isStackValid(output) || !isStackValid(schematic)) {
                OTHTechnology.LOG.warn("NASA: missing output or schematic for {}", drone);
                continue;
            }

            GTRecipeBuilder builder = GTRecipeBuilder.builder()
                .special(schematic);
            if (i < 2) {
                GTRecipe source = RecipeMaps.assemblerRecipes.getAllRecipes()
                    .stream()
                    .filter(
                        recipe -> recipe.mOutputs.length == 1 && isStackValid(recipe.mOutputs[0])
                            && output.isItemEqual(recipe.mOutputs[0]))
                    .findFirst()
                    .orElse(null);
                if (source == null) {
                    OTHTechnology.LOG.warn("NASA: missing assembler recipe for {}", drone);
                    continue;
                }
                ItemStack[] inputs = mergeInputs(Arrays.asList(source.mInputs));
                if (inputs.length == 0) {
                    OTHTechnology.LOG.warn("NASA: invalid assembler inputs for {}", drone);
                    continue;
                }
                builder.itemInputsUnsafe(inputs)
                    .itemOutputs(source.mOutputs[0].copy())
                    .fluidInputs(copyFluids(source.mFluidInputs))
                    .duration(source.mDuration)
                    .eut(source.mEUt);
            } else {
                RecipeAssemblyLine source = RecipeAssemblyLine.sAssemblylineRecipes.stream()
                    .filter(recipe -> isStackValid(recipe.mOutput) && output.isItemEqual(recipe.mOutput))
                    .findFirst()
                    .orElse(null);
                if (source == null || source.mInputs.length <= 3 || !isStackValid(source.mInputs[3])) {
                    OTHTechnology.LOG.warn("NASA: missing assembly line recipe or circuit for {}", drone);
                    continue;
                }
                ItemStack[] parts = source.mInputs.clone();
                ItemStack circuit = parts[3];
                parts[3] = null;
                ItemStack[] merged = mergeInputs(Arrays.asList(parts));
                if (merged.length == 0) {
                    OTHTechnology.LOG.warn("NASA: invalid assembly line inputs for {}", drone);
                    continue;
                }
                // The ore-dictionary overload keeps large stacks, including UMV's 128 lightweight plates.
                Object[] inputs = Arrays.copyOf(merged, merged.length + 1, Object[].class);
                inputs[merged.length] = new Object[] { OrePrefixes.circuit.get(assemblyLineCircuits[i - 2]),
                    circuit.stackSize };
                builder.itemInputs(inputs)
                    .itemOutputs(source.mOutput.copy())
                    .fluidInputs(copyFluids(source.mFluidInputs))
                    .duration(source.mDuration)
                    .eut(source.mEUt);
            }
            if (builder.addTo(NASA)
                .isEmpty()) OTHTechnology.LOG.warn("NASA: {} was not registered", drone);
        }
    }

    private static FluidStack[] copyFluids(FluidStack[] fluids) {
        return Arrays.stream(fluids)
            .map(FluidStack::copy)
            .toArray(FluidStack[]::new);
    }

    private static void registerHigherTierRockets() {
        List<List<INasaWorkbenchRecipe>> recipesByTier = Arrays.asList(
            RocketRecipes.getRocketT2Recipes(),
            RocketRecipes.getRocketT3Recipes(),
            RocketRecipes.getRocketT4Recipes(),
            RocketRecipes.getRocketT5Recipes(),
            RocketRecipes.getRocketT6Recipes(),
            RocketRecipes.getRocketT7Recipes(),
            RocketRecipes.getRocketT8Recipes());
        for (int i = 0; i < recipesByTier.size(); i++) {
            int tier = i + 2;
            INasaWorkbenchRecipe basicRecipe = recipesByTier.get(i)
                .stream()
                .filter(recipe -> {
                    ItemStack output = recipe.getRecipeOutput();
                    return isStackValid(output) && output.getItemDamage() == 0;
                })
                .findFirst()
                .orElse(null);
            ItemStack schematic = GTModHandler.getModItem(NewHorizonsCoreMod.ID, "SchematicsTier" + tier, 0, 0);
            if (basicRecipe == null || !isStackValid(schematic)) {
                OTHTechnology.LOG
                    .warn("NASA: skipping T{} rocket because its workbench recipe or schematic is missing", tier);
                continue;
            }

            ItemStack[] inputs = mergeInputs(new TreeMap<>(basicRecipe.getRecipeInput()).values());
            if (inputs.length == 0 || GTRecipeBuilder.builder()
                .itemInputsUnsafe(inputs)
                .itemOutputs(
                    basicRecipe.getRecipeOutput()
                        .copy())
                .special(schematic)
                .duration(ROCKET_DURATION)
                .eut(ROCKET_EUT)
                .addTo(NASA)
                .isEmpty()) {
                OTHTechnology.LOG.warn("NASA: T{} basic rocket was not registered", tier);
            }
        }
    }

    private static ItemStack[] mergeInputs(Iterable<ItemStack> inputs) {
        // Slots can be empty; only combine matching items with the same metadata and NBT.
        List<ItemStack> merged = new ArrayList<>();
        for (ItemStack input : inputs) {
            if (input == null) continue;
            if (!isStackValid(input) || input.stackSize <= 0) return new ItemStack[0];
            ItemStack existing = null;
            for (ItemStack stack : merged) {
                if (stack.isItemEqual(input) && ItemStack.areItemStackTagsEqual(stack, input)) {
                    existing = stack;
                    break;
                }
            }
            if (existing == null) merged.add(input.copy());
            else setStackSize(existing, existing.stackSize + input.stackSize);
        }
        return merged.toArray(new ItemStack[0]);
    }

    private static void registerBulkPlatingRecipes(ItemStack tier1) {
        ItemStack tier2 = GTModHandler.getModItem(GalacticraftMars.ID, "item.null", 1, 3);
        ItemStack tier3 = GTModHandler.getModItem(GalacticraftMars.ID, "item.itemBasicAsteroids", 1, 0);
        ItemStack tier4 = NHItemList.HeavyDutyPlateTier4.get();
        ItemStack tier5 = NHItemList.HeavyDutyPlateTier5.get();
        ItemStack tier6 = NHItemList.HeavyDutyPlateTier6.get();
        ItemStack tier7 = NHItemList.HeavyDutyPlateTier7.get();
        ItemStack tier8 = NHItemList.HeavyDutyPlateTier8.get();

        // Keep the preceding armor tier. Flatten only this tier's compressed plates to ordinary plates.
        // EU/t and fluid come from the alloy ingot recipe; the final armor plate replaces the ingot as output.
        registerBulkPlating(
            2,
            tier2,
            RECIPE_EV,
            Materials.TungstenSteel.getMolten(INGOTS / 2),
            tier1,
            Materials.MeteoricIron.getPlates(2));
        registerBulkPlating(
            3,
            tier3,
            TierEU.RECIPE_IV,
            Materials.Platinum.getMolten(INGOTS / 2),
            tier2,
            Materials.Desh.getPlates(4));

        // Six ice plates come from three compressed Ledox and three compressed Callisto Ice, at 3:1 each.
        registerBulkPlating(
            4,
            tier4,
            RECIPE_LuV,
            FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140), 36),
            tier3,
            Materials.Ledox.getPlates(9),
            Materials.CallistoIce.getPlates(9),
            Ruridit.get(OrePrefixes.bolt, 4));

        // T5–T8 use the upstream assembly line's solder fallback when Indalloy 140 is absent.
        String indalloy = RecipeFluids.gtppMoltenName(MaterialsAlloy.INDALLOY_140);
        String solder = FluidRegistry.getFluid(indalloy) != null ? indalloy : "molten.solderingalloy";
        registerBulkPlating(
            5,
            tier5,
            TierEU.RECIPE_ZPM,
            FluidRegistry.getFluidStack(solder, 72),
            tier4,
            Materials.Quantium.getPlates(24),
            GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Europium, 8));
        // Ten lead/oriharukon plates come from five compressed plates of each metal, at 3:1 each.
        registerBulkPlating(
            6,
            tier6,
            TierEU.RECIPE_UV,
            FluidRegistry.getFluidStack(solder, 144),
            tier5,
            Materials.Lead.getPlates(15),
            Materials.Oriharukon.getPlates(15),
            GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Tritanium, 8));
        registerBulkPlating(
            7,
            tier7,
            TierEU.RECIPE_UHV,
            FluidRegistry.getFluidStack(solder, 288),
            tier6,
            Materials.MysteriousCrystal.getPlates(36),
            GTOreDictUnificator.get(OrePrefixes.bolt, Materials.Neutronium, 10));
        registerBulkPlating(
            8,
            tier8,
            TierEU.RECIPE_UEV,
            FluidRegistry.getFluidStack(solder, 576),
            tier7,
            Materials.BlackPlutonium.getPlates(42),
            GTOreDictUnificator.get(OrePrefixes.bolt, Materials.BlackPlutonium, 12));

        if (GalacticraftAmunRa.isModLoaded()) {
            ItemStack lightweight = GTModHandler.getModItem(GalacticraftAmunRa.ID, "item.baseItem", 1, 15);
            registerBulkPlating(
                9,
                lightweight,
                TierEU.RECIPE_UEV,
                Materials.RadoxPolymer.getMolten(4 * INGOTS),
                tier8,
                Materials.Kevlar.getPlates(14),
                GTOreDictUnificator.get(OrePrefixes.screw, Materials.TengamAttuned, 12));
        }
    }

    private static void registerBulkPlating(int tier, ItemStack output, long eut, FluidStack fluid,
        ItemStack... inputs) {
        // Lightweight (T9) uses the highest schematic accepted by the hatch.
        ItemStack schematic = GTModHandler
            .getModItem(NewHorizonsCoreMod.ID, "SchematicsTier" + Math.min(tier, 8), 0, 0);
        if (!isStackValid(output) || !isStackValid(schematic)
            || fluid == null
            || !Arrays.stream(inputs)
                .allMatch(Utils::isStackValid)) {
            OTHTechnology.LOG.warn("NASA: skipping T{} bulk plating because a required item or fluid is missing", tier);
            return;
        }

        ItemStack[] batchInputs = Arrays.stream(inputs)
            .map(input -> setStackSize(input.copy(), input.stackSize * PLATING_BATCH_SIZE))
            .toArray(ItemStack[]::new);
        if (GTRecipeBuilder.builder()
            .itemInputsUnsafe(batchInputs)
            .itemOutputs(setStackSize(output.copy(), output.stackSize * PLATING_BATCH_SIZE))
            .fluidInputs(fluid)
            .special(schematic)
            .duration(BULK_PLATING_DURATION)
            .eut(eut)
            .addTo(NASA)
            .isEmpty()) {
            OTHTechnology.LOG.warn("NASA: T{} bulk plating was not registered", tier);
        }
    }
}
