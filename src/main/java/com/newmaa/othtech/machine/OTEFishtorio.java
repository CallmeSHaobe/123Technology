package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static net.minecraft.util.StatCollector.translateToLocal;

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
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import crazypants.enderio.EnderIO;
import gregtech.api.enums.TAE;
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
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class OTEFishtorio extends OTHMultiMachineBase<OTEFishtorio> {
    // TODO 憋憋渔场.

    public OTEFishtorio(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEFishtorio(String aName) {
        super(aName);
    }

    public boolean mode = false;

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
    @NotNull
    public CheckRecipeResult checkProcessing() {

        setupProcessingLogic(processingLogic);

        if (!mode) {
            CheckRecipeResult result = doCheckRecipe();
            result = postCheckRecipe(result, processingLogic);
            updateSlots();
            if (!result.wasSuccessful()) return result;

            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = processingLogic.getDuration();
            mOutputItems = processingLogic.getOutputItems();
            mOutputFluids = processingLogic.getOutputFluids();
            setEnergyUsage(processingLogic);
        } else {
            mEUt = 114;
            mMaxProgresstime = 100;

            ItemStack soulVessel = null;
            ItemStack brokenSpawner = null;

            for (ItemStack soul : getAllStoredInputs()) {
                if (soul.getItem() == EnderIO.itemSoulVessel) {
                    soulVessel = soul;
                    break;
                }
            }

            for (ItemStack spawner : getAllStoredInputs()) {
                if (spawner.getItem() == EnderIO.itemBrokenSpawner) {
                    brokenSpawner = spawner;
                    break;
                }
            }

            if (soulVessel == null || brokenSpawner == null) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }

            NBTTagCompound soulTag = soulVessel.getTagCompound();
            if (soulTag == null || !soulTag.hasKey("id")) {
                return CheckRecipeResultRegistry.NO_RECIPE;
            }

            String mobType = soulTag.getString("id");
            ItemStack outputItem = brokenSpawner.copy();
            NBTTagCompound spawnerTag = new NBTTagCompound();
            spawnerTag.setString("mobType", mobType);
            outputItem.setTagCompound(spawnerTag);
            outputItem.stackSize = 1;

            mOutputItems = new ItemStack[] { outputItem, new ItemStack(EnderIO.itemSoulVessel) };
            soulVessel.stackSize--;
            brokenSpawner.stackSize--;

            updateSlots();
            return CheckRecipeResultRegistry.SUCCESSFUL;
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
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
        GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal(mode ? "刷怪笼绑定模式启动" : "刷怪笼绑定模式关闭"));
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 3;
    private final int verticalOffSet = 3;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTEFishtorio> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":",
        translateToLocal("1 - 输入输出总线, 输入输出仓, 能源仓 : 替换渔场机械方块, 支持TecTech能源仓") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEFishtorio> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEFishtorio>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by LyeeR
    private final String[][] shapeMain = new String[][] { {} };

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
