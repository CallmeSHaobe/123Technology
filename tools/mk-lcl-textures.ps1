# 生成锑罗斯 LCL 海水的动画贴图（红色、半透明）。
#
# 输出（会覆盖现有文件，跑之前先确认改动符合预期）:
#   src/main/resources/assets/123technology/textures/blocks/antimonia/stillLCL.png    16x512  (32 帧)
#   src/main/resources/assets/123technology/textures/blocks/antimonia/flowingLCL.png  32x1024 (32 帧)
#
# 帧数/尺寸必须和同目录下的 .mcmeta 对得上（still 的 mcmeta 写了 frametime=2，flowing 用默认值）。
# 改颜色就改下面的 $baseR/$peakR 等参数；改完重新进游戏（或 F3+T 重载资源）即可看到。

Add-Type -AssemblyName System.Drawing

$dir = Join-Path $PSScriptRoot "..\src\main\resources\assets\123technology\textures\blocks\antimonia"

# 颜色: 从暗红过渡到亮红; alpha 半透明, 保证能看见海床上的公路和建筑
$baseR = 140; $peakR = 115   # R = baseR + peakR * t   (最亮 255)
$baseG = 14;  $peakG = 46
$baseB = 8;   $peakB = 22
$baseA = 150; $peakA = 62    # A = baseA + peakA * t   (半透明)

$tau = [Math]::PI * 2

function Get-PixelColor([double]$t) {
    if ($t -lt 0) { $t = 0 }
    if ($t -gt 1) { $t = 1 }
    $r = [int]($baseR + $peakR * $t)
    $g = [int]($baseG + $peakG * $t)
    $b = [int]($baseB + $peakB * $t)
    $a = [int]($baseA + $peakA * $t)
    return [System.Drawing.Color]::FromArgb($a, $r, $g, $b)
}

function New-Still() {
    $frames = 32; $w = 16; $h = 16
    $bmp = New-Object System.Drawing.Bitmap $w, ($h * $frames), ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($f = 0; $f -lt $frames; $f++) {
        $ph = $f / [double]$frames
        for ($y = 0; $y -lt $h; $y++) {
            for ($x = 0; $x -lt $w; $x++) {
                $a1 = [Math]::Sin($tau * ($x * 2.0 / $w + $y * 1.0 / $h + $ph))
                $a2 = [Math]::Sin($tau * ($x * 1.0 / $w - $y * 2.0 / $h - $ph))
                $a3 = [Math]::Sin($tau * ($x * 3.0 / $w + $y * 3.0 / $h + $ph * 2.0))
                $t = 0.5 + 0.18 * $a1 + 0.18 * $a2 + 0.12 * $a3
                $bmp.SetPixel($x, ($f * $h + $y), (Get-PixelColor $t))
            }
        }
    }
    $bmp.Save((Join-Path $dir "stillLCL.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

function New-Flowing() {
    $frames = 32; $w = 32; $h = 32
    $bmp = New-Object System.Drawing.Bitmap $w, ($h * $frames), ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($f = 0; $f -lt $frames; $f++) {
        $ph = $f / [double]$frames
        for ($y = 0; $y -lt $h; $y++) {
            for ($x = 0; $x -lt $w; $x++) {
                $a1 = [Math]::Sin($tau * ($y / [double]$h + $ph))
                $a2 = [Math]::Sin($tau * (($x * 2.0) / $w - $ph))
                $a3 = [Math]::Sin($tau * (($x * 1.0) / $w + $y / [double]$h))
                $t = 0.5 + 0.22 * $a1 + 0.16 * $a2 + 0.12 * $a3
                $bmp.SetPixel($x, ($f * $h + $y), (Get-PixelColor $t))
            }
        }
    }
    $bmp.Save((Join-Path $dir "flowingLCL.png"), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
}

New-Still
New-Flowing
Write-Host "LCL textures regenerated in $dir"
