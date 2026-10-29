package com.newmaa.othtech.machine.machineclass;

/**
 * 迪普西科的速度 / 耗电 / 三环数学。
 *
 * <p>
 * 这个类**故意不 import 任何 Minecraft / GT 类**，这样它可以脱离游戏单独编译运行
 * （{@code E:\NHMOD\tools\ds_math_check} 里有一套断言，用来证明公式没写反、边界没溢出）。
 * 机器类只做转发，实际数值全部在这里。
 * </p>
 */
public final class DeepSeikoMath {

    /** 算力倍率上限 */
    public static final float COMPUTE_MULT_CAP = 10000f;
    /** 机器自带速度倍率 */
    public static final float BASE_SPEED = 10f;
    /** 终极升级完成后的额外倍率 */
    public static final float SPEED_UNLOCK_MULT = 2f;
    /** 三环每级加成 */
    public static final float RING_TIME_BONUS_PER_LEVEL = 0.05f;
    public static final float RING_SPACE_DISCOUNT_PER_LEVEL = 0.05f;
    public static final float RING_STAB_WEAR_DISCOUNT_PER_LEVEL = 0.05f;
    public static final int RING_LEVEL_MAX = 9;

    private DeepSeikoMath() {}

    /**
     * 算力倍率 = min(10000, 200 × √(算力 / 配方最低))；算力不够返回 0（= 不带得动）。
     */
    public static float computeMultiplier(long compute, long minCompute) {
        if (minCompute <= 0) return 1.0f;
        if (compute < minCompute) return 0f;
        double ratio = (double) compute / minCompute;
        return (float) Math.min(COMPUTE_MULT_CAP, 200.0 * Math.sqrt(ratio));
    }

    /**
     * 三环实际运行等级：调试币**强制 9**；否则 1 ~ 已装方块等级（选择 0 = 跟随方块）。
     * <p>
     * 注意：调试币必须无条件返回 9 —— 早期版本先取 limit 再 min(selected, limit)，
     * 玩家之前手动选过 2 级的话插着币也只会是 2 级（本地断言抓到的 bug）。
     * </p>
     */
    public static int ringLevel(int selected, int installed, boolean tokenInserted) {
        if (tokenInserted) return RING_LEVEL_MAX;
        int limit = Math.max(1, Math.min(RING_LEVEL_MAX, installed));
        if (selected <= 0) return limit;
        return Math.max(1, Math.min(selected, limit));
    }

    /**
     * 时间方块：每级 +5% 速度，满级（9）再额外 +10% ⇒ 1.00 → **×1.50**。
     * <p>
     * 用户定的数：每级 0.05、满级总共给到 +0.5。
     * </p>
     */
    public static float ringSpeedFactor(int timeLevel) {
        int lv = clampLevel(timeLevel);
        return 1f + RING_TIME_BONUS_PER_LEVEL * (lv - 1) + (lv >= RING_LEVEL_MAX ? 0.10f : 0f);
    }

    /** 空间方块：每级 -5% 耗电，满级再额外 -10% ⇒ 1.00 → **×0.50** */
    public static float ringEuFactor(int spaceLevel) {
        int lv = clampLevel(spaceLevel);
        return Math.max(0.1f, 1f - RING_SPACE_DISCOUNT_PER_LEVEL * (lv - 1) - (lv >= RING_LEVEL_MAX ? 0.10f : 0f));
    }

    /** 稳定方块：速度 +2.5%/级（满级 ×1.20）、耗电 -2.5%/级（满级 ×0.80） */
    public static float ringStabSpeedFactor(int stabLevel) {
        return 1f + 0.025f * (clampLevel(stabLevel) - 1);
    }

    public static float ringStabEuFactor(int stabLevel) {
        return Math.max(0.1f, 1f - 0.025f * (clampLevel(stabLevel) - 1));
    }

    /**
     * 装配线模式的总加速：**没有算力 / 终极 / 基准**，只有三环（时间 + 稳定）。
     * 用户要求：这个模式「不再给额外加速」，速度完全由三种结构方块决定。
     */
    public static float alSpeed(int timeLevel, int stabLevel) {
        return Math.max(1f, ringSpeedFactor(timeLevel) * ringStabSpeedFactor(stabLevel));
    }

    /** 稳定力场：每级 -5% 烧卡概率（1 级 = ×1.0，最低 ×0.1） */
    public static float ringWearFactor(int stabLevel) {
        return Math.max(0.1f, 1f - RING_STAB_WEAR_DISCOUNT_PER_LEVEL * (clampLevel(stabLevel) - 1));
    }

    /**
     * 总加速倍率。
     *
     * @param computeMult 算力倍率（{@link #computeMultiplier}）
     * @param timeLevel   时间膨胀等级 1~9
     * @param ultimate    终极升级是否已完成
     * @return 总倍率（最小 1）
     */
    // ===================== BEC 点数模型 =====================

    /** 终极前：基础推进（点/tick，未计三环与算力卡） */
    public static final float BEC_POINTS_PER_TICK = 21340f; // 19400 × 1.1
    /** 终极后：基础推进（点/tick） */
    public static final float BEC_POINTS_PER_TICK_ULT = 500000f;
    /** 单件硬下限（秒）：任何加成也突破不了 */
    public static final float BEC_MIN_SECONDS = 1.0f;
    /** 单件 tick 下限：未终极 20 tick（1 秒）；终极解锁后 4 tick（0.2 秒） */
    public static final int BEC_MIN_TICKS = 20;
    public static final int BEC_MIN_TICKS_ULT = 4;
    /** 终极前并行指数（批量总时长按 N^(1-α) 增长） */
    public static final double BEC_ALPHA = 0.70;
    /** 终极后并行指数 */
    public static final double BEC_ALPHA_ULT = 0.85;

    /**
     * BEC 单次配方 → 实际运行 tick 数。
     * <p>
     * 点数模型：配方 duration 是「点数」不是秒。每 tick 推进
     * {@code P = 基础 × 时间环 × 稳定环 × 算力卡系数} 点；
     * 单件 = max(1 秒, 点数 ÷ P ÷ 20)；批量 = 单件 × 并行^(1-α)。
     * </p>
     */
    public static int becDurationTicks(long points, int parallel, boolean ultimate, int timeLevel, int stabLevel,
        float cardMult) {
        float base = ultimate ? BEC_POINTS_PER_TICK_ULT : BEC_POINTS_PER_TICK;
        float perTick = base * ringSpeedFactor(timeLevel) * ringStabSpeedFactor(stabLevel) * Math.max(1f, cardMult);
        float floorSec = Math.max(0.1f, BEC_MIN_SECONDS / Math.max(1f, cardMult));
        float singleSec = Math.max(floorSec, (float) points / perTick / 20f);
        double alpha = ultimate ? BEC_ALPHA_ULT : BEC_ALPHA;
        int p = effectiveParallelForTime(parallel);
        double batchSec = singleSec * Math.pow(p, 1.0 - alpha);
        int minTicks = ultimate ? BEC_MIN_TICKS_ULT : BEC_MIN_TICKS;
        return Math.max(minTicks, (int) Math.ceil(batchSec * 20.0));
    }

    /** GUI 用：当前 BEC 推进（点/tick） */
    public static float becPointsPerTick(boolean ultimate, int timeLevel, int stabLevel, float cardMult) {
        float base = ultimate ? BEC_POINTS_PER_TICK_ULT : BEC_POINTS_PER_TICK;
        return base * ringSpeedFactor(timeLevel) * ringStabSpeedFactor(stabLevel) * Math.max(1f, cardMult);
    }

    public static float totalSpeed(float computeMult, int timeLevel, boolean ultimate) {
        float speed = computeMult * ringSpeedFactor(timeLevel) * (ultimate ? SPEED_UNLOCK_MULT : 1f) * BASE_SPEED;
        return Math.max(1f, speed);
    }

    /**
     * 交给 GT 的「时长倍率」。
     * <p>
     * ⚠ {@code ProcessingLogic.setSpeedBonus(x)} 内部就是 {@code setDurationModifier(x)}：
     * <b>时长 = 基础时长 × x</b>，所以必须传总倍率的**倒数**（×2000 的机器传 0.0005）。
     * </p>
     */
    public static float durationModifier(float totalSpeed) {
        return 1f / Math.max(1f, totalSpeed);
    }

    /** 并行基数：三环平均 1 级时的并行上限 */
    public static final int PARALLEL_BASE = 512;
    /** 每升一级（三环平均）并行 ×8；满级 9 级夹到 Integer.MAX_VALUE（=「无限并行」） */
    public static final int PARALLEL_MULT_PER_LEVEL = 8;
    /** BEC 批量时长计算里使用的并行数上限（超过它只加数量、不再缩短时长） */
    public static final int BEC_PARALLEL_TIME_CAP = 65536;

    /** 升级档数：BEC 解锁 / 纳米蜂群满级 / 终极解锁 */
    public static final int UPGRADE_STEPS = 3;

    /**
     * 升级进度对**吞吐**的乘区：0 升级 75%，三级全满 100%（线性）。
     * 作用在 BEC 点/tick 与装配线速度上。
     */
    public static float upgradeThroughput(int stepsDone) {
        float t = Math.max(0, Math.min(UPGRADE_STEPS, stepsDone)) / (float) UPGRADE_STEPS;
        return 0.75f + 0.25f * t;
    }

    /**
     * 并行上限：三环平均等级 L → {@code 512 × 8^(L-1)}，L9 夹到 {@link Integer#MAX_VALUE}。
     *
     * @param avgLevel 三环（时间/空间/稳定）已装方块等级的平均值，1~9
     */
    public static int maxParallel(int avgLevel) {
        int lv = Math.max(1, Math.min(RING_LEVEL_MAX, avgLevel));
        long p = (long) PARALLEL_BASE * (long) Math.pow(PARALLEL_MULT_PER_LEVEL, lv - 1);
        return (int) Math.min(Integer.MAX_VALUE, p);
    }

    /** 并行参与的 BEC 时长计算：超过 {@link #BEC_PARALLEL_TIME_CAP} 的部分不再加速 */
    public static int effectiveParallelForTime(int parallel) {
        return Math.max(1, Math.min(BEC_PARALLEL_TIME_CAP, parallel));
    }

    private static int clampLevel(int level) {
        return Math.max(1, Math.min(RING_LEVEL_MAX, level));
    }
}
