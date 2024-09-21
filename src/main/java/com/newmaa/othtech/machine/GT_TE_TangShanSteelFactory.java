package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.GregTech_API.*;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.enums.Mods.Chisel;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GT_StructureUtility.ofCoil;
import static gregtech.api.util.GT_StructureUtility.ofFrame;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.Utils.Utils;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTH_processingLogics.OTH_ProcessingLogic;

import crazypants.enderio.EnderIO;
import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.ItemList;
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
import gregtech.api.util.GT_HatchElementBuilder;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.common.blocks.GT_Block_Casings2;
import gtPlusPlus.core.block.ModBlocks;

public class GT_TE_TangShanSteelFactory extends OTH_MultiMachineBase<GT_TE_TangShanSteelFactory> {

    public GT_TE_TangShanSteelFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_TangShanSteelFactory(String aName) {
        super(aName);
    }

    private boolean $123 = false;
    private byte mode = 0;
    public byte glassTier = 0;

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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 20 == 0 && !$123) {
            ItemStack aGuiStack = this.getControllerSlot();
            if (aGuiStack != null) {
                if (GT_Utility.areStacksEqual(aGuiStack, ItemList.Field_Generator_UV.get(1))) {
                    this.$123 = true;
                }
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("$123", $123);
        aNBT.setByte("glassTier", glassTier);
        aNBT.setByte("mode", mode);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        $123 = aNBT.getBoolean("123");
        glassTier = aNBT.getByte("glassTier");
        mode = aNBT.getByte("mode");
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return $123;
    }

    protected int getMaxParallelRecipes() {
        return 2560;
    }

    protected float getSpeedBonus() {
        return Math.max(0.1F, (float) 1 / (glassTier * getCoilTier()));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.TSSF;
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

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GT_Recipe recipe) {
                if (recipe.mSpecialValue > coilLevel.getHeat()) {
                    return SimpleCheckRecipeResult.ofFailure("HeatErr");
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
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

    private final int horizontalOffSet = 2;
    private final int verticalOffSet = 38;
    private final int depthOffSet = 2;
    private static final int HORIZONTAL_DIRT_METAID = 10;
    private static final int HORIZONTAL_DIRT_METAID_A = 4;
    private static final int HORIZONTAL_DIRT_METAID_B = 0;
    private static final int HORIZONTAL_DIRT_METAID_C = 14;
    private static IStructureDefinition<GT_TE_TangShanSteelFactory> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_TangShanSteelFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_TangShanSteelFactory>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', ofBlock(Block.getBlockFromName(IndustrialCraft2.ID + ":blockAlloy"), 0))
                .addElement('B', ofBlock(sBlockCasings1, 11))
                .addElement(
                    'C',
                    GT_HatchElementBuilder.<GT_TE_TangShanSteelFactory>builder()
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(GT_TE_TangShanSteelFactory::addToMachineList)
                        .dot(1)
                        .casingIndex(((GT_Block_Casings2) GregTech_API.sBlockCasings2).getTextureIndex(0))
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
                        ofCoil(GT_TE_TangShanSteelFactory::setCoilLevel, GT_TE_TangShanSteelFactory::getCoilLevel)))
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
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null)
                        ? ofBlock(Block.getBlockFromName(Chisel.ID + ":concrete"), HORIZONTAL_DIRT_METAID_A)
                        : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'V',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null)
                        ? ofBlock(Block.getBlockFromName(Chisel.ID + ":concrete"), HORIZONTAL_DIRT_METAID)
                        : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'W',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null)
                        ? ofBlock(Block.getBlockFromName(Chisel.ID + ":hempcrete"), HORIZONTAL_DIRT_METAID_B)
                        : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'X',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null)
                        ? ofBlock(Block.getBlockFromName(Chisel.ID + ":hempcrete"), HORIZONTAL_DIRT_METAID_C)
                        : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'Y',
                    GT_HatchElementBuilder.<GT_TE_TangShanSteelFactory>builder()
                        .atLeast(Muffler)
                        .adder(GT_TE_TangShanSteelFactory::addToMachineList)
                        .dot(2)
                        .casingIndex(((GT_Block_Casings2) GregTech_API.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement('Z', ofFrame(Materials.Steel))
                .addElement('1', ofBlock(sBlockCasings8, 4))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structure by LyeeR
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
        return 192000;
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§e§l重工业计划 - 唐山钢铁厂")
            .addInfo("§l§8无数黑烟源源不断地从烟囱中冒出")
            .addInfo("§l§8工业化的必由之路......")
            .addInfo("§l§8一步到位各种与钢铁相关之合金")
            .addInfo("耗时 = NEI耗时 * max((1 / 玻璃等级 * 线圈等级), 0.1F)")
            .addInfo("请注意炉温要求")
            .addInfo("主机放入UV立场发生器解锁无损超频")
            .addInfo("§q支持§bTecTech§q能源仓及激光仓，但不支持无线电网直接供给EU")
            .addPollutionAmount(192000)
            .addSeparator()
            .addController("钢铁厂")
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
        return new GT_TE_TangShanSteelFactory(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GT_Utility.getCasingTextureIndex(sBlockCasings2, 0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GT_Utility.getCasingTextureIndex(sBlockCasings2, 0)),
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
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings2, 0)) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }
}
