package com.newmaa.othtech.common.blocks.nailong;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * 奈隆系机壳注册入口（preInit 调用，与 DeepSeikoBlocks / AntimoniaBlocks 同款时序）。
 * <p>
 * 一个 ID 两个 meta：0 = 奈隆机械方块，1 = 黄桃罐头机械方块。
 */
public class NailongCasings {

    public static BlockNailongCasing nailongCasing;

    public static void initialize() {
        nailongCasing = new BlockNailongCasing();
        GameRegistry.registerBlock(nailongCasing, ItemBlockNailongCasing.class, "nailongCasing");
    }

    private NailongCasings() {}
}
