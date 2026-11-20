package com.newmaa.othtech.common.compat.thaumcraft;

import com.newmaa.othtech.machine.voidnexus.VoidNexusCalculator;

/**
 * One entry in the ordered Void Nexus Matrix progression tree.
 */
public enum VoidNexusUpgrade {

    PSIONIC_THEORY(1, VoidNexusCalculator.Stage.UHV),
    GAIA_COMMUNICATOR(2, VoidNexusCalculator.Stage.UHV),
    ENDLESS_WAND_SYSTEM(3, VoidNexusCalculator.Stage.UHV),
    BLOOD_RUNE_SAVER(4, VoidNexusCalculator.Stage.UHV),
    BEYOND_DIMENSIONS(5, VoidNexusCalculator.Stage.UEV),
    VOID_LINK_THEORY(6, VoidNexusCalculator.Stage.UEV, 2),
    SOUL_MOON_NEST(7, VoidNexusCalculator.Stage.UEV),
    HUNGRY_TOOL(8, VoidNexusCalculator.Stage.UEV),
    LIFE_WEAVE(9, VoidNexusCalculator.Stage.UEV),
    WORLD_DEVOURER(10, VoidNexusCalculator.Stage.UEV),
    PSIONIC_HARMONY(11, VoidNexusCalculator.Stage.UIV, 3),
    MOON_CONTRACT(12, VoidNexusCalculator.Stage.UIV, 1, 7, 11),
    TOOL_CONTRACT(13, VoidNexusCalculator.Stage.UIV, 1, 8, 11),
    WEAVE_CONTRACT(14, VoidNexusCalculator.Stage.UIV, 1, 9, 11),
    DEVOURER_CONTRACT(15, VoidNexusCalculator.Stage.UIV, 1, 10, 11),
    ENTER_THE_VOID(16, VoidNexusCalculator.Stage.UMV, 4),
    EARTH_SHAPER(17, VoidNexusCalculator.Stage.UMV),
    OUTER_PLANE_GUEST(18, VoidNexusCalculator.Stage.UMV),
    GOLDEN_DREAM(19, VoidNexusCalculator.Stage.UMV),
    ENDLESS_TIDE(20, VoidNexusCalculator.Stage.UMV),
    VAST_MOUNTAIN(21, VoidNexusCalculator.Stage.UMV),
    HOLLOW_POINT(22, VoidNexusCalculator.Stage.UMV),
    THRONE_OF_EMPTINESS(23, VoidNexusCalculator.Stage.UMV),
    DARK_RIVER(24, VoidNexusCalculator.Stage.UMV),
    REFORGE_THE_VOID(25, VoidNexusCalculator.Stage.UXV, 5),
    WHISPER_OF_VOID(26, VoidNexusCalculator.Stage.UXV),
    END_OF_REINCARNATION(27, VoidNexusCalculator.Stage.UXV),
    FIND_ONES_OWN_PATH(28, VoidNexusCalculator.Stage.UXV);

    public static final String RESEARCH_PREFIX = "OTH_VOID_NEXUS_";

    public final int id;
    public final VoidNexusCalculator.Stage minimumStage;
    public final int minimumStructureTier;
    private final int[] prerequisiteIds;

    VoidNexusUpgrade(int id, VoidNexusCalculator.Stage minimumStage) {
        this(id, minimumStage, 1);
    }

    VoidNexusUpgrade(int id, VoidNexusCalculator.Stage minimumStage, int minimumStructureTier, int... prerequisiteIds) {
        this.id = id;
        this.minimumStage = minimumStage;
        this.minimumStructureTier = minimumStructureTier;
        this.prerequisiteIds = prerequisiteIds.clone();
    }

    public String getResearchKey() {
        return RESEARCH_PREFIX + id;
    }

    public String getDescriptionKey() {
        return "tc.research_page." + getResearchKey() + ".description";
    }

    public String getEffectKey() {
        return "tc.research_page." + getResearchKey() + ".effect";
    }

    public int[] getPrerequisiteIds() {
        return prerequisiteIds.clone();
    }

    public static VoidNexusUpgrade byId(int id) {
        VoidNexusUpgrade[] values = values();
        return id >= 1 && id <= values.length ? values[id - 1] : null;
    }
}
