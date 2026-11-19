package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.common.recipemap.Recipemaps.MegaCAL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bartworks.API.recipe.BartWorksRecipeMaps;
import gregtech.api.util.GTRecipe;

public final class RecipesMCATransformer {

    private static final Logger LOG = LogManager.getLogger("123Technology/MegaCAL");

    private RecipesMCATransformer() {}

    public static void load() {
        MegaCAL.getBackend()
            .clearRecipes();
        int copied = copyCalRecipes();
        if (copied == 0) {
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
