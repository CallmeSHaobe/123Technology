package com.newmaa.othtech.common.materials;

import static gregtech.api.util.GTLanguageManager.addStringLocalization;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

public class bwMaterialsLocalization {
    public void loader() {
        // fluid and materials
        String[] akey2 = new String[] { "bw.werkstoff.12303.name", "fluid.fluidgalaxy", "bw.werkstoff.12304.name", "fluid.fluidvoid"
        ,"bw.werkstoff.12305.name", "fluid.moltenstar","bw.werkstoff.12308.name", "fluid.fuela","bw.werkstoff.12309.name", "fluid.fuela_de",
            "bw.werkstoff.12310.name", "fluid.promoterzpm","bw.werkstoff.12311.name", "fluid.promoteruev", "molten.irossm", "bw.werkstoff.12320.name"};
        String[] aenglish2 = new String[] { "fluid.FluidGalaxy.name", "fluid.FluidGalaxy.name","fluid.FluidVoid.name", "fluid.FluidVoid.name"
        ,"fluid.MoltenStar.name", "fluid.MoltenStar.name","fluid.FUELA.name", "fluid.FUELA.name","fluid.FUELA_DE.name", "fluid.FUELA_DE.name"
        ,"fluid.PromoterZPM.name", "fluid.PromoterZPM.name","fluid.PromoterUEV.name", "fluid.PromoterUEV.name", "fluid.IrOsSm.name", "item.IrOsSm.name"};
        for (int i = 0; i < akey2.length; i++) {
            addStringLocalization(akey2[i], translateToLocalFormatted(aenglish2[i]));
        }
    }
}
