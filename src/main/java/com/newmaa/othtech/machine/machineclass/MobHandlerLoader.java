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

import static gregtech.api.enums.Mods.NewHorizonsCoreMod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kuba6000.mobsinfo.api.IChanceModifier;
import com.kuba6000.mobsinfo.api.MobDrop;
import com.kuba6000.mobsinfo.api.MobOverride;
import com.kuba6000.mobsinfo.api.MobRecipe;
import com.kuba6000.mobsinfo.api.event.MobNEIRegistrationEvent;
import com.kuba6000.mobsinfo.api.event.PostMobRegistrationEvent;
import com.kuba6000.mobsinfo.api.event.PostMobsOverridesLoadEvent;
import com.kuba6000.mobsinfo.api.event.PreMobsRegistrationEvent;
import com.newmaa.othtech.machine.GT_TE_MegaEEC;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.TierEU;
import gregtech.api.util.GT_Utility;
import kubatech.Tags;
import kubatech.api.helpers.ReflectionHelper;
import kubatech.config.Config;

public class MobHandlerLoader {

    private static final Logger LOG = LogManager.getLogger(Tags.MODID + "[Mob Handler Loader]");

    private static MobHandlerLoader instance = null;

    public static void init() {
        instance = new MobHandlerLoader();
        MinecraftForge.EVENT_BUS.register(instance);
    }

    public static Map<String, MobEECRecipe> recipeMap = new HashMap<>();

    public static class MobEECRecipe {

        public final List<MobDrop> mOutputs;

        public final MobRecipe recipe;

        public final long mEUt = 64L * TierEU.MAX;
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
            mDuration = 10;
        }

        public ItemStack[] generateOutputs(Random rnd, GT_TE_MegaEEC MTE, double attackDamage, int lootinglevel,
            boolean preferInfernalDrops, boolean voidAllDamagedAndEnchantedItems) {
            MTE.lEUt = mEUt;
            MTE.mMaxProgresstime = 10;
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

                if (o.playerOnly) {
                    chance = (int) ((double) chance * Config.MobHandler.playerOnlyDropsModifier);
                    if (chance < 1) chance = 1;
                }
                int amount = o.stack.stackSize;
                if (o.lootable && lootinglevel > 0) {
                    chance += lootinglevel * 5000;
                    if (chance > 10000) {
                        int div = (int) Math.ceil(chance / 10000d);
                        amount *= div;
                        chance /= div;
                    }
                }
                if (chance != 10000) {
                    ItemStack s = o.stack.copy();
                    s.stackSize = 134217728;
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
            @SuppressWarnings("unchecked")
            ArrayList<MobDrop> drops = (ArrayList<MobDrop>) event.drops.clone();
            if (!drops.isEmpty()) {
                recipeMap.put(event.currentMob, new MobEECRecipe(drops, event.recipe));
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPostOverridesConfigLoad(PostMobsOverridesLoadEvent event) throws ReflectiveOperationException {
        if (NewHorizonsCoreMod.isModLoaded()) {
            LOG.info("Detected GTNH Core Mod, parsing custom drops from there.");
            final Class<?> cMainRegistry = Class.forName("com.dreammaster.main.MainRegistry");
            final Object dropsHandler = cMainRegistry.getField("Module_CustomDrops")
                .get(null);
            if (dropsHandler == null) return;
            final Class<?> cDrops = Class.forName("com.dreammaster.modcustomdrops.CustomDrops");
            final Object coredrops = ReflectionHelper.getField(dropsHandler, "_mCustomDrops", null);
            final Method mGetCustomDrops = cDrops.getMethod("getCustomDrops");

            final Class<?> cCustomDrop = Class.forName("com.dreammaster.modcustomdrops.CustomDrops$CustomDrop");
            final Method mGetCustomDropEntityName = cCustomDrop.getMethod("getEntityName");
            final Method mGetCustomDropDrops = cCustomDrop.getMethod("getDrops");

            final Class<?> cDrop = Class.forName("com.dreammaster.modcustomdrops.CustomDrops$CustomDrop$Drop");
            final Method mDropGetItemName = cDrop.getMethod("getItemName");
            final Method mDropGetChance = cDrop.getMethod("getChance");
            final Method mDropGetAmount = cDrop.getMethod("getAmount");
            final Method mDropGetIsRandomAmount = cDrop.getMethod("getIsRandomAmount");

            if (coredrops != null) {
                final ArrayList<?> customDrops = new ArrayList<>((ArrayList<?>) mGetCustomDrops.invoke(coredrops));
                for (final Object customDrop : customDrops) {
                    try {
                        final String entityName = (String) mGetCustomDropEntityName.invoke(customDrop);

                        final Class<?> eclass = Class.forName(entityName);
                        if (!EntityLiving.class.isAssignableFrom(eclass)) continue;
                        final String ename = EntityList.classToStringMapping.get(eclass);
                        if (ename == null) continue;
                        final MobOverride override = event.overrides.computeIfAbsent(ename, k -> new MobOverride());
                        final List<?> entityDrops = (List<?>) mGetCustomDropDrops.invoke(customDrop);

                        for (final Object drop : entityDrops) {
                            final String itemName = (String) mDropGetItemName.invoke(drop);
                            String[] parts = itemName.split(":");
                            ItemStack stack = GameRegistry.findItemStack(parts[0], parts[1], 1);
                            if (stack == null) continue;
                            if (parts.length > 2) stack.setItemDamage(Integer.parseInt(parts[2]));
                            String pNBT = ReflectionHelper.getField(drop, "mTag", null);
                            if (pNBT != null && !pNBT.isEmpty()) {
                                try {
                                    stack.stackTagCompound = (NBTTagCompound) JsonToNBT.func_150315_a(pNBT);
                                } catch (Exception ignored) {}
                            }
                            int chance = ((int) mDropGetChance.invoke(drop)) * 100;
                            int amount = (int) mDropGetAmount.invoke(drop);
                            if ((boolean) mDropGetIsRandomAmount.invoke(drop)) {
                                // average chance formula
                                // chance *= ((((amount * (amount + 1d)) / 2d)) + 1d) / (amount + 1d);
                                chance *= (2d + (amount * amount) + amount) / (2d * (amount + 1d));
                                amount = 1;
                                if (chance > 10000) {
                                    int div = (int) Math.ceil(chance / 10000d);
                                    amount *= div;
                                    chance /= div;
                                }
                            }
                            stack.stackSize = amount;
                            // Drops from coremod are player only
                            MobDrop mobDrop = new MobDrop(
                                stack,
                                MobDrop.DropType.Normal,
                                chance,
                                null,
                                null,
                                false,
                                true);
                            mobDrop.additionalInfo.add(
                                StatCollector.translateToLocalFormatted(
                                    "kubatech.mobhandler.eec_chance",
                                    (((double) chance / 100d) * Config.MobHandler.playerOnlyDropsModifier)));
                            override.additions.add(mobDrop);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onMobNEIRegistration(MobNEIRegistrationEvent event) {
        MobEECRecipe recipe = recipeMap.get(event.mobName);
        if (recipe != null) {
            event.additionalInformation.addAll(
                Arrays.asList(
                    GT_Utility.trans("153", "Usage: ") + GT_Utility.formatNumbers(recipe.mEUt) + " EU/t",
                    GT_Utility.trans("158", "Time: ") + GT_Utility.formatNumbers(recipe.mDuration / 20d) + " secs"));
        }
    }
}
