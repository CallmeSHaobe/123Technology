package com.newmaa.othtech.common.blocks.deepseiko;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * 迪普西科结构方块注册入口（preInit 调用，与 AntimoniaBlocks 同款时序）。
 */
public class DeepSeikoBlocks {

    public static BlockDeepSeikoCasing dsCasing;

    /** 米饭全息投影（旋转的米饭碗）的影子方块：不可见载体，由机器自动摆放 */
    public static BlockDeepSeikoHolo dsHolo;

    public static void initialize() {
        dsCasing = new BlockDeepSeikoCasing();
        GameRegistry.registerBlock(dsCasing, ItemBlockDeepSeikoCasing.class, "dsCasing");

        dsHolo = new BlockDeepSeikoHolo();
        GameRegistry.registerBlock(dsHolo, "dsHoloRice");
        GameRegistry.registerTileEntity(TEDeepSeikoHolo.class, "dsHoloRiceTE");
    }

    private DeepSeikoBlocks() {}
}
