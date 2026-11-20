package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings3;
import static gregtech.api.GregTechAPI.sBlockCasings4;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.GregTechAPI.sBlockConcretes;
import static gregtech.api.GregTechAPI.sBlockMetal1;
import static gregtech.api.GregTechAPI.sBlockMetal6;
import static gregtech.api.GregTechAPI.sBlockMetal7;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Chisel;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
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
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTESINOPEC extends OTHMultiMachineBase<OTESINOPEC> {

    private boolean isUnlocked = false;

    private HeatingCoilLevel coilLevel;

    private static ItemStack dustIrOsSmM = null;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 26;

    private final int verticalOffSet = 46;

    private final int depthOffSet = 2;

    private static final int HORIZONTAL_DIRT_METAID = 10;

    private static IStructureDefinition<OTESINOPEC> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.sinopec.0") + ":",
        translateToLocal("ote.cm.sinopec.1") };

    // Structured by LyeeR
    @SuppressWarnings("SpellCheckingInspection")
    private final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTESINOPEC_shapeMain.shape.gz");

    // ===================== Constructors =====================

    public OTESINOPEC(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTESINOPEC(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("isUnlocked", isUnlocked);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        isUnlocked = aNBT.getBoolean("isUnlocked");
    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return isUnlocked;
    }

    public int getMaxParallelRecipes() {
        if (isUnlocked) {
            return Integer.MAX_VALUE;
        } else {
            return 64;
        }
    }

    protected float getSpeedBonus() {
        if (getCoilTier() >= 10) {
            return 0.1F;
        }
        return 1 - getCoilTier() * 0.1F;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.SINOPEC;
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 6400;
    }

    public HeatingCoilLevel getCoilLevel() {
        return this.coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    public int getCoilTier() {
        return Utils.getCoilTier(coilLevel);
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new OTHProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                return super.process();
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        updatetier();
        return super.checkProcessing();
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (dustIrOsSmM == null) dustIrOsSmM = OTHItemList.dustIrOsSmM.get(1);
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        updatetier();
        repairMachine();
        coilLevel = HeatingCoilLevel.None;
        checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet, errors);
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
    public IStructureDefinition<OTESINOPEC> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTESINOPEC>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', BorosilicateGlass.ofBoroGlass(3))
                .addElement('B', BorosilicateGlass.ofBoroGlass(3))
                .addElement('C', ofBlock(sBlockCasings2, 13))
                .addElement('D', ofBlock(sBlockCasings2, 15))
                .addElement('E', ofBlock(sBlockCasings3, 15))
                .addElement('G', ofBlock(sBlockCasings4, 1))
                .addElement('H', withChannel("coil", ofCoil(OTESINOPEC::setCoilLevel, OTESINOPEC::getCoilLevel)))
                .addElement('I', ofBlock(sBlockCasings8, 0))
                .addElement('J', ofBlock(sBlockCasings8, 1))
                .addElement('K', ofBlock(sBlockMetal1, 12))
                .addElement('L', ofBlock(sBlockMetal6, 13))
                .addElement('M', ofBlock(sBlockMetal7, 11))
                .addElement('P', ofFrame(Materials.Steel))
                .addElement(
                    'N',
                    (Chisel.isModLoaded() && Block.getBlockFromName(Chisel.ID + ":concrete") != null) ? ofBlock(
                        Objects.requireNonNull(Block.getBlockFromName(Chisel.ID + ":concrete")),
                        HORIZONTAL_DIRT_METAID) : ofBlock(sBlockConcretes, 0))
                .addElement(
                    'F',
                    buildHatchAdder(OTESINOPEC.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTESINOPEC::addToMachineList)
                        .hint(1)
                        .casingIndex(48)
                        .buildAndChain(sBlockCasings4, 0))
                .addElement(
                    'O',
                    buildHatchAdder(OTESINOPEC.class).atLeast(Muffler)
                        .adder(OTESINOPEC::addToMachineList)
                        .hint(2)
                        .casingIndex(48)
                        .buildAndChain(sBlockCasings4, 0))
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
        tt.addMachineType(translateToLocal("ote.tm.sinopec.0"))
            .addInfo(translateToLocal("ote.tm.sinopec.1"))
            .addInfo(translateToLocal("ote.tm.sinopec.2"))
            .addInfo(translateToLocal("ote.tm.sinopec.3"))
            .addInfo(translateToLocal("ote.tm.sinopec.4"))
            .addPollutionAmount(6400)
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - Heavy industry - SINOPEC")
            .addController(translateToLocal("ote.tn.sinopec"))
            .beginStructureBlock(48, 49, 33, false)
            .addInputBus(translateToLocal("oth.hint.any_input_bus"), 1)
            .addOutputBus(translateToLocal("oth.hint.any_output_bus"), 1)
            .addInputHatch(translateToLocal("oth.hint.any_input_hatch"), 1)
            .addOutputHatch(translateToLocal("oth.hint.any_output_hatch"), 1)
            .addEnergyHatch(translateToLocal("oth.hint.any_energy_hatch"), 1)
            .addMufflerHatch(translateToLocal("oth.hint.any_muffler_hatch"), 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTESINOPEC(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings4, 0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings4, 0)),
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
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings4, 0)) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }

    // ===================== Misc =====================

    protected void updatetier() {
        ItemStack aGuiStack = this.getControllerSlot();
        isUnlocked = aGuiStack != null && GTUtility.areStacksEqual(aGuiStack, dustIrOsSmM);
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("123Processing", isUnlocked);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("otht.waila.123") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getBoolean("123Processing")
                + EnumChatFormatting.RESET);
    }
}
