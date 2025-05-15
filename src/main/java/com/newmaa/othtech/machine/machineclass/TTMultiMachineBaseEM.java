package com.newmaa.othtech.machine.machineclass;

import static gregtech.api.enums.GTValues.V;

import com.brandon3055.brandonscore.common.handlers.ProcessHandler;
import com.brandon3055.draconicevolution.common.tileentities.multiblocktiles.reactor.ReactorExplosion;
import com.newmaa.othtech.Config;

import gregtech.api.interfaces.ISecondaryDescribable;
import gregtech.common.pollution.Pollution;
import tectech.TecTech;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public abstract class TTMultiMachineBaseEM extends TTMultiblockBase implements ISecondaryDescribable {

    public TTMultiMachineBaseEM(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public TTMultiMachineBaseEM(String aName) {
        super(aName);
    }

    public void repairMachine() {
        mHardHammer = true;
        mSoftHammer = true;
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

    private boolean explodedThisTick = false;

    public final void explodeMultiblockOTH() {
        if (explodedThisTick) {
            return;
        }
        if (Config.BOOM_SWITCH) {
            TecTech.proxy.broadcast(
                "Multi Explode BOOM! " + getBaseMetaTileEntity().getXCoord()
                    + ' '
                    + getBaseMetaTileEntity().getYCoord()
                    + ' '
                    + getBaseMetaTileEntity().getZCoord());
            StackTraceElement[] ste = Thread.currentThread()
                .getStackTrace();
            TecTech.proxy.broadcast("Multi Explode BOOM! " + ste[2].toString());
            explodedThisTick = true;
            extraExplosions_EM();
            Pollution.addPollution(getBaseMetaTileEntity(), 60000000);
            mInventory[1] = null;
            ProcessHandler.addProcess(
                new ReactorExplosion(
                    getBaseMetaTileEntity().getWorld(),
                    getBaseMetaTileEntity().getXCoord(),
                    (int) getBaseMetaTileEntity().getYCoord(),
                    getBaseMetaTileEntity().getZCoord(),
                    (float) Math.log(getMaxInputEu())));
            getBaseMetaTileEntity().doExplosion(V[15]);
        }
    }

    @Override
    public void onRemoval() {

        if (ePowerPass && getEUVar() > V[3] || eDismantleBoom && mMaxProgresstime > 0 && areChunksAroundLoaded_EM()) {
            explodeMultiblockOTH();
        }
    }

    @Override
    public void doExplosion(long aExplosionPower) {
        if (Config.BOOM_SWITCH) {
            explodeMultiblockOTH();
            super.doExplosion(aExplosionPower);
        }
    }
}
