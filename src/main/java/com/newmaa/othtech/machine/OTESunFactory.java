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

    public OTESunFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTESunFactory(String aName) {
        super(aName);
    }

    private int stabilisationFieldMetadata = 0;

    private byte glassTier = 0;

    private double Beeyonds = 0;
    private String BYDS = "0";

    /**
     * MUI1 copies of the mode icons. Only the MUI2 set in {@link OTESunFactoryGui} is ever rendered by GT5U 2.9, these
     * keep the legacy {@code addUIWidgets} path showing the same artwork instead of a null background.
     */
    private static final UITexture LEGACY_MODE_ICON_PCB = UITexture
        .fullImage("123technology", "gui/overlay_button/sunfactory_mode_pcb");
    private static final UITexture LEGACY_MODE_ICON_SMD = UITexture
        .fullImage("123technology", "gui/overlay_button/sunfactory_mode_smd");
    private static final UITexture LEGACY_MODE_ICON_WAFER = UITexture
        .fullImage("123technology", "gui/overlay_button/sunfactory_mode_wafer");
    private static final UITexture LEGACY_MODE_ICON_COMPONENT = UITexture
        .fullImage("123technology", "gui/overlay_button/sunfactory_mode_component");

    @Override
    public int totalMachineMode() {
        return 4;
    }

    /**
     * Legacy (MUI1) mode icon list. GT5U 2.9 builds multiblock GUIs with MUI2, where the mode switch button is
     * created from {@link #getGui()}; this is kept so the legacy path can never inject a null background.
     */
    @Override
    public void setMachineModeIcons() {
        machineModeIcons.add(LEGACY_MODE_ICON_PCB);
        machineModeIcons.add(LEGACY_MODE_ICON_SMD);
        machineModeIcons.add(LEGACY_MODE_ICON_WAFER);
        machineModeIcons.add(LEGACY_MODE_ICON_COMPONENT);
    }

    /**
     * MUI2 GUI, which is the only one GT5U 2.9 renders for multiblocks. The mode switch button is only added when
     * the GUI owns at least one icon ({@code MTEMultiBlockBaseGui#createLeftPanelGapRow}), so the four icons passed
     * in by {@link OTESunFactoryGui} are what actually make the recipe map switchable in game.
     */
    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new OTESunFactoryGui(this);
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

    /**
     * Cycles the recipe map. The selectable range is limited by the installed stabilisation field generators, so a
     * player can never put the controller into a mode that {@link #checkProcessing()} would refuse to run. Wrapping
     * instead of clamping keeps repeated clicks / screwdriver uses moving even at the top of the unlocked range.
     * <p>
     * This method is also the {@code machineMode} GUI sync setter on the client, and the client never learns the sfg
     * channel, so the restriction must only be applied server side - remapping it on the client would make the GUI
     * display a different mode (icon, tooltip and name) than the one the machine actually runs.
     */
    @Override
    public void setMachineMode(int index) {
        if (getBaseMetaTileEntity() != null && getBaseMetaTileEntity().isServerSide()) {
            super.setMachineMode(Math.floorMod(index, getUnlockedMachineModes()));
        } else {
            super.setMachineMode(index);
        }
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

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    private static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    private static ItemStack itemEnqingM = null;

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (itemEnqingM == null) itemEnqingM = OTHItemList.itemEnqingM.get(1);
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
    public void stopMachine(@Nonnull ShutDownReason reason) {
        super.stopMachine(reason);
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

    /**
     * Cycles the recipe map. {@link #setMachineMode(int)} restricts the range to the modes unlocked by the installed
     * sfg channel, so the player can never land on a mode {@link #checkProcessing()} would reject.
     * <p>
     * The feedback uses our own translation keys: GT5U.MULTI_MACHINE_CHANGE is not translated in GT's zh_CN.lang and
     * appends "mode" to the (already translated) mode name, which reads wrong in every language.
     */
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

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 31;
    private final int verticalOffSet = 27;
    private final int depthOffSet = 51;
    private static IStructureDefinition<OTESunFactory> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.sf.0") };

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

    // Structured by JulianChum Version 1 & 2
    private final String[][] shapeMain = new String[][] {
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "IIIIIII                                                 IIIIIII",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IIIIIII                                                 IIIIIII",
            "IIIIIII                        I                        IIIIIII",
            "IIIIIII                       III                       IIIIIII",
            "IIIIIII                     IIIIIII                     IIIIIII" },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               D                               ",
            "                               D                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            " IIIII                                                   IIIII ",
            "IEEEEEI                                                 IEEEEEI",
            "AEEEEEA                                                 AEEEEEA",
            "ALLLLLA                                                 ALLLLLA",
            "AEEEEEA                                                 AEEEEEA",
            "AEEEEEA                                                 AEEEEEA",
            "ALLLLLA                                                 ALLLLLA",
            "AEEEEEA                                                 AEEEEEA",
            "AEEEEEA                                                 AEEEEEA",
            "ALLLLLA                                                 ALLLLLA",
            "AEEEEEA                        I                        AEEEEEA",
            "AEEEEEA                        I                        AEEEEEA",
            "ALLLLLA                        I                        ALLLLLA",
            "AEEEEEA                        I                        AEEEEEA",
            "AEEEEEA                        I                        AEEEEEA",
            "IEEEEEI                        I                        IEEEEEI",
            "ICCCCCI                        I                        ICCCCCI",
            "I     I                     DDIIIDD                     I     I",
            "ICCCCCIIIIIIIIII           IBBBBBBBI            IIIIIIIIICCCCCI" },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            " IIIII                                                   IIIII ",
            "IE   EI                                                 IE   EI",
            "AE   EA                                                 AE   EA",
            "AL   LA                                                 AL   LA",
            "AE   EA                                                 AE   EA",
            "AE   EA                                                 AE   EA",
            "AL   LA                                                 AL   LA",
            "AEEEEEA                                                 AEEEEEA",
            "AEEEEEA                        I                        AEEEEEA",
            "AL   LA                       III                       AL   LA",
            "AEEEEEA                       III                       AEEEEEA",
            "AEEEEEA                       III                       AEEEEEA",
            "AL   LA                       III                       AL   LA",
            "AEEEEEA                       III                       AEEEEEA",
            "AEEEEEA                      KIIIK                      AEEEEEA",
            "IEEEEEI                      KIIIK                      IEEEEEI",
            "ICCCCCI                      KIIIK                      ICCCCCI",
            "I     IKKKKKKKKK           DDKIIIKDD            KKKKKKKKI     I",
            "ICCCCCI         III       IBBBBBBBBBI        III        ICCCCCI" },
        { "                                                               ",
            "                                                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                              III                              ",
            "                              GGG                              ",
            " IIIIII                       GGG                       IIIIII ",
            "IE    EI                      GGG                      IE    EI",
            "AE    EI                      GGG                      IE    EA",
            "AL    LI                      III                      IL    LA",
            "AE    EI                     IKDKI                     IE    EA",
            "AE    EI                     IKDKI                     IE    EA",
            "AL    LI                     IKDKI                     IL    LA",
            "AEEEEEEI                     IKDKI                     IEEEEEEA",
            "AEEEEEEI                     IIIII                     IEEEEEEA",
            "AL    LI                     IIIII                     IL    LA",
            "AEEEEEEI                     IIIII                     IEEEEEEA",
            "AEEEEEEI                     I   I                     IEEEEEEA",
            "AL    LI                     I   I                     ILL   LA",
            "AEEEEEEI                     I   I                     IEEEEEEA",
            "AEEEEEEI                    KI   IK                    IEEEEEEA",
            "IEEEEEEI                    KI   IK                    IEEEEEEI",
            "ICCCCCCIKKKKKKKK            KI   IK             KKKKKKKICCCCCCI",
            "I      IBBBBBBBBKKK        DKI   IKD         KKKBBBBBBBI      I",
            "ICCCCCCIKKKKKKKK   II     IBBBBBBBBBI      II          ICCCCCCI" },
        { "                                                               ",
            "                                                               ",
            "                              DDD                              ",
            "                              G G                              ",
            "                              G G                              ",
            "                              III                              ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                            DDDDDDD                            ",
            "                            DD   DD                            ",
            "                            II   II                            ",
            "                            GG   GG                            ",
            " IIIII                      GG   GG                      IIIII ",
            "IE   EI                     GG   GG                     IE   EI",
            "AE   EA                     GG   GG                     AE   EA",
            "AL   LA                     II   II                     AL   LA",
            "AE   EA                     KDD DDK                     AE   EA",
            "AE   EA                     KDD DDK                     AE   EA",
            "AL   LA                     KDD DDK                     AL   LA",
            "AEEEEEA                     KDD DDK                     AEEEEEA",
            "AEEEEEA                     I     I                     AEEEEEA",
            "AL   LA                     D     D                     AL   LA",
            "AEEEEEA                     D     D                     AEEEEEA",
            "AEEEEEA                     D     D                     AEEEEEA",
            "AL   LA                     D     D                     AL   LA",
            "AEEEEEA                     D     D                     AEEEEEA",
            "AEEEEEA                     I     I                     AEEEEEA",
            "IEEEEEI                     I     I                     IEEEEEI",
            "ICCCCCCCI       KKK        II     II         KKK      ICCCCCCCI",
            "I       IKKKKKKKBBBKK     III     III      KKBBBKKKKKKI       I",
            "ICCCCCCCI       KKK  I    IBBBBBBBBBI    II           ICCCCCCCI" },
        { "                                                               ",
            "                              DDD                              ",
            "                             D   D                             ",
            "                             G   G                             ",
            "                             G   G                             ",
            "                            IIDDDII                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            D     D                            ",
            "                            I     I                            ",
            "                            G     G                            ",
            " IIII                       G     G                       IIII ",
            "IEE EI                      G     G                      IE EEI",
            "AEE EA                      G     G                      AE EEA",
            "ALL LA                      I     I                      AL LLA",
            "AEE EA                      D     D                      AE EEA",
            "AEE EA                      D     D                      AE EEA",
            "ALL LA                      D     D                      AL LLA",
            "AEEEEA                      D     D                      AEEEEA",
            "AEEEEA                      I     I                      AEEEEA",
            "ALL LA                      D     D                      AL LLA",
            "AEEEEA                      D     D                      AEEEEA",
            "AEEEEA                      D     D                      AEEEEA",
            "ALLLLA                      D     D                      AL LLA",
            "AEEEEA                      D     D                      AEEEEA",
            "AEEEEA                     II     II                     AEEEEA",
            "IEEEEI                     II     II                     IEEEEI",
            "ICCCCCCCCI         KK     III     III      KK        ICCCCCCCCI",
            "I        I      KKKBBK    III     III    KKBBKKK     I        I",
            "ICCCCCCCCI         KK I   IBBBBIBBBBI   I  KK        ICCCCCCCCI" },
        { "                                                               ",
            "                             DDDDD                             ",
            "                              H H                              ",
            "                              H H                              ",
            "                              H H                              ",
            "                            IDDDDDI                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            D     D                            ",
            "                            I     I                            ",
            "                            G     G                            ",
            "   I                        G     G                        I   ",
            "IIIEI                       G     G                       IEIII",
            "IAAEA                       G     G                       AEAAI",
            "IAALA                       I     I                       ALAAI",
            "IAAEA                       KDD  DK                       AEAAI",
            "IAAEA                       KDD  DK                       AEAAI",
            "IAALA                       KDD  DK                       ALAAI",
            "IAAEA                       KDD  DK                       AEAAI",
            "IAAEA                       I    DI                       AEAAI",
            "IAALA                       D     D                       ALAAI",
            "IAAEA                       D     D                       AEAAI",
            "IAAEA                       D     D                       AEAAI",
            "IAALA                       D     D                       ALAAI",
            "IAAEA                       D     D                       AEAAI",
            "IAAEA                       I     I                       AEAAI",
            "IIIEI                       I     I                       IEIII",
            "IIICCCCCCCIII        K     II     II     KK       IIICCCCCCCIII",
            "III       III      KKBK   III     III   KBBKK     III       III",
            "IIICCCCCCCIII        K I  IBBBBBBBBBI  I KK       IIICCCCCCCIII" },
        { "                                                               ",
            "                             DDDDD                             ",
            "                              H H                              ",
            "                              H H                              ",
            "                              H H                              ",
            "                            IDDDDDI                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             IIIII                             ",
            "         KKK                 GGGGG                 KKK         ",
            "                             GGGGG                             ",
            "   I                         GGGGG                         I   ",
            "   I     KKK                 GGGGG                 KKK     I   ",
            "   I                         IIIII                         I   ",
            "   I                         IDDDI                         I   ",
            "   I     KKK                 IDDDI                 KKK     I   ",
            "   I                         IDDDI                         I   ",
            "   I                         IDDDI                         I   ",
            "   I     KKK                 IIIII                 KKK     I   ",
            "   I                         DDDDD                         I   ",
            "   I                         DDDDD                         I   ",
            "   I     KKK                 DDDDD                 KKK     I   ",
            "   I                         DDDDD                         I   ",
            "   I                         DDDDD                         I   ",
            "   I                        KI   IK                        I   ",
            "   I      I                 KI   IK                 I      I   ",
            "   ICCCCCCCCIII       K    DKI   IKD    K       IIICCCCCCCCI   ",
            "  KI        III      KBK   IKI   IKI   KBKK     III        IK  ",
            " I ICCCCCCCCIII       K I IBBBBBBBBBI I K       IIICCCCCCCCI I " },
        { "                                                               ",
            "                               G                               ",
            "                             DDDDD                             ",
            "                              H H                              ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                               D                               ",
            "        K   K                  D                  K   K        ",
            "                               D                               ",
            "                               D                               ",
            "        K   K                 DDD                 K   K        ",
            "                              DDD                              ",
            "                              DDD                              ",
            "        K   K                 DDD                 K   K        ",
            "                              DDD                              ",
            "                              DDD                              ",
            "          I                  KIIIK                  I          ",
            "         III                 KIIIK                 III         ",
            "   KICCCCCCCCCIIII     K   DDKIIIKDD   K     IIIICCCCCCCCCIK   ",
            "  KBI         IIII    KBK  DDKIIIKDD  KBK    IIII         IBK  ",
            " I  ICCCCCCCCCIIII     K IIBBBBBBBBBII K     IIIICCCCCCCCCIK I " },
        { "                                                               ",
            "                               G                               ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                                                               ",
            "                                                               ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                DDD                K     K       ",
            "          I                   DDD                   I          ",
            "          I                   DDD                   I          ",
            "          I                   DDD                   I          ",
            "        IIIII                 DDD                 IIIII        ",
            "   K ICCCCCCCCCCCIIIII K    DDDDDDD    K IIIIICCCCCCCCCCCI K   ",
            "  KBKI           IIIIIKBK   DDDDDDD   KBKIIIII           IKBK  ",
            " I   ICCCCCCCCCCCIIIII K IIIBBBBBBBIII K IIIIICCCCCCCCCCCI K I " },
        { "                                                               ",
            "                               G                               ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             DBBBD                             ",
            "                             IBBBI                             ",
            "                             KBBBK                             ",
            "                             KBBBK                             ",
            "                             IKKKI                             ",
            "                                                               ",
            "                                                               ",
            "         IBI                                       IBI         ",
            "       K  B  K                                   K  B  K       ",
            "         IBI                                       IBI         ",
            "         IBI                                       IBI         ",
            "       K  B  K                                   K  B  K       ",
            "         IBI                                       IBI         ",
            "         IBI                                       IBI         ",
            "       K  B  K                                   K  B  K       ",
            "         IBI                                       IBI         ",
            "         IBI         KKKKKKKKKKKKKKKKKKKKK         IBI         ",
            "       K  B  K                                   K  B  K       ",
            "         IBI         KKKKKKKKKKKKKKKKKKKKK         IBI         ",
            "         IBI                                       IBI         ",
            "       K  B  K       KKKKKKKKKKKKKKKKKKKKK       K  B  K       ",
            "         IBI                                       IBI         ",
            "         IBI         KKKKKKKKKIIIKKKKKKKKK         IBI         ",
            "        IIIII                 III                 IIIII        ",
            "       IIIIIII       KKKKKKKKKIIIKKKKKKKKK       IIIIIII       ",
            "   K  ICCCCCCCCCCCCCCIIIIIIIIIIIIIIIIIIIIICCCCCCCCCCCCCCI  K   ",
            "  KBK I              IIIIIIIIDDDDDIIIIIIII              I KBK  ",
            " I    ICCCCCCCCCCCCCCIIIIIIIIIIIIIIIIIIIIICCCCCCCCCCCCCCI  K I " },
        { "                                                               ",
            "                                                               ",
            "                               G                               ",
            "                             DKKKD                             ",
            "                             DBBBD                             ",
            "                             IBBBI                             ",
            "                             KBBBK                             ",
            "                             KBBBK                             ",
            "                             IKKKI                             ",
            "                                                               ",
            "                                                               ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I       KKK                     KKK       I          ",
            "       K     K                                   K     K       ",
            "          I       KKK                     KKK       I          ",
            "          I                                         I          ",
            "       K     K    KKK                     KKK    K     K       ",
            "          I                                         I          ",
            "          I       KKK                     KKK       I          ",
            "          I                                         I          ",
            "        IIIII     KKK                     KKK     IIIII        ",
            "   K  ICCCCCCCCCCCIIIFFFFFFFDDDDDDDFFFFFFFIIICCCCCCCCCCCI  K   ",
            "  KBK I           IIIFFFFFFFDDDDDDDFFFFFFFIII           I KBK  ",
            " I    ICCCCCCCCCCCIIIFFFFFFFCCCCCCCFFFFFFFIIICCCCCCCCCCCI  K I " },
        { "                                                               ",
            "                               G                               ",
            "                              KKK                              ",
            "                             DBBBD                             ",
            "                             DDDDD                             ",
            "                             IIIII                             ",
            "                             IKKKI                             ",
            "                             IKKKI                             ",
            "                             IIIII                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                KK                           KK                ",
            "        K   K                                     K   K        ",
            "                KK                           KK                ",
            "                                                               ",
            "        K   K   KK                           KK   K   K        ",
            "                                                               ",
            "                KK                           KK                ",
            "          I                                         I          ",
            "         III    KK                           KK    III         ",
            "   K  IICCCCCCCCIIFFF       DDDDDDD       FFFIICCCCCCCCII  K   ",
            "  KBK II        IIFFF       DDDDDDD       FFFII        II KBK  ",
            " I    IICCCCCCCCIIFFF    CCCCCCCCCCCCC    FFFIICCCCCCCCII  K I " },
        { "                               G                               ",
            "                              KKK                              ",
            "                             KBBBK                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             GGGGG                             ",
            "                             GGGGG                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "         KKK                                       KKK         ",
            "                                                               ",
            "                                                               ",
            "         KKK                                       KKK         ",
            "                                                               ",
            "                                                               ",
            "         KKK                                       KKK         ",
            "                                                               ",
            "               K                               K               ",
            "         KKK                                       KKK         ",
            "               K                               K               ",
            "                                                               ",
            "         KKK   K                               K   KKK         ",
            "                                                               ",
            "               K                               K               ",
            "                                                               ",
            "          I    K                               K    I          ",
            "   K   ICCCCCCCIFF          DDDDDDD          FFICCCCCCCI   K   ",
            "  KBK  I       IFF          DDDDDDD          FFI       I  KBK  ",
            " I     ICCCCCCCIFF       CCCCCCCCCCCCC       FFICCCCCCCI   K I " },
        { "                               G                               ",
            "                              KKK                              ",
            "                             KBBBK                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             GGGGG                             ",
            "                             GGGGG                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "              K                                 K              ",
            "                                                               ",
            "              K                                 K              ",
            "                                                               ",
            "              K                                 K              ",
            "                                                               ",
            "              K                                 K              ",
            "                                                               ",
            "              K                                 K              ",
            "   K   IICCCCCIF            DDDDDDD            FICCCCCII   K   ",
            "  KBK  II     IF            DDDDDDD            FI     II  KBK  ",
            " I     IICCCCCIF         CCCCCCCCCCCCC         FICCCCCII   K I " },
        { "                               G                               ",
            "                              KKK                              ",
            "                             KBBBK                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             GGGGG                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "             K                                   K             ",
            "                                                               ",
            "             K                                   K             ",
            "                                                               ",
            "             K                                   K             ",
            "                                                               ",
            "             K                                   K             ",
            "                                                               ",
            "             K                                   K             ",
            "    K   ICCCCIF             DDDDDDD             FICCCCI    K   ",
            "   KBK  I    IF             DDDDDDD             FI    I   KBK  ",
            "  I     ICCCCIF          CCCCCCCCCCCCC          FICCCCI    K I " },
        { "                               G                               ",
            "                              KKK                              ",
            "                             KBBBK                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             GGGGG                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "    K   ICCCIF              DDDDDDD              FICCCI   K    ",
            "   KBK  I   IF              DDDDDDD              FI   I  KBK   ",
            "  I     ICCCIF           CCCCCCCCCCCCC           FICCCI   K I  " },
        { "                               G                               ",
            "                             IIIII                             ",
            "                             IBBBI                             ",
            "                             IBBBI                             ",
            "                             IIIII                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "    K   IICCIF              DDDDDDD              FICCII   K    ",
            "   KBK  II  IF              DDDDDDD              FI  II  KBK   ",
            "  I     IICCIF           CCCCCCCCCCCCC           FICCII   K I  " },
        { "                               G                               ",
            "                             IKKKI                             ",
            "                             KBBBK                             ",
            "                             KBBBK                             ",
            "                             IKKKI                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "     K   ICIF               DDDDDDD               FICI    K    ",
            "    KBK  I IF               DDDDDDD               FI I   KBK   ",
            "   I K   ICIF          CCCCCCCCCCCCCCCCC          FICI    K I  " },
        { "                               G                               ",
            "                             IKKKI                             ",
            "                             KBBBK                             ",
            "                             KBBBK                             ",
            "                             IKKKI                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "     K   ICIF               DDDDDDD               FICI   K     ",
            "    KBK  I IF               DDDDDDD               FI I  KBK    ",
            "   I K   ICIF         CCCCCCCCCCCCCCCCCCC         FICI   K I   " },
        { "                               G                               ",
            "                             IIIII                             ",
            "                             IKKKI                             ",
            "                             IKKKI                             ",
            "                             IIIII                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "      K  ICIF               DDDDDDD               FICI   K     ",
            "     KBK I IF             KKDDDDDDDKK             FI I  KBK    ",
            "    I K  ICIF        CCCCCCCCCCCCCCCCCCCCC        FICI   K I   " },
        { "                               G                               ",
            "                              DDD                              ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                              DDD                              ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "      K  IIF              KKDDDDDDDKK              FII  K      ",
            "     KBK IIF             KBBDDDDDDDBBK             FII KBK     ",
            "    I K  IIF        CCCCCCCCCCCCCCCCCCCCCCC        FII  K I    " },
        { "                                                               ",
            "                               G                               ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                               D                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "       K  IF             KCCDDDDDDDCCK             FI  K       ",
            "      KBKKIF            KBCCDDDDDDDCCBK            FIKKBK      ",
            "     I K  IF       CCCCCCCCCCCCCCCCCCCCCCCCC       FI  K I     " },
        { "                                                               ",
            "                               G                               ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                              III                              ",
            "                              III                              ",
            "                              III                              ",
            "                              III                              ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "        KKIF            KCDDJDDJDDJDDCK            FIKK        ",
            "       KBBIF           KBCDDDDDDDDDDDCBK           FIBBK       ",
            "      I KKIF      CCCCCCCCCCCCCCCCCCCCCCCCCCC      FIKK I      " },
        { "                                                               ",
            "                                                               ",
            "                               G                               ",
            "                              III                              ",
            "                              GIG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              III                              ",
            "                            IIIIIII                            ",
            "                            I III I                            ",
            "                            I IBI I                            ",
            "                           II     II                           ",
            "                           I       I                           ",
            "                           I       I                           ",
            "                           I       I                           ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "          IF           KCDDDDJDJDJDDDDCK           FI          ",
            "        KKIF          KBCDDDDDDDDDDDDDCBK          FIKK        ",
            "       I  IF      CCCCCCCCCCCCCCCCCCCCCCCCCCC      FI  I       " },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                              III                              ",
            "                              III                              ",
            "                              III                              ",
            "                              III                              ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "          IF          KCDDDDDDDDDDDDDDDCK          FI          ",
            "          IF         KBCDDDDDDDDDDDDDDDCBK         FI          ",
            "        IIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIII        " },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "     I    IF         KCDDDDDDDJJJDDDDDDDCK         FI    I     ",
            "    III   IF        KBCDDDDDDDCCCCDDDDDDCBK        FI   III    ",
            "  IIIIIIIIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIIIIIIIII  " },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                        I      I      I                        ",
            "                        I             I                        ",
            "                        I             I                        ",
            "                        I             I                        ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "     I                                                   I     ",
            "     I    K                                         K    I     ",
            "    IIIDD IF         KCDDDDDJJDDDJJDDDDDCK         FI DDIII    ",
            "  DDIIIID IF        KBCDDDDDCCDDDDCDDDDDCBK        FI DIIIIDD  ",
            " IBBBBBBBIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIIBBBBBBBI " },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "     III                                               III     ",
            "     DDD                                               DDD     ",
            "     DDD                                               DDD     ",
            "     DDD                                               DDD     ",
            "    DDD                                                 DDD    ",
            "    DDD                                                 DDD    ",
            "    III                                                 III    ",
            "    GGG                 I             I                 GGG    ",
            "    GGG                 I             I                 GGG    ",
            "    GGG                 I             I                 GGG    ",
            "    GGG                 I      I      I                 GGG    ",
            "    III                        I                        III    ",
            "    KDK                        I                        KDK    ",
            "    KDK                        I                        KDK    ",
            "    KDK                                                 KDK    ",
            "    KDK   K                                         K   KDK    ",
            "    III                                                 III    ",
            "    DDD   K                                         K   DDD    ",
            "    DDD                                                 DDD    ",
            "    DDD   K                                         K   DDD    ",
            "    DDD                                                 DDD    ",
            "    DDD   K                                         K   DDD    ",
            "   KIIIK                                               KIIIK   ",
            "   KIIIK  K                                         K  KIIIK   ",
            "   KIIIKDDIDDDDDDDDDDDDJDDDJDDDDDDDJDDDJDDDDDDDDDDDDIDDKIIIK   ",
            " DDKIIIKDDIDDDDDDDDDDDDDDDDCDDDDDDDCDDDDDDDDDDDDDDDDIDDKIIIKDD ",
            "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI" },
        { "                                                               ",
            "      DD         IIII                     IIII         DD      ",
            "     D  DDD  KKKKIKKID                   DIKKIKKKK  DDD  D     ",
            "     G   DDDDIIIIIKKID                   DIKKIIIIIDDDD   G     ",
            "     G  IDDDDIIIIIIII                     IIIIIIIIDDDDI  G     ",
            "     IDDIIIIIGGGG                             GGGGIIIIIDDI     ",
            "     DDDDIKKIGG                                 GGIKKIDDDD     ",
            "     DDDDIKKI                                     IKKIDDDD     ",
            "     DDDDIIII                                     IIIIDDDD     ",
            "    DDDD                                               DDDD    ",
            "    D  D                                               D  D    ",
            "    I  I                                               I  I    ",
            "    G  G                I             I                G  G    ",
            "    G  G                                               G  G    ",
            "    G  G                                               G  G    ",
            "    G  G                                               G  G    ",
            "    I  I                                               I  I    ",
            "   ID DI                                               ID DI   ",
            "   ID DI                                               ID DI   ",
            "   ID DI                                               ID DI   ",
            "   ID DI  K                                         K  ID DI   ",
            "   I  DI                                               I   I   ",
            "   I   D  K                                         K  D   I   ",
            "   I   D                                               D   I   ",
            "   I   D  K                                         K  D   I   ",
            "   I   D                                               D   I   ",
            "   I   D  K                                         K  D   I   ",
            "  KI   IK                                             KI   IK  ",
            "  KI   IK K                                         K KI   IK  ",
            "  KI   IKDIDDDDDDDDDDDDDJDDJDDDDDDDJDDJDDDDDDDDDDDDDIDKI   IK  ",
            " DKI   IKDDDDDDDDDDDDDDDDDCDDDDDDDDCDDDDDDDDDDDDDDDDDDKI   IKD ",
            "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI" },
        { "                                                               ",
            "     DDD     KKKKIKKID                   DIKKIKKKK     DDD     ",
            "    D HHDDD KBBBBBBBKDDD               DDDKBBBBBBBK DDDHH D    ",
            "    G HHHDDKBIIIIBBBKDDDI             IDDDKBBBIIIIBKDDHHH G    ",
            "    G HHIDBBDIIIIIKKID  G             G  DIKKIIIIIDBBDIHH G    ",
            "    IDDDIIBBIGGGG       G             G       GGGGIBBIIDDDI    ",
            "    DDDDDIBBKGG         G             G         GGKBBIDDDDD    ",
            "    DDDDDIBBK           G             G           KBBIDDDDD    ",
            "    DDDDDIKKI           G             G           IKKIDDDDD    ",
            "   DDDDD                G             G                DDDDD   ",
            "   D   D                G             G                D   D   ",
            "   I   I               III           III               I   I   ",
            "   G   G               III           III               G   G   ",
            "   G   G               III           III               G   G   ",
            "   G   G               III           III               G   G   ",
            "   G   G                                               G   G   ",
            "   I   I                                               I   I   ",
            "   KD  D                                               DD DK   ",
            "   KD  D                                               DD DK   ",
            "   KD  D                                               DD DK   ",
            "   KD  D  K                                         K  DD DK   ",
            "   I   ID                                             DI   I   ",
            "  II   DD K                                         K DD   II  ",
            "  II   DD                                             DD   II  ",
            "  I    DDDK                                         KDDD    I  ",
            "  I    DDD                                           DDD    I  ",
            "  I    DDDI                                         IDDD    I  ",
            "  I     IDI                                         IDI     I  ",
            "  I     IDI                                         IDI     I  ",
            "  I     IDIDDDDDDDDDDDDDDDJDDDDJDDDDJDDDDDDDDDDDDDDDIDI     I  ",
            "III     IDDDDDDDDDDDDDDDDDCDDDDCDDDDCDDDDDDDDDDDDDDDDDI     III",
            "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI" },
        { "             GGGGGGGGG                   GGGGGGGGG             ",
            "     DDDGGG GKKKKIKKIDGG               GGDIKKIKKKKG GGGDDD     ",
            "   DD   DDDGKBBBBBBBKDDDG             GDDDKBBBBBBBKGDDD   DD   ",
            " DDD     DDKBIIIIBBBKDDDII           IIDDDKBBBIIIIBKDD     DDD ",
            " DDD    IDBBDIIIIIKKIDDGIG           GIGDDIKKIIIIIDBBDI    DDD ",
            "  DDIDDDIIBBIGGGG      GGG           GGG      GGGGIBBIIDDDIDD  ",
            "  DDDDDDDIBBKGG        GGG           GGG        GGKBBIDDDDDDD  ",
            "   DDDDDDIBBK          GGG           GGG          KBBIDDDDDD   ",
            "   DDDDDDIKKI          GGG           GGG          IKKIDDDDDD   ",
            "   DDDDD               GGG           GGG               DDDDD   ",
            "   D   D               GGG           GGG               D   D   ",
            "   I   I               III           III               I   I   ",
            "   G   G             IIIIIII       IIIIIII             G   G   ",
            "   G   G             I III I       I III I             G   G   ",
            "   G   G             I IBI I       I IBI I             G   G   ",
            "   G   G            II     II     II     II            G   G   ",
            "   I   I            I       I     I       I            I   I   ",
            "   D   DD           I       I     I       I           DD   D   ",
            "   D   DD           I       I     I       I           DD   D   ",
            "   D   DD                                             DD   D   ",
            "   D   DD K                                         K DD   D   ",
            "  II   ID                                             DI   II  ",
            "  II   DD K                                         K DD   II  ",
            " III   DD                                             DD   III ",
            " II    DDDK                                         KDDD    II ",
            " II    DDD                                           DDD    II ",
            " II    DDDI                                         IDDD    II ",
            " II     IDI                                         IDI     II ",
            " II     IDI                                         IDI     II ",
            "III     IDIDDDDDDDDDDDDJJDJDDDJJJDDDJDJJDDDDDDDDDDDDIDI     III",
            "III     IDDDDDDDDDDDDDDDDDCDDDCCCDDDCDDDDDDDDDDDDDDDDDI     III",
            "IBBBBIBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBIBBBBI" },
        { "                                                               ",
            "     DDD     KKKKIKKID                   DIKKIKKKK     DDD     ",
            "    D HHDDD KBBBBBBBKDDD               DDDKBBBBBBBK DDDHH D    ",
            "    G HHHDDKBIIIIBBBKDDDI             IDDDKBBBIIIIBKDDHHH G    ",
            "    G HHIDBBDIIIIIKKID  G             G  DIKKIIIIIDBBDIHH G    ",
            "    IDDDIIBBIGGGG       G             G       GGGGIBBIIDDDI    ",
            "    DDDDDIBBKGG         G             G         GGKBBIDDDDD    ",
            "    DDDDDIBBK           G             G           KBBIDDDDD    ",
            "    DDDDDIKKI           G             G           IKKIDDDDD    ",
            "   DDDDD                G             G                DDDDD   ",
            "   D   D                G             G                D   D   ",
            "   I   I               III           III               I   I   ",
            "   G   G               III           III               G   G   ",
            "   G   G               III           III               G   G   ",
            "   G   G               III           III               G   G   ",
            "   G   G                                               G   G   ",
            "   I   I                                               I   I   ",
            "   KD DD                                               D  DK   ",
            "   KD DD                                               D  DK   ",
            "   KD DD                                               D  DK   ",
            "   KD DD  K                                         K  D  DK   ",
            "   I   ID                                             DI   I   ",
            "  II   DD K                                         K DD   II  ",
            "  II   DD                                             DD   II  ",
            "  I    DDDK                                         KDDD    I  ",
            "  I    DDD                                           DDD    I  ",
            "  I    DDDI                                         IDDD    I  ",
            "  I     IDI                                         IDI     I  ",
            "  I     IDI                                         IDI     I  ",
            "  I     IDIDDDDDDDDDDDDDDDJDDDDJDDDDJDDDDDDDDDDDDDDDIDI     I  ",
            "III     IDDDDDDDDDDDDDDDDDCDDDDCDDDDCDDDDDDDDDDDDDDDDDI     III",
            "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI" },
        { "                                                               ",
            "      DD         IIII                     IIII         DD      ",
            "     D  DDD  KKKKIKKID                   DIKKIKKKK  DDD  D     ",
            "     G   DDDDIIIIIKKID                   DIKKIIIIIDDDD   G     ",
            "     G  IDDDDIIIIIIII                     IIIIIIIIDDDDI  G     ",
            "     IDDIIIIIGGGG                             GGGGIIIIIDDI     ",
            "     DDDDIKKIGG                                 GGIKKIDDDD     ",
            "     DDDDIKKI                                     IKKIDDDD     ",
            "     DDDDIIII                                     IIIIDDDD     ",
            "    DDDD                                               DDDD    ",
            "    D  D                                               D  D    ",
            "    I  I                                               I  I    ",
            "    G  G                I             I                G  G    ",
            "    G  G                                               G  G    ",
            "    G  G                                               G  G    ",
            "    G  G                                               G  G    ",
            "    I  I                                               I  I    ",
            "   ID DI                                               ID DI   ",
            "   ID DI                                               ID DI   ",
            "   ID DI                                               ID DI   ",
            "   ID DI  K                                         K  ID DI   ",
            "   I   I                                               ID  I   ",
            "   I   D  K                                         K  D   I   ",
            "   I   D                                               D   I   ",
            "   I   D  K                                         K  D   I   ",
            "   I   D                                               D   I   ",
            "   I   D  K                                         K  D   I   ",
            "  KI   IK                                             KI   IK  ",
            "  KI   IK K                                         K KI   IK  ",
            "  KI   IKDIDDDDDDDDDDDDDJDDJDDDDDDDJDDJDDDDDDDDDDDDDIDKI   IK  ",
            " DKI   IKDDDDDDDDDDDDDDDDDDCDDDDDDDDCDDDDDDDDDDDDDDDDDKI   IKD ",
            "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI" },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "     III                                               III     ",
            "     DDD                                               DDD     ",
            "     DDD                                               DDD     ",
            "     DDD                                               DDD     ",
            "    DDD                                                 DDD    ",
            "    DDD                                                 DDD    ",
            "    III                                                 III    ",
            "    GGG                 I             I                 GGG    ",
            "    GGG                 I             I                 GGG    ",
            "    GGG                 I             I                 GGG    ",
            "    GGG                 I      I      I                 GGG    ",
            "    III                        I                        III    ",
            "    KDK                        I                        KDK    ",
            "    KDK                        I                        KDK    ",
            "    KDK                                                 KDK    ",
            "    KDK   K                                         K   KDK    ",
            "    III                                                 III    ",
            "    DDD   K                                         K   DDD    ",
            "    DDD                                                 DDD    ",
            "    DDD   K                                         K   DDD    ",
            "    DDD                                                 DDD    ",
            "    DDD   K                                         K   DDD    ",
            "   KIIIK                                               KIIIK   ",
            "   KIIIK  K                                         K  KIIIK   ",
            "   KIIIKDDIDDDDDDDDDDDDJDDDJDDDDDDDJDDDJDDDDDDDDDDDDIDDKIIIK   ",
            " DDKIIIKDDIDDDDDDDDDDDDDDDDCDDDDDDDCDDDDDDDDDDDDDDDDIDDKIIIKDD ",
            "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI" },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                        I      I      I                        ",
            "                        I             I                        ",
            "                        I             I                        ",
            "                        I             I                        ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "     I                                                   I     ",
            "     I    K                                         K    I     ",
            "    IIIDD IF         KCDDDDDJJDDDJJDDDDDCK         FI DDIII    ",
            "  DDIIIID IF        KBCDDDDDCDDDDCCDDDDDCBK        FI DIIIIDD  ",
            " IBBBBBBBIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIIBBBBBBBI " },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "     I    IF         KCDDDDDDDJJJDDDDDDDCK         FI    I     ",
            "    III   IF        KBCDDDDDDCCCCDDDDDDDCBK        FI   III    ",
            "  IIIIIIIIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIIIIIIIII  " },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                              III                              ",
            "                              III                              ",
            "                              III                              ",
            "                              III                              ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "          IF          KCDDDDDDDDDDDDDDDCK          FI          ",
            "          IF         KBCDDDDDDDDDDDDDDDCBK         FI          ",
            "        IIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIII        " },
        { "                                                               ",
            "                                                               ",
            "                               G                               ",
            "                              III                              ",
            "                              GIG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              GGG                              ",
            "                              III                              ",
            "                            IIIIIII                            ",
            "                            I III I                            ",
            "                            I IBI I                            ",
            "                           II     II                           ",
            "                           I       I                           ",
            "                           I       I                           ",
            "                           I       I                           ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "          IF           KCDDDDJDJDJDDDDCK           FI          ",
            "        KKIF          KBCDDDDDDDDDDDDDCBK          FIKK        ",
            "       I  IF      CCCCCCCCCCCCCCCCCCCCCCCCCCC      FI  I       " },
        { "                                                               ",
            "                               G                               ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                               G                               ",
            "                              III                              ",
            "                              III                              ",
            "                              III                              ",
            "                              III                              ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "        KKIF            KCDDJDDJDDJDDCK            FIKK        ",
            "       KBBIF           KBCDDDDDDDDDDDCBK           FIBBK       ",
            "      I KKIF      CCCCCCCCCCCCCCCCCCCCCCCCCCC      FIKK I      " },
        { "                                                               ",
            "                               G                               ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                               D                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "       K  IF             KCCDDDDDDDCCK             FI  K       ",
            "      KBKKIF            KBCCDDDDDDDCCBK            FIKKBK      ",
            "     I K  IF       CCCCCCCCCCCCCCCCCCCCCCCCC       FI  K I     " },
        { "                               G                               ",
            "                              DDD                              ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                              DDD                              ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "                                                               ",
            "          K                                         K          ",
            "      K  IIF              KKDDDDDDDKK              FII  K      ",
            "     KBK IIF             KBBDDDDDDDBBK             FII KBK     ",
            "    I K  IIF        CCCCCCCCCCCCCCCCCCCCCCC        FII  K I    " },
        { "                               G                               ",
            "                             IIIII                             ",
            "                             IKKKI                             ",
            "                             IKKKI                             ",
            "                             IIIII                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                               I                               ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "     K   ICIF               DDDDDDD               FICI  K      ",
            "    KBK  I IF             KKDDDDDDDKK             FI I KBK     ",
            "   I K   ICIF        CCCCCCCCCCCCCCCCCCCCC        FICI  K I    " },
        { "                               G                               ",
            "                             IKKKI                             ",
            "                             KBBBK                             ",
            "                             KBBBK                             ",
            "                             IKKKI                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "     K   ICIF               DDDDDDD               FICI   K     ",
            "    KBK  I IF               DDDDDDD               FI I  KBK    ",
            "   I K   ICIF         CCCCCCCCCCCCCCCCCCC         FICI   K I   " },
        { "                               G                               ",
            "                             IKKKI                             ",
            "                             KBBBK                             ",
            "                             KBBBK                             ",
            "                             IKKKI                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "                                                               ",
            "           K                                       K           ",
            "    K    ICIF               DDDDDDD               FICI   K     ",
            "   KBK   I IF               DDDDDDD               FI I  KBK    ",
            "  I K    ICIF          CCCCCCCCCCCCCCCCC          FICI   K I   " },
        { "                               G                               ",
            "                             IIIII                             ",
            "                             IBBBI                             ",
            "                             IBBBI                             ",
            "                             IIIII                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "    K   IICCIF              DDDDDDD              FICCII   K    ",
            "   KBK  II  IF              DDDDDDD              FI  II  KBK   ",
            "  I K   IICCIF           CCCCCCCCCCCCC           FICCII     I  " },
        { "                               G                               ",
            "                              KKK                              ",
            "                             KBBBK                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             GGGGG                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "                                                               ",
            "            K                                     K            ",
            "    K   ICCCIF              DDDDDDD              FICCCI   K    ",
            "   KBK  I   IF              DDDDDDD              FI   I  KBK   ",
            "  I K   ICCCIF           CCCCCCCCCCCCC           FICCCI     I  " },
        { "                               G                               ",
            "                              KKK                              ",
            "                             KBBBK                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             GGGGG                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "             K                                   K             ",
            "                                                               ",
            "             K                                   K             ",
            "                                                               ",
            "             K                                   K             ",
            "                                                               ",
            "             K                                   K             ",
            "                                                               ",
            "             K                                   K             ",
            "   K    ICCCCIF             DDDDDDD             FICCCCI   K    ",
            "  KBK   I    IF             DDDDDDD             FI    I  KBK   ",
            " I K    ICCCCIF          CCCCCCCCCCCCC          FICCCCI     I  " },
        { "                               G                               ",
            "                              KKK                              ",
            "                             KBBBK                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             GGGGG                             ",
            "                             GGGGG                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "              K                                 K              ",
            "                                                               ",
            "              K                                 K              ",
            "                                                               ",
            "              K                                 K              ",
            "                                                               ",
            "              K                                 K              ",
            "                                                               ",
            "              K                                 K              ",
            "   K   IICCCCCIF            DDDDDDD            FICCCCCII   K   ",
            "  KBK  II     IF            DDDDDDD            FI     II  KBK  ",
            " I K   IICCCCCIF         CCCCCCCCCCCCC         FICCCCCII     I " },
        { "                               G                               ",
            "                              KKK                              ",
            "                             KBBBK                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             GGGGG                             ",
            "                             GGGGG                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "         KKK                                       KKK         ",
            "                                                               ",
            "                                                               ",
            "         KKK                                       KKK         ",
            "                                                               ",
            "                                                               ",
            "         KKK                                       KKK         ",
            "                                                               ",
            "               K                               K               ",
            "         KKK                                       KKK         ",
            "               K                               K               ",
            "                                                               ",
            "         KKK   K                               K   KKK         ",
            "                                                               ",
            "               K                               K               ",
            "                                                               ",
            "          I    K                               K    I          ",
            "   K   ICCCCCCCIFF          DDDDDDD          FFICCCCCCCI   K   ",
            "  KBK  I       IFF          DDDDDDD          FFI       I  KBK  ",
            " I K   ICCCCCCCIFF       CCCCCCCCCCCCC       FFICCCCCCCI     I " },
        { "                                                               ",
            "                               G                               ",
            "                              KKK                              ",
            "                             DBBBD                             ",
            "                             DDDDD                             ",
            "                             IIIII                             ",
            "                             IKKKI                             ",
            "                             IKKKI                             ",
            "                             IIIII                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                KK                           KK                ",
            "        K   K                                     K   K        ",
            "                KK                           KK                ",
            "                                                               ",
            "        K   K   KK                           KK   K   K        ",
            "                                                               ",
            "                KK                           KK                ",
            "          I                                         I          ",
            "         III    KK                           KK    III         ",
            "   K  IICCCCCCCCIIFFF       DDDDDDD       FFFIICCCCCCCCII  K   ",
            "  KBK II        IIFFF       DDDDDDD       FFFII        II KBK  ",
            " I K  IICCCCCCCCIIFFF    CCCCCCCCCCCCC    FFFIICCCCCCCCII    I " },
        { "                                                               ",
            "                                                               ",
            "                               G                               ",
            "                             DKKKD                             ",
            "                             DBBBD                             ",
            "                             IBBBI                             ",
            "                             KBBBK                             ",
            "                             KBBBK                             ",
            "                             IKKKI                             ",
            "                                                               ",
            "                                                               ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I       KKK                     KKK       I          ",
            "       K     K                                   K     K       ",
            "          I       KKK                     KKK       I          ",
            "          I                                         I          ",
            "       K     K    KKK                     KKK    K     K       ",
            "          I                                         I          ",
            "          I       KKK         III         KKK       I          ",
            "          I                  II~II                  I          ",
            "        IIIII     KKK       IIIIIII       KKK     IIIII        ",
            "   K  ICCCCCCCCCCCIIIFFFFFFFDDDDDDDFFFFFFFIIICCCCCCCCCCCI  K   ",
            "  KBK I           IIIFFFFFFFDDDDDDDFFFFFFFIII           I KBK  ",
            " I K  ICCCCCCCCCCCIIIFFFFFFFCCCCCCCFFFFFFFIIICCCCCCCCCCCI    I " },
        { "                                                               ",
            "                               G                               ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             DBBBD                             ",
            "                             IBBBI                             ",
            "                             KBBBK                             ",
            "                             KBBBK                             ",
            "                             IKKKI                             ",
            "                                                               ",
            "                                                               ",
            "         IBI                                       IBI         ",
            "       K  B  K                                   K  B  K       ",
            "         IBI                                       IBI         ",
            "         IBI                                       IBI         ",
            "       K  B  K                                   K  B  K       ",
            "         IBI                                       IBI         ",
            "         IBI                                       IBI         ",
            "       K  B  K                                   K  B  K       ",
            "         IBI                                       IBI         ",
            "         IBI         KKKKKKKKKKKKKKKKKKKKK         IBI         ",
            "       K  B  K                                   K  B  K       ",
            "         IBI         KKKKKKKKKKKKKKKKKKKKK         IBI         ",
            "         IBI                                       IBI         ",
            "       K  B  K       KKKKKKKKKKKKKKKKKKKKK       K  B  K       ",
            "         IBI                                       IBI         ",
            "         IBI         KKKKKKKKKIIIKKKKKKKKK         IBI         ",
            "        IIIII                 III                 IIIII        ",
            "       IIIIIII       KKKKKKKKKIIIKKKKKKKKK       IIIIIII       ",
            "   K  ICCCCCCCCCCCCCCIIIIIIIIIIIIIIIIIIIIICCCCCCCCCCCCCCI  K   ",
            "  KBK I              IIIIIIIIDDDDDIIIIIIII              I KBK  ",
            " I K  ICCCCCCCCCCCCCCIIIIIIIIIIIIIIIIIIIIICCCCCCCCCCCCCCI    I " },
        { "                                                               ",
            "                               G                               ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                                                               ",
            "                                                               ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                                   K     K       ",
            "          I                                         I          ",
            "          I                                         I          ",
            "       K     K                DDD                K     K       ",
            "          I                   DDD                   I          ",
            "          I                   DDD                   I          ",
            "          I                   DDD                   I          ",
            "        IIIII                 DDD                 IIIII        ",
            "   K ICCCCCCCCCCCIIIII K    DDDDDDD    K IIIIICCCCCCCCCCCI K   ",
            "  KBKI           IIIIIKBK   DDDDDDD   KBKIIIII           IKBK  ",
            " I K ICCCCCCCCCCCIIIII K IIIBBBBBBBIII K IIIIICCCCCCCCCCCI   I " },
        { "                                                               ",
            "                               G                               ",
            "                             DDDDD                             ",
            "                              H H                              ",
            "                             IIIII                             ",
            "                             IIIII                             ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                                                               ",
            "        K   K                                     K   K        ",
            "                                                               ",
            "                               D                               ",
            "        K   K                  D                  K   K        ",
            "                               D                               ",
            "                               D                               ",
            "        K   K                 DDD                 K   K        ",
            "                              DDD                              ",
            "                              DDD                              ",
            "        K   K                 DDD                 K   K        ",
            "                              DDD                              ",
            "                              DDD                              ",
            "          I                  KIIIK                  I          ",
            "         III                 KIIIK                 III         ",
            "   KICCCCCCCCCIIII     K   DDKIIIKDD   K     IIIICCCCCCCCCIK   ",
            "  KBI         IIII    KBK  DDKIIIKDD  KBK    IIII         IBK  ",
            " I KICCCCCCCCCIIII     K IIBBBBBBBBBII K     IIIICCCCCCCCCI  I " },
        { "                                                               ",
            "                             DDDDD                             ",
            "                              H H                              ",
            "                              H H                              ",
            "                              H H                              ",
            "                            IDDDDDI                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                             DDDDD                             ",
            "                             DDDDD                             ",
            "                             IIIII                             ",
            "         KKK                 GGGGG                 KKK         ",
            "                             GGGGG                             ",
            "   I                         GGGGG                         I   ",
            "   I     KKK                 GGGGG                 KKK     I   ",
            "   I                         IIIII                         I   ",
            "   I                         IDDDI                         I   ",
            "   I     KKK                 IDDDI                 KKK     I   ",
            "   I                         IDDDI                         I   ",
            "   I                         IDDDI                         I   ",
            "   I     KKK                 IIIII                 KKK     I   ",
            "   I                         DDDDD                         I   ",
            "   I                         DDDDD                         I   ",
            "   I     KKK                 DDDDD                 KKK     I   ",
            "   I                         DDDDD                         I   ",
            "   I                         DDDDD                         I   ",
            "   I                        KI   IK                        I   ",
            "   I      I                 KI   IK                 I      I   ",
            "   ICCCCCCCCIII       K    DKI   IKD    K       IIICCCCCCCCI   ",
            "  KI        III     KKBK   IKI   IKI   KBK      III        IK  ",
            " I ICCCCCCCCIII       K I IBBBBBBBBBI I K       IIICCCCCCCCI I " },
        { "                                                               ",
            "                             DDDDD                             ",
            "                              H H                              ",
            "                              H H                              ",
            "                              H H                              ",
            "                            IDDDDDI                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            D     D                            ",
            "                            I     I                            ",
            "                            G     G                            ",
            "   I                        G     G                        I   ",
            "IIIEI                       G     G                       IEIII",
            "IAAEA                       G     G                       AEAAI",
            "IAALA                       I     I                       ALAAI",
            "IAAEA                       KD  DDK                       AEAAI",
            "IAAEA                       KD  DDK                       AEAAI",
            "IAALA                       KD  DDK                       ALAAI",
            "IAAEA                       KD  DDK                       AEAAI",
            "IAAEA                       ID    I                       AEAAI",
            "IAALA                       D     D                       ALAAI",
            "IAAEA                       D     D                       AEAAI",
            "IAAEA                       D     D                       AEAAI",
            "IAALA                       D     D                       ALAAI",
            "IAAEA                       D     D                       AEAAI",
            "IAAEA                       I     I                       AEAAI",
            "IIIEI                       I     I                       IEIII",
            "IIICCCCCCCIII       KK     II     II     K        IIICCCCCCCIII",
            "III       III     KKBBK   III     III   KBKK      III       III",
            "IIICCCCCCCIII       KK I  IBBBBBBBBBI  I K        IIICCCCCCCIII" },
        { "                                                               ",
            "                              DDD                              ",
            "                             D   D                             ",
            "                             G   G                             ",
            "                             G   G                             ",
            "                            IIDDDII                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            DDDDDDD                            ",
            "                            D     D                            ",
            "                            I     I                            ",
            "                            G     G                            ",
            " IIII                       G     G                       IIII ",
            "IEE EI                      G     G                      IE EEI",
            "AEE EA                      G     G                      AE EEA",
            "ALL LA                      I     I                      AL LLA",
            "AEE EA                      D     D                      AE EEA",
            "AEE EA                      D     D                      AE EEA",
            "ALL LA                      D     D                      AL LLA",
            "AEEEEA                      D     D                      AEEEEA",
            "AEEEEA                      I     I                      AEEEEA",
            "ALL LA                      D     D                      AL LLA",
            "AEEEEA                      D     D                      AEEEEA",
            "AEEEEA                      D     D                      AEEEEA",
            "ALL LA                      D     D                      ALLLLA",
            "AEEEEA                      D     D                      AEEEEA",
            "AEEEEA                     II     II                     AEEEEA",
            "IEEEEI                     II     II                     IEEEEI",
            "ICCCCCCCCI        KK      III     III     KK         ICCCCCCCCI",
            "I        I     KKKBBKK    III     III    KBBKKK      I        I",
            "ICCCCCCCCI        KK  I   IBBBBIBBBBI   I KK         ICCCCCCCCI" },
        { "                                                               ",
            "                                                               ",
            "                              DDD                              ",
            "                              G G                              ",
            "                              G G                              ",
            "                              III                              ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                            DDDDDDD                            ",
            "                            DD   DD                            ",
            "                            II   II                            ",
            "                            GG   GG                            ",
            " IIIII                      GG   GG                      IIIII ",
            "IE   EI                     GG   GG                     IE   EI",
            "AE   EA                     GG   GG                     AE   EA",
            "AL   LA                     II   II                     AL   LA",
            "AE   EA                     KDD DDK                     AE   EA",
            "AE   EA                     KDD DDK                     AE   EA",
            "AL   LA                     KDD DDK                     AL   LA",
            "AEEEEEA                     KDD DDK                     AEEEEEA",
            "AEEEEEA                     I     I                     AEEEEEA",
            "AL   LA                     D     D                     AL   LA",
            "AEEEEEA                     D     D                     AEEEEEA",
            "AEEEEEA                     D     D                     AEEEEEA",
            "AL   LA                     D     D                     AL   LA",
            "AEEEEEA                     D     D                     AEEEEEA",
            "AEEEEEA                     I     I                     AEEEEEA",
            "IEEEEEI                     I     I                     IEEEEEI",
            "ICCCCCCCI      KKK         II     II        KKK       ICCCCCCCI",
            "I       IKKKKKKBBBKK      III     III     KKBBBKKKKKKKI       I",
            "ICCCCCCCI           II    IBBBBBBBBBI    I  KKK       ICCCCCCCI" },
        { "                                                               ",
            "                                                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                              DDD                              ",
            "                              DDD                              ",
            "                              III                              ",
            "                              GGG                              ",
            " IIIIII                       GGG                       IIIIII ",
            "IE    EI                      GGG                      IE    EI",
            "AE    EI                      GGG                      IE    EA",
            "AL    LI                      III                      IL    LA",
            "AE    EI                     IKDKI                     IE    EA",
            "AE    EI                     IKDKI                     IE    EA",
            "AL    LI                     IKDKI                     IL    LA",
            "AEEEEEEI                     IKDKI                     IEEEEEEA",
            "AEEEEEEI                     IIIII                     IEEEEEEA",
            "AL    LI                     IIIII                     IL    LA",
            "AEEEEEEI                     IIIII                     IEEEEEEA",
            "AEEEEEEI                     I   I                     IEEEEEEA",
            "AL   LLI                     I   I                     IL    LA",
            "AEEEEEEI                     I   I                     IEEEEEEA",
            "AEEEEEEI                    KI   IK                    IEEEEEEA",
            "IEEEEEEI                    KI   IK                    IEEEEEEI",
            "ICCCCCCIKKKKKKK             KI   IK            KKKKKKKKICCCCCCI",
            "I      IBBBBBBBKKK         DKI   IKD        KKKBBBBBBBBI      I",
            "ICCCCCCI          II      IBBBBBBBBBI     II   KKKKKKKKICCCCCCI" },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                               D                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            " IIIII                                                   IIIII ",
            "IE   EI                                                 IE   EI",
            "AE   EA                                                 AE   EA",
            "AL   LA                                                 AL   LA",
            "AE   EA                                                 AE   EA",
            "AE   EA                                                 AE   EA",
            "AL   LA                                                 AL   LA",
            "AEEEEEA                                                 AEEEEEA",
            "AEEEEEA                        I                        AEEEEEA",
            "AL   LA                       III                       AL   LA",
            "AEEEEEA                       III                       AEEEEEA",
            "AEEEEEA                       III                       AEEEEEA",
            "AL   LA                       III                       AL   LA",
            "AEEEEEA                       III                       AEEEEEA",
            "AEEEEEA                      KIIIK                      AEEEEEA",
            "IEEEEEI                      KIIIK                      IEEEEEI",
            "ICCCCCI                      KIIIK                      ICCCCCI",
            "I     IKKKKKKKK            DDKIIIKDD           KKKKKKKKKI     I",
            "ICCCCCI        III        IBBBBBBBBBI       III         ICCCCCI" },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                               D                               ",
            "                               D                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            " IIIII                                                   IIIII ",
            "IEEEEEI                                                 IEEEEEI",
            "AEEEEEA                                                 AEEEEEA",
            "ALLLLLA                                                 ALLLLLA",
            "AEEEEEA                                                 AEEEEEA",
            "AEEEEEA                                                 AEEEEEA",
            "ALLLLLA                                                 ALLLLLA",
            "AEEEEEA                                                 AEEEEEA",
            "AEEEEEA                                                 AEEEEEA",
            "ALLLLLA                                                 ALLLLLA",
            "AEEEEEA                        I                        AEEEEEA",
            "AEEEEEA                        I                        AEEEEEA",
            "ALLLLLA                        I                        ALLLLLA",
            "AEEEEEA                        I                        AEEEEEA",
            "AEEEEEA                        I                        AEEEEEA",
            "IEEEEEI                        I                        IEEEEEI",
            "ICCCCCI                        I                        ICCCCCI",
            "I     I                     DDIIIDD                     I     I",
            "ICCCCCIIIIIIIII            IBBBBBBBI           IIIIIIIIIICCCCCI" },
        { "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "                                                               ",
            "IIIIIII                                                 IIIIIII",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IAAAAAI                                                 IAAAAAI",
            "IIIIIII                                                 IIIIIII",
            "IIIIIII                        I                        IIIIIII",
            "IIIIIII                       III                       IIIIIII",
            "IIIIIII                     IIIIIII                     IIIIIII" } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType("搂c搂l鑰佺櫥鐨勪紶濂囬€犵墿 - 宸ㄥ瀷PCB宸ュ巶涓ㄨ创鐗囧伐鍧婁辅NMD鏅跺渾鍘備辅鍏冧欢鑱氬悎鑰?)
     * .addInfo("搂e搂l涓?浣?鍗?涓?")
     * .addInfo("搂5浠庢煇鍗婂矝绁炵鍖楁柟鍥藉寮曡繘鐨勫皷绔珮绉戞妧宸ㄦ瀯")
     * .addInfo("搂5鍒╃敤銆屾仼鎯呫€嶇殑鍔涢噺灏嗗師鏉愭枡涓€姝ヨ浆鍖栦负鐢佃矾鏉挎墍闇€瑕佺殑鏉愭枡")
     * .addInfo("搂5鍏锋湁浠や汉鐣忔儳鐨勮壇鍝佺巼鍜屾鏃犵憰鐤电殑绮惧瘑搴?)
     * .addInfo("搂a娑堣€楁仼鎯呰繍琛?: 1t/涓?)
     * .addInfo("鍦ㄩ厤缃枃浠惰缃厤鏂筃EI浜у嚭鍊嶇巼, 榛樿涓?.5, 鍥涜垗浜斿叆")
     * .addInfo("--------------------")
     * .addInfo("搂b涓绘満鏀惧叆涓€鍥㈣渹缇? 鎸夌収鍫嗗彔鏁伴噺鑾峰緱鐩稿簲鍔犻€?log2(铚傜兢鏁伴噺), 鑸嶅純灏忔暟鍚戜笅鍙栨暣)")
     * .addInfo("搂b纰崇撼绫宠渹缇?-- -2%")
     * .addInfo("搂b瓒呮椂绌鸿渹缇?-- 30%")
     * .addInfo("搂b姘告亽铚傜兢 -- 75%")
     * .addInfo("搂b瀹囧畽绱犺渹缇?-- 150%")
     * .addInfo("--------------------")
     * .addInfo("sfg淇￠亾 = 0 瑙ｉ攣PCB宸ュ巶")
     * .addInfo("sfg淇￠亾 = 2 瑙ｉ攣璐寸墖宸ュ潑")
     * .addInfo("sfg淇￠亾 = 5 瑙ｉ攣NMD鏅跺渾鍘?)
     * .addInfo("sfg淇￠亾 = 8 瑙ｉ攣鍏冧欢鑱氬悎鑰?)
     * .addInfo("鏇存崲鍔涘満鍚庣粨鏋勪笉鎴愬瀷璇烽噸鏀句富鏈?)
     * .addTecTechHatchInfo()
     * .addSeparator()
     * .addController("绾㈡棩鎭╂儏宸ュ潑")
     * .beginStructureBlock(63, 32, 63, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .addMufflerHatch("AnyMufflerHatch", 1)
     * .toolTipFinisher("搂a123Technology - 搂cBeautiful World!");
     * return tt;
     * }
     */
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
            .addController(translateToLocal("ote.tn.sf"))
            .beginStructureBlock(63, 32, 63, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("搂a123Technology - 搂cBeautiful World!");
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
        return new OTESunFactory(this.mName);
    }

    @Override
    protected OTHSoundResource getProcessStartSoundOTH() {
        if (is_Enqing_Song_Play) return OTHSoundResource.ENQING;
        return super.getProcessStartSoundOTH();
    }

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
}
