package com.newmaa.othtech.machine;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.newmaa.othtech.Config.is_Enqing_Song_Play;
import static com.newmaa.othtech.utils.Utils.metaItemEqual;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_OFF;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.StabilisationFieldGenerators;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.gui.OTESunFactoryGui;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.machine.machineclass.OTHSoundResource;

import bartworks.API.BorosilicateGlass;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.shutdown.ShutDownReason;
import gregtech.api.util.shutdown.ShutDownReasonRegistry;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.items.MetaGeneratedItem03;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.block.BlockQuantumGlass;

public class OTESunFactory extends OTHMultiMachineBase<OTESunFactory> {

    private int stabilisationFieldMetadata = 0;

    private byte glassTier = 0;

    private double Beeyonds = 0;

    private String BYDS = "0";

    private static final UITexture LEGACY_MODE_ICON_PCB = UITexture
        .fullImage("123technology", "gui/overlay_button/sunfactory_mode_pcb");

    private static final UITexture LEGACY_MODE_ICON_SMD = UITexture
        .fullImage("123technology", "gui/overlay_button/sunfactory_mode_smd");

    private static final UITexture LEGACY_MODE_ICON_WAFER = UITexture
        .fullImage("123technology", "gui/overlay_button/sunfactory_mode_wafer");

    private static final UITexture LEGACY_MODE_ICON_COMPONENT = UITexture
        .fullImage("123technology", "gui/overlay_button/sunfactory_mode_component");

    private static ItemStack itemEnqingM = null;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 31;

    private final int verticalOffSet = 27;

    private final int depthOffSet = 51;

    private static IStructureDefinition<OTESunFactory> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.sf.0") };

    // Structured by JulianChum Version 1 & 2
    private final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTESunFactory_shapeMain.shape.gz");

    // ===================== Constructors =====================

    public OTESunFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTESunFactory(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        // machineMode is already persisted by MTEMultiBlockBase#saveNBTData ("machineMode") whenever
        // supportsMachineModeSwitch() is true, so a second "mode" key was only a chance for the two to diverge.
        aNBT.setByte("glassTier", glassTier);
        aNBT.setDouble("speedBonus", Beeyonds);
        aNBT.setInteger("sfg", stabilisationFieldMetadata);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        // Saves written before the controller had modes only carried the legacy "mode" key.
        if (!aNBT.hasKey("machineMode") && aNBT.hasKey("mode")) {
            machineMode = aNBT.getInteger("mode");
        }
        glassTier = aNBT.getByte("glassTier");
        Beeyonds = aNBT.getDouble("speedBonus");
        stabilisationFieldMetadata = aNBT.getInteger("sfg");

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
        getBeeyonds();
        return (float) (1 / (1 + Beeyonds));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (machineMode == 1) return Recipemaps.SF2;
        if (machineMode == 2) return Recipemaps.SF3;
        if (machineMode == 3) return Recipemaps.SF4;
        return Recipemaps.SF1;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(Recipemaps.SF1, Recipemaps.SF2, Recipemaps.SF3, Recipemaps.SF4);
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

    @Override
    public int totalMachineMode() {
        return 4;
    }

    @Override
    public String getMachineModeName(int mode) {
        return switch (mode) {
            case 0 -> translateToLocal("ote.sf.mode.1");
            case 1 -> translateToLocal("ote.sf.mode.2");
            case 2 -> translateToLocal("ote.sf.mode.3");
            case 3 -> translateToLocal("ote.sf.mode.4");
            default -> translateToLocal("ote.sf.mode.unknown");
        };
    }

    @Override
    public void setMachineMode(int index) {
        if (getBaseMetaTileEntity() != null && getBaseMetaTileEntity().isServerSide()) {
            super.setMachineMode(Math.floorMod(index, getUnlockedMachineModes()));
        } else {
            super.setMachineMode(index);
        }
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new OTHProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (stabilisationFieldMetadata < 2 && machineMode > 0)
            return CheckRecipeResultRegistry.insufficientMachineTier(2);
        if (stabilisationFieldMetadata < 5 && machineMode > 1)
            return CheckRecipeResultRegistry.insufficientMachineTier(5);
        if (stabilisationFieldMetadata < 8 && machineMode > 2)
            return CheckRecipeResultRegistry.insufficientMachineTier(8);

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mOutputFluids = processingLogic.getOutputFluids();
        mOutputItems = processingLogic.getOutputItems();
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);

        return result;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        startRecipeProcessing();
        if (checkEnqing(1)) {
            endRecipeProcessing();
        } else {
            endRecipeProcessing();
            stopMachine(ShutDownReasonRegistry.outOfStuff(translateToLocal("for.bees.species.enqing"), 1));
            return false;
        }

        return super.onRunningTick(aStack);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (itemEnqingM == null) itemEnqingM = OTHItemList.itemEnqingM.get(1);
    }

    @Override
    public void stopMachine(@Nonnull ShutDownReason reason) {
        super.stopMachine(reason);
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
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
    public IStructureDefinition<OTESunFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTESunFactory>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'A',
                    withChannel(
                        "glass",
                        BorosilicateGlass.ofBoroGlass(
                            (byte) 0,
                            (byte) 1,
                            Byte.MAX_VALUE,
                            (te, t) -> te.glassTier = t,
                            te -> te.glassTier)))
                .addElement('B', ofBlock(sBlockCasings1, 14))
                .addElement('C', ofBlock(sBlockCasings8, 12))
                .addElement('D', ofBlock(sBlockCasings8, 13))
                .addElement('E', ofBlock(sBlockCasings8, 14))
                .addElement('F', ofBlock(sBlockCasings9, 1))
                .addElement('G', ofBlock(sBlockCasingsSE, 1))
                .addElement('H', ofBlock(sBlockCasingsTT, 6))
                .addElement(
                    'J',
                    withChannel(
                        "sfg",
                        ofBlocksTiered(
                            OTEMegaQFT::getBlockStabilisationFieldGeneratorTier,
                            ImmutableList.of(
                                Pair.of(StabilisationFieldGenerators, 0),
                                Pair.of(StabilisationFieldGenerators, 1),
                                Pair.of(StabilisationFieldGenerators, 2),
                                Pair.of(StabilisationFieldGenerators, 3),
                                Pair.of(StabilisationFieldGenerators, 4),
                                Pair.of(StabilisationFieldGenerators, 5),
                                Pair.of(StabilisationFieldGenerators, 6),
                                Pair.of(StabilisationFieldGenerators, 7),
                                Pair.of(StabilisationFieldGenerators, 8)),
                            0,
                            (t, meta) -> t.stabilisationFieldMetadata = meta,
                            t -> t.stabilisationFieldMetadata)))
                .addElement('K', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement('L', ofFrame(Materials.InfinityCatalyst))
                .addElement(
                    'I',
                    buildHatchAdder(OTESunFactory.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTESunFactory::addToMachineList)
                        .hint(1)
                        .casingIndex(1024 + 12)
                        .buildAndChain(sBlockCasingsTT, 12))
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
        tt.addMachineType(translateToLocal("ote.tm.sf.0"))
            .addInfo(translateToLocal("ote.tm.sf.1"))
            .addInfo(translateToLocal("ote.tm.sf.2"))
            .addInfo(translateToLocal("ote.tm.sf.3"))
            .addInfo(translateToLocal("ote.tm.sf.4"))
            .addInfo(translateToLocal("ote.tm.sf.5"))
            .addInfo(translateToLocal("ote.tm.sf.attention"))
            .addInfo(translateToLocal("ote.tm.sf.6"))
            .addSeparator()
            .addInfo(translateToLocal("ote.tm.sf.7"))
            .addInfo(translateToLocal("ote.tm.sf.8"))
            .addInfo(translateToLocal("ote.tm.sf.9"))
            .addInfo(translateToLocal("ote.tm.sf.10"))
            .addInfo(translateToLocal("ote.tm.sf.11"))
            .addSeparator()
            .addInfo(translateToLocal("ote.tm.sf.12"))
            .addInfo(translateToLocal("ote.tm.sf.13"))
            .addInfo(translateToLocal("ote.tm.sf.14"))
            .addInfo(translateToLocal("ote.tm.sf.15"))
            .addInfo(translateToLocal("ote.tm.sf.16"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - §c§zBeautiful World!")
            .addController(translateToLocal("ote.tn.sf"))
            .beginStructureBlock(63, 32, 63, false)
            .addInputBus(translateToLocal("oth.hint.any_input_bus"), 1)
            .addOutputBus(translateToLocal("oth.hint.any_output_bus"), 1)
            .addInputHatch(translateToLocal("oth.hint.any_input_hatch"), 1)
            .addOutputHatch(translateToLocal("oth.hint.any_output_hatch"), 1)
            .addEnergyHatch(translateToLocal("oth.hint.any_energy_hatch"), 1)
            .addMufflerHatch(translateToLocal("oth.hint.any_muffler_hatch"), 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTESunFactory(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings1, 12)), TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_ON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] { getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings1, 12)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_OFF)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings1, 12)) };
    }

    // ===================== Misc =====================

    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(LEGACY_MODE_ICON_PCB);
        machineModeIcons.add(LEGACY_MODE_ICON_SMD);
        machineModeIcons.add(LEGACY_MODE_ICON_WAFER);
        machineModeIcons.add(LEGACY_MODE_ICON_COMPONENT);
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new OTESunFactoryGui(this);
    }

    /**
     * @return Amount of modes unlocked by the sfg channel of the installed stabilisation field generators.
     */
    public int getUnlockedMachineModes() {
        if (stabilisationFieldMetadata >= 8) return 4;
        if (stabilisationFieldMetadata >= 5) return 3;
        if (stabilisationFieldMetadata >= 2) return 2;
        return 1;
    }

    private static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    private boolean checkEnqing(int amount) {
        int needAmount = amount;
        for (ItemStack items : getStoredInputsWithoutDualInputHatch()) {
            if (metaItemEqual(items, itemEnqingM)) {
                if (items.stackSize >= needAmount) {
                    items.stackSize -= needAmount;
                    return true;
                } else {
                    needAmount -= items.stackSize;
                    items.stackSize = 0;
                }
            }

        }
        return false;
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("otht.waila.bonus.speed") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.RED
                + tag.getString("speedBonus")
                + EnumChatFormatting.RESET
                + " ");

    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            BYDS = formatNumber(getSpeedBonus());
            tag.setString("speedBonus", BYDS);

        }
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (!getBaseMetaTileEntity().isServerSide() || !supportsMachineModeSwitch()) {
            super.onScrewdriverRightClick(side, aPlayer, aX, aY, aZ, aTool);
            return;
        }
        setMachineMode(nextMachineMode());
        GTUtility.sendChatTrans(aPlayer, "ote.sf.chat.mode." + (machineMode + 1));
    }

    private void getBeeyonds() {
        ItemStack items = getControllerSlot();
        if (items != null && items.getItem() instanceof MetaGeneratedItem03
            && items.getItemDamage() == 4010
            && items.stackSize >= 1) {
            this.Beeyonds = -2F * Math.floor(log(items.stackSize, 2)) / 100;
        }
        if (items != null && items.getItem() instanceof MetaGeneratedItem03
            && items.getItemDamage() == 4581
            && items.stackSize >= 1) {
            this.Beeyonds = 30F * Math.floor(log(items.stackSize, 2)) / 100F;
        }
        if (items != null && items.getItem() instanceof MetaGeneratedItem03
            && items.getItemDamage() == 4139
            && items.stackSize >= 1) {
            this.Beeyonds = 75F * Math.floor(log(items.stackSize, 2)) / 100;
        }
        if (items != null && items.getItem() instanceof MetaGeneratedItem03
            && items.getItemDamage() == 4141
            && items.stackSize >= 1) {
            this.Beeyonds = 150F * Math.floor(log(items.stackSize, 2)) / 100;
        }
    }

    @Override
    protected OTHSoundResource getProcessStartSoundOTH() {
        if (is_Enqing_Song_Play) return OTHSoundResource.ENQING;
        return super.getProcessStartSoundOTH();
    }
}
