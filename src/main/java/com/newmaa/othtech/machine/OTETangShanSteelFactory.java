package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static com.newmaa.othtech.utils.Utils.NEGATIVE_ONE;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Chisel;
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
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
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
import gregtech.api.structure.error.StructureError;
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

    private byte mode = 0;

    public byte glassTier = 0;

    private UUID ownerUUID;

    private boolean isWirelessMode = false;

    private String costingWirelessEU = "0";

    private HeatingCoilLevel coilLevel;

    private int overclockParameter = 1;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 2;

    private final int verticalOffSet = 38;

    private final int depthOffSet = 2;

    private static final int HORIZONTAL_DIRT_METAID = 10;

    private static final int HORIZONTAL_DIRT_METAID_A = 4;

    private static final int HORIZONTAL_DIRT_METAID_B = 0;

    private static final int HORIZONTAL_DIRT_METAID_C = 14;

    private static IStructureDefinition<OTETangShanSteelFactory> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.ts.0") + ":",
        translateToLocal("ote.cm.ts.1") };

    // Structured by LyeeR
    private final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTETangShanSteelFactory_shapeMain.shape.gz");

    // ===================== Constructors =====================

    public OTETangShanSteelFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTETangShanSteelFactory(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

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

    // ===================== Machine Parameters =====================

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
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 19200;
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

    public HeatingCoilLevel getCoilLevel() {
        return this.coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    public int getCoilTier() {
        return Utils.getCoilTier(coilLevel);
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new OTHProcessingLogic() {

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
                // 结构尚未检查过时 coilLevel 可能为 null，按“无线圈”处理，避免 NPE
                HeatingCoilLevel coil = coilLevel == null ? HeatingCoilLevel.None : coilLevel;
                if (recipe.mSpecialValue > coil.getHeat()) {
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
            costingWirelessEU = GTUtility.scientificFormat(costingWirelessEUTemp);
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

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        coilLevel = HeatingCoilLevel.None;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet, errors)) return;
        repairMachine();
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

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTETangShanSteelFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTETangShanSteelFactory>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', ofBlock(sBlockReinforced, 13))
                .addElement('B', ofBlock(sBlockCasings1, 11))
                .addElement(
                    'C',
                    buildHatchAdder(OTETangShanSteelFactory.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTETangShanSteelFactory::addToMachineList)
                        .hint(1)
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
                        .hint(2)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement('Z', ofFrame(Materials.Steel))
                .addElement('1', ofBlock(sBlockCasings8, 4))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // ===================== Tooltip =====================

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
            .addPollutionAmount(19200)
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - Heavy industry - SteelFactory")
            .addController(translateToLocal("ote.tn.ts"))
            .beginStructureBlock(43, 41, 42, false)
            .addInputBus(translateToLocal("oth.hint.any_input_bus"), 1)
            .addOutputBus(translateToLocal("oth.hint.any_output_bus"), 1)
            .addInputHatch(translateToLocal("oth.hint.any_input_hatch"), 1)
            .addOutputHatch(translateToLocal("oth.hint.any_output_hatch"), 1)
            .addEnergyHatch(translateToLocal("oth.hint.any_energy_hatch"), 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTETangShanSteelFactory(this.mName);
    }

    // ===================== Textures =====================

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

    // ===================== Misc =====================

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
            translateToLocal("otht.waila.gen.wireless") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("costingWirelessEU")
                + EnumChatFormatting.RESET
                + " EU");
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (!isWirelessMode) {
            isWirelessMode = true;
        } else {
            isWirelessMode = false;
        }
        if (isWirelessMode) {
            aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.nfg.mode.0"));
        } else {
            aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.nfg.mode.1"));
        }
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
}
