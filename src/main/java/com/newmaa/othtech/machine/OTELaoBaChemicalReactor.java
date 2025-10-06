package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.utils.Utils;

import bartworks.API.BorosilicateGlass;
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
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTELaoBaChemicalReactor extends OTHMultiMachineBase<OTELaoBaChemicalReactor> {

    public OTELaoBaChemicalReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTELaoBaChemicalReactor(String aName) {
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
                if (GTUtility
                    .areStacksEqual(aGuiStack, GTModHandler.getModItem("123Technology", "MetaItemOTH", 1, 0))) {
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
            translateToLocal("otht.waila.123") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("123Processing")
                + EnumChatFormatting.RESET);
        currentTip.add(
            translateToLocal("otht.waila.mode") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("Mode")
                + EnumChatFormatting.RESET);
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        if (mode == 0 & !$123) {
            return false;
        } else {
            return true;
        }
    }

    public int getMaxParallelRecipes() {
        if (mode == 0 && !$123) {
            return 16;
        } else {
            return Integer.MAX_VALUE;
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
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (mode < 1) {
                mode++;
            } else {
                mode = 0;
            }
            switch (mode) {
                case 0 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.chem.mode.0"));
                case 1 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.chem.mode.1"));
            }
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

    private final int horizontalOffSet = 3;
    private final int verticalOffSet = 12;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTELaoBaChemicalReactor> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.chem.0") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTELaoBaChemicalReactor> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTELaoBaChemicalReactor>builder()
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
                .addElement(
                    'D',
                    withChannel(
                        "coil",
                        ofCoil(OTELaoBaChemicalReactor::setCoilLevel, OTELaoBaChemicalReactor::getCoilLevel)))
                .addElement(
                    'F',
                    buildHatchAdder(OTELaoBaChemicalReactor.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTELaoBaChemicalReactor::addToMachineList)
                        .dot(1)
                        .casingIndex(CASING_TEXTURE_ID)
                        .buildAndChain(sBlockCasings8, 6))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // Structured by LyeeR
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

    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType("§e§l老登的终极造物 - 铑钯反应釜(化学反应釜 & 化工厂)")
     * .addInfo("§l§a    吃饱啦!    ")
     * .addInfo("化工厂模式 ： 耗时 = NEI耗时 * (1 - 线圈等级 * 0.15),最低耗时倍率为0.1; 并行16, 无催化剂消耗, 有损超频")
     * .addInfo("大型化学反应釜模式 : 耗时 = NEI耗时 * (1 - 线圈等级 * 0.1), 最低耗时倍率为0.1; 无限制并行, 无损超频")
     * .addInfo("玻璃等级决定化工厂等级，化工厂等级==玻璃信道等级-3")
     * .addInfo("不成型请重放主机")
     * .addInfo("如遇配方不执行，请放置一个普通输入总线，并将所有输入总线的编程电路调到一致")
     * .addInfo("主机放入铱锇钐合金粉解锁化工厂无损超频及并行限制")
     * .addTecTechHatchInfo()
     * .addSeparator()
     * .addController("反应釜")
     * .beginStructureBlock(7, 13, 7, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .toolTipFinisher("§a123Technology - CHEM-ENDER");
     * return tt;
     * }
     */
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.chem.0"))
            .addInfo(translateToLocal("ote.tm.chem.1"))
            .addInfo(translateToLocal("ote.tm.chem.2"))
            .addInfo(translateToLocal("ote.tm.chem.3"))
            .addInfo(translateToLocal("ote.tm.chem.4"))
            .addInfo(translateToLocal("ote.tm.chem.5"))
            .addInfo(translateToLocal("ote.tm.chem.6"))
            .addInfo(translateToLocal("ote.tm.chem.7"))
            .addTecTechHatchInfo()
            .addSeparator()
            .addController(translateToLocal("ote.tn.chem"))
            .beginStructureBlock(7, 13, 7, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .toolTipFinisher("§a123Technology - Chem-Terminator");
        return tt;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTELaoBaChemicalReactor(this.mName);
    }

    private static ITexture CASING_TEXTURE = null;
    private static final int CASING_TEXTURE_ID = 182;

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (CASING_TEXTURE == null) {
            CASING_TEXTURE = Textures.BlockIcons
                .getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings8, 6));
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

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_DISTILLERY_LOOP;
    }
}
