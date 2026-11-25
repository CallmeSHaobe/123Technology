package com.newmaa.othtech.common.entity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.ReflectionHelper;

/** Optional native click adapter. The server caller must verify the precise allowlisted item before calling. */
final class AyanamiItemClick {

    // One reserved identity per process to avoid filling the vanilla statistics cache with one entry per click.
    // Brackets are invalid in normal player names; this identity is never the sender of the dialogue.
    private static final GameProfile CLICK_PROFILE = new GameProfile(UUID.randomUUID(), "[OTH-Rei-Click]");
    private static final int SELECTED_SLOT = 0;
    private static final int MAX_HOLD_TICKS = 20;
    private static final String DENIED = "item_hook_denied";
    private static final String FAILED = "item_hook_failed";
    private static final String REQUIRES_HOLD = "item_requires_hold";

    private AyanamiItemClick() {}

    static final class Result {

        final boolean success;
        final String reason;
        final ItemStack replacement;
        final Hold hold;

        private Result(String reason, ItemStack replacement, Hold hold) {
            this.success = reason == null;
            this.reason = reason;
            this.replacement = replacement;
            this.hold = hold;
        }
    }

    static Result rightClick(EntityAyanami npc, ItemStack unit) {
        return rightClick(npc, unit, false);
    }

    static Result rightClick(EntityAyanami npc, ItemStack unit, boolean hold) {
        if (npc == null || !(npc.worldObj instanceof WorldServer world)
            || world.isRemote
            || !npc.isEntityAlive()
            || unit == null
            || unit.getItem() == null
            || unit.stackSize != 1) return result(DENIED, unit);

        ClickPlayer actor = null;
        boolean staged = false;
        boolean retained = false;
        try {
            actor = new ClickPlayer(world);
            actor.theItemInWorldManager.setGameType(GameType.SURVIVAL);
            actor.follow(npc);
            actor.inventory.currentItem = SELECTED_SLOT;
            // Neither this inventory nor the mutable selected unit is backed by a real player or Rei.
            actor.inventory.mainInventory[SELECTED_SLOT] = unit;
            staged = true;
            ItemStack initial = unit.copy();
            PlayerInteractEvent event = ForgeEventFactory
                .onPlayerInteract(actor, PlayerInteractEvent.Action.RIGHT_CLICK_AIR, 0, 0, 0, -1, world);
            ItemStack selected = actor.selected();
            if (event.isCanceled() || event.useItem == Event.Result.DENY) return result(DENIED, selected);
            if (selected != unit || unit.stackSize != 1 || unit.getItem() != initial.getItem())
                return result(FAILED, selected);
            actor.inventory.currentItem = SELECTED_SLOT;

            // Actual ItemStack air-click dispatcher. ItemInWorldManager.tryUseItem additionally requires a connection.
            ItemStack returned = unit.useItemRightClick(world, actor);
            selected = actor.selected();
            if (returned == unit && selected != unit && ItemStack.areItemStacksEqual(unit, initial)) {
                // Some hooks alter the slot directly while returning the unchanged input.
                returned = selected;
            }
            if (!actor.isUsingItem()) return result(actor.useStartDenied ? DENIED : null, returned);
            if (!hold) return result(REQUIRES_HOLD, returned);
            if (returned != unit || selected != unit
                || actor.getItemInUse() != unit
                || unit.stackSize != 1
                || unit.getItem() != initial.getItem()
                || actor.getItemInUseCount() <= 0
                || UseCount.FIELD == null) {
                return result(FAILED, returned);
            }
            Hold pending = new Hold(npc, actor, unit, world.getTotalWorldTime());
            retained = true;
            return new Result(null, null, pending);
        } catch (RuntimeException | LinkageError ignored) {
            // A trusted addon may assume a networked player. Preserve partial slot mutations without duplication.
            return result(FAILED, staged ? actor.selected() : unit);
        } finally {
            if (actor != null && !retained) actor.discard();
        }
    }

    /** The owner drives this from Rei's ticks, never by ticking or spawning the FakePlayer. */
    static final class Hold {

        private final EntityAyanami owner;
        private final ItemStack unit;
        private final Item item;
        private final long started;
        private long lastTick;
        private int ticks;
        private ClickPlayer actor;
        private Result terminal;

        private Hold(EntityAyanami owner, ClickPlayer actor, ItemStack unit, long started) {
            this.owner = owner;
            this.actor = actor;
            this.unit = unit;
            this.item = unit.getItem();
            this.started = started;
            this.lastTick = started;
        }

        Result tick(EntityAyanami npc) {
            if (terminal != null) return terminal;
            if (npc != owner || !npc.isEntityAlive()
                || npc.worldObj != actor.worldObj
                || npc.dimension != actor.dimension
                || npc.worldObj.isRemote
                || npc.worldObj.getEntityByID(npc.getEntityId()) != npc) return cancel();
            long now = actor.worldObj.getTotalWorldTime();
            if (now < lastTick || now - started > MAX_HOLD_TICKS) return cancel();
            if (now == lastTick) return null;
            lastTick = now;
            ++ticks;
            try {
                if (!matchesUse()) return end(FAILED, actor.selected());
                actor.follow(npc);
                // Match the Forge hook order, but cancellation isn't permission to force a finish.
                int remaining = ForgeEventFactory.onItemUseTick(actor, unit, actor.getItemInUseCount());
                if (remaining < 0) return cancel();
                if (!matchesUse()) return end(FAILED, actor.selected());
                if (remaining > 0) {
                    UseCount.FIELD.setInt(actor, remaining);
                    item.onUsingTick(unit, actor, remaining);
                    if (!matchesUse()) return end(FAILED, actor.selected());
                    remaining = Math.min(remaining, actor.getItemInUseCount()) - 1;
                }
                UseCount.FIELD.setInt(actor, Math.max(0, remaining));
                if (remaining <= 0) return finishUse();
                if (ticks < MAX_HOLD_TICKS && now - started < MAX_HOLD_TICKS) return null;
                if (ForgeEventFactory.onUseItemStop(actor, unit, remaining)) return cancel();
                if (!matchesUse()) return end(FAILED, actor.selected());
                unit.onPlayerStoppedUsing(actor.worldObj, actor, remaining);
                return end(null, actor.selected());
            } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
                return end(FAILED, actor.selected());
            }
        }

        Result cancel() {
            return terminal == null ? end(DENIED, actor.selected()) : terminal;
        }

        private boolean matchesUse() {
            return actor.inventory.currentItem == SELECTED_SLOT && actor.selected() == unit
                && actor.getItemInUse() == unit
                && unit.getItem() == item
                && unit.stackSize == 1;
        }

        private Result finishUse() {
            ItemStack returned = unit.onFoodEaten(actor.worldObj, actor);
            returned = ForgeEventFactory.onItemUseFinish(actor, unit, actor.getItemInUseCount(), returned);
            return end(null, returned);
        }

        private Result end(String reason, ItemStack selected) {
            terminal = result(reason, selected);
            actor.discard();
            actor = null;
            return terminal;
        }
    }

    private static Result result(String reason, ItemStack selected) {
        if (selected == null || selected.getItem() == null || selected.stackSize <= 0)
            return new Result(reason, null, null);
        ItemStack returned = selected.copy();
        returned.stackSize = 1; // No bonus inventory entries or oversized outputs from hooks.
        return new Result(reason, returned, null);
    }

    /** The only private field needed to mirror vanilla's countdown without calling EntityPlayer.onUpdate. */
    private static final class UseCount {

        private static final Field FIELD = find();

        private static Field find() {
            try {
                return ReflectionHelper.findField(EntityPlayer.class, "itemInUseCount", "field_71072_f");
            } catch (RuntimeException | LinkageError ignored) {
                return null;
            }
        }
    }

    private static final class ClickPlayer extends FakePlayer {

        private boolean useStartDenied;

        ClickPlayer(WorldServer world) {
            super(world, CLICK_PROFILE);
        }

        private void follow(EntityAyanami npc) {
            setLocationAndAngles(npc.posX, npc.posY, npc.posZ, npc.rotationYawHead, npc.rotationPitch);
            prevRotationYaw = rotationYaw;
            prevRotationPitch = rotationPitch;
            prevRotationYawHead = rotationYawHead = npc.rotationYawHead;
            prevPosX = lastTickPosX = posX;
            prevPosY = lastTickPosY = posY;
            prevPosZ = lastTickPosZ = posZ;
            dimension = npc.dimension;
            onGround = npc.onGround;
        }

        private ItemStack selected() {
            return inventory.mainInventory[SELECTED_SLOT];
        }

        private void discard() {
            clearItemInUse(); // Cancellation never dispatches an item stop/release hook.
            Arrays.fill(inventory.mainInventory, null);
            Arrays.fill(inventory.armorInventory, null);
            inventory.setItemStack(null);
        }

        @Override
        public void setItemInUse(ItemStack stack, int duration) {
            ItemStack previous = getItemInUse();
            super.setItemInUse(stack, duration);
            if (stack != null && previous != stack) useStartDenied = getItemInUse() != stack;
        }

        @Override
        public void sendPlayerAbilities() {}

        @Override
        public void sendContainerToPlayer(Container container) {}

        @Override
        public boolean canPlayerEdit(int x, int y, int z, int face, ItemStack held) {
            return false;
        }

        @Override
        public boolean canAttackPlayer(EntityPlayer player) {
            return false;
        }

        @Override
        public ChunkCoordinates getPlayerCoordinates() {
            return new ChunkCoordinates(
                MathHelper.floor_double(posX),
                MathHelper.floor_double(posY + 0.5D),
                MathHelper.floor_double(posZ));
        }

        @Override
        public boolean interactWith(Entity target) {
            return false;
        }

        @Override
        public void attackTargetEntityWithCurrentItem(Entity target) {}
    }
}
