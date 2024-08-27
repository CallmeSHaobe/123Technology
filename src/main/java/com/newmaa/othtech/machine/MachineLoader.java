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

    public static void loadMachines() {
        MegaIsaForge = new Mega_ISA_Forge(22123, "MegaISAForge", "§l神之艾萨锻炉").getStackForm(1);
        OTHItemList.MegaIsaForge.set(MegaIsaForge);

        inf_WirelessHatch = new infWirelessHatch(22124, "infWirelessEnergyHatch", "神人无线能源仓", 14).getStackForm(1);
        OTHItemList.inf_WirelessHatch.set(inf_WirelessHatch);

        legendary_WirelessHatch = new legendaryWireless(22125, "legendaryWirelessEnergyHatch", "传奇无线能源仓", 13)
            .getStackForm(1);
        OTHItemList.legendary_WirelessHatch.set(legendary_WirelessHatch);

        WirelessMAX = new MAXwireless(22126, "MAX1048576Hatch", "MAX 1,048,576A 无线能源仓", 14).getStackForm(1);
        OTHItemList.WirelessMAX.set(WirelessMAX);

        inf_infWirelessHatch = new WTFHatch(22127, "longlongHatch", "神威大将军毁天灭地无敌金刚炮无线能源仓", 14).getStackForm(1);
        OTHItemList.inf_infWirelessHatch.set(inf_infWirelessHatch);

        Mega_QFT = new MegaQFT(22128, "MegaQFT", "§l§b狄拉克潮汐").getStackForm(1);
        OTHItemList.Mega_QFT.set(Mega_QFT);

        NineInOne = new OneLastMachine(23520, "Mega9in1", "§l§c终极压缩巨型加工厂").getStackForm(1);
        OTHItemList.NineInOne.set(NineInOne);

    }
}
