package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.GregTech_API.*;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GT_StructureUtility.ofCoil;
import static gregtech.api.util.GT_StructureUtility.ofFrame;

import java.util.Arrays;
import java.util.Collection;
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

import com.github.bartimaeusnek.bartworks.API.BorosilicateGlass;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.Utils.Utils;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTH_processingLogics.OTH_ProcessingLogic;

import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_HatchElementBuilder;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.common.blocks.GT_Block_Casings8;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class GT_TE_CHEM extends OTH_MultiMachineBase<GT_TE_CHEM> {

    public GT_TE_CHEM(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_CHEM(String aName) {
        super(aName);
    }

    private boolean $123 = false;

    private byte mode = 0;
    public byte glassTier = 0;

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
                if (GT_Utility.areStacksEqual(aGuiStack, GT_ModHandler.getModItem("123Technology", "dustIrOsSm", 1))) {
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
        aNBT.setByte("mode", mode);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        $123 = aNBT.getBoolean("123");
        glassTier = aNBT.getByte("glassTier");
        mode = aNBT.getByte("mode");
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
            tag.setInteger("Mode", mode);
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
            "Mode" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("Mode")
                + EnumChatFormatting.RESET);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        if (mode == 0 & $123 == false) {
            return false;
        } else {
            return true;
        }
    }

    protected int getMaxParallelRecipes() {
        if (mode == 0) {
            return 16;
        } else {
            return 64;
        }
    }

    protected float getSpeedBonus() {
        if (mode == 1) {
            return Math.max(0.1F, 1 - getCoilTier() * 0.1F);
        } else {
            return Math.max(0.1F, 1 - getCoilTier() * 0.15F);
        }
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (mode < 1) {
                mode++;
            } else {
                mode = 0;
            }
            GT_Utility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal(mode == 1 ? "大型化学反应釜模式" : mode == 0 ? "化工厂模式" : "Null"));
        }
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (mode == 0) return GTPPRecipeMaps.chemicalPlantRecipes;
        return RecipeMaps.multiblockChemicalReactorRecipes;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(GTPPRecipeMaps.chemicalPlantRecipes, RecipeMaps.multiblockChemicalReactorRecipes);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {

        return new OTH_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 2 : 1, 2);
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GT_Recipe recipe) {
                if (recipe.mSpecialValue > glassTier - 3) {
                    return SimpleCheckRecipeResult.ofFailure("casingErr");
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        coilLevel = HeatingCoilLevel.None;
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
    private static IStructureDefinition<GT_TE_CHEM> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_CHEM> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_CHEM>builder()
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
                .addElement('D', withChannel("coil", ofCoil(GT_TE_CHEM::setCoilLevel, GT_TE_CHEM::getCoilLevel)))
                .addElement(
                    'F',
                    GT_HatchElementBuilder.<GT_TE_CHEM>builder()
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(GT_TE_CHEM::addToMachineList)
                        .dot(1)
                        .casingIndex(((GT_Block_Casings8) GregTech_API.sBlockCasings8).getTextureIndex(6))
                        .buildAndChain(sBlockCasings8, 6))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structure by LyeeR
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
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§e§l老登的终极造物 - 铑钯反应釜(化学反应釜 & 化工厂)")
            .addInfo("§l§a    吃饱啦!    ")
            .addInfo("化工厂模式 ： 耗时 = NEI耗时 * (1 - 线圈等级 * 0.15),最低耗时倍率为0.1; 并行16, 无催化剂消耗, 有损超频")
            .addInfo("大型化学反应釜模式 : 耗时 = NEI耗时 * (1 - 线圈等级 * 0.1), 最低耗时倍率为0.1; 并行64, 无损超频")
            .addInfo("glass信道值决定化工厂等级")
            .addInfo("主机放入铱锇钐合金粉解锁化工厂无损超频")
            .addInfo("§q支持§bTecTech§q能源仓及激光仓，但不支持无线电网直接供给EU")
            .addSeparator()
            .addController("反应釜")
            .beginStructureBlock(7, 13, 7, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - CHEM-ENDER");
        return tt;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
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
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
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

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_TE_CHEM(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GT_Utility.getCasingTextureIndex(sBlockCasings8, 6)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GT_Utility.getCasingTextureIndex(sBlockCasings8, 6)),
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
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(GT_Utility.getCasingTextureIndex(GregTech_API.sBlockCasings8, 6)) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }
}
