package com.newmaa.othtech.utils;

import java.util.Locale;

import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.Loader;

public enum modsEnum {

    TwistSpaceTechnology(modsID.TST),
    NHUtilities(modsID.NHU),
    ET_Futurum(modsID.ET_Futurum),
    GTNN(modsID.GTNN);

    public static class modsID {

        public static final String TST = "TwistSpaceTechnology";
        public static final String NHU = "NHUtilities";
        public static final String ET_Futurum = "etfuturum";
        public static final String GTNN = "gtnn";
    }

    public final String ID;
    public final String resourceDomain;
    private Boolean modLoaded;

    modsEnum(String ID) {
        this.ID = ID;
        this.resourceDomain = ID.toLowerCase(Locale.ENGLISH);
    }

    public boolean isModLoaded() {
        if (this.modLoaded == null) {
            this.modLoaded = Loader.isModLoaded(ID);
        }
        return this.modLoaded;
    }

    public String getResourcePath(String... path) {
        return this.getResourceLocation(path)
            .toString();
    }

    public ResourceLocation getResourceLocation(String... path) {
        return new ResourceLocation(this.resourceDomain, String.join("/", path));
    }
}
