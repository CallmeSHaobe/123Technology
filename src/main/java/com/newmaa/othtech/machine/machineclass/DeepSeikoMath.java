package com.newmaa.othtech.machine.machineclass;

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

    public static float computeMultiplier(long compute, long minCompute) {
        if (minCompute <= 0) return 1.0f;
        if (compute < minCompute) return 0f;
        double ratio = (double) compute / minCompute;
        return (float) Math.min(COMPUTE_MULT_CAP, 200.0 * Math.sqrt(ratio));
    }

    public static int ringLevel(int selected, int installed, boolean tokenInserted) {
        if (tokenInserted) return RING_LEVEL_MAX;
        int limit = Math.max(1, Math.min(RING_LEVEL_MAX, installed));
        if (selected <= 0) return limit;
        return Math.max(1, Math.min(selected, limit));
    }

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
