package com.newmaa.othtech.common.item;

import static com.newmaa.othtech.utils.TextHandler.texter;
import static com.newmaa.othtech.common.item.itemsHelper.BasicItems.MetaItem;
import static com.newmaa.othtech.common.item.itemsHelper.ItemAdder.initItem;

import net.minecraft.item.Item;

import com.newmaa.othtech.common.OTHItemList;

import cpw.mods.fml.common.registry.GameRegistry;

public class ItemRegister {

    public static void registryItems() {
        Item[] itemsToReg = { MetaItem };

        for (Item item : itemsToReg) {
            GameRegistry.registerItem(item, item.getUnlocalizedName());
        }

    }

    public static void registryItemContainers() {
        OTHItemList.dustIrOsSmM.set(initItem("dustIrOsSmM", 0, new String[] { texter("IrOsSm", "tt.123") }));
        OTHItemList.dustLookNEIM.set(initItem("dustLookNEIM", 1, new String[] { texter("nei", "tt.nei") }));
        OTHItemList.dustSnAsM.set(initItem("dustSnAsM", 2, new String[] { texter("snas", "tt.snas") }));
        OTHItemList.ingotDogM.set(initItem("ingotDogM", 3, new String[] { texter("dog", "tt.dog") }));
        OTHItemList.ingotSnAsM.set(initItem("ingotSnAsM", 5, new String[] { texter("snas", "tt.snas") }));
        OTHItemList.boardCasimirM
            .set(initItem("boardCasimirM", 6, new String[] { texter("boardCasimir", "tt.boardCasimir") }));
        OTHItemList.boardTransM.set(initItem("boardTransM", 7, new String[] { texter("boardTrans", "tt.boardTrans") }));
        OTHItemList.capTransM.set(initItem("capTransM", 8, new String[] { texter("capTrans", "tt.capTrans") }));
        OTHItemList.dioTransM.set(initItem("dioTransM", 9, new String[] { texter("dioTrans", "tt.dioTrans") }));
        OTHItemList.energyHatchSpacetimeM.set(
            initItem(
                "energyHatchSpacetimeM",
                10,
                new String[] { texter("energyHatchSpacetime", "tt.energyHatchSpacetime") }));
        OTHItemList.finCasimirM
            .set(initItem("finCasimirM", 11, new String[] { texter("finCasimir", "tt.finCasimir") }));
        OTHItemList.induTransM.set(initItem("induTransM", 12, new String[] { texter("induTrans", "tt.induTrans") }));
        OTHItemList.beeISAM.set(initItem("beeISAM", 13, new String[] { texter("beeISA", "tt.beeISA") }));
        OTHItemList.beeMagM.set(initItem("beeMagM", 14, new String[] { texter("beeMag", "tt.beeMag") }));
        OTHItemList.itemEnqingM
            .set(initItem("itemEnqingM", 15, new String[] { texter("itemEnqing", "tt.itemEnqing") }));
        OTHItemList.leCasimirM.set(initItem("leCasimirM", 16, new String[] { texter("leCasimir", "tt.leCasimir") }));
        OTHItemList.resTransM.set(initItem("resTransM", 17, new String[] { texter("resTrans", "tt.resTrans") }));
        OTHItemList.glassSingularityM
            .set(initItem("glassSingularityM", 18, new String[] { texter("glassSingularity", "tt.glassSingularity") }));
        OTHItemList.machineSingularityM.set(
            initItem(
                "machineSingularityM",
                19,
                new String[] { texter("machineSingularity", "tt.machineSingularity") }));
        OTHItemList.socCosmicM.set(initItem("socCosmicM", 20, new String[] { texter("socCosmic", "tt.socCosmic") }));
        OTHItemList.socInfM.set(initItem("socInfM", 21, new String[] { texter("socInf", "tt.socInf") }));
        OTHItemList.socNorM.set(initItem("socNorM", 22, new String[] { texter("socNor", "tt.socNor") }));
        OTHItemList.transTransM
            .set(initItem("transTransM", 23, new String[] { texter("transTrans", "tt.transTrans") }));
    }
}
