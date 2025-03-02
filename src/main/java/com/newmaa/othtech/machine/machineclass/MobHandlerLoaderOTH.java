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
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.event.PostMobRegistrationEvent;
import com.kuba6000.mobsinfo.api.event.PreMobsRegistrationEvent;
import com.newmaa.othtech.machine.OTEMegaEEC;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import kubatech.Tags;
import kubatech.config.Config;

public class MobHandlerLoaderOTH {

    private static final Logger LOG = LogManager.getLogger(Tags.MODID + "[Mob Handler Loader]");

    private static MobHandlerLoaderOTH instance = null;

    public static void init() {
        instance = new MobHandlerLoaderOTH();
        MinecraftForge.EVENT_BUS.register(instance);
    }

    public static Map<String, MobEECRecipe> recipeMap = new HashMap<>();

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
