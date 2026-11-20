"""Generate PIG drone skins without changing the original mesh or texture.

Run: python tools/generate_drone_skins.py
Dependencies: Pillow, numpy. Outputs six model assets and build/drone-skins-preview.png.
The preview is a CPU z-buffer render of the generated OBJ UVs, not an illustration.
"""
from pathlib import Path
import hashlib
import json
import math

import numpy as np
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
MODELS = ROOT / 'src/main/resources/assets/123technology/models'
BUILD = ROOT / 'build'
SIZE = 1024
CELL = 320
# Each basis satisfies cross(screen-right, screen-up) == outward normal.
CHARTS = {
    'front': ((-1, 0, 0), (0, 1, 0), (0, 0, -1)),
    'right': ((0, 0, -1), (0, 1, 0), (1, 0, 0)),
    'left': ((0, 0, 1), (0, 1, 0), (-1, 0, 0)),
    'top': ((1, 0, 0), (0, 0, -1), (0, 1, 0)),
    'bottom': ((1, 0, 0), (0, 0, 1), (0, -1, 0)),
    'back': ((1, 0, 0), (0, 1, 0), (0, 0, 1)),
}
CENTER = np.array([0, 0, (-2.025476 - 1.713147) / 2])
# A square world-space scale preserves logo aspect ratio on every projection.
EXTENT = .48


def font(size):
    for name in ('C:/Windows/Fonts/arialbd.ttf', 'DejaVuSans-Bold.ttf'):
        try:
            return ImageFont.truetype(name, size)
        except OSError:
            pass
    return ImageFont.load_default(size=size)


def emblem(kind, base):
    im = Image.new('RGB', (640, 640), base)
    d = ImageDraw.Draw(im)
    if kind == 'nuclear':
        # Standard radiation trefoil: three 60-degree blades and central dot.
        d.ellipse((95, 95, 545, 545), fill=(255, 212, 20))
        for start in (-90, 30, 150):
            d.pieslice((132, 132, 508, 508), start, start + 60, fill=(12, 12, 12))
        d.ellipse((258, 258, 382, 382), fill=(255, 212, 20))
        d.ellipse((282, 282, 358, 358), fill=(12, 12, 12))
    else:
        green = (118, 185, 0)
        # Original vector drawing of the recognizable NVIDIA angular eye/spiral.
        # No external files, copyrighted raster downloads, or network required.
        outer = [(92, 277), (169, 202), (265, 166), (373, 174), (476, 228),
                 (548, 300), (477, 369), (386, 408), (283, 405), (202, 364),
                 (157, 305), (210, 254), (272, 226), (342, 229), (410, 268),
                 (444, 304), (402, 343), (349, 363), (292, 354), (257, 326),
                 (254, 292), (288, 267), (326, 265), (360, 286), (377, 307),
                 (351, 329), (318, 330), (302, 313), (308, 293), (328, 292),
                 (340, 306), (327, 316), (337, 320), (359, 306), (340, 283),
                 (310, 281), (282, 298), (287, 321), (316, 341), (351, 341),
                 (385, 327), (411, 304), (384, 278), (338, 254), (282, 253),
                 (232, 277), (202, 307), (231, 342), (291, 377), (381, 379),
                 (458, 345), (505, 300), (455, 253), (369, 207), (272, 198),
                 (187, 231), (115, 298)]
        d.polygon(outer, fill=green)
        # Vertical interruption is characteristic of the NVIDIA eye mark.
        d.rectangle((264, 150, 281, 397), fill=base)
        d.text((320, 445), 'NVIDIA', font=font(78), fill=green, anchor='mm')
    return im.resize((CELL, CELL), Image.Resampling.LANCZOS)


def load_obj(path):
    lines = path.read_text(encoding='utf-8').splitlines()
    vertices = np.array([list(map(float, l.split()[1:4])) for l in lines if l.startswith('v ')])
    normals = np.array([list(map(float, l.split()[1:4])) for l in lines if l.startswith('vn ')])
    faces = [[tuple(map(int, c.split('/'))) for c in l.split()[1:]] for l in lines if l.startswith('f ')]
    return lines, vertices, normals, faces


def unwrap(vertices, faces):
    uv, mapped, charts = [], [], []
    for face in faces:
        p = vertices[[c[0] - 1 for c in face]]
        normal = np.cross(p[1] - p[0], p[2] - p[0])
        # All corners must lie in the actual box-shaped head, ahead of the neck.
        is_head = bool(np.max(p[:, 2]) < -1.71)
        name = max(CHARTS, key=lambda n: np.dot(normal, CHARTS[n][2])) if is_head else 'body'
        charts.append(name)
        corners = []
        for corner, xyz in zip(face, p):
            if name == 'body':
                coord = (.97, .03)  # Deep inside the solid base-color gutter.
            else:
                col = list(CHARTS).index(name) % 3
                row = list(CHARTS).index(name) // 3
                right, up, outward = map(np.array, CHARTS[name])
                assert np.array_equal(np.cross(right, up), outward)
                local = xyz - CENTER
                s = .5 + np.dot(local, right) / EXTENT
                t = .5 - np.dot(local, up) / EXTENT
                assert 0 < s < 1 and 0 < t < 1
                coord = ((col * CELL + s * CELL) / SIZE, 1 - (row * CELL + t * CELL) / SIZE)
            uv.append(coord)
            corners.append((corner[0], len(uv), corner[2]))
        mapped.append(corners)
    assert any(c == 'front' for c in charts)
    return np.array(uv), mapped, charts


def save_assets(kind, base, source, vertices, normals, faces, uv, mapped, charts):
    name = 'pig_' + kind
    texture = Image.new('RGB', (SIZE, SIZE), base)
    for i, chart in enumerate(CHARTS):
        tile = emblem(kind, base) if chart in ('front', 'right', 'left', 'top') else Image.new('RGB', (CELL, CELL), base)
        texture.paste(tile, ((i % 3) * CELL, (i // 3) * CELL))
    texture.save(MODELS / (name + '.png'))
    out = ['# Generated by tools/generate_drone_skins.py; geometry and normals from PIG.obj',
           'mtllib ' + name + '.mtl', 'o ' + name]
    out += [l for l in source if l.startswith('v ') or l.startswith('vn ')]
    out += ['vt %.9f %.9f' % tuple(c) for c in uv]
    out += ['s off', 'usemtl ' + name]
    out += ['f ' + ' '.join('%d/%d/%d' % c for c in face) for face in mapped]
    target = MODELS / (name + '.obj')
    target.write_text('\n'.join(out) + '\n', encoding='utf-8')
    (MODELS / (name + '.mtl')).write_text(
        f'newmtl {name}\nKa 1.000000 1.000000 1.000000\nKd 1.000000 1.000000 1.000000\nKs 0.12 0.12 0.12\nNs 32\nd 1\nillum 2\nmap_Kd {name}.png\n', encoding='utf-8')
    # Validate written resources, including exact vertex and normal data and indices.
    lines2, v2, n2, f2 = load_obj(target)
    assert np.array_equal(vertices, v2) and np.array_equal(normals, n2)
    assert [[(c[0], c[2]) for c in f] for f in faces] == [[(c[0], c[2]) for c in f] for f in f2]
    assert all(1 <= c[1] <= len(uv) for f in f2 for c in f)
    return texture


def render(vertices, normals, faces, uv, texture, eye, center, frame, zoom=1):
    w, h = frame
    forward = np.array(center, float) - np.array(eye, float)
    forward /= np.linalg.norm(forward)
    right = np.cross(forward, [0, 1, 0])
    right /= np.linalg.norm(right)
    up = np.cross(right, forward)
    q = vertices - center
    xy = np.stack([q @ right, -q @ up], axis=1)
    scale = min((w - 50) / np.ptp(xy[:, 0]), (h - 70) / np.ptp(xy[:, 1])) * zoom
    xy = xy * scale + np.array([w / 2, h / 2])
    depth = q @ forward
    rgb = np.full((h, w, 3), (27, 31, 38), np.uint8)
    zbuf = np.full((h, w), np.inf)
    tex = np.asarray(texture)
    light = np.array([-.4, .7, -.6]); light /= np.linalg.norm(light)
    for face in faces:
        for k in range(1, len(face) - 1):
            tri = [face[0], face[k], face[k + 1]]
            indices = [c[0] - 1 for c in tri]
            p = xy[indices]
            lo = np.maximum(np.floor(p.min(axis=0)).astype(int), 0)
            hi = np.minimum(np.ceil(p.max(axis=0)).astype(int), [w - 1, h - 1])
            if np.any(hi < lo):
                continue
            x, y = np.meshgrid(np.arange(lo[0], hi[0] + 1) + .5, np.arange(lo[1], hi[1] + 1) + .5)
            a, b, c = p
            den = (b[1]-c[1])*(a[0]-c[0])+(c[0]-b[0])*(a[1]-c[1])
            if abs(den) < 1e-9:
                continue
            wa = ((b[1]-c[1])*(x-c[0])+(c[0]-b[0])*(y-c[1])) / den
            wb = ((c[1]-a[1])*(x-c[0])+(a[0]-c[0])*(y-c[1])) / den
            wc = 1-wa-wb
            z = wa*depth[indices[0]]+wb*depth[indices[1]]+wc*depth[indices[2]]
            view = zbuf[lo[1]:hi[1]+1, lo[0]:hi[0]+1]
            mask = (wa >= 0) & (wb >= 0) & (wc >= 0) & (z < view)
            if not np.any(mask):
                continue
            coords = uv[[corner[1] - 1 for corner in tri]]
            u = wa*coords[0, 0]+wb*coords[1, 0]+wc*coords[2, 0]
            v = wa*coords[0, 1]+wb*coords[1, 1]+wc*coords[2, 1]
            tx = np.clip((u*(SIZE-1)).astype(int), 0, SIZE-1)
            ty = np.clip(((1-v)*(SIZE-1)).astype(int), 0, SIZE-1)
            n = normals[tri[0][2]-1]
            brightness = .64 + .36 * max(0, float(n @ light))
            colors = np.clip(tex[ty, tx] * brightness, 0, 255).astype(np.uint8)
            rgbview = rgb[lo[1]:hi[1]+1, lo[0]:hi[0]+1]
            rgbview[mask] = colors[mask]
            view[mask] = z[mask]
    return Image.fromarray(rgb)


def main():
    BUILD.mkdir(exist_ok=True)
    originals = [MODELS / x for x in ('PIG.obj', 'PIG.mtl', 'pig.PNG')]
    hashes = {p.name: hashlib.sha256(p.read_bytes()).hexdigest() for p in originals}
    # Open and inspect original texture; skins intentionally replace its pig colors.
    with Image.open(originals[2]) as old:
        print('Original texture:', old.size, old.mode)
    source, vertices, normals, faces = load_obj(originals[0])
    uv, mapped, charts = unwrap(vertices, faces)
    preview = Image.new('RGB', (1800, 1120), (27, 31, 38))
    views = [('FRONT -Z / HEAD', (0, .06, -5), (0, 0, -1.88), 1.9),
             ('FRONT THREE-QUARTER', (3.2, 2.4, -4), (0, 0, -.2), 1),
             ('TOP / HEAD AND WINGS', (0, 5, -.8), (0, 0, -.2), 1)]
    for row, (kind, base) in enumerate((('nuclear', (146, 150, 154)), ('nvidia', (23, 25, 28)))):
        texture = save_assets(kind, base, source, vertices, normals, faces, uv, mapped, charts)
        for col, (label, eye, center, zoom) in enumerate(views):
            pane = render(vertices, normals, mapped, uv, texture, eye, np.array(center), (600, 560), zoom)
            draw = ImageDraw.Draw(pane)
            draw.text((20, 16), kind.upper() + ' | ' + label, font=font(20), fill=(231, 236, 240))
            preview.paste(pane, (col*600, row*560))
    preview.save(BUILD / 'drone-skins-preview.png')
    assert hashes == {p.name: hashlib.sha256(p.read_bytes()).hexdigest() for p in originals}
    report = {'original_sha256': hashes, 'vertices': len(vertices), 'normals': len(normals),
              'faces': len(faces), 'uv_corners': len(uv), 'head_face_charts': {n: charts.count(n) for n in CHARTS},
              'body_faces': charts.count('body'), 'head_rule': 'all face vertices z < -1.71',
              'uv_strategy': 'six non-mirrored orthographic head charts; isolated solid body swatch',
              'validation': 'exact source vertices/normals/face winding and indices retained; original hashes unchanged'}
    (BUILD / 'drone-skins-validation.json').write_text(json.dumps(report, indent=2) + '\n', encoding='utf-8')
    print(json.dumps(report, indent=2))
    print('Preview:', BUILD / 'drone-skins-preview.png')


if __name__ == '__main__':
    main()
