package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.newmaa.othtech.common.recipemap.Recipemaps.NaquadahFuelFakeRecipes;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.TickTime.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.materials.BWLiquids;
import com.newmaa.othtech.machine.machineclass.OTHTTMultiMachineBaseEM;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.metatileentity.implementations.MTEHatchMuffler;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.maps.FuelBackend;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings8;
import gregtech.common.tileentities.machines.IRecipeProcessingAwareHatch;
import gregtech.common.tileentities.machines.MTEHatchInputME;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;

public class OTENQFuelGeneratorUniversal extends OTHTTMultiMachineBaseEM
    implements IConstructable, ISurvivalConstructable {

    public OTENQFuelGeneratorUniversal(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        super.useLongPower = true;
    }

    public OTENQFuelGeneratorUniversal(String aName) {
        super(aName);
        super.useLongPower = true;
    }

    private int pipeTier = -1;
    private UUID ownerUUID;
    private boolean isWirelessMode = false;
    private String costingWirelessEU = "0";
    Parameters.Group.ParameterIn time;
    private static final IStatusFunction<OTENQFuelGeneratorUniversal> timeSTATUES = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 1, 2, 1000, 114514);
    private static final INameFunction<OTENQFuelGeneratorUniversal> timeName = (base, p) -> GCCoreUtil
        .translate("ote.tt.exc.0");

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setBoolean("wireless", isWirelessMode);
        aNBT.setInteger("pipe", this.pipeTier);
        aNBT.setLong("Time", running_time);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        this.pipeTier = aNBT.getInteger("pipe");
        isWirelessMode = aNBT.getBoolean("wireless");
        running_time = aNBT.getLong("Time");
        super.loadNBTData(aNBT);
    }

    @Override
    protected void parametersInstantiation_EM() {
        Parameters.Group hatch_0 = parametrization.getGroup(0, false);
        time = hatch_0.makeInParameter(0, 1, timeName, timeSTATUES);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return NaquadahFuelFakeRecipes;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String bonus = GTUtility.formatNumbers(getPowerFlow());
            tag.setString("bonus", bonus);
            tag.setBoolean("WM", isWirelessMode);
            tag.setString("costingWirelessEU", costingWirelessEU);
            tag.setString("EFF", String.valueOf(tEff));
            tag.setString("pipe", String.valueOf(this.pipeTier));
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            EnumChatFormatting.BOLD + translateToLocal("otht.waila.gen.wire")
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("bonus")
                + EnumChatFormatting.GOLD
                + "EU/t"
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + translateToLocal("otht.waila.gen.wireless")
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("costingWirelessEU")
                + EnumChatFormatting.GOLD
                + "EU"
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + translateToLocal("otht.waila.tier.pipe")
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("pipe")
                + EnumChatFormatting.RESET);

        currentTip.add(
            EnumChatFormatting.BOLD + translateToLocal("otht.waila.mode.wireless")
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.RESET
                + tag.getBoolean("WM")
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + translateToLocal("otht.waila.eff")
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("EFF")
                + EnumChatFormatting.GOLD
                + "%"
                + EnumChatFormatting.RESET);
        if (tag.getString("pipe")
            .equals("1")) {
            currentTip.add(
                EnumChatFormatting.BOLD + "otht.waila.gen.requireFluid"
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.AQUA
                    + "ote.fluid.gen.1"
                    + EnumChatFormatting.RESET);
        } else if (tag.getString("pipe")
            .equals("2")) {
                currentTip.add(
                    EnumChatFormatting.BOLD + "otht.waila.gen.requireFluid"
                        + EnumChatFormatting.RESET
                        + ": "
                        + EnumChatFormatting.AQUA
                        + "ote.fluid.gen.2"
                        + EnumChatFormatting.RESET);
            }
    }

    private long tEff;

    public long running_time = 0;

    @Override
    public @NotNull CheckRecipeResult checkProcessing_EM() {
        ArrayList<FluidStack> tFluids = getStoredFluids();
        long PromoterAmount = findLiquidAmount(getPromoter(), tFluids);

        CheckRecipeResult result;
        result = processFuel(tFluids, NaquadahFuelFakeRecipes, PromoterAmount, 0.006D, 3);
        if (result.wasSuccessful()) {
            return result;
        }
        running_time = 0;
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    protected CheckRecipeResult processFuel(ArrayList<FluidStack> tFluids, RecipeMap<FuelBackend> recipeMap,
        long PromoterAmount, double efficiencyCoefficient, double FuelsValueBonus) {
        List<FluidStack> extraFluids = new ArrayList<>();
        for (GTRecipe recipe : recipeMap.getAllRecipes()) {
            FluidStack tFuel = findFuel(recipe);
            FluidStack[] outputFluidStack = new FluidStack[] { recipe.getFluidOutput(0) };
            if (tFuel == null) continue;
            long FuelAmount = findLiquidAmount(tFuel, tFluids);
            if (FuelAmount == 0) continue;
            calculateEfficiency(FuelAmount, PromoterAmount, efficiencyCoefficient);
            consumeAllLiquid(tFuel, tFluids);
            consumeAllLiquid(getPromoter(), tFluids);
            for (FluidStack fluids : outputFluidStack) {
                if (fluids.amount <= Integer.MAX_VALUE / FuelAmount) {
                    fluids.amount *= (int) FuelAmount;
                } else {
                    for (int i = 0; i < FuelAmount - 1; i++) {
                        extraFluids.add(fluids.copy());
                    }
                }
            }
            if (extraFluids.isEmpty()) {
                mOutputFluids = outputFluidStack;
            } else {
                extraFluids.addAll(Arrays.asList(outputFluidStack));
                mOutputFluids = extraFluids.toArray(new FluidStack[] {});
            }
            if (isWirelessMode) {
                this.setPowerFlow(0);
                BigInteger costingWirelessEUTemp = BigInteger.valueOf(recipe.mSpecialValue)
                    .multiply(
                        BigInteger.valueOf((long) FuelsValueBonus)
                            .multiply(BigInteger.valueOf(FuelAmount)))
                    .multiply(BigInteger.valueOf(tEff))
                    .divide(BigInteger.valueOf((100)))
                    .multiply(BigInteger.valueOf(recipe.mDuration));
                costingWirelessEU = GTUtility.formatNumbers(costingWirelessEUTemp);
                if (!addEUToGlobalEnergyMap(ownerUUID, costingWirelessEUTemp)) {
                    return CheckRecipeResultRegistry.INTERNAL_ERROR;
                }
            } else {
                costingWirelessEU = "0";
                this.setPowerFlow(
                    (long) Math.min(
                        Long.MAX_VALUE - 1,
                        FuelAmount * recipe.mSpecialValue
                            * FuelsValueBonus
                            * tEff
                            / 100
                            * recipe.mDuration
                            / time.get()));

            }
            this.mMaxProgresstime = (int) time.get();
            running_time = (running_time + (mMaxProgresstime / 20));
            this.updateSlots();
            return CheckRecipeResultRegistry.GENERATING;
        }
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (this.getBaseMetaTileEntity()
            .isServerSide()) {
            if (heatingTicks < HEATING_TIMER) {
                heatingTicks++;
                isStoppingSafe = true;
            } else if (isStoppingSafe) isStoppingSafe = false;
            addAutoEnergy();
        }
        return true;
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick_EM(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    public FluidStack findFuel(GTRecipe aFuel) {
        if (aFuel.mInputs != null && aFuel.mInputs.length > 0)
            return GTUtility.getFluidForFilledItem(aFuel.mInputs[0], true);
        else return aFuel.mFluidInputs[0];
    }

    public void calculateEfficiency(long aFuel, long aPromoter, double coefficient) {
        if (aPromoter == 0) {
            this.tEff = 0;
            return;
        }
        this.tEff = ((int) (Math.exp(-coefficient * (double) aFuel / (double) aPromoter) * 1.5D * 100)
            + (running_time));
        this.tEff = Math.min(this.tEff, 1230);
    }

    public long findLiquidAmount(FluidStack liquid, List<FluidStack> input) {
        long cnt = 0;
        for (FluidStack fluid : input) {
            if (fluid.isFluidEqual(liquid)) {
                cnt += fluid.amount;
            }
        }
        if (cnt < 0) cnt = 0;
        return cnt;
    }

    public void consumeAllLiquid(FluidStack liquid, List<FluidStack> input) {
        for (FluidStack fluid : input) {
            if (fluid.isFluidEqual(liquid)) fluid.amount = 0;
        }
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        for (final MTEHatchInput tHatch : validMTEList(mInputHatches)) {
            if (tHatch instanceof MTEHatchInputME) {
                return false;
            }
        }
        if (this.pipeTier != 2) {
            this.isWirelessMode = false;
        }

        return structureCheck_EM(mName, 2, 2, 0);
    }

    protected final List<MTEHatchInput> mMiddleInputHatches = new ArrayList<>();

    @Override
    public void startRecipeProcessing() {
        for (MTEHatchInput hatch : validMTEList(mMiddleInputHatches)) {
            if (hatch instanceof IRecipeProcessingAwareHatch aware) {
                aware.startRecipeProcessing();
            }
        }
        super.startRecipeProcessing();
    }

    @Override
    public void endRecipeProcessing() {
        super.endRecipeProcessing();
        for (MTEHatchInput hatch : validMTEList(mMiddleInputHatches)) {
            if (hatch instanceof IRecipeProcessingAwareHatch aware) {
                setResultIfFailure(aware.endRecipeProcessing(this));
            }
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM(mName, 2, 2, 0, stackSize, hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(mName, stackSize, 2, 2, 0, elementBudget, env, false, true);
    }

    @Override
    public void stopMachine() {
        // Reset the counter for heating, so that it works again when the machine restarts
        heatingTicks = 0;
        running_time = 0;
        super.stopMachine();
    }

    private boolean isStoppingSafe;
    private int heatingTicks;
    protected final int HEATING_TIMER = SECOND * 10;

    void addAutoEnergy() {
        long exEU = this.getPowerFlow(); // 直接使用功率流，不再乘以效率

        // 分配能量到所有可用的动力仓
        if (!addEnergyOutputMultipleDynamos(exEU, false)) {
            // 如果能量无法完全输出，停止机器
            if (!isStoppingSafe) {
                stopMachine();
            }
        }
    }

    // 添加能量输出到多个动力仓的方法
    public boolean addEnergyOutputMultipleDynamos(long totalEU, boolean aAllowMixedVoltageDynamos) {
        // check positive
        if (totalEU < 0) totalEU = -totalEU;
        // to store free capacity of dynamo hatch
        long freeCapacity;

        // 检查普通动力仓
        for (MTEHatchDynamo tHatch : validMTEList(mDynamoHatches)) {
            freeCapacity = tHatch.maxEUStore() - tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (freeCapacity > 0) {
                if (totalEU > freeCapacity) {
                    tHatch.setEUVar(tHatch.maxEUStore());
                    totalEU -= freeCapacity;
                } else {
                    tHatch.setEUVar(
                        tHatch.getBaseMetaTileEntity()
                            .getStoredEU() + totalEU);
                    return true;
                }
            }
        }

        // 检查多安仓
        for (MTEHatchDynamoMulti tHatch : validMTEList(eDynamoMulti)) {
            freeCapacity = tHatch.maxEUStore() - tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (freeCapacity > 0) {
                if (totalEU > freeCapacity) {
                    tHatch.setEUVar(tHatch.maxEUStore());
                    totalEU -= freeCapacity;
                } else {
                    tHatch.setEUVar(
                        tHatch.getBaseMetaTileEntity()
                            .getStoredEU() + totalEU);
                    return true;
                }
            }
        }

        // 如果还有剩余能量，返回false
        return totalEU == 0;
    }

    public final boolean addMuffler(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchMuffler) {
                ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
                return this.mMufflerHatches.add((MTEHatchMuffler) aMetaTileEntity);
            }
        }
        return false;
    }

    public final boolean addInputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchInput) {
                ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
                return this.mInputHatches.add((MTEHatchInput) aMetaTileEntity);
            }
        }
        return false;
    }

    public final boolean addDynamoHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) {
            return false;
        } else {
            IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
            if (aMetaTileEntity instanceof MTEHatchDynamo) {
                ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
                return this.mDynamoHatches.add((MTEHatchDynamo) aMetaTileEntity);
            } else if (aMetaTileEntity instanceof MTEHatchDynamoMulti) {
                ((MTEHatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
                return this.eDynamoMulti.add((MTEHatchDynamoMulti) aMetaTileEntity);
            }
        }
        return false;
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTENQFuelGeneratorUniversal> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTENQFuelGeneratorUniversal>builder()
                .addShape(
                    mName,
                    transpose(
                        new String[][] {
                            { "TTTTT", "TTMMT", "TTMMT", "TTMMT", "TTMMT", "TTMMT", "TTMMT", "TTMMT", "TTTTT" },
                            { "TTTTT", "SPCCI-", "SPCCI-", "SPCCI-", "SPCCI-", "SPCCI-", "SPCCI-", "SPCCI-", "TTTTT" },
                            { "TT~TT", "SPGGI-", "SPGGI-", "SPGGI-", "SPGGI-", "SPGGI-", "SPGGI-", "SPGGI-", "TTETT" },
                            { "TTTTT", "TTTTT", "TTTTT", "TTTTT", "TTTTT", "TTTTT", "TTTTT", "TTTTT", "TTTTT" } }))
                .addElement('T', ofBlock(sBlockCasings8, 10))
                .addElement(
                    'M',
                    buildHatchAdder(OTENQFuelGeneratorUniversal.class).atLeast(Muffler)
                        .adder(OTENQFuelGeneratorUniversal::addToMachineList)
                        .dot(2)
                        .casingIndex(((BlockCasings8) sBlockCasings8).getTextureIndex(10))
                        .buildAndChain(sBlockCasings8, 10))
                .addElement(
                    'S',
                    buildHatchAdder(OTENQFuelGeneratorUniversal.class).atLeast(InputHatch, OutputHatch)
                        .adder(OTENQFuelGeneratorUniversal::addToMachineList)
                        .dot(3)
                        .casingIndex(((BlockCasings8) sBlockCasings8).getTextureIndex(10))
                        .buildAndChain(sBlockCasings8, 10))
                .addElement(
                    'E',
                    buildHatchAdder(OTENQFuelGeneratorUniversal.class).atLeast(Energy.or(ExoticEnergy))
                        .adder(OTENQFuelGeneratorUniversal::addToMachineList)
                        .dot(4)
                        .casingIndex(((BlockCasings8) sBlockCasings8).getTextureIndex(10))
                        .buildAndChain(sBlockCasings8, 10))
                .addElement(
                    'P',
                    withChannel(
                        "pipe",
                        ofBlocksTiered(
                            this::getPipeTier,
                            ImmutableList.of(Pair.of(sBlockCasings2, 15), Pair.of(sBlockCasings9, 14)),
                            -1,
                            (t, meta) -> t.pipeTier = meta,
                            OTENQFuelGeneratorUniversal::getPipeTierInstance)))
                .addElement('C', ofBlock(sBlockCasings6, 8))
                .addElement('G', ofBlock(sBlockCasings2, 4))
                .addElement('I', ofBlock(sBlockCasings8, 4))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public FluidStack getPromoter() {
        if (this.pipeTier == 1) { // T1
            return BWLiquids.PromoterZPM.getFluidOrGas(1);
        } else if (this.pipeTier >= 2) { // T2
            return BWLiquids.PromoterUEV.getFluidOrGas(1);
        } else {
            return BWLiquids.PromoterZPM.getFluidOrGas(1); // fallback
        }
    }

    public Integer getPipeTierInstance() {
        return this.pipeTier;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        isWirelessMode = this.pipeTier == 2 && !isWirelessMode;
        if (isWirelessMode) {
            aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.nfg.mode.0"));
        } else {
            aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.nfg.mode.1"));
        }
        /*
         * GTUtility.sendChatToPlayer(aPlayer, translateToLocal(isWirelessMode ? "无线模式启动" : "无线模式关闭"));
         */
    }

    private static IStructureDefinition<OTENQFuelGeneratorUniversal> STRUCTURE_DEFINITION = null;

    @Nullable
    protected Integer getPipeTier(Block block, int meta) {
        if (block == null) return null;

        if (block == sBlockCasings2 && meta == 15) {
            this.pipeTier = 1; // 使用 this.pipeTier 设置当前实例的值
            return 1;
        } else if (block == sBlockCasings9 && meta == 14) {
            this.pipeTier = 2; // 使用 this.pipeTier 设置当前实例的值
            return 2;
        }
        return null;
    }

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.nfg.0") + ":",
        translateToLocal("ote.cm.nfg.1") + ":", translateToLocal("ote.cm.nfg.2"), };

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTENQFuelGeneratorUniversal(this.mName);
    }

    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType("通化之梦")
     * .addInfo(" §l§4“你产能不够吧? -v-” ")
     * .addInfo("通化的升级版本, 可以支持硅岩燃料, 但不能使用传统燃料喔 ~ 真正的清洁能源")
     * .addInfo("耗时根据主机参数决定, 单位tick, 无线模式实际功率为总发电 / 耗时(tick)")
     * .addInfo("和通化一样的设计, 但同时兼备了大硅岩的加成")
     * .addInfo("一次消耗输入仓内所有燃料和助燃剂")
     * .addInfo("感谢伟大的铱锇钐大人，所有输出功率都将为原来的三倍")
     * .addInfo("二级管道方块解锁无线模式, 使用螺丝刀开启")
     * .addInfo("§a更高级的结构意味着更高级的§c助燃剂, §a一级结构:原子分离助燃剂, 二级结构:超维度等离子助燃剂")
     * .addInfo("§a效率随着运行时间提升, 每秒提升1%效率，具体效率阈值为1230%")
     * .addInfo("计算公式：总发电量=燃料数量*输出系数(3)*效率*燃料输出功率*配方时间")
     * .addInfo("PS：上述计算的发电量为一次运行的总发电")
     * .addInfo("§a注意 : 如果耗时小于20ticks, 则效率不会累加")
     * .addInfo("§4你知道会发生什么。")
     * .addInfo("§e警告 : 如果强行使用有线模式烧高级硅岩燃料, 将会导致电表倒转憋憋")
     * .addInfo("§c支持TecTech多安动力仓, 激光仓, 但尽量请使用1,073,741,824A激光仓直连电容库")
     * .addSeparator()
     * .addController("发电坤")
     * .beginStructureBlock(7, 13, 7, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .toolTipFinisher("§b123Technology - KING OF GENERATORS , FOR THE GOD PIGEON");
     * return tt;
     * }
     */
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.nfg.0"))
            .addInfo(translateToLocal("ote.tm.nfg.1"))
            .addInfo(translateToLocal("ote.tm.nfg.2"))
            .addInfo(translateToLocal("ote.tm.nfg.3"))
            .addInfo(translateToLocal("ote.tm.nfg.4"))
            .addInfo(translateToLocal("ote.tm.nfg.5"))
            .addInfo(translateToLocal("ote.tm.nfg.6"))
            .addInfo(translateToLocal("ote.tm.nfg.7"))
            .addInfo(translateToLocal("ote.tm.nfg.8"))
            .addInfo(translateToLocal("ote.tm.nfg.9"))
            .addInfo(translateToLocal("ote.tm.nfg.10"))
            .addInfo(translateToLocal("ote.tm.nfg.12"))
            .addInfo(translateToLocal("ote.tm.nfg.13"))
            .addInfo(translateToLocal("ote.tm.nfg.14"))
            .addInfo(translateToLocal("ote.tm.nfg.15"))
            .addSeparator()
            .addController(translateToLocal("ote.tm.nfg.16"))
            .beginStructureBlock(7, 13, 7, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§b123Technology - KING OF GENERATORS , FOR THE GOD PIGEON");
        return tt;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsInputSeparation() {
        return true;
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return true;
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings8, 10)), TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] { getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings8, 10)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings8, 10)) };
    }
}
