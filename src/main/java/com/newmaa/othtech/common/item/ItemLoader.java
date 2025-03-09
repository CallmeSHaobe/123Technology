package com.newmaa.othtech.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.foods.ingotHotDog;
import com.newmaa.othtech.common.item.foods.itemBrain;
import com.newmaa.othtech.common.item.foods.itemLeekBox;
import com.newmaa.othtech.common.item.foods.itemTST;
import com.newmaa.othtech.common.item.foods.itemZhangww;
import com.newmaa.othtech.common.item.tools.Dasima;
import com.newmaa.othtech.common.item.tools.itemRecord;
import com.newmaa.othtech.common.item.tools.itemSunLighter;
import com.newmaa.othtech.common.item.weapons.ShikanokoNoko;
import com.newmaa.othtech.common.item.weapons.itemNukeThrowable;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@SuppressWarnings("SameParameterValue")
public class ItemLoader {

    public static Item itemZhangww = new itemZhangww();
    public static Item ingotHotDog = new ingotHotDog();
    public static Item itemLeekBox = new itemLeekBox();
    public static Item Dasima = new Dasima();
    public static Item ShikanokoNoko = new ShikanokoNoko();
    public static Item itemSunLighter = new itemSunLighter();
    public static Item itemNukeThrowable = new itemNukeThrowable();
    public static Item itemTST = new itemTST();
    public static Item RecordPRKA = new itemRecord("PRK_A");
    public static Item RecordPRKB = new itemRecord("PRK_B");
    public static Item RecordEVAA = new itemRecord("EVA_A");
    public static Item RecordEVAB = new itemRecord("EVA_B");
    public static Item RecordEVAC = new itemRecord("EVA_C");
    public static Item RecordNGGU = new itemRecord("NGG_U");
    public static Item itemBrain = new itemBrain();

    public ItemLoader(FMLPreInitializationEvent event) {
        OTHItemList.Zhangww.set(registryAndCallback(itemZhangww, "itemZhangww"));
        OTHItemList.ingotHotDog.set(registryAndCallback(ingotHotDog, "ingotHotDog"));
        OTHItemList.Dasima.set(registryAndCallback(Dasima, "Dasima"));
        OTHItemList.ShikanokoNoko.set(registryAndCallback(ShikanokoNoko, "ShikanokoNoko"));
        OTHItemList.SunLighter.set(registryAndCallback(itemSunLighter, "itemSunLighter"));
        OTHItemList.LeekBox.set(registryAndCallback(itemLeekBox, "itemLeekBox"));
        OTHItemList.NukeThrowable.set(registryAndCallback(itemNukeThrowable, "itemNukeThrowable"));
        OTHItemList.RecordPRKA.set(registryAndCallback(RecordPRKA, "attackingBattle"));
        OTHItemList.RecordPRKB.set(registryAndCallback(RecordPRKB, "runningHorse"));
        OTHItemList.RecordEVAA.set(registryAndCallback(RecordEVAA, "EVAA"));
        OTHItemList.RecordEVAB.set(registryAndCallback(RecordEVAB, "EVAB"));
        OTHItemList.RecordEVAC.set(registryAndCallback(RecordEVAC, "EVAC"));
        OTHItemList.RecordNGGU.set(registryAndCallback(RecordNGGU, "NGGU"));
        OTHItemList.itemTST.set(registryAndCallback(itemTST, "itemTST"));
        OTHItemList.Brains.set(registryAndCallback(itemBrain, "BrainsHuman"));
    }

    private static ItemStack registryAndCallback(Item item, String name) {
        return registryAndCallback(item, name, 0);
    }

    private static ItemStack registryAndCallback(Item item, String name, int aMeta) {
        GameRegistry.registerItem(item, name);
        return new ItemStack(item, 1, aMeta);
    }

}
