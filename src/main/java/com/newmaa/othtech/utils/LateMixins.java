package com.newmaa.othtech.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class LateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.123technology.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return new ArrayList<>();
    }
}
