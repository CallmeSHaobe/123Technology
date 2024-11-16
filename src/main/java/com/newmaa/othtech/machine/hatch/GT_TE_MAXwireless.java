package com.newmaa.othtech.machine.hatch;

import net.minecraft.util.EnumChatFormatting;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEWirelessEnergy;

public class GT_TE_MAXwireless extends MTEWirelessEnergy {

    public GT_TE_MAXwireless(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public GT_TE_MAXwireless(String aName, byte aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_TE_MAXwireless(mName, (byte) 14, new String[] { "" }, mTextures);
    }

    // endregion

    // region IO info

    private static final long LongMaxDecreaseInt = Long.MAX_VALUE - Integer.MAX_VALUE;
    private static final long max = 1048576;

    @Override
    public long getMinimumStoredEU() {
        return 512;
    }

    @Override
    public long maxEUInput() {
        return Integer.MAX_VALUE;
    }

    @Override
    public long maxEUStore() {
        return LongMaxDecreaseInt;
    }

    @Override
    public long maxAmperesIn() {
        return max;
    }

    // endregion

    // region General
    @Override
    public String[] getDescription() {
        return new String[] { EnumChatFormatting.GRAY + "Stores energy globally in a network, up to 2^(2^31) EU.",
            EnumChatFormatting.GRAY + "Does not connect to wires. This block accepts EU into the network.",
            EnumChatFormatting.WHITE + "1,048,576A", EnumChatFormatting.GOLD + "123Technology" };
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI_WIRELESS_ON[1] };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture, Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI_WIRELESS_ON[1] };
    }
}
