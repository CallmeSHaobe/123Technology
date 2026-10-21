package com.newmaa.othtech.common.dimensions.gtoregen;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gregtech.api.enums.Materials;
import gregtech.common.OreMixBuilder;
import gregtech.common.WorldgenGTOreLayer;

public final class AntimoniaGTOreVeinReg {

    // GPT NIUBI
    private static final List<OreMixBuilder> BUILDERS = new ArrayList<>();

    private static final Set<String> REGISTERED_NAMES = new HashSet<>();

    private AntimoniaGTOreVeinReg() {}

    /**
     * 注册锑星的全部自定义大矿脉。 由 {@code OTHDimensionIntegration#scheduleOreVeinRegistration()} 通过
     * {@code GregTechAPI.sAfterGTPostload} 在 GT 自身矿脉注册完成之后调用。
     */
    public static void registerAll() {
        register(veinSb());
        register(veinAr());
        register(veinAir());
        register(veinStone());
        register(veinSteel());
        register(veinIrOsSm());
    }

    public static void register(OreMixBuilder builder) {
        if (builder == null || builder.oreMixName == null) {
            throw new IllegalArgumentException("Custom vein must have a name");
        }

        // 避免同一个自定义矿脉重复注册
        if (!REGISTERED_NAMES.add(builder.oreMixName)) {
            return;
        }

        for (WorldgenGTOreLayer vein : WorldgenGTOreLayer.sList) {
            if (builder.oreMixName.equals(vein.getName())) {
                return;
            }
        }

        // 保存 Builder，供 NEI 生成显示数据
        BUILDERS.add(builder);

        // 注册真正的 GT 大矿脉生成器
        new WorldgenGTOreLayer(builder);
    }

    public static List<OreMixBuilder> getBuilders() {
        return Collections.unmodifiableList(BUILDERS);
    }

    private static OreMixBuilder veinSb() {
        return new OreMixBuilder().name("otht.vein.sb")
            .enableInDim(ANTIMONIA_NAME)
            .heightRange(5, 28)
            .weight(30)
            .density(2)
            .size(16)
            .primary(Materials.Antimony)
            .secondary(Materials.Borax)
            .inBetween(Materials.Sulfur)
            .sporadic(Materials.Naquadah);
    }

    private static OreMixBuilder veinAr() {
        return new OreMixBuilder().name("otht.vein.ar")
            .enableInDim(ANTIMONIA_NAME)
            .heightRange(5, 28)
            .weight(30)
            .density(20)
            .size(2)
            .primary(Materials.HydrochloricAcid)
            .secondary(Materials.Nitrogen)
            .inBetween(Materials.Chlorine)
            .sporadic(Materials.Hydrogen);
    }

    private static OreMixBuilder veinAir() {
        return new OreMixBuilder().name("otht.vein.air")
            .enableInDim(ANTIMONIA_NAME)
            .heightRange(2, 200)
            .weight(3)
            .density(20)
            .size(1)
            .primary(Materials.Air)
            .secondary(Materials.Air)
            .inBetween(Materials.Air)
            .sporadic(Materials.Air);
    }

    private static OreMixBuilder veinStone() {
        return new OreMixBuilder().name("otht.vein.st")
            .enableInDim(ANTIMONIA_NAME)
            .heightRange(2, 200)
            .weight(3)
            .density(20)
            .size(1)
            .primary(Materials.GraniteBlack)
            .secondary(Materials.GraniteRed)
            .inBetween(Materials.Basalt)
            .sporadic(Materials.Stone);
    }

    private static OreMixBuilder veinSteel() {
        return new OreMixBuilder().name("otht.vein.se")
            .enableInDim(ANTIMONIA_NAME)
            .heightRange(2, 200)
            .weight(20)
            .density(30)
            .size(15)
            .primary(Materials.Steel)
            .secondary(Materials.Bronze)
            .inBetween(Materials.Brass)
            .sporadic(Materials.Charcoal);
    }

    private static OreMixBuilder veinIrOsSm() {
        return new OreMixBuilder().name("otht.vein.ir")
            .enableInDim(ANTIMONIA_NAME)
            .heightRange(2, 50)
            .weight(8)
            .density(12)
            .size(10)
            .primary(Materials.Iridium)
            .secondary(Materials.Osmium)
            .inBetween(Materials.Samarium)
            .sporadic(Materials.Stone);
    }
}
