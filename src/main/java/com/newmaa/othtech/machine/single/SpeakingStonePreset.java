package com.newmaa.othtech.machine.single;

/** Built-in public station URLs, also used as an exact-match allowlist on multiplayer clients. */
public enum SpeakingStonePreset {

    CCTV_NEWS("cctv_news", "https://piccpndali.v.myalicdn.com/audio/cctv13_2.m3u8"),
    CNR_VOICE("cnr1", "https://apicnrapp.cnr.cn/html/play.html?channelId=1"),
    CNR_ECONOMY("cnr2", "https://apicnrapp.cnr.cn/html/play.html?channelId=2"),
    CNR_MUSIC("cnr4", "https://apicnrapp.cnr.cn/html/play.html?channelId=4"),
    BBC_RADIO_ONE(true, "bbc_radio_one",
        "https://as-hls-ww-live.akamaized.net/pool_01505109/live/ww/bbc_radio_one/bbc_radio_one.isml/bbc_radio_one-audio=320000.norewind.m3u8"),
    FIP(true, "fip", "https://stream.radiofrance.fr/fip/fip_hifi.m3u8"),
    FRANCE_CULTURE(true, "france_culture", "https://stream.radiofrance.fr/franceculture/franceculture_hifi.m3u8"),
    ABC_CLASSIC(true, "abc_classic",
        "https://mediaserviceslive.akamaized.net/hls/live/2108547/classicfmnsw/v0-221.m3u8");

    private static final SpeakingStonePreset[] LOOKUP = values();

    private final String labelKey;
    private final String url;
    private final boolean international;

    SpeakingStonePreset(String key, String url) {
        this(false, key, url);
    }

    SpeakingStonePreset(boolean international, String key, String url) {
        this.labelKey = "ote.speaking_stone." + key;
        this.url = url;
        this.international = international;
    }

    public boolean isInternational() {
        return international;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public String getUrl() {
        return url;
    }

    public static SpeakingStonePreset find(String url) {
        for (SpeakingStonePreset station : LOOKUP) {
            if (station.url.equals(url)) return station;
        }
        return null;
    }
}
