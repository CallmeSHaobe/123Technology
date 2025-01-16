package com.newmaa.othtech.common.item.tools;

import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;

import com.newmaa.othtech.common.creativetab.CreativeTabsLoader;

public class itemRecord extends ItemRecord {

    public itemRecord(String recordName) {
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
