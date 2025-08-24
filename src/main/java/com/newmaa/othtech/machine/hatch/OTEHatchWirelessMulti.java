package com.newmaa.othtech.machine.hatch;

import static com.gtnewhorizon.gtnhlib.util.AnimatedTooltipHandler.*;
import static gregtech.api.enums.GTValues.V;
import static gregtech.common.misc.WirelessNetworkManager.*;
import static java.lang.Long.min;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.math.BigInteger;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.math.LongMath;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GTUtility;
import tectech.thing.metaTileEntity.Textures;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.util.TTUtility;

public class OTEHatchWirelessMulti extends MTEHatchEnergyMulti {
    // From TecTech

    private final long precisionMultiplier = LongMath.pow(10, 15);
    private final BigInteger eu_transferred_per_operation = BigInteger.valueOf(Amperes * V[mTier])
        .multiply(BigInteger.valueOf(ticks_between_energy_addition));

    private final double overflowDivisor = getOverflowDivisor(eu_transferred_per_operation);

    private final long actualTicksBetweenEnergyAddition = overflowDivisor > 1
        ? (long) (ticks_between_energy_addition / (overflowDivisor * 2))
        : ticks_between_energy_addition;

    private final long eu_transferred_per_operation_long = overflowDivisor > 1
        ? eu_transferred_per_operation.divide(BigInteger.valueOf((long) (overflowDivisor * precisionMultiplier * 2)))
            .multiply(BigInteger.valueOf(precisionMultiplier))
            .longValue()
        : eu_transferred_per_operation.longValue();

    private UUID owner_uuid;

    public OTEHatchWirelessMulti(int aID, String aName, String aNameRegional, int aTier, int aAmp) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            0,
            new String[] { EnumChatFormatting.GRAY + "将能量存储于全局网络中, 上限为2^(2^31)EU.",
                EnumChatFormatting.GRAY + "不连接导线. 此方块可以从网络中抽取EU.", EnumChatFormatting.GRAY + "小心能量溢出.",
                GOLD + "123Technology",
                translateToLocal("gt.blockmachines.hatch.energytunnel.desc.1") + ": "
                    + YELLOW
                    + GTUtility.formatNumbers(aAmp * V[aTier])
                    + GRAY
                    + " EU/t" },
            aAmp);
        TTUtility.setTier(aTier, this);
    }

    public OTEHatchWirelessMulti(String aName, int aTier, int aAmp, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aAmp, aDescription, aTextures);
    }

    private double getOverflowDivisor(BigInteger euTransferredPerOperation) {
        if (euTransferredPerOperation.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
            return euTransferredPerOperation.doubleValue() / Long.MAX_VALUE;
        }
        return 1d;
    }

    private ITexture[] TEXTURE_OVERLAY;

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        switch (Amperes) {
            case 4:
                TEXTURE_OVERLAY = Textures.OVERLAYS_ENERGY_IN_WIRELESS_MULTI_4A;
                break;
            case 16:
                TEXTURE_OVERLAY = Textures.OVERLAYS_ENERGY_IN_WIRELESS_MULTI_16A;
                break;
            case 64:
                TEXTURE_OVERLAY = Textures.OVERLAYS_ENERGY_IN_WIRELESS_MULTI_64A;
                break;
            default:
                TEXTURE_OVERLAY = Textures.OVERLAYS_ENERGY_IN_WIRELESS_LASER;
                break;
        }
        return new ITexture[] { aBaseTexture, TEXTURE_OVERLAY[mTier] };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        switch (Amperes) {
            case 4:
                TEXTURE_OVERLAY = Textures.OVERLAYS_ENERGY_IN_WIRELESS_MULTI_4A;
                break;
            case 16:
                TEXTURE_OVERLAY = Textures.OVERLAYS_ENERGY_IN_WIRELESS_MULTI_16A;
                break;
            case 64:
                TEXTURE_OVERLAY = Textures.OVERLAYS_ENERGY_IN_WIRELESS_MULTI_64A;
                break;
            default:
                TEXTURE_OVERLAY = Textures.OVERLAYS_ENERGY_IN_WIRELESS_LASER;
                break;
        }
        return new ITexture[] { aBaseTexture, TEXTURE_OVERLAY[mTier] };
    }

    @Override
    public boolean isFacingValid(ForgeDirection facing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isEnetInput() {
        return false;
    }

    @Override
    public boolean isInputFacing(ForgeDirection side) {
        return side == getBaseMetaTileEntity().getFrontFacing();
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return false;
    }

    @Override
    public long getMinimumStoredEU() {
        return Amperes * V[mTier];
    }

    @Override
    public long maxEUInput() {
        return V[mTier];
    }

    @Override
    public long maxEUStore() {
        return (long) (totalStorage(V[mTier]) / (2 * overflowDivisor) * Amperes);
    }

    @Override
    public long maxAmperesIn() {
        return Amperes;
    }

    @Override
    public long maxWorkingAmperesIn() {
        return Amperes;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEHatchWirelessMulti(mName, mTier, Amperes, mDescriptionArray, mTextures);
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return false;
    }

    @Override
    public ConnectionType getConnectionType() {
        return ConnectionType.WIRELESS;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        if (aBaseMetaTileEntity.isServerSide()) {
            // On first tick find the player name and attempt to add them to the map.

            // UUID and username of the owner.
            owner_uuid = aBaseMetaTileEntity.getOwnerUuid();

            strongCheckOrAddUser(owner_uuid);

            tryFetchingEnergy();
        }
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {

        super.onPreTick(aBaseMetaTileEntity, aTick);

        if (aBaseMetaTileEntity.isServerSide()) {

            // This is set up in a way to be as optimised as possible. If a user has a relatively plentiful energy
            // network
            // it should make no difference to them. Minimising the number of operations on BigInteger is essential.

            // Every actualTicksBetweenEnergyAddition add eu_transferred_per_operation to internal EU storage from
            // network.
            if (aTick % actualTicksBetweenEnergyAddition == 0L) {
                tryFetchingEnergy();
            }
        }
    }

    private void tryFetchingEnergy() {
        long currentEU = getBaseMetaTileEntity().getStoredEU();
        long maxEU = maxEUStore();
        long euToTransfer = min(maxEU - currentEU, eu_transferred_per_operation_long);
        if (euToTransfer <= 0) return; // nothing to transfer
        if (!addEUToGlobalEnergyMap(owner_uuid, -euToTransfer)) return;
        setEUVar(currentEU + euToTransfer);
    }
}
