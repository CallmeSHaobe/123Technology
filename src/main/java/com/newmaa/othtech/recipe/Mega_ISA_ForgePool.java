package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.Utils.Utils.setStackSize;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.Utils.RecipeBuilder;
import com.newmaa.othtech.common.materials.liquids;
import com.newmaa.othtech.common.recipemap.Recipemaps;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class Mega_ISA_ForgePool implements IRecipePool {

    @Override
    public void loadRecipes() {
        Fluid UIVsc = FluidRegistry.getFluid("molten.superconductoruivbase");
        Fluid UMVsc = FluidRegistry.getFluid("molten.superconductorumvbase");
        final RecipeMap<?> ISA = Recipemaps.MegaIsaForge;

        GTValues.RA.stdBuilder()
            .fluidInputs(new FluidStack(FluidRegistry.getFluid("molten.helicopter"), 514))
            .itemInputs(GTModHandler.getModItem("123Technology", "LookNEIdust", 1))
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
                GTModHandler.getModItem("123Technology", "IsaBee", 0),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 31027),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4581),
                ItemList.Field_Generator_UIV.get(1))
            .itemOutputs(GTModHandler.getModItem("123Technology", "IsaBee", 1))
            .fluidInputs(new FluidStack(FluidRegistry.getFluidID("ic2uumatter"), 16000))
            .noOptimize()
            .duration(1000 * 20)
            .eut(Integer.MAX_VALUE)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(8),
                GTModHandler.getModItem("123Technology", "IsaBee", 0),
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
                GTModHandler.getModItem("123Technology", "IsaBee", 0),
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
                GTModHandler.getModItem("123Technology", "IsaBee", 1),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4139),
                ItemList.Field_Generator_UMV.get(4))
            .itemOutputs(GTModHandler.getModItem("123Technology", "MagBee", 4))
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
                GTModHandler.getModItem("123Technology", "MagBee", 0),
                GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 0),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 32727),
                GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 4581),
                GTOreDictUnificator.get(OrePrefixes.wireFine, Materials.Infinity, 64),
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 29979),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 1))
            .itemOutputs(GTModHandler.getModItem("123Technology", "socCosmic", 4))
            .noOptimize()
            .duration(400 * 20)
            .eut(TierEU.RECIPE_MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemInputs(
                GTModHandler.getModItem("123Technology", "socCosmic", 8),
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
            .fluidInputs(liquids.MagmatterA.getFluidOrGas(64 * 144))
            .fluidOutputs(liquids.MagmatterB.getFluidOrGas(48 * 144))
            .itemInputs(GTModHandler.getModItem("123Technology", "IsaBee", 4), ItemList.Field_Generator_UEV.get(3))
            .noOptimize()
            .duration(123 * 20)
            .eut(1232112321)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .fluidInputs(
                liquids.MagmatterB.getFluidOrGas(64 * 144),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 8000),
                new FluidStack(UIVsc, 64 * 144))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluidID("molten.magmatter"), 64 * 144))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 32417),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Optical, 8))
            .noOptimize()
            .duration(3600 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .fluidInputs(
                liquids.MagmatterB.getFluidOrGas(64 * 144),
                new FluidStack(FluidRegistry.getFluidID("exciteddtsc"), 6000),
                new FluidStack(UMVsc, 32 * 144))
            .fluidOutputs(new FluidStack(FluidRegistry.getFluidID("molten.magmatter"), 64 * 144))
            .itemInputs(
                GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 32417),
                GTOreDictUnificator.get(OrePrefixes.circuit, Materials.Exotic, 64))
            .noOptimize()
            .duration(1800 * 20)
            .eut(TierEU.MAX)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 1, 26))
            .itemInputs(
                GTModHandler.getModItem("GalacticraftAmunRa", "item.baseItem", 0, 26),
                GTModHandler.getModItem("123Technology", "IsaBee", 0))
            .fluidInputs(
                new FluidStack(FluidRegistry.getFluidID("molten.blackdwarfmatter"), 72),
                new FluidStack(FluidRegistry.getFluidID("molten.whitedwarfmatter"), 72))
            .noOptimize()
            .duration(100 * 20)
            .eut(123123123)
            .addTo(ISA);
        GTValues.RA.stdBuilder()
            .itemOutputs(GTModHandler.getModItem("123Technology", "singularity1", 1))
            .itemInputs(
                GTUtility.getIntegratedCircuit(12),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 0, 15410),
                GTModHandler.getModItem("eternalsingularity", "eternal_singularity", 0),
                GTModHandler.getModItem("gregtech", "gt.blockmachines", 4, 4239),
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
            .itemOutputs(GTModHandler.getModItem("123Technology", "singularity2", 4))
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
        //IMBABlastFurnace
        RecipeBuilder.builder()
            .itemOutputs(setStackSize(GTModHandler.getModItem("gregtech","gt.blockmachines",1,23543),1))
            .fluidInputs(FluidRegistry.getFluidStack("molten.solderingalloy", Integer.MAX_VALUE))
            .itemInputs(
                setStackSize(GTModHandler.getModItem("gregtech", "gt.blockmachines", 1, 12730), Integer.MAX_VALUE))
            .noOptimize()
            .eut(TierEU.RECIPE_UXV)
            .duration(114 * 20)
            .addTo(ISA);
    }
}
