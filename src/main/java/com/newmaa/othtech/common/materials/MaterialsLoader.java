package com.newmaa.othtech.common.materials;

import com.newmaa.othtech.common.item.ItemRegister;
import com.newmaa.othtech.common.item.ItemRegisterISAModule;

import bartworks.API.WerkstoffAdderRegistry;

public class MaterialsLoader {

    public static void load() {
        WerkstoffAdderRegistry.addWerkstoffAdder(new liquids());
        ItemRegister.registryItems();
        ItemRegister.registryItemContainers();
        ItemRegisterISAModule.registryItemContainersISA();
        ItemRegisterISAModule.registryItems();
    }
}
