# GT 配方硬编码溯源：其他外部 Mod

[返回审计总览](gt-recipe-audit.md)

### 非 GT5 外部物品注册键索引

按 `modId:registry@meta` 去重；表中对未写 meta 的调用按普通默认 0 展示，精确实参见调用索引，Forge 也可能返回自带 meta/NBT 的默认栈。每组先列实际 GTNH 2.9.0 本地运行包文件名；同版本 dev 有缓存时用 dev，未取得时用运行 jar。常量命中表示类含键，不自动代表运行时注册与 meta 已证实；未确认者明确列候选/查证方式。预览、`.fake()` 与真实生产配方分开判断。文中类名按 jar 内定义，方括号链接为 OTH 引用点。

#### `ae2fc`

运行包：[ae2fc-1.5.116-gtnh.jar](<../build/deepseiko-pack-check/mods/ae2fc-1.5.116-gtnh.jar>). 字节码优先使用同版本 `AE2FluidCraft-Rework-1.5.116-gtnh-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `ae2fc:fluid_storage.singularity@0` | [RecipesDeepSeiko:511](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L511) | `com.glodblock.github.util.NameConst`、`com.glodblock.github.loader.ItemAndBlockHolder` | `new ItemStack(ItemAndBlockHolder.SINGULARITY_CELL,n)` |
| `ae2fc:fluid_storage.Universe@0` | [RecipesDeepSeiko:617](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L617) | `com.glodblock.github.util.NameConst`、`com.glodblock.github.loader.ItemAndBlockHolder` | `new ItemStack(ItemAndBlockHolder.ARTIFICIAL_UNIVERSE_CELL,n)` |
| `ae2fc:walrus@0` | [RecipesMain:1429](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1429) | `com.glodblock.github.util.NameConst`、`com.glodblock.github.common.block.BlockWalrus` | `new ItemStack(ItemAndBlockHolder.WALRUS,n)` |

#### `appliedenergistics2`

运行包：[appliedenergistics2-rv3-beta-1095-GTNH.jar](<../build/deepseiko-pack-check/mods/appliedenergistics2-rv3-beta-1095-GTNH.jar>). 字节码优先使用同版本 `Applied-Energistics-2-Unofficial-rv3-beta-1095-GTNH-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `appliedenergistics2:item.ItemExtremeStorageCell.Singularity@0` | [RecipesDeepSeiko:510](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L510), [RecipesDeepSeiko:1910](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L1910), [RecipesMain:198](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L198), [RecipesMain:199](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L199), [RecipesMain:200](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L200) | `appeng.core.api.definitions.ApiItems/ApiBlocks`、`appeng.api.definitions.IItems/IBlocks`、`appeng.items.materials.MaterialType` | `AEApi.instance().definitions().items().cellSingularity().maybeStack(n)` |
| `appliedenergistics2:item.ItemExtremeStorageCell.Universe@0` | [RecipesDeepSeiko:616](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L616) | `appeng.core.api.definitions.ApiItems/ApiBlocks`、`appeng.api.definitions.IItems/IBlocks`、`appeng.items.materials.MaterialType` | `AEApi.instance().definitions().items().cellUniverse().maybeStack(n)` |
| `appliedenergistics2:item.ItemMultiMaterial@47` | [RecipesMain:1298](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1298) | `appeng.core.api.definitions.ApiItems/ApiBlocks`、`appeng.api.definitions.IItems/IBlocks`、`appeng.items.materials.MaterialType` | `AEApi.instance().definitions().materials().singularity().maybeStack(n)`，`MaterialType.Singularity` 的 ID=47，非 Java ordinal 68 |
| `appliedenergistics2:tile.BlockSingularityCraftingStorage@0` | [RecipesDeepSeiko:621](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L621) | `appeng.core.api.definitions.ApiItems/ApiBlocks`、`appeng.api.definitions.IItems/IBlocks`、`appeng.items.materials.MaterialType` | `AEApi.instance().definitions().blocks().craftingStorageSingularity().maybeStack(n)` |

#### `Avaritia`

运行包：[Avaritia-1.100.jar](<../build/deepseiko-pack-check/mods/Avaritia-1.100.jar>). 字节码优先使用同版本 `Avaritia-1.100-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `Avaritia:Cosmic_Meatballs@0` | [RecipesVoidNexusUpgradePreview:121](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L121) | `fox.spiteful.avaritia.items.LudicrousItems` | `LudicrousItems.cosmic_meatballs` |
| `Avaritia:Infinity_Sword@0` | [RecipesVoidNexusUpgradePreview:159](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L159) | `fox.spiteful.avaritia.items.LudicrousItems` | `LudicrousItems.infinity_sword` |
| `Avaritia:Resource@5` | [RecipesMegaISAForge:145](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L145), [RecipesVoidNexusUpgradePreview:58](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L58) | `fox.spiteful.avaritia.items.LudicrousItems` | `LudicrousItems.resource` + meta 5（Infinity Catalyst） |
| `Avaritia:Resource@6` | [RecipesVoidNexusUpgradePreview:80](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L80), [RecipesVoidNexusUpgradePreview:85](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L85), [RecipesVoidNexusUpgradePreview:158](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L158) | `fox.spiteful.avaritia.items.LudicrousItems` | `LudicrousItems.resource` + meta 6（Infinity Ingot） |
| `Avaritia:Resource@8` | [RecipesDeepSeiko:369](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L369), [RecipesVoidNexusUpgradePreview:131](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L131) | `fox.spiteful.avaritia.items.LudicrousItems` | `LudicrousItems.resource` + meta 8（Star Fuel） |
| `Avaritia:Singularity@5` | [RecipesTangshanSteelFactory:1003](../src/main/java/com/newmaa/othtech/recipe/RecipesTangshanSteelFactory.java#L1003) | `fox.spiteful.avaritia.items.LudicrousItems` | `LudicrousItems.singularity` + 已核对的 meta 5（不跨 Mod 换等名奇点） |
| `Avaritia:Ultimate_Stew@0` | [RecipesVoidNexusUpgradePreview:120](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L120) | `fox.spiteful.avaritia.items.LudicrousItems` | `LudicrousItems.ultimate_stew` |

#### `avaritiaddons`

运行包：[Avaritiaddons-1.9.6-GTNH.jar](<../build/deepseiko-pack-check/mods/Avaritiaddons-1.9.6-GTNH.jar>). 字节码优先使用同版本 `Avaritiaddons-1.9.6-GTNH-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `avaritiaddons:InfinityChest@0` | [RecipesSunFactoryEnqing:1466](../src/main/java/com/newmaa/othtech/recipe/RecipesSunFactoryEnqing.java#L1466) | `wanion.avaritiaddons.block.chest.infinity.BlockInfinityChest`、`wanion.avaritiaddons.CommonProxy` | `CommonProxy` 对应 `BlockInfinityChest`；没有稳定定义时保留精确 registry helper，且需要 Avaritia |

#### `BiomesOPlenty`

运行包：[BiomesOPlenty-1.7.10-2.1.0.2308-universal.jar](<../build/deepseiko-pack-check/mods/BiomesOPlenty-1.7.10-2.1.0.2308-universal.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `BiomesOPlenty:hard_ice@0` | [RecipesDeepSeiko:161](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L161) | 未在 jar 常量中确认完整键 | 2.1.0.2308 未见该字面注册键；仅保留为兼容 fallback，不合并为其它冰 |
| `BiomesOPlenty:hardIce@0` | [RecipesDeepSeiko:161](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L161) | `biomesoplenty.common.core.BOPBlocks`、`biomesoplenty.api.content.BOPCBlocks` | `BOPCBlocks.hardIce` 候选（`BOPBlocks` 初始化）；保留与 `hard_ice` 的 fallback 次序 |

#### `Botania`

运行包：[Botania-1.13.42-GTNH.jar](<../build/deepseiko-pack-check/mods/Botania-1.13.42-GTNH.jar>). 字节码优先使用同版本 `Botania-1.13.42-GTNH-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `Botania:manaResource@10` | [RecipesMegaQFT:306](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaQFT.java#L306) | `vazkii.botania.common.item.ModItems`、`vazkii.botania.common.block.BlockRoot` | `ModItems.manaResource` + meta 10，`ItemManaResource` 验证变种，不能用任意植物素材 |

#### `computronics`

运行包：[Computronics-1.9.13-GTNH.jar](<../build/deepseiko-pack-check/mods/Computronics-1.9.13-GTNH.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `computronics:computronics.ocSpecialParts@0` | [RecipesDeepSeiko:460](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L460) | `pl.asie.computronics.oc.IntegrationOpenComputers` | `IntegrationOpenComputers` 的 special-parts 对象，需同时等到 Computronics 与 OC 集成启用；保留 meta 0 |

#### `cropsnh`

运行包：[cropsnh-2.0.140.jar](<../build/deepseiko-pack-check/mods/cropsnh-2.0.140.jar>). 字节码优先使用同版本 `CropsNH-2.0.140-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `cropsnh:materialLeaf@10` | [RecipesSINOPEC:80](../src/main/java/com/newmaa/othtech/recipe/RecipesSINOPEC.java#L80) | `CropsNHItems`、`ItemMaterialLeaf`、`MaterialLeafLoader` | `CropsNHItemList.oilBerry.get(n)`；`MaterialLeafLoader` 显式 meta 10，见 `CropsNHItems` 初始化 |

#### `DraconicEvolution`

运行包：[Draconic-Evolution-1.5.35-GTNH.jar](<../build/deepseiko-pack-check/mods/Draconic-Evolution-1.5.35-GTNH.jar>). 字节码优先使用同版本 `Draconic-Evolution-1.5.35-GTNH-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `DraconicEvolution:draconicCore@0` | [RecipesMain:1357](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1357) | `com.brandon3055.draconicevolution.common.ModItems`、`com.brandon3055.draconicevolution.common.items.DraconicCore` | `ModItems.draconicCore` |
| `DraconicEvolution:draconiumEnergyCore@0` | [RecipesMain:1358](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1358) | `com.brandon3055.draconicevolution.common.ModItems`、`com.brandon3055.draconicevolution.common.items.DraconiumEnergyCore` | `ModItems.draconiumEnergyCore` |

#### `dreamcraft`

运行包：[GTNewHorizonsCoreMod-2.9.99.jar](<../build/deepseiko-pack-check/mods/GTNewHorizonsCoreMod-2.9.99.jar>). 字节码优先使用同版本 `NewHorizonsCoreMod-2.9.99-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `dreamcraft:BloodyIchorium@0` | [RecipesVoidNexusUpgradePreview:79](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L79) | `com.dreammaster.block.BlockList`、`com.dreammaster.scripts.ScriptBloodMagic` | `BlockList.BloodyIchorium.get(n)`；仅预览 |
| `dreamcraft:ChromaticLens@0` | [RecipesMAXs:217](../src/main/java/com/newmaa/othtech/recipe/RecipesMAXs.java#L217), [RecipesMAXs:233](../src/main/java/com/newmaa/othtech/recipe/RecipesMAXs.java#L233) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptNanochipRecipes` | `NHItemList.ChromaticLens.get(n)` |
| `dreamcraft:EngineeringProcessorItemAdvEmeraldCore@0` | [RecipesSunFactoryEnqing:1361](../src/main/java/com/newmaa/othtech/recipe/RecipesSunFactoryEnqing.java#L1361), [RecipesSunFactoryEnqing:1482](../src/main/java/com/newmaa/othtech/recipe/RecipesSunFactoryEnqing.java#L1482) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptCoreMod` | `NHItemList.EngineeringProcessorItemAdvEmeraldCore.get(n)` |
| `dreamcraft:GTNHBioItems@2` | [RecipesMegaQFT:189](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaQFT.java#L189) | `BioItemLoader` 的 `BIOTEMS` 与 `registerItem` | `BioItemLoader.BIOTEMS` + 保留 meta 2；`BioItemLoader` 调用 FML 三参注册，第三参是已废弃且未使用的参数；[开发运行日志:38532](<../run/client/logs/fml-client-latest.log#L38532>) 实证为 `dreamcraft:GTNHBioItems`。 |
| `dreamcraft:HeavenlyPikoCircuit@0` | [RecipesDeepSeiko:512](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L512) | `com.dreammaster.item.NHItemList` | `NHItemList.PikoCircuitHeavenly.get(n)` |
| `dreamcraft:HeavenlyQuantumCircuit@0` | [RecipesDeepSeiko:548](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L548) | `com.dreammaster.item.NHItemList` | `NHItemList.QuantumCircuitHeavenly.get(n)` |
| `dreamcraft:HighEnergyFlowCircuit@0` | [RecipesMain:1490](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1490) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.recipes.CircuitAssemblerRecipes` | `NHItemList.HighEnergyFlowCircuit.get(n)` |
| `dreamcraft:LapotronDust@0` | [RecipesMegaISAForge:46](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L46) | `com.dreammaster.item.NHItemList`、`com.dreammaster.oredict.OreDictHandler` | `NHItemList.LapotronDust.get(n)` |
| `dreamcraft:MysteriousCrystalLens@0` | [RecipesMain:196](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L196) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.recipes.DTPFRecipes` | `NHItemList.MysteriousCrystalLens.get(n)` |
| `dreamcraft:NeutroniumBars@0` | [RecipesDeepSeiko:388](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L388) | `com.dreammaster.block.BlockList`、`com.dreammaster.gthandler.GT_Loader_OreDictionary` | `BlockList.NeutroniumBars.get(n)` |
| `dreamcraft:PicoWafer@0` | [RecipesMegaQFT:219](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaQFT.java#L219) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.recipes.AssemblingLineRecipes` | `NHItemList.PicoWafer.get(n)` |
| `dreamcraft:PikoCircuit@0` | [OTEHatchRack:282](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L282) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.GT_Loader_Items` | `NHItemList.PikoCircuit.get(n)` |
| `dreamcraft:PlanckCircuit@0` | [OTEHatchRack:292](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L292), [RecipesDeepSeiko:580](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L580), [RecipesDeepSeiko:613](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L613) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.GT_Loader_Items` | `NHItemList.PlanckCircuit.get(n)` |
| `dreamcraft:Quantinum@0` | [OTHItemCombs:162](../src/main/java/com/newmaa/othtech/common/beeyonds/OTHItemCombs.java#L162), [OTHItemCombs:182](../src/main/java/com/newmaa/othtech/common/beeyonds/OTHItemCombs.java#L182) | `com.dreammaster.block.BlockList`、`com.dreammaster.gthandler.GT_Loader_OreDictionary` | `BlockList.Quantinum.get(n)`；包外 `registerTC` GT 注册 |
| `dreamcraft:QuantumCircuit@0` | [OTEHatchRack:287](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L287) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.GT_Loader_Items` | `NHItemList.QuantumCircuit.get(n)` |
| `dreamcraft:RadoxPolymerLens@0` | [RecipesMain:1202](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1202), [RecipesMain:1205](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1205) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.recipes.LatheRecipes` | `NHItemList.RadoxPolymerLens.get(n)` |
| `dreamcraft:SchematicsTier1@0` | [RecipesNASA:62](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L62), [RecipesNASA:122](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L122) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.recipes.CircuitAssemblerRecipes` | `NHItemList.SchematicsTier1.get(n)` |
| `dreamcraft:SchematicsTier2@0` | [RecipesNASA:218](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L218), [RecipesNASA:305](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L305), [RecipesNASA:439](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L439) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptGalaxySpace` | `NHItemList.SchematicsTier2.get(n)` |
| `dreamcraft:SchematicsTier3@0` | [RecipesNASA:116](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L116), [RecipesNASA:137](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L137), [RecipesNASA:218](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L218), [RecipesNASA:305](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L305), [RecipesNASA:439](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L439) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptGalaxySpace` | `NHItemList.SchematicsTier3.get(n)` |
| `dreamcraft:SchematicsTier4@0` | [RecipesNASA:218](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L218), [RecipesNASA:305](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L305), [RecipesNASA:439](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L439) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptGalaxySpace` | `NHItemList.SchematicsTier4.get(n)` |
| `dreamcraft:SchematicsTier5@0` | [RecipesNASA:151](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L151), [RecipesNASA:218](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L218), [RecipesNASA:305](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L305), [RecipesNASA:439](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L439) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptGalaxySpace` | `NHItemList.SchematicsTier5.get(n)` |
| `dreamcraft:SchematicsTier6@0` | [RecipesNASA:218](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L218), [RecipesNASA:305](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L305), [RecipesNASA:439](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L439) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptGalaxySpace` | `NHItemList.SchematicsTier6.get(n)` |
| `dreamcraft:SchematicsTier7@0` | [RecipesNASA:218](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L218), [RecipesNASA:305](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L305), [RecipesNASA:439](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L439) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptGalaxySpace` | `NHItemList.SchematicsTier7.get(n)` |
| `dreamcraft:SchematicsTier8@0` | [RecipesNASA:166](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L166), [RecipesNASA:218](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L218), [RecipesNASA:305](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L305), [RecipesNASA:439](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L439) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptGalaxySpace` | `NHItemList.SchematicsTier8.get(n)` |
| `dreamcraft:StargateCrystalDust@0` | [RecipesMegaISAForge:410](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L410) | `com.dreammaster.item.NHItemList`、`com.dreammaster.gthandler.recipes.MixerRecipes` | `NHItemList.StargateCrystalDust.get(n)` |
| `dreamcraft:StargateFramePart@0` | [RecipesMegaISAForge:304](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L304) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptSGCraft` | `NHItemList.StargateFramePart.get(n)` |
| `dreamcraft:StargateShieldingFoil@0` | [RecipesMegaISAForge:303](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L303), [RecipesMegaISAForge:406](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L406), [RecipesVoidNexusUpgradePreview:195](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L195) | `com.dreammaster.item.NHItemList`、`com.dreammaster.scripts.ScriptSGCraft` | `NHItemList.StargateShieldingFoil.get(n)` |

#### `EnderIO`

运行包：[EnderIO-2.10.51.jar](<../build/deepseiko-pack-check/mods/EnderIO-2.10.51.jar>). 字节码优先使用同版本 `EnderIO-2.10.51-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `EnderIO:blockEndermanSkull@0` | [RecipesEIO:216](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L216), [RecipesEIO:227](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L227) | `crazypants.enderio.item.skull.BlockEndermanSkull`、`crazypants.enderio.item.skull.EndermanSkullRenderer` | `new ItemStack(EnderIO.blockEndermanSkull,n,meta)`；0 / 2 保留各自变种 |
| `EnderIO:blockEndermanSkull@2` | [RecipesEIO:211](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L211) | `crazypants.enderio.item.skull.BlockEndermanSkull`、`crazypants.enderio.item.skull.EndermanSkullRenderer` | `new ItemStack(EnderIO.blockEndermanSkull,n,meta)`；0 / 2 保留各自变种 |
| `EnderIO:itemBasicCapacitor@0` | [RecipesEIO:194](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L194), [RecipesEIO:213](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L213) | `crazypants.enderio.material.ItemCapacitor`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Capacitors.BASIC_CAPACITOR.getItemStack()`，复制后改量 |
| `EnderIO:itemBasicCapacitor@3` | [RecipesEIO:260](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L260) | `crazypants.enderio.material.ItemCapacitor`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Capacitors.CRYSTALLINE_CAPACITOR.getItemStack()`，复制后改量 |
| `EnderIO:itemBasicCapacitor@6` | [RecipesEIO:255](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L255) | `crazypants.enderio.material.ItemCapacitor`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Capacitors.TOTEMIC_CAPACITOR.getItemStack()`，复制后改量 |
| `EnderIO:itemBrokenSpawner@0` | [RecipesEIO:176](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L176), [RecipesEIO:179](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L179) | `crazypants.enderio.machine.spawner.ItemBrokenSpawner`、`crazypants.enderio.EnderIO` | 空态用 `EnderIO.itemBrokenSpawner`；装生物用 `ItemBrokenSpawner.createStackForMobType(id)` 前核对原有 tag |
| `EnderIO:itemFrankenSkull@0` | [RecipesEIO:189](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L189) | `crazypants.enderio.material.ItemFrankenSkull`、`crazypants.enderio.EnderIO` | `new ItemStack(EnderIO.itemFrankenSkull,n,FrankenSkull.ZOMBIE_ELECTRODE.ordinal())` |
| `EnderIO:itemFrankenSkull@1` | [RecipesEIO:67](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L67), [RecipesEIO:77](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L77), [RecipesEIO:200](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L200) | `crazypants.enderio.material.ItemFrankenSkull`、`crazypants.enderio.EnderIO` | `new ItemStack(EnderIO.itemFrankenSkull,n,FrankenSkull.ZOMBIE_CONTROLLER.ordinal())` |
| `EnderIO:itemFrankenSkull@2` | [RecipesEIO:69](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L69), [RecipesEIO:78](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L78), [RecipesEIO:79](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L79) | `crazypants.enderio.material.ItemFrankenSkull`、`crazypants.enderio.EnderIO` | `new ItemStack(EnderIO.itemFrankenSkull,n,FrankenSkull.FRANKEN_ZOMBIE.ordinal())` |
| `EnderIO:itemFrankenSkull@3` | [RecipesEIO:89](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L89), [RecipesEIO:99](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L99), [RecipesEIO:154](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L154), [RecipesEIO:164](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L164), [RecipesEIO:222](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L222) | `crazypants.enderio.material.ItemFrankenSkull`、`crazypants.enderio.EnderIO` | `new ItemStack(EnderIO.itemFrankenSkull,n,FrankenSkull.ENDER_RESONATOR.ordinal())` |
| `EnderIO:itemFrankenSkull@4` | [RecipesEIO:91](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L91), [RecipesEIO:100](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L100), [RecipesEIO:101](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L101), [RecipesEIO:156](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L156), [RecipesEIO:165](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L165), [RecipesEIO:166](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L166) | `crazypants.enderio.material.ItemFrankenSkull`、`crazypants.enderio.EnderIO` | `new ItemStack(EnderIO.itemFrankenSkull,n,FrankenSkull.SENTIENT_ENDER.ordinal())` |
| `EnderIO:itemFrankenSkull@5` | [RecipesEIO:233](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L233) | `crazypants.enderio.material.ItemFrankenSkull`、`crazypants.enderio.EnderIO` | `new ItemStack(EnderIO.itemFrankenSkull,n,FrankenSkull.SKELETAL_CONTRACTOR.ordinal())` |
| `EnderIO:itemFrankenSkull@6` | [RecipesEIO:244](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L244) | `crazypants.enderio.material.ItemFrankenSkull`、`crazypants.enderio.EnderIO` | `new ItemStack(EnderIO.itemFrankenSkull,n,FrankenSkull.GUARDIAN_DIODE.ordinal())` |
| `EnderIO:itemMaterial@5` | [RecipesEIO:132](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L132), [RecipesEIO:142](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L142), [RecipesEIO:249](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L249) | `crazypants.enderio.material.ItemMaterial`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Material.PULSATING_CYSTAL.getStack(n)` |
| `EnderIO:itemMaterial@6` | [RecipesEIO:111](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L111), [RecipesEIO:121](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L121) | `crazypants.enderio.material.ItemMaterial`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Material.VIBRANT_CYSTAL.getStack(n)` |
| `EnderIO:itemMaterial@8` | [RecipesEIO:113](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L113), [RecipesEIO:122](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L122), [RecipesEIO:123](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L123) | `crazypants.enderio.material.ItemMaterial`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Material.ENDER_CRYSTAL.getStack(n)` |
| `EnderIO:itemMaterial@9` | [RecipesEIO:47](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L47), [RecipesEIO:56](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L56), [RecipesEIO:57](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L57) | `crazypants.enderio.material.ItemMaterial`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Material.ATTRACTOR_CRYSTAL.getStack(n)` |
| `EnderIO:itemMaterial@13` | [RecipesEIO:134](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L134), [RecipesEIO:143](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L143), [RecipesEIO:144](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L144) | `crazypants.enderio.material.ItemMaterial`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Material.PRECIENT_CRYSTAL.getStack(n)` |
| `EnderIO:itemMaterial@15` | [RecipesEIO:259](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L259) | `crazypants.enderio.material.ItemMaterial`、`crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade` | `Material.VIBRANT_POWDER.getStack(n)` |
| `EnderIO:itemSoulVessel@0` | [RecipesEIO:24](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L24), [RecipesEIO:29](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L29), [RecipesEIO:34](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L34), [RecipesEIO:39](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L39), [RecipesEIO:174](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L174), [RecipesEIO:181](../src/main/java/com/newmaa/othtech/recipe/RecipesEIO.java#L181), [RecipesNewWetware:123](../src/main/java/com/newmaa/othtech/recipe/RecipesNewWetware.java#L123), [RecipesNewWetware:132](../src/main/java/com/newmaa/othtech/recipe/RecipesNewWetware.java#L132) | `crazypants.enderio.item.ItemSoulVessel`、`crazypants.enderio.EnderIO` | 空罐 `new ItemStack(EnderIO.itemSoulVessel,n)`；`ItemSoulVessel.createVesselWithEntityStub(id)` 只是候选，必须核对实际 `id`/NBT，与 EIOSpawnerBinding 及 Wetware 现有匹配一致；不能以空罐替代 |

#### `eternalsingularity`

运行包：[eternalsingularity-1.4.3.jar](<../build/deepseiko-pack-check/mods/eternalsingularity-1.4.3.jar>). 字节码优先使用同版本 `Eternal-Singularity-1.4.3-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `eternalsingularity:combined_singularity@1` | [RecipesVoidNexusUpgradePreview:154](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L154), [RecipesVoidNexusUpgradePreview:299](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L299) | `singulariteam.eternalsingularity.proxy.CommonProxy` 的 preInit/postInit | `CommonProxy.postInit` 有条件注册，meta 1（预览的圣灵奇点）；确保初始化后存在，不能以永恒奇点或 Avaritia 奇点代替 |
| `eternalsingularity:eternal_singularity@0` | [RecipesMAXs:250](../src/main/java/com/newmaa/othtech/recipe/RecipesMAXs.java#L250), [RecipesMain:164](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L164), [RecipesMain:329](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L329), [RecipesMegaISAForge:192](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L192), [RecipesMegaISAForge:227](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L227), [RecipesMegaISAForge:248](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L248), [RecipesMegaQFT:205](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaQFT.java#L205), [RecipesSunFactoryEnqing:1481](../src/main/java/com/newmaa/othtech/recipe/RecipesSunFactoryEnqing.java#L1481), [RecipesVoidNexusUpgradePreview:295](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L295) | `singulariteam.eternalsingularity.proxy.CommonProxy` 的 preInit/postInit | `CommonProxy.preInit` 注册；保留经验证的 registry helper（`getEternalSingularityRecipe()` 是 recipe，不是 stack factory） |

#### `etfuturum`

运行包：[etfuturum-2.6.65-GTNH.jar](<../build/deepseiko-pack-check/mods/etfuturum-2.6.65-GTNH.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `etfuturum:blue_ice@0` | [RecipesDeepSeiko:162](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L162) | `ganymedes01.etfuturum.blocks.BlockBlueIce` | `ganymedes01.etfuturum.ModBlocks.BLUE_ICE.get()`；缺失时按 DeepSeiko 原分支跳过 |
| `etfuturum:elytra@0` | [RecipesMain:1538](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1538) | `ganymedes01.etfuturum.items.equipment.ItemArmorElytra`、`ganymedes01.etfuturum.core.handlers.ServerEventHandler` | `ganymedes01.etfuturum.ModItems.ELYTRA.get()`；功能启用/对象非空后再建栈 |

#### `ExtraUtilities`

运行包：[extrautilities-1.2.12.jar](<../build/deepseiko-pack-check/mods/extrautilities-1.2.12.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `ExtraUtilities:defoliageAxe@0` | [RecipesVoidNexusUpgradePreview:122](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L122) | 部分键常量：`com.rwtema.extrautils.item.ItemHealingAxe` | `ItemHealingAxe` / 与 pack 运行时验证过的精确 registry，保留 meta 0；仅预览 |

#### `Forestry`

运行包：[Forestry-4.11.40.jar](<../build/deepseiko-pack-check/mods/Forestry-4.11.40.jar>). 字节码优先使用同版本 `ForestryMC-4.11.40-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `Forestry:honeyDrop@0` | [OTHItemCombs:273](../src/main/java/com/newmaa/othtech/common/beeyonds/OTHItemCombs.java#L273), [OTHItemCombs:280](../src/main/java/com/newmaa/othtech/common/beeyonds/OTHItemCombs.java#L280) | `forestry.core.inventory.ItemInventoryAlyzer`、`forestry.apiculture.items.ItemRegistryApiculture` | `ItemRegistryApiculture.honeyDrop`（通过 Forestry apiculture item registry 获取）复制为 stack；现为包外 GT 配方输出 |

#### `Gadomancy`

运行包：[gadomancy-1.5.17.jar](<../build/deepseiko-pack-check/mods/gadomancy-1.5.17.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `Gadomancy:ItemEtherealFamiliar@0` | [RecipesAbstractSanctum:627](../src/main/java/com/newmaa/othtech/recipe/RecipesAbstractSanctum.java#L627) | `makeo.gadomancy.common.items.baubles.ItemEtherealFamiliar` | 只是动态候选过滤/排除，不是 GT 配方槽；保留模组检测与可空 `findItem` |

#### `GalacticraftAmunRa`

运行包：[AmunRa-GC-0.8.14.jar](<../build/deepseiko-pack-check/mods/AmunRa-GC-0.8.14.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `GalacticraftAmunRa:item.baseItem@15` | [RecipesNASA:423](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L423) | `de.katzenpapst.amunra.item.ARItems.initItems` / `ItemBasicMulti` | `ARItems.lightPlating` 的 `ItemDamagePair` 转 stack，或 `ARItems.baseItem.getItemStack("lightPlate", n)` |
| `GalacticraftAmunRa:item.baseItem@26` | [RecipesCyclotronRecipes:25](../src/main/java/com/newmaa/othtech/recipe/RecipesCyclotronRecipes.java#L25), [RecipesMegaISAForge:211](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L211), [RecipesMegaISAForge:213](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L213), [RecipesMegaQFT:248](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaQFT.java#L248) | `de.katzenpapst.amunra.item.ARItems.initItems` / `ItemBasicMulti` | `ARItems.darkShard` 的 `ItemDamagePair` 转 stack，或 `ARItems.baseItem.getItemStack("darkShard", n)` |

#### `GalacticraftCore`

运行包：[Galacticraft-3.4.37-GTNH.jar](<../build/deepseiko-pack-check/mods/Galacticraft-3.4.37-GTNH.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `GalacticraftCore:item.basicItem@14` | [RecipesMain:1673](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1673) | `GCItems.initItems` / `GCBlocks.registerBlocks`，由 unlocalizedName 拼注册串 | `new ItemStack(GCItems.basicItem,n,meta)`；保留当前 subtype |
| `GalacticraftCore:item.engine@0` | [RecipesNASA:74](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L74), [RecipesNASA:93](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L93) | `GCItems.initItems` / `GCBlocks.registerBlocks`，由 unlocalizedName 拼注册串 | `new ItemStack(GCItems.rocketEngine,n,meta)`；保留当前 subtype |
| `GalacticraftCore:item.heavyPlating@0` | [RecipesNASA:70](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L70), [RecipesNASA:92](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L92), [RecipesNASA:185](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L185) | `GCItems.initItems` / `GCBlocks.registerBlocks`，由 unlocalizedName 拼注册串 | `new ItemStack(GCItems.heavyPlatingTier1,n,meta)`；保留当前 subtype |
| `GalacticraftCore:item.noseCone@0` | [RecipesNASA:69](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L69) | `GCItems.initItems` / `GCBlocks.registerBlocks`，由 unlocalizedName 拼注册串 | `new ItemStack(GCItems.partNoseCone,n,meta)`；保留当前 subtype |
| `GalacticraftCore:item.rocketFins@0` | [RecipesNASA:73](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L73) | `GCItems.initItems` / `GCBlocks.registerBlocks`，由 unlocalizedName 拼注册串 | `new ItemStack(GCItems.partFins,n,meta)`；保留当前 subtype |
| `GalacticraftCore:item.spaceship@0` | [RecipesNASA:68](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L68) | `GCItems.initItems` / `GCBlocks.registerBlocks`，由 unlocalizedName 拼注册串 | `new ItemStack(GCItems.rocketTier1,n,meta)`；保留当前 subtype |
| `GalacticraftCore:tile.rocketWorkbench@0` | [RecipesMain:1672](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1672) | `GCItems.initItems` / `GCBlocks.registerBlocks`，由 unlocalizedName 拼注册串 | `new ItemStack(GCBlocks.nasaWorkbench,n,meta)`；保留当前 subtype |

#### `GalacticraftMars`

运行包：[Galacticraft-3.4.37-GTNH.jar](<../build/deepseiko-pack-check/mods/Galacticraft-3.4.37-GTNH.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `GalacticraftMars:item.itemBasicAsteroids@0` | [RecipesNASA:349](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L349) | `MarsItems` / `AsteroidsItems`、`ItemBasicMars` / `ItemBasicAsteroids` | `AsteroidsItems.basicItem` + 0（Compressed Titanium） |
| `GalacticraftMars:item.null@3` | [RecipesNASA:348](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L348) | `MarsItems` / `AsteroidsItems`、`ItemBasicMars` / `ItemBasicAsteroids` | `MarsItems.marsItemBasic` + 3，先验证注册名/对象（GC 通过 Item#getUnlocalizedName 注册非显式键） |

#### `GalaxySpace`

运行包：[GalaxySpace-1.1.144-GTNH.jar](<../build/deepseiko-pack-check/mods/GalaxySpace-1.1.144-GTNH.jar>). 字节码优先使用同版本 `Galaxy-Space-GTNH-1.1.144-GTNH-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `GalaxySpace:item.ModuleLander@0` | [RecipesNASA:71](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L71) | `GSItems` / `TCetiBlocks` 的构造与注册字节码 | `GSItems.ModuleLander` |
| `GalaxySpace:item.ModuleSmallFuelCanister@0` | [RecipesNASA:72](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L72) | `GSItems` / `TCetiBlocks` 的构造与注册字节码 | `GSItems.ModuleSmallFuelCanister` |
| `GalaxySpace:item.RobotArm@0` | [RecipesMain:1674](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1674) | `GSItems` / `TCetiBlocks` 的构造与注册字节码 | `GSItems.RobotArm` |
| `GalaxySpace:item.RocketControlComputer@1` | [RecipesNASA:65](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L65) | `GSItems` / `TCetiBlocks` 的构造与注册字节码 | `GSItems.ControlComputer` + meta 1 |
| `GalaxySpace:tcetiedandelions@0` | [RecipesMegaQFT:223](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaQFT.java#L223) | `GSItems` / `TCetiBlocks` 的构造与注册字节码 | `TCetiBlocks` 对应花方块；若对象版本不稳，保留验证过的 registry helper |

#### `GraviSuite`

运行包：[GraviSuite-1.7.10-2.0.3.jar](<../build/deepseiko-pack-check/mods/GraviSuite-1.7.10-2.0.3.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `GraviSuite:itemSimpleItem@2` | [RecipesDeepSeiko:162](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L162) | `gravisuite.GraviSuite` | `gravisuite.GraviSuite` 对应已注册 Item + 原 meta；不要使用 `gravisuiteneo` 同名组件代替 |
| `GraviSuite:itemSimpleItem@3` | [RecipesDeepSeiko:2254](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L2254), [RecipesDeepSeiko:2255](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L2255), [RecipesDeepSeiko:2256](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L2256), [RecipesDeepSeiko:2257](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L2257), [RecipesMain:1541](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1541), [RecipesSunFactoryEnqing:1382](../src/main/java/com/newmaa/othtech/recipe/RecipesSunFactoryEnqing.java#L1382) | `gravisuite.GraviSuite` | `gravisuite.GraviSuite` 对应已注册 Item + 原 meta；不要使用 `gravisuiteneo` 同名组件代替 |

#### `harvestcraft`

运行包：[harvestcraft-1.3.15-GTNH.jar](<../build/deepseiko-pack-check/mods/harvestcraft-1.3.15-GTNH.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `harvestcraft:riceItem@0` | [RecipesDeepSeiko:139](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L139) | `com.pam.harvestcraft.BlockPamCrop`、`com.pam.harvestcraft.ItemRegistry` | `com.pam.harvestcraft.ItemRegistry` 中对应 riceItem 或包内 `listAllrice` 等价性经 OreDict 核对后才可考虑；输出/精确需求优先原对象 |

#### `IC2`

运行包：[industrialcraft-2-2.2.828-experimental.jar](<../build/deepseiko-pack-check/mods/industrialcraft-2-2.2.828-experimental.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `IC2:blockGenerator@5` | [RecipesMain:1627](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1627), [RecipesNASA:177](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L177) | `ic2.core.init.BlocksItems`、`ic2.core.init.InternalName` | 候选 `Ic2Items.nuclearReactor`，必须核对 meta 5，不以同 registry 的发电机其他变种替代 |
| `IC2:blockITNT@0` | [RecipesMain:1626](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1626) | `ic2.core.block.BlockITNT`、`ic2.core.init.BlocksItems` | `Ic2Items.industrialTnt.copy()` |
| `IC2:blockReactorChamber@0` | [RecipesMain:1628](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1628) | `ic2.core.init.BlocksItems`、`ic2.core.init.InternalName` | `Ic2Items.reactorChamber.copy()` |
| `IC2:itemFertilizer@0` | [RecipesSINOPEC:207](../src/main/java/com/newmaa/othtech/recipe/RecipesSINOPEC.java#L207), [RecipesSINOPEC:232](../src/main/java/com/newmaa/othtech/recipe/RecipesSINOPEC.java#L232), [RecipesSINOPEC:275](../src/main/java/com/newmaa/othtech/recipe/RecipesSINOPEC.java#L275) | `ic2.core.init.BlocksItems`、`ic2.core.init.InternalName` | `Ic2Items.fertilizer.copy()` |
| `IC2:itemFuelPlantBall@0` | [RecipesMain:1011](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1011), [RecipesSINOPEC:278](../src/main/java/com/newmaa/othtech/recipe/RecipesSINOPEC.java#L278) | `ic2.core.init.BlocksItems`、`ic2.core.init.InternalName` | `Ic2Items.plantBall.copy()` |
| `IC2:itemPartCarbonPlate@0` | [RecipesMain:1539](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1539) | `ic2.core.init.BlocksItems`、`ic2.core.init.InternalName` | `Ic2Items.carbonPlate.copy()` |
| `IC2:itemPartIridium@0` | [RecipesSunFactoryEnqing:1459](../src/main/java/com/newmaa/othtech/recipe/RecipesSunFactoryEnqing.java#L1459) | `ic2.core.init.BlocksItems`、`ic2.core.init.InternalName` | `Ic2Items.iridiumPlate.copy()`；`BlocksItems` 的 `InternalName.itemPartIridium` 构建的对象明确写入该字段 |
| `IC2:reactorVentDiamond@1` | [RecipesCircuit:73](../src/main/java/com/newmaa/othtech/recipe/RecipesCircuit.java#L73), [RecipesMain:1154](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1154), [RecipesMegaISAForge:330](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L330) | `ic2.core.Ic2Items`、`ic2.core.init.BlocksItems` | `Ic2Items.reactorVentDiamond.copy()` 仅供对象模板；`BlocksItems` 用单参数 `ItemStack` 构造（默认 damage 0），旧硬编码要求 1，必须显式保留 1 |

#### `NHUtilities`

本地 2.9.0 包未见独立 jar。 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `NHUtilities:AncientDragonEgg@0` | [RecipesDE:43](../src/main/java/com/newmaa/othtech/recipe/RecipesDE.java#L43), [RecipesDE:47](../src/main/java/com/newmaa/othtech/recipe/RecipesDE.java#L47) | 仅本项目调用可证实 | 本地包与缓存未发现对应 jar；保留经过运行时验证的精确键，缺失跳过 `RecipesDE` 该分支，零数量模板和输出分开 |
| `NHUtilities:ChaosDragonEgg@0` | [RecipesDE:55](../src/main/java/com/newmaa/othtech/recipe/RecipesDE.java#L55), [RecipesDE:59](../src/main/java/com/newmaa/othtech/recipe/RecipesDE.java#L59) | 仅本项目调用可证实 | 本地包与缓存未发现对应 jar；同 `AncientDragonEgg`，不能用 Draconic Evolution 原版掉落替代 |

#### `OpenComputers`

运行包：[OpenComputers-1.12.68-GTNH.jar](<../build/deepseiko-pack-check/mods/OpenComputers-1.12.68-GTNH.jar>). 字节码优先使用同版本 `OpenComputers-1.12.68-GTNH-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `OpenComputers:item@10` | [OTEHatchRack:314](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L314), [RecipesDeepSeiko:459](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L459) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | `li.cil.oc.api.Items.get("graphicsCard3").createItemStack(n)` |
| `OpenComputers:item@27` | [RecipesCircuit:64](../src/main/java/com/newmaa/othtech/recipe/RecipesCircuit.java#L64) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | `Items.get("alu").createItemStack(n)` |
| `OpenComputers:item@28` | [RecipesCircuit:65](../src/main/java/com/newmaa/othtech/recipe/RecipesCircuit.java#L65) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | `Items.get("cu").createItemStack(n)` |
| `OpenComputers:item@33` | [RecipesCircuit:62](../src/main/java/com/newmaa/othtech/recipe/RecipesCircuit.java#L62), [RecipesMegaISAForge:319](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L319) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | `Items.get("card").createItemStack(n)` |
| `OpenComputers:item@39` | [RecipesMegaISAForge:322](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L322) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | 候选 `Items.get("ram6").createItemStack(n)`；仍做反查确认 |
| `OpenComputers:item@43` | [OTEHatchRack:309](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L309) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | `Items.get("cpu3").createItemStack(n)` |
| `OpenComputers:item@44` | [RecipesDeepSeiko:458](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L458) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | 候选 `Items.get("internetCard").createItemStack(n)`；仍做反查确认 |
| `OpenComputers:item@90` | [RecipesCircuit:66](../src/main/java/com/newmaa/othtech/recipe/RecipesCircuit.java#L66), [RecipesMegaISAForge:320](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L320) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | 先用 `Items.get(原 stack).name()` 精确反查，再用 `Items.get(name).createItemStack(n)`；不要猜编号 |
| `OpenComputers:item@102` | [OTEHatchRack:304](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L304) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | `Items.get(原 stack).name()` 反查，旧源码注释 APU T3 不构成模板等价证据 |
| `OpenComputers:item@103` | [OTEHatchRack:319](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L319), [RecipesCircuit:58](../src/main/java/com/newmaa/othtech/recipe/RecipesCircuit.java#L58), [RecipesMain:1133](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1133), [RecipesMain:1134](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1134), [RecipesMegaISAForge:321](../src/main/java/com/newmaa/othtech/recipe/RecipesMegaISAForge.java#L321) | `li.cil.oc.common.init.Items$.init`、`Delegator.add`、`Delegate$class`、`li.cil.oc.api.detail.ItemInfo` | `Items.get(原 stack).name()` 反查；`apuCreative` 是具名候选，须核查相同 meta/NBT，Research 项也要同步 |

#### `Railcraft`

运行包：[Railcraft-9.17.36.jar](<../build/deepseiko-pack-check/mods/Railcraft-9.17.36.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `Railcraft:firestone.raw@0` | [RecipesMain:439](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L439) | `mods.railcraft.common.core.RailcraftConfig` | `RailcraftConfig` 的 raw firestone / 精确保留 registry（判断模块启用）；不是 cut/refined}) |
| `Railcraft:fuel.coke@0` | [RecipesEpicCokeOvenFake:22](../src/main/java/com/newmaa/othtech/recipe/RecipesEpicCokeOvenFake.java#L22), [RecipesEpicCokeOvenFake:28](../src/main/java/com/newmaa/othtech/recipe/RecipesEpicCokeOvenFake.java#L28), [RecipesEpicCokeOvenFake:34](../src/main/java/com/newmaa/othtech/recipe/RecipesEpicCokeOvenFake.java#L34), [RecipesEpicCokeOvenFake:40](../src/main/java/com/newmaa/othtech/recipe/RecipesEpicCokeOvenFake.java#L40), [RecipesEpicCokeOvenFake:46](../src/main/java/com/newmaa/othtech/recipe/RecipesEpicCokeOvenFake.java#L46) | `mods.railcraft.common.core.RailcraftConfig` | `RailcraftConfig` 的 item factory / 精确保留 registry（判断对应模块启用）；这是 GT 假配方输出 |

#### `RandomThings`

运行包：[RandomThings-2.7.13.jar](<../build/deepseiko-pack-check/mods/RandomThings-2.7.13.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `RandomThings:ingredient@4` | [RecipesVoidNexusUpgradePreview:101](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L101) | `lumien.randomthings.Items.ItemIngredient`、`lumien.randomthings.Handler.ImbuingStation.ImbuingRecipe` | `ItemIngredient` 的变种 4，只有版本固定的 meta，无需假设有公开命名 factory；仅预览 |

#### `SGCraft`

运行包：[SGCraft-1.4.14-GTNH.jar](<../build/deepseiko-pack-check/mods/SGCraft-1.4.14-GTNH.jar>). 字节码优先使用同版本 `SGCraft-1.4.14-GTNH-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `SGCraft:sgCoreCrystal@0` | [RecipesVoidNexusUpgradePreview:180](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L180) | `gcewing.sg.SGCraft` | `SGCraft.sgCoreCrystal` 初始化对象；独立于 dreamcraft 的星门物品，预览须判空 |

#### `supersolarpanel`

运行包：[supersolarpanel-1.1.5.jar](<../build/deepseiko-pack-check/mods/supersolarpanel-1.1.5.jar>). 字节码优先使用同版本 `supersolarpanels-1.1.5-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `supersolarpanel:bluecomponent@0` | [RecipesDeepSeiko:2160](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L2160) | `com.supsolpans.MainSSP` | `MainSSP` 对应 `bluecomponent` 组件；未知公开入口时用精确注册键集中适配 |
| `supersolarpanel:greencomponent@0` | [RecipesDeepSeiko:2159](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L2159) | `com.supsolpans.MainSSP` | `MainSSP` 对应 `greencomponent` 组件；未知公开入口时用精确注册键集中适配 |
| `supersolarpanel:redcomponent@0` | [RecipesDeepSeiko:2158](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L2158) | `com.supsolpans.MainSSP` | `MainSSP` 对应 `redcomponent` 组件；未知公开入口时用精确注册键集中适配 |

#### `TaintedMagic`

运行包：[Tainted-Magic-7.7.8-GTNH.jar](<../build/deepseiko-pack-check/mods/Tainted-Magic-7.7.8-GTNH.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `TaintedMagic:ItemFocusTime@0` | [RecipesVoidNexus:88](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexus.java#L88) | `taintedmagic.common.registry.ItemRegistry`、`taintedmagic.common.items.wand.foci.ItemFocusTime` | `taintedmagic.common.registry.ItemRegistry.ItemFocusTime`；该类只在可选集成门内解析，保留 VoidNexus 的缺失判断 |

#### `Thaumcraft`

运行包：[Thaumcraft-1.7.10-4.2.3.5.jar](<../build/deepseiko-pack-check/mods/Thaumcraft-1.7.10-4.2.3.5.jar>). 未发现同版本 dev 缓存；若有运行包则据运行 jar。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `Thaumcraft:ItemResource@3` | [RecipesTangshanSteelFactory:849](../src/main/java/com/newmaa/othtech/recipe/RecipesTangshanSteelFactory.java#L849) | `thaumcraft.common.config.ConfigItems` | `ConfigItems.itemResource` + meta 3；保持损伤 |
| `Thaumcraft:WandCasting@9000` | [RecipesVoidNexusUpgradePreview:303](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L303) | `thaumcraft.common.config.ConfigItems` | `ConfigItems.itemWandCasting` 不能无参数替换：保持 9000 及 wand rod/cap/vis NBT；本条为预览 |

#### `ThaumicHorizons`

运行包：[ThaumicHorizons-1.8.28.jar](<../build/deepseiko-pack-check/mods/ThaumicHorizons-1.8.28.jar>). 字节码优先使用同版本 `ThaumicHorizons-1.8.28-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `ThaumicHorizons:soulBeacon@0` | [RecipesVoidNexusUpgradePreview:103](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L103) | `com.kentington.thaumichorizons.common.ThaumicHorizons`、`com.kentington.thaumichorizons.common.tiles.TileSoulBeacon` | `ThaumicHorizons` 的 soul beacon 对象/已验证 registry helper；仅预览 |

#### `TwistSpaceTechnology`

运行包：[TwistSpaceTechnology-0.8.0.jar](<../build/deepseiko-pack-check/mods/TwistSpaceTechnology-0.8.0.jar>). 字节码优先使用同版本 `Twist-Space-Technology-Mod-0.8.0-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `TwistSpaceTechnology:MetaItem01@30` | [RecipesSunFactoryEnqing:309](../src/main/java/com/newmaa/othtech/recipe/RecipesSunFactoryEnqing.java#L309) | `com.Nxer.TwistSpaceTechnology.common.init.TstItems`、`com.Nxer.TwistSpaceTechnology.common.item.ItemAdder01` | `GTCMItemList.SpaceTimeSuperconductingInlaidMotherboard.get(n)`；javap `ItemRegister` 显式登记 meta 30 |

#### `universalsingularities`

运行包：[Universal-Singularities-8.15.4.jar](<../build/deepseiko-pack-check/mods/Universal-Singularities-8.15.4.jar>). 字节码优先使用同版本 `Universal-Singularities-8.15.4-dev.jar`。

| 键 | OTH 调用 | jar 证据 | 可替换入口 / 注意 |
|---|---|---|---|
| `universalsingularities:universal.enderIO.singularity@6` | [RecipesVoidNexusUpgradePreview:102](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L102) | `wealthyturtle.uiesingularities.proxy.CommonProxy` 构造中维护组和 meta，注册时拼接 `universal.<组>.singularity` | `CommonProxy.getSingularityByName("enderIO")` + meta 6（`soularium`）；该变种默认禁用生成配方不等于物品未注册，且此调用仅预览 |
| `universalsingularities:universal.vanilla.singularity@3` | [RecipesDeepSeiko:165](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L165) | `wealthyturtle.uiesingularities.proxy.CommonProxy` 构造中维护组和 meta，注册时拼接 `universal.<组>.singularity` | `CommonProxy.getSingularityByName("vanilla")` + meta 3（`water`）；保持各版本 subtype，不能改为独立 Avaritia:Singularity |

### 非字符串和补充跨包入口

| 键/调用 | 本地证据、可替换入口与限制 |
| --- | --- |
| `TwistSpaceTechnology:MetaItem01@30` | TST 0.8.0 dev 的 `ItemRegister` 显式 `bipush 30 -> TstItems.MetaItem01.registerVariant -> GTCMItemList.SpaceTimeSuperconductingInlaidMotherboard.set`；以后可用该枚举 `.get(n)`，超大数量继续用保真数量 helper。 |
| `TST:ProofOfHeroes`、`SolarSail`、`StellarConstructionFrameMaterial`、`GravitationalLens` | TST 0.8.0 dev 的 `ItemRegister` 和 `GTCMItemList`；[Main:1508](../src/main/java/com/newmaa/othtech/recipe/RecipesMain.java#L1508)、[DeepSeiko:1570–1571](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L1570-L1571)、[Void preview:131](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L131) 已用命名入口；最后一项是预览。 |
| `TST:Axonium` 锭、`LiquidMana` | TST 0.8.0 dev：`MaterialsTST.Axonium` + GT ore unification，已用于 [Void Nexus:251–252](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexus.java#L251-L252)；`MaterialPool.LiquidMana` Werkstoff 已由 [AbstractSanctumContent:33](../src/main/java/com/newmaa/othtech/common/materials/AbstractSanctumContent.java#L33) 包装为流体解析入口，不另造同名流体。 |
| `dreamcraft:SchematicsTier1..8@0` | `NHItemList.SchematicsTier1` 至 `SchematicsTier8` `.get(n)`；[NASA:218](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L218)、[305](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L305)、[439](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L439) 为拼接/cap，不是单个 `SchematicsTier`。`.special` 输入量 0；保留前置有效性判断。`HeavyDutyPlateTier4..8` 已在 [NASA:350–354](../src/main/java/com/newmaa/othtech/recipe/RecipesNASA.java#L350-L354) 使用命名枚举；T2..8 火箭的 `RocketRecipes` 从 GalaxySpace 的工作台表继承，不把继承项误记成 OTH 自身写死。 |
| `IC2:reactorVent`、`reactorVentCore`、`reactorVentGold`、`reactorVentDiamond` | [OTEHatchRack:263–267](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L263-L267) 的 `GTModHandler.getIC2Item` 实际进入 [假配方构造器:333–350](../src/main/java/com/newmaa/othtech/machine/hatch/OTEHatchRack.java#L333-L350)，与同名直接 registry `reactorVentDiamond@1` 不可直接认定等价；`Ic2Items` 的命名 stack 直接复制并核查 damage。 |
| `Botania:manaResource@0/1/2/4/5`、`livingrock@0`、`pylon@2`、`overgrowthSeed@0` | Botania 1.13.42-GTNH dev `ModItems` / `ItemManaResource` / `ModBlocks`；[Abstract Sanctum:251](../src/main/java/com/newmaa/othtech/recipe/RecipesAbstractSanctum.java#L251)、[308–319](../src/main/java/com/newmaa/othtech/recipe/RecipesAbstractSanctum.java#L308-L319)、[Void preview:55–57](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L55-L57) 已用对象但 meta 仍硬编码。集中 meta 供该版本适配；不要把精确配方直接换成任意 OreDict。 |
| `Thaumcraft:ItemResource@14`、`blockStoneDevice@5`；`AspectRecipeIndex` 的 `ModItems.itemAspect@0` | AspectRecipeIndex 1.1.6 的 `com.gtnewhorizons.aspectrecipeindex.ModItems.itemAspect`；Thaumcraft 4.2.3.5 `ConfigItems.itemResource`、`ConfigBlocks.blockStoneDevice`；[Sanctum:1145](../src/main/java/com/newmaa/othtech/recipe/RecipesAbstractSanctum.java#L1145)、[Void preview:71](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L71)；[Void Nexus:424](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexus.java#L424) 的瓶类入口应保持 `Aspect` NBT（见源码导入）；无具名 meta 工厂的留在兼容层。 |
| `ThaumicEnergistics:EssentiaProvider`、`EssentiaStorageComponent_16384k`、`EssentiaCell_Creative` | 包版本 1.7.68-GTNH，`ThEApi.instance().blocks()/items()` 定义是现有稳定入口，[Void Nexus:221–224](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexus.java#L221-L224)、[354](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexus.java#L354)、[Void preview:69–70](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L69-L70)。可选时隔离加载并处理 definition 返回空。 |
| `BloodMagic:bloodRune@1/5/6`、`GalacticraftCore:blockMoon@3`、`DraconicEvolution:awakenedCore@0` | BloodMagic 1.9.16 的 `ModBlocks.bloodRune`；Galacticraft 3.4.37 `GCBlocks.blockMoon`；Draconic Evolution 1.5.35 `ModItems.awakenedCore`；[Void preview:75–78](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L75-L78)、[99](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L99)、[110](../src/main/java/com/newmaa/othtech/recipe/RecipesVoidNexusUpgradePreview.java#L110)。已具名的对象不要再退回字符串；只集中 variants。 |
| `FluidRegistry:ic2coolant`、`ic2superheatedsteam` | IC2 2.2.828 `ic2.core.init.BlocksItems` / `InternalName` / Forge `FluidRegistry`；[DeepSeiko:174](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L174)、[944](../src/main/java/com/newmaa/othtech/recipe/RecipesDeepSeiko.java#L944)、[Wooden Fusion:20](../src/main/java/com/newmaa/othtech/recipe/RecipesWoodenFusionReactor.java#L20)；缓存通过同名 Forge 注册流体对象；`new FluidStack(ic2coolant, ...)` 前须判空。 |
| `FluidRegistry:lifeessence` | BloodMagic 1.9.16 的 `AlchemicalWizardry.lifeEssenceFluid` 候选（实际同一 Forge fluid 对象才可换）；[NewWetware:38](../src/main/java/com/newmaa/othtech/recipe/RecipesNewWetware.java#L38)；缺失时不可无条件注册。 |
| `FluidRegistry:creosote` / `bioethanol` / `steam` | Forge 全局键，多模可注册/复用；本地 Railcraft 9.17.36 的 `RailcraftFluids`、Forestry 4.11.40 的 `Fluids` 可用于验证，不能仅凭内容推断运行时 owner。SINOPEC/MISA/Wooden Fusion 保持同名 Forge identity；`steam` 与 `ic2steam` 不能擅自合并。`cryotheum` 也按全局键处理，不能仅凭命名判定为 Thermal 专有；需核对当前加载顺序。 |

**版本差异：** 本地运行包为 Et Futurum `2.6.65-GTNH`、Biomes O’ Plenty `2.1.0.2308`、Railcraft `9.17.36`；项目 [dependencies.gradle:17,44,70](../dependencies.gradle#L17) 开发运行依赖分别固定 Et Futurum `2.7.3-GTNH`、Curse 文件号 `2499612`、Railcraft `9.17.34`，不能用开发解析版本替代运行包证据；TST `0.8.0` 是该实例内额外启用的版本，项目也显式固定 `0.8.0`。

可选依赖原则：`compileOnly` 或 `runtimeOnlyNonPublishable` 不代表 FML 可选。OTH `@Mod` 当前显式 `required-after:dreamcraft`、`required-before:TwistSpaceTechnology`；TST 不只是配方物品，`RecipesDeepSeiko` 还直接引用 TST builder/`setStackSize`、`common.material` 直接引用 TST `MaterialPool`。要变为可选须处理类链接并验证加载阶段，不能仅在下游加一个 `isModLoaded`。对通过 registry 查找的真正可选集成，先验证 mod、物品/流体、meta/NBT，再跳过整条受影响配方；旧配方预览应继续与真实升级条件分开。GT 催化剂/研究物保留 stack size 0，拷贝模板后才改量/损伤/NBT，不能把 `null` 从输入数组过滤掉而使配方更便宜。两个计数参数不能因 helper 顺序不同而互换。
