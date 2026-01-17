package com.newmaa.othtech.machine;

import static net.minecraft.util.StatCollector.translateToLocal;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.hatch.OTEHatchRack;
import com.newmaa.othtech.machine.hatch.OTEWTFHatch;

import gregtech.api.enums.Materials;
import gregtech.api.enums.TierEU;
import gtPlusPlus.xmod.gregtech.api.metatileentity.implementations.base.MTEHatchCustomFluidBase;
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

        OTHItemList.LargeSteamHatch.set(
            new MTEHatchCustomFluidBase(
                Materials.Steam.getGas(1)
                    .getFluid(), // Fluid to restrict hatch to
                123123123, // Capacity
                IDs + 63, // ID
                "LargeSteamHatch",
                translateToLocal("ote.tm.largesteamhatch.name"),
                0 // Casing texture
            ).getStackForm(1L));

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
            .set(new MTEHatchEnergyMulti(IDs + 21, "HVEnergy", translateToLocal("ote.en.hv"), 3, 1073741824));

        OTHItemList.GTTEDynamoULV
            .set(new MTEHatchDynamoMulti(IDs + 22, "HVDynamo", translateToLocal("ote.dy.hv"), 3, 2147483647));

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

        // Lasers

        final int AMPS = 1073741824;

        OTHItemList.LVA
            .set(new MTEHatchDynamoTunnel(IDs + 30, "LV Laser 1073DY", translateToLocal("ote.la.lva"), 1, AMPS));
        OTHItemList.LVB
            .set(new MTEHatchEnergyTunnel(IDs + 31, "LV Laser 1073EN", translateToLocal("ote.la.lvb"), 1, AMPS));

        OTHItemList.MVA
            .set(new MTEHatchDynamoTunnel(IDs + 32, "MV Laser 1073DY", translateToLocal("ote.la.mva"), 2, AMPS));
        OTHItemList.MVB
            .set(new MTEHatchEnergyTunnel(IDs + 33, "MV Laser 1073EN", translateToLocal("ote.la.mvb"), 2, AMPS));

        OTHItemList.HVA
            .set(new MTEHatchDynamoTunnel(IDs + 34, "HV Laser 1073DY", translateToLocal("ote.la.hva"), 3, AMPS));
        OTHItemList.HVB
            .set(new MTEHatchEnergyTunnel(IDs + 35, "HV Laser 1073EN", translateToLocal("ote.la.hvb"), 3, AMPS));

        OTHItemList.EVA
            .set(new MTEHatchDynamoTunnel(IDs + 36, "EV Laser 1073DY", translateToLocal("ote.la.eva"), 4, AMPS));
        OTHItemList.EVB
            .set(new MTEHatchEnergyTunnel(IDs + 37, "EV Laser 1073EN", translateToLocal("ote.la.evb"), 4, AMPS));

        OTHItemList.IVA
            .set(new MTEHatchDynamoTunnel(IDs + 38, "IV Laser 1073DY", translateToLocal("ote.la.iva"), 5, AMPS));
        OTHItemList.IVB
            .set(new MTEHatchEnergyTunnel(IDs + 39, "IV Laser 1073EN", translateToLocal("ote.la.ivb"), 5, AMPS));

        OTHItemList.LUVA
            .set(new MTEHatchDynamoTunnel(IDs + 40, "LUV Laser 1073DY", translateToLocal("ote.la.luv"), 6, AMPS));
        OTHItemList.LUVB
            .set(new MTEHatchEnergyTunnel(IDs + 41, "LUV Laser 1073EN", translateToLocal("ote.la.luvb"), 6, AMPS));

        OTHItemList.ZPMA
            .set(new MTEHatchDynamoTunnel(IDs + 42, "ZPM Laser 1073DY", translateToLocal("ote.la.zpm"), 7, AMPS));
        OTHItemList.ZPMB
            .set(new MTEHatchEnergyTunnel(IDs + 43, "ZPM Laser 1073EN", translateToLocal("ote.la.zpmb"), 7, AMPS));

        OTHItemList.UVA
            .set(new MTEHatchDynamoTunnel(IDs + 44, "UV Laser 1073DY", translateToLocal("ote.la.uv"), 8, AMPS));
        OTHItemList.UVB
            .set(new MTEHatchEnergyTunnel(IDs + 45, "UV Laser 1073EN", translateToLocal("ote.la.uvb"), 8, AMPS));

        OTHItemList.UHVA
            .set(new MTEHatchDynamoTunnel(IDs + 46, "UHV Laser 1073DY", translateToLocal("ote.la.uhv"), 9, AMPS));
        OTHItemList.UHVB
            .set(new MTEHatchEnergyTunnel(IDs + 47, "UHV Laser 1073EN", translateToLocal("ote.la.uhvb"), 9, AMPS));

        OTHItemList.UEVA
            .set(new MTEHatchDynamoTunnel(IDs + 48, "UEV Laser 1073DY", translateToLocal("ote.la.uev"), 10, AMPS));
        OTHItemList.UEVB
            .set(new MTEHatchEnergyTunnel(IDs + 49, "UEV Laser 1073EN", translateToLocal("ote.la.uevb"), 10, AMPS));

        OTHItemList.UIVA
            .set(new MTEHatchDynamoTunnel(IDs + 50, "UIV Laser 1073DY", translateToLocal("ote.la.uiv"), 11, AMPS));
        OTHItemList.UIVB
            .set(new MTEHatchEnergyTunnel(IDs + 51, "UIV Laser 1073EN", translateToLocal("ote.la.uivb"), 11, AMPS));

        OTHItemList.UMVA
            .set(new MTEHatchDynamoTunnel(IDs + 52, "UMV Laser 1073DY", translateToLocal("ote.la.umv"), 12, AMPS));
        OTHItemList.UMVB
            .set(new MTEHatchEnergyTunnel(IDs + 53, "UMV Laser 1073EN", translateToLocal("ote.la.umvb"), 12, AMPS));

        OTHItemList.UXVA
            .set(new MTEHatchDynamoTunnel(IDs + 54, "UXV Laser 1073DY", translateToLocal("ote.la.uxv"), 13, AMPS));
        OTHItemList.UXVB
            .set(new MTEHatchEnergyTunnel(IDs + 55, "UXV Laser 1073EN", translateToLocal("ote.la.uxvb"), 13, AMPS));

        OTHItemList.SteamNeinInOne
            .set(new OTEMiniSteamNineInOne(IDs + 57, "SteamNineInOne", translateToLocal("ote.tn.s9in1")));
        OTHItemList.Beeyonds.set(new OTEBeeyonds(IDs + 58, "IndustrialBeeHouse", translateToLocal("ote.tn.bee")));
        OTHItemList.OTEBBPlasmaForge
            .set(new OTEBBPlasmaForge(IDs + 59, "OTEBBPlasmaForge", translateToLocal("ote.tn.bbpf")));
        OTHItemList.OTEComputer.set(new OTEComputer(IDs + 60, "OTEComputer", translateToLocal("ote.computer.name")));
        OTHItemList.OTEHatchRack
            .set(new OTEHatchRack(IDs + 61, "OTEHatchRack", translateToLocal("ote.calc.rank"), 11).getStackForm(1L));
        // 模型错位,已无言
        // OTHItemList.OTEHatchRack.set(
        // new OTEHatchRack(IDs + 114514, "OTESuperHatchRack", translateToLocal("ote.super.calc.rank"), 1, true)
        // .getStackForm(1L));

        // bug的元凶:没有run()
        OTEHatchRack.run();
        OTHItemList.NASA
            .set(new OTEFireRocketAssembler(IDs + 62, "FireRocketAssembler", translateToLocal("ote.tn.nasa")));
    }

    public static void loadMachinePostInit() {
        final int IDs = 23520;

        OTHItemList.SpaceElevatorModulePumpT4
            .set(new OTETileEntityModulePumpT4.ModulePumpT4(IDs + 56, "ModulePumpT4", translateToLocal("ote.tn.pump")));
    }

}
