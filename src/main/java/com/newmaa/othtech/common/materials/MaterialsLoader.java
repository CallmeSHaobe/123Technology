package com.newmaa.othtech.common.materials;

import bartworks.API.WerkstoffAdderRegistry;

public class MaterialsLoader {

    public static void load() {
        WerkstoffAdderRegistry.addWerkstoffAdder(new liquids());
    }
}
