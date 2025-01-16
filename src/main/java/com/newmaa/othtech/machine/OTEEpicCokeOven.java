package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.newmaa.othtech.Utils.Utils.metaItemEqual;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Mods.Chisel;
import static gregtech.api.enums.Mods.TinkerConstruct;
import static gregtech.api.util.GTStructureUtility.*;

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
import gregtech.common.blocks.BlockCasings2;
import gtPlusPlus.core.block.ModBlocks;

public class OTEEpicCokeOven extends OTH_MultiMachineBase<OTEEpicCokeOven> {

    public OTEEpicCokeOven(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEEpicCokeOven(String aName) {
        super(aName);
    }

    public int amountCoal = 0;
    public int multiCoal = 1;

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

    private final int horizontalOffSet = 48;
    private final int verticalOffSet = 5;
    private final int depthOffSet = 48;
    private static IStructureDefinition<OTEEpicCokeOven> STRUCTURE_DEFINITION = null;

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
                        .dot(1)
                        .casingIndex(((BlockCasings2) GregTechAPI.sBlockCasings2).getTextureIndex(0))
                        .buildAndChain(sBlockCasings2, 0))
                .addElement('B', ofFrame(Materials.Steel))
                .addElement('C', ofBlock(ModBlocks.blockCasingsMisc, 2))
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

    // Structured by NewMaa
    private final String[][] shapeMain = new String[][] {
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                     FFFFFFFFFFFFFFFFFFFFFFF                                     ",
            "                                     FFFFFCCCCCCCCCCCCCFFFFF                                     ",
            "                                     FFFFFCCCCCCCCCCCCCFFFFF                                     ",
            "                                     FFFFFCCCCCCCCCCCCCFFFFF                                     ",
            "                                     FFFFFFFFFFFFFFFFFFFFFFF                                     ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF                                 ",
            "                                 FFFFCCCCCDDDDDDDDDDDDDCCCCCFFFF                                 ",
            "                                 FFFFCCCCCDDDDDDDDDDDDDCCCCCFFFF                                 ",
            "                                 FFFFCCCCCDDDDDDDDDDDDDCCCCCFFFF                                 ",
            "                                 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                              FFFFFFFFFFFF             FFFFFFFFFFFF                              ",
            "                              FFFCCCCDDDDD             DDDDDCCCCFFF                              ",
            "                              FFFCCCCDDDDD             DDDDDCCCCFFF                              ",
            "                              FFFCCCCDDDDD             DDDDDCCCCFFF                              ",
            "                              FFFFFFFFFFFF             FFFFFFFFFFFF                              ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                            FFFFFFFFF                       FFFFFFFFF                            ",
            "                            FFCCCDDDD                       DDDDCCCFF                            ",
            "                            FFCCCDDDD                       DDDDCCCFF                            ",
            "                            FFCCCDDDD                       DDDDCCCFF                            ",
            "                            FFFFFFFFF                       FFFFFFFFF                            ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                          FFFFFFF                               FFFFFFF                          ",
            "                          FFCCCDD                               DDCCCFF                          ",
            "                          FFCCCDD                               DDCCCFF                          ",
            "                          FFCCCDD                               DDCCCFF                          ",
            "                          FFFFFFF                               FFFFFFF                          ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                        FFFFFFF                                   FFFFFFF                        ",
            "                        FFCCCDD                                   DDCCCFF                        ",
            "                        FFCCCDD                                   DDCCCFF                        ",
            "                        FFCCCDD                                   DDCCCFF                        ",
            "                        FFFFFFF                                   FFFFFFF                        ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                       FFFFFF                                       FFFFFF                       ",
            "                       FCCCDD                                       DDCCCF                       ",
            "                       FCCCDD                                       DDCCCF                       ",
            "                       FCCCDD                                       DDCCCF                       ",
            "                       FFFFFF                                       FFFFFF                       ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                     FFFFFF                                           FFFFFF                     ",
            "                     FFCCDD                                           DDCCFF                     ",
            "                     FFCCDD                                           DDCCFF                     ",
            "                     FFCCDD                                           DDCCFF                     ",
            "                     FFFFFF                                           FFFFFF                     ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                    FFFFF                                               FFFFF                    ",
            "                    FCCDD                                               DDCCF                    ",
            "                    FCCDD                                               DDCCF                    ",
            "                    FCCDD                                               DDCCF                    ",
            "                    FFFFF                                               FFFFF                    ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                  FFFFF                                                   FFFFF                  ",
            "                  FFCCD                                                   DCCFF                  ",
            "                  FFCCD                                                   DCCFF                  ",
            "                  FFCCD                                                   DCCFF                  ",
            "                  FFFFF                                                   FFFFF                  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                 FFFFF                                                     FFFFF                 ",
            "                 FCCDD                                                     DDCCF                 ",
            "                 FCCDD                                                     DDCCF                 ",
            "                 FCCDD                                                     DDCCF                 ",
            "                 FFFFF                                                     FFFFF                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                FFFF                                                         FFFF                ",
            "                FCCD                                                         DCCF                ",
            "                FCCD                                                         DCCF                ",
            "                FCCD                                                         DCCF                ",
            "                FFFF                                                         FFFF                ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "               FFFF                                                           FFFF               ",
            "               FCCD                                                           DCCF               ",
            "               FCCD                                                           DCCF               ",
            "               FCCD                                                           DCCF               ",
            "               FFFF                                                           FFFF               ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "              FFFF                                                             FFFF              ",
            "              FCCD                                                             DCCF              ",
            "              FCCD                                                             DCCF              ",
            "              FCCD                                                             DCCF              ",
            "              FFFF                                                             FFFF              ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "             FFFF                                                               FFFF             ",
            "             FCCD                                                               DCCF             ",
            "             FCCD                                                               DCCF             ",
            "             FCCD                                                               DCCF             ",
            "             FFFF                                                               FFFF             ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "            FFFF                                                                 FFFF            ",
            "            FCCD                                                                 DCCF            ",
            "            FCCD                                                                 DCCF            ",
            "            FCCD                                                                 DCCF            ",
            "            FFFF                                                                 FFFF            ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "           FFFF                                                                   FFFF           ",
            "           FCCD                                                                   DCFF           ",
            "           FCCD                                                                   DCFF           ",
            "           FCCD                                                                   DCFF           ",
            "           FFFF                                                                   FFFF           ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "          FFFF                                                                     FFFF          ",
            "          FCCD                                                                     DCCF          ",
            "          FCCD                                                                     DCCF          ",
            "          FCCD                                                                     DCCF          ",
            "          FFFF                                                                     FFFF          ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "          FFF                                                                       FFF          ",
            "          FCD                                                                       DCF          ",
            "          FCD                                                                       DCF          ",
            "          FCD                                                                       DCF          ",
            "          FFF                                                                       FFF          ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "         FFF                                                                         FFF         ",
            "         FCD                                                                         DCF         ",
            "         FCD                                                                         DCF         ",
            "         FCD                                                                         DCF         ",
            "         FFF                                                                         FFF         ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "        FFFF                                                                         FFFF        ",
            "        FCCD                                                                         DCCF        ",
            "        FCCD                                                                         DCCF        ",
            "        FCCD                                                                         DCCF        ",
            "        FFFF                                                                         FFFF        ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "        FFF                                                                           FFF        ",
            "        FCD                                                                           DCF        ",
            "        FCD                                                                           DCF        ",
            "        FCD                                                                           DCF        ",
            "        FFF                                                                           FFF        ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "       FFF                                                                             FFF       ",
            "       FCD                                                                             DCF       ",
            "       FCD                                                                             DCF       ",
            "       FCD                                                                             DCF       ",
            "       FFF                                                                             FFF       ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "      FFFF                                                                             FFFF      ",
            "      FCCD                                                                             DCCF      ",
            "      FCCD                                                                             DCCF      ",
            "      FCCD                                                                             DCCF      ",
            "      FFFF                                                                             FFFF      ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "      FFF                                                                               FFF      ",
            "      FCD                                                                               DCF      ",
            "      FCD                                                                               DCF      ",
            "      FCD                                                                               DCF      ",
            "      FFF                                                                               FFF      ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "     FFFF                                                                               FFFF     ",
            "     FCCD                                                                               DCCF     ",
            "     FCCD                                                                               DCCF     ",
            "     FCCD                                                                               DCCF     ",
            "     FFFF                                                                               FFFF     ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "     FFF                                                                                 FFF     ",
            "     FCD                                                                                 DCF     ",
            "     FCD                                                                                 DCF     ",
            "     FCD                                                                                 DCF     ",
            "     FFF                                                                                 FFF     ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "    FFFF                                                                                 FFFF    ",
            "    FCCD                                                                                 DCCF    ",
            "    FCCD                                                                                 DCCF    ",
            "    FCCD                                                                                 DCCF    ",
            "    FFFF                                                                                 FFFF    ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "    FFF                                                                                   FFF    ",
            "    FCD                                                                                   DCF    ",
            "    FCD                                                                                   DCF    ",
            "    FCD                                                                                   DCF    ",
            "    FFF                                                                                   FFF    ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "   FFFF                                                                                   FFFF   ",
            "   FCCD                                                                                   DCCF   ",
            "   FCCD                                                                                   DCCF   ",
            "   FCCD                                                                                   DCCF   ",
            "   FFFF                                                                                   FFFF   ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "   FFF                                                                                     FFF   ",
            "   FCD                                                                                     DCF   ",
            "   FCD                                                                                     DCF   ",
            "   FCD                                                                                     DCF   ",
            "   FFF                                                                                     FFF   ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "   FFF                                                                                     FFF   ",
            "   FCD                                                                                     DCF   ",
            "   FCD                                                                                     DCF   ",
            "   FCD                                                                                     DCF   ",
            "   FFF                                                                                     FFF   ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "  FFF                                                                                       FFF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FFF                                                                                       FFF  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "  FFF                                                                                       FFF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FFF                                                                                       FFF  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "  FFF                                                                                       FFF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FFF                                                                                       FFF  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "  FFF                                                                                       FFF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FFF                                                                                       FFF  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                               FFF                                               ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                               FFF                                               ",
            "                                                                                                 " },
        { "                                                F                                                ",
            "                                              FFFFF                                              ",
            "                                               B B                                               ",
            "FFF                                            B B                                            FFF",
            "FCD                                            B B                                            DCF",
            "FCD                                            B B                                            DCF",
            "FCD                                            B B                                            DCF",
            "FFF                                            B B                                            FFF",
            "                                               B B                                               ",
            "                                              FFFFF                                              ",
            "                                                F                                                " },
        { "                                              FFFFF                                              ",
            "                                            FFFFFFFFF                                            ",
            "                                                                                                 ",
            "FFF                                                                                           FFF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FFF                                                                                           FFF",
            "                                                                                                 ",
            "                                            FFFFFFFFF                                            ",
            "                                              FFFFF                                              " },
        { "                                             FFFFFFF                                             ",
            "                                           FFFFFFFFFFF                                           ",
            "                                              AAAAA                                              ",
            "FFF                                                                                           FFF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FFF                                                                                           FFF",
            "                                              AAAAA                                              ",
            "                                           FFFFFFFFFFF                                           ",
            "                                             FFFFFFF                                             " },
        { "                                            FFFFFFFFF                                            ",
            "                                           FFFFFFFFFFF                                           ",
            "                                             AAAAAAA                                             ",
            "FFF                                            AAA                                            FFF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FFF                                            AAA                                            FFF",
            "                                             AAAAAAA                                             ",
            "                                           FFFFFFFFFFF                                           ",
            "                                            FFFFFFFFF                                            " },
        { "                                           FFFFFFFFFFF                                           ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                            AAAAAAAAA                                            ",
            "FFF                                           AAAAA                                           FFF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FFF                                           AAAAA                                           FFF",
            "                                            AAAAAAAAA                                            ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                           FFFFFFFFFFF                                           " },
        { "                                           FFFFFFFFFFF                                           ",
            "                                         FFFFFFFFFFFFFFF                                         ",
            "                                          F AAAAAAAAA F                                          ",
            "FFF                                          AAAAAAA                                          FFF",
            "FCD                                       F    B B    F                                       DCF",
            "FCD                                       F    B B    F                                       DCF",
            "FCD                                       F    B B    F                                       DCF",
            "FFF                                          AAAAAAA                                          FFF",
            "                                          F AAAAAAAAA F                                          ",
            "                                         FFFFFFFFFFFFFFF                                         ",
            "                                           FFFFFFFFFFF                                           " },
        { "                                          FFFFFFFFFFFFF                                          ",
            "                                         FFFFFFFFFFFFFFF                                         ",
            "                                          F AAAAAAAAA F                                          ",
            "FFF                                       F  AAAAAAA  F                                       FFF",
            "FCD                                       F     A     F                                       DCF",
            "FCD                                       F     ~     F                                       DCF",
            "FCD                                       F     A     F                                       DCF",
            "FFF                                       F  AAAAAAA  F                                       FFF",
            "                                          F AAAAAAAAA F                                          ",
            "                                         FFFFFFFFFFFFFFF                                         ",
            "                                          FFFFFFFFFFFFF                                          " },
        { "                                           FFFFFFFFFFF                                           ",
            "                                         FFFFFFFFFFFFFFF                                         ",
            "                                          F AAAAAAAAA F                                          ",
            "FFF                                          AAAAAAA                                          FFF",
            "FCD                                       F    B B    F                                       DCF",
            "FCD                                       F    B B    F                                       DCF",
            "FCD                                       F    B B    F                                       DCF",
            "FFF                                          AAAAAAA                                          FFF",
            "                                          F AAAAAAAAA F                                          ",
            "                                         FFFFFFFFFFFFFFF                                         ",
            "                                           FFFFFFFFFFF                                           " },
        { "                                           FFFFFFFFFFF                                           ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                            AAAAAAAAA                                            ",
            "FFF                                           AAAAA                                           FFF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FFF                                           AAAAA                                           FFF",
            "                                            AAAAAAAAA                                            ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                           FFFFFFFFFFF                                           " },
        { "                                            FFFFFFFFF                                            ",
            "                                           FFFFFFFFFFF                                           ",
            "                                             AAAAAAA                                             ",
            "FFF                                            AAA                                            FFF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FFF                                            AAA                                            FFF",
            "                                             AAAAAAA                                             ",
            "                                           FFFFFFFFFFF                                           ",
            "                                            FFFFFFFFF                                            " },
        { "                                             FFFFFFF                                             ",
            "                                           FFFFFFFFFFF                                           ",
            "                                              AAAAA                                              ",
            "FFF                                                                                           FFF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FFF                                                                                           FFF",
            "                                               AAAA                                              ",
            "                                           FFFFFFFFFFF                                           ",
            "                                             FFFFFFF                                             " },
        { "                                              FFFFF                                              ",
            "                                            FFFFFFFFF                                            ",
            "                                                                                                 ",
            "FFF                                                                                           FFF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FCD                                                                                           DCF",
            "FFF                                                                                           FFF",
            "                                                                                                 ",
            "                                            FFFFFFFFF                                            ",
            "                                              FFFFF                                              " },
        { "                                                F                                                ",
            "                                              FFFFF                                              ",
            "                                               B B                                               ",
            "FFF                                            B B                                            FFF",
            "FCD                                            B B                                            DCF",
            "FCD                                            B B                                            DCF",
            "FCD                                            B B                                            DCF",
            "FFF                                            B B                                            FFF",
            "                                               B B                                               ",
            "                                              FFFFF                                              ",
            "                                                F                                                " },
        { "                                                                                                 ",
            "                                               FFF                                               ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                               FFF                                               ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            " FFF                                                                                         FFF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FCD                                                                                         DCF ",
            " FFF                                                                                         FFF ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "  FFF                                                                                       FFF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FFF                                                                                       FFF  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "  FFF                                                                                       FFF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FFF                                                                                       FFF  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "  FFF                                                                                       FFF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FFF                                                                                       FFF  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "  FFF                                                                                       FFF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FCD                                                                                       DCF  ",
            "  FFF                                                                                       FFF  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "   FFF                                                                                     FFF   ",
            "   FCD                                                                                     DCF   ",
            "   FCD                                                                                     DCF   ",
            "   FCD                                                                                     DCF   ",
            "   FFF                                                                                     FFF   ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "   FFF                                                                                     FFF   ",
            "   FCD                                                                                     DCF   ",
            "   FCD                                                                                     DCF   ",
            "   FCD                                                                                     DCF   ",
            "   FFF                                                                                     FFF   ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "   FFFF                                                                                   FFFF   ",
            "   FCCD                                                                                   DCCF   ",
            "   FCCD                                                                                   DCCF   ",
            "   FCCD                                                                                   DCCF   ",
            "   FFFF                                                                                   FFFF   ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "    FFF                                                                                   FFF    ",
            "    FCD                                                                                   DCF    ",
            "    FCD                                                                                   DCF    ",
            "    FCD                                                                                   DCF    ",
            "    FFF                                                                                   FFF    ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "    FFFF                                                                                 FFFF    ",
            "    FCCD                                                                                 DCCF    ",
            "    FCCD                                                                                 DCCF    ",
            "    FCCD                                                                                 DCCF    ",
            "    FFFF                                                                                 FFFF    ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "     FFF                                                                                 FFF     ",
            "     FCD                                                                                 DCF     ",
            "     FCD                                                                                 DCF     ",
            "     FCD                                                                                 DCF     ",
            "     FFF                                                                                 FFF     ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "     FFFF                                                                               FFFF     ",
            "     FC D                                                                               DCCF     ",
            "     FC D                                                                               DCCF     ",
            "     FC D                                                                               DCCF     ",
            "     FFFF                                                                               FFFF     ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "      FFF                                                                               FFF      ",
            "      FCD                                                                               DCF      ",
            "      FCD                                                                               DCF      ",
            "      FCD                                                                               DCF      ",
            "      FFF                                                                               FFF      ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "      FFFF                                                                             FFFF      ",
            "      FCCD                                                                             DCCF      ",
            "      FCCD                                                                             DCCF      ",
            "      FCCD                                                                             DCCF      ",
            "      FFFF                                                                             FFFF      ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "       FFF                                                                             FFF       ",
            "       FCD                                                                             DCF       ",
            "       FCD                                                                             DCF       ",
            "       FCD                                                                             DCF       ",
            "       FFF                                                                             FFF       ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "        FFF                                                                           FFF        ",
            "        FCD                                                                           DCF        ",
            "        FCD                                                                           DCF        ",
            "        FCD                                                                           DCF        ",
            "        FFF                                                                           FFF        ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "        FFFF                                                                         FFFF        ",
            "        FCCD                                                                         DCCF        ",
            "        FCCD                                                                         DCCF        ",
            "        FCCD                                                                         DCCF        ",
            "        FFFF                                                                         FFFF        ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "         FFF                                                                         FFF         ",
            "         FCD                                                                         DCF         ",
            "         FCD                                                                         DCF         ",
            "         FCD                                                                         DCF         ",
            "         FFF                                                                         FFF         ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "          FFF                                                                       FFF          ",
            "          FCD                                                                       DCF          ",
            "          FCD                                                                       DCF          ",
            "          FCD                                                                       DCF          ",
            "          FFF                                                                       FFF          ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "          FFFF                                                                     FFFF          ",
            "          FCCD                                                                     DCCF          ",
            "          FCCD                                                                     DCCF          ",
            "          FCCD                                                                     DCCF          ",
            "          FFFF                                                                     FFFF          ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "           FFFF                                                                   FFFF           ",
            "           FCCD                                                                   DCCF           ",
            "           FCCD                                                                   DCCF           ",
            "           FCCD                                                                   DCCF           ",
            "           FFFF                                                                   FFFF           ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "            FFFF                                                                 FFFF            ",
            "            FCCD                                                                 DCCF            ",
            "            FCCD                                                                 DCCF            ",
            "            FCCD                                                                 DCCF            ",
            "            FFFF                                                                 FFFF            ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "             FFFF                                                               FFFF             ",
            "             FCCD                                                               DCCF             ",
            "             FCCD                                                               DCCF             ",
            "             FCCD                                                               DCCF             ",
            "             FFFF                                                               FFFF             ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "              FFFF                                                             FFFF              ",
            "              FCCD                                                             DCCF              ",
            "              FCCD                                                             DCCF              ",
            "              FCCD                                                             DCCF              ",
            "              FFFF                                                             FFFF              ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "               FFFF                                                           FFFF               ",
            "               FCCD                                                           DCCF               ",
            "               FCCD                                                           DCCF               ",
            "               FCCD                                                           DCCF               ",
            "               FFFF                                                           FFFF               ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                FFFF                                                         FFFF                ",
            "                FCCD                                                         DCCF                ",
            "                FCCD                                                         DCCF                ",
            "                FCCD                                                         DCCF                ",
            "                FFFF                                                         FFFF                ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                 FFFFF                                                     FFFFF                 ",
            "                 FCCDD                                                     DDCCF                 ",
            "                 FCCDD                                                     DDCCF                 ",
            "                 FCCDD                                                     DDCCF                 ",
            "                 FFFFF                                                     FFFFF                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                  FFFFF                                                   FFFFF                  ",
            "                  FFCCD                                                   DCCFF                  ",
            "                  FFCCD                                                   DCCFF                  ",
            "                  FFCCD                                                   DCCFF                  ",
            "                  FFFFF                                                   FFFFF                  ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                    FFFFF                                               FFFFF                    ",
            "                    FCCDD                                               DDCCF                    ",
            "                    FCCDD                                               DDCCF                    ",
            "                    FCCDD                                               DDCCF                    ",
            "                    FFFFF                                               FFFFF                    ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                     FFFFFF                                           FFFFFF                     ",
            "                     FFCCDD                                           DDCCFF                     ",
            "                     FFCCDD                                           DDCCFF                     ",
            "                     FFCCDD                                           DDCCFF                     ",
            "                     FFFFFF                                           FFFFFF                     ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                       FFFFFF                                       FFFFFF                       ",
            "                       FCCCDD                                       DDCCCF                       ",
            "                       FCCCDD                                       DDCCCF                       ",
            "                       FCCCDD                                       DDCCCF                       ",
            "                       FFFFFF                                       FFFFFF                       ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                        FFFFFFF                                   FFFFFFF                        ",
            "                        FFCCCDD                                   DDCCCFF                        ",
            "                        FFCCCDD                                   DDCCCFF                        ",
            "                        FFCCCDD                                   DDCCCFF                        ",
            "                        FFFFFFF                                   FFFFFFF                        ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                          FFFFFFF                               FFFFFFF                          ",
            "                          FFCCCDD                               DDCCCFF                          ",
            "                          FFCCCDD                               DDCCCFF                          ",
            "                          FFCCCDD                               DDCCCFF                          ",
            "                          FFFFFFF                               FFFFFFF                          ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                            FFFFFFFFF                       FFFFFFFFF                            ",
            "                            FFCCCDDDD                       DDDDCCCFF                            ",
            "                            FFCCCDDDD                       DDDDCCCFF                            ",
            "                            FFCCCDDDD                       DDDDCCCFF                            ",
            "                            FFFFFFFFF                       FFFFFFFFF                            ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                              FFFFFFFFFFFF             FFFFFFFFFFFF                              ",
            "                              FFFCCCCDDDDD             DDDDDCCCCFFF                              ",
            "                              FFFCCCCDDDDD             DDDDDCCCCFFF                              ",
            "                              FFFCCCCDDDDD             DDDDDCCCCFFF                              ",
            "                              FFFFFFFFFFFF             FFFFFFFFFFFF                              ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF                                 ",
            "                                 FFFFCCCCCDDDDDDDDDDDDDCCCCCFFFF                                 ",
            "                                 FFFFCCCCCDDDDDDDDDDDDDCCCCCFFFF                                 ",
            "                                 FFFFCCCCCDDDDDDDDDDDDDCCCCCFFFF                                 ",
            "                                 FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                     FFFFFFFFFFFFFFFFFFFFFFF                                     ",
            "                                     FFFFFCCCCCCCCCCCCCFFFFF                                     ",
            "                                     FFFFFCCCCCCCCCCCCCFFFFF                                     ",
            "                                     FFFFFCCCCCCCCCCCCCFFFFF                                     ",
            "                                     FFFFFFFFFFFFFFFFFFFFFFF                                     ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " },
        { "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                          FFFFFFFFFFFFF                                          ",
            "                                                                                                 ",
            "                                                                                                 ",
            "                                                                                                 " } };

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
            .addInfo("§e转换比例: 钻石(粉)->焦炭 1:1024, 煤炭(粉)->焦炭 1:16， 土->焦炭 1:1(这是怎么做到的?)")
            .addInfo("§e耗时: 1800 * 20ticks - (煤炭产出 / 5)ticks, 耗电: 0EU/t")
            .addInfo("§c§l注意:机器污染过高:如遇跳电并报错“无法排出污染”, 请尝试放置多个消声仓")
            .addSeparator()
            .addPollutionAmount(114514)
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
        return new OTEEpicCokeOven(this.mName);
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
