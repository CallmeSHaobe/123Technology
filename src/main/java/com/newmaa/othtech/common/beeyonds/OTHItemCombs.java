package com.newmaa.othtech.common.beeyonds;

import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.enums.Mods.*;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidRegistry;

import com.dreammaster.block.BlockList;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;
import com.newmaa.othtech.utils.RecipeFluids;
import com.newmaa.othtech.utils.RecipeStacks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.block.base.BasicBlock.BlockTypes;
import gtPlusPlus.core.item.base.BaseItemComponent.ComponentTypes;
import gtPlusPlus.core.material.MaterialsAlloy;
import gtPlusPlus.core.material.MaterialsElements.STANDALONE;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechItemList;
import gtPlusPlus.xmod.gregtech.common.items.MetaGeneratedItemIDs;

public class OTHItemCombs extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon secondIcon;

    public OTHItemCombs() {
        super();
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setHasSubtypes(true);
        this.setUnlocalizedName("oth.comb");
        GameRegistry.registerItem(this, "oth.comb", OTHTechnology.MODID);
    }

    public ItemStack getStackForType(OTHCombTypes type) {
        return new ItemStack(this, 1, type.ordinal());
    }

    public ItemStack getStackForType(OTHCombTypes type, int count) {
        return new ItemStack(this, count, type.ordinal());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (OTHCombTypes type : OTHCombTypes.values()) {
            if (type.mShowInList) {
                list.add(this.getStackForType(type));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public int getRenderPasses(int meta) {
        return 2;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon("forestry:beeCombs.0");
        this.secondIcon = par1IconRegister.registerIcon("forestry:beeCombs.1");
    }

    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        return (pass == 0) ? itemIcon : secondIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        int colour = OTHCombTypes.get(stack.getItemDamage())
            .getColours()[0];

        if (pass >= 1) {
            colour = OTHCombTypes.get(stack.getItemDamage())
                .getColours()[1];
        }

        return colour;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return OTHCombTypes.get(stack.getItemDamage())
            .getName();
    }

    public static void initCombsRecipes() {
        // HYPOGEN COMB FOR DTPF RECIPES
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.HYPOGEN.getStackForType(1),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 40))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.QUANTUM), 5760),
                Materials.Infinity.getMolten(1440),
                Materials.ExcitedDTRC.getFluid(1000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(
                    RecipeFluids.gtppMoltenName(gtPlusPlus.core.material.MaterialsElements.STANDALONE.HYPOGEN),
                    2880 * 2),
                Materials.DTR.getFluid(1000))
            .specialValue(12000)
            .eut(1200000000)
            .duration(75 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.HYPOGEN.getStackForType(1),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.Compressed_Fusion_Reactor.hasBeenSet()
                        ? RecipeStacks.exact(
                            0,
                            GregtechItemList.Compressed_Fusion_Reactor.getItem(),
                            32000 + MetaGeneratedItemIDs.Compressed_Fusion_Reactor.ID)
                        : null),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 40))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.QUANTUM), 5760),
                Materials.Infinity.getMolten(1440),
                Materials.ExcitedDTEC.getFluid(1000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(
                    RecipeFluids.gtppMoltenName(gtPlusPlus.core.material.MaterialsElements.STANDALONE.HYPOGEN),
                    5760 * 2),
                Materials.DTR.getFluid(1000 * 2))
            .specialValue(13000)
            .eut(1600000000)
            .duration(75 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.HYPOGEN.getStackForType(1),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.Compressed_Fusion_Reactor.hasBeenSet()
                        ? RecipeStacks.exact(
                            0,
                            GregtechItemList.Compressed_Fusion_Reactor.getItem(),
                            32000 + MetaGeneratedItemIDs.Compressed_Fusion_Reactor.ID)
                        : null),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Neutronium, 1), 80))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.QUANTUM), 5760),
                Materials.Infinity.getMolten(1440),
                Materials.ExcitedDTSC.getFluid(1000))
            .fluidOutputs(
                FluidRegistry.getFluidStack(
                    RecipeFluids.gtppMoltenName(gtPlusPlus.core.material.MaterialsElements.STANDALONE.HYPOGEN),
                    11520 * 2),
                Materials.DTR.getFluid(2000 * 2))
            .specialValue(13000)
            .eut(2000000000)
            .duration(75 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        // SHABI QUANTUM ALLOY
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.HYPOGEN.getStackForType(4),
                RecipeStacks.exact(4, BlockList.Quantinum.block, 0),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.Laser_Lens_Special.hasBeenSet()
                        ? RecipeStacks.exact(
                            3,
                            GregtechItemList.Laser_Lens_Special.getItem(),
                            32000 + MetaGeneratedItemIDs.Laser_Lens_Special.ID)
                        : null))
            .itemOutputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    ? GTModHandler.getModItem(
                        gregtech.api.enums.Mods.GTPlusPlus.ID,
                        BlockTypes.STANDARD.getTexture() + STANDALONE.ASTRAL_TITANIUM.getUnlocalizedName(),
                        4)
                    : null))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.BLACK_TITANIUM), 64512),
                Materials.Americium.getMolten(9216),
                Materials.Bismuth.getMolten(9216),
                Materials.Titanium.getPlasma(10368),
                Materials.ExcitedDTSC.getFluid(1023))
            .fluidOutputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.QUANTUM), 92160),
                Materials.DTR.getFluid(2046))
            .specialValue(13000)
            .eut(534141769)
            .duration(13 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.HYPOGEN.getStackForType(2),
                RecipeStacks.exact(2, BlockList.Quantinum.block, 0),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.Laser_Lens_Special.hasBeenSet()
                        ? RecipeStacks.exact(
                            2,
                            GregtechItemList.Laser_Lens_Special.getItem(),
                            32000 + MetaGeneratedItemIDs.Laser_Lens_Special.ID)
                        : null))
            .itemOutputs(
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    ? GTModHandler.getModItem(
                        gregtech.api.enums.Mods.GTPlusPlus.ID,
                        BlockTypes.STANDARD.getTexture() + STANDALONE.ASTRAL_TITANIUM.getUnlocalizedName(),
                        2)
                    : null))
            .fluidInputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.BLACK_TITANIUM), 32256),
                Materials.Americium.getMolten(4608),
                Materials.Bismuth.getMolten(4608),
                Materials.Titanium.getPlasma(5184),
                Materials.ExcitedDTEC.getFluid(2274))
            .fluidOutputs(
                FluidRegistry.getFluidStack(RecipeFluids.gtppMoltenName(MaterialsAlloy.QUANTUM), 46080),
                Materials.DTR.getFluid(2274))
            .specialValue(13000)
            .eut(267070884)
            .duration(27 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        // BYD CHROMATIC GLASS
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.CHROMATICGLASS.getStackForType(4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glass, 1), 256))
            .fluidInputs(Materials.ExcitedDTCC.getFluid(156040))
            .fluidOutputs(
                FluidRegistry.getFluidStack(
                    RecipeFluids
                        .gtppMoltenName(gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS),
                    36864),
                Materials.DTR.getFluid(19505))
            .specialValue(10000)
            .eut(50331648 * 4)
            .duration(150 / 4 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.CHROMATICGLASS.getStackForType(4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glass, 1), 512))
            .fluidInputs(Materials.ExcitedDTPC.getFluid(61059))
            .fluidOutputs(
                FluidRegistry.getFluidStack(
                    RecipeFluids
                        .gtppMoltenName(gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS),
                    73728),
                Materials.DTR.getFluid(15264))
            .specialValue(11000)
            .eut(100663296 * 4)
            .duration(75 * 20 / 4)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.CHROMATICGLASS.getStackForType(4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glass, 1), 1024))
            .fluidInputs(Materials.ExcitedDTRC.getFluid(26350))
            .fluidOutputs(
                FluidRegistry.getFluidStack(
                    RecipeFluids
                        .gtppMoltenName(gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS),
                    147456),
                Materials.DTR.getFluid(13175))
            .specialValue(10000)
            .eut(201326592 * 4)
            .duration(187)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.CHROMATICGLASS.getStackForType(4),
                setStackSize(GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glass, 1), 2048))
            .fluidInputs(Materials.ExcitedDTEC.getFluid(11116))
            .fluidOutputs(
                FluidRegistry.getFluidStack(
                    RecipeFluids
                        .gtppMoltenName(gtPlusPlus.core.material.MaterialsElements.STANDALONE.CHRONOMATIC_GLASS),
                    294912),
                Materials.DTR.getFluid(11116))
            .specialValue(13000)
            .eut(402653184 * 4)
            .duration(93)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(24),
                OTHCombTypes.CHROMATICGLASS.getStackForType(4),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.Laser_Lens_Special.hasBeenSet()
                        ? RecipeStacks.exact(
                            0,
                            GregtechItemList.Laser_Lens_Special.getItem(),
                            32000 + MetaGeneratedItemIDs.Laser_Lens_Special.ID)
                        : null))
            .itemOutputs(RecipeStacks.gtppComponent(2, STANDALONE.CHRONOMATIC_GLASS, ComponentTypes.DUST))
            .duration(250 * 20)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.laserEngraverRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(
                GTUtility.getIntegratedCircuit(17),
                OTHCombTypes.CHROMATICGLASS.getStackForType(1),
                new ItemStack(Blocks.glass, 32),
                (GregTechAPI.sPreloadStarted && gregtech.api.enums.Mods.GTPlusPlus.isModLoaded()
                    && GregtechItemList.Laser_Lens_Special.hasBeenSet()
                        ? RecipeStacks.exact(
                            0,
                            GregtechItemList.Laser_Lens_Special.getItem(),
                            32000 + MetaGeneratedItemIDs.Laser_Lens_Special.ID)
                        : null))
            .itemOutputs(RecipeStacks.gtppComponent(2, STANDALONE.CHRONOMATIC_GLASS, ComponentTypes.DUST))
            .duration(75 * 20)
            .eut(TierEU.RECIPE_UEV)
            .addTo(RecipeMaps.laserEngraverRecipes);
        // Mad Glass
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(OTHCombTypes.NORMALGLASS.getStackForType(1))
            .itemOutputs(new ItemStack(Blocks.glass, 1), GTModHandler.getModItem(Forestry.ID, "honeyDrop", 1))
            .outputChances(100, 25)
            .eut(TierEU.HV)
            .duration(20 * 20)
            .addTo(RecipeMaps.centrifugeRecipes);
        GTValues.RA.stdBuilder()
            .itemInputsUnsafe(OTHCombTypes.NORMALGLASS.getStackForType(1))
            .itemOutputs(new ItemStack(Blocks.glass, 1), GTModHandler.getModItem(Forestry.ID, "honeyDrop", 1))
            .outputChances(100, 25)
            .eut(TierEU.HV)
            .duration(20 * 20)
            .addTo(RecipeMaps.centrifugeNonCellRecipes);
    }

}
