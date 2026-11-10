package com.newmaa.othtech.common.recipemap;

import static com.newmaa.othtech.common.recipemap.Recipemaps.MegaCAL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bartworks.API.recipe.BartWorksRecipeMaps;
import gregtech.api.util.GTRecipe;

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
