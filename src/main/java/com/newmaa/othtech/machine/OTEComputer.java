package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockUnlocalizedName;
import static gregtech.api.GregTechAPI.sBlockCasings10;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.GregTechAPI.sBlockGlass1;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.recipe.RecipeMaps.quantumComputerFakeRecipes;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.util.Vec3Impl;
import com.newmaa.othtech.machine.hatch.OTEHatchRack;
import com.newmaa.othtech.machine.machineclass.OTHTTMultiMachineBaseEM;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.GTUtility;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.common.WirelessComputationPacket;
import tectech.mechanics.dataTransport.QuantumDataPacket;
import tectech.thing.block.BlockQuantumGlass;
import tectech.thing.casing.BlockGTCasingsTT;
import tectech.thing.casing.TTCasingsContainer;
import tectech.thing.metaTileEntity.hatch.MTEHatchDataInput;
import tectech.thing.metaTileEntity.hatch.MTEHatchDataOutput;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessComputationOutput;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

/**
 * Created by danie_000 on 17.12.2016.
 */
public class OTEComputer extends OTHTTMultiMachineBaseEM implements IConstructable, ISurvivalConstructable {

    // region variables
    private final ArrayList<OTEHatchRack> eRacks = new ArrayList<>();

    private final ArrayList<MTEHatchWirelessComputationOutput> eWirelessComputationOutputs = new ArrayList<>();

    private static Textures.BlockIcons.CustomIcon ScreenOFF;
    private static Textures.BlockIcons.CustomIcon ScreenON;
    private static IStructureDefinition<OTEComputer> STRUCTURE_DEFINITION = null;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private final int horizontalOffSet = 3;
    private final int verticalOffSet = 8;
    private final int depthOffSet = 2;
    private final ArrayList<MTEHatchInput> eCoolantInputs = new ArrayList<>();
    private int coolantStored = 0;
    private static final int MAX_COOLANT_STORAGE = Integer.MAX_VALUE; // 最大冷却液存储量
    private int coolantConsumptionRate = 0;
    private boolean hasInsufficientCoolant = false;

    // endregion

    // region structure
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("tt.keyphrase.Hint_Details") + ":",
        translateToLocal("ote.computer.hint.0"), // 1 - Classic/Data Hatches or
        // Computer casing
        translateToLocal("ote.computer.hint.1") // 2 - Rack Hatches or Advanced
    };

    // computer casing

    @Override
    public IStructureDefinition<OTEComputer> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEComputer>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', ofBlock(sBlockCasings10, 9))
                .addElement('B', ofBlock(sBlockCasings8, 14))
                .addElement(
                    'C',
                    OTEComputer.RackHatchElement.INSTANCE
                        .newAnyOrCasing(BlockGTCasingsTT.textureOffset, 2, TTCasingsContainer.sBlockCasingsTT, 0))
                .addElement(
                    'D',
                    buildHatchAdder(OTEComputer.class)
                        .atLeast(
                            Energy.or(HatchElement.EnergyMulti),
                            Maintenance,
                            HatchElement.Uncertainty,
                            HatchElement.InputData,
                            HatchElement.OutputData,
                            CoolantHatchElement.INSTANCE,
                            WirelessComputationHatchElement.INSTANCE)
                        .casingIndex(BlockGTCasingsTT.textureOffset + 1)
                        .dot(1)
                        .buildAndChain(ofBlock(TTCasingsContainer.sBlockCasingsTT, 1)))
                .addElement('E', ofBlock(sBlockCasingsTT, 2))
                // HatchElementBuilder.<OTEComputer>builder()
                // .atLeast(
                // Energy.or(ExoticEnergy),
                // InputBus,
                // InputHatch,
                // InputData,
                // WirelessComputationHatchElement.INSTANCE,
                // OutputData)
                // .adder(OTEComputer::addToMachineList)
                // .dot(1)
                // .casingIndex(BlockGTCasingsTT.textureOffset)
                // .buildAndChain(sBlockCasingsTT, 0))
                .addElement('F', ofBlock(sBlockCasingsTT, 3))
                .addElement('G', ofBlock(sBlockGlass1, 1))
                .addElement('I', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement('H', ofBlockUnlocalizedName("chisel", "futura", 2))
                .addElement(
                    'J',
                    ofBlockUnlocalizedName("appliedenergistics2", "tile.BlockSingularityCraftingStorage", 0))
                .addElement('L', ofBlockUnlocalizedName("appliedenergistics2", "tile.BlockAdvancedCraftingUnit", 3))

                // A -> ofBlock...(gt.blockcasings10, 9, ...);
                // B -> ofBlock...(gt.blockcasings8, 14, ...);
                // C -> ofBlock...(gt.blockcasingsTT, 0, ...);
                // D -> ofBlock...(gt.blockcasingsTT, 1, ...);
                // E -> ofBlock...(gt.blockcasingsTT, 2, ...);
                // F -> ofBlock...(gt.blockcasingsTT, 3, ...);
                // G -> ofBlock...(gt.blockglass1, 1, ...);
                // H -> ofBlock...(tile.chisel.futura, 2, ...);
                // I -> ofBlock...(tile.quantumGlass, 0, ...);
                //
                // Tiles:
                //
                // Special Tiles:
                // J -> ofSpecialTileAdder(appeng.tile.crafting.TileCraftingStorageTile, ...); // You will probably want
                // to change it to something else
                // K -> ofSpecialTileAdder(gregtech.api.metatileentity.BaseMetaTileEntity, ...); // You will probably
                // want to change it to something else
                // L -> ofSpecialTileAdder(appeng.tile.crafting.TileCraftingTile, ...); // You will probably want to
                // change it to something else

                .build();
            // endregion

        }
        return STRUCTURE_DEFINITION;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private final String[][] shapeMain = new String[][] {
        { "           ", "           ", "           ", "           ", "           ", "           ", "  EDE      ",
            "           ", "           ", "           ", "  FFF      " },
        { "           ", "           ", "           ", "           ", "           ", "   F       ", "  EDE      ",
            "           ", "           ", "           ", "  FFF      " },
        { "           ", "           ", "           ", "           ", "           ", "   F       ", "  EDE      ",
            "   D       ", "   ~       ", "   D       ", "  FFF      " },
        { "           ", "           ", "           ", "           ", "           ", "   F       ", "  EDE      ",
            "           ", "           ", "           ", "  FFF      " },
        { "           ", " FFFFFFFFF ", " FDDDCCCCF ", " FDDDCCCCF ", " FDDDCCCCF ", " FDDDCCCCF ", " FDDDCCCCF ",
            " FDDDCCCCF ", " FDDDCCCCF ", " FFFFFFFFF ", "  FFF      " },
        { "FFFFFFFFFFF", "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID",
            "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID", "FFFFFFFFFFF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IHBBBBBBBHI", "IHBBBBBBBHI", "IHBBBBBBBHI", "IHBBBBBBBHI", "IHBBBBBBBHI",
            "IHBBBBBBBHI", "IHBBBBBBBHI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IBGGGGGGGBI", "IBGGGGGGGBI", "IBGGGGGGGBI", "IBGGGGGGGBI", "IBGGGGGGGBI",
            "IBGGGGGGGBI", "IBGGGGGGGBI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IBGGGGGGGBI", "IBGLAAALGBI", "IBGALALAGBI", "IBGAALAAGBI", "IBGALALAGBI",
            "IBGLALALGBI", "IBGGGGGGGBI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IBGGGGGGGBI", "IBGALALAGBI", "IBGLAAALGBI", "IBGAALAAGBI", "IBGLAAALGBI",
            "IBGALALAGBI", "IBGGGGGGGBI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IBGGGGGGGBI", "IBGAALAAGBI", "IBGAALAAGBI", "IBGLLJLLGBI", "IBGAALAAGBI",
            "IBGLALALGBI", "IBGGGGGGGBI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IBGGGGGGGBI", "IBGALALAGBI", "IBGLAAALGBI", "IBGAALAAGBI", "IBGLAAALGBI",
            "IBGALALAGBI", "IBGGGGGGGBI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IBGGGGGGGBI", "IBGLAAALGBI", "IBGALALAGBI", "IBGAALAAGBI", "IBGALALAGBI",
            "IBGLALALGBI", "IBGGGGGGGBI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IBGGGGGGGBI", "IBGGGGGGGBI", "IBGGGGGGGBI", "IBGGGGGGGBI", "IBGGGGGGGBI",
            "IBGGGGGGGBI", "IBGGGGGGGBI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FEEEEEEEEEF", "IHHHHHHHHHI", "IHBBBBBBBHI", "IHBBBBBBBHI", "IHBBBBBBBHI", "IHBBBBBBBHI", "IHBBBBBBBHI",
            "IHBBBBBBBHI", "IHBBBBBBBHI", "IHHHHHHHHHI", "FBBBBBBBBBF" },
        { "FFFFFFFFFFF", "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID",
            "DIIIIIIIIID", "DIIIIIIIIID", "DIIIIIIIIID", "FFFFFFFFFFF" } };

    // region parameters
    protected Parameters.Group.ParameterIn overclock, overvolt;
    protected Parameters.Group.ParameterOut availableData;

    private boolean wirelessModeEnabled = false;

    private static final INameFunction<OTEComputer> OC_NAME = (base, p) -> translateToLocal("ote.computer.cfgi.0"); // Overclock
    // ratio
    private static final INameFunction<OTEComputer> OV_NAME = (base, p) -> translateToLocal("ote.computer.cfgi.1"); // Overvoltage
    // ratio
    private static final INameFunction<OTEComputer> COMPUTE_NAME = (base, p) -> translateToLocal("ote.computer.cfgo.1"); // Produced
    // computation
    private static final IStatusFunction<OTEComputer> OC_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 1, 8000, 9999);
    private static final IStatusFunction<OTEComputer> OV_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 1, 8000, 9999);
    private static final IStatusFunction<OTEComputer> COMPUTE_STATUS = (base, p) -> {
        if (base.eAvailableData < 0) {
            return LedStatus.STATUS_TOO_LOW;
        }
        if (base.eAvailableData == 0) {
            return LedStatus.STATUS_NEUTRAL;
        }
        return LedStatus.STATUS_OK;
    };
    // endregion

    public OTEComputer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eCertainMode = 5;
        eCertainStatus = -128; // no-brain value
        eAvailableData = 0; // 确保初始化时为0
    }

    public OTEComputer(String aName) {
        super(aName);
        eCertainMode = 5;
        eCertainStatus = -128; // no-brain value
        eAvailableData = 0; // 确保初始化时为0
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return quantumComputerFakeRecipes;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEComputer(mName);
    }

    @Override
    protected void parametersInstantiation_EM() {
        Parameters.Group hatch_0 = parametrization.getGroup(0);
        overclock = hatch_0.makeInParameter(0, 1, OC_NAME, OC_STATUS);
        overvolt = hatch_0.makeInParameter(1, 1, OV_NAME, OV_STATUS);
        availableData = hatch_0.makeOutParameter(0, 0, COMPUTE_NAME, COMPUTE_STATUS);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        repairMachine();
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet)) {
            return false;
        }
        if (mMachine) {
            eAvailableData = 0;
            if (availableData != null) {
                availableData.set(0);
            }
        }
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setLong("eAvailableData", eAvailableData);
        aNBT.setBoolean("wirelessModeEnabled", wirelessModeEnabled);
        aNBT.setInteger("coolantStored", coolantStored); // 保存冷却液数据
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        aNBT.setLong("eAvailableData", eAvailableData);
        if (aNBT.hasKey("wirelessModeEnabled")) {
            wirelessModeEnabled = aNBT.getBoolean("wirelessModeEnabled");
            if (wirelessModeEnabled) {
                if (getBaseMetaTileEntity() != null) {
                    WirelessComputationPacket.enableWirelessNetWork(getBaseMetaTileEntity());
                }
            }
        } else {
            wirelessModeEnabled = false;
        }
        coolantStored = aNBT.getInteger("coolantStored");// 加载冷却液数据
        if (aNBT.hasKey("eAvailableData")) {
            eAvailableData = aNBT.getInteger("eAvailableData");
        } else {
            eAvailableData = 0;
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && mMachine) {
            // 冷却液输入处理
            for (MTEHatchInput coolantHatch : validMTEList(eCoolantInputs)) {
                if (coolantHatch.getFluid() != null) {
                    FluidStack coolantFluid = coolantHatch.getFluid();
                    if (isValidCoolant(coolantFluid)) {
                        int amountToDrain = Math.min(coolantFluid.amount, MAX_COOLANT_STORAGE - coolantStored);
                        if (amountToDrain > 0) {
                            FluidStack drained = coolantHatch.drain(amountToDrain, true);
                            if (drained != null) {
                                coolantStored += drained.amount;
                                break;
                            }
                        }
                    }
                }
            }

            // 新增：如果机器不活动，确保所有数据被清空
            if (!aBaseMetaTileEntity.isActive()) {
                if (eAvailableData != 0) {
                    eAvailableData = 0;
                    if (availableData != null) {
                        availableData.set(0);
                    }
                }

                // 重置机架状态
                for (OTEHatchRack rack : validMTEList(eRacks)) {
                    rack.getBaseMetaTileEntity()
                        .setActive(false);
                }
            }

            if (hasInsufficientCoolant && aBaseMetaTileEntity.isActive()) {
                hasInsufficientCoolant = false;
            }
        }
    }

    private boolean isValidCoolant(FluidStack fluid) {
        if (fluid.getFluid() == null) return false;
        return fluid.getFluid() == Materials.SuperCoolant.mFluid;
    }

    @Override
    @NotNull
    protected CheckRecipeResult checkProcessing_EM() {
        parametrization.setToDefaults(false, true);
        eAvailableData = 0; // 确保每次检查时重置输出数据
        int rackComputation;
        double overClockRatio = overclock.get();
        double overVoltageRatio = overvolt.get();
        if (Double.isNaN(overClockRatio) || Double.isNaN(overVoltageRatio)) {
            return SimpleCheckRecipeResult.ofFailure("no_computing");
        }

        if (overclock.getStatus(true).isOk && overvolt.getStatus(true).isOk) {
            float eut = Math.max(V[6], V[7] * (float) overClockRatio * (float) overVoltageRatio);
            if (eut < Integer.MAX_VALUE - 7) {
                mEUt = -(int) eut;
            } else {
                mEUt = -(int) V[7];
                return CheckRecipeResultRegistry.POWER_OVERFLOW;
            }
            short thingsActive = 0;

            for (OTEHatchRack rack : validMTEList(eRacks)) {
                rackComputation = rack.tickComponents((float) overClockRatio, (float) overVoltageRatio);
                if (rackComputation > 0) {
                    eAvailableData += rackComputation;
                    thingsActive = 4;
                }
                rack.getBaseMetaTileEntity()
                    .setActive(true);
            }
            availableData.set(eAvailableData);

            for (MTEHatchDataInput di : eInputData) {
                if (di.q != null) // ok for power losses
                {
                    thingsActive++;
                }
            }

            if (thingsActive > 0 && eCertainStatus == 0) {
                // 计算冷却液消耗量：输出算力 * 超频比^过压比 / 10000
                double coolantRequired = (eAvailableData / 100.0) * (overClockRatio + overVoltageRatio);
                int coolantRequiredInt = (int) Math.ceil(coolantRequired);

                // 检查冷却液是否足够
                if (coolantStored >= coolantRequiredInt) {
                    // 消耗冷却液
                    coolantStored -= coolantRequiredInt;
                    coolantConsumptionRate = coolantRequiredInt;
                    hasInsufficientCoolant = false;

                    thingsActive += (short) eOutputData.size();
                    eAmpereFlow = 1 + (thingsActive >> 2);
                    mMaxProgresstime = 20;
                    mEfficiencyIncrease = 10000;
                    availableData.set(eAvailableData);
                    return SimpleCheckRecipeResult.ofSuccess("computing");
                } else {
                    // 冷却液不足，停止处理
                    eAvailableData = 0;
                    mEUt = 0; // 停止消耗能量
                    eAmpereFlow = 0;
                    mMaxProgresstime = 0;
                    coolantConsumptionRate = 0;
                    hasInsufficientCoolant = true;
                    availableData.set(0);
                    return SimpleCheckRecipeResult.ofFailure("ote.computer.noliquid");
                }
            } else {
                eAvailableData = 0;
                mEUt = -(int) V[7];
                eAmpereFlow = 1;
                mMaxProgresstime = 20;
                mEfficiencyIncrease = 10000;
                coolantConsumptionRate = 0;
                availableData.set(eAvailableData);
                return SimpleCheckRecipeResult.ofSuccess("no_computing");
            }
        }
        return SimpleCheckRecipeResult.ofFailure("no_computing");
    }

    @Override
    public void outputAfterRecipe_EM() {
        if (!eOutputData.isEmpty()) {
            Vec3Impl pos = null;
            if (getBaseMetaTileEntity() != null) {
                pos = new Vec3Impl(
                    getBaseMetaTileEntity().getXCoord(),
                    getBaseMetaTileEntity().getYCoord(),
                    getBaseMetaTileEntity().getZCoord());
            }

            int eHatchData = 0;

            for (MTEHatchDataInput hatch : eInputData) {
                if (hatch.q == null || hatch.q.contains(pos)) {
                    continue;
                }
                eHatchData += hatch.q.getContent();
            }

            if (eOutputData.isEmpty()) {
                return;
            }
            QuantumDataPacket pack = new QuantumDataPacket((eAvailableData + eHatchData) / eOutputData.size())
                .unifyTraceWith(pos);
            if (pack == null) {
                return;
            }

            for (MTEHatchDataOutput o : eOutputData) {
                o.providePacket(pack);
            }
        }
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.computer.machinetype")) // Machine Type:
            // Quantum
            // Computer
            .addInfo(translateToLocal("ote.computer.desc.0")) // Controller block of
            // the Quantum Computer
            .addInfo(translateToLocal("ote.computer.desc.1")) // Used to generate
            // computation (and heat)
            .addInfo(translateToLocal("ote.computer.desc.2"))

            .addInfo(translateToLocal("ote.computer.desc.3")) // 新增：冷却液系统说明

            .addInfo(translateToLocal("ote.computer.desc.coolant")) // 冷却液效果说明// Use screwdriver to
            // toggle
            // wireless mode
            .addTecTechHatchInfo()
            .beginVariableStructureBlock(2, 2, 4, 4, 5, 16, false)
            .addOtherStructurePart(
                translateToLocal("gt.blockmachines.hatch.certain.tier.07.name"),
                translateToLocal("tt.keyword.Structure.AnyComputerCasing"),
                1) // Uncertainty Resolver: Any Computer Casing on first or last slice
            .addOtherStructurePart(
                translateToLocal("tt.keyword.Structure.DataConnector"),
                translateToLocal("tt.keyword.Structure.AnyComputerCasing"),
                1) // Optical Connector: Any Computer Casing on first or last slice
            .addOtherStructurePart(translateToLocal("ote.calc.rank"), translateToLocal("gt.blockcasingsTT.0.name"), 2)
            .addInputHatch(translateToLocal("ote.computer.coolantInput"), 1) // 新增冷却液输入
            // Computer Rack: Any Advanced Computer Casing, except the outer ones
            .addEnergyHatch(translateToLocal("tt.keyword.Structure.AnyComputerCasing"), 1) // Energy
            // Hatch:
            // Any
            // Computer
            // Casing
            // on
            // first
            // or
            // last
            // slice
            .addMaintenanceHatch(translateToLocal("tt.keyword.Structure.AnyComputerCasing"), 1) // Maintenance
            // Hatch:
            // Any
            // Computer
            // Casing
            // on
            // first
            // or
            // last
            // slice
            .toolTipFinisher();
        return tt;
    }

    @Override
    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            wirelessModeEnabled = !wirelessModeEnabled;
            if (wirelessModeEnabled) {
                GTUtility.sendChatToPlayer(aPlayer, "Wireless mode enabled");
                WirelessComputationPacket.enableWirelessNetWork(getBaseMetaTileEntity());
            } else {
                GTUtility.sendChatToPlayer(aPlayer, "Wireless mode disabled");
                WirelessComputationPacket.disableWirelessNetWork(getBaseMetaTileEntity());
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        super.registerIcons(aBlockIconRegister);
        if (ScreenOFF == null) {
            ScreenOFF = new Textures.BlockIcons.CustomIcon("iconsets/EM_COMPUTER");
        }
        if (ScreenON == null) {
            ScreenON = new Textures.BlockIcons.CustomIcon("iconsets/EM_COMPUTER_ACTIVE");
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.casingTexturePages[BlockGTCasingsTT.texturePage][3],
                new TTRenderedExtendedFacingTexture(aActive ? ScreenON : ScreenOFF) };
        }
        return new ITexture[] { Textures.BlockIcons.casingTexturePages[BlockGTCasingsTT.texturePage][3] };
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_FX_HIGH_FREQ;
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        if (wirelessModeEnabled) {
            if (getBaseMetaTileEntity() != null) {
                WirelessComputationPacket.disableWirelessNetWork(getBaseMetaTileEntity());
            }
        }
        for (OTEHatchRack rack : validMTEList(eRacks)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
        }
    }

    @Override
    protected long getAvailableData_EM() {
        return eAvailableData;
    }

    @Override
    public void stopMachine(@Nonnull ShutDownReason reason) {
        super.stopMachine(reason);

        // 确保停止时完全清空所有计算相关数据
        eAvailableData = 0;
        if (availableData != null) {
            availableData.set(0);
        }

        // 清空所有机架的计算状态
        for (OTEHatchRack rack : validMTEList(eRacks)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
            // 调用机架的方法来重置其内部计算状态
            rack.tickComponents(0, 0); // 传入0来清空计算状态
        }

        // 重置冷却液消耗率
        coolantConsumptionRate = 0;
        hasInsufficientCoolant = false;
    }

    @Override
    protected void afterRecipeCheckFailed() {
        super.afterRecipeCheckFailed();
        // 确保检查失败时完全清空输出数据
        eAvailableData = 0;
        if (availableData != null) {
            availableData.set(0);
        }
        for (OTEHatchRack rack : validMTEList(eRacks)) {
            rack.getBaseMetaTileEntity()
                .setActive(false);
            rack.tickComponents(0, 0);
        }
    }

    public final boolean addRackToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof OTEHatchRack) {
            ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            return eRacks.add((OTEHatchRack) aMetaTileEntity);
        }
        return false;
    }

    public final boolean addWirelessDataOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchWirelessComputationOutput) {
            ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            // Add to wireless computation outputs, so we can detect these and turn on wireless mode,
            // but also add to regular outputs, so they are used as output data hatches by the quantum computer
            return eWirelessComputationOutputs.add((MTEHatchWirelessComputationOutput) aMetaTileEntity)
                && eOutputData.add((MTEHatchWirelessComputationOutput) aMetaTileEntity);
        }
        return false;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList(super.getInfoData()));

        // 添加冷却液信息
        data.add(
            StatCollector.translateToLocalFormatted(
                "ote.computer.info.coolant",
                EnumChatFormatting.AQUA + ""
                    + coolantStored
                    + " / "
                    + MAX_COOLANT_STORAGE
                    + " mb"
                    + EnumChatFormatting.RESET));

        if (coolantConsumptionRate > 0) {
            data.add(
                StatCollector.translateToLocalFormatted(
                    "ote.computer.info.coolant_usage",
                    EnumChatFormatting.GREEN + "" + coolantConsumptionRate + " mb/t" + EnumChatFormatting.RESET));
        }

        if (hasInsufficientCoolant) {
            data.add(
                StatCollector.translateToLocal("ote.computer.noliquid") + EnumChatFormatting.RED
                    + " INSUFFICIENT COOLANT!"
                    + EnumChatFormatting.RESET);
        }

        if (wirelessModeEnabled) {
            WirelessComputationPacket wirelessComputationPacket = null;
            if (getBaseMetaTileEntity() != null) {
                wirelessComputationPacket = WirelessComputationPacket
                    .getPacketByUserId(getBaseMetaTileEntity().getOwnerUuid());
            }
            data.add(StatCollector.translateToLocal("tt.infodata.qc.wireless_mode.enabled"));
            if (wirelessComputationPacket != null) {
                data.add(
                    StatCollector.translateToLocalFormatted(
                        "tt.infodata.qc.total_wireless_computation",
                        "" + EnumChatFormatting.YELLOW + wirelessComputationPacket.getAvailableComputationStored()));
            }
        } else {
            data.add(StatCollector.translateToLocal("tt.infodata.qc.wireless_mode.disabled"));
        }
        return data.toArray(new String[] {});
    }

    public final boolean addCoolantInputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        }
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) {
            return false;
        }
        if (aMetaTileEntity instanceof MTEHatchInput) {
            ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            return eCoolantInputs.add((MTEHatchInput) aMetaTileEntity);
        }
        return false;
    }

    private enum RackHatchElement implements IHatchElement<OTEComputer> {

        INSTANCE;

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return Collections.singletonList(OTEHatchRack.class);
        }

        @Override
        public IGTHatchAdder<? super OTEComputer> adder() {
            return OTEComputer::addRackToMachineList;
        }

        @Override
        public long count(OTEComputer t) {
            return t.eRacks.size();
        }
    }

    private enum CoolantHatchElement implements IHatchElement<OTEComputer> {

        INSTANCE;

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return Collections.singletonList(MTEHatchInput.class);
        }

        @Override
        public IGTHatchAdder<? super OTEComputer> adder() {
            return OTEComputer::addCoolantInputToMachineList;
        }

        @Override
        public long count(OTEComputer t) {
            return t.eCoolantInputs.size();
        }
    }

    private enum WirelessComputationHatchElement implements IHatchElement<OTEComputer> {

        INSTANCE;

        @Override
        public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
            return Collections.singletonList(MTEHatchWirelessComputationOutput.class);
        }

        @Override
        public IGTHatchAdder<? super OTEComputer> adder() {
            return OTEComputer::addWirelessDataOutputToMachineList;
        }

        @Override
        public long count(OTEComputer gtMetaTileEntityEmComputer) {
            return gtMetaTileEntityEmComputer.eWirelessComputationOutputs.size();
        }
    }
}
