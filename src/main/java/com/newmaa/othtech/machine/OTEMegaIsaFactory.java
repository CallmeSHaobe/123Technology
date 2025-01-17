package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import gregtech.api.enums.Materials;
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
import gregtech.api.util.MultiblockTooltipBuilder;

public class OTEMegaIsaFactory extends OTH_MultiMachineBase<OTEMegaIsaFactory> {

    public OTEMegaIsaFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaIsaFactory(String aName) {
        super(aName);
    }

    public byte tierMill = 0;

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("tierMill", tierMill);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        tierMill = aNBT.getByte("tierMill");
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    protected int getMaxParallelRecipes() {
        return 123;
    }

    protected float getSpeedBonus() {
        return 1F;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.MISA;
    }

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

    private final int horizontalOffSet = 3;
    private final int verticalOffSet = 12;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTEMegaIsaFactory> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<OTEMegaIsaFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaIsaFactory>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('B', ofBlock(sBlockCasings1, 6))
                .addElement('C', ofBlock(sBlockCasings4, 12))
                .addElement('E', ofBlock(sBlockCasings8, 1))
                .addElement('G', ofFrame(Materials.Iridium))
                .addElement(
                    'F',
                    buildHatchAdder(OTEMegaIsaFactory.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEMegaIsaFactory::addToMachineList)
                        .dot(1)
                        .casingIndex(6)
                        .buildAndChain(sBlockCasings8, 6))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by
    private final String[][] shapeMain = new String[][] {};

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§2§l艾萨领主的终极造物 - 艾萨集成工厂")
            .addInfo("§o神奇的艾萨系列, 将垃圾和化工产品融为一体, 这不神奇吗?")
            .addInfo("§oNEI点击机器主机, 获取一个集大成之作...")
            .addInfo("§d许多老登们最喜欢的一步到位: 扔掉了乱七八糟的流程, 只是了节约一些tps")
            .addInfo("执行无损超频")
            .addInfo("'研磨器复合体' 决定机器执行的配方等级, 只是一个物品")
            .addInfo("§a更是拥有§4意想不到§a的惊喜呀嘻嘻...")
            .addTecTechHatchInfo()
            .addSeparator()
            .addController("MISA")
            .beginStructureBlock(7, 13, 7, false)
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
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return super.explodesOnComponentBreak(aStack);
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
