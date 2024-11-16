package com.newmaa.othtech.machine.hatch;

import net.minecraft.util.EnumChatFormatting;

import gregtech.api.enums.Textures;
import gregtech.api.enums.TierEU;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEWirelessEnergy;

public class GT_TE_legendaryWireless extends MTEWirelessEnergy {

    public GT_TE_legendaryWireless(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public GT_TE_legendaryWireless(String aName, byte aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_TE_legendaryWireless(mName, (byte) 13, new String[] { "" }, mTextures);
    }

    // endregion

    // region IO info

    private static final long LongMaxDecreaseInt = TierEU.UXV * TierEU.UXV * 24;

    @Override
    public long getMinimumStoredEU() {
        return 512;
    }

    @Override
    public long maxEUInput() {
        return TierEU.UXV;
    }

    @Override
    public long maxEUStore() {
        return LongMaxDecreaseInt;
    }

    @Override
    public long maxAmperesIn() {
        return TierEU.UXV;
    }

    // endregion

    // region General
    @Override
    public String[] getDescription() {
        return new String[] { EnumChatFormatting.GRAY + "Stores energy globally in a network, up to 2^(2^31) EU.",
            EnumChatFormatting.GRAY + "Does not connect to wires. This block accepts EU into the network.",
            EnumChatFormatting.WHITE + "536,870,912 * 536,870,912EU/t(288,230,376,151,711,744EU/t)",
            EnumChatFormatting.GOLD + "123Technology" };
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
