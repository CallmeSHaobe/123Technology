package com.newmaa.othtech.common.materials;

import static net.minecraft.util.StatCollector.translateToLocalFormatted;

import gregtech.api.enums.MaterialBuilder;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SubTag;
import gregtech.api.enums.TextureSet;

public final class OTHMetalMaterials {

    private OTHMetalMaterials() {}

    public static MaterialRegistrar irOsSm() {
        return metal("IrOsSm", "IrOsSm", "IrOsSm", 0x24A8FF, TextureSet.SET_SHINY, 114514, false);
    }

    public static MaterialRegistrar gunmu() {
        return metal(
            "Gunmu",
            "Observable Gunmu",
            translateToLocalFormatted("oth.tt.gunmu.name"),
            0xF8F8FF,
            TextureSet.SET_NONE,
            114514,
            true);
    }

    public static MaterialRegistrar deGunmu() {
        return new MaterialRegistrar(
            "DeGunmu",
            () -> new MaterialBuilder().setName("DeGunmu")
                .setDefaultLocalName("Unobservable Gunmu")
                .setChemicalFormula("")
                .setIconSet(TextureSet.SET_SHINY)
                .setARGB(0xFFFFF0)
                .setMeltingPoint(114514)
                .setBlastFurnaceTemp(114514)
                .addFluid()
                .addCell()
                .addOrePrefix(OrePrefixes.cellMolten)
                .addSubTag(SubTag.SMELTING_TO_FLUID)
                .constructMaterial(),
            material -> OTHMaterials.assign("DeGunmu", material));
    }

    private static MaterialRegistrar metal(String name, String localName, String formula, int rgb,
        TextureSet textureSet, int meltingPoint, boolean allParts) {
        return new MaterialRegistrar(name, () -> {
            MaterialBuilder builder = new MaterialBuilder().setName(name)
                .setDefaultLocalName(localName)
                .setChemicalFormula(formula)
                .setIconSet(textureSet)
                .setARGB(rgb)
                .addDustItems()
                .addMetalItems()
                .setMeltingPoint(meltingPoint)
                .setBlastFurnaceTemp(meltingPoint)
                .addSubTag(SubTag.METAL)
                .addSubTag(SubTag.SMELTING_TO_FLUID);
            if (allParts) {
                builder = builder.addGearItems()
                    .addToolHeadItems()
                    .addOrePrefix(OrePrefixes.round);
            }
            return builder.constructMaterial();
        }, material -> OTHMaterials.assign(name, material));
    }
}
