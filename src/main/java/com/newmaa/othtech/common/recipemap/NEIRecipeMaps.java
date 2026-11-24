package com.newmaa.othtech.common.recipemap;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;

public class NEIRecipeMaps {

    public static void IMCSender() {
        sendCatalyst("otht.recipe.COC", "gregtech:gt.blockmachines:23539");
        sendCatalyst("otht.recipe.MCA", "gregtech:gt.blockmachines:23549");
        sendCatalyst("bw.recipe.cal", "gregtech:gt.blockmachines:23549");
        sendCatalyst("otht.recipe.RCY", "gregtech:gt.blockmachines:23519");
        sendCatalyst("otht.recipe.void_nexus_upgrades", "gregtech:gt.blockmachines:23591");
        sendCatalyst("mobsinfo.mobhandler", "gregtech:gt.blockmachines:23536");
        sendCatalyst("otht.recipe.DeepSeikoAL", "gregtech:gt.blockmachines:23529");
        sendCatalyst("otht.recipe.DeepSeikoBEC", "gregtech:gt.blockmachines:23529");
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
