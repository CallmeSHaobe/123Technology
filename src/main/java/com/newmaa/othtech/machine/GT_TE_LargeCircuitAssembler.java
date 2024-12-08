package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.newmaa.othtech.Utils.Utils.filterValidMTEs;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static tectech.thing.casing.TTCasingsContainer.TimeAccelerationFieldGenerator;

import java.util.List;

import net.minecraft.block.Block;
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
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import bartworks.API.BorosilicateGlass;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
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
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class GT_TE_LargeCircuitAssembler extends OTH_MultiMachineBase<GT_TE_LargeCircuitAssembler> {

    public GT_TE_LargeCircuitAssembler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_LargeCircuitAssembler(String aName) {
        super(aName);
    }

    public byte glassTier = 0;

    protected int casingTier = 0;
    private byte mode = 0;
    protected int energyHatchTier = 0;

    private String Par = "0";

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("glassTier", glassTier);
        aNBT.setInteger("casingTier", casingTier);
        aNBT.setByte("mode", mode);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        glassTier = aNBT.getByte("glassTier");
        casingTier = aNBT.getInteger("casingTier");
        mode = aNBT.getByte("mode");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            Par = GTUtility.formatNumbers(getMaxParallelRecipes());
            tag.setString("Parallel", Par);
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

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    protected int getMaxParallelRecipes() {
        return 256;
    }

    protected float getSpeedBonus() {
        return (1F / 2.23F);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.circuitAssemblerRecipes;
    }

    public long getMachineVoltageLimit() {
        checkEnergyHatchTier();
        energyHatchTier = checkEnergyHatchTier();
        return GTValues.V[energyHatchTier];
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        boolean useSingleAmp = mEnergyHatches.size() == 1 && mExoticEnergyHatches.isEmpty();
        logic.setAvailableVoltage(getMachineVoltageLimit());
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
                setOverclock(isEnablePerfectOverclock() ? 2 : 1, 2);
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

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();

        return checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);

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

    public static int getCasingTier(Block block, int meta) {
        if (block == TimeAccelerationFieldGenerator && meta <= 3) {
            return meta + 1;
        }
        return 0;
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 6;
    private final int verticalOffSet = 4;
    private final int depthOffSet = 2;
    private static IStructureDefinition<GT_TE_LargeCircuitAssembler> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_LargeCircuitAssembler> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_LargeCircuitAssembler>builder()
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
                .addElement('B', ofBlock(sBlockCasings2, 5))
                .addElement('C', ofBlock(sBlockCasings2, 9))
                .addElement('D', ofBlock(sBlockCasings3, 11))
                .addElement('E', ofBlock(sBlockReinforced, 2))
                .addElement('I', ofFrame(Materials.Titanium))
                .addElement(
                    'H',
                    buildHatchAdder(GT_TE_LargeCircuitAssembler.class).atLeast(Energy.or(ExoticEnergy), Muffler)
                        .adder(GT_TE_LargeCircuitAssembler::addToMachineList)
                        .casingIndex(1539)
                        .dot(1)
                        .buildAndChain(
                            ofBlocksTiered(
                                GT_TE_LargeCircuitAssembler::getCasingTier,
                                ImmutableList.of(
                                    Pair.of(Loaders.impreciseUnitCasing, 0),
                                    Pair.of(Loaders.preciseUnitCasing, 0),
                                    Pair.of(Loaders.preciseUnitCasing, 1),
                                    Pair.of(Loaders.preciseUnitCasing, 2),
                                    Pair.of(Loaders.preciseUnitCasing, 3)),
                                0,
                                (t, meta) -> t.casingTier = meta,
                                t -> t.casingTier)))
                .addElement(
                    'G',
                    buildHatchAdder(GT_TE_LargeCircuitAssembler.class).atLeast(OutputHatch, OutputBus)
                        .adder(GT_TE_LargeCircuitAssembler::addToMachineList)
                        .casingIndex(1539)
                        .dot(2)
                        .buildAndChain(
                            ofBlocksTiered(
                                GT_TE_LargeCircuitAssembler::getCasingTier,
                                ImmutableList.of(
                                    Pair.of(Loaders.impreciseUnitCasing, 0),
                                    Pair.of(Loaders.preciseUnitCasing, 0),
                                    Pair.of(Loaders.preciseUnitCasing, 1),
                                    Pair.of(Loaders.preciseUnitCasing, 2),
                                    Pair.of(Loaders.preciseUnitCasing, 3)),
                                0,
                                (t, meta) -> t.casingTier = meta,
                                t -> t.casingTier)))
                .addElement(
                    'F',
                    buildHatchAdder(GT_TE_LargeCircuitAssembler.class).atLeast(InputBus, InputHatch)
                        .adder(GT_TE_LargeCircuitAssembler::addToMachineList)
                        .casingIndex(1539)
                        .dot(3)
                        .buildAndChain(
                            ofBlocksTiered(
                                GT_TE_LargeCircuitAssembler::getCasingTier,
                                ImmutableList.of(
                                    Pair.of(Loaders.impreciseUnitCasing, 0),
                                    Pair.of(Loaders.preciseUnitCasing, 0),
                                    Pair.of(Loaders.preciseUnitCasing, 1),
                                    Pair.of(Loaders.preciseUnitCasing, 2),
                                    Pair.of(Loaders.preciseUnitCasing, 3)),
                                0,
                                (t, meta) -> t.casingTier = meta,
                                t -> t.casingTier)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structure by NewMaa
    private final String[][] shapeMain = new String[][] {
        { "             ", "             ", "             ", "             ", "             ", "EEEEEEEEEEEEE" },
        { " IIIIIIIIIII ", " I         I ", " I         I ", " I         I ", " II       II ", "EEEEEEEEEEEEE" },
        { " IHHHHHHHHHI ", "             ", "             ", "             ", " IHHHH~HHHHI ", "EEEEEEEEEEEEE" },
        { " IHHHHHHHHHI ", "   HAAAAAF   ", "   HAAAAAF   ", "   HAAAAAF   ", " IGHBHHHBHFI ", "EEEEEEEEEEEEE" },
        { " IHHHHHHHHHI ", "   H     F   ", "   G     F   ", "   G     F   ", " IGHBCCCBHFI ", "EEEEEEEEEEEEE" },
        { " IHHHHHHHHHI ", "  HHHHHHHHH  ", "  HHDDDDDHH  ", "  HHDDDDDHH  ", " IHHHHHHHHHI ", "EEEEEEEEEEEEE" },
        { " IIIIIIIIIII ", " I         I ", " I         I ", " I         I ", " II       II ", "EEEEEEEEEEEEE" },
        { "             ", "             ", "             ", "             ", "             ", "EEEEEEEEEEEEE" } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 1024;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§e§l轻工业计划 - 大型电路组装机")
            .addInfo("§l朴实无华...")
            .addInfo("能源仓等级限制配方等级")
            .addInfo("§q§l并行固定为256, 升级电子单元机械方块获得更加§4§l炫酷§q§l的外观(§e§l这实在是太帅太有用了吧!§q§l)")
            .addInfo("速度 + 123%")
            .addInfo("执行无损超频")
            .addInfo("结构太不复杂了!")
            .addTecTechHatchInfo()
            .addPollutionAmount(1024)
            .addSeparator()
            .addController("电路组装机")
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
        return new GT_TE_LargeCircuitAssembler(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        int t = casingTier;
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(1541 + t),
                TextureFactory.of(textureFontOn), TextureFactory.builder()
                    .addIcon(textureFontOn_Glow)
                    .glow()
                    .build() };
            else return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(1541 + t),
                TextureFactory.of(textureFontOff), TextureFactory.builder()
                    .addIcon(textureFontOff_Glow)
                    .glow()
                    .build() };
        } else return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(1541 + t) };
    }

    private static final IIconContainer textureFontOn = new Textures.BlockIcons.CustomIcon("iconsets/OVERLAY_QTANK");
    private static final IIconContainer textureFontOn_Glow = new Textures.BlockIcons.CustomIcon(
        "iconsets/OVERLAY_QTANK_GLOW");
    private static final IIconContainer textureFontOff = new Textures.BlockIcons.CustomIcon("iconsets/OVERLAY_QCHEST");
    private static final IIconContainer textureFontOff_Glow = new Textures.BlockIcons.CustomIcon(
        "iconsets/OVERLAY_QCHEST_GLOW");

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.IC2_MACHINES_COMPRESSOR_OP;
    }
}
