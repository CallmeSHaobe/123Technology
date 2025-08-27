package com.newmaa.othtech.machine.hatch;

import net.minecraft.util.EnumChatFormatting;

import gregtech.api.enums.Textures;
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
        return new OTEWTFHatch(mName, (byte) 14, new String[] { "" }, mTextures);
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
        return new String[] { EnumChatFormatting.GRAY + "将能量存储于全局网络中, 上限为2^(2^31)EU.",
            EnumChatFormatting.GRAY + "不连接导线. 此方块可以从网络中抽取EU.", EnumChatFormatting.GRAY + "小心能量溢出.",
            EnumChatFormatting.GOLD + "此处省略一百字.", EnumChatFormatting.GOLD + "只是个玩笑 有bug别用 嘻嘻。",
            EnumChatFormatting.DARK_PURPLE
                + "9,223,372,034,707,292,160 * 9,223,372,034,707,292,160 EU/t(85,070,591,690,620,534,613,323,169,079,597,465,600EU/t)",
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
