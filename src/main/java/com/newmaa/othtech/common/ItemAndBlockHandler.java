package com.newmaa.othtech.common;

import com.newmaa.othtech.common.blocks.ModelAsuka;
import com.newmaa.othtech.common.blocks.ModelAyanami;
import com.newmaa.othtech.common.blocks.ModelTallHat;

public class ItemAndBlockHandler implements Runnable {

    public static final ItemAndBlockHandler INSTANCE = new ItemAndBlockHandler();
    public static ModelAyanami MODEL_AYANAMI = new ModelAyanami().register();
    public static ModelAsuka MODEL_ASUKA = new ModelAsuka().register();

    public static ModelTallHat TALL_HAT = new ModelTallHat().register();

    @Override
    public void run() {

    }
}
