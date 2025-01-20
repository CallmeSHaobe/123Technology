package com.newmaa.othtech.machine;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.item.ItemLoader;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.*;
import gregtech.api.interfaces.IFoodStat;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings2;
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;
import squeek.applecore.api.food.FoodValues;

import java.util.List;
import java.util.Objects;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.Chisel;
import static gregtech.api.enums.Mods.TinkerConstruct;
import static gregtech.api.recipe.RecipeMaps.cannerRecipes;
import static gregtech.api.util.GTRecipeBuilder.SECONDS;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

public class OTEFoodGenerator extends OTH_MultiMachineBase<OTEFoodGenerator> {
    public OTEFoodGenerator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEFoodGenerator(String aName) {
        super(aName);
    }

    public int amountCoal = 0;
    public int multiCoal = 1;
    public int valveFood = 0;
    public int coilTier = 1;
    public int casingTier = 1;

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("valveFood", valveFood);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        valveFood = aNBT.getInteger("valveFood");
        super.loadNBTData(aNBT);
    }
    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
                                int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String bonus = GTUtility.formatNumbers(mEUt);
            tag.setString("bonus", bonus);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
                             IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "发电" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("bonus")
                + EnumChatFormatting.GOLD
                + "EU/t"
                + EnumChatFormatting.RESET);
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
        if (inputStacks == null) {return CheckRecipeResultRegistry.NO_RECIPE;}

        for (ItemStack itemStack : inputStacks){

            int initialStackSize = itemStack.stackSize;
            FoodValues getFoodValues = FoodValues.get(itemStack);
            if (getFoodValues == null) {
                break;
            } else {
                valveFood = getFoodValues.hunger;
                if (initialStackSize <= 64) {
                    mEUt = (int) Math.min(valveFood * coilTier * (Math.pow(casingTier, 4) * initialStackSize), Integer.MAX_VALUE);
                    itemStack.stackSize -= initialStackSize;
                    return CheckRecipeResultRegistry.GENERATING;
                } else {
                    itemStack.stackSize -= 64;
                    mEUt = (int) Math.min(valveFood * coilTier * (Math.pow(casingTier, 4) * getMaxParallelRecipes()), Integer.MAX_VALUE);
                    return CheckRecipeResultRegistry.GENERATING;
                }
            }

        }

        mEfficiency = 10000;
        mProgresstime = 128;
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

    private final int horizontalOffSet = 0;
    private final int verticalOffSet = 1;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTEFoodGenerator> STRUCTURE_DEFINITION = null;
    @Override
    public IStructureDefinition<OTEFoodGenerator> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEFoodGenerator>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'A',
                    buildHatchAdder(OTEFoodGenerator.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Muffler, Dynamo)
                        .adder(OTEFoodGenerator::addToMachineList)
                        .dot(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by NewMaa
    private final String[][] shapeMain = new String[][] {{"A~A"}};
    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 100000;
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("只是一个发电机")
            .addInfo("再也不用担心吃得太饱了")
            .addInfo("丰矿地烧掉一切食物")
            .addInfo("发电:食物饥饿值 * 线圈等级 * 机器外壳等级^4 , 最高发电1A MAX/t , 64并行")
            .addInfo("§c§l注意:机器污染过高:如遇跳电并报错“无法排出污染”, 请尝试放置多个消声仓")
            .addInfo("支持TecTech多安动力舱")
            .addSeparator()
            .addPollutionAmount(100000)
            .addController("发电坤")
            .beginStructureBlock(7, 13, 7, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - FoodGenerator");
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
        return new OTEFoodGenerator(this.mName);
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


}




