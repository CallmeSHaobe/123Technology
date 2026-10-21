package com.newmaa.othtech.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

/**
 * 晚期 mixin（{@code mixins.othtech.late.json}）的注册入口。
 * <p>
 * 注意：原来的 {@code BWRecipeLoadMixin} 已经移除。它靠注入
 * {@code CircuitImprintLoader#makeCraftingRecipes} 尾部来把 {@code bw.recipe.cal} 的配方拷进 MegaCAL，
 * 而 GT5U 5.09.54.x 里 CAL 配方池已经是空的（CAL 改为复用电路装配机配方），所以那个 mixin 只是静默失效。
 * 现在改为普通回调 {@code com.newmaa.othtech.common.recipemap.MegaCALRecipeLoader}。
 */
@LateMixin
public class LateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.othtech.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return new ArrayList<>();
    }
}
