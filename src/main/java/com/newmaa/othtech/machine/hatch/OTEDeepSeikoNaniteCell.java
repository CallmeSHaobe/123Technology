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

/**
 * 迪普西科的「纳米蜂收容仓」（原版纳米蜂群物品）：1 槽，放一颗纳米蜂（1~10 级）。
 * 决定凝聚态模式能跑什么等级的配方（对齐原版 BEC 的蜂群等级门槛），<b>不影响速度</b>。
 * 放 10 级（MagMatter 纳米蜂）= 什么都能跑。
 */
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

    /**
     * tier（1~10）→ 参照物品。注意 {@link NaniteTier} 有 12 个枚举项但只有 10 个真实等级
     * （Carbon 与 Glowstone 同为 1、Silver 与 Neutronium 同为 2…），所以必须按 {@code getTier()} 取，
     * 不能用 ordinal+1，否则 Glowstone 会被判成 2、MagMatter 会被判成 12。
     */
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
