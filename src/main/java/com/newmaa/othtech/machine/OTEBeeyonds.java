package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static gregtech.api.GregTechAPI.sBlockCasings1;
import static gregtech.api.GregTechAPI.sBlockReinforced;
import static gregtech.api.enums.HatchElement.*;
import static gregtech.api.enums.Textures.BlockIcons.*;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.newmaa.othtech.machine.machineclass.TTMultiMachineBaseEM;

import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;

public class OTEBeeyonds extends TTMultiMachineBaseEM implements IConstructable, ISurvivalConstructable {

    private boolean grace = false;

    @Override
    public boolean checkMachine_EM(IGregTechTileEntity iGregTechTileEntity, ItemStack itemStack) {
        casingCount = 0;
        if (structureCheck_EM("main", 0, 1, 0) && casingCount >= 0) {
            grace = true;
            return true;
        } else if (grace) {
            grace = false;
            return true;
        }
        return false;
    }

    @Override
    public void onFirstTick_EM(IGregTechTileEntity aBaseMetaTileEntity) {
        if (!mMachine) {
            aBaseMetaTileEntity.disableWorking();
        }
    }

    private int casingCount = 0;

    // region structure
    private static final String[] description = new String[] { EnumChatFormatting.AQUA + translateToLocal("搭建细节") + ":",
        translateToLocal("1 - 能源仓, 动力仓或者激光仓, 输入输出总线/仓: 替换塑料混凝土方块"), // 1 - Energy IO Hatches or High
        // Power Casing
    };
    private static final IStructureDefinition<OTEBeeyonds> STRUCTURE_DEFINITION = IStructureDefinition
        .<OTEBeeyonds>builder()
        .addShape(
            "main",
            new String[][] {
                { "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "        BBBBBBB        ", "       BCCCCCCCB       ",
                    "     BBBCCCCCCCBBB     ", "   BBBBBCCCCCCCBBBBB   ", "     BBBCCCCCCCBBB     ",
                    "       BCCCCCCCB       ", "        BBBBBBB        ", "                       ",
                    "                       " },
                { "                       ", "       BBB    BBB      ", "      BBBBB  BBBBB     ",
                    "      BBBBBBBBBBBB     ", "       BBBBBBBBBB      ", "        AAEAEAE        ",
                    "       AAAAEAEAE       ", "      AEAEAEAEAEA      ", "      AAAAAEAEAEAE     ",
                    "      AABAAEAEAEA      ", "       AAAAEAEAE       ", "        AAEAEAE        ",
                    "                       ", "       BBBBBBBBB       ", "    BBBBCCCCCCCBBBB    ",
                    "    BBBBCCCCCCCBBBB    ", "  BBBBBBC     CBBBBBB  ", "    BBBBC     CBBBB    ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "          F~F          ",
                    "          FFF          " },
                { "       BBB    BBB      ", "      BBBBB  BBBBB     ", "     BBBBBBBBBBBBBB    ",
                    "     BBBBBBBBBBBBBB    ", "      BBBBBBBBBBBB     ", "       AAAEAEAEE       ",
                    "      AAAAAEAEAEA      ", "     AAEAEAEAEAEAE     ", "     AAAAAAEAEAEAEEE   ",
                    "     AAABAAEAEAEAE     ", "      AAAAAEAEAEA      ", "       AAAEAEAEE       ",
                    "        AAEAEAE        ", "       BBBBBBBBB       ", "    BBBBC     CBBBB    ",
                    "   BBBBBC     CBBBBB   ", "BBBBBBBBC     CBBBBBBBB", "   BBBBBC     CBBBBB   ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "         FFBFF         ",
                    "          FFF          " },
                { "                       ", "       BBB    BBB      ", "      BBBBB  BBBBB     ",
                    "      BBBBBBBBBBBB     ", "       BBBBBBBBBB      ", "        AAEAEAE        ",
                    "       AAAAEAEAE       ", "      AAAAAEAEAEA      ", "      AAAAAEAEAEAE     ",
                    "      AAAAAEAEAEA      ", "       AAAAEAEAE       ", "        AAEAEAE        ",
                    "                       ", "       BBBBBBBBB       ", "    BBBBCCCCCCCBBBB    ",
                    "    BBBBCCCCCCCBBBB    ", "  BBBBBBC     CBBBBBB  ", "    BBBBC     CBBBB    ",
                    "    BBBBC     CBBBB    ", "       BBDDDDDBB       ", "          FFF          ",
                    "          FFF          " },
                { "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "                       ", "                       ",
                    "                       ", "        BBBBBBB        ", "       BCCCCCCCB       ",
                    "     BBBCCCCCCCBBB     ", "   BBBBBCCCCCCCBBBBB   ", "     BBBCCCCCCCBBB     ",
                    "       BCCCCCCCB       ", "        BBBBBBB        ", "                       ",
                    "                       " } })
        .addElement('A', ofBlock(sBlockCasings1, 10))
        .addElement('B', ofBlock(sBlockReinforced, 2))
        .addElement('C', ofBlock(Blocks.glass, 0))
        .addElement('D', ofBlock(Blocks.grass, 0))
        .addElement('E', ofBlock(Blocks.obsidian, 0))
        .addElement(
            'F',
            buildHatchAdder(OTEBeeyonds.class)
                .atLeast(InputBus, OutputBus, InputHatch, OutputHatch, Energy.or(ExoticEnergy))
                .adder(OTEBeeyonds::addToMachineList)
                .dot(1)
                .casingIndex(210)
                .buildAndChain(sBlockReinforced, 2))
        .build();

    @Override
    public IStructureDefinition<OTEBeeyonds> getStructure_EM() {
        return STRUCTURE_DEFINITION;
    }
    // endregion

    public OTEBeeyonds(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eDismantleBoom = true;
    }

    public OTEBeeyonds(String aName) {
        super(aName);
        eDismantleBoom = true;
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEBeeyonds(mName);
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("ote.tm.bee.0"))
            .addInfo(translateToLocal("ote.tm.bee.1"))
            .addInfo(translateToLocal("ote.tm.bee.2"))
            .addTecTechHatchInfo()
            .addSeparator()
            .toolTipFinisher("§a123Technology - Beeyonds");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection sideDirection,
        ForgeDirection facingDirection, int colorIndex, boolean active, boolean redstoneLevel) {
        if (sideDirection == facingDirection) {
            return new ITexture[] { TextureFactory.of(BLOCK_PLASCRETE), active
                ? TextureFactory.of(
                    TextureFactory.of(TTMultiblockBase.ScreenON),
                    TextureFactory.builder()
                        .addIcon(TTMultiblockBase.ScreenON)
                        .glow()
                        .build())
                : TextureFactory.of(
                    TextureFactory.of(TTMultiblockBase.ScreenOFF),
                    TextureFactory.builder()
                        .addIcon(TTMultiblockBase.ScreenOFF)
                        .glow()
                        .build()) };
        }
        return new ITexture[] { TextureFactory.of(BLOCK_PLASCRETE) };
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
        return false;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM("main", 11, 20, 1, stackSize, hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, IItemSource source, EntityPlayerMP actor) {
        if (mMachine) return -1;
        return survivalBuildPiece("main", stackSize, 11, 20, 1, elementBudget, source, actor, false, true);
    }

    @Override
    public String[] getStructureDescription(ItemStack stackSize) {
        return description;
    }

    @Override
    public boolean isPowerPassButtonEnabled() {
        return true;
    }

    @Override
    public boolean isSafeVoidButtonEnabled() {
        return false;
    }

    @Override
    public boolean isAllowedToWorkButtonEnabled() {
        return true;
    }

    @Override
    public boolean getDefaultHasMaintenanceChecks() {
        return false;
    }
}
