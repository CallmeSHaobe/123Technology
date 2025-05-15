package com.newmaa.othtech.machine;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static gregtech.api.enums.HatchElement.Dynamo;
import static gregtech.api.enums.HatchElement.Energy;
import static gregtech.api.util.GTStructureUtility.buildHatchAdder;
import static net.minecraft.util.StatCollector.translateToLocal;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.structurelib.alignment.constructable.IConstructable;
import com.gtnewhorizon.structurelib.alignment.constructable.ISurvivalConstructable;
import com.gtnewhorizon.structurelib.structure.IItemSource;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.newmaa.othtech.machine.machineclass.TTMultiMachineBaseEM;

import gregtech.api.enums.SoundResource;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.check.CheckRecipeResult;
import gregtech.api.recipe.check.SimpleCheckRecipeResult;
import gregtech.api.util.MultiblockTooltipBuilder;
import tectech.thing.casing.BlockGTCasingsTT;
import tectech.thing.casing.TTCasingsContainer;
import tectech.thing.metaTileEntity.multi.base.TTMultiblockBase;
import tectech.thing.metaTileEntity.multi.base.render.TTRenderedExtendedFacingTexture;

public class OTEMiniActiveTransformer extends TTMultiMachineBaseEM implements IConstructable, ISurvivalConstructable {

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
        translateToLocal("1 - 能源仓, 动力仓或者激光仓 : 替换超能机械方块"), // 1 - Energy IO Hatches or High
        // Power Casing
    };
    private static final IStructureDefinition<OTEMiniActiveTransformer> STRUCTURE_DEFINITION = IStructureDefinition
        .<OTEMiniActiveTransformer>builder()
        .addShape("main", new String[][] { { "1", "~", "1", } })
        .addElement(
            '1',
            buildHatchAdder(OTEMiniActiveTransformer.class)
                .atLeast(Energy, HatchElement.EnergyMulti, Dynamo, HatchElement.DynamoMulti)
                .casingIndex(BlockGTCasingsTT.textureOffset)
                .dot(1)
                .buildAndChain(onElementPass(t -> t.casingCount++, ofBlock(TTCasingsContainer.sBlockCasingsTT, 0))))
        .build();

    @Override
    public IStructureDefinition<OTEMiniActiveTransformer> getStructure_EM() {
        return STRUCTURE_DEFINITION;
    }
    // endregion

    public OTEMiniActiveTransformer(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        eDismantleBoom = true;
    }

    public OTEMiniActiveTransformer(String aName) {
        super(aName);
        eDismantleBoom = true;
    }

    @Override
    public void onRemoval() {
        super.onRemoval();
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new OTEMiniActiveTransformer(mName);
    }

    @Override
    @NotNull
    protected CheckRecipeResult checkProcessing_EM() {
        if (ePowerPass) {
            mEfficiencyIncrease = 10000;
            mMaxProgresstime = 20;
        } else {
            mEfficiencyIncrease = 0;
            mMaxProgresstime = 0;
        }
        eAmpereFlow = 0;
        mEUt = 0;
        return ePowerPass ? SimpleCheckRecipeResult.ofSuccess("routing")
            : SimpleCheckRecipeResult.ofFailure("no_routing");
    }

    @Override
    public MultiblockTooltipBuilder createTooltip() {
        final MultiblockTooltipBuilder tt = new MultiblockTooltipBuilder();
        tt.addMachineType(translateToLocal("中登的小型核弹 : 迷你有源变压器"))
            .addInfo(translateToLocal("由于落后的IV工艺, 此机器导能时破坏主机依然会爆炸, 损耗不变"))
            .addInfo(translateToLocal("他真的会有用吗?"))
            .addInfo(translateToLocal("桑百安够么, 应该够吧? 来吧! 试一下米妮!"))
            .addInfo(translateToLocal("请不要使用特高电流之激光仓, 不然有源可能会坏掉."))
            .addInfo(translateToLocal("§c§l警告 : 爆炸为龙堆式爆炸 , 威力和输入功率正相关 ! 服务器请在配置文件内关闭 !"))
            .addInfo(translateToLocal("§c§l警告 : 爆炸为龙堆式爆炸 , 威力和输入功率正相关 ! 服务器请在配置文件内关闭 !"))
            .addInfo(translateToLocal("§c§l警告 : 爆炸为龙堆式爆炸 , 威力和输入功率正相关 ! 服务器请在配置文件内关闭 !"))
            // .addInfo(translateToLocal("爆炸威力等于安培数"))
            .addTecTechHatchInfo()
            .beginStructureBlock(1, 3, 1, false)
            .addController(translateToLocal("结构正中心")) // Controller: Front center
            .addCasingInfoMin(translateToLocal("0x 超能机械方块(最低)"), 5, false) // 0x High Power Casing
            // (minimum)
            .addEnergyHatch(translateToLocal("任意能源仓, 激光靶仓"), 1) // Energy Hatch: Any
            // High Power Casing
            .addDynamoHatch(translateToLocal("任意动力仓, 激光源仓"), 1) // Dynamo Hatch: Any
            // High Power Casing
            .toolTipFinisher("§a123Technology - MINI NUKE");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean aActive, boolean aRedstone) {
        if (side == facing) {
            return new ITexture[] { Textures.BlockIcons.casingTexturePages[BlockGTCasingsTT.texturePage][0],
                new TTRenderedExtendedFacingTexture(aActive ? TTMultiblockBase.ScreenON : TTMultiblockBase.ScreenOFF) };
        }
        return new ITexture[] { Textures.BlockIcons.casingTexturePages[BlockGTCasingsTT.texturePage][0] };
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
    public void onPreTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if ((aTick & 31) == 31) {
            ePowerPass = aBaseMetaTileEntity.isAllowedToWork();
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        structureBuild_EM("main", 0, 1, 0, stackSize, hintsOnly);
    }

    @Override
    public int survivalConstruct(ItemStack stackSize, int elementBudget, IItemSource source, EntityPlayerMP actor) {
        if (mMachine) return -1;
        return survivialBuildPiece("main", stackSize, 0, 1, 0, elementBudget, source, actor, false, true);
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
