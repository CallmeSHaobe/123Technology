package com.newmaa.othtech.common.recipemap;

import static com.newmaa.othtech.common.recipemap.Recipemaps.DeepSeikoAL;

import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

/**
 * 给「迪普西科」({@code OTEDeepSeiko}) 的装配线配方池 {@link Recipemaps#DeepSeikoAL} 灌配方。
 * <p>
 * 模式 A 设计是「直查 GT 装配线配方表、零维护」——不需要数据棒。拷贝
 * GT 装配线可视化配方表（{@link RecipeMaps#assemblylineVisualRecipes}）：NEI 里看到的那份，
 * 本体是 fake 配方、数据棒放在特殊槽 —— 拷贝后<b>转正（去 fake）并清掉特殊槽的数据棒需求</b>。
 * <b>只收装配线</b>：部件装配线 CAL（电路装配线）配方不属于这里（v1.5.2 应用户要求移除）。
 * <p>
 * 输入/输出/时长/EU 全部保持原样。
 * <p>
 * 自己的配方（时空冷却核心）<b>不写在这里</b>：它走原版装配线配方池（带研究/闪存），
 * 这份拷贝会把它一起接管过来 —— 见 {@code RecipesDeepSeiko#addSpacetimeCoolingCore()}。
 * <p>
 * 调用时机：{@code GregTechAPI.sGTCompleteLoad}（FML load complete 阶段执行），晚于 GT 与 TT 的全部配方注册。
 */
public final class DeepSeikoALRecipeLoader {

    private static final Logger LOG = LogManager.getLogger("123Technology/DeepSeikoAL");

    private DeepSeikoALRecipeLoader() {}

    public static void load() {
        DeepSeikoAL.getBackend()
            .clearRecipes();
        int copied = copyVisualAssemblyLine();
        LOG.info("DeepSeikoAL recipe pool filled with {} recipes (AL visual only)", copied);
    }

    /**
     * AL 可视化配方 → 用 stdBuilder 重建为普通 GTRecipe。
     * <p>
     * <b>绝对不能用 {@code recipe.copy()} 直接转正</b>：AL 可视化配方带 {@code mOreDictAlt}
     * （每个输入槽的替代品数组），而 GT5U 的配方查找树（GTRecipeLookupBuilder）按替代品
     * <b>交叉积</b>建节点——16 个槽 × 几十个替代品能把节点数炸到上亿（实测 5.2 GB → OOM）。
     * 用 stdBuilder 重建出来的普通配方每个槽只有一个查找项，查找树是安全的。
     * 代价：失去替代品容错，玩家必须按 NEI 显示的精确物品投料。
     */
    private static int copyVisualAssemblyLine() {
        int copied = 0;
        for (GTRecipe recipe : RecipeMaps.assemblylineVisualRecipes.getAllRecipes()) {
            if (recipe == null || recipe.mHidden) continue;
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(filled(recipe.mInputs, GTValues.emptyItemStackArray))
                    .fluidInputs(filled(recipe.mFluidInputs, GTValues.emptyFluidStackArray))
                    .itemOutputs(filled(recipe.mOutputs, GTValues.emptyItemStackArray))
                    .duration(recipe.mDuration)
                    .eut(recipe.mEUt)
                    .addTo(DeepSeikoAL);
                copied++;
            } catch (Throwable t) {
                LOG.warn("Failed to copy an AL visual recipe into DeepSeikoAL", t);
            }
        }
        return copied;
    }

    /**
     * 可视化配方的输入/输出数组里可能混 null 槽（GTRecipeBuilder 遇 null 直接抛 IllegalArgumentException 整条丢弃），先滤掉。
     * <ul>
     * <li>空引用 -> 调用方传入的类型化空数组（{@code GTValues.emptyItemStackArray} 等）；</li>
     * <li>结果数组按原始组件类型创建：{@code (T[]) new Object[0]} 会在调用点被插 checkcast 而 CCE。</li>
     * </ul>
     */
    private static <T> T[] filled(T[] array, T[] empty) {
        if (array == null) return empty;
        return Arrays.stream(array)
            .filter(Objects::nonNull)
            .toArray(
                len -> (T[]) java.lang.reflect.Array.newInstance(
                    array.getClass()
                        .getComponentType(),
                    len));
    }
}
