package com.newmaa.othtech.machine.hatch;

import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.enums.NaniteTier;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;

public class OTEDeepSeikoNaniteCell extends MTEHatch {

    /** tier 序号（1~10）→ 参照物品，运行时初始化一次。 */
    private static final Map<Integer, ItemStack> TIER_REFS = new HashMap<>();

    public OTEDeepSeikoNaniteCell(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            aName,
            aNameRegional,
            aTier,
            1,
            new String[] { translateToLocal("ote.tt.nanite.0"), translateToLocal("ote.tt.nanite.1"),
                EnumChatFormatting.AQUA + translateToLocal("ote.tt.nanite.2") });
    }

    public OTEDeepSeikoNaniteCell(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 1, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEDeepSeikoNaniteCell(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture };
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[] { aBaseTexture };
    }

    private static ItemStack tierRef(int tier) {
        return TIER_REFS.computeIfAbsent(tier, t -> {
            for (NaniteTier nt : NaniteTier.values()) {
                if (nt.getTier() == t) {
                    return GTOreDictUnificator.get(OrePrefixes.nanite, nt.getMaterial(), 1);
                }
            }
            return null;
        });
    }

    /** 槽内纳米蜂的等级（1~10）；没放或不是纳米蜂返回 0。 */
    public int getPlacedTier() {
        ItemStack stack = mInventory[0];
        if (GTUtility.isStackInvalid(stack)) return 0;
        for (NaniteTier nt : NaniteTier.values()) {
            ItemStack ref = tierRef(nt.getTier());
            if (ref != null && GTUtility.areStacksEqual(ref, stack, true)) return nt.getTier();
        }
        return 0;
    }

    public static boolean isNanite(ItemStack stack) {
        if (GTUtility.isStackInvalid(stack)) return false;
        for (NaniteTier nt : NaniteTier.values()) {
            ItemStack ref = tierRef(nt.getTier());
            if (ref != null && GTUtility.areStacksEqual(ref, stack, true)) return true;
        }
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack itemStack) {
        return isNanite(itemStack);
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return isNanite(aStack) && side == aBaseMetaTileEntity.getFrontFacing();
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, ForgeDirection side,
        ItemStack aStack) {
        return side == aBaseMetaTileEntity.getFrontFacing();
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) {
            return true;
        }
        openGui(aPlayer);
        return true;
    }
}
