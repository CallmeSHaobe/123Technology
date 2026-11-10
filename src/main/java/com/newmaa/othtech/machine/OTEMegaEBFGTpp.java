package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.activeCoils;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.utils.Utils;

import bartworks.API.BorosilicateGlass;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchEnergy;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.misc.GTStructureChannels;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class OTEMegaEBFGTpp extends OTHMultiMachineBase<OTEMegaEBFGTpp> {

    private HeatingCoilLevel coilLevel;

    private int mHeatingCapacity = 0;

    private byte glassTier;

    private boolean lava = false;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.eec.0") };

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 7;

    private final int verticalOffSet = 17;

    private final int depthOffSet = 0;

    private static IStructureDefinition<OTEMegaEBFGTpp> STRUCTURE_DEFINITION = null;

    // Structured By BW
    private final String[][] shapeMain = new String[][] {
        { "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBDBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAA~AAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "AAAAAAAAAAAAAAA", "ACCCCCCCCCCCCCA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "AC           CA", "AC           CA",
            "AC           CA", "AC           CA", "AC           CA", "ACCCCCCCCCCCCCA", "AAAAAAAAAAAAAAA" },
        { "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB",
            "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBBB" } };

    // ===================== Constructors =====================

    public OTEMegaEBFGTpp(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaEBFGTpp(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setByte("glass", glassTier);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        glassTier = aNBT.getByte("glass");
        super.loadNBTData(aNBT);
    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    public int getMaxParallelRecipes() {
        return 256 * 8;
    }

    protected float getSpeedBonus() {
        return 1F / 2.2F;
    }

    protected float getEuModifier() {
        return 0.9F;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.blastFurnaceRecipes;

    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 500 * 256;
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
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return recipe.mSpecialValue <= coilLevel.getHeat() & lava ? CheckRecipeResultRegistry.SUCCESSFUL
                    : SimpleCheckRecipeResult.ofFailure("nowater");
            }

            @NotNull
            @Override
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                return super.createOverclockCalculator(recipe).setHeatOC(true)
                    .setMachineHeat(mHeatingCapacity)
                    .setHeatDiscount(true)
                    .setRecipeHeat(recipe.mSpecialValue);
            }

            @NotNull
            @Override
            public CheckRecipeResult process() {
                setEuModifier(0.9F);
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);

    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        this.mHeatingCapacity = 0;
        if (!this.checkPiece(STRUCTURE_PIECE_MAIN, 7, 17, 0, errors) || this.getCoilLevel() == HeatingCoilLevel.None)
            return;
        if (this.glassTier < 8) {
            for (MTEHatch hatch : this.mExoticEnergyHatches) {
                if (hatch.getConnectionType() == MTEHatch.ConnectionType.LASER) {
                    return;
                }
                if (this.glassTier < hatch.mTier) {
                    return;
                }
            }
            for (MTEHatchEnergy mEnergyHatch : this.mEnergyHatches) {
                if (this.glassTier < mEnergyHatch.mTier) {
                    return;
                }
            }
        }
        if (checkForWater()) {
            this.lava = true;
        }
        this.mHeatingCapacity = ((int) getCoilLevel().getHeat());
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        this.buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
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
    public IStructureDefinition<OTEMegaEBFGTpp> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaEBFGTpp>builder()
                .addShape(STRUCTURE_PIECE_MAIN, transpose(shapeMain))
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
                .addElement(
                    'B',
                    buildHatchAdder(OTEMegaEBFGTpp.class)
                        .atLeast(Energy.or(ExoticEnergy), InputBus, OutputBus, InputHatch, OutputHatch)
                        .adder(OTEMegaEBFGTpp::addToMachineList)
                        .hint(1)
                        .casingIndex(TAE.getIndexFromPage(2, 11))
                        .buildAndChain(ofBlock(ModBlocks.blockCasings3Misc, 11)))
                .addElement(
                    'C',
                    GTStructureChannels.HEATING_COIL
                        .use(activeCoils(ofCoil(OTEMegaEBFGTpp::setCoilLevel, OTEMegaEBFGTpp::getCoilLevel))))
                .addElement(
                    'D',
                    buildHatchAdder(OTEMegaEBFGTpp.class).atLeast(Muffler)
                        .adder(OTEMegaEBFGTpp::addToMachineList)
                        .hint(2)
                        .casingIndex(TAE.getIndexFromPage(2, 11))
                        .buildAndChain(ofBlock(ModBlocks.blockCasings3Misc, 11)))
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
        tt.addMachineType(translateToLocal("ote.tm.mebf.0"))
            .addInfo(translateToLocal("ote.tm.mebf.1"))
            .addInfo(translateToLocal("ote.tm.mebf.2"))
            .addInfo(translateToLocal("ote.tm.mebf.3"))
            .addInfo(translateToLocal("ote.tm.mebf.4"))
            .addInfo(translateToLocal("ote.tm.mebf.5"))
            .addInfo(translateToLocal("ote.tm.mebf.6"))
            .addInfo(translateToLocal("ote.tm.mebf.7"))
            .addInfo(translateToLocal("oth.tm.pollution"))
            .addPollutionAmount(128000)
            .addTecTechHatchInfo()
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - EBF")
            .addController(translateToLocal("ote.tn.mebf"))
            .beginStructureBlock(15, 20, 15, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 2)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEMegaEBFGTpp(this.mName);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced_Active)
                    .extFacing()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(TAE.getIndexFromPage(2, 11)) };
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_ADV_EBF_LOOP;
    }

    // ===================== Misc =====================

    public boolean checkForWater() {
        if (this.getStoredFluids() != null) {
            for (FluidStack stored : this.getStoredFluids()) {
                if (stored.isFluidEqual(FluidUtils.getWildcardFluidStack("pyrotheum", 1))) {
                    if (stored.amount >= 123 * 1000) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
// spotless:on
