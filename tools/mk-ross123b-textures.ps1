# 生成 Ross123b 的星球图标：红色海洋（占主体）+ 左上角锑白色大陆。
#
# 输出（会覆盖现有文件）：
#   src/main/resources/assets/123technology/textures/gui/celestialbodies/antimonia/ross123b.png
#   src/main/resources/assets/gtneioreplugin/textures/blocks/R3_{top,bottom,front,back,left,right}.png
#   src/main/resources/assets/123technology/textures/blocks/R3_{top,bottom,front,back,left,right}.png
#   build/ross123b-preview.png（10x 放大预览，不进 jar）
#
# 做法和原来那批星球图标一致：先在 $SS 倍分辨率下用球面光照 + 平滑渐变把整张图算出来，
# 再用高质量双线性缩到 12x12，所以是柔和的"照片感"而不是硬边像素块。
# 陆地形状 = 左上角一个圆形范围 + 几层正弦扰动（海岸线） + 往右下渐隐（保证海洋占主体）。

Add-Type -AssemblyName System.Drawing

$root = Join-Path $PSScriptRoot "..\src\main\resources\assets"
$build = Join-Path $PSScriptRoot "..\build"
$SS = 96          # 超采样边长
$OUT = 12         # 最终边长

# 配色（sRGB）
$deepOceanR = 0x54; $deepOceanG = 0x0B; $deepOceanB = 0x06
$midOceanR = 0x9C; $midOceanG = 0x17; $midOceanB = 0x0C
$hotOceanR = 0xC9; $hotOceanG = 0x2E; $hotOceanB = 0x18
$landDarkR = 0xA8; $landDarkG = 0xAE; $landDarkB = 0xB7
$landLitR = 0xF1; $landLitG = 0xF4; $landLitB = 0xF7

function Clamp01([double]$x) {
    if ($x -lt 0) { return 0.0 }
    if ($x -gt 1) { return 1.0 }
    return $x
}

function Lerp([double]$a, [double]$b, [double]$t) { return $a + ($b - $a) * $t }

$big = New-Object System.Drawing.Bitmap $SS, $SS, ([System.Drawing.Imaging.PixelFormat]::Format24bppRgb)

for ($y = 0; $y -lt $SS; $y++) {
    $v = ($y + 0.5) / $SS * 2.0 - 1.0
    for ($x = 0; $x -lt $SS; $x++) {
        $u = ($x + 0.5) / $SS * 2.0 - 1.0

        # 球面光照：中心亮、四周暗，左上稍亮
        $rr = [Math]::Sqrt($u * $u + $v * $v) / 1.3
        $light = Clamp01(1.1 - 0.66 * $rr)
        $light = Clamp01($light * (0.93 - 0.09 * ($u + $v)))

        # 海洋：暗红 -> 亮红
        $tOcean = Clamp01($light * 1.05)
        if ($tOcean -lt 0.55) {
            $k = $tOcean / 0.55
            $oR = Lerp $deepOceanR $midOceanR $k
            $oG = Lerp $deepOceanG $midOceanG $k
            $oB = Lerp $deepOceanB $midOceanB $k
        } else {
            $k = ($tOcean - 0.55) / 0.45
            $oR = Lerp $midOceanR $hotOceanR $k
            $oG = Lerp $midOceanG $hotOceanG $k
            $oB = Lerp $midOceanB $hotOceanB $k
        }

        # 大陆：左上角一块，海岸线用几层正弦扰动，越往右下越淡出
        $du = $u + 0.40
        $dv = ($v + 0.46) * 1.02
        $warp = 0.16 * [Math]::Sin(2.9 * $u * 3.1 + 0.9) +
        0.11 * [Math]::Cos(3.4 * $v * 3.0 - 0.5) +
        0.06 * [Math]::Sin(6.1 * ($u + $v) + 2.1)
        $d = [Math]::Sqrt($du * $du + $dv * $dv) + $warp
        $land = Clamp01((0.60 - $d) / 0.20)
        $land *= Clamp01((0.38 - ($u + $v) * 0.8) / 0.46)
        $land *= Clamp01((0.95 - [Math]::Max([Math]::Abs($u), [Math]::Abs($v))) / 0.28)
        $land = Clamp01($land)

        # 云带：只压在海面上，给柔和的照片感加点纹理
        $cloud = [Math]::Pow(0.5 + 0.5 * [Math]::Sin(5.1 * $u - 2.3 * $v + 1.6), 6.0) * 0.16
        $oR = Lerp $oR 0xF0 $cloud
        $oG = Lerp $oG 0xEE $cloud
        $oB = Lerp $oB 0xEA $cloud

        # 陆地明暗跟着光照走，白里带一点冷灰
        $tLand = Clamp01(0.25 + $light * 1.05)
        $lR = Lerp $landDarkR $landLitR $tLand
        $lG = Lerp $landDarkG $landLitG $tLand
        $lB = Lerp $landDarkB $landLitB $tLand

        $r = Lerp $oR $lR $land
        $g = Lerp $oG $lG $land
        $b = Lerp $oB $lB $land

        $big.SetPixel($x, $y, [System.Drawing.Color]::FromArgb([int]$r, [int]$g, [int]$b))
    }
}

$small = New-Object System.Drawing.Bitmap $OUT, $OUT, ([System.Drawing.Imaging.PixelFormat]::Format24bppRgb)
$gfx = [System.Drawing.Graphics]::FromImage($small)
$gfx.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBilinear
$gfx.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
$gfx.DrawImage($big, 0, 0, $OUT, $OUT)
$gfx.Dispose()
$big.Dispose()

$icon = Join-Path $root "123technology\textures\gui\celestialbodies\antimonia\ross123b.png"
$small.Save($icon, [System.Drawing.Imaging.ImageFormat]::Png)

$faces = @("top", "bottom", "front", "back", "left", "right")
foreach ($dir in @("gtneioreplugin\textures\blocks", "123technology\textures\blocks")) {
    $out = Join-Path $root $dir
    if (-not (Test-Path $out)) { New-Item -ItemType Directory -Path $out -Force | Out-Null }
    foreach ($face in $faces) {
        $small.Save((Join-Path $out "R3_$face.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    }
}

$preview = New-Object System.Drawing.Bitmap 120, 120
$pg = [System.Drawing.Graphics]::FromImage($preview)
$pg.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$pg.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
$pg.DrawImage($small, 0, 0, 120, 120)
$pg.Dispose()
$preview.Save((Join-Path $build "ross123b-preview.png"), [System.Drawing.Imaging.ImageFormat]::Png)
$preview.Dispose()
$small.Dispose()

Write-Host "wrote $icon and 12 block faces"
