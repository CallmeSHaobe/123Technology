package com.newmaa.othtech.common.item;

import static net.minecraft.item.ItemArmor.ArmorMaterial.DIAMOND;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.item.itemArmor.ItemHelmTallHatLaodeng;
import com.newmaa.othtech.common.item.itemFoods.IngotHotDog;
import com.newmaa.othtech.common.item.itemFoods.ItemBrain;
import com.newmaa.othtech.common.item.itemFoods.ItemLeekBox;
import com.newmaa.othtech.common.item.itemFoods.ItemSpoiledRice;
import com.newmaa.othtech.common.item.itemFoods.ItemTST;
import com.newmaa.othtech.common.item.itemFoods.ItemWhiteRice;
import com.newmaa.othtech.common.item.itemFoods.ItemZhangww;
import com.newmaa.othtech.common.item.itemMisc.ItemComputeCenter;
import com.newmaa.othtech.common.item.itemMisc.ItemDeepSeikoToken;
import com.newmaa.othtech.common.item.itemMisc.ItemDeepSeikoTokenSilver;
import com.newmaa.othtech.common.item.itemMisc.ItemRiceSingularity;
import com.newmaa.othtech.common.item.itemMisc.ItemSpacetimeCoolingCore;
import com.newmaa.othtech.common.item.itemSpawnEgg.ItemDogSpawnEgg;
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
    public static Item itemWhiteRice = new ItemWhiteRice();
    public static Item itemSpoiledRice = new ItemSpoiledRice();
    public static Item itemDeepSeikoToken = new ItemDeepSeikoToken();
    public static Item itemDeepSeikoTokenSilver = new ItemDeepSeikoTokenSilver();
    public static Item itemComputeCenter = new ItemComputeCenter();
    public static Item itemRiceSingularity = new ItemRiceSingularity();
    public static Item itemSpacetimeCoolingCore = new ItemSpacetimeCoolingCore();
    public static Item dogSpawnEgg = new ItemDogSpawnEgg();

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
        OTHItemList.WhiteRice.set(registryAndCallback(itemWhiteRice, "WhiteRice"));
        OTHItemList.SpoiledRice.set(registryAndCallback(itemSpoiledRice, "SpoiledRice"));
        OTHItemList.DeepSeikoToken.set(registryAndCallback(itemDeepSeikoToken, "DeepSeikoToken"));
        OTHItemList.DeepSeikoTokenSilver.set(registryAndCallback(itemDeepSeikoTokenSilver, "DeepSeikoTokenSilver"));
        OTHItemList.ComputeCenter.set(registryAndCallback(itemComputeCenter, "ComputeCenter"));
        OTHItemList.RiceSingularity.set(registryAndCallback(itemRiceSingularity, "RiceSingularity"));
        OTHItemList.SpacetimeCoolingCore.set(registryAndCallback(itemSpacetimeCoolingCore, "SpacetimeCoolingCore"));

        // 三种狗共用一个刷怪蛋物品，靠 metadata 区分（registryAndCallback 里已经 registerItem 过一次）
        OTHItemList.DogYellowSpawnEgg.set(registryAndCallback(dogSpawnEgg, "dogSpawnEgg"));
        OTHItemList.DogRedSpawnEgg.set(new ItemStack(dogSpawnEgg, 1, ItemDogSpawnEgg.META_RED));
        OTHItemList.DogBlueSpawnEgg.set(new ItemStack(dogSpawnEgg, 1, ItemDogSpawnEgg.META_BLUE));
    }

    private static ItemStack registryAndCallback(Item item, String name) {
        return registryAndCallback(item, name, 0);
    }

    private static ItemStack registryAndCallback(Item item, String name, int aMeta) {
        GameRegistry.registerItem(item, name);
        return new ItemStack(item, 1, aMeta);
    }

}
