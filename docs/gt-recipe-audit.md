# 123Technology：GT 配方硬编码溯源

**结论：多数组件可改用原 Mod 的命名入口，但不能按显示名做全局替换。** GT 的离散元物品一般用 `ItemList`；GT / GT++ / BartWorks 的自动生成材料件应分别走各自材料系统；GoodGenerator、TecTech、Lanthanides 等还有各自的注册表或公开工厂。部分历史 meta、可选 Mod 注册键和动态导入则应保留，或先解决兼容问题。

本报告及 CSV 是**迁移前的静态快照**，其中的调用行号和统计不是当前源码的剩余硬编码数量。版本基线是项目配置的 [GTNH 2.9.0](<../dependencies.gradle#L2-L13>) 和开发客户端实际载入的 [GT5-Unofficial 5.09.54.265](<../run/client/logs/fml-client-latest.log#L9474-L9490>)。优先采用该版本 `sources` / `dev` 构件中的注册代码；历史运行日志用于佐证实际注册键或当次运行行为，不等于重新启动了游戏测试。

## 已实施的物品迁移

- 在 `bd_new` 重放物品迁移并适配本分支，共更新 841 处物品调用，涉及 22 个配方注册源文件。其中 14 处位于本分支新增配方，D199 则保留本分支改用蜜蜂材料的修正。优先使用 GT `ItemList`、`Casings`、`MetaTileEntityIDs`、本项目 `OTHItemList`、BW `Werkstoff`，以及原 Mod 的公开注册对象；有些共享 `gt.blockmachines` 的模块只把数字换成已证实的命名 ID，保留原取栈路径。
- [RecipeStacks](<../src/main/java/com/newmaa/othtech/utils/RecipeStacks.java>) 从原注册入口构造精确的栈，不改成矿辞统一物品；[ExternalRecipeItems](<../src/main/java/com/newmaa/othtech/utils/ExternalRecipeItems.java>) 封装 Ender IO、AE2、AE2FC 的可选加载路径。调用点的 NBT 设置、零数量催化剂和额外的超栈数量设置不变。
- 保留 `gt.blockmachines@860/@25011`、Chlorine 原矿／矿块、GT++ 历史化学物品 `@30`，以及无法验证等价的动态调用。此轮不改流体或矿辞。`gradlew.bat check --offline` 已通过；未启动游戏对比两套注册结果。

## 已实施的流体迁移

- 在 24 个配方源文件中更新 426 处流体引用：424 处直接 Forge 字面键调用（含 `bd_new` 新增的 Oganesson）和 2 处回退候选键表达式。GT 的单一绑定使用 `Materials` 对应的液体、气体、熔融物或等离子体入口；同名竞争的 `oil`、`bioethanol`、原版 `water` 以及原本按 Fluid ID 构造的调用，仍按名字通过 Forge 当前默认映射查找。
- [RecipeFluids](<../src/main/java/com/newmaa/othtech/utils/RecipeFluids.java>) 按 GT++、BartWorks 和 IC2 的原注册规则推导精确键，不把 GT++ 生成阶段选中的别名当成原键；保留可选模块缺失分支和原 Forge 查找重载。保留 `mud.red.slurry`、`cryotheum`、`pineoil` 等无法证明可用公开字段精确替换的键，以及既有回退顺序。
- 静态对照确认流体槽数、数量表达式、物品输入输出、能耗、时长与注册目标不变，`gradlew.bat check --offline` 已通过。未启动整合包重跑流体注册。

## 从哪里查

| 清单 | 内容 |
| --- | --- |
| [GT5U 与 BartWorks](<gt-recipe-audit-gt-bw.md>) | 原 mod 逐键溯源：`ItemList` / MTE / `Casings`、`Materials` + `OrePrefixes`、`Werkstoff`、BW 封装件及玻璃；含源码证据和调用行号。 |
| [GT++、GoodGenerator、TecTech、Intergalactic、Lanthanides、OTH](<gt-recipe-audit-addons.md>) | 模块自有 `ItemList`、材料池、公开物品和工厂，以及本项目可直接使用的字段。 |
| [其他外部 Mod](<gt-recipe-audit-external.md>) | GTNH CoreMod、OpenComputers、AE2、Ender IO、IC2、TST、太空/魔法/奇点等；区分已证实入口与仍需保留动态查找的项。 |
| [物品调用点 CSV](<gt-recipe-audit-data/calls.csv>) | 按源文件/行号保留调用实参；可按注册名、meta、Mod ID 过滤。 |
| [Meta 反查 CSV](<gt-recipe-audit-data/meta-reverse.csv>) | 按当前依赖的原始分配规则反查显式 meta：763 条数值调用，475 个唯一数值键，另保留 3 处包装器参数转发。含公式／原分配槽、入口、异常和证据；不把省略 meta 的调用假定为 0。 |
| [流体和矿辞 CSV](<gt-recipe-audit-data/fluid-ore.csv>) | 字面键索引，作为物品审计的补充；不声称每个流体都已验证同一 `Fluid` 对象。 |
| [入口扫描 CSV](<gt-recipe-audit-data/entrypoints.csv>) | 配方包文件与包外配方 API 命中；命中数不是配方数（一个配方可同时命中 builder 和 add）。 |

## 审计范围

- 自 [RecipeLoader](<../src/main/java/com/newmaa/othtech/recipe/RecipeLoader.java#L19-L52>) 向下检查 27 个固定配方池、条件加载的 `RecipesDE`、加载器自身的注册和包外蒸汽九合一。也覆盖 `TTRecipeAdder`、`GTModHandler.addCraftingRecipe`、[项目 RecipeBuilder](<../src/main/java/com/newmaa/othtech/utils/RecipeBuilder.java#L101-L121>) 及复制/移植配方入口，不只搜 `stdBuilder()`。
- 包外实际注册：[蜂梳 GT 配方](<../src/main/java/com/newmaa/othtech/common/beeyonds/OTHItemCombs.java#L107-L284>)、[凝聚态发生器](<../src/main/java/com/newmaa/othtech/common/materials/OTHEntangled.java#L147-L159>)、[计算机机架假配方](<../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L252-L350>)。假配方和 [Void Nexus 升级预览](<../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L153-L196>) 与实际生产分开看；机器运行逻辑若共用相同物品键，只记录相关位置，不算新 GT 配方。原版对象 `Items` / `Blocks` 已是公开命名入口，不需另造 `ItemList`；数字子类型在索引中可单独查看。
- 运行期生成的配方不等于项目里手写了一条原 Mod 配方：[NASA](<../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L206-L240>) 读取已有装配机/装配线条目并用动态图纸键，另一入口 [导入火箭工作台配方](<../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L286-L319>)；[DeepSeiko](<../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L818-L834>) 回填视觉装配线；[MegaCAL](<../src/main/java/com/newmaa/othtech/recipe/RecipesMCATransformer.java#L17-L47>) 复制 BW 电路装配线，[EXH](<../src/main/java/com/newmaa/othtech/recipe/RecipesEXH.java#L25-L94>) 从真空冷冻机配方派生；[Abstract Sanctum](<../src/main/java/com/newmaa/othtech/recipe/RecipesAbstractSanctum.java#L616-L639>) 的 Gadomancy/Tinkers 查找是输出黑名单，不是固定原料。索引统计调用点，不把每次循环、运行时注册条目或上游原料展开作项目硬编码。
- 索引共 **1,018 条物品相关调用点**（含预览、筛选、动态助手和 5 条关联机器逻辑），按静态表达式去重得到 **615 个键**；不是 615 种已确认注册物品。另有 **474 条流体/矿辞字面键调用**。词法索引去除注释，展开已知项目包装器（`getGM`、IC2、预览的 registry/子类型助手等）。动态表达式和默认栈保留原实参。`默认` 不盲目写成 `0`，不同路径可从 Forge 取得带 meta/NBT 的栈。已有源码别名或代码分支按报告备注判断，不凭 CSV 一键替换。

## 优先处理顺序

1. **先修实际查找失败，再清理风格。** 已有运行日志显示 [DeepSeiko 冷却核心](<../run/client/logs/fml-client-latest.log#L24357-L24447>) 和 [Big Fat Fish 控制器](<../run/client/logs/fml-client-latest.log#L24448-L24492>) 因输入未解析到而跳过。`GraviSuite:itemSimpleItem`、`computronics:computronics.ocSpecialParts` 等需要核对依赖是否存在、实际版本键与调用时机；日志不能单独证明完整整合包也有同样问题。`firstNonNull(getModItem(...), getModItem(...))` 两个调用实参会先求值，`hard_ice` 回退的报错本身不代表 `hardIce` 也失败。
2. **优先替换有直接注册证据的离散项，按配方文件分批。** 电机、传感器、SMD、切片模具等用 GT `ItemList`；GT++ `MU-metaitem.01:32105` 用 `GregtechItemList.Laser_Lens_Special`；TecTech 星阵用 `CustomItemList.astralArrayFabricator`；GoodGenerator 紧凑聚变线圈用 `ItemRefer`。以本次索引看，主要集中在 [RecipesSunFactoryEnqing](<../src/main/java/com/newmaa/othtech/recipe/RecipesSunFactoryEnqing.java>)、[RecipesMain](<../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java>)、[RecipesDeepSeiko](<../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java>)、[RecipesTangshanSteelFactory](<../src/main/java/com/newmaa/othtech/recipe/RecipesTangshanSteelFactory.java>) 等文件。
3. **材料件用原 Mod 的材料池。** GT `gt.metaitem.01:22979` 对应 `OrePrefixes.plateDense` + `Materials.RadoxPolymer`；BW 锭/粉/透镜优先用 `Werkstoff.get(OrePrefixes.*, n)`，不要把 Werkstoff ID 套入 GT `Materials`；GT++ `itemIngotWatertightSteel` 对应 `MaterialsAlloy.AQUATIC_STEEL.getIngot(n)`。换用矿辞前区分原配方是具体物品，还是本来就接受矿辞；尤其输出不能无意换成另一 Mod 的统一代表。
4. **保留兼容项，除非另行修改设计。** `miscutils:item.BasicGenericChemItem:30` 是项目当前消费/生成的历史 meta，未找到上游命名子项；`RecipesOTHOre` 的 Chlorine ore/rawOre 也不是普通 GT 材料生成入口可重建的物品。本地或可选 Mod 的 ID、NBT 工艺堆栈、入口只能动态调用的旧版本等，应当记录意图并保留原行为；不要为降低硬编码率发明不存在的上游 API。
5. **如后续实施，先核对 Item、meta、NBT、数量，再核对注册结果。** 保持 `0` 数量催化剂、超栈输入输出、研究物品、特殊槽、槽位重复、通配 meta、可选 Mod 空值路径。`ItemList.get` 可能做矿辞统一；本版 `ICasing.toStack(int)` 还忽略传入数量（详见 GT/BW 表）。先在两种环境验证：完整依赖、可选 Mod 缺失。NEI 预览和实际机器消耗需要使用同样的定义时也要一起核对。

## 附记：流体与矿辞边界

- 可独立迁移的例子：`molten.spacetime` → `Materials.SpaceTime.getMolten(n)`；`molten.shirabon` → 已在 [RecipesNaquadah](<../src/main/java/com/newmaa/othtech/recipe/RecipesNaquadah.java#L70-L102>) 使用的 `GGMaterial.shirabon.getMolten(n)`；`molten.mutatedlivingsolder` → 已在 [RecipesDeepSeiko](<../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L768-L770>) 使用的 `MaterialMisc.MUTATED_LIVING_SOLDER.getFluidStack(n)`；`exciteddtsc` / `dimensionallytranscendentresidue` 是 [GT 流体注册器](<https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gregtech/loaders/preload/LoaderGTBlockFluid.java#L1525-L1578>) 绑定的 `Materials.ExcitedDTSC.getFluid(n)` / `Materials.DTR.getFluid(n)`。已有 fallback 的调用仍保留 fallback。
- `unknowwater` 是 GT 定义的内部拼写（见 [UndergroundFluidNames](<https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gregtech/api/enums/UndergroundFluidNames.java#L33>)），不是应直接改成 `unknownwater` 的笔误。`circuitExotic`、`circuitCosmic`、`plateSuperdenseCallistoIce` 等属于矿辞能力表达；前两者在 [DeepSeiko](<../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L757-L781>) 还有 ItemList fallback，不应把两种语义合并成固定主机。
