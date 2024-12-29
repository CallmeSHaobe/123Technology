package com.newmaa.othtech.machine;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.*;
import gregtech.common.tileentities.machines.multi.MTEElectricBlastFurnace;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nonnull;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;

public class GT_TE_IMBABlastFurnace extends MTEElectricBlastFurnace {

    private int mHeatingCapacity = 0;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int CASING_INDEX = 11;
    private static final IStructureDefinition<MTEElectricBlastFurnace> STRUCTURE_DEFINITION = StructureDefinition
        .<MTEElectricBlastFurnace>builder()
        .addShape(
            STRUCTURE_PIECE_MAIN,
            transpose(
                new String[][]{
                    {"ttt", "tmt", "ttt"},
                    {"CCC", "C-C", "CCC"},
                    {"CCC", "C-C", "CCC"},
                    {"b~b", "bbb", "bbb"}
                }))
        .addElement(
            't',
            buildHatchAdder(MTEElectricBlastFurnace.class)
                .atLeast(
                    OutputHatch.withAdder((instance, hatchTileEntity, textureIndex) -> {
                        if (instance instanceof GT_TE_IMBABlastFurnace) {
                            GT_TE_IMBABlastFurnace blastFurnace = (GT_TE_IMBABlastFurnace) instance;
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
            'b', // 输入舱口、输出舱口、能量舱口
            buildHatchAdder(MTEElectricBlastFurnace.class)
                .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Maintenance, Energy)
                .casingIndex(CASING_INDEX)
                .dot(1)
                .buildAndChain(GregTechAPI.sBlockCasings1, CASING_INDEX))
        .build();

    public GT_TE_IMBABlastFurnace(int aID, String aName) {
        super(aID, aName, "IMBA高炉");
    }

    public GT_TE_IMBABlastFurnace(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_TE_IMBABlastFurnace(this.mName);
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
                return new ParallelHelper()
                    .setRecipe(recipe)
                    .setItemInputs(inputItems)
                    .setFluidInputs(inputFluids)
                    .setAvailableEUt(availableVoltage * availableAmperage)
                    .setMachine(machine, protectItems, protectFluids)
                    .setRecipeLocked(recipeLockableMachine, isRecipeLocked)
                    .setMaxParallel(maxParallel)
                    .setConsumption(true)
                    .setOutputCalculation(true);
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
        if (!checkPiece(STRUCTURE_PIECE_MAIN, 1, 3, 0)) return false;
        if (getCoilLevel() == HeatingCoilLevel.None) return false;
        if (mMaintenanceHatches.size() != 1) return false;
        int voltageTier = GTUtility.getTier(getMaxInputVoltage());
        this.mHeatingCapacity = (int) getCoilLevel().getHeat() + 100 * (voltageTier - 2);
        return true;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 3, 0);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 1, 3, 0, elementBudget, env, false, true);
    }

    public int getPollutionOutputHatchCount() {
        return this.mPollutionOutputHatches.size();
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
                                 int colorIndex, boolean aActive, boolean redstoneLevel) {
        if (side == aFacing) {
            if (aActive) return new ITexture[] {
                casingTexturePages[0][CASING_INDEX],
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build()
            };
            return new ITexture[] {
                casingTexturePages[0][CASING_INDEX],
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ELECTRIC_BLAST_FURNACE_GLOW)
                    .extFacing()
                    .glow()
                    .build()
            };
        }
        return new ITexture[] { casingTexturePages[0][CASING_INDEX] };
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("Advanced Blast Furnace")
            .addInfo("Supports advanced heating coils for higher temperatures.")
            .addInfo("Each 900K over the minimum heat reduces power consumption by 5% (multiplicatively).")
            .addInfo("Each 1800K over the minimum heat allows for a perfect overclock upgrade.")
            .addInfo("Advanced coils provide an additional 2000K heating capacity.")
            .addPollutionAmount(getPollutionPerSecond(null))
            .beginStructureBlock(3, 4, 3, true)
            .addController("Front bottom")
            .addCasingInfoRange("Heat Proof Machine Casing", 0, 15, false)
            .addOtherStructurePart("Heating Coils (including advanced coils)", "Two middle Layers")
            .addEnergyHatch("Any bottom layer casing", 3)
            .addMaintenanceHatch("Any bottom layer casing", 3)
            .addMufflerHatch("Top middle", 2)
            .addInputBus("Any bottom layer casing", 3)
            .addInputHatch("Any bottom layer casing", 3)
            .addOutputBus("Any bottom layer casing", 3)
            .addOutputHatch("Fluid outputs, Any bottom layer casing")
            .addOutputHatch("Pollution gases (CO2/CO/SO2), Any top layer casing", 1)
            .addStructureInfo("Pollution gas output amount scales with Muffler Hatch tier")
            .toolTipFinisher();
        return tt;
    }
}
