package com.newmaa.othtech.system.RecipePattern;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;

import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public abstract class OTEPatternRecipeBaseMachine extends OTHMultiMachineBase {

    private final ArrayList<OTEHatchMEIO> ultimateIOHatchesList = new ArrayList<>();

    protected OTEPatternRecipeBaseMachine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    protected OTEPatternRecipeBaseMachine(String aName) {
        super(aName);
    }

    public boolean addUltimateMEIOHatchToHatchList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof OTEHatchMEIO hatch) {
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
