package com.newmaa.othtech.machine;

import com.github.bartimaeusnek.bartworks.API.BorosilicateGlass;
import com.github.technus.tectech.thing.block.QuantumGlassBlock;
import com.github.technus.tectech.thing.casing.TT_Container_Casings;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.gtnhintergalactic.block.IGBlocks;
import com.gtnewhorizons.gtnhintergalactic.recipe.IGRecipeMaps;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTH_processingLogics.OTH_ProcessingLogic;
import galaxyspace.core.register.GSBlocks;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_HatchElementBuilder;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Utility;
import gregtech.common.items.GT_IntegratedCircuit_Item;
import gregtech.common.items.GT_MetaGenerated_Item_03;
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
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
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.github.technus.tectech.thing.casing.TT_Container_Casings.*;
import static com.github.technus.tectech.thing.casing.TT_Container_Casings.sBlockCasingsTT;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTech_API.*;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_OFF;
import static gregtech.api.util.GT_StructureUtility.ofFrame;

public class GT_TE_SunFactory extends OTH_MultiMachineBase<GT_TE_SunFactory>{
    public GT_TE_SunFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_SunFactory(String aName) {
        super(aName);
    }

    private int stabilisationFieldMetadata = 0;

    private byte mode = 0;

    private byte glassTier = 0;

    private double Beeyonds = 0;
    private String BYDS = "0";



    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mode", mode);
        aNBT.setByte("glassTier", glassTier);
        aNBT.setDouble("speedBonus", Beeyonds);
        aNBT.setInteger("stabilisationfieldgenerators", stabilisationFieldMetadata);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mode = aNBT.getByte("mode");
        glassTier = aNBT.getByte("glassTier");
        Beeyonds = aNBT.getDouble("speedBonus");
        stabilisationFieldMetadata = aNBT.getInteger("stabilisationfieldgenerators");

    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    protected int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }


    private static double log(double value, double base) {
        return Math.log(value) / Math.log(base);
    }
    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
                             IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "耗时倍率" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.RED
                + tag.getString("speedBonus")
                + EnumChatFormatting.RESET
                + " (Double)");
    }
    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
                                int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setString("speedBonus", BYDS);
        }
    }

    protected float getSpeedBonus() {

        return (float)Beeyonds;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (mode == 0) return Recipemaps.SF1;
        if (mode == 2) return Recipemaps.SF2;
        if (mode == 3) return Recipemaps.SF3;
        return Recipemaps.SF4;
    }
    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(Recipemaps.SF1, Recipemaps.SF2, Recipemaps.SF3, Recipemaps.SF4);
    }
    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (getBaseMetaTileEntity().isServerSide()) {
            int modeAmount;
            if (stabilisationFieldMetadata >= 8) {
                modeAmount = 4;
            } else if (stabilisationFieldMetadata >= 5) {
                modeAmount = 3;
            } else if (stabilisationFieldMetadata >= 2) {
                    modeAmount = 2;
            } else {
                modeAmount = 1;
            }
            this.mode = (byte) ((this.mode + 1) % modeAmount);
            GT_Utility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal(mode == 0 ? "巨型PCB工厂模式" : mode == 1 ? "贴片工坊模式" : mode == 2 ? "NMD晶圆厂模式" : mode == 3 ? "元件批发者模式" : "null"));}
    }
    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new OTH_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 2 : 1, 2);
                return super.process();
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        if (stabilisationFieldMetadata < 2 && mode > 0) return CheckRecipeResultRegistry.INTERNAL_ERROR;
        if (stabilisationFieldMetadata < 5 && mode > 1) return CheckRecipeResultRegistry.INTERNAL_ERROR;
        if (stabilisationFieldMetadata < 8 && mode > 2) return CheckRecipeResultRegistry.INTERNAL_ERROR;

        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        // inputs are consumed at this point
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);


        return result;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        ItemStack items = getControllerSlot();
        if (items != null && items.getItem() instanceof GT_MetaGenerated_Item_03
            && items.getItemDamage() == 4010
            && items.stackSize >= 1) {
            this.Beeyonds = -0.02F * Math.floor(log(items.stackSize, 2)) / 100;
        }
        if (items != null && items.getItem() instanceof GT_MetaGenerated_Item_03
            && items.getItemDamage() == 4581
            && items.stackSize >= 1) {
            this.Beeyonds = 1F * Math.floor(log(items.stackSize, 2)) / 100F;
        }
        if (items != null && items.getItem() instanceof GT_MetaGenerated_Item_03
            && items.getItemDamage() == 4139
            && items.stackSize >= 1) {
            this.Beeyonds = 3F * Math.floor(log(items.stackSize, 2)) / 100;
        }
        if (items != null && items.getItem() instanceof GT_MetaGenerated_Item_03
            && items.getItemDamage() == 4141
            && items.stackSize >= 1) {
            this.Beeyonds = 5F * Math.floor(log(items.stackSize, 2)) / 100;
        }
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

        return this.survivialBuildPiece(
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
    private static IStructureDefinition<GT_TE_SunFactory> STRUCTURE_DEFINITION = null;


    @Override
    public IStructureDefinition<GT_TE_SunFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_SunFactory>builder()
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
                .addElement('G', ofBlock(IGBlocks.SpaceElevatorCasing, 1))
                .addElement('H', ofBlock(sBlockCasingsTT, 6))
                .addElement('J',
                    withChannel("stabilisationfieldgenerators",
                        ofBlocksTiered(
                            GT_TE_MegaQFTFake::getBlockStabilisationFieldGeneratorTier,
                            ImmutableList.of(
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 0),
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 1),
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 2),
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 3),
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 4),
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 5),
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 6),
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 7),
                                Pair.of(TT_Container_Casings.StabilisationFieldGenerators, 8)),
                            0,
                            (t, meta) -> t.stabilisationFieldMetadata = meta,
                            t -> t.stabilisationFieldMetadata)))
                .addElement('K', ofBlock(QuantumGlassBlock.INSTANCE, 0))
                .addElement('L', ofFrame(Materials.InfinityCatalyst))
                .addElement(
                    'I',
                    GT_HatchElementBuilder.<GT_TE_SunFactory>builder()
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(GT_TE_SunFactory::addToMachineList)
                        .dot(1)
                        .casingIndex(1024 + 12)
                        .buildAndChain(sBlockCasingsTT, 12))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structure by JulianChum Version 1 & 2
    private final String[][] shapeMain = new String[][]{{
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
        "IIIIIII                     IIIIIII                     IIIIIII"
    },{
        "                                                               ",
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
        "ICCCCCIIIIIIIIII           IBBBBBBBI            IIIIIIIIICCCCCI"
    },{
        "                                                               ",
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
        "ICCCCCI         III       IBBBBBBBBBI        III        ICCCCCI"
    },{
        "                                                               ",
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
        "I      IBBBBBBBBKKK        DKI   IKI         KKKBBBBBBBI      I",
        "ICCCCCCIKKKKKKKK   II     IBBBBBBBBBI      II          ICCCCCCI"
    },{
        "                                                               ",
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
        "ICCCCCCCI       KKK         I     II         KKK      ICCCCCCCI",
        "I       IKKKKKKKBBBKK     III     III      KKBBBKKKKKKI       I",
        "ICCCCCCCI       KKK  I    IBBBBBBBBBI    II           ICCCCCCCI"
    },{
        "                                                               ",
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
        "ICCCCCCCCI         KK I   IBBBBIBBBBI   I  KK        ICCCCCCCCI"
    },{
        "                                                               ",
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
        "IIICCCCCCCIII        K I  IBBBBBBBBBI  I KK       IIICCCCCCCIII"
    },{
        "                                                               ",
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
        " I ICCCCCCCCIII       K I IBBBBBBBBBI I K       IIICCCCCCCCI I "
    },{
        "                                                               ",
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
        " I  ICCCCCCCCCIIII     K IIBBBBBBBBBII K     IIIICCCCCCCCCIK I "
    },{
        "                                                               ",
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
        " I   ICCCCCCCCCCCIIIII K IIIBBBBBBBIII K IIIIICCCCCCCCCCCI K I "
    },{
        "                                                               ",
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
        " I    ICCCCCCCCCCCCCCIIIIIIIIIIIIIIIIIIIIICCCCCCCCCCCCCCI  K I "
    },{
        "                                                               ",
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
        " I    ICCCCCCCCCCCIIIFFFFFFFCCCCCCCFFFFFFFIIICCCCCCCCCCCI  K I "
    },{
        "                                                               ",
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
        " I    IICCCCCCCCIIFFF    CCCCCCCCCCCCC    FFFIICCCCCCCCII  K I "
    },{
        "                               G                               ",
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
        " I     ICCCCCCCIFF       CCCCCCCCCCCCC       FFICCCCCCCI   K I "
    },{
        "                               G                               ",
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
        " I     IICCCCCIF         CCCCCCCCCCCCC         FICCCCCII   K I "
    },{
        "                               G                               ",
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
        "  I     ICCCCIF          CCCCCCCCCCCCC          FICCCCI    K I "
    },{
        "                               G                               ",
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
        "  I     ICCCIF           CCCCCCCCCCCCC           FICCCI   K I  "
    },{
        "                               G                               ",
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
        "  I     IICCIF           CCCCCCCCCCCCC           FICCII   K I  "
    },{
        "                               G                               ",
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
        "   I K   ICIF          CCCCCCCCCCCCCCCCC          FICI    K I  "
    },{
        "                               G                               ",
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
        "   I K   ICIF         CCCCCCCCCCCCCCCCCCC         FICI   K I   "
    },{
        "                               G                               ",
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
        "    I K  ICIF        CCCCCCCCCCCCCCCCCCCCC        FICI   K I   "
    },{
        "                               G                               ",
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
        "    I K  IIF        CCCCCCCCCCCCCCCCCCCCCCC        FII  K I    "
    },{
        "                                                               ",
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
        "     I K  IF       CCCCCCCCCCCCCCCCCCCCCCCCC       FI  K I     "
    },{
        "                                                               ",
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
        "      I KKIF      CCCCCCCCCCCCCCCCCCCCCCCCCCC      FIKK I      "
    },{
        "                                                               ",
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
        "       I  IF      CCCCCCCCCCCCCCCCCCCCCCCCCCC      FI  I       "
    },{
        "                                                               ",
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
        "        IIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIII        "
    },{
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
        "  IIIIIIIIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIIIIIIIII  "
    },{
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
        "    IIIDD IF         KCDDDDDJJDDDJJDDDDDCK         FI DDII     ",
        "  DIIIIID IF        KBCDDDDDCCDDDDCDDDDDCBK        FI DIIIIDD  ",
        " IBBBBBBBIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIIBBBBBBBI "
    },{
        "                                                               ",
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
        "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI"
    },{
        "                                                               ",
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
        "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI"
    },{
        "                                                               ",
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
        "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI"
    },{
        "             GGGGGGGGG                   GGGGGGGGG             ",
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
        "IBBBBIBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBIBBBBI"
    },{
        "                                                               ",
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
        "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI"
    },{
        "                                                               ",
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
        "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI"
    },{
        "                                                               ",
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
        "IBBBBBBBBBICCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCIBBBBBBBBBI"
    },{
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
        "     IIDD IF         KCDDDDDJJDDDJJDDDDDCK         FI DDIII    ",
        "  DDIIIID IF        KBCDDDDDCDDDDCCDDDDDCBK        FI DIIIIID  ",
        " IBBBBBBBIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIIBBBBBBBI "
    },{
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
        "  IIIIIIIIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIIIIIIIII  "
    },{
        "                                                               ",
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
        "        IIIFCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCFIII        "
    },{
        "                                                               ",
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
        "       I  IF      CCCCCCCCCCCCCCCCCCCCCCCCCCC      FI  I       "
    },{
        "                                                               ",
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
        "      I KKIF      CCCCCCCCCCCCCCCCCCCCCCCCCCC      FIKK I      "
    },{
        "                                                               ",
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
        "     I K  IF       CCCCCCCCCCCCCCCCCCCCCCCCC       FI  K I     "
    },{
        "                               G                               ",
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
        "    I K  IIF        CCCCCCCCCCCCCCCCCCCCCCC        FII  K I    "
    },{
        "                               G                               ",
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
        "   I K   ICIF        CCCCCCCCCCCCCCCCCCCCC        FICI  K I    "
    },{
        "                               G                               ",
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
        "   I K   ICIF         CCCCCCCCCCCCCCCCCCC         FICI   K I   "
    },{
        "                               G                               ",
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
        "  I K    ICIF          CCCCCCCCCCCCCCCCC          FICI   K I   "
    },{
        "                               G                               ",
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
        "  I K   IICCIF           CCCCCCCCCCCCC           FICCII     I  "
    },{
        "                               G                               ",
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
        "  I K   ICCCIF           CCCCCCCCCCCCC           FICCCI     I  "
    },{
        "                               G                               ",
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
        " I K    ICCCCIF          CCCCCCCCCCCCC          FICCCCI     I  "
    },{
        "                               G                               ",
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
        " I K   IICCCCCIF         CCCCCCCCCCCCC         FICCCCCII     I "
    },{
        "                               G                               ",
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
        " I K   ICCCCCCCIFF       CCCCCCCCCCCCC       FFICCCCCCCI     I "
    },{
        "                                                               ",
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
        " I K  IICCCCCCCCIIFFF    CCCCCCCCCCCCC    FFFIICCCCCCCCII    I "
    },{
        "                                                               ",
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
        " I K  ICCCCCCCCCCCIIIFFFFFFFCCCCCCCFFFFFFFIIICCCCCCCCCCCI    I "
    },{
        "                                                               ",
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
        " I K  ICCCCCCCCCCCCCCIIIIIIIIIIIIIIIIIIIIICCCCCCCCCCCCCCI    I "
    },{
        "                                                               ",
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
        " I K ICCCCCCCCCCCIIIII K IIIBBBBBBBIII K IIIIICCCCCCCCCCCI   I "
    },{
        "                                                               ",
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
        " I KICCCCCCCCCIIII     K IIBBBBBBBBBII K     IIIICCCCCCCCCI  I "
    },{
        "                                                               ",
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
        " I ICCCCCCCCIII       K I IBBBBBBBBBI I K       IIICCCCCCCCI I "
    },{
        "                                                               ",
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
        "IIICCCCCCCIII       KK I  IBBBBBBBBBI  I K        IIICCCCCCCIII"
    },{
        "                                                               ",
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
        "ICCCCCCCCI        KK  I   IBBBBIBBBBI   I KK         ICCCCCCCCI"
    },{
        "                                                               ",
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
        "ICCCCCCCI      KKK         II     I         KKK       ICCCCCCCI",
        "I       IKKKKKKBBBKK      III     III     KKBBBKKKKKKKI       I",
        "ICCCCCCCI           II    IBBBBBBBBBI    I  KKK       ICCCCCCCI"
    },{
        "                                                               ",
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
        "I      IBBBBBBBKKK         IKI   IKD        KKKBBBBBBBBI      I",
        "ICCCCCCI          II      IBBBBBBBBBI     II   KKKKKKKKICCCCCCI"
    },{
        "                                                               ",
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
        "ICCCCCI        III        IBBBBBBBBBI       III         ICCCCCI"
    },{
        "                                                               ",
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
        "ICCCCCIIIIIIIII            IBBBBBBBI           IIIIIIIIIICCCCCI"
    },{
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
        "IIIIIII                     IIIIIII                     IIIIIII"
    }};
    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§c§l老登的传奇造物 - 巨型PCB工厂丨贴片工坊丨NMD晶圆厂丨元件聚合者")
            .addInfo("§e§l忠!诚!南!下!")
            .addInfo("§5从某半岛神秘北方国家引进的尖端高科技巨构")
            .addInfo("§5利用「恩情」的力量将原材料一步转化为电路板所需要的材料")
            .addInfo("§5具有令人畏惧的良品率和毫无瑕疵的精密度")
            .addInfo("--------------------")
            .addInfo("§b主机放入一团蜂群, 按照堆叠数量获得相应加速(log2(蜂群数量), 舍弃小数向下取整)")
            .addInfo("§b碳纳米蜂群 -- -2%")
            .addInfo("§b超时空蜂群 -- 1%")
            .addInfo("§b永恒蜂群 -- 3%")
            .addInfo("§b宇宙素蜂群 -- 5%")
            .addInfo("--------------------")
            .addInfo("stabilisationfieldgenerators信道 = 0 解锁PCB工厂")
            .addInfo("stabilisationfieldgenerators信道 = 2 解锁贴片工坊")
            .addInfo("stabilisationfieldgenerators信道 = 5 解锁NMD晶圆厂")
            .addInfo("stabilisationfieldgenerators信道 = 8 解锁元件聚合者")
            .addInfo("更换力场后结构不成型请重放主机")
            .addInfo("§q支持§bTecTech§q能源仓及激光仓，但不支持无线电网直接供给EU")
            .addSeparator()
            .addController("红日恩情工坊")
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
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_TE_SunFactory(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
                                 final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GT_Utility.getCasingTextureIndex(sBlockCasings1, 12)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_ON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GT_Utility.getCasingTextureIndex(sBlockCasings1, 12)),
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
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GT_Utility.getCasingTextureIndex(sBlockCasings1, 12)) };
    }
}
