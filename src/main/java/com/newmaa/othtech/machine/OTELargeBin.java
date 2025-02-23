package com.newmaa.othtech.machine;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.newmaa.othtech.machine.machineclass.TT_MultiMachineBase_EM;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings2;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

public class OTELargeBin extends TT_MultiMachineBase_EM implements IConstructable, ISurvivalConstructable {

    private boolean grace = false;

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        casingCount = 0;
        if (structureCheck_EM("main", 0, 1, 0) && casingCount >= 0) {
            grace = true;
            return true;
        } else if (grace) {
            grace = false;
            return true;
        }
        return false;
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        if (!mMachine) {
            aBaseMetaTileEntity.disableWorking();
        }
    }

    private int casingCount = 0;

    // region structure
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 输入总线, 输入仓 : 替换脱氧钢机械方块"),
        // Power Casing
    };
    private static final IStructureDefinition<OTELargeBin> STRUCTURE_DEFINITION = IStructureDefinition
        .<OTELargeBin>builder()
        .addShape("main", new String[][] { { "1", "~", "1", } })
        .addElement(
            '1',
            buildHatchAdder(OTELargeBin.class)
                .atLeast(InputBus, InputHatch)
                .dot(1)
                .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                .buildAndChain(sBlockCasings2, 0))
        .build();

    @Override
    public IStructureDefinition<OTELargeBin> getStructure_EM() {
        return STRUCTURE_DEFINITION;
    }
    // endregion

    public OTELargeBin(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTELargeBin(String aName) {
        super(aName);
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTELargeBin(mName);
    }

    @Override
    @NotNull
    protected CheckRecipeResult checkProcessing_EM() {
        mEUt = 0;
        mMaxProgresstime = 10;
        ItemStack[] itemStack = getStoredInputs().toArray(new ItemStack[0]);
        FluidStack[] fluidStack = getStoredFluids().toArray(new FluidStack[0]);
        if (getStoredInputs() != null){
            for (ItemStack item : itemStack){
                item.stackSize -= item.stackSize;
            }
        }
        if (getStoredFluids() != null){
            for (FluidStack fluid : fluidStack){
                fluid.amount -= fluid.amount;
            }
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("垃圾桶"))
            .addInfo(translateToLocal("销毁一切"))
            .addTecTechHatchInfo()
            .beginStructureBlock(1, 3, 1, false)
            .addController(translateToLocal("结构正中心")) // Controller: Front center
            .addCasingInfoMin(translateToLocal("0x 脱氧钢机械方块(最低)"), 5, false) // 0x High Power Casing
            // (minimum)
            .addEnergyHatch(translateToLocal("任意输入总线, 输入仓"), 1) // Energy Hatch: Any
            // High Power Casing
            .toolTipFinisher();
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
                new TTRenderedExtendedFacingTexture(aActive ? TTMultiblockBase.ScreenON : TTMultiblockBase.ScreenOFF) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)) };
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_NOISE;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean doRandomMaintenanceDamage() {
        return true;
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if ((aTick & 31) == 31) {
            ePowerPass = aBaseMetaTileEntity.isAllowedToWork();
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM("main", 0, 1, 0, stackSize, hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, IItemSource source, EntityPlayerMP actor) {
        if (mMachine) return -1;
        return survivialBuildPiece("main", stackSize, 0, 1, 0, elementBudget, source, actor, false, true);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public boolean isPowerPassButtonEnabled() {
        return true;
    }

    @Override
    public boolean isSafeVoidButtonEnabled() {
        return false;
    }

    @Override
    public boolean isAllowedToWorkButtonEnabled() {
        return true;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }
}
