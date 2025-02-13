package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.newmaa.othtech.common.recipemap.Recipemaps.NaquadahFuelFakeRecipes;
import static goodgenerator.main.GGConfigLoader.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.TickTime.SECOND;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
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
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.materials.liquids;
import com.newmaa.othtech.machine.machineclass.TT_MultiMachineBase_EM;
import com.newmaa.othtech.utils.PairA;

import gregtech.api.enums.Materials;
import gregtech.api.enums.MaterialsUEVplus;
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
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;

public class OTENQFuelGeneratorUniversal extends TT_MultiMachineBase_EM
    implements IConstructable, ISurvivalConstructable {

    public OTENQFuelGeneratorUniversal(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        super.useLongPower = true;
    }

    public OTENQFuelGeneratorUniversal(String aName) {
        super(aName);
        super.useLongPower = true;
    }

    private static final List<PairA<FluidStack, Integer>> coolant;

    private int pipeTier = 0;
    private UUID ownerUUID;
    private boolean isWirelessMode = false;
    private String costingWirelessEU = "0";

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {

        aNBT.setBoolean("wireless", isWirelessMode);
        aNBT.setInteger("pipe", pipeTier);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        pipeTier = aNBT.getInteger("pipe");
        isWirelessMode = aNBT.getBoolean("wireless");
        super.loadNBTData(aNBT);
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
            String bonus = GTUtility.formatNumbers(getPowerFlow() * tEff / 10000);
            tag.setString("bonus", bonus);
            tag.setBoolean("WM", isWirelessMode);
            tag.setString("costingWirelessEU", costingWirelessEU);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            EnumChatFormatting.BOLD + "有线发电"
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("bonus")
                + EnumChatFormatting.GOLD
                + "EU/t"
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + "无线发电"
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("costingWirelessEU")
                + EnumChatFormatting.GOLD
                + "EU/t"
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + "无线模式"
                + EnumChatFormatting.RESET
                + tag.getBoolean("WM")
                + EnumChatFormatting.RESET);
    }

    private long tEff;

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (pipeTier != 0 && !isWirelessMode) {
            isWirelessMode = true;
        } else {
            isWirelessMode = false;
        }
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal(isWirelessMode ? "无线模式启动" : "无线模式关闭"));
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing_EM() {
        ArrayList<FluidStack> tFluids = getStoredFluids();

        long PromoterAmount = findLiquidAmount(getPromoter(), tFluids);

        CheckRecipeResult result;
        result = processFuel(tFluids, NaquadahFuelFakeRecipes, PromoterAmount, 0.006D, 3);
        if (result.wasSuccessful()) {
            return result;
        }
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
            FluidStack[] input = getStoredFluids().toArray(new FluidStack[0]);
            int eff = consumeCoolant(input);
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
                BigInteger costingWirelessEUTemp = PromoterAmount != 0 ? BigInteger.valueOf(recipe.mSpecialValue)
                    .multiply(
                        BigInteger.valueOf((long) FuelsValueBonus)
                            .multiply(BigInteger.valueOf(FuelAmount)))
                    .multiply(BigInteger.valueOf(eff))
                    .divide(BigInteger.valueOf(100 * SECOND))
                    .multiply(BigInteger.valueOf(recipe.mDuration)) : BigInteger.ZERO;
                costingWirelessEU = GTUtility.formatNumbers(costingWirelessEUTemp);
                if (!addEUToGlobalEnergyMap(ownerUUID, costingWirelessEUTemp)) {
                    return CheckRecipeResultRegistry.INTERNAL_ERROR;
                }
            } else {
                costingWirelessEU = "0";
                this.setPowerFlow(
                    (long) Math.min(
                        Long.MAX_VALUE - 1,
                        FuelAmount * recipe.mSpecialValue * FuelsValueBonus * eff / 100 * recipe.mDuration / SECOND));

            }
            this.mMaxProgresstime = 20;
            this.updateSlots();
            return CheckRecipeResultRegistry.GENERATING;
        }
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        startRecipeProcessing();
        if (this.getBaseMetaTileEntity()
            .isServerSide()) {

            if (heatingTicks < HEATING_TIMER) {
                heatingTicks++;
                isStoppingSafe = true;
            } else if (isStoppingSafe) isStoppingSafe = false;
            endRecipeProcessing();
            addAutoEnergy();
        }
        endRecipeProcessing();
        return true;
    }

    static {
        coolant = Arrays.asList(
            new PairA<>(MaterialsUEVplus.Time.getMolten(1000), 120),
            new PairA<>(FluidRegistry.getFluidStack("cryotheum", 1000), 80),
            new PairA<>(Materials.SuperCoolant.getFluid(1000), 60),
            new PairA<>(FluidRegistry.getFluidStack("ic2coolant", 1000), 40));
    }

    /**
     * Finds valid coolant from given inputs and consumes if found.
     *
     * @param input Fluid inputs.
     * @return Efficiency of the coolant. 100 if not found.
     */
    private int consumeCoolant(FluidStack[] input) {
        for (PairA<FluidStack, Integer> fluidPair : coolant) {
            FluidStack tFluid = fluidPair.getKey();
            for (FluidStack inFluid : input) {
                if (inFluid != null && inFluid.isFluidEqual(tFluid) && inFluid.amount >= tFluid.amount) {
                    inFluid.amount -= tFluid.amount;
                    return fluidPair.getValue();
                }
            }
        }
        return 20;
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick_EM(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    public FluidStack getPromoter() {
        return pipeTier != 0 ? liquids.PromoterUEV.getFluidOrGas(1) : liquids.PromoterZPM.getFluidOrGas(1);
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
        this.tEff = (int) (Math.exp(-coefficient * (double) aFuel / (double) aPromoter) * 1.5D * 10000);
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
        return structureCheck_EM(mName, 2, 2, 0);
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM(mName, 2, 2, 0, stackSize, hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivialBuildPiece(mName, stackSize, 2, 2, 0, elementBudget, env, false, true);
    }

    @Override
    public void stopMachine() {
        // Reset the counter for heating, so that it works again when the machine restarts
        heatingTicks = 0;
        super.stopMachine();
    }

    private boolean isStoppingSafe;
    private int heatingTicks;
    protected final int HEATING_TIMER = SECOND * 10;

    void addAutoEnergy() {
        long exEU = this.getPowerFlow() * tEff / 10000;
        if (!mDynamoHatches.isEmpty()) {
            MTEHatchDynamo tHatch = mDynamoHatches.get(0);
            if (tHatch.maxEUOutput() * tHatch.maxAmperesOut() >= exEU) {
                tHatch.setEUVar(
                    Math.min(
                        tHatch.maxEUStore(),
                        tHatch.getBaseMetaTileEntity()
                            .getStoredEU() + exEU));
            } else if (!isStoppingSafe) {
                stopMachine();
            }
        }
        if (!eDynamoMulti.isEmpty()) {
            MTEHatchDynamoMulti tHatch = eDynamoMulti.get(0);
            if (tHatch.maxEUOutput() * tHatch.maxAmperesOut() >= exEU) {
                tHatch.setEUVar(
                    Math.min(
                        tHatch.maxEUStore(),
                        tHatch.getBaseMetaTileEntity()
                            .getStoredEU() + exEU));
            } else if (!isStoppingSafe) {
                stopMachine();
            }
        }
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

    private static IStructureDefinition<OTENQFuelGeneratorUniversal> STRUCTURE_DEFINITION = null;

    public static int getPipeTier(Block block, int meta) {
        if (block == sBlockCasings9) {
            return 1;
        } else if (block == sBlockCasings2) {
            return 0;
        }
        return 0;
    }

    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("4 - 动力仓或者激光仓 : 替换防辐射硅岩机械方块, 支持TecTech动力仓") + ":",
        translateToLocal("3 - 输入仓输出仓 : 替换防辐射硅岩机械方块") + ":", translateToLocal("2 - 消声仓(装饰 : 替换防辐射硅岩机械方块)"), };

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
                    buildHatchAdder(OTENQFuelGeneratorUniversal.class).atLeast(Dynamo)
                        .adder(OTENQFuelGeneratorUniversal::addToMachineList)
                        .dot(4)
                        .casingIndex(((BlockCasings8) sBlockCasings8).getTextureIndex(10))
                        .buildAndChain(sBlockCasings8, 10))
                .addElement(
                    'P',
                    withChannel(
                        "pipe",
                        ofBlocksTiered(
                            OTENQFuelGeneratorUniversal::getPipeTier,
                            ImmutableList.of(Pair.of(sBlockCasings2, 15), Pair.of(sBlockCasings9, 14)),
                            0,
                            (t, meta) -> t.pipeTier = meta,
                            t -> t.pipeTier)))
                .addElement('C', ofBlock(sBlockCasings6, 8))
                .addElement('G', ofBlock(sBlockCasings2, 4))
                .addElement('I', ofBlock(sBlockCasings8, 4))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTENQFuelGeneratorUniversal(this.mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("通化之梦")
            .addInfo(" §l§4“你产能不够吧? -v-” ")
            .addInfo("通化的升级版本, 可以支持硅岩燃料, 但不能使用传统燃料喔 ~ 真正的清洁能源")
            .addInfo("和通化一样的设计, 但同时兼备了大硅岩的加成")
            .addInfo("一次消耗输入仓内所有燃料和助燃剂, 实际发电需乘上 配方耗时 (秒)")
            .addInfo("消耗冷却液提升效率, 默认效率为20%, 消耗速率为1000L/s")
            .addInfo("IC2冷却液:40%, 超级冷却液:60%, 极寒之凛冰:80%, 富快子时间流体: 120%")
            .addInfo("二级管道方块解锁无线模式, 使用螺丝刀开启")
            .addInfo("§a更高级的结构意味着更高级的§c助燃剂, §a一级结构:原子分离助燃剂, 二级结构:超维度等离子助燃剂")
            .addInfo("你知道会发生什么。")
            .addInfo("§e警告 : 如果强行使用有线模式烧高级硅岩燃料, 将会导致电表倒转憋憋")
            .addInfo("§c支持TecTech多安动力仓, 激光仓, 但尽量请使用1,073,741,824A激光仓直连电容库")
            .addSeparator()
            .addController("发电坤")
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
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
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
