package com.newmaa.othtech.common.recipemap.formatter;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.util.StatCollector;

import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MISASpecialValueFormatter implements INEISpecialInfoFormatter {

    public static final MISASpecialValueFormatter INSTANCE = new MISASpecialValueFormatter();

    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        int levelIsa = recipeInfo.recipe.mSpecialValue;
        return Collections.singletonList(StatCollector.translateToLocalFormatted("otht.nei.isa." + levelIsa));

    }

}
