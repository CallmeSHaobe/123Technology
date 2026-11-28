package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.common.OTHItemList.itemEnqingM;
import static com.newmaa.othtech.recipe.RecipesCircuit.getAstralArray;
import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.dreammaster.item.NHItemList;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import bartworks.system.material.WerkstoffLoader;
import fox.spiteful.avaritia.items.LudicrousItems;
import goodgenerator.items.GGMaterial;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.Mods;
import gregtech.api.enums.NaniteTier;
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
import gtPlusPlus.core.material.MaterialsElements;
import gtPlusPlus.core.material.Particle;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtPlusPlus.xmod.gregtech.common.items.MetaGeneratedItemIDs;
import ic2.core.init.InternalName;
import tectech.thing.casing.TTCasingsContainer;

public class RecipesMegaISAForge implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid UIVsc = FluidRegistry
            .getFluid(RecipeFluids.registryName(Materials.SuperconductorUIVBase.mStandardMoltenFluid));
        Fluid UMVsc = FluidRegistry
            .getFluid(RecipeFluids.registryName(Materials.SuperconductorUMVBase.mStandardMoltenFluid));
        final RecipeMap<?> ISA = Recipemaps.MegaIsaForge;

        GTValues.RA.stdBuilder()
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluid(RecipeFluids.gtppMoltenName(MaterialsAlloy.HELICOPTER)), 514))
            .itemInputs(OTHItemList.dustLookNEIM.get(1))
            .itemOutputs(
                RecipeStacks.gtppComponent(64, MaterialsElements.getInstance().RHENIUM, ComponentTypes.DUST),
                RecipeStacks.gtppComponent(64, MaterialsElements.getInstance().THALLIUM, ComponentTypes.DUST),
                RecipeStacks.gtppComponent(64, MaterialsElements.getInstance().SELENIUM, ComponentTypes.DUST),
                RecipeStacks.gtppComponent(64, MaterialsElements.getInstance().GERMANIUM, ComponentTypes.DUST),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Strontium),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Dysprosium),
                RecipeStacks.exact(64, NHItemList.LapotronDust.item, 0))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluid(RecipeFluids.gtppMoltenName(MaterialsAlloy.QUANTUM)), 114))

            .eut(TierEU.RECIPE_MAX)
            .duration(20)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Robot_Arm_UXV.get(0), Materials.Gold.getDust(1))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Sunnarium.mStandardMoltenFluid)),
                    144))
            .duration(100)
            .eut(123123123)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Robot_Arm_UXV.get(0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Sunnarium.mStandardMoltenFluid)),
                    288))

            .duration(50)
            .eut(123123123)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                (GregTechAPI.sPreloadStarted && Mods.TecTech.isModLoaded()
                    ? RecipeStacks.exact(0, TTCasingsContainer.SpacetimeCompressionFieldGenerators, 2)
                    : null),
                ItemList.Field_Generator_UMV.get(1))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Neutronium.mStandardMoltenFluid)),
                    5760))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.WhiteDwarfMatter.mStandardMoltenFluid)),
                    3600))

            .duration(4000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                (GregTechAPI.sPreloadStarted && Mods.TecTech.isModLoaded()
                    ? RecipeStacks.exact(0, TTCasingsContainer.SpacetimeCompressionFieldGenerators, 5)
                    : null),
                ItemList.Field_Generator_UMV.get(2))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.CosmicNeutronium.mStandardMoltenFluid)),
                    5760))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.BlackDwarfMatter.mStandardMoltenFluid)),
                    3600))

            .duration(8000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                (GregTechAPI.sPreloadStarted && Mods.TecTech.isModLoaded()
                    ? RecipeStacks.exact(0, TTCasingsContainer.SpacetimeCompressionFieldGenerators, 8)
                    : null),
                getAstralArray(0))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.SpaceTime.mStandardMoltenFluid)),
                    16 * 144),
                new FluidStack(FluidRegistry.getFluid(RecipeFluids.registryName(Materials.RawStarMatter.mFluid)), 1000))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.Infinity.mStandardMoltenFluid)),
                    576),
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.gtppMoltenName(MaterialsElements.STANDALONE.HYPOGEN)),
                    288))

            .duration(16000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                (GregTechAPI.sPreloadStarted && Mods.TecTech.isModLoaded()
                    ? RecipeStacks.exact(0, TTCasingsContainer.SpacetimeCompressionFieldGenerators, 2)
                    : null),
                ItemList.Field_Generator_UXV.get(1))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.CosmicNeutronium.mStandardMoltenFluid)),
                    5760))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.WhiteDwarfMatter.mStandardMoltenFluid)),
                    57600))

            .duration(40000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                (GregTechAPI.sPreloadStarted && Mods.TecTech.isModLoaded()
                    ? RecipeStacks.exact(0, TTCasingsContainer.SpacetimeCompressionFieldGenerators, 5)
                    : null),
                ItemList.Field_Generator_UXV.get(2))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.CosmicNeutronium.mStandardMoltenFluid)),
                    5760))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluid(RecipeFluids.registryName(Materials.BlackDwarfMatter.mStandardMoltenFluid)),
                    57600))

            .duration(80000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                OTHItemList.beeISAM.get(0),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.Controller_IsaMill.ID),
                RecipeStacks
                    .generated(16, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.TranscendentMetal),
                ItemList.Field_Generator_UIV.get(1))
            .itemOutputs(OTHItemList.beeISAM.get(1))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.ic2FluidName(InternalName.fluidUuMatter)), 16000))

            .duration(1000 * 20)
            .eut(Integer.MAX_VALUE)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                OTHItemList.beeISAM.get(0),
                RecipeStacks.exact(64, LudicrousItems.resource, 5))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.ExcitedDTSC.mFluid)),
                    181968))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Infinity.mStandardMoltenFluid)),
                    2359296))

            .eut(Integer.MAX_VALUE)
            .duration(2000 * 20)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                OTHItemList.beeISAM.get(0),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.Compressed_Fusion_Reactor.hasBeenSet()
                        ? RecipeStacks.exact(
                            0,
                            GregtechItemList.Compressed_Fusion_Reactor.getItem(),
                            32000 + MetaGeneratedItemIDs.Compressed_Fusion_Reactor.ID)
                        : null))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Neutronium.mStandardMoltenFluid)),
                    115200),
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsAlloy.QUANTUM)), 57600),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Infinity.mStandardMoltenFluid)),
                    14400),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.ExcitedDTSC.mFluid)),
                    10000))
            .fluidOutputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsElements.STANDALONE.HYPOGEN)),
                    230400))

            .eut(Integer.MAX_VALUE)
            .duration(4000 * 20)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                RecipeStacks.exact(0, ItemList.FlotationCell),
                OTHItemList.beeISAM.get(1),
                RecipeStacks.generated(16, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.Universium),
                ItemList.Field_Generator_UMV.get(4))
            .itemOutputs(OTHItemList.beeMagM.get(4))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.ic2FluidName(InternalName.fluidUuMatter)), 64000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.MHDCSM.mStandardMoltenFluid)),
                    144 * 24),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.TengamPurified.mStandardMoltenFluid)),
                    144 * 64))

            .duration(2000 * 20)
            .eut(Integer.MAX_VALUE)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                OTHItemList.beeMagM.get(0),
                getAstralArray(0),
                RecipeStacks.exact(4, ItemList.Optical_Cpu_Containment_Housing),
                RecipeStacks
                    .generated(4, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.TranscendentMetal),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Infinity, 64),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.foil, Materials.RadoxPolymer),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 1))
            .itemOutputs(OTHItemList.socCosmicM.get(4))

            .duration(400 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                OTHItemList.socCosmicM.get(8),
                (GregTechAPI.sPreloadStarted && Mods.BartWorks.isModLoaded() ? RecipeStacks.exact(
                    64,
                    WerkstoffLoader.items.get(OrePrefixes.wireFine),
                    (Mods.GoodGenerator.isModLoaded() ? GGMaterial.shirabon.getmID() : 10112)) : null),
                RecipeStacks.exact(4, ItemList.EnergisedTesseract))
            .itemOutputs(RecipeStacks.exact(16, ItemList.Circuit_CosmicProcessor))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Hydrogen.mPlasma)), 400))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.DTR.mFluid)), 333 * 8))

            .duration(40 * 16 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 1, 26))
            .itemInputs(
                GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 0, 26),
                OTHItemList.beeISAM.get(0))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.BlackDwarfMatter.mStandardMoltenFluid)),
                    72),
                new FluidStack(
                    FluidRegistry
                        .getFluidID(RecipeFluids.registryName(Materials.WhiteDwarfMatter.mStandardMoltenFluid)),
                    72))

            .duration(100 * 20)
            .eut(123123123)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.glassSingularityM.get(1))
            .itemInputs(
                GTUtility.getIntegratedCircuit(12),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.EyeofHarmony.ID),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 0),
                RecipeStacks.exact(4, GregTechAPI.sBlockFrames, Materials.Eternity.getId()),
                RecipeStacks.exact(64, ItemList.NuclearStar),
                RecipeStacks.gtppComponent(
                    64,
                    gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS,
                    ComponentTypes.PLATEDENSE),
                RecipeStacks.gtppComponent(
                    64,
                    gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS,
                    ComponentTypes.PLATEDENSE),
                RecipeStacks.gtppComponent(
                    64,
                    gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS,
                    ComponentTypes.PLATEDENSE),
                RecipeStacks.gtppComponent(
                    64,
                    gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS,
                    ComponentTypes.PLATEDENSE),
                RecipeStacks.gtppComponent(
                    64,
                    gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS,
                    ComponentTypes.PLATEDENSE))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialsElements.STANDALONE.HYPOGEN)),
                    144000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Infinity.mStandardMoltenFluid)),
                    144000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.SpaceTime.mStandardMoltenFluid)),
                    144000))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.DTR.mFluid)), 288000))

            .duration(123 * 20)
            .eut(TierEU.MAX / 2)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.machineSingularityM.get(4))
            .itemInputs(
                GTUtility.getIntegratedCircuit(13),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 0),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UHV, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UEV, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.UIV, 64),
                ItemList.Robot_Arm_UIV.get(64),
                ItemList.Robot_Arm_UEV.get(64),
                ItemList.Robot_Arm_UHV.get(64))
            .fluidInputs(
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.gtppMoltenName(MaterialMisc.MUTATED_LIVING_SOLDER)),
                    144000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.ExcitedDTSC.mFluid)),
                    128000),
                new FluidStack(
                    FluidRegistry.getFluidID(RecipeFluids.registryName(Materials.Infinity.mStandardMoltenFluid)),
                    144000))

            .duration(123 * 20)
            .eut(TierEU.MAX / 2)
            .addTo(ISA);
        // byd Glass
        GTValues.RA.stdBuilder()
            .fluidInputs(Materials.ExcitedDTSC.getFluid(11116 / 4), Materials.Glass.getMolten(294912 * 2))
            .fluidOutputs(
                Materials.DTR.getFluid(11116 * 4),
                FluidRegistry.getFluidStack(
                    RecipeFluids.gtppMoltenName(MaterialsElements.STANDALONE.CHRONOMATIC_GLASS),
                    294912 * 2))

            .eut(TierEU.MAX)
            .duration(20 * 20)
            .addTo(ISA);
        // NanoBee - GlowStone
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.Glowstone),
                    512))
            .fluidInputs(Materials.SpaceTime.getMolten(288))
            .itemInputsUnsafe(
                setStackSize(new ItemStack(Blocks.glowstone, 1), 256),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_SoC2), 256))

            .eut(TierEU.RECIPE_UMV)
            .duration(123 * 20)
            .addTo(ISA);
        // NanoBee - Neut
        GTValues.RA.stdBuilder()
            .itemOutputs(
                setStackSize(
                    RecipeStacks.generated(1, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.Neutronium),
                    512))
            .fluidInputs(Materials.SpaceTime.getMolten(288 * 4))
            .itemInputsUnsafe(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.block, Materials.Neutronium, 1), 4096),
                setStackSize(RecipeStacks.exact(1, ItemList.Circuit_Chip_SoC2), 20480))

            .eut(TierEU.RECIPE_UXV)
            .duration(114 * 20)
            .addTo(ISA);
        // IMBABlastFurnace
        GTValues.RA.stdBuilder()
            .itemOutputs(setStackSize(RecipeStacks.exact(1, OTHItemList.ImbaBlastFurnace), 1))
            .fluidInputs(Materials.SolderingAlloy.getMolten(Integer.MAX_VALUE))
            .itemInputsUnsafe(
                setStackSize(RecipeStacks.exact(1, ItemList.ExothermicHearth), Integer.MAX_VALUE),
                setStackSize(RecipeStacks.exact(1, NHItemList.StargateShieldingFoil.item, 0), 114514),
                setStackSize(RecipeStacks.exact(1, NHItemList.StargateFramePart.item, 0), 114514),
                setStackSize(
                    GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, MetaTileEntityIDs.ForgeoftheGods.ID),
                    123123123),
                setStackSize(new ItemStack(ItemLoader.itemLeekBox, 1), Integer.MAX_VALUE),
                setStackSize(new ItemStack(ItemLoader.itemZhangww, 1), Integer.MAX_VALUE),
                setStackSize(new ItemStack(ItemLoader.itemNukeThrowable, 1), 123123),
                setStackSize(itemEnqingM.get(1), Integer.MAX_VALUE))

            .eut(TierEU.RECIPE_UXV)
            .duration(114 * 20)
            .addTo(ISA);
        /*
         * // GTX690
         * GTValues.RA.stdBuilder()
         * .itemOutputs(new ItemStack(ItemLoader.itemNukeThrowable, 1, 0))
         * .itemInputsUnsafe(
         * GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 33),
         * GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 90),
         * setStackSize(GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 103), 3072),
         * setStackSize(GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 39), 4096),
         * GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
         * setStackSize(GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.AnnealedCopper, 1), 16384),
         * setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32178), 2048),
         * setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32179), 2048),
         * setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32180), 2048),
         * setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32181), 2048),
         * setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32184), 2048),
         * setStackSize(GTModHandler.getModItem("IC2", "reactorVentDiamond", 1, 1), 48),
         * GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Copper, 16))
         * .fluidInputs(
         * FluidRegistry.getFluidStack("molten.solderingalloy", 4096),
         * FluidRegistry.getFluidStack("supercoolant", 500))
         * .duration(3600 * 20)
         * .eut(220)
         * .addTo(ISA);
         */
        // Magmatter
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                RecipeStacks.generated(0, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.MagMatter),
                ItemList.Field_Generator_MAX.get(0),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.ForgeoftheGods.ID),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.HeliofusionExoticizer.ID))
            .fluidInputs(
                Materials.Time.getMolten(1152),
                Materials.Space.getMolten(1152),
                Materials.MHDCSM.getMolten(4608),
                Materials.Infinity.getPlasma(9216),
                Materials.Neutronium.getPlasma(9216),
                Materials.SixPhasedCopper.getPlasma(9216),
                Materials.Bedrockium.getPlasma(9216),
                FluidRegistry
                    .getFluidStack(RecipeFluids.gtppPlasmaName(MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN), 9216),
                Materials.CosmicNeutronium.getPlasma(9216),
                FluidRegistry
                    .getFluidStack(RecipeFluids.gtppPlasmaName(MaterialsElements.STANDALONE.CHRONOMATIC_GLASS), 9216),
                FluidRegistry.getFluidStack(RecipeFluids.gtppPlasmaName(MaterialsElements.STANDALONE.HYPOGEN), 9216),
                Materials.Ichorium.getPlasma(9216),
                Materials.DraconiumAwakened.getPlasma(9216),
                FluidRegistry.getFluidStack(RecipeFluids.gtppPlasmaName(MaterialsElements.STANDALONE.RHUGNOR), 9216),
                Materials.Draconium.getPlasma(9216),
                FluidRegistry
                    .getFluidStack(RecipeFluids.gtppPlasmaName(MaterialsElements.STANDALONE.DRAGON_METAL), 9216),
                Materials.Flerovium.getPlasma(9216))
            .fluidOutputs(Materials.MagMatter.getMolten(123123))
            .duration(12 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);

        // Better magmatter
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                RecipeStacks.generated(0, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.MagMatter),
                ItemList.Field_Generator_MAX.get(0),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.ForgeoftheGods.ID),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.HeliofusionExoticizer.ID),
                GTModHandler
                    .getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.HeliothermalPlasmaFabricator.ID),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Infinity),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Neutronium),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.SixPhasedCopper),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Bedrockium),
                MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getDust(64),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getDust(64),
                MaterialsElements.STANDALONE.HYPOGEN.getDust(64),
                MaterialsElements.STANDALONE.RHUGNOR.getDust(64),
                MaterialsElements.STANDALONE.DRAGON_METAL.getDust(64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 64),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.CosmicNeutronium),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Ichorium),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.DraconiumAwakened),
                RecipeStacks.generated(64, MetaGeneratedItem01.INSTANCE, OrePrefixes.dust, Materials.Flerovium))
            .fluidInputs(
                Materials.Time.getMolten(9216),
                Materials.Space.getMolten(9216),
                Materials.MHDCSM.getMolten(159408),
                Materials.Eternity.getMolten(159408))
            .fluidOutputs(Materials.MagMatter.getMolten(123123123))
            .duration(123 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);

        // quark
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                OTHItemList.beeISAM.get(0),
                ItemList.Field_Generator_MAX.get(0),
                RecipeStacks.exact(0, NHItemList.StargateShieldingFoil.item, 0),
                RecipeStacks.generated(0, MetaGeneratedItem03.INSTANCE, OrePrefixes.nanite, Materials.MagMatter),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.ForgeoftheGods.ID),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, MetaTileEntityIDs.HeliofusionExoticizer.ID),
                RecipeStacks.exact(64, NHItemList.StargateCrystalDust.item, 0))
            .fluidInputs(Materials.PhononMedium.getFluid(256000))
            .fluidOutputs(Materials.QuarkGluonPlasma.getFluid(3690000))
            .duration(123 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);

        // 惰性寰宇焊料 -> 无界寰宇焊料（定制配方）
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(NaniteTier.Universium.getStack(), 16),
                setStackSize(RecipeStacks.exact(1, ItemList.PseudoStar), 256),
                OTHItemList.dustIrOsSmM.get(1),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(64, Particle.getBaseParticle(Particle.GRAVITON))
                    : null),
                (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                    ? RecipeStacks.exact(64, Particle.getBaseParticle(Particle.HIGGS_BOSON))
                    : null))
            .fluidInputs(Materials.InactiveCosmicSolder.getFluid(3_210_000))
            .fluidOutputs(Materials.BoundlessCosmicSolder.getFluid(3_210_000))
            .duration(200 * 20)
            .eut(Integer.MAX_VALUE)
            .specialValue(1)
            .addTo(ISA);

        // 稳定空白约束单元 -> 四类稳定约束单元（定制配方）
        GTValues.RA.stdBuilder()
            .itemInputs(
                setStackSize(RecipeStacks.exact(1, ItemList.StableEmptyContainmentUnit), 4096),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                        ? RecipeStacks.exact(1, Particle.getBaseParticle(Particle.PROTON))
                        : null),
                    256),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                        ? RecipeStacks.exact(1, Particle.getBaseParticle(Particle.ELECTRON))
                        : null),
                    256),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                        ? RecipeStacks.exact(1, Particle.getBaseParticle(Particle.ETA_MESON))
                        : null),
                    256),
                setStackSize(
                    (GregTechAPI.sPreloadStarted && Mods.GTPlusPlus.isModLoaded()
                        ? RecipeStacks.exact(1, Particle.getBaseParticle(Particle.HIGGS_BOSON))
                        : null),
                    256),
                setStackSize(RecipeStacks.exact(1, ItemList.Quark_Catalyst_Housing), 64))
            .fluidInputs(FluidRegistry.getFluidStack(RecipeFluids.werkstoffName(WerkstoffLoader.Oganesson), 1_000_000))
            .itemOutputs(
                setStackSize(RecipeStacks.exact(1, ItemList.StableBaryonContainmentUnit), 1024),
                setStackSize(RecipeStacks.exact(1, ItemList.StableLeptonContainmentUnit), 1024),
                setStackSize(RecipeStacks.exact(1, ItemList.StableMesonContainmentUnit), 1024),
                setStackSize(RecipeStacks.exact(1, ItemList.StableBosonContainmentUnit), 1024))
            .duration(500 * 20)
            .eut(Integer.MAX_VALUE)
            .specialValue(1)
            .addTo(ISA);
    }
}
