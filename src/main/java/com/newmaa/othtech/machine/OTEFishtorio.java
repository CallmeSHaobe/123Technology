package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.machineclass.EIOSpawnerBinding;
import com.newmaa.othtech.machine.machineclass.FishtorioLootTable;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import crazypants.enderio.EnderIO;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.tileentities.machines.MTEHatchInputBusME;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;
import gtneioreplugin.plugin.item.ItemDimensionDisplay;

public class OTEFishtorio extends OTHMultiMachineBase<OTEFishtorio> {
    // TODO 憋憋渔场.

    public OTEFishtorio(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEFishtorio(String aName) {
        super(aName);
    }

    public boolean mode = false;

    /** 一次全物品池抽奖需要的恩情数量 */
    private static final int ENQING_PER_FULL_POOL = 1000;

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("glassTier", mode);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mode = aNBT.getBoolean("glassTier");

    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return 64;
    }

    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return null;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        return mode ? doBindSpawner() : doFishing();
    }

    /** 捕鱼: 主机槽的维度方块决定抽奖池, 输入 1000 恩情可以改抽全物品池 */
    @NotNull
    private CheckRecipeResult doFishing() {
        boolean fullPool = countEnqing() >= ENQING_PER_FULL_POOL;
        int tier = 0;
        List<ItemStack> pool;

        if (fullPool) {
            pool = FishtorioLootTable.allItems();
        } else {
            ItemStack dimensionBlock = findDimensionBlock();
            if (dimensionBlock == null) return SimpleCheckRecipeResult.ofFailure("ft_no_dim");
            tier = FishtorioLootTable.dimensionTier(ItemDimensionDisplay.getDimension(dimensionBlock));
            if (tier < 0) return SimpleCheckRecipeResult.ofFailure("ft_no_dim");
            pool = FishtorioLootTable.poolForTier(tier);
        }

        if (pool == null || pool.isEmpty()) return SimpleCheckRecipeResult.ofFailure("ft_no_pool");
        if (fullPool && !consumeEnqing(ENQING_PER_FULL_POOL)) {
            return SimpleCheckRecipeResult.ofFailure("ft_no_enqing");
        }

        ItemStack output = pool.get(getBaseMetaTileEntity().getWorld().rand.nextInt(pool.size()))
            .copy();
        output.stackSize = Math.min(1 + getBaseMetaTileEntity().getWorld().rand.nextInt(4), output.getMaxStackSize());

        mEUt = (int) -GTValues.V[Math.min(15, Math.max(1, GTUtility.getTier(getMaxInputVoltage())))];
        mMaxProgresstime = 100;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mOutputItems = new ItemStack[] { output };
        mOutputFluids = null;
        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @NotNull
    private CheckRecipeResult doBindSpawner() {
        // GT 里 mEUt 正数是发电, 消耗必须为负
        mEUt = -114;
        mMaxProgresstime = 100;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mOutputFluids = null;

        List<ItemStack> storedInputs = getAllStoredInputs();
        ItemStack soulVessel = EIOSpawnerBinding.findSoulVessel(storedInputs);
        ItemStack brokenSpawner = EIOSpawnerBinding.findBrokenSpawner(storedInputs);

        if (soulVessel == null || brokenSpawner == null) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        String mobType = EnderIO.itemSoulVessel.getMobTypeFromStack(soulVessel);
        if (mobType == null || mobType.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        ItemStack outputItem = EIOSpawnerBinding.createBoundSpawner(brokenSpawner, mobType);

        mOutputItems = new ItemStack[] { outputItem, new ItemStack(EnderIO.itemSoulVessel) };
        soulVessel.stackSize--;
        brokenSpawner.stackSize--;

        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    /** 维度方块放主机槽或输入总线都行, 不会被消耗 */
    private ItemStack findDimensionBlock() {
        ItemStack controllerSlot = getControllerSlot();
        if (controllerSlot != null && controllerSlot.getItem() instanceof ItemDimensionDisplay) return controllerSlot;

        for (MTEHatchInputBus bus : GTUtility.filterValidMTEs(mInputBusses)) {
            if (bus instanceof MTEHatchInputBusME) continue;
            IGregTechTileEntity tile = bus.getBaseMetaTileEntity();
            for (int i = 0; i < tile.getSizeInventory(); i++) {
                ItemStack stack = tile.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemDimensionDisplay) return stack;
            }
        }
        return null;
    }

    private static ItemStack enqing;

    private static ItemStack enqingStack() {
        if (enqing == null) enqing = OTHItemList.itemEnqingM.get(1);
        return enqing;
    }

    private int countEnqing() {
        ItemStack sample = enqingStack();
        int count = 0;
        for (MTEHatchInputBus bus : GTUtility.filterValidMTEs(mInputBusses)) {
            if (bus instanceof MTEHatchInputBusME) continue;
            IGregTechTileEntity tile = bus.getBaseMetaTileEntity();
            for (int i = 0; i < tile.getSizeInventory(); i++) {
                ItemStack stack = tile.getStackInSlot(i);
                if (stack != null && GTUtility.areStacksEqual(stack, sample)) count += stack.stackSize;
            }
        }
        return count;
    }

    /** ME 总线里的东西抹不掉, 所以不计入 */
    private boolean consumeEnqing(int amount) {
        ItemStack sample = enqingStack();
        int left = amount;
        for (MTEHatchInputBus bus : GTUtility.filterValidMTEs(mInputBusses)) {
            if (bus instanceof MTEHatchInputBusME) continue;
            IGregTechTileEntity tile = bus.getBaseMetaTileEntity();
            for (int i = 0; i < tile.getSizeInventory() && left > 0; i++) {
                ItemStack stack = tile.getStackInSlot(i);
                if (stack == null || !GTUtility.areStacksEqual(stack, sample)) continue;
                int used = Math.min(left, stack.stackSize);
                left -= used;
                int remaining = stack.stackSize - used;
                stack.stackSize = remaining;
                if (remaining <= 0) tile.setInventorySlotContents(i, null);
            }
            if (left <= 0) break;
        }
        return left <= 0;
    }

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet, errors)) return;
        repairMachine();
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
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (mode == false) {
            mode = true;
        } else {
            mode = false;
        }
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal(mode ? "ote.tm.ft.mode.on" : "ote.tm.ft.mode.off"));
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 3;
    private final int verticalOffSet = 3;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTEFishtorio> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.ft.0") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEFishtorio> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEFishtorio>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'C',
                    buildHatchAdder(OTEFishtorio.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEFishtorio::addToMachineList)
                        .hint(1)
                        .casingIndex(TAE.GTPP_INDEX(32))
                        .buildAndChain(ofBlock(ModBlocks.blockCasings3Misc, 0)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    /** 7×4×5 的空心壳体, 切片顺序是 [深度][高度][宽度] */
    private final String[][] shapeMain = new String[][] { { "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCC~CCC" },
        { "CCCCCCC", "C     C", "C     C", "CCCCCCC" }, { "CCCCCCC", "C     C", "C     C", "CCCCCCC" },
        { "CCCCCCC", "C     C", "C     C", "CCCCCCC" }, { "CCCCCCC", "CCCCCCC", "CCCCCCC", "CCCCCCC" } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType(EnumChatFormatting.DARK_GREEN + "§l老登的终极造物 - 异星渔场")
     * .addInfo(EnumChatFormatting.ITALIC + "§b赞美伟大的造物主Dr.Jumping! 如果没有她的万物至理公式'主 = 6', 老登们又怎么可能会有机会用到如此机器呢 ?")
     * .addInfo(
     * EnumChatFormatting.ITALIC
     * + "§b具体捕鱼原理 : Jumping恒等式解决了维度方块所蕴含的不确定性问题, 从而使得渔场内部小型维度的建立成为可能, 各种'鱼'将会被渔场内部的墨鱼之神所捕捉.")
     * .addInfo(EnumChatFormatting.ITALIC + "'鱼'包括鱼, 海象, 韭菜盒子, 鲸鱼座藻类, 立方休, 苔石墙 etc. *也许可以放入锑星的维度方块, 但后果自负.")
     * .addInfo(EnumChatFormatting.WHITE + "执行无损超频, 玻璃等级决定能源仓等级")
     * .addInfo(EnumChatFormatting.WHITE + "主机需要放入维度方块")
     * .addInfo("随机产出喔, 可恶的抽卡. 憋憋.")
     * .addTecTechHatchInfo()
     * .addSeparator()
     * .addController("渔场")
     * .beginStructureBlock(7, 4, 5, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .toolTipFinisher("§a123Technology - Fishtorio");
     * return tt;
     * }
     */
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(EnumChatFormatting.DARK_GREEN + translateToLocal("ote.tm.ft.0"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.ft.1"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.ft.2"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.ft.3"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.ft.4"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.ft.5"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.ft.7"))
            .addInfo(translateToLocal("ote.tm.ft.6"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addController(translateToLocal("ote.tn.ft"))
            .beginStructureBlock(7, 4, 5, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - Fishtorio");
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEFishtorio(this.mName);
    }

    private static ITexture CASING_TEXTURE = null;

    private int getCasingTextureIndex() {
        return TAE.GTPP_INDEX(32);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced_Active)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex()) };
    }

}
