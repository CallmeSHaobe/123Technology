/*
 * spotless:off
 * KubaTech - Gregtech Addon
 * Copyright (C) 2022 - 2024  kuba6000
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see <https://www.gnu.org/licenses/>.
 * spotless:on
 */

package com.newmaa.othtech.machine.machineclass;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuba6000.mobsinfo.api.DummyWorld;
import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.IMobInfoProvider;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.event.PostMobRegistrationEvent;
import com.kuba6000.mobsinfo.api.event.PreMobsRegistrationEvent;
import com.kuba6000.mobsinfo.loader.MobRecipeLoader;
import com.newmaa.othtech.common.entity.EntityFakePlayer;
import com.newmaa.othtech.machine.OTEMegaEEC;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kubatech.config.Config;

public class OTHMobHandlerLoader {

    private static OTHMobHandlerLoader instance = null;

    public static void init() {
        instance = new OTHMobHandlerLoader();
        MinecraftForge.EVENT_BUS.register(instance);
    }

    public static Map<String, MobEECRecipe> recipeMap = new HashMap<>();

    /** 本模组自己注册、需要在 EEC / mobsinfo 里出现的实体 */
    private static final List<Class<? extends EntityLiving>> OWN_MOBS = java.util.Collections
        .singletonList(EntityFakePlayer.class);

    private static final Logger LOG = LogManager.getLogger("123Technology/MobHandler");

    public static class MobEECRecipe {

        public final List<MobDrop> mOutputs;

        public final MobRecipe recipe;
        public final int mDuration;
        public final EntityLiving entityCopy;

        public MobEECRecipe(List<MobDrop> transformedDrops, MobRecipe recipe) {
            this.mOutputs = transformedDrops;
            this.recipe = recipe;
            try {
                this.entityCopy = this.recipe.createEntityCopy();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException
                | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            mDuration = 20;
        }

        public ItemStack[] generateOutputs(Random rnd, OTEMegaEEC MTE, double attackDamage, int lootinglevel,
            boolean preferInfernalDrops, boolean voidAllDamagedAndEnchantedItems) {
            MTE.lEUt = 2147483640L * 64;
            MTE.mMaxProgresstime = 20;
            ArrayList<ItemStack> stacks = new ArrayList<>(this.mOutputs.size());
            this.entityCopy.setPosition(
                MTE.getBaseMetaTileEntity()
                    .getXCoord(),
                MTE.getBaseMetaTileEntity()
                    .getYCoord(),
                MTE.getBaseMetaTileEntity()
                    .getZCoord());
            for (MobDrop o : this.mOutputs) {
                if (voidAllDamagedAndEnchantedItems && (o.damages != null || o.enchantable != null)) continue;
                int chance = o.chance;

                double dChance = (double) chance / 100d;
                for (IChanceModifier chanceModifier : o.chanceModifiers) {
                    dChance = chanceModifier.apply(
                        dChance,
                        MTE.getBaseMetaTileEntity()
                            .getWorld(),
                        stacks,
                        MTE.EECPlayer,
                        this.entityCopy);
                }

                chance = (int) (dChance * 100d);
                if (chance == 0) continue;

                if (o.playerOnly) {
                    chance = (int) ((double) chance * Config.MobHandler.playerOnlyDropsModifier);
                    if (chance < 1) chance = 1;
                }
                int amount = o.stack.stackSize;
                if (lootinglevel > 0) {
                    chance += lootinglevel * 5000;
                    if (chance > 10000) {
                        int div = (int) Math.ceil(chance / 10000d);
                        amount *= div * 134217728;
                        chance /= div;
                    }
                }
                if (chance == 10000 || rnd.nextInt(10000) < chance) {
                    ItemStack s = o.stack.copy();
                    s.stackSize = amount;
                    if (o.enchantable != null) EnchantmentHelper.addRandomEnchantment(rnd, s, o.enchantable);
                    if (o.damages != null) {
                        int rChance = rnd.nextInt(recipe.mMaxDamageChance);
                        int cChance = 0;
                        for (Map.Entry<Integer, Integer> damage : o.damages.entrySet()) {
                            cChance += damage.getValue();
                            if (rChance <= cChance) {
                                s.setItemDamage(damage.getKey());
                                break;
                            }
                        }
                    }
                    stacks.add(s);
                }
            }
            return stacks.toArray(new ItemStack[0]);
        }

    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPreMobsRegistration(PreMobsRegistrationEvent event) {
        recipeMap.clear();
        registerOwnMobsIfMissing();
    }

    /**
     * 把本模组自己的实体补进 mobsinfo 的 {@code GeneralMobList}。
     * <p>
     * mobsinfo 的掉落缓存（{@code config/mobsinfo/MobRecipeLoader.cache}）默认只在「模组集合」变化时失效
     * （{@code ModAdditionRemoval}，见 {@code ModUtils#getModListVersionForMobDrops(false)}，版本号不参与比较）。
     * 也就是说老存档里缓存一旦生成，本模组后续新增/修改的实体就永远不会被扫描进去 ——
     * 表现为 mobsinfo 里看不到该实体，而依赖 mobsinfo 注册事件的 EEC 自然也不会得到配方
     * （issue #253）。
     * <p>
     * {@code PreMobsRegistrationEvent} 在 mobsinfo 开始处理前触发（服务端和客户端两条路径都会 fire），
     * 在这里补登记等于走和原生扫描完全相同的管线：后续会正常触发 {@code PostMobRegistrationEvent}，
     * 从而同时修好 mobsinfo 显示与本模组 EEC 的配方表。
     */
    private static void registerOwnMobsIfMissing() {
        for (Class<? extends EntityLiving> clazz : OWN_MOBS) {
            String mobName = EntityList.classToStringMapping.get(clazz);
            if (mobName == null || MobRecipeLoader.GeneralMobList.containsKey(mobName)) continue;

            Entity entity = EntityList.createEntityByName(mobName, new DummyWorld());
            if (!(entity instanceof EntityLiving mob) || !(entity instanceof IMobInfoProvider provider)) continue;

            ArrayList<MobDrop> drops = new ArrayList<>();
            provider.provideDropsInformation(drops);
            for (MobDrop drop : drops) drop.clampChance();

            MobRecipe recipe = MobRecipe.generateMobRecipe(mob, mobName, drops);
            MobRecipeLoader.GeneralMobList.put(mobName, new MobRecipeLoader.GeneralMappedMob(mob, recipe, drops, true));

            LOG.info("Registered own mob {} into mobsinfo (cache did not contain it)", mobName);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPostMobRegistration(PostMobRegistrationEvent event) {
        if (!event.drops.isEmpty() && event.recipe.isUsableInVial) {
            for (MobDrop drop : event.drops) {
                if (drop.playerOnly) {
                    drop.additionalInfo.add(
                        StatCollector.translateToLocalFormatted(
                            "kubatech.mobhandler.eec_chance",
                            (((double) drop.chance / 100d) * Config.MobHandler.playerOnlyDropsModifier)));
                }
            }
            @SuppressWarnings("unchecked")
            ArrayList<MobDrop> drops = (ArrayList<MobDrop>) event.drops.clone();
            if (!drops.isEmpty()) {
                recipeMap.put(event.currentMob, new MobEECRecipe(drops, event.recipe));
            }
        }
    }

}
