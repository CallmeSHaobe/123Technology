package com.newmaa.othtech.client;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.newmaa.othtech.Config;
import com.newmaa.othtech.client.radio.BasicHlsRadioDecoder;
import com.newmaa.othtech.client.radio.FfmpegRadioDecoder;
import com.newmaa.othtech.client.radio.GameRadioOutput;
import com.newmaa.othtech.client.radio.RadioStreamResolver;
import com.newmaa.othtech.client.radio.RadioStreamResolver.StreamInfo;
import com.newmaa.othtech.machine.single.OTESpeakingStone;
import com.newmaa.othtech.machine.single.SpeakingStonePreset;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import paulscode.sound.SoundSystem;

public final class SpeakingStoneClient {

    private static final Logger LOG = LogManager.getLogger("123technology.SpeakingStone");
    private static final double LABEL_RANGE = 64;
    private static final ThreadPoolExecutor PROGRAMMES = new ThreadPoolExecutor(
        1,
        1,
        0L,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(1),
        task -> {
            Thread thread = new Thread(task, "OTH Radio programme guide");
            thread.setDaemon(true);
            return thread;
        });
    private static final long RETRY_DELAY = TimeUnit.SECONDS.toNanos(30);
    private static final long STALL_TIMEOUT = TimeUnit.SECONDS.toNanos(25);
    private static final AudioFormat FORMAT = new AudioFormat(
        FfmpegRadioDecoder.SAMPLE_RATE,
        16,
        FfmpegRadioDecoder.CHANNELS,
        true,
        false);

    private World world;
    private Playback playback;
    private List<OTESpeakingStone> labels = Collections.emptyList();
    private String failedSource;
    private long failedAt;
    private int ticks;

    private SpeakingStoneClient() {}

    public static void install() {
        GameRadioOutput.registerCodec();
        SpeakingStoneClient client = new SpeakingStoneClient();
        FMLCommonHandler.instance()
            .bus()
            .register(client);
        MinecraftForge.EVENT_BUS.register(client);
        LOG.info("Speaking Stone client listener registered");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getMinecraft();
        SoundSystem sound = GameRadioOutput.current(mc.getSoundHandler());
        if (world != mc.theWorld || (playback != null && !playback.output.belongsTo(sound))) {
            stop(sound);
            world = mc.theWorld;
            labels = Collections.emptyList();
            failedSource = null;
        }
        if (world == null || mc.thePlayer == null || mc.isGamePaused()) {
            stop(sound);
            return;
        }
        if (++ticks % 5 != 0) return;
        OTESpeakingStone stone = findStations(mc);
        // The game engine already applies master volume to all its sources.
        float volume = mc.gameSettings.getSoundLevel(SoundCategory.MASTER) > 0
            ? mc.gameSettings.getSoundLevel(SoundCategory.RECORDS)
            : 0;
        if (stone == null || sound == null || volume <= 0) {
            stop(sound);
            return;
        }
        String source = stone.getStreamUrl();
        if (playback != null
            && (!playback.executable.equals(Config.speakingStoneFfmpeg) || !playback.source.equals(source)))
            stop(sound);
        long now = System.nanoTime();
        if (playback != null && (playback.finished || now - playback.lastProgress > STALL_TIMEOUT)) {
            if (!playback.finished) LOG.warn("Speaking Stone stalled at {}", playback.failureKey);
            mc.thePlayer.addChatMessage(new ChatComponentTranslation(playback.failureKey));
            stop(sound);
            failedSource = source;
            failedAt = now;
        }
        float gain = (float) (volume
            * (1 - Math.sqrt(distanceSq(mc, stone.getBaseMetaTileEntity())) / stone.getBroadcastRadius()));
        if (playback == null) {
            if (source.equals(failedSource) && now - failedAt < RETRY_DELAY) return;
            playback = new Playback(Config.speakingStoneFfmpeg, source, new GameRadioOutput(sound));
            Thread thread = new Thread(playback, "OTH Speaking Stone");
            thread.setDaemon(true);
            thread.start();
        } else {
            playback.updateOutput(gain);
            playback.refreshProgramme(now);
        }
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        if (!labels.isEmpty()) SpeakingStoneDisplay.render(event, labels);
    }

    private OTESpeakingStone findStations(Minecraft mc) {
        List<OTESpeakingStone> nearby = new ArrayList<>();
        OTESpeakingStone nearest = null;
        double nearestSq = Double.POSITIVE_INFINITY;
        if (!(mc.theWorld.getChunkProvider() instanceof ChunkProviderClient)) {
            labels = nearby;
            return null;
        }
        ChunkProviderClient provider = (ChunkProviderClient) mc.theWorld.getChunkProvider();
        int centerX = MathHelper.floor_double(mc.thePlayer.posX) >> 4;
        int centerZ = MathHelper.floor_double(mc.thePlayer.posZ) >> 4;
        int scan = Math
            .min((OTESpeakingStone.MAX_RADIUS + 15) / 16, Math.max(2, mc.gameSettings.renderDistanceChunks) + 2);
        // Hodgepodge removes the vanilla private chunk list. On a client provider, provideChunk only looks up
        // the cache and returns an empty placeholder for a missing chunk; it does not request or load anything.
        for (int x = centerX - scan; x <= centerX + scan; x++) {
            for (int z = centerZ - scan; z <= centerZ + scan; z++) {
                Chunk chunk = provider.provideChunk(x, z);
                if (chunk == null || chunk.isEmpty()) continue;
                for (Object tile : chunk.chunkTileEntityMap.values()) {
                    if (!(tile instanceof TileEntity) || ((TileEntity) tile).isInvalid()
                        || !(tile instanceof IGregTechTileEntity)) continue;
                    IGregTechTileEntity machine = (IGregTechTileEntity) tile;
                    if (!(machine.getMetaTileEntity() instanceof OTESpeakingStone) || !machine.isActive()) continue;
                    OTESpeakingStone stone = (OTESpeakingStone) machine.getMetaTileEntity();
                    if (!stone.hasRadioSettings()) continue;
                    double distance = distanceSq(mc, machine);
                    double radius = stone.getBroadcastRadius();
                    String source = stone.getStreamUrl();
                    if (distance < LABEL_RANGE * LABEL_RANGE && distance < radius * radius) {
                        nearby.add(stone);
                        StreamInfo info = playback != null && playback.source.equals(source) ? playback.info : null;
                        // For known stations without a programme guide, the tile supplies the station name.
                        stone.setLocalNowPlaying(
                            info == null || (!info.hasProgrammeMetadata() && SpeakingStonePreset.find(source) != null)
                                ? ""
                                : info.getDisplayName());
                    }
                    if (distance < nearestSq && distance < radius * radius && mayListen(mc, source)) {
                        nearestSq = distance;
                        nearest = stone;
                    }
                }
            }
        }
        labels = nearby;
        return nearest;
    }

    private static boolean mayListen(Minecraft mc, String source) {
        // A multiplayer server can set any tile's URL. Never fetch arbitrary server-provided URLs by default.
        return !source.isEmpty() && !Config.speakingStoneStream.isEmpty()
            && (mc.isIntegratedServerRunning() || Config.speakingStoneAllowServerUrls
                || source.equals(Config.speakingStoneStream)
                || SpeakingStonePreset.find(source) != null);
    }

    private static double distanceSq(Minecraft mc, IGregTechTileEntity tile) {
        return mc.thePlayer.getDistanceSq(tile.getXCoord() + 0.5, tile.getYCoord() + 0.5, tile.getZCoord() + 0.5);
    }

    private void stop(SoundSystem sound) {
        if (playback == null) return;
        playback.cancel();
        playback.output.close(sound);
        playback = null;
    }

    private static final class Playback implements Runnable {

        private final String executable;
        private final String source;
        private final GameRadioOutput output;
        private volatile StreamInfo info;
        private long nextProgrammeRefresh;
        private boolean programmeScheduled;
        private boolean reportedProgress;
        private boolean reportedInput;
        private volatile Process process;
        private volatile Thread worker;
        private volatile boolean cancelled;
        private volatile boolean finished;
        private volatile long lastProgress = System.nanoTime();
        private volatile String failureKey = "ote.speaking_stone.stream_failed";

        private Playback(String executable, String source, GameRadioOutput output) {
            this.executable = executable;
            this.source = source;
            this.output = output;
        }

        private void refreshProgramme(long now) {
            StreamInfo current = info;
            if (current == null || !current.hasProgrammeMetadata()
                || (programmeScheduled && now - nextProgrammeRefresh < 0)) return;
            nextProgrammeRefresh = now + TimeUnit.SECONDS.toNanos(15);
            programmeScheduled = true;
            try {
                PROGRAMMES.execute(() -> { if (!cancelled) current.refreshDisplayName(); });
            } catch (RejectedExecutionException ignored) {
                // A previous channel lookup is still running. Retry at the next refresh interval.
            }
        }

        private void updateOutput(float gain) {
            if (output.update(gain)) {
                LOG.info("Speaking Stone attached to Minecraft audio ({})", output.libraryName());
            }
            if (!reportedProgress) {
                float played = output.millisecondsPlayed();
                if (played >= 1000) {
                    reportedProgress = true;
                    LOG.info("Speaking Stone Minecraft audio advanced ({} ms)", (int) played);
                }
            }
        }

        private void cancel() {
            cancelled = true;
            Process current = process;
            if (current != null) current.destroyForcibly();
            Thread thread = worker;
            if (thread != null) thread.interrupt();
        }

        @Override
        public void run() {
            worker = Thread.currentThread();
            try {
                if (cancelled) return;
                LOG.info(
                    "Speaking Stone playback requested (GT TE, {})",
                    executable.isEmpty() ? "built-in HLS" : "FFmpeg");
                StreamInfo current = RadioStreamResolver.resolveInfo(source);
                info = current;
                URI stream = current.getUri();
                if (cancelled) return;
                if (executable.isEmpty()) {
                    failureKey = "ote.speaking_stone.builtin_failed";
                    new BasicHlsRadioDecoder().play(stream, () -> cancelled, this::writePcm);
                } else {
                    playFfmpeg(stream);
                }
            } catch (Exception | LinkageError ex) {
                // Do not log exception messages or causes: network failures may contain signed URLs.
                if (!cancelled) LOG.warn(
                    "Speaking Stone playback failed at {} ({})",
                    failureKey,
                    ex.getClass()
                        .getSimpleName());
            } finally {
                Process current = process;
                if (current != null && current.isAlive()) current.destroyForcibly();
                worker = null;
                finished = true;
            }
        }

        private void playFfmpeg(URI stream) throws IOException {
            failureKey = "ote.speaking_stone.ffmpeg_failed";
            process = FfmpegRadioDecoder.start(executable, stream);
            if (cancelled) return;
            failureKey = "ote.speaking_stone.stream_failed";
            try (AudioInputStream pcm = new AudioInputStream(
                process.getInputStream(),
                FORMAT,
                AudioSystem.NOT_SPECIFIED)) {
                byte[] buffer = new byte[FfmpegRadioDecoder.FRAME_BYTES * 512];
                int count;
                while (!cancelled && (count = pcm.read(buffer)) >= 0) {
                    if (count > 0) {
                        writePcm(buffer, count, FfmpegRadioDecoder.SAMPLE_RATE, FfmpegRadioDecoder.CHANNELS);
                    }
                }
            }
        }

        private void writePcm(byte[] pcm, int count, int sampleRate, int channels) throws IOException {
            if (cancelled) return;
            String decoderStage = failureKey;
            failureKey = "ote.speaking_stone.audio_failed";
            output.write(pcm, count, sampleRate, channels);
            lastProgress = System.nanoTime();
            if (!reportedInput && count > 0) {
                reportedInput = true;
                LOG.info("Speaking Stone queued PCM for Minecraft ({} Hz, {} channels)", sampleRate, channels);
            }
            failureKey = decoderStage;
        }
    }
}
