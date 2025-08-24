package com.newmaa.othtech.machine;

import static gregtech.api.enums.GTValues.VP;

import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizons.modularui.common.widget.DynamicPositionedColumn;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.gtnewhorizons.modularui.common.widget.SlotWidget;
import com.gtnewhorizons.modularui.common.widget.TextWidget;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.ParallelHelper;
import gregtech.common.tileentities.machines.MTEHatchOutputME;
import gtnhintergalactic.recipe.SpacePumpingRecipes;
import gtnhintergalactic.tile.multi.elevator.TileEntitySpaceElevator;
import gtnhintergalactic.tile.multi.elevatormodules.TileEntityModuleBase;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public abstract class OTETileEntityModulePumpT4 extends TileEntityModuleBase {

    public static final long ENERGY_CONSUMPTION = (int) VP[15];
    /** Input parameters */
    Parameters.Group.ParameterIn[] parallelSettings;
    Parameters.Group.ParameterIn[] gasTypeSettings;
    Parameters.Group.ParameterIn[] planetTypeSettings;
    Parameters.Group.ParameterIn batchSetting;
    /** Name of the planet type setting */
    private static final INameFunction<OTETileEntityModulePumpT4> PLANET_TYPE_SETTING_NAME = (base,
        p) -> GCCoreUtil.translate("gt.blockmachines.multimachine.project.ig.pump.cfgi.0") + " "
            + (p.hatchId() / 2 + 1); // Planet Type
    /** Status of the planet type setting */
    private static final IStatusFunction<OTETileEntityModulePumpT4> PLANET_TYPE_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 1, 0, 100, 100);
    /** Name of the gas type setting */
    private static final INameFunction<OTETileEntityModulePumpT4> GAS_TYPE_SETTING_NAME = (base,
        p) -> GCCoreUtil.translate("gt.blockmachines.multimachine.project.ig.pump.cfgi.1") + " "
            + (p.hatchId() / 2 + 1); // Gas Type
    /** Status of the gas type setting */
    private static final IStatusFunction<OTETileEntityModulePumpT4> GAS_TYPE_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 1, 0, 100, 100);
    /** Name of the parallel setting */
    private static final INameFunction<OTETileEntityModulePumpT4> PARALLEL_SETTING_NAME = (base,
        p) -> GCCoreUtil.translate("gt.blockmachines.multimachine.project.ig.pump.cfgi.2") + " "
            + (p.hatchId() / 2 + 1); // Parallels
    /** Status of the parallel setting */
    private static final IStatusFunction<OTETileEntityModulePumpT4> PARALLEL_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 0, 1, 123123, base.getParallels());
    /** Name of the batch setting */
    private static final INameFunction<OTETileEntityModulePumpT4> BATCH_SETTING_NAME = (base, p) -> GCCoreUtil
        .translate("gt.blockmachines.multimachine.project.ig.pump.cfgi.3"); // Batch size
    /** Status of the batch setting */
    private static final IStatusFunction<OTETileEntityModulePumpT4> BATCH_STATUS = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 1, 0, 32, 128);
    /** Flag if this machine has an ME output hatch, will be updated in the structure check */
    protected boolean hasMeOutputHatch = false;

    public OTETileEntityModulePumpT4(int aID, String aName, String aNameRegional, int tTier, int tModuleTier,
        int tMinMotorTier) {
        super(aID, aName, aNameRegional, tTier, tModuleTier, tMinMotorTier);
    }

    public OTETileEntityModulePumpT4(String aName, int Tier, int tModuleTier, int tMinMotorTier) {
        super(aName, Tier, tModuleTier, tMinMotorTier);
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing_EM() {
        // 检查能量
        if (ENERGY_CONSUMPTION * getParallelRecipes() * getParallels() > getEUVar()) {
            return CheckRecipeResultRegistry
                .insufficientPower(ENERGY_CONSUMPTION * getParallelRecipes() * getParallels());
        }
        // 处理配方
        ArrayList<FluidStack> outputs = new ArrayList<>();
        int usedEUt = 0;
        int maxBatchSize = (int) Math.min(Math.max(batchSetting.get(), 1.0D), 128.0D);
        for (int i = 0; i < getParallelRecipes(); i++) {
            FluidStack fluid = SpacePumpingRecipes.RECIPES
                .get(Pair.of((int) planetTypeSettings[i].get(), (int) gasTypeSettings[i].get()));
            if (fluid != null) {
                int batchSize = (int) Math.min(Math.max(batchSetting.get(), 1.0D), 128.0D);
                MTEHatchOutput targetOutput = null;
                if (!hasMeOutputHatch && !eSafeVoid) {
                    for (MTEHatchOutput output : mOutputHatches) {
                        if (output.mFluid != null && output.mFluid.getFluid() != null
                            && output.getLockedFluidName() != null
                            && output.getLockedFluidName()
                                .equals(
                                    fluid.getFluid()
                                        .getName())
                            && output.mFluid.getFluid()
                                .equals(fluid.getFluid())) {
                            targetOutput = output;
                            break;
                        }
                    }
                }
                int parallels = Math.min((int) parallelSettings[i].get(), getParallels());
                if (targetOutput != null) {
                    int outputSpace = targetOutput.getCapacity() - targetOutput.getFluidAmount();
                    if (outputSpace < fluid.amount) {
                        continue;
                    }
                    parallels = Math.min(parallels, outputSpace / fluid.amount);
                    batchSize = Math.min(batchSize, outputSpace / (fluid.amount * parallels));
                    maxBatchSize = Math.max(maxBatchSize, batchSize);
                }
                if (parallels > 0 && batchSize > 0) {
                    fluid = fluid.copy();
                    long fluidLong = (long) fluid.amount * parallels * batchSize;
                    usedEUt += (int) (ENERGY_CONSUMPTION * parallels);
                    ParallelHelper.addFluidsLong(outputs, fluid, fluidLong);
                }
            }
        }
        lEUt = -usedEUt;
        mOutputFluids = outputs.toArray(new FluidStack[0]);
        eAmpereFlow = 1;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = 20 * maxBatchSize;
        return !outputs.isEmpty() ? CheckRecipeResultRegistry.SUCCESSFUL : CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    protected void parametersInstantiation_EM() {
        super.parametersInstantiation_EM();
        int parallels = getParallelRecipes();
        planetTypeSettings = new Parameters.Group.ParameterIn[parallels];
        gasTypeSettings = new Parameters.Group.ParameterIn[parallels];
        parallelSettings = new Parameters.Group.ParameterIn[parallels];
        for (int i = 0; i < getParallelRecipes(); i++) {
            planetTypeSettings[i] = parametrization.getGroup(i * 2, false)
                .makeInParameter(0, 1, PLANET_TYPE_SETTING_NAME, PLANET_TYPE_STATUS);
            gasTypeSettings[i] = parametrization.getGroup(i * 2, false)
                .makeInParameter(1, 1, GAS_TYPE_SETTING_NAME, GAS_TYPE_STATUS);
            parallelSettings[i] = parametrization.getGroup(i * 2 + 1, false)
                .makeInParameter(0, getParallels(), PARALLEL_SETTING_NAME, PARALLEL_STATUS);
        }
        batchSetting = parametrization.getGroup(9, false)
            .makeInParameter(1, 1, BATCH_SETTING_NAME, BATCH_STATUS);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        boolean state = super.checkMachine_EM(aBaseMetaTileEntity, aStack);
        hasMeOutputHatch = false;
        if (state) {
            for (MTEHatchOutput output : mOutputHatches) {
                if (output instanceof MTEHatchOutputME) {
                    hasMeOutputHatch = true;
                    break;
                }
            }
        }
        return state;
    }

    protected static Textures.BlockIcons.CustomIcon engraving;

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE),
                new TTRenderedExtendedFacingTexture(aActive ? TTMultiblockBase.ScreenON : TTMultiblockBase.ScreenOFF) };
        } else if (facing.getRotation(ForgeDirection.UP) == side || facing.getRotation(ForgeDirection.DOWN) == side) {
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE),
                new TTRenderedExtendedFacingTexture(engraving) };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TileEntitySpaceElevator.CASING_INDEX_BASE) };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        engraving = new Textures.BlockIcons.CustomIcon("iconsets/OVERLAY_SIDE_PUMP_MODULE");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    protected void drawTexts(DynamicPositionedColumn screenElements, SlotWidget inventorySlot) {
        super.drawTexts(screenElements, inventorySlot);
        screenElements.widget(
            new TextWidget(StatCollector.translateToLocal("gt.blockmachines.multimachine.ig.elevator.gui.config"))
                .setDefaultColor(COLOR_TEXT_WHITE.get())
                .setEnabled(widget -> mMachine));
        for (int i = 0; i < getParallelRecipes(); i++) {
            final int fluidIndex = i;
            screenElements.widget(TextWidget.dynamicString(() -> {
                String fluidName = getPumpedFluid(fluidIndex);
                if (fluidName != null) {
                    return " - " + fluidName;
                }
                return "";
            })
                .setSynced(false)
                .setDefaultColor(COLOR_TEXT_WHITE.get())
                .setEnabled(widget -> mMachine && getPumpedFluid(fluidIndex) != null))
                .widget(
                    new FakeSyncWidget.IntegerSyncer(
                        () -> (int) planetTypeSettings[fluidIndex].get(),
                        val -> parametrization.trySetParameters(
                            planetTypeSettings[fluidIndex].id % 10,
                            planetTypeSettings[fluidIndex].id / 10,
                            planetTypeSettings[fluidIndex].get())))
                .widget(
                    new FakeSyncWidget.IntegerSyncer(
                        () -> (int) planetTypeSettings[fluidIndex].get(),
                        val -> parametrization.trySetParameters(
                            gasTypeSettings[fluidIndex].id % 10,
                            gasTypeSettings[fluidIndex].id / 10,
                            gasTypeSettings[fluidIndex].get())));
        }
    }

    private String getPumpedFluid(int index) {
        if (index < 0 || index >= getParallelRecipes()) {
            return null;
        }
        FluidStack fluid = SpacePumpingRecipes.RECIPES
            .get(Pair.of((int) planetTypeSettings[index].get(), (int) gasTypeSettings[index].get()));
        if (fluid == null) {
            return null;
        }
        return fluid.getLocalizedName();
    }

    protected abstract int getParallels();

    protected abstract int getParallelRecipes();

    public static class ModulePumpT4 extends OTETileEntityModulePumpT4 {

        /** Voltage tier of this module */
        protected static final int MODULE_VOLTAGE_TIER = 16;
        /** Tier of this module */
        protected static final int MODULE_TIER = 4;
        /** Minimum motor tier that is needed for this module */
        protected static final int MINIMUM_MOTOR_TIER = 5;
        /** Maximum amount of parallels of one recipe */
        protected static final int MAX_PARALLELS = 123123;
        /** Maximum amount of different recipes that can be done at once */
        protected static final int MAX_PARALLEL_RECIPES = 5;

        /**
         * Create a new T4 mining module controller
         *
         * @param aID           ID of the controller
         * @param aName         Name of the controller
         * @param aNameRegional Localized name of the controller
         */
        public ModulePumpT4(int aID, String aName, String aNameRegional) {
            super(aID, aName, aNameRegional, MODULE_VOLTAGE_TIER, MODULE_TIER, MINIMUM_MOTOR_TIER);
        }

        /**
         * Create a new T4 mining module controller
         *
         * @param aName Name of the controller
         */
        public ModulePumpT4(String aName) {
            super(aName, MODULE_VOLTAGE_TIER, MODULE_TIER, MINIMUM_MOTOR_TIER);
        }

        /**
         * Get the number of parallels that this module can handle
         *
         * @return Number of possible parallels
         */
        protected int getParallels() {
            return MAX_PARALLELS;
        }

        /**
         * Get the number of parallel recipes that this module can handle
         *
         * @return Number of possible parallel recipes
         */
        protected int getParallelRecipes() {
            return MAX_PARALLEL_RECIPES;
        }

        /**
         * Get a new meta tile entity of this controller
         *
         * @param iGregTechTileEntity this
         * @return New meta tile entity
         */
        @Override
        public IMetaTileEntity newMetaEntity(IGregTechTileEntity iGregTechTileEntity) {
            return new ModulePumpT4(mName);
        }

        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType("太空钻机模块")
                .addInfo("将太空钻机功能添加到太空电梯")
                .addInfo("§n§b我不羡慕太阳 照不亮你过往")
                .addInfo("直接从太空电梯接受能量,不使用能源仓")
                .addInfo("设定的行星和气体类型决定了输出的流体")
                .addInfo("锁定输出仓即可防止溢出销毁")
                .addInfo("§9运行电压：§aMAX 每个配方的最大并行：§c123123")
                .addInfo("一次处理多达§e5§r种不同的配方")
                .addInfo("需要§6MK-V§r或更高等级的加速轨道")
                .addSeparator()
                .beginStructureBlock(1, 5, 2, false)
                .addCasingInfoRange("太空电梯基座机械方块", 0, 9, false)
                .addOutputHatch("任意太空电梯基座机械方块", 1)
                .addInfo("§b§lAuthor:§l憋憋大师--HuimaoX")
                .toolTipFinisher("§a123Technology - SpacePump");
            return tt;
        }
    }
}
