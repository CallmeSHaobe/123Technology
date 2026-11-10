package com.newmaa.othtech.common.recipemap;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.nei.RecipeDisplayInfo;
import tectech.recipe.BECAssemblyFrontend;

public class DeepSeikoBECFrontend extends BECAssemblyFrontend {

    public DeepSeikoBECFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    protected void drawSpecialInfo(RecipeDisplayInfo recipeInfo) {
        super.drawSpecialInfo(recipeInfo);
        int tier = recipeInfo.recipe.mSpecialValue;
        if (tier > 0) {
            // 两行显示（长句在 NEI 信息区末尾会被裁掉）
            recipeInfo.drawText(StatCollector.translateToLocal("ote.ds.nei.nanite.line1"));
            recipeInfo.drawText(StatCollector.translateToLocalFormatted("ote.ds.nei.nanite.line2", tier));
        }
    }
}
