package com.newmaa.othtech.machine.machineclass;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import gregtech.api.logic.ProcessingLogic;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.ParallelHelper;

public class OTHProcessingLogic extends ProcessingLogic {

    public OTHProcessingLogic() {
        super();
        setMaxTierSkips(999);
    }

    @NotNull
    @Override
    protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
        return new ParallelHelper().setRecipe(recipe)
            .setItemInputs(inputItems)
            .setFluidInputs(inputFluids)
            .setAvailableEUt(availableVoltage * availableAmperage)
            .setMachine(machine, protectItems, protectFluids)
            .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
            .setMaxParallel(maxParallel)
            .setEUtModifier(euModifier)
            .enableBatchMode(batchSize)
            .setConsumption(true)
            .setOutputCalculation(true);
    }
}
