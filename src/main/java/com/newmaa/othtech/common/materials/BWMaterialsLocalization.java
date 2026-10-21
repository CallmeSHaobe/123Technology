package com.newmaa.othtech.common.materials;

import static gregtech.api.util.GTLanguageManager.addStringLocalization;
import static net.minecraft.util.StatCollector.translateToLocalFormatted;

/**
 * 123Technology 的 BartWorks Werkstoff 本地化。
 * <p>
 * GT5U(5.09.54.x) 的 BartWorks 已改成「桥接材料」体系：{@code BridgeMaterialsLoader} 会为每个 Werkstoff 用
 * {@code MaterialBuilder} 造一个 {@link gregtech.api.enums.Materials} 桥接项，物品显示名走
 * {@code BWMetaGeneratedItems#getItemStackDisplayName}
 * → {@code OrePrefixes#getLocalizedNameForItem(IOreMaterial)}
 * → {@code IOreMaterial#getLocalizedNameKey()}， 而该 key 的默认实现是
 * {@code "Material." + getInternalName().toLowerCase()}（Werkstoff 的 internalName 即 varName）。
 * <p>
 * 也就是说旧版的 {@code bw.werkstoff.<id>.name} 已经没有任何名称查询会读取它，必须改注册
 * {@code Material.<varName 小写>}。这里两套 key 都注册：新 key 负责实际显示，旧 key 仅作兼容。
 */
public class BWMaterialsLocalization {

    /**
     * {werkstoff id, werkstoff varName, 英文 lang key}
     * <p>
     * 其中新键位 = {@code "Material." + varName.toLowerCase()}。
     */
    private static final String[][] ENTRIES = {
        // spotless:off
        { "12303", "FluidGalaxy",   "fluid.FluidGalaxy.name" },
        { "12304", "FluidVoid",     "fluid.FluidVoid.name" },
        { "12305", "MoltenStar",    "fluid.MoltenStar.name" },
        { "12308", "FUELA",         "fluid.FUELA.name" },
        { "12309", "FUELA_DE",      "fluid.FUELA_DE.name" },
        { "12310", "PromoterZPM",   "fluid.PromoterZPM.name" },
        { "12311", "PromoterUEV",   "fluid.PromoterUEV.name" },
        { "12320", "IrOsSm",        "item.IrOsSm.name" },
        { "12312", "HCl10",         "fluid.HCl10.name" },
        { "12313", "HCl100",        "fluid.HCl100.name" },
        { "12314", "HCl1000",       "fluid.HCl1000.name" },
        { "12315", "HCl10000",      "fluid.HCl10000.name" },
        { "12316", "HCl100000",     "fluid.HCl100000.name" },
        // spotless:on
    };

    public void loader() {
        for (String[] entry : ENTRIES) {
            final String id = entry[0];
            final String varName = entry[1];
            final String localizedName = translateToLocalFormatted(entry[2]);
            final String lowerVarName = varName.toLowerCase();

            // 当前 GT5U 的名称键位（IOreMaterial#getLocalizedNameKey）：物品名实际读的就是这个
            addStringLocalization("Material." + lowerVarName, localizedName);
            // 流体名键位
            addStringLocalization("fluid." + lowerVarName, localizedName);
            // 旧版 BartWorks 键位，保留兼容
            addStringLocalization("bw.werkstoff." + id + ".name", localizedName);
        }

        // 铱锇钐的熔融流体
        addStringLocalization("molten.irossm", translateToLocalFormatted("fluid.IrOsSm.name"));
    }
}
