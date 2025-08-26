package com.newmaa.othtech.common.item;

import static net.minecraft.item.ItemArmor.ArmorMaterial.DIAMOND;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.itemArmor.ItemHelmTallHatLaodeng;
import com.newmaa.othtech.common.item.itemFoods.IngotHotDog;
import com.newmaa.othtech.common.item.itemFoods.ItemBrain;
import com.newmaa.othtech.common.item.itemFoods.ItemLeekBox;
import com.newmaa.othtech.common.item.itemFoods.ItemTST;
import com.newmaa.othtech.common.item.itemFoods.ItemZhangww;
import com.newmaa.othtech.common.item.itemTools.Dasima;
import com.newmaa.othtech.common.item.itemTools.ItemRecord;
import com.newmaa.othtech.common.item.itemTools.ItemSunLighter;
import com.newmaa.othtech.common.item.itemWeapons.ItemNukeThrowable;
import com.newmaa.othtech.common.item.itemWeapons.ShikanokoNoko;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@SuppressWarnings("SameParameterValue")
public class ItemLoader {

    public static Item itemZhangww = new ItemZhangww();
    public static Item ingotHotDog = new IngotHotDog();
    public static Item itemLeekBox = new ItemLeekBox();
    public static Item Dasima = new Dasima();
    public static Item ShikanokoNoko = new ShikanokoNoko();
    public static Item itemSunLighter = new ItemSunLighter();
    public static Item itemNukeThrowable = new ItemNukeThrowable();
    public static Item itemTST = new ItemTST();
    public static Item RecordPRKA = new ItemRecord("PRK_A");
    public static Item RecordPRKB = new ItemRecord("PRK_B");
    public static Item RecordEVAA = new ItemRecord("EVA_A");
    public static Item RecordEVAB = new ItemRecord("EVA_B");
    public static Item RecordEVAC = new ItemRecord("EVA_C");
    public static Item RecordNGGU = new ItemRecord("NGG_U");
    public static Item itemBrain = new ItemBrain();
    public static Item itemHelmTallHatLaodeng = new ItemHelmTallHatLaodeng(DIAMOND, 0, "HelmTallHatLaodeng");

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
        OTHItemList.Tallhat.set(registryAndCallback(itemHelmTallHatLaodeng, "HelmTallHatLaodeng"));
    }

    private static ItemStack registryAndCallback(Item item, String name) {
        return registryAndCallback(item, name, 0);
    }

    private static ItemStack registryAndCallback(Item item, String name, int aMeta) {
        GameRegistry.registerItem(item, name);
        return new ItemStack(item, 1, aMeta);
    }

}
