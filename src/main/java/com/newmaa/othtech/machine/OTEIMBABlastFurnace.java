package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import gregtech.common.tileentities.machines.multi.MTEElectricBlastFurnace;

public class OTEIMBABlastFurnace extends MTEElectricBlastFurnace {

    private int mHeatingCapacity = 0;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int CASING_INDEX = 11;
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
                .dot(1)
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
                .dot(1)
                .buildAndChain(GregTechAPI.sBlockCasings1, CASING_INDEX))
        .build();

    public OTEIMBABlastFurnace(int aID, String aName) {
        super(aID, aName, "IMBA高炉");
    }

    public OTEIMBABlastFurnace(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEIMBABlastFurnace(this.mName);
    }

    @Override
    public IStructureDefinition<MTEElectricBlastFurnace> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

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

    @Override
    public boolean drainEnergyInput(long aEUt) {
        return true;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        this.mHeatingCapacity = 0;
        setCoilLevel(HeatingCoilLevel.None);
        mPollutionOutputHatches.clear();
        if (!checkPiece(STRUCTURE_PIECE_MAIN, 2, 3, 0)) return false;
        if (getCoilLevel() == HeatingCoilLevel.None) return false;
        if (mMaintenanceHatches.size() != 1) return false;
        this.mHeatingCapacity = 20001;

        return true;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 2, 3, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 2, 3, 0, elementBudget, env, false, true);
    }

    public int getPollutionOutputHatchCount() {
        return this.mPollutionOutputHatches.size();
    }

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

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§l超级无敌数值高炉")
            .addInfo("§6§l不耗电！")
            .addInfo("§6§lint并行！")
            .addInfo("§6§l任何工作都可以10tick完成！")
            .addInfo("§6§l内置一颗中子星！炉温无上限！")
            .addInfo("§1§o来自EOHBUF作者的眷顾")
            .addInfo("§6“传说中，神明在喝醉后设计了一台机器，醒来后却忘了删掉它。于是，它降临到了凡人世界。”")
            .addInfo("§4§l性能？直接拉满！成本？低到怀疑人生！")
            .addInfo("§c甚至你会怀疑，‘我靠，这东西真的允许造出来吗？’")
            .addInfo("§9只要有它，资源匮乏、效率低下、能量不足统统不存在。它不仅能让你在服务器里翻天覆地，§m还能让你在群聊里发出嘲讽的哈哈哈哈！！")
            .addInfo("§6它不是工具，它是玩家的梦想，是DEV的噩梦！")
            .addPollutionAmount(getPollutionPerSecond(null))
            .beginStructureBlock(3, 4, 3, true)
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
            .toolTipFinisher("§4§l123Technology");

        return tt;
    }

    public int getmHeatingCapacity() {
        return mHeatingCapacity;
    }
}
