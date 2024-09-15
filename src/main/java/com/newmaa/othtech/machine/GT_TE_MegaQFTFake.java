package com.newmaa.othtech.machine;

import static com.github.technus.tectech.thing.casing.TT_Container_Casings.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GT_StructureUtility.ofFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.github.technus.tectech.thing.block.QuantumGlassBlock;
import com.github.technus.tectech.thing.casing.TT_Container_Casings;
import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizons.gtnhintergalactic.block.IGBlocks;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTH_processingLogics.OTH_ProcessingLogic;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_HatchElementBuilder;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.core.block.ModBlocks;

// spotless:off
public class GT_TE_MegaQFTFake extends OTH_MultiMachineBase<GT_TE_MegaQFTFake> {

    public GT_TE_MegaQFTFake(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_MegaQFTFake(String aName) {
        super(aName);
    }

    private int stabilisationFieldMetadata = 0;
    private int spacetimeCompressionFieldMetadata = 0;
    private int timeAccelerationFieldMetadata = 0;
    private byte mode = 0;
    private int multiplier = 1;


    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

        aNBT.setInteger("stabilisationFieldMetadata", stabilisationFieldMetadata);
        aNBT.setInteger("spacetimeCompressionFieldMetadata", spacetimeCompressionFieldMetadata);
        aNBT.setInteger("timeAccelerationFieldMetadata", timeAccelerationFieldMetadata);
        aNBT.setByte("mode", mode);
        aNBT.setInteger("multiplier", multiplier);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

        stabilisationFieldMetadata = aNBT.getInteger("stabilisationFieldMetadata");
        spacetimeCompressionFieldMetadata = aNBT.getInteger("spacetimeCompressionFieldMetadata");
        timeAccelerationFieldMetadata = aNBT.getInteger("timeAccelerationFieldMetadata");
        mode = aNBT.getByte("mode");
        multiplier = aNBT.getInteger("multiplier");

    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    protected int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    //耗时倍率 = 1 - (时空膨胀场等级 * 0.1)
    protected float getSpeedBonus() {
        return (float) (1 - (timeAccelerationFieldMetadata * 0.1));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
    return Recipemaps.QFTMega;

    }





    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {


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

        // if in this state , no extra settings is in need.
        if (multiplier < 1) {
            mOutputItems = processingLogic.getOutputItems();
            mOutputFluids = processingLogic.getOutputFluids();
            return result;
        }

        ItemStack[] outputItemStack = processingLogic.getOutputItems();
        FluidStack[] outputFluidStack = processingLogic.getOutputFluids();

        if (mode != 2) {
            // compressor mode and extractor mode
            mOutputItems = outputItemStack;
            mOutputFluids = outputFluidStack;
        } else {



            // process Items
            List<ItemStack> extraItems = new ArrayList<>();
            for (ItemStack items : outputItemStack) {
                if (items.stackSize <= Integer.MAX_VALUE / multiplier) {
                    // set amount directly if in integer area
                    items.stackSize *= multiplier;
                } else {
                    for (int i = 0; i < multiplier - 1; i++) {
                        extraItems.add(items.copy());
                    }
                }
            }

            if (extraItems.isEmpty()) {
                // no over integer amount
                mOutputItems = outputItemStack;
            } else {
                extraItems.addAll(Arrays.asList(outputItemStack));
                mOutputItems = extraItems.toArray(new ItemStack[] {});
            }

            // process Fluids
            List<FluidStack> extraFluids = new ArrayList<>();
            for (FluidStack fluids : outputFluidStack) {
                if (fluids.amount <= Integer.MAX_VALUE / multiplier) {
                    fluids.amount *= multiplier;
                } else {
                    for (int i = 0; i < multiplier - 1; i++) {
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

        }

        return result;
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


    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        if (stabilisationFieldMetadata < 6) {
            multiplier = 0;
        }else{
            if (stabilisationFieldMetadata >= 6 & stabilisationFieldMetadata <= 8){
                multiplier = 2;
            }else{
                if (stabilisationFieldMetadata > 9) {
                    multiplier = 3;
                }
            }
        }

        return checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);

    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
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

    private final int horizontalOffSet = 9;
    private final int verticalOffSet = 22;
    private final int depthOffSet = 1;

    private static IStructureDefinition<GT_TE_MegaQFTFake> STRUCTURE_DEFINITION = null;

    public static int getBlockStabilisationFieldGeneratorTier(Block block, int meta) {
        if (block == StabilisationFieldGenerators && meta <= 8) {
            return meta + 1;
        }
        return 0;
    }

    public static int getBlockSpacetimeCompressionFieldGeneratorTier(Block block, int meta) {
        if (block == SpacetimeCompressionFieldGenerators && meta <= 8) {
            return meta + 1;
        }
        return 0;
    }

    public static int getBlockTimeAccelerationFieldGeneratorTier(Block block, int meta) {
        if (block == TimeAccelerationFieldGenerator && meta <= 8) {
            return meta + 1;
        }
        return 0;
    }


    @Override
    public IStructureDefinition<GT_TE_MegaQFTFake> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_MegaQFTFake>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .addElement('J',
                    withChannel("StabilisationFieldGenerators",
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
                .addElement('I',
                    withChannel("SpacetimeCompressionFieldGenerators",
                        ofBlocksTiered(
                            GT_TE_MegaQFTFake::getBlockSpacetimeCompressionFieldGeneratorTier,
                            ImmutableList.of(
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 0),
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 1),
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 2),
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 3),
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 4),
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 5),
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 6),
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 7),
                                Pair.of(TT_Container_Casings.SpacetimeCompressionFieldGenerators, 8)),
                            0,
                            (t, meta) -> t.spacetimeCompressionFieldMetadata = meta,
                            t -> t.spacetimeCompressionFieldMetadata)))
                .addElement('K',
                    withChannel("TimeAccelerationFieldGenerators",
                        ofBlocksTiered(
                            GT_TE_MegaQFTFake::getBlockTimeAccelerationFieldGeneratorTier,
                            ImmutableList.of(
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 0),
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 1),
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 2),
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 3),
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 4),
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 5),
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 6),
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 7),
                                Pair.of(TT_Container_Casings.TimeAccelerationFieldGenerator, 8)),
                            0,
                            (t, meta) -> t.timeAccelerationFieldMetadata = meta,
                            t -> t.timeAccelerationFieldMetadata)))
                .addElement('L', ofBlock(ModBlocks.blockCasings4Misc, 4))
                .addElement('M', ofBlock(ModBlocks.blockCasings5Misc, 10))
                .addElement('N', ofBlock(ModBlocks.blockCasings5Misc, 14))
                .addElement('O', ofBlock(ModBlocks.blockCasings5Misc, 15))
                .addElement('G', ofBlock(sBlockCasingsTT, 10))
                .addElement('H', ofBlock(sBlockCasingsTT, 10))
                .addElement('C', ofBlock(sBlockCasingsBA0, 10))
                .addElement('D', ofBlock(IGBlocks.SpaceElevatorCasing, 2))
                .addElement('E', ofBlock(sBlockCasingsTT, 4))
                .addElement('F', ofBlock(sBlockCasingsTT, 9))
                .addElement('A', ofFrame(Materials.Infinity))
                .addElement('P', ofBlock(QuantumGlassBlock.INSTANCE, 0))
                .addElement(
                    'B',
                    GT_HatchElementBuilder.<GT_TE_MegaQFTFake>builder()
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(GT_TE_MegaQFTFake::addToMachineList)
                        .dot(1)
                        .casingIndex(1024 + 12)
                        .buildAndChain(GregTech_API.sBlockCasings8, 3))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }
//Structure by LyeeR
    private final String[][] shapeMain = new String[][]{
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","        LGL        ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","        LGL        ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","        LGL        ","       LGIGL       ","        LGL        ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","        LGL        ","       LGIGL       ","        LGL        ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","        LLL        ","       LGJGL       ","       LJIJL       ","       LGJGL       ","        LLL        ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","        LGL        ","       LGIGL       ","        LGL        ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","        LGL        ","       LGIGL       ","        LGL        ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","         L         ","        LGL        ","         L         ","                   ","                   ","                   ","                   ","                   ","                   ","                   ","                   "},
        {"                   ","                   ","         J         ","                   ","                   ","                   ","                   ","                   ","         L         ","  J     LGL     J  ","         L         ","                   ","                   ","                   ","                   ","                   ","         J         ","                   ","                   "},
        {"                   ","         C         ","         J         ","                   ","                   ","                   ","                   ","                   ","                   "," CJ      L      JC ","                   ","                   ","                   ","                   ","                   ","                   ","         J         ","         C         ","                   "},
        {"                   ","        PCP        ","                   ","                   ","                   ","                   ","                   ","                   "," P               P "," C       L       C "," P               P ","                   ","                   ","                   ","                   ","                   ","                   ","        PCP        ","                   "},
        {"        PCP        ","         I         ","                   ","                   ","                   ","                   ","                   ","                   ","P                 P","CI               IC","P                 P","                   ","                   ","                   ","                   ","                   ","                   ","         I         ","        PCP        "},
        {"        PCP        ","         I         ","                   ","                   ","                   ","                   ","                   ","                   ","P                 P","CI       F       IC","P                 P","                   ","                   ","                   ","                   ","                   ","                   ","         I         ","        PCP        "},
        {"        PCP        ","         I         ","                   ","                   ","                   ","                   ","                   ","                   ","P                 P","CI       F       IC","P                 P","                   ","                   ","                   ","                   ","                   ","                   ","         I         ","        PCP        "},
        {"                   ","        ECE        ","                   ","                   ","                   ","                   ","                   ","                   "," E      MNM      E "," C      NGN      C "," E      MNM      E ","                   ","                   ","                   ","                   ","                   ","                   ","        ECE        ","                   "},
        {"                   ","        ECE        ","                   ","                   ","                   ","                   ","                   ","       E   E       "," E       O       E "," C      OGO      C "," E       O       E ","       E   E       ","                   ","                   ","                   ","                   ","                   ","        ECE        ","                   "},
        {"                   ","        ECE        ","         A         ","        A A        ","       A   A       ","      A     A      ","     AE     EA     ","    A         A    "," E A    MNM    A E "," CA     NGN     AC "," E A    MNM    A E ","    A         A    ","     AE     EA     ","      A     A      ","       A   A       ","        A A        ","         A         ","        ECE        ","                   "},
        {"                   ","                   ","        EKE        ","                   ","                   ","     E       E     ","                   ","                   ","  E             E  ","  K      F      K  ","  E             E  ","                   ","                   ","     E       E     ","                   ","                   ","        EKE        ","                   ","                   "},
        {"                   ","                   ","        EKE        ","                   ","    E         E    ","     E       E     ","                   ","                   ","  E             E  ","  K      F      K  ","  E             E  ","                   ","                   ","     E       E     ","    E         E    ","                   ","        EKE        ","                   ","                   "},
        {"                   ","        EEE        ","       EECEE       ","   E    EKE    E   ","    E    E    E    ","     E       E     ","                   ","                   ","  EE     H     EE  ","  CKE   HGH   EKC  ","  EE     H     EE  ","                   ","                   ","     E       E     ","    E    E    E    ","   E    EKE    E   ","        ECE        ","                   ","                   "},
        {"                   ","        E~E        ","  E    ECCCE    E  ","   E   BBBBB   E   ","    E BBBBBBB E    ","     BBBBEBBBB     ","    BBBBBEBBBBB    ","  EBBBBBCCCBBBBBE  ","  CBBBBCCCCCBBBBC  ","  CBBEECCCCCEEBBC  ","  CBBBBCCCCCBBBBC  ","  EBBBBBCCCBBBBBE  ","    BBBBBEBBBBB    ","     BBBBEBBBB     ","    E BBBBBBB E    ","   E   BBBBB   E   ","  E    ECCCE    E  ","                   ","                   "},
        {"                   ","        EEE        ","  D    DDDDD    D  ","   DDDDDDDDDDDDD   ","   DDDD     DDDD   ","   DDD       DDD   ","   DD         DD   ","  DD           DD  ","  DD           DD  ","  DD           DD  ","  DD           DD  ","  DD           DD  ","   D          DD   ","   DD        DDD   ","   DDD      DDDD   ","   DDDDDDDDDDDDD   ","  D    DDDDD    D  ","                   ","                   "}};


    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>(Arrays.asList(super.getInfoData()));
        return str.toArray(new String[0]);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§e§l老登们的又一造物 - §b「狄拉克逆变器」")
            .addInfo("§b§l对虚粒子的操控在量子之海中掀起了滔天巨浪...")
            .addInfo("§d提供基础原料，可以让复杂的化学、物理过程一步到位")
            .addInfo("§d依靠量子之海中的虚粒子，产物能以超出原料的规模输出")
            .addInfo("§d*物质依旧守恒，只不过从虚粒子转为了实粒子")
            .addInfo("狄拉克逆变模式会有意想不到的惊喜")
            .addInfo("§9时间膨胀场发生器每提升一级，减少10%的配方时间")
            .addInfo("§9稳定力场发生器等级<6时, 产量不变")
            .addInfo("§9稳定力场发生器等级≤8, ≥6时，产量为二倍")
            .addInfo("§9稳定力场发生器等级≥9时, 产量为三倍")
            .addInfo("§q支持§bTecTech§q能源仓及激光仓，但不支持无线电网直接供给EU")
            .addSeparator()
            .addController("Mega QFT")
            .beginStructureBlock(19, 24, 19, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§b123Technology - Make QFT great again!");
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
        return new GT_TE_MegaQFTFake(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings1, 12)),
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
                    .getCasingTextureForId(GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings1, 12)),
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
            .getCasingTextureForId(GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings1, 12)) };
    }
}
// spotless:on
