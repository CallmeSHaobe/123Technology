package com.newmaa.othtech.common.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.world.WorldEvent;

import com.newmaa.othtech.OTHTechnology;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

/** Fixed launch and landing windows. No chunks are forced along the route. */
public final class DroneChunkLoader implements ForgeChunkManager.LoadingCallback {

    private static final int DEFAULT_DEPTH = 25;
    static final double LAUNCH_RADIUS = 32D;
    static final double LANDING_RADIUS = 24D;
    private static final double DETECTION_PADDING = 2D;
    private static final String ROLE = "DroneChunkRole";
    private static final String ROUTE = "route";
    private static final String ROUTE_EXTRA = "route_extra";
    private static final String TARGET = "target";
    private static final String OWNER_MOST = "DroneOwnerMost";
    private static final String OWNER_LEAST = "DroneOwnerLeast";
    private static final DroneChunkLoader INSTANCE = new DroneChunkLoader();
    private static final Map<EntitySuicideDrone, Tickets> ACTIVE = new IdentityHashMap<>();
    private static Object modHandle;
    private static boolean eventsRegistered;

    private DroneChunkLoader() {}

    public static void register(Object mod) {
        if (mod == null) throw new IllegalArgumentException("A mod instance is required");
        modHandle = mod;
        ForgeChunkManager.setForcedChunkLoadingCallback(mod, INSTANCE);
        if (!eventsRegistered) {
            MinecraftForge.EVENT_BUS.register(INSTANCE);
            eventsRegistered = true;
        }
    }

    /** Returns false without retaining tickets if the fixed endpoint windows cannot be held. */
    public static boolean update(EntitySuicideDrone drone, double launchX, double launchZ, int targetX, int targetZ,
        float explosionPower) {
        releaseDead();
        if (drone == null) return false;
        if (drone.isDead || drone.worldObj == null || drone.worldObj.isRemote || modHandle == null) {
            release(drone);
            return false;
        }
        Tickets pair = ACTIVE.get(drone);
        if (pair != null && pair.world != drone.worldObj) {
            release(drone);
            pair = null;
        }
        if (pair == null) {
            pair = new Tickets(drone.worldObj);
            ACTIVE.put(drone, pair);
        }
        if (pair.ready) return true;
        try {
            int depth = ticketDepth(ForgeChunkManager.getMaxChunkDepthFor(OTHTechnology.MODID));
            long budget = (long) ForgeChunkManager.getMaxTicketLengthFor(OTHTechnology.MODID) * depth;
            Set<ChunkCoordIntPair> launch = window(launchX, launchZ, LAUNCH_RADIUS + DETECTION_PADDING, budget);
            Set<ChunkCoordIntPair> target = window(
                targetX + 0.5D,
                targetZ + 0.5D,
                LANDING_RADIUS + DETECTION_PADDING + explosionPower * 2D + 2D,
                budget);
            if (launch == null || target == null) {
                release(drone);
                return false;
            }
            int routeCount = 1 + (launch.size() - 1) / depth;
            int targetCount = 1 + (target.size() - 1) / depth;
            trimTickets(pair.route, routeCount);
            trimTickets(pair.target, targetCount);
            long extraTickets = (long) routeCount - pair.route.size() + targetCount - pair.target.size();
            if (extraTickets > ForgeChunkManager.ticketCountAvailableFor(modHandle, drone.worldObj)
                || !growTickets(drone, pair.route, routeCount, ROUTE_EXTRA, true)
                || !growTickets(drone, pair.target, targetCount, TARGET, false)) {
                release(drone);
                return false;
            }
            applyWindow(pair.route, launch, depth);
            applyWindow(pair.target, target, depth);
            pair.ready = true;
            return true;
        } catch (RuntimeException e) {
            release(drone);
            return false;
        }
    }

    static boolean inWindow(double x, double z, double centerX, double centerZ, double radius) {
        double dx = x - centerX, dz = z - centerZ;
        return dx * dx + dz * dz <= radius * radius;
    }

    public static void release(EntitySuicideDrone drone) {
        Tickets pair = ACTIVE.remove(drone);
        if (pair != null) release(pair);
    }

    private static Ticket request(EntitySuicideDrone drone, String role, Type type) {
        Ticket ticket = ForgeChunkManager.requestTicket(modHandle, drone.worldObj, type);
        if (ticket == null) return null;
        if (type == Type.ENTITY) ticket.bindEntity(drone);
        ticket.getModData()
            .setString(ROLE, role);
        UUID owner = drone.getUniqueID();
        ticket.getModData()
            .setLong(OWNER_MOST, owner.getMostSignificantBits());
        ticket.getModData()
            .setLong(OWNER_LEAST, owner.getLeastSignificantBits());
        configureDepth(ticket);
        return ticket;
    }

    private static int ticketDepth(int forgeMaximum) {
        return forgeMaximum > 0 ? Math.min(DEFAULT_DEPTH, forgeMaximum) : DEFAULT_DEPTH;
    }

    private static void configureDepth(Ticket ticket) {
        int maximum = ticket.getMaxChunkListDepth();
        if (maximum > 0) ticket.setChunkListDepth(ticketDepth(maximum));
    }

    private static void trimTickets(List<Ticket> tickets, int count) {
        while (tickets.size() > count) {
            ForgeChunkManager.releaseTicket(tickets.remove(tickets.size() - 1));
        }
    }

    private static boolean growTickets(EntitySuicideDrone drone, List<Ticket> tickets, int count, String extraRole,
        boolean entityFirst) {
        while (tickets.size() < count) {
            boolean entityTicket = entityFirst && tickets.isEmpty();
            Ticket ticket = request(drone, entityTicket ? ROUTE : extraRole, entityTicket ? Type.ENTITY : Type.NORMAL);
            if (ticket == null) return false;
            tickets.add(ticket);
        }
        return true;
    }

    private static void applyWindow(List<Ticket> tickets, Set<ChunkCoordIntPair> window, int depth) {
        // Preserve assignments on restored tickets where possible.
        Set<ChunkCoordIntPair> unassigned = new LinkedHashSet<>(window);
        List<Set<ChunkCoordIntPair>> assignments = new ArrayList<>(tickets.size());
        for (Ticket ticket : tickets) {
            Set<ChunkCoordIntPair> assigned = new LinkedHashSet<>();
            for (ChunkCoordIntPair chunk : ticket.getChunkList()) {
                if (assigned.size() == depth) break;
                if (unassigned.remove(chunk)) assigned.add(chunk);
            }
            assignments.add(assigned);
        }
        Iterator<ChunkCoordIntPair> remaining = unassigned.iterator();
        for (int index = 0; index < tickets.size(); index++) {
            Set<ChunkCoordIntPair> assigned = assignments.get(index);
            while (assigned.size() < depth && remaining.hasNext()) assigned.add(remaining.next());
            apply(tickets.get(index), assigned);
        }
    }

    private static Set<ChunkCoordIntPair> window(double x, double z, double margin, long maxWindowChunks) {
        if (!finite(x) || !finite(z) || !finite(margin) || margin < 0D) return null;
        double minX = Math.floor((x - margin) / 16D);
        double maxX = Math.floor((x + margin) / 16D);
        double minZ = Math.floor((z - margin) / 16D);
        double maxZ = Math.floor((z + margin) / 16D);
        if (minX < Integer.MIN_VALUE || maxX >= Integer.MAX_VALUE
            || minZ < Integer.MIN_VALUE
            || maxZ >= Integer.MAX_VALUE
            || (maxX - minX + 1D) * (maxZ - minZ + 1D) > Math.min(Integer.MAX_VALUE, maxWindowChunks)) return null;
        Set<ChunkCoordIntPair> chunks = new LinkedHashSet<>();
        for (int cx = (int) minX; cx <= (int) maxX; cx++) {
            for (int cz = (int) minZ; cz <= (int) maxZ; cz++) {
                chunks.add(new ChunkCoordIntPair(cx, cz));
            }
        }
        return chunks;
    }

    private static void apply(Ticket ticket, Set<ChunkCoordIntPair> desired) {
        // Remove stale chunks first so Forge's depth eviction cannot evict a still-desired chunk.
        for (ChunkCoordIntPair chunk : ticket.getChunkList()) {
            if (!desired.contains(chunk)) ForgeChunkManager.unforceChunk(ticket, chunk);
        }
        Set<ChunkCoordIntPair> existing = ticket.getChunkList();
        for (ChunkCoordIntPair chunk : desired) {
            if (!existing.contains(chunk)) ForgeChunkManager.forceChunk(ticket, chunk);
            // forceChunk only registers a ticket; it does not synchronously load terrain.
            if (!ticket.world.getChunkProvider()
                .chunkExists(chunk.chunkXPos, chunk.chunkZPos)) {
                ticket.world.getChunkFromChunkCoords(chunk.chunkXPos, chunk.chunkZPos);
            }
        }
    }

    @Override
    public void ticketsLoaded(List<Ticket> tickets, World world) {
        Map<UUID, EntitySuicideDrone> owners = new HashMap<>();
        // Only one ENTITY ticket per drone: Forge's pending-entity map is keyed by entity UUID.
        for (Ticket ticket : tickets) {
            if (!ROUTE.equals(
                ticket.getModData()
                    .getString(ROLE)))
                continue;
            Entity entity = ticket.getEntity();
            if (ticket.getType() != Type.ENTITY || ticket.world != world
                || !(entity instanceof EntitySuicideDrone)
                || entity.isDead
                || entity.worldObj != world) {
                ForgeChunkManager.releaseTicket(ticket);
                continue;
            }
            EntitySuicideDrone drone = (EntitySuicideDrone) entity;
            EntitySuicideDrone previous = owners.get(drone.getUniqueID());
            Tickets pair = ACTIVE.get(drone);
            if ((previous != null && previous != drone)
                || (pair != null && !pair.route.isEmpty() && pair.route.get(0) != ticket)) {
                ForgeChunkManager.releaseTicket(ticket);
                continue;
            }
            if (pair == null) {
                pair = new Tickets(drone.worldObj);
                ACTIVE.put(drone, pair);
            }
            configureDepth(ticket);
            if (pair.route.isEmpty()) pair.route.add(ticket);
            owners.put(drone.getUniqueID(), drone);
        }
        for (Ticket ticket : tickets) {
            String role = ticket.getModData()
                .getString(ROLE);
            if (ROUTE.equals(role)) continue;
            if ((!TARGET.equals(role) && !ROUTE_EXTRA.equals(role)) || ticket.getType() != Type.NORMAL
                || ticket.world != world
                || !ticket.getModData()
                    .hasKey(OWNER_MOST)
                || !ticket.getModData()
                    .hasKey(OWNER_LEAST)) {
                ForgeChunkManager.releaseTicket(ticket);
                continue;
            }
            UUID owner = new UUID(
                ticket.getModData()
                    .getLong(OWNER_MOST),
                ticket.getModData()
                    .getLong(OWNER_LEAST));
            EntitySuicideDrone drone = owners.get(owner);
            Tickets pair = drone == null ? null : ACTIVE.get(drone);
            if (pair == null) {
                ForgeChunkManager.releaseTicket(ticket);
                continue;
            }
            configureDepth(ticket);
            (TARGET.equals(role) ? pair.target : pair.route).add(ticket);
        }
        // Forge already loaded the entity chunk. The next entity update restores both windows;
        // never request replacement tickets or synchronously load more chunks inside this callback.
    }

    @SubscribeEvent
    public void onEntityCanUpdate(EntityEvent.CanUpdate event) {
        if (!(event.entity instanceof EntitySuicideDrone) || event.entity.worldObj.isRemote) return;
        // A restored endpoint needs one tick to rebuild its tickets even without a nearby player.
        Entity entity = event.entity;
        if (entity.addedToChunk && entity.worldObj.getChunkProvider()
            .chunkExists(entity.chunkCoordX, entity.chunkCoordZ)) {
            event.canUpdate = true;
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        Iterator<Map.Entry<EntitySuicideDrone, Tickets>> iterator = ACTIVE.entrySet()
            .iterator();
        while (iterator.hasNext()) {
            Map.Entry<EntitySuicideDrone, Tickets> entry = iterator.next();
            Tickets pair = entry.getValue();
            if (entry.getKey().worldObj == event.world || pair.world == event.world) {
                // Forge owns persistence/unloading. Drop references, not the saved tickets.
                iterator.remove();
            }
        }
    }

    private static void releaseDead() {
        Iterator<Map.Entry<EntitySuicideDrone, Tickets>> iterator = ACTIVE.entrySet()
            .iterator();
        while (iterator.hasNext()) {
            Map.Entry<EntitySuicideDrone, Tickets> entry = iterator.next();
            if (entry.getKey().isDead) {
                Tickets pair = entry.getValue();
                iterator.remove();
                release(pair);
            }
        }
    }

    private static void release(Tickets pair) {
        trimTickets(pair.route, 0);
        trimTickets(pair.target, 0);
    }

    private static boolean finite(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    private static final class Tickets {

        private final World world;
        private boolean ready;
        private final List<Ticket> route = new ArrayList<>();
        private final List<Ticket> target = new ArrayList<>();

        private Tickets(World world) {
            this.world = world;
        }
    }
}
