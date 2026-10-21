package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockReinforced;
import static gregtech.api.enums.GTValues.V;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.BLOCK_PLASCRETE;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTUtility.validMTEList;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.newmaa.othtech.machine.machineclass.OTHTTMultiMachineBaseEM;

import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.apiculture.IBeekeepingMode;
import forestry.api.genetics.AlleleManager;
import forestry.apiculture.genetics.Bee;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.VoltageIndex;
import gregtech.api.interfaces.IOutputBus;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.ErrorType;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class OTEBeeyonds extends OTHTTMultiMachineBaseEM implements IConstructable, ISurvivalConstructable {

    private static final int MAX_SLOTS = 12;
    private static final int CYCLE_TICKS = 100;
    private static final double BEE_ACCELERATION = 64d;
    /** 每只女王 0.5A HV */
    private static final long EU_PER_BEE = V[VoltageIndex.HV] / 2;
    /** 4 * 1.2^8, 与GT巨型蜂箱同款的生产修正基准 */
    private static final float BASE_PRODUCTION_MODIFIER = 17.19926784f;

    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final int H_OFFSET = 11;
    private static final int V_OFFSET = 20;
    private static final int D_OFFSET = 1;

    private final ArrayList<BeeSim> mStorage = new ArrayList<>();
    private final Map<GTUtility.ItemId, Double> dropProgress = new HashMap<>();
    private IBeeRoot beeRoot;
    /** false = 产出模式, true = 取回模式(把女王送回输出总线) */
    private boolean outputMode = false;
    /** 当前这轮实际供电运行的女王数(受能源仓功率限制) */
    private int activeBees = 0;

    public OTEBeeyonds(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eDismantleBoom = true;
    }

    public OTEBeeyonds(String aName) {
        super(aName);
        eDismantleBoom = true;
    }

    private IBeeRoot bees() {
        if (beeRoot == null) beeRoot = (IBeeRoot) AlleleManager.alleleRegistry.getSpeciesRoot("rootBees");
        return beeRoot;
    }

    // region 结构

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, H_OFFSET, V_OFFSET, D_OFFSET, errors)) return;
        repairMachine();
        checkEnergyHatchLimit(errors);
        checkHasOutputBus(errors);
    }

    /** 能源仓(含激光/无线)最多一个 */
    private void checkEnergyHatchLimit(List<StructureError> errors) {
        int count = (int) (Energy.count(this) + ExoticEnergy.count(this));
        if (count < 1) {
            errors.add(StructureErrors.hatchCount(ErrorType.TOO_FEW, Energy, count, 1));
        } else if (count > 1) {
            errors.add(StructureErrors.hatchCount(ErrorType.TOO_MANY, Energy, count, 1));
        }
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        if (!mMachine) aBaseMetaTileEntity.disableWorking();
    }

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.bee.0"), };

    private static final IStructureDefinition<OTEBeeyonds> STRUCTURE_DEFINITION = IStructureDefinition
        .<OTEBeeyonds>builder()
        .addShape(
            STRUCTURE_PIECE_MAIN,
            new String[][] {
                { "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "        BBBBBBB        ", "       BCCCCCCCB       ",
                    "     BBBCCCCCCCBBB     ", "   BBBBBCCCCCCCBBBBB   ", "     BBBCCCCCCCBBB     ",
                    "       BCCCCCCCB       ", "        BBBBBBB        ", "                       ",
                    "                       " },
                { "                       ", "       BBB    BBB      ", "      BBBBB  BBBBB     ",
                    "      BBBBBBBBBBBB     ", "       BBBBBBBBBB      ", "        AAEAEAE        ",
                    "       AAAAEAEAE       ", "      AEAEAEAEAEA      ", "      AAAAAEAEAEAE     ",
                    "      AABAAEAEAEA      ", "       AAAAEAEAE       ", "        AAEAEAE        ",
                    "                       ", "       BBBBBBBBB       ", "    BBBBCCCCCCCBBBB    ",
                    "    BBBBCCCCCCCBBBB    ", "  BBBBBBC     CBBBBBB  ", "    BBBBC     CBBBB    ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "          F~F          ",
                    "          FFF          " },
                { "       BBB    BBB      ", "      BBBBB  BBBBB     ", "     BBBBBBBBBBBBBB    ",
                    "     BBBBBBBBBBBBBB    ", "      BBBBBBBBBBBB     ", "       AAAEAEAEE       ",
                    "      AAAAAEAEAEA      ", "     AAEAEAEAEAEAE     ", "     AAAAAAEAEAEAEEE   ",
                    "     AAABAAEAEAEAE     ", "      AAAAAEAEAEA      ", "       AAAEAEAEE       ",
                    "        AAEAEAE        ", "       BBBBBBBBB       ", "    BBBBC     CBBBB    ",
                    "   BBBBBC     CBBBBB   ", "BBBBBBBBC     CBBBBBBBB", "   BBBBBC     CBBBBB   ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "         FFBFF         ",
                    "          FFF          " },
                { "                       ", "       BBB    BBB      ", "      BBBBB  BBBBB     ",
                    "      BBBBBBBBBBBB     ", "       BBBBBBBBBB      ", "        AAEAEAE        ",
                    "       AAAAEAEAE       ", "      AAAAAEAEAEA      ", "      AAAAAEAEAEAE     ",
                    "      AAAAAEAEAEA      ", "       AAAAEAEAE       ", "        AAEAEAE        ",
                    "                       ", "       BBBBBBBBB       ", "    BBBBCCCCCCCBBBB    ",
                    "    BBBBCCCCCCCBBBB    ", "  BBBBBBC     CBBBBBB  ", "    BBBBC     CBBBB    ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "          FFF          ",
                    "          FFF          " },
                { "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "        BBBBBBB        ", "       BCCCCCCCB       ",
                    "     BBBCCCCCCCBBB     ", "   BBBBBCCCCCCCBBBBB   ", "     BBBCCCCCCCBBB     ",
                    "       BCCCCCCCB       ", "        BBBBBBB        ", "                       ",
                    "                       " } })
        .addElement('A', ofBlock(sBlockCasings1, 10))
        .addElement('B', ofBlock(sBlockReinforced, 2))
        .addElement('C', ofBlock(Blocks.glass, 0))
        .addElement('D', ofBlock(Blocks.grass, 0))
        .addElement('E', ofBlock(Blocks.obsidian, 0))
        .addElement(
            'F',
            buildHatchAdder(OTEBeeyonds.class)
                .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                .adder(OTEBeeyonds::addToMachineList)
                .hint(1)
                .casingIndex(210)
                .buildAndChain(sBlockReinforced, 2))
        .build();

    @Override
    public IStructureDefinition<OTEBeeyonds> getStructure_EM() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, H_OFFSET, V_OFFSET, D_OFFSET);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            STRUCTURE_PIECE_MAIN,
            stackSize,
            H_OFFSET,
            V_OFFSET,
            D_OFFSET,
            elementBudget,
            env,
            false,
            true);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    // endregion

    // region 产蜂

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        tag.setInteger("beeStored", mStorage.size());
        tag.setInteger("beeActive", activeBees);
        tag.setInteger("beeTier", getTierIndex());
        tag.setDouble("beeMultiplier", getOutputMultiplier());
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("ote.waila.bee.stored") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getInteger("beeStored")
                + "/"
                + MAX_SLOTS
                + EnumChatFormatting.RESET
                + EnumChatFormatting.GRAY
                + " ("
                + translateToLocal("ote.waila.bee.active")
                + " "
                + tag.getInteger("beeActive")
                + ")"
                + EnumChatFormatting.RESET);
        currentTip.add(
            translateToLocal("ote.waila.bee.tier") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + GTValues.VN[Math.min(tag.getInteger("beeTier"), GTValues.VN.length - 1)]
                + EnumChatFormatting.RESET);
        currentTip.add(
            translateToLocal("otht.waila.bonus.output") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + String.format(java.util.Locale.ROOT, "x%.2f", tag.getDouble("beeMultiplier"))
                + EnumChatFormatting.RESET);
    }

    @NotNull
    @Override
    protected CheckRecipeResult checkProcessing_EM() {
        IBeeRoot root = bees();
        World world = getBaseMetaTileEntity() == null ? null : getBaseMetaTileEntity().getWorld();
        if (root == null || world == null) return CheckRecipeResultRegistry.NO_RECIPE;

        // 取回模式: 每次把一只女王送回输出总线, 不消耗电力
        if (outputMode) {
            if (mStorage.isEmpty()) return CheckRecipeResultRegistry.NO_RECIPE;
            if (!ejectQueen(mStorage.get(0).queen)) return CheckRecipeResultRegistry.ITEM_OUTPUT_FULL;
            mStorage.remove(0);
            activeBees = 0;
            setPowerFlow(0);
            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = 10;
            mOutputItems = null;
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        // 产出模式: 从输入总线补女王蜂, 满 12 槽就不再吃新的
        boolean changed = false;
        if (mStorage.size() < MAX_SLOTS) {
            for (ItemStack input : getStoredInputs()) {
                if (mStorage.size() >= MAX_SLOTS) break;
                BeeSim sim = BeeSim.of(root, input);
                if (sim == null) continue;
                mStorage.add(sim);
                input.stackSize--;
                changed = true;
            }
        }
        if (changed) updateSlots();

        if (mStorage.isEmpty()) {
            // 总线里有蜂但不是女王, 给个明确提示, 免得看起来像"机器不工作"
            for (ItemStack input : getStoredInputs()) {
                EnumBeeType type = input == null ? EnumBeeType.NONE : root.getType(input);
                if (type != EnumBeeType.NONE && type != EnumBeeType.QUEEN) {
                    return SimpleCheckRecipeResult.ofFailure("bee_needqueen");
                }
            }
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        // 只给能源仓供得起的女王供电, 否则 TT 的 drainEnergyInput 会判超功率直接停机
        int active = mStorage.size();
        long cost = EU_PER_BEE * active;
        long cap = Math.max(0L, getMaxInputEu());
        if (cap > 0 && cost > cap) {
            active = (int) Math.max(1L, Math.min(active, cap / EU_PER_BEE));
            cost = Math.min(EU_PER_BEE * active, cap);
        }

        float tier = getTierIndex();
        double multiplier = getOutputMultiplier();
        List<ItemStack> outputs = new ArrayList<>();
        for (int i = 0; i < active; i++) {
            BeeSim sim = mStorage.get(i);
            sim.build(root, world, tier);
            sim.collect(dropProgress, outputs, multiplier);
        }

        setPowerFlow(-cost);
        activeBees = active;
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        mMaxProgresstime = CYCLE_TICKS;
        mOutputItems = mergeOutputs(outputs);
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    /** 输出总线放得下才拿走, 避免把女王吞掉 */
    private boolean ejectQueen(ItemStack queen) {
        for (IOutputBus bus : getOutputBusses()) {
            if (!bus.storePartial(queen.copy(), true)) continue;
            return bus.storePartial(queen.copy(), false);
        }
        return false;
    }

    private int getTierIndex() {
        long voltage = getMaxInputVoltage();
        if (voltage > 0) return Math.max(1, Math.min(V.length - 1, GTUtility.getTier(voltage)));
        // 兜底: 直接按能源仓等级推
        int tier = 1;
        for (MTEHatchEnergy hatch : validMTEList(mEnergyHatches)) {
            tier = Math.max(tier, GTUtility.getTier(hatch.maxEUInput()));
        }
        return Math.min(V.length - 1, tier);
    }

    /** 12 槽塞满且能源仓高于 EV 时产出 × √电压等级, 否则 1 */
    private double getOutputMultiplier() {
        int tier = getTierIndex();
        if (mStorage.size() < MAX_SLOTS || tier <= VoltageIndex.EV) return 1d;
        return Math.sqrt(tier);
    }

    private static ItemStack[] mergeOutputs(List<ItemStack> stacks) {
        Map<GTUtility.ItemId, ItemStack> merged = new HashMap<>();
        for (ItemStack stack : stacks) {
            ItemStack old = merged.get(GTUtility.ItemId.createNoCopy(stack));
            if (old == null) merged.put(GTUtility.ItemId.createNoCopy(stack), stack.copy());
            else old.stackSize += stack.stackSize;
        }
        return merged.values()
            .toArray(new ItemStack[0]);
    }

    /** 只解析基因算产物, 不参与 Forestry 的 beekeeping tick */
    private static class BeeSim {

        private final ItemStack queen;
        private final List<Drop> drops = new ArrayList<>();
        private float builtTier = Float.NaN;

        private BeeSim(ItemStack queen) {
            this.queen = queen;
        }

        static BeeSim of(IBeeRoot root, ItemStack stack) {
            if (root == null || stack == null || root.getType(stack) != EnumBeeType.QUEEN) return null;
            if (root.getMember(stack) == null) return null;
            ItemStack copy = stack.copy();
            copy.stackSize = 1;
            return new BeeSim(copy);
        }

        void build(IBeeRoot root, World world, float tier) {
            if (builtTier == tier) return;
            builtTier = tier;
            drops.clear();
            IBee queenBee = root.getMember(queen);
            if (queenBee == null) return;
            IBeeGenome genome = queenBee.getGenome();
            if (genome == null) return;

            IBeekeepingMode mode = root.getBeekeepingMode(world);
            float prodMod = BASE_PRODUCTION_MODIFIER + mode.getBeeModifier()
                .getProductionModifier(null, BASE_PRODUCTION_MODIFIER);
            float speed = genome.getSpeed();
            IAlleleBeeSpecies primary = genome.getPrimary();
            IAlleleBeeSpecies secondary = genome.getSecondary();

            primary.getProductChances()
                .forEach((stack, chance) -> add(stack, chance, speed, prodMod, tier));
            secondary.getProductChances()
                .forEach((stack, chance) -> add(stack, chance / 2f, speed, prodMod, tier));
            primary.getSpecialtyChances()
                .forEach((stack, chance) -> add(stack, chance, speed, prodMod, tier));
        }

        private void add(ItemStack stack, float chance, float speed, float prodMod, float tier) {
            if (stack == null) return;
            double amount = Bee.getFinalChance(chance, speed, prodMod, tier) * stack.stackSize;
            if (amount > 0d) drops.add(new Drop(stack, amount));
        }

        void collect(Map<GTUtility.ItemId, Double> progress, List<ItemStack> out, double multiplier) {
            double scale = BEE_ACCELERATION * CYCLE_TICKS / 550d * multiplier;
            for (Drop drop : drops) {
                double v = progress.getOrDefault(drop.id, 0d) + drop.amount * scale;
                while (v >= 1d) {
                    int size = Math.min((int) v, 64);
                    ItemStack stack = drop.stack.copy();
                    stack.stackSize = size;
                    out.add(stack);
                    v -= size;
                }
                progress.put(drop.id, v);
            }
        }

        private static class Drop {

            private final ItemStack stack;
            private final GTUtility.ItemId id;
            private final double amount;

            private Drop(ItemStack stack, double amount) {
                this.stack = stack;
                this.id = GTUtility.ItemId.createNoCopy(stack);
                this.amount = amount;
            }
        }
    }

    // endregion

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (getBaseMetaTileEntity() == null || !getBaseMetaTileEntity().isServerSide()) return;
        outputMode = !outputMode;
        GTUtility.sendChatToPlayer(
            aPlayer,
            translateToLocal(outputMode ? "ote.tm.bee.mode.out" : "ote.tm.bee.mode.produce"));
    }

    // region 存档

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("outputMode", outputMode);
        NBTTagList list = new NBTTagList();
        for (BeeSim sim : mStorage) {
            NBTTagCompound tag = new NBTTagCompound();
            sim.queen.writeToNBT(tag);
            list.appendTag(tag);
        }
        aNBT.setTag("mStorage", list);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        outputMode = aNBT.getBoolean("outputMode");
        mStorage.clear();
        dropProgress.clear();
        NBTTagList list = aNBT.getTagList("mStorage", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            BeeSim sim = BeeSim.of(bees(), ItemStack.loadItemStackFromNBT(list.getCompoundTagAt(i)));
            if (sim != null) mStorage.add(sim);
        }
    }

    /** 拆机把存着的女王蜂吐出来, 别吞物品 */
    @Override
    public void onRemoval() {
        super.onRemoval();
        IGregTechTileEntity base = getBaseMetaTileEntity();
        if (base == null || !base.isServerSide()) return;
        for (BeeSim sim : mStorage) {
            EntityItem item = new EntityItem(
                base.getWorld(),
                base.getXCoord(),
                base.getYCoord(),
                base.getZCoord(),
                sim.queen.copy());
            item.delayBeforeCanPickup = 10;
            base.getWorld()
                .spawnEntityInWorld(item);
        }
        mStorage.clear();
    }

    // endregion

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEBeeyonds(mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.bee.0"))
            .addInfo(translateToLocal("ote.tm.bee.1"))
            .addInfo(translateToLocal("ote.tm.bee.2"))
            .addInfo(translateToLocal("ote.tm.bee.3"))
            .addInfo(translateToLocal("ote.tm.bee.4"))
            .addInfo(translateToLocal("ote.tm.bee.5"))
            .addInfo(translateToLocal("ote.tm.bee.6"))
            .addTecTechHatchInfo()
            .addSeparator()
            .beginStructureBlock(23, 22, 5, false)
            .addController(translateToLocal("ote.tn.bee"))
            .addEnergyHatch("Any Plastic Concrete", 1)
            .addInputBus("Any Plastic Concrete", 1)
            .addOutputBus("Any Plastic Concrete", 1)
            .addInputHatch("Any Plastic Concrete", 1)
            .addOutputHatch("Any Plastic Concrete", 1)
            .toolTipFinisher("§a123Technology - Beeyonds");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            return new ITexture[] { TextureFactory.of(BLOCK_PLASCRETE), active
                ? TextureFactory.of(
                    TextureFactory.of(TTMultiblockBase.ScreenON),
                    TextureFactory.builder()
                        .addIcon(TTMultiblockBase.ScreenON)
                        .glow()
                        .build())
                : TextureFactory.of(
                    TextureFactory.of(TTMultiblockBase.ScreenOFF),
                    TextureFactory.builder()
                        .addIcon(TTMultiblockBase.ScreenOFF)
                        .glow()
                        .build()) };
        }
        return new ITexture[] { TextureFactory.of(BLOCK_PLASCRETE) };
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_NOISE;
    }

    @Override
    public boolean isPowerPassButtonEnabled() {
        return true;
    }

    @Override
    public boolean isSafeVoidButtonEnabled() {
        return false;
    }

    @Override
    public boolean isAllowedToWorkButtonEnabled() {
        return true;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }
}
