"""Assign non-overlapping palette UVs and texture the positron cannon.

Run with: python tools/generate-positron-texture.py
Requires Pillow (pip install Pillow). Running it again is safe.
"""
from collections import Counter
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
MODEL = ROOT / "src/main/resources/assets/123technology/models"
OBJ = MODEL / "Positron_e+_artiliary.obj"
OUTPUT = MODEL / "Positron_e+_artiliary.png"

palette = {
    "body": (61, 73, 78),
    "upper": (86, 101, 102),
    "steel": (124, 138, 137),
    "shadow": (39, 48, 53),
    "barrel": (42, 57, 62),
    "edge": (158, 171, 166),
    "energy": (37, 177, 200),
    "warning": (224, 132, 60),
}


def classify(corners, vertices):
    xyz = [vertices[int(part.split("/")[0]) - 1] for part in corners]
    x = sum(p[0] for p in xyz) / len(xyz)
    y = sum(p[1] for p in xyz) / len(xyz)
    z = sum(abs(p[2]) for p in xyz) / len(xyz)
    if -0.78 < x < -0.71 and y > -0.705 and z > 0.009:
        return "energy"
    if -0.36 < x < -0.26 and y < -0.724 and z < 0.007:
        return "warning"
    if x < -0.96:
        return "barrel" if z < 0.019 else "steel"
    if x < -0.82:
        return "shadow" if y < -0.72 else "steel"
    if x < -0.47 and y < -0.737:
        return "edge" if z > 0.016 else "body"
    if y > -0.688:
        return "upper"
    if y < -0.729:
        return "shadow" if z < 0.009 else "steel"
    return "body"


def main():
    lines = OBJ.read_text(encoding="utf-8").splitlines(keepends=True)
    vertices = [tuple(map(float, line.split()[1:4])) for line in lines if line.startswith("v ")]
    names = list(palette)
    width, height = 1024, 512
    image = Image.new("RGB", (width, height))
    draw = ImageDraw.Draw(image)
    uv = []
    for index, name in enumerate(names):
        col, row = index % 4, index // 4
        left, top = col * 256, row * 256
        draw.rectangle((left, top, left + 255, top + 255), fill=palette[name])
        # Keep every triangle well inside its swatch to avoid filter bleed.
        for px, py in ((left + 32, top + 32), (left + 224, top + 32), (left + 128, top + 224)):
            uv.append(f"vt {px / width:.6f} {1 - py / height:.6f}\n")

    counts = Counter()
    rewritten = []
    for line in lines:
        if line.startswith("vt "):
            continue
        if line.startswith("f "):
            corners = line.split()[1:]
            if len(corners) != 3:
                raise ValueError("Only triangular faces are supported")
            name = classify(corners, vertices)
            base = names.index(name) * 3 + 1
            updated = []
            for offset, corner in enumerate(corners):
                indices = corner.split("/")
                if len(indices) != 3 or not indices[2]:
                    raise ValueError("Expected vertex/UV/normal face references")
                updated.append(f"{indices[0]}/{base + offset}/{indices[2]}")
            rewritten.append("f " + " ".join(updated) + "\n")
            counts[name] += 1
        else:
            rewritten.append(line)

    # OBJ vertex/texture/normal records must precede the face records.
    first_face = next(i for i, line in enumerate(rewritten) if line.startswith("f "))
    rewritten[first_face:first_face] = uv
    OBJ.write_text("".join(rewritten), encoding="utf-8", newline="")
    image.save(OUTPUT, optimize=True)
    print(f"Wrote {OUTPUT} ({width}x{height}) and UV-mapped {OBJ}")
    print("Face palette:", dict(counts))


if __name__ == "__main__":
    main()
