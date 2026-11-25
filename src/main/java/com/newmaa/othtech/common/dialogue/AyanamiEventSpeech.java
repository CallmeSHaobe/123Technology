package com.newmaa.othtech.common.dialogue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;

import com.newmaa.othtech.common.dialogue.AyanamiDialogueNetwork.LocalLine;
import com.newmaa.othtech.common.entity.EntityAyanami;

/** Short-lived server-thread speech requests. Their prompts never receive inventories, chat or player names. */
public final class AyanamiEventSpeech {

    private static final int WAIT_TICKS = 60;
    private static final int MAX_PRIVATE_PENDING = 12;
    private static final int MAX_AMBIENT_LOCALES = 4;
    private static final double PRIVATE_RANGE_SQ = 8 * 8;
    private static final double AMBIENT_RANGE_SQ = AyanamiDialogueNetwork.BUBBLE_RANGE
        * AyanamiDialogueNetwork.BUBBLE_RANGE;

    private final EntityAyanami npc;
    private final Map<EntityPlayerMP, PrivateLine> pending = new LinkedHashMap<>();
    private AmbientBatch ambient;

    public AyanamiEventSpeech(EntityAyanami npc) {
        this.npc = npc;
    }

    public void privateLine(EntityPlayerMP player, LocalLine fallback) {
        if (!canHear(player, PRIVATE_RANGE_SQ)) return;
        // An older, slow reply must not arrive after an immediately subsequent handover/return.
        pending.remove(player);
        if (pending.size() >= MAX_PRIVATE_PENDING || !AyanamiDialogue.mayGenerateEventLine(player)) {
            AyanamiDialogueNetwork.sendLocalLine(player, npc, fallback);
            return;
        }
        PrivateLine line = new PrivateLine(fallback, AyanamiDialogueNetwork.playerLocale(player), now() + WAIT_TICKS);
        pending.put(player, line);
        if (!AyanamiDialogue
            .requestEventLine(line.locale, privateCue(fallback), result -> finishPrivate(player, line, result)))
            finishPrivate(player, line, "");
    }

    public void selfTalk(int variant) {
        if (npc.worldObj.isRemote || variant < 1 || variant > 4) return;
        AmbientBatch batch = new AmbientBatch(variant, now() + WAIT_TICKS);
        ambient = batch;
        Map<String, List<EntityPlayerMP>> byLocale = new LinkedHashMap<>();
        for (EntityPlayer player : npc.worldObj.playerEntities) {
            if (!(player instanceof EntityPlayerMP listener) || !canHear(listener, AMBIENT_RANGE_SQ)) continue;
            if (!AyanamiDialogue.mayGenerateEventLine(listener)) {
                AyanamiDialogueNetwork.sendAmbientFallback(listener, npc, variant);
                continue;
            }
            String locale = AyanamiDialogueNetwork.playerLocale(listener);
            if (!byLocale.containsKey(locale) && byLocale.size() >= MAX_AMBIENT_LOCALES) {
                AyanamiDialogueNetwork.sendAmbientFallback(listener, npc, variant);
            } else {
                byLocale.computeIfAbsent(locale, ignored -> new ArrayList<>())
                    .add(listener);
            }
        }
        for (Map.Entry<String, List<EntityPlayerMP>> entry : byLocale.entrySet()) {
            String locale = entry.getKey();
            batch.audiences.put(locale, entry.getValue());
            if (!AyanamiDialogue
                .requestEventLine(locale, ambientCue(variant), result -> finishAmbient(batch, locale, result)))
                finishAmbient(batch, locale, "");
        }
        if (batch.audiences.isEmpty()) ambient = null;
    }

    public void cancelAmbient() {
        ambient = null;
    }

    public void cancelAll() {
        cancelAmbient();
        pending.clear();
    }

    public void tick(long tick) {
        for (Iterator<Map.Entry<EntityPlayerMP, PrivateLine>> it = pending.entrySet()
            .iterator(); it.hasNext();) {
            Map.Entry<EntityPlayerMP, PrivateLine> entry = it.next();
            EntityPlayerMP player = entry.getKey();
            PrivateLine line = entry.getValue();
            if (!canHear(player, PRIVATE_RANGE_SQ)) {
                it.remove();
            } else if (tick >= line.deadline) {
                it.remove();
                AyanamiDialogueNetwork.sendLocalLine(player, npc, line.fallback);
            }
        }
        AmbientBatch batch = ambient;
        if (batch != null && tick >= batch.deadline) {
            for (String locale : new ArrayList<>(batch.audiences.keySet())) finishAmbient(batch, locale, "");
            ambient = null;
        }
    }

    private void finishPrivate(EntityPlayerMP player, PrivateLine line, String result) {
        if (pending.get(player) != line) return;
        pending.remove(player);
        if (!canHear(player, PRIVATE_RANGE_SQ)) return;
        if (now() >= line.deadline || result == null
            || result.isEmpty()
            || !AyanamiDialogue.mayGenerateEventLine(player)
            || !line.locale.equals(AyanamiDialogueNetwork.playerLocale(player))) {
            AyanamiDialogueNetwork.sendLocalLine(player, npc, line.fallback);
        } else {
            AyanamiDialogueNetwork.sendEventLine(player, npc, result, false);
        }
    }

    private void finishAmbient(AmbientBatch batch, String locale, String result) {
        if (ambient != batch) return;
        List<EntityPlayerMP> listeners = batch.audiences.remove(locale);
        if (listeners == null) return;
        boolean fallback = now() >= batch.deadline || result == null || result.isEmpty();
        for (EntityPlayerMP player : listeners) {
            if (!canHear(player, AMBIENT_RANGE_SQ)) continue;
            if (fallback || !AyanamiDialogue.mayGenerateEventLine(player)
                || !locale.equals(AyanamiDialogueNetwork.playerLocale(player))) {
                AyanamiDialogueNetwork.sendAmbientFallback(player, npc, batch.variant);
            } else {
                AyanamiDialogueNetwork.sendEventLine(player, npc, result, true);
            }
        }
    }

    private boolean canHear(EntityPlayerMP player, double rangeSq) {
        return player != null && !(player instanceof FakePlayer)
            && !npc.worldObj.isRemote
            && npc.isEntityAlive()
            && player.worldObj == npc.worldObj
            && player.isEntityAlive()
            && npc.getDistanceSqToEntity(player) <= rangeSq
            && npc.worldObj.getEntityByID(npc.getEntityId()) == npc
            && npc.worldObj.playerEntities.contains(player);
    }

    private long now() {
        return npc.worldObj.getTotalWorldTime();
    }

    private static String privateCue(LocalLine line) {
        return switch (line) {
            case RECEIVED -> "The listener just gave you one ordinary item. Acknowledge the gift without saying where you put it, or naming or describing the item.";
            case RETURNED -> "You just handed the listener an item back. Acknowledge returning it. Do not name or describe the item.";
            case CLONE_WAKE -> "You have just woken in an unfamiliar place, and a stranger is beside you. You don't know why you are here. Don't explain any backstory.";
            case AWAKENING -> "You just held the Spear of Longinus for the first time you can remember. Your hands recognized the grip before your memory did. Keep that uncertainty.";
            case LONGINUS_AGAIN -> "You have been handed the Spear of Longinus again. The grip already feels familiar. No new memory or revelation.";
        };
    }

    private static String ambientCue(int variant) {
        return switch (variant) {
            case 1 -> "You are walking alone, under the rain. Quietly remark on the sound of rain to yourself.";
            case 2 -> "You are walking alone at night. Quietly remark on the stillness to yourself.";
            case 3 -> "You are walking alone. Quietly wonder to yourself if you are going the right way back.";
            default -> "You are walking alone. Quietly think aloud about saving one question for tomorrow, without saying what it is.";
        };
    }

    private static final class PrivateLine {

        final LocalLine fallback;
        final String locale;
        final long deadline;

        PrivateLine(LocalLine fallback, String locale, long deadline) {
            this.fallback = fallback;
            this.locale = locale;
            this.deadline = deadline;
        }
    }

    private static final class AmbientBatch {

        final int variant;
        final long deadline;
        final Map<String, List<EntityPlayerMP>> audiences = new LinkedHashMap<>();

        AmbientBatch(int variant, long deadline) {
            this.variant = variant;
            this.deadline = deadline;
        }
    }
}
