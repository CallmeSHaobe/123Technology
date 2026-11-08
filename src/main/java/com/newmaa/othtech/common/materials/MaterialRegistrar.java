package com.newmaa.othtech.common.materials;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.newmaa.othtech.OTHTechnology;

import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IMaterialHandler;

public final class MaterialRegistrar implements IMaterialHandler {

    private final String name;
    private final Supplier<Materials> factory;
    private final Consumer<Materials> assigner;

    public MaterialRegistrar(String name, Supplier<Materials> factory, Consumer<Materials> assigner) {
        this.name = name;
        this.factory = factory;
        this.assigner = assigner;
    }

    public String getName() {
        return name;
    }

    @Override
    public void onMaterialsInit() {
        final Materials material = factory.get();
        if (material == null) {
            OTHTechnology.LOG.error("Failed to build GregTech material {}", name);
            return;
        }
        material.mMetaItemSubID = MaterialIds.next();
        assigner.accept(material);
        OTHTechnology.LOG.info("Registered GregTech material {} with id {}", name, material.mMetaItemSubID);
    }
}
