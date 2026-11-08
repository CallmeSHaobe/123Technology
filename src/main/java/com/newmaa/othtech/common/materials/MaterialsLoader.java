package com.newmaa.othtech.common.materials;

import com.newmaa.othtech.common.item.ItemRegister;
import com.newmaa.othtech.common.item.ItemRegisterISAModule;

public class MaterialsLoader {

    public static void load() {
        OTHEntangled.registerFluids();
        ItemRegister.registryItems();
        ItemRegister.registryItemContainers();
        ItemRegisterISAModule.registryItemContainersISA();
        ItemRegisterISAModule.registryItems();
    }
}
