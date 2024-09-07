package com.newmaa.othtech.machine;

import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.machine.hatch.MAXwireless;
import com.newmaa.othtech.machine.hatch.WTFHatch;
import com.newmaa.othtech.machine.hatch.infWirelessHatch;
import com.newmaa.othtech.machine.hatch.legendaryWireless;

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

    public static void loadMachines() {
        final int IDs = 23520;
        NineInOne = new GT_TE_MegaNineInOne(IDs, "Mega9in1", "§l§c终极压缩巨型加工厂").getStackForm(1);
        OTHItemList.NineInOne.set(NineInOne);

        MegaIsaForge = new GT_TE_MegaIsaForge(IDs + 1, "MegaISAForge", "§l神之艾萨锻炉").getStackForm(1);
        OTHItemList.MegaIsaForge.set(MegaIsaForge);

        inf_WirelessHatch = new infWirelessHatch(IDs + 2, "infWirelessEnergyHatch", "神人无线能源仓", 14).getStackForm(1);
        OTHItemList.inf_WirelessHatch.set(inf_WirelessHatch);

        legendary_WirelessHatch = new legendaryWireless(IDs + 3, "legendaryWirelessEnergyHatch", "传奇无线能源仓", 13)
            .getStackForm(1);
        OTHItemList.legendary_WirelessHatch.set(legendary_WirelessHatch);

        WirelessMAX = new MAXwireless(IDs + 4, "MAX1048576Hatch", "MAX 1,048,576A 无线能源仓", 14).getStackForm(1);
        OTHItemList.WirelessMAX.set(WirelessMAX);

        inf_infWirelessHatch = new WTFHatch(IDs + 5, "longlongHatch", "神威大将军毁天灭地无敌金刚炮无线能源仓", 14).getStackForm(1);
        OTHItemList.inf_infWirelessHatch.set(inf_infWirelessHatch);

        Mega_QFT = new GT_TE_MegaQFTFake(IDs + 6, "GT_TE_MegaQFTFake", "§l§b狄拉克潮汐").getStackForm(1);
        OTHItemList.Mega_QFT.set(Mega_QFT);

        MEBFpp = new GT_TE_MegaEBFGTpp(IDs + 7, "MegaEBFGTPP", "巨型炽焱高炉").getStackForm(1);
        OTHItemList.MEBFpp.set(MEBFpp);

        MFREpp = new GT_TE_MegaFreezerGTpp(IDs + 8, "MegaFreezerGTPP", "巨型凛冰冷冻机").getStackForm(1);
        OTHItemList.MFREpp.set(MFREpp);

        EVACannon = new GT_TE_EVACannon(IDs + 9, "EVACannon", "§l§6EVA专用改造阳电子炮GT").getStackForm(1);
        OTHItemList.EVACannon.set(EVACannon);

    }
}
