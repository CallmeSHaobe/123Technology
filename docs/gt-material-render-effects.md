# GT 材料「特殊特效」是怎么实现的（无尽 / 超时空金属 / 宇宙素 …）

> 结论先说：**特效不在材质里，而是每个 `Materials` 实例上挂了一个客户端 `IItemRenderer`。**
> 贴图走 `TextureSet`（`materialicons/CUSTOM/<set>/<suffix>.png`，动画靠 `.png.mcmeta`），
> 额外花活（光晕、旋转、shader、色差）全部是渲染器里自己写 GL。
> 依据：GT5U 5.09.54.265 源码，行号为该版本源码行。

---

## 1. 机制总览

### 1.1 挂载点

| 位置 | 内容 |
|---|---|
| `gregtech/api/enums/Materials.java:1159` | `public GeneratedMaterialRenderer renderer;` —— 每个材料一个字段（默认 `null`） |
| `Materials.initClient()` | 客户端 preInit 里由 `GTClient.onPreInitialization`（`gregtech/common/GTClient.java:192`，调用点 L202）执行，逐条 `Materials.Xxx.renderer = new XxxRenderer();` |
| `gregtech/api/interfaces/IGT_ItemWithMaterialRenderer` | 物品侧协议：`getMaterialRenderer(int meta)` / `getIcon(meta,pass)` / `getOverlayIcon(meta,pass)` / `getRGBa(stack)` / `allowMaterialRenderer(meta)` / `shouldUseCustomRenderer(meta)` |
| `MetaGeneratedItemX32.getMaterialRenderer(meta)` | `meta` 是 `前缀序号 * 1000 + 材料 subID`；`Materials.isMaterialItem(meta)` = `0 <= meta < 32000`，成立就返回 `materials.renderer` |
| `MetaGeneratedItem.getMaterialRenderer(meta)` | 返回 `null`（普通 metaitem 不自带材料渲染器，交给统一分发器） |

### 1.2 分发链（谁来决定用哪个 renderer）

```
MinecraftForgeClient.registerItemRenderer(item, metaItemRenderer)      // GTClient L230：对 MetaGeneratedItem.sInstances.values() 全部注册
  └─ MetaGeneratedItemRenderer.getRendererForItemStack(stack)          // gregtech/common/render/items/MetaGeneratedItemRenderer.java
       1) specialRenderers.get(pack(item, meta))      ← 单 meta 特例（registerSpecialRenderer 注册，如 DataStick / 无限喷漆罐）
       2) aItem.getMaterialRenderer(meta)             ← 材料渲染器（本文件主角）
       3) GTOreDictUnificator.getAssociation(stack)   ← meta ≥ 32000 时用矿辞反查 itemData.mMaterial.mMaterial.renderer（GT++/BW 的物品也能吃到）
       4) 兜底：mMaterialRenderer = new GeneratedMaterialRenderer()（材料物品）/ mItemRenderer = new GeneratedItemRenderer()（非材料物品）
```

### 1.3 基类给的三个钩子

`gregtech/common/render/items/GeneratedMaterialRenderer.java`：

```java
renderItem(type, stack, data) {          // 默认实现
    for (pass ...) {
        tIcon    = aItem.getIcon(meta, pass);
        tOverlay = aItem.getOverlayIcon(meta, pass);
        renderRegularItem(type, stack, tIcon, aFluid == null, pass, data);   // ① 本体（颜色调制 + 贴图）
        renderContainedFluid(type, aFluid, fluidIcon);                      // ② 单元格里的流体
        renderItemOverlay(type, tOverlay);                                  // ③ 覆盖层
    }
}
```

三个方法都是 `protected`，子类基本只需要重写它们（要改整体流程才重写 `renderItem`）。
`handleRenderType()` 默认只处理 `EQUIPPED / EQUIPPED_FIRST_PERSON / INVENTORY / ENTITY` —— **包含 `INVENTORY` 才能在背包和 NEI 里看到特效**。

### 1.4 `Materials.initClient()` 里的完整名单（13 个）

| 材料 | 渲染器 | 效果 | 开关（`gregtech/common/config/Client.java`） |
|---|---|---|---|
| `TranscendentMetal`（超时空金属） | `TranscendentMetalRenderer` | 物品绕轴自转 | `render.renderTransMetalFancy`（L286） |
| `Infinity`, `Eternity`, `MagMatter`, `GravitonShard`, `ExoHalkonite`, `HotExoHalkonite`, `Amalgatite` | `InfinityRenderer` | 光晕 + 脉动 + 动画贴图 | `render.renderInfinityFancy`（L291） |
| `CosmicNeutronium` | `CosmicNeutroniumRenderer` | 模糊光晕 + 材质配色 | `render.renderCosmicNeutroniumFancy`（L296） |
| `Universium` | `UniversiumRenderer` | GLSL 星空 shader 叠加 | `render.renderUniversiumFancy`（L306） |
| `SixPhasedCopper` | `GlitchEffectRenderer` | 红/青 RGB 分离故障帧 | `render.renderGlitchFancy`（L301） |
| `PrismaticNaquadah` | `RainbowOverlayRenderer(rgba)` | HSB 彩虹循环 | 无（始终开） |
| `GaiaSpirit` | `GaiaSpiritRenderer` | HSB 循环（周期 180 帧） | 无 |

---

## 2. 逐个拆解

### 2.1 无尽 `InfinityRenderer`（141 行）

重写了整个 `renderItem`，`INVENTORY` 时在画本体之前加两层：

1. **光晕** `renderHalo()`：取 `Textures.ItemIcons.HALO` 贴图，用 `Tessellator` 画一个从 `-10` 到 `26`（四周各扩 10 像素）的四边形，颜色 `20/255` 压暗、alpha `0xFF`；先 `glDisable(ALPHA_TEST/DEPTH_TEST)`，画完再恢复。
2. **脉动** `renderPulse(tOverlay, tIcon)`：同一张贴图再画一遍，`scale = 0.95 ± 0.15 * nextGaussian()`（每帧随机抖动），顶点色 alpha `0.6`，`glScalef` 居中偏移 `(1-scale)/2`。

加上 `TextureSet.SET_INFINITY`（`CUSTOM/infinity/*.png` + 每帧动画的 `.mcmeta`），就形成「流光 + 呼吸光晕」的无尽观感。

### 2.2 超时空金属 `TranscendentMetalRenderer`（178 行）

不用 Tessellator 画平面，而是 `ItemRenderer.renderItemIn2D(...)` 画成 3D 薄片（`0.0625F` 厚度），然后每帧：

```java
GL11.glRotatef((GTMod.clientProxy().getAnimationRenderTicks() * 3.5f) % 360, 0.3f, 0.5f, 0.2f);
GL11.glRotatef(180, 0.5f, 0.0f, 0.0f);
```

即绕 `(0.3,0.5,0.2)` 轴匀速自转，外加 180° 翻转让背面法线朝向正确。`renderContainedFluid` / `renderItemOverlay` 也各重写一遍，保证「薄片 + 流体 + 覆盖层」三者的旋转一致；`INVENTORY` 分支先 `glScalef(16,16,32)` 再翻转 UV。
另外 `ENTITY` 分支在关掉 fancy 图形时会手工补 `glTranslatef(-0.5F,-0.25F,0.0421875F)`（vanilla 常量）让掉落物仍正常呈现。

### 2.3 宇宙素 `UniversiumRenderer`（174 行）

唯一用 shader 的：`com.gtnewhorizon.gtnhlib.client.renderer.postprocessing.shaders.UniversiumShader`。

- 正常画一遍本体 → `GL11.glDepthFunc(GL_EQUAL)` → `shader.use()` 画第二遍（只在已画过的像素上叠星空）→ `UniversiumShader.unbind()` → 恢复 `GL_LEQUAL`。
- **光照进 shader**：`processLightLevel()` 按渲染类型取光 —— `ENTITY` 用 `EntityItem` 坐标、`EQUIPPED*` 用玩家坐标调 `shader.setLightFromLocation(...)`，其余 `setLightLevel(1.0f)`。所以它看起来是「跟着环境明暗变化的星空」。
- 背包/NEI 走 `shader.setRenderInInventory()`，且要先 `RenderHelper.enableGUIStandardItemLighting()`。
- 单元格里的流体也能吃这个效果：`renderFluidDisplayItem()` 返回 `true` 表示自己接管。

### 2.4 宇宙中子素 `CosmicNeutroniumRenderer`（94 行）

`Textures.ItemIcons.HALO_FUZZY` 光晕，alpha 固定 `0x4D`（半透明模糊边），本体用 `GTRenderUtil.colorGTItem(item)` 上材质色；背包里用 `ItemRenderUtil.renderItemIcon(icon, 16.0D, 0.001D, 0, 0, -1)`（z 抬 0.001 防 z-fighting）。

### 2.5 盖亚之魂 / 彩虹 `GaiaSpiritRenderer` / `RainbowOverlayRenderer`

最省事的「变色流」，不碰几何、不碰贴图，只调 `glColor`：

```java
float animationTicks = GTMod.clientProxy().getAnimationRenderTicks();
Color c = Color.getHSBColor((animationTicks % 180) / 180f, 0.4f, 0.9f);  // 盖亚：180 帧一圈
GL11.glColor3f(c.getRed()/255f, c.getGreen()/255f, c.getBlue()/255f);
```

彩虹（`RecipesCircuit` 里那张 `PrismaticNaquadah`）周期 90 帧，并且把 HSB 结果与材料基色**相乘**（构造器里存 `baseR/G/B`），这样仍保留材料本色。

### 2.6 六相铜 `GlitchEffectRenderer`（136 行）

帧计时：`currentFrame = (System.nanoTime() % (10ms * 200 帧)) / 10ms`，前 40 帧是「故障态」，每 5 帧把红/青偏移量重置为 `±1.7 px` 的高斯随机；然后：

```java
GL11.glDisable(GL11.GL_DEPTH_TEST);
applyGlitchEffect(type, shouldModulateColor, offsetCyan, cyan, icon, overlay);  // 0,220,220,160
GL11.glEnable(GL11.GL_DEPTH_TEST);
applyGlitchEffect(type, shouldModulateColor, offsetRed,  red,   icon, overlay);  // 255,50,50,192
```

即把本体贴图用两种颜色各画一份、偏移 1~2 像素，做出 RGB 分离的电子故障感；只在 `INVENTORY` 生效（`applyGlitchEffect` 第一行就 `if (!INVENTORY) return;`）。

### 2.7 只对单个 meta 生效的 `*MetaItemRenderer`

`InfinityMetaItemRenderer` / `TranscendentalMetaItemRenderer` / `UniversiumMetaItemRenderer` / `CosmicNeutroniumMetaItemRenderer` / `RainbowOverlayMetaItemRenderer` / `GlitchEffectMetaItemRenderer` 是同样效果、但**绑死在某个具体物品/ meta 上**的版本，通过：

```java
GTMod.clientProxy().metaItemRenderer.registerSpecialRenderer(ItemList.X, renderer);   // ItemList.set(stack, renderer) 内部也是这句
```

用于 GT++ / bartworks 那种 `meta ≥ 32000`、走不到 `Materials.renderer` 的物品（它们被分发链第 3 步的矿辞反查兜住，或者走这里的 special 表）。

---

## 3. 贴图侧：`TextureSet` 与 `CUSTOM/` 目录

```java
// gregtech/api/enums/TextureSet.java
public static final TextureSet SET_INFINITY = new TextureSet("infinity", true);
public TextureSet(String aSetName, boolean isCustom) {
    this("CUSTOM/" + aSetName);      // mSetName = "CUSTOM/infinity"
    this.is_custom = isCustom;
}
```

构造时按 `MaterialIconRegistry.IconType` 逐个建 `IIconContainer`，路径规则：

- `ITEM` → `gregtech:textures/items/materialicons/CUSTOM/infinity/<suffix>.png`；
- `BLOCK` → `blocks/materialicons/CUSTOM/...`；
- `BLOCK_WITH_ALPHA` → 带 alpha 的矿石类；
- `IconType.DUST("/dust")`、`INGOT("/ingot")`、`PLATE_DENSE("/plateDense")` … 共 ~180 个 `suffix`（`MaterialIconRegistry.IconType` 枚举就是这张对照表，改它必须只在末尾追加）。

`assets/gregtech/textures/items/materialicons/CUSTOM/infinity/` 里每个 png 都带同名 `.png.mcmeta` —— **动画帧是原版贴图动画机制做的，代码里没有任何计时器**。

所以「给新材料做特效」的第一件事不是写渲染器，而是：抄一份 `CUSTOM/<你的名字>/` 贴图目录（可以只放你要用的几个前缀），并 `new TextureSet("你的名字", true)` 挂到材料上。

---

## 4. 自己照抄：三步 + 注意事项

### 步骤 1 · 贴图
`src/main/resources/assets/<yourmod>/textures/items/materialicons/CUSTOM/<setName>/{dust,ingot,plateDense,...}.png`
想要动画就配 `<name>.png.mcmeta`（`{"animation":{"frametime":2,"frames":[...]}}`）。
注意 GT 的 `Textures.ItemIcons.textureSet(domain, set, suffix)` 是按 **gregtech 域** 拼路径的；用自己域时得用 `Textures.ItemIcons.custom(...)` 或自己的 `IIconContainer`，简单做法是直接用 GT 的 `TextureSet` + 把贴图放进 `assets/gregtech/...`（会污染 GT 资源域，因此更推荐自建 `IIconContainer`）。

### 步骤 2 · 渲染器

```java
package com.newmaa.othtech.client.render;

import java.awt.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;
import com.gtnewhorizon.gtnhlib.util.ItemRenderUtil;
import gregtech.GTMod;
import gregtech.common.render.items.GeneratedMaterialRenderer;

/** 无限流光 + 彩虹调色，参考 GT 的 InfinityRenderer + RainbowOverlayRenderer。 */
public class OTERainbowFlowRenderer extends GeneratedMaterialRenderer {

    @Override
    protected void renderRegularItem(ItemRenderType type, ItemStack stack, IIcon icon, boolean modulateColor) {
        if (modulateColor && stack.getItem() instanceof IGT_ItemWithMaterialRenderer item) {
            short[] rgba = item.getRGBa(stack);
            float ticks = GTMod.clientProxy().getAnimationRenderTicks();
            Color rainbow = Color.getHSBColor((ticks % 120) / 120f, 0.55f, 1.0f);
            GL11.glColor3f(
                rgba[0] / 255f * rainbow.getRed() / 255f,
                rgba[1] / 255f * rainbow.getGreen() / 255f,
                rgba[2] / 255f * rainbow.getBlue() / 255f);
        }
        ItemRenderUtil.renderItem(type, icon);
    }

    @Override
    protected void renderItemOverlay(ItemRenderType type, IIcon overlay) {
        if (type == ItemRenderType.INVENTORY) {
            // 复用 GT 的脉动：同一层再画一遍、随机缩放、半透明
            float scale = 0.95f + 0.15f * (float) RANDOM.nextGaussian();
            float offset = (1.0f - scale) / 2.0f;
            GL11.glPushMatrix();
            GL11.glTranslatef(offset * 16.0f, offset * 16.0f, 1.0f);
            GL11.glScalef(scale, scale, 1.0f);
            GL11.glColor4f(1f, 1f, 1f, 0.6f);
            ItemRenderUtil.renderItem(type, overlay);
            GL11.glPopMatrix();
        } else {
            super.renderItemOverlay(type, overlay);
        }
    }
}
```

### 步骤 3 · 注册（客户端、必须晚于 GT 的 `Materials.initClient()`）

```java
// 客户端代理 / FMLLoadCompleteEventHandler 里
Materials.MyMaterial.renderer = new OTERainbowFlowRenderer();
```

- GT 只在 `Materials.initClient()` 里给它自己那 13 个材料赋值，**不会覆盖**别人的材料，所以任意更晚的时机都安全（`postInit`、`FMLLoadCompleteEvent` 最稳）。
- 若材料是 GT++ / bartworks 的（meta ≥ 32000，走不到 `Materials.renderer`）：要么 `GTMod.clientProxy().metaItemRenderer.registerSpecialRenderer(item, renderer)`，要么像 `bartworks/client/renderer/CircuitPartItemsRenderer` 那样自己 `MinecraftForgeClient.registerItemRenderer(item, renderer)`。

### 通用注意事项
1. `handleRenderType` 必须含 `INVENTORY`，否则背包/NEI 无特效（`ENTITY` 决定掉落物，`EQUIPPED*` 决定手持）。
2 .GL 状态必须成对：`BLEND/ALPHA_TEST/DEPTH_TEST/LIGHTING`、`glPushMatrix/glPopMatrix`；NEI 渲染与游戏内渲染共享 GL 栈，漏 pop 会污染整个 GUI。
3. 想用 GT 自带的光晕：`Textures.ItemIcons.HALO` / `HALO_FUZZY`（`Textures.java:2889`）。
4. 帧时间统一用 `GTMod.clientProxy().getAnimationRenderTicks()`（`GTClient.java:530`，`mAnimationTick + renderTickTime`）；不依赖 GT 的话用 `System.currentTimeMillis()` 折算。
5. 自己加配置开关，别复用 `gregtech.common.config.Client.render.*`。

---

## 5. 一句话总结

| 层 | 负责什么 |
|---|---|
| `TextureSet` + `materialicons/CUSTOM/<set>/`（+ `.mcmeta`） | 底图与逐帧动画（无尽那种「流动」） |
| `Materials.renderer`（`GeneratedMaterialRenderer` 子类） | 光晕 / 脉动 / 旋转 / shader / 色差等一切动态花活 |
| `MetaGeneratedItemRenderer` + `IGT_ItemWithMaterialRenderer` | 把物品 meta 解析成「哪个材料的哪个前缀」，再派发到上面的渲染器 |
| `registerSpecialRenderer` / `MinecraftForgeClient` | 非 GT 材料物品（meta ≥ 32000）的特效入口 |
