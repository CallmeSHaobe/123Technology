package com.newmaa.othtech.Mixins;

import static bartworks.API.recipe.BartWorksRecipeMaps.circuitAssemblyLineRecipes;
import static com.newmaa.othtech.common.recipemap.Recipemaps.MegaCAL;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import bartworks.system.material.CircuitGeneration.CircuitImprintLoader;

@SuppressWarnings("UnusedMixin")
@Mixin(value = CircuitImprintLoader.class, remap = false)
public class BWRecipeLoadMixin {

    @Inject(method = "run", at = @At("TAIL"))
    private static void oth$methodHandle(CallbackInfo ci) {
        MegaCAL.getBackend()
            .clearRecipes();
        circuitAssemblyLineRecipes.getAllRecipes()
            .forEach(recipe -> MegaCAL.add(recipe.copy()));
    }

}
