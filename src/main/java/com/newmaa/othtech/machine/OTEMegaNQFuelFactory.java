package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlocksTiered;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static com.newmaa.othtech.common.recipemap.Recipemaps.NQF;
import static goodgenerator.api.recipe.GoodGeneratorRecipeMaps.naquadahFuelRefineFactoryRecipes;
import static gregtech.api.enums.HatchElement.InputHatch;
import static gregtech.api.enums.HatchElement.OutputHatch;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofFrame;
import static net.minecraft.util.StatCollector.translateToLocal;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableList;
import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.machineclass.OTHTTMultiMachineBaseEM;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import goodgenerator.loader.Loaders;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.api.util.ParallelHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class OTEMegaNQFuelFactory extends OTHTTMultiMachineBaseEM implements IConstructable, ISurvivalConstructable {

    public OTEMegaNQFuelFactory(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEMegaNQFuelFactory(String aName) {
        super(aName);
    }

    public int mode = 0;
    private int tier = -1;

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("mTier", this.tier);
        aNBT.setInteger("mode", this.mode);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        this.tier = aNBT.getInteger("mTier");
        this.mode = aNBT.getInteger("mode");
        super.loadNBTData(aNBT);

    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String Mode = GTUtility.formatNumbers(mode);
            String OC = GTUtility.formatNumbers(this.tier);
            tag.setString("Tier", Mode);
            tag.setString("OC", OC);
            tag.setString("Parallel", String.valueOf(maxParallel));
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("otht.waila.mode") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("Tier")
                + EnumChatFormatting.RESET);
        currentTip.add(
            translateToLocal("ote.tm.mnf.parallel.max") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("Parallel")
                + EnumChatFormatting.RESET);
        currentTip.add(
            translateToLocal("otht.waila.oc.amount") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("OC")
                + EnumChatFormatting.RESET);
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (this.mode < 1 && this.tier == 4) {
                this.mode++;
            } else {
                this.mode = 0;
            }
            switch (this.mode) {
                case 0 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.mnf.mode.0"));
                case 1 -> aPlayer.addChatMessage(new ChatComponentTranslation("ote.tm.mnf.mode.1"));
            }
            /*
             * GTUtility.sendChatToPlayer(
             * aPlayer,
             * StatCollector.translateToLocal(mode == 1 ? "压缩硅岩燃料精炼厂模式" : mode == 0 ? "硅岩燃料精炼厂模式" : "Null"));
             */
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
        return structureCheck_EM(mName, horizontalOffSet, verticalOffSet, depthOffSet);
    }

    public int getParallel() {
        if (this.tier == 1) {
            return 16;
        }
        if (this.tier == 2) {
            return 32;
        }
        if (this.tier == 3) {
            return 64;
        }
        if (this.tier == 4) {
            return 256;
        }
        return 0;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected ParallelHelper createParallelHelper(@NotNull GTRecipe recipe) {
                return super.createParallelHelper(recipe).setConsumption(!mRunningOnLoad);
            }

            @NotNull
            @Override
            protected OverclockCalculator createOverclockCalculator(@NotNull GTRecipe recipe) {
                int overclockAmount = tier;
                return super.createOverclockCalculator(recipe).setMaxOverclocks(overclockAmount);
            }

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                if (recipe.mSpecialValue > tier) {
                    return CheckRecipeResultRegistry.insufficientMachineTier(recipe.mSpecialValue);
                }
                lEUt = processingLogic.getCalculatedEut() * processingLogic.getCurrentParallels();
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

        }.setOverclock(4.0, 4.0)
            .setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    public int getMaxParallelRecipes() {
        maxParallel = getParallel();
        return maxParallel;
    }

    @Override
    public void construct(ItemStack itemStack, boolean hintsOnly) {
        structureBuild_EM(mName, horizontalOffSet, verticalOffSet, depthOffSet, itemStack, hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        return survivalBuildPiece(
            mName,
            stackSize,
            horizontalOffSet,
            verticalOffSet,
            depthOffSet,
            elementBudget,
            env,
            false,
            true);
    }

    private static final String[] description = new String[] {
        EnumChatFormatting.AQUA + translateToLocal("otht.con") + ":", translateToLocal("ote.cm.mnf.0") };

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    private final int horizontalOffSet = 24;
    private final int verticalOffSet = 3;
    private final int depthOffSet = 40;
    private static IStructureDefinition<OTEMegaNQFuelFactory> STRUCTURE_DEFINITION = null;

    @Override
    public IStructureDefinition<OTEMegaNQFuelFactory> getStructure_EM() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEMegaNQFuelFactory>builder()
                .addShape(
                    mName,
                    new String[][] {
                        { "                                                 ",
                            "                      G   G                      ",
                            "                      G   G                      ",
                            "                      G   G                      ",
                            "                      G   G                      ",
                            "                      G   G                      ",
                            "                                                 " },
                        { "                      G   G                      ",
                            "                     GFFFFFG                     ",
                            "                     GFAAAFG                     ",
                            "                     GFAAAFG                     ",
                            "                     GFAAAFG                     ",
                            "                     GFFFFFG                     ",
                            "                      G   G                      " },
                        { "                      GGGGG                      ",
                            "                     GFAAAFG                     ",
                            "                    FF     FF                    ",
                            "                    FF     FF                    ",
                            "                    FF     FF                    ",
                            "                     GFAAAFG                     ",
                            "                      GGGGG                      " },
                        { "                     GFFFFFG                     ",
                            "                    FF     FF                    ",
                            "                 FFFFF     FFFFF                 ",
                            "                 FFFBBBBBBBBBFFF                 ",
                            "                 FFFFF     FFFFF                 ",
                            "                    FF     FF                    ",
                            "                     GFFFFFG                     " },
                        { "                     GFAAAFG                     ",
                            "                 FFFFF     FFFFF                 ",
                            "               FFFFFBBBBBBBBBFFFFF               ",
                            "               FFBBBBBBBBBBBBBBBFF               ",
                            "               FFFFFBBBBBBBBBFFFFF               ",
                            "                 FFFFF     FFFFF                 ",
                            "                     GFAAAFG                     " },
                        { "                     GFFFFFG                     ",
                            "               FFFFFFF     FFFFFFF               ",
                            "             FFFFBBBFF     FFBBBFFFF             ",
                            "             FFBBBBBBBBBBBBBBBBBBBFF             ",
                            "             FFFFBBBFF     FFBBBFFFF             ",
                            "               FFFFFFF     FFFFFFF               ",
                            "                     GFFFFFG                     " },
                        { "                      GGGGG                      ",
                            "             FFFFFFF GFAAAFG FFFFFFF             ",
                            "            FFFBBFFFFF     FFFFFBBFFF            ",
                            "            FBBBBBBBFF     FFBBBBBBBF            ",
                            "            FFFBBFFFFF     FFFFFBBFFF            ",
                            "             FFFFFFF GFAAAFG FFFFFFF             ",
                            "                      GGGGG                      " },
                        { "                      G   G                      ",
                            "            FFFFF    GFFFFFG    FFFFF            ",
                            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ",
                            "           FBBBBBFFF GFAAAFG FFFBBBBBF           ",
                            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ",
                            "            FFFFF    GFFFFFG    FFFFF            ",
                            "                      G   G                      " },
                        { "                                                 ",
                            "           FFFF       G   G       FFFF           ",
                            "          FFBFFFF     G   G     FFFFBFF          ",
                            "          FBBBBFF     G   G     FFBBBBF          ",
                            "          FFBFFFF     G   G     FFFFBFF          ",
                            "           FFFF       G   G       FFFF           ",
                            "                                                 " },
                        { "                                                 ",
                            "          FFF                       FFF          ",
                            "         FFBFFF                   FFFBFF         ",
                            "         FBBBFF                   FFBBBF         ",
                            "         FFBFFF                   FFFBFF         ",
                            "          FFF                       FFF          ",
                            "                                                 " },
                        { "                                                 ",
                            "         FFF                         FFF         ",
                            "        FFBFE                       EFBFF        ",
                            "        FBBBF                       FBBBF        ",
                            "        FFBFE                       EFBFF        ",
                            "         FFF                         FFF         ",
                            "                                                 " },
                        { "                                                 ",
                            "        FFF                           FFF        ",
                            "       EFBFF                         FFBFE       ",
                            "       FBBBF                         FBBBF       ",
                            "       EFBFF                         FFBFE       ",
                            "        FFF                           FFF        ",
                            "                                                 " },
                        { "                                                 ",
                            "       FFF                             FFF       ",
                            "      FFBFE                           EFBFF      ",
                            "      FBBBF                           FBBBF      ",
                            "      FFBFE                           EFBFF      ",
                            "       FFF                             FFF       ",
                            "                                                 " },
                        { "                                                 ",
                            "      FFF                               FFF      ",
                            "     FFBFF                             FFBFF     ",
                            "     FBBBF                             FBBBF     ",
                            "     FFBFF                             FFBFF     ",
                            "      FFF                               FFF      ",
                            "                                                 " },
                        { "                                                 ",
                            "      FFF                               FFF      ",
                            "     FFBFF                             FFBFF     ",
                            "     FBBBF                             FBBBF     ",
                            "     FFBFF                             FFBFF     ",
                            "      FFF                               FFF      ",
                            "                                                 " },
                        { "                                                 ",
                            "     FFF                                 FFF     ",
                            "    FFBFF                               FFBFF    ",
                            "    FBBBF                               FBBBF    ",
                            "    FFBFF                               FFBFF    ",
                            "     FFF                                 FFF     ",
                            "                                                 " },
                        { "                                                 ",
                            "     FFF                                 FFF     ",
                            "    FFBFF                               FFBFF    ",
                            "    FBBBF                               FBBBF    ",
                            "    FFBFF                               FFBFF    ",
                            "     FFF                                 FFF     ",
                            "                                                 " },
                        { "                                                 ",
                            "    FFF                                   FFF    ",
                            "   FFBFF                                 FFBFF   ",
                            "   FBBBF                                 FBBBF   ",
                            "   FFBFF                                 FFBFF   ",
                            "    FFF                                   FFF    ",
                            "                                                 " },
                        { "                                                 ",
                            "    FFF                                   FFF    ",
                            "   FFBFF                                 FFBFF   ",
                            "   FBBBF                                 FBBBF   ",
                            "   FFBFF                                 FFBFF   ",
                            "    FFF                                   FFF    ",
                            "                                                 " },
                        { "                                                 ",
                            "    FFF                                   FFF    ",
                            "   FFBFF                                 FFBFF   ",
                            "   FBBBF                                 FBBBF   ",
                            "   FFBFF                                 FFBFF   ",
                            "    FFF                                   FFF    ",
                            "                                                 " },
                        { "                                                 ",
                            "   FFF                                     FFF   ",
                            "  FFBFF                                   FFBFF  ",
                            "  FBBBF                                   FBBBF  ",
                            "  FFBFF                                   FFBFF  ",
                            "   FFF                                     FFF   ",
                            "                                                 " },
                        { "   GGG                                     GGG   ",
                            " GGFFFGG                                 GGFFFGG ",
                            " GFFBFFG                                 GFFBFFG ",
                            " GFBBBFG                                 GFBBBFG ",
                            " GFFBFFG                                 GFFBFFG ",
                            " GGFFFGG                                 GGFFFGG ",
                            "   GGG                                     GGG   " },
                        { " GGFFFGG                                 GGFFFGG ",
                            "GFF   FFG                               GFF   FFG",
                            "GF  B  FG                               GF  B  FG",
                            "GF BBB FG                               GF BBB FG",
                            "GF  B  FG                               GF  B  FG",
                            "GFF   FFG                               GFF   FFG",
                            " GGFFFGG                                 GGFFFGG " },
                        { "  GFAFG                                   GFAFG  ",
                            " FA   AF                                 FA   AF ",
                            " A  B  A                                 A  B  A ",
                            " A BBB A                                 A BBB A ",
                            " A  B  A                                 A  B  A ",
                            " FA   AF                                 FA   AF ",
                            "  GFAFG                                   GFAFG  " },
                        { "  GFAFG                                   GFAFG  ",
                            " FA   AF                                 FA   AF ",
                            " A  B  A                                 A  B  A ",
                            " A BBB A                                 A BBB A ",
                            " A  B  A                                 A  B  A ",
                            " FA   AF                                 FA   AF ",
                            "  GFAFG                                   GFAFG  " },
                        { "  GFAFG                                   GFAFG  ",
                            " FA   AF                                 FA   AF ",
                            " A  B  A                                 A  B  A ",
                            " A BBB A                                 A BBB A ",
                            " A  B  A                                 A  B  A ",
                            " FA   AF                                 FA   AF ",
                            "  GFAFG                                   GFAFG  " },
                        { " GGFFFGG                                 GGFFFGG ",
                            "GFF   FFG                               GFF   FFG",
                            "GF  B  FG                               GF  B  FG",
                            "GF BBB FG                               GF BBB FG",
                            "GF  B  FG                               GF  B  FG",
                            "GFF   FFG                               GFF   FFG",
                            " GGFFFGG                                 GGFFFGG " },
                        { "   GGG                                     GGG   ",
                            " GGFFFGG                                 GGFFFGG ",
                            " GFFBFFG                                 GFFBFFG ",
                            " GFBBBFG                                 GFBBBFG ",
                            " GFFBFFG                                 GFFBFFG ",
                            " GGFFFGG                                 GGFFFGG ",
                            "   GGG                                     GGG   " },
                        { "                                                 ",
                            "   FFF                                     FFF   ",
                            "  FFBFF                                   FFBFF  ",
                            "  FBBBF                                   FBBBF  ",
                            "  FFBFF                                   FFBFF  ",
                            "   FFF                                     FFF   ",
                            "                                                 " },
                        { "                                                 ",
                            "    FFF                                   FFF    ",
                            "   FFBFF                                 FFBFF   ",
                            "   FBBBF                                 FBBBF   ",
                            "   FFBFF                                 FFBFF   ",
                            "    FFF                                   FFF    ",
                            "                                                 " },
                        { "                                                 ",
                            "    FFF                                   FFF    ",
                            "   FFBFF                                 FFBFF   ",
                            "   FBBBF                                 FBBBF   ",
                            "   FFBFF                                 FFBFF   ",
                            "    FFF                                   FFF    ",
                            "                                                 " },
                        { "                                                 ",
                            "    FFF                                   FFF    ",
                            "   FFBFF                                 FFBFF   ",
                            "   FBBBF                                 FBBBF   ",
                            "   FFBFF                                 FFBFF   ",
                            "    FFF                                   FFF    ",
                            "                                                 " },
                        { "                                                 ",
                            "     FFF                                 FFF     ",
                            "    FFBFF                               FFBFF    ",
                            "    FBBBF                               FBBBF    ",
                            "    FFBFF                               FFBFF    ",
                            "     FFF                                 FFF     ",
                            "                                                 " },
                        { "                                                 ",
                            "     FFF                                 FFF     ",
                            "    FFBFF                               FFBFF    ",
                            "    FBBBF                               FBBBF    ",
                            "    FFBFF                               FFBFF    ",
                            "     FFF                                 FFF     ",
                            "                                                 " },
                        { "                                                 ",
                            "      FFF                               FFF      ",
                            "     FFBFF                             FFBFF     ",
                            "     FBBBF                             FBBBF     ",
                            "     FFBFF                             FFBFF     ",
                            "      FFF                               FFF      ",
                            "                                                 " },
                        { "                                                 ",
                            "      FFF                               FFF      ",
                            "     FFBFF                             FFBFF     ",
                            "     FBBBF                             FBBBF     ",
                            "     FFBFF                             FFBFF     ",
                            "      FFF                               FFF      ",
                            "                                                 " },
                        { "                                                 ",
                            "       FFF                             FFF       ",
                            "      FFBFE                           EFBFF      ",
                            "      FBBBF                           FBBBF      ",
                            "      FFBFE                           EFBFF      ",
                            "       FFF                             FFF       ",
                            "                                                 " },
                        { "                                                 ",
                            "        FFF                           FFF        ",
                            "       EFBFF                         FFBFE       ",
                            "       FBBBF                         FBBBF       ",
                            "       EFBFF                         FFBFE       ",
                            "        FFF                           FFF        ",
                            "                                                 " },
                        { "                                                 ",
                            "         FFF                         FFF         ",
                            "        FFBFE                       EFBFF        ",
                            "        FBBBF                       FBBBF        ",
                            "        FFBFE                       EFBFF        ",
                            "         FFF                         FFF         ",
                            "                                                 " },
                        { "                                                 ",
                            "          FFF                       FFF          ",
                            "         FFBFFF                   FFFBFF         ",
                            "         FBBBFF                   FFBBBF         ",
                            "         FFBFFF                   FFFBFF         ",
                            "          FFF                       FFF          ",
                            "                                                 " },
                        { "                                                 ",
                            "           FFFF       G   G       FFFF           ",
                            "          FFBFFFF     GDDDG     FFFFBFF          ",
                            "          FBBBBFF     GD~DG     FFBBBBF          ",
                            "          FFBFFFF     GDDDG     FFFFBFF          ",
                            "           FFFF       G   G       FFFF           ",
                            "                                                 " },
                        { "                      G   G                      ",
                            "            FFFFF    GFFFFFG    FFFFF            ",
                            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ",
                            "           FBBBBBFFF GFAAAFG FFFBBBBBF           ",
                            "           EFBBFFFFF GFAAAFG FFFFFBBFE           ",
                            "            FFFFF    GFFFFFG    FFFFF            ",
                            "                      G   G                      " },
                        { "                      GGGGG                      ",
                            "             FFFFFFF GFAAAFG FFFFFFF             ",
                            "            FFFBBFFFFF     FFFFFBBFFF            ",
                            "            FBBBBBBBFF     FFBBBBBBBF            ",
                            "            FFFBBFFFFF     FFFFFBBFFF            ",
                            "             FFFFFFF GFAAAFG FFFFFFF             ",
                            "                      GGGGG                      " },
                        { "                     GFFFFFG                     ",
                            "               FFFFFFF     FFFFFFF               ",
                            "             FFFFBBBFF     FFBBBFFFF             ",
                            "             FFBBBBBBBBBBBBBBBBBBBFF             ",
                            "             FFFFBBBFF     FFBBBFFFF             ",
                            "               FFFFFFF     FFFFFFF               ",
                            "                     GFFFFFG                     " },
                        { "                     GFAAAFG                     ",
                            "                 FFFFF     FFFFF                 ",
                            "               FFFFFBBBBBBBBBFFFFF               ",
                            "               FFBBBBBBBBBBBBBBBFF               ",
                            "               FFFFFBBBBBBBBBFFFFF               ",
                            "                 FFFFF     FFFFF                 ",
                            "                     GFAAAFG                     " },
                        { "                     GFFFFFG                     ",
                            "                    FF     FF                    ",
                            "                 FFFFF     FFFFF                 ",
                            "                 FFFBBBBBBBBBFFF                 ",
                            "                 FFFFF     FFFFF                 ",
                            "                    FF     FF                    ",
                            "                     GFFFFFG                     " },
                        { "                      GGGGG                      ",
                            "                     GFAAAFG                     ",
                            "                    FF     FF                    ",
                            "                    FF     FF                    ",
                            "                    FF     FF                    ",
                            "                     GFAAAFG                     ",
                            "                      GGGGG                      " },
                        { "                      G   G                      ",
                            "                     GFFFFFG                     ",
                            "                     GFAAAFG                     ",
                            "                     GFAAAFG                     ",
                            "                     GFAAAFG                     ",
                            "                     GFFFFFG                     ",
                            "                      G   G                      " },
                        { "                                                 ",
                            "                      G   G                      ",
                            "                      G   G                      ",
                            "                      G   G                      ",
                            "                      G   G                      ",
                            "                      G   G                      ",
                            "                                                 " } })
                .addElement('F', ofBlock(Loaders.FRF_Casings, 0))
                .addElement('E', ofBlock(Loaders.FRF_Casings, 0))
                .addElement(
                    'D',
                    buildHatchAdder(OTEMegaNQFuelFactory.class).atLeast(InputHatch, OutputHatch)
                        .adder(OTEMegaNQFuelFactory::addToMachineList)
                        .casingIndex(179)
                        .dot(1)
                        .buildAndChain(ofBlock(Loaders.FRF_Casings, 0)))
                .addElement('A', ofBlock(Loaders.fieldRestrictingGlass, 0))
                .addElement('G', ofFrame(Materials.NaquadahAlloy))
                .addElement(
                    'B',
                    withChannel(
                        "coil",
                        ofBlocksTiered(
                            this::getCoilTier,
                            ImmutableList.of(
                                Pair.of(Loaders.FRF_Coil_1, 0),
                                Pair.of(Loaders.FRF_Coil_2, 1),
                                Pair.of(Loaders.FRF_Coil_3, 2),
                                Pair.of(Loaders.FRF_Coil_4, 3)),
                            -1,
                            (t, meta) -> tier = meta,
                            t -> tier)))
                // .addElement(
                // 'B',
                // ofChain(
                // onElementPass(x -> ++x.cnt[0], ofFieldCoil(0)),
                // onElementPass(x -> ++x.cnt[1], ofFieldCoil(1)),
                // onElementPass(x -> ++x.cnt[2], ofFieldCoil(2)),
                // onElementPass(x -> ++x.cnt[3], ofFieldCoil(3))))
                .build();

        }
        return STRUCTURE_DEFINITION;
    }

    // private static final Block[] coils = new Block[] { Loaders.FRF_Coil_1, Loaders.FRF_Coil_2, Loaders.FRF_Coil_3,
    // Loaders.FRF_Coil_4 };

    public int getCoilTier(Block block, int meta) {
        if (block == Loaders.FRF_Coil_1) {
            this.tier = 1;
            return 1;
        }
        if (block == Loaders.FRF_Coil_2) {
            this.tier = 2;
            return 2;
        }
        if (block == Loaders.FRF_Coil_3) {
            this.tier = 3;
            return 3;
        }
        if (block == Loaders.FRF_Coil_4) {
            this.tier = 4;
            return 4;
        }
        this.tier = -1;
        return 0;
    }

    // public int getTier() {
    // for (int i = 0; i < 4; i++) {
    // if (cnt[i] == 560) {
    // tier = i + 1;
    // return i;
    // }
    // }
    // tier = -1;
    // return -1;
    // }

    // public static <T> IStructureElement<T> ofFieldCoil(int aIndex) {
    // return new IStructureElement<>() {
    //
    // @Override
    // public boolean check(T t, World world, int x, int y, int z) {
    // Block block = world.getBlock(x, y, z);
    // return block.equals(coils[aIndex]);
    // }
    //
    // @Override
    // public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
    // StructureLibAPI.hintParticle(world, x, y, z, coils[getIndex(trigger)], 0);
    // return true;
    // }
    //
    // private int getIndex(ItemStack trigger) {
    // int s = trigger.stackSize;
    // if (s > 4 || s <= 0) s = 4;
    // return s - 1;
    // }
    //
    // @Override
    // public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
    // return world.setBlock(x, y, z, coils[getIndex(trigger)], 0, 3);
    // }
    //
    // @Override
    // public BlocksToPlace getBlocksToPlace(T t, World world, int x, int y, int z, ItemStack trigger,
    // AutoPlaceEnvironment env) {
    // return BlocksToPlace.create(coils[getIndex(trigger)], 0);
    // }
    //
    // @Override
    // public PlaceResult survivalPlaceBlock(T t, World world, int x, int y, int z, ItemStack trigger,
    // AutoPlaceEnvironment env) {
    // if (check(t, world, x, y, z)) return PlaceResult.SKIP;
    // return StructureUtility.survivalPlaceBlock(
    // coils[getIndex(trigger)],
    // 0,
    // world,
    // x,
    // y,
    // z,
    // env.getSource(),
    // env.getActor(),
    // env.getChatter());
    // }
    // };
    // }

    // spotless:off
    // structure copied from compactFusionReactor
    /*
     * @Override
     * protected MultiblockTooltipBuilder createTooltip() {
     * final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
     * tt.addMachineType("§z§l秒杀八台巨蒸的史诗级机器 - 压缩硅岩燃料精炼厂")
     * .addInfo("§l§o甜甜圈喜加一系列")
     * .addInfo("更高级的线圈允许并行与加速 :")
     * .addInfo("一级线圈 : 16并行, 超频一次; 二级线圈 : 32并行, 超频二次;")
     * .addInfo("三级线圈 : 64并行, 超频三次; 四级线圈 : 256并行, 超频四次")
     * .addInfo("四级线圈解锁§c独有配方池")
     * .addInfo("§b螺丝刀切换模式, §c独有配方池§b允许更高级的单步配方")
     * .addSeparator()
     * .addController("压缩硅岩燃料精炼厂")
     * .beginStructureBlock(49, 7, 49, false)
     * .addInputBus("AnyInputBus", 1)
     * .addOutputBus("AnyOutputBus", 1)
     * .addInputHatch("AnyInputHatch", 1)
     * .addOutputHatch("AnyOutputHatch", 1)
     * .addEnergyHatch("AnyEnergyHatch", 1)
     * .addMufflerHatch("AnyMufflerHatch", 1)
     * .toolTipFinisher("§7123Technology - Naquadah's Dream");
     * return tt;
     * }
     */
    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.mnf.0"))
            .addInfo(translateToLocal("ote.tm.mnf.1"))
            .addInfo(translateToLocal("ote.tm.mnf.2"))
            .addInfo(translateToLocal("ote.tm.mnf.3"))
            .addInfo(translateToLocal("ote.tm.mnf.4"))
            .addInfo(translateToLocal("ote.tm.mnf.5"))
            .addInfo(translateToLocal("ote.tm.mnf.6"))
            .addSeparator()
            .addController(translateToLocal("ote.tn.mnf"))
            .beginStructureBlock(49, 7, 49, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addMufflerHatch("AnyMufflerHatch", 1)
            .toolTipFinisher("§7123Technology - Naquadah's Dream");
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
        return new OTEMegaNQFuelFactory(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            if (aActive) return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(179),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE)
                    .extFacing()
                    .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(179), TextureFactory.builder()
                .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
        }
        return new ITexture[] { Textures.BlockIcons.getCasingTextureForId(179) };
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_FUSION_LOOP;
    }
}
