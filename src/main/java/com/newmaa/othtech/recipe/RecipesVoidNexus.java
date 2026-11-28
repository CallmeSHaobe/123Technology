package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.setStackSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.Nxer.TwistSpaceTechnology.common.material.MaterialsTST;
import com.gtnewhorizons.aspectrecipeindex.ModItems;
import com.gtnewhorizons.aspectrecipeindex.common.items.ItemAspect;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.blocks.nailong.NailongCasingMeta;
import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusFuelProfiles;
import com.newmaa.othtech.common.materials.OTHMaterials;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.voidnexus.VoidNexusCalculator;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import fox.spiteful.avaritia.items.LudicrousItems;
import gregtech.api.casing.Casings;
import gregtech.api.enums.CondensateType;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.NaniteTier;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements;
import kubatech.loaders.BlockLoader;
import tectech.recipe.TTRecipeAdder;
import tectech.recipe.TecTechRecipeMaps;
import tectech.thing.CustomItemList;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.ThEApi;

public class RecipesVoidNexus implements IRecipePool {

    private static final int CONTROLLER_RESEARCH_POINTS = 100_000_000;
    private static final int RESEARCH_COMPUTATION_PER_TICK = 4_096;
    private static final long RESEARCH_POWER = TierEU.RECIPE_UHV;
    private static final int RESEARCH_AMPERAGE = 1;

    @Override
    public void loadRecipes() {
        registerFuelPreviewRecipes();
        registerControllerRecipe();
        registerShellRecipes();
        registerEssentiaStockingBusRecipe();
        registerManifestedPsionicsRecipe();
        registerPsionicStabilizerRecipe();
        registerManifestedPsionicModuleRecipe();
        RecipesVoidNexusUpgradePreview.register();
    }

    private void registerFuelPreviewRecipes() {
        List<Aspect> aspects = new ArrayList<>(Aspect.aspects.values());
        aspects.sort(
            Comparator.comparingInt(VoidNexusFuelProfiles::getLevel)
                .thenComparing(Aspect::getTag));
        int registered = 0;
        for (Aspect aspect : aspects) {
            if (aspect == null || aspectItem(aspect) == null) continue;
            GTValues.RA.stdBuilder()
                .itemInputs(aspectItem(aspect))
                .duration(VoidNexusCalculator.BURN_TICKS)
                .eut(0)
                .specialValue((int) VoidNexusFuelProfiles.getHeat(aspect))
                .addTo(Recipemaps.VOID_NEXUS);
            registered++;
        }
        OTHTechnology.LOG.info("Void Nexus Matrix: registered {} aspect fuel previews.", registered);
    }

    private void registerControllerRecipe() {
        ItemStack focus = GTModHandler.getModItem("TaintedMagic", "ItemFocusTime", 1);
        ItemStack[] inputs = new ItemStack[] { kubatech.api.enums.ItemList.DraconicEvolutionFusionCrafter.get(1),
            ItemList.Electric_Motor_UHV.get(16), ItemList.Robot_Arm_UHV.get(4), ItemList.Field_Generator_UHV.get(4),
            GTOreDictUnificator.get(OrePrefixes.plate, Materials.Infinity, 4),
            itemStackOrNull(LudicrousItems.akashic_record, 1), focus,
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Trinium, 64),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Mytryl, 64),
            GTOreDictUnificator.get(OrePrefixes.plateSuperdense, Materials.Void, 64),
            MaterialsAlloy.ABYSSAL.getPlateSuperdense(64) };
        FluidStack[] fluids = new FluidStack[] { Materials.Mellion.getMolten(2_880),
            Materials.DraconiumAwakened.getMolten(2_880), Materials.Naquadria.getMolten(2_880) };
        if (containsNullInputs(inputs) || containsNullInputs(fluids) || !OTHItemList.VoidNexusMatrix.hasBeenSet()) {
            OTHTechnology.LOG
                .warn("Void Nexus Matrix controller recipe skipped: one or more inputs are" + " unavailable.");
            return;
        }
        boolean added = TTRecipeAdder.addResearchableAssemblylineRecipe(
            itemStackOrNull(LudicrousItems.akashic_record, 1),
            CONTROLLER_RESEARCH_POINTS,
            RESEARCH_COMPUTATION_PER_TICK,
            (int) RESEARCH_POWER,
            RESEARCH_AMPERAGE,
            inputs,
            fluids,
            OTHItemList.VoidNexusMatrix.get(1),
            20 * 120,
            (int) TierEU.RECIPE_UHV);
        if (!added) OTHTechnology.LOG.error("Void Nexus Matrix controller recipe registration failed.");
    }

    private void registerShellRecipes() {
        if (OTHMaterials.ManifestedPsionics == null) {
            OTHTechnology.LOG
                .warn("Void Nexus shell recipes skipped: manifested psionics material is" + " unavailable.");
            return;
        }

        ItemStack wrappedCasing = casing(NailongCasingMeta.PSYCHIC_WRAPPED_CASING, 8);
        ItemStack[] wrappedInputs = new ItemStack[] { new ItemStack(ModBlocks.blockCasings6Misc, 16, 0),
            CustomItemList.StabilisationFieldGeneratorTier0.get(1),
            GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.MagMatter, 1), manifested(OrePrefixes.plate, 8),
            manifested(OrePrefixes.screw, 8), manifested(OrePrefixes.stick, 8), getNanites(16, Materials.Neutronium) };
        addBecRecipe(
            "psychic wrapped casing",
            wrappedCasing,
            wrappedInputs,
            entangled(
                CondensateType.Neutronium,
                1_440,
                CondensateType.CosmicNeutronium,
                1_440,
                CondensateType.Bedrockium,
                1_440),
            TierEU.RECIPE_UMV,
            240);

        ItemStack realityCasing = casing(NailongCasingMeta.PSIONIC_REALITY_CASING, 4);
        ItemStack[] realityInputs = new ItemStack[] {
            new ItemStack(bartworks.system.material.WerkstoffLoader.BWBlockCasingsAdvanced, 8, 10_111),
            manifested(OrePrefixes.plate, 4), manifested(OrePrefixes.itemCasing, 16),
            CustomItemList.Godforge_SingularityShieldingCasing.get(4) };
        addAssemblerRecipe(
            "psionic reality casing",
            realityCasing,
            realityInputs,
            new FluidStack[] { Materials.Mellion.getMolten(1_440) },
            TierEU.RECIPE_UMV,
            120);

        ItemStack subspaceCasing = casing(NailongCasingMeta.SUBSPACE_SHIELD_CASING, 1);
        ItemStack[] subspaceInputs = new ItemStack[] { new ItemStack(
            BlockLoader.defcCasingBlock,
            1,
            kubatech.api.enums.ItemList.DEFCCasingT5.hasBeenSet()
                ? kubatech.api.enums.ItemList.DEFCCasingT5.getInternalStack_unsafe()
                    .getItemDamage()
                : 12),
            manifested(OrePrefixes.plateDense, 12), OTHItemList.SpacetimeCoolingCore.get(1) };
        addAssemblerRecipe(
            "subspace shield casing",
            subspaceCasing,
            subspaceInputs,
            new FluidStack[] { Materials.SpaceTime.getMolten(1_440) },
            TierEU.RECIPE_UXV,
            50);

        ItemStack voidCasing = casing(NailongCasingMeta.VOID_STABILIZATION_CASING, 1);
        ItemStack[] voidInputs = new ItemStack[] { casing(NailongCasingMeta.PSIONIC_REALITY_CASING, 1),
            manifested(OrePrefixes.plate, 24), quintupleOrEquivalentPlates(Materials.Eternity, 12),
            quintupleOrEquivalentPlates(Materials.MagMatter, 12), CustomItemList.EOH_Reinforced_Spatial_Casing.get(4) };
        addBecRecipe(
            "void stabilization casing",
            voidCasing,
            voidInputs,
            entangled(
                CondensateType.Neutronium,
                1_440,
                CondensateType.CosmicNeutronium,
                1_440,
                CondensateType.Bedrockium,
                1_440),
            TierEU.RECIPE_UMV,
            60);

        ItemStack endlessGlass = casing(NailongCasingMeta.ENDLESS_PSIONIC_GLASS, 1);
        if (!containsNullInputs(RecipeStacks.casing(1, Casings.OmniPurposeInfinityFusedGlass), endlessGlass)) {
            FluidStack manifestedFluid = OTHMaterials.ManifestedPsionics.getMolten(1_152);
            if (manifestedFluid == null) {
                OTHTechnology.LOG
                    .warn("Void Nexus endless psionic glass recipe skipped: molten fluid is" + " unavailable.");
            } else {
                GTValues.RA.stdBuilder()
                    .itemInputs(RecipeStacks.casing(1, Casings.OmniPurposeInfinityFusedGlass))
                    .fluidInputs(manifestedFluid)
                    .itemOutputs(endlessGlass)
                    .duration(20 * 10)
                    .eut(TierEU.RECIPE_UIV)
                    .addTo(RecipeMaps.fluidSolidifierRecipes);
            }
        } else {
            OTHTechnology.LOG
                .warn("Void Nexus endless psionic glass recipe skipped: required items are" + " unavailable.");
        }

        ItemStack spacetimeGlass = casing(NailongCasingMeta.SPACETIME_POTENTIAL_GLASS, 1);
        ItemStack essentiaCell = creativeEssentiaCell(1);
        addAssemblerRecipe(
            "spacetime potential glass",
            spacetimeGlass,
            new ItemStack[] { endlessGlass, essentiaCell },
            new FluidStack[] { Materials.SpaceTime.getMolten(1_152) },
            TierEU.RECIPE_UMV,
            10);
    }

    private void registerEssentiaStockingBusRecipe() {
        if (!OTHItemList.VoidNexusEssentiaBus.hasBeenSet()) return;
        ItemStack advancedBus = ItemList.Hatch_Input_Bus_ME_Advanced.get(1);
        ItemStack essentiaProvider = ThEApi.instance()
            .blocks().EssentiaProvider.getStack();
        ItemStack components = copyAmount(
            ThEApi.instance()
                .items().EssentiaStorageComponent_16384k.getStack(),
            4);
        addAssemblerRecipe(
            "advanced essentia stocking input bus",
            OTHItemList.VoidNexusEssentiaBus.get(1),
            new ItemStack[] { advancedBus, essentiaProvider, components },
            null,
            TierEU.RECIPE_LuV,
            10);
    }

    private void registerManifestedPsionicsRecipe() {
        ItemStack shijima = GTOreDictUnificator.get(OrePrefixes.ingot, Materials.Shijima, 1);
        ItemStack output = OTHMaterials.ManifestedPsionics == null ? null
            : GTOreDictUnificator.get(OrePrefixes.ingot, OTHMaterials.ManifestedPsionics, 1);
        if (containsNullInputs(shijima) || GTUtility.isStackInvalid(output)) {
            OTHTechnology.LOG
                .warn("Psionic manifestation basic recipe skipped: required material is" + " unavailable.");
        } else {
            GTValues.RA.stdBuilder()
                .itemInputs(shijima)
                .itemOutputs(output)
                .duration(36_000)
                .eut(TierEU.RECIPE_UIV)
                .addTo(Recipemaps.PSIONIC_MANIFESTATION);
        }

        ItemStack axonium = MaterialsTST.Axonium == null ? null
            : GTOreDictUnificator.get(OrePrefixes.ingot, MaterialsTST.Axonium, 1);
        if (axonium != null) axonium = setStackSize(axonium, 256);
        ItemStack magmatterNanite = Materials.MagMatter.getNanite(1);
        ItemStack exquisiteAmalgatite = GTOreDictUnificator.get(OrePrefixes.gemExquisite, Materials.Amalgatite, 1);
        ItemStack potential = OTHItemList.VoidPotential.get(1);
        if (!containsNullInputs(axonium, magmatterNanite, exquisiteAmalgatite, potential)
            && !GTUtility.isStackInvalid(output)) {
            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(axonium, magmatterNanite, exquisiteAmalgatite, potential)
                .fluidInputs(Materials.RawStarMatter.getFluid(1_000))
                .itemOutputs(setStackSize(output, 256))
                .duration(1_200)
                .eut(TierEU.RECIPE_UXV)
                .specialValue(1)
                .addTo(Recipemaps.PSIONIC_MANIFESTATION);
        } else {
            OTHTechnology.LOG
                .warn("Psionic manifestation advanced recipe skipped: required inputs are" + " unavailable.");
        }
    }

    private void registerPsionicStabilizerRecipe() {
        if (OTHMaterials.ManifestedPsionics == null || !OTHItemList.PsionicStabilizer.hasBeenSet()) return;
        ItemStack fieldGenerator = ItemList.Field_Generator_UEV.get(1);
        ItemStack circuit = GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 1);
        addAssemblerRecipe(
            "psionic stabilizer",
            OTHItemList.PsionicStabilizer.get(1),
            new ItemStack[] { fieldGenerator, circuit },
            new FluidStack[] { OTHMaterials.ManifestedPsionics.getMolten(144) },
            TierEU.RECIPE_UEV,
            60);
    }

    private void registerManifestedPsionicModuleRecipe() {
        if (!OTHItemList.PsionicManifestationModule.hasBeenSet()) return;
        ItemStack research = CustomItemList.Hatch_BEC_IOController.get(1);
        ItemStack[] inputs = new ItemStack[] { CustomItemList.Hatch_BEC_CondensateDetector.get(1),
            ItemList.Electric_Motor_UIV.get(4), ItemList.Conveyor_Module_UIV.get(4), ItemList.Robot_Arm_UIV.get(4),
            ItemList.Field_Generator_UIV.get(4), creativeEssentiaCell(2),
            GTOreDictUnificator.get(OrePrefixes.plate, Materials.Infinity, 64),
            MaterialsElements.STANDALONE.HYPOGEN.getPlate(64) };
        FluidStack[] fluids = new FluidStack[] { MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(2_880),
            MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(2_880), FluidRegistry
                .getFluidStack(RecipeFluids.gtppMoltenName(MaterialsElements.STANDALONE.DRAGON_METAL), 14_400) };
        if (containsNullInputs(research) || containsNullInputs(inputs) || containsNullInputs(fluids)) {
            OTHTechnology.LOG.warn(
                "Psionic manifestation module recipe skipped: one or more research or recipe"
                    + " inputs are unavailable.");
            return;
        }
        boolean added = TTRecipeAdder.addResearchableAssemblylineRecipe(
            research,
            CONTROLLER_RESEARCH_POINTS,
            RESEARCH_COMPUTATION_PER_TICK,
            (int) RESEARCH_POWER,
            RESEARCH_AMPERAGE,
            inputs,
            fluids,
            OTHItemList.PsionicManifestationModule.get(1),
            20 * 120,
            (int) TierEU.RECIPE_UIV);
        if (!added) OTHTechnology.LOG.error("Psionic manifestation module recipe registration failed.");
    }

    private static ItemStack manifested(OrePrefixes prefix, int amount) {
        return OTHMaterials.ManifestedPsionics == null ? null
            : GTOreDictUnificator.get(prefix, OTHMaterials.ManifestedPsionics, amount);
    }

    private static ItemStack casing(int meta, int amount) {
        if (!OTHItemList.PsychicWrappedCasing.hasBeenSet()) return null;
        return new ItemStack(OTHItemList.PsychicWrappedCasing.getItem(), amount, meta);
    }

    private static ItemStack getNanites(int amount, Materials material) {
        return GTOreDictUnificator.get(OrePrefixes.nanite, material, amount);
    }

    private static ItemStack quintupleOrEquivalentPlates(Materials material, int amount) {
        ItemStack quintuple = GTOreDictUnificator.get(OrePrefixes.plateQuintuple, material, amount);
        if (!GTUtility.isStackInvalid(quintuple)) return quintuple;
        OTHTechnology.LOG.warn(
            "Void Nexus recipe: {} has no quintuple plates in this GT version; using equivalent" + " single plates.",
            material.mDefaultLocalName);
        return GTOreDictUnificator.get(OrePrefixes.plate, material, amount * 5);
    }

    private static ItemStack itemStackOrNull(net.minecraft.item.Item item, int amount) {
        return item == null ? null : new ItemStack(item, amount);
    }

    private static ItemStack copyAmount(ItemStack stack, int amount) {
        if (GTUtility.isStackInvalid(stack)) return null;
        ItemStack copy = stack.copy();
        copy.stackSize = amount;
        return copy;
    }

    private static ItemStack creativeEssentiaCell(int amount) {
        return copyAmount(
            ThEApi.instance()
                .items().EssentiaCell_Creative.getStack(),
            amount);
    }

    private static FluidStack[] entangled(CondensateType first, int firstAmount, CondensateType second,
        int secondAmount, CondensateType third, int thirdAmount) {
        return new FluidStack[] { first.getEntangled(firstAmount), second.getEntangled(secondAmount),
            third.getEntangled(thirdAmount) };
    }

    private static void addBecRecipe(String name, ItemStack output, ItemStack[] inputs, FluidStack[] condensates,
        long eut, int seconds) {
        if (containsNullInputs(output) || containsNullInputs(inputs) || containsNullInputs(condensates)) {
            OTHTechnology.LOG.warn("Void Nexus {} BEC recipe skipped: one or more inputs are unavailable.", name);
            return;
        }
        NaniteTier[] naniteTiers = new NaniteTier[inputs.length];
        Arrays.fill(naniteTiers, NaniteTier.SixPhasedCopper);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(inputs)
            .fluidInputs(condensates)
            .itemOutputs(output)
            .metadata(GTRecipeConstants.NANITE_TIERS, naniteTiers)
            .duration(seconds * 20)
            .eut(eut)
            .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
    }

    private static void addAssemblerRecipe(String name, ItemStack output, ItemStack[] inputs, FluidStack[] fluids,
        long eut, int seconds) {
        if (containsNullInputs(output) || containsNullInputs(inputs) || containsNullInputs(fluids)) {
            OTHTechnology.LOG.warn("Void Nexus {} assembler recipe skipped: one or more inputs are unavailable.", name);
            return;
        }
        if (fluids == null || fluids.length == 0) {
            GTValues.RA.stdBuilder()
                .itemInputs(inputs)
                .itemOutputs(output)
                .duration(seconds * 20)
                .eut(eut)
                .addTo(RecipeMaps.assemblerRecipes);
        } else {
            GTValues.RA.stdBuilder()
                .itemInputs(inputs)
                .fluidInputs(fluids)
                .itemOutputs(output)
                .duration(seconds * 20)
                .eut(eut)
                .addTo(RecipeMaps.assemblerRecipes);
        }
    }

    private static boolean containsNullInputs(ItemStack... inputs) {
        if (inputs == null) return true;
        for (ItemStack input : inputs) {
            if (GTUtility.isStackInvalid(input)) return true;
        }
        return false;
    }

    private static boolean containsNullInputs(FluidStack... inputs) {
        if (inputs == null) return false;
        for (FluidStack input : inputs) {
            if (input == null || input.getFluid() == null || input.amount <= 0) return true;
        }
        return false;
    }

    public static ItemStack aspectItem(Aspect aspect) {
        if (aspect == null || ModItems.itemAspect == null) return null;
        ItemStack stack = new ItemStack(ModItems.itemAspect, 1, 0);
        ItemAspect.setAspect(stack, aspect);
        return stack;
    }
}
