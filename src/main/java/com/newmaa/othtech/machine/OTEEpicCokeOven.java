package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.newmaa.othtech.utils.Utils.metaItemEqual;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Mods.Chisel;
import static gregtech.api.enums.Mods.TinkerConstruct;
import static gregtech.api.util.GTStructureUtility.*;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.StructureError;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings2;

public class OTEEpicCokeOven extends OTHMultiMachineBase<OTEEpicCokeOven> {

    public int amountCoal = 0;

    public int multiCoal = 1;

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 48;

    private final int verticalOffSet = 5;

    private final int depthOffSet = 48;

    private static IStructureDefinition<OTEEpicCokeOven> STRUCTURE_DEFINITION = null;

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.cocc.0") };

    // Structured by NewMaa
    private final String[][] shapeMain = com.newmaa.othtech.utils.ShapeLoader
        .loadCompressed("/assets/123technology/structures/ote/OTEEpicCokeOven_shapeMain.shape.gz");

    private static IIconContainer ScreenON;

    // ===================== Constructors =====================

    public OTEEpicCokeOven(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEEpicCokeOven(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("amountCoal", amountCoal);
        aNBT.setInteger("multiCoal", multiCoal);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        aNBT.getInteger("amountCoal");
        aNBT.getInteger("multiCoal");
        super.loadNBTData(aNBT);
    }

    // ===================== Machine Parameters =====================

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    public int getMaxParallelRecipes() {
        return 64;
    }

    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 114514;
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

    // ===================== Processing Logic =====================

    @Override
    public @NotNull CheckRecipeResult checkProcessing() {

        List<ItemStack> inputStacks = getStoredInputs();
        if (inputStacks.isEmpty()) {
            return CheckRecipeResultRegistry.NO_RECIPE;
        }

        for (ItemStack itemStack : inputStacks) {
            int initialStackSize = itemStack.stackSize;

            if (metaItemEqual(itemStack, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Diamond, 1))) {
                amountCoal = 64;
                multiCoal = 16;
            } else if (metaItemEqual(itemStack, new ItemStack(Items.diamond, 1))) {
                amountCoal = 64;
                multiCoal = 16;
            } else if (metaItemEqual(itemStack, GTOreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1))) {
                amountCoal = 16;
                multiCoal = 1;
            } else if (metaItemEqual(itemStack, new ItemStack(Items.coal, 1))) {
                amountCoal = 16;
                multiCoal = 1;
            } else if (metaItemEqual(itemStack, new ItemStack(Blocks.dirt, 1))) {
                amountCoal = 1;
                multiCoal = 1;
            } else {
                continue;
            }
            // Multiply coal Amount -> amountCoal * multiCoal * initialStackSize
            itemStack.stackSize -= initialStackSize;
            ItemStack[] outputItemStack = new ItemStack[] {
                GTModHandler.getModItem("Railcraft", "fuel.coke", amountCoal) };
            @NotNull
            List<ItemStack> extraItems = new ArrayList<>();
            for (ItemStack items : outputItemStack) {
                if (items.stackSize <= Integer.MAX_VALUE / multiCoal * initialStackSize) {
                    // set amount directly if in integer area
                    items.stackSize *= multiCoal * initialStackSize;
                } else {
                    for (int i = 0; i < (multiCoal * initialStackSize) - 1; i++) {
                        extraItems.add(items.copy());
                    }
                }
            }

            if (extraItems.isEmpty()) {
                // no over integer amount
                mOutputItems = outputItemStack;
            } else {
                extraItems.addAll(Arrays.asList(outputItemStack));
                mOutputItems = extraItems.toArray(new ItemStack[] {});
            }

            mEUt = 0;
            mMaxProgresstime = Math.max(200, (1800 * 20 - (amountCoal * initialStackSize * multiCoal) / 5));

            return CheckRecipeResultRegistry.SUCCESSFUL;
        }

        return CheckRecipeResultRegistry.NO_RECIPE;
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
    public IStructureDefinition<OTEEpicCokeOven> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEEpicCokeOven>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement(
                    'A',
                    buildHatchAdder(OTEEpicCokeOven.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Muffler)
                        .adder(OTEEpicCokeOven::addToMachineList)
                        .hint(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement('B', ofFrame(Materials.Steel))
                .addElement('C', ofBlock(GregTechAPI.sBlockCasings3, 13))
                .addElement(
                    'D',
                    ofBlock(Objects.requireNonNull(Block.getBlockFromName(TinkerConstruct.ID + ":GlassBlock")), 0))
                .addElement(
                    'F',
                    Chisel.isModLoaded()
                        ? ofBlock(Objects.requireNonNull(Block.getBlockFromName(Chisel.ID + ":brickCustom")), 2)
                        : ofBlock(Blocks.brick_block, 0))
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
        tt.addMachineType(translateToLocal("ote.tm.cocc.0"))
            .addInfo(translateToLocal("ote.tm.cocc.1"))
            .addInfo(translateToLocal("ote.tm.cocc.2"))
            .addInfo(translateToLocal("ote.tm.cocc.3"))
            .addInfo(translateToLocal("ote.tm.cocc.4"))
            .addInfo(translateToLocal("ote.tm.cocc.5"))
            .addInfo(translateToLocal("ote.tm.cocc.6"))
            .addPollutionAmount(114514)
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - CoccOven")
            .addController(translateToLocal("ote.tn.cocc"))
            .beginStructureBlock(97, 11, 97, false)
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
        return new OTEEpicCokeOven(this.mName);
    }

    // ===================== Textures =====================

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
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenON = Textures.BlockIcons.custom("iconsets/GODFORGE_CONTROLLER");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    protected SoundResource getProcessStartSound() {
        return SoundResource.GT_MACHINES_GOD_FORGE_LOOP;
    }
}
