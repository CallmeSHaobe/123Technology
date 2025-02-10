package com.newmaa.othtech.common.item;

import static com.newmaa.othtech.utils.TextHandler.texter;
import static com.newmaa.othtech.common.item.itemsHelper.BasicItemsISA.MetaItemISA;
import static com.newmaa.othtech.common.item.itemsHelper.ItemISAAdder.initItemISA;

import net.minecraft.item.Item;

import com.newmaa.othtech.common.OTHItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemRegisterISAModule {

    public static void registryItems() {
        Item[] itemsToReg = { MetaItemISA };

        for (Item item : itemsToReg) {
            GameRegistry.registerItem(item, item.getUnlocalizedName());
        }

    }

    public static void registryItemContainersISA() {
        OTHItemList.IsaNEI.set(initItemISA("NEIModule", 1, new String[] { texter("NEIModule", "tt.neiISA") }));
        OTHItemList.ISAIOS.set(initItemISA("IOSModule", 2, new String[] { texter("IOSModule", "tt.IOS") }));
        OTHItemList.ISAHYP.set(initItemISA("HYPModule", 3, new String[] { texter("HYPModule", "tt.HYP") }));
        OTHItemList.ISASPE.set(initItemISA("SPEModule", 4, new String[] { texter("SPEModule", "tt.SPE") }));

    }
}
