package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static com.newmaa.othtech.common.recipemap.Recipemaps.MegaCAL;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.GregTechAPI.sBlockCasings9;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.SoundResource.IC2_MACHINES_MAGNETIZER_LOOP;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import bartworks.API.BorosilicateGlass;
import goodgenerator.loader.Loaders;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings2;
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEMegaCircuitAssLine extends OTHMultiMachineBase<OTEMegaCircuitAssLine> {

    public OTEMegaCircuitAssLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaCircuitAssLine(String aName) {
        super(aName);
    }

    private boolean $123 = false;

    public byte glassTier = 0;

    private static ItemStack Field_Generator_UEV = null;

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        if (Field_Generator_UEV == null) Field_Generator_UEV = ItemList.Field_Generator_UEV.get(1);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 20 == 0 && !$123) {
            ItemStack aGuiStack = this.getControllerSlot();
            if (aGuiStack != null) {
                if (GTUtility.areStacksEqual(aGuiStack, Field_Generator_UEV)) {
                    this.$123 = true;
                }
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("$123", $123);
        aNBT.setByte("glassTier", glassTier);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        $123 = aNBT.getBoolean("123");
        glassTier = aNBT.getByte("glassTier");
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setBoolean("123Processing", $123);
        }
        if (tileEntity != null) {
            tag.setInteger("parallel", getMaxParallelRecipes());
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "123" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("123Processing")
                + EnumChatFormatting.RESET);
        currentTip.add(
            "目前并行" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("parallel")
                + EnumChatFormatting.RESET);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return $123;
    }

    protected int getMaxParallelRecipes() {
        return 128 + GTUtility.getTier(this.getMaxInputVoltage()) * 16;
    }

    protected float getSpeedBonus() {
        return 1f;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return MegaCAL;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        return super.checkProcessing();
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
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        return checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet);

    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
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
    private final int verticalOffSet = 6;
    private final int depthOffSet = 3;
    private static IStructureDefinition<OTEMegaCircuitAssLine> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 输入输出总线, 输入输出仓, 能源仓 : 替换脱氧钢机械方块, 支持TecTech能源仓") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEMegaCircuitAssLine> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaCircuitAssLine>builder()
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
                .addElement('B', ofBlock(Loaders.componentAssemblylineCasing, 7))
                .addElement('C', ofBlock(sBlockCasings2, 0))
                .addElement('E', ofBlock(sBlockCasings2, 5))
                .addElement('F', ofBlock(sBlockCasings2, 9))
                .addElement('H', ofBlock(sBlockCasings8, 2))
                .addElement('I', ofBlock(sBlockCasings8, 3))
                .addElement('J', ofBlock(sBlockCasings9, 1))
                .addElement('K', ofBlock(ModBlocks.blockCasings2Misc, 12))
                .addElement('L', ofBlock(Loaders.preciseUnitCasing, 2))
                .addElement(
                    'D',
                    buildHatchAdder(OTEMegaCircuitAssLine.class).atLeast(OutputBus)
                        .adder(OTEMegaCircuitAssLine::addToMachineList)
                        .dot(2)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement(
                    'G',
                    buildHatchAdder(OTEMegaCircuitAssLine.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, InputHatch, OutputHatch)
                        .adder(OTEMegaCircuitAssLine::addToMachineList)
                        .dot(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by NewMaa
    private final String[][] shapeMain = new String[][] {
        { "                             ", "                             ", "                             ",
            "                             ", "                             ", "                             ",
            "                             ", "                             ", " HHHHHHHHHHHHHHHHHHHHHHHHHHH " },
        { "                             ", "                             ", "                             ",
            "                             ", "                             ", "                             ",
            "                             ", "                             ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "                             ", "                             ", "                             ",
            "                             ", "                             ", "                             ",
            "                             ", "  I                       I  ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "                             ", "                             ", "                             ",
            "                             ", "                             ", "                             ",
            "  I~CCCCCCCCCCCCCCCCCCCCCCI  ", "  IGGGGGGGGGGGGGGGGGGGGGGGI  ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "                             ", "                             ", "  IIIIIIIIIIIIIIIIIIIIIIIII  ",
            "  IAAAAAAAAAAAAAAAAAAAAAAAI  ", "  IAAAAAAAAAAAAAAAAAAAAAAAI  ", "  IAAAAAAAAAAAAAAAAAAAAAAAI  ",
            "  IAAAAAAAAAAAAAAAAAAAAAAAI  ", " IICCCCCCCCCCCCCCCCCCCCCCCII ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "  I                       I  ", "  IJJJJJJJJJJJJJJJJJJJJJJJI  ", "  IJJJJJJJJJJJJJJJJJJJJJJJI  ",
            "  I                       I  ", "  C                       C  ", "  C                       C  ",
            "  CKLLLLLLLLLLLLLLLLLLLLLKC  ", " IIFFFFFFFFFFFFFFFFFFFFFFFII ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "  I                       I  ", "  IJJJJJJJJJJJJJJJJJJJJJJJI  ", "  CJBBBBBBBBBBBBBBBBBBBBBJC  ",
            "  C                       C  ", "  C                       C  ", "  C                       D  ",
            "  CK                     KC  ", " IIFEEEEEEEEEEEEEEEEEEEEEFII ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "  I                       I  ", "  IJJJJJJJJJJJJJJJJJJJJJJJI  ", "  IJJJJJJJJJJJJJJJJJJJJJJJI  ",
            "  I                       I  ", "  C                       C  ", "  C                       C  ",
            "  CKLLLLLLLLLLLLLLLLLLLLLKC  ", " IIFFFFFFFFFFFFFFFFFFFFFFFII ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "                             ", "                             ", "  IIIIIIIIIIIIIIIIIIIIIIIII  ",
            "  IAAAAAAAAAAAAAAAAAAAAAAAI  ", "  IAAAAAAAAAAAAAAAAAAAAAAAI  ", "  IAAAAAAAAAAAAAAAAAAAAAAAI  ",
            "  IAAAAAAAAAAAAAAAAAAAAAAAI  ", " IICCCCCCCCCCCCCCCCCCCCCCCII ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "                             ", "                             ", "                             ",
            "                             ", "                             ", "                             ",
            "  ICCCCCCCCCCCCCCCCCCCCCCCI  ", "  ICCCCCCCCCCCCCCCCCCCCCCCI  ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "                             ", "                             ", "                             ",
            "                             ", "                             ", "                             ",
            "                             ", "  I                       I  ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "                             ", "                             ", "                             ",
            "                             ", "                             ", "                             ",
            "                             ", "                             ", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHH" },
        { "                             ", "                             ", "                             ",
            "                             ", "                             ", "                             ",
            "                             ", "                             ", " HHHHHHHHHHHHHHHHHHHHHHHHHHH " } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§d§l电路组装的最终奥义 - 进阶高能电路装配线")
            .addInfo("§q便宜好用的电路装配设施, 继承了原有的电装打包机制, 但是直接支持总成")
            .addInfo("§q§o“是不是有点太超模了?”")
            .addInfo("§b玻璃等级决定他的外观, 不成型请重放主机")
            .addInfo("默认为128并行. 电压等级每提高一级, 并行 + 16")
            .addInfo("主机放入UEV力场以解锁无损超频")
            .addTecTechHatchInfo()
            .addSeparator()
            .addController("电装")
            .beginStructureBlock(13, 9, 29, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§d123Technology - CircuitAssemblerLine");
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEMegaCircuitAssLine(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
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
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)) };
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 20) {
            GTUtility.doSoundAtClient(IC2_MACHINES_MAGNETIZER_LOOP, 10, 1.0F, aX, aY, aZ);
        }
    }
}
