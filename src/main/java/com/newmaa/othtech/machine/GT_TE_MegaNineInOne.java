package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.GT_HatchElement.*;
import static gregtech.api.util.GT_StructureUtility.ofFrame;

import java.util.Arrays;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.github.bartimaeusnek.bartworks.API.BorosilicateGlass;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTH_processingLogics.OTH_ProcessingLogic;

import gregtech.api.GregTech_API;
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
import gregtech.api.util.GT_HatchElementBuilder;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.api.recipe.GTPPRecipeMaps;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.xmod.gregtech.common.blocks.textures.TexturesGtBlock;

public class GT_TE_MegaNineInOne extends OTH_MultiMachineBase<GT_TE_MegaNineInOne> {

    public GT_TE_MegaNineInOne(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_MegaNineInOne(String aName) {
        super(aName);
    }

    private byte mode = 0;

    public int getTextureIndex() {
        return TAE.getIndexFromPage(2, 2);
    }

    @Override
    protected int getCasingTextureId() {
        return getTextureIndex();
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("mode", mode);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mode = aNBT.getByte("mode");
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return true;
    }

    @Override
    protected int getMaxParallelRecipes() {
        return Math.max(9, 32 - (GT_Utility.getTier(this.getMaxInputVoltage()) * 2));
    }

    @Override
    protected float getSpeedBonus() {
        return (float) (1 + (GT_Utility.getTier(this.getMaxInputVoltage()) * 0.1));
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (mode == 1) return RecipeMaps.latheRecipes;
        if (mode == 2) return RecipeMaps.polarizerRecipes;
        if (mode == 3) return RecipeMaps.fermentingRecipes;
        if (mode == 4) return RecipeMaps.fluidExtractionRecipes;
        if (mode == 5) return RecipeMaps.extractorRecipes;
        if (mode == 6) return RecipeMaps.laserEngraverRecipes;
        if (mode == 7) return RecipeMaps.autoclaveRecipes;
        if (mode == 8) return RecipeMaps.fluidSolidifierRecipes;
        if (mode == 9) return GTPPRecipeMaps.millingRecipes;
        if (mode == 10) return GTPPRecipeMaps.flotationCellRecipes;
        if (mode == 11) return GTPPRecipeMaps.vacuumFurnaceRecipes;
        return RecipeMaps.compressorRecipes;
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
        return new OTH_ProcessingLogic() {

            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 2 : 1, 2);
                return super.process();
            }

            @Override
            protected @NotNull CheckRecipeResult validateRecipe(GT_Recipe recipe) {
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.enablePerfectOverclock()
            .setMaxParallelSupplier(this::getMaxParallelRecipes);

    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.mode = (byte) ((this.mode + 1) % 12);
            GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("Mode." + this.mode));
        }
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
                    GT_HatchElementBuilder.<GT_TE_MegaNineInOne>builder()
                        .atLeast(Energy.or(ExoticEnergy))
                        .adder(GT_TE_MegaNineInOne::addToMachineList)
                        .dot(1)
                        .casingIndex(getTextureIndex())
                        .buildAndChain(ModBlocks.blockCasings3Misc, 2))
                .addElement(
                    'D',
                    GT_HatchElementBuilder.<GT_TE_MegaNineInOne>builder()
                        .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Muffler, Maintenance)
                        .adder(GT_TE_MegaNineInOne::addToMachineList)
                        .dot(1)
                        .casingIndex(getTextureIndex())
                        .buildAndChain(ModBlocks.blockCasings3Misc, 2))
                .addElement('A', BorosilicateGlass.ofBoroGlass(3))
                .addElement('G', ofFrame(Materials.HSSS))
                .addElement('B', ofBlock(GregTech_API.sBlockCasings5, 4))
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
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("§c§l老登的终极造物 - 九合一 (巨型加工厂)")
            .addInfo("§a共有十二种模式")
            .addInfo("§amode 0 - 8为原九合一九种模式")
            .addInfo("§amode 9 - 11为艾萨研磨机 工业浮选机 真空干燥炉模式")
            .addInfo("§b并行耗时公式来自某位§9冰之妖精")
            .addInfo("§b电压等级提高一级，并行 -2， 最低为⑨, 默认为32")
            .addInfo("§b电压等级提高一级，速度 -10%")
            .addInfo("§b执行无损超频")
            .addInfo("§q支持§bTecTech§q能源仓及激光仓，但不支持无线电网直接供给EU")
            .addInfo("§e九合一，我们敬爱你口牙！！ ---Sukune_News")
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
