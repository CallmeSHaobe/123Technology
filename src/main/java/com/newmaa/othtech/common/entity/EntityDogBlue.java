package com.newmaa.othtech.common.entity;

import net.minecraft.world.World;

/**
 * 蓝狗：属性与黄狗 / 红狗完全一致，只有贴图不同。刷怪注册在 {@link EntityLoader#registerSpawns()}。
 */
public class EntityDogBlue extends EntityDogBase {

    public EntityDogBlue(World worldIn) {
        super(worldIn);
    }
}
