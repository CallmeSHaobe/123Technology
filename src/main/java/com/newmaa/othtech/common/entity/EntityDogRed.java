package com.newmaa.othtech.common.entity;

import net.minecraft.world.World;

/**
 * 红狗：属性与黄狗 / 蓝狗完全一致，只有贴图不同。刷怪注册在 {@link EntityLoader#registerSpawns()}。
 */
public class EntityDogRed extends EntityDogBase {

    public EntityDogRed(World worldIn) {
        super(worldIn);
    }
}
