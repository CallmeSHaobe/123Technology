package com.newmaa.othtech.common.dimensions.gtoregen;

public class GTWorldGenLoader implements Runnable {

    @Override
    public void run() {
        for (EnumGTOreMixes oreMix : EnumGTOreMixes.values()) {
            oreMix.addGTOreLayer();
        }
        for (EnumGTOreMixes mix : EnumGTOreMixes.values()) {
            mix.addGaGregOreLayer();
        }
    }
}
