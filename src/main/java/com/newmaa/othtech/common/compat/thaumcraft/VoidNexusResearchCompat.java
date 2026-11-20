package com.newmaa.othtech.common.compat.thaumcraft;

import net.minecraft.util.ResourceLocation;

import com.newmaa.othtech.OTHTechnology;

import cpw.mods.fml.common.Loader;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

/**
 * Registers the Void Nexus research branch without assuming a new research tab
 * when another loaded feature has already created the 123Technology category.
 */
public final class VoidNexusResearchCompat {

    public static final String CATEGORY = "123technology";
    // Leave columns 0 through 2 for the existing Thaumcraft research.
    private static final int BRANCH_COLUMN_OFFSET = 4;

    private VoidNexusResearchCompat() {}

    public static void register() {
        if (!Loader.isModLoaded("Thaumcraft")) {
            OTHTechnology.LOG.info("Thaumcraft not loaded; skipping Void Nexus research integration.");
            return;
        }

        registerCategory();
        for (VoidNexusUpgrade upgrade : VoidNexusUpgrade.values()) registerResearch(upgrade);
    }

    private static void registerCategory() {
        if (ResearchCategories.getResearchList(CATEGORY) != null) return;
        ResearchCategories.registerCategory(
            CATEGORY,
            new ResourceLocation("thaumcraft", "textures/aspects/vacuos.png"),
            new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
    }

    private static void registerResearch(VoidNexusUpgrade upgrade) {
        String key = upgrade.getResearchKey();
        if (ResearchCategories.getResearch(key) != null) return;

        AspectList aspects = new AspectList().add(Aspect.ELDRITCH, 2 + upgrade.id)
            .add(Aspect.MAGIC, 2 + upgrade.id / 2)
            .add(Aspect.MIND, 1 + upgrade.id / 3);
        ResearchItem item = new ResearchItem(
            key,
            CATEGORY,
            aspects,
            BRANCH_COLUMN_OFFSET + getColumn(upgrade),
            getRow(upgrade),
            upgrade.id < 5 ? 1 : (upgrade.id < 16 ? 2 : 3),
            Aspect.ELDRITCH.getImage());
        item.setPages(new ResearchPage(upgrade.getDescriptionKey()), new ResearchPage(upgrade.getEffectKey()));
        String[] parents = getParents(upgrade);
        if (parents.length > 0) item.setParents(parents);
        item.registerResearchItem();
    }

    private static int getColumn(VoidNexusUpgrade upgrade) {
        switch (upgrade) {
            case PSIONIC_THEORY:
                return 0;
            case GAIA_COMMUNICATOR:
                return 0;
            case ENDLESS_WAND_SYSTEM:
                return 0;
            case BLOOD_RUNE_SAVER:
                return 0;
            case BEYOND_DIMENSIONS:
                return 2;
            case VOID_LINK_THEORY:
                return 4;
            case SOUL_MOON_NEST:
            case HUNGRY_TOOL:
            case LIFE_WEAVE:
            case WORLD_DEVOURER:
                return 6;
            case PSIONIC_HARMONY:
                return 8;
            case MOON_CONTRACT:
            case TOOL_CONTRACT:
            case WEAVE_CONTRACT:
            case DEVOURER_CONTRACT:
                return 10;
            case ENTER_THE_VOID:
                return 12;
            case EARTH_SHAPER:
            case OUTER_PLANE_GUEST:
            case GOLDEN_DREAM:
            case ENDLESS_TIDE:
            case VAST_MOUNTAIN:
            case HOLLOW_POINT:
            case THRONE_OF_EMPTINESS:
            case DARK_RIVER:
                return 14;
            case REFORGE_THE_VOID:
                return 16;
            case WHISPER_OF_VOID:
            case END_OF_REINCARNATION:
            case FIND_ONES_OWN_PATH:
                return 18;
            default:
                return 0;
        }
    }

    private static int getRow(VoidNexusUpgrade upgrade) {
        switch (upgrade) {
            case PSIONIC_THEORY:
                return 0;
            case GAIA_COMMUNICATOR:
                return -3;
            case ENDLESS_WAND_SYSTEM:
                return 3;
            case BLOOD_RUNE_SAVER:
                return 6;
            case BEYOND_DIMENSIONS:
                return 0;
            case VOID_LINK_THEORY:
                return 0;
            case SOUL_MOON_NEST:
                return -6;
            case HUNGRY_TOOL:
                return -2;
            case LIFE_WEAVE:
                return 2;
            case WORLD_DEVOURER:
                return 6;
            case PSIONIC_HARMONY:
                return 0;
            case MOON_CONTRACT:
                return -6;
            case TOOL_CONTRACT:
                return -2;
            case WEAVE_CONTRACT:
                return 2;
            case DEVOURER_CONTRACT:
                return 6;
            case ENTER_THE_VOID:
                return 0;
            case EARTH_SHAPER:
                return -8;
            case OUTER_PLANE_GUEST:
                return -6;
            case GOLDEN_DREAM:
                return -4;
            case ENDLESS_TIDE:
                return -2;
            case VAST_MOUNTAIN:
                return 0;
            case HOLLOW_POINT:
                return 2;
            case THRONE_OF_EMPTINESS:
                return 4;
            case DARK_RIVER:
                return 6;
            case REFORGE_THE_VOID:
                return 0;
            case WHISPER_OF_VOID:
                return -2;
            case END_OF_REINCARNATION:
                return 0;
            case FIND_ONES_OWN_PATH:
                return 2;
            default:
                return 0;
        }
    }

    private static String[] getParents(VoidNexusUpgrade upgrade) {
        switch (upgrade) {
            case PSIONIC_THEORY:
                return new String[] { "INFUSION", "ICHOR" };
            case GAIA_COMMUNICATOR:
            case ENDLESS_WAND_SYSTEM:
            case BLOOD_RUNE_SAVER:
                return new String[] { VoidNexusUpgrade.PSIONIC_THEORY.getResearchKey() };
            case BEYOND_DIMENSIONS:
                return new String[] { VoidNexusUpgrade.BLOOD_RUNE_SAVER.getResearchKey(),
                    VoidNexusUpgrade.GAIA_COMMUNICATOR.getResearchKey(),
                    VoidNexusUpgrade.ENDLESS_WAND_SYSTEM.getResearchKey() };
            case VOID_LINK_THEORY:
                return new String[] { VoidNexusUpgrade.BEYOND_DIMENSIONS.getResearchKey() };
            case SOUL_MOON_NEST:
            case HUNGRY_TOOL:
            case LIFE_WEAVE:
            case WORLD_DEVOURER:
            case PSIONIC_HARMONY:
                return new String[] { VoidNexusUpgrade.VOID_LINK_THEORY.getResearchKey() };
            case MOON_CONTRACT:
                return new String[] { VoidNexusUpgrade.SOUL_MOON_NEST.getResearchKey(),
                    VoidNexusUpgrade.PSIONIC_HARMONY.getResearchKey() };
            case TOOL_CONTRACT:
                return new String[] { VoidNexusUpgrade.HUNGRY_TOOL.getResearchKey(),
                    VoidNexusUpgrade.PSIONIC_HARMONY.getResearchKey() };
            case WEAVE_CONTRACT:
                return new String[] { VoidNexusUpgrade.LIFE_WEAVE.getResearchKey(),
                    VoidNexusUpgrade.PSIONIC_HARMONY.getResearchKey() };
            case DEVOURER_CONTRACT:
                return new String[] { VoidNexusUpgrade.WORLD_DEVOURER.getResearchKey(),
                    VoidNexusUpgrade.PSIONIC_HARMONY.getResearchKey() };
            case ENTER_THE_VOID:
                return new String[] { VoidNexusUpgrade.PSIONIC_HARMONY.getResearchKey() };
            case EARTH_SHAPER:
            case OUTER_PLANE_GUEST:
            case GOLDEN_DREAM:
            case ENDLESS_TIDE:
            case VAST_MOUNTAIN:
            case HOLLOW_POINT:
            case THRONE_OF_EMPTINESS:
            case DARK_RIVER:
                return new String[] { VoidNexusUpgrade.ENTER_THE_VOID.getResearchKey() };
            case REFORGE_THE_VOID:
                return new String[] { VoidNexusUpgrade.ENTER_THE_VOID.getResearchKey() };
            case WHISPER_OF_VOID:
            case END_OF_REINCARNATION:
            case FIND_ONES_OWN_PATH:
                return new String[] { VoidNexusUpgrade.REFORGE_THE_VOID.getResearchKey() };
            default:
                return new String[0];
        }
    }
}
