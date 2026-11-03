package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.newmaa.othtech.CommonProxy.RocketRenderBlock;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import gregtech.api.GregTechAPI;
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

public class OTEFireRocketAssembler extends OTHMultiMachineBase<OTEFireRocketAssembler>
    implements ISurvivalConstructable, IConstructable {

    public ItemStack blueprint;

    public int tier;

    private static final int ROCKET_OFFSET_BACK = 3;

    private static final int ROCKET_OFFSET_RIGHT = 20;

    private static final CheckRecipeResult NO_SCHEMATIC = SimpleCheckRecipeResult.ofFailure("ote_no_schematic");

    private boolean mRocketPlaced;

    private int mRocketX;

    private int mRocketY;

    private int mRocketZ;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 10;

    private final int verticalOffSet = 15;

    private final int depthOffSet = 3;

    private static IStructureDefinition<OTEFireRocketAssembler> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("otht.cm.nasa.0") };

    private final String[][] shapeMain = new String[][] {
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                 EE  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                 EE  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                 EE  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                  E  ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", "                                     ",
            " FEEEEEEEEEEEEEEEEEF             EE  ", " FEDDDDDDDDDDDDDDDEF                 ",
            " FEEEEEEEEEEEEEEEEEF                 ", "                                     ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            "                                     ", " FCCCCCCCCCCCCCCCCCF                 ",
            " F                 F              E  ", " F                 F                 ",
            " F                 F                 ", " FCCCCCCCCCCCCCCCCCF                 ",
            "                                     ", "                                     ",
            "                                     " },
        { "                                     ", "                                     ",
            "                                     ", "                                     ",
            " FBBBBBBBBBBBBBBBBBF                 ", " F                 F                 ",
            " F                 F             EE  ", " F                 F                 ",
            " F                 F                 ", " F                 F                 ",
            " FBBBBBBBBBBBBBBBBBF                 ", "                                     ",
            "                                     " },
        { "                            AAAAA    ", "                          AA     AA  ",
            "                         A         A ", "                         A         A ",
            " FBBBBBBBBBBBBBBBBBF    A           A", " F                 F    A           A",
            " F                 F    A         E A", " F                 F    A           A",
            " F                 F    A           A", " F                 F     A         A ",
            " FBBBBBBBBBBBBBBBBBF     A         A ", "                          AA     AA  ",
            "                            AAAAA    " },
        { "                            ABBBA    ", "                          BB     BB  ",
            "                         B         B ", "                         B         B ",
            " FBBBBBBBBBBBBBBBBBF    A           A", " F                 F    B           B",
            " F                 F    B        EE B", " F                 F    B           B",
            " F                 F    A           A", " F                 F     B         B ",
            " FBBBBBBBBBBBBBBBBBF     B         B ", "                          BB     BB  ",
            "                            ABBBA    " },
        { "                            ABBBA    ", "                          BB     BB  ",
            "                         B         B ", "                         B         B ",
            " FBBBBBBBBBBBBBBBBBF    A           A", " F                 F    B           B",
            " F                 F              E B", " F                 F    B           B",
            " F                 F    A           A", " F                 F     B         B ",
            " FBBBBBBBBBBBBBBBBBF     B         B ", "                          BB     BB  ",
            "                            ABBBA    " },
        { "                            ABBBA    ", "                          BB     BB  ",
            "                         B         B ", "        FF~FF            B         B ",
            " FAAAAAAAAAAAAAAAAAF    A           A", " F                 F    B         E B",
            " F                 F             EEEB", " F                 F    B         E B",
            " F                 F    A           A", " F                 F     B         B ",
            " FAAAAAAAAAAAAAAAAAF     B         B ", "                          BB     BB  ",
            "                            ABBBA    " },
        { "       IIIIIII              IIIII    ", "IIIIIIIICCCCCIIIIIIII     IIIAAAIII  ",
            "ICCCCCCCCCCCCCCCCCCCI    IIAAAAAAAII ", "ICCCCCCCCCCCCCCCCCCCI    IAAGGGGGAAI ",
            "ICCCCCCCCCCCCCCCCCCCI   IIAGGHHHGGAII", "IFFFFFFFFFFFFFFFFFFFIIIIIAAGHHHHHGAAI",
            "IFFFFFFFFFFFFFFFFFFFICCCIAAGHHHHHGAAI", "IFFFFFFFFFFFFFFFFFFFIIIIIAAGHHHHHGAAI",
            "IFFFFFFFFFFFFFFFFFFFI   IIAGGHHHGGAII", "IFFFFFFFFFFFFFFFFFFFI    IAAGGGGGAAI ",
            "ICCCCCCCCCCCCCCCCCCCI    IIAAAAAAAII ", "IIIIIIIIIIIIIIIIIIIII     IIIAAAIII  ",
            "                            IIIII    " } };

    // ===================== Constructors =====================

    public OTEFireRocketAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEFireRocketAssembler(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);

    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return 64;
    }

    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.NASA;
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return hasRequiredSpecialItems(recipe.mSpecialItems) ? CheckRecipeResultRegistry.SUCCESSFUL
                    : NO_SCHEMATIC;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {

        return super.checkProcessing();
    }

    @Override
    public boolean onRunningTick(ItemStack stack) {
        super.onRunningTick(stack);
        createRenderBlock();
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (!aBaseMetaTileEntity.isServerSide()) return;
        if (!mMachine || mMaxProgresstime <= 0) clearRenderBlock();
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
    public IStructureDefinition<OTEFireRocketAssembler> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEFireRocketAssembler>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .addElement('A', ofBlockOr(IndustrialCraft2.ID + ":blockAlloy", 0, sBlockReinforced, 0))
                .addElement('B', ofBlockOr(IndustrialCraft2.ID + ":blockAlloyGlass", 0, sBlockReinforced, 1))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 0))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings3, 11))
                .addElement('E', ofBlock(GregTechAPI.sBlockFrames, 335))
                .addElement('G', ofBlock(GregTechAPI.sBlockReinforced, 8))
                .addElement('H', ofBlock(GregTechAPI.sBlockReinforced, 9))
                .addElement('I', ofBlockOr(Chisel.ID + ":factoryblock", 6, sBlockConcretes, 0))
                .addElement(
                    'F',
                    buildHatchAdder(OTEFireRocketAssembler.class)
                        .atLeast(OutputBus, InputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                        .adder(OTEFireRocketAssembler::addToMachineList)
                        .hint(1)
                        .casingIndex(210)
                        .buildAndChain(sBlockReinforced, 2))
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
        tt.addMachineType(EnumChatFormatting.DARK_RED + translateToLocal("ote.tm.nasa.0"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.1"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.2"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.3"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.nasa.4"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.nasa.5"))
            .addInfo(translateToLocal("ote.tm.nasa.6"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - FIRE-RocketAssembler")
            .addController(translateToLocal("ote.tn.nasa"))
            .beginStructureBlock(37, 17, 13, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEFireRocketAssembler(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(210), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(210), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(210) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GTCEU_LOOP_ASSEMBLER;
    }

    // ===================== Misc =====================

    private boolean hasRequiredSpecialItems(Object aSpecialItems) {
        if (aSpecialItems == null) return true;
        ItemStack specialSlot = getControllerSlot();
        if (aSpecialItems instanceof ItemStack) {
            return isSpecialItemPresent(specialSlot, (ItemStack) aSpecialItems);
        }
        if (aSpecialItems instanceof ItemStack[]) {
            for (ItemStack stack : (ItemStack[]) aSpecialItems) {
                if (stack != null && !isSpecialItemPresent(specialSlot, stack)) return false;
            }
        }
        return true;
    }

    private static boolean isSpecialItemPresent(ItemStack aSpecialSlot, ItemStack aRequired) {
        return aSpecialSlot != null && GTUtility.areStacksEqual(aSpecialSlot, aRequired);
    }

    @Override
    protected void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
    }

    private static IStructureElement<OTEFireRocketAssembler> ofBlockOr(String aBlockName, int aMeta, Block aFallback,
        int aFallbackMeta) {
        Block block = Block.getBlockFromName(aBlockName);
        return block == null ? ofBlock(aFallback, aFallbackMeta) : ofBlock(block, aMeta);
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    private void createRenderBlock() {
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !base.isServerSide()) return;
        World world = base.getWorld();
        if (world == null) return;

        ExtendedFacing facing = getExtendedFacing();
        ForgeDirection back = facing.getRelativeBackInWorld();
        ForgeDirection right = facing.getRelativeRightInWorld();
        int rocketX = base.getXCoord() + ROCKET_OFFSET_BACK * back.offsetX - ROCKET_OFFSET_RIGHT * right.offsetX;
        int rocketY = base.getYCoord() + ROCKET_OFFSET_BACK * back.offsetY - ROCKET_OFFSET_RIGHT * right.offsetY;
        int rocketZ = base.getZCoord() + ROCKET_OFFSET_BACK * back.offsetZ - ROCKET_OFFSET_RIGHT * right.offsetZ;

        if (world.getBlock(rocketX, rocketY, rocketZ) == RocketRenderBlock) {
            rememberRocket(rocketX, rocketY, rocketZ);
            return;
        }
        if (mRocketPlaced && (mRocketX != rocketX || mRocketY != rocketY || mRocketZ != rocketZ)) clearRenderBlock();
        if (!world.isAirBlock(rocketX, rocketY, rocketZ)) return;
        world.setBlock(rocketX, rocketY, rocketZ, RocketRenderBlock, getRocketMeta(base), 3);
        rememberRocket(rocketX, rocketY, rocketZ);
    }

    private void rememberRocket(int x, int y, int z) {
        mRocketPlaced = true;
        mRocketX = x;
        mRocketY = y;
        mRocketZ = z;
    }

    private void clearRenderBlock() {
        if (!mRocketPlaced) return;
        mRocketPlaced = false;
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !base.isServerSide()) return;
        World world = base.getWorld();
        if (world != null && world.getBlock(mRocketX, mRocketY, mRocketZ) == RocketRenderBlock) {
            world.setBlockToAir(mRocketX, mRocketY, mRocketZ);
        }
    }

    private static int getRocketMeta(IGregTechTileEntity base) {
        switch (base.getFrontFacing()) {
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
            default:
                return 3;
        }
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
        clearRenderBlock();
    }
}
