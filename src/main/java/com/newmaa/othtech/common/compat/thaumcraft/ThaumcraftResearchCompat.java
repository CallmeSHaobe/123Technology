package com.newmaa.othtech.common.compat.thaumcraft;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import com.arc.bloodarsenal.common.items.ModItems;
import com.dreammaster.block.BlockList;
import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.blocks.nailong.NailongCasingMeta;
import com.newmaa.othtech.common.blocks.nailong.NailongCasings;

import cpw.mods.fml.common.Loader;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TCAspects;
import gregtech.api.util.GTOreDictUnificator;
import net.glease.tc4tweak.api.infusionrecipe.InfusionRecipeExt;
import net.glease.tc4tweak.api.infusionrecipe.RecipeIngredient;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

/**
 * Thaumcraft integration registered after Thaumcraft's own research initialization.
 */
public final class ThaumcraftResearchCompat {

    private static final String CATEGORY = "123technology";
    private static final String PEACH_CAN_RESEARCH = "OTH_PEACH_CAN_INFUSION";
    private static final String NAILONG_RESEARCH = "OTH_NAILONG_INFUSION";
    private static final String SANCTUM_RESEARCH = "OTH_ABSTRACT_SANCTUM";
    private static final String ICHOR_RESEARCH = "ICHOR";
    private static final String ASPECT_TAG = "sechsieben";
    private static final int INFUSION_INSTABILITY = 8;
    private static final int LOW_WARP = 2;
    private static final int VERY_DANGEROUS_WARP = 4;

    private static Aspect sechsieben;
    private static Aspect vesania;

    private ThaumcraftResearchCompat() {}

    public static void register() {
        if (!Loader.isModLoaded("Thaumcraft")) {
            OTHTechnology.LOG.info("Thaumcraft not loaded; skipping Thaumonomicon integration.");
            return;
        }
        if (!Loader.isModLoaded("ForbiddenMagic")) {
            OTHTechnology.LOG.warn("ForbiddenMagic not loaded; Gula/Desidia aspects are unavailable.");
            return;
        }

        vesania = resolveVesaniaAspect();
        if (vesania == null) {
            OTHTechnology.LOG.error("Cannot register {}: Vesania is missing.", ASPECT_TAG);
            return;
        }
        sechsieben = registerSechsiebenAspect();
        if (sechsieben == null) return;

        registerYellowPeachAspect();
        registerCategory();
        registerNailongInfusionResearch();
        registerPeachCanInfusionResearch();
        registerAbstractSanctumResearch();
    }

    private static Aspect resolveVesaniaAspect() {
        Aspect aspect = TCAspects.VESANIA.getAspect();
        return aspect == null ? Aspect.getAspect("custom2") : aspect;
    }

    private static Aspect registerSechsiebenAspect() {
        Aspect existing = Aspect.getAspect(ASPECT_TAG);
        if (existing != null) return existing;

        Aspect humanus = TCAspects.HUMANUS.getAspect();
        if (humanus == null) humanus = Aspect.MAN;
        if (humanus == null) {
            OTHTechnology.LOG.error("Cannot register aspect {}: Humanus is missing.", ASPECT_TAG);
            return null;
        }

        return new Aspect(
            ASPECT_TAG,
            0xFFFFFF,
            new Aspect[] { humanus, vesania },
            new ResourceLocation(OTHTechnology.MODID, "textures/aspects/sechsieben.png"),
            1);
    }

    private static void registerYellowPeachAspect() {
        ItemStack peach = OTHItemList.YellowPeach.get(1);
        AspectList aspects = ThaumcraftApiHelper.getObjectAspects(peach);
        aspects = aspects == null ? new AspectList() : aspects.copy();
        aspects.add(sechsieben, 1);
        ThaumcraftApi.registerObjectTag(peach, aspects);
    }

    private static void registerCategory() {
        if (ResearchCategories.getResearchList(CATEGORY) != null) return;

        ResearchCategories.registerCategory(
            CATEGORY,
            new ResourceLocation(OTHTechnology.MODID, "textures/aspects/sechsieben.png"),
            new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
    }

    private static void registerPeachCanInfusionResearch() {
        if (ResearchCategories.getResearch(PEACH_CAN_RESEARCH) != null) return;

        Aspect gula = Aspect.getAspect("gula");
        Aspect desidia = Aspect.getAspect("desidia");
        if (gula == null || desidia == null) {
            OTHTechnology.LOG.error("Cannot register {}: Gula or Desidia is missing.", PEACH_CAN_RESEARCH);
            return;
        }

        ItemStack input = nailongCasing(1, NailongCasingMeta.NAILONG);
        ItemStack output = nailongCasing(1, NailongCasingMeta.PEACH_CAN);
        if (input == null || output == null) {
            OTHTechnology.LOG.error("Cannot register {}: Nailong casing block is unavailable.", PEACH_CAN_RESEARCH);
            return;
        }

        ItemStack peach = OTHItemList.YellowPeach.get(1);
        ItemStack glass = GTOreDictUnificator.get(OrePrefixes.plate, Materials.BorosilicateGlass, 1);
        ItemStack leek = OTHItemList.LeekBox.get(1);
        ItemStack motor = ItemList.Electric_Motor_LuV.get(1);
        RecipeIngredient circuit = circuitIngredient(Materials.LuV);
        if (peach == null
            || peach.getItem() == null
            || glass == null
            || glass.getItem() == null
            || leek == null
            || leek.getItem() == null
            || motor == null
            || motor.getItem() == null
            || circuit == null) {
            OTHTechnology.LOG
                .error("Cannot register {}: one or more infusion components are unavailable.", PEACH_CAN_RESEARCH);
            return;
        }

        AspectList infusionAspects = nailongInfusionAspects(gula, desidia);
        InfusionRecipe recipe = InfusionRecipeExt.get()
            .addInfusionCraftingRecipe(
                PEACH_CAN_RESEARCH,
                output,
                INFUSION_INSTABILITY,
                infusionAspects,
                input,
                peach,
                motor,
                glass,
                leek,
                circuit,
                leek,
                glass,
                motor,
                peach);
        if (recipe == null) {
            OTHTechnology.LOG.error("Failed to register infusion recipe for {}.", PEACH_CAN_RESEARCH);
            return;
        }

        AspectList researchAspects = new AspectList().add(sechsieben, 10);

        new ResearchItem(PEACH_CAN_RESEARCH, CATEGORY, researchAspects, 0, -2, 1, output)
            .setPages(new ResearchPage(recipe))
            .setParents(NAILONG_RESEARCH)
            .setSecondary()
            .registerResearchItem();
        ThaumcraftApi.addWarpToResearch(PEACH_CAN_RESEARCH, LOW_WARP);
    }

    private static void registerNailongInfusionResearch() {
        if (ResearchCategories.getResearch(NAILONG_RESEARCH) != null) return;

        Aspect gula = Aspect.getAspect("gula");
        Aspect desidia = Aspect.getAspect("desidia");
        if (gula == null || desidia == null) {
            OTHTechnology.LOG.error("Cannot register {}: Gula or Desidia is missing.", NAILONG_RESEARCH);
            return;
        }

        ItemStack input = ItemList.Casing_ZPM.get(1);
        ItemStack output = nailongCasing(1, NailongCasingMeta.NAILONG);
        ItemStack peach = OTHItemList.YellowPeach.get(1);
        ItemStack motor = ItemList.Electric_Motor_LuV.get(1);
        RecipeIngredient circuit = circuitIngredient(Materials.IV);
        if (input == null
            || input.getItem() == null
            || output == null
            || output.getItem() == null
            || peach == null
            || peach.getItem() == null
            || motor == null
            || motor.getItem() == null
            || circuit == null) {
            OTHTechnology.LOG.error("Cannot register {}: one or more recipe items are unavailable.", NAILONG_RESEARCH);
            return;
        }

        AspectList infusionAspects = nailongInfusionAspects(gula, desidia);
        InfusionRecipe recipe = InfusionRecipeExt.get()
            .addInfusionCraftingRecipe(
                NAILONG_RESEARCH,
                output,
                INFUSION_INSTABILITY,
                infusionAspects,
                input,
                peach,
                motor,
                circuit,
                peach,
                peach,
                circuit,
                motor,
                peach);
        if (recipe == null) {
            OTHTechnology.LOG.error("Failed to register infusion recipe for {}.", NAILONG_RESEARCH);
            return;
        }

        AspectList researchAspects = new AspectList().add(sechsieben, 5).add(vesania, 5);
        new ResearchItem(NAILONG_RESEARCH, CATEGORY, researchAspects, 0, 0, 1, output)
            .setPages(new ResearchPage(recipe))
            .setParents("INFUSION")
            .setSecondary()
            .registerResearchItem();
        ThaumcraftApi.addWarpToResearch(NAILONG_RESEARCH, LOW_WARP);
    }

    private static void registerAbstractSanctumResearch() {
        if (ResearchCategories.getResearch(SANCTUM_RESEARCH) != null) return;
        if (!Loader.isModLoaded("ThaumicTinkerer") || ResearchCategories.getResearch(ICHOR_RESEARCH) == null) {
            OTHTechnology.LOG.error(
                "Cannot register {}: prerequisite research {} is unavailable.",
                SANCTUM_RESEARCH,
                ICHOR_RESEARCH);
            return;
        }

        ItemStack input = ItemList.Hull_LuV.get(1);
        ItemStack output = OTHItemList.AbstractSanctum.get(1);
        ItemStack gaia = BlockList.Gaia.get(1);
        ItemStack bloodyIchorium = BlockList.BloodyIchorium.get(1);
        ItemStack unchargedBloodDiamond = ModItems.blood_infused_diamond_unactive == null ? null
            : new ItemStack(ModItems.blood_infused_diamond_unactive);
        ItemStack nailong = nailongCasing(1, NailongCasingMeta.NAILONG);
        ItemStack fieldGenerator = ItemList.Field_Generator_LuV.get(1);
        if (input == null || input.getItem() == null
            || output == null
            || output.getItem() == null
            || gaia == null
            || gaia.getItem() == null
            || bloodyIchorium == null
            || bloodyIchorium.getItem() == null
            || unchargedBloodDiamond == null
            || unchargedBloodDiamond.getItem() == null
            || nailong == null
            || nailong.getItem() == null
            || fieldGenerator == null
            || fieldGenerator.getItem() == null) {
            OTHTechnology.LOG.error("Cannot register {}: one or more recipe items are unavailable.", SANCTUM_RESEARCH);
            return;
        }

        AspectList infusionAspects = new AspectList().add(sechsieben, 67)
            .add(Aspect.MAGIC, 300)
            .add(Aspect.CRAFT, 300)
            .add(Aspect.MIND, 300)
            .add(Aspect.TAINT, 300);
        ItemStack[] components = {
            gaia,
            bloodyIchorium,
            unchargedBloodDiamond,
            nailong,
            fieldGenerator,
            nailong,
            fieldGenerator,
            fieldGenerator,
            nailong,
            fieldGenerator,
            nailong,
            unchargedBloodDiamond,
            bloodyIchorium,
            gaia };

        InfusionRecipe recipe = ThaumcraftApi.addInfusionCraftingRecipe(
            SANCTUM_RESEARCH,
            output,
            INFUSION_INSTABILITY,
            infusionAspects,
            input,
            components);
        if (recipe == null) {
            OTHTechnology.LOG.error("Failed to register infusion recipe for {}.", SANCTUM_RESEARCH);
            return;
        }

        // Mirrors INFUSION's research-note aspects, replacing MAGIC 6 with Sechsieben 6.
        AspectList researchAspects = new AspectList().add(sechsieben, 6)
            .add(Aspect.MECHANISM, 3)
            .add(Aspect.CRAFT, 6);

        new ResearchItem(SANCTUM_RESEARCH, CATEGORY, researchAspects, 2, 0, 2, output)
            .setPages(
                new ResearchPage("tc.research_page." + SANCTUM_RESEARCH + ".1"),
                new ResearchPage(recipe))
            .setParents(NAILONG_RESEARCH, ICHOR_RESEARCH)
            .setConcealed()
            .registerResearchItem();
        ThaumcraftApi.addWarpToResearch(SANCTUM_RESEARCH, VERY_DANGEROUS_WARP);
    }

    private static ItemStack nailongCasing(int amount, int meta) {
        if (NailongCasings.nailongCasing == null) return null;
        return new ItemStack(NailongCasings.nailongCasing, amount, meta);
    }

    private static AspectList nailongInfusionAspects(Aspect gula, Aspect desidia) {
        return new AspectList().add(sechsieben, 11)
            .add(gula, 45)
            .add(desidia, 14);
    }

    private static RecipeIngredient circuitIngredient(Materials material) {
        Object circuitOre = OrePrefixes.circuit.get(material);
        if (circuitOre == null) return null;
        String oreDict = circuitOre.toString();
        if (oreDict == null || OreDictionary.getOres(oreDict)
            .isEmpty()) {
            return null;
        }
        return RecipeIngredient.oredict(oreDict);
    }
}
