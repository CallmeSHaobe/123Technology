package com.newmaa.othtech.common.materials;

import com.github.bartimaeusnek.bartworks.API.WerkstoffAdderRegistry;

public class MaterialsLoader {

    public static void load() {
        WerkstoffAdderRegistry.addWerkstoffAdder(new liquids());
    }
}
