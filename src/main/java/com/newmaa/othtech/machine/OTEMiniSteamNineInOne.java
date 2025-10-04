package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockCasings2;
import static gregtech.api.GregTechAPI.sBlockCasings3;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.ItemList.Circuit_Integrated;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_BRONZE_SIDE;
import static gregtech.api.util.GTStructureUtility.*;
import static gregtech.api.util.GTUtility.filterValidMTEs;
import static gregtech.api.util.GTUtility.validMTEList;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.utils.RecipeBuilder;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IItemLockable;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.metatileentity.implementations.MTEHatchOutput;
import gregtech.api.metatileentity.implementations.MTEHatchOutputBus;
import gregtech.api.metatileentity.implementations.MTEHatchVoidBus;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
import gregtech.common.tileentities.machines.MTEHatchOutputBusME;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTESteamMultiBase;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class OTEMiniSteamNineInOne extends MTESteamMultiBase<OTEMiniSteamNineInOne> implements ISurvivalConstructable {

    public OTEMiniSteamNineInOne(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMiniSteamNineInOne(String aName) {
        super(aName);
    }

    @Override
    public String getMachineType() {
        return null;
    }

    private boolean IS_FURNACE_MODE = false;
    private int mCountCasing = 0;
    private static final long RECIPE_EUT = 4;
    protected int mInternalMode = 0;
    private static final int MODE_PUMP = 0;
    private static final int MODE_ALLOY_FURNACE = 1;
    private static final int MODE_EXTRACTOR = 2;
    private static final int MODE_ORE_WASHING = 3;
    private static final int MODE_CRACKING = 4;
    private static final int MODE_HAMMER = 5;
    private static final int MODE_COMPRESSOR = 6;
    private static final int MODE_MIXER = 7;
    private static final int MODE_CENTRIFUGE = 8;
    private static final int[][] MODE_MAP = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("mInternalMode", mInternalMode);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        this.mInternalMode = aNBT.getInteger("mInternalMode");

    }

    @Override
    public boolean addOutput(ItemStack aStack) {
        if (GTUtility.isStackInvalid(aStack)) return false;
        aStack = GTUtility.copyOrNull(aStack);

        final List<MTEHatchOutputBus> validBusses = filterValidMTEs(mOutputBusses);
        if (dumpItem(validBusses, aStack, true, false)) return true;
        if (dumpItem(validBusses, aStack, false, false)) return true;

        boolean outputSuccess = true;
        final List<MTEHatchOutput> filteredHatches = filterValidMTEs(mOutputHatches);
        while (outputSuccess && aStack.stackSize > 0) {
            outputSuccess = false;
            ItemStack single = aStack.splitStack(1);
            for (MTEHatchOutput tHatch : filteredHatches) {
                if (!outputSuccess && tHatch.outputsItems()) {
                    if (tHatch.getBaseMetaTileEntity()
                        .addStackToSlot(1, single)) outputSuccess = true;
                }
            }
        }
        return outputSuccess;
    }

    public ArrayList<ItemStack> getStoredOutputs() {
        ArrayList<ItemStack> rList = new ArrayList<>();
        for (MTEHatchOutputBus tHatch : validMTEList(mOutputBusses)) {
            IGregTechTileEntity baseMetaTileEntity = tHatch.getBaseMetaTileEntity();
            for (int i = baseMetaTileEntity.getSizeInventory() - 1; i >= 0; i--) {
                rList.add(baseMetaTileEntity.getStackInSlot(i));
            }
        }
        return rList;
    }

    @Override
    public List<ItemStack> getItemOutputSlots(ItemStack[] toOutput) {
        List<ItemStack> ret = new ArrayList<>();
        for (final MTEHatch tBus : validMTEList(mOutputBusses)) {
            if (!(tBus instanceof MTEHatchOutputBusME meBus)) {
                final IInventory tBusInv = tBus.getBaseMetaTileEntity();
                for (int i = 0; i < tBusInv.getSizeInventory(); i++) {
                    final ItemStack stackInSlot = tBus.getStackInSlot(i);

                    if (stackInSlot == null && tBus instanceof IItemLockable lockable && lockable.isLocked()) {
                        // getItemOutputSlots is only used to calculate free room for the purposes of parallels and
                        // void protection. We can use a fake item stack here without creating weirdness in the output
                        // bus' actual inventory.
                        assert lockable.getLockedItem() != null;
                        ItemStack fakeItemStack = lockable.getLockedItem()
                            .copy();
                        fakeItemStack.stackSize = 0;
                        ret.add(fakeItemStack);
                    } else {
                        ret.add(stackInSlot);
                    }
                }
            } else {
                if (meBus.isLocked() && meBus.canAcceptItem()) {
                    for (ItemStack stack : meBus.getLockedItems()) {
                        ItemStack fakeItemStack = stack.copy();
                        fakeItemStack.stackSize = 65;
                        ret.add(fakeItemStack);
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public List<ItemStack> getVoidOutputSlots() {
        List<ItemStack> ret = new ArrayList<>();
        for (final MTEHatch tBus : validMTEList(mOutputBusses)) {
            if (tBus instanceof MTEHatchVoidBus vBus && vBus.isLocked()) {
                for (ItemStack lockedItem : vBus.getLockedItems()) {
                    if (lockedItem == null) continue;
                    ret.add(lockedItem.copy());
                }
            }
        }
        return ret;
    }

    @Override
    public int getMaxParallelRecipes() {
        return 16;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return null;
    }

    protected float getEuModifier() {
        return 1F;
    }

    private ItemStack getCircuit(ItemStack[] t) {
        for (ItemStack j : t) {
            if (j.getItem() == Circuit_Integrated.getItem()) {
                if (j.getItemDamage() >= 20 && j.getItemDamage() <= 22) {
                    return j;
                }
            }
        }
        return null;
    }

    private int getCircuitID(ItemStack circuit) {
        int H = circuit.getItemDamage();
        int T = (H == 20 ? 0 : (H == 21 ? 1 : (H == 22 ? 2 : -1)));
        return MODE_MAP[this.mInternalMode][T];
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(
            Recipemaps.STEAM_PUMP,
            RecipeMaps.alloySmelterRecipes,
            RecipeMaps.extractorRecipes,
            RecipeMaps.oreWasherRecipes,
            RecipeMaps.maceratorRecipes,
            RecipeMaps.hammerRecipes,
            RecipeMaps.compressorRecipes,
            GTPPRecipeMaps.mixerNonCellRecipes,
            GTPPRecipeMaps.centrifugeNonCellRecipes);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            private ItemStack lastCircuit = null;

            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(getEuModifier());
                setSpeedBonus(0.85f);
                return super.process();
            }

            @Nonnull
            @Override
            protected Stream<GTRecipe> findRecipeMatches(@Nullable RecipeMap<?> map) {
                ItemStack circuit = getCircuit(inputItems);
                if (circuit == null) {
                    return Stream.empty();
                }
                if (!GTUtility.areStacksEqual(circuit, lastCircuit)) {
                    lastRecipe = null;
                    lastCircuit = circuit;
                }
                RecipeMap<?> foundMap = getRecipeMap(getCircuitID(circuit));
                if (foundMap == null) {
                    return Stream.empty();
                }
                return super.findRecipeMatches(foundMap);
            }

            @Nonnull
            @Override
            protected CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (availableVoltage < recipe.mEUt) {
                    return CheckRecipeResultRegistry.insufficientPower(recipe.mEUt);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setMaxParallelSupplier(this::getMaxParallelRecipes);

    }

    private static RecipeMap<?> getRecipeMap(int aMode) {
        if (aMode == MODE_PUMP) {
            return Recipemaps.STEAM_PUMP;
        } else if (aMode == MODE_ALLOY_FURNACE) {
            return RecipeMaps.alloySmelterRecipes;
        } else if (aMode == MODE_EXTRACTOR) {
            return RecipeMaps.extractorRecipes;
        } else if (aMode == MODE_ORE_WASHING) {
            return RecipeMaps.oreWasherRecipes;
        } else if (aMode == MODE_CRACKING) {
            return RecipeMaps.maceratorRecipes;
        } else if (aMode == MODE_HAMMER) {
            return RecipeMaps.hammerRecipes;
        } else if (aMode == MODE_COMPRESSOR) {
            return RecipeMaps.compressorRecipes;
        } else if (aMode == MODE_MIXER) {
            return GTPPRecipeMaps.mixerNonCellRecipes;
        } else if (aMode == MODE_CENTRIFUGE) {
            return GTPPRecipeMaps.centrifugeNonCellRecipes;
        } else return null;
    }

    @Override
    public void onModeChangeByScrewdriver(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (mInternalMode < 2) {
            mInternalMode++;
        } else {
            mInternalMode = 0;
        }
        switch (mInternalMode) {
            case 0 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.s9in1.mode.0"));
            case 1 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.s9in1.mode.1"));
            case 2 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.s9in1.mode.2"));
        }
        /*
         * GTUtility.sendChatToPlayer(
         * aPlayer,
         * StatCollector.translateToLocal(
         * mInternalMode == 0 ? translateToLocal("ote.tm.s9in1.mode.0")
         * : mInternalMode == 1 ? translateToLocal("ote.tm.s9in1.mode.1")
         * : mInternalMode == 2 ? translateToLocal("ote.tm.s9in1.mode.2") : "null"));
         */
    }

    @Override
    protected ITexture getFrontOverlay() {
        return TextureFactory.builder()
            .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced)
            .extFacing()
            .build();
    }

    @Override
    protected ITexture getFrontOverlayActive() {
        return TextureFactory.builder()
            .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced_Active)
            .extFacing()
            .build();
    }

    @Override
    public int getTierRecipes() {
        return 2;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        if (!checkPiece(STRUCTURE_PIECE_MAIN, horizontalOffSet, verticalOffSet, depthOffSet)) return false;
        if (mCountCasing >= 0 && checkHatches()) {
            updateHatchTexture();
            return true;
        }
        return false;
    }

    protected void updateHatchTexture() {
        for (MTEHatch h : mSteamInputs) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mSteamOutputs) h.updateTexture(getCasingTextureID());
        for (MTEHatch h : mSteamInputFluids) h.updateTexture(getCasingTextureID());
    }

    private int getCasingTextureID() {
        return ((BlockCasings1) GregTechAPI.sBlockCasings1).getTextureIndex(10);
    }

    private boolean checkHatches() {
        return !mSteamInputFluids.isEmpty();

    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 233;
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

    private final int horizontalOffSet = 2;
    private final int verticalOffSet = 1;
    private final int depthOffSet = 0;
    private static IStructureDefinition<OTEMiniSteamNineInOne> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.s9in1.0"),
        EnumChatFormatting.LIGHT_PURPLE + translateToLocal("ote.cm.s9in1.1"),
        EnumChatFormatting.GOLD + translateToLocal("ote.cm.s9in1.2") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEMiniSteamNineInOne> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMiniSteamNineInOne>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', ofBlock(sBlockCasings2, 0))
                .addElement('B', ofBlock(sBlockCasings2, 2))
                .addElement('C', ofBlock(sBlockCasings2, 3))
                .addElement('D', ofBlock(sBlockCasings2, 12))
                .addElement('E', ofBlock(sBlockCasings2, 13))
                .addElement('F', ofBlock(sBlockCasings3, 13))
                .addElement('G', ofBlock(sBlockCasings1, 10))
                .addElement(
                    'H',
                    ofChain(
                        buildSteamInput(OTEMiniSteamNineInOne.class).casingIndex(10)
                            .dot(1)
                            .build(),
                        buildHatchAdder(OTEMiniSteamNineInOne.class)
                            .atLeast(
                                SteamHatchElement.InputBus_Steam,
                                InputHatch,
                                InputBus,
                                SteamHatchElement.OutputBus_Steam,
                                OutputHatch,
                                OutputBus,
                                Muffler)
                            .casingIndex(10)
                            .dot(1)
                            .buildAndChain(),
                        ofBlock(sBlockCasings1, 10)))
                .build();

        }
        return STRUCTURE_DEFINITION;
    }

    // spotless:off
        private final String[][] shapeMain = new String[][]{{
        "AHHHA",
        "GH~HG",
        "AHHHA"
    },{
        "AECEA",
        "FBDBF",
        "AECEA"
    },{
        "AAAAA",
        "FFFFF",
        "AAAAA"
    }};
        @Override
        public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
            return super.addToMachineList(aTileEntity, aBaseCasingIndex)
                || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
        }
        @Override
        protected MultiblockTooltipBuilder createTooltip() {
            final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
            tt.addMachineType(translateToLocal("ote.tm.s9in1.0"))
                .addSeparator()
                .addInfo(translateToLocal("ote.tm.s9in1.1"))
                .addInfo(translateToLocal("ote.tm.s9in1.2"))
                .addInfo(translateToLocal("ote.tm.s9in1.3"))
                .addInfo(translateToLocal("ote.tm.s9in1.4"))
                .addInfo(translateToLocal("ote.tm.s9in1.5"))
                .addInfo(translateToLocal("ote.tm.s9in1.6"))
                .addSeparator()
                .addInfo(translateToLocal("ote.tm.s9in1.7"))
                .addInfo(translateToLocal("ote.tm.s9in1.8"))
                .addInfo(translateToLocal("ote.tm.s9in1.9"))
                .addInfo(translateToLocal("ote.tm.s9in1.10"))
                .addInfo(translateToLocal("ote.tm.s9in1.11"))
                .addInfo(translateToLocal("ote.tm.s9in1.12"))
                .addInfo(translateToLocal("ote.tm.s9in1.13"))
                .addPollutionAmount(233)
                .addSeparator()
                .addController(translateToLocal("ote.tn.s9in1"))
                .beginStructureBlock(49, 7, 49, false)
                .addInputBus("AnyInputBus", 1)
                .addOutputBus("AnyOutputBus", 1)
                .addInputHatch("AnyInputHatch", 1)
                .addOutputHatch("AnyOutputHatch", 1)
                .addEnergyHatch("AnyEnergyHatch", 1)
                .addMufflerHatch("AnyMufflerHatch", 1)
                .toolTipFinisher("§a123Technology - For The Machines of the Past");
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
            return new OTEMiniSteamNineInOne(this.mName);
        }


        @Override
        public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
                                     final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

            if (sideDirection == facing) {
                if (active) return new ITexture[] {
                    TextureFactory.of(MACHINE_BRONZE_SIDE),
                    TextureFactory.builder()
                        .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced_Active)
                        .extFacing()
                        .build() };
                return new ITexture[] {
                    TextureFactory.of(MACHINE_BRONZE_SIDE),
                    TextureFactory.builder()
                        .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced)
                        .extFacing()
                        .glow()
                        .build() };
            }
            return new ITexture[] { TextureFactory.of(MACHINE_BRONZE_SIDE) };
        }
    public static void loadRecipes() {
        RecipeBuilder.builder()
            .itemInputs(
                GTUtility.getIntegratedCircuit(1)
            )
            .fluidOutputs(
                Materials.Water.getFluid(12300)
            )
            .duration(20)
            .eut(30)
            .addTo(Recipemaps.STEAM_PUMP);
    }
}
//spotless:on
