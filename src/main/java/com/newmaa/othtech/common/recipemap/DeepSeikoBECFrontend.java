package com.newmaa.othtech.common.recipemap;

import net.minecraft.util.StatCollector;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.nei.RecipeDisplayInfo;
import tectech.recipe.BECAssemblyFrontend;

/**
 * 大肥鱼凝聚态的 NEI 页面：完全复用 TecTech 的 {@link BECAssemblyFrontend}（原版装配线排版），
 * 只额外画一行「需要机体升级纳米蜂群达到 X 级」——X 取自 loader 转存进 {@code mSpecialValue} 的等级。
 */
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
