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
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
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
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
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
    // From MTEMultiFurnace.java FAILED
    /*
     * @Override
     * @NotNull
     * public CheckRecipeResult checkProcessing() {
     * if (this.IS_FURNACE_MODE) {
     * List <ItemStack>tInput = getAllStoredInputs();
     * long availableEUt = GTUtility.roundUpVoltage(getMaxInputVoltage());
     * for (ItemStack i : tInput) {
     * if (availableEUt < RECIPE_EUT) {
     * return CheckRecipeResultRegistry.insufficientPower(RECIPE_EUT);
     * }
     * if (tInput.isEmpty()) {
     * return CheckRecipeResultRegistry.NO_RECIPE;
     * }
     * }
     * OverclockCalculator calculator = new OverclockCalculator().setEUt(availableEUt)
     * .setRecipeEUt(RECIPE_EUT)
     * .setDuration(128)
     * .setParallel(16);
     * int currentParallel = (int) Math.min(getMaxParallelRecipes(), availableEUt / RECIPE_EUT);
     * int itemParallel = 0;
     * for (ItemStack item : tInput) {
     * ItemStack smeltedOutput = GTModHandler.getSmeltingOutput(item, false, null);
     * if (smeltedOutput != null) {
     * int parallelsLeft = currentParallel - itemParallel;
     * if (parallelsLeft <= 0) break;
     * itemParallel += Math.min(item.stackSize, parallelsLeft);
     * }
     * }
     * currentParallel = itemParallel;
     * if (currentParallel <= 0) {
     * return CheckRecipeResultRegistry.NO_RECIPE;
     * }
     * if (currentParallel <= 0) {
     * return CheckRecipeResultRegistry.NO_RECIPE;
     * }
     * // Copy the getItemOutputSlots as to not mutate the output busses' slots.
     * List<ItemStack> outputSlots = new ArrayList<>();
     * for (ItemStack stack : getItemOutputSlots(null)) {
     * if (stack != null) {
     * outputSlots.add(stack.copy());
     * } else {
     * outputSlots.add(null);
     * }
     * }
     * boolean hasMEOutputBus = false;
     * for (final MTEHatch bus : validMTEList(mOutputBusses)) {
     * if (bus instanceof MTEHatchOutputBusME meBus) {
     * if (!meBus.isLocked() && meBus.canAcceptItem()) {
     * hasMEOutputBus = true;
     * break;
     * }
     * }
     * }
     * // Consume items and generate outputs
     * ArrayList<ItemStack> smeltedOutputs = new ArrayList<>();
     * int toSmelt = currentParallel;
     * for (ItemStack item : tInput) {
     * ItemStack smeltedOutput = GTModHandler.getSmeltingOutput(item, false, null);
     * if (smeltedOutput != null) {
     * int maxOutput = 0;
     * int remainingToSmelt = Math.min(toSmelt, item.stackSize);
     * if (hasMEOutputBus) {
     * // Has an unlocked ME Output Bus and therefore can always fit the full stack
     * maxOutput = remainingToSmelt;
     * } else {
     * // Calculate how many of this output can fit in the output slots
     * int needed = remainingToSmelt;
     * ItemStack outputType = smeltedOutput.copy();
     * outputType.stackSize = 1;
     * for (int i = 0; i < outputSlots.size(); i++) {
     * ItemStack slot = outputSlots.get(i);
     * if (slot == null) {
     * // Empty slot: can fit a full stack
     * int canFit = Math.min(needed, outputType.getMaxStackSize());
     * ItemStack newStack = outputType.copy();
     * newStack.stackSize = canFit;
     * outputSlots.set(i, newStack); // Fill the slot
     * maxOutput += canFit;
     * needed -= canFit;
     * } else if (slot.isItemEqual(outputType)) {
     * int canFit;
     * // Check for locked ME Output bus
     * if (slot.stackSize == 65) {
     * canFit = needed;
     * } else {
     * // Same type: can fit up to max stack size
     * int space = outputType.getMaxStackSize() - slot.stackSize;
     * canFit = Math.min(needed, space);
     * }
     * slot.stackSize += canFit;
     * maxOutput += canFit;
     * needed -= canFit;
     * // No need to set, since slot is a reference
     * }
     * if (needed <= 0) break;
     * }
     * }
     * // If void protection is enabled, only process what fits
     * int toProcess = protectsExcessItem() ? maxOutput : remainingToSmelt;
     * if (toProcess > 0) {
     * ItemStack outputStack = smeltedOutput.copy();
     * outputStack.stackSize *= toProcess;
     * smeltedOutputs.add(outputStack);
     * item.stackSize -= toProcess;
     * toSmelt -= toProcess;
     * if (toSmelt <= 0) break;
     * }
     * }
     * }
     * if (smeltedOutputs.isEmpty()) {
     * return CheckRecipeResultRegistry.NO_RECIPE;
     * }
     * this.mOutputItems = smeltedOutputs.toArray(new ItemStack[0]);
     * this.mEfficiency = 10000 - (getIdealStatus() - getRepairStatus()) * 1000;
     * this.mEfficiencyIncrease = 10000;
     * this.mMaxProgresstime = (int) (calculator.getDuration());
     * this.lEUt = calculator.getConsumption();
     * if (this.lEUt > 0) {
     * this.lEUt = -this.lEUt;
     * }
     * this.updateSlots();
     * return CheckRecipeResultRegistry.SUCCESSFUL;
     * }
     * return doCheckRecipe();
     * }
     */

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
        switch (mInternalMode){
            case 0 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.s9in1.mode.0"));
            case 1 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.s9in1.mode.1"));
            case 2 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.s9in1.mode.2"));
        }
        /*
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal(
                mInternalMode == 0 ? translateToLocal("ote.tm.s9in1.mode.0")
                    : mInternalMode == 1 ? translateToLocal("ote.tm.s9in1.mode.1")
                        : mInternalMode == 2 ? translateToLocal("ote.tm.s9in1.mode.2") : "null"));

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
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 消声仓, 蒸汽输入仓, 输入输出总线, 输入输出仓 : 替换镀铜砖块"),
        EnumChatFormatting.LIGHT_PURPLE + translateToLocal("这便是蒸汽时代了吗？."),
        EnumChatFormatting.GOLD + translateToLocal("天无二日, 九合一便是我们心中唯一的太阳") };

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
