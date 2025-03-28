package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
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
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class OTEMegaFreezerGTpp extends OTH_MultiMachineBase<OTEMegaFreezerGTpp> {

    public OTEMegaFreezerGTpp(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaFreezerGTpp(String aName) {
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
        return GTPPRecipeMaps.advancedFreezerRecipes;

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
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return checkForLava() ? CheckRecipeResultRegistry.SUCCESSFUL
                    : SimpleCheckRecipeResult.ofFailure("nolava");
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
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

    private static IStructureDefinition<OTEMegaFreezerGTpp> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 消声仓, 能源仓, 输入输出总线, 输入输出仓 : 替换凛冰机械方块, 支持TecTech能源仓"),
        EnumChatFormatting.GOLD + translateToLocal("你真做啊?") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEMegaFreezerGTpp> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaFreezerGTpp>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .addElement(
                    'A',
                    buildHatchAdder(OTEMegaFreezerGTpp.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(OTEMegaFreezerGTpp::addToMachineList)
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

    // Structured by BW
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
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§l老登的终极造物 - 真空冷冻机")
            .addInfo("§b  EU消耗: 100%")
            .addInfo("§b  速度 + 100%")
            .addInfo("§b  并行: 1024")
            .addInfo("§6  不消耗极寒之凛冰， 但输入仓必须至少有123 * 1000L极寒之凛冰才可运行")
            .addInfo("§q§l「巨凛冰这个段位就很尴尬，往上走有TST冰箱，再往下有原版立方体，巨凛冰上不去下不来卡在这里了」 ")
            .addInfo("§c§l注意:机器污染过高:如遇跳电并报错“无法排出污染”, 请尝试放置多个消声仓")
            .addTecTechHatchInfo()
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
            .toolTipFinisher("§a123Technology - VacuumFreezer");
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
        return new OTEMegaFreezerGTpp(this.mName);
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
