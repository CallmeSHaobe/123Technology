# GT 配方硬编码溯源：GT++、GoodGenerator、TecTech、Intergalactic、Lanthanides、项目自身

本表针对 GTNH 2.9.0 当前解析的 GT5-Unofficial **5.09.54.265**。前五个 Mod 在该 GT5U 构件内；链接到该版本原源码。`n` 是原数量；若现有代码先取 1 再 `setStackSize`/`copyAmountUnsafe`，保留其路径及超栈数量，不把它简化成普通 `get(n)`。默认 meta 不是全局规则：不传 meta 走 Forge 默认栈，显式指定才覆盖；以下普通非子类型物品默认是 meta 0。

## GT++ (`miscutils`)

| 现有注册名 / Meta | 原 Mod 入口 | 注册证据 / 状态 |
| --- | --- | --- |
| `MU-metaitem.01:32100` | `GregtechItemList.Compressed_Fusion_Reactor.get(n)` | GT++ 自有 ItemList；`MetaGeneratedItemIDs` 100 |
| `MU-metaitem.01:32105` | `GregtechItemList.Laser_Lens_Special.get(n)` | GT++ 自有 ItemList；ID 105，显示名 Quantum Anomaly |
| `MU-metaitem.01:32140` | `GregtechItemList.Laser_Lens_WoodsGlass.get(n)` | GT++ 自有 ItemList；ID 140 |
| `gtplusplus.blockcasings.3:11` | `GregtechItemList.Casing_Adv_BlastFurnace.get(n)` | [CasingBlocks3](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/xmod/gregtech/common/blocks/GregtechMetaCasingBlocks3.java#L71) |
| `gtplusplus.blockcasings.5:10 / 14` | `GregtechItemList.SpaceTimeContinuumRipper.get(n)` / `SpaceTimeBendingCore.get(n)` | [CasingBlocks5](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/xmod/gregtech/common/blocks/GregtechMetaCasingBlocks5.java#L43-L47)，预览与生产都有 meta 10 |
| `gtplusplus.blockcasings.6:0 / 1` | `GregtechItemList.Casing_Fusion_External2.get(n)` / `Casing_Fusion_Internal2.get(n)` | [CasingBlocks6](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/xmod/gregtech/common/blocks/GregtechMetaCasingBlocks6.java#L41-L46)；[VoidNexus:126](<../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexus.java#L126>) 直接用了 `ModBlocks.blockCasings6Misc` 的 meta 0。 |
| `item.BasicAgrichemItem:13` | `GregtechItemList.EmptyCatalystCarrier.get(n)` | [ModItems](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/item/ModItems.java#L207-L210)，亦注册 `catalystEmpty` |
| `item.BasicGenericChemItem:12` | `GregtechItemList.PotassiumHydroxide.get(n)` | [ModItems](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/item/ModItems.java#L247-L254) |
| `item.BasicGenericChemItem:30` | **无已验证等价入口；保留并注明兼容用途** | 当前 [ModItems](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/item/ModItems.java#L263-L297) 的 ItemList 跳过 29–32，[基类](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/item/chemistry/general/ItemGenericChemBase.java#L30-L58) 仍注册了底层物品；不能把底层物品存在当成 meta 30 对应有效上游催化剂。项目在没有 GTNN 时以该 meta 产出，并在 MegaQFT 以数量 0 使用；不能换成任意现有催化剂。 |
| `milledMonazite` | `GregtechItemList.MilledMonazite.get(n)` | [ModItems](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/item/ModItems.java#L299-L310) |
| `particleBase` `0／默认, 7, 8, 15, 16, 17, 18, 19, 20, 21, 23, 24` | `Particle.getBaseParticle(Particle.<常量>, n)`；按同序 `GRAVITON, ELECTRON, ELECTRON_NEUTRINO, Z_BOSON, W_BOSON, HIGGS_BOSON, PROTON, NEUTRON, LAMBDA, OMEGA, ETA_MESON, UNKNOWN` | 非 ItemList；[StandardBaseParticles](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/item/chemistry/StandardBaseParticles.java#L28-L46) 定义 meta，[Particle](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/material/Particle.java#L140-L156) 本身返回同种栈，支持传原数量。 |

`MU-metaitem.01` 的依据是 [MetaGeneratedItemIDs](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/xmod/gregtech/common/items/MetaGeneratedItemIDs.java#L22-L29) 的数值与 [实际 ItemList 写入](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/xmod/gregtech/common/items/MetaGeneratedGregtechItems.java#L249-L285)。数量 0 的见证/催化剂要保持为 0（比如蜂梳配方）；不是空栈。

### GT++ 动态材料件

这些不是普通 ItemList 项；用 GT++ 的 `gtPlusPlus.core.material.Material`。注册名由构造器拼前缀并登记生成件，并加入该材料组件表；见 [BaseItemComponent](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/item/base/BaseItemComponent.java#L55-L72) 和 [Material#getComponentByPrefix](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/material/Material.java#L820-L861)。这个 getter 在组件缺失时有 GT/矿辞回退，**不能**仅凭方法返回非空认定和原注册物一致；换输出前要验证实际 Item、meta 和 NBT。

| 硬编码名称后缀 | 材料字段 | 取件入口（按现有形态） |
| --- | --- | --- |
| `EglinSteel`, `IncoloyDS`, `Incoloy020`, `IncoloyMA956`, `HastelloyW`, `HastelloyX` | `MaterialsAlloy.EGLIN_STEEL`, `INCOLOY_DS`, `INCOLOY_020`, `INCOLOY_MA956`, `HASTELLOY_W`, `HASTELLOY_X` | `.getIngot(n)` |
| `MaragingSteel250`, `MaragingSteel300`, `MaragingSteel350`, `WatertightSteel`, `Zeron100` | `MaterialsAlloy.MARAGING250`, `MARAGING300`, `MARAGING350`, `AQUATIC_STEEL`, `ZERON_100` | `.getIngot(n)` |
| `Titansteel`, `Octiron`, `Arcanite`, `Quantum`, `AbyssalAlloy`, `BlackTitanium` | `MaterialsAlloy.TITANSTEEL`, `OCTIRON`, `ARCANITE`, `QUANTUM`, `ABYSSAL`, `BLACK_TITANIUM` | `.getIngot(n)` |
| `Pikyonium64B`, `CinobiteA243`, `LafiumCompound`, `ArceusAlloy2B`, `Botmium` | `MaterialsAlloy.PIKYONIUM`, `CINOBITE`, `LAFIUM`, `TRINIUM_REINFORCED_STEEL`, `BOTMIUM` | `.getIngot(n)` / `.getDust(n)` |
| `Laurenium` | `MaterialsAlloy.LAURENIUM` | `.getScrew(n)` |
| `Rhenium`, `Thallium`, `Selenium`, `Germanium`, `Plutonium238`, `Uranium233` | `MaterialsElements.getInstance().RHENIUM`, `THALLIUM`, `SELENIUM`, `GERMANIUM`, `PLUTONIUM238`, `URANIUM233` | `.getDust(n)` |
| `CelestialTungsten`, `Rhugnor`, `Dragonblood` | `MaterialsElements.STANDALONE.CELESTIAL_TUNGSTEN`, `RHUGNOR`, `DRAGON_METAL` | `.getDust(n)` |
| `AstralTitanium` | `MaterialsElements.STANDALONE.ASTRAL_TITANIUM` | `.getDust(n)`；`blockBlockAstralTitanium` 用 `.getBlock(n)`，不是无参 `getBlock()` |
| `Hypogen` | `MaterialsElements.STANDALONE.HYPOGEN` | `.getDust(n)`、`.getIngot(n)`、`.getPlateDouble(n)` |
| `ChromaticGlass` | `MaterialsElements.STANDALONE.CHRONOMATIC_GLASS`（常量拼法如此） | `.getDust(n)` / `.getPlateDense(n)`；不要拿 `plateSuperdense` 替换。 |

字段来源：[MaterialsAlloy](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/material/MaterialsAlloy.java)、[MaterialsElements](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/material/MaterialsElements.java)，生成入口为 [ModItems](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtPlusPlus/core/item/ModItems.java#L433-L584)。

## GoodGenerator

| 硬编码物品 | 入口 | 说明 |
| --- | --- | --- |
| `circuitWrap:12 / 13 / 14` | `GTOreDictUnificator.get(OrePrefixes.wrapCircuit, Materials.UMV / UXV / MAX, n)` | [Loaders](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/goodgenerator/loader/Loaders.java#L498-L512) 确认矿辞注册。如需锁定原 Item/meta 而非统一代表，采用 `new ItemStack(Loaders.circuitWrap, n, meta)`。未见这三档的独立 `ItemRefer`。 |
| `compactFusionCoil:1 / 2 / 3 / 4` | `ItemRefer.Compact_Fusion_Coil_T1 / T2 / T3 / T4` | 配合 `.get(n)`，不是 GT 通用 `ItemList` |
| `componentAssemblylineCasing:10 / 12 / 13` | `ItemRefer.Compassline_Casing_UIV / UXV / MAX` | meta 从 LV 起算，与 `circuitWrap` 从 ULV 起算不同 |
| `huiCircuit`（默认） | `ItemRefer.HiC_T1.get(n)` | meta 0 |
| `FRF_Casings`（默认） | `ItemRefer.Naquadah_Fuel_Refinery_Casing.get(n)` | meta 0 |
| `advancedRadiationProtectionPlate` | `ItemRefer.Advanced_Radiation_Protection_Plate.get(n)` | 同一公开物品。 |

来源为 [ItemRefer](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/goodgenerator/util/ItemRefer.java#L98-L204)；核对了精确 meta 而非显示名。

## TecTech、Intergalactic、Lanthanides

| Item ID / Meta | 原 Mod 入口 | 证据 |
| --- | --- | --- |
| `tectech:gt.blockcasingsTT:0／默认, 9` | `CustomItemList.eM_Power.get(n)` / `CustomItemList.eM_Spacetime.get(n)` | [BlockGTCasingsTT](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/tectech/thing/casing/BlockGTCasingsTT.java#L30-L50) |
| `tectech:gt.godforgecasing:7` | `CustomItemList.Godforge_GravitonFlowModulatorTier3.get(n)` | [BlockGodforgeCasings](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/tectech/thing/casing/BlockGodforgeCasings.java#L32-L45) |
| `tectech:gt.spacetime_compression_field_generator:2 / 5 / 8` | `CustomItemList.SpacetimeCompressionFieldGeneratorTier2 / Tier5 / Tier8` 再 `.get(n)` | [SpacetimeCompressionFieldCasing](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/tectech/thing/casing/SpacetimeCompressionFieldCasing.java#L44-L52) |
| `tectech:gt.time_acceleration_field_generator:8` / `gt.stabilisation_field_generator:8` | `CustomItemList.TimeAccelerationFieldGeneratorTier8.get(n)` / `StabilisationFieldGeneratorTier8.get(n)` | [TimeAccelerationFieldCasing](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/tectech/thing/casing/TimeAccelerationFieldCasing.java#L52)、[StabilisationFieldCasing](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/tectech/thing/casing/StabilisationFieldCasing.java#L52) |
| `tectech:item.tm.itemAstralArrayFabricator` | `CustomItemList.astralArrayFabricator.get(n)` | [ItemAstralArrayFabricator](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/tectech/thing/item/ItemAstralArrayFabricator.java#L42-L44)，实际升级及机器判断也可复用 |
| TecTech `sBlockCasingsBA0:12`（预览） | `CustomItemList.EOH_Infinite_Energy_Casing.get(n)` | [BlockGTCasingsBA0](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/tectech/thing/casing/BlockGTCasingsBA0.java#L60-L62) |
| `gtnhintergalactic:dysonswarmparts:4` | `ItemList.DysonSwarmDeploymentUnitMagnet.get(n)` | 这是方块，[BlockCasingDysonSwarm](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtnhintergalactic/block/BlockCasingDysonSwarm.java#L33-L39) |
| `gtnhintergalactic:item.DysonSwarmParts:0／默认, 3` | `ItemList.DysonSwarmModule.get(n)` / `ItemList.UHTResistantMesh.get(n)` | 这是物品，[ItemDysonSwarmParts](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtnhintergalactic/item/ItemDysonSwarmParts.java#L20-L34) |
| `gtnhintergalactic:item.MiningDrone:0` | `ItemList.MiningDroneLV.get(n)` | [ItemMiningDrones](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtnhintergalactic/item/ItemMiningDrones.java#L26-L48) |
| `gtnhlanth:item.photomask.{cpu,nand,nor,ram,ilc,soc,ssoc,asoc,ncpu,qbit}` | `new ItemStack(LanthItemList.maskMap.get(MaskList.<对应大写枚举>), 0)` | 非 GT `ItemList`；[LanthItemList 的循环注册](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtnhlanth/common/register/LanthItemList.java#L164-L185) 与 [MaskList](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/gtnhlanth/common/item/MaskList.java#L26-L70)。20 处全部是数量 0，不能改为 1 或只取矿辞字符串。 |

## KekzTech（GT5U 内置）

[RecipesMAXs:284–285](<../src/main/java/com/newmaa/othtech/recipe/RecipesMAXs.java#L284-L285>) 的 `kekztech:kekztech_lapotronicenergyunit_block` 默认及 meta 10，均可用 `new ItemStack(kekztech.common.Blocks.lscLapotronicEnergyUnit, n, 0 / 10)` 保留同一块；代入对应的单个 meta。未找到独立 ItemList。这是 [注册器](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/kekztech/common/blocks/BlockLapotronicEnergyUnit.java#L82-L102) 返回给 [公开字段](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/kekztech/common/Blocks.java#L55) 的同一个对象；上游 [Assembler](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/kekztech/common/recipeLoaders/Assembler.java#L129-L137) 明确用 meta 0。

## KubaTech（GT5U 内置）

[VoidNexus:158](<../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexus.java#L158>) 的 `new ItemStack(BlockLoader.defcCasingBlock, 1, 12)` 已由 [BlockDEFCCasing](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/kubatech/loaders/block/defc/BlockDEFCCasing.java#L35) 登记为 `kubatech.api.enums.ItemList.DEFCCasingT5`；上游 [DEFCRecipes](https://github.com/GTNewHorizons/GT5-Unofficial/blob/5.09.54.265/src/main/java/kubatech/loaders/DEFCRecipes.java#L122) 也用该命名栈。

## 项目自己注册的物品／方块

| 硬编码 | 更直接的入口 | 原注册位置 |
| --- | --- | --- |
| `123technology:WhiteRice` | `OTHItemList.WhiteRice.get(n)` | [ItemLoader:123](../src/main/java/com/newmaa/othtech/common/item/ItemLoader.java#L123) |
| `antimoniaDirtBlock` / `antimoniaGrassBlock` / `antimoniaStoneBlock` | `new ItemStack(AntimoniaBlocks.antimoniaBlockDirt / antimoniaBlockGrass / antimoniaBlockStone, n)` | [AntimoniaBlocks:7–21](../src/main/java/com/newmaa/othtech/common/blocks/antimonia/AntimoniaBlocks.java#L7-L21)，当前为公开字段，没有必要另建 ItemList |
| `modelAsuka` / `modelTallHat` | `new ItemStack(ItemAndBlockHandler.MODEL_ASUKA / TALL_HAT, n)` | [ItemAndBlockHandler:11–13](../src/main/java/com/newmaa/othtech/common/ItemAndBlockHandler.java#L11-L13)；确保原注册阶段先于配方。 |
| `DeepSeikoBlocks.dsCasing` 的数字 meta／`OTHItemList.ComputeCenter` 后 `.setItemDamage(meta)` | 原对象入口已经明确；如果重构，使用本项目命名常量／集中工厂 | [RecipesDeepSeiko:800–809](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L800-L809)，不是找外部 ItemList。 |

**本文为静态审计，不是替换补丁。** 特别不要只根据显示名替换 `particleBase`、`circuitWrap`、催化剂与戴森球部件；数量为 0、超栈数量及自带 NBT 都属于旧配方行为。
