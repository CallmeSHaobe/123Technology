package com.newmaa.othtech.common.item.itemTools;

import net.minecraft.util.ResourceLocation;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

public class ItemRecord extends net.minecraft.item.ItemRecord {

    public ItemRecord(String recordName) {
        super(recordName);
        setTextureName("123technology:Record." + recordName);
        setUnlocalizedName("itemRecord");
        setCreativeTab(CreativeTabsLoader.tabothtech);
    }

    @Override
    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation("123technology:" + "record." + recordName);
    }
}
