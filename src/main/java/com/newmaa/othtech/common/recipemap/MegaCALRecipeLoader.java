package com.newmaa.othtech.common.recipemap;

import static com.newmaa.othtech.common.recipemap.Recipemaps.MegaCAL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bartworks.API.recipe.BartWorksRecipeMaps;
import gregtech.api.util.GTRecipe;

/**
 * 给「进阶高能电路装配线」({@code OTEMegaCircuitAssLine}) 的配方池 {@link Recipemaps#MegaCAL} 灌配方。
 * <p>
 * 这里的配方就是 CAL（压印装配线）配方本身：CAL 的配方是把电路组装机配方「倍增」后的版本
 * —— 6 个输入（电路元件已换成 {@code Wrap_*} 封装件）、输出 ×16、时长 ×12、并在<b>特殊槽</b>放压印电路。
 * 老版本 GT5U 由 {@code CircuitImprintLoader#reBuildRecipe} 自动生成这些配方，现在改由
 * NewHorizonsCoreMod 的 {@code CircuitAssemblyLineRecipes} 手写注册进 {@code bw.recipe.cal}。
 * <p>
 * 本机不需要压印板，所以唯一要做的就是：<b>拷贝 CAL 配方，然后把特殊槽里的压印电路去掉</b>。
 * <b>绝不能去动 {@code mInputs}</b> —— 输入里的「封装电路板 / 封装集成电路 / 封装贴片元件」和压印电路
 * 是同一个 meta item 的不同 meta，按物品身份去过滤会把正常输入一起删掉。
 * <p>
 * 调用时机：{@code GregTechAPI.sGTCompleteLoad}（FML load complete 阶段执行），必须晚于
 * NewHorizonsCoreMod 注册 CAL 配方（dreamcraft 也是挂在这个列表里，本模组 required-after:dreamcraft）。
 */
public final class MegaCALRecipeLoader {

    private static final Logger LOG = LogManager.getLogger("123Technology/MegaCAL");

    private MegaCALRecipeLoader() {}

    public static void load() {
        MegaCAL.getBackend()
            .clearRecipes();

        // CAL 配方（已倍增：输出 ×16 / 时长 ×12），去掉特殊槽的压印电路需求后灌进 MegaCAL
        int copied = copyCalRecipes();

        if (copied == 0) {
            // 只有 CAL 配方进这个池；电路组装机原配方由另一台机器负责，不做兜底拷贝
            LOG.warn("CAL recipe pool is empty, MegaCAL will have no recipes");
        }

        LOG.info("MegaCAL recipe pool filled with {} recipes", copied);
    }

    private static int copyCalRecipes() {
        int copied = 0;
        for (GTRecipe recipe : BartWorksRecipeMaps.circuitAssemblyLineRecipes.getAllRecipes()) {
            if (!usable(recipe)) continue;
            try {
                GTRecipe copy = recipe.copy();
                // 只去掉“压印电路”这个特殊槽需求，输入/输出/时长/EU 全部保持 CAL 原样
                copy.mSpecialItems = null;
                if (MegaCAL.add(copy) != null) {
                    copied++;
                    logSample(copy);
                }
            } catch (Throwable t) {
                LOG.warn("Failed to copy a CAL recipe into MegaCAL", t);
            }
        }
        return copied;
    }

    private static boolean usable(GTRecipe recipe) {
        return recipe != null && recipe.mEnabled && !recipe.mFakeRecipe && !recipe.mHidden;
    }

    /** 只打印第一条，方便在日志里确认「输入没被删、输出仍是 ×16、特殊槽已清空」 */
    private static boolean sampleLogged = false;

    private static void logSample(GTRecipe recipe) {
        if (sampleLogged) return;
        sampleLogged = true;
        LOG.info(
            "MegaCAL sample: inputs={} outputs={} outputSize={} special={} duration={}",
            recipe.mInputs == null ? 0 : recipe.mInputs.length,
            recipe.mOutputs == null ? 0 : recipe.mOutputs.length,
            recipe.mOutputs != null && recipe.mOutputs.length > 0 ? recipe.mOutputs[0].stackSize : -1,
            recipe.mSpecialItems,
            recipe.mDuration);
    }
}
