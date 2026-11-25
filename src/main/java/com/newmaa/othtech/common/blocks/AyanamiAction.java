package com.newmaa.othtech.common.blocks;

/** Stable names shared by saved block state, localization and animation assets. */
public enum AyanamiAction {

    STANDING("standing"),
    NOD("nod"),
    LOOK_AROUND("look_around"),
    WAVE("wave"),
    AWAKENING("awakening"),
    STRIKE("strike"),
    THRUST("thrust");

    private static final AyanamiAction[] PRESETS = { STANDING, NOD, LOOK_AROUND, WAVE };
    private final String id;

    AyanamiAction(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public AyanamiAction next() {
        return PRESETS[(ordinal() + 1) % PRESETS.length];
    }

    public static AyanamiAction fromId(String id) {
        for (AyanamiAction action : PRESETS) {
            if (action.id.equals(id)) return action;
        }
        return STANDING;
    }
}
