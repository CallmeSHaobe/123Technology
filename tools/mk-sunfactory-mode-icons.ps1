# Generates the 16x16 pixel-art mode icons of the Sun Factory controller (OTESunFactory).
#
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools/mk-sunfactory-mode-icons.ps1
#
# Output: src/main/resources/assets/123technology/textures/gui/overlay_button/sunfactory_mode_*.png
#         tools/icon-preview.png  (buttons rendered with the "button" palette, then the "gt" palette)
#
# The art is drawn pixel by pixel from the character maps below, so every icon can be tweaked by editing its 16
# strings. Tone characters are palette independent, which lets the same art be rendered in either colour scheme:
#
#   .  transparent      K  outline             N  darkest detail
#   D  dark             M  mid                 L  body / light          W  highlight / brightest
#
# Palettes:
#   gt     - the tones GT uses for its own machine mode / toggle overlays (golden yellow ramp)
#   button - the exact tones of GT's standard GUI button (white bevel, #8B8B8B face, #373737 border)
$style = 'gt'   # 'gt' or 'button'

Add-Type -AssemblyName System.Drawing

$scriptDir = $PSScriptRoot
$repoRoot = Split-Path -Parent $scriptDir
$outDir = Join-Path $repoRoot 'src\main\resources\assets\123technology\textures\gui\overlay_button'
New-Item -ItemType Directory -Force $outDir | Out-Null

function C([int]$r, [int]$g, [int]$b) { [System.Drawing.Color]::FromArgb(255, $r, $g, $b) }

$palettes = @{
    gt     = @{
        'N' = (C 0x8c 0x8c 0x14)   # deepest gold - holes, dies, contents
        'K' = (C 0xb1 0xb1 0x1b)   # dark gold outline
        'D' = (C 0xca 0xca 0x28)   # body
        'M' = (C 0xeb 0xeb 0x13)   # lit body
        'L' = (C 0xeb 0xeb 0x13)   # lit body
        'W' = (C 0xff 0xff 0x1f)   # highlight
    }
    button = @{
        'N' = (C 0x20 0x20 0x20)   # darkest detail
        'K' = (C 0x37 0x37 0x37)   # #373737 - button border / shadow
        'D' = (C 0x5a 0x5a 0x5a)   # shadowed face
        'M' = (C 0x8b 0x8b 0x8b)   # #8b8b8b - button face
        'L' = (C 0xc7 0xc7 0xc7)   # lit face
        'W' = (C 0xff 0xff 0xff)   # #ffffff - button bevel highlight
    }
}

function New-Icon([string[]]$art, $pal) {
    $bmp = [System.Drawing.Bitmap]::new(16, 16, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    for ($y = 0; $y -lt 16; $y++) {
        $row = $art[$y]
        if ($row.Length -ne 16) { throw "row $y has length $($row.Length), expected 16" }
        for ($x = 0; $x -lt 16; $x++) {
            $ch = [string]$row[$x]
            if ($ch -eq '.') { $bmp.SetPixel($x, $y, [System.Drawing.Color]::FromArgb(0, 0, 0, 0)) }
            else { $bmp.SetPixel($x, $y, $pal[$ch]) }
        }
    }
    return $bmp
}

# ---------------------------------------------------------------- 0: PCB factory (SF1)
# a bare board with a 3x3 field of vias / through holes
$artPcb = @(
    '................',
    '................',
    '..KKKKKKKKKKKK..',
    '..KWWWWWWWWWWK..',
    '..KWLLLLLLLLDK..',
    '..KWLLNLLNLLDK..',
    '..KWLLLLLLLLDK..',
    '..KWLLNLLNLLDK..',
    '..KWLLLLLLLLDK..',
    '..KWLLNLLNLLDK..',
    '..KWLLLLLLLLDK..',
    '..KWLLLLLLLLDK..',
    '..KDDDDDDDDDDK..',
    '..KKKKKKKKKKKK..',
    '................',
    '................'
)

# ---------------------------------------------------------------- 1: SMD workshop (SF2)
# a chip with three leads on each side, like a soldered SOIC
$artSmd = @(
    '................',
    '................',
    '................',
    '....KKKKKKKK....',
    '..KKKWWWWWWKKK..',
    '....KDDDDDDK....',
    '..KKKDDDDDDKKK..',
    '....KDDDDDDK....',
    '..KKKDDDDDDKKK..',
    '....KDDDDDDK....',
    '....KKKKKKKK....',
    '................',
    '................',
    '................',
    '................',
    '................'
)

# ---------------------------------------------------------------- 2: NMD wafer fab (SF3)
# silicon wafer with a handful of dies and the flat notch at the bottom
$artWafer = @(
    '................',
    '.....KKKKKK.....',
    '...KLLLLLLLLK...',
    '..KLLWLLLLLLLK..',
    '.KLLWLLLLLLLLLK.',
    '.KLLLLLNLLLLLLK.',
    '.KLLLLLLLLLLLLK.',
    '.KLLLNDLLLDLLNK.',
    '.KLLLLLLLLLLLLK.',
    '.KLLLLLNLLLLLLK.',
    '.KLLLLLLLLLLLLK.',
    '.KLLLLLLLLLLLLK.',
    '..KLLLLLLLLLLK..',
    '...KKKKDDKKKK...',
    '................',
    '................'
)

# ---------------------------------------------------------------- 3: component aggregator (SF4)
# a crate that already holds parts, with one more part on its lid
$artComponent = @(
    '................',
    '................',
    '....KKKKKKKK....',
    '....KWWWWWWK....',
    '....KDDDDDDK....',
    '....KKKKKKKK....',
    '..KKKKKKKKKKKK..',
    '..KWWWWWWWWWWK..',
    '..KWLLLLLLLLDK..',
    '..KWLNLLLLNLNK..',
    '..KWLLLLLLLLDK..',
    '..KWLNLLLLNLNK..',
    '..KDDDDDDDDDDK..',
    '..KKKKKKKKKKKK..',
    '................',
    '................'
)

$icons = [ordered]@{
    'sunfactory_mode_pcb'       = $artPcb
    'sunfactory_mode_smd'       = $artSmd
    'sunfactory_mode_wafer'     = $artWafer
    'sunfactory_mode_component' = $artComponent
}

foreach ($name in $icons.Keys) {
    $bmp = New-Icon $icons[$name] $palettes[$style]
    $bmp.Save("$outDir\$name.png", [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Host "wrote $outDir\$name.png  ($style palette)"
}

# ---------------------------------------------------------------- preview: 8x, on a mock standard button
$scale = 8
$btn = 18
$gap = 10
$cell = $btn * $scale
$rows = @('button', 'gt')
$sheet = [System.Drawing.Bitmap]::new(($cell + $gap) * $icons.Count + $gap, ($cell + $gap) * $rows.Count + $gap)
$g = [System.Drawing.Graphics]::FromImage($sheet)
$g.Clear([System.Drawing.Color]::FromArgb(255, 78, 80, 84))
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
$g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::Half
$frame = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 0x8b, 0x8b, 0x8b))
$inner = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 0x9d, 0x9d, 0x9d))
$edge = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 0x37, 0x37, 0x37), $scale)
$bevel = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 0xff, 0xff, 0xff), $scale)
foreach ($rowPal in $rows) {
    $rowIndex = [array]::IndexOf($rows, $rowPal)
    $i = 0
    foreach ($name in $icons.Keys) {
        $x = $gap + $i * ($cell + $gap)
        $y = $gap + $rowIndex * ($cell + $gap)
        $g.FillRectangle($frame, $x, $y, $cell, $cell)
        $g.DrawRectangle($bevel, ($x + $scale), ($y + $scale), ($cell - 2 * $scale), ($cell - 2 * $scale))
        $g.DrawRectangle($edge, $x, $y, $cell, $cell)
        $g.FillRectangle($inner, ($x + 2 * $scale), ($y + 2 * $scale), ($cell - 4 * $scale), ($cell - 4 * $scale))
        $bmp = New-Icon $icons[$name] $palettes[$rowPal]
        $g.DrawImage($bmp, ($x + $scale), ($y + $scale), 16 * $scale, 16 * $scale)
        $bmp.Dispose()
        $i++
    }
}
$frame.Dispose()
$inner.Dispose()
$edge.Dispose()
$bevel.Dispose()
$g.Dispose()
$sheet.Save((Join-Path $scriptDir 'icon-preview.png'), [System.Drawing.Imaging.ImageFormat]::Png)
$sheet.Dispose()
Write-Host "preview: $(Join-Path $scriptDir 'icon-preview.png')  (row 1 = button palette, row 2 = GT yellow palette)"
