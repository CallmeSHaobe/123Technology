#!/usr/bin/env python3
"""Generate/verify DeepSeiko's final StructureLib shape, without game dependencies.

Verify committed data:
    python tools/migrate_deepseiko_shape.py
Reproduce from an original (pre-migration) Java file, comparing every character:
    python tools/migrate_deepseiko_shape.py --source build/deepseiko-shape/OTEDeepSeiko.original.java
Regenerate resource and manifest from that original file:
    python tools/migrate_deepseiko_shape.py --source ORIGINAL.java --generate
The initial migration can preserve its input with --snapshot. Snapshots and javac
outputs live under build/, never in tracked sources. Obtain an original Java file
from version control if the local snapshot is unavailable. Requires Python 3;
--source additionally requires javac/java on PATH (no game libraries or Gson).
"""

import argparse
from collections import Counter
import gzip
import hashlib
import json
from pathlib import Path
import re
import shutil
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
RESOURCE = ROOT / 'src/main/resources/assets/123technology/structures/ote/OTEDeepSeiko_main.shape.gz'
MANIFEST = ROOT / 'tools/deepseiko_shape_manifest.json'
BUILD = ROOT / 'build/deepseiko-shape'


def canonical(shape):
    return json.dumps(shape, ensure_ascii=False, separators=(',', ':')).encode('utf-8')


def sha256(data):
    return hashlib.sha256(data).hexdigest()


def extract(source, start, end):
    first = source.index(start)
    return source[first:source.index(end, first)]


def original_shape(path):
    source_bytes = path.read_bytes()
    source = source_bytes.decode('utf-8-sig')
    ring = extract(source, '    private static final String RING_SLOTS_RAW',
                   '    private static final ITexture CONTROLLER_FRONT_TEXTURE;')
    marked = re.search(r'        MARKED_HATCHES = new String\[\] \{.*?\};', source, re.S)
    if marked is None:
        raise ValueError('Original MARKED_HATCHES assignment not found')
    marked = marked.group().replace('        MARKED_HATCHES =',
                                    '    private static final String[] MARKED_HATCHES =', 1)
    methods = extract(source, '    private static String[][] generateWhaleShape()',
                      '    @NotNull\n    protected MTEMultiBlockBaseGui<?> getGui()')
    nested = source[source.index('    private static final class OTEDeepSeikoShape'):].rstrip()
    if not nested.endswith('}'):
        raise ValueError('Missing outer class terminator')
    nested = nested[:-1]
    # Compile the original generator unchanged. Its only non-JDK reference is
    # LOG.debug(String), replaced by a no-op logger; no Minecraft stubs exist.
    harness = ('public final class DeepSeikoShapeExtractor {\n'
               '    private static final DebugLog LOG = new DebugLog();\n'
               '    private static final class DebugLog { void debug(String s) {} }\n'
               + ring + marked + '\n' + methods + nested + r'''
    public static void main(String[] args) throws Exception {
        String[][] shape = generateWhaleShape();
        StringBuilder out = new StringBuilder("[");
        for (int i = 0; i < shape.length; i++) {
            if (i > 0) out.append(',');
            out.append('[');
            for (int j = 0; j < shape[i].length; j++) {
                if (j > 0) out.append(',');
                out.append('"');
                for (char c : shape[i][j].toCharArray()) {
                    if (c == '"' || c == '\\') out.append('\\');
                    if (c < 32 || c > 126) throw new IllegalStateException("Unexpected character");
                    out.append(c);
                }
                out.append('"');
            }
            out.append(']');
        }
        out.append(']');
        java.nio.file.Files.write(java.nio.file.Paths.get(args[0]),
            out.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
''')
    BUILD.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='extract-', dir=BUILD) as directory:
        temp = Path(directory)
        java_file = temp / 'DeepSeikoShapeExtractor.java'
        java_file.write_text(harness, encoding='utf-8')
        result = temp / 'shape.json'
        subprocess.run(['javac', '-encoding', 'UTF-8', '-d', str(temp), str(java_file)], check=True)
        subprocess.run(['java', '-cp', str(temp), 'DeepSeikoShapeExtractor', str(result)], check=True)
        shape = json.loads(result.read_text(encoding='utf-8'))
    return shape, sha256(source_bytes)


def describe(shape):
    if not isinstance(shape, list) or len(shape) != 60:
        raise ValueError('Expected 60 slices')
    for i, layer in enumerate(shape):
        if not isinstance(layer, list) or len(layer) != 23:
            raise ValueError(f'Expected 23 rows in slice {i}')
        for j, row in enumerate(layer):
            if not isinstance(row, str) or len(row) != 33:
                raise ValueError(f'Expected 33 characters at [{i}][{j}]')
    data = canonical(shape)
    return {
        'canonical_json_sha256': sha256(data),
        'canonical_json_bytes': len(data),
        'dimensions': [60, 23, 33],
        'rows': sum(map(len, shape)),
        'characters': sum(len(row) for layer in shape for row in layer),
        'character_counts': dict(sorted(Counter(''.join(row for layer in shape for row in layer)).items())),
    }


def compare(expected, actual):
    if expected != actual:
        for x, (left, right) in enumerate(zip(expected, actual)):
            for y, (a, b) in enumerate(zip(left, right)):
                if a != b:
                    raise ValueError(f'Geometry differs at slice={x}, row={y}: {a!r} != {b!r}')
        raise ValueError('Shape dimensions differ')


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--source', type=Path, help='Original pre-migration OTEDeepSeiko.java')
    parser.add_argument('--generate', action='store_true', help='Write resource and manifest from --source')
    parser.add_argument('--snapshot', action='store_true', help='Preserve original input under build/')
    args = parser.parse_args()
    if (args.generate or args.snapshot) and args.source is None:
        parser.error('--generate/--snapshot requires --source')
    original = None
    source_hash = None
    if args.source:
        original, source_hash = original_shape(args.source)
        describe(original)
        if args.snapshot:
            target = BUILD / 'OTEDeepSeiko.original.java'
            if args.source.resolve() != target.resolve():
                shutil.copyfile(args.source, target)
    if args.generate:
        metadata = {
            'schema_version': 1,
            'resource': RESOURCE.relative_to(ROOT).as_posix(),
            'format': 'gzip UTF-8 JSON String[][]',
            'canonical_json': 'UTF-8; ensure_ascii=false; separators=(comma,colon); no newline',
            'source_java_sha256': source_hash,
            'generator': 'Original generateWhaleShape + fillRowGaps + OTEDeepSeikoShape; javac, JDK only, no-op LOG.debug',
            'coordinate_mapping': 'shape[x][row][z] = g[22-row][z][x]; already final addShape orientation, no transpose',
            **describe(original),
        }
        RESOURCE.parent.mkdir(parents=True, exist_ok=True)
        RESOURCE.write_bytes(gzip.compress(canonical(original), compresslevel=9, mtime=0))
        MANIFEST.write_text(json.dumps(metadata, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    metadata = json.loads(MANIFEST.read_text(encoding='utf-8'))
    decompressed = gzip.decompress(RESOURCE.read_bytes())
    actual = json.loads(decompressed.decode('utf-8'))
    if decompressed != canonical(actual):
        raise ValueError('Resource is not the canonical UTF-8 JSON encoding')
    description = describe(actual)
    for key, value in description.items():
        if metadata[key] != value:
            raise ValueError(f'Manifest mismatch for {key}: {metadata[key]!r} != {value!r}')
    if original is not None:
        if source_hash != metadata['source_java_sha256']:
            raise ValueError('Original Java SHA-256 differs from manifest')
        compare(original, actual)
        print('GEOMETRY EXACT: original javac output == resource, every row/character/space/order')
    print(json.dumps(description, ensure_ascii=False, sort_keys=True))
    print(f'OK: {RESOURCE.relative_to(ROOT).as_posix()} ({RESOURCE.stat().st_size} compressed bytes)')


if __name__ == '__main__':
    main()
