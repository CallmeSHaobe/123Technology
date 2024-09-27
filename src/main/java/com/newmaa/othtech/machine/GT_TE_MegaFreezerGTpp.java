package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.GT_HatchElement.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_HatchElementBuilder;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class GT_TE_MegaFreezerGTpp extends OTH_MultiMachineBase<GT_TE_MegaFreezerGTpp> {

    public GT_TE_MegaFreezerGTpp(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_MegaFreezerGTpp(String aName) {
        super(aName);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    protected int getMaxParallelRecipes() {
        return 256 * 4;
    }

    protected float getSpeedBonus() {
        return 1F / 2F;
    }

    protected float getEuModifier() {
        return 1F;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.vacuumFreezerRecipes;

    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 500 * 256;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GT_Recipe recipe) {
                return checkForLava() ? CheckRecipeResultRegistry.SUCCESSFUL
                    : SimpleCheckRecipeResult.ofFailure("nolava");
            }
            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 2 : 1, 2);
                setEuModifier(getEuModifier());
                return super.process();
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

    public boolean checkForLava() {
        if (this.getStoredFluids() != null) {
            for (FluidStack stored : this.getStoredFluids()) {
                if (stored.isFluidEqual(FluidUtils.getFluidStack("cryotheum", 1))) {
                    if (stored.amount >= 123 * 1000) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 7;
    private final int verticalOffSet = 7;
    private final int depthOffSet = 0;

    private static IStructureDefinition<GT_TE_MegaFreezerGTpp> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_MegaFreezerGTpp> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_MegaFreezerGTpp>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .addElement(
                    'A',
                    GT_HatchElementBuilder.<GT_TE_MegaFreezerGTpp>builder()
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(GT_TE_MegaFreezerGTpp::addToMachineList)
                        .dot(1)
                        .casingIndex(TAE.getIndexFromPage(2, 10))
                        .buildAndChain(ofBlock(ModBlocks.blockCasings3Misc, 10)))

                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // Structure From BW
    private final String[][] shapeMain = new String[][] {
        { "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA",
            "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA",
            "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAA~AAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "A             A",
            "A             A", "A             A", "A             A", "A             A", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA",
            "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA",
            "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA" } };

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§l老登的终极造物 - 真空冷冻机")
            .addInfo("§b  EU消耗: 100%")
            .addInfo("§b  速度 + 100%")
            .addInfo("§b  并行: 1024")
            .addInfo("§6  不消耗极寒之凛冰， 但输入仓必须至少有123 * 1000L极寒之凛冰才可运行")
            .addInfo("§q支持§bTecTech§q能源仓及激光仓，但不支持无线电网直接供给EU")
            .addInfo("§q§l「巨凛冰这个段位就很尴尬，往上走有TST冰箱，再往下有原版立方体，巨凛冰上不去下不来卡在这里了」 ")
            .addPollutionAmount(128000)
            .addSeparator()
            .addController("巨型凛冰冷冻机")
            .beginStructureBlock(15, 15, 15, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§a123Technology - Freezer");
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
        return new GT_TE_MegaFreezerGTpp(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 10)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced_Active)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 10)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 10)) };
    }
}
