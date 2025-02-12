package com.newmaa.othtech.Mixins;

import static gregtech.api.util.GTUtility.validMTEList;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import gregtech.GTMod;
import gregtech.api.interfaces.modularui.IAddGregtechLogo;
import gregtech.api.interfaces.modularui.IAddUIWidgets;
import gregtech.api.interfaces.modularui.IBindPlayerInventoryUI;
import gregtech.api.interfaces.modularui.IControllerWithOptionalFeatures;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchMuffler;
import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MTEMultiBlockBaseMixin extends MetaTileEntity
    implements IControllerWithOptionalFeatures, IAddGregtechLogo, IAddUIWidgets, IBindPlayerInventoryUI {

    public MTEMultiBlockBaseMixin(int aID, String aBasicName, String aRegionalName, int aInvSlotCount) {
        super(aID, aBasicName, aRegionalName, aInvSlotCount);
    }

    @Shadow
    public int mPollution = 0;

    @Shadow
    public ArrayList<MTEHatchMuffler> mMufflerHatches = new ArrayList<>();

    /**
     * @author 123Tech
     * @reason 覆写污染处理逻辑
     */
    @Overwrite
    public boolean polluteEnvironment(int aPollutionLevel) {
        final int VENT_AMOUNT = 50_000_000;

        if (!GTMod.gregtechproxy.mPollution) {
            return true;
        }

        this.mPollution += aPollutionLevel;

        if (this.mPollution < VENT_AMOUNT) {
            return true;
        }

        if (this.mMufflerHatches.isEmpty()) {
            return false;
        }

        if (this.mMufflerHatches.size() == 1) {
            MTEHatchMuffler muffler = this.mMufflerHatches.get(0);
            if (muffler == null || !muffler.isValid()) {
                this.mMufflerHatches.remove(0);
                return false;
            } else {
                if (muffler.polluteEnvironment(this, VENT_AMOUNT)) {
                    this.mPollution -= VENT_AMOUNT;
                } else {
                    return false;
                }
            }
        }

        int mufflerCount = 0;
        int ventAmount = 0;
        for (MTEHatchMuffler muffler : validMTEList(this.mMufflerHatches)) {
            mufflerCount++;
            if (ventAmount + VENT_AMOUNT <= this.mPollution) {
                ventAmount += VENT_AMOUNT;
            }
        }

        ventAmount /= mufflerCount;

        for (MTEHatchMuffler muffler : validMTEList(this.mMufflerHatches)) {
            if (muffler.polluteEnvironment(this, ventAmount)) {
                this.mPollution -= ventAmount;
            } else {
                return false;
            }
        }

        return this.mPollution < VENT_AMOUNT;
    }
}
