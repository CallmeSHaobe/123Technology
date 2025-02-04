package com.newmaa.othtech.common;

import com.newmaa.othtech.common.blocks.modelAsuka;
import com.newmaa.othtech.common.blocks.modelAyanami;

public class ItemAndBlockHandler implements Runnable {

    public static final ItemAndBlockHandler INSTANCE = new ItemAndBlockHandler();
    public static modelAyanami MODEL_AYANAMI = new modelAyanami().register();
    public static modelAsuka MODEL_ASUKA = new modelAsuka().register();

    @Override
    public void run() {

    }
}
