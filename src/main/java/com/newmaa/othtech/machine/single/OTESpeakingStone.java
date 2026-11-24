package com.newmaa.othtech.machine.single;

import java.net.URI;
import java.nio.charset.StandardCharsets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.UISettings;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.newmaa.othtech.machine.machineclass.OTHSingleMachineBase;

import gregtech.api.enums.GTValues;
import gregtech.api.enums.SoundResource;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBuilder;
import gregtech.api.render.TextureFactory;
import io.netty.buffer.ByteBuf;

public final class OTESpeakingStone extends OTHSingleMachineBase {

    // GT5U requires a nonzero UI capacity even for a placeholder map. The stone itself has no I/O slots.
    private static final RecipeMap<?> NO_RECIPES = RecipeMapBuilder.of("otht.recipe.speaking_stone")
        .maxIO(1, 1, 0, 0)
        .disableRegisterNEI()
        .build();
    private static final ITexture[] STONE = { TextureFactory.of(Blocks.stone, 0, ForgeDirection.NORTH) };
    private static final ITexture[] TOP = { TextureFactory.of(Blocks.jukebox, 0, ForgeDirection.UP) };

    public static final String DEFAULT_STREAM_URL = SpeakingStonePreset.CNR_VOICE.getUrl();
    public static final int MAX_URL_LENGTH = 512;
    public static final int MIN_RADIUS = 1;
    public static final int MAX_RADIUS = (int) GTValues.V[4];
    private static final int DEFAULT_RADIUS = 24;
    private static final int BROADCAST_INTERVAL = 20; // One game second at 20 TPS.

    private boolean broadcastEnabled;
    private int broadcastRadius = DEFAULT_RADIUS;
    private String streamUrl = DEFAULT_STREAM_URL;
    private String nowPlaying = ""; // Local metadata only; never written to disk or trusted from other players.
    private boolean radioSettingsSynced;

    public OTESpeakingStone(int id, String name, String regionalName, String[] description) {
        super(
            id,
            name,
            regionalName,
            4,
            description,
            NO_RECIPES,
            0,
            0,
            false,
            false,
            SoundResource.NONE,
            SpecialEffects.NONE);
    }

    private OTESpeakingStone(OTESpeakingStone prototype) {
        super(prototype);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity tile) {
        return new OTESpeakingStone(this);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity tile, ForgeDirection side, ForgeDirection facing, int color,
        boolean active, boolean redstone) {
        return side == ForgeDirection.UP ? TOP : STONE;
    }

    @Override
    public ITexture[][] getInventoryTextures() {
        ITexture[][] textures = new ITexture[6][];
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            textures[side.ordinal()] = side == ForgeDirection.UP ? TOP : STONE;
        }
        return textures;
    }

    @Override
    public void initDefaultModes(NBTTagCompound nbt) {
        mMainFacing = ForgeDirection.UP;
    }

    @Override
    protected boolean isValidMainFacing(ForgeDirection side) {
        return side == ForgeDirection.UP;
    }

    @Override
    public boolean isFacingValid(ForgeDirection side) {
        return side != ForgeDirection.UNKNOWN;
    }

    @Override
    public void onClientSoundStateChanged() {
        // GT's basic-machine loop would try to play SoundResource.NONE when the stone becomes active.
        // The client radio player handles all audio for this stone instead.
    }

    @Override
    public long maxAmperesIn() {
        return 1;
    }

    @Override
    public long maxEUInput() {
        return GTValues.V[4];
    }

    @Override
    public int checkRecipe() {
        return DID_NOT_FIND_RECIPE;
    }

    @Override
    protected boolean useMui2() {
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity tile, EntityPlayer player) {
        openGui(player);
        return true;
    }

    @Override
    public ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        return SpeakingStoneUI.build(this, syncManager);
    }

    public boolean isBroadcasting() {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        return tile != null && tile.isActive();
    }

    public boolean hasRadioSettings() {
        return radioSettingsSynced;
    }

    public boolean isBroadcastEnabled() {
        return broadcastEnabled;
    }

    public void setBroadcastEnabled(boolean enabled) {
        if (broadcastEnabled == enabled) return;
        broadcastEnabled = enabled;
        settingsChanged();
    }

    public int getBroadcastRadius() {
        return broadcastRadius;
    }

    public void setBroadcastRadius(int radius) {
        int bounded = clampRadius(radius);
        if (broadcastRadius == bounded) return;
        broadcastRadius = bounded;
        settingsChanged();
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String url) {
        String validated = validateStreamUrl(url);
        if (validated == null || streamUrl.equals(validated)) return;
        streamUrl = validated;
        nowPlaying = "";
        settingsChanged();
    }

    public String getNowPlaying() {
        if (!nowPlaying.isEmpty() || streamUrl.isEmpty()) return nowPlaying;
        SpeakingStonePreset station = SpeakingStonePreset.find(streamUrl);
        if (station != null) return StatCollector.translateToLocal(station.getLabelKey());
        return URI.create(streamUrl)
            .getHost();
    }

    public void setLocalNowPlaying(String title) {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        if (tile != null && tile.isClientSide()) nowPlaying = title;
    }

    private void settingsChanged() {
        IGregTechTileEntity tile = getBaseMetaTileEntity();
        // MUI2 invokes these setters on both sides when synchronizing the panel.
        if (tile != null && tile.isServerSide()) {
            markDirty();
            tile.issueTileUpdate();
        }
    }

    @Override
    public void onPostTick(IGregTechTileEntity tile, long tick) {
        if (tick % BROADCAST_INTERVAL != 0 || !tile.isServerSide()) return;
        // Pay for the next broadcast interval in one transfer, keeping 1 EU/t per block on average.
        long cost = (long) broadcastRadius * BROADCAST_INTERVAL;
        boolean active = broadcastEnabled && !streamUrl.isEmpty()
            && tile.isAllowedToWork()
            && tile.getStoredEU() >= cost
            && tile.decreaseStoredEnergyUnits(cost, false);
        if (tile.isActive() != active) {
            tile.setActive(active);
            markDirty();
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound nbt) {
        super.saveNBTData(nbt);
        nbt.setBoolean("SpeakingStoneBroadcasting", broadcastEnabled);
        nbt.setInteger("SpeakingStoneRadius", broadcastRadius);
        nbt.setString("SpeakingStoneUrl", streamUrl);
    }

    @Override
    public void loadNBTData(NBTTagCompound nbt) {
        super.loadNBTData(nbt);
        mMainFacing = ForgeDirection.UP;
        broadcastEnabled = nbt.getBoolean("SpeakingStoneBroadcasting");
        broadcastRadius = nbt.hasKey("SpeakingStoneRadius", 99) ? clampRadius(nbt.getInteger("SpeakingStoneRadius"))
            : DEFAULT_RADIUS;
        String validated = nbt.hasKey("SpeakingStoneUrl", 8) ? validateStreamUrl(nbt.getString("SpeakingStoneUrl"))
            : DEFAULT_STREAM_URL;
        streamUrl = validated == null ? "" : validated;
    }

    @Override
    public void writeToStream(ByteBuf buffer) {
        super.writeToStream(buffer);
        byte[] encoded = streamUrl.getBytes(StandardCharsets.UTF_8);
        buffer.writeBoolean(broadcastEnabled);
        buffer.writeShort(broadcastRadius);
        buffer.writeShort(encoded.length);
        buffer.writeBytes(encoded);
    }

    @Override
    public void readFromStream(ByteBuf buffer) {
        super.readFromStream(buffer);
        if (buffer.readableBytes() < 5) return;
        boolean enabled = buffer.readBoolean();
        int radius = buffer.readUnsignedShort();
        int length = buffer.readUnsignedShort();
        if (length > MAX_URL_LENGTH || length > buffer.readableBytes()) return;
        String url = buffer.toString(buffer.readerIndex(), length, StandardCharsets.UTF_8);
        buffer.skipBytes(length);
        setStreamUrl(url);
        setBroadcastRadius(radius);
        setBroadcastEnabled(enabled);
        radioSettingsSynced = true;
    }

    private static int clampRadius(int radius) {
        return Math.max(MIN_RADIUS, Math.min(MAX_RADIUS, radius));
    }

    private static String validateStreamUrl(String input) {
        if (input == null || input.length() > MAX_URL_LENGTH) return null;
        String value = input.trim();
        if (value.isEmpty()) return "";
        try {
            URI uri = URI.create(value);
            if (uri.getHost() == null || uri.getRawUserInfo() != null
                || uri.getRawFragment() != null
                || !("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))) {
                return null;
            }
            String ascii = uri.toASCIIString();
            return ascii.length() <= MAX_URL_LENGTH ? ascii : null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
