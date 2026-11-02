package com.newmaa.othtech.common.recipemap;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;

public class NEIRecipeMaps {

    public static void IMCSender() {
        sendCatalyst("otht.recipe.COC", "gregtech:gt.blockmachines:23539");
        sendCatalyst("otht.recipe.MCA", "gregtech:gt.blockmachines:23549");
        sendCatalyst("bw.recipe.cal", "gregtech:gt.blockmachines:23549");
        sendCatalyst("otht.recipe.RCY", "gregtech:gt.blockmachines:23519");
        sendCatalyst("mobsinfo.mobhandler", "gregtech:gt.blockmachines:23536");
        // 迪普西科（双模式共用主机作为 NEI 催化剂）
        sendCatalyst("otht.recipe.DeepSeikoAL", "gregtech:gt.blockmachines:23529");
        sendCatalyst("otht.recipe.DeepSeikoBEC", "gregtech:gt.blockmachines:23529");
        // 机体升级（升级材料清单）：同样挂在主机上 —— 对主机按 U/R 就能翻到，不用记配方图名字
        sendCatalyst("otht.recipe.DeepSeikoUpgrade", "gregtech:gt.blockmachines:23529");
    }

    private static void sendCatalyst(String aName, String aStack, int aPriority) {
        NBTTagCompound aNBT = new NBTTagCompound();
        aNBT.setString("handlerID", aName);
        aNBT.setString("itemName", aStack);
        aNBT.setInteger("priority", aPriority);
        FMLInterModComms.sendMessage("NotEnoughItems", "registerCatalystInfo", aNBT);
    }

    private static void sendCatalyst(String aName, String aStack) {
        sendCatalyst(aName, aStack, 0);
    }
}
