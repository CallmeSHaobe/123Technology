package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.GregTechAPI.sBlockCasings8;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.utils.Utils;

import bartworks.API.BorosilicateGlass;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEMegaIsaFactory extends OTHMultiMachineBase<OTEMegaIsaFactory> {

    private int tierMill = 0;

    private byte glassTier = 0;

    private HeatingCoilLevel coilLevel;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 40;

    private final int verticalOffSet = 27;

    private final int depthOffSet = 2;

    private static IStructureDefinition<OTEMegaIsaFactory> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.misa.0") + ":",
        translateToLocal("ote.cm.misa.1") };

    // Structured by Monkey , LyeeR, NewMaa
    private final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEMegaIsaFactory_shapeMain.shape.gz");

    // ===================== Constructors =====================

    public OTEMegaIsaFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaIsaFactory(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("glassTier", glassTier);
        aNBT.setInteger("millTier", tierMill);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        glassTier = aNBT.getByte("glassTier");
        tierMill = aNBT.getInteger("millTier");
    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    public int getMaxParallelRecipes() {
        return 123;
    }

    protected float getSpeedBonus() {
        return (float) 1 / getCoilTier();
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return Recipemaps.MISA;
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 12321;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return super.getMaxEfficiency(aStack);
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return super.getDamageToComponent(aStack);
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return super.isCorrectMachinePart(aStack);
    }

    @Override
    public boolean supportsVoidProtection() {
        return super.supportsVoidProtection();
    }

    @Override
    public boolean supportsInputSeparation() {
        return super.supportsInputSeparation();
    }

    @Override
    public boolean supportsSingleRecipeLocking() {
        return super.supportsSingleRecipeLocking();
    }

    public HeatingCoilLevel getCoilLevel() {
        return this.coilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel coilLevel) {
        this.coilLevel = coilLevel;
    }

    public int getCoilTier() {
        return Utils.getCoilTier(coilLevel);
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
                setEuModifier(Math.pow(glassTier, -0.1D));
                return super.process();
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mSpecialValue > tierMill) {
                    return SimpleCheckRecipeResult.ofFailure("tierMill");
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aTick % 20 == 0) {
            // 先归零再判定，控制器换成别的物品时档位不会残留
            ItemStack controllerSlotItemStack = getControllerSlot();
            tierMill = 0;
            if (controllerSlotItemStack != null) {
                if (GTUtility.areStacksEqual(controllerSlotItemStack, OTHItemList.IsaNEI.get(1))) {
                    tierMill = 1;
                } else if (GTUtility.areStacksEqual(controllerSlotItemStack, OTHItemList.ISAIOS.get(1))) {
                    tierMill = 2;
                } else if (GTUtility.areStacksEqual(controllerSlotItemStack, OTHItemList.ISAHYP.get(1))) {
                    tierMill = 3;
                } else if (GTUtility.areStacksEqual(controllerSlotItemStack, OTHItemList.ISASPE.get(1))) {
                    tierMill = 4;
                }
            }
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
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

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEMegaIsaFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaIsaFactory>builder()
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
                .addElement('B', ofBlock(sBlockCasings4, 0))
                .addElement(
                    'C',
                    withChannel("coil", ofCoil(OTEMegaIsaFactory::setCoilLevel, OTEMegaIsaFactory::getCoilLevel)))
                .addElement('D', ofBlock(sBlockCasings8, 10))
                .addElement('E', ofBlock(ModBlocks.blockCasings3Misc, 1))
                .addElement('F', ofBlock(ModBlocks.blockCasings4Misc, 10))
                .addElement('G', ofBlock(ModBlocks.blockCasings5Misc, 0))
                .addElement('H', ofBlock(ModBlocks.blockCasings5Misc, 1))
                .addElement('I', ofBlock(ModBlocks.blockCasings5Misc, 2))
                .addElement('J', ofBlock(ModBlocks.blockSpecialMultiCasings, 9))
                .addElement(
                    'L',
                    buildHatchAdder(OTEMegaIsaFactory.class).atLeast(Muffler)
                        .adder(OTEMegaIsaFactory::addToMachineList)
                        .hint(2)
                        .casingIndex(TAE.getIndexFromPage(3, 10))
                        .buildAndChain(ModBlocks.blockCasings4Misc, 10))
                .addElement(
                    'M',
                    buildHatchAdder(OTEMegaIsaFactory.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEMegaIsaFactory::addToMachineList)
                        .hint(1)
                        .casingIndex(TAE.GTPP_INDEX(2))
                        .buildAndChain(ModBlocks.blockCasings5Misc, 0))
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
        tt.addMachineType(translateToLocal("ote.tm.misa.0"))
            .addInfo(translateToLocal("ote.tm.misa.1"))
            .addInfo(translateToLocal("ote.tm.misa.2"))
            .addInfo(translateToLocal("ote.tm.misa.3"))
            .addInfo(translateToLocal("ote.tm.misa.4"))
            .addInfo(translateToLocal("ote.tm.misa.5"))
            .addInfo(translateToLocal("ote.tm.misa.6"))
            .addInfo(translateToLocal("ote.tm.misa.7"))
            .addInfo(translateToLocal("ote.tm.misa.8"))
            .addPollutionAmount(12321)
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - One Last ISA")
            .addController(translateToLocal("ote.tn.misa"))
            .addInputBus(translateToLocal("oth.hint.any_input_bus"), 1)
            .addOutputBus(translateToLocal("oth.hint.any_output_bus"), 1)
            .addInputHatch(translateToLocal("oth.hint.any_input_hatch"), 1)
            .addOutputHatch(translateToLocal("oth.hint.any_output_hatch"), 1)
            .addEnergyHatch(translateToLocal("oth.hint.any_energy_hatch"), 1)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEMegaIsaFactory(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(2)),
                TextureFactory.builder()
                    .addIcon(OVERLAY_DTPF_ON)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(2)),
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
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.GTPP_INDEX(2)) };
    }

    // ===================== Misc =====================

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String bonus = GTUtility.formatShortenedLong(tierMill);
            tag.setString("tierMill", bonus);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("otht.waila.isa.tier") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.DARK_GREEN
                + tag.getString("tierMill")
                + EnumChatFormatting.RESET);
    }
}
