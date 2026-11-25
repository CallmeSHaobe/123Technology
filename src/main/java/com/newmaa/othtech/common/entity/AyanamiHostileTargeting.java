package com.newmaa.othtech.common.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.world.ExplosionEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/**
 * Fallback hostile targeting for Forge 1.7.10. Zombies, skeletons and witches use their existing attack goals;
 * blazes, spiders, silverfish and giants use legacy creature targeting. Ghasts retain their own fireball AI.
 * Creepers and other mobs with a standard swelling goal are excluded. The vanilla Wither already targets non-undead
 * living mobs; slimes and dragons have custom player combat, which is not modified. Modded creatures opt in by already
 * implementing the standard hostile player acquisition and combat goals. Unknown private/custom AI is left alone.
 */
public final class AyanamiHostileTargeting {

    private static final double MAX_RANGE = 16.0D;
    private static final double VERTICAL_RANGE = 6.0D;
    private static final int SCAN_INTERVAL = 20;
    private static final int GHAST_SCAN_INTERVAL = 10;
    private static final int GHAST_TARGET_COOLDOWN = 20;
    private static final Field PLAYER_TARGET_CLASS = field(
        EntityAINearestAttackableTarget.class,
        "targetClass",
        "field_75307_b");
    private static final Field PLAYER_TARGET_SELECTOR = field(
        EntityAINearestAttackableTarget.class,
        "targetEntitySelector",
        "field_82643_g");
    private static final Field MELEE_TARGET_CLASS = field(
        EntityAIAttackOnCollide.class,
        "classTarget",
        "field_75444_h");
    private static final Field MELEE_SPEED = field(
        EntityAIAttackOnCollide.class,
        "speedTowardsTarget",
        "field_75440_e");
    private static final Field MELEE_MEMORY = field(EntityAIAttackOnCollide.class, "longMemory", "field_75437_f");
    private static final Field PIG_ANGER = field(EntityPigZombie.class, "angerLevel", "field_70837_d");
    private static final Field GHAST_TARGET = field(EntityGhast.class, "targetedEntity", "field_70792_g");
    private static final Field GHAST_COOLDOWN = field(EntityGhast.class, "aggroCooldown", "field_70798_h");
    private static final Field SWELL_TARGET = field(EntityAICreeperSwell.class, "creeperAttackTarget", "field_75268_b");
    private static final Field SWELL_CREEPER = field(EntityAICreeperSwell.class, "swellingCreeper", "field_75269_a");
    private static final Method AI_ENABLED = aiEnabledMethod();

    // Server-thread only. Values cannot reference the mob (directly or through an AI goal).
    // Keep only the leased NPC ID, not its world or entity, so unloading does not pin either one.
    private final Map<EntityLiving, Integer> leases = new WeakHashMap<>();
    private final Set<EntityLiving> clearingSelfDetonators = Collections.newSetFromMap(new IdentityHashMap<>());

    private static boolean isSelfDetonator(EntityLiving mob) {
        if (mob instanceof EntityCreeper) return true;
        for (EntityAITaskEntry entry : mob.tasks.taskEntries) {
            if (entry.action instanceof EntityAICreeperSwell) return true;
        }
        return false;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingSetAttackTarget(LivingSetAttackTargetEvent event) {
        // Forge posts this notification after assigning the target; it cannot be canceled.
        if (event.target instanceof EntityAyanami && event.entityLiving instanceof EntityLiving mob
            && !mob.worldObj.isRemote
            && isSelfDetonator(mob)) clearSelfDetonatorTarget(mob);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onExplosionDetonate(ExplosionEvent.Detonate event) {
        // Raw source, not getExplosivePlacedBy(): the latter can be a TNT primer or projectile shooter.
        if (event.world.isRemote || !(event.explosion.exploder instanceof EntityLiving source)) return;
        if (isSelfDetonator(source) || (source.isDead && source instanceof IMob
            && !(source instanceof EntityWither)
            && source.getDistanceSq(event.explosion.explosionX, event.explosion.explosionY, event.explosion.explosionZ)
                <= 4.0D)) {
            // Detonate supplies the original victim list: remove only Rei, leaving blocks and all other victims alone.
            event.getAffectedEntities()
                .removeIf(entity -> entity instanceof EntityAyanami);
        }
    }

    private void clearSelfDetonatorTarget(EntityLiving mob) {
        if (!clearingSelfDetonators.add(mob)) return;
        try {
            boolean aimedAtRei = false;
            if (mob.getAttackTarget() instanceof EntityAyanami) {
                mob.setAttackTarget(null);
                aimedAtRei = true;
            }
            if (mob.getAITarget() instanceof EntityAyanami) {
                mob.setRevengeTarget(null);
                aimedAtRei = true;
            }
            if (mob instanceof EntityCreature creature && creature.getEntityToAttack() instanceof EntityAyanami) {
                creature.setTarget(null);
                aimedAtRei = true;
            }
            for (EntityAITaskEntry entry : mob.tasks.taskEntries) {
                if (!(entry.action instanceof EntityAICreeperSwell swell)) continue;
                if (value(SWELL_CREEPER, swell) == mob && value(SWELL_TARGET, swell) instanceof EntityAyanami) {
                    swell.resetTask();
                    aimedAtRei = true;
                }
            }
            if (!aimedAtRei) return;
            leases.remove(mob);
            if (!alive(mob.getAttackTarget()) && !alive(mob.getAITarget())
                && !(mob instanceof EntityCreature creature && alive(creature.getEntityToAttack()))) {
                mob.getNavigator()
                    .clearPathEntity();
                // Preserve manual flint-and-steel ignition even if the ignited creeper tracked Rei.
                if (mob instanceof EntityCreeper creeper && !creeper.func_146078_ca()) creeper.setCreeperState(-1);
            }
        } finally {
            clearingSelfDetonators.remove(mob);
        }
    }

    /**
     * Hostile now according to the vanilla variant or a standard, unconditional Forge player targeting task.
     * Includes confirmed natural attackers with combat implementations not adapted here (e.g. large slimes).
     * Merely implementing IMob, retaliation/pet defense, and neutral modded AI are not sufficient evidence.
     */
    public static boolean isNaturalAttacker(EntityLiving mob) {
        if (mob == null || !mob.isEntityAlive()
            || mob.worldObj == null
            || mob.worldObj.difficultySetting == EnumDifficulty.PEACEFUL
            || isSelfDetonator(mob)) return false;
        Class<?> kind = mob.getClass();
        if (mob instanceof EntityPigZombie) return positiveInt(PIG_ANGER, mob);
        if (mob instanceof EntityEnderman enderman) {
            return enderman.isScreaming() && alive(enderman.getEntityToAttack());
        }
        if (mob instanceof EntitySpider) return mob.getBrightness(1.0F) < 0.5F
            && (kind == EntitySpider.class || kind == EntityCaveSpider.class || hasStandardPlayerGoal(mob));
        if (kind == EntitySlime.class) return ((EntitySlime) mob).getSlimeSize() > 1;
        return kind == EntityMagmaCube.class || kind == EntityWither.class
            || kind == EntityDragon.class
            || kind == EntityGhast.class
            || kind == EntityZombie.class
            || kind == EntitySkeleton.class
            || kind == EntityWitch.class
            || kind == EntityBlaze.class
            || kind == EntitySilverfish.class
            || kind == EntityGiantZombie.class
            || hasStandardPlayerGoal(mob);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent event) {
        if (!(event.entityLiving instanceof EntityLiving mob) || mob.worldObj.isRemote) return;
        if (mob instanceof EntityCreeper) {
            clearSelfDetonatorTarget(mob);
            return;
        }
        Class<?> kind = mob.getClass();
        int interval = kind == EntityGhast.class ? GHAST_SCAN_INTERVAL : SCAN_INTERVAL;
        if (Math.floorMod(mob.ticksExisted + mob.getEntityId(), interval) != 0) return;
        if (isSelfDetonator(mob)) {
            clearSelfDetonatorTarget(mob);
            return;
        }
        // Provoked mobs keep their original relationship: their anger does not transfer to Rei.
        if (mob instanceof EntityPigZombie || mob instanceof EntityEnderman) return;
        Mode mode = mode(mob, kind);
        if (mode == null) return;

        Entity current = getTarget(mob, mode);
        Integer lease = leases.get(mob);
        if (lease != null && (!(current instanceof EntityAyanami) || current.getEntityId() != lease)) {
            leases.remove(mob);
            lease = null;
        }
        if (alive(current) && lease == null) return;

        boolean otherSlotBusy = otherSlotHasTarget(mob, mode);
        double range = range(mob, kind);
        if (lease != null && current instanceof EntityAyanami npc) {
            if (!otherSlotBusy && isNaturalAttacker(mob)
                && eligible(mob, npc, range)
                && (mode == Mode.TASKS || !hasVanillaPlayerTarget(mob, kind))) return;
            leases.remove(mob);
            setTarget(mob, mode, null);
            return; // let vanilla perform its own target acquisition first
        }
        if (otherSlotBusy || !isNaturalAttacker(mob) || (mode != Mode.TASKS && hasVanillaPlayerTarget(mob, kind)))
            return;
        if (mode == Mode.TASKS && !(mob instanceof EntityCreature)) return;
        EntityAyanami candidate = nearest(mob, range);
        if (candidate == null) return;
        if (mode == Mode.TASKS && !supportsAyanamiAttack((EntityCreature) mob)) return;
        // Defend against changes triggered by mod hooks while testing class, home radius or line of sight.
        if (alive(getTarget(mob, mode)) || otherSlotHasTarget(mob, mode)) return;
        if (setTarget(mob, mode, candidate)) leases.put(mob, candidate.getEntityId());
    }

    private enum Mode {
        TASKS,
        LEGACY,
        GHAST
    }

    private static Mode mode(EntityLiving mob, Class<?> kind) {
        if (kind == EntityWither.class) return null; // Its vanilla selectors already include EntityAyanami.
        if (kind == EntityGhast.class) return GHAST_TARGET != null && GHAST_COOLDOWN != null ? Mode.GHAST : null;
        if (kind == EntitySpider.class || kind == EntityCaveSpider.class
            || kind == EntityBlaze.class
            || kind == EntitySilverfish.class
            || kind == EntityGiantZombie.class) return Mode.LEGACY;
        if (kind == EntityZombie.class || kind == EntitySkeleton.class
            || kind == EntityWitch.class
            || hasStandardPlayerGoal(mob)) return Mode.TASKS;
        return null;
    }

    private static double range(EntityLiving mob, Class<?> kind) {
        if (kind == EntitySilverfish.class) return 8.0D;
        if (kind == EntityGhast.class) return MAX_RANGE;
        IAttributeInstance follow = mob.getEntityAttribute(SharedMonsterAttributes.followRange);
        return follow == null ? MAX_RANGE : Math.min(MAX_RANGE, follow.getAttributeValue());
    }

    private static boolean eligible(EntityLiving mob, EntityAyanami npc, double range) {
        if (npc.worldObj != mob.worldObj || !npc.isEntityAlive()
            || npc.isInvisible()
            || !mob.canAttackClass(npc.getClass())
            || mob.isOnSameTeam(npc)
            || Math.abs(mob.posY - npc.posY) > VERTICAL_RANGE
            || mob.getDistanceSqToEntity(npc) > range * range) return false;
        if (mob instanceof EntityCreature creature && !creature.isWithinHomeDistance(
            MathHelper.floor_double(npc.posX),
            MathHelper.floor_double(npc.posY),
            MathHelper.floor_double(npc.posZ))) return false;
        return mob.canEntityBeSeen(npc);
    }

    private static EntityAyanami nearest(EntityLiving mob, double range) {
        if (!(range > 0)) return null;
        EntityAyanami nearest = null;
        double best = range * range;
        for (EntityAyanami npc : mob.worldObj
            .getEntitiesWithinAABB(EntityAyanami.class, mob.boundingBox.expand(range, VERTICAL_RANGE, range))) {
            double distance = mob.getDistanceSqToEntity(npc);
            if (distance > best || (distance == best && nearest != null && npc.getEntityId() >= nearest.getEntityId())
                || !eligible(mob, npc, range)) continue;
            best = distance;
            nearest = npc;
        }
        return nearest;
    }

    private static boolean hasVanillaPlayerTarget(EntityLiving mob, Class<?> kind) {
        double radius = kind == EntityGhast.class ? 100.0D : kind == EntitySilverfish.class ? 8.0D : 16.0D;
        EntityPlayer player = mob.worldObj.getClosestVulnerablePlayerToEntity(mob, radius);
        return alive(player) && (kind == EntityGhast.class || kind == EntitySilverfish.class
            || kind == EntitySpider.class
            || kind == EntityCaveSpider.class
            || mob.canEntityBeSeen(player));
    }

    private static boolean otherSlotHasTarget(EntityLiving mob, Mode mode) {
        if (mode == Mode.TASKS) return mob instanceof EntityCreature creature && alive(creature.getEntityToAttack());
        return alive(mob.getAttackTarget());
    }

    private static Entity getTarget(EntityLiving mob, Mode mode) {
        if (mode == Mode.LEGACY) return ((EntityCreature) mob).getEntityToAttack();
        if (mode == Mode.GHAST) return (Entity) value(GHAST_TARGET, mob);
        return mob.getAttackTarget();
    }

    private static boolean setTarget(EntityLiving mob, Mode mode, EntityAyanami target) {
        switch (mode) {
            case TASKS:
                mob.setAttackTarget(target);
                break;
            case LEGACY:
                ((EntityCreature) mob).setTarget(target);
                break;
            case GHAST:
                try {
                    GHAST_COOLDOWN.setInt(mob, target == null ? 0 : GHAST_TARGET_COOLDOWN);
                    GHAST_TARGET.set(mob, target);
                } catch (IllegalAccessException ignored) {
                    return false;
                }
                break;
            default:
                return false;
        }
        return getTarget(mob, mode) == target;
    }

    private static boolean alive(Entity e) {
        return e != null && e.isEntityAlive();
    }

    private static boolean hasStandardPlayerGoal(EntityLiving mob) {
        if (!(mob instanceof EntityCreature) || !(mob instanceof IMob)
            || AI_ENABLED == null
            || PLAYER_TARGET_CLASS == null
            || PLAYER_TARGET_SELECTOR == null) return false;
        try {
            if (!(Boolean) AI_ENABLED.invoke(mob)) return false;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return false;
        }
        for (EntityAITaskEntry entry : mob.targetTasks.taskEntries) {
            if (entry.action.getClass() != EntityAINearestAttackableTarget.class) continue;
            Object target = value(PLAYER_TARGET_CLASS, entry.action);
            if (target instanceof Class<?>type && EntityPlayer.class.isAssignableFrom(type)
                && hasNoCustomSelector(value(PLAYER_TARGET_SELECTOR, entry.action))) return true;
        }
        return false;
    }

    /** Do not guess that a mod's conditional selector will accept Rei just because its target class is Player. */
    private static boolean hasNoCustomSelector(Object wrapper) {
        if (wrapper == null || wrapper.getClass()
            .getEnclosingClass() != EntityAINearestAttackableTarget.class) return false;
        try {
            for (Field member : wrapper.getClass()
                .getDeclaredFields()) {
                if (member.isSynthetic() && member.getType() == IEntitySelector.class) {
                    member.setAccessible(true);
                    return member.get(wrapper) == null;
                }
            }
        } catch (IllegalAccessException | RuntimeException ignored) {}
        return false;
    }

    private static boolean supportsAyanamiAttack(EntityCreature mob) {
        EntityAITaskEntry playerMelee = null;
        for (EntityAITaskEntry entry : mob.tasks.taskEntries) {
            if (entry.action.getClass() == EntityAIArrowAttack.class || entry.action instanceof AyanamiMeleeGoal)
                return true;
            if (entry.action.getClass() != EntityAIAttackOnCollide.class || MELEE_TARGET_CLASS == null) continue;
            Object configured = value(MELEE_TARGET_CLASS, entry.action);
            if (configured == null
                || (configured instanceof Class<?>type && type.isAssignableFrom(EntityAyanami.class))) {
                return true;
            }
            if (configured instanceof Class<?>type && EntityPlayer.class.isAssignableFrom(type)
                && (playerMelee == null || entry.priority < playerMelee.priority)) playerMelee = entry;
        }
        if (playerMelee == null) return false;
        Object speed = value(MELEE_SPEED, playerMelee.action);
        Object memory = value(MELEE_MEMORY, playerMelee.action);
        if (!(speed instanceof Double) || !(memory instanceof Boolean)) return false;
        var fallback = new AyanamiMeleeGoal(mob, (Double) speed, (Boolean) memory);
        mob.tasks.addTask(playerMelee.priority, fallback);
        return true;
    }

    private static final class AyanamiMeleeGoal extends EntityAIAttackOnCollide {

        private final EntityCreature mob;

        private AyanamiMeleeGoal(EntityCreature mob, double speed, boolean memory) {
            super(mob, EntityAyanami.class, speed, memory);
            this.mob = mob;
        }

        @Override
        public boolean continueExecuting() {
            // Vanilla's continueExecuting does not recheck the target's class when another goal takes over.
            return mob.getAttackTarget() instanceof EntityAyanami && super.continueExecuting();
        }
    }

    private static Field field(Class<?> owner, String... names) {
        for (String name : names) {
            try {
                Field found = owner.getDeclaredField(name);
                found.setAccessible(true);
                return found;
            } catch (ReflectiveOperationException | RuntimeException ignored) {}
        }
        return null;
    }

    private static Method aiEnabledMethod() {
        for (String name : new String[] { "isAIEnabled", "func_70650_aV" }) {
            try {
                Method found = EntityLivingBase.class.getDeclaredMethod(name);
                found.setAccessible(true);
                return found;
            } catch (ReflectiveOperationException | RuntimeException ignored) {}
        }
        return null;
    }

    private static boolean positiveInt(Field field, Object object) {
        Object value = value(field, object);
        return value instanceof Integer && (Integer) value > 0;
    }

    private static Object value(Field field, Object owner) {
        if (field == null) return null;
        try {
            return field.get(owner);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }
}
