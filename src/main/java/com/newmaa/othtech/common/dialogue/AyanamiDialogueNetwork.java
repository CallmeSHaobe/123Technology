package com.newmaa.othtech.common.dialogue;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.util.FakePlayer;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.entity.EntityAyanami;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

/** Delivers world-space speech; input text and history are not part of the bubble protocol. */
public final class AyanamiDialogueNetwork {

    public static final double BUBBLE_RANGE = 12D;

    private static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel("oth_ayanami");
    private static final int AMBIENT_TEXT = 0x80;
    private static final int AMBIENT_VARIANTS = 4;
    private static final String AMBIENT_KEY_PREFIX = "oth.ayanami.ambient.";
    private static final String FALLBACK_LOCALE = "en_us";
    private static final Pattern LOCALE_CODE = Pattern.compile("[A-Za-z]{2,3}_[A-Za-z0-9]{2,3}");
    private static final Field PLAYER_TRANSLATOR = findPlayerTranslator();

    private static Field findPlayerTranslator() {
        try {
            // EntityPlayerMP stores the client's vanilla C15PacketClientSettings locale here.
            return ReflectionHelper.findField(EntityPlayerMP.class, "translator", "field_71148_cg");
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    public static String playerLocale(EntityPlayerMP player) {
        if (player != null && PLAYER_TRANSLATOR != null) {
            try {
                Object value = PLAYER_TRANSLATOR.get(player);
                if (value instanceof String code && LOCALE_CODE.matcher(code)
                    .matches()) {
                    return code.toLowerCase(Locale.ROOT);
                }
            } catch (IllegalAccessException | RuntimeException ignored) {
                // Fail to vanilla's default, never forward an unvalidated client string.
            }
        }
        return FALLBACK_LOCALE;
    }

    private AyanamiDialogueNetwork() {}

    public static void register() {
        CHANNEL.registerMessage(OpenHandler.class, Open.class, 0, Side.SERVER);
        CHANNEL.registerMessage(ReplyHandler.class, Reply.class, 1, Side.CLIENT);
    }

    public static void requestOpen(int entityId) {
        CHANNEL.sendToServer(new Open(entityId));
    }

    static void sendReply(EntityAyanami npc, String text) {
        if (npc.worldObj.isRemote || !npc.isEntityAlive()) return;
        // Broadcast only Rei's already-sanitized line, never the question, history, or inventory snapshot.
        for (EntityPlayer player : npc.worldObj.playerEntities) {
            if (player instanceof EntityPlayerMP listener && !(listener instanceof FakePlayer)
                && listener.worldObj == npc.worldObj
                && listener.isEntityAlive()
                && npc.getDistanceSqToEntity(listener) <= BUBBLE_RANGE * BUBBLE_RANGE) {
                CHANNEL.sendTo(new Reply(listener.dimension, npc.getEntityId(), text), listener);
            }
        }
    }

    public static void sendLocalLine(EntityPlayerMP player, EntityAyanami npc, LocalLine line) {
        CHANNEL.sendTo(new Reply(player.dimension, npc.getEntityId(), line), player);
    }

    public static void sendEventLine(EntityPlayerMP player, EntityAyanami npc, String text, boolean ambient) {
        if (player == null || npc == null || text == null || player.worldObj != npc.worldObj) return;
        String cleaned = AyanamiDialogueText.clean(text, AyanamiDialogueText.MAX_REPLY);
        if (cleaned.isEmpty()) return;
        CHANNEL.sendTo(new Reply(player.dimension, npc.getEntityId(), ambient ? AMBIENT_TEXT : 0, cleaned), player);
    }

    public static void sendAmbientFallback(EntityPlayerMP player, EntityAyanami npc, int variant) {
        if (player == null || npc == null
            || player.worldObj != npc.worldObj
            || variant < 1
            || variant > AMBIENT_VARIANTS) return;
        CHANNEL.sendTo(new Reply(player.dimension, npc.getEntityId(), AMBIENT_TEXT + variant, null), player);
    }

    public enum LocalLine {

        RECEIVED("received"),
        RETURNED("returned"),
        CLONE_WAKE("clone_wake"),
        AWAKENING("awakening"),
        LONGINUS_AGAIN("longinus_again");

        private final String key;

        LocalLine(String key) {
            this.key = "oth.ayanami.speech." + key;
        }
    }

    public static final class Open implements IMessage {

        private int entityId = -1;

        public Open() {}

        private Open(int entityId) {
            this.entityId = entityId;
        }

        @Override
        public void fromBytes(ByteBuf buffer) {
            if (buffer.readableBytes() == 4) entityId = buffer.readInt();
        }

        @Override
        public void toBytes(ByteBuf buffer) {
            buffer.writeInt(entityId);
        }
    }

    public static final class OpenHandler implements IMessageHandler<Open, IMessage> {

        @Override
        public IMessage onMessage(Open message, MessageContext context) {
            if (message.entityId >= 0)
                AyanamiDialogue.queueOpen(context.getServerHandler().playerEntity, message.entityId);
            return null;
        }
    }

    public static final class Reply implements IMessage {

        private int dimension;
        private int entityId;
        private int kind = -1;
        private String text;

        public Reply() {}

        private Reply(int dimension, int entityId, String text) {
            this(dimension, entityId, 0, text);
        }

        private Reply(int dimension, int entityId, LocalLine line) {
            this(dimension, entityId, line.ordinal() + 1, null);
        }

        private Reply(int dimension, int entityId, int kind, String text) {
            this.dimension = dimension;
            this.entityId = entityId;
            this.kind = kind;
            this.text = text;
        }

        @Override
        public void fromBytes(ByteBuf buffer) {
            if (buffer.readableBytes() < 9 || buffer.readableBytes() > 9 + AyanamiDialogueText.MAX_REPLY * 3) return;
            dimension = buffer.readInt();
            entityId = buffer.readInt();
            int id = buffer.readUnsignedByte();
            if (id == 0 || id == AMBIENT_TEXT) {
                text = AyanamiDialogueText
                    .clean(buffer.toString(StandardCharsets.UTF_8), AyanamiDialogueText.MAX_REPLY);
                buffer.skipBytes(buffer.readableBytes());
                if (!text.isEmpty()) kind = id;
            } else if (!buffer.isReadable()
                && (id <= LocalLine.values().length || (id > AMBIENT_TEXT && id <= AMBIENT_TEXT + AMBIENT_VARIANTS))) {
                    kind = id;
                }
        }

        @Override
        public void toBytes(ByteBuf buffer) {
            buffer.writeInt(dimension)
                .writeInt(entityId)
                .writeByte(kind);
            if (text != null) buffer.writeBytes(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static final class ReplyHandler implements IMessageHandler<Reply, IMessage> {

        @Override
        public IMessage onMessage(Reply message, MessageContext context) {
            if (message.kind == -1 || message.entityId < 0) return null;
            if (message.kind == 0) {
                OTHTechnology.proxy.queueAyanamiReply(message.dimension, message.entityId, message.text);
            } else if (message.kind == AMBIENT_TEXT) {
                OTHTechnology.proxy.queueAyanamiAmbientReply(message.dimension, message.entityId, message.text, false);
            } else if (message.kind > AMBIENT_TEXT) {
                OTHTechnology.proxy.queueAyanamiAmbientReply(
                    message.dimension,
                    message.entityId,
                    AMBIENT_KEY_PREFIX + (message.kind - AMBIENT_TEXT),
                    true);
            } else {
                OTHTechnology.proxy.queueAyanamiLocalReply(
                    message.dimension,
                    message.entityId,
                    LocalLine.values()[message.kind - 1].key);
            }
            return null;
        }
    }
}
