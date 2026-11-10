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
import static gregtech.api.util.GTUtility.filterValidMTEs;
import static gregtech.api.util.GTUtility.validMTEList;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.metaTileEntity.multi.base.TTMultiblockBase.HatchElement.DynamoMulti;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.maps.FuelBackend;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
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

    private int pipeTier = -1;

    private UUID ownerUUID;

    private boolean isWirelessMode = false;

    private String costingWirelessEU = "0";

    Parameters.Group.ParameterIn time;

    private static final IStatusFunction<OTENQFuelGeneratorUniversal> timeSTATUES = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 1, 2, 1000, 114514);

    private static final INameFunction<OTENQFuelGeneratorUniversal> timeName = (base, p) -> GCCoreUtil
        .translate("ote.tt.exc.0");

    private long tEff;

    public long running_time = 0;

    protected final List<MTEHatchInput> mMiddleInputHatches = new ArrayList<>();

    private boolean isStoppingSafe;

    private int heatingTicks;

    protected final int HEATING_TIMER = SECOND * 10;

    private static IStructureDefinition<OTENQFuelGeneratorUniversal> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.nfg.0") + ":",
        translateToLocal("ote.cm.nfg.1") + ":", translateToLocal("ote.cm.nfg.2"), };

    // ===================== Constructors =====================

    public OTENQFuelGeneratorUniversal(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        super.useLongPower = true;
    }

    public OTENQFuelGeneratorUniversal(String aName) {
        super(aName);
        super.useLongPower = true;
    }

    // ===================== NBT =====================

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

    // ===================== Machine Parameters =====================

    @Override
    public RecipeMap<?> getRecipeMap() {
        return NaquadahFuelFakeRecipes;
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
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
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

    // ===================== Processing Logic =====================

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

    @Override
    public boolean onRunningTick(ItemStack stack) {
        if (this.getBaseMetaTileEntity() != null && this.getBaseMetaTileEntity()
            .isServerSide()) {
            long euPerTick = getPowerFlow();
            if (this.getBaseMetaTileEntity()
                .isActive() && !isWirelessMode
                && euPerTick > 0) {
                addEnergyOutput_EM(euPerTick, 1);
            }
            if (heatingTicks < HEATING_TIMER) {
                heatingTicks++;
                isStoppingSafe = true;
            } else if (isStoppingSafe) isStoppingSafe = false;
        }
        return true;
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick_EM(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    protected void parametersInstantiation_EM() {
        Parameters.Group hatch_0 = parametrization.getGroup(0, false);
        time = hatch_0.makeInParameter(0, 1, timeName, timeSTATUES);
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        for (final MTEHatchInput tHatch : validMTEList(mInputHatches)) {
            if (tHatch instanceof MTEHatchInputME) {
                return;
            }
        }
        if (this.pipeTier != 2) {
            this.isWirelessMode = false;
        }
        checkPiece(mName, 2, 2, 0, errors);
        repairMachine();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        repairMachine();
        buildPiece(mName, stackSize, hintsOnly, 2, 2, 0);

    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(mName, stackSize, 2, 2, 0, elementBudget, env, false, true);
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
                        .hint(2)
                        .casingIndex(((BlockCasings8) sBlockCasings8).getTextureIndex(10))
                        .buildAndChain(sBlockCasings8, 10))
                .addElement(
                    'S',
                    buildHatchAdder(OTENQFuelGeneratorUniversal.class).atLeast(InputHatch, OutputHatch)
                        .adder(OTENQFuelGeneratorUniversal::addToMachineList)
                        .hint(3)
                        .casingIndex(((BlockCasings8) sBlockCasings8).getTextureIndex(10))
                        .buildAndChain(sBlockCasings8, 10))
                .addElement(
                    'E',
                    buildHatchAdder(OTENQFuelGeneratorUniversal.class).atLeast(Dynamo.or(DynamoMulti))
                        .adder(OTENQFuelGeneratorUniversal::addToMachineList)
                        .hint(4)
                        .casingIndex(((BlockCasings8) sBlockCasings8).getTextureIndex(10))
                        .buildAndChain(sBlockCasings8, 10))
                .addElement('P', withChannel("pipe", ofBlocksTiered((block, meta) -> {
                    if (block == null) return null;
                    if (block == sBlockCasings2 && meta == 15) {
                        return 1;
                    } else if (block == sBlockCasings9 && meta == 14) {
                        return 2;
                    }
                    return null;
                },
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

    // ===================== Tooltip =====================

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
            .addInfo("§bAdded by: 123Technology - KING OF GENERATORS , FOR THE GOD PIGEON")
            .addController(translateToLocal("ote.tm.nfg.16"))
            .beginStructureBlock(6, 4, 9, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTENQFuelGeneratorUniversal(this.mName);
    }

    // ===================== Textures =====================

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

    // ===================== Misc =====================

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String bonus = GTUtility.formatShortenedLong(getPowerFlow());
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
                EnumChatFormatting.BOLD + translateToLocal("otht.waila.gen.requirefluid")
                    + EnumChatFormatting.RESET
                    + ": "
                    + EnumChatFormatting.AQUA
                    + translateToLocal("ote.fluid.gen.1")
                    + EnumChatFormatting.RESET);
        } else if (tag.getString("pipe")
            .equals("2")) {
                currentTip.add(
                    EnumChatFormatting.BOLD + translateToLocal("otht.waila.gen.requirefluid")
                        + EnumChatFormatting.RESET
                        + ": "
                        + EnumChatFormatting.AQUA
                        + translateToLocal("ote.fluid.gen.2")
                        + EnumChatFormatting.RESET);
            }
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
                BigInteger costingWirelessEUTemp = BigDecimal.valueOf(recipe.mSpecialValue)
                    .multiply(BigDecimal.valueOf(FuelsValueBonus))
                    .multiply(BigDecimal.valueOf(FuelAmount))
                    .multiply(BigDecimal.valueOf(tEff))
                    .multiply(BigDecimal.valueOf(recipe.mDuration))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN)
                    .toBigInteger();
                costingWirelessEU = GTUtility.scientificFormat(costingWirelessEUTemp);
                if (!addEUToGlobalEnergyMap(ownerUUID, costingWirelessEUTemp)) {
                    return CheckRecipeResultRegistry.INTERNAL_ERROR;
                }
            } else {
                costingWirelessEU = "0";
                // Bug fix: cast FuelAmount to double first to avoid long overflow when
                // FuelAmount * recipe.mSpecialValue exceeds Long.MAX_VALUE for high-tier fuels.
                this.setPowerFlow(
                    (long) Math.min(
                        Long.MAX_VALUE - 1,
                        (double) FuelAmount * recipe.mSpecialValue
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
        this.setPowerFlow(0);
        return CheckRecipeResultRegistry.NO_FUEL_FOUND;
    }

    @Override
    public boolean addEnergyOutput_EM(long EU, long Amperes) {
        return addEnergyOutput_EM(EU, Amperes, true);
    }

    public boolean addEnergyOutput_EM(long EU, long Amperes, boolean allowMixedVoltages) {
        if (EU < 0) {
            EU = -EU;
        }
        if (Amperes < 0) {
            Amperes = -Amperes;
        }
        long euVar = EU * Amperes;
        long diff;
        for (MTEHatchDynamo tHatch : filterValidMTEs(mDynamoHatches)) {
            if (tHatch.maxEUOutput() < euVar && !allowMixedVoltages) {
                explodeMultiblock();
            }
            diff = tHatch.maxEUStore() - tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            if (diff > 0) {
                if (euVar <= diff) {
                    tHatch.setEUVar(
                        tHatch.getBaseMetaTileEntity()
                            .getStoredEU() + euVar);
                    return true;
                }
                tHatch.setEUVar(tHatch.maxEUStore());
                euVar -= diff;
            }
        }
        for (MTEHatchDynamoMulti tHatch : filterValidMTEs(eDynamoMulti)) {
            if (tHatch.maxEUOutput() < euVar && !allowMixedVoltages) {
                explodeMultiblock();
            }
            long maxStore = tHatch.maxEUStore();
            long storedEU = tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            diff = maxStore - storedEU;
            if (diff > 0) {
                if (euVar <= diff) {
                    tHatch.setEUVar(
                        tHatch.getBaseMetaTileEntity()
                            .getStoredEU() + euVar);
                    return true;
                }
                tHatch.setEUVar(tHatch.maxEUStore());
                euVar -= diff;
            }
        }
        return false;
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

    public FluidStack getPromoter() {
        if (this.pipeTier == 1) { // T1
            return BWLiquids.PromoterZPM.getFluidOrGas(1);
        } else if (this.pipeTier >= 2) { // T2
            return BWLiquids.PromoterUEV.getFluidOrGas(1);
        } else {
            return BWLiquids.PromoterZPM.getFluidOrGas(1); // fallback
        }
    }

    public int getPipeTierInstance() {
        return this.pipeTier;
    }

    public int getPipeTier() {
        return this.pipeTier;
    }// 可以没用但是不能没有,lol

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        isWirelessMode = this.pipeTier == 2 && !isWirelessMode;
        if (isWirelessMode) {
            aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.nfg.mode.0"));
        } else {
            aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.nfg.mode.1"));
        }
    }
}
