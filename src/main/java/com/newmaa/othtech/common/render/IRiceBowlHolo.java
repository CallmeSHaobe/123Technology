package com.newmaa.othtech.common.render;

/**
 * 米饭全息投影的状态接口。
 *
 * <p>
 * 放在 common 包里（**不能**带 {@code @SideOnly(CLIENT)}）——承载它的 TileEntity 在服务端也存在，
 * 如果接口挂在客户端专属类里，服务端加载 TE 类型时会直接炸。
 * </p>
 */
public interface IRiceBowlHolo {

    /** 投影是否应该存在（结构成型就为 true；false 时投影会自己消散） */
    boolean isHoloRunning();

    /** 是否在「吃」——加工中才播放吃→添循环，停机时一碗满满地摆着不动 */
    boolean isHoloEating();

    /** 动画时钟（秒），客户端用毫秒差累加，不跟 tick 绑定 */
    float getHoloTimer();

    void setHoloTimer(float timer);

    /** 整体淡入淡出 0..1（停机时单独衰减，不碰动画时钟，避免跳帧） */
    float getHoloFade();

    void setHoloFade(float fade);

    long getHoloLastMillis();

    void setHoloLastMillis(long millis);
}
