package com.newmaa.othtech.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;

import gregtech.api.util.MultiblockTooltipBuilder;

/**
 * GT5 2.9.0（5.09.54.2xx）起 {@link MultiblockTooltipBuilder#toolTipFinisher(String...)} 只是为了二进制兼容才留着，
 * 传进去的参数会被直接忽略：GT 把多块机器 tooltip 里那行 "Added by" 署名删掉了，连 {@code GTAuthors} 和
 * {@code GT5U.MBTT.Mod} 这个 lang key 都一起没了。所以原来的 {@code .toolTipFinisher("§a123Technology - xxx")}
 * 变成了一段死代码，编译不报错、游戏里什么都不显示。
 * <p>
 * 这个子类把署名行补回来，位置和 2.9.0 之前一致（分隔线和 "Hold [LSHIFT] to display structure" 之下）：
 * GT 的 {@code MTETooltipMultiBlockBase} 只通过 {@link #getInformation()} 取正文，重写这个 getter 追加即可，
 * 不用去碰 {@code toolTipFinisher} 内部的渲染流程。链式写法不用改：
 *
 * <pre>
 * new OTHTooltipBuilder().addMachineType(...)
 *     ...
 *     .toolTipFinisher("§a123Technology - xxx");
 * </pre>
 */
public class OTHTooltipBuilder extends MultiblockTooltipBuilder {

    /** 2.9.0 之前这里读的是 GT5U.MBTT.Mod（"Added by"），那个 lang key 已经被 GT 删了，只能写死。 */
    private static final String ADDED_BY = "" + EnumChatFormatting.DARK_GRAY
        + "Added by"
        + EnumChatFormatting.GRAY
        + ": ";

    private final List<String> credits = new ArrayList<>();

    /** 合并后的正文，缓存一份，避免每次取 tooltip 都重新拼数组。 */
    private String[] information;

    @Override
    public MultiblockTooltipBuilder toolTipFinisher(String... aCredits) {
        if (aCredits != null) {
            for (String credit : aCredits) {
                if (credit != null && !credit.isEmpty()) {
                    credits.add(credit);
                }
            }
        }
        return super.toolTipFinisher();
    }

    @Override
    public String[] getInformation() {
        final String[] base = super.getInformation();
        if (base == null || credits.isEmpty()) {
            return base;
        }
        if (information == null) {
            final String[] merged = Arrays.copyOf(base, base.length + credits.size());
            for (int i = 0; i < credits.size(); i++) {
                merged[base.length + i] = ADDED_BY + credits.get(i);
            }
            information = merged;
        }
        return information;
    }
}
