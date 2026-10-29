package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.machineclass.FishtorioLootTable;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.enums.VoidingMode;
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

    /** 一次全物品池抽奖需要的恩情数量 */
    private static final int ENQING_PER_FULL_POOL = 1000;

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

    /** 捕鱼: 主机槽的维度方块决定抽奖池, 输入 1000 恩情可以改抽全物品池 */
    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        boolean fullPool = countEnqing() >= ENQING_PER_FULL_POOL;
        List<ItemStack> pool;
        int parallels;

        if (fullPool) {
            pool = FishtorioLootTable.allItems();
            // 并行 = 电压等级 ^ 1.4, 每并行各抽一次; 恩情按次算, 一次固定只扣 1000
            parallels = getFullPoolParallels();
        } else {
            ItemStack dimensionBlock = findDimensionBlock();
            if (dimensionBlock == null) return SimpleCheckRecipeResult.ofFailure("ft_no_dim");
            int tier = FishtorioLootTable.dimensionTier(ItemDimensionDisplay.getDimension(dimensionBlock));
            if (tier < 0) return SimpleCheckRecipeResult.ofFailure("ft_no_dim");
            pool = FishtorioLootTable.orePool(tier);
            // 采矿(维度矿粉池)并行 = 电压等级 ^ 3
            parallels = getMiningParallels();
        }

        if (pool == null || pool.isEmpty()) return SimpleCheckRecipeResult.ofFailure("ft_no_pool");
        if (fullPool && !consumeEnqing(ENQING_PER_FULL_POOL)) {
            return SimpleCheckRecipeResult.ofFailure("ft_no_enqing");
        }

        Random rand = getBaseMetaTileEntity().getWorld().rand;
        // 同种物品合并后再按最大堆叠拆分, 几千次抽取也不会撑出几千个输出槽位
        Map<GTUtility.ItemId, ItemStack> types = new LinkedHashMap<>();
        Map<GTUtility.ItemId, Integer> amounts = new LinkedHashMap<>();
        for (int i = 0; i < parallels; i++) {
            ItemStack draw = pool.get(rand.nextInt(pool.size()));
            GTUtility.ItemId id = GTUtility.ItemId.createNoCopy(draw);
            types.putIfAbsent(id, draw);
            amounts.merge(id, Math.min(1 + rand.nextInt(4), Math.max(1, draw.getMaxStackSize())), Integer::sum);
        }

        List<ItemStack> outputs = new ArrayList<>(amounts.size());
        for (Map.Entry<GTUtility.ItemId, Integer> entry : amounts.entrySet()) {
            ItemStack sample = types.get(entry.getKey());
            int remaining = entry.getValue();
            int max = Math.max(1, sample.getMaxStackSize());
            while (remaining > 0) {
                int amount = Math.min(max, remaining);
                outputs.add(GTUtility.copyAmount(amount, sample));
                remaining -= amount;
            }
        }

        // MTEExtendedPowerMultiBlockBase 只认 lEUt, 写 mEUt 不会真的扣电
        lEUt = -GTValues.V[getVoltageTier()] * parallels;
        // 耗时固定, 并行只影响产出与耗电
        mMaxProgresstime = 100;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mOutputItems = outputs.toArray(new ItemStack[0]);
        mOutputFluids = null;
        updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    /** 能源仓电压等级(1 = LV), 没插仓时按 LV 算 */
    private int getVoltageTier() {
        return Math.min(15, Math.max(1, GTUtility.getTier(getMaxInputVoltage())));
    }

    /** 全物品池并行 = 电压等级 ^ 1.4, 向下取整 */
    private int getFullPoolParallels() {
        return Math.max(1, (int) Math.floor(Math.pow(getVoltageTier(), 1.4D)));
    }

    /** 采矿(维度矿粉池)并行 = 电压等级 ^ 3 */
    private int getMiningParallels() {
        int tier = getVoltageTier();
        return Math.max(1, tier * tier * tier);
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
                        .atLeast(Energy.or(ExoticEnergy), MultiAmpEnergy, InputBus, OutputBus, InputHatch, OutputHatch)
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
            || addMultiAmpEnergyInputToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    /** 强制溢出销毁: 只允许 VOID_ALL, 玩家没法切回保护模式 */
    @Override
    public Set<VoidingMode> getAllowedVoidingModes() {
        return EnumSet.of(VoidingMode.VOID_ALL);
    }

    @Override
    public VoidingMode getDefaultVoidingMode() {
        return VoidingMode.VOID_ALL;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(EnumChatFormatting.DARK_GREEN + translateToLocal("ote.tm.ft.0"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.ft.1"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.ft.2"))
            .addInfo(EnumChatFormatting.ITALIC + translateToLocal("ote.tm.ft.3"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.ft.4"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.ft.9"))
            .addInfo(EnumChatFormatting.WHITE + translateToLocal("ote.tm.ft.7"))
            .addInfo(translateToLocal("ote.tm.ft.8"))
            .addInfo(translateToLocal("ote.tm.ft.10"))
            .addInfo(translateToLocal("ote.tm.ft.6"))
            .addSupportMultiAmp()
            .addSupportLaser()
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
