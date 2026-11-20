package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.GregTechAPI.sBlockCasingsDyson;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;

import bartworks.API.BorosilicateGlass;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
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
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import gtnhintergalactic.recipe.IGRecipeMaps;
import tectech.thing.block.BlockQuantumGlass;

public class OTEMegaIsaForge extends OTHMultiMachineBase<OTEMegaIsaForge> {

    private byte mode = 0;

    private boolean isUnlocked = false;

    private byte glassTier = 0;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 32;

    private final int verticalOffSet = 46;

    private final int depthOffSet = 19;

    private static IStructureDefinition<OTEMegaIsaForge> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.mifo.0") };

    // Structured by JulianChum Version 1 & 2
    private final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEMegaIsaForge_shapeMain.shape.gz");

    // ===================== Constructors =====================

    public OTEMegaIsaForge(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaIsaForge(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("isUnlocked", isUnlocked);
        aNBT.setInteger("mode", mode);
        aNBT.setByte("glassTier", glassTier);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        isUnlocked = aNBT.getBoolean("isUnlocked");
        mode = aNBT.getByte("mode");
        glassTier = aNBT.getByte("glassTier");

    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    // Speedbouns = 1 / {VoltageTier^[(glassTier / 2)+1]}^(1/3)
    protected float getSpeedBonus() {
        return (float) (1 / Math
            .pow(Math.pow(GTUtility.getTier(this.getMaxInputEu()), (((double) glassTier / 2) + 1)), ((double) 1 / 3)));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (mode == 0) return Recipemaps.MegaIsaForge;
        return IGRecipeMaps.spaceAssemblerRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(Recipemaps.MegaIsaForge, IGRecipeMaps.spaceAssemblerRecipes);
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
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
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

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTHProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

            @Override
            protected @NotNull CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return !isUnlocked && mode == 1 ? SimpleCheckRecipeResult.ofFailure("nofb")
                    : CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.enablePerfectOverclock()
            .setMaxParallelSupplier(this::getMaxParallelRecipes);

    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        updatetier();
        return super.checkProcessing();
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet, errors)) return;
        updatetier();
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
    public IStructureDefinition<OTEMegaIsaForge> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaIsaForge>builder()
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
                .addElement('B', ofBlock(sBlockCasingsBA0, 12))
                .addElement('C', ofBlock(sBlockCasingsTT, 8))
                .addElement('D', ofBlock(sBlockCasingsTT, 10))
                .addElement('F', ofBlock(SpacetimeCompressionFieldGenerators, 8))
                .addElement('G', ofBlock(StabilisationFieldGenerators, 8))
                .addElement('H', ofBlock(TimeAccelerationFieldGenerator, 8))
                .addElement('I', ofBlock(ModBlocks.blockCasings5Misc, 1))
                .addElement('J', ofBlock(sBlockCasingsDyson, 1))
                .addElement('K', ofBlock(sBlockCasingsDyson, 5))
                .addElement('L', ofBlock(sBlockCasingsDyson, 9))
                .addElement('M', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement('N', ofFrame(Materials.InfinityCatalyst))
                .addElement(
                    'E',
                    buildHatchAdder(OTEMegaIsaForge.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(OTEMegaIsaForge::addToMachineList)
                        .hint(1)
                        .casingIndex(1024 + 12)
                        .buildAndChain(sBlockCasingsTT, 12))
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
        tt.addMachineType(translateToLocal("ote.tm.mifo.0"))
            .addInfo(translateToLocal("ote.tm.mifo.1"))
            .addInfo(translateToLocal("ote.tm.mifo.2"))
            .addInfo(translateToLocal("ote.tm.mifo.3"))
            .addInfo(translateToLocal("ote.tm.mifo.4"))
            .addInfo(translateToLocal("ote.tm.mifo.5"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - Make ISA great again!")
            .addController(translateToLocal("ote.tn.mifo"))
            .beginStructureBlock(64, 50, 64, false)
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
        return new OTEMegaIsaForge(this.mName);
    }

    // ===================== Textures =====================

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

    // ===================== Misc =====================

    protected void updatetier() {
        ItemStack aGuiStack = this.getControllerSlot();
        isUnlocked = aGuiStack != null && GTUtility
            .areStacksEqual(aGuiStack, GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1));
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (mode < 1 && isUnlocked) {
                mode++;
            } else {
                mode = 0;
            }
            switch (mode) {
                case 0 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.mifo.mode.0"));
                case 1 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.mifo.mode.1"));
            }
        }
    }
}
