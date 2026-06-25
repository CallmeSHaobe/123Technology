package com.newmaa.othtech.recipe;

import gregtech.api.util.recipe.QuantumComputerRecipeData;

public class RecipesOTEFakeQuantumComputerData extends QuantumComputerRecipeData {

    public final float heatConstant, coolConstant, computation, maxHeat;
    public final boolean subZero;

    public RecipesOTEFakeQuantumComputerData(float heatConstant, float coolConstant, float computation,
        boolean subZero) {
        this(heatConstant, coolConstant, computation, 0, subZero);
    }

    public RecipesOTEFakeQuantumComputerData(float heatConstant, float coolConstant, float computation, float maxHeat,
        boolean subZero) {
        super(heatConstant, coolConstant, computation, maxHeat, subZero);
        this.heatConstant = heatConstant;
        this.coolConstant = coolConstant;
        this.computation = computation;
        this.maxHeat = maxHeat;
        this.subZero = subZero;
    }
}
