package com.newmaa.othtech.machine.hatch;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import com.newmaa.othtech.common.materials.AbstractSanctumContent;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import vazkii.botania.common.block.tile.mana.TilePool;

/**
 * Converts Botania pool Mana into the configured liquid Mana at 10 Mana = 1 mB.
 * This is the only Mana source registered by 123Technology when TST is absent.
 */
public class OTEManaHatch extends MTEHatchInput {

    private static final int MAX_FLUID_PER_TICK = 1_000;

    public OTEManaHatch(int aID, String aName, String aNameRegional, int aTier) {
        super(
            aID,
            1,
            aName,
            aNameRegional,
            aTier,
            new String[] { "Converts Botania Mana to liquid Mana", "10 Botania Mana = 1 mB",
                "Maximum conversion: 1000 mB/tick", "Facing a Botania Mana Pool" });
    }

    public OTEManaHatch(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 1, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEManaHatch(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isClientSide()) return;

        TilePool pool = getManaPool();
        if (pool == null || pool.getCurrentMana() <= 0) return;

        int space = getCapacity() - getFluidAmount();
        int amount = Math.min(MAX_FLUID_PER_TICK, Math.min(pool.getCurrentMana() / 10, space));
        FluidStack fluid = AbstractSanctumContent.getManaStack(amount);
        if (fluid == null || amount <= 0) return;

        int filled = fill(fluid, true);
        if (filled > 0) pool.recieveMana(-filled * 10);
    }

    @Nullable
    private TilePool getManaPool() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null) return null;
        TileEntity tile = base.getTileEntityAtSideAndDistance(base.getFrontFacing(), 1);
        return tile instanceof TilePool pool ? pool : null;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack fluid) {
        Fluid mana = AbstractSanctumContent.getManaFluid();
        return fluid != null && mana != null && fluid.getFluid() == mana;
    }

    @Override
    public int getCapacity() {
        return 1_000_000;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        // One-way source hatch: no toggling is needed.
    }

}
