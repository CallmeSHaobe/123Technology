package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.newmaa.othtech.common.recipemap.Recipemaps.NQF;
import static goodgenerator.api.recipe.GoodGeneratorRecipeMaps.naquadahFuelRefineFactoryRecipes;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_BRONZE_SIDE;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.TT_MultiMachineBase_EM;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.TAE;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gtPlusPlus.core.block.ModBlocks;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEMegaNQFuelFactory extends TT_MultiMachineBase_EM implements IConstructable, ISurvivalConstructable {

    public OTEMegaNQFuelFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaNQFuelFactory(String aName) {
        super(aName);
    }

    public int getTextureIndex() {
        return TAE.getIndexFromPage(2, 2);
    }

    public int mode = 0;
    public int amountPlasma = 1;
    public byte tier = 0;

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("mode", mode);
        aNBT.setInteger("amountPlasma", amountPlasma);
        super.saveNBTData(aNBT);

    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        mode = aNBT.getInteger("mode");
        amountPlasma = aNBT.getInteger("amountPlasma");
        super.loadNBTData(aNBT);

    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String tier = GTUtility.formatNumbers(mode);
            String pla = GTUtility.formatNumbers(amountPlasma);
            tag.setString("Tier", tier);
            tag.setString("PlasmaAmount", pla);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            "模式" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("Tier")
                + EnumChatFormatting.RESET);
        currentTip.add(
            "千升蒸汽等离子产出" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("PlasmaAmount")
                + EnumChatFormatting.RESET);
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (mode < 1) {
                mode++;
            } else {
                mode = 0;
            }
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal(mode == 1 ? "压缩硅岩燃料精炼厂模式" : mode == 0 ? "硅岩燃料精炼厂模式" : "Null"));
        }
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return mode == 0 ? naquadahFuelRefineFactoryRecipes : NQF;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(naquadahFuelRefineFactoryRecipes, NQF);
    }

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
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

    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 消声仓, 能源仓, 输入输出总线, 输入输出仓 : 替换硅岩燃料精炼机械方块, 支持TecTech能源仓") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    private static final String STRUCTURE_PIECE_MAIN = "main";

    private final int horizontalOffSet = 24;
    private final int verticalOffSet = 3;
    private final int depthOffSet = 40;
    private static IStructureDefinition<OTEMegaNQFuelFactory> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<OTEMegaNQFuelFactory> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaNQFuelFactory>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('F', ofBlock(Blocks.planks, 0))
                .addElement('E', ofBlock(Blocks.planks, 0))
                .addElement(
                    'D',
                    buildHatchAdder(OTEMegaNQFuelFactory.class).atLeast(InputHatch, OutputHatch)
                        .adder(OTEMegaNQFuelFactory::addToMachineList)
                        .dot(1)
                        .casingIndex(getTextureIndex())
                        .buildAndChain(Blocks.stonebrick, 0))
                .addElement('A', ofBlock(Blocks.glass, 0))
                .addElement('G', ofFrame(Materials.Wood))
                .addElement('B', ofBlock(ModBlocks.blockCasingsMisc, 2))
                .build();

        }
        return STRUCTURE_DEFINITION;
    }

    // spotless:off
    // structured from compactFusionReactor
    private final String[][] shapeMain = new String[][] {
        { "                                                 ", "                      G   G                      ",
            "                      G   G                      ", "                      G   G                      ",
            "                      G   G                      ", "                      G   G                      ",
            "                                                 " },
        { "                      G   G                      ", "                     GFFFFFG                     ",
            "                     GFAAAFG                     ", "                     GFAAAFG                     ",
            "                     GFAAAFG                     ", "                     GFFFFFG                     ",
            "                      G   G                      " },
        { "                      GGGGG                      ", "                     GFAAAFG                     ",
            "                    FF     FF                    ", "                    FF     FF                    ",
            "                    FF     FF                    ", "                     GFAAAFG                     ",
            "                      GGGGG                      " },
        { "                     GFFFFFG                     ", "                    FF     FF                    ",
            "                 FFFFF     FFFFF                 ", "                 FFFBBBBBBBBBFFF                 ",
            "                 FFFFF     FFFFF                 ", "                    FF     FF                    ",
            "                     GFFFFFG                     " },
        { "                     GFAAAFG                     ", "                 FFFFF     FFFFF                 ",
            "               FFFFFBBBBBBBBBFFFFF               ", "               FFBBBBBBBBBBBBBBBFF               ",
            "               FFFFFBBBBBBBBBFFFFF               ", "                 FFFFF     FFFFF                 ",
            "                     GFAAAFG                     " },
        { "                     GFFFFFG                     ", "               FFFFFFF     FFFFFFF               ",
            "             FFFFBBBFF     FFBBBFFFF             ", "             FFBBBBBBBBBBBBBBBBBBBFF             ",
            "             FFFFBBBFF     FFBBBFFFF             ", "               FFFFFFF     FFFFFFF               ",
            "                     GFFFFFG                     " },
        { "                      GGGGG                      ", "             FFFFFFF GFAAAFG FFFFFFF             ",
            "            FFFBBFFFFF     FFFFFBBFFF            ", "            FBBBBBBBFF     FFBBBBBBBF            ",
            "            FFFBBFFFFF     FFFFFBBFFF            ", "             FFFFFFF GFAAAFG FFFFFFF             ",
            "                      GGGGG                      " },
        { "                      G   G                      ", "            FFFFF    GFFFFFG    FFFFF            ",
            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ", "           FBBBBBFFF GFAAAFG FFFBBBBBF           ",
            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ", "            FFFFF    GFFFFFG    FFFFF            ",
            "                      G   G                      " },
        { "                                                 ", "           FFFF       G   G       FFFF           ",
            "          FFBFFFF     G   G     FFFFBFF          ", "          FBBBBFF     G   G     FFBBBBF          ",
            "          FFBFFFF     G   G     FFFFBFF          ", "           FFFF       G   G       FFFF           ",
            "                                                 " },
        { "                                                 ", "          FFF                       FFF          ",
            "         FFBFFF                   FFFBFF         ", "         FBBBFF                   FFBBBF         ",
            "         FFBFFF                   FFFBFF         ", "          FFF                       FFF          ",
            "                                                 " },
        { "                                                 ", "         FFF                         FFF         ",
            "        FFBFE                       EFBFF        ", "        FBBBF                       FBBBF        ",
            "        FFBFE                       EFBFF        ", "         FFF                         FFF         ",
            "                                                 " },
        { "                                                 ", "        FFF                           FFF        ",
            "       EFBFF                         FFBFE       ", "       FBBBF                         FBBBF       ",
            "       EFBFF                         FFBFE       ", "        FFF                           FFF        ",
            "                                                 " },
        { "                                                 ", "       FFF                             FFF       ",
            "      FFBFE                           EFBFF      ", "      FBBBF                           FBBBF      ",
            "      FFBFE                           EFBFF      ", "       FFF                             FFF       ",
            "                                                 " },
        { "                                                 ", "      FFF                               FFF      ",
            "     FFBFF                             FFBFF     ", "     FBBBF                             FBBBF     ",
            "     FFBFF                             FFBFF     ", "      FFF                               FFF      ",
            "                                                 " },
        { "                                                 ", "      FFF                               FFF      ",
            "     FFBFF                             FFBFF     ", "     FBBBF                             FBBBF     ",
            "     FFBFF                             FFBFF     ", "      FFF                               FFF      ",
            "                                                 " },
        { "                                                 ", "     FFF                                 FFF     ",
            "    FFBFF                               FFBFF    ", "    FBBBF                               FBBBF    ",
            "    FFBFF                               FFBFF    ", "     FFF                                 FFF     ",
            "                                                 " },
        { "                                                 ", "     FFF                                 FFF     ",
            "    FFBFF                               FFBFF    ", "    FBBBF                               FBBBF    ",
            "    FFBFF                               FFBFF    ", "     FFF                                 FFF     ",
            "                                                 " },
        { "                                                 ", "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ", "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ", "    FFF                                   FFF    ",
            "                                                 " },
        { "                                                 ", "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ", "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ", "    FFF                                   FFF    ",
            "                                                 " },
        { "                                                 ", "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ", "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ", "    FFF                                   FFF    ",
            "                                                 " },
        { "                                                 ", "   FFF                                     FFF   ",
            "  FFBFF                                   FFBFF  ", "  FBBBF                                   FBBBF  ",
            "  FFBFF                                   FFBFF  ", "   FFF                                     FFF   ",
            "                                                 " },
        { "   GGG                                     GGG   ", " GGFFFGG                                 GGFFFGG ",
            " GFFBFFG                                 GFFBFFG ", " GFBBBFG                                 GFBBBFG ",
            " GFFBFFG                                 GFFBFFG ", " GGFFFGG                                 GGFFFGG ",
            "   GGG                                     GGG   " },
        { " GGFFFGG                                 GGFFFGG ", "GFF   FFG                               GFF   FFG",
            "GF  B  FG                               GF  B  FG", "GF BBB FG                               GF BBB FG",
            "GF  B  FG                               GF  B  FG", "GFF   FFG                               GFF   FFG",
            " GGFFFGG                                 GGFFFGG " },
        { "  GFAFG                                   GFAFG  ", " FA   AF                                 FA   AF ",
            " A  B  A                                 A  B  A ", " A BBB A                                 A BBB A ",
            " A  B  A                                 A  B  A ", " FA   AF                                 FA   AF ",
            "  GFAFG                                   GFAFG  " },
        { "  GFAFG                                   GFAFG  ", " FA   AF                                 FA   AF ",
            " A  B  A                                 A  B  A ", " A BBB A                                 A BBB A ",
            " A  B  A                                 A  B  A ", " FA   AF                                 FA   AF ",
            "  GFAFG                                   GFAFG  " },
        { "  GFAFG                                   GFAFG  ", " FA   AF                                 FA   AF ",
            " A  B  A                                 A  B  A ", " A BBB A                                 A BBB A ",
            " A  B  A                                 A  B  A ", " FA   AF                                 FA   AF ",
            "  GFAFG                                   GFAFG  " },
        { " GGFFFGG                                 GGFFFGG ", "GFF   FFG                               GFF   FFG",
            "GF  B  FG                               GF  B  FG", "GF BBB FG                               GF BBB FG",
            "GF  B  FG                               GF  B  FG", "GFF   FFG                               GFF   FFG",
            " GGFFFGG                                 GGFFFGG " },
        { "   GGG                                     GGG   ", " GGFFFGG                                 GGFFFGG ",
            " GFFBFFG                                 GFFBFFG ", " GFBBBFG                                 GFBBBFG ",
            " GFFBFFG                                 GFFBFFG ", " GGFFFGG                                 GGFFFGG ",
            "   GGG                                     GGG   " },
        { "                                                 ", "   FFF                                     FFF   ",
            "  FFBFF                                   FFBFF  ", "  FBBBF                                   FBBBF  ",
            "  FFBFF                                   FFBFF  ", "   FFF                                     FFF   ",
            "                                                 " },
        { "                                                 ", "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ", "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ", "    FFF                                   FFF    ",
            "                                                 " },
        { "                                                 ", "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ", "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ", "    FFF                                   FFF    ",
            "                                                 " },
        { "                                                 ", "    FFF                                   FFF    ",
            "   FFBFF                                 FFBFF   ", "   FBBBF                                 FBBBF   ",
            "   FFBFF                                 FFBFF   ", "    FFF                                   FFF    ",
            "                                                 " },
        { "                                                 ", "     FFF                                 FFF     ",
            "    FFBFF                               FFBFF    ", "    FBBBF                               FBBBF    ",
            "    FFBFF                               FFBFF    ", "     FFF                                 FFF     ",
            "                                                 " },
        { "                                                 ", "     FFF                                 FFF     ",
            "    FFBFF                               FFBFF    ", "    FBBBF                               FBBBF    ",
            "    FFBFF                               FFBFF    ", "     FFF                                 FFF     ",
            "                                                 " },
        { "                                                 ", "      FFF                               FFF      ",
            "     FFBFF                             FFBFF     ", "     FBBBF                             FBBBF     ",
            "     FFBFF                             FFBFF     ", "      FFF                               FFF      ",
            "                                                 " },
        { "                                                 ", "      FFF                               FFF      ",
            "     FFBFF                             FFBFF     ", "     FBBBF                             FBBBF     ",
            "     FFBFF                             FFBFF     ", "      FFF                               FFF      ",
            "                                                 " },
        { "                                                 ", "       FFF                             FFF       ",
            "      FFBFE                           EFBFF      ", "      FBBBF                           FBBBF      ",
            "      FFBFE                           EFBFF      ", "       FFF                             FFF       ",
            "                                                 " },
        { "                                                 ", "        FFF                           FFF        ",
            "       EFBFF                         FFBFE       ", "       FBBBF                         FBBBF       ",
            "       EFBFF                         FFBFE       ", "        FFF                           FFF        ",
            "                                                 " },
        { "                                                 ", "         FFF                         FFF         ",
            "        FFBFE                       EFBFF        ", "        FBBBF                       FBBBF        ",
            "        FFBFE                       EFBFF        ", "         FFF                         FFF         ",
            "                                                 " },
        { "                                                 ", "          FFF                       FFF          ",
            "         FFBFFF                   FFFBFF         ", "         FBBBFF                   FFBBBF         ",
            "         FFBFFF                   FFFBFF         ", "          FFF                       FFF          ",
            "                                                 " },
        { "                                                 ", "           FFFF       G   G       FFFF           ",
            "          FFBFFFF     GDDDG     FFFFBFF          ", "          FBBBBFF     GD~DG     FFBBBBF          ",
            "          FFBFFFF     GDDDG     FFFFBFF          ", "           FFFF       G   G       FFFF           ",
            "                                                 " },
        { "                      G   G                      ", "            FFFFF    GFFFFFG    FFFFF            ",
            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ", "           FBBBBBFFF GFAAAFG FFFBBBBBF           ",
            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ", "            FFFFF    GFFFFFG    FFFFF            ",
            "                      G   G                      " },
        { "                      GGGGG                      ", "             FFFFFFF GFAAAFG FFFFFFF             ",
            "            FFFBBFFFFF     FFFFFBBFFF            ", "            FBBBBBBBFF     FFBBBBBBBF            ",
            "            FFFBBFFFFF     FFFFFBBFFF            ", "             FFFFFFF GFAAAFG FFFFFFF             ",
            "                      GGGGG                      " },
        { "                     GFFFFFG                     ", "               FFFFFFF     FFFFFFF               ",
            "             FFFFBBBFF     FFBBBFFFF             ", "             FFBBBBBBBBBBBBBBBBBBBFF             ",
            "             FFFFBBBFF     FFBBBFFFF             ", "               FFFFFFF     FFFFFFF               ",
            "                     GFFFFFG                     " },
        { "                     GFAAAFG                     ", "                 FFFFF     FFFFF                 ",
            "               FFFFFBBBBBBBBBFFFFF               ", "               FFBBBBBBBBBBBBBBBFF               ",
            "               FFFFFBBBBBBBBBFFFFF               ", "                 FFFFF     FFFFF                 ",
            "                     GFAAAFG                     " },
        { "                     GFFFFFG                     ", "                    FF     FF                    ",
            "                 FFFFF     FFFFF                 ", "                 FFFBBBBBBBBBFFF                 ",
            "                 FFFFF     FFFFF                 ", "                    FF     FF                    ",
            "                     GFFFFFG                     " },
        { "                      GGGGG                      ", "                     GFAAAFG                     ",
            "                    FF     FF                    ", "                    FF     FF                    ",
            "                    FF     FF                    ", "                     GFAAAFG                     ",
            "                      GGGGG                      " },
        { "                      G   G                      ", "                     GFFFFFG                     ",
            "                     GFAAAFG                     ", "                     GFAAAFG                     ",
            "                     GFAAAFG                     ", "                     GFFFFFG                     ",
            "                      G   G                      " },
        { "                                                 ", "                      G   G                      ",
            "                      G   G                      ", "                      G   G                      ",
            "                      G   G                      ", "                      G   G                      ",
            "                                                 " } };

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
        return new OTEWoodenFusionReactor(this.mName);
    }

    @Override
    public ITexture[] getTexture(final IGregTechTileEntity baseMetaTileEntity, final ForgeDirection sideDirection,
        final ForgeDirection facing, final int aColorIndex, final boolean active, final boolean aRedstone) {

        if (sideDirection == facing) {
            if (active) return new ITexture[] { TextureFactory.of(MACHINE_BRONZE_SIDE), TextureFactory.builder()
                .addIcon(OVERLAY_FRONT_STEAM_FURNACE_ACTIVE)
                .extFacing()
                .build() };
            return new ITexture[] { TextureFactory.of(MACHINE_BRONZE_SIDE), TextureFactory.builder()
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
