package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static kubatech.loaders.BlockLoader.defcCasingBlock;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
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

import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import kubatech.loaders.BlockLoader;
import kubatech.loaders.DEFCRecipes;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEGraveDragon extends OTH_MultiMachineBase<OTEGraveDragon> {

    public OTEGraveDragon(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEGraveDragon(String mName) {
        super(mName);
    }

    @Override
    public IStructureDefinition<OTEGraveDragon> getStructureDefinition() {
        if (STRUCTURE_DEFINITiON == null) {
            STRUCTURE_DEFINITiON = StructureDefinition.<OTEGraveDragon>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'D',
                    withChannel("coil", ofCoil(OTEGraveDragon::setCoilLevel, OTEGraveDragon::getCoilLevel)))
                .addElement(
                    'C',
                    ofBlocksTiered(
                        OTEGraveDragon::getMachanicalblocksDate,
                        ImmutableList.of(
                            Pair.of(defcCasingBlock, 8),
                            Pair.of(defcCasingBlock, 9),
                            Pair.of(defcCasingBlock, 10),
                            Pair.of(defcCasingBlock, 11),
                            Pair.of(defcCasingBlock, 12)),
                        0,
                        (ne, n) -> ne.MachanicalblocksDate = n,
                        ne -> ne.MachanicalblocksDate))
                .addElement('A', ofBlock(ModBlocks.blockCasings6Misc, 0))
                .addElement('E', ofBlock(Blocks.dragon_egg, 0))
                .addElement(
                    'B',
                    buildHatchAdder(OTEGraveDragon.class)
                        .atLeast(InputBus, InputHatch, OutputBus, OutputHatch, Energy.or(ExoticEnergy))
                        .casingIndex(CASING_INDEX)
                        .dot(1)
                        .buildAndChain(BlockLoader.defcCasingBlock, 7))
                .build();
        }
        return STRUCTURE_DEFINITiON;
    }

    private static final int CASING_INDEX = (1 << 7) + (15 + 48);

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new OTEGraveDragon(mName);
    }

    private int MachanicalblocksDate = 0;

    public static int getMachanicalblocksDate(Block block, int meta) {
        if (block == defcCasingBlock) {
            return meta - 7;
        }
        return 0;
    }

    public HeatingCoilLevel coilLevel;

    public HeatingCoilLevel getCoilLevel() {
        return coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    protected boolean isEnablePerfectOverclock() {
        if (MachanicalblocksDate < 3) {
            return false;
        } else {
            return true;
        }
    }

    protected int getMaxParallelRecipes() {
        switch (MachanicalblocksDate) {
            case 1:
                return 128;
            case 2:
                return 256;
            case 3:
                return 512;
            case 4:
                return 1024;
            case 5:
                return Integer.MAX_VALUE;
            default:
                return 64;
        }
    }

    // 耗时倍率
    protected float getSpeedBonus() {
        return (float) (1 - coilLevel.getLevel() * 0.05);
    }

    public int getMaxEfficiency() {
        return 10000;
    }

    public boolean explodesOnComponentBreak() {

        return false;
    }

    public RecipeMap<?> getRecipeMap() {
        return DEFCRecipes.fusionCraftingRecipes;
    }

    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1-能源仓，输入输出总线，输入输出仓：替换硅岩聚合机械方块，支持Tech的能源仓"), };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        setupProcessingLogic(processingLogic);
        CheckRecipeResult result = doCheckRecipe();
        result = postCheckRecipe(result, processingLogic);
        updateSlots();
        if (!result.wasSuccessful()) return result;

        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mOutputFluids = processingLogic.getOutputFluids();
        mOutputItems = processingLogic.getOutputItems();
        mMaxProgresstime = processingLogic.getDuration();
        setEnergyUsage(processingLogic);
        return result;
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
                return recipe.mSpecialValue <= MachanicalblocksDate ? CheckRecipeResultRegistry.SUCCESSFUL
                    : CheckRecipeResultRegistry.insufficientMachineTier(recipe.mSpecialValue);
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

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 9;
    private final int verticalOffSet = 9;
    private final int depthOffSet = 7;

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { TextureFactory.of(MACHINE_CASING_MAGIC), TextureFactory.builder()
                .addIcon(OVERLAY_TELEPORTER_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_TELEPORTER_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { TextureFactory.of(MACHINE_CASING_MAGIC), TextureFactory.builder()
                .addIcon(OVERLAY_TELEPORTER)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_TELEPORTER_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        if (aActive) return new ITexture[] { TextureFactory.of(MACHINE_CASING_MAGIC), TextureFactory.builder()
            .addIcon(MACHINE_CASING_MAGIC_ACTIVE)
            .extFacing()
            .build(),
            TextureFactory.builder()
                .addIcon(MACHINE_CASING_MAGIC_ACTIVE_GLOW)
                .extFacing()
                .glow()
                .build() };
        return new ITexture[] { TextureFactory.of(MACHINE_CASING_MAGIC), TextureFactory.builder()
            .addIcon(MACHINE_CASING_MAGIC)
            .extFacing()
            .build(),
            TextureFactory.builder()
                .addIcon(MACHINE_CASING_MAGIC_GLOW)
                .extFacing()
                .glow()
                .build() };
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    private static IStructureDefinition<OTEGraveDragon> STRUCTURE_DEFINITiON = null;
    // spotless:off
    private static final String[][] shapeMain = new String[][] {
        { "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", " CCC           CCC " },
        { "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "                   ", "                   ",
            "                   ", "  C             C  ", "CCCCA         ACCCC" },
        { "  A             A  ", "  A             A  ", "  A             A  ", "  A             A  ",
            "  CA            A  ", "  CA           AC  ", "  CC           AC  ", "  CC           CC  ",
            "  CC           CC  ", " CCC           CCC ", "CCCCBBBBBBBBBBBCCCC" },
        { "                   ", "   D           D   ", "                   ", "                   ",
            "  A                ", "  A             A  ", "  C             A  ", "  C             C  ",
            "  C             C  ", "  C             C  ", "CCCBBBBBBBBBBBBBCCC" },
        { "                   ", "                   ", "    D         D    ", "                   ",
            "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", " ABBCCCBBBBBCCCBB  " },
        { "                   ", "                   ", "                   ", "     D       D     ",
            "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "  BBCBBBBBBBBBCBB  " },
        { "                   ", "                   ", "                   ", "                   ",
            "      D     D      ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "  BBCBBBBBBBBBCBB  " },
        { "                   ", "                   ", "                   ", "                   ",
            "                   ", "       D   D       ", "                   ", "                   ",
            "                   ", "       CA~AC       ", "  BBBBBBBBBBBBBBB  " },
        { "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "        DCD        ", "                   ",
            "                   ", "       A   A       ", "  BBBBBBBBBBBBBBB  " },
        { "                   ", "                   ", "                   ", "                   ",
            "         E         ", "         C         ", "        CDC        ", "         C         ",
            "         C         ", "       A C A       ", "  BBBBBBBBBBBBBBB  " },
        { "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "        DCD        ", "                   ",
            "                   ", "       A   A       ", "  BBBBBBBBBBBBBBB  " },
        { "                   ", "                   ", "                   ", "                   ",
            "                   ", "       D   D       ", "                   ", "                   ",
            "                   ", "       CAAAC       ", "  BBBBBBBBBBBBBBB  " },
        { "                   ", "                   ", "                   ", "                   ",
            "      D     D      ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "  BBCBBBBBBBBBCBB  " },
        { "                   ", "                   ", "                   ", "     D       D     ",
            "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "  BBCBBBBBBBBBCBB  " },
        { "                   ", "                   ", "    D         D    ", "                   ",
            "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", " ABBCCCBBBBBCCCBB  " },
        { "                   ", "   D           D   ", "                   ", "                   ",
            "                   ", "  A             A  ", "  A             A  ", "  C             C  ",
            "  C             C  ", "  C             C  ", "CCCBBBBBBBBBBBBBCCC" },
        { "  A             A  ", "  A             A  ", "  A             A  ", "  A             A  ",
            "  A             A  ", "  CA           AC  ", "  CA           AC  ", "  CC           CC  ",
            "  CC           CC  ", " CCC           CCC ", "CCCCBBBBBBBBBBBCCCC" },
        { "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "                   ", "                   ",
            "                   ", "  C             C  ", "CCCCA         ACCCC" },
        { "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", "                   ", "                   ",
            "                   ", "                   ", " CCC           CCC " } };

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§c§l龙研聚合装置")
            .addInfo("§e§l世上只有一种英雄主义")
            .addInfo("§e§l那就是在认清生活后依然热爱生活")
            .addInfo("§c§l铱锇钐倾尽所有创造的墓穴")
            .addInfo("拥有不可思议的力量")
            .addInfo("三级机械方块解锁无损超频")
            .addInfo("并行=机械方块等级 * 128，最高等级解锁int并行")
            .addInfo("§e耗时倍率 = 1 - 0.05 * 加热圈等级")
            .addInfo("§e机械方块等级限制可执行的配方等级")
            .addInfo("§e更换结构时请重放主机以检测结构！！！")
            .addTecTechHatchInfo()
            .addSeparator()
            .addController("巨龙之墓")
            .beginStructureBlock(19, 11, 19, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§a123Technology - §c§l巨龙的悲鸣！");
        return tt;
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>(Arrays.asList(super.getInfoData()));
        return str.toArray(new String[0]);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("MachanicalblocksDate", MachanicalblocksDate);
        aNBT.setInteger("coilLevel", coilLevel.getLevel());
        aNBT.setFloat("speedBonus", getSpeedBonus());
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        MachanicalblocksDate = aNBT.getInteger("MachanicalblocksDate");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setString("SpeedBonus", String.valueOf(getSpeedBonus()));
            tag.setString("MachanicalblocksDate", String.valueOf(MachanicalblocksDate));
            tag.setBoolean("PerfectOverclock", isEnablePerfectOverclock());
            tag.setString("MaxParallelRecipes", String.valueOf(getMaxParallelRecipes()));
            tag.setString("coilLevel", String.valueOf(coilLevel.getLevel()));
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            EnumChatFormatting.BOLD + "耗时倍率"
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("SpeedBonus")
                + EnumChatFormatting.GOLD
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + "无损超频"
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.RESET
                + tag.getBoolean("PerfectOverclock")
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + "机械方块等级"
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("MachanicalblocksDate")
                + EnumChatFormatting.GOLD
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + "并行数量"
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("MaxParallelRecipes")
                + EnumChatFormatting.GOLD
                + EnumChatFormatting.RESET);
        currentTip.add(
            EnumChatFormatting.BOLD + "线圈等级"
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("coilLevel")
                + EnumChatFormatting.GOLD
                + EnumChatFormatting.RESET);
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
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

}
