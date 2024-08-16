package com.newmaa.othtech.common.creativetab;

import net.minecraft.creativetab.CreativeTabs;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CreativeTabsLoader {

    public static CreativeTabs tabothtech;

    public CreativeTabsLoader(FMLPreInitializationEvent event) {
        tabothtech = new CreativeTabsothtech();
    }

}
