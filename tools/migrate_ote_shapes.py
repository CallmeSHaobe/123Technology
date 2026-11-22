#!/usr/bin/env python3
"""Migrate literal OTE shapes without changing surrounding Java source.

Run --dry-run first (the default), then --apply, then --verify. No compilation
is performed. Canonical JSON is UTF-8, ensure_ascii=False, separators=(',', ':').
The manifest deliberately stores every row's string lengths, including ragged
and empty rows; dimensions are not inferred from only the first row.
"""
import argparse
import gzip
import hashlib
import io
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCES = ROOT / 'src/main/java/com/newmaa/othtech/machine'
RESOURCE_DIR = 'src/main/resources/assets/123technology/structures/ote'
RESOURCE_PREFIX = '/assets/123technology/structures/ote/'
MANIFEST = ROOT / 'tools/migrate_ote_shapes_manifest.json'
EXCLUDED = {'OTEDeepSeiko.java', 'OTEMegaEEC.java', 'OTEAbstractSanctum.java'}
TOKEN = re.compile(r'\s+|//[^\r\n]*|/\*[\s\S]*?\*/|"(?:\\[\s\S]|[^"\\])*"|\'(?:\\[\s\S]|[^\'\\])*\'|[A-Za-z_$][\w$]*|.', re.UNICODE)
LOADER = 'com.newmaa.othtech.utils.ShapeLoader.loadCompressed'


def sha(data):
    return hashlib.sha256(data).hexdigest()


def canonical(value):
    return json.dumps(value, ensure_ascii=False, separators=(',', ':')).encode('utf-8')


def tokens(text):
    result = []
    end = 0
    for match in TOKEN.finditer(text):
        if match.start() != end:
            raise ValueError('Unlexed Java source')
        end = match.end()
        value = match.group()
        if value.isspace() or value.startswith('//') or value.startswith('/*'):
            continue
        result.append((value, match.start(), match.end()))
    if end != len(text):
        raise ValueError('Unlexed Java source suffix')
    return result


def java_string(literal):
    # Unicode translation precedes Java lexical escape decoding. Respect the
    # eligibility of a backslash (an odd preceding run makes it ineligible).
    raw = literal[1:-1]
    out = []
    i = 0
    slashes = 0
    while i < len(raw):
        c = raw[i]
        if c == '\\' and slashes % 2 == 0 and i + 1 < len(raw) and raw[i + 1] == 'u':
            j = i + 1
            while j < len(raw) and raw[j] == 'u':
                j += 1
            if not re.fullmatch('[0-9a-fA-F]{4}', raw[j:j + 4]):
                raise ValueError('Invalid Java Unicode escape')
            c = chr(int(raw[j:j + 4], 16))
            i = j + 4
        else:
            i += 1
        out.append(c)
        slashes = slashes + 1 if c == '\\' else 0
    raw = ''.join(out)
    out = []
    i = 0
    escapes = {'b': '\b', 't': '\t', 'n': '\n', 'f': '\f', 'r': '\r', '"': '"', "'": "'", '\\': '\\'}
    while i < len(raw):
        c = raw[i]
        i += 1
        if c != '\\':
            out.append(c)
            continue
        if i == len(raw):
            raise ValueError('Incomplete Java escape')
        c = raw[i]
        i += 1
        if c in escapes:
            out.append(escapes[c])
        elif c in '01234567':
            digits = c
            limit = 3 if c in '0123' else 2
            while len(digits) < limit and i < len(raw) and raw[i] in '01234567':
                digits += raw[i]
                i += 1
            out.append(chr(int(digits, 8)))
        else:
            raise ValueError('Unsupported Java escape: ' + c)
    # Combine Java UTF-16 surrogate pairs into their Unicode scalar equivalent.
    return ''.join(out).encode('utf-16-le', 'surrogatepass').decode('utf-16-le')


def parse_array(ts, start):
    def array(i, depth):
        if ts[i][0] != '{':
            raise ValueError('Expected literal array initializer')
        i += 1
        result = []
        while ts[i][0] != '}':
            if depth == 2:
                if [t[0] for t in ts[i:i + 4]] == ['new', 'String', '[', ']']:
                    i += 4
                value, i = array(i, 1)
            else:
                if not ts[i][0].startswith('"'):
                    raise ValueError('Not a pure String literal array')
                value = java_string(ts[i][0])
                i += 1
            result.append(value)
            if ts[i][0] == ',':
                i += 1
            elif ts[i][0] != '}':
                raise ValueError('Unexpected token in literal array')
        return result, i + 1
    return array(start, 2)


def discover(path):
    data = path.read_bytes()
    text = data.decode('utf-8')
    ts = tokens(text)
    shapes = []
    skipped = []
    inline_number = 0
    for i in range(len(ts) - 6):
        values = [t[0] for t in ts[i:i + 6]]
        if values != ['new', 'String', '[', ']', '[', ']']:
            continue
        start = i
        field = None
        if i >= 2 and ts[i - 1][0] == '=' and re.fullmatch(r'[A-Za-z_$][\w$]*', ts[i - 2][0]):
            field = ts[i - 2][0]
        # Track open parentheses to establish inline addShape ancestry. Comments,
        # string contents, character literals and their braces are opaque tokens.
        stack = []
        for k in range(i):
            if ts[k][0] == '(':
                stack.append(ts[k - 1][0] if k else '')
            elif ts[k][0] == ')' and stack:
                stack.pop()
        is_shape = (field and 'shape' in field.lower()) or 'addShape' in stack
        if ts[i + 6][0] != '{' or not is_shape:
            skipped.append({'line': text.count('\n', 0, ts[i][1]) + 1, 'reason': 'non-literal or non-shape'})
            continue
        value, end = parse_array(ts, i + 6)
        if field:
            name = field
        else:
            inline_number += 1
            name = str(inline_number)
        resource = RESOURCE_PREFIX + path.stem + '_' + name + '.shape.gz'
        payload = canonical(value)
        a, b = ts[start][1], ts[end - 1][2]
        shapes.append(({
            'source_path': path.relative_to(ROOT).as_posix(),
            'source_sha256_before': sha(data),
            'machine': path.stem,
            'field_or_n': name,
            'original_line': text.count('\n', 0, a) + 1,
            'start': a, 'end': b,
            'literal_sha256': sha(text[a:b].encode('utf-8')),
            'original_canonical_json_sha256': sha(payload),
            'dimensions': {'layers': len(value), 'rows_per_layer': [len(row) for row in value],
                           'characters_per_string': [[len(s) for s in row] for row in value]},
            'character_count': sum(len(s) for row in value for s in row),
            'utf16_character_count': sum(len(s.encode('utf-16-le')) // 2 for row in value for s in row),
            'resource_path': resource,
            'resource_file': RESOURCE_DIR + '/' + path.stem + '_' + name + '.shape.gz',
        }, value))
    # Bare declarations are uncommon, but must never silently evade migration.
    for i in range(len(ts) - 7):
        if [t[0] for t in ts[i:i + 5]] == ['String', '[', ']', '[', ']']:
            if ts[i + 6][0] == '=' and ts[i + 7][0] == '{':
                raise ValueError(f'Bare String[][] initializer requires explicit handling: {path}:{ts[i][1]}')
    return text, shapes, skipped


def source_paths():
    return sorted(p for p in SOURCES.glob('*.java')
                  if (p.name.startswith('OTE') or p.name == '_OTEMachinePattern.java') and p.name not in EXCLUDED)


def dry_run():
    entries = []
    skipped = {}
    for path in source_paths():
        _, found, omitted = discover(path)
        entries.extend(entry for entry, _ in found)
        if omitted:
            skipped[path.relative_to(ROOT).as_posix()] = omitted
    manifest = {'format_version': 1, 'canonical_json': 'UTF-8; ensure_ascii=False; separators=(comma,colon)',
                'excluded': sorted(EXCLUDED), 'shapes': entries, 'skipped_arrays': skipped}
    MANIFEST.write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(f'DRY RUN: {len(entries)} shapes in {len(set(e["source_path"] for e in entries))} Java files; manifest={MANIFEST.relative_to(ROOT)}')
    return manifest


def load_manifest():
    return json.loads(MANIFEST.read_text(encoding='utf-8'))


def apply():
    manifest = load_manifest()  # A prior dry run is mandatory.
    planned = {}
    for entry in manifest['shapes']:
        planned.setdefault(entry['source_path'], []).append(entry)
    prepared = []
    # Validate the entire plan against current working tree before writing anything.
    for name, expected in planned.items():
        path = ROOT / name
        text, found, _ = discover(path)
        if [e for e, _ in found] != expected:
            raise ValueError(f'Source changed since dry run: {name}; regenerate manifest')
        prepared.append((path, text, found))
    for path, text, found in prepared:
        for entry, value in found:
            target = ROOT / entry['resource_file']
            target.parent.mkdir(parents=True, exist_ok=True)
            buffer = io.BytesIO()
            with gzip.GzipFile(filename='', mode='wb', fileobj=buffer, mtime=0) as stream:
                stream.write(canonical(value))
            target.write_bytes(buffer.getvalue())
            unpacked = gzip.decompress(target.read_bytes())
            if unpacked != canonical(value) or json.loads(unpacked) != value:
                raise ValueError(f'Resource round-trip mismatch: {target}')
        for entry, _ in reversed(found):
            replacement = LOADER + '("' + entry['resource_path'] + '")'
            text = text[:entry['start']] + replacement + text[entry['end']:]
        # Bytes preserve existing CRLF, BOM, whitespace and all unrelated edits.
        path.write_bytes(text.encode('utf-8'))
    verify()


def verify():
    manifest = load_manifest()
    for entry in manifest['shapes']:
        payload = gzip.decompress((ROOT / entry['resource_file']).read_bytes())
        value = json.loads(payload)
        if canonical(value) != payload or sha(payload) != entry['original_canonical_json_sha256']:
            raise ValueError('Canonical shape mismatch: ' + entry['resource_file'])
        dimensions = {'layers': len(value), 'rows_per_layer': [len(row) for row in value],
                      'characters_per_string': [[len(s) for s in row] for row in value]}
        if dimensions != entry['dimensions'] or sum(len(s) for row in value for s in row) != entry['character_count']:
            raise ValueError('Dimension/character count mismatch: ' + entry['resource_file'])
        compressed = (ROOT / entry['resource_file']).read_bytes()
        if compressed[4:8] != b'\0\0\0\0':
            raise ValueError('Nonzero gzip mtime: ' + entry['resource_file'])
        source = (ROOT / entry['source_path']).read_bytes().decode('utf-8')
        if source.count(LOADER + '("' + entry['resource_path'] + '")') != 1:
            raise ValueError('Missing/duplicate loader reference: ' + entry['source_path'])
    remaining = []
    for path in source_paths():
        _, found, _ = discover(path)
        remaining.extend(entry['source_path'] + ':' + str(entry['original_line']) for entry, _ in found)
    if remaining:
        raise ValueError('Remaining literal shapes: ' + ', '.join(remaining))
    print(f'VERIFIED: {len(manifest["shapes"])} gzip resources, canonical SHA-256, dimensions, character counts, loader references and mtime=0; remaining literal shapes=0. Compilation NOT run.')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument('--dry-run', action='store_true')
    mode.add_argument('--apply', action='store_true')
    mode.add_argument('--verify', action='store_true')
    args = parser.parse_args()
    if args.apply:
        apply()
    elif args.verify:
        verify()
    else:
        dry_run()
