package com.newmaa.othtech.common.blocks.nailong;

/**
 * BlockNailongCasing 的 meta 顺序表（贴图 nailong/casing_N 与之一一对应）。
 * <p>
 * 一个方块 ID 塞两个机壳：奈隆是纯黄底色+一点点淡黄，黄桃罐头在奈隆的基础上加玻璃质感。
 */
public final class NailongCasingMeta {

    /** 奈隆机械方块：纯黄色混合一点淡黄色 */
    public static final int NAILONG = 0;
    /** 黄桃罐头机械方块：在奈隆基础上加玻璃质感 */
    public static final int PEACH_CAN = 1;

    /** 贴图数量（每个 meta 一张独立贴图） */
    public static final int TEXTURE_COUNT = 2;

    public static final int[] VALUES = { NAILONG, PEACH_CAN };

    private NailongCasingMeta() {}
}
