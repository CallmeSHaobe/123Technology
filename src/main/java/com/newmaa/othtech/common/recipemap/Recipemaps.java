package com.newmaa.othtech.common.recipemap;

import com.newmaa.othtech.common.recipemap.recipeMapFrontends.OTH_GeneralFrontend;

import gregtech.api.enums.ItemList;
import gregtech.api.gui.modularui.GT_UITextures;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBuilder;

public class Recipemaps {

    public static final RecipeMap<OTH_RecipeMapBackend> Mega_ISA_Forge = RecipeMapBuilder
        .of("othtech.megaisa.map", OTH_RecipeMapBackend::new)
        .maxIO(16, 16, 16, 4)
        .progressBar(GT_UITextures.PROGRESSBAR_ARROW_MULTIPLE)
        .frontend(OTH_GeneralFrontend::new)
        .neiHandlerInfo(
            builder -> builder.setDisplayStack(ItemList.Electric_Motor_MAX.get(1))
                .setMaxRecipesPerPage(1))
        .disableOptimize()
        .build();
}
