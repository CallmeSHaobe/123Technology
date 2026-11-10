package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class _OTEMachinePattern extends OTHMultiMachineBase<_OTEMachinePattern> {

    // ===================== Tuning Constants =====================

    /** 并行数 */
    private static final int MAX_PARALLEL = 1024;

    /** 速度倍率（1F = 100% 耗时） */
    private static final float SPEED_BONUS = 1F;

    /** EU 消耗倍率（1F = 100% 耗电） */
    private static final float EU_MODIFIER = 1F;

    /** 是否无损超频 */
    private static final boolean PERFECT_OVERCLOCK = true;

    /** 每秒污染 */
    private static final int POLLUTION_PER_SECOND = 0;

    // ===================== Constructors =====================

    public _OTEMachinePattern(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public _OTEMachinePattern(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        // 需要持久化的自定义字段在这里 aNBT.setXxx(...)
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        // 与 saveNBTData 一一对应
    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return PERFECT_OVERCLOCK;
    }

    @Override
    public int getMaxParallelRecipes() {
        return MAX_PARALLEL;
    }

    @Override
    protected float getSpeedBonus() {
        return SPEED_BONUS;
    }

    @Override
    protected float getEuModifier() {
        return EU_MODIFIER;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        // TODO: 换成这台机器真正的配方表
        return RecipeMaps.assemblerRecipes;
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return POLLUTION_PER_SECOND;
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTHProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setEuModifier(getEuModifier());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
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

    private final int horizontalOffSet = 1;
    private final int verticalOffSet = 1;
    private final int depthOffSet = 0;

    private static IStructureDefinition<_OTEMachinePattern> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":",
        // 结构说明一行一条，可换成 translateToLocal("ote.cm.<abbr>.<N>")
        "1 - 能源仓 / 输入输出总线 / 输入输出仓 : 替换机械方块" };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<_OTEMachinePattern> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<_OTEMachinePattern>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'A',
                    buildHatchAdder(_OTEMachinePattern.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(_OTEMachinePattern::addToMachineList)
                        .casingIndex(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0))
                        .hint(1)
                        .buildAndChain(ofBlock(GregTechAPI.sBlockCasings2, 0)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    // 示例结构：3x3x3 空心中空，控制器位于正面中心（换正式结构后连同本注释一起替换）
    private final String[][] shapeMain = new String[][] { { "AAA", "A~A", "AAA" }, { "AAA", "A A", "AAA" },
        { "AAA", "AAA", "AAA" } };

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt
            // TODO: 正式机器一律用 translateToLocal("ote.tm.<abbr>.<N>") + 两份 .lang
            .addMachineType("§e§lTODO 机器类型 - Machine Pattern")
            .addInfo("TODO 正文行 1")
            .addInfo("TODO 正文行 2")
            .addPollutionAmount(POLLUTION_PER_SECOND)
            // 支持多安/激光仓用 addTecTechHatchInfo()；只支持多安用 addSupportMultiAmp()；只支持激光用 addSupportLaser()
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - §4MachinePattern")
            .addController("TODO 控制器名称")
            .beginStructureBlock(3, 3, 3, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new _OTEMachinePattern(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        final ITexture casing = Textures.BlockIcons
            .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0));
        if (side == facing) {
            if (aActive) return new ITexture[] { casing, TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casing, TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { casing };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.IC2_MACHINES_COMPRESSOR_OP;
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_THERMAL_CENTRIFUGE_LOOP;
    }

}
