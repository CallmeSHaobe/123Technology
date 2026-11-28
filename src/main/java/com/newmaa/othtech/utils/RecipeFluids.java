package com.newmaa.othtech.utils;

import java.util.Locale;

import net.minecraftforge.fluids.Fluid;

import bartworks.system.material.Werkstoff;
import gregtech.api.util.StringUtils;
import gtPlusPlus.core.material.Material;
import ic2.core.init.InternalName;

/** Derives exact registry keys from upstream fluid registration rules, without alias fallback. */
public final class RecipeFluids {

    private RecipeFluids() {}

    /** Looks up by name instead of resolving a cached Fluid in the world's current ID map. */
    public static String registryName(Fluid fluid) {
        return fluid == null ? null : fluid.getName();
    }

    /** GT++ can bind a material to an alias fluid during generation; this selects only its molten registry key. */
    public static String gtppMoltenName(Material material) {
        return StringUtils.sanitizeString(("molten." + material.getUnlocalizedName()).toLowerCase(Locale.ROOT));
    }

    public static String gtppPlasmaName(Material material) {
        return "plasma." + StringUtils.sanitizeString(
            material.getDefaultLocalName()
                .toLowerCase(Locale.ROOT));
    }

    public static String ic2FluidName(InternalName fluid) {
        return "ic2" + fluid.name()
            .substring("fluid".length())
            .toLowerCase(Locale.ENGLISH);
    }

    public static String werkstoffName(Werkstoff material) {
        return material.getDefaultName()
            .toLowerCase(Locale.ENGLISH);
    }

    public static String werkstoffMoltenName(Werkstoff material) {
        return "molten." + werkstoffName(material);
    }
}
