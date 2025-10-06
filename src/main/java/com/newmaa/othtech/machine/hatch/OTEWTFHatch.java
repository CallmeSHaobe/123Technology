package com.newmaa.othtech.machine.hatch;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAYS_ENERGY_ON_WIRELESS;
import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.util.EnumChatFormatting;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEWirelessEnergy;

public class OTEWTFHatch extends MTEWirelessEnergy {

    public OTEWTFHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public OTEWTFHatch(String aName, byte aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEWTFHatch(mName, mTier, new String[] { "" }, mTextures);
    }

    // endregion

    // region IO info

    private static final long LongMaxDecreaseInt = Long.MAX_VALUE - Integer.MAX_VALUE;

    @Override
    public long getMinimumStoredEU() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long maxEUInput() {
        return LongMaxDecreaseInt;
    }

    @Override
    public long maxEUStore() {
        return LongMaxDecreaseInt;
    }

    @Override
    public long maxAmperesIn() {
        return LongMaxDecreaseInt;
    }

    // endregion

    // region General
    @Override
    public String[] getDescription() {
        return new String[] { EnumChatFormatting.GRAY + translateToLocal("ote.tm.wtf.0"),
            EnumChatFormatting.GRAY + translateToLocal("ote.tm.wtf.1"),
            EnumChatFormatting.GRAY + translateToLocal("ote.tm.wtf.2"),
            EnumChatFormatting.GOLD + translateToLocal("ote.tm.wtf.3"),
            EnumChatFormatting.GOLD + translateToLocal("ote.tm.wtf.4"),
            EnumChatFormatting.DARK_PURPLE
                + "9,223,372,034,707,292,160 * 9,223,372,034,707,292,160 EU/t(85,070,591,690,620,534,613,323,169,079,597,465,600EU/t)",
            EnumChatFormatting.GOLD + "123Technology" };
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, OVERLAYS_ENERGY_ON_WIRELESS[14] };
        // return super.getTexturesActive(aBaseTexture);
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, OVERLAYS_ENERGY_ON_WIRELESS[14] };
        // return super.getTexturesInactive(aBaseTexture);
    }
}
