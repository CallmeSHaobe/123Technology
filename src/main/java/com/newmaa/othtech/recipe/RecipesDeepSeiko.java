package com.newmaa.othtech.recipe;

import static com.newmaa.othtech.utils.Utils.hasNullItem;
import static gregtech.api.enums.Mods.GraviSuite;
import static gregtech.api.enums.Mods.PamsHarvestCraft;
import static gregtech.api.enums.TierEU.RECIPE_UMV;
import static gregtech.api.util.GTModHandler.getModItem;
import static gregtech.api.util.GTRecipeConstants.COMPRESSION_TIER;
import static gregtech.api.util.GTRecipeConstants.NANITE_TIERS;

import java.util.Arrays;
import java.util.Objects;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.newmaa.othtech.OTHTechnology;
import com.newmaa.othtech.common.OTHItemList;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoBlocks;
import com.newmaa.othtech.common.blocks.deepseiko.DeepSeikoCasingMeta;
import com.newmaa.othtech.common.recipemap.Recipemaps;
import com.newmaa.othtech.machine.OTEDeepSeiko;

import gregtech.api.GregTechAPI;
import gregtech.api.enums.CondensateType;
import gregtech.api.enums.GTValues;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.MetaTileEntityIDs;
import gregtech.api.enums.NaniteTier;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMaps;
import gregtech.api.util.GTModHandler;
import gregtech.api.util.GTOreDictUnificator;
import gregtech.api.util.GTUtility;
import gtPlusPlus.core.material.MaterialMisc;
import tectech.recipe.TTRecipeAdder;
import tectech.recipe.TecTechRecipeMaps;

/**
 * 迪普西科 DeepSeiko 的全部配方：白米饭（潘马斯水稻 + 碗）、鲸跃显卡、数据硬盘、结构方块、
 * 控制器 + 纳米蜂收容仓、米饭奇点（稳定黑洞），以及「机体升级」三项升级的材料清单（只给 NEI 查）。
 */
public class RecipesDeepSeiko implements IRecipePool {

    @Override
    public void loadRecipes() {
        addRiceRecipe();
        addCasingRecipes();
        addMachineRecipes();
        addRiceSingularityRecipe();
        addSpacetimeCoolingCore();
        addDeepSeikoBecRecipe();
        addDsCasingAssemblyLineRecipe();
        addAssemblyCasingAssemblyLineRecipe();
        addExoticCasingRecipes();
        addFinCasingRecipes();
        addCondensateAdapterBecRecipe();
        addComputeCenterBecRecipes();
        addUpgradeRecipes();
    }

    /**
     * 米饭奇点：1234 个白米饭 → 1 个奇点。
     * <p>
     * {@code COMPRESSION_TIER = 2} ⇒ NEI 会标「需要稳定的黑洞」，只能由亚稳态黑洞遏制场（BHC）
     * 在黑洞稳定时压出来；中子态素压缩机（tier &gt; 0 的配方直接拒绝）跑不了。
     * </p>
     */
    private void addRiceSingularityRecipe() {
        GTValues.RA.stdBuilder()
            .itemInputs(OTHItemList.WhiteRice.get(1234))
            .itemOutputs(OTHItemList.RiceSingularity.get(1))
            .metadata(COMPRESSION_TIER, 2)
            .duration(1234 * 20)
            .eut(RECIPE_UMV)
            .addTo(RecipeMaps.neutroniumCompressorRecipes);
    }

    /**
     * 时空冷却核心：<b>原版装配线</b>配方（带研究/闪存）。
     * <p>
     * 用 TecTech 的 {@code addResearchableAssemblylineRecipe}：它会同时登记
     * ①正式装配线配方（需要研究好的闪存）②研究站假配方（扫描 → 闪存）③NEI 可视化配方。
     * 我们的迪普西科装配线模式拷贝的是③，所以这台机器也能直接做这条配方。
     * </p>
     * <ul>
     * <li>研究物品：超级冷却机械方块（{@code gt.foundrycasings:9}）</li>
     * <li>研究参数：总算力 123,321,000、每秒算力 8,192、功率 2,000,000,000、4A</li>
     * <li>装配线本体：500,000,000 EU/t、300 秒</li>
     * </ul>
     */
    private void addSpacetimeCoolingCore() {
        ItemStack[] inputs = new ItemStack[] { new ItemStack(Blocks.ice, 64), // 冰
            new ItemStack(Blocks.packed_ice, 64), // 浮冰
            firstNonNull(getModItem("BiomesOPlenty", "hardIce", 64, 0), getModItem("BiomesOPlenty", "hard_ice", 64, 0)), // 硬化冰块
            getModItem("etfuturum", "blue_ice", 64, 0), // 蓝冰
            getModItem(GraviSuite.ID, "itemSimpleItem", 64, 2), // 冷却核心（GraviSuite 子物品 #2）
            new ItemStack(GregTechAPI.sBlockCasings8, 64, 14), // 无尽冷却机械方块
            ItemList.StableAdhesive.get(32), // 超稳态自修复黏合剂
            ItemList.EnergisedTesseract.get(16), // 充能超立方体
            GTOreDictUnificator.get("plateSuperdenseCallistoIce", 64), // 超致密木卫四冰板
            getModItem("universalsingularities", "universal.vanilla.singularity", 64, 3) }; // 水奇点（meta 3）

        if (Arrays.stream(inputs)
            .anyMatch(Objects::isNull)) {
            OTHTechnology.LOG.warn("时空冷却核心配方缺少物品，本次跳过");
            return;
        }

        FluidStack water = Materials.Water.getFluid(123321);
        FluidStack coolant = FluidRegistry.getFluidStack("ic2coolant", 123321);
        FluidStack superCoolant = Materials.SuperCoolant.getFluid(123321);
        FluidStack spacetime = FluidRegistry.getFluidStack("molten.spacetime", 1017);
        if (water == null || coolant == null || superCoolant == null || spacetime == null) {
            OTHTechnology.LOG.warn("时空冷却核心配方缺少流体，本次跳过");
            return;
        }

        ItemStack researchItem = new ItemStack(GregTechAPI.sBlockCasingsFoundry, 1, 9); // 超级冷却机械方块
        boolean ok = TTRecipeAdder.addResearchableAssemblylineRecipe(
            researchItem,
            123_321_000, // 总算力
            8192, // 每秒算力
            2_000_000_000, // 研究功率 EU/t
            4, // 研究电流
            inputs,
            new FluidStack[] { water, coolant, superCoolant, spacetime },
            OTHItemList.SpacetimeCoolingCore.get(1),
            6000, // 装配线时长（tick）= 300 秒
            500_000_000); // 装配线 EU/t
        if (!ok) OTHTechnology.LOG.warn("时空冷却核心配方注册失败（addResearchableAssemblylineRecipe 返回 false）");
    }

    private static ItemStack firstNonNull(ItemStack a, ItemStack b) {
        return a != null ? a : b;
    }

    /** 白米饭：潘马斯水稻 ×3 + 碗。 */
    /**
     * 迪普西科（控制器）的凝聚态配方。
     *
     * <p>
     * 写进原版 {@code condensateAssemblingRecipes}；本模组的 {@code DeepSeikoBEC} 在 sGTCompleteLoad 阶段会把
     * TecTech 的凝聚态配方整批拷过来（并把纠缠凝聚态镜像回普通流体），所以 <b>BEC 机器和迪普西科都能做</b>。
     * </p>
     *
     * <p>
     * 两个防御：{@code circuitExotic} 是 GT++/核心模组动态注册的矿辞，个别整合包可能没有 ⇒ 回退到
     * {@code ItemList.Circuit_ExoticMainframe}；任何输入取空就整条跳过并打日志，不注册半条配方。
     * </p>
     */
    private void addDeepSeikoBecRecipe() {
        ItemStack exoticCircuit = GTOreDictUnificator.get("circuitExotic", 48);
        if (exoticCircuit == null) {
            exoticCircuit = ItemList.Circuit_ExoticMainframe.get(48);
            OTHTechnology.LOG.warn("[DeepSeiko] 矿辞 circuitExotic 不存在，回退为 Circuit_ExoticMainframe ×48");
        }
        ItemStack[] inputs = new ItemStack[] { OTHItemList.RiceSingularity.get(8),
            OTHItemList.SpacetimeCoolingCore.get(16), exoticCircuit, new ItemStack(GregTechAPI.sBlockCasings8, 32, 14),
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.ASSEMBLING_LINE_CONTROLLER.ID),
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.AdvancedAssline.ID),
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.CircuitAssemblyLine.ID),
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.ComponentAssemblyLine.ID),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32277),
            new ItemStack(GregTechAPI.sBlockMachines, 16, MetaTileEntityIDs.UncertaintyResolution.ID),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 48, 32045),
            GTModHandler.getModItem("OpenComputers", "item", 32, 44),
            GTModHandler.getModItem("OpenComputers", "item", 32, 10),
            GTModHandler.getModItem("computronics", "computronics.ocSpecialParts", 8) };
        if (hasNullItem((Object[]) inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] 控制器凝聚态配方缺少物品（检查 OpenComputers/computronics/gt.metaitem），本次跳过");
            return;
        }
        // 凝聚态的量必须是 unit/2 = 72 的整数倍（CondensateType.getEntangled 会硬校验，
        // 之前写 123321 直接抛 IllegalArgumentException: not cleanly divisible by 144 or half that）。
        final int condAmount = 123_336; // = 72 * 1713，最接近 123321 的合法值
        FluidStack[] fluids = new FluidStack[] { entangled(CondensateType.Time, condAmount),
            entangled(CondensateType.Space, condAmount), entangled(CondensateType.Infinity, condAmount),
            entangled(CondensateType.TranscendentMetal, condAmount) };
        if (hasNullItem((Object[]) fluids)) {
            OTHTechnology.LOG.warn("[DeepSeiko] 控制器凝聚态配方缺少凝聚态流体，本次跳过");
            return;
        }
        // NANITE_TIERS 的长度必须等于输入格数（BEC 后端会校验，不等直接抛异常）
        NaniteTier[] nanites = new NaniteTier[] { NaniteTier.Gold, NaniteTier.Gold, NaniteTier.Gold, NaniteTier.Gold,
            NaniteTier.Carbon, NaniteTier.Carbon, NaniteTier.Silver, NaniteTier.Silver, NaniteTier.Transcendent,
            NaniteTier.Transcendent, NaniteTier.Transcendent, NaniteTier.Carbon, NaniteTier.Carbon,
            NaniteTier.Transcendent };
        try {
            GTValues.RA.stdBuilder()
                .itemInputs(inputs)
                .fluidInputs(fluids)
                .itemOutputs(OTHItemList.DeepSeiko.get(1))
                .metadata(NANITE_TIERS, nanites)
                .duration(20 * 300_000)
                .eut(RECIPE_UMV)
                .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
            OTHTechnology.LOG.info("[DeepSeiko] 控制器凝聚态（BEC）配方已注册：14 输入 / 4 凝聚态 / 6000000 tick / 凝聚态量 {}", condAmount);
        } catch (Throwable t) {
            // 单条配方炸掉不能影响后面 addUpgradeRecipes() 等步骤
            OTHTechnology.LOG.error("[DeepSeiko] 控制器凝聚态（BEC）配方注册失败", t);
        }
    }

    /**
     * 迪普西科结构方块（鲸脂外壳，{@code dsCasing} meta 0）×64 的研究站 / 原版装配线配方。
     *
     * <p>
     * 走 {@code TTRecipeAdder.addResearchableAssemblylineRecipe}：一次生成
     * ①研究站假配方（扫描米饭奇点 → 写入研究结果的数据棒）
     * ②正式装配线配方（要数据棒）
     * ③NEI 可视化配方（带数据棒格）。
     * </p>
     *
     * <p>
     * 研究参数：总计算量 16,777,216 ÷ 每秒 2,048 = 8,192 秒研究时长，研究功率 201,234,576 EU/t、1A；
     * 装配线本体 6000 tick（300 秒）、{@code TierEU.RECIPE_UMV}。
     * </p>
     *
     * <p>
     * 流体用 {@link #moltenOrFluid} 兜底：超导基材/时空这类材料不一定注册熔融态，直接 getMolten 可能为 null。
     * </p>
     */
    private void addDsCasingAssemblyLineRecipe() {
        ItemStack[] inputs = new ItemStack[] {
            new ItemStack(GregTechAPI.sBlockMachines, 64, MetaTileEntityIDs.HULL_UIV.ID),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 32017),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 48, 32041),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 32, 32045),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 6154),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 31174),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 64, 18174),
            GTModHandler.getModItem("gregtech", "gt.blockcasings5", 32, 13),
            GTModHandler.getModItem("gregtech", "gt.foundrycasings", 8, 9),
            GTModHandler.getModItem("bartworks", "BW_ExtraGlass", 32, 4),
            GTModHandler.getModItem("gregtech", "gt.blockframes", 48, 588),
            GTModHandler.getModItem("gregtech", "gt.blockframes", 32, 154),
            GTModHandler.getModItem("gregtech", "gt.sheetmetal", 64, 582) };
        if (hasNullItem((Object[]) inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] 鲸脂外壳装配线配方缺少物品，本次跳过");
            return;
        }

        FluidStack spacetime = moltenOrFluid(Materials.SpaceTime, 1000);
        FluidStack solder = MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(12_300);
        FluidStack superconductor = moltenOrFluid(Materials.SuperconductorUIVBase, 5000);
        FluidStack shiftedSuperfluid = Materials.DimensionallyShiftedSuperfluid.getFluid(100_000);
        if (hasNullItem((Object[]) new FluidStack[] { spacetime, solder, superconductor, shiftedSuperfluid })) {
            OTHTechnology.LOG.warn("[DeepSeiko] 鲸脂外壳装配线配方缺少流体，本次跳过");
            return;
        }

        ItemStack research = OTHItemList.RiceSingularity.get(1);
        if (hasNullItem((Object) research)) {
            OTHTechnology.LOG.warn("[DeepSeiko] 米饭奇点不存在，鲸脂外壳装配线配方跳过");
            return;
        }
        try {
            boolean ok = TTRecipeAdder.addResearchableAssemblylineRecipe(
                research, // 研究物品
                16_777_216, // 总计算量（研究耗时 = 总量 ÷ 每秒计算量 = 8192 秒）
                2048, // 每秒计算量
                201_234_576, // 研究 EU/t
                1, // 研究电流 A
                inputs,
                new FluidStack[] { spacetime, solder, superconductor, shiftedSuperfluid },
                new ItemStack(DeepSeikoBlocks.dsCasing, 64, DeepSeikoCasingMeta.BLUBBER_CASING),
                20 * 300, // 装配时长 tick
                (int) RECIPE_UMV);
            if (ok) {
                OTHTechnology.LOG.info("[DeepSeiko] 鲸脂外壳研究站/装配线配方已注册（16777216/2048 → 8192s 研究，装配 6000 tick）");
            } else {
                OTHTechnology.LOG.warn("[DeepSeiko] 鲸脂外壳研究站/装配线配方注册失败（TTRecipeAdder 返回 false）");
            }
        } catch (Throwable t) {
            OTHTechnology.LOG.error("[DeepSeiko] 鲸脂外壳研究站/装配线配方注册异常", t);
        }
    }

    /** 按流体注册名拿流体（GT 的 Materials 里没有 Hypogen，只能走名字）。 */
    private static FluidStack fluidByNames(int amount, String... names) {
        for (String name : names) {
            FluidStack stack = FluidRegistry.getFluidStack(name, amount);
            if (stack != null) return stack;
        }
        return null;
    }

    /** 优先拿熔融态，没有就退回普通流体（避免 getMolten 返回 null 让整条配方缺流体）。 */
    private static FluidStack moltenOrFluid(Materials material, int amount) {
        FluidStack molten = material.getMolten(amount);
        return molten != null ? molten : material.getFluid(amount);
    }

    /** 通用：拿 exotic 电路（矿辞优先，取不到就回退 GT 自带的 Exotic 主机）。 */
    private static ItemStack exoticCircuit(int amount) {
        ItemStack stack = GTOreDictUnificator.get("circuitExotic", amount);
        if (stack == null) {
            stack = ItemList.Circuit_ExoticMainframe.get(amount);
            OTHTechnology.LOG.warn("[DeepSeiko] 矿辞 circuitExotic 不存在，回退为 Circuit_ExoticMainframe ×{}", amount);
        }
        return stack;
    }

    /** GT++ 变异活体焊料（GT 的 Materials 里没有这个材料，只能走 GT++ 的 MaterialMisc）。 */
    private static FluidStack mutatedLivingSolder(int amount) {
        return MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(amount);
    }

    /**
     * 装配腔方块（dsCasing meta 4）的研究站 / 装配线配方。
     *
     * <p>
     * 研究物品是时空冷却核心：总计算量 32,777,216 ÷ 每秒 4,096 = 8,000 秒研究时长；
     * 研究功率 40,000,000,000 EU/t、1A（研究站要能扛 UMV 级电压）；装配线 6000 tick、RECIPE_UMV。
     * </p>
     */
    private void addAssemblyCasingAssemblyLineRecipe() {
        ItemStack[] inputs = new ItemStack[] {
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 1, 10),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32033),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 8, 21588),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 24588),
            new ItemStack(GregTechAPI.sBlockMachines, 16, 876), new ItemStack(GregTechAPI.sBlockMachines, 1, 2716),
            GTModHandler.getModItem("gtnhintergalactic", "dysonswarmparts", 8, 4),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32269),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 2, 6148),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 4, 6154),
            GTModHandler.getModItem("gregtech", "gt.sheetmetal", 2, 588),
            new ItemStack(DeepSeikoBlocks.dsCasing, 1, DeepSeikoCasingMeta.BLUBBER_CASING) };
        if (hasNullItem((Object[]) inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] 装配腔方块装配线配方缺少物品，本次跳过");
            return;
        }
        FluidStack solder = mutatedLivingSolder(12_000);
        FluidStack spacetime = moltenOrFluid(Materials.SpaceTime, 24_000);
        FluidStack hypogen = fluidByNames(30_000, "molten.hypogen", "hypogen", "liquid_hypogen");
        FluidStack superconductor = moltenOrFluid(Materials.SuperconductorUIVBase, 6000);
        if (hasNullItem((Object[]) new FluidStack[] { solder, spacetime, hypogen, superconductor })) {
            OTHTechnology.LOG.warn("[DeepSeiko] 装配腔方块装配线配方缺少流体，本次跳过");
            return;
        }
        ItemStack research = OTHItemList.SpacetimeCoolingCore.get(1);
        if (hasNullItem((Object) research)) {
            OTHTechnology.LOG.warn("[DeepSeiko] 时空冷却核心不存在，装配腔方块装配线配方跳过");
            return;
        }
        try {
            boolean ok = TTRecipeAdder.addResearchableAssemblylineRecipe(
                research,
                32_777_216,
                4096,
                // 4.0e10 已超过 int 上限：原写法是恒真三元并把值悄悄降级到 2.1e9，这里直接写明真实上限
                Integer.MAX_VALUE,
                1,
                inputs,
                new FluidStack[] { solder, spacetime, hypogen, superconductor },
                new ItemStack(DeepSeikoBlocks.dsCasing, 12, DeepSeikoCasingMeta.ASSEMBLY_CASING),
                20 * 300,
                (int) RECIPE_UMV);
            if (ok) {
                OTHTechnology.LOG.info("[DeepSeiko] 装配腔方块研究站/装配线配方已注册（32777216/4096 → 8000s）");
            } else {
                OTHTechnology.LOG.warn("[DeepSeiko] 装配腔方块研究站/装配线配方注册失败（TTRecipeAdder 返回 false）");
            }
        } catch (Throwable t) {
            OTHTechnology.LOG.error("[DeepSeiko] 装配腔方块研究站/装配线配方注册异常", t);
        }
    }

    /**
     * 三条「高级机壳」配方（用户提供）：
     * <ul>
     * <li>鲸脂外壳 ×8 → 墨黑外壳（meta 8）×8（组装机）</li>
     * <li>鲸脂外壳 ×8 → 浅蓝外壳（meta 7）×8（组装机）</li>
     * <li>7 种材料 → 鲸须格栅（meta 2）×8</li>
     * </ul>
     *
     * <p>
     * ⚠ 最后一条用户写的是粉碎机（macerator），但 GT 的 {@code maceratorRecipes} 是
     * {@code maxIO(1, 4, 0, 0)} —— 只有 1 个物品输入格，7 输入配方永远匹配不到，NEI 也画不下。
     * 这里改放进组装机（{@code maxIO(9, 1, 1, 0)}，7 输入 0 流体合法）。
     * </p>
     */
    private void addExoticCasingRecipes() {
        ItemStack blubber = new ItemStack(DeepSeikoBlocks.dsCasing, 8, DeepSeikoCasingMeta.BLUBBER_CASING);

        // 墨黑外壳（meta 8）
        ItemStack[] darkInputs = new ItemStack[] { blubber,
            GTModHandler.getModItem("gregtech", "gt.sheetmetal", 64, 368),
            GTModHandler.getModItem("gregtech", "gt.blockmetal9", 32, 4), exoticCircuit(8),
            new ItemStack(GregTechAPI.sBlockMachines, 2, 2086) };
        if (!hasNullItem((Object[]) darkInputs)) {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(darkInputs)
                    .fluidInputs(mutatedLivingSolder(16_000))
                    .itemOutputs(new ItemStack(DeepSeikoBlocks.dsCasing, 8, DeepSeikoCasingMeta.BLUBBER_DARK))
                    .duration(20 * 300)
                    .eut(TierEU.RECIPE_UIV)
                    .addTo(RecipeMaps.assemblerRecipes);
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] 墨黑外壳配方注册失败", t);
            }
        } else {
            OTHTechnology.LOG.warn("[DeepSeiko] 墨黑外壳配方缺少物品，本次跳过");
        }

        // 浅蓝外壳（meta 7）
        ItemStack[] lightInputs = new ItemStack[] { blubber, GTUtility.getIntegratedCircuit(5), // 编程电路 5
            GTModHandler.getModItem("gregtech", "gt.blockframes", 16, 174),
            GTModHandler.getModItem("Avaritia", "Resource", 8, 8), exoticCircuit(8),
            GTModHandler.getModItem("gregtech", "gt.sheetmetal", 64, 80) };
        if (!hasNullItem((Object[]) lightInputs)) {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(lightInputs)
                    .fluidInputs(mutatedLivingSolder(16_000))
                    .itemOutputs(new ItemStack(DeepSeikoBlocks.dsCasing, 8, DeepSeikoCasingMeta.BLUBBER_LIGHT))
                    .duration(20 * 300)
                    .eut(TierEU.RECIPE_UIV)
                    .addTo(RecipeMaps.assemblerRecipes);
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] 浅蓝外壳配方注册失败", t);
            }
        } else {
            OTHTechnology.LOG.warn("[DeepSeiko] 浅蓝外壳配方缺少物品，本次跳过");
        }

        // 鲸须格栅（meta 2）：原本用户写粉碎机，7 输入放不进 macerator，改组装机
        ItemStack[] grilleInputs = new ItemStack[] { GTUtility.getIntegratedCircuit(7), // 编程电路 7
            GTModHandler.getModItem("dreamcraft", "NeutroniumBars", 64),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32025),
            new ItemStack(GregTechAPI.sBlockMachines, 8, 5306),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 4, 32051),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 32, 22154),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32264),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 4581) };
        if (!hasNullItem((Object[]) grilleInputs)) {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(grilleInputs)
                    .itemOutputs(new ItemStack(DeepSeikoBlocks.dsCasing, 8, DeepSeikoCasingMeta.BALEEN_GRILLE))
                    .duration(20 * 120)
                    .eut(TierEU.RECIPE_UIV)
                    .addTo(RecipeMaps.assemblerRecipes);
                OTHTechnology.LOG.info("[DeepSeiko] 鲸须格栅配方已注册（原稿是粉碎机，粉碎机只有 1 输入格，已改组装机）");
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] 鲸须格栅配方注册失败", t);
            }
        } else {
            OTHTechnology.LOG.warn("[DeepSeiko] 鲸须格栅配方缺少物品，本次跳过");
        }
    }

    /**
     * 凝聚态适配舱（dsCasing meta 6）的凝聚态配方：16 个输入、4 种纠缠凝聚态、蜂群最高 SixPhasedCopper，
     * 时长 20 × 999999 tick。
     */
    private void addCondensateAdapterBecRecipe() {
        // 23529 = 本实例里迪普西科控制器的 MTE meta（IDs+9 运行时分配）⇒ 用 OTHItemList 更稳
        ItemStack[] inputs = new ItemStack[] { OTHItemList.DeepSeiko.get(1),
            new ItemStack(DeepSeikoBlocks.dsCasing, 16, DeepSeikoCasingMeta.BLUBBER_CASING),
            new ItemStack(DeepSeikoBlocks.dsCasing, 16, DeepSeikoCasingMeta.BLUBBER_LIGHT),
            new ItemStack(DeepSeikoBlocks.dsCasing, 16, DeepSeikoCasingMeta.BLUBBER_DARK),
            new ItemStack(DeepSeikoBlocks.dsCasing, 32, DeepSeikoCasingMeta.BALEEN_GRILLE),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32307),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32310),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32313),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 32316), exoticCircuit(64),
            GTModHandler.getModItem("gregtech", "gt.blockframes", 16, 588),
            GTModHandler.getModItem("miscutils", "gtplusplus.blockcasings.5", 16, 10),
            GTModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Singularity", 1),
            GTModHandler.getModItem("ae2fc", "fluid_storage.singularity", 1),
            GTModHandler.getModItem("dreamcraft", "HeavenlyPikoCircuit", 16),
            GTModHandler.getModItem("123Technology", "WhiteRice", 64) };
        if (hasNullItem((Object[]) inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] 凝聚态适配舱配方缺少物品，本次跳过");
            return;
        }
        // 凝聚态量必须是 72 的倍数（unit 144 的一半）
        FluidStack time = entangled(CondensateType.Time, 1_008_000);
        FluidStack space = entangled(CondensateType.Space, 1_008_000);
        FluidStack spacetime = entangled(CondensateType.SpaceTime, 3_024_000);
        FluidStack hypogen = entangled(CondensateType.Hypogen, 1_481_400);
        if (hasNullItem((Object[]) new FluidStack[] { time, space, spacetime, hypogen })) {
            OTHTechnology.LOG.warn("[DeepSeiko] 凝聚态适配舱配方缺少凝聚态流体，本次跳过");
            return;
        }
        NaniteTier[] nanites = new NaniteTier[] { NaniteTier.SixPhasedCopper, NaniteTier.Carbon, NaniteTier.Silver,
            NaniteTier.Gold, NaniteTier.Transcendent, NaniteTier.Gold, NaniteTier.Gold, NaniteTier.Gold,
            NaniteTier.Gold, NaniteTier.Carbon, NaniteTier.Transcendent, NaniteTier.Carbon, NaniteTier.Transcendent,
            NaniteTier.Transcendent, NaniteTier.Carbon, NaniteTier.Carbon };
        try {
            GTValues.RA.stdBuilder()
                .itemInputs(inputs)
                .fluidInputs(time, space, spacetime, hypogen)
                .itemOutputs(new ItemStack(DeepSeikoBlocks.dsCasing, 4, DeepSeikoCasingMeta.CONDENSATE_ADAPTER))
                .metadata(NANITE_TIERS, nanites)
                .duration(20 * 999_999)
                .eut(RECIPE_UMV)
                .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
            OTHTechnology.LOG.info("[DeepSeiko] 凝聚态适配舱凝聚态配方已注册（16 输入 / 4 凝聚态）");
        } catch (Throwable t) {
            OTHTechnology.LOG.error("[DeepSeiko] 凝聚态适配舱凝聚态配方注册失败", t);
        }
    }

    /**
     * 三个鳍方块：鲸脂外壳 + 白米饭 → 左鳍 / 右鳍 / 顶鳍（dsCasing meta 9 / 10 / 11），
     * 组装机配方，分别用编程电路 2 / 3 / 4 区分。
     */
    private void addFinCasingRecipes() {
        int[] circuits = { 2, 3, 4 };
        int[] metas = { DeepSeikoCasingMeta.FIN_TIME, DeepSeikoCasingMeta.FIN_SPACE, DeepSeikoCasingMeta.FIN_STAB };
        String[] names = { "左鳍（时间）", "右鳍（空间）", "顶鳍（稳定）" };
        for (int i = 0; i < metas.length; ++i) {
            ItemStack casing = new ItemStack(DeepSeikoBlocks.dsCasing, 1, DeepSeikoCasingMeta.BLUBBER_CASING);
            ItemStack rice = OTHItemList.WhiteRice.get(1);
            ItemStack output = new ItemStack(DeepSeikoBlocks.dsCasing, 1, metas[i]);
            if (hasNullItem(casing, rice, output)) {
                OTHTechnology.LOG.warn("[DeepSeiko] {} 配方缺少物品，本次跳过", names[i]);
                continue;
            }
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(casing, rice, GTUtility.getIntegratedCircuit(circuits[i]))
                    .itemOutputs(output)
                    .duration(20 * 30)
                    .eut(RECIPE_UMV)
                    .addTo(RecipeMaps.assemblerRecipes);
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] {} 配方注册失败", names[i], t);
            }
        }
        OTHTechnology.LOG.info("[DeepSeiko] 三个鳍方块配方已注册（左 2 / 右 3 / 顶 4 编程电路）");
    }

    /** 宇宙电路：矿辞 circuitCosmic 在整合包里没注册（日志实测），取不到就回退 GT 自带的 Cosmic 主机。 */
    private static ItemStack cosmicCircuit(int amount) {
        ItemStack stack = GTOreDictUnificator.get("circuitCosmic", amount);
        if (stack == null) {
            stack = ItemList.Circuit_CosmicMainframe.get(amount);
            OTHTechnology.LOG.warn("[DeepSeiko] 矿辞 circuitCosmic 不存在，回退为 Circuit_CosmicMainframe ×{}", amount);
        }
        return stack;
    }

    /**
     * 按凝聚态自身的 unit 取出最接近的合法量，并返回 entangled 流体。
     *
     * <p>
     * {@code CondensateType.getEntangled(amount)} 会硬校验 {@code amount % (unit / 2) == 0}，
     * 而不同凝聚态的 unit 不一样：大部分是 144（步长 72），
     * {@code QuarkGluonPlasma / BoundlessCosmicSolder / PhononMedium / DimensionallyShiftedSuperfluid}
     * 是 1000（步长 500）。写死 72 的倍数会在这些类型上炸：
     * {@code IllegalArgumentException: amount 7200 of condensate quarkgluonplasma is not cleanly divisible...}
     * </p>
     */
    private static FluidStack entangled(CondensateType type, int wanted) {
        int step = Math.max(1, type.getUnit() / 2);
        int down = (wanted / step) * step;
        int up = down + step;
        int amount = (wanted - down <= up - wanted) ? down : up;
        if (amount < step) amount = step;
        if (amount != wanted) {
            OTHTechnology.LOG.warn("[DeepSeiko] 凝聚态 {} 的量 {} 不是步长 {} 的倍数，已调整为 {}", type.name(), wanted, step, amount);
        }
        return type.getEntangled(amount);
    }

    /** 算力中心（ComputeCenter）按 meta 取物品：0 = D99 / 1 = D199 / 2 = D999。 */
    private static ItemStack computeCenter(int amount, int meta) {
        ItemStack base = OTHItemList.ComputeCenter.get(amount);
        if (base == null) return null;
        ItemStack copy = base.copy();
        copy.setItemDamage(meta);
        return copy;
    }

    /**
     * 三档算力中心的凝聚态配方（D99 / D199 / D999 各 ×8）。
     *
     * <p>
     * 三条都是 BEC {@code condensateAssemblingRecipes}；蜂群要求按档递增：
     * D99 最高 8 级、D199 最高 9 级、D999 最高 10 级（同一条配方内各输入格等级可以不同，门槛取最高档）；
     * 时长分别 20×999999 / 20×2999999 / 20×9999999 tick，电压 UXV / UXV / MAX。
     * </p>
     */
    private void addComputeCenterBecRecipes() {
        // ---------- D99（meta 0） ----------
        ItemStack[] d99Inputs = new ItemStack[] { cosmicCircuit(4),
            GTModHandler.getModItem("dreamcraft", "HeavenlyQuantumCircuit", 8),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 1, 32758),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 64, 28585),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 48, 28586),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 32, 28139),
            OTHItemList.SpacetimeCoolingCore.get(16) };
        if (hasNullItem((Object[]) d99Inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] D99 算力中心配方缺少物品，本次跳过");
        } else {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(d99Inputs)
                    .fluidInputs(
                        entangled(CondensateType.QuarkGluonPlasma, 7200),
                        entangled(CondensateType.BoundlessCosmicSolder, 14_400),
                        entangled(CondensateType.Universium, 3600))
                    .itemOutputs(computeCenter(8, 0))
                    .metadata(
                        NANITE_TIERS,
                        new NaniteTier[] { NaniteTier.Gold, NaniteTier.Transcendent, NaniteTier.SixPhasedCopper,
                            NaniteTier.WhiteDwarf, NaniteTier.BlackDwarf, NaniteTier.Universium,
                            NaniteTier.Universium })
                    .duration(20 * 999_999)
                    .eut(TierEU.RECIPE_UXV)
                    .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
                OTHTechnology.LOG.info("[DeepSeiko] D99 算力中心凝聚态配方已注册（7 输入 / 3 凝聚态）");
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] D99 算力中心配方注册失败", t);
            }
        }

        // ---------- D199（meta 1） ----------
        // 注意 gt.metaitem.03@32278 的语义：beta-3 是「普朗克尺度流形」，RC-2 起同一 ID 被复用为「卡拉比-丘流形」
        // （ItemList.Planck_Manifold 已重命名为 CalabiYauManifold）。物品本身两版都存在，功能等价，此处按 RC-2 命名。
        ItemStack[] d199Inputs = new ItemStack[] { GTModHandler.getModItem("dreamcraft", "PlanckCircuit", 8),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 16, 32278),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 32275),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 2, 32043),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 2, 32039),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 1, 32047), computeCenter(8, 0),
            OTHItemList.SpacetimeCoolingCore.get(32) };
        if (hasNullItem((Object[]) d199Inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] D199 算力中心配方缺少物品，本次跳过");
        } else {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(d199Inputs)
                    .fluidInputs(
                        entangled(CondensateType.BoundlessCosmicSolder, 720_000),
                        entangled(CondensateType.MagMatter, 144_000),
                        entangled(CondensateType.Eternity, 2_880_000))
                    .itemOutputs(computeCenter(8, 1))
                    .metadata(
                        NANITE_TIERS,
                        new NaniteTier[] { NaniteTier.Transcendent, NaniteTier.SixPhasedCopper, NaniteTier.WhiteDwarf,
                            NaniteTier.BlackDwarf, NaniteTier.Universium, NaniteTier.Eternity, NaniteTier.Eternity,
                            NaniteTier.Eternity })
                    .duration(20 * 2_999_999)
                    .eut(TierEU.RECIPE_UXV)
                    .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
                OTHTechnology.LOG.info("[DeepSeiko] D199 算力中心凝聚态配方已注册（8 输入 / 3 凝聚态）");
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] D199 算力中心配方注册失败", t);
            }
        }

        // ---------- D999（meta 2） ----------
        ItemStack[] d999Inputs = new ItemStack[] { GTModHandler.getModItem("dreamcraft", "PlanckCircuit", 64),
            GTModHandler.getModItem("tectech", "gt.godforgecasing", 32, 7),
            GTModHandler.getModItem("gregtech", "gt.metaitem.02", 16, 30192),
            GTModHandler.getModItem("appliedenergistics2", "item.ItemExtremeStorageCell.Universe", 1),
            GTModHandler.getModItem("ae2fc", "fluid_storage.Universe", 1),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 4143),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 32, 6141), computeCenter(16, 1),
            GTModHandler.getModItem("GoodGenerator", "componentAssemblylineCasing", 12, 12),
            GTModHandler.getModItem("appliedenergistics2", "tile.BlockSingularityCraftingStorage", 8),
            GTModHandler.getModItem("tectech", "gt.spacetime_compression_field_generator", 64, 8),
            GTModHandler.getModItem("tectech", "gt.time_acceleration_field_generator", 64, 8),
            GTModHandler.getModItem("tectech", "gt.stabilisation_field_generator", 64, 8),
            GTModHandler.getModItem("tectech", "item.tm.itemAstralArrayFabricator", 1),
            GTModHandler.getModItem("gregtech", "gt.metaitem.03", 64, 6583),
            GTModHandler.getModItem("gregtech", "gt.metaitem.01", 16, 32039) };
        if (hasNullItem((Object[]) d999Inputs)) {
            OTHTechnology.LOG.warn("[DeepSeiko] D999 算力中心配方缺少物品，本次跳过");
        } else {
            try {
                GTValues.RA.stdBuilder()
                    .itemInputs(d999Inputs)
                    .fluidInputs(
                        entangled(CondensateType.MHDCSM, 1_008_000),
                        entangled(CondensateType.MagMatter, 1_008_000),
                        entangled(CondensateType.Universium, 1_008_000),
                        entangled(CondensateType.Eternity, 1_008_000))
                    .itemOutputs(computeCenter(8, 2))
                    .metadata(
                        NANITE_TIERS,
                        new NaniteTier[] { NaniteTier.SixPhasedCopper, NaniteTier.WhiteDwarf, NaniteTier.BlackDwarf,
                            NaniteTier.Universium, NaniteTier.Universium, NaniteTier.Eternity, NaniteTier.Eternity,
                            NaniteTier.Eternity, NaniteTier.MagMatter, NaniteTier.MagMatter, NaniteTier.Eternity,
                            NaniteTier.Universium, NaniteTier.BlackDwarf, NaniteTier.WhiteDwarf, NaniteTier.Eternity,
                            NaniteTier.MagMatter })
                    .duration(20 * 9_999_999)
                    .eut(TierEU.RECIPE_MAX)
                    .addTo(TecTechRecipeMaps.condensateAssemblingRecipes);
                OTHTechnology.LOG.info("[DeepSeiko] D999 算力中心凝聚态配方已注册（16 输入 / 4 凝聚态）");
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] D999 算力中心配方注册失败", t);
            }
        }
    }

    private void addRiceRecipe() {
        if (!PamsHarvestCraft.isModLoaded()) return;
        ItemStack rice = getModItem(PamsHarvestCraft.ID, "riceItem", 1, 0);
        if (hasNullItem(rice)) {
            OTHTechnology.LOG.warn("Pam's rice not found, white rice recipe skipped");
            return;
        }
        GTModHandler.addCraftingRecipe(
            OTHItemList.WhiteRice.get(2),
            new Object[] { "RRR", " B ", 'R', rice.copy(), 'B', new ItemStack(Items.bowl, 1) });
    }

    /** 结构方块：装配机制作（鲸脂外壳本体改为研究站/装配线配方，见 addDsCasingAssemblyLineRecipe）。 */
    private void addCasingRecipes() {
        ItemStack[][] casings = {
            { stack(DeepSeikoCasingMeta.BLUBBER_FILLER, 4), stack(DeepSeikoCasingMeta.BLUBBER_CASING, 4),
                GTOreDictUnificator.get(OrePrefixes.plate, Materials.Lead, 8) },
            { stack(DeepSeikoCasingMeta.TAIL_FIN_FRAME, 4), stack(DeepSeikoCasingMeta.BLUBBER_CASING, 4),
                GTOreDictUnificator.get(OrePrefixes.frameGt, Materials.Titanium, 4) }, };
        for (ItemStack[] recipe : casings) {
            if (hasNullItem(recipe)) continue;
            GTValues.RA.stdBuilder()
                .itemInputs(tail(recipe))
                .fluidInputs(new FluidStack(FluidRegistry.getFluid("molten.polytetrafluoroethylene"), 288))
                .itemOutputs(recipe[0])
                .duration(100)
                .eut(TierEU.RECIPE_IV)
                .addTo(RecipeMaps.assemblerRecipes);
        }
    }

    /** 鲸 + 专用仓。控制器按设计 §1（手合彩蛋白米饭 ×128）。 */
    private void addMachineRecipes() {
        // 迪普西科之脑（控制器）：工作台配方已按用户要求删除，只保留 BEC 凝聚态配方
        // （见 addDeepSeikoBecRecipe：TecTechRecipeMaps.condensateAssemblingRecipes）
        // 纳米蜂收容仓
        GTModHandler.addCraftingRecipe(
            OTHItemList.DeepSeikoNaniteCell.get(1),
            new Object[] { "BBB", "BGB", "BBB", 'B', stack(DeepSeikoCasingMeta.BLUBBER_CASING, 1), 'G',
                ItemList.Field_Generator_UIV.get(1) });
    }

    /**
     * 「机体升级」三项升级的材料清单。
     * <p>
     * 这张配方图只用于 NEI 查询：机器真正扣材料发生在 {@code OTEDeepSeiko.tryUpgradeBec /
     * tryUpgradeNanite / tryUpgradeUltimate}（面板里的 6×4 虚拟暂存格）。
     * </p>
     * <p>
     * ⚠ 教训（2026-10-26 14:26 事故）：这里原先用「矿辞输入」`Object[]{"nanite", 64}` 来覆盖所有等级的纳米蜂群，
     * 结果 `GTOreDictUnificator.getOres("nanite")` 返回**空表**（它只认 GT 统一化过的条目），
     * `GTRecipeBuilder.itemInputs` 直接抛 `IllegalArgumentException: null in argument`；
     * 异常发生在 `completeInit` 里 ⇒ FML 记为致命错误 ⇒ **所有存档进不去、新建世界也失败**。
     * 现在改成「每种纳米蜂群各发一条具体物品配方」，并且每条都独立 try/catch，绝不让 NEI 展示用的配方拖垮初始化。
     * </p>
     */
    private void addUpgradeRecipes() {
        ItemStack[] req = OTEDeepSeiko.getUpgradeRequirements();
        ItemStack bec = req[0], ultCasing = req[2], ultMagmatter = req[3], ultStargate = req[4];
        int added = 0;

        if (!hasNullItem((Object) bec)) {
            added += addUpgradeRecipeSafely("凝聚态解锁", bec, "机体升级 → 解锁凝聚态", "消耗 4×凝聚态适配舱");
        }

        // 蜂群逐级：T(k-1)→T(k) 必须用**第 k 级**的纳米蜂群（用户明确要求，不能拿一级碳蜂群连点到 10 级）
        ItemStack[] tierRefs = OTEDeepSeiko.getNaniteRefsByTier();
        int variants = 0;
        for (int tier = 1; tier <= tierRefs.length; tier++) {
            ItemStack ref = tierRefs[tier - 1];
            if (hasNullItem((Object) ref)) continue;
            ItemStack sixtyFour = ref.copy();
            sixtyFour.stackSize = OTEDeepSeiko.NANITE_PER_TIER;
            variants += addUpgradeRecipeSafely(
                "蜂群 T" + (tier - 1) + "→T" + tier,
                sixtyFour,
                "机体升级 → 蜂群 T" + (tier - 1) + "→T" + tier,
                "需要 " + OTEDeepSeiko.NANITE_PER_TIER + " 个第 " + tier + " 级蜂群");
        }
        if (variants == 0) {
            OTHTechnology.LOG.warn("[DeepSeiko] 没能取到任何等级的纳米蜂群，蜂群升级的 NEI 配方已跳过");
        }
        added += variants;

        if (!hasNullItem(ultCasing, ultMagmatter, ultStargate)) {
            try {
                GTValues.RA.stdBuilder()
                    // itemInputsUnsafe：普通 itemInputs 会被 GTOreDictUnificator 把 stackSize 压到 64，
                    // 1234 个磁蜂群在 NEI 里就会显示成 64（用户反馈过）
                    .itemInputsUnsafe(ultCasing, ultMagmatter, ultStargate)
                    .duration(1)
                    .eut(1)
                    .fake()
                    .setNEIDesc(
                        "机体升级 → 终极任务（总算速 ×2）",
                        "UXV 外壳 " + OTEDeepSeiko.getUltCasingCount()
                            + " + 磁蜂群 "
                            + OTEDeepSeiko.getUltMagmatterCount()
                            + " + 星门板 "
                            + OTEDeepSeiko.getUltStargateCount())
                    .addTo(Recipemaps.DeepSeikoUpgrade);
                added++;
            } catch (Throwable t) {
                OTHTechnology.LOG.error("[DeepSeiko] 终极升级 NEI 配方生成失败，已跳过（不影响机器功能）", t);
            }
        }

        OTHTechnology.LOG.info("[DeepSeiko] 机体升级 NEI 配方已写入 {} 条", added);
    }

    /** 单条升级展示配方：任何异常只记日志，绝不向外抛（否则会拖垮 completeInit） */
    private int addUpgradeRecipeSafely(String label, ItemStack input, String... neiDesc) {
        try {
            GTValues.RA.stdBuilder()
                .itemInputsUnsafe(input)
                .duration(1)
                .eut(1)
                .fake()
                .setNEIDesc(neiDesc)
                .addTo(Recipemaps.DeepSeikoUpgrade);
            return 1;
        } catch (Throwable t) {
            OTHTechnology.LOG.error("[DeepSeiko] 机体升级 NEI 配方生成失败（" + label + "），已跳过", t);
            return 0;
        }
    }

    private static ItemStack stack(int casingMeta, int amount) {
        return new ItemStack(DeepSeikoBlocks.dsCasing, amount, casingMeta);
    }

    private static ItemStack[] tail(ItemStack[] all) {
        ItemStack[] rest = new ItemStack[all.length - 1];
        System.arraycopy(all, 1, rest, 0, rest.length);
        return rest;
    }
}
