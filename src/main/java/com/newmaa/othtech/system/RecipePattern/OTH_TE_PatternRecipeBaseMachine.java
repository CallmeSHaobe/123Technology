package com.newmaa.othtech.system.RecipePattern;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public abstract class OTH_TE_PatternRecipeBaseMachine extends OTH_MultiMachineBase {

    private final ArrayList<OTH_TE_Hatch_MEIO> ultimateIOHatchesList = new ArrayList<>();

    protected OTH_TE_PatternRecipeBaseMachine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected OTH_TE_PatternRecipeBaseMachine(String aName) {
        super(aName);
    }

    public boolean addUltimateMEIOHatchToHatchList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof OTH_TE_Hatch_MEIO hatch) {
            hatch.updateTexture(aBaseCasingIndex);
            hatch.updateCraftingIcon(this.getMachineCraftingIcon());
            return ultimateIOHatchesList.add(hatch);
        }
        return false;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        super.onScrewdriverRightClick(side, aPlayer, aX, aY, aZ);
    }
}
