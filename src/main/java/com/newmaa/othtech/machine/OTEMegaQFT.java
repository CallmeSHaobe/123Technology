package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.block.BlockQuantumGlass;

// spotless:off
public class OTEMegaQFT extends OTHMultiMachineBase<OTEMegaQFT> {

    public OTEMegaQFT(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaQFT(String aName) {
        super(aName);
    }

    private int stabilisationFieldMetadata = 0;
    private int spacetimeCompressionFieldMetadata = 0;
    private int timeAccelerationFieldMetadata = 0;
    private byte mode = 0;
    private int multiplier = 1;
    private String plier = "0";


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

    public int getMaxParallelRecipes() {
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

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
                             IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("otht.waila.bonus.output") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.RED
                + tag.getString("multiplier")
                + EnumChatFormatting.RESET
                + " ");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
                                int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            plier = GTUtility.formatNumbers(multiplier);
            tag.setString("multiplier", plier);

        }
    }



    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {


        setupProcessingLogic(processingLogic);
        checkMultiplier();
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

        if (mode != 0) {
            // compressor mode and extractor mode
            mOutputItems = outputItemStack;
            mOutputFluids = outputFluidStack;
        } else {


            @NotNull
            // process Items
            List<ItemStack> extraItems = new ArrayList<>();
            if (outputItemStack != null){
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
            }else{
                mOutputItems = processingLogic.getOutputItems();
            }

            // process Fluids
            List<FluidStack> extraFluids = new ArrayList<>();
            if (outputFluidStack != null) {
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
            }}else {
                mOutputFluids = processingLogic.getOutputFluids();
            }

        }

        return result;
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
    private void checkMultiplier() {
        if (stabilisationFieldMetadata <6)
        {multiplier = 1;
        }else if (stabilisationFieldMetadata >= 6 && stabilisationFieldMetadata <= 8)
        {multiplier = 2;
        }else if (stabilisationFieldMetadata > 8 )
        {multiplier = 3;
        }
    }


    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
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

    private final int horizontalOffSet = 17;
    private final int verticalOffSet = 43;
    private final int depthOffSet = 1;

    private static IStructureDefinition<OTEMegaQFT> STRUCTURE_DEFINITION = null;

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
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":",
        translateToLocal("ote.cm.mqft.0")   };
    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEMegaQFT> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaQFT>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .addElement('O',
                    withChannel("spacetimecompressionfieldgenerators",
                        ofBlocksTiered(
                            OTEMegaQFT::getBlockSpacetimeCompressionFieldGeneratorTier,
                            ImmutableList.of(
                                Pair.of(SpacetimeCompressionFieldGenerators, 0),
                                Pair.of(SpacetimeCompressionFieldGenerators, 1),
                                Pair.of(SpacetimeCompressionFieldGenerators, 2),
                                Pair.of(SpacetimeCompressionFieldGenerators, 3),
                                Pair.of(SpacetimeCompressionFieldGenerators, 4),
                                Pair.of(SpacetimeCompressionFieldGenerators, 5),
                                Pair.of(SpacetimeCompressionFieldGenerators, 6),
                                Pair.of(SpacetimeCompressionFieldGenerators, 7),
                                Pair.of(SpacetimeCompressionFieldGenerators, 8)),
                            0,
                            (t, meta) -> t.spacetimeCompressionFieldMetadata = meta,
                            t -> t.spacetimeCompressionFieldMetadata)))
                .addElement('P',
                    withChannel("stabilisationfieldgenerators",
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
                .addElement('Q',
                    withChannel("timeaccelerationfieldgenerators",
                        ofBlocksTiered(
                            OTEMegaQFT::getBlockTimeAccelerationFieldGeneratorTier,
                            ImmutableList.of(
                                Pair.of(TimeAccelerationFieldGenerator, 0),
                                Pair.of(TimeAccelerationFieldGenerator, 1),
                                Pair.of(TimeAccelerationFieldGenerator, 2),
                                Pair.of(TimeAccelerationFieldGenerator, 3),
                                Pair.of(TimeAccelerationFieldGenerator, 4),
                                Pair.of(TimeAccelerationFieldGenerator, 5),
                                Pair.of(TimeAccelerationFieldGenerator, 6),
                                Pair.of(TimeAccelerationFieldGenerator, 7),
                                Pair.of(TimeAccelerationFieldGenerator, 8)),
                            0,
                            (t, meta) -> t.timeAccelerationFieldMetadata = meta,
                            t -> t.timeAccelerationFieldMetadata)))
                .addElement(
                    'A',
                    buildHatchAdder(OTEMegaQFT.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(OTEMegaQFT::addToMachineList)
                        .dot(1)
                        .casingIndex(1024 + 12)
                        .buildAndChain(GregTechAPI.sBlockCasings8, 3))
                .addElement('B', ofBlock(sBlockCasingsBA0, 10))
                .addElement('C', ofBlock(sBlockCasingsBA0, 11))
                .addElement('D', ofBlock(sBlockCasingsBA0, 12))
                .addElement('E', ofBlock(sBlockCasingsSE, 0))
                .addElement('F', ofBlock(sBlockCasingsSE, 1))
                .addElement('G', ofBlock(sBlockCasingsSE, 2))
                .addElement('H', ofBlock(sBlockCasingsTT, 4))
                .addElement('I', ofBlock(sBlockCasingsTT, 9))
                .addElement('J', ofBlock(sBlockCasingsTT, 10))
                .addElement('L', ofFrame(Materials.Infinity))
                .addElement('K', ofBlock(sBlockCasingsTT, 12))
                .addElement('M', ofBlock(sBlockMetal9, 4))
                .addElement('N', ofBlock(sBlockMetal9, 9))
                .addElement('R', ofBlock(ModBlocks.blockCasings4Misc, 4))
                .addElement('S', ofBlock(ModBlocks.blockCasings5Misc, 10))
                .addElement('T', ofBlock(ModBlocks.blockCasings5Misc, 14))
                .addElement('U', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }
    //Structured by LyeeR-Version-1, 商陆-Version-2
    private final String[][] shapeMain = new String[][]{
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","             RRRRRRRRR             ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","             RRRRRRRRR             ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","             RRRRRRRRR             ","            RRRRRRRRRRR            ","             RRRRRRRRR             ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","             RRRRRRRRR             ","            RRRRRRRRRRR            ","             RRRRRRRRR             ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                RRR                ","               RRRRR               ","              RRRRRRR              ","             RRRRRRRRR             ","            RRRRRRRRRRR            ","            RRRRRRRRRRR            ","            RRRRRRRRRRR            ","             RRRRRRRRR             ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","             RRRRRRRRR             ","            RRRRRRRRRRR            ","             RRRRRRRRR             ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","             RRRRRRRRR             ","            RRRRRRRRRRR            ","             RRRRRRRRR             ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 M                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","      M      RRRRRRRRR      M      ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 M                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                 P                 ","                 M                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","               RRRRR               ","              RRRRRRR              ","     PM      RRRRRRRRR      MP     ","              RRRRRRR              ","               RRRRR               ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 M                 ","                 P                 ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                 P                 ","                PMP                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","     P         RRRRR         P     ","    PM        RRRRRRR        MP    ","     P         RRRRR         P     ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                PMP                ","                 P                 ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                 P                 ","                PMP                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","                RRR                ","     P         RRRRR         P     ","    PM        RRRRRRR        MP    ","     P         RRRRR         P     ","                RRR                ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                PMP                ","                 P                 ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                 P                 ","                PMP                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","    P           RRR           P    ","   PM          RRRRR          MP   ","    P           RRR           P    ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                PMP                ","                 P                 ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                 P                 ","                PMP                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 R                 ","    P           RRR           P    ","   PM          RRRRR          MP   ","    P           RRR           P    ","                 R                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                PMP                ","                 P                 ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                 P                 ","                PMP                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","    P            R            P    ","   PM           RRR           MP   ","    P            R            P    ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                PMP                ","                 P                 ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                 P                 ","                PMP                ","                 P                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","   P             R             P   ","  PMP           RRR           PMP  ","   P             R             P   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 P                 ","                PMP                ","                 P                 ","                                   ","                                   "},
        {"                                   ","                 P                 ","                PMP                ","                 P                 ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  P                             P  "," PMP             R             PMP ","  P                             P  ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 P                 ","                PMP                ","                 P                 ","                                   "},
        {"                                   ","                 M                 ","                CBC                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  C                             C  "," MB              R              BM ","  C                             C  ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                CBC                ","                 M                 ","                                   "},
        {"                 M                 ","                CBC                ","                CBC                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "," CC                             CC ","MBB                             BBM"," CC                             CC ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                CBC                ","                CBC                ","                 M                 "},
        {"                 M                 ","                CBC                ","                UBU                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "," CU                             UC ","MBB              I              BBM"," CU                             UC ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 B                 ","                CBC                ","                 M                 "},
        {"                UBU                ","               UCBCU               ","                LOL                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "," U                               U ","UCL                             LCU","BBO              I              OBB","UCL                             LCU"," U                               U ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                LOL                ","               UCBCU               ","                UBU                "},
        {"                UBU                ","               UCBCU               ","                LOL                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "," U                               U ","UCL              I              LCU","BBO             III             OBB","UCL              I              LCU"," U                               U ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                LOL                ","               UCBCU               ","                UBU                "},
        {"                UBU                ","               UCBCU               ","                LOL                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "," U                               U ","UCL              I              LCU","BBO             III             OBB","UCL              I              LCU"," U                               U ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                LOL                ","               UCBCU               ","                UBU                "},
        {"                UBU                ","               UCBCU               ","                LOL                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "," U              STS              U ","UCL            SSISS            LCU","BBO            TIIIT            OBB","UCL            SSISS            LCU"," U              STS              U ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                LOL                ","               UCBCU               ","                UBU                "},
        {"                UBU                ","               UCBCU               ","                LOL                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "," U                               U ","UCL              I              LCU","BBO             III             OBB","UCL              I              LCU"," U                               U ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                LOL                ","               UCBCU               ","                UBU                "},
        {"                                   ","                CBC                ","               HCKCH               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  H                             H  "," CB              I              BC "," BB             III             BB "," CB              I              BC ","  H                             H  ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","               HBBBH               ","                CBC                ","                                   "},
        {"                                   ","                CBC                ","               HCKCH               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","              O     O              ","  H              T              H  "," CB             SIS             BC "," BB            TIIIT            BB "," CB             SIS             BC ","  H              T              H  ","              O     O              ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","               HBBBH               ","                CBC                ","                                   "},
        {"                                   ","                CBC                ","               HCBCH               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                LLL                ","              LL   LL              ","             LO     OL             ","  H          L  STS  L          H  "," CB         L  SSISS  L         BC "," BB         L  TIIIT  L         BB "," CB         L  SSISS  L         BC ","  H          L  STS  L          H  ","             LO     OL             ","              LL   LL              ","                LLL                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","               HBBBH               ","                CBC                ","                                   "},
        {"                                   ","                CBC                ","               HCBCH               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","             U       U             ","            UO       OU            ","                                   ","  H              T              H  "," CB             SIS             BC "," BB            TIIIT            BB "," CB             SIS             BC ","  H              T              H  ","                                   ","            UO       OU            ","             U       U             ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","               HBBBH               ","                CBC                ","                                   "},
        {"                                   ","                CBC                ","               HCBCH               ","               LLLLL               ","              LL   LL              ","             LL     LL             ","            LL       LL            ","           LL         LL           ","          LL           LL          ","         LL             LL         ","        LL               LL        ","       LL  UH         HU  LL       ","      LL   HO         OH   LL      ","     LL                     LL     ","    LL                       LL    ","  HLL                         LLH  "," CBL             I             LBC "," BBL            III            LBB "," CBL             I             LBC ","  HLL                         LLH  ","    LL                       LL    ","     LL                     LL     ","      LL   HO         OH   LL      ","       LL  UH         HU  LL       ","        LL               LL        ","         LL             LL         ","          LL           LL          ","           LL         LL           ","            LL       LL            ","             LL     LL             ","              LL   LL              ","               LLLLL               ","               HBBBH               ","                CBC                ","                                   "},
        {"                                   ","                                   ","                BBB                ","               HBBBH               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","          UH           HU          ","          HH           HH          ","                                   ","                                   ","                                   ","   H                           H   ","  BB             I             BB  ","  BB            III            BB  ","  BB             I             BB  ","   H                           H   ","                                   ","                                   ","                                   ","          HH           HH          ","          UH           HU          ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","               HBBBH               ","                BBB                ","                                   ","                                   "},
        {"                                   ","                                   ","                BQB                ","               HQQQH               ","                                   ","                                   ","                                   ","                                   ","                                   ","         UHH           HHU         ","         HB             BH         ","         H               H         ","                                   ","                                   ","                                   ","   H                           H   ","  BQ             P             QB  ","  QQ            PPP            QQ  ","  BQ             P             QB  ","   H                           H   ","                                   ","                                   ","                                   ","         H               H         ","         HB             BH         ","         UHH           HHU         ","                                   ","                                   ","                                   ","                                   ","                                   ","               HQQQH               ","                BQB                ","                                   ","                                   "},
        {"                                   ","                                   ","                BQB                ","               HQQQH               ","               H   H               ","                                   ","                                   ","                                   ","        UUHH           HHUU        ","        UKB             BKU        ","        HB               BH        ","        H                 H        ","                                   ","                                   ","                                   ","   HH                         HH   ","  BQ             P             QB  ","  QQ            PPP            QQ  ","  BQ             P             QB  ","   HH                         HH   ","                                   ","                                   ","                                   ","        H                 H        ","        HB               BH        ","        UKB             BKU        ","        UUHH           HHUU        ","                                   ","                                   ","                                   ","               H   H               ","               HQQQH               ","                BQB                ","                                   ","                                   "},
        {"                                   ","                 K                 ","                BBB                ","               HBBBH               ","              HHQQQHH              ","               H   H               ","                                   ","        UUH             HUU        ","       UPKB             BKPU       ","       UKK               KKU       ","       HB                 BH       ","                                   ","                                   ","                                   ","    H         J     J         H    ","   HHH                       HHH   ","  BBQ            P            QBB  ","  BBQ           PPP           QBB  ","  BBQ            P            QBB  ","   HHH                       HHH   ","    H         J     J         H    ","                                   ","                                   ","                                   ","       HB                 BH       ","       UKK               KKU       ","       UPKB             BKPU       ","        UUH             HUU        ","                                   ","               H   H               ","              HHQQQHH              ","               HBBBH               ","                BBB                ","                                   ","                                   "},
        {"                                   ","                KMK                ","               BBBBB               ","              KCBBBCK              ","             HHHQQQHHH             ","              HHQQQHH              ","        UUH    H   H    HUU        ","       UPPK             KPPU       ","      UPPK               KPPU      ","      UPK                 KPU      ","      HK                   KK      ","                                   ","                                   ","    H                         H    ","   KHH        LJ   JL        HHK   ","  BCHHH       J  P  J       HHHCB  ","  BBQQ          PPP          QQBB  ","  BBQQ         PPPPP         QQBB  ","  BBQQ          PPP          QQBB  ","  BCHHH       J  P  J       HHHCB  ","   KHH        LJ   JL        HHK   ","    H                         H    ","                                   ","                                   ","      HK                   KH      ","      UPK                 KPU      ","      UPPK               KPPU      ","       UPPK             KPPU       ","        UUH    H   H    HUU        ","              HHQQQHH              ","             HHHQQQHHH             ","              KCBBBCK              ","               BBBBB               ","                                   ","                                   "},
        {"                                   ","               KNMNK               ","              BBBBBBB              ","             KCBBBBBCK             ","            KAAAAAAAAAK            ","        UUHLLLAAQQQAALLLHUU        ","       UPLLAALLAQQQALLAALLPU       ","      UPLAAAAALHQQQHLAAAAALPU      ","     UPLAAAAAALHKQKHLAAAAAALPU     ","     ULAAAAAAAALKQKLAAAAAAAALU     ","     HLAAAAAAAALKQKLAAAAAAAALH     ","     LAAAAAAAAALKAKLAAAAAAAAAL     ","    KLAAAAAAAAALKAKLAAAAAAAAALK    ","   KALLAAAAAAAALKAKLAAAAAAAALLAK   ","  BCAALLLAAAAALKJQJKLAAAAALLLAACB  ","  BBAAAHHLLLLLKJQQQJKLLLLLHHAAABB  ","  BBAQQQKKKKKKJQQQQQJKKKKKKQQQABB  ","  BBAQQQQQQQQQQQQQQQQQQQQQQQQQABB  ","  BBAQQQKKKKKKJQQQQQJKKKKKKQQQABB  ","  BBAAAHHLLLLLKJQQQJKLLLLLHHAAABB  ","  BCAALLLAAAAALKJQJKLAAAAALLLAACB  ","   KALLAAAAAAAALKQKLAAAAAAAALLAK   ","    KLAAAAAAAAALKQKLAAAAAAAAALK    ","     LAAAAAAAAALKQKLAAAAAAAAAL     ","     HLAAAAAAAALKQKLAAAAAAAALH     ","     ULAAAAAAAALKQKLAAAAAAAALU     ","     UPLAAAAAALHKQKHLAAAAAALPU     ","      UPLAAAAALHQQQHLAAAAALPU      ","       UPLLAALLAQQQALLAALLPU       ","        UUHLLLAAQQQAALLLHUU        ","            KAAAAAAAAAK            ","             KCBBBBBCK             ","              BBBBBBB              ","                                   ","                                   "},
        {"                                   ","              KMM~MMK              ","  D           DDDDDDD           D  ","   D         DDDDDDDDD         D   ","    DDDDDDDDDDEEEEEEEDDDDDDDDDD    ","    DEEEEEEEEEEEEEEEEEEEEEEEEED    ","    DEEEGGGEEEDDDDDDDEEEGGGEEED    ","    DEEGGEEEDDDLLLLLDDDEEEGGEED    ","    DEGGEEDDDLL     LLDDDEEGGED    ","    DEGEEDDLL         LLDDEEGED    ","    DEGEDDL             LDDEGED    ","    DEEEDL               LDEEED    ","    DEEDDL               LDDEED    ","   DDEEDL                 LDEEDD   ","  DDEEDDL                 LDDEEDD  ","  DDEEDL                   LDEEDD  ","  DDEEDL                   LDEEDD  ","  DDEEDL                   LDEEDD  ","  DDEEDL                   LDEEDD  ","  DDEEDL                   LDEEDD  ","  DDEEDDL                 LDDEEDD  ","   DDEEDL                 LDEEDD   ","    DEEDDL               LDDEED    ","    DEEEDL               LDEEED    ","    DEGEDDL             LDDEGED    ","    DEGEEDDLL         LLDDEEGED    ","    DEGGEEDDDLL     LLDDDEEGGED    ","    DEEGGEEEDDDLLLLLDDDEEEGGEED    ","    DEEEGGGEEEDDDDDDDEEEGGGEEED    ","    DEEEEEEEEEEEEEEEEEEEEEEEEED    ","    DDDDDDDDDDEEEEEEEDDDDDDDDDD    ","   D         DDDDDDDDD         D   ","  D           DDDDDDD           D  ","                                   ","                                   "},
        {"                                   ","             KKKNMNKKK             ","  GGG        GGGGGGGGG        GGG  ","  GGGG       GGGGGGGGG       GGGG  ","  GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG  ","   GGEEEEEEEEGGGGGGGGGEEEEEEEEGG   ","    GELLLEEGGGGLLLLLGGGGEELLLEG    ","    GELLEEGGGLL     LLGGGEELLEG    ","    GELEEGGLL         LLGGEELEG    ","    GEEEGGL             LGGEEEG    ","    GEEGGL               LGGEEG    ","    GEGGL                 LGGEG    ","    GEGGL                 LGGEG    ","  GGGGGL                   LGGGG   ","  GGGGGL                   LGGGGG  ","  GGGGL                     LGGGG  ","  GGGGL                     LGGGG  ","  GGGGL                     LGGGG  ","  GGGGL                     LGGGG  ","  GGGGL                     LGGGG  ","  GGGGGL                   LGGGGG  ","  GGGGGL                   LGGGG   ","    GEGGL                 LGGEG    ","    GEGGL                 LGGEG    ","    GEEGGL               LGGEEG    ","    GEEEGGL             LGGEEEG    ","    GELEEGGLL         LLGGEELEG    ","    GELLEEGGGLL     LLGGGEELLEG    ","    GELLLEEGGGGLLLLLGGGGEELLLEG    ","   GGEEEEEEEEGGGGGGGGGEEEEEEEEGG   ","  GGGGGGGGGGGGGGGGGGGGGGGGGGGGGG   ","  GGGG       GGGGGGGGG       GGGG  ","  GG         GGGGGGGGG         GG  ","                                   ","                                   "},
        {"                                   ","             KKKKMKKKK             ","  FFFF       FFFFFFFFF       FFFF  ","  FFFFFF     FFFFFFFFF     FFFFFF  ","  FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF  ","  FFFEEEEEEEEEEFFFFFEEEEEEEEEEFFF  ","   FFEEEEEEEEFF     FFEEEEEEEEFF   ","   FFEEEEEEFF         FFEEEEEEFF   ","    FEEEEEF             FEEEEEF    ","    FEEEEF               FEEEEF    ","    FEEEF                 FEEEF    ","    FEEF                   FEEF    ","    FEEF                   FEEF    ","  FFFEF                     FEFFF  ","  FFFEF                     FEFFF  ","  FFFF                       FFFF  ","  FFFF                       FFFF  ","  FFFF                       FFFF  ","  FFFF                       FFFF  ","  FFFF                       FFFF  ","  FFFEF                     FEFFF  ","  FFFEF                     FEFFF  ","    FEEF                   FEEF    ","    FEEF                   FEEF    ","    FEEEF                 FEEEF    ","    FEEEEF               FEEEEF    ","    FEEEEEF             FEEEEEF    ","   FFEEEEEEFF         FFEEEEEEFF   ","   FFEEEEEEEEFF     FFEEEEEEEEFF   ","  FFFEEEEEEEEEEFFFFFEEEEEEEEEEFFF  ","  FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF  ","  FFFFFF     FFFFFFFFF     FFFFFF  ","  FFFF       FFFFFFFFF       FFFF  ","                                   ","                                   "}
    };
    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>(Arrays.asList(super.getInfoData()));
        return str.toArray(new String[0]);
    }
    /*

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
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
            .addTecTechHatchInfo()
            .addSeparator()
            .addController("Mega recipesQFTRecipes")
            .beginStructureBlock(19, 24, 19, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§b123Technology - Make QFT great again!");
        return tt;
    }*/
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.mqft.0"))
            .addInfo(translateToLocal("ote.tm.mqft.1"))
            .addInfo(translateToLocal("ote.tm.mqft.2"))
            .addInfo(translateToLocal("ote.tm.mqft.3"))
            .addInfo(translateToLocal("ote.tm.mqft.4"))
            .addInfo(translateToLocal("ote.tm.mqft.5"))
            .addInfo(translateToLocal("ote.tm.mqft.6"))
            .addInfo(translateToLocal("ote.tm.mqft.7"))
            .addInfo(translateToLocal("ote.tm.mqft.8"))
            .addInfo(translateToLocal("ote.tm.mqft.9"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addController(translateToLocal("ote.tn.mqft"))
            .beginStructureBlock(35, 46, 35, false)
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
        return new OTEMegaQFT(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 12)),
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
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 12)),
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
            .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 12)) };
    }
}
// spotless:on
