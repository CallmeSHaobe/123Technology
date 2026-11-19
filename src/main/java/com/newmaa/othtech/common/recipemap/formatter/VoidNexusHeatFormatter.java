package com.newmaa.othtech.common.recipemap.formatter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.newmaa.othtech.common.compat.thaumcraft.VoidNexusFuelProfiles;

import gregtech.api.util.MethodsReturnNonnullByDefault;
import gregtech.nei.RecipeDisplayInfo;
import gregtech.nei.formatter.INEISpecialInfoFormatter;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VoidNexusHeatFormatter implements INEISpecialInfoFormatter {

    public static final VoidNexusHeatFormatter INSTANCE = new VoidNexusHeatFormatter();

    @Override
    public List<String> format(RecipeDisplayInfo recipeInfo) {
        List<String> lines = new ArrayList<>();
        Aspect aspect = findAspect(recipeInfo.recipe.mInputs);
        if (aspect != null) {
            lines.add(
                StatCollector
                    .translateToLocalFormatted("otht.nei.void_nexus.aspect", aspect.getName(), aspect.getTag()));
            lines.add(
                StatCollector
                    .translateToLocalFormatted("otht.nei.void_nexus.level", VoidNexusFuelProfiles.getLevel(aspect)));
        }
        lines.add(StatCollector.translateToLocalFormatted("otht.nei.void_nexus.heat", recipeInfo.recipe.mSpecialValue));
        lines.add(StatCollector.translateToLocal("otht.nei.void_nexus.note"));
        return lines;
    }

    private static Aspect findAspect(ItemStack[] inputs) {
        if (inputs == null) return null;
        for (ItemStack stack : inputs) {
            if (stack == null || !(stack.getItem() instanceof IEssentiaContainerItem)) continue;
            AspectList aspects = ((IEssentiaContainerItem) stack.getItem()).getAspects(stack);
            if (aspects != null && aspects.size() > 0) return aspects.getAspects()[0];
        }
        return null;
    }
}
