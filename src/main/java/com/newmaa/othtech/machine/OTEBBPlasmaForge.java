package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.withChannel;
import static com.newmaa.othtech.utils.Utils.NEGATIVE_ONE;
import static gregtech.api.GregTechAPI.*;
import static gregtech.api.enums.GTValues.VN;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_OFF;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_DTPF_ON;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FUSION1_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static gregtech.api.util.GTStructureUtility.ofCoil;
import static gregtech.api.util.GTUtility.validMTEList;
import static gregtech.common.misc.WirelessNetworkManager.addEUToGlobalEnergyMap;
import static net.minecraft.util.StatCollector.translateToLocal;

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
import com.gtnewhorizons.modularui.api.drawable.IDrawable;
import com.gtnewhorizons.modularui.api.drawable.UITexture;
import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.api.screen.UIBuildContext;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.FakeSyncWidget;
import com.newmaa.othtech.machine.machineclass.OTHMultiMachineBase;
import com.newmaa.othtech.machine.machineclass.OTHProcessingLogic;
import com.newmaa.othtech.utils.Utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.SoundResource;
import gregtech.api.gui.modularui.GTUITextures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.metatileentity.implementations.MTEHatch;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.CheckRecipeResultRegistry;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.*;
import gregtech.common.misc.GTStructureChannels;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import tectech.thing.gui.TecTechUITextures;

public class OTEBBPlasmaForge extends OTHMultiMachineBase<OTEBBPlasmaForge> implements ISurvivalConstructable {

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aTick % 20 == 0 && (MLevel == 1)) {
            ItemStack aGuiStack = this.getControllerSlot();
            if (aGuiStack != null) {
                if (GTUtility
                    .areStacksEqual(aGuiStack, GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32758))) {
                    this.MLevel = 2;
                }
            }
        }
    }

    // 老大哥锻炉,老大哥的恩情还不完
    protected float getSpeedBonus() {
        return 1;
    }

    @Override
    public int getMaxParallelRecipes() {
        if (MLevel == 2) {
            return Integer.MAX_VALUE;
        } else {
            return mCoilLevel == null ? 0 : 1230 + mCoilLevel.getTier() * 1230;
        }
    }

    private int mHeatingCapacity = 0;
    private int MLevel = 1;
    private HeatingCoilLevel mCoilLevel;
    private UUID ownerUUID;
    private static IStructureDefinition<OTEBBPlasmaForge> STRUCTURE_DEFINITION = null;
    private boolean isWirelessMode = false;
    private String costingWirelessEU = "0";
    private OverclockCalculator overclockCalculator;

    public int getCoilTier() {
        return Utils.getCoilTier(mCoilLevel);
    }

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        mCoilLevel = aCoilLevel;
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
    protected void setProcessingLogicPower(ProcessingLogic logic) {
        if (isWirelessMode) {
            logic.setAvailableVoltage(Long.MAX_VALUE);
            logic.setAvailableAmperage(1);
            logic.setAmperageOC(false);
        } else {
            super.setProcessingLogicPower(logic);
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private final String[][] shapeMain = new String[][] {
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
            "                                               ", "  AAA                                     AAA  ",
            " AADAA                                   AADAA ", " ADDDA                                   ADDDA ",
            " AADAA                                   AADAA ", "  AAA                                     AAA  ",
            "                                               ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "  BBB                AAAAA                BBB  ", " BAAAB              ABBBBBA              BAAAB ",
            "BAADAAB             ABCCCBA             BAADAAB", "BADDDAB             ABC~CBA             BADDDAB",
            "BAADAAB             ABCCCBA             BAADAAB", " BAAAB              ABBBBBA              BAAAB ",
            "  BBB                AAAAA                BBB  ", "                                               ",
            "                                               " },
        { "                                               ", "                     AAAAA                     ",
            "  AAA               ABCBCBA               AAA  ", " A   A              BDDDDDB              A   A ",
            "A  D  A             BD   DB             A  D  A", "A DDD A             BD   DB             A DDD A",
            "A  D  A             BD   DB             A  D  A", " A   A              BDDDDDB              A   A ",
            "  AAA               ABCBCBA               AAA  ", "                     AAAAA                     ",
            "                                               " },
        { "                                               ", "                     ABBBA                     ",
            "  ACA               ACBCBCA               ACA  ", " C   C              BD   DB              C   C ",
            "A  D  A             C     C             A  D  A", "C DDD C             C     C             C DDD C",
            "A  D  A             C     C             A  D  A", " C   C              BD   DB              C   C ",
            "  ACA               ACBCBCA               ACA  ", "                     ABBBA                     ",
            "                                               " },
        { "                                               ", "                     ABBBA                     ",
            "  ACA               ABCBCBA               ACA  ", " C   C              BD   DB              C   C ",
            "A  D  A             C     C             A  D  A", "C DDD C                                 C DDD C",
            "A  D  A             C     C             A  D  A", " C   C              BD   DB              C   C ",
            "  ACA               ABCBCBA               ACA  ", "                     ABBBA                     ",
            "                                               " },
        { "                                               ", "                     ABBBA                     ",
            "  ACA               ACBCBCA               ACA  ", " C   C              BD   DB              C   C ",
            "A  D  A             C     C             A  D  A", "C DDD C             C     C             C DDD C",
            "A  D  A             C     C             A  D  A", " C   C              BD   DB              C   C ",
            "  ACA               ACBCBCA               ACA  ", "                     ABBBA                     ",
            "                                               " },
        { "                                               ", "                     AAAAA                     ",
            "  AAA               ABCBCBA               AAA  ", " A   A              BDDDDDB              A   A ",
            "A  D  A             BD   DB             A  D  A", "A DDD A             BD   DB             A DDD A",
            "A  D  A             BD   DB             A  D  A", " A   A              BDDDDDB              A   A ",
            "  AAA               ABCBCBA               AAA  ", "                     AAAAA                     ",
            "                                               " },
        { "                                               ", "                                               ",
            "  BBB                AAAAA                BBB  ", " BAAAB              ABBBBBA              BAAAB ",
            "BAADAAB             ABCCCBA             BAADAAB", "BADDDAB             ABC CBA             BADDDAB",
            "BAADAAB             ABCCCBA             BAADAAB", " BAAAB              ABBBBBA              BAAAB ",
            "  BBB                AAAAA                BBB  ", "                                               ",
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "  AAA                                     AAA  ",
            " AADAA                                   AADAA ", " ADDDA                                   ADDDA ",
            " AADAA                                   AADAA ", "  AAA                                     AAA  ",
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
            "                                               " },
        { "                                               ", "                                               ",
            "                                               ", "                                               ",
            "                    BAAAAAB                    ", "                    BACCCAB                    ",
            "                    BAAAAAB                    ", "                                               ",
            "                                               ", "                                               ",
            "                                               " } };

    protected static final int DIM_BRIDGE_CASING = 12;
    protected static final int DIM_INJECTION_CASING = 13;

    protected static final String STRUCTURE_PIECE_MAIN = "main";

    public IStructureDefinition<OTEBBPlasmaForge> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<OTEBBPlasmaForge>builder()
                .addShape(STRUCTURE_PIECE_MAIN, shapeMain)
                .addElement('A', ofBlock(sBlockCasings1, 12))
                .addElement(
                    'B',
                    buildHatchAdder(OTEBBPlasmaForge.class)
                        .atLeast(InputHatch, OutputHatch, InputBus, OutputBus, Energy, ExoticEnergy, Maintenance)
                        .adder(OTEBBPlasmaForge::addToMachineList)
                        .casingIndex(DIM_INJECTION_CASING)
                        .dot(1)
                        .buildAndChain(sBlockCasings1, 13))
                .addElement('C', ofBlock(sBlockCasings1, 14))
                .addElement(
                    'D',
                    withChannel("coil", ofCoil(OTEBBPlasmaForge::setCoilLevel, OTEBBPlasmaForge::getCoilLevel)))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    public OTEBBPlasmaForge(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public OTEBBPlasmaForge(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEBBPlasmaForge(mName);
    }

    @Override
    protected MultiblockTooltipBuilder createTooltip() {
        MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.bbpf.0"))
            .addInfo(translateToLocal("ote.bbpf.1"))
            .addInfo(translateToLocal("ote.bbpf.2"))
            .addInfo(translateToLocal("ote.bbpf.3"))
            .addInfo((translateToLocal("ote.bbpf.4")))
            .addInfo((translateToLocal("ote.bbpf.6")))
            .addSeparator()
            .addController(translateToLocal("ote.bbpf.0"))
            .beginStructureBlock(47, 11, 47, false)
            .addInputBus("AnyInputBus", 1)
            .addOutputBus("AnyOutputBus", 1)
            .addInputHatch("AnyInputHatch", 1)
            .addOutputHatch("AnyOutputHatch", 1)
            .addEnergyHatch("AnyEnergyHatch", 1)
            .addSubChannelUsage(GTStructureChannels.HEATING_COIL)
            .addSeparator()
            .addInfo("§b§lAuthor:§r§kunknown§r§lczqwq§r")
            .toolTipFinisher("§a123Technology - BBPlasmaForge");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection aFacing,
        int colorIndex, boolean aActive, boolean redstoneLevel) {
        IIconContainer glow = OVERLAY_FUSION1_GLOW;

        if (side == aFacing) {
            if (aActive) return new ITexture[] { casingTexturePages[0][DIM_INJECTION_CASING], TextureFactory.builder()
                .addIcon(OVERLAY_DTPF_ON)
                .extFacing()
                .build(),
                TextureFactory.builder()
                    .addIcon(glow)
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

    @Override
    public RecipeMap<?> getRecipeMap() {
        return RecipeMaps.plasmaForgeRecipes;
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        return new OTHProcessingLogic() {

            @Override
            public ProcessingLogic setSpeedBonus(double speedModifier) {
                return super.setSpeedBonus(getSpeedBonus());
            }

            protected float getSpeedBonus() {
                if (getCoilTier() == 14) {
                    return 0.1F;
                }
                return 1 - getCoilTier() * 0.5F;
            }

            @Nonnull
            @Override
            protected OverclockCalculator createOverclockCalculator(@Nonnull GTRecipe recipe) {
                overclockCalculator = super.createOverclockCalculator(recipe).setRecipeHeat(recipe.mSpecialValue)
                    .setMachineHeat(mHeatingCapacity);

                // 添加限制：当 MLevel=1 且使用能源仓时限制超频
                if (MLevel == 1 && !isWirelessMode && !mEnergyHatches.isEmpty()) {
                    // 获取能源仓的总功率
                    long totalPowerCapacity = getTotalPowerCapacity();
                    // 获取配方基础功耗
                    long baseEUt = recipe.mEUt;
                    // 计算最大允许的超频次数，确保不超过能源仓总功率
                    int maxOverclocks = calculateMaxOverclocks(baseEUt, totalPowerCapacity);

                    // 限制超频次数
                    overclockCalculator = overclockCalculator.setMaxOverclocks(maxOverclocks);
                } else if (MLevel >= 2) {
                    // MLevel>=2 时允许无损超频
                    overclockCalculator = overclockCalculator.enablePerfectOC();
                }

                return overclockCalculator;
            }

            @Override
            protected @Nonnull CheckRecipeResult validateRecipe(@Nonnull GTRecipe recipe) {
                // 原有的热量检查
                if (recipe.mSpecialValue > mHeatingCapacity) {
                    return CheckRecipeResultRegistry.insufficientHeat(recipe.mSpecialValue);
                }

                // 添加功率限制检查：当 MLevel=1 且使用能源仓时
                if (MLevel == 1 && !isWirelessMode && !mEnergyHatches.isEmpty()) {
                    long totalPowerCapacity = getTotalPowerCapacity();
                    long baseEUt = recipe.mEUt;

                    // 如果基础功耗已经超过能源仓总功率，直接拒绝配方
                    if (baseEUt > totalPowerCapacity) {
                        return CheckRecipeResultRegistry.insufficientPower(totalPowerCapacity);
                    }

                    // 检查超频后的功耗是否会超过限制
                    int maxOverclocks = calculateMaxOverclocks(baseEUt, totalPowerCapacity);
                    if (maxOverclocks <= 0) {
                        return CheckRecipeResultRegistry.insufficientPower(totalPowerCapacity);
                    }
                }

                return CheckRecipeResultRegistry.SUCCESSFUL;
            }
        }.setMaxParallelSupplier(this::getMaxParallelRecipes);
    }

    // 计算能源仓总功率
    private long getTotalPowerCapacity() {
        long totalCapacity = 0;
        for (MTEHatch hatch : mEnergyHatches) {
            // 获取单个能源仓的功率容量（电压 * 安培）
            long hatchCapacity = 0;
            if (hatch.getBaseMetaTileEntity() != null) {
                hatchCapacity = hatch.getBaseMetaTileEntity()
                    .getInputVoltage()
                    * hatch.getBaseMetaTileEntity()
                        .getInputAmperage();
            }
            totalCapacity += hatchCapacity;
        }
        return totalCapacity;
    }

    // 计算最大允许的超频次数
    private int calculateMaxOverclocks(long baseEUt, long totalPowerCapacity) {
        int maxOverclocks = 0;
        long currentEUt = baseEUt;

        // 每次超频功耗翻倍，计算在不超过总功率的前提下能超频多少次
        while (currentEUt * 2 <= totalPowerCapacity && maxOverclocks < 10) { // 限制最大超频次数为10
            currentEUt *= 2;
            maxOverclocks++;
        }

        return maxOverclocks;
    }

    // 获取实际能耗使用情况
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        // Reset heating capacity.
        mHeatingCapacity = 0;

        // Get heating capacity from coils in structure.
        setCoilLevel(HeatingCoilLevel.None);

        if (!checkPiece(STRUCTURE_PIECE_MAIN, 23, 5, 20)) {
            return false;
        }

        if (getCoilLevel() == HeatingCoilLevel.None) return false;

        // 检查能源仓数量
        if (mEnergyHatches.size() > 1) {
            return false;
        }

        // Heat capacity of coils used on multi. No free heat from extra EU!
        mHeatingCapacity = (int) getCoilLevel().getHeat();

        // All structure checks passed, return true.
        return true;
    }
    // if (isWirelessMode) {
    // mMaxProgresstime = (int) 6.4 * 20;
    // } else mMaxProgresstime = processingLogic.getDuration();

    // // Item input bus check.
    // if (mInputBusses.size() > max_input_bus) return false;
    //
    // // Item output bus check.
    // if (mOutputBusses.size() > max_output_bus) return false;
    //
    // // Fluid input hatch check.
    // if (mInputHatches.size() > max_input_hatch) return false;
    //
    // // Fluid output hatch check.
    // if (mOutputHatches.size() > max_output_hatch) return false;
    //
    // // If there is more than 1 TT energy hatch, the structure check will fail.
    // // If there is a TT hatch and a normal hatch, the structure check will fail.
    // if (!mExoticEnergyHatches.isEmpty()) {
    // if (!mEnergyHatches.isEmpty()) return false;
    // if (mExoticEnergyHatches.size() > 1) return false;
    // }
    //
    // // If there is 0 or more than 2 energy hatches structure check will fail.
    // if (!mEnergyHatches.isEmpty()) {
    // if (mEnergyHatches.size() > 2) return false;
    //
    // // Check will also fail if energy hatches are not of the same tier.
    // byte tier_of_hatch = mEnergyHatches.get(0).mTier;
    // for (MTEHatchEnergy energyHatch : mEnergyHatches) {
    // if (energyHatch.mTier != tier_of_hatch) {
    // return false;
    // }
    // }
    // }
    //
    // // If there are no energy hatches or TT energy hatches, structure will fail to form.
    // if ((mEnergyHatches.isEmpty()) && (mExoticEnergyHatches.isEmpty())) return false;
    //
    // // Maintenance hatch not required but left for compatibility.
    // // Don't allow more than 1, no free casing spam!
    // if (mMaintenanceHatches.size() > 1) return false;

    @NotNull
    @Override
    public CheckRecipeResult checkProcessing() {
        setupProcessingLogic(processingLogic);

        CheckRecipeResult result = doCheckRecipe();
        // inputs are consumed at this point
        updateSlots();
        if (!result.wasSuccessful()) return result;

        if (isWirelessMode) {
            BigInteger c = BigInteger.valueOf(1);
            BigInteger costingWirelessEUTemp = BigInteger.valueOf(processingLogic.getCalculatedEut())
                .multiply(BigInteger.valueOf(processingLogic.getDuration()))
                .multiply(c.pow(2));
            costingWirelessEU = GTUtility.formatNumbers(costingWirelessEUTemp);
            if (!addEUToGlobalEnergyMap(ownerUUID, costingWirelessEUTemp.multiply(NEGATIVE_ONE))) {
                return CheckRecipeResultRegistry.insufficientPower(costingWirelessEUTemp.longValue());
            }

            // set progress time a fixed value
            mMaxProgresstime = 128;
            mOutputItems = processingLogic.getOutputItems();
            mOutputFluids = processingLogic.getOutputFluids();
        } else {
            mMaxProgresstime = processingLogic.getDuration();
            mOutputItems = processingLogic.getOutputItems();
            mOutputFluids = processingLogic.getOutputFluids();
            lEUt = -processingLogic.getCalculatedEut();
        }
        mEfficiency = 10000;
        mEfficiencyIncrease = 10000;
        return result;
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        this.ownerUUID = aBaseMetaTileEntity.getOwnerUuid();
    }

    @Override
    public boolean addOutput(FluidStack aLiquid) {
        if (aLiquid == null) return false;
        FluidStack tLiquid = aLiquid.copy();

        return dumpFluid(mOutputHatches, tLiquid, true) || dumpFluid(mOutputHatches, tLiquid, false);
    }

    @Override
    public String[] getInfoData() {

        long storedEnergy = 0;
        long maxEnergy = 0;

        for (MTEHatch tHatch : validMTEList(mExoticEnergyHatches)) {
            storedEnergy += tHatch.getBaseMetaTileEntity()
                .getStoredEU();
            maxEnergy += tHatch.getBaseMetaTileEntity()
                .getEUCapacity();
        }
        long voltage = getAverageInputVoltage();
        long amps = getMaxInputAmps();

        return new String[] {
            EnumChatFormatting.STRIKETHROUGH + "------------"
                + EnumChatFormatting.RESET
                + " "
                + StatCollector.translateToLocal("GT5U.infodata.critical_info")
                + " "
                + EnumChatFormatting.STRIKETHROUGH
                + "------------",
            StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(mProgresstime)
                + EnumChatFormatting.RESET
                + "t / "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(mMaxProgresstime)
                + EnumChatFormatting.RESET
                + "t",
            StatCollector.translateToLocal("GT5U.multiblock.energy") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(storedEnergy)
                + EnumChatFormatting.RESET
                + " EU / "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(maxEnergy)
                + EnumChatFormatting.RESET
                + " EU",
            StatCollector.translateToLocal("GT5U.multiblock.usage") + ": "
                + EnumChatFormatting.RED
                + GTUtility.formatNumbers(getActualEnergyUsage())
                + EnumChatFormatting.RESET
                + " EU/t",
            StatCollector.translateToLocal("GT5U.multiblock.mei") + ": "
                + EnumChatFormatting.YELLOW
                + GTUtility.formatNumbers(voltage)
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
                + EnumChatFormatting.RESET,
            StatCollector.translateToLocal("GT5U.EBF.heat") + ": "
                + EnumChatFormatting.GREEN
                + GTUtility.formatNumbers(mHeatingCapacity)
                + EnumChatFormatting.RESET
                + " K",
            EnumChatFormatting.STRIKETHROUGH + "-----------------------------------------" };
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

    @SideOnly(Side.CLIENT)
    @Override
    protected SoundResource getActivitySoundLoop() {
        return SoundResource.GT_MACHINES_PLASMAFORGE_LOOP;
    }

    @Override
    public void addUIWidgets(ModularWindow.Builder builder, UIBuildContext buildContext) {
        builder.widget(new ButtonWidget().setOnClick((clickData, widget) -> {
            // isWireless on!
            // 亿万火种之怒,燃尽此身!
            if (getMLevel() >= 2) {
                if (this.mEnergyHatches.isEmpty() | this.mExoticEnergyHatches.isEmpty()) {
                    isWirelessMode = true;
                } else {
                    isWirelessMode = false;
                }
            }
            // 如果MLevel < 2，保持isWirelessMode = false，不执行任何操作
        })
            .setPlayClickSound(true)
            .setBackground(() -> {
                List<UITexture> ret = new ArrayList<>();
                ret.add(GTUITextures.BUTTON_STANDARD);
                if (isWirelessMode && getMLevel() >= 2) {
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_POWER_PASS_ON);
                } else {
                    ret.add(TecTechUITextures.OVERLAY_BUTTON_POWER_PASS_OFF);
                }
                return ret.toArray(new IDrawable[0]);
            })
            .addTooltip(translateToLocal("ote.bbpf.5"))
            .setPos(174, 112)
            .setSize(16, 16)
            .attachSyncer(
                new FakeSyncWidget.BooleanSyncer(
                    () -> isWirelessMode && getMLevel() >= 2, // 只同步当条件满足时的状态
                    (val) -> {
                        // 只有当MLevel >= 2时才更新isWirelessMode
                        if (getMLevel() >= 2) {
                            isWirelessMode = val;
                        }
                    }),
                builder));
        super.addUIWidgets(builder, buildContext);
    }

    private int getMLevel() {
        return MLevel;
    }

    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setBoolean("wireless", isWirelessMode);
        aNBT.setInteger("Mlevel", MLevel);
        super.saveNBTData(aNBT);
    }

    @Override
    public void loadNBTData(final NBTTagCompound aNBT) {
        MLevel = aNBT.getInteger("MLevel");
        isWirelessMode = aNBT.getBoolean("wireless");
        super.loadNBTData(aNBT);
    }

    public HeatingCoilLevel getCoilLevel() {
        return mCoilLevel;
    }

    protected boolean isEnablePerfectOverclock() {
        if (MLevel >= 2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean supportsVoidProtection() {
        return true;
    }

    @Override
    public boolean supportsBatchMode() {
        return true;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }

    public final void onScrewdriverRightClick(ForgeDirection side, EntityPlayer aPlayer, float aX, float aY, float aZ,
        ItemStack aTool) {
        if (aPlayer.isSneaking()) {
            if (MLevel >= 2) {
                isWirelessMode = true;
            } else {
                isWirelessMode = false;
            }
            if (isWirelessMode) {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("ote.bbpf.wireless.on"));
            } else {
                GTUtility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("ote.bbpf.wireless.off"));
            }
        }
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
