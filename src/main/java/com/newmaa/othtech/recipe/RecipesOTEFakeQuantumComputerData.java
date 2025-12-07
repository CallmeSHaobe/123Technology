package com.newmaa.othtech.recipe;

import gregtech.api.util.recipe.QuantumComputerRecipeData;

public class RecipesOTEFakeQuantumComputerData extends QuantumComputerRecipeData {

    public final float heatConstant, coolConstant, computation;
    public final boolean subZero;

    public RecipesOTEFakeQuantumComputerData(float heatConstant, float coolConstant, float computation,
        boolean subZero) {
        super(heatConstant, coolConstant, computation, 0, subZero);
        this.heatConstant = heatConstant;
        this.coolConstant = coolConstant;
        this.computation = computation;
        this.subZero = subZero;
    }
}
