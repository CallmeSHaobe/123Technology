package com.newmaa.othtech.machine;

import net.minecraft.util.EnumChatFormatting;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.hatch.OTEHatchWirelessMulti;
import com.newmaa.othtech.machine.hatch.OTEWTFHatch;

import gregtech.api.enums.TierEU;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;

public class MachineLoader {

    public static void loadMachines() {
        final int IDs = 23520;

        OTHItemList.NineInOne.set(new OTEMegaNineInOne(IDs, "Mega9in1", "§l§c终极压缩巨型加工厂"));

        OTHItemList.MegaIsaForge.set(new OTEMegaIsaForge(IDs + 1, "MegaISAForge", "§l神之艾萨锻炉"));

        OTHItemList.inf_WirelessHatch
            .set(new OTEHatchWirelessMulti(IDs + 2, "infWirelessEnergyHatch", "神人无线能源仓", 14, 2147483647));

        OTHItemList.legendary_WirelessHatch
            .set(new OTEHatchWirelessMulti(IDs + 3, "legendaryWirelessEnergyHatch", "传奇无线能源仓", 13, (int) TierEU.UXV));

        OTHItemList.WirelessMAX
            .set(new OTEHatchWirelessMulti(IDs + 4, "MAX1048576Hatch", "MAX 1,048,576A 无线能源仓", 14, 1048576));

        OTHItemList.inf_infWirelessHatch.set(new OTEWTFHatch(IDs + 5, "longlongHatch", "神威大将军毁天灭地无敌金刚炮无线能源仓", 14));

        OTHItemList.Mega_QFT.set(new OTEMegaQFT(IDs + 6, "OTEMegaQFT", "§l§b狄拉克潮汐"));

        OTHItemList.MEBFpp.set(new OTEMegaEBFGTpp(IDs + 7, "MegaEBFGTPP", "巨型炽焱高炉"));

        OTHItemList.MFREpp.set(new OTEMegaFreezerGTpp(IDs + 8, "MegaFreezerGTPP", "巨型凛冰冷冻机"));

        OTHItemList.SINOPECd.set(new OTESINOPEC(IDs + 13, "SINOPEC", "中国石化集成工厂"));

        OTHItemList.Chem.set(new OTELaoBaChemicalReactor(IDs + 14, "CHEMOTH", "铑钯蜜汁化工厂"));

        OTHItemList.SF.set(new OTETangShanSteelFactory(IDs + 15, "TangshanSteelFactory", "唐山炼钢厂"));

        OTHItemList.MegaEEC.set(new OTEMegaEEC(IDs + 16, "MegaEEC", "§l§c噬魂监狱"));

        OTHItemList.Sun.set(new OTESunFactory(IDs + 17, "SunFactory", "§l§4红日之将军恩情配件厂"));

        OTHItemList.LCA.set(new OTELargeCircuitAssembler(IDs + 18, "LCA", "大型电路组装机"));

        OTHItemList.CoccOven.set(new OTEEpicCokeOven(IDs + 19, "CoccOven", "§c§l史诗焦炭终结者 T123"));

        OTHItemList.WoodFusion.set(new OTEWoodenFusionReactor(IDs + 20, "WoodFusionReactor", "§b§l压缩原木聚变反应堆Mk 0"));

        OTHItemList.GTTEEnergyULV
            .set(new MTEHatchEnergyMulti(IDs + 21, "HVEnergy", "HV 1073741824A 能源仓", 3, 1073741824));

        OTHItemList.GTTEDynamoULV
            .set(new MTEHatchDynamoMulti(IDs + 22, "HVDynamo", "HV 2147483647A 动力仓", 3, 2147483647));

        OTHItemList.ImbaBlastFurnace.set(new OTEIMBABlastFurnace(IDs + 23, "IMBABlastFurnace"));

        OTHItemList.MISA.set(new OTEMegaIsaFactory(IDs + 24, "MISA", "§a艾萨处理集成工厂OTH"));

        OTHItemList.FooD.set(new OTEFoodGenerator(IDs + 25, "FOOD", "食物发电机"));

        OTHItemList.NQF.set(new OTENQFuelGeneratorUniversal(IDs + 26, "NaquadahFuelGenerator", "§4通用硅岩燃料引擎"));

        OTHItemList.AF.set(new OTEMiniActiveTransformer(IDs + 27, "MiniActiveTransformer", "迷你有源变压器"));

        OTHItemList.NQFF
            .set(new OTEMegaNQFuelFactory(IDs + 28, "MegaNQFuelFactory", EnumChatFormatting.DARK_AQUA + "压缩硅岩燃料精炼厂"));

        OTHItemList.MCA
            .set(new OTEMegaCircuitAssLine(IDs + 29, "LargeCircuitAssLine", EnumChatFormatting.BOLD + "进阶高能电路装配线"));

        OTHItemList.TP.set(new OTELargeBin(IDs - 1, "LargeBin", "§b盖世神功垃圾桶"));
        OTHItemList.EIO.set(new OTEEIO(IDs - 2, "EIOM", "末影接口综合体"));
        OTHItemList.EXH.set(new OTEHeatExchanger(IDs - 3, "EXchanger", EnumChatFormatting.GOLD + "吊炸天热交换机"));
        OTHItemList.OTEGraveDragon
            .set(new OTEGraveDragon(IDs - 4, "GraveDragon", EnumChatFormatting.DARK_AQUA + "巨龙之墓"));
        OTHItemList.FISH.set(new OTEFishtorio(IDs - 5, "Fishtorio", EnumChatFormatting.GOLD + "异星渔场"));
        // Lasers

        final int AMPS = 1073741824;

        OTHItemList.LVA.set(new MTEHatchDynamoTunnel(IDs + 30, "LV Laser 1073DY", "LV 1,073,741,824A 激光源仓", 1, AMPS));
        OTHItemList.LVB.set(new MTEHatchEnergyTunnel(IDs + 31, "LV Laser 1073EN", "LV 1,073,741,824A 激光靶仓", 1, AMPS));

        OTHItemList.MVA.set(new MTEHatchDynamoTunnel(IDs + 32, "MV Laser 1073DY", "MV 1,073,741,824A 激光源仓", 2, AMPS));
        OTHItemList.MVB.set(new MTEHatchEnergyTunnel(IDs + 33, "MV Laser 1073EN", "MV 1,073,741,824A 激光靶仓", 2, AMPS));

        OTHItemList.HVA.set(new MTEHatchDynamoTunnel(IDs + 34, "HV Laser 1073DY", "HV 1,073,741,824A 激光源仓", 3, AMPS));
        OTHItemList.HVB.set(new MTEHatchEnergyTunnel(IDs + 35, "HV Laser 1073EN", "HV 1,073,741,824A 激光靶仓", 3, AMPS));

        OTHItemList.EVA.set(new MTEHatchDynamoTunnel(IDs + 36, "EV Laser 1073DY", "EV 1,073,741,824A 激光源仓", 4, AMPS));
        OTHItemList.EVB.set(new MTEHatchEnergyTunnel(IDs + 37, "EV Laser 1073EN", "EV 1,073,741,824A 激光靶仓", 4, AMPS));

        OTHItemList.IVA.set(new MTEHatchDynamoTunnel(IDs + 38, "IV Laser 1073DY", "IV 1,073,741,824A 激光源仓", 5, AMPS));
        OTHItemList.IVB.set(new MTEHatchEnergyTunnel(IDs + 39, "IV Laser 1073EN", "IV 1,073,741,824A 激光靶仓", 5, AMPS));

        OTHItemList.LUVA
            .set(new MTEHatchDynamoTunnel(IDs + 40, "LUV Laser 1073DY", "LUV 1,073,741,824A 激光源仓", 6, AMPS));
        OTHItemList.LUVB
            .set(new MTEHatchEnergyTunnel(IDs + 41, "LUV Laser 1073EN", "LUV 1,073,741,824A 激光靶仓", 6, AMPS));

        OTHItemList.ZPMA
            .set(new MTEHatchDynamoTunnel(IDs + 42, "ZPM Laser 1073DY", "ZPM 1,073,741,824A 激光源仓", 7, AMPS));
        OTHItemList.ZPMB
            .set(new MTEHatchEnergyTunnel(IDs + 43, "ZPM Laser 1073EN", "ZPM 1,073,741,824A 激光靶仓", 7, AMPS));

        OTHItemList.UVA.set(new MTEHatchDynamoTunnel(IDs + 44, "UV Laser 1073DY", "UV 1,073,741,824A 激光源仓", 8, AMPS));
        OTHItemList.UVB.set(new MTEHatchEnergyTunnel(IDs + 45, "UV Laser 1073EN", "UV 1,073,741,824A 激光靶仓", 8, AMPS));

        OTHItemList.UHVA
            .set(new MTEHatchDynamoTunnel(IDs + 46, "UHV Laser 1073DY", "UHV 1,073,741,824A 激光源仓", 9, AMPS));
        OTHItemList.UHVB
            .set(new MTEHatchEnergyTunnel(IDs + 47, "UHV Laser 1073EN", "UHV 1,073,741,824A 激光靶仓", 9, AMPS));

        OTHItemList.UEVA
            .set(new MTEHatchDynamoTunnel(IDs + 48, "UEV Laser 1073DY", "UEV 1,073,741,824A 激光源仓", 10, AMPS));
        OTHItemList.UEVB
            .set(new MTEHatchEnergyTunnel(IDs + 49, "UEV Laser 1073EN", "UEV 1,073,741,824A 激光靶仓", 10, AMPS));

        OTHItemList.UIVA
            .set(new MTEHatchDynamoTunnel(IDs + 50, "UIV Laser 1073DY", "UIV 1,073,741,824A 激光源仓", 11, AMPS));
        OTHItemList.UIVB
            .set(new MTEHatchEnergyTunnel(IDs + 51, "UIV Laser 1073EN", "UIV 1,073,741,824A 激光靶仓", 11, AMPS));

        OTHItemList.UMVA
            .set(new MTEHatchDynamoTunnel(IDs + 52, "UMV Laser 1073DY", "UMV 1,073,741,824A 激光源仓", 12, AMPS));
        OTHItemList.UMVB
            .set(new MTEHatchEnergyTunnel(IDs + 53, "UMV Laser 1073EN", "UMV 1,073,741,824A 激光靶仓", 12, AMPS));

        OTHItemList.UXVA
            .set(new MTEHatchDynamoTunnel(IDs + 54, "UXV Laser 1073DY", "UXV 1,073,741,824A 激光源仓", 13, AMPS));
        OTHItemList.UXVB
            .set(new MTEHatchEnergyTunnel(IDs + 55, "UXV Laser 1073EN", "UXV 1,073,741,824A 激光靶仓", 13, AMPS));
    }

    public static void loadMachinePostInit() {
        final int IDs = 23520;

        OTHItemList.SpaceElevatorModulePumpT4
            .set(new OTETileEntityModulePumpT4.ModulePumpT4(IDs + 56, "ModulePumpT4", "太空钻机模块MK-321"));
    }

}
