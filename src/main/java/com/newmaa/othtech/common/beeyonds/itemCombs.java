package com.newmaa.othtech.common.beeyonds;

import static gregtech.api.enums.GTValues.*;
import static gregtech.api.enums.Mods.*;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidRegistry;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.Utils.RecipeBuilder;
import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;

public class itemCombs extends Item {

    @SideOnly(Side.CLIENT)
    private IIcon secondIcon;

    public itemCombs() {
        super();
        this.setCreativeTab(CreativeTabsLoader.tabothtech);
        this.setHasSubtypes(true);
        this.setUnlocalizedName("oth.comb");
        GameRegistry.registerItem(this, "oth.comb", OTHTechnology.MODID);
    }

    public ItemStack getStackForType(combTypes type) {
        return new ItemStack(this, 1, type.ordinal());
    }

    public ItemStack getStackForType(combTypes type, int count) {
        return new ItemStack(this, count, type.ordinal());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (combTypes type : combTypes.values()) {
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
        int colour = combTypes.get(stack.getItemDamage())
            .getColours()[0];

        if (pass >= 1) {
            colour = combTypes.get(stack.getItemDamage())
                .getColours()[1];
        }

        return colour;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return combTypes.get(stack.getItemDamage())
            .getName();
    }

    public static void initCombsRecipes() {
        // HYPOGEN COMB FOR DTPF RECIPES
        RecipeBuilder.builder()
            .itemInputs(combTypes.HYPOGEN.getStackForType(1))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.neutronium", 5760),
                FluidRegistry.getFluidStack("molten.quantum", 5760),
                FluidRegistry.getFluidStack("molten.infinity", 1440),
                FluidRegistry.getFluidStack("exciteddtrc", 1000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.hypogen", 2880 * 2),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 1000))
            .specialValue(12000)
            .eut(1200000000)
            .duration(75 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        RecipeBuilder.builder()
            .itemInputs(
                combTypes.HYPOGEN.getStackForType(1),
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32100))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.neutronium", 5760),
                FluidRegistry.getFluidStack("molten.quantum", 5760),
                FluidRegistry.getFluidStack("molten.infinity", 1440),
                FluidRegistry.getFluidStack("exciteddtec", 1000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.hypogen", 5760 * 2),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 1000 * 2))
            .specialValue(13000)
            .eut(1600000000)
            .duration(75 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        RecipeBuilder.builder()
            .itemInputs(
                combTypes.HYPOGEN.getStackForType(1),
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32100))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.neutronium", 11520),
                FluidRegistry.getFluidStack("molten.quantum", 5760),
                FluidRegistry.getFluidStack("molten.infinity", 1440),
                FluidRegistry.getFluidStack("exciteddtsc", 1000))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.hypogen", 11520 * 2),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 2000 * 2))
            .specialValue(13000)
            .eut(2000000000)
            .duration(75 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        // SHABI QUANTUM ALLOY
        RecipeBuilder.builder()
            .itemInputs(
                combTypes.HYPOGEN.getStackForType(4),
                GTModHandler.getModItem("miscutils", "item.itemBufferCore10", 0),
                GTModHandler.getModItem(NewHorizonsCoreMod.ID, "tile.Quantinum", 4),
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 3, 32105))
            .itemOutputs(GTModHandler.getModItem(GTPlusPlus.ID, "blockBlockAstralTitanium", 4))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.blacktitanium", 64512),
                FluidRegistry.getFluidStack("molten.americium", 9216),
                FluidRegistry.getFluidStack("molten.bismuth", 9216),
                FluidRegistry.getFluidStack("plasma.titanium", 10368),
                FluidRegistry.getFluidStack("exciteddtsc", 1023))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.quantum", 92160),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 2046))
            .specialValue(13000)
            .eut(534141769)
            .duration(13 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        RecipeBuilder.builder()
            .itemInputs(
                combTypes.HYPOGEN.getStackForType(2),
                GTModHandler.getModItem("miscutils", "item.itemBufferCore10", 0),
                GTModHandler.getModItem(NewHorizonsCoreMod.ID, "tile.Quantinum", 2),
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 2, 32105))
            .itemOutputs(GTModHandler.getModItem(GTPlusPlus.ID, "blockBlockAstralTitanium", 2))
            .fluidInputs(
                FluidRegistry.getFluidStack("molten.blacktitanium", 32256),
                FluidRegistry.getFluidStack("molten.americium", 4608),
                FluidRegistry.getFluidStack("molten.bismuth", 4608),
                FluidRegistry.getFluidStack("plasma.titanium", 5184),
                FluidRegistry.getFluidStack("exciteddtec", 2274))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.quantum", 46080),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 2274))
            .specialValue(13000)
            .eut(267070884)
            .duration(27 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        // BYD CHROMATIC GLASS
        RecipeBuilder.builder()
            .itemInputs(combTypes.CHROMATICGLASS.getStackForType(4))
            .fluidInputs(
                FluidRegistry.getFluidStack("exciteddtcc", 156040),
                FluidRegistry.getFluidStack("molten.glass", 36864))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.chromaticglass", 36864),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 19505))
            .specialValue(10000)
            .eut(50331648 * 4)
            .duration(150 / 4 * 20)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        RecipeBuilder.builder()
            .itemInputs(combTypes.CHROMATICGLASS.getStackForType(4))
            .fluidInputs(
                FluidRegistry.getFluidStack("exciteddtpc", 61059),
                FluidRegistry.getFluidStack("molten.glass", 73728))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.chromaticglass", 73728),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 15264))
            .specialValue(11000)
            .eut(100663296 * 4)
            .duration(75 * 20 / 4)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        RecipeBuilder.builder()
            .itemInputs(combTypes.CHROMATICGLASS.getStackForType(4))
            .fluidInputs(
                FluidRegistry.getFluidStack("exciteddtrc", 26350),
                FluidRegistry.getFluidStack("molten.glass", 147456))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.chromaticglass", 147456),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 13175))
            .specialValue(10000)
            .eut(201326592 * 4)
            .duration(187)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        RecipeBuilder.builder()
            .itemInputs(combTypes.CHROMATICGLASS.getStackForType(4))
            .fluidInputs(
                FluidRegistry.getFluidStack("exciteddtec", 11116),
                FluidRegistry.getFluidStack("molten.glass", 294912))
            .fluidOutputs(
                FluidRegistry.getFluidStack("molten.chromaticglass", 294912),
                FluidRegistry.getFluidStack("dimensionallytranscendentresidue", 11116))
            .specialValue(13000)
            .eut(402653184 * 4)
            .duration(93)
            .addTo(RecipeMaps.plasmaForgeRecipes);
        RecipeBuilder.builder()
            .itemInputs(
                combTypes.CHROMATICGLASS.getStackForType(4),
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32105))
            .itemOutputs(GTModHandler.getModItem(GTPlusPlus.ID, "itemDustChromaticGlass", 2))
            .duration(250 * 20)
            .eut(TierEU.RECIPE_UHV)
            .addTo(RecipeMaps.laserEngraverRecipes);
        RecipeBuilder.builder()
            .itemInputs(
                combTypes.CHROMATICGLASS.getStackForType(1),
                GTOreDictUnificator.get(OrePrefixes.dust, Materials.Glass, 32),
                GTModHandler.getModItem("miscutils", "MU-metaitem.01", 0, 32105))
            .itemOutputs(GTModHandler.getModItem(GTPlusPlus.ID, "itemDustChromaticGlass", 2))
            .duration(75 * 20)
            .eut(TierEU.RECIPE_UEV)
            .addTo(RecipeMaps.laserEngraverRecipes);
        // Mad Glass
        RecipeBuilder.builder()
            .itemInputs(combTypes.NORMALGLASS.getStackForType(1))
            .itemOutputs(new ItemStack(Blocks.glass, 1), GTModHandler.getModItem(Forestry.ID, "honeyDrop", 1))
            .outputChances(100, 25)
            .eut(TierEU.HV)
            .duration(20 * 20)
            .addTo(RecipeMaps.centrifugeRecipes);
    }

}
