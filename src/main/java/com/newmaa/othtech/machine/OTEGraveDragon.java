package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.sBlockReinforced;
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
import com.newmaa.othtech.common.blocks.nailong.NailongCasings;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;

import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import kubatech.loaders.DEFCRecipes;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEGraveDragon extends OTHMultiMachineBase<OTEGraveDragon> {

    private static final int CASING_INDEX = (1 << 7) + (15 + 48);

    private int MachanicalblocksDate = 0;

    public HeatingCoilLevel coilLevel = HeatingCoilLevel.None;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.gd.0"), };

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 33;

    private final int verticalOffSet = 58;

    private final int depthOffSet = 43;

    private static IStructureDefinition<OTEGraveDragon> STRUCTURE_DEFINITiON = null;

    // spotless:off
    private static final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEGraveDragon_shapeMain.shape.gz");

    // ===================== Constructors =====================

    public OTEGraveDragon(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEGraveDragon(String mName) {
        super(mName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("MachanicalblocksDate", MachanicalblocksDate);
        aNBT.setInteger("coilLevel", coilLevel.getTier());
        aNBT.setFloat("speedBonus", getSpeedBonus());
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        MachanicalblocksDate = aNBT.getInteger("MachanicalblocksDate");
    }

    // ===================== Machine Parameters =====================

    protected boolean isEnablePerfectOverclock() {
        if (MachanicalblocksDate < 3) {
            return false;
        } else {
            return true;
        }
    }

    public int getMaxParallelRecipes() {
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
        return (float) (1 - coilLevel.getTier() * 0.05);
    }

    public RecipeMap<?> getRecipeMap() {
        return DEFCRecipes.fusionCraftingRecipes;
    }

    public int getMaxEfficiency() {
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

    public HeatingCoilLevel getCoilLevel() {
        return coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTHProcessingLogic() {

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

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet, errors)) return;
        repairMachine();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
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
    public IStructureDefinition<OTEGraveDragon> getStructureDefinition() {
        if (STRUCTURE_DEFINITiON == null) {
            STRUCTURE_DEFINITiON = StructureDefinition.<OTEGraveDragon>builder()
                // shapeMain 是「转置扫描」的原始输出，这里用 transpose() 还原成 [切片][行]
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                // C：加热线圈（任意等级），决定耗时倍率
                .addElement(
                    'C',
                    withChannel("coil", ofCoil(OTEGraveDragon::setCoilLevel, OTEGraveDragon::getCoilLevel)))
                // B：丝岩聚合机械方块 8~12，机械方块等级来源（并行 / 无损超频）
                .addElement(
                    'B',
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
                .addElement('A', ofBlock(defcCasingBlock, 7))
                .addElement(
                    'J',
                    buildHatchAdder(OTEGraveDragon.class).atLeast(InputBus, InputHatch, OutputBus, OutputHatch)
                        .casingIndex(CASING_INDEX)
                        .hint(1)
                        .buildAndChain(defcCasingBlock, 7))
                // D：gt.blockreinforced meta 11 —— 能源仓占位（能源仓 + TecTech 异种能源仓都只放这里）
                .addElement('D', ofBlock(sBlockReinforced, 11))
                .addElement(
                    'G',
                    buildHatchAdder(OTEGraveDragon.class).atLeast(Energy.or(ExoticEnergy))
                        .casingIndex(CASING_INDEX)
                        .hint(2)
                        .buildAndChain(sBlockReinforced, 11))
                // E：GT++ 融合机械方块 MK-IV
                .addElement('E', ofBlock(ModBlocks.blockCasings6Misc, 0))
                // F：Chisel 萤石
                .addElement('F', ofBlock(Blocks.glowstone, 0))
                // H / I：奈隆机械方块 / 黄桃罐头机械方块
                .addElement('H', ofBlock(NailongCasings.nailongCasing, 0))
                .addElement('I', ofBlock(NailongCasings.nailongCasing, 1))
                .build();
        }
        return STRUCTURE_DEFINITiON;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.gd.0"))
            .addInfo(translateToLocal("ote.tm.gd.1"))
            .addInfo(translateToLocal("ote.tm.gd.2"))
            .addInfo(translateToLocal("ote.tm.gd.3"))
            .addInfo(translateToLocal("ote.tm.gd.4"))
            .addInfo(translateToLocal("ote.tm.gd.5"))
            .addInfo(translateToLocal("ote.tm.gd.6"))
            .addInfo(translateToLocal("ote.tm.gd.7"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addController(translateToLocal("ote.tn.gd"))
            .beginStructureBlock(67, 63, 67, false)
            .addInputBus(translateToLocal("oth.hint.any_input_bus"), 1)
            .addOutputBus(translateToLocal("oth.hint.any_output_bus"), 1)
            .addInputHatch(translateToLocal("oth.hint.any_input_hatch"), 1)
            .addOutputHatch(translateToLocal("oth.hint.any_output_hatch"), 1)
            .addEnergyHatch(translateToLocal("oth.hint.any_energy_hatch"), 2)
            .addMufflerHatch(translateToLocal("oth.hint.any_muffler_hatch"), 1)
            .addInfo("§b§l" + translateToLocal("oth.tooltip.author") + "§l憋憋大师--HuimaoX")
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - §c§lDragonGrave")
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tileEntity) {
        return new OTEGraveDragon(mName);
    }

    @Override
    public String[] getInfoData() {
        ArrayList<String> str = new ArrayList<>(Arrays.asList(super.getInfoData()));
        return str.toArray(new String[0]);
    }

    // ===================== Textures =====================

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

    // ===================== Misc =====================

    public static int getMachanicalblocksDate(Block block, int meta) {
        if (block == defcCasingBlock) {
            return meta - 7;
        }
        return 0;
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setString("SpeedBonus", String.valueOf(getSpeedBonus()));
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            EnumChatFormatting.BOLD + translateToLocal("otht.waila.bonus.speed")
                + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("SpeedBonus")
                + EnumChatFormatting.GOLD
                + EnumChatFormatting.RESET);
    }
}
