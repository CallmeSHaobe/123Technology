package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static goodgenerator.loader.Loaders.pressureResistantWalls;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.HashSet;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.TT_MultiMachineBase_EM;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.multitileentity.multiblock.casing.Glasses;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import tectech.thing.metaTileEntity.multi.base.INameFunction;
import tectech.thing.metaTileEntity.multi.base.IStatusFunction;
import tectech.thing.metaTileEntity.multi.base.LedStatus;
import tectech.thing.metaTileEntity.multi.base.Parameters;

public class OTEHeatExchanger extends TT_MultiMachineBase_EM implements ISurvivalConstructable, IConstructable {

    public OTEHeatExchanger(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEHeatExchanger(String aName) {
        super(aName);
    }

    Parameters.Group.ParameterIn time;
    private static final IStatusFunction<OTEHeatExchanger> timeSTATUES = (base, p) -> LedStatus
        .fromLimitsInclusiveOuterBoundary(p.get(), 1, 2, 1000, 114514);
    private static final INameFunction<OTEHeatExchanger> timeName = (base, p) -> GCCoreUtil.translate("耗时");

    @Override
    protected void parametersInstantiation_EM() {
        Parameters.Group hatch_0 = parametrization.getGroup(0, false);
        time = hatch_0.makeInParameter(0, 1, timeName, timeSTATUES);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.EXH;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing_EM() {
        final HashSet<String> IngotHotOreDictNames = new HashSet<>();
        final HashSet<String> IngotOreDictNames = new HashSet<>();
        final HashSet<GTUtility.ItemId> IngotHots = new HashSet<>();
        final HashSet<GTUtility.ItemId> Ingots = new HashSet<>();
        for (GTRecipe recipe : RecipeMaps.blastFurnaceRecipes.getAllRecipes()) {
            for (String name : OreDictionary.getOreNames()) {
                if (name.startsWith("ingotHot")) {
                    IngotHotOreDictNames.add(name);
                    for (ItemStack items : OreDictionary.getOres(name)) {
                        IngotHots.add(GTUtility.ItemId.createWithoutNBT(items));
                    }
                }
            }
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
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

    private final int horizontalOffSet = 6;
    private final int verticalOffSet = 7;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTEHeatExchanger> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 输入输出总线, 输入输出仓, 能源仓 : 替换钨钢机械方块, 支持TecTech能源仓") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEHeatExchanger> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEHeatExchanger>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .addElement('A', Glasses.chainAllGlasses())

                .addElement('B', ofBlock(sBlockCasings2, 1))
                .addElement('C', ofBlock(sBlockCasings2, 15))
                .addElement('D', ofBlock(sBlockCasings3, 10))
                .addElement('F', ofBlock(pressureResistantWalls, 0))
                .addElement(
                    'E',
                    buildHatchAdder(OTEHeatExchanger.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEHeatExchanger::addToMachineList)
                        .dot(1)
                        .casingIndex(48)
                        .buildAndChain(sBlockCasings4, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by NewMaa
    private final String[][] shapeMain = new String[][] {
        { "             ", "             ", "             ", "   BBBBBBB   ", "   BBBBBBB   ", "   BDDDDDB   ",
            "   BBBBBBB   ", "   BBBBBBB   ", "             ", "             ", "             " },
        { "             ", " BBB BBB BBB ", " BBBBBBBBBBB ", " BBBBBBBBBBB ", " BBBBBBBBBBB ", " BBBBBBBBBBB ",
            " BBBBBBBBBBB ", " BBBBBBBBBBB ", " BBBBBBBBBBB ", " BBB BBB BBB ", "             " },
        { " EEE EEE EEE ", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE",
            "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", " EEE EEE EEE " },
        { " EEE EEE EEE ", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA",
            "AFFFAFFFAFFFA", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA", " EEE EEE EEE " },
        { " EEE EEE EEE ", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA",
            "AFFFAFFFAFFFA", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA", " EEE EEE EEE " },
        { " EEE EEE EEE ", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA",
            "AFFFAFFFAFFFA", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA", " EEE EEE EEE " },
        { " EEE EEE EEE ", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA",
            "AFFFAFFFAFFFA", "ACCCACCCACCCA", "AFFFAFFFAFFFA", "ACCCACCCACCCA", " EEE EEE EEE " },
        { " EEE E~E EEE ", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE",
            "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", "EEEEEEEEEEEEE", " EEE EEE EEE " } };

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(EnumChatFormatting.BLUE + "真正的热能饕餮 - 真空冷冻热交换机")
            .addInfo(EnumChatFormatting.RED + "回收热锭冶炼的废热, 化为超临界蒸汽.")
            .addInfo(EnumChatFormatting.RED + "理论可回收热锭高炉配方50%之EU")
            .addInfo(EnumChatFormatting.RED + "物理学还存在吗?")
            .addInfo(EnumChatFormatting.YELLOW + "耗电: 0EU/t , 耗时请使用主机参数调整")
            .addTecTechHatchInfo()
            .addSeparator()
            .addController("热交换e")
            .beginStructureBlock(13, 8, 11, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - FrostyHeatExchanger");
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEHeatExchanger(this.mName);
    }

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

}
