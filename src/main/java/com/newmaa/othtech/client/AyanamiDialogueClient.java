package com.newmaa.othtech.client;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.newmaa.othtech.common.dialogue.AyanamiDialogueNetwork;
import com.newmaa.othtech.common.entity.EntityAyanami;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

/** Target-aware key binding and per-NPC world speech, independent of the open screen. */
public final class AyanamiDialogueClient {

    private static final long AMBIENT_DURATION = TimeUnit.MILLISECONDS.toNanos(4500);
    private static final int MAX_BUBBLES = 32;
    private static final ArrayBlockingQueue<Reply> INCOMING = new ArrayBlockingQueue<>(MAX_BUBBLES);
    private static final ArrayBlockingQueue<Reply> INCOMING_AMBIENT = new ArrayBlockingQueue<>(MAX_BUBBLES);
    // Client-thread only. Keep a speaker reference as an entity ID can be recycled.
    private static final Map<Integer, Speech> DIALOGUE = new LinkedHashMap<>();
    private static final Map<Integer, Speech> AMBIENT = new LinkedHashMap<>();
    private static World speechWorld;
    private final KeyBinding talk = new KeyBinding(
        "oth.ayanami.dialogue.key",
        Keyboard.KEY_T,
        "key.categories.multiplayer");

    public static void install() {
        AyanamiDialogueClient handler = new AyanamiDialogueClient();
        ClientRegistry.registerKeyBinding(handler.talk);
        FMLCommonHandler.instance()
            .bus()
            .register(handler);
    }

    @SubscribeEvent
    public void onKeyboard(InputEvent.KeyInputEvent event) {
        if (Keyboard.getEventKeyState() && !Keyboard.isRepeatEvent()
            && talk.getKeyCode() != 0
            && Keyboard.getEventKey() == talk.getKeyCode()) handleInput();
    }

    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState() && Mouse.getEventButton() >= 0
            && Mouse.getEventButton() - 100 == talk.getKeyCode()) handleInput();
    }

    private void handleInput() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen != null || mc.thePlayer == null || mc.theWorld == null) return;
        KeyBinding vanillaChat = mc.gameSettings.keyBindChat;
        boolean sharedWithChat = vanillaChat.getKeyCode() == talk.getKeyCode();
        // 1.7.10 tracks only one binding per key code. Explicitly preserve ordinary chat off-target.
        while (talk.isPressed()) { /* consume this context binding */ }
        if (sharedWithChat) while (vanillaChat.isPressed()) { /* vanilla must not open a second screen */ }
        MovingObjectPosition hit = mc.objectMouseOver;
        if (hit != null && hit.entityHit instanceof EntityAyanami npc
            && npc.isEntityAlive()
            && mc.thePlayer.getDistanceSqToEntity(npc) <= 64) {
            AyanamiDialogueNetwork.requestOpen(npc.getEntityId());
        } else if (sharedWithChat && mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
            mc.displayGuiScreen(new GuiChat());
        }
    }

    public static void enqueueReply(int dimension, int entityId, String text) {
        INCOMING.offer(new Reply(dimension, entityId, text, false));
    }

    public static void enqueueLocalReply(int dimension, int entityId, String key) {
        INCOMING.offer(new Reply(dimension, entityId, key, true));
    }

    public static void enqueueAmbientReply(int dimension, int entityId, String text, boolean localize) {
        INCOMING_AMBIENT.offer(new Reply(dimension, entityId, text, localize));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (speechWorld != mc.theWorld) {
            DIALOGUE.clear();
            AMBIENT.clear();
            if (speechWorld != null) {
                INCOMING.clear();
                INCOMING_AMBIENT.clear();
            }
            speechWorld = mc.theWorld;
        }
        if (mc.theWorld == null || mc.thePlayer == null) {
            INCOMING.clear();
            INCOMING_AMBIENT.clear();
            return;
        }
        long now = System.nanoTime();
        tickSpeech(mc, INCOMING, DIALOGUE, now, false);
        tickSpeech(mc, INCOMING_AMBIENT, AMBIENT, now, true);
    }

    private static void tickSpeech(Minecraft mc, ArrayBlockingQueue<Reply> incoming, Map<Integer, Speech> active,
        long now, boolean ambient) {
        active.values()
            .removeIf(
                line -> !line.speaker.isEntityAlive()
                    || mc.theWorld.getEntityByID(line.speaker.getEntityId()) != line.speaker
                    || now - line.until >= 0);
        for (int i = 0; i < MAX_BUBBLES; i++) {
            Reply reply = incoming.poll();
            if (reply == null) break;
            EntityAyanami npc = resolve(mc, reply);
            if (npc == null) continue;
            String text = displayText(reply);
            if (text.isEmpty()) continue;
            long seconds = 5 + (text.codePointCount(0, text.length()) + 17) / 18;
            long duration = ambient ? AMBIENT_DURATION : TimeUnit.SECONDS.toNanos(Math.min(seconds, 32));
            active.remove(reply.entityId);
            if (active.size() >= MAX_BUBBLES) active.remove(
                active.keySet()
                    .iterator()
                    .next());
            active.put(reply.entityId, new Speech(npc, text, now + duration));
        }
    }

    private static EntityAyanami resolve(Minecraft mc, Reply reply) {
        if (reply == null || mc.thePlayer == null || mc.theWorld == null || mc.thePlayer.dimension != reply.dimension) {
            return null;
        }
        Entity entity = mc.theWorld.getEntityByID(reply.entityId);
        return entity instanceof EntityAyanami npc && npc.isEntityAlive() ? npc : null;
    }

    private static String displayText(Reply reply) {
        return reply.localize ? StatCollector.translateToLocal(reply.text) : reply.text;
    }

    public static String speechFor(EntityAyanami entity) {
        EntityPlayer viewer = Minecraft.getMinecraft().thePlayer;
        double range = AyanamiDialogueNetwork.BUBBLE_RANGE;
        if (!entity.isEntityAlive() || viewer == null
            || viewer.worldObj != entity.worldObj
            || speechWorld != entity.worldObj
            || viewer.getDistanceSqToEntity(entity) > range * range) return "";
        long now = System.nanoTime();
        Speech line = DIALOGUE.get(entity.getEntityId());
        if (visible(line, entity, now)) return line.text;
        line = !entity.isInDialogue() ? AMBIENT.get(entity.getEntityId()) : null;
        return visible(line, entity, now) ? line.text : "";
    }

    private static boolean visible(Speech line, EntityAyanami entity, long now) {
        return line != null && line.speaker == entity && now - line.until < 0;
    }

    private static final class Speech {

        final EntityAyanami speaker;
        final String text;
        final long until;

        Speech(EntityAyanami speaker, String text, long until) {
            this.speaker = speaker;
            this.text = text;
            this.until = until;
        }
    }

    private static final class Reply {

        final int dimension;
        final int entityId;
        final String text;
        final boolean localize;

        Reply(int dimension, int entityId, String text, boolean localize) {
            this.dimension = dimension;
            this.entityId = entityId;
            this.text = text;
            this.localize = localize;
        }
    }
}
