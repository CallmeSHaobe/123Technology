package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.IndustrialCraft2;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsNH;

import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.TT_MultiMachineBase_EM;
import com.newmaa.othtech.utils.Utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.MTEHatchDynamo;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
import gregtech.common.blocks.BlockCasings2;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import squeek.applecore.api.food.FoodValues;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;

public class OTEFoodGenerator extends TT_MultiMachineBase_EM implements IConstructable, ISurvivalConstructable {

    public OTEFoodGenerator(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEFoodGenerator(String aName) {
        super(aName);
    }

    public int valveFood = 0;
    public int casingTier = 1;
    private long tStored;
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

    protected int getMaxParallelRecipes() {
        return 64;
    }

    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public @NotNull CheckRecipeResult checkProcessing_EM() {
        List<ItemStack> inputStacks = getStoredInputs();

        if (inputStacks == null || inputStacks.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        for (int i = 0; i < inputStacks.size(); i++) {
            ItemStack itemStack = inputStacks.get(i);

            if (itemStack == null || itemStack.stackSize <= 0) {
                inputStacks.set(i, null);
                continue;
            }

            FoodValues foodValues = FoodValues.get(itemStack);
            if (foodValues == null) {
                continue;
            }

            valveFood = (int) Math.min(Math.pow(foodValues.hunger, 2), Integer.MAX_VALUE);

            long generatedEU = (long) ((long) valveFood * getCoilTier() * itemStack.stackSize);
            mEUt = (int) Math.min(generatedEU, Integer.MAX_VALUE);
            itemStack.stackSize = 0;
            inputStacks.set(i, null);

            updateSlots();

            break;
        }

        inputStacks.removeIf(item -> item == null || item.stackSize <= 0);

        if (mEUt == 0) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        tStored = mEUt;
        mEfficiency = 10000;
        mMaxProgresstime = 1;
        updateSlots();

        return CheckRecipeResultRegistry.GENERATING;
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
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
    public boolean onRunningTick(ItemStack stack) {
        if (tStored > 0) {
            // push eu to dynamo
            for (MTEHatchDynamo eDynamo : super.mDynamoHatches) {
                if (eDynamo == null || !eDynamo.isValid()) {
                    continue;
                }
                final long power = eDynamo.maxEUStore() - eDynamo.getEUVar();
                if (tStored >= power) {
                    eDynamo.setEUVar(eDynamo.getEUVar() + power);
                    tStored -= power;
                } else {
                    eDynamo.setEUVar(eDynamo.getEUVar() + tStored);
                    tStored = 0L;
                }
            }

            for (MTEHatchDynamoMulti eDynamo : eDynamoMulti) {
                if (eDynamo == null || !eDynamo.isValid()) {
                    continue;
                }
                final long power = eDynamo.maxEUStore() - eDynamo.getEUVar();
                if (tStored >= power) {
                    eDynamo.setEUVar(eDynamo.getEUVar() + power);
                    tStored -= power;
                } else {
                    eDynamo.setEUVar(eDynamo.getEUVar() + tStored);
                    tStored = 0L;
                }
            }
        }

        return true;
    }

    public static int getBlockCasings(Block block, int meta) {
        if (block == sBlockCasings1 & block == sBlockCasingsNH) {
            if (meta <= 14) {
                return meta + 1;
            }

        }
        return 0;
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 7;
    private final int verticalOffSet = 12;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTEFoodGenerator> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 输入输出总线, 输入输出仓, 消声仓 : 替换脱氧钢机械方块") + ":", translateToLocal("2 - 动力仓 : 替换尾部脱氧钢机械方块") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEFoodGenerator> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEFoodGenerator>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'C',
                    buildHatchAdder(OTEFoodGenerator.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(OTEFoodGenerator::addToMachineList)
                        .dot(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement(
                    'A',
                    ofBlock(Objects.requireNonNull(Block.getBlockFromName(IndustrialCraft2.ID + ":blockFenceIron")), 0))
                .addElement(
                    'B',
                    withChannel(
                        "casing",
                        ofBlocksTiered(
                            OTEFoodGenerator::getBlockCasings,
                            ImmutableList.of(
                                Pair.of(sBlockCasings1, 1),
                                Pair.of(sBlockCasings1, 2),
                                Pair.of(sBlockCasings1, 3),
                                Pair.of(sBlockCasings1, 4),
                                Pair.of(sBlockCasings1, 5),
                                Pair.of(sBlockCasings1, 6),
                                Pair.of(sBlockCasings1, 7),
                                Pair.of(sBlockCasings1, 8),
                                Pair.of(sBlockCasings1, 9),
                                Pair.of(sBlockCasingsNH, 10),
                                Pair.of(sBlockCasingsNH, 11),
                                Pair.of(sBlockCasingsNH, 12),
                                Pair.of(sBlockCasingsNH, 13),
                                Pair.of(sBlockCasingsNH, 14)),
                            1,
                            (t, meta) -> t.casingTier = meta,
                            t -> t.casingTier)))
                .addElement('D', ofBlock(sBlockCasings3, 14))
                .addElement(
                    'E',
                    withChannel("coil", ofCoil(OTEFoodGenerator::setCoilLevel, OTEFoodGenerator::getCoilLevel)))
                .addElement(
                    'F',
                    buildHatchAdder(OTEFoodGenerator.class).atLeast(Dynamo)
                        .adder(OTEFoodGenerator::addToMachineList)
                        .dot(2)
                        .casingIndex(((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(9))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement('G', ofBlock(Blocks.iron_block, 0))
                .addElement('H', ofBlock(Blocks.wool, 15))
                .addElement('I', ofBlock(Blocks.glass, 0))
                .addElement('J', ofBlock(Blocks.obsidian, 0))
                .addElement('K', ofBlock(sBlockMetal8, 8))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    private final String[][] shapeMain = new String[][] {
        { "               ", "               ", "     BBBBB     ", "   BB     BB   ", "   B       B   ",
            "  B         B  ", "  B         B  ", "  B         B  ", "  B         B  ", "  B         B  ",
            "   B       B   ", "   BB     BB   ", "     CC~CC     ", "               ", "               " },
        { "               ", "    BBBBBBB    ", "   BB     BB   ", "  BB JJJJJ BB  ", " BB J A A J BB ",
            " B JA A A AJ B ", " B J       J B ", " B J       J B ", " B JA     AJ B ", " B JA A A AJ B ",
            " BB J A A J BB ", "  BB JJJJJ BB  ", "   BB     BB   ", "    BBBBBBB    ", "               " },
        { "    BBBBBBB    ", " BBB       BBB ", " BB         BB ", " B           B ", "B    HJJJH    B",
            "B   HJ   JH   B", "B   J     J   B", "B   J     J   B", "B   J     J   B", "B   HJ   JH   B",
            "B    HJJJH    B", " B           B ", " BB         BB ", " BBB       BBB ", "    BBBBBBB    " },
        { " BBBIIIIIIIBBB ", "B             B", "B             B", "B             B", "I     JJJ     I",
            "I    J   J    I", "I   J     J   I", "I   J     J   I", "I   J     J   I", "I    J   J    I",
            "I     JJJ     I", "B             B", "B             B", "B             B", " BBBIIIIIIIBBB " },
        { " BBIIIIIIIIIBB ", "B             B", "B             B", "I             I", "I             I",
            "I    DJJJD    I", "I    J   J    I", "I    J   J    I", "I    J   J    I", "I    DJJJD    I",
            "I             I", "I             I", "B             B", "B             B", " BBIIIIIIIIIBB " },
        { " BIIIIIIIIIIIB ", "B             B", "I             I", "I             I", "I             I",
            "I    DJJJD    I", "I    J   J    I", "I    J   J    I", "I    J   J    I", "I    DJJJD    I",
            "I             I", "I             I", "I             I", "B             B", " BIIIIIIIIIIIB " },
        { "BIIIIIIIIIIIIIB", "I             I", "I             I", "I             I", "I             I",
            "I    DDDDD    I", "I    DHJHD    I", "I    DJKJD    I", "I    DHJHD    I", "I    DDDDD    I",
            "I             I", "I             I", "I             I", "I             I", "BIIIIIIIIIIIIIB" },
        { "BIIIIIIIIIIIIIB", "I             I", "I             I", "I             I", "I             I",
            "I             I", "I             I", "I      K      I", "I             I", "I             I",
            "I             I", "I             I", "I             I", "I             I", "BIIIIIIIIIIIIIB" },
        { "BIIIIIIIIIIIIIB", "I             I", "I             I", "I             I", "I             I",
            "I     EEE     I", "I    E A E    I", "I    E K E    I", "I    E A E    I", "I     EEE     I",
            "I             I", "I             I", "I             I", "I             I", "BIIIIIIIIIIIIIB" },
        { "BIIIIIIIIIIIIIB", "I             I", "I             I", "I             I", "I             I",
            "I             I", "I             I", "I      K      I", "I             I", "I             I",
            "I             I", "I             I", "I             I", "I             I", "BIIIIIIIIIIIIIB" },
        { "BIIIIIIIIIIIIIB", "I             I", "I             I", "I             I", "I             I",
            "I     EEE     I", "I    E A E    I", "I    E K E    I", "I    E A E    I", "I     EEE     I",
            "I             I", "I             I", "I             I", "I             I", "BIIIIIIIIIIIIIB" },
        { "BIIIIIIIIIIIIIB", "I             I", "I             I", "I             I", "I             I",
            "I             I", "I             I", "I      K      I", "I             I", "I             I",
            "I             I", "I             I", "I             I", "I             I", "BIIIIIIIIIIIIIB" },
        { "BIIIIIIIIIIIIIB", "I             I", "I             I", "I             I", "I             I",
            "I     EEE     I", "I    E A E    I", "I    E K E    I", "I    E A E    I", "I     EEE     I",
            "I             I", "I             I", "I             I", "I             I", "BIIIIIIIIIIIIIB" },
        { " BIIIIIIIIIIIB ", "B             B", "I             I", "I             I", "I             I",
            "I             I", "I             I", "I      K      I", "I             I", "I             I",
            "I             I", "I             I", "I             I", "B             B", " BIIIIIIIIIIIB " },
        { " BBIIIIIIIIIBB ", "B             B", "B             B", "I             I", "I             I",
            "I     EEE     I", "I    E A E    I", "I    E K E    I", "I    E A E    I", "I     EEE     I",
            "I             I", "I             I", "B             B", "B             B", " BBIIIIIIIIIBB " },
        { " BBBIIIIIIIBBB ", "B             B", "B             B", "B             B", "I             I",
            "I             I", "I             I", "I      K      I", "I             I", "I             I",
            "I             I", "B             B", "B             B", "B             B", " BBBIIIIIIIBBB " },
        { "    BBBBBBB    ", " BBB       BBB ", " BB         BB ", " B           B ", "B             B",
            "B     EEE     B", "B    E A E    B", "B    E K E    B", "B    E A E    B", "B     EEE     B",
            "B             B", " B           B ", " BB         BB ", " BBB       BBB ", "    BBBBBBB    " },
        { "               ", "    BBBBBBB    ", "   BB     BB   ", "  BB       BB  ", " BB         BB ",
            " B           B ", " B           B ", " B     K     B ", " B           B ", " B           B ",
            " BB         BB ", "  BB       BB  ", "   BB     BB   ", "    BBBBBBB    ", "               " },
        { "               ", "               ", "     BBBBB     ", "    BBBBBBB    ", "   BBB   BBB   ",
            "  BBB     BBB  ", "  BB       BB  ", "  BB   K   BB  ", "  BB       BB  ", "  BBB     BBB  ",
            "   BBB   BBB   ", "    BBBBBBB    ", "     BBBBB     ", "               ", "               " },
        { "               ", "               ", "               ", "               ", "      BBB      ",
            "     BBBBB     ", "    BBGGGBB    ", "    BBGFGBB    ", "    BBGGGBB    ", "     BBBBB     ",
            "      BBB      ", "               ", "               ", "               ", "               " } };

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEFoodGenerator(this.mName);
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
            .addInfo("发电:食物饥饿值^2 * 线圈等级 , 最高发电1A MAX/t , 64并行 , 一次最多烧毁一组相同食物")
            .addInfo("§c§l注意:机器污染过高:如遇跳电并报错“无法排出污染”, 请尝试放置多个消声仓")
            .addInfo("支持TecTech多安动力舱")
            .addInfo("赞美伟大的富婆Safari_xiu吧!没有他就没有现在的123了憋憋。")
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

    private static Textures.BlockIcons.CustomIcon ScreenON;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenON = new Textures.BlockIcons.CustomIcon("123technology:iconSetOTH/LEEK");
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
