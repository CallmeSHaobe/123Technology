package com.newmaa.othtech.machine;

import static net.minecraft.util.StatCollector.translateToLocal;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.hatch.OTEWTFHatch;

import gregtech.api.enums.TierEU;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchDynamoTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyMulti;
import tectech.thing.metaTileEntity.hatch.MTEHatchEnergyTunnel;
import tectech.thing.metaTileEntity.hatch.MTEHatchWirelessMulti;

public class MachineLoader {

    public static void loadMachines() {
        final int IDs = 23520;

        OTHItemList.NineInOne.set(new OTEMegaNineInOne(IDs, "Mega9in1", translateToLocal("ote.tn.mnio")));

        OTHItemList.MegaIsaForge.set(new OTEMegaIsaForge(IDs + 1, "MegaISAForge", translateToLocal("ote.tn.mifo")));

        OTHItemList.inf_WirelessHatch.set(
            new MTEHatchWirelessMulti(
                IDs + 2,
                "infWirelessEnergyHatch",
                translateToLocal("ote.tn.inf"),
                14,
                2147483647));

        OTHItemList.legendary_WirelessHatch.set(
            new MTEHatchWirelessMulti(
                IDs + 3,
                "legendaryWirelessEnergyHatch",
                translateToLocal("ote.tn.leg"),
                13,
                (int) TierEU.UXV));

        OTHItemList.WirelessMAX
            .set(new MTEHatchWirelessMulti(IDs + 4, "MAX1048576Hatch", translateToLocal("ote.tn.max"), 14, 1048576));

        OTHItemList.inf_infWirelessHatch
            .set(new OTEWTFHatch(IDs + 5, "longlongHatch", translateToLocal("ote.tn.long"), 14));

        OTHItemList.Mega_QFT.set(new OTEMegaQFT(IDs + 6, "OTEMegaQFT", translateToLocal("ote.tn.mqft")));

        OTHItemList.MEBFpp.set(new OTEMegaEBFGTpp(IDs + 7, "MegaEBFGTPP", translateToLocal("ote.tn.mebf")));

        OTHItemList.MFREpp.set(new OTEMegaFreezerGTpp(IDs + 8, "MegaFreezerGTPP", translateToLocal("ote.tn.fz")));

        OTHItemList.SINOPECd.set(new OTESINOPEC(IDs + 13, "SINOPEC", translateToLocal("ote.tn.sinopec")));

        OTHItemList.Chem.set(new OTELaoBaChemicalReactor(IDs + 14, "CHEMOTH", translateToLocal("ote.tn.chem")));

        OTHItemList.SF
            .set(new OTETangShanSteelFactory(IDs + 15, "TangshanSteelFactory", translateToLocal("ote.tn.ts")));

        OTHItemList.MegaEEC.set(new OTEMegaEEC(IDs + 16, "MegaEEC", translateToLocal("ote.tn.eec")));

        OTHItemList.Sun.set(new OTESunFactory(IDs + 17, "SunFactory", translateToLocal("ote.tn.sf")));

        OTHItemList.LCA.set(new OTELargeCircuitAssembler(IDs + 18, "LCA", translateToLocal("ote.tn.lca")));

        OTHItemList.CoccOven.set(new OTEEpicCokeOven(IDs + 19, "CoccOven", translateToLocal("ote.tn.cocc")));

        OTHItemList.WoodFusion
            .set(new OTEWoodenFusionReactor(IDs + 20, "WoodFusionReactor", translateToLocal("ote.tn.wfr")));

        OTHItemList.GTTEEnergyULV
            .set(new MTEHatchEnergyMulti(IDs + 21, "HVEnergy", "HV 1073741824A 能源仓", 3, 1073741824));

        OTHItemList.GTTEDynamoULV
            .set(new MTEHatchDynamoMulti(IDs + 22, "HVDynamo", "HV 2147483647A 动力仓", 3, 2147483647));

        OTHItemList.ImbaBlastFurnace.set(new OTEIMBABlastFurnace(IDs + 23, "IMBABlastFurnace"));

        OTHItemList.MISA.set(new OTEMegaIsaFactory(IDs + 24, "MISA", translateToLocal("ote.tn.misa")));

        OTHItemList.FooD.set(new OTEFoodGenerator(IDs + 25, "FOOD", translateToLocal("ote.tn.food")));

        OTHItemList.NQF
            .set(new OTENQFuelGeneratorUniversal(IDs + 26, "NaquadahFuelGenerator", translateToLocal("ote.tn.nfg")));

        OTHItemList.AF
            .set(new OTEMiniActiveTransformer(IDs + 27, "MiniActiveTransformer", translateToLocal("ote.tn.mat")));

        OTHItemList.NQFF.set(new OTEMegaNQFuelFactory(IDs + 28, "MegaNQFuelFactory", translateToLocal("ote.tn.mnf")));

        OTHItemList.MCA.set(new OTEMegaCircuitAssLine(IDs + 29, "MegaCircuitAssLine", translateToLocal("ote.tn.mca")));

        OTHItemList.TP.set(new OTELargeBin(IDs - 1, "LargeBin", translateToLocal("ote.tn.lb")));
        OTHItemList.EIO.set(new OTEEIO(IDs - 2, "EIOM", translateToLocal("ote.tn.eio")));
        OTHItemList.EXH.set(new OTEHeatExchanger(IDs - 3, "EXchanger", translateToLocal("ote.tn.exc")));
        OTHItemList.OTEGraveDragon.set(new OTEGraveDragon(IDs - 4, "GraveDragon", translateToLocal("ote.tn.gd")));
        OTHItemList.FISH.set(new OTEFishtorio(IDs - 5, "Fishtorio", translateToLocal("ote.tn.ft")));
        // TODO FUTURE

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

        OTHItemList.SteamNeinInOne
            .set(new OTEMiniSteamNineInOne(IDs + 57, "SteamNineInOne", translateToLocal("ote.tn.s9in1")));
        OTHItemList.Beeyonds.set(new OTEBeeyonds(IDs + 58, "IndustrialBeeHouse", translateToLocal("ote.tn.bee")));
        OTHItemList.OTEBBPlasmaForge
            .set(new OTEBBPlasmaForge(IDs + 59, "OTEBBPlasmaForge", translateToLocal("ote.tn.bbpf")));
        //OTHItemList.OTEComputer.set(new OTEComputer(IDs + 60, "OTEComputer", translateToLocal("ote.computer.name")));
        // TODO FUTURE

    }

    public static void loadMachinePostInit() {
        final int IDs = 23520;

        OTHItemList.SpaceElevatorModulePumpT4
            .set(new OTETileEntityModulePumpT4.ModulePumpT4(IDs + 56, "ModulePumpT4", translateToLocal("ote.tn.pump")));
    }

}
