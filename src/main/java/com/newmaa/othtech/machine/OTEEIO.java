package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import crazypants.enderio.EnderIO;
import gregtech.api.GregTechAPI;
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

public class OTEEIO extends OTH_MultiMachineBase<OTEEIO> {

    public OTEEIO(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEEIO(String aName) {
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

    protected int getMaxParallelRecipes() {
        return 64;
    }

    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.EIO;
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

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
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
    private static IStructureDefinition<OTEEIO> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 输入输出总线, 输入输出仓, 能源仓 : 替换魂金块, 支持TecTech能源仓") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEEIO> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEEIO>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
                .addElement('C', ofBlock(Blocks.iron_bars, 0))
                .addElement('A', ofBlock(Blocks.anvil, 0))
                .addElement(
                    'B',
                    buildHatchAdder(OTEEIO.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEEIO::addToMachineList)
                        .dot(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(EnderIO.blockIngotStorage, 7))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by LyeeR
    private final String[][] shapeMain = new String[][] { { "BBBBBBB", "BBBBBBB", "BBBBBBB", "BBBBBBB", "BBBBBBB" },
        { "BCCCCCB", "B     B", "B     B", "B     B", "BBBBBBB" },
        { "BCCCCCB", "B     B", "B     B", "B AAA B", "BBBBBBB" },
        { "BBB~BBB", "BBBBBBB", "BBBBBBB", "BBBBBBB", "BBBBBBB" } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(EnumChatFormatting.WHITE + "§l中登的终极造物 - 末影接口综合体")
            .addInfo(EnumChatFormatting.ITALIC + "怪物的灵魂们终将得到解放")
            .addInfo(EnumChatFormatting.ITALIC + "这就是工业化的魅力吗..?")
            .addInfo(EnumChatFormatting.LIGHT_PURPLE + "使用先进的EU系机器来代替RF系机器, 真是伟大的进步.")
            .addInfo(EnumChatFormatting.LIGHT_PURPLE + "处理EIO一些机器的配方, 如头颅装配机, 灵魂绑定机等.")
            .addInfo(EnumChatFormatting.LIGHT_PURPLE + "执行无损超频, 默认并行64.")
            .addInfo("螺丝刀右键开启刷怪笼绑定模式, 耗电固定114EU/t, 耗时100ticks")
            .addTecTechHatchInfo()
            .addSeparator()
            .addController("EIO")
            .beginStructureBlock(7, 4, 5, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - EnderIO");
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEEIO(this.mName);
    }

    private static ITexture CASING_TEXTURE = null;

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (CASING_TEXTURE == null) {
            CASING_TEXTURE = TextureFactory.of(EnderIO.blockSliceAndSplice, 0);
        }
        if (sideDirection == facing) {
            if (active) return new ITexture[] { CASING_TEXTURE, TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] { CASING_TEXTURE, TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { CASING_TEXTURE };
    }

}
