package com.newmaa.othtech.common.entity;

import net.minecraft.world.World;

/**
 * 黄狗：属性与红狗 / 蓝狗完全一致，只有贴图不同。刷怪注册在 {@link EntityLoader#registerSpawns()}。
 */
public class EntityDogYellow extends EntityDogBase {

    public EntityDogYellow(World worldIn) {
        super(worldIn);
    }
}
