package com.newmaa.othtech.common.machinelogic;

import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public abstract class TTMachinelogic123 extends TTMultiblockBase {

    public TTMachinelogic123(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public TTMachinelogic123(String aName) {
        super(aName);
    }

    public void repairMachine() {
        mHardHammer = true;
        mSoftMallet = true;
        mScrewdriver = true;
        mCrowbar = true;
        mSolderingTool = true;
        mWrench = true;
    }

    /**
     * No more machine error
     */
    @Override
    public boolean doRandomMaintenanceDamage() {
        return true;
    }

    /**
     * No more machine error
     */
    @Override
    public void checkMaintenance() {}

    /**
     * No more machine error
     */
    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    /**
     * No more machine error
     */
    @Override
    public final boolean shouldCheckMaintenance() {
        return false;
    }

}
