package com.newmaa.othtech.common.recipemap.recipeMapFrontends;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.nei.RecipeDisplayInfo;

/**
 * Minimal NEI layout for Void Nexus upgrade material previews.
 *
 * <p>
 * GT's default description includes generated EU, duration, metadata and material-summary lines. These
 * are meaningless for a preview-only recipe, so only the recipe's own NEI description is drawn.
 */
public class VoidNexusUpgradeFrontend extends OTH_GeneralFrontend {

    public VoidNexusUpgradeFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    public void drawDescription(RecipeDisplayInfo recipeInfo) {
        drawSpecialInfo(recipeInfo);
    }
}
