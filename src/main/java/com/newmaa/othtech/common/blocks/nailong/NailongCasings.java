package com.newmaa.othtech.common.blocks.nailong;

import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class NailongCasings {

    public static BlockNailongCasing nailongCasing;

    public static void initialize() {
        nailongCasing = new BlockNailongCasing();
        GameRegistry.registerBlock(nailongCasing, ItemBlockNailongCasing.class, "nailongCasing");

        OTHItemList.PsychicWrappedCasing.set(new ItemStack(nailongCasing, 1, NailongCasingMeta.PSYCHIC_WRAPPED_CASING));
        OTHItemList.PsionicRealityCasing.set(new ItemStack(nailongCasing, 1, NailongCasingMeta.PSIONIC_REALITY_CASING));
        OTHItemList.SubspaceShieldCasing.set(new ItemStack(nailongCasing, 1, NailongCasingMeta.SUBSPACE_SHIELD_CASING));
        OTHItemList.VoidStabilizationCasing
            .set(new ItemStack(nailongCasing, 1, NailongCasingMeta.VOID_STABILIZATION_CASING));
        OTHItemList.EndlessPsionicGlass.set(new ItemStack(nailongCasing, 1, NailongCasingMeta.ENDLESS_PSIONIC_GLASS));
        OTHItemList.SpacetimePotentialGlass
            .set(new ItemStack(nailongCasing, 1, NailongCasingMeta.SPACETIME_POTENTIAL_GLASS));
    }

    private NailongCasings() {}
}
