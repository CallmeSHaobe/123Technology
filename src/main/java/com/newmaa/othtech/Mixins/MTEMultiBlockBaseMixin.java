package com.newmaa.othtech.Mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import gregtech.api.metatileentity.implementations.MTEMultiBlockBase;

@SuppressWarnings("UnusedMixin")
@Mixin(value = MTEMultiBlockBase.class, remap = false)
public abstract class MTEMultiBlockBaseMixin {

    @ModifyConstant(method = "polluteEnvironment", constant = @Constant(intValue = 10_000, ordinal = 0), require = 1)
    private int oth$modify(int constant) {
        return 50_000;
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
