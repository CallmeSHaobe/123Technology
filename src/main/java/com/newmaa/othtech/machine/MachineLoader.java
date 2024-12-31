package com.newmaa.othtech.machine;

import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.hatch.GT_MetaTileEntity_Hatch_OutputBus_ME_B;
import com.newmaa.othtech.machine.hatch.GT_MetaTileEntity_Hatch_Output_ME_B;
import com.newmaa.othtech.machine.hatch.GT_TE_MAXwireless;
import com.newmaa.othtech.machine.hatch.GT_TE_WTFHatch;
import com.newmaa.othtech.machine.hatch.GT_TE_infWirelessHatch;
import com.newmaa.othtech.machine.hatch.GT_TE_legendaryWireless;

import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;

public class MachineLoader {

    public static ItemStack MegaIsaForge;
    public static ItemStack inf_WirelessHatch;
    public static ItemStack legendary_WirelessHatch;
    public static ItemStack WirelessMAX;
    public static ItemStack inf_infWirelessHatch;
    public static ItemStack Mega_QFT;
    public static ItemStack NineInOne;
    public static ItemStack MEBFpp;
    public static ItemStack MFREpp;
    public static ItemStack EVACannon;
    public static ItemStack HatchME;
    public static ItemStack BusME;
    public static ItemStack SINOPECd;
    public static ItemStack Chem;
    public static ItemStack SF;
    public static ItemStack EEC;
    public static ItemStack Sun;
    public static ItemStack LCA;
    public static ItemStack coccoven;
    public static ItemStack WoodFusion;
    public static ItemStack IMBAIMBAblastfurnace;

    public static void loadMachines() {
        final int IDs = 23520;
        NineInOne = new GT_TE_MegaNineInOne(IDs, "Mega9in1", "§l§c终极压缩巨型加工厂").getStackForm(1);
        OTHItemList.NineInOne.set(NineInOne);

        MegaIsaForge = new GT_TE_MegaIsaForge(IDs + 1, "MegaISAForge", "§l神之艾萨锻炉").getStackForm(1);
        OTHItemList.MegaIsaForge.set(MegaIsaForge);

        inf_WirelessHatch = new GT_TE_infWirelessHatch(IDs + 2, "infWirelessEnergyHatch", "神人无线能源仓", 14)
            .getStackForm(1);
        OTHItemList.inf_WirelessHatch.set(inf_WirelessHatch);

        legendary_WirelessHatch = new GT_TE_legendaryWireless(IDs + 3, "legendaryWirelessEnergyHatch", "传奇无线能源仓", 13)
            .getStackForm(1);
        OTHItemList.legendary_WirelessHatch.set(legendary_WirelessHatch);

        WirelessMAX = new GT_TE_MAXwireless(IDs + 4, "MAX1048576Hatch", "MAX 1,048,576A 无线能源仓", 14).getStackForm(1);
        OTHItemList.WirelessMAX.set(WirelessMAX);

        inf_infWirelessHatch = new GT_TE_WTFHatch(IDs + 5, "longlongHatch", "神威大将军毁天灭地无敌金刚炮无线能源仓", 14).getStackForm(1);
        OTHItemList.inf_infWirelessHatch.set(inf_infWirelessHatch);

        Mega_QFT = new GT_TE_MegaQFTFake(IDs + 6, "GT_TE_MegaQFTFake", "§l§b狄拉克潮汐").getStackForm(1);
        OTHItemList.Mega_QFT.set(Mega_QFT);

        MEBFpp = new GT_TE_MegaEBFGTpp(IDs + 7, "MegaEBFGTPP", "巨型炽焱高炉").getStackForm(1);
        OTHItemList.MEBFpp.set(MEBFpp);

        MFREpp = new GT_TE_MegaFreezerGTpp(IDs + 8, "MegaFreezerGTPP", "巨型凛冰冷冻机").getStackForm(1);
        OTHItemList.MFREpp.set(MFREpp);

        HatchME = new GT_MetaTileEntity_Hatch_Output_ME_B(IDs + 11, "OutputHatchME", "输出仓(ME)(123t)").getStackForm(1);
        OTHItemList.HatchME.set(HatchME);

        BusME = new GT_MetaTileEntity_Hatch_OutputBus_ME_B(IDs + 12, "OutputBusME", "输出总线(ME)(123t)").getStackForm(1);
        OTHItemList.BusME.set(BusME);

        SINOPECd = new GT_TE_SINOPEC(IDs + 13, "SINOPEC", "中国石化集成工厂").getStackForm(1);
        OTHItemList.SINOPECd.set(SINOPECd);

        Chem = new GT_TE_CHEM(IDs + 14, "CHEMOTH", "铑钯蜜汁化工厂").getStackForm(1);
        OTHItemList.Chem.set(Chem);

        SF = new GT_TE_TangShanSteelFactory(IDs + 15, "TangshanSteelFactory", "唐山炼钢厂").getStackForm(1);
        OTHItemList.SF.set(SF);

        EEC = new GT_TE_MegaEEC(IDs + 16, "MegaEEC", "§l§c噬魂监狱").getStackForm(1);
        OTHItemList.MegaEEC.set(EEC);

        Sun = new GT_TE_SunFactory(IDs + 17, "SunFactory", "§l§4红日之将军恩情配件厂").getStackForm(1);
        OTHItemList.Sun.set(Sun);

        LCA = new GT_TE_LargeCircuitAssembler(IDs + 18, "LCA", "大型电路组装机").getStackForm(1);
        OTHItemList.LCA.set(LCA);

        coccoven = new GT_TE_EpicCokeOven(IDs + 19, "CoccOven", "§c§l史诗焦炭终结者 T123").getStackForm(1);
        OTHItemList.CoccOven.set(coccoven);

        WoodFusion = new GT_TE_WoodFusionReactor(IDs + 20, "WoodFusionReactor", "§b§l压缩原木聚变反应堆Mk 0").getStackForm(1);
        OTHItemList.WoodFusion.set(WoodFusion);
        OTHItemList.GTTEEnergyULV
            .set(new MTEHatchEnergyMulti(IDs + 21, "HVEnergy", "HV 1073741824A 能源仓", 3, 1073741824).getStackForm(1L));
        OTHItemList.GTTEDynamoULV
            .set(new MTEHatchDynamoMulti(IDs + 22, "HVDynamo", "HV 2147483647A 动力仓", 3, 2147483647).getStackForm(1L));

        IMBAIMBAblastfurnace = new GT_TE_IMBABlastFurnace(IDs + 23, "IMBABlastFurnace").getStackForm(1);
        OTHItemList.ImbaBlastFurnace.set(IMBAIMBAblastfurnace);

    }
}
