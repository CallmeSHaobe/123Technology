package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import gregtech.common.tileentities.machines.multi.MTEElectricBlastFurnace;

public class OTEIMBABlastFurnace extends MTEElectricBlastFurnace {

    private int mHeatingCapacity = 0;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private static final int CASING_INDEX = 11;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":",
        EnumChatFormatting.GOLD + translateToLocal("ote.cm.imba.0") };

    private static final IStructureDefinition<MTEElectricBlastFurnace> STRUCTURE_DEFINITION = StructureDefinition
        .<MTEElectricBlastFurnace>builder()
        .addShape(
            STRUCTURE_PIECE_MAIN,
            transpose(
                new String[][] { { "ttttt", "ttttt", "ttmtt", "ttttt", "ttttt" },
                    { "CCCCC", "C---C", "C---C", "C---C", "CCCCC" }, { "CCCCC", "C---C", "C---C", "C---C", "CCCCC" },
                    { "bb~bb", "bbbbb", "bbbbb", "bbbbb", "bbbbb" } }))
        .addElement(
            't',
            buildHatchAdder(MTEElectricBlastFurnace.class)
                .atLeast(OutputHatch.withAdder((instance, hatchTileEntity, textureIndex) -> {
                    if (instance instanceof OTEIMBABlastFurnace) {
                        OTEIMBABlastFurnace blastFurnace = (OTEIMBABlastFurnace) instance;
                        int hatchCount = blastFurnace.getPollutionOutputHatchCount();
                        if (hatchCount > 0 && hatchTileEntity != null) {
                            return true;
                        }
                    }
                    return false;
                }))
                .casingIndex(CASING_INDEX)
                .hint(1)
                .buildAndChain(GregTechAPI.sBlockCasings1, CASING_INDEX))
        .addElement(
            'm', // 消音舱口
            Muffler.newAny(CASING_INDEX, 2))
        .addElement(
            'C', // 加热线圈
            ofCoil(MTEElectricBlastFurnace::setCoilLevel, MTEElectricBlastFurnace::getCoilLevel))
        .addElement(
            'b', // 输入舱口、输出舱口
            buildHatchAdder(MTEElectricBlastFurnace.class)
                .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance)
                .casingIndex(CASING_INDEX)
                .hint(1)
                .buildAndChain(GregTechAPI.sBlockCasings1, CASING_INDEX))
        .build();

    // ===================== Constructors =====================

    public OTEIMBABlastFurnace(int aID, String aName) {
        super(aID, aName, EnumChatFormatting.LIGHT_PURPLE + "The IMBA EBF");
    }

    public OTEIMBABlastFurnace(String aName) {
        super(aName);
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTHProcessingLogic() {

            @Nonnull
            @Override
            protected ParallelHelper createParallelHelper(@Nonnull GTRecipe recipe) {
                return new ParallelHelper().setRecipe(recipe)
                    .setItemInputs(inputItems)
                    .setFluidInputs(inputFluids)
                    .setAvailableEUt(Integer.MAX_VALUE)
                    .setMachine(machine, protectItems, protectFluids)
                    .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
                    .setMaxParallel(Integer.MAX_VALUE)
                    .setEUtModifier(0)
                    .enableBatchMode(batchSize)
                    .setConsumption(true)
                    .setOutputCalculation(true);
            }

            @Override
            protected double calculateDuration(@Nonnull GTRecipe recipe, @Nonnull ParallelHelper helper,
                @Nonnull OverclockCalculator calculator) {
                return 10;
            }
        };
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        this.mHeatingCapacity = 0;
        setCoilLevel(HeatingCoilLevel.None);
        mMufflerHatches.clear();
        if (!checkPiece(STRUCTURE_PIECE_MAIN, 2, 3, 0, errors)) return;
        if (getCoilLevel() == HeatingCoilLevel.None) return;
        if (mMaintenanceHatches.size() != 1) return;
        this.mHeatingCapacity = 20001;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 2, 3, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 2, 3, 0, elementBudget, env, false, true);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<MTEElectricBlastFurnace> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.imba.0"))
            .addInfo(translateToLocal("ote.tm.imba.1"))
            .addInfo(translateToLocal("ote.tm.imba.2"))
            .addInfo(translateToLocal("ote.tm.imba.3"))
            .addInfo(translateToLocal("ote.tm.imba.4"))
            .addInfo(translateToLocal("ote.tm.imba.5"))
            .addInfo(translateToLocal("ote.tm.imba.6"))
            .addInfo(translateToLocal("ote.tm.imba.7"))
            .addInfo(translateToLocal("ote.tm.imba.8"))
            .addInfo(translateToLocal("ote.tm.imba.9"))
            .addInfo(translateToLocal("ote.tm.imba.10"))
            .addPollutionAmount(getPollutionPerSecond(null))
            .beginStructureBlock(5, 4, 5, true)
            .addController("Front bottom")
            .addCasingInfoRange("Heat Proof Machine Casing", 0, 15, false)
            .addOtherStructurePart("Heating Coils (including advanced coils)", "Two middle Layers")
            .addMaintenanceHatch("Any bottom layer casing", 3)
            .addMufflerHatch("Top middle", 2)
            .addInputBus("Any bottom layer casing", 3)
            .addInputHatch("Any bottom layer casing", 3)
            .addOutputBus("Any bottom layer casing", 3)
            .addOutputHatch("Fluid outputs, Any bottom layer casing")
            .addOutputHatch("Pollution gases (CO2/CO/SO2), Any top layer casing", 1)
            .addStructureInfo("Pollution gas output amount scales with Muffler Hatch tier")
            .addInfo("§7§lAuthor:§4§l§m勃艮第大公,疯狂的富婆§4§l---§c§lSafariXiu☆")
            .addSeparator()
            .addInfo("§4§lAdded by: 123Technology")
            .toolTipFinisher();

        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEIMBABlastFurnace(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][CASING_INDEX], TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { casingTexturePages[0][CASING_INDEX] };
    }

    // ===================== Misc =====================

    @Override
    public boolean drainEnergyInput(long aEUt) {
        return true;
    }

    public int getPollutionOutputHatchCount() {
        return this.mMufflerHatches.size();
    }

    public int getmHeatingCapacity() {
        return mHeatingCapacity;
    }
}
