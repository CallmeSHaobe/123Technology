package com.newmaa.othtech.common.blocks.nailong;

public final class NailongCasingMeta {

    /** 奈隆机械方块：纯黄色混合一点淡黄色 */
    public static final int NAILONG = 0;
    /** 黄桃罐头机械方块：在奈隆基础上加玻璃质感 */
    public static final int PEACH_CAN = 1;
    public static final int PSYCHIC_WRAPPED_CASING = 2;
    public static final int PSIONIC_REALITY_CASING = 3;
    public static final int SUBSPACE_SHIELD_CASING = 4;
    public static final int VOID_STABILIZATION_CASING = 5;
    public static final int ENDLESS_PSIONIC_GLASS = 6;
    public static final int SPACETIME_POTENTIAL_GLASS = 7;
    /** Internal controller-front variant; not exposed as a buildable casing. */
    public static final int VOID_NEXUS_CONTROLLER_FRONT = 8;

    /** 贴图数量（每个 meta 一张独立贴图） */
    public static final int TEXTURE_COUNT = 9;

    public static final int[] VALUES = { NAILONG, PEACH_CAN, PSYCHIC_WRAPPED_CASING, PSIONIC_REALITY_CASING,
        SUBSPACE_SHIELD_CASING, VOID_STABILIZATION_CASING, ENDLESS_PSIONIC_GLASS, SPACETIME_POTENTIAL_GLASS,
        VOID_NEXUS_CONTROLLER_FRONT };

    private NailongCasingMeta() {}
}
