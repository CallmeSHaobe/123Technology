package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.newmaa.othtech.Utils.Utils.getCoilTier;
import static com.newmaa.othtech.config.Config.Parallel_PerPiece_ISA_Forge;
import static com.newmaa.othtech.config.Config.SpeedBonus_MultiplyPerVoltageTier_ISA_Forge;
import static com.newmaa.othtech.config.Config.SpeedMultiplier_ISA_Forge;
import static gregtech.api.enums.Textures.BlockIcons.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.Utils.Utils;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.GTCM_MultiMachineBase;

import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Utility;

public class Mega_ISA_Forge extends GTCM_MultiMachineBase<Mega_ISA_Forge> {

    public Mega_ISA_Forge(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public Mega_ISA_Forge(String aName) {
        super(aName);
    }

    private int piece = 1;

    private int parallel = 1;

    private float speedBonus = 1;
    private HeatingCoilLevel coilLevel;

    public HeatingCoilLevel getCoilLevel() {
        return coilLevel;
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
        aNBT.setInteger("piece", piece);
        aNBT.setInteger("parallel", parallel);
        aNBT.setFloat("speedBonus", speedBonus);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        piece = aNBT.getInteger("piece");
        parallel = aNBT.getInteger("parallel");
        speedBonus = aNBT.getFloat("speedBonus");
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    protected int getMaxParallelRecipes() {
        return parallel;
    }

    protected float getSpeedBonus() {
        return speedBonus;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.Mega_ISA_Forge;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        coilLevel = HeatingCoilLevel.None;

        this.piece = 0;
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet)) {
            return false;
        }


        parallel = (int) Math.min((long) piece * getCoilTier() * Parallel_PerPiece_ISA_Forge, Integer.MAX_VALUE);
        speedBonus = (float) (Math
            .pow(SpeedBonus_MultiplyPerVoltageTier_ISA_Forge, GT_Utility.getTier(this.getMaxInputEu()))
            / (getCoilTier() * SpeedMultiplier_ISA_Forge));
        return true;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        int piece = stackSize.stackSize;
        this.buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);


    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) return -1;
        int[] built = new int[stackSize.stackSize + 2];

        built[0] = survivialBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);

        return Utils.multiBuildPiece(built);
    }

    private static final String STRUCTURE_PIECE_MAIN = "mainMEGAISA";
    private static final String STRUCTURE_PIECE_MIDDLE = "middleMEGAISA";
    private static final String STRUCTURE_PIECE_END = "endMEGAISA";
    private final int horizontalOffSet = 3;
    private final int verticalOffSet = 3;
    private final int depthOffSet = 0;
    private static IStructureDefinition<Mega_ISA_Forge> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<Mega_ISA_Forge> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<Mega_ISA_Forge>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', ofBlock(GregTech_API.sBlockCasings1, 1))
                .addElement('B', ofBlock(GregTech_API.sBlockCasings1, 1))
                .addElement('C', ofBlock(GregTech_API.sBlockCasings1, 1))
                .addElement('D', ofBlock(GregTech_API.sBlockCasings1, 1))
                .addElement('F', ofBlock(GregTech_API.sBlockCasings1, 1))
                .addElement('I', ofBlock(GregTech_API.sBlockCasings1, 1))
                .addElement('E', ofBlock(GregTech_API.sBlockCasings1, 1))
                .addElement('H', ofBlock(GregTech_API.sBlockCasings1, 1))
                .addElement('G', ofBlock(GregTech_API.sBlockCasings1, 1))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    private final String[][] shapeMain = new String[][] {
        { "       ", "       ", "       ", "       ", "  DDD  ", "  D~D  ", "  DDD  " },
        { "       ", "  III  ", "  III  ", "  III  ", "  DDD  ", "  DDD  ", "  DDD  " },
        { "       ", "  CCC  ", "  CBC  ", "  CCC  ", "       ", "       ", "DDDDDDD" },
        { "  AAA  ", " ACCCA ", " ACBCA ", " ACCCA ", " HAAAH ", " H   H ", "DDDDDDD" },
        { "  EEE  ", " EFFFE ", " EFBFE ", " EFFFE ", "  EEE  ", "       ", "DDDDDDD" },
        { "  EEE  ", " EFFFE ", " EFBFE ", " EFFFE ", "  EEE  ", "       ", "  DDD  " },
        { "  EEE  ", " EFFFE ", " EFBFE ", " EFFFE ", "  EEE  ", "       ", "  DDD  " },
        { "       ", "  GGG  ", "  GBG  ", "  GGG  ", "  DDD  ", "  DDD  ", "  DDD  " },
        { "       ", "  GGG  ", "  GGG  ", "  GGG  ", "       ", "  DDD  ", "  DDD  " } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§b§l老登的终极造物 - 神之艾萨机")
            .addInfo("§c§l'我拒绝了§4血魔法的恩赐§c和§e蜜蜂的甜蜜'")
            .addInfo("§d艾萨领主认证")
            .addInfo("§7无法想象的速度与并行")
            .addSeparator()
            .addController("")
            .addInputBus("")
            .addOutputBus("")
            .toolTipFinisher("§a123Technology - Make ISA great again!");
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
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new Mega_ISA_Forge(this.mName);
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
