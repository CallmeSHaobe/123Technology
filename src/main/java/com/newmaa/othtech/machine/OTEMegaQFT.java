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

    private final int horizontalOffSet = 18;
    private final int verticalOffSet = 47;
    private final int depthOffSet = 2;

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
                .addElement('Q',
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
                .addElement('R',
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
                .addElement('M', ofBlock(sBlockMetal9, 4))
                .addElement('N', ofBlock(sBlockMetal9, 9))
                .addElement('G', ofBlock(sBlockCasingsSE, 1))
                .addElement('H', ofBlock(sBlockCasingsSE, 2))
                .addElement('I', ofBlock(sBlockCasingsTT, 4))
                .addElement('J', ofBlock(sBlockCasingsTT, 9))
                .addElement('K', ofBlock(sBlockCasingsTT, 10))
                .addElement('L', ofBlock(sBlockCasingsTT, 12))
                .addElement('C', ofBlock(sBlockCasingsBA0, 10))
                .addElement('D', ofBlock(sBlockCasingsBA0, 11))
                .addElement('E', ofBlock(sBlockCasingsBA0, 12))
                .addElement('F', ofBlock(sBlockCasingsSE, 0))
                .addElement('A', ofFrame(Materials.Infinity))
                .addElement(
                    'B',
                    buildHatchAdder(OTEMegaQFT.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(OTEMegaQFT::addToMachineList)
                        .dot(1)
                        .casingIndex(1024 + 12)
                        .buildAndChain(GregTechAPI.sBlockCasings8, 3))
                .addElement('S', ofBlock(ModBlocks.blockCasings4Misc, 4))
                .addElement('T', ofBlock(ModBlocks.blockCasings5Misc, 10))
                .addElement('U', ofBlock(ModBlocks.blockCasings5Misc, 14))
                .addElement('V', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement('W', ofFrame(Materials.Infinity))
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
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","              SSSSSSSSS            ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","              SSSSSSSSS            ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","              SSSSSSSSS            ","             SSSSSSSSSSS           ","              SSSSSSSSS            ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","              SSSSSSSSS            ","             SSSSSSSSSSS           ","              SSSSSSSSS            ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","              SSSSSSSSS            ","             SSSSSSSSSSS           ","             SSSSSSSSSSS           ","             SSSSSSSSSSS           ","              SSSSSSSSS            ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","              SSSSSSSSS            ","             SSSSSSSSSSS           ","              SSSSSSSSS            ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","              SSSSSSSSS            ","             SSSSSSSSSSS           ","              SSSSSSSSS            ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  M                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","       M      SSSSSSSSS      M     ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  M                ","                                   ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  Q                ","                  M                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","                SSSSS              ","               SSSSSSS             ","      QM      SSSSSSSSS      MQ    ","               SSSSSSS             ","                SSSSS              ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  M                ","                  Q                ","                                   ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                  Q                ","                 QMQ               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","      Q         SSSSS         Q    ","     QM        SSSSSSS        MQ   ","      Q         SSSSS         Q    ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 QMQ               ","                  Q                ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                                   ","                  Q                ","                 QMQ               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","                 SSS               ","      Q         SSSSS         Q    ","     QM        SSSSSSS        MQ   ","      Q         SSSSS         Q    ","                 SSS               ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 QMQ               ","                  Q                ","                                   ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                  Q                ","                 QMQ               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","     Q           SSS           Q   ","    QM          SSSSS          MQ  ","     Q           SSS           Q   ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 QMQ               ","                  Q                ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                  Q                ","                 QMQ               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  S                ","     Q           SSS           Q   ","    QM          SSSSS          MQ  ","     Q           SSS           Q   ","                  S                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 QMQ               ","                  Q                ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                                   ","                  Q                ","                 QMQ               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","     Q            S            Q   ","    QM           SSS           MQ  ","     Q            S            Q   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 QMQ               ","                  Q                ","                                   ","                                   "},
        {"                                   ","                                   ","                                   ","                  Q                ","                 QMQ               ","                  Q                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","    Q             S             Q  ","   QMQ           SSS           QMQ ","    Q             S             Q  ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  Q                ","                 QMQ               ","                  Q                ","                                   "},
        {"                                   ","                                   ","                  Q                ","                 QMQ               ","                  Q                ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","   Q                             Q ","  QMQ             S             QMQ","   Q                             Q ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  Q                ","                 QMQ               ","                  Q                "},
        {"                                   ","                                   ","                  M                ","                 DCD               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","   D                             D ","  MC              S              CM","   D                             D ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 DCD               ","                  M                "},
        {"                                   ","                  M                ","                 DCD               ","                 DCD               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  DD                             DD"," MCC                             CC","  DD                             DD","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 DCD               ","                 DCD               "},
        {"                                   ","                  M                ","                 DCD               ","                 VCV               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  DV                             VD"," MCC              J              CC","  DV                             VD","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                  C                ","                 DCD               "},
        {"                                   ","                 VCV               ","                VDCDV              ","                 WOW               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  V                               V"," VDW                             WD"," CCO              J              OC"," VDW                             WD","  V                               V","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 WOW               ","                VDCDV              "},
        {"                                   ","                 VCV               ","                VDCDV              ","                 WOW               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  V                               V"," VDW              J              WD"," CCO             JJJ             OC"," VDW              J              WD","  V                               V","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 WOW               ","                VDCDV              "},
        {"                                   ","                 VCV               ","                VDCDV              ","                 WOW               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  V                               V"," VDW              J              WD"," CCO             JJJ             OC"," VDW              J              WD","  V                               V","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 WOW               ","                VDCDV              "},
        {"                                   ","                 VCV               ","                VDCDV              ","                 WOW               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  V              TUT              V"," VDW            TTJTT            WD"," CCO            UJJJU            OC"," VDW            TTJTT            WD","  V              TUT              V","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 WOW               ","                VDCDV              "},
        {"                                   ","                 VCV               ","                VDCDV              ","                 WOW               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","  V                               V"," VDW              J              WD"," CCO             JJJ             OC"," VDW              J              WD","  V                               V","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 WOW               ","                VDCDV              "},
        {"                                   ","                                   ","                 DCD               ","                IDLDI              ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","   I                             I ","  DC              J              CD","  CC             JJJ             CC","  DC              J              CD","   I                             I ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                ICCCI              ","                 DCD               "},
        {"                                   ","                                   ","                 DCD               ","                IDLDI              ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","               O     O             ","   I              U              I ","  DC             TJT             CD","  CC            UJJJU            CC","  DC             TJT             CD","   I              U              I ","               O     O             ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                ICCCI              ","                 DCD               "},
        {"                                   ","                                   ","                 DCD               ","                IDCDI              ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                 WWW               ","               WW   WW             ","              WO     OW            ","   I          W  TUT  W          I ","  DC         W  TTJTT  W         CD","  CC         W  UJJJU  W         CC","  DC         W  TTJTT  W         CD","   I          W  TUT  W          I ","              WO     OW            ","               WW   WW             ","                 WWW               ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                ICCCI              ","                 DCD               "},
        {"                                   ","                                   ","                 DCD               ","                IDCDI              ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","              V       V            ","             VO       OV           ","                                   ","   I              U              I ","  DC             TJT             CD","  CC            UJJJU            CC","  DC             TJT             CD","   I              U              I ","                                   ","             VO       OV           ","              V       V            ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                ICCCI              ","                 DCD               "},
        {"                                   ","                                   ","                 DCD               ","                IDCDI              ","                WWWWW              ","               WW   WW             ","              WW     WW            ","             WW       WW           ","            WW         WW          ","           WW           WW         ","          WW             WW        ","         WW               WW       ","        WW  VI         IV  WW      ","       WW   IO         OI   WW     ","      WW                     WW    ","     WW                       WW   ","   IWW                         WWI ","  DCW             J             WCD","  CCW            JJJ            WCC","  DCW             J             WCD","   IWW                         WWI ","     WW                       WW   ","      WW                     WW    ","       WW   IO         OI   WW     ","        WW  VI         IV  WW      ","         WW               WW       ","          WW             WW        ","           WW           WW         ","            WW         WW          ","             WW       WW           ","              WW     WW            ","               WW   WW             ","                WWWWW              ","                ICCCI              ","                 DCD               "},
        {"                                   ","                                   ","                                   ","                 CCC               ","                ICCCI              ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","           VI           IV         ","           II           II         ","                                   ","                                   ","                                   ","    I                           I  ","   CC             J             CC ","   CC            JJJ            CC ","   CC             J             CC ","    I                           I  ","                                   ","                                   ","                                   ","           II           II         ","           VI           IV         ","                                   ","                                   ","                                   ","                                   ","                                   ","                                   ","                ICCCI              ","                 CCC               ","                                   "},
        {"                                   ","                                   ","                                   ","                 CRC               ","                IRRRI              ","                                   ","                                   ","                                   ","                                   ","                                   ","          VII           IIV        ","          IC             LI        ","          I               I        ","                                   ","                                   ","                                   ","    I                           I  ","   CR             Q             RC ","   RR            QQQ            RR ","   CR             Q             RC ","    I                           I  ","                                   ","                                   ","                                   ","          I               I        ","          I               I        ","          VII           IIV        ","                                   ","                                   ","                                   ","                                   ","                                   ","                IRRRI              ","                 CRC               ","                                   "},
        {"                                   ","                                   ","                                   ","                 CRC               ","                IRRRI              ","                I   I              ","                                   ","                                   ","                                   ","         VVII           IIVV       ","         VLC             LLV       ","         IC               LI       ","         I                 I       ","                                   ","                                   ","                                   ","    II                         II  ","   CR             Q             RC ","   RR            QQQ            RR ","   CR             Q             RC ","    II                         II  ","                                   ","                                   ","                                   ","         I                 I       ","         IL               LI       ","         VLL             LLV       ","         VVII           IIVV       ","                                   ","                                   ","                                   ","                I   I              ","                IRRRI              ","                 CRC               ","                                   "},
        {"                                   ","                                   ","                  L                ","                 CCC               ","                ICCCI              ","               IIRRRII             ","                I   I              ","                                   ","         VVI             IVV       ","        VQLC             LLQV      ","        VLL               LLV      ","        IC                 LI      ","                                   ","                                   ","                                   ","     I         K     K         I   ","    III                       III  ","   CCR            Q            RCC ","   CCR           QQQ           RCC ","   CCR            Q            RCC ","    III                       III  ","     I         K     K         I   ","                                   ","                                   ","                                   ","        IL                 LI      ","        VLL               LLV      ","        VQLL             LLQV      ","         VVI             IVV       ","                                   ","                I   I              ","               IIRRRII             ","                ICCCI              ","                 CCC               ","                                   "},
        {"                                   ","                                   ","                 LML               ","                CCCCC              ","               LDCCCDL             ","              IIIRRRIII            ","               IIRRRII             ","         VVI    I   I    IVV       ","        VQQL             LQQV      ","       VQQL               LQQV     ","       VQL                 LQV     ","       IL                   LV     ","                                   ","                                   ","     I          W              I   ","    LII        WK   KW        IIL  ","   CDIII       K  Q  K       IIIDC ","   CCRR          QQQ          RRCC ","   CCRR         QQQQQ         RRCC ","   CCRR          QQQ          RRCC ","   CDIII       K  Q  K       IIIDC ","    LII        WK   KW        IIL  ","     I                         I   ","                                   ","                                   ","       IL                   LI     ","       VQL                 LQV     ","       VQQL               LQQV     ","        VQQL             LQQV      ","         VVI    I   I    IVV       ","               IIRRRII             ","              IIIRRRIII            ","               LDCCCDL             ","                CCCCC              ","                                   "},
        {"                                   ","                                   ","                LNMNL              ","               CCCCCCC             ","              LDCCCCCDL            ","             LBBBBBBBBBL           ","         VVIWWWBBRRRBBWWWIVV       ","        VQWWBBWWBRRRBWWBBWWQV      ","       VQWBBBBBWIRRRIWBBBBBWQV     ","      VQWBBBBBBWILRLIWBBBBBBWQV    ","      VWBBBBBBBBWLRLWBBBBBBBBWV    ","      IWBBBBBBBBWLRLWBBBBBBBBWI    ","      WBBBBBBBBBWLBLWBBBBBBBBBW    ","     LWBBBBBBBBBWLBLWBBBBBBBBBWL   ","    LBWWBBBBBBBBWLBLWBBBBBBBBWWBL  ","   CDBBWWWBBBBBWLKRKLWBBBBBWWWBBDC ","   CCBBBIIWWWWWLKRRRKLWWWWWIIBBBCC ","   CCBRRRLLLLLLKRRRRRKLLLLLLRRRBCC ","   CCBRRRRRRRRRRRRRRRRRRRRRRRRRBCC ","   CCBRRRLLLLLLKRRRRRKLLLLLLRRRBCC ","   CCBBBIIWWWWWLKRRRKLWWWWWIIBBBCC ","   CDBBWWWBBBBBWLKRKLWBBBBBWWWBBDC ","    LBWWBBBBBBBBWLRLWBBBBBBBBWWBL  ","     LWBBBBBBBBBWLRLWBBBBBBBBBWL   ","      WBBBBBBBBBWLRLWBBBBBBBBBW    ","      IWBBBBBBBBWLRLWBBBBBBBBWI    ","      VWBBBBBBBBWLRLWBBBBBBBBWV    ","      VQWBBBBBBWILRLIWBBBBBBWQV    ","       VQWBBBBBWIRRRIWBBBBBWQV     ","        VQWWBBWWBRRRBWWBBWWQV      ","         VVIWWWBBRRRBBWWWIVV       ","             LBBBBBBBBBL           ","              LDCCCCCDL            ","               CCCCCCC             ","                                   "},
        {"                                   ","                                   ","               LMM~MML             ","   E           EEEEEEE           E ","    E         EEEEEEEEE         E  ","     EEEEEEEEEEFFFFFFFEEEEEEEEEE   ","     EFFFFFFFFFFFFFFFFFFFFFFFFFE   ","     EFFFHHHFFFEEEEEEEFFFHHHFFFE   ","     EFFHHFFFEEEAAAAAEEEFFFHHFFE   ","     EFHHFFEEEAA     AAEEEFFHHFE   ","     EFHFFEEAA         AAEEFFHFE   ","     EFHFEEA             AEEFHFE   ","     EFFFEA               AEFFFE   ","     EFFEEA               AEEFFE   ","    EEFFEA                 AEFFEE  ","   EEFFEEA                 AEEFFEE ","   EEFFEA                   AEFFEE ","   EEFFEA                   AEFFEE ","   EEFFEA                   AEFFEE ","   EEFFEA                   AEFFEE ","   EEFFEA                   AEFFEE ","   EEFFEEA                 AEEFFEE ","    EEFFEA                 AEFFEE  ","     EFFEEA               AEEFFE   ","     EFFFEA               AEFFFE   ","     EFHFEEA             AEEFHFE   ","     EFHFFEEAA         AAEEFFHFE   ","     EFHHFFEEEAA     AAEEEFFHHFE   ","     EFFHHFFFEEEAAAAAEEEFFFHHFFE   ","     EFFFHHHFFFEEEEEEEFFFHHHFFFE   ","     EFFFFFFFFFFFFFFFFFFFFFFFFFE   ","     EEEEEEEEEEFFFFFFFEEEEEEEEEE   ","    E         EEEEEEEEE         E  ","   E           EEEEEEE           E ","                                   "},
        {"                                   ","                                   ","              LLLNMNLLL            ","   HHH        HHHHHHHHH        HHH ","   HHHH       HHHHHHHHH       HHHH ","   HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH ","    HHFFFFFFFFHHHHHHHHHFFFFFFFFHH  ","     HFWWWFFHHHHWWWWWHHHHFFWWWFH   ","     HFWWFFHHHWW     WWHHHFFWWFH   ","     HFWFFHHWW         WWHHFFWFH   ","     HFFFHHW             WHHFFFH   ","     HFFHHW               WHHFFH   ","     HFHHW                 WHHFH   ","     HFHHW                 WHHFH   ","   HHHHHW                   WHHHH  ","   HHHHHW                   WHHHHH ","   HHHHW                     WHHHH ","   HHHHW                     WHHHH ","   HHHHW                     WHHHH ","   HHHHW                     WHHHH ","   HHHHW                     WHHHH ","   HHHHHW                   WHHHHH ","   HHHHHW                   WHHHH  ","     HFHHW                 WHHFH   ","     HFHHW                 WHHFH   ","     HFFHHW               WHHFFH   ","     HFFFHHW             WHHFFFH   ","     HFWFFHHWW         WWHHFFWFH   ","     HFWWFFHHHWW     WWHHHFFWWFH   ","     HFWWWFFHHHHWWWWWHHHHFFWWWFH   ","    HHFFFFFFFFHHHHHHHHHFFFFFFFFHH  ","   HHHHHHHHHHHHHHHHHHHHHHHHHHHHHH  ","   HHHH       HHHHHHHHH       HHHH ","   HH         HHHHHHHHH         HH ","                                   "},
        {"                                   ","                                   ","              LLLLMLLLL            ","   GGGG       GGGGGGGGG       GGGG ","   GGGGGG     GGGGGGGGG     GGGGGG ","   GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG ","   GGGFFFFFFFFFFGGGGGFFFFFFFFFFGGG ","    GGFFFFFFFFGG     GGFFFFFFFFGG  ","    GGFFFFFFGG         GGFFFFFFGG  ","     GFFFFFG             GFFFFFG   ","     GFFFFG               GFFFFG   ","     GFFFG                 GFFFG   ","     GFFG                   GFFG   ","     GFFG                   GFFG   ","   GGGFG                     GFGGG ","   GGGFG                     GFGGG ","   GGGG                       GGGG ","   GGGG                       GGGG ","   GGGG                       GGGG ","   GGGG                       GGGG ","   GGGG                       GGGG ","   GGGFG                     GFGGG ","   GGGFG                     GFGGG ","     GFFG                   GFFG   ","     GFFG                   GFFG   ","     GFFFG                 GFFFG   ","     GFFFFG               GFFFFG   ","     GFFFFFG             GFFFFFG   ","    GGFFFFFFGG         GGFFFFFFGG  ","    GGFFFFFFFFGG     GGFFFFFFFFGG  ","   GGGFFFFFFFFFFGGGGGFFFFFFFFFFGGG ","   GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG ","   GGGGGG     GGGGGGGGG     GGGGGG ","   GGGG       GGGGGGGGG       GGGG ","                                   "}
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
