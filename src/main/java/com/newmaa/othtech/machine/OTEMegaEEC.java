package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_OFF;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_ON;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FUSION1_GLOW;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.kuba6000.mobsinfo.api.utils.FastRandom;
import com.mojang.authlib.GameProfile;
import com.newmaa.othtech.machine.machineclass.OTHMobHandlerLoader;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.utils.ShapeLoader;

import bartworks.API.BorosilicateGlass;
import crazypants.enderio.EnderIO;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatchInputBus;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.material.MaterialsAlloy;
import tectech.thing.block.BlockQuantumGlass;

public class OTEMegaEEC extends OTHMultiMachineBase<OTEMegaEEC> {

    private static final Logger LOG_PREVIEW = LogManager.getLogger("123Technology/MegaEECPreview");

    /** NEI 预览的 DummyWorld；同一个预览世界只整体建造一次 */
    private static World sPreviewBuiltWorld = null;

    private byte glassTier = 0;

    private static final Item poweredSpawnerItem = Item.getItemFromBlock(EnderIO.blockPoweredSpawner);

    public final Random rand = new FastRandom();

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 25;

    private final int verticalOffSet = 55;

    private final int depthOffSet = 26;

    private static IStructureDefinition<OTEMegaEEC> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":",
        translateToLocal("1 - 消声仓, 能源仓, 输入输出总线, 输入输出仓 : 替换分子机械方块, 支持TecTech能源仓") };

    private static final int SHAPE_WIDTH = 50;

    private static final int SHAPE_HEIGHT = 143;

    private static final int SHAPE_LAYERS = 56;

    private static final String[][] shapeMain = ShapeLoader
        .load("/assets/123technology/structures/megaeec_shape.txt", SHAPE_WIDTH, SHAPE_HEIGHT, SHAPE_LAYERS);

    public OTEMegaEEC.EECFakePlayer EECPlayer = null;

    private static class EECFakePlayer extends FakePlayer {

        OTEMegaEEC mte;
        ItemStack currentWeapon;

        public EECFakePlayer(OTEMegaEEC mte) {
            super(
                (WorldServer) mte.getBaseMetaTileEntity()
                    .getWorld(),
                new GameProfile(
                    UUID.nameUUIDFromBytes("[EEC Fake Player]".getBytes(StandardCharsets.UTF_8)),
                    "[EEC Fake Player]"));
            this.mte = mte;
        }

        @Override
        public void renderBrokenItemStack(ItemStack p_70669_1_) {}

        @Override
        public void destroyCurrentEquippedItem() {}

        @Override
        public ItemStack getCurrentEquippedItem() {
            return currentWeapon;
        }

        @Override
        public ItemStack getHeldItem() {
            return currentWeapon;
        }
    }

    // ===================== Constructors =====================

    public OTEMegaEEC(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaEEC(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    // public final Random rand = new FastRandom();
    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("glassTier", glassTier);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        glassTier = aNBT.getByte("glassTier");

    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return 134217728;
    }

    protected float getSpeedBonus() {
        return 1 / 3.5F;
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
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {
        if (getBaseMetaTileEntity().isClientSide()) return CheckRecipeResultRegistry.NO_RECIPE;
        ItemStack aStack = mInventory[1];
        if (aStack == null) return SimpleCheckRecipeResult.ofFailure("EEC_nospawner");

        if (aStack.getItem() != poweredSpawnerItem) return SimpleCheckRecipeResult.ofFailure("EEC_nospawner");

        if (aStack.getTagCompound() == null) return SimpleCheckRecipeResult.ofFailure("EEC_invalidspawner");
        String mobType = aStack.getTagCompound()
            .getString("mobType");
        if (mobType.isEmpty()) return SimpleCheckRecipeResult.ofFailure("EEC_invalidspawner");

        if (mobType.equals("Skeleton") && getBaseMetaTileEntity().getWorld().provider instanceof WorldProviderHell
            && rand.nextInt(5) > 0) mobType = "witherSkeleton";

        OTHMobHandlerLoader.MobEECRecipe recipe = OTHMobHandlerLoader.recipeMap.get(mobType);

        if (recipe == null) return CheckRecipeResultRegistry.NO_RECIPE;
        if (!recipe.recipe.isPeacefulAllowed && this.getBaseMetaTileEntity()
            .getWorld().difficultySetting == EnumDifficulty.PEACEFUL)
            return SimpleCheckRecipeResult.ofFailure("EEC_peaceful");
        {
            double attackDamage = 114514;
            weaponCheck: {
                MTEHatchInputBus inputbus = this.mInputBusses.isEmpty() ? null : this.mInputBusses.get(0);
                if (inputbus != null && !inputbus.isValid()) inputbus = null;
                ItemStack lootingHolder = inputbus == null ? null : inputbus.getStackInSlot(0);
                if (lootingHolder == null) break weaponCheck;
            }
            if (EECPlayer == null) EECPlayer = new OTEMegaEEC.EECFakePlayer(this);
            this.mOutputItems = recipe.generateOutputs(rand, this, attackDamage, 64, false, true);

            EECPlayer.currentWeapon = null;

            this.mOutputFluids = new FluidStack[] { FluidRegistry.getFluidStack("xpjuice", 2147483647) };
            int times = 20;
        }
        if (this.lEUt > 0) this.lEUt = -this.lEUt;
        this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
        this.mEfficiencyIncrease = 10000;
        this.updateSlots();
        return CheckRecipeResultRegistry.SUCCESSFUL;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        // this.addOutput(recipeMap.get();
        if (!super.onRunningTick(aStack)) return false;
        this.updateSlots();
        return true;
    }

    // ===================== Structure =====================

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
        if (this.isPreviewWorld()) {
            World world = this.getBaseMetaTileEntity()
                .getWorld();
            if (sPreviewBuiltWorld != world) {
                sPreviewBuiltWorld = world;
                long t0 = System.nanoTime();
                try {
                    this.buildPiece(
                        STRUCTURE_PIECE_MAIN,
                        stackSize,
                        false,
                        horizontalOffSet,
                        verticalOffSet,
                        depthOffSet);
                } catch (Throwable t) {
                    LOG_PREVIEW.error("MegaEEC 预览整建抛异常", t);
                }
                LOG_PREVIEW.info("MegaEEC 预览整建完成：{} ms", (System.nanoTime() - t0) / 1000000L);
            }
            return 0;
        }
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
    public IStructureDefinition<OTEMegaEEC> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaEEC>builder()
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
                .addElement('B', ofBlock(MaterialsAlloy.BOTMIUM.getBlock(), 0))
                .addElement(
                    'C',
                    buildHatchAdder(OTEMegaEEC.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEMegaEEC::addToMachineList)
                        .hint(1)
                        .casingIndex(12)
                        .buildAndChain(sBlockCasings1, 12))
                .addElement('D', ofBlock(sBlockCasings1, 13))
                .addElement('E', ofBlock(sBlockCasings1, 14))
                .addElement('F', ofBlock(sBlockCasings2, 0))
                .addElement('G', ofBlock(sBlockCasings8, 10))
                .addElement('H', ofBlock(sBlockCasingsSE, 1))
                .addElement('I', ofBlock(sBlockCasingsTT, 4))
                .addElement('J', ofBlock(sBlockCasingsTT, 6))
                .addElement('K', ofBlock(sBlockCasingsTT, 9))
                .addElement('L', ofBlock(EnderIO.blockDarkIronBars, 0))
                .addElement('M', isAir())
                .addElement('N', ofBlock(Blocks.soul_sand, 0))
                .addElement('O', ofBlock(Blocks.obsidian, 0))
                .addElement('R', ofBlock(Blocks.obsidian, 0))
                .addElement('S', ofBlock(Blocks.obsidian, 0))
                .addElement('Q', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .addElement('P', ofBlock(Blocks.obsidian, 0))
                .addElement('T', ofFrame(Materials.Steel))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.eec.0"))
            .addInfo(translateToLocal("ote.tm.eec.1"))
            .addInfo(translateToLocal("ote.tm.eec.2"))
            .addInfo(translateToLocal("ote.tm.eec.3"))
            .addInfo(translateToLocal("ote.tm.eec.4"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - §4EEC")
            .addController(translateToLocal("ote.tn.eec"))
            .beginStructureBlock(50, 143, 56, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEMegaEEC(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 12)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_ON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 12)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_OFF)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_OFF)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings1, 12)) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }

    // ===================== Misc =====================

    /** BlockRenderer6343 的预览世界类名带 Dummy / Observer / Tracked（与迪普西科同一套判据）。 */
    private boolean isPreviewWorld() {
        IGregTechTileEntity base = this.getBaseMetaTileEntity();
        if (base == null || base.getWorld() == null) return false;
        String name = base.getWorld()
            .getClass()
            .getName();
        return name.contains("Dummy") || name.contains("Observer") || name.contains("Tracked");
    }
}
