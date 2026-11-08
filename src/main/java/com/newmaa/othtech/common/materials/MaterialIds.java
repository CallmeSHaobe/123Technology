package com.newmaa.othtech.common.materials;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;

final class MaterialIds {

    private static boolean[] used;
    private static int cursor = 999;

    private MaterialIds() {}

    static synchronized int next() {
        if (used == null) {
            used = new boolean[1000];
            for (Materials material : Materials.getAll()) {
                final int id = material == null ? -1 : material.mMetaItemSubID;
                if (id >= 0 && id < used.length) used[id] = true;
            }
            for (int i = 0; i < used.length; i++) {
                if (GregTechAPI.sGeneratedMaterials[i] != null) used[i] = true;
            }
        }
        while (cursor >= 0 && used[cursor]) cursor--;
        if (cursor < 0) throw new IllegalStateException("No free GregTech material ID left for 123 Technology");
        used[cursor] = true;
        return cursor;
    }
}
