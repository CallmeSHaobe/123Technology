package com.newmaa.othtech.common.entity;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/** Server-side use of a closed set of vanilla consumables; never invokes item use hooks. */
final class AyanamiItemUse {

    private static final int BREWED_DRINK = 8192;
    private static final int LEVEL_TWO = 32;
    private static final int EXTENDED = 64;
    private static final int POTION_TYPE = 15;
    private static final int ALLOWED_POTION_BITS = BREWED_DRINK | LEVEL_TWO | EXTENDED | POTION_TYPE;

    private AyanamiItemUse() {}

    static boolean canConsume(ItemStack stack, String verb) {
        if (stack == null || stack.stackSize <= 0 || stack.getItem() == null) return false;
        if ("eat".equals(verb)) return food(stack) != null;
        return "use".equals(verb) && (isMilk(stack) || brew(stack) != null);
    }

    /** Called after the caller has revalidated and removed one item. Returns only an optional empty container. */
    static ItemStack consume(EntityAyanami npc, ItemStack unit, String verb) {
        if (npc == null || npc.worldObj == null
            || npc.worldObj.isRemote
            || !npc.isEntityAlive()
            || unit == null
            || unit.stackSize != 1
            || !canConsume(unit, verb)) return null;
        if ("eat".equals(verb)) {
            npc.heal(food(unit).func_150905_g(unit));
            return null;
        }
        if (isMilk(unit)) {
            npc.clearActivePotions();
            return new ItemStack(Items.bucket);
        }
        Brew potion = brew(unit);
        if (potion.effect == Potion.heal) {
            npc.heal(potion.amplifier == 0 ? 4F : 8F);
        } else {
            npc.addPotionEffect(new PotionEffect(potion.effect.id, potion.duration, potion.amplifier));
        }
        return new ItemStack(Items.glass_bottle);
    }

    private static ItemFood food(ItemStack stack) {
        Item item = stack.getItem();
        int damage = stack.getItemDamage();
        if (item == Items.cooked_fished && item.getClass() == ItemFishFood.class && (damage == 0 || damage == 1)) {
            return (ItemFood) item;
        }
        if (damage != 0) return null;
        if (item.getClass() == ItemSeedFood.class && (item == Items.carrot || item == Items.potato)) {
            return (ItemFood) item;
        }
        // Only exact vanilla food classes, excluding golden apples, raw fish, foods with negative effects and stews.
        return item.getClass() == ItemFood.class && (item == Items.apple || item == Items.bread
            || item == Items.porkchop
            || item == Items.cooked_porkchop
            || item == Items.beef
            || item == Items.cooked_beef
            || item == Items.cooked_chicken
            || item == Items.cookie
            || item == Items.melon
            || item == Items.baked_potato
            || item == Items.golden_carrot
            || item == Items.pumpkin_pie) ? (ItemFood) item : null;
    }

    private static boolean isMilk(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.milk_bucket && item.getClass() == ItemBucketMilk.class && stack.getItemDamage() == 0;
    }

    private static Brew brew(ItemStack stack) {
        Item item = stack.getItem();
        if (item != Items.potionitem || item.getClass() != ItemPotion.class
            || (stack.hasTagCompound() && stack.getTagCompound()
                .hasKey("CustomPotionEffects")))
            return null;
        int data = stack.getItemDamage();
        if ((data & ~ALLOWED_POTION_BITS) != 0 || (data & BREWED_DRINK) == 0) return null;
        boolean extended = (data & EXTENDED) != 0;
        boolean stronger = (data & LEVEL_TWO) != 0;
        if (extended && stronger) return null;

        // Canonical 1.7.10 brewing data only. Reading these combinations directly avoids using potion NBT,
        // calling the item, or resolving arbitrary entries from the globally mutable potion registry.
        Potion effect = switch (data & POTION_TYPE) {
            case 1 -> Potion.regeneration;
            case 2 -> Potion.moveSpeed;
            case 3 -> Potion.fireResistance;
            case 5 -> Potion.heal;
            case 6 -> Potion.nightVision;
            case 9 -> Potion.damageBoost;
            case 13 -> Potion.waterBreathing;
            case 14 -> Potion.invisibility;
            default -> null;
        };
        if (effect == null || effect.id < 0
            || effect.id >= Potion.potionTypes.length
            || Potion.potionTypes[effect.id] != effect) return null;
        if (extended && effect == Potion.heal) return null;
        if (stronger && effect != Potion.heal
            && effect != Potion.regeneration
            && effect != Potion.moveSpeed
            && effect != Potion.damageBoost) return null;

        int duration = extended ? 9600 : stronger ? 1800 : 3600;
        if (effect == Potion.regeneration) duration /= 4;
        if (effect == Potion.heal) duration = 1;
        return new Brew(effect, duration, stronger ? 1 : 0);
    }

    private static final class Brew {

        private final Potion effect;
        private final int duration;
        private final int amplifier;

        private Brew(Potion effect, int duration, int amplifier) {
            this.effect = effect;
            this.duration = duration;
            this.amplifier = amplifier;
        }
    }
}
