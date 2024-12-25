package com.newmaa.othtech.machine;

import bartworks.API.BorosilicateGlass;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTH_MultiMachineBase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTechAPI;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;

public class GT_TE_WoodFusionReactor extends OTH_MultiMachineBase<GT_TE_WoodFusionReactor> {

    public GT_TE_WoodFusionReactor(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_TE_WoodFusionReactor(String aName) {
        super(aName);
    }



    public int getTextureIndex() {
        return TAE.getIndexFromPage(2, 2);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);


    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);


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



    protected float getEuModifier() {
        return 0.99F;
    }





    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {



            @NotNull
            @Override
            public CheckRecipeResult process() {

                setEuModifier(getEuModifier());
                setSpeedBonus(getSpeedBonus());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
                return super.process();
            }

            @Override
            protected @NotNull CheckRecipeResult validateRecipe(GTRecipe recipe) {
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }



        }.enablePerfectOverclock()
            .setMaxParallelSupplier(this::getMaxParallelRecipes);

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

    private final int horizontalOffSet = 24;
    private final int verticalOffSet = 3;
    private final int depthOffSet = 40;
    private static IStructureDefinition<GT_TE_WoodFusionReactor> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<GT_TE_WoodFusionReactor> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<GT_TE_WoodFusionReactor>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('F', ofBlock(ModBlocks.blockCasings3Misc, 2))
                .addElement(
                    'E',
                    buildHatchAdder(GT_TE_WoodFusionReactor.class).atLeast(Energy.or(ExoticEnergy))
                        .adder(GT_TE_WoodFusionReactor::addToMachineList)
                        .dot(2)
                        .casingIndex(getTextureIndex())
                        .buildAndChain(ModBlocks.blockCasings3Misc, 2))
                .addElement(
                    'D',
                    buildHatchAdder(GT_TE_WoodFusionReactor.class)
                        .atLeast(InputHatch, OutputHatch)
                        .adder(GT_TE_WoodFusionReactor::addToMachineList)
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
    //structured from compactFusionReactor
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
        tt.addMachineType("§c§l蒸汽时代的最终幻想 - 蒸汽聚变反应堆")
            .addInfo("§b§o他所能做的, 仅仅只是通过一些无法猜测的手段, 将蒸汽化为氢等离子体")
            .addInfo("EU消耗 : 0, 耗时 : (输入蒸汽数量(L) / 1000) * 20sec , 更高级的蒸汽减少运行时间")
            .addInfo("耗时 : 普通蒸汽 - 100% , 过热蒸汽 : 50% , 超临界蒸汽 : 10%")
            .addInfo("产出 : 普通蒸汽 - 1000L / 1L氢等离子体 , 过热蒸汽 : 1000L / 10L氢等离子体 , 超临界蒸汽 : 1000L / 100L氢等离子体")
            .addSeparator()
            .addController("压缩木头聚变堆")
            .beginStructureBlock(49, 7, 49, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§7123Technology - Steam age");
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
        return new GT_TE_WoodFusionReactor(this.mName);
    }


    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
                                 final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] {
                TextureFactory.of(MACHINE_BRONZE_SIDE),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_STEAM_FURNACE_ACTIVE)
                    .extFacing()
                    .build() };
            return new ITexture[] {
                TextureFactory.of(MACHINE_BRONZE_SIDE),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FRONT_STEAM_FURNACE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { TextureFactory.of(MACHINE_BRONZE_SIDE) };
    }
    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_FUSION_LOOP;
    }
}
//spotless:on
