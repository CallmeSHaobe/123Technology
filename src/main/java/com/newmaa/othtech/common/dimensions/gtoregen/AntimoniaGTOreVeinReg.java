package com.newmaa.othtech.common.dimensions.gtoregen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gregtech.common.OreMixBuilder;
import gregtech.common.WorldgenGTOreLayer;

public final class AntimoniaGTOreVeinReg {

    // GPT NIUBI
    private static final List<OreMixBuilder> BUILDERS = new ArrayList<>();

    private static final Set<String> REGISTERED_NAMES = new HashSet<>();

    private AntimoniaGTOreVeinReg() {}

    public static void register(OreMixBuilder builder) {
        if (builder == null || builder.oreMixName == null) {
            throw new IllegalArgumentException("Custom vein must have a name");
        }

        // 避免同一个自定义矿脉重复注册
        if (!REGISTERED_NAMES.add(builder.oreMixName)) {
            return;
        }

        // 保存 Builder，供 NEI 生成显示数据
        BUILDERS.add(builder);

        // 注册真正的 GT 大矿脉生成器
        new WorldgenGTOreLayer(builder);
    }

    public static List<OreMixBuilder> getBuilders() {
        return Collections.unmodifiableList(BUILDERS);
    }
}
