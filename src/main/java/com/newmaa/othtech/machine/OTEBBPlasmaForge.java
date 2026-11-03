package com.newmaa.othtech.machine;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.VN;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_OFF;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_ON;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FUSION1_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.activeCoils;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static gregtech.api.util.GTUtility.validMTEList;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static gregtech.common.misc.WirelessNetworkManager.getUserEU;
import static net.minecraft.util.StatCollector.translateToLocal;
import static tectech.thing.casing.TTCasingsContainer.sBlockCasingsTT;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

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

import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.newmaa.othtech.machine.gui.OTEBBPlasmaForgeGui;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.utils.Utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechDeviceInformation;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.render.TextureFactory;
import gregtech.api.structure.error.ErrorType;
import gregtech.api.structure.error.StructureError;
import gregtech.api.structure.error.StructureErrorRegistry;
import gregtech.api.structure.error.StructureErrors;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;
import gregtech.api.util.GTUtility;
import gregtech.api.util.MultiblockTooltipBuilder;
import gregtech.api.util.OverclockCalculator;
import gregtech.common.gui.modularui.multiblock.base.MTEMultiBlockBaseGui;
import gregtech.common.misc.GTStructureChannels;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.block.BlockQuantumGlass;

public class OTEBBPlasmaForge extends OTHMultiMachineBase<OTEBBPlasmaForge> implements ISurvivalConstructable {

    private int mHeatingCapacity = 0;

    private int MLevel = 1;

    private boolean failure = false;

    private HeatingCoilLevel mCoilLevel;

    private UUID ownerUUID;

    private static IStructureDefinition<OTEBBPlasmaForge> STRUCTURE_DEFINITION = null;

    private boolean isWirelessMode = false;

    private String costingWirelessEU = "0";

    private OverclockCalculator overclockCalculator;

    @SuppressWarnings("SpellCheckingInspection")
    private final String[][] shapeMain = new String[][] {
        { "                                               ", "                                               ",
            "                                               ", "                                               ",
            "                                               ", "                                               ",
            "                                               ", "                                               ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "                                               ",
            "                    BAAAAAB                    ", "                    BACCCAB                    ",
            "                    BAAAAAB                    ", "                                               ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "                    BACCCAB                    ",
            "                   AA     AA                   ", "                   AA     AA                   ",
            "                   AA     AA                   ", "                    BACCCAB                    ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                    BAAAAAB                    ", "                   AA     AA                   ",
            "                AAAAA     AAAAA                ", "                AAADDDDDDDDDAAA                ",
            "                AAAAA     AAAAA                ", "                   AA     AA                   ",
            "                    BAAAAAB                    ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                    BACCCAB                    ", "                AAAAA     AAAAA                ",
            "              AAAAADDDDDDDDDAAAAA              ", "              AADDDDDDDDDDDDDDDAA              ",
            "              AAAAADDDDDDDDDAAAAA              ", "                AAAAA     AAAAA                ",
            "                    BACCCAB                    ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                    BAAAAAB                    ", "              AAAAAAA     AAAAAAA              ",
            "            AAAADDDAA     AADDDAAAA            ", "            AADDDDDDDDDDDDDDDDDDDAA            ",
            "            AAAADDDAA     AADDDAAAA            ", "              AAAAAAA     AAAAAAA              ",
            "                    BAAAAAB                    ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "            AAAAAAA BACCCAB AAAAAAA            ",
            "           AAADDAAAAA     AAAAADDAAA           ", "           ADDDDDDDAA     AADDDDDDDA           ",
            "           AAADDAAAAA     AAAAADDAAA           ", "            AAAAAAA BACCCAB AAAAAAA            ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "           AAAAA               AAAAA           ",
            "          AADDAAAAA BAAAAAB AAAAADDAA          ", "          ADDDDDAAA BACCCAB AAADDDDDA          ",
            "          AADDAAAAA BAAAAAB AAAAADDAA          ", "           AAAAA               AAAAA           ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "          AAAA                   AAAA          ",
            "         AADAAAA               AAAADAA         ", "         ADDDDAA               AADDDDA         ",
            "         AADAAAA               AAAADAA         ", "          AAAA                   AAAA          ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "         AAA                       AAA         ",
            "        AADAAA                   AAADAA        ", "        ADDDAA                   AADDDA        ",
            "        AADAAA                   AAADAA        ", "         AAA                       AAA         ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "        AAA                         AAA        ",
            "       AADAA                       AADAA       ", "       ADDDA                       ADDDA       ",
            "       AADAA                       AADAA       ", "        AAA                         AAA        ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "       AAA                           AAA       ",
            "      AADAA                         AADAA      ", "      ADDDA                         ADDDA      ",
            "      AADAA                         AADAA      ", "       AAA                           AAA       ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "      AAA                             AAA      ",
            "     AADAA                           AADAA     ", "     ADDDA                           ADDDA     ",
            "     AADAA                           AADAA     ", "      AAA                             AAA      ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "     AAA                               AAA     ",
            "    AADAA                             AADAA    ", "    ADDDA                             ADDDA    ",
            "    AADAA                             AADAA    ", "     AAA                               AAA     ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "     AAA                               AAA     ",
            "    AADAA                             AADAA    ", "    ADDDA                             ADDDA    ",
            "    AADAA                             AADAA    ", "     AAA                               AAA     ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "    AAA                                 AAA    ",
            "   AADAA                               AADAA   ", "   ADDDA                               ADDDA   ",
            "   AADAA                               AADAA   ", "    AAA                                 AAA    ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "    AAA                                 AAA    ",
            "   AADAA                               AADAA   ", "   ADDDA                               ADDDA   ",
            "   AADAA                               AADAA   ", "    AAA                                 AAA    ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "   AAA                                   AAA   ",
            "  AADAA                                 AADAA  ", "  ADDDA                                 ADDDA  ",
            "  AADAA                                 AADAA  ", "   AAA                                   AAA   ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "   AAA                                   AAA   ",
            "  AADAA                                 ADDAA  ", "  ADDDA                                 ADDDA  ",
            "  AADAA                                 AADAA  ", "   AAA                                   AAA   ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "   AAA                                   AAA   ",
            "  AADAA                                 AADAA  ", "  ADDDA                                 ADDDA  ",
            "  AADAA                                 AADAA  ", "   AAA                                   AAA   ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                      AAA                      ", "  AAA                ABBBA                AAA  ",
            " AADAA              ABBBBBA              AADAA ", " ADDDA              ABB~BBA              ADDDA ",
            " AADAA              ABBBBBA              AADAA ", "  AAA                ABBBA                AAA  ",
            "                      AAA                      ", "                                               ",
            "                                               " },
        { "                                               ", "                      AAA                      ",
            "  BBB                AAAAA                BBB  ", " BAAAB              ABBBBBA              BAAAB ",
            "BAADAAB            AABCCCBAA            BAADAAB", "BADDDAB            AABCCCBAA            BADDDAB",
            "BAADAAB            AABCCCBAA            BAADAAB", " BAAAB              ABBBBBA              BAAAB ",
            "  BBB                AAAAA                BBB  ", "                      AAA                      ",
            "                                               " },
        { "                                               ", "                     AAAAA                     ",
            "  AAA               ABCBCBA               AAA  ", " A   A             ABDDDDDBA             A   A ",
            "A  D  A             BDGGGDB             A  D  A", "A DDD A             BDGGGDB             A DDD A",
            "A  D  A             BDGGGDB             A  D  A", " A   A             ABDDDDDBA             A   A ",
            "  AAA               ABCBCBA               AAA  ", "                     AAAAA                     ",
            "                                               " },
        { "                                               ", "                    AABBBAA                    ",
            "  ACA              AACBCBCAA              ACA  ", " C   C              BDGGGDB              C   C ",
            "A  D  A             CGEEEGC             A  D  A", "C DDD C             CGEEEGC             C DDD C",
            "A  D  A             CGEEEGC             A  D  A", " C   C              BDGGGDB              C   C ",
            "  ACA              AACBCBCAA              ACA  ", "                    AABBBAA                    ",
            "                                               " },
        { "                                               ", "                    AABBBAA                    ",
            "  ACA              AABCBCBAA              ACA  ", " C   C              BDGGGDB              C   C ",
            "A  D  A             CGEEEGC             A  D  A", "C DDD C             CGEFEGC             C DDD C",
            "A  D  A             CGEEEGC             A  D  A", " C   C              BDGGGDB              C   C ",
            "  ACA              AABCBCBAA              ACA  ", "                    AABBBAA                    ",
            "                                               " },
        { "                                               ", "                    AABBBAA                    ",
            "  ACA              AACBCBCAA              ACA  ", " C   C              BDGGGDB              C   C ",
            "A  D  A             CGEEEGC             A  D  A", "C DDD C             CGEEEGC             C DDD C",
            "A  D  A             CGEEEGC             A  D  A", " C   C              BDGGGDB              C   C ",
            "  ACA              AACBCBCAA              ACA  ", "                    AABBBAA                    ",
            "                                               " },
        { "                                               ", "                     AAAAA                     ",
            "  AAA               ABCBCBAA              AAA  ", " A   A             ABDDDDDBA             A   A ",
            "A  D  A             BDGGGDB             A  D  A", "A DDD A             BDGGGDB             A DDD A",
            "A  D  A             BDGGGDB             A  D  A", " A   A             ABDDDDDBA             A   A ",
            "  AAA               ABCBCBA               AAA  ", "                     AAAAA                     ",
            "                                               " },
        { "                                               ", "                      AAA                      ",
            "  BBB                AAAAA                BBB  ", " BAAAB              ABBBBBA              BAAAB ",
            "BAADAAB            AABCCCBAA            BAADAAB", "BADDDAB            AABCCCBAA            BADDDAB",
            "BAADAAB            AABCCCBAA            BAADAAB", " BAAAB              ABBBBBA              BAAAB ",
            "  BBB                AAAAA                BBB  ", "                      AAA                      ",
            "                                               " },
        { "                                               ", "                                               ",
            "                      AAA                      ", "  AAA                A   A                AAA  ",
            " AADAA              A     A              AADAA ", " ADDDA              A     A              ADDDA ",
            " AADAA              A     A              AADAA ", "  AAA                A   A                AAA  ",
            "                      AAA                      ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "   AAA                                   AAA   ",
            "  AADAA                                 AADAA  ", "  ADDDA                                 ADDDA  ",
            "  AADAA                                 AADAA  ", "   AAA                                   AAA   ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "   AAA                                   AAA   ",
            "  AADAA                                 AADAA  ", "  ADDDA                                 ADDDA  ",
            "  AADAA                                 AADAA  ", "   AAA                                   AAA   ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "   AAA                                   AAA   ",
            "  AADAA                                 AADAA  ", "  ADDDA                                 ADDDA  ",
            "  AADAA                                 AADAA  ", "   AAA                                   AAA   ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "    AAA                                 AAA    ",
            "   AADAA                               AADAA   ", "   ADDDA                               ADDDA   ",
            "   AADAA                               AADAA   ", "    AAA                                 AAA    ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "    AAA                                 AAA    ",
            "   AADAA                               AADAA   ", "   ADDDA                               ADDDA   ",
            "   AADAA                               AADAA   ", "    AAA                                 AAA    ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "     AAA                               AAA     ",
            "    AADAA                             AADAA    ", "    ADDDA                             ADDDA    ",
            "    AADAA                             AADAA    ", "     AAA                               AAA     ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "     AAA                               AAA     ",
            "    AADAA                             AADAA    ", "    ADDDA                             ADDDA    ",
            "    AADAA                             AADAA    ", "     AAA                               AAA     ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "      AAA                             AAA      ",
            "     AADAA                           AADAA     ", "     ADDDA                           ADDDA     ",
            "     AADAA                           AADAA     ", "      AAA                             AAA      ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "       AAA                           AAA       ",
            "      AADAA                         AADAA      ", "      ADDDA                         ADDDA      ",
            "      AADAA                         AADAA      ", "       AAA                           AAA       ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "        AAA                         AAA        ",
            "       AADAA                       AADAA       ", "       ADDDA                       ADDDA       ",
            "       AADAA                       AADAA       ", "        AAA                         AAA        ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "         AAA                       AAA         ",
            "        AADAAA                   AAADAA        ", "        ADDDAA                   AADDDA        ",
            "        AADAAA                   AAADAA        ", "         AAA                       AAA         ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "          AAAA                   AAAA          ",
            "         AADAAAA               AAAADAA         ", "         ADDDDAA               AADDDDA         ",
            "         AADAAAA               AAAADAA         ", "          AAAA                   AAAA          ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "           AAAAA               AAAAA           ",
            "          AADDAAAAA BAAAAAB AAAAADDAA          ", "          ADDDDDAAA BACCCAB AAADDDDDA          ",
            "          AADDAAAAA BAAAAAB AAAAADDAA          ", "           AAAAA               AAAAA           ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "            AAAAAAA BACCCAB AAAAAAA            ",
            "           AAADDAAAAA     AAAAADDAAA           ", "           ADDDDDDDAA     AADDDDDDDA           ",
            "           AAADDAAAAA     AAAAADDAAA           ", "            AAAAAAA BACCCAB AAAAAAA            ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                    BAAAAAB                    ", "              AAAAAAA     AAAAAAA              ",
            "            AAAADDDAA     AADDDAAAA            ", "            AADDDDDDDDDDDDDDDDDDDAA            ",
            "            AAAADDDAA     AADDDAAAA            ", "              AAAAAAA     AAAAAAA              ",
            "                    BAAAAAB                    ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                    BACCCAB                    ", "                AAAAA     AAAAA                ",
            "              AAAAADDDDDDDDDAAAAA              ", "              AADDDDDDDDDDDDDDDAA              ",
            "              AAAAADDDDDDDDDAAAAA              ", "                AAAAA     AAAAA                ",
            "                    BACCCAB                    ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                    BAAAAAB                    ", "                   AA     AA                   ",
            "                AAAAA     AAAAA                ", "                AAADDDDDDDDDAAA                ",
            "                AAAAA     AAAAA                ", "                   AA     AA                   ",
            "                    BAAAAAB                    ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "                    BACCCAB                    ",
            "                   AA     AA                   ", "                   AA     AA                   ",
            "                   AA     AA                   ", "                    BACCCAB                    ",
            "                                               ", "                                               ",
            "                                               " } };

    protected static final int DIM_INJECTION_CASING = 13;

    protected static final String STRUCTURE_PIECE_MAIN = "main";

    // ===================== Constructors =====================

    public OTEBBPlasmaForge(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEBBPlasmaForge(String aName) {
        super(aName);
    }

    // ===================== NBT =====================

    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setBoolean("wireless", isWirelessMode);
        aNBT.setInteger("MLevel", MLevel);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        MLevel = aNBT.getInteger("MLevel");
        isWirelessMode = aNBT.getBoolean("wireless");
        super.loadNBTData(aNBT);
    }

    // ===================== Machine Parameters =====================

    protected boolean isEnablePerfectOverclock() {
        return MLevel >= 2;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (MLevel == 2) {
            return Integer.MAX_VALUE;
        } else {
            return mCoilLevel == null ? 0 : 1230 + mCoilLevel.getTier() * 1230;
        }
    }

    protected float getSpeedBonus() {
        if (getCoilTier() == 14) {
            return 0.01F;
        }
        return 1.0F - (getCoilTier() - 1) * 0.08F;
    }

    protected float getEuModifier() {
        if (getCoilTier() == 14) {
            return 0.01F;
        }
        return 1.0F - (getCoilTier() - 1) * 0.08F;
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.plasmaForgeRecipes;
    }

    @Override
    public boolean supportsVoidProtection() {
        return super.supportsVoidProtection();
    }

    @Override
    public boolean supportsBatchMode() {
        return super.supportsBatchMode();
    }

    public HeatingCoilLevel getCoilLevel() {
        return mCoilLevel;
    }

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        mCoilLevel = aCoilLevel;
    }

    public int getCoilTier() {
        return Utils.getCoilTier(mCoilLevel);
    }

    // ===================== Processing Logic =====================

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTHProcessingLogic() {

            BigInteger recipeEU;
            BigInteger finalConsumption = BigInteger.ZERO;

            @Override
            public ProcessingLogic setSpeedBonus(double speedModifier) {
                return super.setSpeedBonus(getSpeedBonus());
            }

            @Override
            public ProcessingLogic setEuModifier(double EuModifier) {
                return super.setEuModifier(getEuModifier());
            }

            private float getEuModifier() {
                if (getCoilTier() == 14) {
                    return 0.01F;
                }
                return 1.0F - (getCoilTier() - 1) * 0.08F;
            }

            private float getSpeedBonus() {
                if (getCoilTier() == 14) {
                    return 0.01F;
                }
                return 1.0F - (getCoilTier() - 1) * 0.08F;
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                overclockCalculator = super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(mHeatingCapacity);

                if (MLevel >= 2) {
                    overclockCalculator = overclockCalculator.enablePerfectOC();
                }

                return overclockCalculator;
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                if (failure) {
                    return SimpleCheckRecipeResult.ofFailure("nohatch");
                }
                setSpeedBonus(getSpeedBonus());
                setEuModifier(getEuModifier());
                setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);

                // 加热检查
                if (recipe.mSpecialValue > mHeatingCapacity) {
                    return CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
                }

                // 无线模式能量检查
                if (isWirelessMode) {
                    // 1. 获取用户可用能量
                    BigInteger availableEU = getUserEU(ownerUUID);

                    // 2. 获取能量乘数并计算单配方能量
                    long multiplier = recipe.getMetadataOrDefault(GTRecipeConstants.EU_MULTIPLIER, 1);
                    recipeEU = BigInteger.valueOf(multiplier * recipe.mEUt * recipe.mDuration);

                    // 3. 检查是否有足够能量处理至少一个配方
                    if (availableEU.compareTo(recipeEU) < 0) {
                        finalConsumption = BigInteger.ZERO;
                        return CheckRecipeResultRegistry.insufficientStartupPower(recipeEU);
                    }

                    // 4. 计算基于能量的最大并行数
                    int energyBasedParallel = availableEU.divide(recipeEU)
                        .min(BigInteger.valueOf(Integer.MAX_VALUE))
                        .intValue();

                    // 5. 取机器最大并行数和能量并行数的较小值
                    maxParallel = Math.min(energyBasedParallel, maxParallel);

                    // 6. 再次检查
                    if (maxParallel <= 0) {
                        finalConsumption = BigInteger.ZERO;
                        return CheckRecipeResultRegistry.insufficientStartupPower(recipeEU);
                    }

                    // 7. 计算总能量需求（用于信息显示）
                    BigInteger TotalEU = recipeEU.multiply(BigInteger.valueOf(maxParallel));
                    costingWirelessEU = GTUtility.scientificFormat(TotalEU);
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }

            // @Override
            // protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
            // if (failure) {
            // return SimpleCheckRecipeResult.ofFailure("nohatch");
            // }
            // setSpeedBonus(getSpeedBonus());
            // setEuModifier(getEuModifier());
            // setOverclock(isEnablePerfectOverclock() ? 4 : 2, 4);
            //
            // // 加热检查
            // if (recipe.mSpecialValue > mHeatingCapacity) {
            // return CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
            // }
            // if (isWirelessMode) {
            // BigInteger availableEU = getUserEU(ownerUUID);
            // long multiplier = recipe.getMetadataOrDefault(GTRecipeConstants.EU_MULTIPLIER, 1);
            // recipeEU = BigInteger.valueOf(multiplier * recipe.mEUt * recipe.mDuration);
            // // 计算能量允许的并行数
            // BigInteger energyBasedParallel = availableEU.divide(recipeEU);
            // // 取能量允许的并行数和机器最大并行数的最小值
            // maxParallel = energyBasedParallel.min(BigInteger.valueOf(maxParallel)).intValue();
            // if (maxParallel <= 0) {
            // finalConsumption = BigInteger.ZERO;
            // return CheckRecipeResultRegistry.insufficientStartupPower(recipeEU);
            // }
            // }

            // // 无线模式能量检查
            // if (isWirelessMode) {
            // BigInteger availableEU = getUserEU(ownerUUID);
            // long multiplier = recipe.getMetadataOrDefault(GTRecipeConstants.EU_MULTIPLIER, 1);
            // maxParallel = availableEU.divide(recipeEU)
            // .min(BigInteger.valueOf(maxParallel))
            // .intValue();
            // recipeEU = BigInteger.valueOf(multiplier * recipe.mEUt * recipe.mDuration);
            // BigInteger TotalEU = BigInteger.valueOf(maxParallel).multiply(recipeEU);
            // if (availableEU.compareTo(TotalEU) < 0) {
            // finalConsumption = BigInteger.ZERO;
            // return CheckRecipeResultRegistry.insufficientStartupPower(recipeEU);
            // }
            //// maxParallel = availableEU.divide(recipeEU)
            //// .min(BigInteger.valueOf(maxParallel))
            //// .intValue();
            //
            // }
            //
            // return CheckRecipeResultRegistry.SUCCESSFUL;
            // }

            @NotNull
            @Override
            protected CheckRecipeResult onRecipeStart(@Nonnull GTRecipe recipe) {
                // 无线模式下，在配方开始时扣除能量
                if (isWirelessMode) {
                    finalConsumption = recipeEU.multiply(BigInteger.valueOf(-calculatedParallels));
                    costingWirelessEU = GTUtility.scientificFormat(finalConsumption.abs());
                    // 从无线网络扣除能量
                    if (!addEUToGlobalEnergyMap(ownerUUID, finalConsumption)) {
                        return CheckRecipeResultRegistry.insufficientStartupPower(finalConsumption);
                    }
                    // 能量已一次性扣除，设置 EU/t 为 0
                    overwriteCalculatedEut(0);
                }
                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    @Override
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        if (isWirelessMode) {
            logic.setAvailableVoltage(Long.MAX_VALUE);
            logic.setAvailableAmperage(1);
            logic.setAmperageOC(false);
            logic.setUnlimitedTierSkips();
        } else {
            super.setProcessingLogicPower(logic);
        }
    }

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        updatetier();
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        if (!result.wasSuccessful()) return result;

        // 从 processingLogic 获取结果
        mMaxProgresstime = processingLogic.getDuration();
        mOutputItems = processingLogic.getOutputItems();
        mOutputFluids = processingLogic.getOutputFluids();

        // 设置能量消耗（无线模式下 calculatedEut 已在 onRecipeStart 中被置为 0，此处恒为 0；
        // 若不更新，lEUt 会残留有线模式下的数值，导致机器无能源仓却空转耗电）
        lEUt = -processingLogic.getCalculatedEut();

        return result;
    }

    protected long getActualEnergyUsage() {
        if (isWirelessMode) {
            // 无线模式使用无线网络能量
            return processingLogic.getCalculatedEut();
        } else {
            // 返回当前配方的实际能耗
            return Math.abs(lEUt);
        }
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    // ===================== Structure =====================

    @Override
    public void checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack, List<StructureError> errors) {
        // Reset value
        mHeatingCapacity = 0;
        setCoilLevel(HeatingCoilLevel.None);

        // 线圈等级由 checkPiece 扫描结构时通过 ofCoil 写入 mCoilLevel,必须先扫描再判断
        if (!checkPiece(STRUCTURE_PIECE_MAIN, 23, 5, 20, errors)) return;
        if (getCoilLevel() == HeatingCoilLevel.None) {
            errors.add(StructureErrorRegistry.COIL_LEVEL_NOT_ENOUGH);
        }

        // 无线模式使用无线网络供电,不需要能源仓
        if (!isWirelessMode) {
            checkHasAnyEnergy(errors);
        }
        checkHasInputBus(errors);
        checkHasOutputBus(errors);

        // 无线模式下不允许能源仓
        if (isWirelessMode && (!mEnergyHatches.isEmpty() || !mExoticEnergyHatches.isEmpty())) {
            errors.add(
                StructureErrors
                    .hatchCount(ErrorType.TOO_MANY, Energy, mEnergyHatches.size() + mExoticEnergyHatches.size(), 0));
            return;
        }
        if (errors.isEmpty()) {
            mHeatingCapacity = (int) getCoilLevel().getHeat();
        }
        updatetier();
        repairMachine();
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 23, 5, 20);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, ISurvivalBuildEnvironment env) {
        if (mMachine) return -1;
        int realBudget = elementBudget >= 200 ? elementBudget : Math.min(200, elementBudget * 5);
        return survivalBuildPiece(STRUCTURE_PIECE_MAIN, stackSize, 23, 5, 20, realBudget, env, false, true);
    }

    public IStructureDefinition<OTEBBPlasmaForge> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEBBPlasmaForge>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', ofBlock(sBlockCasings1, 12))
                .addElement(
                    'B',
                    buildHatchAdder(OTEBBPlasmaForge.class)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Energy.or(ExoticEnergy), Maintenance)
                        .casingIndex(DIM_INJECTION_CASING)
                        .hint(1)
                        .buildAndChain(sBlockCasings1, 13))
                .addElement('C', ofBlock(sBlockCasings1, 14))
                .addElement(
                    'D',
                    GTStructureChannels.HEATING_COIL
                        .use(activeCoils(ofCoil(OTEBBPlasmaForge::setCoilLevel, OTEBBPlasmaForge::getCoilLevel))))
                .addElement('E', ofBlock(sBlockCasingsTT, 7))
                .addElement('F', ofBlock(sBlockCasingsTT, 8))
                .addElement('G', ofBlock(BlockQuantumGlass.INSTANCE, 0))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    // ===================== Tooltip =====================

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        // ── 序章:中二宣言 ──
        tt.addMachineType(translateToLocal("ote.bbpf.0"))
            .addInfo(translateToLocal("ote.bbpf.1"))
            .addInfo(translateToLocal("ote.bbpf.8"))
            .addSeparator()
            // ── 核心数值:并行 / 线圈效率 / 上限突破 ──
            .addInfo(translateToLocal("ote.bbpf.2"))
            .addInfo(translateToLocal("ote.bbpf.9"))
            .addInfo(translateToLocal("ote.bbpf.10"))
            .addInfo(translateToLocal("ote.bbpf.3"))
            .addSeparator()
            // ── 强化:校准矩阵 → 完美超频 + 无线模式 ──
            .addInfo(translateToLocal("ote.bbpf.4"))
            .addInfo(translateToLocal("ote.bbpf.11"))
            .addInfo(translateToLocal("ote.bbpf.6"))
            .addSeparator()
            .addInfo(translateToLocal("ote.bbpf.7"))
            .addSeparator()
            .addInfo("§aAdded by: 123Technology - PlasmaForge")
            // ── 结构(按 SHIFT 查看)──
            .addController(translateToLocal("ote.bbpf.0"))
            .beginStructureBlock(47, 11, 47, false)
            .addStructureInfo(translateToLocal("ote.bbpf.12"))
            .addStructureInfo(translateToLocal("ote.bbpf.13"))
            .addStructureInfo(translateToLocal("ote.bbpf.14"))
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .toolTipFinisher();
        return tt;
    }

    // ===================== Standard Overrides =====================

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEBBPlasmaForge(mName);
    }

    @Override
    public String[] getInfoData() {

        long storedEnergy = 0;
        long maxEnergy = 0;

        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
            if (tHatch.getBaseMetaTileEntity() != null) {
                storedEnergy += tHatch.getBaseMetaTileEntity()
                    .getStoredEU();
            }
            if (tHatch.getBaseMetaTileEntity() != null) {
                maxEnergy += tHatch.getBaseMetaTileEntity()
                    .getEUCapacity();
            }
        }
        long voltage = getAverageInputVoltage();
        long amps = getMaxInputAmps();

        List<String> infoData = new ArrayList<>();
        infoData.add(
            EnumChatFormatting.STRIKETHROUGH + "------------"
                + EnumChatFormatting.RESET
                + " "
                + StatCollector.translateToLocal("GT5U.infodata.critical_info")
                + " "
                + EnumChatFormatting.STRIKETHROUGH
                + "------------");
        infoData.add(
            StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.scientificFormat(mProgresstime)
                + EnumChatFormatting.RESET
                + "t / "
                + EnumChatFormatting.YELLOW
                + GTUtility.scientificFormat(mMaxProgresstime)
                + EnumChatFormatting.RESET
                + "t");

        // 无线模式显示无线能量消耗
        if (isWirelessMode) {
            infoData.add(
                StatCollector.translateToLocal("otht.waila.wirelesseu") + ": "
                    + EnumChatFormatting.YELLOW
                    + costingWirelessEU
                    + EnumChatFormatting.RESET
                    + " EU (total)");
        } else {
            infoData.add(
                StatCollector.translateToLocal("GT5U.multiblock.energy") + ": "
                    + EnumChatFormatting.GREEN
                    + GTUtility.scientificFormat(storedEnergy)
                    + EnumChatFormatting.RESET
                    + " EU / "
                    + EnumChatFormatting.YELLOW
                    + GTUtility.scientificFormat(maxEnergy)
                    + EnumChatFormatting.RESET
                    + " EU");
        }

        infoData.add(
            StatCollector.translateToLocal("GT5U.multiblock.usage") + ": "
                + EnumChatFormatting.RED
                + GTUtility.scientificFormat(getActualEnergyUsage())
                + EnumChatFormatting.RESET
                + " EU/t");
        infoData.add(
            StatCollector.translateToLocal("GT5U.multiblock.mei") + ": "
                + EnumChatFormatting.YELLOW
                + GTUtility.scientificFormat(voltage)
                + EnumChatFormatting.RESET
                + " EU/t(*"
                + EnumChatFormatting.YELLOW
                + amps
                + EnumChatFormatting.RESET
                + "A) "
                + StatCollector.translateToLocal("GT5U.machines.tier")
                + ": "
                + EnumChatFormatting.YELLOW
                + VN[GTUtility.getTier(voltage)]
                + EnumChatFormatting.RESET);
        infoData.add(
            StatCollector.translateToLocal("GT5U.EBF.heat") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.scientificFormat(mHeatingCapacity)
                + EnumChatFormatting.RESET
                + " K");
        infoData.add(EnumChatFormatting.STRIKETHROUGH + "-----------------------------------------");

        return infoData.toArray(new String[0]);
    }

    // ===================== Textures =====================

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {

        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][DIM_INJECTION_CASING], TextureFactory.builder()
                .addIcon(OVERLAY_DTPF_ON)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(OVERLAY_FUSION1_GLOW)
                    .extFacing()
                    .glow()
                    .build() };
            return new ITexture[] { casingTexturePages[0][DIM_INJECTION_CASING], TextureFactory.builder()
                .addIcon(OVERLAY_DTPF_OFF)
                .extFacing()
                .build() };
        }
        return new ITexture[] { casingTexturePages[0][DIM_INJECTION_CASING] };
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_PLASMAFORGE_LOOP;
    }

    // ===================== Misc =====================

    // 老大哥锻炉,老大哥的恩情还不完
    protected void updatetier() {
        if (hasCalibrationMatrix()) {
            this.MLevel = 2;
        } else {
            this.MLevel = 1;
        }
    }

    /**
     * 控制器槽位是否放入超维校准矩阵(meta 32758),MLevel 2 的判定依据。
     * 控制器槽是同步的库存槽,客户端永远最新,GUI 按钮以此作为门槛(同 DTPF 的做法)。
     */
    public boolean hasCalibrationMatrix() {
        ItemStack aGuiStack = this.getControllerSlot();
        return aGuiStack != null
            && GTUtility.areStacksEqual(aGuiStack, GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32758));
    }

    @Override
    public void getWailaNBTData(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y,
        int z) {
        super.getWailaNBTData(player, tile, tag, world, x, y, z);
        final IGregTechTileEntity tileEntity = getBaseMetaTileEntity();
        if (tileEntity != null) {
            tag.setString("costingWirelessEU", costingWirelessEU);
        }
    }

    @Override
    public void getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaBody(itemStack, currentTip, accessor, config);
        final NBTTagCompound tag = accessor.getNBTData();
        currentTip.add(
            translateToLocal("otht.waila.wirelesseu") + EnumChatFormatting.RESET
                + ": "
                + EnumChatFormatting.GOLD
                + tag.getString("costingWirelessEU")
                + EnumChatFormatting.RESET
                + " EU");
    }

    @Override
    public void getExtraInfoData(List<String> info) {
        info.add(IGregTechDeviceInformation.encode("GT5U.EBF.heat.s", formatNumber(this.mHeatingCapacity)));
    }

    @Override
    public boolean addOutput(FluidStack aLiquid) {
        if (aLiquid == null) return false;
        FluidStack tLiquid = aLiquid.copy();
        addOutputPartial(tLiquid);
        return tLiquid.amount == 0;
    }

    @Override
    protected @NotNull MTEMultiBlockBaseGui<?> getGui() {
        return new OTEBBPlasmaForgeGui(this);
    }

    public int getMLevel() {
        return MLevel;
    }

    public boolean isWirelessModeEnabled() {
        return isWirelessMode;
    }

    /**
     * 无线模式开关。规则在服务端强制执行(GUI 的 C2S 同步值经此落字段):
     * 只有 MLevel &gt;= 2 且无能源仓才能开启;关闭永远允许。
     * 注意:NBT 加载(loadNBTData)直接写字段,不走此规则。
     */
    public void setWirelessModeEnabled(boolean value) {
        if (value && (getMLevel() < 2 || !areEnergyHatchesEmpty())) {
            isWirelessMode = false;
            return;
        }
        isWirelessMode = value;
    }

    public boolean areEnergyHatchesEmpty() {
        return mEnergyHatches.isEmpty() && mExoticEnergyHatches.isEmpty();
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return super.getDefaultHasMaintenanceChecks();
    }

    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (getMLevel() < 2) {
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("ote.bbpf.wireless.invalid"));
            return;
        }
        if (areEnergyHatchesEmpty()) {
            setWirelessModeEnabled(!isWirelessModeEnabled());
            if (isWirelessModeEnabled()) {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("ote.bbpf.wireless.on"));
            } else {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("ote.bbpf.wireless.off"));
            }
        } else {
            setWirelessModeEnabled(false);
            GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("ote.bbpf.wireless.energyhatch"));
        }
    }
}
