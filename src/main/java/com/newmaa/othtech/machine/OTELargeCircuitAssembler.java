package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.newmaa.othtech.utils.Utils.filterValidMTEs;
import static goodgenerator.loader.Loaders.impreciseUnitCasing;
import static goodgenerator.loader.Loaders.preciseUnitCasing;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static gregtech.api.util.GTUtility.validMTEList;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import bartworks.API.BorosilicateGlass;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.IDualInputHatch;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTELargeCircuitAssembler extends OTHMultiMachineBase<OTELargeCircuitAssembler> {

    // region Constructor
    public OTELargeCircuitAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTELargeCircuitAssembler(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTELargeCircuitAssembler(this.mName);
    }
    // endregion

    protected byte mode = 0;
    protected int casingTier = -1;
    protected long machineLimitedVoltage = -1;
    protected static final int CASING_INDEX = 1540;

    // region Logic
    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return Integer.MAX_VALUE;
    }

    protected float getSpeedBonus() {
        return (1F / 2.23F);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.circuitAssemblerRecipes;
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty();
        logic.setAvailableVoltage(this.machineLimitedVoltage);
        logic.setAvailableAmperage(useSingleAmp ? 1 : getMaxInputAmps());
        logic.setAmperageOC(true);
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
                if (availableVoltage < recipe.mEUt) {
                    return SimpleCheckRecipeResult.ofFailure("voltage");
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }
    // endregion

    // region Texture
    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedStone) {
        var id = Math.max(CASING_INDEX, CASING_INDEX + casingTier);
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id),
                TextureFactory.of(textureFontOn), TextureFactory.builder()
                    .addIcon(textureFontOn_Glow)
                    .glow()
                    .build() };
            else return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id),
                TextureFactory.of(textureFontOff), TextureFactory.builder()
                    .addIcon(textureFontOff_Glow)
                    .glow()
                    .build() };
        } else return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(id) };
    }

    private static final IIconContainer textureFontOn = new Textures.BlockIcons.CustomIcon("iconsets/OVERLAY_QTANK");
    private static final IIconContainer textureFontOn_Glow = new Textures.BlockIcons.CustomIcon(
        "iconsets/OVERLAY_QTANK_GLOW");
    private static final IIconContainer textureFontOff = new Textures.BlockIcons.CustomIcon("iconsets/OVERLAY_QCHEST");
    private static final IIconContainer textureFontOff_Glow = new Textures.BlockIcons.CustomIcon(
        "iconsets/OVERLAY_QCHEST_GLOW");

    @Override
    public byte getUpdateData() {
        if (casingTier <= -1) return 0;
        return (byte) casingTier;
    }

    @Override
    public void onValueUpdate(byte aValue) {
        if ((byte) casingTier != aValue) {
            casingTier = (byte) (aValue & 0x0F);
        }
    }
    // endregion

    // region Structure Construct
    private final int horizontalOffSet = 6;
    private final int verticalOffSet = 4;
    private final int depthOffSet = 2;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static IStructureDefinition<OTELargeCircuitAssembler> STRUCTURE_DEFINITION = null;

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        this.casingTier = -2;
        this.machineLimitedVoltage = -1L;
        var res = checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);
        if (res) {
            this.machineLimitedVoltage = GTValues.V[checkEnergyHatchTier()];
            if (casingTier >= 0) {
                reUpdate(Math.max(CASING_INDEX, CASING_INDEX + casingTier));
            }
        }
        return res;
    }

    private final List<List<? extends MTEHatch>> hatchList = Arrays.asList(
        mInputHatches,
        mInputBusses,
        mOutputHatches,
        mOutputBusses,
        mEnergyHatches,
        mMaintenanceHatches,
        mMufflerHatches,
        mExoticEnergyHatches);

    public void reUpdate(int texture) {
        for (IDualInputHatch hatch : mDualInputHatches) {
            if (((MetaTileEntity) hatch).isValid()) {
                hatch.updateTexture(texture);
            }
        }

        for (List<? extends MTEHatch> mteHatches : hatchList) {
            for (MTEHatch mteHatch : validMTEList(mteHatches)) {
                mteHatch.updateTexture(texture);
            }
        }
    }

    private int checkEnergyHatchTier() {
        int tier = 0;
        for (MTEHatchEnergy tHatch : filterValidMTEs(mEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        for (MTEHatch tHatch : filterValidMTEs(mExoticEnergyHatches)) {
            tier = Math.max(tHatch.mTier, tier);
        }
        return tier;
    }

    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 消声仓, 能源仓 : 替换精密机械方块, 支持TecTech能源仓") + ":", translateToLocal("2 - 输出仓, 输出总线") + ":",
        translateToLocal("3 - 输入仓, 输入总线") + ":", translateToLocal("细节仅供参考因为我不会写仓室位置限制憋憋") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTELargeCircuitAssembler> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            var c = StructureUtility.<OTELargeCircuitAssembler, Integer>ofBlocksTiered(
                (a, b) -> a == impreciseUnitCasing ? 0 : a == preciseUnitCasing ? b + 1 : -1,
                ImmutableList.of(
                    Pair.of(impreciseUnitCasing, 0),
                    Pair.of(preciseUnitCasing, 0),
                    Pair.of(preciseUnitCasing, 1),
                    Pair.of(preciseUnitCasing, 2),
                    Pair.of(preciseUnitCasing, 3)),
                -2,
                (t, meta) -> t.casingTier = meta,
                t -> t.casingTier);
            STRUCTURE_DEFINITION = StructureDefinition.<OTELargeCircuitAssembler>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', BorosilicateGlass.ofBoroGlass(6))
                .addElement('B', ofBlock(sBlockCasings2, 5))
                .addElement('C', ofBlock(sBlockCasings2, 9))
                .addElement('D', ofBlock(sBlockCasings3, 11))
                .addElement('E', ofBlock(sBlockReinforced, 2))
                .addElement('I', ofFrame(Materials.Titanium))
                .addElement(
                    'H',
                    buildHatchAdder(OTELargeCircuitAssembler.class).atLeast(Energy.or(ExoticEnergy), Muffler)
                        .adder(OTELargeCircuitAssembler::addToMachineList)
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .buildAndChain(c))
                .addElement(
                    'G',
                    buildHatchAdder(OTELargeCircuitAssembler.class).atLeast(OutputHatch, OutputBus)
                        .adder(OTELargeCircuitAssembler::addToMachineList)
                        .casingIndex(CASING_INDEX)
                        .dot(2)
                        .buildAndChain(c))
                .addElement(
                    'F',
                    buildHatchAdder(OTELargeCircuitAssembler.class).atLeast(InputBus, InputHatch)
                        .adder(OTELargeCircuitAssembler::addToMachineList)
                        .casingIndex(CASING_INDEX)
                        .dot(3)
                        .buildAndChain(c))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // region Structured by NewMaa
    private final String[][] shapeMain = new String[][] {
        { "             ", "             ", "             ", "             ", "             ", "EEEEEEEEEEEEE" },
        { " IIIIIIIIIII ", " I         I ", " I         I ", " I         I ", " II       II ", "EEEEEEEEEEEEE" },
        { " IHHHHHHHHHI ", "             ", "             ", "             ", " IHHHH~HHHHI ", "EEEEEEEEEEEEE" },
        { " IHHHHHHHHHI ", "   HAAAAAF   ", "   HAAAAAF   ", "   HAAAAAF   ", " IGHBHHHBHFI ", "EEEEEEEEEEEEE" },
        { " IHHHHHHHHHI ", "   H     F   ", "   G     F   ", "   G     F   ", " IGHBCCCBHFI ", "EEEEEEEEEEEEE" },
        { " IHHHHHHHHHI ", "  HHHHHHHHH  ", "  HHDDDDDHH  ", "  HHDDDDDHH  ", " IHHHHHHHHHI ", "EEEEEEEEEEEEE" },
        { " IIIIIIIIIII ", " I         I ", " I         I ", " I         I ", " II       II ", "EEEEEEEEEEEEE" },
        { "             ", "             ", "             ", "             ", "             ", "EEEEEEEEEEEEE" } };
    // endregion

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
    // endregion

    // region Misc

    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType("§e§l轻工业计划 - 大型电路组装机")
     * .addInfo("§l朴实无华...")
     * .addInfo("能源仓等级限制配方等级")
     * .addInfo("§q§l升级电子单元机械方块获得更加§4§l炫酷§q§l的外观(§e§l这实在是太帅太有用了吧!§q§l)")
     * .addInfo("速度 + 123%")
     * .addInfo("执行无损超频")
     * .addInfo("结构太不复杂了!")
     * .addTecTechHatchInfo()
     * .addPollutionAmount(1024)
     * .addSeparator()
     * .addController("电路组装机")
     * .beginStructureBlock(8, 6, 13, false)
     * .addInputBus("AnyInputBus", 3)
     * .addOutputBus("AnyOutputBus", 2)
     * .addInputHatch("AnyInputHatch", 3)
     * .addOutputHatch("AnyOutputHatch", 2)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .addMufflerHatch("AnyMufflerHatch", 1)
     * .toolTipFinisher("§a123Technology - Light Industry - LargeCircuitAssembler");
     * return tt;
     * }
     */
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.lca.0"))
            .addInfo(translateToLocal("ote.tm.lca.1"))
            .addInfo(translateToLocal("ote.tm.lca.2"))
            .addInfo(translateToLocal("ote.tm.lca.3"))
            .addInfo(translateToLocal("ote.tm.lca.4"))
            .addInfo(translateToLocal("ote.tm.lca.5"))
            .addInfo(translateToLocal("ote.tm.lca.6"))
            .addTecTechHatchInfo()
            .addPollutionAmount(1024)
            .addSeparator()
            .addController(translateToLocal("ote.tn.lca"))
            .beginStructureBlock(8, 6, 13, false)
            .addInputBus("AnyInputBus", 3)
            .addOutputBus("AnyOutputBus", 2)
            .addInputHatch("AnyInputHatch", 3)
            .addOutputHatch("AnyOutputHatch", 2)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§a123Technology - Light Industry - LargeCircuitAssembler");
        return tt;
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 1024;
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.IC2_MACHINES_COMPRESSOR_OP;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("mode", mode);
        aNBT.setInteger("casingTier", casingTier);
        aNBT.setLong("machineLimitedVoltage", machineLimitedVoltage);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mode = aNBT.getByte("mode");
        casingTier = aNBT.getInteger("casingTier");
        machineLimitedVoltage = aNBT.getLong("machineLimitedVoltage");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String par = GTUtility.formatNumbers(getMaxParallelRecipes());
            tag.setString("Parallel", par);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "并行" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.BLUE
                + tag.getString("Parallel")
                + EnumChatFormatting.RESET);
    }
    // endregion
}
