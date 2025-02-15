package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static com.newmaa.othtech.common.recipemap.Recipemaps.MCA;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import com.newmaa.othtech.utils.Utils;

import bartworks.API.BorosilicateGlass;
import bartworks.API.recipe.BartWorksRecipeMaps;
import bartworks.system.material.CircuitGeneration.BWMetaItems;
import bartworks.system.material.CircuitGeneration.CircuitImprintLoader;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTLanguageManager;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings2;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEMegaCircuitAssLine extends OTH_MultiMachineBase<OTEMegaCircuitAssLine> {

    public OTEMegaCircuitAssLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaCircuitAssLine(String aName) {
        super(aName);
    }

    private boolean $123 = false;

    public byte glassTier = 0;
    private String imprintedItemName;
    private ItemStack imprintedStack;

    private HeatingCoilLevel coilLevel;

    public HeatingCoilLevel getCoilLevel() {
        return this.coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    public int getCoilTier() {
        return Utils.getCoilTier(coilLevel);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 20 == 0 && !$123) {
            ItemStack aGuiStack = this.getControllerSlot();
            if (aGuiStack != null) {
                if (GTUtility.areStacksEqual(aGuiStack, ItemList.Field_Generator_UEV.get(1))) {
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

    public String getTypeForDisplay() {

        if (!isImprinted()) return "";
        return GTLanguageManager.getTranslation(
            GTLanguageManager.getTranslateableItemStackName(CircuitImprintLoader.getStackFromTag(this.type)));
    }

    public boolean isImprinted() {
        return !this.type.hasNoTags();
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
        if (tag.hasKey("ImprintedWith")) currentTip.add(
            StatCollector.translateToLocal("tooltip.cal.imprintedWith") + " "
                + EnumChatFormatting.YELLOW
                + tag.getString("ImprintedWith"));
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
        return MCA;
    }

    private NBTTagCompound type = new NBTTagCompound();

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        if (this.imprintedItemName == null || this.imprintedStack == null) {
            this.imprintedStack = new ItemStack(BWMetaItems.getCircuitParts(), 1, 0);
            this.imprintedStack.setTagCompound(this.type);
            this.imprintedItemName = GTLanguageManager.getTranslateableItemStackName(this.imprintedStack);
        }
        for (ItemStack input : getStoredInputs()) {
            if (input.getItem() instanceof BWMetaItems.BW_GT_MetaGenCircuits) imprintMachine(input);
        }
        return super.checkProcessing();
    }

    private boolean imprintMachine(ItemStack itemStack) {
        if (!GTUtility.isStackValid(itemStack)) return false;
        if (itemStack.getItem() instanceof BWMetaItems.BW_GT_MetaGenCircuits && itemStack.getItemDamage() == 0
            && itemStack.getTagCompound() != null) {
            this.type = itemStack.getTagCompound();
            itemStack.stackSize -= 1;
            if (itemStack == getControllerSlot() && itemStack.stackSize <= 0) {
                mInventory[getControllerSlotIndex()] = null;
            }
            this.getBaseMetaTileEntity()
                .issueBlockUpdate();
            return true;
        }
        return false;
    }

    @Override
    public void onLeftclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (!isImprinted() && getBaseMetaTileEntity().isServerSide()) {
            ItemStack heldItem = aPlayer.getHeldItem();
            if (imprintMachine(heldItem)) {
                if (heldItem.stackSize <= 0) {
                    aPlayer.inventory.setInventorySlotContents(aPlayer.inventory.currentItem, null);
                }
                return;
            }
        }
        super.onLeftclick(aBaseMetaTileEntity, aPlayer);
    }

    private String[] infoDataBuffer;

    @Override
    public String[] getInfoData() {
        if (this.infoDataBuffer != null) return this.infoDataBuffer;

        String[] oldInfo = super.getInfoData();
        this.infoDataBuffer = new String[oldInfo.length + 1];
        System.arraycopy(oldInfo, 0, this.infoDataBuffer, 0, oldInfo.length);
        this.infoDataBuffer[oldInfo.length] = StatCollector.translateToLocal("tooltip.cal.imprintedWith") + " "
            + EnumChatFormatting.YELLOW
            + this.getTypeForDisplay();
        return this.infoDataBuffer;
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
                .addElement('B', ofBlock(sBlockCasings1, 6))
                .addElement('C', ofBlock(sBlockCasings4, 12))
                .addElement('E', ofBlock(sBlockCasings8, 1))
                .addElement('G', ofFrame(Materials.Iridium))
                .addElement(
                    'D',
                    withChannel(
                        "coil",
                        ofCoil(OTEMegaCircuitAssLine::setCoilLevel, OTEMegaCircuitAssLine::getCoilLevel)))
                .addElement(
                    'F',
                    buildHatchAdder(OTEMegaCircuitAssLine.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEMegaCircuitAssLine::addToMachineList)
                        .dot(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by LyeeR
    private final String[][] shapeMain = new String[][] {
        { "FFFFFFF", "G     G", "G     G", "G     G", "G     G", "G     G", "G     G", "G     G", "G     G", "G     G",
            "G     G", "G     G", "FFF~FFF" },
        { "FFFFFFF", " BBBBB ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ",
            " AAAAA ", " BBBBB ", "FFFFFFF" },
        { "FFFFFFF", " BDDDB ", " A G A ", " A E A ", " A E A ", " A G A ", " ADDDA ", " A G A ", " A E A ", " A E A ",
            " A G A ", " BDDDB ", "FFFFFFF" },
        { "FFFFFFF", " BDCDB ", " AGCGA ", " AECEA ", " AECEA ", " AGCGA ", " ADCDA ", " AGCGA ", " AECEA ", " AECEA ",
            " AGCGA ", " BDCDB ", "FFFFFFF" },
        { "FFFFFFF", " BDDDB ", " A G A ", " A E A ", " A E A ", " A G A ", " ADDDA ", " A G A ", " A E A ", " A E A ",
            " A G A ", " BDDDB ", "FFFFFFF" },
        { "FFFFFFF", " BBBBB ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ", " AAAAA ",
            " AAAAA ", " BBBBB ", "FFFFFFF" },
        { "FFFFFFF", "G     G", "G     G", "G     G", "G     G", "G     G", "G     G", "G     G", "G     G", "G     G",
            "G     G", "G     G", "FFFFFFF" } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    protected void setupProcessingLogic(ProcessingLogic logic) {
        super.setupProcessingLogic(logic);
        logic.setSpecialSlotItem(this.imprintedStack);
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
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_CUTTING_MACHINE_LOOP;
    }
}
