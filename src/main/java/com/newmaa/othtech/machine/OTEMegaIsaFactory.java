package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.utils.Utils;

import bartworks.API.BorosilicateGlass;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.TAE;
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
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEMegaIsaFactory extends OTHMultiMachineBase<OTEMegaIsaFactory> {

    public OTEMegaIsaFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaIsaFactory(String aName) {
        super(aName);
    }

    private int tierMill = 0;
    private byte glassTier = 0;

    private HeatingCoilLevel coilLevel;

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
        aNBT.setInteger("millTier", tierMill);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        glassTier = aNBT.getByte("glassTier");
        tierMill = aNBT.getInteger("millTier");
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return 123;
    }

    protected float getSpeedBonus() {
        return (float) 1 / getCoilTier();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.MISA;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String bonus = GTUtility.formatNumbers(tierMill);
            tag.setString("tierMill", bonus);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("otht.waila.isa.tier") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.DARK_GREEN
                + tag.getString("tierMill")
                + EnumChatFormatting.RESET);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new OTHProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                setEuModifier(Math.pow(glassTier, -0.1D));
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mSpecialValue > tierMill) {
                    return SimpleCheckRecipeResult.ofFailure("tierMill");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {

        repairMachine();

        return checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);

    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aTick % 20 == 0) {
            ItemStack controllerSlotItemStack = getControllerSlot();
            if (controllerSlotItemStack != null) {
                if (GTUtility.areStacksEqual(controllerSlotItemStack, OTHItemList.IsaNEI.get(1))) {
                    tierMill = 1;
                } else if (GTUtility.areStacksEqual(controllerSlotItemStack, OTHItemList.ISAIOS.get(1))) {
                    tierMill = 2;
                } else if (GTUtility.areStacksEqual(controllerSlotItemStack, OTHItemList.ISAHYP.get(1))) {
                    tierMill = 3;
                } else if (GTUtility.areStacksEqual(controllerSlotItemStack, OTHItemList.ISASPE.get(1))) {
                    tierMill = 4;
                }
            } else {
                tierMill = 0;
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
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

    private final int horizontalOffSet = 40;
    private final int verticalOffSet = 27;
    private final int depthOffSet = 2;
    private static IStructureDefinition<OTEMegaIsaFactory> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.misa.0") + ":",
        translateToLocal("ote.cm.misa.1") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEMegaIsaFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaIsaFactory>builder()
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
                .addElement('B', ofBlock(sBlockCasings4, 0))
                .addElement(
                    'C',
                    withChannel("coil", ofCoil(OTEMegaIsaFactory::setCoilLevel, OTEMegaIsaFactory::getCoilLevel)))
                .addElement('D', ofBlock(sBlockCasings8, 10))
                .addElement('E', ofBlock(ModBlocks.blockCasings3Misc, 1))
                .addElement('F', ofBlock(ModBlocks.blockCasings4Misc, 10))
                .addElement('G', ofBlock(ModBlocks.blockCasings5Misc, 0))
                .addElement('H', ofBlock(ModBlocks.blockCasings5Misc, 1))
                .addElement('I', ofBlock(ModBlocks.blockCasings5Misc, 2))
                .addElement('J', ofBlock(ModBlocks.blockSpecialMultiCasings, 9))
                .addElement(
                    'L',
                    buildHatchAdder(OTEMegaIsaFactory.class).atLeast(Muffler)
                        .adder(OTEMegaIsaFactory::addToMachineList)
                        .dot(2)
                        .casingIndex(TAE.getIndexFromPage(3, 10))
                        .buildAndChain(ModBlocks.blockCasings4Misc, 10))
                .addElement(
                    'M',
                    buildHatchAdder(OTEMegaIsaFactory.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEMegaIsaFactory::addToMachineList)
                        .dot(1)
                        .casingIndex(TAE.GTPP_INDEX(2))
                        .buildAndChain(ModBlocks.blockCasings5Misc, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by Monkey , LyeeR, NewMaa
    private final String[][] shapeMain = new String[][] {
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 DDD                                         DDD                 ",
            "                 DDD                                         DDD                 ",
            "                 DDD                                         DDD                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "               DDDDDDD                                     DDDDDDD               ",
            "            DDDDDDDDDDDDD                               DDDDDDDDDDDDD            ",
            "           DDDDDDDDDDDDDDD                             DDDDDDDDDDDDDDD           ",
            "         DDDDDDDDDDDDDDDDDDD                         DDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 DDD                                         DDD                 ",
            "                DDDDD                                       DDDDD                ",
            "                DDDDD                                       DDDDD                ",
            "                DDDDD                                       DDDDD                ",
            "                 DDD                                         DDD                 ",
            "                 D D                                         D D                 ",
            "                 D D                                         D D                 ",
            "                 D D                                         D D                 ",
            "               DDDDDDD                                     DDDDDDD               ",
            "            DDDDDDDDDDDDD                               DDDDDDDDDDDDD            ",
            "           DDDDDDDDDDDDDDD                             DDDDDDDDDDDDDDD           ",
            "         DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "               GGGGGGG                                     GGGGGGG               ",
            "             GGGGGGGGGGG                                 GGGGGGGGGGG             ",
            "            GAAAAGGGAAAAG                               GAAAAGGGAAAAG            ",
            "            GAAAAAGAAAAAG                               GAAAAAGAAAAAG            ",
            "           GGAAAAAGAAAAAGG                             GGAAAAAGAAAAAGG           ",
            "           GGAAAAGGGAAAAGG                             GGAAAAGGGAAAAGG           ",
            "           GGGAAGCCCGAAGGG                             GGGAAGCCCGAAGGG           ",
            "           GGGGGGCCCGGGGGG                             GGGGGGCCCGGGGGG           ",
            "           GGGAAGCCCGAAGGG                             GGGAAGCCCGAAGGG           ",
            "           GGAAAAGGGAAAAGG                             GGAAAAGGGAAAAGG           ",
            "           GGAAAAAGAAAAAGG                             GGAAAAAGAAAAAGG           ",
            "            GAAAAAGAAAAAG                               GAAAAAGAAAAAG            ",
            "            GAAAAGGGAAAAG                               GAAAAGGGAAAAG            ",
            "             GGGGGGGGGGG               MMM               GGGGGGGGGGG             ",
            "            D  GGGGGGG  D             MM~MM             D  GGGGGGG  D            ",
            "           DDD  DDDDD  DDD            MMMMM            DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC   H   CG                                 GC   H   CG             ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G                             G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "             GC   H   CG                                 GC   H   CG             ",
            "             GGCCBBBCCGG              EEEEE              GGCCBBBCCGG             ",
            "             D GG   GG D              EEEEE              D GG   GG D             ",
            "            D    GGG    D   EEEEEEEEEEEEEEEEEEEEEEEEE   D    GGG    D            ",
            "           DDD  DDDDD  DDD  EEEEEEEEEEEEEEEEEEEEEEEEE  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "             GC       CG               EEE               GC       CG             ",
            "             GGCCBBBCCGG    EEEEEEEEEEEEEEEEEEEEEEEEE    GGCCBBBCCGG             ",
            "             D GG   GG D    EEEEEEEEEEEEEEEEEEEEEEEEE    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC  II II  CG            ",
            "             GC       CG    EEEEEEEEEEEEEEEEEEEEEEEEE    GC       CG             ",
            "             GGCCBBBCCGG    E                       E    GGCCBBBCCGG             ",
            "             D GG   GG D    E                       E    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                 GGG        JJJJJJJJJJJJJJJJJJJJJJJJJ        GGG                 ",
            "               GG   GG      JJJJJJJJJJJJJJJJJJJJJJJJJ      GG   GG               ",
            "             GGCCBBBCCGG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GGCCBBBCCGG             ",
            "             GC   H   CG                                 GC   H   CG             ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G                             G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B IIHCHII B G           ",
            "            GC I IHI I CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC I IHI I CG            ",
            "            GC  IIHII  CG   E                       E   GC  IIHII  CG            ",
            "             GC   H   CG    E                       E    GC   H   CG             ",
            "             GGCCBBBCCGG    E                       E    GGCCBBBCCGG             ",
            "             D GG   GG D    E                       E    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         ", },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                           J                         J                           ",
            "                 GGG       J                         J       GGG                 ",
            "               GG   GG     J                         J     GG   GG               ",
            "             GGCCBBBCCGG   J                         J   GGCCBBBCCGG             ",
            "             GC       CG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B IIHCHII B G           ",
            "           G B   CCC   B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B   CCC   B G           ",
            "           G B IIHCHII B G  E                       E  G B IIHCHII B G           ",
            "            GC I I I I CG   A                       A   GC I I I I CG            ",
            "            GC  II II  CG   A                       A   GC  II II  CG            ",
            "             GC       CG    A                       A    GC       CG             ",
            "             GGCCBBBCCGG    A                       A    GGCCBBBCCGG             ",
            "             D GG   GG D    A                       A    D GG   GG D             ",
            "            D    GGG    D   A                       A   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                           J                         J                           ",
            "                 GGG       J                         J       GGG                 ",
            "               GG   GG     J                         J     GG   GG               ",
            "             GGCCBBBCCGG   J                         J   GGCCBBBCCGG             ",
            "             GC       CG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GC       CG             ",
            "            GC  II II  CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC  II II  CG            ",
            "            GC I I I I CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC I I I I CG            ",
            "           G B IIHCHII B G  E                       E  G B IIHCHII B G           ",
            "           G B   CCC   B G  A                       A  G B   CCC   B G           ",
            "           G B IIHCHII B G  A                       A  G B IIHCHII B G           ",
            "            GC I I I I CG   A                       A   GC I I I I CG            ",
            "            GC  II II  CG   A                       A   GC  II II  CG            ",
            "             GC       CG    A                       A    GC       CG             ",
            "             GGCCBBBCCGG    A                       A    GGCCBBBCCGG             ",
            "             D GG   GG D    A                       A    D GG   GG D             ",
            "            D    GGG    D   A                       A   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                           J                         J                           ",
            "                 GGG       J                         J       GGG                 ",
            "               GG   GG     J                         J     GG   GG               ",
            "             GGCCBBBCCGG   J                         J   GGCCBBBCCGG             ",
            "             GC   H   CG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GC   H   CG             ",
            "            GC  IIHII  CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC  IIHII  CG            ",
            "            GC I IHI I CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC I IHI I CG            ",
            "           G B IIHCHII B G  E                       E  G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G  A                       A  G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G  A                       A  G B IIHCHII B G           ",
            "            GC I IHI I CG   A                       A   GC I IHI I CG            ",
            "            GC  IIHII  CG   A                       A   GC  IIHII  CG            ",
            "             GC   H   CG    A                       A    GC   H   CG             ",
            "             GGCCBBBCCGG    A                       A    GGCCBBBCCGG             ",
            "             D GG   GG D    A                       A    D GG   GG D             ",
            "            D    GGG    D   A                       A   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                           J                         J                           ",
            "                 GGG       J                         J       GGG                 ",
            "               GG   GG     J                         J     GG   GG               ",
            "             GGCCBBBCCGG   J                         J   GGCCBBBCCGG             ",
            "             GC       CG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B IIHCHII B G           ",
            "           G B   CCC   B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B   CCC   B G           ",
            "           G B IIHCHII B G  E                       E  G B IIHCHII B G           ",
            "            GC I I I I CG   A                       A   GC I I I I CG            ",
            "            GC  II II  CG   A                       A   GC  II II  CG            ",
            "             GC       CG    A                       A    GC       CG             ",
            "             GGCCBBBCCGG    A                       A    GGCCBBBCCGG             ",
            "             D GG   GG D    A                       A    D GG   GG D             ",
            "            D    GGG    D   A                       A   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                 GGG        JJJJJJJJJJJJJJJJJJJJJJJJJ        GGG                 ",
            "               GG   GG      JJJJJJJJJJJJJJJJJJJJJJJJJ      GG   GG               ",
            "             GGCCBBBCCGG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B IIHCHII B G           ",
            "            GC I I I I CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC I I I I CG            ",
            "            GC  II II  CG   E                       E   GC  II II  CG            ",
            "             GC       CG    E                       E    GC       CG             ",
            "             GGCCBBBCCGG    E                       E    GGCCBBBCCGG             ",
            "             D GG   GG D    E                       E    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC   H   CG                                 GC   H   CG             ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G                             G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "            GC  IIHII  CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC  IIHII  CG            ",
            "             GC   H   CG    EEEEEEEEEEEEEEEEEEEEEEEEE    GC   H   CG             ",
            "             GGCCBBBCCGG    E                       E    GGCCBBBCCGG             ",
            "             D GG   GG D    E                       E    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "             GC       CG                                 GC       CG             ",
            "             GGCCBBBCCGG    EEEEEEEEEEEEEEEEEEEEEEEEE    GGCCBBBCCGG             ",
            "             D GG   GG D    EEEEEEEEEEEEEEEEEEEEEEEEE    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "             GC       CG                                 GC       CG             ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             D GG   GG D                                 D GG   GG D             ",
            "            D    GGG    D   EEEEEEEEEEEEEEEEEEEEEEEEE   D    GGG    D            ",
            "           DDD  DDDDD  DDD  EEEEEEEEEEEEEEEEEEEEEEEEE  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC   H   CG                                 GC   H   CG             ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G                             G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "             GC   H   CG                                 GC   H   CG             ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             D GG   GG D                                 D GG   GG D             ",
            "            D    GGG    D                               D    GGG    D            ",
            "           DDD  DDDDD  DDD                             DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "             GC       CG                                 GC       CG             ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             D GG   GG D                                 D GG   GG D             ",
            "            D    GGG    D                               D    GGG    D            ",
            "           DDD  DDDDD  DDD                             DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                 GGG            AAAAAAAAAAAAAAAAA            GGG                 ",
            "               GG   GG          AAAAAAAAAAAAAAAAA          GG   GG               ",
            "             GGCCBBBCCGG        AAAAAAAAAAAAAAAAA        GGCCBBBCCGG             ",
            "             GC       CG        AAAAAAAAAAAAAAAAA        GC       CG             ",
            "            GC  II II  CG       AAAAAAAAAAAAAAAAA       GC  II II  CG            ",
            "            GC I I I I CG       AAAAAAAAAAAAAAAAA       GC I I I I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G B   CCC   B G      FAAAAAAAAAAAAAAAF      G B   CCC   B G           ",
            "           G B IIHCHII B G     FFAAAAAAAAAAAAAAAFF     G B IIHCHII B G           ",
            "            GC I I I I CG      FFAAAAAAAAAAAAAAAFF      GC I I I I CG            ",
            "            GC  II II  CG     FFAAAAAAAAAAAAAAAAAFF     GC  II II  CG            ",
            "             GC       CG      FFAAAAAAAAAAAAAAAAAFF      GC       CG             ",
            "             GGCCBBBCCGG     FFFAAAAAAAAAAAAAAAAAFFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FFAAAAAAAAAAAAAAAAAAAFF     D GG   GG D             ",
            "            D    GGG    D   FFFAAAAAAAAAAAAAAAAAAAFFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFFAAAAAAAAAAAAAAAAAAAFFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFFFFFFFFFFFFFFFFFFFFFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                 GGG            ACCCCCCCCCCCCCCCA            GGG                 ",
            "               GG   GG          ACCCCCCCCCCCCCCCA          GG   GG               ",
            "             GGCCBBBCCGG        ACCCCCCCCCCCCCCCA        GGCCBBBCCGG             ",
            "             GC   H   CG        ACCCCCCCCCCCCCCCA        GC   H   CG             ",
            "            GC  IIHII  CG       ACCCCCCCCCCCCCCCA       GC  IIHII  CG            ",
            "            GC I IHI I CG       ACCCCCCCCCCCCCCCA       GC I IHI I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G      F               F      G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I IHI I CG      FF               FF      GC I IHI I CG            ",
            "            GC  IIHII  CG     FF                 FF     GC  IIHII  CG            ",
            "             GC   H   CG      FF                 FF      GC   H   CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFFFFFFFFFFFFFFFFFFFFFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                 GGG            AC             CA            GGG                 ",
            "               GG   GG          AC             CA          GG   GG               ",
            "             GGCCBBBCCGG        AC             CA        GGCCBBBCCGG             ",
            "             GC       CG        AC             CA        GC       CG             ",
            "            GC  II II  CG       AC             CA       GC  II II  CG            ",
            "            GC I I I I CG       AC             CA       GC I I I I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G B   CCC   B G      F               F      G B   CCC   B G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I I I I CG      FF               FF      GC I I I I CG            ",
            "            GC  II II  CG     FF                 FF     GC  II II  CG            ",
            "             GC       CG      FF                 FF      GC       CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFCCCCCCCCCCCCCCCCCCCFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                 GGG            AC             CA            GGG                 ",
            "               GG   GG          AC             CA          GG   GG               ",
            "             GGCCBBBCCGG        AC             CA        GGCCBBBCCGG             ",
            "             GC       CG        AC             CA        GC       CG             ",
            "            GC  II II  CG       AC             CA       GC  II II  CG            ",
            "            GC I I I I CG       AC             CA       GC I I I I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G B   CCC   B G      F               F      G B   CCC   B G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I I I I CG      FF               FF      GC I I I I CG            ",
            "            GC  II II  CG     FF                 FF     GC  II II  CG            ",
            "             GC       CG      FF                 FF      GC       CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFCCCCCCCCCCCCCCCCCCCFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                 GGG            AC             CA            GGG                 ",
            "               GG   GG          AC             CA          GG   GG               ",
            "             GGCCBBBCCGG        AC             CA        GGCCBBBCCGG             ",
            "             GC   H   CG        AC             CA        GC   H   CG             ",
            "            GC  IIHII  CG       AC             CA       GC  IIHII  CG            ",
            "            GC I IHI I CG       AC             CA       GC I IHI I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G      F               F      G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I IHI I CG      FF               FF      GC I IHI I CG            ",
            "            GC  IIHII  CG     FF                 FF     GC  IIHII  CG            ",
            "             GC   H   CG      FF                 FF      GC   H   CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFCCCCCCCCCCCCCCCCCCCFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFLLLFFFFFFF                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                 GGG            AC             CA            GGG                 ",
            "               GG   GG          AC             CA          GG   GG               ",
            "             GGCCBBBCCGG        AC             CA        GGCCBBBCCGG             ",
            "             GC       CG        AC             CA        GC       CG             ",
            "            GC  II II  CG       AC             CA       GC  II II  CG            ",
            "            GC I I I I CG       AC             CA       GC I I I I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G B   CCC   B G      F               F      G B   CCC   B G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I I I I CG      FF               FF      GC I I I I CG            ",
            "            GC  II II  CG     FF                 FF     GC  II II  CG            ",
            "             GC       CG      FF                 FF      GC       CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFCCCCCCCCCCCCCCCCCCCFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFLLLFFFFFFF                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                 GGG            AC             CA            GGG                 ",
            "               GG   GG          AC             CA          GG   GG               ",
            "             GGCCBBBCCGG        AC             CA        GGCCBBBCCGG             ",
            "             GC       CG        AC             CA        GC       CG             ",
            "            GC  II II  CG       AC             CA       GC  II II  CG            ",
            "            GC I I I I CG       AC             CA       GC I I I I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G B   CCC   B G      F               F      G B   CCC   B G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I I I I CG      FF               FF      GC I I I I CG            ",
            "            GC  II II  CG     FF                 FF     GC  II II  CG            ",
            "             GC       CG      FF                 FF      GC       CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFCCCCCCCCCCCCCCCCCCCFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                 GGG            AC             CA            GGG                 ",
            "               GG   GG          AC             CA          GG   GG               ",
            "             GGCCBBBCCGG        AC             CA        GGCCBBBCCGG             ",
            "             GC   H   CG        AC             CA        GC   H   CG             ",
            "            GC  IIHII  CG       AC             CA       GC  IIHII  CG            ",
            "            GC I IHI I CG       AC             CA       GC I IHI I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G      F               F      G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I IHI I CG      FF               FF      GC I IHI I CG            ",
            "            GC  IIHII  CG     FF                 FF     GC  IIHII  CG            ",
            "             GC   H   CG      FF                 FF      GC   H   CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFCCCCCCCCCCCCCCCCCCCFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                 GGG            AC             CA            GGG                 ",
            "               GG   GG          AC             CA          GG   GG               ",
            "             GGCCBBBCCGG        AC             CA        GGCCBBBCCGG             ",
            "             GC       CG        AC             CA        GC       CG             ",
            "            GC  II II  CG       AC             CA       GC  II II  CG            ",
            "            GC I I I I CG       AC             CA       GC I I I I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G B   CCC   B G      F               F      G B   CCC   B G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I I I I CG      FF               FF      GC I I I I CG            ",
            "            GC  II II  CG     FF                 FF     GC  II II  CG            ",
            "             GC       CG      FF                 FF      GC       CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFCCCCCCCCCCCCCCCCCCCFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                                AC             CA                                ",
            "                 GGG            AC             CA            GGG                 ",
            "               GG   GG          AC             CA          GG   GG               ",
            "             GGCCBBBCCGG        AC             CA        GGCCBBBCCGG             ",
            "             GC       CG        AC             CA        GC       CG             ",
            "            GC  II II  CG       AC             CA       GC  II II  CG            ",
            "            GC I I I I CG       AC             CA       GC I I I I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G B   CCC   B G      F               F      G B   CCC   B G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I I I I CG      FF               FF      GC I I I I CG            ",
            "            GC  II II  CG     FF                 FF     GC  II II  CG            ",
            "             GC       CG      FF                 FF      GC       CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFCCCCCCCCCCCCCCCCCCCFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                                ACCCCCCCCCCCCCCCA                                ",
            "                 GGG            ACCCCCCCCCCCCCCCA            GGG                 ",
            "               GG   GG          ACCCCCCCCCCCCCCCA          GG   GG               ",
            "             GGCCBBBCCGG        ACCCCCCCCCCCCCCCA        GGCCBBBCCGG             ",
            "             GC   H   CG        ACCCCCCCCCCCCCCCA        GC   H   CG             ",
            "            GC  IIHII  CG       ACCCCCCCCCCCCCCCA       GC  IIHII  CG            ",
            "            GC I IHI I CG       ACCCCCCCCCCCCCCCA       GC I IHI I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G      F               F      G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G     FF               FF     G B IIHCHII B G           ",
            "            GC I IHI I CG      FF               FF      GC I IHI I CG            ",
            "            GC  IIHII  CG     FF                 FF     GC  IIHII  CG            ",
            "             GC   H   CG      FF                 FF      GC   H   CG             ",
            "             GGCCBBBCCGG     FFF                 FFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FF                   FF     D GG   GG D             ",
            "            D    GGG    D   FFF                   FFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFF                   FFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFFFFFFFFFFFFFFFFFFFFFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                FFFFFFFFFFFFFFFFF                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                                AAAAAAAAAAAAAAAAA                                ",
            "                 GGG            AAAAAAAAAAAAAAAAA            GGG                 ",
            "               GG   GG          AAAAAAAAAAAAAAAAA          GG   GG               ",
            "             GGCCBBBCCGG        AAAAAAAAAAAAAAAAA        GGCCBBBCCGG             ",
            "             GC       CG        AAAAAAAAAAAAAAAAA        GC       CG             ",
            "            GC  II II  CG       AAAAAAAAAAAAAAAAA       GC  II II  CG            ",
            "            GC I I I I CG       AAAAAAAAAAAAAAAAA       GC I I I I CG            ",
            "           G B IIHCHII B G      FFFFFFFFFFFFFFFFF      G B IIHCHII B G           ",
            "           G B   CCC   B G      FAAAAAAAAAAAAAAAF      G B   CCC   B G           ",
            "           G B IIHCHII B G     FFAAAAAAAAAAAAAAAFF     G B IIHCHII B G           ",
            "            GC I I I I CG      FFAAAAAAAAAAAAAAAFF      GC I I I I CG            ",
            "            GC  II II  CG     FFAAAAAAAAAAAAAAAAAFF     GC  II II  CG            ",
            "             GC       CG      FFAAAAAAAAAAAAAAAAAFF      GC       CG             ",
            "             GGCCBBBCCGG     FFFAAAAAAAAAAAAAAAAAFFF     GGCCBBBCCGG             ",
            "             D GG   GG D     FFAAAAAAAAAAAAAAAAAAAFF     D GG   GG D             ",
            "            D    GGG    D   FFFAAAAAAAAAAAAAAAAAAAFFF   D    GGG    D            ",
            "           DDD  DDDDD  DDD  FFFAAAAAAAAAAAAAAAAAAAFFF  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDFFFFFFFFFFFFFFFFFFFFFFFFFDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "             GC       CG                                 GC       CG             ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             D GG   GG D                                 D GG   GG D             ",
            "            D    GGG    D                               D    GGG    D            ",
            "           DDD  DDDDD  DDD                             DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC   H   CG                                 GC   H   CG             ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G                             G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "             GC   H   CG                                 GC   H   CG             ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             D GG   GG D                                 D GG   GG D             ",
            "            D    GGG    D                               D    GGG    D            ",
            "           DDD  DDDDD  DDD                             DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "             GC       CG                                 GC       CG             ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             D GG   GG D                                 D GG   GG D             ",
            "            D    GGG    D   EEEEEEEEEEEEEEEEEEEEEEEEE   D    GGG    D            ",
            "           DDD  DDDDD  DDD  EEEEEEEEEEEEEEEEEEEEEEEEE  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "             GC       CG                                 GC       CG             ",
            "             GGCCBBBCCGG    EEEEEEEEEEEEEEEEEEEEEEEEE    GGCCBBBCCGG             ",
            "             D GG   GG D    EEEEEEEEEEEEEEEEEEEEEEEEE    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC   H   CG                                 GC   H   CG             ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G                             G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "            GC  IIHII  CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC  IIHII  CG            ",
            "             GC   H   CG    EEEEEEEEEEEEEEEEEEEEEEEEE    GC   H   CG             ",
            "             GGCCBBBCCGG    E                       E    GGCCBBBCCGG             ",
            "             D GG   GG D    E                       E    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                 GGG        JJJJJJJJJJJJJJJJJJJJJJJJJ        GGG                 ",
            "               GG   GG      JJJJJJJJJJJJJJJJJJJJJJJJJ      GG   GG               ",
            "             GGCCBBBCCGG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B IIHCHII B G           ",
            "            GC I I I I CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC I I I I CG            ",
            "            GC  II II  CG   E                       E   GC  II II  CG            ",
            "             GC       CG    E                       E    GC       CG             ",
            "             GGCCBBBCCGG    E                       E    GGCCBBBCCGG             ",
            "             D GG   GG D    E                       E    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                           J                         J                           ",
            "                 GGG       J                         J       GGG                 ",
            "               GG   GG     J                         J     GG   GG               ",
            "             GGCCBBBCCGG   J                         J   GGCCBBBCCGG             ",
            "             GC       CG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B IIHCHII B G           ",
            "           G B   CCC   B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B   CCC   B G           ",
            "           G B IIHCHII B G  E                       E  G B IIHCHII B G           ",
            "            GC I I I I CG   A                       A   GC I I I I CG            ",
            "            GC  II II  CG   A                       A   GC  II II  CG            ",
            "             GC       CG    A                       A    GC       CG             ",
            "             GGCCBBBCCGG    A                       A    GGCCBBBCCGG             ",
            "             D GG   GG D    A                       A    D GG   GG D             ",
            "            D    GGG    D   A                       A   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                           J                         J                           ",
            "                 GGG       J                         J       GGG                 ",
            "               GG   GG     J                         J     GG   GG               ",
            "             GGCCBBBCCGG   J                         J   GGCCBBBCCGG             ",
            "             GC   H   CG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GC   H   CG             ",
            "            GC  IIHII  CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC  IIHII  CG            ",
            "            GC I IHI I CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC I IHI I CG            ",
            "           G B IIHCHII B G  E                       E  G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G  A                       A  G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G  A                       A  G B IIHCHII B G           ",
            "            GC I IHI I CG   A                       A   GC I IHI I CG            ",
            "            GC  IIHII  CG   A                       A   GC  IIHII  CG            ",
            "             GC   H   CG    A                       A    GC   H   CG             ",
            "             GGCCBBBCCGG    A                       A    GGCCBBBCCGG             ",
            "             D GG   GG D    A                       A    D GG   GG D             ",
            "            D    GGG    D   A                       A   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                           J                         J                           ",
            "                 GGG       J                         J       GGG                 ",
            "               GG   GG     J                         J     GG   GG               ",
            "             GGCCBBBCCGG   J                         J   GGCCBBBCCGG             ",
            "             GC       CG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GC       CG             ",
            "            GC  II II  CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC  II II  CG            ",
            "            GC I I I I CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC I I I I CG            ",
            "           G B IIHCHII B G  E                       E  G B IIHCHII B G           ",
            "           G B   CCC   B G  A                       A  G B   CCC   B G           ",
            "           G B IIHCHII B G  A                       A  G B IIHCHII B G           ",
            "            GC I I I I CG   A                       A   GC I I I I CG            ",
            "            GC  II II  CG   A                       A   GC  II II  CG            ",
            "             GC       CG    A                       A    GC       CG             ",
            "             GGCCBBBCCGG    A                       A    GGCCBBBCCGG             ",
            "             D GG   GG D    A                       A    D GG   GG D             ",
            "            D    GGG    D   A                       A   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                           J                         J                           ",
            "                 GGG       J                         J       GGG                 ",
            "               GG   GG     J                         J     GG   GG               ",
            "             GGCCBBBCCGG   J                         J   GGCCBBBCCGG             ",
            "             GC       CG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B IIHCHII B G           ",
            "           G B   CCC   B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B   CCC   B G           ",
            "           G B IIHCHII B G  E                       E  G B IIHCHII B G           ",
            "            GC I I I I CG   A                       A   GC I I I I CG            ",
            "            GC  II II  CG   A                       A   GC  II II  CG            ",
            "             GC       CG    A                       A    GC       CG             ",
            "             GGCCBBBCCGG    A                       A    GGCCBBBCCGG             ",
            "             D GG   GG D    A                       A    D GG   GG D             ",
            "            D    GGG    D   A                       A   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                            JJJJJJJJJJJJJJJJJJJJJJJJJ                            ",
            "                 GGG        JJJJJJJJJJJJJJJJJJJJJJJJJ        GGG                 ",
            "               GG   GG      JJJJJJJJJJJJJJJJJJJJJJJJJ      GG   GG               ",
            "             GGCCBBBCCGG    JJJJJJJJJJJJJJJJJJJJJJJJJ    GGCCBBBCCGG             ",
            "             GC   H   CG                                 GC   H   CG             ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G                             G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G  EEEEEEEEEEEEEEEEEEEEEEEEE  G B IIHCHII B G           ",
            "            GC I IHI I CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC I IHI I CG            ",
            "            GC  IIHII  CG   E                       E   GC  IIHII  CG            ",
            "             GC   H   CG    E                       E    GC   H   CG             ",
            "             GGCCBBBCCGG    E                       E    GGCCBBBCCGG             ",
            "             D GG   GG D    E                       E    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG   EEEEEEEEEEEEEEEEEEEEEEEEE   GC  II II  CG            ",
            "             GC       CG    EEEEEEEEEEEEEEEEEEEEEEEEE    GC       CG             ",
            "             GGCCBBBCCGG    E                       E    GGCCBBBCCGG             ",
            "             D GG   GG D    E                       E    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC       CG                                 GC       CG             ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G B   CCC   B G                             G B   CCC   B G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I I I I CG                               GC I I I I CG            ",
            "            GC  II II  CG                               GC  II II  CG            ",
            "             GC       CG                                 GC       CG             ",
            "             GGCCBBBCCGG    EEEEEEEEEEEEEEEEEEEEEEEEE    GGCCBBBCCGG             ",
            "             D GG   GG D    EEEEEEEEEEEEEEEEEEEEEEEEE    D GG   GG D             ",
            "            D    GGG    D   E                       E   D    GGG    D            ",
            "           DDD  DDDDD  DDD  E                       E  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 GGG                                         GGG                 ",
            "               GG   GG                                     GG   GG               ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             GC   H   CG                                 GC   H   CG             ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "           G BHHHCCCHHHB G                             G BHHHCCCHHHB G           ",
            "           G B IIHCHII B G                             G B IIHCHII B G           ",
            "            GC I IHI I CG                               GC I IHI I CG            ",
            "            GC  IIHII  CG                               GC  IIHII  CG            ",
            "             GC   H   CG                                 GC   H   CG             ",
            "             GGCCBBBCCGG                                 GGCCBBBCCGG             ",
            "             D GG   GG D                                 D GG   GG D             ",
            "            D    GGG    D   EEEEEEEEEEEEEEEEEEEEEEEEE   D    GGG    D            ",
            "           DDD  DDDDD  DDD  EEEEEEEEEEEEEEEEEEEEEEEEE  DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "               GGGGGGG                                     GGGGGGG               ",
            "             GGGGGGGGGGG                                 GGGGGGGGGGG             ",
            "            GAAAAGGGAAAAG                               GAAAAGGGAAAAG            ",
            "            GAAAAAGAAAAAG                               GAAAAAGAAAAAG            ",
            "           GGAAAAAGAAAAAGG                             GGAAAAAGAAAAAGG           ",
            "           GGAAAAGGGAAAAGG                             GGAAAAGGGAAAAGG           ",
            "           GGGAAGCCCGAAGGG                             GGGAAGCCCGAAGGG           ",
            "           GGGGGGCCCGGGGGG                             GGGGGGCCCGGGGGG           ",
            "           GGGAAGCCCGAAGGG                             GGGAAGCCCGAAGGG           ",
            "           GGAAAAGGGAAAAGG                             GGAAAAGGGAAAAGG           ",
            "           GGAAAAAGAAAAAGG                             GGAAAAAGAAAAAGG           ",
            "            GAAAAAGAAAAAG                               GAAAAAGAAAAAG            ",
            "            GAAAAGGGAAAAG                               GAAAAGGGAAAAG            ",
            "             GGGGGGGGGGG                                 GGGGGGGGGGG             ",
            "            D  GGGGGGG  D                               D  GGGGGGG  D            ",
            "           DDD  DDDDD  DDD                             DDD  DDDDD  DDD           ",
            "         DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 DDD                                         DDD                 ",
            "                DDDDD                                       DDDDD                ",
            "                DDDDD                                       DDDDD                ",
            "                DDDDD                                       DDDDD                ",
            "                 DDD                                         DDD                 ",
            "                 D D                                         D D                 ",
            "                 D D                                         D D                 ",
            "                 D D                                         D D                 ",
            "               DDDDDDD                                     DDDDDDD               ",
            "            DDDDDDDDDDDDD                               DDDDDDDDDDDDD            ",
            "           DDDDDDDDDDDDDDD                             DDDDDDDDDDDDDDD           ",
            "         DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD         " },
        { "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                 DDD                                         DDD                 ",
            "                 DDD                                         DDD                 ",
            "                 DDD                                         DDD                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "                                                                                 ",
            "               DDDDDDD                                     DDDDDDD               ",
            "            DDDDDDDDDDDDD                               DDDDDDDDDDDDD            ",
            "           DDDDDDDDDDDDDDD                             DDDDDDDDDDDDDDD           ",
            "         DDDDDDDDDDDDDDDDDDD                         DDDDDDDDDDDDDDDDDDD         " }, };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 12321;
    }

    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType("§2§l艾萨领主的终极造物 - 艾萨集成工厂")
     * .addInfo("§o神奇的艾萨系列, 将垃圾和化工产品融为一体, 这不神奇吗?")
     * .addInfo("§oNEI点击机器主机, 获取一个集大成之作...")
     * .addInfo("§d许多老登们最喜欢的一步到位: 扔掉了乱七八糟的流程, 只是了节约一些tps")
     * .addInfo("执行无损超频")
     * .addInfo("耗时倍率 : 1/线圈等级")
     * .addInfo("耗电倍率 : 玻璃等级^-0.1")
     * .addInfo("'研磨器复合体' 决定机器执行的配方等级, 只是一个物品")
     * .addInfo("§a更是拥有§4意想不到§a的惊喜呀嘻嘻...")
     * .addTecTechHatchInfo()
     * .addSeparator()
     * .addPollutionAmount(12321)
     * .addController("MISA")
     * .beginStructureBlock(7, 13, 7, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .toolTipFinisher("§a123Technology - One Last ISA");
     * return tt;
     * }
     */
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.misa.0"))
            .addInfo(translateToLocal("ote.tm.misa.1"))
            .addInfo(translateToLocal("ote.tm.misa.2"))
            .addInfo(translateToLocal("ote.tm.misa.3"))
            .addInfo(translateToLocal("ote.tm.misa.4"))
            .addInfo(translateToLocal("ote.tm.misa.5"))
            .addInfo(translateToLocal("ote.tm.misa.6"))
            .addInfo(translateToLocal("ote.tm.misa.7"))
            .addInfo(translateToLocal("ote.tm.misa.8"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addPollutionAmount(12321)
            .addController(translateToLocal("ote.tn.misa"))
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - One Last ISA");
        return tt;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return super.isCorrectMachinePart(aStack);
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return super.getMaxEfficiency(aStack);
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return super.getDamageToComponent(aStack);
    }

    @Override
    public boolean supportsVoidProtection() {
        return super.supportsVoidProtection();
    }

    @Override
    public boolean supportsInputSeparation() {
        return super.supportsInputSeparation();
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return super.supportsSingleRecipeLocking();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEMegaIsaFactory(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(2)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_ON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(2)),
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
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(2)) };
    }

}
