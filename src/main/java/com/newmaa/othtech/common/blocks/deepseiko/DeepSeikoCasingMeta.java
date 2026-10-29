package com.newmaa.othtech.common.blocks.deepseiko;

/**
 * BlockDeepSeikoCasing 的 meta 顺序表（贴图 casing_N 与之一一对应）。
 * v1.3：删掉鸿蒙三件套/插槽底座/机房方块（机房取消、鸿蒙环改用原版 EOH 场发生器）。
 * v8：三环槽改成三个「信道」，每个信道 1 级的方块就是这里的鳍（meta 9/10/11，外观沿用 casing_0）。
 */
public final class DeepSeikoCasingMeta {

    public static final int BLUBBER_CASING = 0; // 鲸脂外壳
    public static final int BLUBBER_FILLER = 1; // 鲸脂填充块（P0 占位，P2 换凝聚态适配舱）
    public static final int BALEEN_GRILLE = 2; // 鲸须格栅
    public static final int TAIL_FIN_FRAME = 3; // 尾鳍加强框
    public static final int ASSEMBLY_CASING = 4; // 装配腔方块
    /** meta 5 旧「装配腔外壳（UXV）」已停用：编号保留（不能挪 meta），但结构/配方/创造栏都不再出现 */
    public static final int ASSEMBLY_CASING_UXV_RETIRED = 5;
    public static final int CONDENSATE_ADAPTER = 6; // 凝聚态适配舱（P2）
    public static final int BLUBBER_LIGHT = 7; // 鲸脂外壳·浅蓝（体侧，参考鲸 L）
    public static final int BLUBBER_DARK = 8; // 鲸脂外壳·墨黑（眼/细节，参考鲸 K）

    // —— 三个鳍：三环信道各自的 1 级方块（外观与 casing_0 一致，只是名字不同） ——
    public static final int FIN_TIME = 9; // 左鳍（时间膨胀信道 1 级）
    public static final int FIN_SPACE = 10; // 右鳍（压缩时空信道 1 级）
    public static final int FIN_STAB = 11; // 顶鳍（稳定力场信道 1 级）

    /** 用独立贴图的 meta 数量（9~11 复用 casing_0 的贴图） */
    public static final int TEXTURE_COUNT = 9;

    public static final int[] VALUES = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

    private DeepSeikoCasingMeta() {}
}
