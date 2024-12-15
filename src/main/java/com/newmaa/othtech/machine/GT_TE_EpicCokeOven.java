package com.newmaa.othtech.machine;

import static com.newmaa.othtech.Utils.Utils.metaItemEqual;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;

public class GT_TE_EpicCokeOven extends OTH_MultiMachineBase<GT_TE_EpicCokeOven> {

    public GT_TE_EpicCokeOven(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_EpicCokeOven(String aName) {
        super(aName);
    }

    public int amountCoal = 0;

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("amountCoal", amountCoal);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        aNBT.getInteger("amountCoal");
        super.loadNBTData(aNBT);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    protected int getMaxParallelRecipes() {
        return 64;
    }

    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {
        List<ItemStack> inputStacks = getStoredInputs();
        if (inputStacks.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        for (ItemStack itemStack : inputStacks) {
            int initialStackSize = itemStack.stackSize;

            if (metaItemEqual(itemStack, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1))) {
                amountCoal = 4096;
            } else if (metaItemEqual(itemStack, new ItemStack(Items.diamond, 1))) {
                amountCoal = 4096;
            } else if (metaItemEqual(itemStack, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1))) {
                amountCoal = 16;
            } else if (metaItemEqual(itemStack, new ItemStack(Items.coal, 1))) {
                amountCoal = 16;
            } else if (metaItemEqual(itemStack, new ItemStack(Blocks.dirt, 1))) {
                amountCoal = 1;
            } else {
                continue;
            }

            itemStack.stackSize -= initialStackSize;

            mOutputItems = new ItemStack[] {
                GTModHandler.getModItem("Railcraft", "fuel.coke", amountCoal * initialStackSize)
            };

            if (mOutputItems == null) {
                return CheckRecipeResultRegistry.INTERNAL_ERROR;
            }

            mEUt = 0;
            mMaxProgresstime = Math.max(200, (1800 - amountCoal / 100) * 20);

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

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 1;
    private final int verticalOffSet = 1;
    private final int depthOffSet = 0;
    private static IStructureDefinition<GT_TE_EpicCokeOven> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_EpicCokeOven> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_EpicCokeOven>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'A',
                    buildHatchAdder(GT_TE_EpicCokeOven.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(GT_TE_EpicCokeOven::addToMachineList)
                        .dot(1)
                        .casingIndex(6)
                        .buildAndChain(sBlockCasings8, 6))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structure by LyeeR
    private final String[][] shapeMain = new String[][] { { "AAA" }, { "A~A" }, { "AAA" } };

    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 114514;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§d§n小登的终极造物, 中登的期盼之物, 老登的可望不可得之物 - 史诗焦炉")
            .addInfo("§n§c永远不要忽略小登的力量...")
            .addInfo("§o真正的终极机器, 没有高贵的造价, 更没有乱七八糟的机制(")
            .addInfo("§o他所做的, 只有将特定材料烧为焦炭")
            .addInfo("§e转换比例: 钻石(粉)->焦炭 1:4096, 煤炭(粉)->焦炭 1:16， 土->焦炭 1:1(这是怎么做到的?)")
            .addInfo("§e耗时: 1800s - (煤炭产出 / 1000), 耗电: 0EU/t")
            .addPollutionAmount(114514)
            .addSeparator()
            .addController("无敌焦炉")
            .beginStructureBlock(7, 13, 7, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - CoccOven");
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
        return new GT_TE_EpicCokeOven(this.mName);
    }

    private static Textures.BlockIcons.CustomIcon ScreenON;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenON = new Textures.BlockIcons.CustomIcon("iconsets/GODFORGE_CONTROLLER");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)),
                TextureFactory.builder()
                    .addIcon(ScreenON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(ScreenON)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(GregTechAPI.sBlockCasings2, 0)) };
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_GOD_FORGE_LOOP;
    }
}
