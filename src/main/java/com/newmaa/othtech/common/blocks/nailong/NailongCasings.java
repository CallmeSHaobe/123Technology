package com.newmaa.othtech.common.blocks.nailong;

import cpw.mods.fml.common.registry.GameRegistry;

public class NailongCasings {

    public static BlockNailongCasing nailongCasing;

    public static void initialize() {
        nailongCasing = new BlockNailongCasing();
        GameRegistry.registerBlock(nailongCasing, ItemBlockNailongCasing.class, "nailongCasing");
    }

    private NailongCasings() {}
}
