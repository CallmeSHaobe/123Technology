package com.newmaa.othtech.common.materials;

import com.newmaa.othtech.OTHTechnology;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;

public final class OTHMaterials {

    public static Materials Galaxy;
    public static Materials VoidEntropy;
    public static Materials Stars;
    public static Materials LCL;
    public static Materials FUELA;
    public static Materials FUELA_DE;
    public static Materials PromoterZPM;
    public static Materials PromoterUEV;
    public static Materials HCl10;
    public static Materials HCl100;
    public static Materials HCl1000;
    public static Materials HCl10000;
    public static Materials HCl100000;
    public static Materials IrOsSm;
    public static Materials Gunmu;
    public static Materials DeGunmu;

    private static boolean installed;

    private OTHMaterials() {}

    public static void install() {
        if (installed) return;
        installed = true;
        if (GregTechAPI.sBeforeGTPreload == null) {
            OTHTechnology.LOG.error("GregTechAPI.sBeforeGTPreload is gone, material migration aborted");
            return;
        }
        GregTechAPI.sBeforeGTPreload.add(OTHMaterials::registerAll);
    }

    private static void registerAll() {
        Materials.add(OTHFluidMaterials.galaxy());
        Materials.add(OTHFluidMaterials.voidEntropy());
        Materials.add(OTHFluidMaterials.stars());
        Materials.add(OTHFluidMaterials.lcl());
        Materials.add(OTHFluidMaterials.fuela());
        Materials.add(OTHFluidMaterials.fuelaDepleted());
        Materials.add(OTHFluidMaterials.promoterZPM());
        Materials.add(OTHFluidMaterials.promoterUEV());
        Materials.add(OTHFluidMaterials.hcl10());
        Materials.add(OTHFluidMaterials.hcl100());
        Materials.add(OTHFluidMaterials.hcl1000());
        Materials.add(OTHFluidMaterials.hcl10000());
        Materials.add(OTHFluidMaterials.hcl100000());
        Materials.add(OTHMetalMaterials.irOsSm());
        Materials.add(OTHMetalMaterials.gunmu());
        Materials.add(OTHMetalMaterials.deGunmu());
    }

    static void assign(String name, Materials material) {
        switch (name) {
            case "Galaxy" -> Galaxy = material;
            case "VoidEntropy" -> VoidEntropy = material;
            case "Stars" -> Stars = material;
            case "LCL" -> LCL = material;
            case "FUELA" -> FUELA = material;
            case "FUELA_DE" -> FUELA_DE = material;
            case "PromoterZPM" -> PromoterZPM = material;
            case "PromoterUEV" -> PromoterUEV = material;
            case "HCl10" -> HCl10 = material;
            case "HCl100" -> HCl100 = material;
            case "HCl1000" -> HCl1000 = material;
            case "HCl10000" -> HCl10000 = material;
            case "HCl100000" -> HCl100000 = material;
            case "IrOsSm" -> IrOsSm = material;
            case "Gunmu" -> Gunmu = material;
            case "DeGunmu" -> DeGunmu = material;
            default -> OTHTechnology.LOG.error("Unknown 123 Technology material {}", name);
        }
    }
}
