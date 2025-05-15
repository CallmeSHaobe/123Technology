package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static com.newmaa.othtech.common.recipemap.Recipemaps.BIN;
import static com.newmaa.othtech.utils.Utils.setStackSize;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Mods.AppliedEnergistics2;
import static gregtech.api.recipe.RecipeMaps.recyclerRecipes;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;

import bartworks.API.BorosilicateGlass;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.common.blocks.BlockCasings1;
import gregtech.common.blocks.BlockCasings2;
import gregtech.common.blocks.BlockCasings4;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class OTELargeBin extends OTHMultiMachineBase<OTELargeBin> implements IConstructable, ISurvivalConstructable {

    private int mode = 0;
    public int mSA = 0;
    public int Tier = 1;
    public static String Tier1 = "Tier1";
    public static String Tier2 = "Tier2";
    public static String Tier3 = "Tier3";
    public static String Tier4 = "Tier4";
    protected static final int[] progressingTick = { 1, 5, 10, 20, 32, 64, 128, 192, 256, 512 };
    protected byte progressingTickIndex = 5;

    @Override
    public IStructureDefinition<OTELargeBin> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        return Tier == 1 ? checkPiece(Tier1, 2, 3, 1)
            : Tier == 2 ? checkPiece(Tier2, 4, 9, 2)
                : Tier == 3 ? checkPiece(Tier3, 7, 18, 3)
                    : Tier == 4 ? checkPiece(Tier4, 9, 27, 1) : checkPiece(Tier1, 2, 3, 1);

    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        repairMachine();
        if (Tier == 1) {
            buildPiece(Tier1, stackSize, hintsOnly, 2, 3, 1);
        } else if (Tier == 2) {
            buildPiece(Tier2, stackSize, hintsOnly, 4, 9, 2);
        } else if (Tier == 3) {
            buildPiece(Tier3, stackSize, hintsOnly, 7, 18, 3);
        } else if (Tier == 4) {
            buildPiece(Tier4, stackSize, hintsOnly, 9, 27, 1);
        } else {
            buildPiece(Tier1, stackSize, hintsOnly, 2, 3, 1);
        }
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, IItemSource source, EntityPlayerMP actor) {
        if (mMachine) return -1;
        return Tier == 1 ? survivialBuildPiece(Tier1, stackSize, 2, 3, 1, elementBudget, source, actor, false, true)
            : Tier == 2 ? survivialBuildPiece(Tier2, stackSize, 4, 9, 2, elementBudget, source, actor, false, true)
                : Tier == 3 ? survivialBuildPiece(Tier3, stackSize, 7, 18, 3, elementBudget, source, actor, false, true)
                    : Tier == 4
                        ? survivialBuildPiece(Tier4, stackSize, 9, 27, 1, elementBudget, source, actor, false, true)
                        : survivialBuildPiece(Tier1, stackSize, 2, 3, 1, elementBudget, source, actor, false, true);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        if (mode == 1) return recyclerRecipes;
        if (mode == 2) return Recipemaps.AE2;
        if (mode == 3) return RecipeMaps.neutroniumCompressorRecipes;
        return BIN;
    }

    @NotNull
    @Override
    public Collection<RecipeMap<?>> getAvailableRecipeMaps() {
        return Arrays.asList(recyclerRecipes, Recipemaps.AE2, RecipeMaps.neutroniumCompressorRecipes);
    }

    // region structure
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 输入输出总线, 输入仓, 能源仓, 动力仓"),
        // Power Casing
    };
    private static final IStructureDefinition<OTELargeBin> STRUCTURE_DEFINITION = StructureDefinition
        .<OTELargeBin>builder()
        .addShape(
            Tier1,
            transpose(
                new String[][] { { "     ", "     ", "  A  ", "     ", "     " },
                    { "AAAAA", "AAAAA", "AAAAA", "AAAAA", "AAAAA" }, { "     ", " AAA ", " A A ", " AAA ", "     " },
                    { "     ", " A~A ", " A A ", " AAA ", "     " }, { "     ", " AAA ", " AAA ", " AAA ", "     " } }))
        .addShape(
            Tier2,
            transpose(
                new String[][] {
                    { "         ", "         ", "         ", "         ", "    Y    ", "         ", "         ",
                        "         ", "         " },
                    { "         ", " YYYYYYY ", " YYYYYYY ", " YYYYYYY ", " YYYYYYY ", " YYYYYYY ", " YYYYYYY ",
                        " YYYYYYY ", "         " },
                    { "YYYYYYYYY", "YYYYYYYYY", "YY     YY", "YY     YY", "YY     YY", "YY     YY", "YY     YY",
                        "YYYYYYYYY", "YYYYYYYYY" },
                    { "         ", " YYZZZYY ", " Y     Y ", " Z     Z ", " Z     Z ", " Z     Z ", " Y     Y ",
                        " YYZZZYY ", "         " },
                    { "         ", " YYYYYYY ", " Y     Y ", " Y     Y ", " Y     Y ", " Y     Y ", " Y     Y ",
                        " YYYYYYY ", "         " },
                    { "         ", " YYYYYYY ", " YYYYYYY ", " YY   YY ", " YY   YY ", " YY   YY ", " YYYYYYY ",
                        " YYYYYYY ", "         " },
                    { "         ", "         ", "  YYYYY  ", "  Y   Y  ", "  Y   Y  ", "  Y   Y  ", "  YYYYY  ",
                        "         ", "         " },
                    { "         ", "         ", "  YYYYY  ", "  Y   Y  ", "  Y   Y  ", "  Y   Y  ", "  YYYYY  ",
                        "         ", "         " },
                    { "         ", "         ", "  YYYYY  ", "  Y   Y  ", "  Y   Y  ", "  Y   Y  ", "  YYYYY  ",
                        "         ", "         " },
                    { "         ", "         ", "  YY~YY  ", "  Y   Y  ", "  Y   Y  ", "  Y   Y  ", "  YYYYY  ",
                        "         ", "         " },
                    { "         ", "         ", "  XXXXX  ", "  XXXXX  ", "  XXXXX  ", "  XXXXX  ", "  AAAAA  ",
                        "         ", "         " } }))
        .addShape(
            Tier3,
            transpose(
                new String[][] {
                    { "               ", "               ", "               ", "               ", "               ",
                        "               ", "       M       ", "       M       ", "       M       ", "               ",
                        "               ", "               ", "               ", "               ", "               " },
                    { "               ", "               ", "       M       ", "       M       ", "       M       ",
                        "       M       ", "               ", "               ", "               ", "       M       ",
                        "       M       ", "       M       ", "       M       ", "               ", "               " },
                    { "               ", "               ", "      MMMM     ", "               ", "               ",
                        "               ", "               ", "               ", "               ", "               ",
                        "               ", "               ", "      MMM      ", "               ", "               " },
                    { "     MMMMM     ", "    MMMMMMM    ", "  MMMMMMMMMMM  ", "  MMMPPPPPMMM  ", " MMMPPPPPPPMMM ",
                        "MMMPPPPPPPPPMMM", "MMMPPPPPPPPPMMM", "MMMPPPPPPPPPMMM", "MMMPPPPPPPPPMMM", "MMMPPPPPPPPPMMM",
                        " MMMPPPPPPPMMM ", "  MMMPPPPPMMM  ", "  MMMMMMMMMMM  ", "    MMMMMMM    ", "     MMMMM     " },
                    { "               ", "      MMM      ", "     M   M     ", "   MM     MM   ", "   M       M   ",
                        "  M         M  ", " M           M ", " M           M ", " M           M ", "  M         M  ",
                        "   M       M   ", "   MM     MM   ", "     M   M     ", "      MMM      ", "               " },
                    { "               ", "      MMM      ", "     M   M     ", "   MM     MM   ", "   M       M   ",
                        "  M         M  ", " M           M ", " M           M ", " M           M ", "  M         M  ",
                        "   M       M   ", "   MM     MM   ", "     M   M     ", "      MMM      ", "               " },
                    { "               ", "      MMM      ", "     M   M     ", "   MM     MM   ", "   M       M   ",
                        "  M         M  ", " M           M ", " M           M ", " M           M ", "  M         M  ",
                        "   M       M   ", "   MM     MM   ", "     M   M     ", "      MMM      ", "               " },
                    { "               ", "      MMM      ", "     M   M     ", "   MM     MM   ", "   M       M   ",
                        "  M         M  ", " M           M ", " M           M ", " M           M ", "  M         M  ",
                        "   M       M   ", "   MM     MM   ", "     M   M     ", "      MMM      ", "               " },
                    { "               ", "      MMM      ", "     M   M     ", "   MM     MM   ", "   M       M   ",
                        "  M         M  ", " M           M ", " M           M ", " M           M ", "  M         M  ",
                        "   M       M   ", "   MM     MM   ", "     M   M     ", "      MMM      ", "               " },
                    { "               ", "      MMM      ", "     M   M     ", "   MM     MM   ", "   M       M   ",
                        "  M         M  ", " M           M ", " M           M ", " M           M ", "  M         M  ",
                        "   M       M   ", "   MM     MM   ", "     M   M     ", "      MMM      ", "               " },
                    { "               ", "               ", "     MMMMM     ", "   MMMMMMMMM   ", "   MMM   MMM   ",
                        "  MMM     MMM  ", "  MM       MM  ", "  MM       MM  ", "  MM       MM  ", "  MMM     MMM  ",
                        "   MMM   MMM   ", "   MMMMMMMMM   ", "     MMMMM     ", "               ", "               " },
                    { "               ", "               ", "               ", "      MMM      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "      MMM      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "      MMM      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "      MMM      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "      MMM      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "      MMM      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "      MMM      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "      M~M      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "      MMM      ", "    MM   MM    ",
                        "    M     M    ", "   M       M   ", "   M       M   ", "   M       M   ", "    M     M    ",
                        "    MM   MM    ", "      MMM      ", "               ", "               ", "               " },
                    { "               ", "               ", "               ", "   MMMMMMMMM   ", "   MMMMMMMMM   ",
                        "   MMMMMMMMM   ", "   MMMMMMMMM   ", "   MMMMMMMMM   ", "   MMMMMMMMM   ", "   MMMMMMMMM   ",
                        "   MMMMMMMMM   ", "   MMMMMMMMM   ", "               ", "               ", "               " }

                }))
        .addShape(
            Tier4,
            transpose(
                new String[][] {
                    { "                   ", "                   ", "                   ", "                   ",
                        "                   ", "                   ", "                   ", "         E         ",
                        "         E         ", "         E         ", "         E         ", "         E         ",
                        "                   ", "                   ", "                   ", "                   ",
                        "                   ", "                   ", "                   " },
                    { "                   ", "                   ", "                   ", "                   ",
                        "                   ", "         E         ", "         E         ", "                   ",
                        "                   ", "                   ", "                   ", "                   ",
                        "         E         ", "         E         ", "                   ", "                   ",
                        "                   ", "                   ", "                   " },
                    { "                   ", "                   ", "         E         ", "         E         ",
                        "         E         ", "                   ", "                   ", "                   ",
                        "                   ", "                   ", "                   ", "                   ",
                        "                   ", "                   ", "         E         ", "         E         ",
                        "         E         ", "                   ", "                   " },
                    { "                   ", "        EEE        ", "                   ", "                   ",
                        "                   ", "                   ", "                   ", "                   ",
                        "                   ", "                   ", "                   ", "                   ",
                        "                   ", "                   ", "                   ", "                   ",
                        "                   ", "        EEE        ", "                   " },
                    { "                   ", "     EEEEEEEEE     ", "   EEE1111111EEE   ", "  EE11111111111EE  ",
                        "  E1111111111111E  ", " EE1111111111111EE ", " E111111111111111E ", " E111111111111111E ",
                        " E111111111111111E ", " E111111111111111E ", " E111111111111111E ", " E111111111111111E ",
                        " E111111111111111E ", " EE1111111111111EE ", "  E1111111111111E  ", "  EE11111111111EE  ",
                        "   EEE1111111EEE   ", "     EEEEEEEEE     ", "                   " },
                    { "     EEEEEEEEE     ", "   EE         EE   ", "  E             E  ", " E               E ",
                        " E               E ", "E                 E", "E                 E", "E                 E",
                        "E                 E", "E                 E", "E                 E", "E                 E",
                        "E                 E", "E                 E", " E               E ", " E               E ",
                        "  E             E  ", "   EE         EE   ", "     EEEEEEEEE     " },
                    { "     EE11111EE     ", "   11         11   ", "  E             E  ", " 1               1 ",
                        " 1               1 ", "E                 E", "E                 E", "1                 1",
                        "1                 1", "1                 1", "1                 1", "1                 1",
                        "E                 E", "E                 E", " 1               1 ", " 1               1 ",
                        "  E             E  ", "   11         11   ", "     EE11111EE     " },
                    { "     EE11111EE     ", "   11         11   ", "  E             E  ", " 1               1 ",
                        " 1               1 ", "E                 E", "E                 E", "1                 1",
                        "1                 1", "1                 1", "1                 1", "1                 1",
                        "E                 E", "E                 E", " 1               1 ", " 1               1 ",
                        "  E             E  ", "   11         11   ", "     EE11111EE     " },
                    { "     EE11111EE     ", "   11         11   ", "  E             E  ", " 1               1 ",
                        " 1               1 ", "E                 E", "E                 E", "1                 1",
                        "1                 1", "1                 1", "1                 1", "1                 1",
                        "E                 E", "E                 E", " 1               1 ", " 1               1 ",
                        "  E             E  ", "   11         11   ", "     EE11111EE     " },
                    { "     EE11111EE     ", "   11         11   ", "  E             E  ", " 1               1 ",
                        " 1               1 ", "E                 E", "E                 E", "1                 1",
                        "1                 1", "1                 1", "1                 1", "1                 1",
                        "E                 E", "E                 E", " 1               1 ", " 1               1 ",
                        "  E             E  ", "   11         11   ", "     EE11111EE     " },
                    { "     EE11111EE     ", "   EE         EE   ", "  E             E  ", " E               E ",
                        " E               E ", "E                 E", "E                 E", "1                 1",
                        "1                 1", "1                 1", "1                 1", "1                 1",
                        "E                 E", "E                 E", " E               E ", " E               E ",
                        "  E             E  ", "   EE         EE   ", "     EE11111EE     " },
                    { "     EEEEEEEEE     ", "   EEEEEEEEEEEEE   ", "  EEEEE11111EEEEE  ", " EEEE111111111EEEE ",
                        " EEE111     111EEE ", "EEE11         11EEE", "EEE11         11EEE", "EE11           11EE",
                        "EE11           11EE", "EE11           11EE", "EE11           11EE", "EE11           11EE",
                        "EEE11         11EEE", "EEE11         11EEE", " EEE111     111EEE ", " EEEE111111111EEEE ",
                        "  EEEEE11111EEEEE  ", "   EEEEEEEEEEEEE   ", "     EEEEEEEEE     " },
                    { "                   ", "      EEEEEEE      ", "    EEEDDDDDEEE    ", "   EEDDFFFFFDDEE   ",
                        "  EEDFF     FFDEE  ", "  EDF         FDE  ", " EEDF         FDEE ", " EDF           FDE ",
                        " EDF           FDE ", " EDF           FDE ", " EDF           FDE ", " EDF           FDE ",
                        " EEDF         FDEE ", "  EDF         FDE  ", "  EEDFF     FFDEE  ", "   EEDDFFFFFDDEE   ",
                        "    EEEDDDDDEEE    ", "      EEEEEEE      ", "                   " },
                    { "                   ", "       1EEE1       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       11E11       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       1EEE1       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       1EEE1       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       1EEE1       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CEEEC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CEEEC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CNENC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CNENC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CNNNC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CNNNC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CNNNC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CNNNC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CNNNC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CN~NC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        "  DF           FD  ", "  DF           FD  ", "  DF           FD  ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "                   ", "                   " },
                    { "                   ", "       CNNNC       ", "     11DDDDD11     ", "   11DDFFFFFDD11   ",
                        "   1DFF     FFD1   ", "  1DF         FD1  ", "  1DF         FD1  ", "  DF           FD  ",
                        " NDF           FDN ", " NDF           FDN ", " NDF           FDN ", "  DF           FD  ",
                        "  1DF         FD1  ", "  1DF         FD1  ", "   1DFF     FFD1   ", "   11DDFFFFFDD11   ",
                        "     11DDDDD11     ", "        NNN        ", "                   " },
                    { "                   ", "       CNNNC       ", "     GGNNNNNGG     ", "   GGNNNNNNNNNGG   ",
                        "   GNNNNNNNNNNNG   ", "  GNNNNNNNNNNNNNG  ", "  GNNNNNNNNNNNNNG  ", " CNNNNNNNNNNNNNNNC ",
                        " NNNNNNNNNNNNNNNNN ", " NNNNNNNNNNNNNNNNN ", " NNNNNNNNNNNNNNNNN ", " CNNNNNNNNNNNNNNNC ",
                        "  GNNNNNNNNNNNNNG  ", "  GNNNNNNNNNNNNNG  ", "   GNNNNNNNNNNNG   ", "   GGNNNNNNNNNGG   ",
                        "     GGNNNNNGG     ", "       CNNNC       ", "                   " },
                    { "                   ", "       CNNNC       ", "  NNNNNNNNNNNNNNN  ", "  NNNNNNNNNNNNNNN  ",
                        "  NNNNNNNNNNNNNNN  ", "  NNNNNNNNNNNNNNN  ", "  NNNNNNNNNNNNNNN  ", " CNNNNNNNNNNNNNNNC ",
                        " NNNNNNNNNNNNNNNNN ", " NNNNNNNNNNNNNNNNN ", " NNNNNNNNNNNNNNNNN ", " CNNNNNNNNNNNNNNNC ",
                        "  NNNNNNNNNNNNNNN  ", "  NNNNNNNNNNNNNNN  ", "  NNNNNNNNNNNNNNN  ", "  NNNNNNNNNNNNNNN  ",
                        "  NNNNNNNNNNNNNNN  ", "       CNNNC       ", "                   " }

                }))
        .addElement(
            'A',
            buildHatchAdder(OTELargeBin.class).atLeast(InputBus, InputHatch, Energy.or(ExoticEnergy), Dynamo, OutputBus)
                .dot(1)
                .casingIndex(((BlockCasings2) sBlockCasings2).getTextureIndex(0))
                .buildAndChain(sBlockCasings2, 0))
        .addElement(
            'Y',
            buildHatchAdder(OTELargeBin.class).atLeast(InputBus, InputHatch, Energy.or(ExoticEnergy), Dynamo, OutputBus)
                .dot(1)
                .casingIndex(((BlockCasings4) sBlockCasings4).getTextureIndex(2))
                .buildAndChain(sBlockCasings4, 2))
        .addElement(
            'M',
            buildHatchAdder(OTELargeBin.class).atLeast(InputBus, InputHatch, Energy.or(ExoticEnergy), Dynamo, OutputBus)
                .dot(1)
                .casingIndex(((BlockCasings4) sBlockCasings4).getTextureIndex(0))
                .buildAndChain(sBlockCasings4, 0))
        .addElement(
            'N',
            buildHatchAdder(OTELargeBin.class).atLeast(InputBus, InputHatch, Energy.or(ExoticEnergy), Dynamo, OutputBus)
                .dot(1)
                .casingIndex(((BlockCasings1) sBlockCasings1).getTextureIndex(12))
                .buildAndChain(sBlockCasings1, 12))
        .addElement('Z', ofBlock(sBlockCasings3, 10))
        .addElement('X', ofBlock(sBlockCasings2, 0))
        .addElement('P', BorosilicateGlass.ofBoroGlass(3))
        .addElement('E', ofBlock(sBlockCasings10, 8))
        .addElement('C', ofBlock(sBlockCasings1, 14))
        .addElement('1', BorosilicateGlass.ofBoroGlass(9))
        .addElement('D', ofBlock(sBlockCasings10, 6))
        .addElement('F', ofBlock(sBlockCasings9, 1))
        .addElement('G', ofBlock(sBlockCasingsTT, 6))
        .build();

    // endregion
    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("mode", this.mode);
        aNBT.setInteger("Tier", this.Tier);
        aNBT.setInteger("MSA", this.mSA);
        aNBT.setByte("progressingTickIndex", progressingTickIndex);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        this.mode = aNBT.getInteger("mode");
        mSA = aNBT.getInteger("MSA");
        Tier = aNBT.getInteger("Tier");
        progressingTickIndex = aNBT.getByte("progressingTickIndex");
        super.loadNBTData(aNBT);

    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            String Mode = GTUtility.formatNumbers(mode);
            tag.setString("mode", Mode);
            tag.setLong("MM", mSA);
            tag.setInteger("Tier", Tier);
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
                + tag.getString("mode")
                + EnumChatFormatting.RESET);
        currentTip.add(
            "奇点模式物品输入数量" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getLong("MM")
                + EnumChatFormatting.RESET);
        currentTip.add(
            "结构等级" + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getLong("Tier")
                + EnumChatFormatting.RESET);
    }

    public OTELargeBin(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTELargeBin(String aName) {
        super(aName);
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTELargeBin(mName);
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new ProcessingLogic() {

            @NotNull
            @Override
            protected CheckRecipeResult validateRecipe(@NotNull GTRecipe recipe) {
                return super.validateRecipe(recipe);
            }
        };
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return mode == 1;
    }

    @Override
    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    protected int getMaxParallelRecipes() {
        return mode == 1 ? (GTUtility.getTier(this.getMaxInputVoltage()) * 2560) : 256;
    }

    @Override
    @NotNull
    public CheckRecipeResult checkProcessing() {

        setupProcessingLogic(processingLogic);

        if (mode == 3) {
            CheckRecipeResult result = doCheckRecipe();
            result = postCheckRecipe(result, processingLogic);
            updateSlots();
            if (!result.wasSuccessful()) return result;

            mEfficiency = 10000;
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = getBaseProgressingTick();
            mOutputItems = processingLogic.getOutputItems();
            mOutputFluids = processingLogic.getOutputFluids();
            setEnergyUsage(processingLogic);
        }
        if (mode == 0) {
            mEUt = 0;
            mMaxProgresstime = getBaseProgressingTick();
            ItemStack[] itemStack = getStoredInputsNoSeparation().toArray(new ItemStack[0]);
            FluidStack[] fluidStack = getStoredFluids().toArray(new FluidStack[0]);
            if (getStoredInputsNoSeparation() != null) {
                for (ItemStack item : itemStack) {
                    item.stackSize -= item.stackSize;
                }
            }
            if (getStoredFluids() != null) {
                for (FluidStack fluid : fluidStack) {
                    fluid.amount -= fluid.amount;
                }
            }

        } else if (mode == 1) {
            mMaxProgresstime = getBaseProgressingTick();
            int a;
            ItemStack[] itemStack = getStoredInputsNoSeparation().toArray(new ItemStack[0]);
            for (ItemStack item : itemStack) {
                if (item.stackSize <= getMaxParallelRecipes()) {
                    a = item.stackSize;
                    mEUt = (30 * a);
                    item.stackSize -= item.stackSize;
                    mOutputItems = new ItemStack[] {
                        setStackSize(ItemList.IC2_Scrap.get(1), (int) Math.floor(a * 0.5)) };
                } else {
                    a = getMaxParallelRecipes();
                    mEUt = (30 * a);
                    item.stackSize -= getMaxParallelRecipes();
                    mOutputItems = new ItemStack[] {
                        setStackSize(ItemList.IC2_Scrap.get(1), (int) Math.floor(a * 0.5)) };
                }
            }
        } else if (mode == 2) {
            mMaxProgresstime = getBaseProgressingTick();
            ItemStack[] itemStack = getStoredInputsNoSeparation().toArray(new ItemStack[0]);
            for (ItemStack item : itemStack) {
                if (item.stackSize >= 256000) {
                    mSA += 256000;
                    item.stackSize -= 256000;
                    if (mSA >= 256000) {
                        mOutputItems = new ItemStack[] {
                            GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 47) };
                        mSA -= 256000;
                    }
                } else {
                    mSA += item.stackSize;
                    item.stackSize -= item.stackSize;
                    if (mSA >= 256000) {
                        mOutputItems = new ItemStack[] {
                            GTModHandler.getModItem(AppliedEnergistics2.ID, "item.ItemMultiMaterial", 1, 47) };
                        mSA -= 256000;
                    }
                }
            }
        }
        return CheckRecipeResultRegistry.NO_RECIPE;
    }

    @Override
    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (getBaseMetaTileEntity().isServerSide()) {
            if (mode < Tier - 1) {
                mode++;
            } else {
                mode = 0;
            }
            GTUtility.sendChatToPlayer(
                aPlayer,
                StatCollector.translateToLocal(
                    mode == 0 ? "垃圾桶模式"
                        : mode == 1 ? "回收机模式" : mode == 2 ? "AE奇点制造机模式" : mode == 3 ? "中子态素压缩机模式" : "Null"));
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 20 == 0) {
            ItemStack aGuiStack = this.getControllerSlot();
            if (aGuiStack != null) {
                Tier = aGuiStack.getItemDamage();
            }
        }
    }

    protected int getBaseProgressingTick() {
        return progressingTick[progressingTickIndex];
    }

    @Override
    public boolean onWireCutterRightClick(ForgeDirection side, ForgeDirection wrenchingSide, EntityPlayer aPlayer,
        float aX, float aY, float aZ, ItemStack aTool) {
        if (getBaseMetaTileEntity().isServerSide()) {
            this.progressingTickIndex = (byte) ((this.progressingTickIndex + 1) % 10);
            GTUtility
                .sendChatToPlayer(aPlayer, StatCollector.translateToLocal("目前耗时") + getBaseProgressingTick() + " tick");

            return true;
        }
        return false;
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("垃圾桶丨回收机丨AE奇点制造机丨中子态素压缩机"))
            .addInfo(translateToLocal("销毁一切, 仅此而已吗?"))
            .addInfo("支持存储输入系列, 螺丝刀切换模式, 剪线钳切换耗时")
            .addInfo("回收机模式功率为并行 * 30EU/t")
            .addInfo("于回收机模式下, 电压等级 + 1, 并行 + 2560, 完全固定输出输入物品数量50%的废料")
            .addInfo("于奇点制造机模式下, 并行为1, 每256000任意物品制作一个奇点")
            .addInfo("于中子态素压缩机模式下, 执行有损超频, 并行为256")
            .addInfo("升级结构获得模式升级, 主机放入编程电路编辑结构等级")
            .addInfo("一级结构 : 50x脱氧钢机械方块")
            .addInfo("二级结构 : 245x加强钛机械方块, 12x格栅机械方块, 45x脱氧钢机械方块")
            .addInfo("三级结构 : 670x强化钨钢机械方块, 69x硼玻璃方块")
            .addInfo(
                "四级结构 : 459x超维度机械方块, 38x维度桥接机械方块, 28x遏制场发生器, 893x中子强化硼玻璃方块, 478x中子稳定机械方块, 680x中子机械方块, 612x进阶格栅机械方块")
            .addTecTechHatchInfo()
            .beginStructureBlock(1, 3, 1, false)
            .addController(translateToLocal("结构正中心"))
            .addCasingInfoMin(translateToLocal("0x 脱氧钢机械方块(最低)"), 5, false)
            .addEnergyHatch(translateToLocal("任意输入总线, 输入仓, 输出总线, 能源仓, 动力仓"), 1)
            .toolTipFinisher("§a123Technology - JUST A WASTE BIN");
        return tt;
    }

    protected static Textures.BlockIcons.CustomIcon ScreenOFF;
    protected static Textures.BlockIcons.CustomIcon ScreenON;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aBlockIconRegister) {
        ScreenOFF = new Textures.BlockIcons.CustomIcon("iconsets/EM_CONTROLLER");
        ScreenON = new Textures.BlockIcons.CustomIcon("iconsets/EM_CONTROLLER_ACTIVE");
        super.registerIcons(aBlockIconRegister);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] {
                Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)),
                new TTRenderedExtendedFacingTexture(aActive ? ScreenON : ScreenOFF) };
        }
        return new ITexture[] {
            Textures.BlockIcons.getCasingTextureForId(GTUtility.getCasingTextureIndex(sBlockCasings2, 0)) };
    }

    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.TECTECH_MACHINES_NOISE;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean doRandomMaintenanceDamage() {
        return true;
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
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
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }
}
