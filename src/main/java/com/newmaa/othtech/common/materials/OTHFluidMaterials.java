package com.newmaa.othtech.common.materials;

import gregtech.api.enums.MaterialBuilder;
import gregtech.api.enums.TextureSet;

public final class OTHFluidMaterials {

    private OTHFluidMaterials() {}

    public static MaterialRegistrar galaxy() {
        return fluid("Galaxy", "Molten Galaxy", "☆", 0x1250A1);
    }

    public static MaterialRegistrar voidEntropy() {
        return fluid("VoidEntropy", "Void Entropy Flow", "Vi", 0xA6D4DF);
    }

    public static MaterialRegistrar stars() {
        return fluid("Stars", "Molten Stardust", "St", 0xDDE2BC);
    }

    public static MaterialRegistrar lcl() {
        return fluid("LCL", "L.C.L", "L.C.L", 0xFA6F2A);
    }

    public static MaterialRegistrar fuela() {
        return fluid("FUELA", "FUELA", "化学能的尽头", 0x9AF948);
    }

    public static MaterialRegistrar fuelaDepleted() {
        return fluid("FUELA_DE", "Depleted FUELA", "他能干什么？", 0x74B63B);
    }

    public static MaterialRegistrar promoterZPM() {
        return fluid("PromoterZPM", "Atomic Separation Promoter ZPM", "ZPM*", 0xF71616);
    }

    public static MaterialRegistrar promoterUEV() {
        return fluid("PromoterUEV", "Atomic Separation Promoter UEV", "UEV*", 0xECF2FF);
    }

    public static MaterialRegistrar hcl10() {
        return fluid("HCl10", "Super Hydrochloric Acid", "HCl₁₀", 0xF0FFF0);
    }

    public static MaterialRegistrar hcl100() {
        return fluid("HCl100", "Extreme Hydrochloric Acid", "HCl₁₀₀", 0xF0FFF0);
    }

    public static MaterialRegistrar hcl1000() {
        return fluid("HCl1000", "Strange Hydrochloric Acid", "HCl₁₀₀₀", 0xF0FFF0);
    }

    public static MaterialRegistrar hcl10000() {
        return fluid("HCl10000", "Magic Hydrochloric Acid", "HCl₁₀₀₀₀", 0xF0FFF0);
    }

    public static MaterialRegistrar hcl100000() {
        return fluid("HCl100000", "Final Hydrochloric Acid", "HCl₁₀₀₀₀₀", 0xF0FFF0);
    }

    private static MaterialRegistrar fluid(String name, String localName, String formula, int rgb) {
        return new MaterialRegistrar(
            name,
            () -> new MaterialBuilder().setName(name)
                .setDefaultLocalName(localName)
                .setChemicalFormula(formula)
                .setIconSet(TextureSet.SET_FLUID)
                .setARGB(rgb)
                .addFluid()
                .addCell()
                .constructMaterial(),
            material -> OTHMaterials.assign(name, material));
    }
}
