"""Generate orthogonal pixel glyph textures for the drone platform. Requires Pillow."""
from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'src/main/resources/assets/123technology/textures/gui/drone'
OUT.mkdir(parents=True, exist_ok=True)
# Every stroke is horizontal or vertical; 2 follows top/right/middle/left/bottom.
patterns = {
    '1': ['00100', '00100', '00100', '00100', '00100', '00100', '00100'],
    '2': ['11111', '00001', '00001', '11111', '10000', '10000', '11111'],
    '3': ['11111', '00001', '00001', '11111', '00001', '00001', '11111'],
    't': ['11111', '00100', '00100', '00100', '00100', '00100', '00100'],
    'e': ['11111', '10000', '10000', '11111', '10000', '10000', '11111'],
    'c': ['11111', '10000', '10000', '10000', '10000', '10000', '11111'],
}
colors = [(255, 255, 255, 255), (255, 228, 181, 255), (85, 170, 255, 255)]
for index, (symbol, rows) in enumerate(patterns.items()):
    image = Image.new('RGBA', (10, 10))
    draw = ImageDraw.Draw(image)
    for y, row in enumerate(rows):
        for x, bit in enumerate(row):
            if bit == '1':
                draw.point((x + 2, y + 1), fill=colors[index % 3])
    image.save(OUT / f'glyph_{symbol}.png')
print(f'Generated {len(patterns)} orthogonal glyph textures in {OUT}')
