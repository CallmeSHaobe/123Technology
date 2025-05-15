package com.newmaa.othtech.common;

import com.newmaa.othtech.common.blocks.ModelAsuka;
import com.newmaa.othtech.common.blocks.ModelAyanami;

public class ItemAndBlockHandler implements Runnable {

    public static final ItemAndBlockHandler INSTANCE = new ItemAndBlockHandler();
    public static ModelAyanami MODEL_AYANAMI = new ModelAyanami().register();
    public static ModelAsuka MODEL_ASUKA = new ModelAsuka().register();

    @Override
    public void run() {

    }
}
