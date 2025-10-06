package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.enums.HatchElement.ExoticEnergy;
import static gregtech.api.enums.HatchElement.InputBus;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.Maintenance;
import static gregtech.api.enums.HatchElement.Muffler;
import static gregtech.api.enums.HatchElement.OutputBus;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.ItemList.Circuit_Integrated;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockBeacon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.structure.AutoPlaceEnvironment;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.utils.Utils;

import bartworks.API.BorosilicateGlass;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.TAE;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.metadata.CompressionTierKey;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class OTEMegaNineInOne extends OTHMultiMachineBase<OTEMegaNineInOne> {

    public OTEMegaNineInOne(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaNineInOne(String aName) {
        super(aName);
    }

    protected int mInternalMode = 0;
    private static final int MODE_COMPRESSOR = 0;
    private static final int MODE_LATHE = 1;
    private static final int MODE_MAGNETIC = 2;
    private static final int MODE_FERMENTER = 3;
    private static final int MODE_FLUIDEXTRACT = 4;
    private static final int MODE_EXTRACTOR = 5;
    private static final int MODE_LASER = 6;
    private static final int MODE_AUTOCLAVE = 7;
    private static final int MODE_FLUIDSOLIDIFY = 8;
    private static final int MODE_ISA = 9;
    private static final int MODE_FLOAT = 10;
    private static final int MODE_VACUUM = 11;
    private static final int MODE_MOLE = 12;
    private static final int MODE_BREW = 13;
    private static final int MODE_HEAT = 14;
    private static final int[][] MODE_MAP = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 9, 10, 11 },
        { 12, 13, 14 } };

    public int getTextureIndex() {
        return TAE.getIndexFromPage(2, 2);
    }

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
        super.saveNBTData(aNBT);
        aNBT.setInteger("mInternalMode", mInternalMode);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        this.mInternalMode = aNBT.getInteger("mInternalMode");

    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    public int getMaxParallelRecipes() {
        return Math.max(9, 64 - (GTUtility.getTier(this.getMaxInputVoltage()) * 9));
    }

    @Override
    protected float getSpeedBonus() {
        return (float) (1 + (GTUtility.getTier(this.getMaxInputVoltage()) * 0.16));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return null;
    }

    protected float getEuModifier() {
        return (float) Math.max(0.01, ((double) 1 / getCoilTier()) * 0.1);
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
            RecipeMaps.compressorRecipes,
            RecipeMaps.latheRecipes,
            RecipeMaps.polarizerRecipes,
            RecipeMaps.fermentingRecipes,
            RecipeMaps.fluidExtractionRecipes,
            RecipeMaps.extractorRecipes,
            RecipeMaps.laserEngraverRecipes,
            RecipeMaps.autoclaveRecipes,
            RecipeMaps.fluidSolidifierRecipes,
            GTPPRecipeMaps.millingRecipes,
            GTPPRecipeMaps.flotationCellRecipes,
            GTPPRecipeMaps.vacuumFurnaceRecipes,
            GTPPRecipeMaps.molecularTransformerRecipes,
            RecipeMaps.brewingRecipes,
            RecipeMaps.fluidHeaterRecipes);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTHProcessingLogic() {

            private ItemStack lastCircuit = null;

            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

            @Override
            protected @NotNull CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.getMetadata(CompressionTierKey.INSTANCE) != null) {
                    if (getCoilTier() < 10 && recipe.getMetadata(CompressionTierKey.INSTANCE) == 1) {
                        return CheckRecipeResultRegistry.insufficientMachineTier(1);
                    } else if (getCoilTier() < 13 && recipe.getMetadata(CompressionTierKey.INSTANCE) == 2) {
                        return CheckRecipeResultRegistry.insufficientMachineTier(2);
                    } else return super.validateRecipe(recipe);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
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

        }.enablePerfectOverclock()
            .setMaxParallelSupplier(this::getMaxParallelRecipes);

    }

    private static RecipeMap<?> getRecipeMap(int aMode) {
        if (aMode == MODE_COMPRESSOR) {
            return RecipeMaps.compressorRecipes;
        } else if (aMode == MODE_LATHE) {
            return RecipeMaps.latheRecipes;
        } else if (aMode == MODE_MAGNETIC) {
            return RecipeMaps.polarizerRecipes;
        } else if (aMode == MODE_FERMENTER) {
            return RecipeMaps.fermentingRecipes;
        } else if (aMode == MODE_FLUIDEXTRACT) {
            return RecipeMaps.fluidExtractionRecipes;
        } else if (aMode == MODE_EXTRACTOR) {
            return RecipeMaps.extractorRecipes;
        } else if (aMode == MODE_LASER) {
            return RecipeMaps.laserEngraverRecipes;
        } else if (aMode == MODE_AUTOCLAVE) {
            return RecipeMaps.autoclaveRecipes;
        } else if (aMode == MODE_FLUIDSOLIDIFY) {
            return RecipeMaps.fluidSolidifierRecipes;
        } else if (aMode == MODE_ISA) {
            return GTPPRecipeMaps.millingRecipes;
        } else if (aMode == MODE_FLOAT) {
            return GTPPRecipeMaps.flotationCellRecipes;
        } else if (aMode == MODE_VACUUM) {
            return GTPPRecipeMaps.vacuumFurnaceRecipes;
        } else if (aMode == MODE_MOLE) {
            return GTPPRecipeMaps.molecularTransformerRecipes;
        } else if (aMode == MODE_BREW) {
            return RecipeMaps.brewingRecipes;
        } else if (aMode == MODE_HEAT) {
            return RecipeMaps.fluidHeaterRecipes;
        } else return null;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (mInternalMode < 4) {
            mInternalMode++;
        } else {
            mInternalMode = 0;
        }
        switch (mInternalMode) {
            case 0 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.m9in1.mode.0"));
            case 1 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.m9in1.mode.1"));
            case 2 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.m9in1.mode.2"));
            case 3 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.m9in1.mode.3"));
            case 4 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.m9in1.mode.4"));
        }
        /*
         * GTUtility.sendChatToPlayer(
         * aPlayer,
         * StatCollector.translateToLocal(
         * mInternalMode == 0 ? translateToLocal("ote.tm.m9in1.mode.0")
         * : mInternalMode == 1 ? translateToLocal("ote.tm.m9in1.mode.1")
         * : mInternalMode == 2 ? translateToLocal("ote.tm.m9in1.mode.2")
         * : mInternalMode == 3 ? translateToLocal("ote.tm.m9in1.mode.3")
         * : mInternalMode == 4 ? translateToLocal("ote.tm.m9in1.mode.4") : "Null"));
         */
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
    public int getPollutionPerSecond(final ItemStack aStack) {
        return 123;
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

    private final int horizontalOffSet = 24;
    private final int verticalOffSet = 3;
    private final int depthOffSet = 40;
    private static IStructureDefinition<OTEMegaNineInOne> STRUCTURE_DEFINITION = null;
    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.mnio.0"),
        EnumChatFormatting.LIGHT_PURPLE + translateToLocal("ote.cm.mnio.1"),
        EnumChatFormatting.GOLD + translateToLocal("ote.cm.mnio.2") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public IStructureDefinition<OTEMegaNineInOne> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaNineInOne>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('D', ofBlock(ModBlocks.blockCasings3Misc, 2))
                .addElement(
                    'E',
                    buildHatchAdder(OTEMegaNineInOne.class)
                        .atLeast(
                            InputBus,
                            OutputBus,
                            InputHatch,
                            OutputHatch,
                            Muffler,
                            Maintenance,
                            Energy.or(ExoticEnergy))
                        .adder(OTEMegaNineInOne::addToMachineList)
                        .dot(1)
                        .casingIndex(getTextureIndex())
                        .buildAndChain(ModBlocks.blockCasings3Misc, 2))
                .addElement('A', BorosilicateGlass.ofBoroGlass(3))
                .addElement('C', ofFrame(Materials.HSSS))
                .addElement(
                    'B',
                    withChannel("coil", ofCoil(OTEMegaNineInOne::setCoilLevel, OTEMegaNineInOne::getCoilLevel)))
                .addElement('F', new IStructureElement<>() {

                    private final BlocksToPlace cached = BlocksToPlace.create(Blocks.beacon, 0);

                    @Override
                    public boolean check(OTEMegaNineInOne oteMegaNineInOne, World world, int x, int y, int z) {
                        // in fact, we only need it is a beacon
                        // so it also can be chisel's beacon...?
                        return world.getBlock(x, y, z) instanceof BlockBeacon
                            && world.getTileEntity(x, y, z) instanceof TileEntityBeacon;
                    }

                    @Override
                    public boolean couldBeValid(OTEMegaNineInOne oteMegaNineInOne, World world, int x, int y, int z,
                        ItemStack trigger) {
                        // no side effect
                        return check(oteMegaNineInOne, world, x, y, z);
                    }

                    @Override
                    public boolean spawnHint(OTEMegaNineInOne oteMegaNineInOne, World world, int x, int y, int z,
                        ItemStack trigger) {
                        StructureLibAPI.hintParticle(world, x, y, z, Blocks.beacon, 0);
                        return true;
                    }

                    @Override
                    public boolean placeBlock(OTEMegaNineInOne oteMegaNineInOne, World world, int x, int y, int z,
                        ItemStack trigger) {
                        world.setBlock(x, y, z, Blocks.beacon, 0, 2);
                        if (check(oteMegaNineInOne, world, x, y, z)) return true;
                        else return world.setBlockToAir(x, y, z);
                    }

                    @Override
                    public BlocksToPlace getBlocksToPlace(OTEMegaNineInOne oteMegaNineInOne, World world, int x, int y,
                        int z, ItemStack trigger, AutoPlaceEnvironment env) {
                        return cached;
                    }
                })
                .build();

        }
        return STRUCTURE_DEFINITION;
    }

    // spotless:off
    //structured by compactFusionReactor
    private final String[][] shapeMain = new String[][]{{
        "                                                 ",
        "                      C   C                      ",
        "                      C   C                      ",
        "                      C   C                      ",
        "                      C   C                      ",
        "                      C   C                      ",
        "                                                 ",
        "                                                 "
    },{
        "                      C   C                      ",
        "                     CDDDDDC                     ",
        "                     CDEEEDC                     ",
        "                     CDEEEDC                     ",
        "                     CDEEEDC                     ",
        "                     CDDDDDC                     ",
        "                      C   C                      ",
        "                                                 "
    },{
        "                      CCCCC                      ",
        "                     CDAAADC                     ",
        "                    DD     DD                    ",
        "                    DD     DD                    ",
        "                    DD     DD                    ",
        "                     CDAAADC                     ",
        "                      CCCCC                      ",
        "                                                 "
    },{
        "                     CEEEEEC                     ",
        "                    DD     DD                    ",
        "                 DDDDD     DDDDD                 ",
        "                 DDDBBBBBBBBBDDD                 ",
        "                 DDDDD     DDDDD                 ",
        "                    DD     DD                    ",
        "                     CEEEEEC                     ",
        "                                                 "
    },{
        "                     CEEEEEC                     ",
        "                 DDDDD     DDDDD                 ",
        "               DDDDDBBBBBBBBBDDDDD               ",
        "               DDBBBBBBBBBBBBBBBDD               ",
        "               DDDDDBBBBBBBBBDDDDD               ",
        "                 DDDDD     DDDDD                 ",
        "                     CEEEEEC                     ",
        "                                                 "
    },{
        "                     CEEEEEC                     ",
        "               DDDDDDD     DDDDDDD               ",
        "             DDDDBBBDD     DDBBBDDDD             ",
        "             DDBBBBBBBBBBBBBBBBBBBDD             ",
        "             DDDDBBBDD     DDBBBDDDD             ",
        "               DDDDDDD     DDDDDDD               ",
        "                     CEEEEEC                     ",
        "                                                 "
    },{
        "                      CCCCC                      ",
        "             DDDDDDD CDAAADC DDDDDDD             ",
        "            DDDBBDDDDD     DDDDDBBDDD            ",
        "            DBBBBBBBDD     DDBBBBBBBD            ",
        "            DDDBBDDDDD     DDDDDBBDDD            ",
        "             DDDDDDD CDAAADC DDDDDDD             ",
        "                      CCCCC                      ",
        "                                                 "
    },{
        "                      C   C                      ",
        "            DDDDD    CDDDDDC    DDDDD            ",
        "           EDBBDDDDD CDAAADC DDDDDBBDE           ",
        "           DBBBBBDDD CDAAADC DDDBBBBBD           ",
        "           EDBBDDDDD CDAAADC DDDDDBBDE           ",
        "            DDDDD    CDDDDDC    DDDDD            ",
        "                      C   C                      ",
        "                                                 "
    },{
        "                                                 ",
        "           DDDD       C   C       DDDD           ",
        "          DDBDDDD     C   C     DDDDBDD          ",
        "          DBBBBDD     C   C     DDBBBBD          ",
        "          DDBDDDD     C   C     DDDDBDD          ",
        "           DDDD       C   C       DDDD           ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "          DDD                       DDD          ",
        "         DDBDDD                   DDDBDD         ",
        "         DBBBDD                   DDBBBD         ",
        "         DDBDDD                   DDDBDD         ",
        "          DDD                       DDD          ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "         DDD                         DDD         ",
        "        DDBDE                       EDBDD        ",
        "        DBBBD                       DBBBD        ",
        "        DDBDE                       EDBDD        ",
        "         DDD                         DDD         ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "        DDD                           DDD        ",
        "       EDBDD                         DDBDE       ",
        "       DBBBD                         DBBBD       ",
        "       EDBDD                         DDBDE       ",
        "        DDD                           DDD        ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "       DDD                             DDD       ",
        "      DDBDE                           EDBDD      ",
        "      DBBBD                           DBBBD      ",
        "      DDBDE                           EDBDD      ",
        "       DDD                             DDD       ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "      DDD                               DDD      ",
        "     DDBDD                             DDBDD     ",
        "     DBBBD                             DBBBD     ",
        "     DDBDD                             DDBDD     ",
        "      DDD                               DDD      ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "      DDD                               DDD      ",
        "     DDBDD                             DDBDD     ",
        "     DBBBD                             DBBBD     ",
        "     DDBDD                             DDBDD     ",
        "      DDD                               DDD      ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "     DDD                                 DDD     ",
        "    DDBDD                               DDBDD    ",
        "    DBBBD                               DBBBD    ",
        "    DDBDD                               DDBDD    ",
        "     DDD                                 DDD     ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "     DDD                                 DDD     ",
        "    DDBDD                               DDBDD    ",
        "    DBBBD                               DBBBD    ",
        "    DDBDD                               DDBDD    ",
        "     DDD                                 DDD     ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "    DDD                                   DDD    ",
        "   DDBDD              C   C              DDBDD   ",
        "   DBBBD            DDCAAACDD            DBBBD   ",
        "   DDBDD              C   C              DDBDD   ",
        "    DDD                                   DDD    ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "    DDD                                   DDD    ",
        "   DDBDD            DDCAAACDD            DDBDD   ",
        "   DBBBD           DBBDBBBDBBD           DBBBD   ",
        "   DDBDD            DDCAAACDD            DDBDD   ",
        "    DDD                                   DDD    ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "    DDD                                   DDD    ",
        "   DDBDD           DD C   C DD           DDBDD   ",
        "   DBBBD          DBBDCAAACDBBD          DBBBD   ",
        "   DDBDD           DD C   C DD           DDBDD   ",
        "    DDD                                   DDD    ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "   DDD                                     DDD   ",
        "  DDBDD           DD         DD           DDBDD  ",
        "  DBBBD          DBBD       DBBD          DBBBD  ",
        "  DDBDD           DD         DD           DDBDD  ",
        "   DDD                                     DDD   ",
        "                                                 ",
        "                                                 "
    },{
        "   CCC                                     CCC   ",
        " CCDDDCC                                 CCDDDCC ",
        " CDDBDDC          D           D          CDDBDDC ",
        " CDBBBDC         DBD         DBD         CDBBBDC ",
        " CDDBDDC          D           D          CDDBDDC ",
        " CCDDDCC                                 CCDDDCC ",
        "   CCC                                     CCC   ",
        "                                                 "
    },{
        " CCEEECC                                 CCEEECC ",
        "CDD   DDC                               CDD   DDC",
        "CD  B  DC        CCC         CCC        CD  B  DC",
        "CD BBB DC        CDC         CDC        CD BBB DC",
        "CD  B  DC        CCC         CCC        CD  B  DC",
        "CDD   DDC                               CDD   DDC",
        " CCEEECC                                 CCEEECC ",
        "                                                 "
    },{
        "  CEEEC                                   CEEEC  ",
        " DA   AD                                 DA   AD ",
        " E  B  A          A    EEE    A          A  B  E ",
        " E BBB A         ABA   EEE   ABA         A BBB E ",
        " E  B  A          A    EEE    A          A  B  E ",
        " DA   AD                                 DA   AD ",
        "  CEEEC                                   CEEEC  ",
        "                                                 "
    },{
        "  CEEEC                                   CEEEC  ",
        " DA   AD                                 DA   AD ",
        " E  B  A          A    EEE    A          A  B  E ",
        " E BBB A         ABA   EFE   ABA         A BBB E ",
        " E  B  A          A    EEE    A          A  B  E ",
        " DA   AD                                 DA   AD ",
        "  CEEEC                                   CEEEC  ",
        "                                                 "
    },{
        "  CEEEC                                   CEEEC  ",
        " DA   AD                                 DA   AD ",
        " E  B  A          A    EEE    A          A  B  E ",
        " E BBB A         ABA   EEE   ABA         A BBB E ",
        " E  B  A          A    EEE    A          A  B  E ",
        " DA   AD                                 DA   AD ",
        "  CEEEC                                   CEEEC  ",
        "                                                 "
    },{
        " CCEEECC                                 CCEEECC ",
        "CDD   DDC                               CDD   DDC",
        "CD  B  DC        CCC         CCC        CD  B  DC",
        "CD BBB DC        CDC         CDC        CD BBB DC",
        "CD  B  DC        CCC         CCC        CD  B  DC",
        "CDD   DDC                               CDD   DDC",
        " CCEEECC                                 CCEEECC ",
        "                                                 "
    },{
        "   CCC                                     CCC   ",
        " CCDDDCC                                 CCDDDCC ",
        " CDDBDDC          D           D          CDDBDDC ",
        " CDBBBDC         DBD         DBD         CDBBBDC ",
        " CDDBDDC          D           D          CDDBDDC ",
        " CCDDDCC                                 CCDDDCC ",
        "   CCC                                     CCC   ",
        "                                                 "
    },{
        "                                                 ",
        "   DDD                                     DDD   ",
        "  DDBDD           DD         DD           DDBDD  ",
        "  DBBBD          DBBD       DBBD          DBBBD  ",
        "  DDBDD           DD         DD           DDBDD  ",
        "   DDD                                     DDD   ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "    DDD                                   DDD    ",
        "   DDBDD           DD C   C DD           DDBDD   ",
        "   DBBBD          DBBDCAAACDBBD          DBBBD   ",
        "   DDBDD           DD C   C DD           DDBDD   ",
        "    DDD                                   DDD    ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "    DDD                                   DDD    ",
        "   DDBDD            DDCAAACDD            DDBDD   ",
        "   DBBBD           DBBDBBBDBBD           DBBBD   ",
        "   DDBDD            DDCAAACDD            DDBDD   ",
        "    DDD                                   DDD    ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "    DDD                                   DDD    ",
        "   DDBDD              C   C              DDBDD   ",
        "   DBBBD            DDCAAACDD            DBBBD   ",
        "   DDBDD              C   C              DDBDD   ",
        "    DDD                                   DDD    ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "     DDD                                 DDD     ",
        "    DDBDD                               DDBDD    ",
        "    DBBBD                               DBBBD    ",
        "    DDBDD                               DDBDD    ",
        "     DDD                                 DDD     ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "     DDD                                 DDD     ",
        "    DDBDD                               DDBDD    ",
        "    DBBBD                               DBBBD    ",
        "    DDBDD                               DDBDD    ",
        "     DDD                                 DDD     ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "      DDD                               DDD      ",
        "     DDBDD                             DDBDD     ",
        "     DBBBD                             DBBBD     ",
        "     DDBDD                             DDBDD     ",
        "      DDD                               DDD      ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "      DDD                               DDD      ",
        "     DDBDD                             DDBDD     ",
        "     DBBBD                             DBBBD     ",
        "     DDBDD                             DDBDD     ",
        "      DDD                               DDD      ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "       DDD                             DDD       ",
        "      DDBDE                           EDBDD      ",
        "      DBBBD                           DBBBD      ",
        "      DDBDE                           EDBDD      ",
        "       DDD                             DDD       ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "        DDD                           DDD        ",
        "       EDBDD                         DDBDE       ",
        "       DBBBD                         DBBBD       ",
        "       EDBDD                         DDBDE       ",
        "        DDD                           DDD        ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "         DDD                         DDD         ",
        "        DDBDE                       EDBDD        ",
        "        DBBBD                       DBBBD        ",
        "        DDBDE                       EDBDD        ",
        "         DDD                         DDD         ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "          DDD                       DDD          ",
        "         DDBDDD                   DDDBDD         ",
        "         DBBBDD                   DDBBBD         ",
        "         DDBDDD                   DDDBDD         ",
        "          DDD                       DDD          ",
        "                                                 ",
        "                                                 "
    },{
        "                                                 ",
        "           DDDD       C   C       DDDD           ",
        "          DDBDDDD     CEEEC     DDDDBDD          ",
        "          DBBBBDD     CE~EC     DDBBBBD          ",
        "          DDBDDDD     CEEEC     DDDDBDD          ",
        "           DDDD       C   C       DDDD           ",
        "                                                 ",
        "                                                 "
    },{
        "                      C   C                      ",
        "            DDDDD    CDDDDDC    DDDDD            ",
        "           EDBBDDDDD CDAAADC DDDDDBBDE           ",
        "           DBBBBBDDD CDAAADC DDDBBBBBD           ",
        "           EDBBDDDDD CDAAADC DDDDDBBDE           ",
        "            DDDDD    CDDDDDC    DDDDD            ",
        "                      C   C                      ",
        "                                                 "
    },{
        "                      CCCCC                      ",
        "             DDDDDDD CDAAADC DDDDDDD             ",
        "            DDDBBDDDDD     DDDDDBBDDD            ",
        "            DBBBBBBBDD     DDBBBBBBBD            ",
        "            DDDBBDDDDD     DDDDDBBDDD            ",
        "             DDDDDDD CDAAADC DDDDDDD             ",
        "                      CCCCC                      ",
        "                                                 "
    },{
        "                     CEEEEEC                     ",
        "               DDDDDDD     DDDDDDD               ",
        "             DDDDBBBDD     DDBBBDDDD             ",
        "             DDBBBBBBBBBBBBBBBBBBBDD             ",
        "             DDDDBBBDD     DDBBBDDDD             ",
        "               DDDDDDD     DDDDDDD               ",
        "                     CEEEEEC                     ",
        "                                                 "
    },{
        "                     CEEEEEC                     ",
        "                 DDDDD     DDDDD                 ",
        "               DDDDDBBBBBBBBBDDDDD               ",
        "               DDBBBBBBBBBBBBBBBDD               ",
        "               DDDDDBBBBBBBBBDDDDD               ",
        "                 DDDDD     DDDDD                 ",
        "                     CEEEEEC                     ",
        "                                                 "
    },{
        "                     CEEEEEC                     ",
        "                    DD     DD                    ",
        "                 DDDDD     DDDDD                 ",
        "                 DDDBBBBBBBBBDDD                 ",
        "                 DDDDD     DDDDD                 ",
        "                    DD     DD                    ",
        "                     CEEEEEC                     ",
        "                                                 "
    },{
        "                      CCCCC                      ",
        "                     CDAAADC                     ",
        "                    DD     DD                    ",
        "                    DD     DD                    ",
        "                    DD     DD                    ",
        "                     CDAAADC                     ",
        "                      CCCCC                      ",
        "                                                 "
    },{
        "                      C   C                      ",
        "                     CDDDDDC                     ",
        "                     CDEEEDC                     ",
        "                     CDEEEDC                     ",
        "                     CDEEEDC                     ",
        "                     CDDDDDC                     ",
        "                      C   C                      ",
        "                                                 "
    },{
        "                                                 ",
        "                      C   C                      ",
        "                      C   C                      ",
        "                      C   C                      ",
        "                      C   C                      ",
        "                      C   C                      ",
        "                                                 ",
        "                                                 "
    }};
    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }
    /*
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§c§l老登的终极造物 - 九合一 (巨型加工厂)")
            .addInfo("§a共有十五种模式, 使用螺丝刀切换类模式， 使用编程电路20 21 22切换内部模式")
            .addInfo("§aMetal类: 压缩机 车床 电力磁化机")
            .addInfo("§aFluid类: 发酵槽 流体提取机 提取机")
            .addInfo("§aMisc类: 激光蚀刻机 高压釜 流体固化机")
            .addInfo("§aIsa类: 艾萨研磨机 工业浮选机 真空干燥炉")
            .addInfo("§aCol类: 分子重组仪 酿造室 流体加热机")
            .addInfo("§b并行耗时公式来自某位§9冰之妖精")
            .addInfo("§b电压等级提高一级，并行 -2， 最低为⑨, 默认为256")
            .addInfo("§b配方耗时 = NEI耗时 * (1 + (能源仓电压等级 * 10%))")
            .addInfo("§bEU消耗 : (1 / 线圈等级) * 0.1, 最低为0.01")
            .addInfo("§b海珀珍线圈解锁稳定的黑洞, 通流琥珀金线圈解锁HIP")
            .addInfo("§b执行无损超频")
            .addInfo("§e九合一，我们敬爱你口牙！！ ---Sukune_News")
            .addInfo("§c§l注意:机器污染过高:如遇跳电并报错“无法排出污染”, 请尝试放置多个消声仓")
            .addTecTechHatchInfo()
            .addPollutionAmount(123)
            .addSeparator()
            .addController("巨型加工厂")
            .beginStructureBlock(49, 7, 49, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§a123Technology - For The Best Machine");
        return tt;
    }*/
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("oth.tm.mnio.0"))
            .addInfo(translateToLocal("oth.tm.mnio.1"))
            .addInfo(translateToLocal("oth.tm.mnio.2"))
            .addInfo(translateToLocal("oth.tm.mnio.3"))
            .addInfo(translateToLocal("oth.tm.mnio.4"))
            .addInfo(translateToLocal("oth.tm.mnio.5"))
            .addInfo(translateToLocal("oth.tm.mnio.6"))
            .addInfo(translateToLocal("oth.tm.mnio.7"))
            .addInfo(translateToLocal("oth.tm.mnio.8"))
            .addInfo(translateToLocal("oth.tm.mnio.9"))
            .addInfo(translateToLocal("oth.tm.mnio.10"))
            .addInfo(translateToLocal("oth.tm.mnio.11"))
            .addInfo(translateToLocal("oth.tm.mnio.12"))
            .addInfo(translateToLocal("oth.tm.mnio.13"))
            .addInfo(translateToLocal("oth.tm.pollution"))
            .addTecTechHatchInfo()
            .addPollutionAmount(123)
            .addSeparator()
            .addController(translateToLocal("ote.tn.mnio"))
            .beginStructureBlock(49, 7, 49, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§a123Technology - For The Best Machine");
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
        return new OTEMegaNineInOne(this.mName);
    }


    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
                                 final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(TAE.getIndexFromPage(2, 2)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced_Active)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                Textures.BlockIcons
                    .getCasingTextureForId(TAE.getIndexFromPage(2, 2)),
                TextureFactory.builder()
                    .addIcon(TexturesGtBlock.Overlay_Machine_Controller_Advanced)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons
            .getCasingTextureForId(TAE.getIndexFromPage(2, 2)) };
    }
}
//spotless:on
