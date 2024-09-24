package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.util.GT_StructureUtility.*;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.github.bartimaeusnek.bartworks.API.BorosilicateGlass;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.Utils.Utils;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import gregtech.api.enums.HeatingCoilLevel;
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
import gregtech.api.util.GT_OverclockCalculator;
import gregtech.api.util.GT_Recipe;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class GT_TE_MegaEBFGTpp extends OTH_MultiMachineBase<GT_TE_MegaEBFGTpp> {

    public GT_TE_MegaEBFGTpp(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_MegaEBFGTpp(String aName) {
        super(aName);
    }

    private HeatingCoilLevel coilLevel;

    private HeatingCoilLevel mHeatingCapacity = HeatingCoilLevel.None;

    public HeatingCoilLevel getCoilLevel() {
        return mHeatingCapacity;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    public int getCoilTier() {
        return Utils.getCoilTier(coilLevel);
    }

    private byte glassTier;

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
        return 256 * 8;
    }

    protected float getSpeedBonus() {
        return 1F / 2.2F;
    }

    protected float getEuModifier() {
        return 0.9F;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.blastFurnaceRecipes;

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
                return recipe.mSpecialValue <= coilLevel.getHeat() & checkForWater()
                    ? CheckRecipeResultRegistry.SUCCESSFUL
                    : SimpleCheckRecipeResult.ofFailure("nowater");
            }

            @NotNull
            @Override
            protected GT_OverclockCalculator createOverclockCalculator(@NotNull GT_Recipe recipe) {
                return super.createOverclockCalculator(recipe).setHeatOC(true)
                    .setHeatDiscount(true)
                    .setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat((int) getCoilLevel().getHeat());
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

    public boolean checkForWater() {
        if (this.getStoredFluids() != null) {
            for (FluidStack stored : this.getStoredFluids()) {
                if (stored.isFluidEqual(FluidUtils.getFluidStack("pyrotheum", 1))) {
                    if (stored.amount >= 123 * 1000) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int watermol = 0;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 7;
    private final int verticalOffSet = 17;
    private final int depthOffSet = 0;

    private static IStructureDefinition<GT_TE_MegaEBFGTpp> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_MegaEBFGTpp> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_MegaEBFGTpp>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
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
                .addElement(
                    'B',
                    GT_HatchElementBuilder.<GT_TE_MegaEBFGTpp>builder()
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(GT_TE_MegaEBFGTpp::addToMachineList)
                        .dot(1)
                        .casingIndex(TAE.getIndexFromPage(2, 11))
                        .buildAndChain(ofBlock(ModBlocks.blockCasings3Misc, 11)))
                .addElement(
                    'C',
                    withChannel("coil", ofCoil(GT_TE_MegaEBFGTpp::setCoilLevel, GT_TE_MegaEBFGTpp::getCoilLevel)))
                .addElement(
                    'D',
                    GT_HatchElementBuilder.<GT_TE_MegaEBFGTpp>builder()
                        .atLeast(Muffler)
                        .adder(GT_TE_MegaEBFGTpp::addToMachineList)
                        .dot(1)
                        .casingIndex(TAE.getIndexFromPage(2, 11))
                        .buildAndChain(ofBlock(ModBlocks.blockCasings3Misc, 11)))
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
        { "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBDBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAA~AAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB" } };

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§l老登的终极造物 - 工业高炉")
            .addInfo("§b  EU消耗: 90%")
            .addInfo("§b  速度 + 120%")
            .addInfo("§b  并行: 2048")
            .addInfo("§6  不消耗烈焰之炽焱， 但输入仓必须至少有123 * 1000L烈焰之炽焱才可运行")
            .addInfo("§q支持§bTecTech§q能源仓及激光仓，但不支持无线电网直接供给EU")
            .addInfo("§l 「烈焰之炽焱这个东西啊, 用个几千个就淘汰了」---风哥语录·改 ")
            .addPollutionAmount(128000)
            .addSeparator()
            .addController("巨型炽焱高炉")
            .beginStructureBlock(15, 20, 15, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§a123Technology - EBF");
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
        return new GT_TE_MegaEBFGTpp(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced_Active)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)) };
    }
}
// spotless:on
