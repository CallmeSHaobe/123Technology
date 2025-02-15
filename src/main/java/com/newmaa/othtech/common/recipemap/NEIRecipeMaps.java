package com.newmaa.othtech.common.recipemap;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.event.FMLInterModComms;

public class NEIRecipeMaps {

    public static void IMCSender() {
        sendCatalyst("otht.recipe.COC", "gregtech:gt.blockmachines:23539");
        sendCatalyst("otht.recipe.MCA", "gregtech:gt.blockmachines:23549");
        sendCatalyst("bw.recipe.cal", "gregtech:gt.blockmachines:23549");
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
