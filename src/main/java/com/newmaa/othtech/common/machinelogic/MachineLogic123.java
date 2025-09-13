package com.newmaa.othtech.common.machinelogic;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import gregtech.api.logic.ProcessingLogic;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.ParallelHelper;

public class MachineLogic123 extends ProcessingLogic {

    // 默认超频参数
    private int defaultTimeReduction = 2;
    private int defaultPowerIncrease = 4;
    private boolean defaultOverclockApplied = false;
    
    @NotNull
    @Override
    protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
        // @formatter:off
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
        // @formatter:on
    }

    @NotNull
    @Override
    public CheckRecipeResult process() {
        // 如果尚未应用默认超频，则应用默认设置
        if (!defaultOverclockApplied) {
            setOverclock(defaultTimeReduction, defaultPowerIncrease);
            defaultOverclockApplied = true;
        }
        return super.process();
    }

    /**
     * 设置默认超频类型
     * 注意：外部调用 setOverclock 会覆盖此设置
     */
    public MachineLogic123 setDefaultOverclockType(int timeReduction, int powerIncrease) {
        this.defaultTimeReduction = timeReduction;
        this.defaultPowerIncrease = powerIncrease;
        this.defaultOverclockApplied = false; // 重置标志，以便下次处理时应用新设置
        return this;
    }

    /**
     * 重写 setOverclock 方法以确保外部设置的优先级高于默认值
     */
    @Override
    public ProcessingLogic setOverclock(int timeReduction, int powerIncrease) {
        this.defaultOverclockApplied = true; // 标记已通过外部调用设置超频
        return super.setOverclock(timeReduction, powerIncrease);
    }
}