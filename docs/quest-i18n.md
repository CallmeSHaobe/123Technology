# 任务书 i18n

BetterQuesting 不直接显示 quest json 里的 `name:8` / `desc:8`，而是先查 lang key，命中就用翻译：

- `betterquesting.quest.<B64>.name` / `.desc`
- `betterquesting.questline.<B64>.name` / `.desc`

`<B64>` = `UuidConverter.encodeUuidStripPadding(uuid)`：UUID 16 字节 Base64URL，去掉 `=`。
文本会过 `I18n.format`，所以 `%` 写成 `%%`、换行写成 `%n`；`Locale` 只在第一个 `=` 处切分，值里可以有 `=`。

- 中文原文在 `src/main/resources/assets/123technology/quest/DefaultQuests/` 的 json 里；
  `zh_CN.lang` 是它的逐字拷贝，`en_US.lang` 是翻译。
- 改了 json 文案要同步 lang，或删掉对应键回落 json。
- 校验：`powershell -ExecutionPolicy Bypass -File tools/check-quest-i18n.ps1`（缺键/重复键/未转义 `%`，失败退出码 1）。
- 新任务：`/bq_admin default export_lang` 会生成已转义的 `template.lang`，复制进 lang 文件翻译即可。
