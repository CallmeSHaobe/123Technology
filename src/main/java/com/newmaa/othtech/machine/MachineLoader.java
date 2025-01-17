package com.newmaa.othtech.machine;

import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.hatch.OTEMetaTileEntity_Hatch_OutputBus_ME;
import com.newmaa.othtech.machine.hatch.OTEMetaTileEntity_Hatch_Output_ME;
import com.newmaa.othtech.machine.hatch.OTEWTFHatch;
import com.newmaa.othtech.machine.hatch.OTH_MTEHatchWirelessMulti;

import gregtech.api.enums.TierEU;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;

public class MachineLoader {

    public static ItemStack MegaIsaForge;
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
    public static ItemStack MISA;

    public static void loadMachines() {
        final int IDs = 23520;
        NineInOne = new OTEMegaNineInOne(IDs, "Mega9in1", "§l§c终极压缩巨型加工厂").getStackForm(1);
        OTHItemList.NineInOne.set(NineInOne);

        MegaIsaForge = new OTEMegaIsaForge(IDs + 1, "MegaISAForge", "§l神之艾萨锻炉").getStackForm(1);
        OTHItemList.MegaIsaForge.set(MegaIsaForge);

        OTHItemList.inf_WirelessHatch.set(
            new OTH_MTEHatchWirelessMulti(IDs + 2, "infWirelessEnergyHatch", "神人无线能源仓", 14, 2147483647)
                .getStackForm(1));

        OTHItemList.legendary_WirelessHatch.set(
            new OTH_MTEHatchWirelessMulti(IDs + 3, "legendaryWirelessEnergyHatch", "传奇无线能源仓", 13, (int) TierEU.UXV)
                .getStackForm(1));

        OTHItemList.WirelessMAX.set(
            new OTH_MTEHatchWirelessMulti(IDs + 4, "MAX1048576Hatch", "MAX 1,048,576A 无线能源仓", 14, 1048576)
                .getStackForm(1));

        inf_infWirelessHatch = new OTEWTFHatch(IDs + 5, "longlongHatch", "神威大将军毁天灭地无敌金刚炮无线能源仓", 14).getStackForm(1);
        OTHItemList.inf_infWirelessHatch.set(inf_infWirelessHatch);

        Mega_QFT = new OTEMegaQFT(IDs + 6, "OTEMegaQFT", "§l§b狄拉克潮汐").getStackForm(1);
        OTHItemList.Mega_QFT.set(Mega_QFT);

        MEBFpp = new OTEMegaEBFGTpp(IDs + 7, "MegaEBFGTPP", "巨型炽焱高炉").getStackForm(1);
        OTHItemList.MEBFpp.set(MEBFpp);

        MFREpp = new OTEMegaFreezerGTpp(IDs + 8, "MegaFreezerGTPP", "巨型凛冰冷冻机").getStackForm(1);
        OTHItemList.MFREpp.set(MFREpp);

        HatchME = new OTEMetaTileEntity_Hatch_Output_ME(IDs + 11, "OutputHatchME", "输出仓(ME)(123t)").getStackForm(1);
        OTHItemList.HatchME.set(HatchME);

        BusME = new OTEMetaTileEntity_Hatch_OutputBus_ME(IDs + 12, "OutputBusME", "输出总线(ME)(123t)").getStackForm(1);
        OTHItemList.BusME.set(BusME);

        SINOPECd = new OTESINOPEC(IDs + 13, "SINOPEC", "中国石化集成工厂").getStackForm(1);
        OTHItemList.SINOPECd.set(SINOPECd);

        Chem = new OTELaoBaChemicalReactor(IDs + 14, "CHEMOTH", "铑钯蜜汁化工厂").getStackForm(1);
        OTHItemList.Chem.set(Chem);

        SF = new OTETangShanSteelFactory(IDs + 15, "TangshanSteelFactory", "唐山炼钢厂").getStackForm(1);
        OTHItemList.SF.set(SF);

        EEC = new OTEMegaEEC(IDs + 16, "MegaEEC", "§l§c噬魂监狱").getStackForm(1);
        OTHItemList.MegaEEC.set(EEC);

        Sun = new OTESunFactory(IDs + 17, "SunFactory", "§l§4红日之将军恩情配件厂").getStackForm(1);
        OTHItemList.Sun.set(Sun);

        LCA = new OTELargeCircuitAssembler(IDs + 18, "LCA", "大型电路组装机").getStackForm(1);
        OTHItemList.LCA.set(LCA);

        coccoven = new OTEEpicCokeOven(IDs + 19, "CoccOven", "§c§l史诗焦炭终结者 T123").getStackForm(1);
        OTHItemList.CoccOven.set(coccoven);

        WoodFusion = new OTEWoodenFusionReactor(IDs + 20, "WoodFusionReactor", "§b§l压缩原木聚变反应堆Mk 0").getStackForm(1);
        OTHItemList.WoodFusion.set(WoodFusion);
        OTHItemList.GTTEEnergyULV
            .set(new MTEHatchEnergyMulti(IDs + 21, "HVEnergy", "HV 1073741824A 能源仓", 3, 1073741824).getStackForm(1L));
        OTHItemList.GTTEDynamoULV
            .set(new MTEHatchDynamoMulti(IDs + 22, "HVDynamo", "HV 2147483647A 动力仓", 3, 2147483647).getStackForm(1L));

        IMBAIMBAblastfurnace = new OTEIMBABlastFurnace(IDs + 23, "IMBABlastFurnace").getStackForm(1);
        OTHItemList.ImbaBlastFurnace.set(IMBAIMBAblastfurnace);

        MISA = new OTEMegaIsaFactory(IDs + 24, "MISA", "§a艾萨处理集成工厂OTH").getStackForm(1);
        OTHItemList.MISA.set(MISA);

    }
}
