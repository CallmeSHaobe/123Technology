package com.newmaa.othtech.common.recipemap;

import static com.newmaa.othtech.common.recipemap.Recipemaps.DeepSeikoBEC;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gregtech.api.enums.Materials;
import gregtech.api.enums.NaniteTier;
import gregtech.api.util.GTRecipe;
import gregtech.api.util.GTRecipeConstants;
import gtPlusPlus.core.material.MaterialsElements;
import tectech.recipe.TecTechRecipeMaps;

/**
 * 给「迪普西科」({@code OTEDeepSeiko}) 的凝聚态配方池 {@link Recipemaps#DeepSeikoBEC} 灌配方。
 * <p>
 * ⚠ 关键坑：TecTech 的 {@code condensateAssemblingRecipes} 后端在 compileRecipe 里把
 * {@code mFluidInputs} 清空、真流体（纠缠凝聚态）转存进 metadata
 * {@link GTRecipeConstants#CONDENSATE_INPUT}——所以拷贝时必须从 metadata 取流体，
 * 直接读 mFluidInputs 永远是空数组（v1.5.1 悬案根因）。
 * <p>
 * 拿到纠缠凝聚态流体后，按 {@link #ENTANGLED_TO_SOURCE} 硬编码镜像表 1:1 换回对应普通材料流体
 * （同数量）。19 条映射与 TecTech {@code CondensateType} 枚举的 source 供应商逐一对应，
 * A1→A2 数量不变；表外流体留原样并打 WARN。
 * <p>
 * 同时把配方的蜂群等级要求（NANITE_TIERS metadata 的最高档，1~10）压进 {@code mSpecialValue}，
 * 供鲸的纳米蜂收容仓做门槛判定（GTRecipe.copy() 会丢 metadata，所以必须转存）。
 * <p>
 * 执行时机：{@code GregTechAPI.sGTCompleteLoad} + 迪普西科自己的 FMLLoadComplete（{@code RecipeLoader}
 * 末尾再跑一次本类，幂等）——双保险兜住一切晚注册的 BEC 配方。
 */
public final class DeepSeikoBECRecipeLoader {

    private static final Logger LOG = LogManager.getLogger("123Technology/DeepSeikoBEC");

    /**
     * 纠缠凝聚态流体名（{@code entangled_<id>}）→ 普通形态源流体。
     * 19 条 = TecTech {@code CondensateType} 全枚举，逐条照抄其 source 供应商
     * （熔融态优先，无熔融的用标准流体；独立元素用 getFluidStack）。
     */
    private static final Map<String, Fluid> ENTANGLED_TO_SOURCE = buildEntangledToSource();

    private DeepSeikoBECRecipeLoader() {}

    public static void load() {
        DeepSeikoBEC.getBackend()
            .clearRecipes();
        int copied = copyBecRecipes();
        if (copied == 0) {
            LOG.warn("BEC recipe pool is empty, DeepSeikoBEC will have no recipes");
        }
        LOG.info(
            "DeepSeikoBEC recipe pool filled with {} recipes ({} fluid mappings)",
            copied,
            ENTANGLED_TO_SOURCE.size());
    }

    private static int copyBecRecipes() {
        int copied = 0;
        boolean sampleLogged = false;
        for (GTRecipe recipe : TecTechRecipeMaps.condensateAssemblingRecipes.getAllRecipes()) {
            if (!usable(recipe)) continue;
            try {
                GTRecipe copy = recipe.copy();
                // 蜂群等级门槛：metadata 会被 copy() 丢掉，转存进 mSpecialValue（1~10，无要求 = 0）
                NaniteTier[] tiers = recipe.getMetadata(GTRecipeConstants.NANITE_TIERS);
                int maxTier = 0;
                if (tiers != null) {
                    for (NaniteTier tier : tiers) {
                        if (tier != null) maxTier = Math.max(maxTier, tier.getTier()); // 用真实的 1~10 等级，不能用 ordinal+1
                    }
                }
                copy.mSpecialValue = maxTier;
                // 真流体在 metadata 里（TecTech compileRecipe 清空了 mFluidInputs），从那里取
                FluidStack[] entangled = recipe.getMetadata(GTRecipeConstants.CONDENSATE_INPUT);
                if (entangled == null || entangled.length == 0) entangled = recipe.mFluidInputs;
                copy.mFluidInputs = toOrdinaryFluids(entangled);
                if (!sampleLogged) {
                    sampleLogged = true;
                    LOG.info(
                        "DeepSeikoBEC sample conversion: {} -> {}",
                        describeFluids(entangled),
                        describeFluids(copy.mFluidInputs));
                }
                if (DeepSeikoBEC.add(copy) != null) {
                    copied++;
                }
            } catch (Throwable t) {
                LOG.warn("Failed to copy a BEC recipe into DeepSeikoBEC", t);
            }
        }
        return copied;
    }

    private static boolean usable(GTRecipe recipe) {
        return recipe != null && recipe.mEnabled && !recipe.mFakeRecipe && !recipe.mHidden;
    }

    private static String describeFluids(FluidStack[] fluids) {
        if (fluids == null || fluids.length == 0) return "(none)";
        StringBuilder sb = new StringBuilder();
        for (FluidStack fs : fluids) {
            if (sb.length() > 0) sb.append('+');
            if (fs == null || fs.getFluid() == null) sb.append("null");
            else sb.append(
                fs.getFluid()
                    .getName())
                .append('x')
                .append(fs.amount);
        }
        return sb.toString();
    }

    /** 纠缠凝聚态 → 普通形态：硬编码镜像表 1:1 同数量换回；表外的 entangled_* 打 WARN 留原样。 */
    private static FluidStack[] toOrdinaryFluids(FluidStack[] fluids) {
        if (fluids == null) return null;
        FluidStack[] result = new FluidStack[fluids.length];
        for (int i = 0; i < fluids.length; i++) {
            FluidStack fs = fluids[i];
            if (fs == null || fs.getFluid() == null) {
                result[i] = fs;
                continue;
            }
            Fluid source = ENTANGLED_TO_SOURCE.get(
                fs.getFluid()
                    .getName());
            if (source == null) {
                String name = fs.getFluid()
                    .getName();
                if (name.startsWith("entangled_")) {
                    LOG.warn("DeepSeikoBEC: no mapping for entangled fluid {}, left as-is", name);
                }
                result[i] = fs;
            } else {
                result[i] = new FluidStack(source, fs.amount);
            }
        }
        return result;
    }

    /**
     * 19 条凝聚态镜像映射（A1→A2，数量不变）。id 与源流体严格照抄
     * {@code gregtech.api.enums.CondensateType} 各枚举项的 source 供应商。
     */
    private static Map<String, Fluid> buildEntangledToSource() {
        Map<String, Fluid> map = new HashMap<>();
        put(map, "neutronium", Materials.Neutronium.getMolten(1));
        put(map, "cosmicneutronium", Materials.CosmicNeutronium.getMolten(1));
        put(map, "bedrockium", Materials.Bedrockium.getMolten(1));
        put(map, "chromaticglass", MaterialsElements.STANDALONE.CHRONOMATIC_GLASS.getFluidStack(1));
        put(map, "celestialtungsten", MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN.getFluidStack(1));
        put(map, "infinity", Materials.Infinity.getMolten(1));
        put(map, "hypogen", MaterialsElements.STANDALONE.HYPOGEN.getFluidStack(1));
        put(map, "transcendentmetal", Materials.TranscendentMetal.getMolten(1));
        put(map, "dimshiftedsuperfluid", Materials.DimensionallyShiftedSuperfluid.getFluid(1));
        put(map, "phononmedium", Materials.PhononMedium.getFluid(1));
        put(map, "quarkgluonplasma", Materials.QuarkGluonPlasma.getFluid(1));
        put(map, "spacetime", Materials.SpaceTime.getMolten(1));
        put(map, "time", Materials.Time.getMolten(1));
        put(map, "space", Materials.Space.getMolten(1));
        put(map, "cosmicsolder", Materials.BoundlessCosmicSolder.getFluid(1));
        put(map, "mhdcsm", Materials.MHDCSM.getMolten(1));
        put(map, "magmatter", Materials.MagMatter.getMolten(1));
        put(map, "universium", Materials.Universium.getMolten(1));
        put(map, "eternity", Materials.Eternity.getMolten(1));
        return map;
    }

    private static void put(Map<String, Fluid> map, String entangledId, FluidStack source) {
        if (source == null || source.getFluid() == null) {
            LOG.warn("DeepSeikoBEC: source fluid for entangled_{} is null, mapping skipped", entangledId);
            return;
        }
        map.put("entangled_" + entangledId, source.getFluid());
    }
}
