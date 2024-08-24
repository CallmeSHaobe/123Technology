package com.newmaa.othtech.machine.machineStructureMulti;

import java.util.HashSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public abstract class GT_TE_multiStrMachine<T extends GT_TE_multiStrMachine<T>> extends OTH_MultiMachineBase<T>
    implements IConstructable, ISurvivalConstructable {

    public int ID = -1;
    public int Type = -1;
    public int fatherID = -1;
    protected long runningTick = 0;
    public HashSet<Integer> InConstruct = new HashSet<>();

    protected GT_TE_multiStrMachine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        setShape();
    }

    public GT_TE_multiStrMachine(String mName) {
        super(mName);
    }

    @Override
    public void onBlockDestroyed() {
        MultiStructureManager.removeMachine(this);
        super.onBlockDestroyed();
    }

    protected abstract boolean isEnablePerfectOverclock();

    protected abstract float getSpeedBonus();

    protected abstract int getMaxParallelRecipes();

    public void setShape() {
        StructureLoader.load(mName, mName);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        return super.survivalConstruct(stackSize, elementBudget, env);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        if (InConstruct.isEmpty()) {
            return;
        }
        int num = InConstruct.iterator()
            .next();
        StructureLoader.MultiStructureDefinition.OffSet offSet = StructureLoader.getOffSet(mName, mName + num);
        if (this.buildPiece(
            mName + num,
            stackSize,
            hintsOnly,
            offSet.horizontalOffSet,
            offSet.verticalOffSet,
            offSet.depthOffSet)) {
            if (!hintsOnly) {
                InConstruct.remove(num);
            }
        }
    }

    public void repair(int num) {
        StructureLoader.MultiStructureDefinition.OffSet offSet = StructureLoader.getOffSet(mName, mName + num);
        if (this
            .buildPiece(mName + num, null, false, offSet.horizontalOffSet, offSet.verticalOffSet, offSet.depthOffSet)) {
            InConstruct.remove(num);
        }
    }

    int checkStructureCount = 0;

    @Override
    public boolean checkStructure(boolean aForceReset, IGregTechTileEntity aBaseMetaTileEntity) {
        StructureLoader.MultiStructureDefinition.OffSet offSet = StructureLoader
            .getOffSet(mName, mName + checkStructureCount);
        if (checkPiece(
            mName + checkStructureCount,
            offSet.horizontalOffSet,
            offSet.verticalOffSet,
            offSet.depthOffSet)) {
            InConstruct.remove(checkStructureCount);
            checkStructureCount++;
            if (checkStructureCount == StructureLoader.readStructure(mName).pieces.size()) {
                checkStructureCount = 0;
            }
        } else {
            InConstruct.add(checkStructureCount);
        }
        return InConstruct.isEmpty();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("ID", ID);
        aNBT.setInteger("TYPE", Type);
        aNBT.setInteger("fatherID", fatherID);
        super.saveNBTData(aNBT);
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        runningTick++;
        if (runningTick % 20 == 0 && aBaseMetaTileEntity.isServerSide()) {
            checkStructure(false, getBaseMetaTileEntity());
            if (!InConstruct.isEmpty()) {
                construct(null, false);
            }
            mMachine = InConstruct.isEmpty();
        }
        if (aTick == 1 && aBaseMetaTileEntity.isServerSide()) {
            MultiStructureManager.registryMachine(this);
        }
        super.onPreTick(aBaseMetaTileEntity, aTick);
    }

    protected void turnOffMaintenance() {
        mWrench = true;
        mScrewdriver = true;
        mSoftHammer = true;
        mHardHammer = true;
        mSolderingTool = true;
        mCrowbar = true;
    }
}
