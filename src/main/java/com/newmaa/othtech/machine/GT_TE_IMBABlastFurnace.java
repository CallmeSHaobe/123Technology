package com.newmaa.othtech.machine;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.MultiblockTooltipBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class GT_TE_IMBABlastFurnace extends OTH_MultiMachineBase<GT_TE_IMBABlastFurnace> {

    public GT_TE_IMBABlastFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    protected float getSpeedBonus() {
        return 0;
    }

    @Override
    protected int getMaxParallelRecipes() {
        return 0;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {

    }

    @Override
    public IStructureDefinition<GT_TE_IMBABlastFurnace> getStructureDefinition() {
        return null;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        return null;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return false;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return null;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection facing, int colorIndex, boolean active, boolean redstoneLevel) {
        return new ITexture[0];
    }
}
