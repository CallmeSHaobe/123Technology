"""Migrate the two pre-existing ShapeLoader text resources to exact GZIP JSON arrays.
Run before deleting old text files. Legacy parsing is reproduced, including dot-to-space conversion.
"""
import gzip
import hashlib
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / 'src/main/resources/assets/123technology/structures'
SPECS = [('megaeec_shape.txt', 'OTEMegaEEC_main.shape.gz', 50, 143, 56),
         ('abstract_sanctum_shape.txt', 'OTEAbstractSanctum_main.shape.gz', 41, 27, 41)]
manifest = []
for source_name, dest_name, width, height, layers in SPECS:
    source = BASE / source_name
    dest = BASE / 'ote' / dest_name
    if source.exists():
        rows = []
        for line in source.read_text(encoding='utf-8').splitlines():
            stripped = line.strip()
            if not stripped or stripped.startswith('#'):
                continue
            assert len(line) == width, (source_name, len(line), width)
            rows.append(line.replace('.', ' '))
        assert len(rows) == height * layers
        shape = [rows[i * height:(i + 1) * height] for i in range(layers)]
        payload = json.dumps(shape, ensure_ascii=False, separators=(',', ':')).encode('utf-8')
        dest.parent.mkdir(parents=True, exist_ok=True)
        dest.write_bytes(gzip.compress(payload, compresslevel=9, mtime=0))
    else:
        payload = gzip.decompress(dest.read_bytes())
        shape = json.loads(payload)
    assert json.loads(gzip.decompress(dest.read_bytes())) == shape
    assert len(shape) == layers and all(len(layer) == height for layer in shape)
    assert all(len(row) == width for layer in shape for row in layer)
    manifest.append({'source': source_name, 'resource': dest_name,
                     'width': width, 'rows_per_layer': height, 'layers': layers,
                     'canonical_sha256': hashlib.sha256(payload).hexdigest(),
                     'uncompressed_bytes': len(payload), 'compressed_bytes': dest.stat().st_size})
    print(f'{source_name}: {len(payload)} -> {dest.stat().st_size} bytes; exact decompressed shape verified')
(ROOT / 'tools/external_shapes_manifest.json').write_text(json.dumps(manifest, indent=2) + '\n', encoding='utf-8')
