package com.newmaa.othtech.machine;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.hatch.OTEMetaTileEntity_Hatch_OutputBus_ME;
import com.newmaa.othtech.machine.hatch.OTEMetaTileEntity_Hatch_Output_ME;
import com.newmaa.othtech.machine.hatch.OTEWTFHatch;
import com.newmaa.othtech.machine.hatch.OTH_MTEHatchWirelessMulti;

import gregtech.api.enums.TierEU;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

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
    public static ItemStack FOOD;
    public static ItemStack NQF;
    public static ItemStack AF;
    public static ItemStack NQFF;
    public static ItemStack MCA;

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

        FOOD = new OTEFoodGenerator(IDs + 25, "FOOD", "食物发电机").getStackForm(1);
        OTHItemList.FooD.set(FOOD);

        NQF = new OTENQFuelGeneratorUniversal(IDs + 26, "NaquadahFuelGenerator", "§4通用硅岩燃料引擎").getStackForm(1);
        OTHItemList.NQF.set(NQF);

        AF = new OTEMiniActiveTransformer(IDs + 27, "MiniActiveTransformer", "迷你有源变压器").getStackForm(1);
        OTHItemList.AF.set(AF);

        NQFF = new OTEMegaNQFuelFactory(IDs + 28, "MegaNQFuelFactory", EnumChatFormatting.DARK_AQUA + "压缩硅岩燃料精炼厂")
            .getStackForm(1);
        OTHItemList.NQFF.set(NQFF);

        MCA = new OTEMegaCircuitAssLine(IDs + 29, "LargeCircuitAssLine", EnumChatFormatting.BOLD + "进阶高能电路装配线")
            .getStackForm(1);
        OTHItemList.MCA.set(MCA);

        // Lasers

        OTHItemList.LVA.set(
            new MTEHatchDynamoTunnel(IDs + 30, "LV Laser 1073DY", "LV 1,073,741,824A 激光源仓", 1, 1073741824)
                .getStackForm(1));
        OTHItemList.LVB.set(
            new MTEHatchEnergyTunnel(IDs + 31, "LV Laser 1073EN", "LV 1,073,741,824A 激光靶仓", 1, 1073741824)
                .getStackForm(1));

        OTHItemList.MVA.set(
            new MTEHatchDynamoTunnel(IDs + 32, "MV Laser 1073DY", "MV 1,073,741,824A 激光源仓", 2, 1073741824)
                .getStackForm(1));
        OTHItemList.MVB.set(
            new MTEHatchEnergyTunnel(IDs + 33, "MV Laser 1073EN", "MV 1,073,741,824A 激光靶仓", 2, 1073741824)
                .getStackForm(1));

        OTHItemList.HVA.set(
            new MTEHatchDynamoTunnel(IDs + 34, "HV Laser 1073DY", "HV 1,073,741,824A 激光源仓", 3, 1073741824)
                .getStackForm(1));
        OTHItemList.HVB.set(
            new MTEHatchEnergyTunnel(IDs + 35, "HV Laser 1073EN", "HV 1,073,741,824A 激光靶仓", 3, 1073741824)
                .getStackForm(1));

        OTHItemList.EVA.set(
            new MTEHatchDynamoTunnel(IDs + 36, "EV Laser 1073DY", "EV 1,073,741,824A 激光源仓", 4, 1073741824)
                .getStackForm(1));
        OTHItemList.EVB.set(
            new MTEHatchEnergyTunnel(IDs + 37, "EV Laser 1073EN", "EV 1,073,741,824A 激光靶仓", 4, 1073741824)
                .getStackForm(1));

        OTHItemList.IVA.set(
            new MTEHatchDynamoTunnel(IDs + 38, "IV Laser 1073DY", "IV 1,073,741,824A 激光源仓", 5, 1073741824)
                .getStackForm(1));
        OTHItemList.IVB.set(
            new MTEHatchEnergyTunnel(IDs + 39, "IV Laser 1073EN", "IV 1,073,741,824A 激光靶仓", 5, 1073741824)
                .getStackForm(1));

        OTHItemList.LUVA.set(
            new MTEHatchDynamoTunnel(IDs + 40, "LUV Laser 1073DY", "LUV 1,073,741,824A 激光源仓", 6, 1073741824)
                .getStackForm(1));
        OTHItemList.LUVB.set(
            new MTEHatchEnergyTunnel(IDs + 41, "LUV Laser 1073EN", "LUV 1,073,741,824A 激光靶仓", 6, 1073741824)
                .getStackForm(1));

        OTHItemList.ZPMA.set(
            new MTEHatchDynamoTunnel(IDs + 42, "ZPM Laser 1073DY", "ZPM 1,073,741,824A 激光源仓", 7, 1073741824)
                .getStackForm(1));
        OTHItemList.ZPMB.set(
            new MTEHatchEnergyTunnel(IDs + 43, "ZPM Laser 1073EN", "ZPM 1,073,741,824A 激光靶仓", 7, 1073741824)
                .getStackForm(1));

        OTHItemList.UVA.set(
            new MTEHatchDynamoTunnel(IDs + 44, "UV Laser 1073DY", "UV 1,073,741,824A 激光源仓", 8, 1073741824)
                .getStackForm(1));
        OTHItemList.UVB.set(
            new MTEHatchEnergyTunnel(IDs + 45, "UV Laser 1073EN", "UV 1,073,741,824A 激光靶仓", 8, 1073741824)
                .getStackForm(1));

        OTHItemList.UHVA.set(
            new MTEHatchDynamoTunnel(IDs + 46, "UHV Laser 1073DY", "UHV 1,073,741,824A 激光源仓", 9, 1073741824)
                .getStackForm(1));
        OTHItemList.UHVB.set(
            new MTEHatchEnergyTunnel(IDs + 47, "UHV Laser 1073EN", "UHV 1,073,741,824A 激光靶仓", 9, 1073741824)
                .getStackForm(1));

        OTHItemList.UEVA.set(
            new MTEHatchDynamoTunnel(IDs + 48, "UEV Laser 1073DY", "UEV 1,073,741,824A 激光源仓", 10, 1073741824)
                .getStackForm(1));
        OTHItemList.UEVB.set(
            new MTEHatchEnergyTunnel(IDs + 49, "UEV Laser 1073EN", "UEV 1,073,741,824A 激光靶仓", 10, 1073741824)
                .getStackForm(1));

        OTHItemList.UIVA.set(
            new MTEHatchDynamoTunnel(IDs + 50, "UIV Laser 1073DY", "UIV 1,073,741,824A 激光源仓", 11, 1073741824)
                .getStackForm(1));
        OTHItemList.UIVB.set(
            new MTEHatchEnergyTunnel(IDs + 51, "UIV Laser 1073EN", "UIV 1,073,741,824A 激光靶仓", 11, 1073741824)
                .getStackForm(1));

        OTHItemList.UMVA.set(
            new MTEHatchDynamoTunnel(IDs + 52, "UMV Laser 1073DY", "UMV 1,073,741,824A 激光源仓", 12, 1073741824)
                .getStackForm(1));
        OTHItemList.UMVB.set(
            new MTEHatchEnergyTunnel(IDs + 53, "UMV Laser 1073EN", "UMV 1,073,741,824A 激光靶仓", 12, 1073741824)
                .getStackForm(1));

        OTHItemList.UXVA.set(
            new MTEHatchDynamoTunnel(IDs + 54, "UXV Laser 1073DY", "UXV 1,073,741,824A 激光源仓", 13, 1073741824)
                .getStackForm(1));
        OTHItemList.UXVB.set(
            new MTEHatchEnergyTunnel(IDs + 55, "UXV Laser 1073EN", "UXV 1,073,741,824A 激光靶仓", 13, 1073741824)
                .getStackForm(1));
    }
}
