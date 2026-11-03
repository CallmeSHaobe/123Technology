package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHTTMultiMachineBaseEM;
import com.newmaa.othtech.utils.Utils;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HatchElement;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICasingTextureProvider;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.structure.error.ErrorType;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class OTELargeQuHub extends OTHTTMultiMachineBaseEM
    implements IConstructable, ISurvivalConstructable, ICasingTextureProvider {

    private static final String PIECE_MAIN = "main";

    private static final String PIECE_COIL = "coil";

    private static final String PIECE_SLICE = "slice";

    private static final int H_OFFSET = 1;

    private static final int V_OFFSET = 1;

    private static final int D_OFFSET = 0;

    private static final int MIN_CASINGS = 9;

    private static final int MAX_BUILD_SLICES = 64;

    private static final String[][] SHAPE_MAIN = new String[][] { { "111", "1~1", "111" } };

    private static final String[][] SHAPE_COIL = new String[][] { { "111", "1K1", "111" } };

    private static final String[][] SHAPE_SLICE = new String[][] { { "111", "111", "111" } };

    private static IStructureDefinition<OTELargeQuHub> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.quhub.0"),
        translateToLocal("ote.cm.quhub.1"), translateToLocal("ote.cm.quhub.2") };

    private int casingCount = 0;

    private int slices = 0;

    private HeatingCoilLevel coilLevel = HeatingCoilLevel.None;

    private boolean grace = false;

    // ===================== Constructors =====================

    public OTELargeQuHub(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eDismantleBoom = true;
    }

    public OTELargeQuHub(String aName) {
        super(aName);
        eDismantleBoom = true;
    }

    // ===================== Machine Parameters =====================

    @Override
    public boolean canBeMuffled() {
        return false;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return false;
    }

    @Override
    public boolean isSafeVoidButtonEnabled() {
        return false;
    }

    @Override
    public boolean isPowerPassButtonEnabled() {
        return true;
    }

    @Override
    public boolean showRecipeTextInGUI() {
        return false;
    }

    @Override
    public boolean showMachineStatusInGUI() {
        return false;
    }

    @Override
    public boolean hasRunningText() {
        return false;
    }

    public HeatingCoilLevel getCoilLevel() {
        return coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel == null ? HeatingCoilLevel.None : coilLevel;
    }

    public int getCoilTier() {
        return coilLevel == HeatingCoilLevel.None ? 0 : Utils.getCoilTier(coilLevel);
    }

    // ===================== Processing Logic =====================

    @Override
    @NotNull
    protected CheckRecipeResult checkProcessing_EM() {
        eAmpereFlow = 0;
        mEUt = 0;
        if (ePowerPass) {
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = 20;
            return SimpleCheckRecipeResult.ofSuccess("routing");
        }
        mEfficiencyIncrease = 0;
        mMaxProgresstime = 0;
        return SimpleCheckRecipeResult.ofFailure("no_routing");
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        return true;
    }

    @Override
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if ((aTick & 31) == 31) {
            ePowerPass = aBaseMetaTileEntity.isAllowedToWork();
        }
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        if (!mMachine) {
            aBaseMetaTileEntity.disableWorking();
        }
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        casingCount = 0;
        slices = 0;
        setCoilLevel(HeatingCoilLevel.None);

        if (checkPiece(PIECE_MAIN, H_OFFSET, V_OFFSET, D_OFFSET, errors)
            && checkPiece(PIECE_COIL, H_OFFSET, V_OFFSET, D_OFFSET - 1, errors)) {
            int stacked = 0;
            while (checkPiece(PIECE_SLICE, H_OFFSET, V_OFFSET, D_OFFSET - 2 - stacked, errors)) {
                stacked++;
            }
            errors.clear();
            slices = stacked;

            if (slices < 1) {
                errors.add(StructureErrorRegistry.TOO_SHORT_HEIGHT);
            } else if (slices > getMaxSlices()) {
                errors.add(StructureErrorRegistry.COIL_LEVEL_NOT_ENOUGH);
            } else {
                checkCasingMin(errors, casingCount, MIN_CASINGS);
                checkHasAnyEnergy(errors);
                if (mDynamoHatches.isEmpty() && eDynamoMulti.isEmpty()) {
                    errors.add(StructureErrors.hatchCount(ErrorType.TOO_FEW, Dynamo, 0, 1));
                }
            }
        }

        if (errors.isEmpty()) {
            grace = true;
        } else if (grace) {
            grace = false;
            errors.clear();
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(PIECE_MAIN, stackSize, hintsOnly, H_OFFSET, V_OFFSET, D_OFFSET);
        buildPiece(PIECE_COIL, stackSize, hintsOnly, H_OFFSET, V_OFFSET, D_OFFSET - 1);
        int height = Math.min(stackSize.stackSize, MAX_BUILD_SLICES);
        for (int i = 0; i < height; i++) {
            buildPiece(PIECE_SLICE, stackSize, hintsOnly, H_OFFSET, V_OFFSET, D_OFFSET - 2 - i);
        }
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int built = 0;
        int temp = survivalBuildPiece(
            PIECE_MAIN,
            stackSize,
            H_OFFSET,
            V_OFFSET,
            D_OFFSET,
            elementBudget - built,
            env,
            false,
            true);
        if (temp > -1) built += temp;
        if (elementBudget - built <= 0) return built;

        temp = survivalBuildPiece(
            PIECE_COIL,
            stackSize,
            H_OFFSET,
            V_OFFSET,
            D_OFFSET - 1,
            elementBudget - built,
            env,
            false,
            true);
        if (temp > -1) built += temp;
        if (elementBudget - built <= 0) return built;

        int height = Math.min(stackSize.stackSize, MAX_BUILD_SLICES);
        for (int i = 0; i < height; i++) {
            temp = survivalBuildPiece(
                PIECE_SLICE,
                stackSize,
                H_OFFSET,
                V_OFFSET,
                D_OFFSET - 2 - i,
                elementBudget - built,
                env,
                false,
                true);
            if (temp > -1) built += temp;
            if (elementBudget - built <= 0) return built;
        }
        return built == 0 ? -1 : built;
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTELargeQuHub> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTELargeQuHub>builder()
                .addShape(PIECE_MAIN, SHAPE_MAIN)
                .addShape(PIECE_COIL, SHAPE_COIL)
                .addShape(PIECE_SLICE, SHAPE_SLICE)
                .addElement(
                    '1',
                    buildHatchAdder(OTELargeQuHub.class)
                        .atLeast(Energy, HatchElement.EnergyMulti, Dynamo, HatchElement.DynamoMulti)
                        .adder(OTELargeQuHub::addHatchToMachineList)
                        .casingIndex(casingTextureIndex())
                        .hint(1)
                        .buildAndChain(onElementPass(t -> t.casingCount++, ofBlock(GregTechAPI.sBlockCasings2, 0))))
                .addElement('K', withChannel("coil", ofCoil(OTELargeQuHub::setCoilLevel, OTELargeQuHub::getCoilLevel)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.quhub.0"))
            .addInfo(translateToLocal("ote.tm.quhub.1"))
            .addInfo(translateToLocal("ote.tm.quhub.2"))
            .addInfo(translateToLocal("ote.tm.quhub.3"))
            .addInfo(translateToLocal("ote.tm.quhub.4"))
            .addInfo(translateToLocal("ote.tm.mat.5"))
            .addSupportMultiAmp()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - §4LargeQuHub")
            .addController(translateToLocal("ote.tn.quhub"))
            .beginStructureBlock(3, 3, 3, false)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addDynamoHatch("AnyDynamoHatch", 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTELargeQuHub(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { getCasingTexture(),
                new TTRenderedExtendedFacingTexture(aActive ? TTMultiblockBase.ScreenON : TTMultiblockBase.ScreenOFF) };
        }
        return new ITexture[] { getCasingTexture() };
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_NOISE;
    }

    @Override
    public ITexture getCasingTexture() {
        return Textures.BlockIcons.getCasingTextureForId(casingTextureIndex());
    }

    // ===================== Misc =====================

    public int getSlices() {
        return slices;
    }

    public int getMaxSlices() {
        int tier = getCoilTier();
        return tier * tier * tier;
    }

    private static int casingTextureIndex() {
        return GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0);
    }

    private boolean addHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity instanceof MTEHatchEnergyTunnel || aMetaTileEntity instanceof MTEHatchDynamoTunnel) {
            return false;
        }
        return addToMachineList(aTileEntity, aBaseCasingIndex);
    }
}
