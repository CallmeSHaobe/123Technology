package com.newmaa.othtech.machine;

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
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.recipemap.Recipemaps;
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
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
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

    @Override
    public int totalMachineMode() {
        return 4;
    }

    @Override
    public void setMachineModeIcons() {
        super.setMachineModeIcons();
    }

    @Override
    public String getMachineModeName(int mode) {
        return switch (mode) {
            case 0 -> "巨型PCB工厂模式";
            case 1 -> "贴片工坊模式";
            case 2 -> "NMD晶圆厂模式";
            case 3 -> "原件批发者模式";
            default -> "mode";
        };
    }

    @Override
    public void setMachineMode(int index) {
        super.setMachineMode(index);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mode", machineMode);
        aNBT.setByte("glassTier", glassTier);
        aNBT.setDouble("speedBonus", Beeyonds);
        aNBT.setInteger("sfg", stabilisationFieldMetadata);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        machineMode = aNBT.getByte("mode");
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
            stopMachine();
            return false;
        }

        return super.onRunningTick(aStack);
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
            BYDS = GTUtility.formatNumbers(getSpeedBonus());
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

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        /*
         * if (getBaseMetaTileEntity().isServerSide()) {
         * if (supportsMachineModeSwitch()) {
         * setMachineMode(nextMachineMode()); {
         * int modeAmount;
         * if (stabilisationFieldMetadata >= 8) {
         * modeAmount = 4;
         * } else if (stabilisationFieldMetadata >= 5) {
         * modeAmount = 3;
         * } else if (stabilisationFieldMetadata >= 2) {
         * modeAmount = 2;
         * } else {
         * modeAmount = 1;
         * }
         * this.machineMode = (byte) ((this.machineMode + 1) % modeAmount);
         * GTUtility.sendChatToPlayer(
         * aPlayer,
         * StatCollector.translateToLocal(
         * machineMode == 0 ? "巨型PCB工厂模式"
         * : machineMode == 1 ? "贴片工坊模式" : machineMode == 2 ? "NMD晶圆厂模式" : machineMode == 3 ? "元件批发者模式" : "null"));
         * }
         * }
         * }
         */
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
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
                        .dot(1)
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
     * tt.addMachineType("§c§l老登的传奇造物 - 巨型PCB工厂丨贴片工坊丨NMD晶圆厂丨元件聚合者")
     * .addInfo("§e§l主!体!南!下!")
     * .addInfo("§5从某半岛神秘北方国家引进的尖端高科技巨构")
     * .addInfo("§5利用「恩情」的力量将原材料一步转化为电路板所需要的材料")
     * .addInfo("§5具有令人畏惧的良品率和毫无瑕疵的精密度")
     * .addInfo("§a消耗恩情运行 : 1t/个")
     * .addInfo("在配置文件设置配方NEI产出倍率, 默认为1.5, 四舍五入")
     * .addInfo("--------------------")
     * .addInfo("§b主机放入一团蜂群, 按照堆叠数量获得相应加速(log2(蜂群数量), 舍弃小数向下取整)")
     * .addInfo("§b碳纳米蜂群 -- -2%")
     * .addInfo("§b超时空蜂群 -- 30%")
     * .addInfo("§b永恒蜂群 -- 75%")
     * .addInfo("§b宇宙素蜂群 -- 150%")
     * .addInfo("--------------------")
     * .addInfo("sfg信道 = 0 解锁PCB工厂")
     * .addInfo("sfg信道 = 2 解锁贴片工坊")
     * .addInfo("sfg信道 = 5 解锁NMD晶圆厂")
     * .addInfo("sfg信道 = 8 解锁元件聚合者")
     * .addInfo("更换力场后结构不成型请重放主机")
     * .addTecTechHatchInfo()
     * .addSeparator()
     * .addController("红日恩情工坊")
     * .beginStructureBlock(63, 32, 63, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .addMufflerHatch("AnyMufflerHatch", 1)
     * .toolTipFinisher("§a123Technology - §cBeautiful World!");
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
            .toolTipFinisher("§a123Technology - §cBeautiful World!");
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
