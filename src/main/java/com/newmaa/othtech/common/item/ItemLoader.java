package com.newmaa.othtech.common.item;

import net.minecraft.item.Item;

import com.newmaa.othtech.common.item.foods.ingotHotDog;
import com.newmaa.othtech.common.item.foods.itemLeekBox;
import com.newmaa.othtech.common.item.foods.itemZhangww;
import com.newmaa.othtech.common.item.tools.Dasima;
import com.newmaa.othtech.common.item.tools.itemRecord;
import com.newmaa.othtech.common.item.tools.itemSunLighter;
import com.newmaa.othtech.common.item.weapons.ShikanokoNoko;
import com.newmaa.othtech.common.item.weapons.itemNukeThrowable;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemLoader {

    public static Item itemZhangww = new itemZhangww();
    public static Item ingotHotDog = new ingotHotDog();
    public static Item itemLeekBox = new itemLeekBox();
    public static Item Dasima = new Dasima();
    public static Item ShikanokoNoko = new ShikanokoNoko();
    public static Item itemSunLighter = new itemSunLighter();
    public static Item itemNukeThrowable = new itemNukeThrowable();
    public static Item RecordPRKA = new itemRecord("PRK_A");
    public static Item RecordPRKB = new itemRecord("PRK_B");
    public static Item RecordEVAA = new itemRecord("EVA_A");
    public static Item RecordEVAB = new itemRecord("EVA_B");
    public static Item RecordEVAC = new itemRecord("EVA_C");
    public static Item RecordNGGU = new itemRecord("NGG_U");

    public ItemLoader(FMLPreInitializationEvent event) {
        IRegistry(itemZhangww, "itemZhangww");
        IRegistry(ingotHotDog, "ingotHotDog");
        IRegistry(Dasima, "Dasima");
        IRegistry(ShikanokoNoko, "ShikanokoNoko");
        IRegistry(itemSunLighter, "itemSunLighter");
        IRegistry(itemLeekBox, "itemLeekBox");
        IRegistry(itemNukeThrowable, "itemNukeThrowable");
        IRegistry(RecordPRKA, "attackingBattle");
        IRegistry(RecordPRKB, "runningHorse");
        IRegistry(RecordEVAA, "EVAA");
        IRegistry(RecordEVAB, "EVAB");
        IRegistry(RecordEVAC, "EVAC");
        IRegistry(RecordNGGU, "NGGU");
    }

    private static void IRegistry(Item item, String name) {
        GameRegistry.registerItem(item, name);
    }

}
