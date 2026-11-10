package com.newmaa.othtech.common.dimensions;

import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ANTIMONIA_NAME;
import static com.newmaa.othtech.common.dimensions.RegisterDimensions.ROSS123B_NAME;

import java.util.Collections;

import com.newmaa.othtech.common.dimensions.gtoregen.AntimoniaGTOreVeinReg;
import com.newmaa.othtech.common.dimensions.gtoregen.Ross123bGTOreVeinReg;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.StoneType;
import gtneioreplugin.util.DimensionHelper;

public final class OTHDimensionIntegration {

    private OTHDimensionIntegration() {}

    public static void registerNEIDimension() {
        // 锑星地下也是锑石, 岩石类型同样按 Moon 处理(见 GTStoneTypeOTHMixin)
        DimensionHelper.register(
            ANTIMONIA_NAME, // fullName
            ANTIMONIA_NAME, // internalName
            "Antimonia", // trimmedName
            "Aa", // unique abbreviation
            "otht.tier.0", // display tier
            Collections.singletonList(StoneType.Moon));
        DimensionHelper.register(
            ROSS123B_NAME, // fullName
            ROSS123B_NAME, // internalName
            "Ross123b", // trimmedName
            "R3", // unique abbreviation
            "otht.tier.1", // display tier
            Collections.singletonList(StoneType.Moon));
    }

    public static void scheduleOreVeinRegistration() {
        if (GregTechAPI.sAfterGTPostload == null) {
            // 极端情况下（postInit 之后才调用）GT 已清空回调列表，直接注册
            AntimoniaGTOreVeinReg.registerAll();
            Ross123bGTOreVeinReg.registerAll();
            return;
        }

        GregTechAPI.sAfterGTPostload.add(AntimoniaGTOreVeinReg::registerAll);
        GregTechAPI.sAfterGTPostload.add(Ross123bGTOreVeinReg::registerAll);
    }
}
