package com.newmaa.othtech.machine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.cleanroommc.modularui.utils.item.ItemStackHandler;
import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.alignment.enumerable.ExtendedFacing;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureUtility;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoBlocks;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoCasingMeta;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoIcons;
import com.newmaa.othtech.common.blocks.deepseiko.TEDeepSeikoHolo;
import com.newmaa.othtech.common.item.itemMisc.ItemDeepSeikoToken;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.gui.DeepSeikoGui;
import com.newmaa.othtech.machine.hatch.OTEDeepSeikoNaniteCell;
import com.newmaa.othtech.machine.machineclass.DeepSeikoMath;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;

import gregtech.api.enums.NaniteTier;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.IItemContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchInput;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTStructureUtility;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.ParallelHelper;
import gregtech.common.blocks.ItemMachines;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import tectech.thing.CustomItemList;

@IMetaTileEntity.SkipGenerateDescription
public class OTEDeepSeiko extends OTHMultiMachineBase<OTEDeepSeiko> implements IConstructable, ISurvivalConstructable {

    /** 调试币给出的虚拟算力，仅用于展示与升级解锁判定。 */
    public static final long TOKEN_COMPUTE = 100000000L;

    /**
     * 三个鳍信道（StructureLib channel id，必须小写）。
     * 每个信道 1~10 级：1 = 鳍块本身，2~10 = 该家族发生器的 9 个等级。
     */
    public static final String CH_FIN_TIME = "ds_left_fin"; // 左鳍 → 时间膨胀
    public static final String CH_FIN_SPACE = "ds_right_fin"; // 右鳍 → 压缩时空
    public static final String CH_FIN_STAB = "ds_top_fin"; // 顶鳍 → 稳定力场
    /** 信道等级上限：1 级鳍 + 9 级发生器 */
    public static final int FIN_CHANNEL_MAX = 10;

    /** 三环原始值 = 信道等级（0 = 没装；1 = 只有鳍；2~10 = 发生器 1~9 级） */
    private int timeTier = 0;
    private int spaceTier = 0;
    private int stabTier = 0;
    private OTEDeepSeikoNaniteCell mNaniteCell = null;
    private float mSpeedMult = 1.0F;
    private boolean mBecUnlocked = false;
    private int mNaniteTier = 0;
    private boolean mSpeedUnlocked = false;
    public static final int UPGRADE_STAGE_SLOTS = 24;

    /**
     * 升级暂存格。玩家可以自由拿放，所以必须在内容变化时 {@code markDirty()}：
     * 否则区块不会进入待保存队列，异常退出时这一格的物品会回滚（=复制）或直接丢失。
     */
    private final ItemStackHandler mUpgradeStage = new ItemStackHandler(UPGRADE_STAGE_SLOTS) {

        @Override
        protected void onContentsChanged(int slot) {
            OTEDeepSeiko.this.markDirty();
        }
    };
    public static final int NANITE_PER_TIER = 64;
    /** 纳米蜂群最高等级（与 GT {@code NaniteTier#getTier()} 的 1~10 对齐） */
    public static final int NANITE_TIER_MAX = 10;
    private static final int ULT_CASING_COUNT = 88;
    private static final int ULT_MAGMATTER_COUNT = 1234;
    private static final int ULT_STARGATE_COUNT = 1;
    private static ItemStack sReqBec;
    private static ItemStack sReqNanite;
    private static ItemStack sReqUltCasing;
    private static ItemStack sReqUltMagmatter;
    private static ItemStack sReqUltStargate;
    private static boolean sReqResolved = false;
    private static final Logger LOG = LogManager.getLogger("123Technology/DeepSeiko");
    /** 全息投影相对控制器的正面距离（格）；TEDeepSeikoHolo 的位置自检共用此常量。 */
    public static final int HOLO_FRONT_DISTANCE = 6;
    /** 水平朝向时投影相对控制器 y 下压的格数（对齐嘴部裂缝 ≈ y6） */
    public static final int HOLO_DROP = 4;
    private boolean mHoloPlaced = false;
    private int mHoloX = 0;
    private int mHoloY = 0;
    private int mHoloZ = 0;
    private int mHoloDiag = 0;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final Logger LOG_PREVIEW = LogManager.getLogger("123Technology/DeepSeikoPreview");
    private static World sPreviewBuiltWorld = null;
    private static IStructureDefinition<OTEDeepSeiko> STRUCTURE_DEFINITION = null;
    /** 控制器在结构坐标系里的位置（StructureLib 的 checkPiece/buildPiece 偏移） */
    private static final int HORIZONTAL_OFFSET = 16;
    private static final int VERTICAL_OFFSET = 10;
    private static final int DEPTH_OFFSET = 0;
    private static final IItemContainer[] TIME_GENS;
    private static final IItemContainer[] SPACE_GENS;
    private static final IItemContainer[] STAB_GENS;
    private static final String[] MARKED_HATCHES;
    /**
     * 三环鳍槽位（用户实机点选的位置）。
     * 格式 x,y,z 用 ; 分隔，三组之间用 | 分隔 —— 顺序是 <b>时间 | 空间 | 稳定</b>。
     * 第 0 组 → 'T'（左鳍）、第 1 组 → 'S'（右鳍）、第 2 组 → 'R'（顶鳍），
     * 每组是一条独立的信道，见 {@link #finSlot(String, int, IItemContainer[], java.util.function.BiConsumer,
     * java.util.function.ToIntFunction)}。
     */
    private static final String RING_SLOTS_RAW = "13,6,25;13,7,25;14,6,25;14,7,25;14,7,26;15,6,25;15,6,26;15,7,25;15,7,26;15,7,27;16,6,25;16,6,26;"
        + "16,6,27;16,7,25;16,7,26;16,7,27;17,5,27;17,6,25;17,6,26;17,6,28;17,7,25;17,7,26;17,7,27;17,7,28;"
        + "18,5,27;18,5,28;18,6,25;18,6,26;18,6,28;18,7,25;18,7,26;18,7,27;18,7,28;18,7,29;19,5,27;19,5,28;"
        + "19,5,29;19,6,25;19,6,26;19,6,29;19,7,26;19,7,27;19,7,28;19,7,29;20,5,27;20,5,28;20,5,29;20,6,27;"
        + "20,6,30;20,7,27;20,7,28;20,7,29;21,5,27;21,5,28;21,5,29;21,5,30;21,6,28;21,6,30;21,7,28;21,7,29;"
        + "22,5,28;22,5,29;22,5,30;22,5,31;22,6,29;22,6,30;22,6,31;22,7,29;23,4,31;23,5,29;23,5,30;23,5,31;"
        + "23,6,30;23,6,31;23,6,32;24,4,31;24,5,31;24,5,32;24,6,31;24,6,32;25,4,31;25,5,32;25,6,32;|13,6,7;"
        + "13,7,7;14,6,7;14,7,6;14,7,7;15,6,6;15,6,7;15,7,5;15,7,6;15,7,7;16,6,5;16,6,6;16,6,7;16,7,5;16,7,6;"
        + "16,7,7;17,5,5;17,6,4;17,6,6;17,6,7;17,7,4;17,7,5;17,7,6;17,7,7;18,5,4;18,5,5;18,6,4;18,6,6;18,6,7;"
        + "18,7,3;18,7,4;18,7,5;18,7,6;18,7,7;19,5,3;19,5,4;19,5,5;19,6,3;19,6,6;19,6,7;19,7,3;19,7,4;19,7,5;"
        + "19,7,6;20,5,3;20,5,4;20,5,5;20,6,2;20,6,5;20,7,3;20,7,4;20,7,5;21,5,2;21,5,3;21,5,4;21,5,5;21,6,2;"
        + "21,6,4;21,7,3;21,7,4;22,5,1;22,5,2;22,5,3;22,5,4;22,6,1;22,6,2;22,6,3;22,7,3;23,4,1;23,5,1;23,5,2;"
        + "23,5,3;23,6,0;23,6,1;23,6,2;24,4,1;24,5,0;24,5,1;24,6,0;24,6,1;25,4,1;25,5,0;25,6,0;|20,19,16;"
        + "21,19,16;22,19,16;23,19,16;24,19,16;25,19,16;26,19,16;34,19,15;34,19,16;34,19,17;35,19,15;35,19,17;"
        + "35,20,15;35,20,16;35,20,17;36,19,15;36,19,17;36,20,15;36,20,17;36,21,15;36,21,16;36,21,17;37,18,15;"
        + "37,18,17;37,19,15;37,19,17;37,20,15;37,20,17;37,21,15;37,21,17;37,22,15;37,22,16;37,22,17;38,18,15;"
        + "38,18,17;38,19,15;38,19,17;38,20,15;38,20,17;38,21,15;38,21,17;38,22,15;38,22,16;38,22,17;39,18,15;"
        + "39,18,17;39,19,15;39,19,17;39,20,15;39,20,17;39,21,15;39,21,16;39,21,17;40,17,15;40,17,17;40,18,15;"
        + "40,18,17;40,19,15;40,19,17;40,20,15;40,20,16;40,20,17;41,17,15;41,17,17;41,18,15;41,18,16;41,18,17;"
        + "41,19,15;41,19,16;41,19,17;";

    private static final ITexture CONTROLLER_FRONT_TEXTURE;
    private static final ITexture CONTROLLER_SIDE_TEXTURE;

    public OTEDeepSeiko(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEDeepSeiko(String aName) {
        super(aName);
    }

    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEDeepSeiko(this.mName);
    }

    public void setItemNBT(NBTTagCompound aNBT) {
        super.setItemNBT(aNBT);
        aNBT.setBoolean("dsBecUnlocked", this.mBecUnlocked);
        aNBT.setInteger("dsNaniteTier", this.mNaniteTier);
        aNBT.setBoolean("dsSpeedUnlocked", this.mSpeedUnlocked);
        aNBT.setTag("dsUpgradeStage", this.mUpgradeStage.serializeNBT());
    }

    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("dsBecUnlocked", this.mBecUnlocked);
        aNBT.setInteger("dsNaniteTier", this.mNaniteTier);
        aNBT.setBoolean("dsSpeedUnlocked", this.mSpeedUnlocked);
        aNBT.setTag("dsUpgradeStage", this.mUpgradeStage.serializeNBT());
    }

    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        this.mBecUnlocked = aNBT.getBoolean("dsBecUnlocked");
        this.mNaniteTier = aNBT.getInteger("dsNaniteTier");
        this.mSpeedUnlocked = aNBT.getBoolean("dsSpeedUnlocked");
        if (aNBT.hasKey("dsUpgradeStage")) {
            this.mUpgradeStage.deserializeNBT(aNBT.getCompoundTag("dsUpgradeStage"));
            if (this.mUpgradeStage.getSlots() != UPGRADE_STAGE_SLOTS) {
                this.mUpgradeStage.setSize(UPGRADE_STAGE_SLOTS);
            }
        }

    }

    public int totalMachineMode() {
        return 2;
    }

    public String getMachineModeName(int mode) {
        return StatCollector.translateToLocal(mode == 0 ? "ote.ds.gui.mode.al" : "ote.ds.gui.mode.bec");
    }

    /**
     * 覆盖 GUI 的模式切换入口：合成中（mMaxProgresstime > 0）直接忽略，
     * 螺丝刀和「机器头里的模式按钮」都走这一个方法，所以两边一起被拦住。
     */
    @Override
    public void setMachineMode(int mode) {
        if (this.mMaxProgresstime > 0 && this.mMachine) return; // 合成中（螺丝刀和 GUI 按钮都走这里）
        super.setMachineMode(mode);
    }

    public boolean supportsMachineModeSwitch() {
        return true;
    }

    public void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aItem) {
        if (!this.getBaseMetaTileEntity()
            .isClientSide()) {
            if (this.mMaxProgresstime > 0) {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("ote.ds.chat.modeLocked"));
                return;
            }
            this.setMachineMode(this.nextMachineMode());
            // 切模式必须硬重置：否则上次的 BEC 配方缓存/基准推进残留，零点几秒就"恢复正常速度"
            this.mMaxProgresstime = 0;
            this.mProgresstime = 0;
            this.mBecBaseRate = 0.0F;
            this.mBecRateAccum = 0.0D;
            this.getBaseMetaTileEntity()
                .setActive(false);
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal(this.machineMode == 0 ? "ote.ds.mode.al" : "ote.ds.mode.bec"));
        }
    }

    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    protected float getSpeedBonus() {
        return 1.0F;
    }

    public int getMaxParallelRecipes() {
        return this.getMaxParallel();
    }

    public RecipeMap<?> getRecipeMap() {
        return this.machineMode == 0 ? Recipemaps.DeepSeikoAL : Recipemaps.DeepSeikoBEC;
    }

    /**
     * 并行上限：跟着结构方块走 —— 三环（时间/空间/稳定）已装方块等级取平均，
     * 1 级 512，每级 ×8，9 级夹到 Integer.MAX_VALUE（=无限并行）。
     */
    public int getParallelLevel() {
        // 与 GUI / 三环倍率同口径：调试币插入时视为三环 9 级（否则会出现"GUI 显示 T9、并行却只有 512"）
        int t = this.ringInstalledLimit(this.timeTier);
        int sp = this.ringInstalledLimit(this.spaceTier);
        int st = this.ringInstalledLimit(this.stabTier);
        return Math.max(1, Math.min(9, (t + sp + st) / 3));
    }

    public int getMaxParallel() {
        return DeepSeikoMath.maxParallel(this.getParallelLevel());
    }

    /**
     * 算力卡（控制器槽里那一叠）对 BEC 点数系数的贡献：`1 + ³√(张数) × 卡系数`。
     * <p>
     * 卡系数：量子电路 0.05 · 普朗克尺度电路 0.10 · 690 战术核显卡 0.0123 ·
     * D99/D199/D999 算力中心 0.08/0.16/0.25。不是算力卡返回 0。
     * </p>
     */
    public static float computeCardCoefficient(net.minecraft.item.ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0.0F;
        // 金色调试币：万能算力卡（BEC 的「必须插一张卡」也认它；倍率另由 getComputeCardMultiplier 拉满）
        if (stack.getItem() == com.newmaa.othtech.common.item.ItemLoader.itemDeepSeikoToken) return 1.0F;
        if (stack.getItem() == com.newmaa.othtech.common.item.ItemLoader.itemComputeCenter) {
            switch (stack.getItemDamage()) {
                case 0:
                    return 0.08F;
                case 1:
                    return 0.16F;
                case 2:
                    return 0.25F;
                default:
                    return 0.0F;
            }
        }
        if (stack.getItem() == com.newmaa.othtech.common.item.ItemLoader.itemNukeThrowable) return 0.0123F; // 690 战术核显卡
        // 银色调试币：等价于「只插了一张 690 战术核显卡」的最低配测试卡
        if (stack.getItem() == com.newmaa.othtech.common.item.ItemLoader.itemDeepSeikoTokenSilver) return 0.0123F;

        // NH 核心模组的三档电路：量子 / 皮米 / 普朗克
        // 注意：这几样不是 GT 的 metaitem，按注册名匹配（之前量子只比 Item，导致完全认不出来）
        if (isDreamcraftItem(stack, "QuantumCircuit")) return 0.05F;
        if (isDreamcraftItem(stack, "PikoCircuit")) return 0.025F;
        if (isDreamcraftItem(stack, "PlanckCircuit")) return 0.10F;

        // GT 自带量子电路（item + damage 一起比；只比 item 会把所有 metaitem.01 都当成卡）
        if (sameStackAs(stack, gregtech.api.enums.ItemList.Circuit_Quantumprocessor)
            || sameStackAs(stack, gregtech.api.enums.ItemList.Circuit_Quantumcomputer)
            || sameStackAs(stack, gregtech.api.enums.ItemList.Circuit_Quantummainframe)) return 0.05F;
        return 0.0F;
    }

    /** 是否是 dreamcraft:<name> 这个物品（按注册名判断，兼容 meta） */
    private static boolean isDreamcraftItem(net.minecraft.item.ItemStack stack, String name) {
        cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier id = cpw.mods.fml.common.registry.GameRegistry
            .findUniqueIdentifierFor(stack.getItem());
        return id != null && "dreamcraft".equals(id.modId) && name.equals(id.name);
    }

    /** 与 ItemList 里的物品完全一致（item + damage 都比） */
    private static boolean sameStackAs(net.minecraft.item.ItemStack stack, gregtech.api.enums.ItemList ref) {
        if (ref == null || !ref.hasBeenSet()) return false;
        net.minecraft.item.ItemStack expected = ref.get(1);
        return expected != null && stack.getItem() == expected.getItem()
            && stack.getItemDamage() == expected.getItemDamage();
    }

    /** 控制器槽里那张卡的系数（0 = 没插卡） */
    public float getComputeCardCoefficientSafe() {
        return computeCardCoefficient(getControllerSlot());
    }

    /** 停机原因：凝聚态模式缺算力卡 */
    public static gregtech.api.util.shutdown.ShutDownReason computeCardShutDown() {
        return gregtech.api.util.shutdown.ShutDownReasonRegistry
            .outOfStuff(StatCollector.translateToLocal("ote.ds.lang.computeCard"), 1);
    }

    /** 有效基准推进：没记录过就用当前推进兜底（避免切模式后残留 0 导致点数显示成 tick 数） */
    public float effectiveBecBaseRate() {
        return this.mBecBaseRate > 0.0F ? this.mBecBaseRate : Math.max(1.0F, getBecPointsPerTick());
    }

    /** 本次配方已完成的点数（GUI 显示用）。返回值必须用 long：终极 + 调试币下单次配方可达 1e11 点。 */
    public long getProgressPointScaled() {
        if (this.mMaxProgresstime <= 0) return 0L;
        // 点数 = 进度 tick × 每 tick 推进（不要再除 20，之前多除了，导致"点数/速度"对不上时长）
        return (long) this.mProgresstime * (long) effectiveBecBaseRate();
    }

    /** 本次配方总点数（GUI 显示用） */
    public long getProgressPointTotal() {
        if (this.mMaxProgresstime <= 0) return 0L;
        return (long) this.mMaxProgresstime * (long) effectiveBecBaseRate();
    }

    /** BEC 动态点数：配方开始时的基准推进（用于每 tick 折算实际速度） */
    private float mBecBaseRate = 0.0F;
    private double mBecRateAccum = 0.0D;

    /** 当前实际推进（点/tick）；装配线返回 0 */
    public float getBecCurrentRate() {
        if (this.machineMode == 0) return 0.0F;
        return getBecPointsPerTick();
    }

    /** BEC 当前推进（点/tick）；装配线模式返回 0 */
    public float getBecPointsPerTick() {
        if (this.machineMode == 0) return 0.0F;
        return DeepSeikoMath
            .becPointsPerTick(isSpeedUnlocked(), getTimeLevel(), getStabLevel(), getComputeCardMultiplier())
            * getThroughputFactor();
    }

    /** 控制器槽里那叠算力卡给出的点数系数（≥1.0；不是卡就是 1.0） */
    public float getComputeCardMultiplier() {
        // 调试币：满算力（等同于算力封顶），BEC 的推进直接按 10000× 算
        if (this.isTokenInserted()) return DeepSeikoMath.COMPUTE_MULT_CAP;
        net.minecraft.item.ItemStack card = getControllerSlot();
        float coef = computeCardCoefficient(card);
        if (coef <= 0.0F) return 1.0F;
        return 1.0F + (float) Math.cbrt(Math.max(1, card.stackSize)) * coef;
    }

    protected ProcessingLogic createProcessingLogic() {
        return (new OTHProcessingLogic() {

            @NotNull
            public CheckRecipeResult process() {
                if (OTEDeepSeiko.this.machineMode == 1) {
                    // 凝聚态：点数模型自己算时长，关掉超频
                    this.setOverclock(1.0D, 1.0D);
                } else {
                    // 装配线：无损超频（每级时长 /2，耗电 ×1）
                    this.setOverclock(2.0D, 1.0D);
                }
                this.setEuModifier(
                    (double) Math
                        .max(0.05F, OTEDeepSeiko.this.getRingEuFactor() * OTEDeepSeiko.this.getRingStabEuFactor()));
                CheckRecipeResult result = super.process();
                OTEDeepSeiko.this.mRunningOnLoad = false;
                return result;
            }

            /** BEC 点数模型的配方引用（validateRecipe 里记下） */
            private GTRecipe becRecipe;

            @Override
            public int getDuration() {
                if (OTEDeepSeiko.this.machineMode == 1 && this.becRecipe != null) {
                    // 真实并行交给 GT（扣料/产出按 N），时长侧在 DeepSeikoMath 内按 BEC_PARALLEL_TIME_CAP 封顶
                    int parallel = Math.max(1, this.getCurrentParallels());
                    int ticks = DeepSeikoMath.becDurationTicks(
                        this.becRecipe.mDuration,
                        parallel,
                        OTEDeepSeiko.this.isSpeedUnlocked(),
                        OTEDeepSeiko.this.getTimeLevel(),
                        OTEDeepSeiko.this.getStabLevel(),
                        OTEDeepSeiko.this.getComputeCardMultiplier());
                    // 升级进度 nerf：吞吐 50%~75% ⇒ 时长按 1/factor 拉长（下限仍 20 tick）
                    return Math.max(20, Math.round(ticks / OTEDeepSeiko.this.getThroughputFactor()));
                }
                return super.getDuration();
            }

            @NotNull
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (OTEDeepSeiko.this.machineMode == 1) {
                    if (!OTEDeepSeiko.this.isBecUnlocked()) {
                        return SimpleCheckRecipeResult.ofFailure("ds_need_adapter");
                    }
                    if (recipe.mSpecialValue > OTEDeepSeiko.this.getNaniteTier()) {
                        return SimpleCheckRecipeResult.ofFailure("ds_nanite_low");
                    }
                    if (OTEDeepSeiko.this.getComputeCardCoefficientSafe() <= 0.0F) {
                        return SimpleCheckRecipeResult.ofFailure("ds_need_card");
                    }
                    // 凝聚态点数模型：配方 duration 存的是点数，真实时长在 getDuration() 里按并行/三环折算
                    this.becRecipe = recipe;
                    OTEDeepSeiko.this.mBecBaseRate = Math.max(1.0F, OTEDeepSeiko.this.getBecPointsPerTick());
                    OTEDeepSeiko.this.mBecRateAccum = 0.0D;
                    OTEDeepSeiko.this.mSpeedMult = 1.0F;
                    return CheckRecipeResultRegistry.SUCCESSFUL;
                }

                // 装配线模式：不需要算力卡，加速只由三种结构方块（时间 + 稳定）决定
                float alSpeed = DeepSeikoMath
                    .alSpeed(OTEDeepSeiko.this.getTimeLevel(), OTEDeepSeiko.this.getStabLevel())
                    * OTEDeepSeiko.this.getThroughputFactor();
                OTEDeepSeiko.this.mSpeedMult = alSpeed;
                this.setSpeedBonus((double) DeepSeikoMath.durationModifier(alSpeed));
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            @NotNull
            protected ParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(recipe).setConsumption(!OTEDeepSeiko.this.mRunningOnLoad);
            }
        }).setMaxParallelSupplier(this::getTrueParallel);
    }

    /**
     * 凝聚态点数模型的**动态**推进：每 tick 按当前实际推进 / 起始推进 的比值额外补/扣进度。
     * <p>
     * 这样中途拔算力卡/换三环会立即反映到进度上（而不是开配方时定死）。
     * </p>
     */
    @Override
    public boolean onRunningTick(net.minecraft.item.ItemStack aStack) {
        boolean ok = super.onRunningTick(aStack);
        // 凝聚态：算力卡是开工前提，中途拔掉 = 强制终止本次工作并关机（加卡则正常加速）
        if (this.machineMode == 1 && this.mMaxProgresstime > 0 && getComputeCardCoefficientSafe() <= 0.0F) {
            this.mMaxProgresstime = 0;
            this.mProgresstime = 0;
            this.mEfficiency = 0;
            this.stopMachine(computeCardShutDown());
            return ok;
        }
        if (this.machineMode == 1 && this.mMaxProgresstime > 0 && this.mBecBaseRate <= 0.0F) {
            this.mBecBaseRate = Math.max(1.0F, getBecPointsPerTick());
        }
        if (this.machineMode == 1 && this.mMaxProgresstime > 0 && this.mBecBaseRate > 0.0F && this.mProgresstime > 0) {
            float ratio = getBecCurrentRate() / this.mBecBaseRate;
            ratio = Math.max(0.02F, Math.min(20.0F, ratio));
            this.mBecRateAccum += (ratio - 1.0D);
            int extra = (int) Math.floor(this.mBecRateAccum);
            if (extra != 0) {
                int next = this.mProgresstime + extra;
                if (next >= 0) {
                    this.mProgresstime = Math.min(this.mMaxProgresstime, next);
                    this.mBecRateAccum -= extra;
                }
            }
        }
        return ok;
    }

    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPreTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && (aTick & 15L) == 0L) {
            this.tickRiceHolo();
        }
    }

    /** 信道等级（0 没装 / 1 只有鳍 / 2~10 发生器 1~9 级）→ 三环实际等级（1~9，1 = 无加成） */
    private static int installedLevel(int channelLevel) {
        return Math.max(1, Math.min(DeepSeikoMath.RING_LEVEL_MAX, channelLevel - 1));
    }

    private int ringInstalledLimit(int channelLevel) {
        return this.isTokenInserted() ? DeepSeikoMath.RING_LEVEL_MAX : installedLevel(channelLevel);
    }

    public int getTimeLevel() {
        return installedLevel(this.timeTier);
    }

    public int getSpaceLevel() {
        return installedLevel(this.spaceTier);
    }

    public int getStabLevel() {
        return installedLevel(this.stabTier);
    }

    /** 模式：0 = 装配线，1 = 凝聚态 */
    public int getModeId() {
        return this.machineMode;
    }

    /** 稳定方块：速度 ×1.00~1.20 */
    public float getRingStabSpeedFactor() {
        return DeepSeikoMath.ringStabSpeedFactor(getStabLevel());
    }

    /** 稳定方块：耗电 ×1.00~0.80 */
    public float getRingStabEuFactor() {
        return DeepSeikoMath.ringStabEuFactor(getStabLevel());
    }

    public float getRingSpeedFactor() {
        return DeepSeikoMath.ringSpeedFactor(this.getTimeLevel());
    }

    public float getRingEuFactor() {
        return DeepSeikoMath.ringEuFactor(this.getSpaceLevel());
    }

    /** 实际耗电乘区（右鳍 × 顶鳍），给左侧面板显示：×0.50 就是半价 */
    public float getEuFactor() {
        return Math.max(0.05F, getRingEuFactor() * getRingStabEuFactor());
    }

    public float getRingWearFactor() {
        return DeepSeikoMath.ringWearFactor(this.getStabLevel());
    }

    public int getTimeLimit() {
        return this.ringInstalledLimit(this.timeTier);
    }

    public int getSpaceLimit() {
        return this.ringInstalledLimit(this.spaceTier);
    }

    public int getStabLimit() {
        return this.ringInstalledLimit(this.stabTier);
    }

    public boolean isTokenInserted() {
        ItemStack stack = this.getControllerSlot();
        return stack != null && stack.getItem() instanceof ItemDeepSeikoToken;
    }

    private static ItemStack lookup(String modid, String name, int meta, int size) {
        Item item = null;
        Object obj = Item.itemRegistry.getObject(modid + ":" + name);
        if (obj instanceof Item) {
            Item found = (Item) obj;
            item = found;
        } else {
            Object blockObj = Block.blockRegistry.getObject(modid + ":" + name);
            if (blockObj instanceof Block) {
                Block block = (Block) blockObj;
                item = Item.getItemFromBlock(block);
            }
        }

        return item == null ? null : new ItemStack(item, size, meta);
    }

    private static void resolveRequirements() {
        if (sReqResolved) return;
        sReqResolved = true;
        sReqBec = new ItemStack(DeepSeikoBlocks.dsCasing, 4, 6);
        sReqNanite = null;
        // GT 的 NaniteTier#getStack() 在对应矿辞缺失时会 NPE 并把 null 永久缓存：
        // 裁剪包（缺纳米蜂）打开升级面板不能整包崩，这里逐档兜住。
        for (NaniteTier tier : NaniteTier.values()) {
            ItemStack ref;
            try {
                ref = tier.getStack();
            } catch (Throwable ignored) {
                continue;
            }
            if (!GTUtility.isStackInvalid(ref)) {
                sReqNanite = GTUtility.copyAmount(64, ref);
                break;
            }
        }

        sReqUltCasing = lookup("GoodGenerator", "componentAssemblylineCasing", 12, 88);
        sReqUltMagmatter = lookup("gregtech", "gt.metaitem.03", 4143, 1234);
        sReqUltStargate = lookup("dreamcraft", "StargateShieldingFoil", 0, 1);
        LOG.info(
            "[DeepSeiko] 升级需求解析: bec=" + sReqBec
                + " nanite="
                + sReqNanite
                + " ultCasing="
                + sReqUltCasing
                + " ultMagmatter="
                + sReqUltMagmatter
                + " ultStargate="
                + sReqUltStargate);
    }

    private int countInStage(ItemStack want) {
        if (want == null) {
            return 0;
        } else {
            int total = 0;

            for (int i = 0; i < this.mUpgradeStage.getSlots(); ++i) {
                ItemStack stack = this.mUpgradeStage.getStackInSlot(i);
                if (stack != null && stack.getItem() == want.getItem()
                    && stack.getItemDamage() == want.getItemDamage()) {
                    total += stack.stackSize;
                }
            }

            return total;
        }
    }

    private int countInStageTier(int tier) {
        int total = 0;

        for (int i = 0; i < this.mUpgradeStage.getSlots(); ++i) {
            ItemStack stack = this.mUpgradeStage.getStackInSlot(i);
            if (stack != null && naniteTierOf(stack) == tier) {
                total += stack.stackSize;
            }
        }

        return total;
    }

    public static int naniteTierOf(ItemStack stack) {
        if (GTUtility.isStackInvalid(stack)) return 0;
        for (NaniteTier tier : NaniteTier.values()) {
            ItemStack ref;
            try {
                ref = tier.getStack();
            } catch (Throwable ignored) {
                continue;
            }
            if (!GTUtility.isStackInvalid(ref) && GTUtility.areStacksEqual(ref, stack, true)) {
                return tier.getTier();
            }
        }
        return 0;
    }

    public static ItemStack naniteRefForTier(int tier) {
        for (NaniteTier t : NaniteTier.values()) {
            if (t.getTier() != tier) continue;
            ItemStack ref;
            try {
                ref = t.getStack();
            } catch (Throwable ignored) {
                return null;
            }
            if (!GTUtility.isStackInvalid(ref)) {
                return GTUtility.copyAmount(1, ref);
            }
        }
        return null;
    }

    public static int getUltCasingCount() {
        return 88;
    }

    public static int getUltMagmatterCount() {
        return 1234;
    }

    public static int getUltStargateCount() {
        return 1;
    }

    public static ItemStack[] getNaniteRefsByTier() {
        ItemStack[] refs = new ItemStack[10];

        for (int tier = 1; tier <= 10; ++tier) {
            refs[tier - 1] = naniteRefForTier(tier);
        }

        return refs;
    }

    public int fillUpgradeStage(EntityPlayer player) {
        if (player != null && player.inventory != null) {
            resolveRequirements();
            int moved = 0;
            if (!this.isBecUnlocked()) {
                moved += this.pullIntoStage(player, sReqBec, 4);
            }

            if (!this.isTokenInserted() && this.mNaniteTier < 10) {
                moved += this.pullIntoStageTier(player, this.mNaniteTier + 1, 64);
            }

            if (!this.isSpeedUnlocked()) {
                moved += this.pullIntoStage(player, sReqUltCasing, 88);
                moved += this.pullIntoStage(player, sReqUltMagmatter, 1234);
                moved += this.pullIntoStage(player, sReqUltStargate, 1);
            }

            if (moved > 0) {
                this.markDirty();
            }

            return moved;
        } else {
            return 0;
        }
    }

    private int pullIntoStage(EntityPlayer player, ItemStack want, int target) {
        if (want == null) {
            return 0;
        } else {
            int need = target - this.countInStage(want);
            if (need <= 0) {
                return 0;
            } else {
                int moved = 0;

                for (int i = 0; i < player.inventory.getSizeInventory() && moved < need; ++i) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (stack != null && stack.getItem() == want.getItem()
                        && stack.getItemDamage() == want.getItemDamage()) {
                        int take = Math.min(need - moved, stack.stackSize);
                        int accepted = this.addToStage(stack, take);
                        if (accepted <= 0) {
                            break;
                        }

                        player.inventory.decrStackSize(i, accepted);
                        moved += accepted;
                    }
                }

                return moved;
            }
        }
    }

    private int pullIntoStageTier(EntityPlayer player, int tier, int target) {
        int need = target - this.countInStageTier(tier);
        if (need <= 0) {
            return 0;
        } else {
            int moved = 0;

            for (int i = 0; i < player.inventory.getSizeInventory() && moved < need; ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack != null && naniteTierOf(stack) == tier) {
                    int take = Math.min(need - moved, stack.stackSize);
                    int accepted = this.addToStage(stack, take);
                    if (accepted <= 0) {
                        break;
                    }

                    player.inventory.decrStackSize(i, accepted);
                    moved += accepted;
                }
            }

            return moved;
        }
    }

    private int addToStage(ItemStack source, int amount) {
        int left = amount;

        int i;
        for (i = 0; i < this.mUpgradeStage.getSlots() && left > 0; ++i) {
            ItemStack cur = this.mUpgradeStage.getStackInSlot(i);
            if (cur != null && cur.getItem() == source.getItem() && cur.getItemDamage() == source.getItemDamage()) {
                int room = 64 - cur.stackSize;
                if (room > 0) {
                    int add = Math.min(room, left);
                    ItemStack copy = cur.copy();
                    copy.stackSize += add;
                    this.mUpgradeStage.setStackInSlot(i, copy);
                    left -= add;
                }
            }
        }

        for (i = 0; i < this.mUpgradeStage.getSlots() && left > 0; ++i) {
            if (this.mUpgradeStage.getStackInSlot(i) == null) {
                int add = Math.min(64, left);
                ItemStack copy = source.copy();
                copy.stackSize = add;
                this.mUpgradeStage.setStackInSlot(i, copy);
                left -= add;
            }
        }

        return amount - left;
    }

    private void takeFromStage(ItemStack want, int amount) {
        if (want != null) {
            int left = amount;

            for (int i = 0; i < this.mUpgradeStage.getSlots() && left > 0; ++i) {
                ItemStack stack = this.mUpgradeStage.getStackInSlot(i);
                if (stack != null && stack.getItem() == want.getItem()
                    && stack.getItemDamage() == want.getItemDamage()) {
                    int take = Math.min(left, stack.stackSize);
                    ItemStack leftOver = stack.copy();
                    leftOver.stackSize -= take;
                    this.mUpgradeStage.setStackInSlot(i, leftOver.stackSize <= 0 ? null : leftOver);
                    left -= take;
                }
            }

            this.markDirty();
        }
    }

    public ItemStackHandler getUpgradeStage() {
        return this.mUpgradeStage;
    }

    /** 银色调试币：最低配测试（BEC 已解锁、蜂群 T1、算力卡等价一张 690） */
    public boolean isSilverTokenInserted() {
        net.minecraft.item.ItemStack stack = getControllerSlot();
        return stack != null && stack.getItem() == com.newmaa.othtech.common.item.ItemLoader.itemDeepSeikoTokenSilver;
    }

    public boolean isBecUnlocked() {
        return this.mBecUnlocked || this.isTokenInserted();
    }

    /**
     * 凝聚态配方的蜂群等级门槛：升级面板累的等级与收容仓里那颗蜂取较高者。
     * 收容仓作为可插拔的「外部蜂群输入」参与判定，不再只是展示柜。
     */
    public int getNaniteTier() {
        if (isSilverTokenInserted()) return 1;
        if (this.isTokenInserted()) return NANITE_TIER_MAX;
        int cellTier = this.mNaniteCell == null ? 0 : this.mNaniteCell.getPlacedTier();
        return Math.max(this.mNaniteTier, cellTier);
    }

    public boolean isSpeedUnlocked() {
        return this.mSpeedUnlocked || this.isTokenInserted();
    }

    /** 已完成的升级档数（0~3）：BEC 解锁 / 纳米蜂群 10 级 / 终极解锁 */
    public int getUpgradeSteps() {
        int steps = 0;
        if (this.isBecUnlocked()) steps++;
        if (this.getNaniteTier() >= NANITE_TIER_MAX) steps++;
        if (this.isSpeedUnlocked()) steps++;
        return steps;
    }

    /** 升级进度吞吐乘区（0.50 ~ 0.75），BEC 推进与装配线速度都乘它 */
    public float getThroughputFactor() {
        return DeepSeikoMath.upgradeThroughput(this.getUpgradeSteps());
    }

    public boolean tryUpgradeBec() {
        if (this.machineMode == 0) return false; // 装配线模式没有升级，直接拒绝
        resolveRequirements();
        if (this.isBecUnlocked()) {
            return false;
        } else if (this.countInStage(sReqBec) < 4) {
            return false;
        } else {
            this.takeFromStage(sReqBec, 4);
            this.mBecUnlocked = true;
            return true;
        }
    }

    public boolean tryUpgradeNanite() {
        if (this.machineMode == 0) return false; // 装配线模式没有升级，直接拒绝
        resolveRequirements();
        if (!this.isTokenInserted() && this.mNaniteTier < 10) {
            if (!this.isBecUnlocked()) {
                return false;
            } else {
                int wantTier = this.mNaniteTier + 1;
                if (this.countInStageTier(wantTier) < 64) {
                    return false;
                } else {
                    this.takeFromStageNanite(wantTier, 64);
                    ++this.mNaniteTier;
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private void takeFromStageNanite(int tier, int amount) {
        int left = amount;

        for (int i = 0; i < this.mUpgradeStage.getSlots() && left > 0; ++i) {
            ItemStack stack = this.mUpgradeStage.getStackInSlot(i);
            if (stack != null && naniteTierOf(stack) == tier) {
                int take = Math.min(left, stack.stackSize);
                ItemStack leftOver = stack.copy();
                leftOver.stackSize -= take;
                this.mUpgradeStage.setStackInSlot(i, leftOver.stackSize <= 0 ? null : leftOver);
                left -= take;
            }
        }

        this.markDirty();
    }

    public boolean tryUpgradeUltimate() {
        if (this.machineMode == 0) return false; // 装配线模式没有升级，直接拒绝
        resolveRequirements();
        if (this.isSpeedUnlocked()) {
            return false;
        } else if (this.isBecUnlocked() && this.getNaniteTier() >= 10) {
            if (sReqUltCasing != null && sReqUltMagmatter != null && sReqUltStargate != null) {
                if (this.countInStage(sReqUltCasing) < 88) {
                    return false;
                } else if (this.countInStage(sReqUltMagmatter) < 1234) {
                    return false;
                } else if (this.countInStage(sReqUltStargate) < 1) {
                    return false;
                } else {
                    this.takeFromStage(sReqUltCasing, 88);
                    this.takeFromStage(sReqUltMagmatter, 1234);
                    this.takeFromStage(sReqUltStargate, 1);
                    this.mSpeedUnlocked = true;
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static ItemStack[] getUpgradeRequirements() {
        resolveRequirements();
        return new ItemStack[] { sReqBec, sReqNanite, sReqUltCasing, sReqUltMagmatter, sReqUltStargate };
    }

    private void tickRiceHolo() {
        IGregTechTileEntity base = this.getBaseMetaTileEntity();
        if (base != null) {
            World world = base.getWorld();
            if (world != null && !world.isRemote) {
                if (!this.mMachine) {
                    this.diag(
                        "holo idle: structure not formed yet (controller " + base
                            .getXCoord() + "," + base.getYCoord() + "," + base.getZCoord() + ")");
                    this.clearRiceHolo();
                } else {
                    ForgeDirection front = base.getFrontFacing();
                    int holoX = base.getXCoord() + front.offsetX * HOLO_FRONT_DISTANCE;
                    int holoZ = base.getZCoord() + front.offsetZ * HOLO_FRONT_DISTANCE;
                    int baseY = base.getYCoord() + front.offsetY * HOLO_FRONT_DISTANCE;
                    // 高度：水平朝向时把投影压到"嘴部裂缝"那一层（控制器 Y - 6，结构里鲸须带在 y=4~7）；
                    // 垂直朝向（朝上/朝下）保持原来的正面偏移。
                    // 固定压在嘴部高度（不做避让扫描：位置必须唯一，否则旧影块会和新的重复渲染出两碗）
                    int holoY = front.offsetY == 0 ? baseY - HOLO_DROP : baseY;
                    if (!world.blockExists(holoX, holoY, holoZ)) {
                        this.diag("holo skipped: chunk not loaded at " + holoX + "," + holoY + "," + holoZ);
                    } else {
                        int meta = this.mMaxProgresstime > 0 ? 2 : 1;
                        Block current = world.getBlock(holoX, holoY, holoZ);
                        if (current == DeepSeikoBlocks.dsHolo) {
                            if (world.getBlockMetadata(holoX, holoY, holoZ) != meta) {
                                world.setBlockMetadataWithNotify(holoX, holoY, holoZ, meta, 3);
                            }

                            this.rememberHolo(holoX, holoY, holoZ);
                        } else if (!world.isAirBlock(holoX, holoY, holoZ)) {
                            this.diag(
                                "holo skipped: " + holoX
                                    + ","
                                    + holoY
                                    + ","
                                    + holoZ
                                    + " occupied by "
                                    + current
                                    + " front="
                                    + front);
                        } else {
                            world.setBlock(holoX, holoY, holoZ, DeepSeikoBlocks.dsHolo, meta, 3);
                            TileEntity tile = world.getTileEntity(holoX, holoY, holoZ);
                            if (tile instanceof TEDeepSeikoHolo) {
                                TEDeepSeikoHolo holo = (TEDeepSeikoHolo) tile;
                                holo.setOwner(base.getXCoord(), base.getYCoord(), base.getZCoord());
                                this.rememberHolo(holoX, holoY, holoZ);
                                this.diag(
                                    "holo placed at " + holoX
                                        + ","
                                        + holoY
                                        + ","
                                        + holoZ
                                        + " front="
                                        + front
                                        + " meta="
                                        + meta);
                            } else {
                                this.diag(
                                    "holo placed but TE missing at " + holoX
                                        + ","
                                        + holoY
                                        + ","
                                        + holoZ
                                        + " -> "
                                        + tile);
                            }

                        }
                    }
                }
            }
        }
    }

    private void diag(String message) {
        if (this.mHoloDiag < 3) {
            ++this.mHoloDiag;
            LOG.info(message);
        }
    }

    private void rememberHolo(int x, int y, int z) {
        this.mHoloPlaced = true;
        this.mHoloX = x;
        this.mHoloY = y;
        this.mHoloZ = z;
    }

    private void clearRiceHolo() {
        if (this.mHoloPlaced) {
            this.mHoloPlaced = false;
            IGregTechTileEntity base = this.getBaseMetaTileEntity();
            if (base != null) {
                World world = base.getWorld();
                if (world != null && !world.isRemote) {
                    if (world.getBlock(this.mHoloX, this.mHoloY, this.mHoloZ) == DeepSeikoBlocks.dsHolo) {
                        world.setBlockToAir(this.mHoloX, this.mHoloY, this.mHoloZ);
                    }

                }
            }
        }
    }

    public void onRemoval() {
        super.onRemoval();
        this.clearRiceHolo();
    }

    public boolean isStructureFormed() {
        return this.mMachine;
    }

    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        this.timeTier = 0;
        this.spaceTier = 0;
        this.stabTier = 0;
        this.mNaniteCell = null;
        if (this.checkPiece(STRUCTURE_PIECE_MAIN, HORIZONTAL_OFFSET, VERTICAL_OFFSET, DEPTH_OFFSET, errors)) {
            this.repairMachine();
        }
    }

    public ExtendedFacing getExtendedFacing() {
        ExtendedFacing facing = super.getExtendedFacing();
        if (facing != null) return facing;
        IGregTechTileEntity base = this.getBaseMetaTileEntity();
        return base == null ? ExtendedFacing.DEFAULT : ExtendedFacing.of(base.getFrontFacing());
    }

    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.repairMachine();
        long t0 = System.nanoTime();
        try {
            this.buildPiece(
                STRUCTURE_PIECE_MAIN,
                stackSize,
                hintsOnly,
                HORIZONTAL_OFFSET,
                VERTICAL_OFFSET,
                DEPTH_OFFSET);
        } catch (Throwable t) {
            LOG_PREVIEW.error("构造（construct）时抛出异常 hintsOnly=" + hintsOnly, t);
            throw t;
        } finally {
            LOG_PREVIEW
                .info("construct(hintsOnly=" + hintsOnly + ") 耗时 " + (System.nanoTime() - t0) / 1000000L + " ms");
        }
    }

    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (this.mMachine) {
            return -1;
        }
        if (this.isPreviewWorld()) {
            World world = this.getBaseMetaTileEntity()
                .getWorld();
            if (sPreviewBuiltWorld != world) {
                sPreviewBuiltWorld = world;
                int before = this.countPreviewBlocks();
                try {
                    this.buildPiece(
                        STRUCTURE_PIECE_MAIN,
                        stackSize,
                        false,
                        HORIZONTAL_OFFSET,
                        VERTICAL_OFFSET,
                        DEPTH_OFFSET);
                } catch (Throwable t) {
                    LOG_PREVIEW.error("预览建造抛异常", t);
                }
                LOG_PREVIEW.info("预览建造完成：方块数 " + before + " -> " + this.countPreviewBlocks());
            }
            return 0;
        }

        int realBudget = elementBudget >= 200 ? elementBudget : Math.min(200, elementBudget * 5);
        try {
            return this.survivalBuildPiece(
                STRUCTURE_PIECE_MAIN,
                stackSize,
                HORIZONTAL_OFFSET,
                VERTICAL_OFFSET,
                DEPTH_OFFSET,
                realBudget,
                env,
                false,
                true);
        } catch (Throwable t) {
            LOG_PREVIEW.error("生存建造抛异常 budget=" + elementBudget, t);
            return -1;
        }
    }

    private boolean isPreviewWorld() {
        IGregTechTileEntity base = this.getBaseMetaTileEntity();
        if (base == null || base.getWorld() == null) return false;
        String name = base.getWorld()
            .getClass()
            .getName();
        return name.contains("Dummy") || name.contains("Observer") || name.contains("Tracked");
    }

    private int countPreviewBlocks() {
        IGregTechTileEntity base = this.getBaseMetaTileEntity();
        if (base == null || base.getWorld() == null) return -1;
        World world = base.getWorld();
        int cx = base.getXCoord();
        int cy = base.getYCoord();
        int cz = base.getZCoord();
        int nonAir = 0;
        Map<String, Integer> byBlock = new HashMap<>();

        for (int dx = -64; dx <= 64; ++dx) {
            for (int dz = -64; dz <= 64; ++dz) {
                for (int dy = -14; dy <= 16; ++dy) {
                    Block block = world.getBlock(cx + dx, cy + dy, cz + dz);
                    if (block == null || block == Blocks.air) continue;
                    ++nonAir;
                    String key = block.getUnlocalizedName() + "@" + world.getBlockMetadata(cx + dx, cy + dy, cz + dz);
                    byBlock.merge(key, 1, Integer::sum);
                }
            }
        }

        byBlock.entrySet()
            .stream()
            .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
            .limit(8L)
            .forEach(e -> LOG_PREVIEW.info("    " + e.getKey() + " x" + e.getValue()));
        return nonAir;
    }

    /**
     * 三环「鳍」信道槽位：**1 级 = 鳍块本身**，2~10 级 = 该家族发生器的 9 个等级（粗制 → 鸿蒙）。
     * <p>
     * 用 StructureLib 的标准分级方块实现（{@code ofBlocksTiered} + {@code withChannel}）：
     * </p>
     * <ul>
     * <li>实机结构检查按方块真实等级走：同一个信道里的方块**必须同级**（要么全填鳍，要么全填同级发生器，
     * 空位/普通机壳不受限）——这就是「时间/空间/稳定 必须同时升级」；</li>
     * <li>BlockRenderer 的 NEI 预览会识别它，并给这条信道生成 1~10 的滑条；不单独设信道时跟随全局
     * 「等级」滑条，三个鳍一起变。</li>
     * </ul>
     */
    private static IStructureElement<OTEDeepSeiko> finSlot(String channel, int finMeta, IItemContainer[] gens,
        java.util.function.BiConsumer<OTEDeepSeiko, Integer> setter,
        java.util.function.ToIntFunction<OTEDeepSeiko> getter) {
        List<Pair<Block, Integer>> reps = new ArrayList<>();
        // 第 1 级：鳍（外观同鲸脂外壳，名字不同）
        reps.add(Pair.of(DeepSeikoBlocks.dsCasing, finMeta));
        // 第 2~10 级：发生器的 9 个等级
        for (int i = 0; i < gens.length; ++i) {
            ItemStack stack = gens[i].get(1L, new Object[0]);
            if (stack == null || stack.getItem() == null) continue;
            Block block = Block.getBlockFromItem(stack.getItem());
            if (block == null) continue;
            reps.add(Pair.of(block, stack.getItemDamage()));
        }
        IStructureElement<OTEDeepSeiko> tiered = StructureUtility.ofBlocksTiered((block, meta) -> {
            for (int i = 0; i < reps.size(); ++i) {
                Pair<Block, Integer> p = reps.get(i);
                if (p.getLeft() == block && p.getRight() == meta) return Integer.valueOf(i + 1);
            }
            return null;
        }, reps, Integer.valueOf(0), setter, t -> Integer.valueOf(getter.applyAsInt(t)));
        // 老存档兼容：空位、以及原先填的普通鲸脂外壳仍然算数（不参与定级）
        return StructureUtility.ofChain(
            StructureUtility.withChannel(channel, tiered),
            StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 0),
            StructureUtility.isAir());
    }

    private static Predicate<ItemStack> anyHatchItem() {
        return (stack) -> { return ItemMachines.getMetaTileEntity(stack) instanceof MTEHatch; };
    }

    private static Predicate<ItemStack> anyFluidInputItem() {
        return (stack) -> { return ItemMachines.getMetaTileEntity(stack) instanceof MTEHatchInput; };
    }

    private boolean addAnyHatchToMachineList(IGregTechTileEntity aBaseMetaTileEntity, int aBaseCasingIndex) {
        IMetaTileEntity aMetaTileEntity = aBaseMetaTileEntity.getMetaTileEntity();
        if (aMetaTileEntity instanceof OTEDeepSeikoNaniteCell) {
            ((OTEDeepSeikoNaniteCell) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            this.mNaniteCell = (OTEDeepSeikoNaniteCell) aMetaTileEntity;
            return true;
        } else {
            return this.addToMachineList(aBaseMetaTileEntity, aBaseCasingIndex);
        }
    }

    public IStructureDefinition<OTEDeepSeiko> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            int casingIndex = GTUtility.getCasingTextureIndex(DeepSeikoBlocks.dsCasing, 0);
            STRUCTURE_DEFINITION = StructureDefinition.builder()
                // 三条鳍信道（ds_left_fin / ds_right_fin / ds_top_fin）由 finSlot() 里的 withChannel 登记，
                // NEI 预览会据此生成 1~10 的信道滑条。注意：StructureLib 1.4.x 没有 addChannel()，
                // 所以这里不显式声明信道上下限（预览滑条的最小值按 0 = 不设置处理）。
                .addShape("main", generateWhaleShape())
                .addElement('A', StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 0))
                .addElement('L', StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 7))
                .addElement('K', StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 8))
                .addElement('B', StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 2))
                .addElement(
                    'H',
                    GTStructureUtility.buildHatchAdder(OTEDeepSeiko.class)
                        .adder(OTEDeepSeiko::addAnyHatchToMachineList)
                        .hint(1)
                        .casingIndex(casingIndex)
                        .hatchItemFilter((t) -> { return anyHatchItem(); })
                        // GT 自带机制：格子已有 GT 机器/舱室时 survivalPlaceBlock 返回 SKIP，
                        // 而 placeBlock 对 SKIP 也返回 true（见 HatchElementBuilder$2 字节码），
                        // 链条就此停止，后面的 ofBlock(casing) 不会把玩家放好的舱室覆盖掉。
                        .shouldSkip((t, te) -> true)
                        .buildAndChain(
                            new IStructureElement[] { StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 0) }))
                .addElement('C', StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 4))
                // 三环 = 三条鳍信道（'T' 左鳍 / 'S' 右鳍 / 'R' 顶鳍）
                .addElement(
                    'T',
                    finSlot(
                        CH_FIN_TIME,
                        DeepSeikoCasingMeta.FIN_TIME,
                        TIME_GENS,
                        (t, lvl) -> { t.timeTier = lvl.intValue(); },
                        t -> t.timeTier))
                .addElement(
                    'S',
                    finSlot(
                        CH_FIN_SPACE,
                        DeepSeikoCasingMeta.FIN_SPACE,
                        SPACE_GENS,
                        (t, lvl) -> { t.spaceTier = lvl.intValue(); },
                        t -> t.spaceTier))
                .addElement(
                    'R',
                    finSlot(
                        CH_FIN_STAB,
                        DeepSeikoCasingMeta.FIN_STAB,
                        STAB_GENS,
                        (t, lvl) -> { t.stabTier = lvl.intValue(); },
                        t -> t.stabTier))
                .addElement(
                    'V',
                    StructureUtility.ofChain(
                        new IStructureElement[] { StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 1),
                            StructureUtility.ofBlock(DeepSeikoBlocks.dsCasing, 6) }))
                .build();
        }

        return STRUCTURE_DEFINITION;
    }

    private static String[][] generateWhaleShape() {
        int HEI = OTEDeepSeikoShape.HEI;
        int WID = OTEDeepSeikoShape.WID;
        int LEN = OTEDeepSeikoShape.LEN;
        int CZ = 16;
        char[][][] g = new char[HEI][WID][LEN];

        int y;
        int z;
        int i;
        for (y = 0; y < 23; ++y) {
            for (z = 0; z < 33; ++z) {
                for (i = 0; i < 60; ++i) {
                    g[y][z][i] = ' ';
                }
            }
        }

        for (y = 0; y < 23; ++y) {
            for (z = 0; z < 33; ++z) {
                for (i = 0; i < 60; ++i) {
                    String row = OTEDeepSeikoShape.RAW_LAYERS[y][z];
                    char c = i < row.length() ? row.charAt(i) : 32;
                    if (c == 'D') {
                        g[y][z][i] = 'A';
                    } else if (c == 'L') {
                        g[y][z][i] = 'L';
                    } else if (c == 'K') {
                        g[y][z][i] = 'K';
                    } else if (c == 'W') {
                        g[y][z][i] = 'C';
                    }
                }
            }
        }

        fillRowGaps(g, 0, 4, 'A');
        fillRowGaps(g, 4, 7, 'B');
        // ⚠ 鲸须（B）只保留下颌 y<=9：fillRowGaps 本来是整段 x=4~6 都填 B，
        // 结果从下颌一路包到头顶，远看"头上套了一圈灰格栅"（用户实机反馈）。
        // 上半段改回背脊 A，让头顶和背脊连成一片，灰格栅只留在嘴/喉那一条。
        for (int ry = 10; ry < HEI; ++ry) {
            for (int rz = 0; rz < WID; ++rz) {
                for (int rx = 4; rx < 7; ++rx) {
                    if (g[ry][rz][rx] == 'B') g[ry][rz][rx] = 'A';
                }
            }
        }
        // 补下巴（下颌）：原来头部只有上半截（上颚在 y=11~12），鼻子下面是空的，
        // 侧面看像被切了一刀。这里在 x=0~3 补一圈白肚（C）当下颌，鼻尖逐格收窄；
        // ⚠ 上颚在 y=11~12，下颌只补到 y<=9 ⇒ y=10 留出 1 格缝 = **微微张嘴**（不要补死）。
        int[][] chinSpec = { { 0, 8, 9 }, { 1, 7, 9 }, { 2, 6, 9 }, { 3, 6, 7 } };
        for (int[] cs : chinSpec) {
            int cx = cs[0], yBot = cs[1], yTop = cs[2];
            int zLo = -1, zHi = -1;
            for (int cz = 0; cz < WID; ++cz) {
                if (g[11][cz][cx] != ' ') {
                    if (zLo < 0) zLo = cz;
                    zHi = cz;
                }
            }
            if (zLo < 0) continue;
            int shrink = (cx == 0) ? 2 : (cx <= 2 ? 1 : 0); // 鼻尖逐格收窄，别做成方头
            for (int cy = yBot; cy <= yTop; ++cy) {
                for (int cz2 = zLo + shrink; cz2 <= zHi - shrink; ++cz2) {
                    if (g[cy][cz2][cx] == ' ') g[cy][cz2][cx] = 'C';
                }
            }
        }
        // 下颌下缘接成斜坡：bottom(x)=8-x，一路连到 x=6 的 y=2（原来 x=5 到 x=6 是 4 格台阶，
        // 侧面看像个方头"中巴"。从后往前填，每格取 (x+1,y) 的 z 范围再各收 1 格 ⇒ 顺着身体收窄。
        for (int jx = 5; jx >= 3; --jx) {
            for (int jy = 8 - jx; jy <= 5; ++jy) {
                int jzA = -1, jzB = -1;
                for (int jz = 0; jz < WID; ++jz) {
                    if (g[jy][jz][jx + 1] != ' ') {
                        if (jzA < 0) jzA = jz;
                        jzB = jz;
                    }
                }
                if (jzA < 0) continue;
                for (int jz2 = jzA + 1; jz2 <= jzB - 1; ++jz2) {
                    if (g[jy][jz2][jx] == ' ') g[jy][jz2][jx] = 'C';
                }
            }
        }
        g[12][16][0] = '~';

        for (y = 2; y <= 6; ++y) {
            for (z = 14; z <= 18; ++z) {
                if (y != 6 || z != 18) {
                    for (i = 22; i >= 0; --i) {
                        if (g[i][z][y] != ' ') {
                            g[i][z][y] = 'H';
                            break;
                        }
                    }
                }
            }
        }

        // 三环槽位：位置由 RING_SLOTS_RAW 指定（三组顺序固定：时间 | 空间 | 稳定）。
        // 三组各自是一条「鳍信道」：'T' 左鳍 / 'S' 右鳍 / 'R' 顶鳍 —— 见 finSlot()。
        int h;
        int row;
        int ringGroupIndex = 0;
        for (String ringGroup : RING_SLOTS_RAW.split("\\|")) {
            char ringChar = ringGroupIndex == 0 ? 'T' : ringGroupIndex == 1 ? 'S' : 'R';
            for (String ringOne : ringGroup.split(";")) {
                String[] rp = ringOne.split(",");
                if (rp.length != 3) continue;
                int rx = Integer.parseInt(rp[0].trim());
                int ry = Integer.parseInt(rp[1].trim());
                int rz = Integer.parseInt(rp[2].trim());
                if (rx < 0 || rx >= LEN || ry < 0 || ry >= HEI || rz < 0 || rz >= WID) continue;
                if (g[ry][rz][rx] != ' ') g[ry][rz][rx] = ringChar;
            }
            ++ringGroupIndex;
        }

        int slice;
        for (i = 20; i <= 22; ++i) {
            for (slice = 22; slice >= 0; --slice) {
                char c0 = g[slice][16][i];
                // 跳过三环鳍槽（T/S/R）：背脊那几个格子是环槽，不能被尾部填充抢走
                if (c0 != ' ' && c0 != 'T' && c0 != 'S' && c0 != 'R') {
                    g[slice][16][i] = 'V';
                    break;
                }
            }
        }

        // 肚皮最底下那一排 7 格统一是「装配腔方块」（历史挖空机制已移除，这里明确补回）
        for (int bellyX = 20; bellyX <= 26; ++bellyX) {
            g[0][16][bellyX] = 'C';
        }

        // 标记舱位（MARKED_HATCHES 的坐标序是 (宽, 22-y, 长)，与上面的 g[y][z][x] 不是同一套顺序）
        for (String mark : MARKED_HATCHES) {
            String[] halves = mark.split(":");
            String[] coords = halves[0].split(",");
            int a = Integer.parseInt(coords[0].trim());
            int b = Integer.parseInt(coords[1].trim());
            int c = Integer.parseInt(coords[2].trim());
            char ch = halves[1].trim()
                .charAt(0);
            if (a < 0 || a >= WID || b < 0 || b >= HEI || c < 0 || c >= LEN) {
                throw new IllegalArgumentException("标记舱位越界: " + mark);
            }

            int yy = HEI - 1 - b;
            if (g[yy][a][c] != '~') {
                g[yy][a][c] = ch;
            }
        }

        // 清理被完全包住的方块：六个面都是方块 ⇒ 从外面和内部腔体都看不见，
        // 纯粹白吃结构材料。实测这一步能砍掉 437 个（3221 → 2784），且一轮就到不动点
        // （第二轮 0 个），所以只扫一次。两阶段标记再删，避免边删边判导致的顺序差异。
        boolean[][][] buried = new boolean[HEI][WID][LEN];
        int culled = 0;
        for (int cx = 0; cx < LEN; ++cx) {
            for (int cy = 0; cy < HEI; ++cy) {
                for (int cz = 0; cz < WID; ++cz) {
                    if (g[cy][cz][cx] == ' ') continue;
                    boolean allSolid = true;
                    for (int d = 0; d < 6 && allSolid; ++d) {
                        int nx = cx + (d == 0 ? 1 : d == 1 ? -1 : 0);
                        int ny = cy + (d == 2 ? 1 : d == 3 ? -1 : 0);
                        int nz = cz + (d == 4 ? 1 : d == 5 ? -1 : 0);
                        if (nx < 0 || nx >= LEN || ny < 0 || ny >= HEI || nz < 0 || nz >= WID) {
                            allSolid = false; // 出界也算露着
                        } else if (g[ny][nz][nx] == ' ') {
                            allSolid = false;
                        }
                    }
                    if (allSolid) buried[cy][cz][cx] = true;
                }
            }
        }
        for (int cx = 0; cx < LEN; ++cx) {
            for (int cy = 0; cy < HEI; ++cy) {
                for (int cz = 0; cz < WID; ++cz) {
                    if (buried[cy][cz][cx]) {
                        g[cy][cz][cx] = ' ';
                        ++culled;
                    }
                }
            }
        }
        if (culled > 0) {
            LOG.debug("迪普西科: 清理被包住的方块 " + culled + " 个");
        }

        String[][] shape = new String[60][23];

        for (slice = 0; slice < 60; ++slice) {
            for (row = 0; row < 23; ++row) {
                h = 22 - row;
                StringBuilder sb = new StringBuilder(33);

                for (int zz = 0; zz < 33; ++zz) {
                    sb.append(g[h][zz][slice]);
                }

                shape[slice][row] = sb.toString();
            }
        }

        return shape;
    }

    private static void fillRowGaps(char[][][] g, int xFrom, int xTo, char fill) {
        for (int x = xFrom; x < xTo; ++x) {
            for (int y = 0; y < g.length; ++y) {
                int minZ = -1;
                int maxZ = -1;

                int z;
                for (z = 0; z < g[0].length; ++z) {
                    if (g[y][z][x] != ' ') {
                        if (minZ < 0) {
                            minZ = z;
                        }

                        maxZ = z;
                    }
                }

                if (minZ >= 0) {
                    for (z = minZ; z <= maxZ; ++z) {
                        if (g[y][z][x] == ' ') {
                            g[y][z][x] = fill;
                        }
                    }
                }
            }
        }

    }

    /**
     * 全息投影仪「搭建细节」聊天栏里列的内容。
     *
     * <p>
     * 注意：这里必须<b>调用时</b>再翻译（用 {@link StatCollector#translateToLocal}），不能在 static 初始化里
     * 就翻好——那样会在语言文件加载之前执行，结果拿到的就是原始 key（之前聊天栏里刷 ote.cm.ds.desc.0 这种
     * 就是这个原因）。
     * </p>
     */
    public String[] getStructureDescription(ItemStack stackSize) {
        return new String[] { EnumChatFormatting.AQUA + StatCollector.translateToLocal("otht.con") + ":",
            "§7" + StatCollector.translateToLocal("ote.ds.struct.0"),
            "§7" + StatCollector.translateToLocal("ote.ds.struct.1"),
            "§7" + StatCollector.translateToLocal("ote.ds.struct.2"),
            "§7" + StatCollector.translateToLocal("ote.ds.struct.3"),
            "§7" + StatCollector.translateToLocal("ote.ds.struct.4") };
    }

    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(EnumChatFormatting.BLUE + StatCollector.translateToLocal("ote.tm.ds.type"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.0"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.0b"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.1"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.2"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.3"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.4"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.5"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.5b"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.perf"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.perf2"))
            .addInfo(StatCollector.translateToLocal("ote.tm.ds.6"))
            .beginStructureBlock(60, 23, 33, false)
            .addController(StatCollector.translateToLocal("ote.tm.ds.hint.controller"))
            .addInputBus(StatCollector.translateToLocal("ote.tm.ds.hint.bays"), 2)
            .addOutputBus(StatCollector.translateToLocal("ote.tm.ds.hint.bays"), 3)
            .addInputHatch(StatCollector.translateToLocal("ote.tm.ds.hint.bays"), 2)
            .addOutputHatch(StatCollector.translateToLocal("ote.tm.ds.hint.bays"), 3)
            .addEnergyHatch(StatCollector.translateToLocal("ote.tm.ds.hint.bays"), 1)
            .addStructureInfo("")
            .addStructureInfo(StatCollector.translateToLocal("ote.tm.ds.hint.rings.2"))
            .addStructureInfo(StatCollector.translateToLocal("ote.tm.ds.hint.rings.4"))
            .addStructureInfo(StatCollector.translateToLocal("ote.tm.ds.hint.rings.5"))
            .addStructureInfo(StatCollector.translateToLocal("ote.tm.ds.hint.rings.6"))
            .toolTipFinisher("§a添加者: 123Technology - DeepSeek && Lokisidus");
        return tt;
    }

    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_NOISE;
    }

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        return side == facing ? new ITexture[] { CONTROLLER_FRONT_TEXTURE }
            : new ITexture[] { CONTROLLER_SIDE_TEXTURE };
    }

    public String[] getInfoData() {
        List<String> info = new ArrayList(Arrays.asList(super.getInfoData()));
        if (this.machineMode == 1 && this.mMaxProgresstime > 0) {
            long totalPoints = (long) this.mMaxProgresstime * (long) effectiveBecBaseRate();
            long donePoints = (long) this.mProgresstime * (long) effectiveBecBaseRate();
            long rate = (long) Math.max(1.0F, getBecCurrentRate());
            long leftTicks = (long) Math.max(0, this.mMaxProgresstime - this.mProgresstime);
            long etaSec = leftTicks / 20L;
            info.add(
                EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("ote.ds.info.points")
                    + ": "
                    + GTUtility.formatShortenedLong(donePoints)
                    + " / "
                    + GTUtility.formatShortenedLong(totalPoints)
                    + "  §7("
                    + String.format("%.1f", 100.0F * this.mProgresstime / Math.max(1, this.mMaxProgresstime))
                    + "%)");
            info.add(
                EnumChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("ote.ds.info.rate")
                    + ": "
                    + GTUtility.formatShortenedLong(rate)
                    + " "
                    + StatCollector.translateToLocal("ote.ds.info.perTick")
                    + "   §7"
                    + StatCollector.translateToLocal("ote.ds.info.eta")
                    + " "
                    + etaSec
                    + " "
                    + StatCollector.translateToLocal("ote.ds.info.seconds"));
        }
        info.add(
            StatCollector.translateToLocal("ote.ds.gui.probe.boost") + ": "
                + EnumChatFormatting.GOLD
                + "×"
                + String.format("%.1f", this.mSpeedMult)
                + (this.isSpeedUnlocked()
                    ? " " + EnumChatFormatting.DARK_GREEN + StatCollector.translateToLocal("ote.ds.gui.probe.ultimate")
                    : ""));
        info.add(
            StatCollector.translateToLocal("ote.ds.gui.probe.rings") + ": "
                + EnumChatFormatting.AQUA
                + this.getTimeLevel()
                + " / "
                + this.getSpaceLevel()
                + " / "
                + this.getStabLevel());
        if (this.isTokenInserted()) {
            info.add(EnumChatFormatting.GOLD + StatCollector.translateToLocal("ote.ds.gui.token"));
        }

        return (String[]) info.toArray(new String[0]);
    }

    @NotNull
    protected MTEMultiBlockBaseGui<?> getGui() {
        return new DeepSeikoGui(this);
    }

    public int getTimeTier() {
        return this.timeTier;
    }

    public int getSpaceTier() {
        return this.spaceTier;
    }

    public int getStabTier() {
        return this.stabTier;
    }

    /**
     * GUI 显示用的总算速。
     * <p>
     * 正常情况就是上次有效配方算出来的 {@code mSpeedMult}；但**插着调试币、还没跑过配方**时它是 1.0，
     * 玩家会以为币没生效（用户反馈过）。所以插币且尚无结果时，直接按「满算力 + 满环 + 终极 + 冷却正常」算一遍理论值。
     * </p>
     */
    public float getSpeedMultiplier() {
        // 装配线模式：加速完全由三环（时间 + 稳定）决定，跟算力/终极无关
        if (this.machineMode == 0) {
            return DeepSeikoMath.alSpeed(getTimeLevel(), getStabLevel());
        }
        if (this.isTokenInserted() && this.mSpeedMult <= 1.0F) {
            return DeepSeikoMath.totalSpeed(10000.0F, getTimeLevel(), true);
        }
        return this.mSpeedMult;
    }

    /** 当前算力乘区（上次有效配方算出来的）—— 给左侧细面板显示 */
    public float getComputeFactor() {
        // 用户要的就是「算力卡公式」本身：1 + ³√(张数) × 卡系数
        // 例：64 张 D999 = 1 + ³√64 × 0.25 = 1 + 4 × 0.25 = ×2.00；没插卡 = ×1.00；调试币 = ×10000
        return this.getComputeCardMultiplier();
    }

    public long getEnergyUsage() {
        return this.getActualEnergyUsage();
    }

    public int getProgressScaled(int scale) {
        return this.mMaxProgresstime <= 0 ? 0
            : (int) ((long) this.mProgresstime * (long) scale / (long) this.mMaxProgresstime);
    }

    static {
        TIME_GENS = new IItemContainer[] { CustomItemList.TimeAccelerationFieldGeneratorTier0,
            CustomItemList.TimeAccelerationFieldGeneratorTier1, CustomItemList.TimeAccelerationFieldGeneratorTier2,
            CustomItemList.TimeAccelerationFieldGeneratorTier3, CustomItemList.TimeAccelerationFieldGeneratorTier4,
            CustomItemList.TimeAccelerationFieldGeneratorTier5, CustomItemList.TimeAccelerationFieldGeneratorTier6,
            CustomItemList.TimeAccelerationFieldGeneratorTier7, CustomItemList.TimeAccelerationFieldGeneratorTier8 };
        SPACE_GENS = new IItemContainer[] { CustomItemList.SpacetimeCompressionFieldGeneratorTier0,
            CustomItemList.SpacetimeCompressionFieldGeneratorTier1,
            CustomItemList.SpacetimeCompressionFieldGeneratorTier2,
            CustomItemList.SpacetimeCompressionFieldGeneratorTier3,
            CustomItemList.SpacetimeCompressionFieldGeneratorTier4,
            CustomItemList.SpacetimeCompressionFieldGeneratorTier5,
            CustomItemList.SpacetimeCompressionFieldGeneratorTier6,
            CustomItemList.SpacetimeCompressionFieldGeneratorTier7,
            CustomItemList.SpacetimeCompressionFieldGeneratorTier8 };
        STAB_GENS = new IItemContainer[] { CustomItemList.StabilisationFieldGeneratorTier0,
            CustomItemList.StabilisationFieldGeneratorTier1, CustomItemList.StabilisationFieldGeneratorTier2,
            CustomItemList.StabilisationFieldGeneratorTier3, CustomItemList.StabilisationFieldGeneratorTier4,
            CustomItemList.StabilisationFieldGeneratorTier5, CustomItemList.StabilisationFieldGeneratorTier6,
            CustomItemList.StabilisationFieldGeneratorTier7, CustomItemList.StabilisationFieldGeneratorTier8 };
        MARKED_HATCHES = new String[] { "19,11,0:H", "18,11,0:H", "17,11,0:H", "16,11,0:H", "15,11,0:H", "14,11,0:H",
            "13,11,0:H", "17,10,0:H", "15,10,0:H", "14,10,0:H", "17,9,0:H", "16,9,0:H", "15,9,0:H", "20,10,1:H",
            "19,10,1:H", "18,10,1:H", "13,10,1:H", "12,10,1:H", "19,9,1:H", "18,9,1:H", "14,9,1:H", "13,9,1:H",
            "18,8,1:H", "17,8,1:H", "16,8,1:H", "15,8,1:H", "14,8,1:H", "21,9,2:H", "20,9,2:H", "12,9,2:H", "11,9,2:H",
            "19,8,2:H", "13,8,2:H", "12,8,2:H", "19,7,2:H", "18,7,2:H", "17,7,2:H", "16,7,2:H", "15,7,2:H", "14,7,2:H",
            "13,7,2:H" };
        CONTROLLER_FRONT_TEXTURE = TextureFactory.builder()
            .addIcon(DeepSeikoIcons.CONTROLLER_FRONT)
            .extFacing()
            .build();
        CONTROLLER_SIDE_TEXTURE = TextureFactory.of(DeepSeikoIcons.CONTROLLER_SIDE);
        registerFinChannels();
    }

    /**
     * 注册三条鳍信道：信道说明 + 1 级对应的方块（三个鳍）。
     * <p>
     * 只影响提示文案/全息投影的信道取值；注册失败（StructureLib 版本差异）不影响结构与预览。
     * </p>
     */
    private static void registerFinChannels() {
        try {
            registerFinChannel(CH_FIN_TIME, DeepSeikoCasingMeta.FIN_TIME);
            registerFinChannel(CH_FIN_SPACE, DeepSeikoCasingMeta.FIN_SPACE);
            registerFinChannel(CH_FIN_STAB, DeepSeikoCasingMeta.FIN_STAB);
        } catch (Throwable ignored) {
            // 信道说明只是锦上添花
        }
    }

    private static void registerFinChannel(String channel, int finMeta) {
        StructureLibAPI.registerChannelDescription(channel, "123technology", "channels.123technology." + channel);
        StructureLibAPI
            .registerChannelItem(channel, "123technology", 1, new ItemStack(DeepSeikoBlocks.dsCasing, 1, finMeta));
    }
}
