package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.common.OTHItemList.itemEnqingM;
import static com.newmaa.othtech.utils.Utils.setStackSize;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialsElements;
import li.cil.oc.OpenComputers;

public class RecipesMegaISAForge implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid UIVsc = FluidRegistry.getFluid("molten.superconductoruivbase");
        Fluid UMVsc = FluidRegistry.getFluid("molten.superconductorumvbase");
        final RecipeMap<?> ISA = Recipemaps.MegaIsaForge;

        GTValues.RA.stdBuilder()
            .fluidInputs(new FluidStack(FluidRegistry.getFluid("molten.helicopter"), 514))
            .itemInputs(OTHItemList.dustLookNEIM.get(1))
            .itemOutputs(
                GTModHandler.getModItem("miscutils", "itemDustRhenium", 64),
                GTModHandler.getModItem("miscutils", "itemDustThallium", 64),
                GTModHandler.getModItem("miscutils", "itemDustSelenium", 64),
                GTModHandler.getModItem("miscutils", "itemDustGermanium", 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2044),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2073),
                GTModHandler.getModItem("dreamcraft", "item.LapotronDust", 64))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluid("molten.quantum"), 114))
            .noOptimize()
            .eut(TierEU.RECIPE_MAX)
            .duration(20)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(ItemList.Robot_Arm_UXV.get(0), Materials.Gold.getDust(1))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluid("molten.sunnarium"), 144))
            .duration(100)
            .eut(123123123)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                ItemList.Robot_Arm_UXV.get(0),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluid("molten.sunnarium"), 288))
            .noOptimize()
            .duration(50)
            .eut(123123123)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 2),
                ItemList.Field_Generator_UMV.get(1))
            .fluidInputs(new FluidStack(FluidRegistry.getFluid("molten.neutronium"), 5760))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluid("molten.whitedwarfmatter"), 3600))
            .noOptimize()
            .duration(4000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 5),
                ItemList.Field_Generator_UMV.get(2))
            .fluidInputs(new FluidStack(FluidRegistry.getFluid("molten.cosmicneutronium"), 5760))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluid("molten.blackdwarfmatter"), 3600))
            .noOptimize()
            .duration(8000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 8),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 0))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluid("molten.spacetime"), 16 * 144),
                new FluidStack(FluidRegistry.getFluid("rawstarmatter"), 1000))
            .fluidOutputs(
                new FluidStack(FluidRegistry.getFluid("molten.infinity"), 576),
                new FluidStack(FluidRegistry.getFluid("molten.hypogen"), 288))
            .noOptimize()
            .duration(16000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 2),
                ItemList.Field_Generator_UXV.get(1))
            .fluidInputs(new FluidStack(FluidRegistry.getFluid("molten.cosmicneutronium"), 5760))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluid("molten.whitedwarfmatter"), 576))
            .noOptimize()
            .duration(40000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 0, 5),
                ItemList.Field_Generator_UXV.get(2))
            .fluidInputs(new FluidStack(FluidRegistry.getFluid("molten.cosmicneutronium"), 5760))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluid("molten.blackdwarfmatter"), 576))
            .noOptimize()
            .duration(80000)
            .eut(2000000000)
            .specialValue(1)
            .addTo(ISA);
        // GTValues.RA.stdBuilder()
        // .itemInputs(ItemList.Field_Generator_UEV.get(4))
        // .fluidInputs(new FluidStack(FluidRegistry.getFluidID("water"), 123123))
        // .fluidOutputs(
        // new FluidStack(FluidRegistry.getFluidID("grade1purifiedwater"), 100000),
        // new FluidStack(FluidRegistry.getFluidID("grade2purifiedwater"), 100000),
        // new FluidStack(FluidRegistry.getFluidID("grade3purifiedwater"), 100000),
        // new FluidStack(FluidRegistry.getFluidID("grade4purifiedwater"), 100000),
        // new FluidStack(FluidRegistry.getFluidID("grade5purifiedwater"), 100000),
        // new FluidStack(FluidRegistry.getFluidID("grade6purifiedwater"), 100000),
        // new FluidStack(FluidRegistry.getFluidID("grade7purifiedwater"), 100000),
        // new FluidStack(FluidRegistry.getFluidID("grade8purifiedwater"), 100000))
        // .noOptimize()
        // .duration(3600 * 20)
        // .eut(TierEU.RECIPE_UEV)
        // .specialValue(1)
        // .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                OTHItemList.beeISAM.get(0),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 31027),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4581),
                ItemList.Field_Generator_UIV.get(1))
            .itemOutputs(OTHItemList.beeISAM.get(1))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 16000))
            .noOptimize()
            .duration(1000 * 20)
            .eut(Integer.MAX_VALUE)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                OTHItemList.beeISAM.get(0),
                GTModHandler.getModItem("Avaritia", "Resource", 64, 5))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 181968))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluidID("molten.infinity"), 2359296))
            .noOptimize()
            .eut(Integer.MAX_VALUE)
            .duration(2000 * 20)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                OTHItemList.beeISAM.get(0),
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32100))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("molten.neutronium"), 115200),
                new FluidStack(FluidRegistry.getFluidID("molten.quantum"), 57600),
                new FluidStack(FluidRegistry.getFluidID("molten.infinity"), 14400),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 10000))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluidID("molten.hypogen"), 230400))
            .noOptimize()
            .eut(Integer.MAX_VALUE)
            .duration(4000 * 20)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 31028),
                OTHItemList.beeISAM.get(1),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4139),
                ItemList.Field_Generator_UMV.get(4))
            .itemOutputs(OTHItemList.beeMagM.get(4))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 64000),
                new FluidStack(
                    FluidRegistry.getFluidID("molten.magnetohydrodynamicallyconstrainedstarmatter"),
                    144 * 24),
                new FluidStack(FluidRegistry.getFluidID("molten.tengampurified"), 144 * 64))
            .noOptimize()
            .duration(2000 * 20)
            .eut(Integer.MAX_VALUE)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                OTHItemList.beeMagM.get(0),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 0),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 32727),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4581),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Infinity, 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29979),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 1))
            .itemOutputs(OTHItemList.socCosmicM.get(4))
            .noOptimize()
            .duration(400 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                OTHItemList.socCosmicM.get(8),
                GTModHandler.getModItem("bartworks", "gt.bwMetaGeneratedwireFine", 64, 10112),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32417))
            .itemOutputs(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32170))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("plasma.hydrogen"), 400))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluidID("dimensionallytranscendentresidue"), 333 * 8))
            .noOptimize()
            .duration(40 * 16 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 1, 26))
            .itemInputs(
                GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 0, 26),
                OTHItemList.beeISAM.get(0))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("molten.blackdwarfmatter"), 72),
                new FluidStack(FluidRegistry.getFluidID("molten.whitedwarfmatter"), 72))
            .noOptimize()
            .duration(100 * 20)
            .eut(123123123)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.glassSingularityM.get(1))
            .itemInputs(
                GTUtility.getIntegratedCircuit(12),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15410),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 0),
                GTModHandler.getModItem("gregtech", "gt.blockframes", 4, 141),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32230),
                GTModHandler.getModItem("miscutils", "itemPlateDenseChromaticGlass", 64),
                GTModHandler.getModItem("miscutils", "itemPlateDenseChromaticGlass", 64),
                GTModHandler.getModItem("miscutils", "itemPlateDenseChromaticGlass", 64),
                GTModHandler.getModItem("miscutils", "itemPlateDenseChromaticGlass", 64),
                GTModHandler.getModItem("miscutils", "itemPlateDenseChromaticGlass", 64))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("molten.hypogen"), 144000),
                new FluidStack(FluidRegistry.getFluidID("molten.infinity"), 144000),
                new FluidStack(FluidRegistry.getFluidID("molten.spacetime"), 144000))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluidID("dimensionallytranscendentresidue"), 288000))
            .noOptimize()
            .duration(123 * 20)
            .eut(TierEU.MAX / 2)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(OTHItemList.machineSingularityM.get(4))
            .itemInputs(
                GTUtility.getIntegratedCircuit(13),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 0),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Optical, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Bio, 64),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Infinite, 64),
                ItemList.Robot_Arm_UIV.get(64),
                ItemList.Robot_Arm_UEV.get(64),
                ItemList.Robot_Arm_UHV.get(64))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("molten.mutatedlivingsolder"), 144000),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 128000),
                new FluidStack(FluidRegistry.getFluidID("molten.infinity"), 144000))
            .noOptimize()
            .duration(123 * 20)
            .eut(TierEU.MAX / 2)
            .addTo(ISA);
        // byd Glass
        GTValues.RA.stdBuilder()
            .fluidInputs(
                FluidRegistry.getFluidStack("exciteddtsc", 11116 / 4),
                FluidRegistry.getFluidStack("molten.glass", 294912 * 2))
            .fluidOutputs(
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 11116 * 4),
                FluidRegistry.getFluidStack("molten.chromaticglass", 294912 * 2))
            .noOptimize()
            .eut(TierEU.MAX)
            .duration(20 * 20)
            .addTo(ISA);
        // NanoBee - GlowStone
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 4811), 512))
            .fluidInputs(FluidRegistry.getFluidStack("molten.spacetime", 288))
            .itemInputs(
                setStackSize(new ItemStack(Blocks.glowstone, 1), 256),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32049), 256))
            .noOptimize()
            .eut(TierEU.RECIPE_UMV)
            .duration(123 * 20)
            .addTo(ISA);
        // NanoBee - Neut
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 4129), 512))
            .fluidInputs(FluidRegistry.getFluidStack("molten.spacetime", 288 * 4))
            .itemInputs(
                setStackSize(GTOreDictUnificator.get(OrePrefixes.block, Materials.Neutronium, 1), 4096),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32049), 20480))
            .noOptimize()
            .eut(TierEU.RECIPE_UXV)
            .duration(114 * 20)
            .addTo(ISA);
        // IMBABlastFurnace
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 23543), 1))
            .fluidInputs(FluidRegistry.getFluidStack("molten.solderingalloy", Integer.MAX_VALUE))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 12730), Integer.MAX_VALUE),
                setStackSize(GTModHandler.getModItem("dreamcraft", "item.StargateShieldingFoil", 1), 114514),
                setStackSize(GTModHandler.getModItem("dreamcraft", "item.StargateFramePart", 1), 114514),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15411), 123123123),
                setStackSize(new ItemStack(ItemLoader.itemLeekBox, 1), Integer.MAX_VALUE),
                setStackSize(new ItemStack(ItemLoader.itemZhangww, 1), Integer.MAX_VALUE),
                setStackSize(new ItemStack(ItemLoader.itemNukeThrowable, 1), Integer.MAX_VALUE),
                setStackSize(itemEnqingM.get(1), Integer.MAX_VALUE))
            .noOptimize()
            .eut(TierEU.RECIPE_UXV)
            .duration(114 * 20)
            .addTo(ISA);
        // GTX690
        RecipeBuilder.builder()
            .itemOutputs(new ItemStack(ItemLoader.itemNukeThrowable, 1, 0))
            .itemInputs(
                GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 33),
                GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 90),
                setStackSize(GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 103), 3072),
                setStackSize(GTModHandler.getModItem(OpenComputers.ID(), "item", 1, 39), 4096),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 15300),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.AnnealedCopper, 1), 16384),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32178), 2048),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32179), 2048),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32180), 2048),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32181), 2048),
                setStackSize(GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32184), 2048),
                setStackSize(GTModHandler.getModItem("IC2", "reactorVentDiamond", 1), 48),
                GTOreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Copper, 16))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.solderingalloy", 4096),
                FluidRegistry.getFluidStack("supercoolant", 500))
            .duration(3600 * 20)
            .eut(220)
            .addTo(ISA);
        // Magmatter
        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 0, 4143),
                ItemList.Field_Generator_MAX.get(0),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15411),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15415))
            .fluidInputs(
                FluidRegistry.getFluidStack("temporalfluid", 1152),
                FluidRegistry.getFluidStack("spatialfluid", 1152),
                FluidRegistry.getFluidStack("molten.magnetohydrodynamicallyconstrainedstarmatter", 4608),
                FluidRegistry.getFluidStack("plasma.infinity", 9216),
                FluidRegistry.getFluidStack("plasma.neutronium", 9216),
                FluidRegistry.getFluidStack("plasma.sixphasedcopper", 9216),
                FluidRegistry.getFluidStack("plasma.bedrockium", 9216),
                FluidRegistry.getFluidStack("plasma.celestialtungsten", 9216),
                FluidRegistry.getFluidStack("plasma.cosmicneutronium", 9216),
                FluidRegistry.getFluidStack("plasma.chromaticglass", 9216),
                FluidRegistry.getFluidStack("plasma.hypogen", 9216),
                FluidRegistry.getFluidStack("plasma.ichorium", 9216),
                FluidRegistry.getFluidStack("plasma.draconiumawakened", 9216),
                FluidRegistry.getFluidStack("plasma.rhugnor", 9216),
                FluidRegistry.getFluidStack("plasma.draconium", 9216),
                FluidRegistry.getFluidStack("plasma.dragonblood", 9216),
                FluidRegistry.getFluidStack("plasma.flerovium_gt5u", 9216))
            .fluidOutputs(FluidRegistry.getFluidStack("molten.magmatter", 123123))
            .duration(12 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);

        // Better magmatter
        RecipeBuilder.builder()
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 0, 4143),
                ItemList.Field_Generator_MAX.get(0),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15411),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15415),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15414),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2397),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2129),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2147),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2395),
                MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getDust(64),
                MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getDust(64),
                MaterialsElements.STANDALONE.HYPOGEN.getDust(64),
                MaterialsElements.STANDALONE.RHUGNOR.getDust(64),
                MaterialsElements.STANDALONE.DRAGON_METAL.getDust(64),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Draconium, 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2982),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2978),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2976),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 2984))
            .fluidInputs(
                FluidRegistry.getFluidStack("temporalfluid", 9216),
                FluidRegistry.getFluidStack("spatialfluid", 9216),
                FluidRegistry.getFluidStack("molten.magnetohydrodynamicallyconstrainedstarmatter", 159408),
                FluidRegistry.getFluidStack("molten.eternity", 159408))
            .fluidOutputs(FluidRegistry.getFluidStack("molten.magmatter", 123123123))
            .duration(123 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);

        // quark
        RecipeBuilder.builder()
            .itemInputs(
                OTHItemList.beeISAM.get(0),
                ItemList.Field_Generator_MAX.get(0),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 0, 4143),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15411),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15415),
                GTModHandler.getModItem("dreamcraft", "item.StargateShieldingFoil", 1),
                GTModHandler.getModItem("dreamcraft", "item.StargateCrystalDust", 64))
            .fluidInputs(FluidRegistry.getFluidStack("phononmedium", 256000))
            .fluidOutputs(FluidRegistry.getFluidStack("quarkgluonplasma", 3690000))
            .duration(123 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);
    }
}
