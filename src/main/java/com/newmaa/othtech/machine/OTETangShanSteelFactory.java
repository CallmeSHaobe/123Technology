package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static com.newmaa.othtech.utils.Utils.NEGATIVE_ONE;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Chisel;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.annotation.Nonnull;

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

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.utils.Utils;

import crazypants.enderio.EnderIO;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.blocks.BlockCasings2;
import gregtech.common.items.ItemIntegratedCircuit;
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTETangShanSteelFactory extends OTHMultiMachineBase<OTETangShanSteelFactory> {

    public OTETangShanSteelFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTETangShanSteelFactory(String aName) {
        super(aName);
    }

    private byte mode = 0;
    public byte glassTier = 0;
    private UUID ownerUUID;
    private boolean isWirelessMode = false;
    private String costingWirelessEU = "0";

    private HeatingCoilLevel coilLevel;
    private int overclockParameter = 1;

    public HeatingCoilLevel getCoilLevel() {
        return this.coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    public int getCoilTier() {
        return Utils.getCoilTier(coilLevel);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("glassTier", glassTier);
        aNBT.setByte("mode", mode);
        aNBT.setBoolean("wireless", isWirelessMode);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        glassTier = aNBT.getByte("glassTier");
        mode = aNBT.getByte("mode");
        isWirelessMode = aNBT.getBoolean("wireless");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setString("costingWirelessEU", costingWirelessEU);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "当前无线EU消耗" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("costingWirelessEU")
                + EnumChatFormatting.RESET
                + " EU");
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        if (isWirelessMode) {
            // wireless mode ignore voltage limit
            logic.setAvailableVoltage(Long.MAX_VALUE);
            logic.setAvailableAmperage(1);
            logic.setAmperageOC(false);
        } else {
            super.setProcessingLogicPower(logic);
        }
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    protected float getSpeedBonus() {
        return (float) 1 / getCoilTier();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.TSSF;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setEuModifier(getEuModifier());
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mSpecialValue > coilLevel.getHeat()) {
                    return SimpleCheckRecipeResult.ofFailure("HeatErr");
                }
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                if (isWirelessMode) {
                    return OverclockCalculator.ofNoOverclock(recipe);
                } else {
                    return super.createOverclockCalculator(recipe);
                }
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (isWirelessMode == false) {
            isWirelessMode = true;
        } else {
            isWirelessMode = false;
        }
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal(isWirelessMode ? "无线模式启动" : "无线模式关闭"));
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        // inputs are consumed at this point
        updateSlots();
        if (!result.wasSuccessful()) return result;

        flushOverclockParameter();
        if (isWirelessMode) {
            BigInteger c = BigInteger.valueOf(overclockParameter);
            BigInteger costingWirelessEUTemp = BigInteger.valueOf(processingLogic.getCalculatedEut())
                .multiply(BigInteger.valueOf(processingLogic.getDuration()))
                .multiply(c.pow(2));
            costingWirelessEU = GTUtility.formatNumbers(costingWirelessEUTemp);
            if (!addEUToGlobalEnergyMap(ownerUUID, costingWirelessEUTemp.multiply(NEGATIVE_ONE))) {
                return CheckRecipeResultRegistry.insufficientPower(costingWirelessEUTemp.longValue());
            }

            // set progress time a fixed value
            mMaxProgresstime = 240 * 20 / ((int) Math.pow(overclockParameter, 2));
            mOutputItems = processingLogic.getOutputItems();
            mOutputFluids = processingLogic.getOutputFluids();
        } else {
            mMaxProgresstime = processingLogic.getDuration();
            mOutputItems = processingLogic.getOutputItems();
            mOutputFluids = processingLogic.getOutputFluids();
            lEUt = -processingLogic.getCalculatedEut();
        }
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        return result;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    private void flushOverclockParameter() {
        ItemStack items = getControllerSlot();
        if (items != null && items.getItem() instanceof ItemIntegratedCircuit && items.getItemDamage() > 0) {
            this.overclockParameter = items.getItemDamage();
        }
        if (items == null) {
            this.overclockParameter = 1;
        }
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        coilLevel = HeatingCoilLevel.None;
        return checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);

    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        repairMachine();
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);

    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        int realBudget = elementBudget >= 200 ? elementBudget : Math.min(200, elementBudget * 5);

        return this.survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            realBudget,
            env,
            false,
            true);

    }

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 2;
    private final int verticalOffSet = 38;
    private final int depthOffSet = 2;
    private static final int HORIZONTAL_DIRT_METAID = 10;
    private static final int HORIZONTAL_DIRT_METAID_A = 4;
    private static final int HORIZONTAL_DIRT_METAID_B = 0;
    private static final int HORIZONTAL_DIRT_METAID_C = 14;
    private static IStructureDefinition<OTETangShanSteelFactory> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 消声仓, 能源仓, 输入输出总线, 输入输出仓 : 替换脱氧钢机械方块, 支持TecTech能源仓") + ":",
        translateToLocal("2 - 消声仓 : 替换脱氧钢机械方块(烟囱上, 其实放主机旁边也行)") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTETangShanSteelFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTETangShanSteelFactory>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'A',
                    ofBlock(Objects.requireNonNull(Block.getBlockFromName(IndustrialCraft2.ID + ":blockAlloy")), 0))
                .addElement('B', ofBlock(sBlockCasings1, 11))
                .addElement(
                    'C',
                    buildHatchAdder(OTETangShanSteelFactory.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTETangShanSteelFactory::addToMachineList)
                        .dot(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement('D', ofBlock(sBlockCasings2, 11))
                .addElement('E', ofBlock(sBlockCasings2, 13))
                .addElement('F', ofBlock(sBlockCasings2, 15))
                .addElement('G', ofBlock(sBlockCasings4, 0))
                .addElement('H', ofBlock(sBlockCasings4, 1))
                .addElement('I', ofBlock(sBlockCasings4, 15))
                .addElement(
                    'J',
                    withChannel(
                        "coil",
                        ofCoil(OTETangShanSteelFactory::setCoilLevel, OTETangShanSteelFactory::getCoilLevel)))
                .addElement('K', ofBlock(sBlockMetal1, 1))
                .addElement('L', ofBlock(sBlockMetal6, 13))
                .addElement('M', ofBlock(sBlockMetal7, 11))
                .addElement('N', ofBlock(sBlockMetal7, 12))
                .addElement('O', ofBlock(ModBlocks.blockCasings2Misc, 11))
                .addElement('P', ofBlock(ModBlocks.blockCasingsMisc, 3))
                .addElement('Q', ofFrame(Materials.Aluminium))
                .addElement('R', ofBlock(EnderIO.blockIngotStorage, 6))
                .addElement('S', ofBlock(EnderIO.blockIngotStorage, 8))
                .addElement('T', ofFrame(Materials.StainlessSteel))
                .addElement(
                    'U',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null) ? ofBlock(
                        Objects.requireNonNull(Block.getBlockFromName(Chisel.ID + ":concrete")),
                        HORIZONTAL_DIRT_METAID_A) : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'V',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null) ? ofBlock(
                        Objects.requireNonNull(Block.getBlockFromName(Chisel.ID + ":concrete")),
                        HORIZONTAL_DIRT_METAID) : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'W',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null) ? ofBlock(
                        Objects.requireNonNull(Block.getBlockFromName(Chisel.ID + ":hempcrete")),
                        HORIZONTAL_DIRT_METAID_B) : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'X',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null) ? ofBlock(
                        Objects.requireNonNull(Block.getBlockFromName(Chisel.ID + ":hempcrete")),
                        HORIZONTAL_DIRT_METAID_C) : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'Y',
                    buildHatchAdder(OTETangShanSteelFactory.class).atLeast(Muffler)
                        .adder(OTETangShanSteelFactory::addToMachineList)
                        .dot(2)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement('Z', ofFrame(Materials.Steel))
                .addElement('1', ofBlock(sBlockCasings8, 4))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by LyeeR
    private final String[][] shapeMain = new String[][] {
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "      CCCCCCC                              ",
            "      Z  Z  Z                              ", "CCCCC Z  Z  Z                              ",
            "Z   Z Z  Z  Z                              ", "Z   Z Z  Z  Z                              ",
            "Z   Z Z  Z  Z                              ", "Z   Z Z  Z  Z                              ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "      CCCCCCC                              ",
            "                                           ", "CCCCC                                      ",
            "        HHH                                ", "        HHH                                ",
            "        HHH                                ", "                                           ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "      CCCCCCC                              ",
            "                                           ", "CCCCC   HHH                                ",
            "       H111H                               ", " CCC   H111H                               ",
            " C~C   H111H                               ", " CCC    HHH                                ",
            "UCCCUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "      CCCCCCC                              ",
            "                                           ", "CCCCC   HHH                                ",
            "       H   H                               ", " CCC   H   H                               ",
            " C C   H   H                               ", " CCC    HHH                                ",
            "UCCCUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "      CCCCCCC                              ",
            "                                           ", "CCCCC   HHH                                ",
            "       H   H                               ", " CCC   H   H                               ",
            " CCC   H   H                               ", " CCC    HHH                                ",
            "UCCCUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "IIIII                                      ", "IIIII                                      ",
            "IIIII                                      ", "IIIII CCCCCCC                              ",
            "IIIII                                      ", "ZIIIZ   HHH                                ",
            "ZIIIZ  H   H                               ", "ZIIIZ  H   H                               ",
            "ZIIIZ  H   H                               ", "ZIIIZ   HHH                                ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "IIIII                                      ", "ICCCI                                      ",
            "IPPPI                                      ", "IPPPI CCCCCCC                              ",
            "IPPPI                                      ", "EPPPE   HHH                                ",
            "EPPPE  H   H                               ", "EPPPE  H   H                               ",
            "EPPPE  H   H                               ", "EPPPE   HHH                                ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "IIIII                                      ", "ICCCI                                      ",
            "IPPPI                                      ", "IPPPI CCCCCCC                              ",
            "IPPPI Z     Z                              ", "EPPPE Z HHH Z                              ",
            "EPPPE ZH   HZ                              ", "EPPPE ZH   HZ                              ",
            "EPPPE ZH   HZ                              ", "EPPPE Z HHH Z                              ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "  M                                        ", "  R                                        ",
            "  X                                        ", "  W                                        ",
            "  X                                        ", "  W                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "IIIII                                      ", "IIIII                                      ",
            "IIIII                                      ", "IIIII CCCCCCC                              ",
            "IIIII                                      ", "EIIIE   HHH                                ",
            "ZIIII  H   H                               ", "ZIIII  H   H                               ",
            "ZIIII  H   H                               ", "ZIIII   HHH                                ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { " M M                                       ", " RYR                                       ",
            " X X                                       ", " W W                                       ",
            " X X                                       ", " W W                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            " L L                                       ", " L L                                       ",
            "IIIII                                      ", "ICCCI                                      ",
            "IPPPI                                      ", "IPPPI CCCCCCC                              ",
            "IPPPI                                      ", "EPPPE   HHH                                ",
            " PPPI  H   H                               ", " PPPEE H   H                               ",
            " PPPIZ H   H                               ", " PPPIZ  HHH                                ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "  M                                        ", "  R                                        ",
            "  X                                        ", "  W                                        ",
            "  X                                        ", "  W                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "  L                                        ", "  L                                        ",
            "IIIII                                      ", "ICCCI                                      ",
            "IPPPI                                      ", "IPPPI CCCCCCC                              ",
            "IPPPI                                      ", "EPPPE   HHH                                ",
            " PPPI  H   H                               ", " PPPIE H   H                               ",
            " PPPI  H   H                               ", " PPPI   HHH                                ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "IIIII                                      ", "IIIII                                      ",
            "IIIII                                      ", "IIIII CCCCCCC                              ",
            "IIIII                                      ", "EIIIE   HHH                                ",
            "ZIIII  H   H                               ", "ZIIIIE H   H                               ",
            "ZIIII  H   H                               ", "ZIIII   HHH                                ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", " CC                                        ",
            " PP                                        ", " PP   CCCCCCC                              ",
            " PP                                        ", "CPPE    HHH                                ",
            " PP    H   H                               ", " PP  E H   H                               ",
            " PP    H   H                               ", " PP     HHH                                ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", " CC                                        ",
            " PP                                        ", " PP   CCCCCCC                              ",
            " PP                                        ", "CPPE                                       ",
            " PP     HHH                                ", " PP  E  H H                                ",
            " PP     HHH                                ", " PP                                        ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "  DD                                       ", "  ZZDD                                     ",
            "  ZZ  DD                                   ", "  ZZ    DD                                 ",
            "  ZZ    Z DD                               ", "  ZZ    Z   DD                             ",
            "  ZZ    Z     DD                           ", "  ZZ    Z     Z DD                         ",
            "IIII    Z     Z   DD                       ", "IIII    Z     Z     DD                     ",
            "IIII    Z     Z     Z DD                   ", "IIII  CCCCCCC Z     Z   DD                 ",
            "IIII  Z Z   Z Z     Z     DD               ", "IIII  Z Z   Z Z     Z     Z DD             ",
            "IIII  Z ZH  Z Z     Z     Z   DD           ", "IIII EZ H H Z Z     Z     Z     DD         ",
            "IIII  Z ZH  Z Z     Z     Z     Z DD       ", "IIII  Z Z   Z Z     Z     Z     Z   DD     ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "  DD                                       ", "  Z DD                                     ",
            "  Z   DD                                   ", "  Z     DD                                 ",
            "  Z     Z DD                               ", "  Z     Z   DD                             ",
            "  Z     Z     DD                           ", "  Z     Z     Z DD                         ",
            "  Z     Z     Z   DD                       ", " CC     Z     Z     DD                     ",
            " PP     Z     Z     Z DD                   ", " PP     Z     Z     Z   DD                 ",
            " PP     Z     Z     Z     DD               ", "CPP     Z     Z     Z     Z DD             ",
            " PP     ZE    Z     Z     Z   DD           ", " PP  E  E E   Z     Z     Z     DD         ",
            " PP     ZE    Z     Z     Z     Z DD       ", " PP     ZZ    Z     Z     Z     Z   DD     ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "  DD                                       ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", " CC                                        ",
            " PP                                        ", " PP                                        ",
            " PP                                        ", "CPP                                        ",
            " PP      E                                 ", " PP  E  E E                                ",
            " PP      E                                 ", " PP                                        ",
            "UUUUUUUUUUUUUUVVVVVVVVVVVVVVVVVVVVVVVVVVVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "         E                                 ", "         E                                 ",
            "         E                                 ", "         E                                 ",
            "         E                                 ", "         E                                 ",
            "         E                                 ", "  DD     E                                 ",
            "         E                                 ", "         E                                 ",
            "         E                                 ", "         E                                 ",
            "         E                                 ", "         E                                 ",
            "         E                                 ", "         E                                 ",
            "IIII     E                                 ", "IIII     E                                 ",
            "IIII     E                                 ", "IIII     E                                 ",
            "IIII     E                                 ", "IIII     E                                 ",
            "IIII     E                                 ", "IIII E  E E                                ",
            "IIII     E                                 ", "IIII                                       ",
            "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "       E   E                               ",
            "       E   E                               ", "       EE EE                               ",
            "       EEEEE                               ", "       EE EE                               ",
            "        E E                                ", "        E E                                ",
            "        E E                                ", "        E E                                ",
            "  DD    E E                                ", "        E E                                ",
            "        E E                                ", "        E E                                ",
            "        E E                                ", "        E E                                ",
            "        E E                                ", "        E E                                ",
            "        E E                                ", "        E E                                ",
            "        E E                                ", " CC     E E                                ",
            " PP     E E                                ", " PP     E E                                ",
            " PP     E E                                ", "CPP     E E                                ",
            " PP     E E                                ", " PPEEEEEE E                                ",
            " PP  Z   E                                 ", " PP  Z                                     ",
            "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "       E   E                               ",
            "                                           ", "                                           ",
            "         E                                 ", "         E QKKQ                            ",
            "         E QTTQ                            ", "         E QTTQ                            ",
            "         E QTTQ                            ", "         E QTTQ                            ",
            "  DD     E QKKQ                            ", "         E QTTQ                            ",
            "         E QTTQ                            ", "         E QTTQ                            ",
            "         E QTTQ                            ", "         E QKKQ                            ",
            "         E QTTQ                            ", "         E QTTQ                            ",
            "         E QTTQ E                          ", "         E QTTQ E                          ",
            "         E QKKQ E                          ", " CC      E QTTQ E                          ",
            " PP      E QTTQ E                          ", " PP      E QTTQ E                          ",
            " PP      E QTTQ E                          ", "CPP      E QKKQ E                          ",
            " PP      E QTTQ E                          ", " PP      E QTTQ E                          ",
            " PP      E QTTQ E                          ", " PP      Z QKKQ EEEEEEEEEEEEEEEEEEEEEE     ",
            "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "       E   E                               ",
            "                                           ", "                                           ",
            "                                           ", "           QKKQ                            ",
            "           Q  Q                            ", "           Q  Q                            ",
            "           Q  Q                            ", "  DD       Q  Q                            ",
            "           QKKQ                            ", "           Q  Q                            ",
            "           Q  Q                            ", "           Q  Q                            ",
            "           Q  Q                            ", "           QKKQ                            ",
            "           Q  Q                            ", "           Q  Q                            ",
            "           Q  Q E                          ", "           Q  Q                            ",
            "IIII       QKKQ                            ", "IIII       Q  Q                            ",
            "IIII       Q  Q                            ", "IIII       Q  Q                            ",
            "IIII       Q  Q                            ", "IIII       QKKQ                            ",
            "IIII       Q  Q                            ", "IIII       Q  Q                            ",
            "IIII       Q  Q                            ", "IIII       QKKQ                            ",
            "UUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "    Z  E Z E  Z                            ",
            "    Z    Z    Z                            ", "    Z    Z    Z                            ",
            "    Z    Z    Z                            ", "    ZCCCCCCCCCZ                            ",
            "    Z    Z    Z                            ", "    Z    Z    Z                            ",
            "    Z    Z    Z                            ", "  DDZ    Z    Z                            ",
            "  ZZZCCCCCCCCCZ                            ", "  ZZZ    Z    Z                            ",
            "  ZZZ    Z    Z                            ", "  ZZZ    Z    Z                            ",
            "  ZZZ    Z    Z                            ", "  ZZZCCCCCCCCCZ                            ",
            "  ZZZ    Z    Z                            ", "  ZZZ    Z    Z                            ",
            "  ZZZ    Z    Z E                          ", "  ZZZ    Z    Z                            ",
            "  ZZZCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CCZZ    Z    ZLLLLZLLLLZLLLLZLLLLZLLLLZ   ",
            " PPZZ    Z    ZLLLLZLLLLZLLLLZLLLLZLLLLZ   ", " PPZZ    Z    ZLLLLZLLLLZLLLLZLLLLZLLLLZ   ",
            " PPZZ    Z    ZLLLLZLLLLZLLLLZLLLLZLLLLZ   ", "CPPCCCCCCCCCCCCCCCCZCCCCZCCCCZCCCCZCCCCC   ",
            " PP Z    Z    ZLLLLZLLLLZLLLLZLLLLZLLLLZ   ", " PP Z    Z    ZLLLLZLLLLZLLLLZLLLLZLLLLZ   ",
            " PP Z    Z    ZLLLLZLLLLZLLLLZLLLLZLLLLZ   ", " PP Z    Z    ZLLLLZLLLLZLLLLZLLLLZLLLLZ   ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "                                           ", "                                           ",
            "                                           ", "    CCCCCCCCCCC                            ",
            "                                           ", "     EEEEEEEEE                             ",
            "  DD         E                             ", "             E                             ",
            "    CCCCCCCCCCC                            ", "             E                             ",
            "     EEEEEEEEE                             ", "             E                             ",
            "             E                             ", "    CCCCCCCCCCC                            ",
            "             E                             ", "             E                             ",
            "             EEEE                          ", "             E                             ",
            "    CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC                                        ",
            " PP                                        ", " PP                                        ",
            " PP                                        ", "CPPCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ",
            " PP                                        ", " PP                                        ",
            " PP                                        ", " PP                                        ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "                                           ", "                                           ",
            "                                           ", "    CCCCCCCCCCC                            ",
            "                                           ", "     E       E                             ",
            "  DD                                       ", "                                           ",
            "    CCCCCCCCCCC                            ", "                                           ",
            "     E       E                             ", "                                           ",
            "                                           ", "    CCCCCCCCCCC                            ",
            "                                           ", "                                           ",
            "                E                          ", "        SSS                                ",
            "IIIICCCCSSSCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", "IIII    SSS                                ",
            "IIII    SSS                                ", "IIII    SSS                                ",
            "IIII    SSS                                ", "IIIICCCCSSSCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ",
            "IIII    SSS                                ", "IIII    SSS                                ",
            "IIII    CCC                                ", "IIII    CCC                                ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "       E   E                               ", "       E   E                               ",
            "       E   E                               ", "    CCCECCCECCC                            ",
            "       E   E                               ", "  DD E E   E E                             ",
            "       EE EE                               ", "       E   E                               ",
            "    CCCECCCECCC                            ", "       ESSSE                               ",
            "     E ESSSE E                             ", "       ESSSE                               ",
            "       ESSSE                               ", "    CCCESSSECCC                            ",
            "       ESSSE                               ", "       ESSSE                               ",
            "       ESSSE    E                          ", "       SJJJS                               ",
            "    CCCSJJJSCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC    SJJJS                               ",
            " PP    SJJJS                               ", " PP    SJJJS                               ",
            " PP    SJJJS                               ", "CPPCCCCSJJJSCCC                        C   ",
            " PP    SJJJS                               ", " PP    SJJJS                               ",
            " PP    CSSSC                               ", " PP    CSSSC                               ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "                                           ", "                                           ",
            "                                           ", "    CCCCSSSCCCC                            ",
            "        SSS                                ", "  DD E  SSS  E                             ",
            "       ESSSE                               ", "        SSS                                ",
            "    CCCCSSSCCCC                            ", "       S   S                               ",
            "     E S   S E                             ", "       S   S                               ",
            "       S   S                               ", "    CCCS   SCCC                            ",
            "       S   S                               ", "       S   S                               ",
            "       S   S    E                          ", "      SJ   JS                              ",
            "    CCSJ   JSCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC   SJ   JS                              ",
            " PP   SJ   JS                              ", " PP   SJ   JS                              ",
            " PP   SJ   JS                              ", "CPPCCCSJ   JSCC                        C   ",
            " PP   SJ   JS                              ", " PP   SJ   JS                              ",
            " PP   CSJJJSC                              ", " PP   CSSSSSC                              ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "    Z  E   E  Z                            ",
            "    Z         Z                            ", "    Z         Z                            ",
            "    Z   DD    Z                            ", "    CCCCSCSCCCCTT                          ",
            "  DDZ   S S   ZTT                          ", "    ZE  S S  EZTT                          ",
            "    Z   S S   ZTT                          ", "    Z   S S   ZTT                          ",
            "    ZCCCS SCCCCTT                          ", "    Z  S   S  ZTT                          ",
            "    ZE S   S EZTT                          ", "    Z  S   S  ZTT                          ",
            "    Z  S   S  ZTT                          ", "    CCCS   SCCCTT                          ",
            "    Z  S   S  ZTT                          ", "    Z  S   S  ZTT                          ",
            "    Z  S   S  ZTE                          ", "    Z SJ   JS ZEEEEEEEEEEEEEEEEEEEEEEEEE   ",
            "IIIICCSJ   JSCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", "IIIIZ SJ   JS Z                        Z   ",
            "IIIIZ SJ   JS Z                        Z   ", "IIIIZ SJ   JS Z                        Z   ",
            "IIIIZ SJ   JS Z                        Z   ", "IIIICCSJ   JSCC                        C   ",
            "IIIIZ SJ   JS Z                     E  Z   ", "IIIIZ SJ   JS Z                     E  Z   ",
            "IIIIZ CSJJJSC Z                     E  Z   ", "IIIIZ CSSSSSC Z                     E  Z   ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "                                           ", "                                           ",
            "        D                                  ", "    CCCCSSSCCCCLL                          ",
            "  DD    SSS     L                          ", "     E  SSS  E  L                          ",
            "       ESSSE    L                          ", "        SSS     L                          ",
            "    CCCCSSSCCCCLK                          ", "       S   S    L                          ",
            "     E S   S E  L                          ", "       S   S    L                          ",
            "       S   S    L                          ", "    CCCS   SCCCLL                          ",
            "       S   S    L                          ", "       S   S    L                          ",
            "       S   S    L                          ", "      SJ   JS LLL                          ",
            "    CCSJ   JSCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC   SJ   JS  DDDDDDDDDDDDDDDDDDDDDDD     ",
            " PP   SJ   JS                              ", " PP   SJ   JS                              ",
            " PP   SJ   JS                              ", "CPPCCCSJ   JSCC                        C   ",
            " PP   SJ   JS                       E      ", " PP   SJ   JS              GGGGGGGGGG      ",
            " PP   CSJJJSC              GGGGGGGGGG      ", " PP   CSSSSSC              GGGGGGGGGG      ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E F E                               ",
            "       E F E                               ", "       E F E                               ",
            "       EDF E                               ", "  DDCCCECCCECCCLL                          ",
            "       E   E    L                          ", "     E E   E E  L                          ",
            "       EE EE    L                          ", "       E   E    K                          ",
            "    CCCECCCECCCLL                          ", "       ESSSE    K                          ",
            "     E ESSSE E  L                          ", "       ESSSE    L                          ",
            "       ESSSE    L                          ", "    CCCESSSECCCLL                          ",
            "       ESSSE    L                          ", "       ESSSE    L                          ",
            "       ESSSE    L                          ", "       SJJJS  LLL                          ",
            "    CCCSJJJSCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC    SJJJS      G                        ",
            " PP    SJJJS      G                        ", " PP    SJJJS      G                        ",
            " PP    SJJJS      D                        ", "CPPCCCCSJJJSCCC                        C   ",
            " PP    SJJJS                        E      ", " PP    SJJJS               GGGGGGGGGG      ",
            " PP    CSSSC                               ", " PP    CSSSC                               ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "    Z  E   E                               ",
            "    Z                                      ", "    Z                                      ",
            "    Z   D                                  ", "  DDCCCCCCCCCCCLL                          ",
            "  ZZ            L                          ", "  ZZ E       E  L                          ",
            "  ZZ            L                          ", "  ZZ            L                          ",
            "  ZZCCCCCCCCCCCLL                          ", "  ZZ            L                          ",
            "  ZZ E       E  K                          ", "  ZZ            L                          ",
            "  ZZ            L                          ", "  ZZCCCCCCCCCCCLL                          ",
            "  ZZ            L                          ", "  ZZ            L                          ",
            "  ZZ            L                          ", "  ZZ    SSS   LLL                          ",
            "IIIICCCCSSSCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", "IIII    SSS    DDDDDDDDDDDDDDDDDDDDDDD     ",
            "IIII    SSS                                ", "IIII    SSS                                ",
            "IIII    SSS      BBB                       ", "IIIICCCCSSSCCCC  BBB                   C   ",
            "IIII    SSS      BBB       EEEEEEEEEE      ", "IIII    SSS      BBB       GGGGGGGGGG      ",
            "IIII    CCC                                ", "IIII    CCC          OOOOOOOOOOOOOOOO      ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "                                           ", "                                           ",
            "  DD    D                                  ", "    CCCCCCCCCCCLL                          ",
            "                L                          ", "     E       E  L                          ",
            "                L                          ", "                N                          ",
            "    CCCCCCCCCCCLN                          ", "                N                          ",
            "     E       E  K                          ", "                L                          ",
            "                L                          ", "    CCCCCCCCCCCLL                          ",
            "         E      L                          ", "         E      L                          ",
            "         E      L                          ", "         E    LLL                          ",
            "    CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC                                        ",
            " PP                                        ", " PP                                        ",
            " PP             B   BB                     ", "CPPCCCCCCCCCCCC B   B                  C   ",
            " PP             B   B               E      ", " PP             B   B      GGGGGGGGGG      ",
            " PP              BBB                       ", " PP                  O              O      ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E Z E  Z                            ",
            "         Z    Z                            ", "         Z    Z                            ",
            "  DDDDDDDZ    Z                            ", "    CCCCCCCCCCCLL                          ",
            "    Z    Z    Z L                          ", "    ZE   Z   EZ L                          ",
            "    Z    Z    Z N                          ", "    Z    Z    Z K                          ",
            "    CCCCCCCCCCCLL                          ", "    Z    Z    Z K                          ",
            "    ZE   Z   EZ N                          ", "    Z    Z    Z L                          ",
            "    Z    Z    Z L                          ", "    CCCCCCCCCCCLL                          ",
            "    Z    Z    Z L                          ", "    Z    Z    Z L                          ",
            "    Z    Z    Z L                          ", "    Z    EEEEEELLEEEEEEEEEEEEEEEEEEEEEEE   ",
            "    CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC Z    Z    Z                        Z   ",
            " PP Z    Z    Z                        Z   ", " PP Z    Z    Z                        Z   ",
            " PP Z    Z    Z B                      Z   ", "CPPCCCCCCCCCCCC B   BB                 C   ",
            " PP Z    Z    Z B   B      EEEEEEEEEE      ", " PP Z    Z    Z B   B      GGGGGGGGGG      ",
            " PP Z    Z    Z  BBB                       ", " PP Z    Z    Z      O              O      ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "                                           ", "                                           ",
            "        D                                  ", "    CCCCCCCCCCCLL                          ",
            "                L                          ", "     E       E  L                          ",
            "                K                          ", "                N                          ",
            "    CCCCCCCCCCCLN                          ", "                N                          ",
            "     E       E  L                          ", "                L                          ",
            "                L                          ", "    CCCCCCCCCCCLL                          ",
            "         E      L                          ", "         E      L                          ",
            "         E      L                          ", "         E    LLL                          ",
            "IIIICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", "IIII                                       ",
            "IIII                                       ", "IIII                                       ",
            "IIII            B   BB                     ", "IIIICCCCCCCCCCC B   B                  C   ",
            "IIII            B   B               E      ", "IIII            B   B      GGGGGGGGGG      ",
            "IIII             BBB                       ", "IIII                 O              O      ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "    Z  E   E                               ",
            "    Z                                      ", "    Z                                      ",
            "    Z   D                                  ", "    CCCCCCCCCCCLL                          ",
            "                L                          ", "     E       E  L                          ",
            "                K                          ", "                L                          ",
            "    CCCCCCCCCCCLL                          ", "                L                          ",
            "     E       E  L                          ", "                L                          ",
            "                L                          ", "    CCCCCCCCCCCLL                          ",
            "                L                          ", "                L                          ",
            "                L                          ", "        SSS   LLL                          ",
            "    CCCCSSSCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC     SSS    DDDDDDDDDDDDDDDDDDDDDDD     ",
            " PP     SSS                                ", " PP     SSS                                ",
            " PP     SSS      BBB                       ", "CPPCCCCCSSSCCCC  BBB                   C   ",
            " PP     SSS      BBB       EEEEEEEEEE      ", " PP     SSS      BBB       GGGGGGGGGG      ",
            " PP     CCC                                ", " PP     CCC          OOOOOOOOOOOOOOOO      ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "       E   E                               ", "       E   E                               ",
            "       ED  E                               ", "    CCCECCCECCCLL                          ",
            "       E   E    L                          ", "     E E   E E  L                          ",
            "       EE EE    L                          ", "       E   E    K                          ",
            "    CCCECCCECCCLL                          ", "       ESSSE    K                          ",
            "     E ESSSE E  L                          ", "       ESSSE    L                          ",
            "       ESSSE    L                          ", "    CCCESSSECCCLL                          ",
            "       ESSSE    L                          ", "       ESSSE    L                          ",
            "       ESSSE    L                          ", "       SJJJS  LLL                          ",
            "    CCCSJJJSCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC    SJJJS      G                        ",
            " PP    SJJJS      G                        ", " PP    SJJJS      G                        ",
            " PP    SJJJS      D                        ", "CPPCCCCSJJJSCCC                        C   ",
            " PP    SJJJS                        E      ", " PP    SJJJS               GGGGGGGGGG      ",
            " PP    CSSSC                               ", " PP    CSSSC                               ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "                                           ", "                                           ",
            "        D                                  ", "    CCCCSSSCCCCLL                          ",
            "        SSS     L                          ", "     E  SSS  E  L                          ",
            "       ESSSE    L                          ", "        SSS     L                          ",
            "    CCCCSSSCCCCLK                          ", "       S   S    L                          ",
            "     E S   S E  L                          ", "       S   S    L                          ",
            "       S   S    L                          ", "    CCCS   SCCCLL                          ",
            "       S   S    L                          ", "       S   S    L                          ",
            "       S   S    L                          ", "      SJ   JS LLL                          ",
            "IIIICCSJ   JSCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", "IIII  SJ   JS  DDDDDDDDDDDDDDDDDDDDDDD     ",
            "IIII  SJ   JS                              ", "IIII  SJ   JS                              ",
            "IIII  SJ   JS                              ", "IIIICCSJ   JSCC                        C   ",
            "IIII  SJ   JS                       E      ", "IIII  SJ   JS              GGGGGGGGGG      ",
            "IIII  CSJJJSC              GGGGGGGGGG      ", "IIII  CSSSSSC              GGGGGGGGGG      ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "    Z  E   E  Z                            ",
            "    Z         Z                            ", "    Z         Z                            ",
            "    Z   DD    Z                            ", "    CCCCSCSCCCCTT                          ",
            "    Z   S S   ZTT                          ", "    ZE  S S  EZTT                          ",
            "    Z   S S   ZTT                          ", "    Z   S S   ZTT                          ",
            "    ZCCCS SCCCCTT                          ", "    Z  S   S  ZTT                          ",
            "    ZE S   S EZTT                          ", "    Z  S   S  ZTT                          ",
            "    Z  S   S  ZTT                          ", "    CCCS   SCCCTT                          ",
            "    Z  S   S  ZTT                          ", "    Z  S   S  ZTT                          ",
            "    Z  S   S  ZTT                          ", "    Z SJ   JS ZEEEEEEEEEEEEEEEEEEEEEEEEE   ",
            "    CCSJ   JSCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC Z SJ   JS Z                        Z   ",
            " PP Z SJ   JS Z                        Z   ", " PP Z SJ   JS Z                        Z   ",
            " PP Z SJ   JS Z                        Z   ", "CPPCCCSJ   JSCC                        C   ",
            " PP Z SJ   JS Z                     E  Z   ", " PP Z SJ   JS Z                     E  Z   ",
            " PP Z CSJJJSC Z                     E  Z   ", " PP Z CSSSSSC Z                     E  Z   ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E   E                               ",
            "                                           ", "                                           ",
            "                                           ", "    CCCCSSSCCCC                            ",
            "        SSS                                ", "     E  SSS  E                             ",
            "       ESSSE                               ", "        SSS                                ",
            "    CCCCSSSCCCC                            ", "       S   S                               ",
            "     E S   S E                             ", "       S   S                               ",
            "       S   S                               ", "    CCCS   SCCC                            ",
            "       S   S                               ", "       S   S                               ",
            "       S   S                               ", "      SJ   JS                              ",
            "    CCSJ   JSCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC   SJ   JS                              ",
            " PP   SJ   JS                              ", " PP   SJ   JS                              ",
            " PP   SJ   JS                              ", "CPPCCCSJ   JSCC                        C   ",
            " PP   SJ   JS                              ", " PP   SJ   JS                              ",
            " PP   CSJJJSC                              ", " PP   CSSSSSC                              ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "       E F E                               ",
            "       E F E                               ", "       E F E                               ",
            "       E F E                               ", "    CCCECCCECCC                            ",
            "       E   E                               ", "     E E   E E                             ",
            "       EE EE                               ", "       E   E                               ",
            "    CCCECCCECCC                            ", "       ESSSE                               ",
            "     E ESSSE E                             ", "       ESSSE                               ",
            "       ESSSE                               ", "    CCCESSSECCC                            ",
            "       ESSSE                               ", "       ESSSE                               ",
            "       ESSSE                               ", "       SJJJS                               ",
            "IIIICCCSJJJSCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", "IIII   SJJJS                               ",
            "IIII   SJJJS                               ", "IIII   SJJJS                               ",
            "IIII   SJJJS                               ", "IIIICCCSJJJSCCC                        C   ",
            "IIII   SJJJS                               ", "IIII   SJJJS                               ",
            "IIII   CSSSC                               ", "IIII   CSSSC                               ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "    CCCCCCCCCCC                            ",
            "                                           ", "     E       E                             ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "                                           ",
            "     E       E                             ", "                                           ",
            "                                           ", "    CCCCCCCCCCC                            ",
            "                                           ", "                                           ",
            "                                           ", "        SSS                                ",
            "    CCCCSSSCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC     SSS                                ",
            " PP     SSS                                ", " PP     SSS                                ",
            " PP     SSS                                ", "CPPCCCCCSSSCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ",
            " PP     SSS                                ", " PP     SSS                                ",
            " PP     CCC                                ", " PP     CCC                                ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "    CCCCCCCCCCC                            ",
            "                                           ", "     EEEEEEEEE                             ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "                                           ",
            "     EEEEEEEEE                             ", "                                           ",
            "                                           ", "    CCCCCCCCCCC                            ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ", " CC                                        ",
            " PP                                        ", " PP                                        ",
            " PP                                        ", "CPPCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC   ",
            " PP                                        ", " PP                                        ",
            " PP                                        ", " PP                                        ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" },
        { "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "                                           ", "                                           ",
            "    CCCCCCCCCCC                            ", "    Z    Z    Z                            ",
            "    Z    Z    Z                            ", "    Z    Z    Z                            ",
            "    Z    Z    Z                            ", "    ZCCCCCCCCCZ                            ",
            "    Z    Z    Z                            ", "    Z    Z    Z                            ",
            "    Z    Z    Z                            ", "    Z    Z    Z                            ",
            "    ZCCCCCCCCCZ                            ", "    Z    Z    Z                            ",
            "    Z    Z    Z                            ", "    Z    Z    Z                            ",
            "    Z    Z    Z                            ", "    ZCCCCCCCCCZ                            ",
            "    Z    Z    Z                            ", "    Z    Z    Z                            ",
            "    Z    Z    Z                            ", "    Z    Z    Z                            ",
            "IIIIZCCCCZCCCCZCCCCCCCCCCCCCCCCCCCCCCCCC   ", "IIIIZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZ   ",
            "IIIIZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZ   ", "IIIIZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZ   ",
            "IIIIZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZ   ", "IIIIZCCCCZCCCCZCCCCZCCCCZCCCCZCCCCZCCCCZ   ",
            "IIIIZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZ   ", "IIIIZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZ   ",
            "IIIIZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZ   ", "IIIIZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZLLLLZ   ",
            "VVVVAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAVVV" } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 19200;
    }

    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType("§e§l重工业计划 - 唐山炼钢厂")
     * .addInfo("§l§8无数黑烟源源不断地从烟囱中冒出")
     * .addInfo("§l§8工业化的必由之路......")
     * .addInfo("§l§8一步到位各种与钢铁相关之合金 : GTPP / GT5U, 以及部分BW")
     * .addInfo("正常耗时倍率 : 1 / 线圈等级")
     * .addInfo("无线耗时 = 240s / 编程电路编号 ^ 2)")
     * .addInfo("无线EU消耗 = 功率 * 耗时 * 编程电路编号 ^ 2")
     * .addInfo("请注意炉温要求.")
     * .addInfo("螺丝刀切换无线模式")
     * .addInfo("非无线模式执行无损超频")
     * .addInfo("§c§l注意:机器污染过高:如遇跳电并报错“无法排出污染”, 请尝试放置多个消声仓")
     * .addTecTechHatchInfo()
     * .addPollutionAmount(19200)
     * .addSeparator()
     * .addController("钢铁厂")
     * .beginStructureBlock(42, 41, 43, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .toolTipFinisher("§a123Technology - Heavy industry - SteelFactory");
     * return tt;
     * }
     */
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.ts.0"))
            .addInfo(translateToLocal("ote.tm.ts.1"))
            .addInfo(translateToLocal("ote.tm.ts.2"))
            .addInfo(translateToLocal("ote.tm.ts.3"))
            .addInfo(translateToLocal("ote.tm.ts.4"))
            .addInfo(translateToLocal("ote.tm.ts.5"))
            .addInfo(translateToLocal("ote.tm.ts.6"))
            .addInfo(translateToLocal("ote.tm.ts.7"))
            .addInfo(translateToLocal("ote.tm.ts.8"))
            .addInfo(translateToLocal("ote.tm.ts.9"))
            .addTecTechHatchInfo()
            .addPollutionAmount(19200)
            .addSeparator()
            .addController(translateToLocal("ote.tn.ts"))
            .beginStructureBlock(42, 41, 43, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - Heavy industry - SteelFactory");
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
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTETangShanSteelFactory(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
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
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }
}
