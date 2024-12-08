package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;

import bartworks.API.BorosilicateGlass;
import gregtech.api.GregTechAPI;
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
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.recipe.common.CI;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class GT_TE_MegaNineInOne extends OTH_MultiMachineBase<GT_TE_MegaNineInOne> {

    public GT_TE_MegaNineInOne(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_MegaNineInOne(String aName) {
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
    private static final int[][] MODE_MAP = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 9, 10, 11 } };

    public int getTextureIndex() {
        return TAE.getIndexFromPage(2, 2);
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
    protected int getMaxParallelRecipes() {
        return Math.max(9, 256 - (GTUtility.getTier(this.getMaxInputVoltage()) * 2));
    }

    @Override
    protected float getSpeedBonus() {
        return (float) (1 + (GTUtility.getTier(this.getMaxInputVoltage()) * 0.1));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return null;
    }

    protected float getEuModifier() {
        return 0.99F;
    }

    private ItemStack getCircuit(ItemStack[] t) {
        for (ItemStack j : t) {
            if (j.getItem() == CI.getNumberedAdvancedCircuit(0)
                .getItem()) {
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
            GTPPRecipeMaps.vacuumFurnaceRecipes);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            private ItemStack lastCircuit = null;

            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 2 : 1, 2);
                return super.process();
            }

            @Override
            protected @NotNull CheckRecipeResult validateRecipe(GTRecipe recipe) {
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
        } else {
            return null;
        }
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (mInternalMode < 3) {
            mInternalMode++;
        } else {
            mInternalMode = 0;
        }
        GTUtility.sendChatToPlayer(
            aPlayer,
            StatCollector.translateToLocal(
                mInternalMode == 0 ? "Metal"
                    : mInternalMode == 1 ? "Fluid"
                        : mInternalMode == 2 ? "Misc" : mInternalMode == 3 ? "Isa" : "Null"));
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
        return 123123;
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

    private final int horizontalOffSet = 24;
    private final int verticalOffSet = 3;
    private final int depthOffSet = 40;
    private static IStructureDefinition<GT_TE_MegaNineInOne> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_MegaNineInOne> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_MegaNineInOne>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('F', ofBlock(ModBlocks.blockCasings3Misc, 2))
                .addElement(
                    'E',
                    buildHatchAdder(GT_TE_MegaNineInOne.class).atLeast(Energy.or(ExoticEnergy))
                        .adder(GT_TE_MegaNineInOne::addToMachineList)
                        .dot(2)
                        .casingIndex(getTextureIndex())
                        .buildAndChain(ModBlocks.blockCasings3Misc, 2))
                .addElement(
                    'D',
                    buildHatchAdder(GT_TE_MegaNineInOne.class)
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Muffler, Maintenance)
                        .adder(GT_TE_MegaNineInOne::addToMachineList)
                        .dot(1)
                        .casingIndex(getTextureIndex())
                        .buildAndChain(ModBlocks.blockCasings3Misc, 2))
                .addElement('A', BorosilicateGlass.ofBoroGlass(3))
                .addElement('G', ofFrame(Materials.HSSS))
                .addElement('B', ofBlock(GregTechAPI.sBlockCasings5, 4))
                .build();

        }
        return STRUCTURE_DEFINITION;
    }

    // spotless:off
    private final String[][] shapeMain = new String[][]{{
            "                                                 ",
            "                      G   G                      ",
            "                      G   G                      ",
            "                      G   G                      ",
            "                      G   G                      ",
            "                      G   G                      ",
            "                                                 "
    },{
            "                      G   G                      ",
            "                     GFFFFFG                     ",
            "                     GFAAAFG                     ",
            "                     GFAAAFG                     ",
            "                     GFAAAFG                     ",
            "                     GFFFFFG                     ",
            "                      G   G                      "
    },{
            "                      GGGGG                      ",
            "                     GFAAAFG                     ",
            "                    FF     FF                    ",
            "                    FF     FF                    ",
            "                    FF     FF                    ",
            "                     GFAAAFG                     ",
            "                      GGGGG                      "
    },{
            "                     GFFFFFG                     ",
            "                    FF     FF                    ",
            "                 FFFFF     FFFFF                 ",
            "                 FFFBBBBBBBBBFFF                 ",
            "                 FFFFF     FFFFF                 ",
            "                    FF     FF                    ",
            "                     GFFFFFG                     "
    },{
            "                     GFAAAFG                     ",
            "                 FFFFF     FFFFF                 ",
            "               FFFFFBBBBBBBBBFFFFF               ",
            "               FFBBBBBBBBBBBBBBBFF               ",
            "               FFFFFBBBBBBBBBFFFFF               ",
            "                 FFFFF     FFFFF                 ",
            "                     GFAAAFG                     "
    },{
            "                     GFFFFFG                     ",
            "               FFFFFFF     FFFFFFF               ",
            "             FFFFBBBFF     FFBBBFFFF             ",
            "             FFBBBBBBBBBBBBBBBBBBBFF             ",
            "             FFFFBBBFF     FFBBBFFFF             ",
            "               FFFFFFF     FFFFFFF               ",
            "                     GFFFFFG                     "
    },{
            "                      GGGGG                      ",
            "             FFFFFFF GFAAAFG FFFFFFF             ",
            "            FFFBBFFFFF     FFFFFBBFFF            ",
            "            FBBBBBBBFF     FFBBBBBBBF            ",
            "            FFFBBFFFFF     FFFFFBBFFF            ",
            "             FFFFFFF GFAAAFG FFFFFFF             ",
            "                      GGGGG                      "
    },{
            "                      G   G                      ",
            "            FFFFF    GFFFFFG    FFFFF            ",
            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ",
            "           FBBBBBFFF GFAAAFG FFFBBBBBF           ",
            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ",
            "            FFFFF    GFFFFFG    FFFFF            ",
            "                      G   G                      "
    },{
            "                                                 ",
            "           FFFF       G   G       FFFF           ",
            "          FFBFFFF     G   G     FFFFBFF          ",
            "          FBBBBFF     G   G     FFBBBBF          ",
            "          FFBFFFF     G   G     FFFFBFF          ",
            "           FFFF       G   G       FFFF           ",
            "                                                 "
    },{
            "                                                 ",
            "          FFF                       FFF          ",
            "         FFBFFF                   FFFBFF         ",
            "         FBBBFF                   FFBBBF         ",
            "         FFBFFF                   FFFBFF         ",
            "          FFF                       FFF          ",
            "                                                 "
    },{
            "                                                 ",
            "         FFF                         FFF         ",
            "        FFBFE                       EFBFF        ",
            "        FBBBF                       FBBBF        ",
            "        FFBFE                       EFBFF        ",
            "         FFF                         FFF         ",
            "                                                 "
    },{
            "                                                 ",
            "        FFF                           FFF        ",
            "       EFBFF                         FFBFE       ",
            "       FBBBF                         FBBBF       ",
            "       EFBFF                         FFBFE       ",
            "        FFF                           FFF        ",
            "                                                 "
    },{
            "                                                 ",
            "       FFF                             FFF       ",
            "      FFBFE                           EFBFF      ",
            "      FBBBF                           FBBBF      ",
            "      FFBFE                           EFBFF      ",
            "       FFF                             FFF       ",
            "                                                 "
    },{
            "                                                 ",
            "      FFF                               FFF      ",
            "     FFBFF                             FFBFF     ",
            "     FBBBF                             FBBBF     ",
            "     FFBFF                             FFBFF     ",
            "      FFF                               FFF      ",
            "                                                 "
    },{
            "                                                 ",
            "      FFF                               FFF      ",
            "     FFBFF                             FFBFF     ",
            "     FBBBF                             FBBBF     ",
            "     FFBFF                             FFBFF     ",
            "      FFF                               FFF      ",
            "                                                 "
    },{
            "                                                 ",
            "     FFF                                 FFF     ",
            "    FFBFF                               FFBFF    ",
            "    FBBBF                               FBBBF    ",
            "    FFBFF                               FFBFF    ",
            "     FFF                                 FFF     ",
            "                                                 "
    },{
            "                                                 ",
            "     FFF                                 FFF     ",
            "    FFBFF                               FFBFF    ",
            "    FBBBF                               FBBBF    ",
            "    FFBFF                               FFBFF    ",
            "     FFF                                 FFF     ",
            "                                                 "
    },{
            "                                                 ",
            "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ",
            "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ",
            "    FFF                                   FFF    ",
            "                                                 "
    },{
            "                                                 ",
            "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ",
            "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ",
            "    FFF                                   FFF    ",
            "                                                 "
    },{
            "                                                 ",
            "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ",
            "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ",
            "    FFF                                   FFF    ",
            "                                                 "
    },{
            "                                                 ",
            "   FFF                                     FFF   ",
            "  FFBFF                                   FFBFF  ",
            "  FBBBF                                   FBBBF  ",
            "  FFBFF                                   FFBFF  ",
            "   FFF                                     FFF   ",
            "                                                 "
    },{
            "   GGG                                     GGG   ",
            " GGFFFGG                                 GGFFFGG ",
            " GFFBFFG                                 GFFBFFG ",
            " GFBBBFG                                 GFBBBFG ",
            " GFFBFFG                                 GFFBFFG ",
            " GGFFFGG                                 GGFFFGG ",
            "   GGG                                     GGG   "
    },{
            " GGFFFGG                                 GGFFFGG ",
            "GFF   FFG                               GFF   FFG",
            "GF  B  FG                               GF  B  FG",
            "GF BBB FG                               GF BBB FG",
            "GF  B  FG                               GF  B  FG",
            "GFF   FFG                               GFF   FFG",
            " GGFFFGG                                 GGFFFGG "
    },{
            "  GFAFG                                   GFAFG  ",
            " FA   AF                                 FA   AF ",
            " A  B  A                                 A  B  A ",
            " A BBB A                                 A BBB A ",
            " A  B  A                                 A  B  A ",
            " FA   AF                                 FA   AF ",
            "  GFAFG                                   GFAFG  "
    },{
            "  GFAFG                                   GFAFG  ",
            " FA   AF                                 FA   AF ",
            " A  B  A                                 A  B  A ",
            " A BBB A                                 A BBB A ",
            " A  B  A                                 A  B  A ",
            " FA   AF                                 FA   AF ",
            "  GFAFG                                   GFAFG  "
    },{
            "  GFAFG                                   GFAFG  ",
            " FA   AF                                 FA   AF ",
            " A  B  A                                 A  B  A ",
            " A BBB A                                 A BBB A ",
            " A  B  A                                 A  B  A ",
            " FA   AF                                 FA   AF ",
            "  GFAFG                                   GFAFG  "
    },{
            " GGFFFGG                                 GGFFFGG ",
            "GFF   FFG                               GFF   FFG",
            "GF  B  FG                               GF  B  FG",
            "GF BBB FG                               GF BBB FG",
            "GF  B  FG                               GF  B  FG",
            "GFF   FFG                               GFF   FFG",
            " GGFFFGG                                 GGFFFGG "
    },{
            "   GGG                                     GGG   ",
            " GGFFFGG                                 GGFFFGG ",
            " GFFBFFG                                 GFFBFFG ",
            " GFBBBFG                                 GFBBBFG ",
            " GFFBFFG                                 GFFBFFG ",
            " GGFFFGG                                 GGFFFGG ",
            "   GGG                                     GGG   "
    },{
            "                                                 ",
            "   FFF                                     FFF   ",
            "  FFBFF                                   FFBFF  ",
            "  FBBBF                                   FBBBF  ",
            "  FFBFF                                   FFBFF  ",
            "   FFF                                     FFF   ",
            "                                                 "
    },{
            "                                                 ",
            "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ",
            "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ",
            "    FFF                                   FFF    ",
            "                                                 "
    },{
            "                                                 ",
            "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ",
            "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ",
            "    FFF                                   FFF    ",
            "                                                 "
    },{
            "                                                 ",
            "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ",
            "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ",
            "    FFF                                   FFF    ",
            "                                                 "
    },{
            "                                                 ",
            "     FFF                                 FFF     ",
            "    FFBFF                               FFBFF    ",
            "    FBBBF                               FBBBF    ",
            "    FFBFF                               FFBFF    ",
            "     FFF                                 FFF     ",
            "                                                 "
    },{
            "                                                 ",
            "     FFF                                 FFF     ",
            "    FFBFF                               FFBFF    ",
            "    FBBBF                               FBBBF    ",
            "    FFBFF                               FFBFF    ",
            "     FFF                                 FFF     ",
            "                                                 "
    },{
            "                                                 ",
            "      FFF                               FFF      ",
            "     FFBFF                             FFBFF     ",
            "     FBBBF                             FBBBF     ",
            "     FFBFF                             FFBFF     ",
            "      FFF                               FFF      ",
            "                                                 "
    },{
            "                                                 ",
            "      FFF                               FFF      ",
            "     FFBFF                             FFBFF     ",
            "     FBBBF                             FBBBF     ",
            "     FFBFF                             FFBFF     ",
            "      FFF                               FFF      ",
            "                                                 "
    },{
            "                                                 ",
            "       FFF                             FFF       ",
            "      FFBFE                           EFBFF      ",
            "      FBBBF                           FBBBF      ",
            "      FFBFE                           EFBFF      ",
            "       FFF                             FFF       ",
            "                                                 "
    },{
            "                                                 ",
            "        FFF                           FFF        ",
            "       EFBFF                         FFBFE       ",
            "       FBBBF                         FBBBF       ",
            "       EFBFF                         FFBFE       ",
            "        FFF                           FFF        ",
            "                                                 "
    },{
            "                                                 ",
            "         FFF                         FFF         ",
            "        FFBFE                       EFBFF        ",
            "        FBBBF                       FBBBF        ",
            "        FFBFE                       EFBFF        ",
            "         FFF                         FFF         ",
            "                                                 "
    },{
            "                                                 ",
            "          FFF                       FFF          ",
            "         FFBFFF                   FFFBFF         ",
            "         FBBBFF                   FFBBBF         ",
            "         FFBFFF                   FFFBFF         ",
            "          FFF                       FFF          ",
            "                                                 "
    },{
            "                                                 ",
            "           FFFF       G   G       FFFF           ",
            "          FFBFFFF     GDDDG     FFFFBFF          ",
            "          FBBBBFF     GD~DG     FFBBBBF          ",
            "          FFBFFFF     GDDDG     FFFFBFF          ",
            "           FFFF       G   G       FFFF           ",
            "                                                 "
    },{
            "                      G   G                      ",
            "            FFFFF    GFFFFFG    FFFFF            ",
            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ",
            "           FBBBBBFFF GFAAAFG FFFBBBBBF           ",
            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ",
            "            FFFFF    GFFFFFG    FFFFF            ",
            "                      G   G                      "
    },{
            "                      GGGGG                      ",
            "             FFFFFFF GFAAAFG FFFFFFF             ",
            "            FFFBBFFFFF     FFFFFBBFFF            ",
            "            FBBBBBBBFF     FFBBBBBBBF            ",
            "            FFFBBFFFFF     FFFFFBBFFF            ",
            "             FFFFFFF GFAAAFG FFFFFFF             ",
            "                      GGGGG                      "
    },{
            "                     GFFFFFG                     ",
            "               FFFFFFF     FFFFFFF               ",
            "             FFFFBBBFF     FFBBBFFFF             ",
            "             FFBBBBBBBBBBBBBBBBBBBFF             ",
            "             FFFFBBBFF     FFBBBFFFF             ",
            "               FFFFFFF     FFFFFFF               ",
            "                     GFFFFFG                     "
    },{
            "                     GFAAAFG                     ",
            "                 FFFFF     FFFFF                 ",
            "               FFFFFBBBBBBBBBFFFFF               ",
            "               FFBBBBBBBBBBBBBBBFF               ",
            "               FFFFFBBBBBBBBBFFFFF               ",
            "                 FFFFF     FFFFF                 ",
            "                     GFAAAFG                     "
    },{
            "                     GFFFFFG                     ",
            "                    FF     FF                    ",
            "                 FFFFF     FFFFF                 ",
            "                 FFFBBBBBBBBBFFF                 ",
            "                 FFFFF     FFFFF                 ",
            "                    FF     FF                    ",
            "                     GFFFFFG                     "
    },{
            "                      GGGGG                      ",
            "                     GFAAAFG                     ",
            "                    FF     FF                    ",
            "                    FF     FF                    ",
            "                    FF     FF                    ",
            "                     GFAAAFG                     ",
            "                      GGGGG                      "
    },{
            "                      G   G                      ",
            "                     GFFFFFG                     ",
            "                     GFAAAFG                     ",
            "                     GFAAAFG                     ",
            "                     GFAAAFG                     ",
            "                     GFFFFFG                     ",
            "                      G   G                      "
    },{
            "                                                 ",
            "                      G   G                      ",
            "                      G   G                      ",
            "                      G   G                      ",
            "                      G   G                      ",
            "                      G   G                      ",
            "                                                 "
    }
    };
    @Override
    public boolean addToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        return super.addToMachineList(aTileEntity, aBaseCasingIndex)
            || addExoticEnergyInputToMachineList(aTileEntity, aBaseCasingIndex);
    }
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType("§c§l老登的终极造物 - 九合一 (巨型加工厂)")
            .addInfo("§a共有十二种模式, 使用螺丝刀切换类模式， 使用突破编程电路20 21 22切换内部模式")
            .addInfo("§aMetal类: 压缩机 车床 电力磁化机")
            .addInfo("§aFluid类: 发酵槽 流体提取机 提取机")
            .addInfo("§aMisc类: 激光蚀刻机 高压釜 流体固化机")
            .addInfo("§aIsa类: 艾萨研磨机 工业浮选机 真空干燥炉")
            .addInfo("§b并行耗时公式来自某位§9冰之妖精")
            .addInfo("§b电压等级提高一级，并行 -2， 最低为⑨, 默认为256")
            .addInfo("§b配方耗时 = NEI耗时 * (1 + (能源仓电压等级 * 10%))")
            .addInfo("§bEU消耗 : 99%")
            .addInfo("§b执行无损超频")
            .addInfo("§e九合一，我们敬爱你口牙！！ ---Sukune_News")
            .addTecTechHatchInfo()
            .addPollutionAmount(123123)
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
        return new GT_TE_MegaNineInOne(this.mName);
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
