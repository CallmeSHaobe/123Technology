package com.newmaa.othtech.common.recipemap.recipeMapFrontends;

import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.math.Pos2d;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.DrawableWidget;

import gregtech.api.recipe.BasicUIPropertiesBuilder;
import gregtech.api.recipe.NEIRecipePropertiesBuilder;
import gregtech.api.recipe.maps.LargeNEIFrontend;

/**
 * 迪普西科配方池的 NEI 页面。
 *
 * <p>
 * 直接继承 GT 自带的 {@link LargeNEIFrontend}：它专门处理「物品/流体都很多」的配方——
 * 输入 3 列贴在左边、输出 3 列贴在右边（x=106）、流体接在物品下面，并按行数自动把背景拉高。
 * 之前自己手写 4 列/6 列布局，结果输入网格会顶到输出位置（材料和目标物叠在一起）、
 * 还把底部那三行文字压住；而覆写 {@code recipeBackgroundSize} 又会让这版 GT 崩客户端。
 * 用 GT 现成的实现最稳。
 * </p>
 */
public class OTH_GeneralFrontend extends LargeNEIFrontend {

    /** 页内左下角的小图标：调试硬币 */
    public static final UITexture OTHUIT = UITexture.fullImage("123technology", "items/DeepSeikoToken");

    public OTH_GeneralFrontend(BasicUIPropertiesBuilder uiPropertiesBuilder,
        NEIRecipePropertiesBuilder neiPropertiesBuilder) {
        super(uiPropertiesBuilder, neiPropertiesBuilder);
    }

    @Override
    public void addGregTechLogo(ModularWindow.Builder builder, Pos2d windowOffset) {
        builder.widget(
            new DrawableWidget().setDrawable(OTHUIT)
                .setSize(18, 18)
                .setPos(uiProperties.logoPos.add(windowOffset)));
    }
}
