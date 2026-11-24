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

import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.dreammaster.item.NHItemList;
import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.blocks.oterender.OTERocketRender;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.hatch.OTERocketSchematicHatch;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IHatchElement;
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
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.IGTHatchAdder;
import gregtech.api.util.MultiblockTooltipBuilder;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEFireRocketAssembler extends OTHMultiMachineBase<OTEFireRocketAssembler>
    implements ISurvivalConstructable, IConstructable {

    public ItemStack blueprint;

    public int tier;

    private static final int ROCKET_OFFSET_BACK = 3;

    private static final int ROCKET_OFFSET_RIGHT = 20;

    private static final CheckRecipeResult NO_SCHEMATIC = SimpleCheckRecipeResult.ofFailure("ote_no_schematic");

    private enum RocketHatchElement implements IHatchElement<OTEFireRocketAssembler> {

        SCHEMATIC;

        @Override
        public List<Class<? extends IMetaTileEntity>> mteClasses() {
            return Collections.singletonList(OTERocketSchematicHatch.class);
        }

        @Override
        public IGTHatchAdder<OTEFireRocketAssembler> adder() {
            return OTEFireRocketAssembler::addSchematicHatch;
        }

        @Override
        public String getDisplayName() {
            return translateToLocal(OTERocketSchematicHatch.NAME_KEY);
        }

        @Override
        public long count(OTEFireRocketAssembler machine) {
            return machine.mSchematicHatch == null ? 0 : 1;
        }
    }

    private OTERocketSchematicHatch mSchematicHatch;

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

    private final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEFireRocketAssembler_shapeMain.shape.gz");

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
        if (mRocketPlaced) aNBT.setIntArray("RocketDisplayPosition", new int[] { mRocketX, mRocketY, mRocketZ });
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        int[] displayPosition = aNBT.getIntArray("RocketDisplayPosition");
        mRocketPlaced = false;
        if (displayPosition.length == 3) rememberRocket(displayPosition[0], displayPosition[1], displayPosition[2]);
    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return mSchematicHatch != null && mSchematicHatch.isValid()
            && mSchematicHatch.containsSchematic(NHItemList.SchematicsTier8.get(1));
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
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (!aBaseMetaTileEntity.isServerSide()) return;
        ItemStack output = mMachine && mMaxProgresstime > 0 ? getDisplayOutput() : null;
        if (output == null) clearRenderBlock();
        else updateRenderBlock(aBaseMetaTileEntity, output);
    }

    private ItemStack getDisplayOutput() {
        if (mOutputItems != null) {
            for (ItemStack stack : mOutputItems) {
                if (stack != null && stack.getItem() != null && stack.stackSize > 0) return stack;
            }
        }
        return null;
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet, errors)) return;
        if (mSchematicHatch == null) {
            errors.add(StructureErrors.of("ote.nasa.missing_schematic_hatch"));
            return;
        }
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
                .addElement('A', ofBlock(sBlockReinforced, 13))
                .addElement('B', ofBlock(sBlockGlass1, 10))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings2, 0))
                .addElement('D', ofBlock(GregTechAPI.sBlockCasings3, 11))
                .addElement('E', ofBlock(GregTechAPI.sBlockFrames, 335))
                .addElement('G', ofBlock(GregTechAPI.sBlockReinforced, 8))
                .addElement('H', ofBlock(GregTechAPI.sBlockReinforced, 9))
                .addElement('I', ofBlockOr(Chisel.ID + ":factoryblock", 6, sBlockConcretes, 0))
                .addElement(
                    'F',
                    buildHatchAdder(OTEFireRocketAssembler.class)
                        .atLeast(
                            OutputBus,
                            InputBus,
                            InputHatch,
                            OutputHatch,
                            Energy.or(ExoticEnergy),
                            RocketHatchElement.SCHEMATIC)
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
        return addSchematicHatch(aTileEntity, aBaseCasingIndex) || super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    private boolean addSchematicHatch(IGregTechTileEntity tile, int casingIndex) {
        IMetaTileEntity meta = tile.getMetaTileEntity();
        if (!(meta instanceof OTERocketSchematicHatch)) return false;
        OTERocketSchematicHatch hatch = (OTERocketSchematicHatch) meta;
        if (mSchematicHatch != null) return mSchematicHatch == hatch;
        hatch.updateTexture(casingIndex);
        mSchematicHatch = hatch;
        addIfSmartInput(hatch);
        return true;
    }

    @Override
    public void clearHatches() {
        super.clearHatches();
        mSchematicHatch = null;
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(EnumChatFormatting.BLUE + translateToLocal("ote.tm.nasa.0"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.1"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.2"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.3"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.nasa.4"))
            .addInfo(EnumChatFormatting.DARK_RED + translateToLocal("ote.tm.nasa.5"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - FIRE-RocketAssembler")
            .addController(translateToLocal("ote.tn.nasa"))
            .beginStructureBlock(37, 17, 13, false)
            .addInputBus(translateToLocal("oth.hint.any_input_bus"), 1)
            .addOutputBus(translateToLocal("oth.hint.any_output_bus"), 1)
            .addInputHatch(translateToLocal("oth.hint.any_input_hatch"), 1)
            .addOutputHatch(translateToLocal("oth.hint.any_output_hatch"), 1)
            .addEnergyHatch(translateToLocal("oth.hint.any_energy_hatch"), 1)
            .addOtherStructurePart(
                translateToLocal(OTERocketSchematicHatch.NAME_KEY),
                translateToLocal("ote.nasa.schematic_hatch_hint"),
                1)
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

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setBoolean("oteNasaPerfectOverclock", isEnablePerfectOverclock());
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        if (!tag.hasKey("oteNasaPerfectOverclock")) return;
        boolean enabled = tag.getBoolean("oteNasaPerfectOverclock");
        currentTip.add(
            (enabled ? EnumChatFormatting.GREEN : EnumChatFormatting.GRAY)
                + translateToLocal(enabled ? "ote.nasa.waila.perfect_oc.enabled" : "ote.nasa.waila.perfect_oc.disabled")
                + EnumChatFormatting.RESET);
    }

    private boolean hasRequiredSpecialItems(Object specialItems) {
        if (specialItems == null) return true;
        if (mSchematicHatch == null || !mSchematicHatch.isValid()) return false;
        if (specialItems instanceof ItemStack) return mSchematicHatch.containsSchematic((ItemStack) specialItems);
        if (specialItems instanceof ItemStack[]) {
            for (ItemStack stack : (ItemStack[]) specialItems) {
                if (stack != null && !mSchematicHatch.containsSchematic(stack)) return false;
            }
            return true;
        }
        return false;
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

    private void updateRenderBlock(IGregTechTileEntity base, ItemStack output) {
        World world = base.getWorld();
        if (world == null) return;

        ExtendedFacing facing = getExtendedFacing();
        ForgeDirection back = facing.getRelativeBackInWorld();
        ForgeDirection right = facing.getRelativeRightInWorld();
        int rocketX = base.getXCoord() + ROCKET_OFFSET_BACK * back.offsetX - ROCKET_OFFSET_RIGHT * right.offsetX;
        int rocketY = base.getYCoord() + ROCKET_OFFSET_BACK * back.offsetY - ROCKET_OFFSET_RIGHT * right.offsetY;
        int rocketZ = base.getZCoord() + ROCKET_OFFSET_BACK * back.offsetZ - ROCKET_OFFSET_RIGHT * right.offsetZ;

        if (mRocketPlaced && (mRocketX != rocketX || mRocketY != rocketY || mRocketZ != rocketZ)) {
            clearRenderBlock();
            if (mRocketPlaced) return;
        }
        if (!world.blockExists(rocketX, rocketY, rocketZ)) return;
        if (world.getBlock(rocketX, rocketY, rocketZ) != RocketRenderBlock
            && (!world.isAirBlock(rocketX, rocketY, rocketZ)
                || !world.setBlock(rocketX, rocketY, rocketZ, RocketRenderBlock, getRocketMeta(base), 3)))
            return;

        TileEntity tile = world.getTileEntity(rocketX, rocketY, rocketZ);
        if (!(tile instanceof OTERocketRender)) return;
        OTERocketRender display = (OTERocketRender) tile;
        int ownerX = base.getXCoord();
        int ownerY = base.getYCoord();
        int ownerZ = base.getZCoord();
        if (!display.isUnclaimed() && !display.isOwnedBy(ownerX, ownerY, ownerZ)) return;
        display.setDisplay(ownerX, ownerY, ownerZ, output);
        if (!mRocketPlaced) {
            rememberRocket(rocketX, rocketY, rocketZ);
            markDirty();
        }
    }

    private void rememberRocket(int x, int y, int z) {
        mRocketPlaced = true;
        mRocketX = x;
        mRocketY = y;
        mRocketZ = z;
    }

    private void clearRenderBlock() {
        if (!mRocketPlaced) return;
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !base.isServerSide()) return;
        World world = base.getWorld();
        if (world == null) return;
        TileEntity tile = world.getTileEntity(mRocketX, mRocketY, mRocketZ);
        if (tile instanceof OTERocketRender
            && ((OTERocketRender) tile).isOwnedBy(base.getXCoord(), base.getYCoord(), base.getZCoord())
            && !world.setBlockToAir(mRocketX, mRocketY, mRocketZ)) return;
        mRocketPlaced = false;
        markDirty();
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
