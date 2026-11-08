package com.newmaa.othtech.client;

import com.newmaa.othtech.common.materials.OTHMaterials;
import com.newmaa.othtech.common.render.OTEGunmuObservableRenderer;
import com.newmaa.othtech.common.render.OTEGunmuUnobservableRenderer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class OTEMaterialRenderEffects {

    public static final boolean ENABLED = true;

    private static final OTEGunmuObservableRenderer OBSERVABLE = new OTEGunmuObservableRenderer();
    private static final OTEGunmuUnobservableRenderer UNOBSERVABLE = new OTEGunmuUnobservableRenderer();

    private OTEMaterialRenderEffects() {}

    public static void install() {
        if (!ENABLED) return;
        if (OTHMaterials.Gunmu != null) OTHMaterials.Gunmu.renderer = OBSERVABLE;
        if (OTHMaterials.DeGunmu != null) OTHMaterials.DeGunmu.renderer = UNOBSERVABLE;
    }
}
