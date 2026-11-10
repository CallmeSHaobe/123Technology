package com.newmaa.othtech.common.recipemap;

import static com.newmaa.othtech.common.recipemap.Recipemaps.DeepSeikoAL;

import java.util.Arrays;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gregtech.api.enums.GTValues;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTRecipe;

public final class DeepSeikoALRecipeLoader {

    private static final Logger LOG = LogManager.getLogger("123Technology/DeepSeikoAL");

    private DeepSeikoALRecipeLoader() {}

    public static void load() {
        DeepSeikoAL.getBackend()
            .clearRecipes();
        int copied = copyVisualAssemblyLine();
        LOG.info("DeepSeikoAL recipe pool filled with {} recipes (AL visual only)", copied);
    }

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
