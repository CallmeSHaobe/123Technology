# OTE compressed structures

OTE machine structure data lives in `ote/*.shape.gz`. These are external resources packaged in the mod JAR, separate from the Java machine definitions. `ShapeLoader.loadCompressed()` decompresses them in memory on first use and caches the decoded data; it does not write temporary files or require a filesystem installation directory.

Each GZIP payload is a UTF-8 JSON array of arrays of strings matching the original Java `String[][]` exactly. Preserve leading/trailing spaces, empty strings, row order, layer order, and ragged dimensions. A dot is an ordinary character in this format; it is not converted to a space. Existing `transpose(...)` calls remain at their original call sites. Block/character mappings, controller offsets, and construction logic remain in the machine classes.

To edit a shape with Python (standard library only):

```python
import gzip, json
from pathlib import Path
path = Path('ote/OTEDroneLaunchPlatform_shapeMain.shape.gz')
shape = json.loads(gzip.decompress(path.read_bytes()))
# Edit shape[layer][row], preserving all significant whitespace.
payload = json.dumps(shape, ensure_ascii=False, separators=(',', ':')).encode('utf-8')
path.write_bytes(gzip.compress(payload, compresslevel=9, mtime=0))
```

Use deterministic GZIP timestamps (`mtime=0`) to keep generated files reproducible. The migration manifests in `tools/` record the original structures' canonical JSON SHA-256 values. They provide a baseline for checking that this storage migration did not change structure geometry.

`src/regression/java/com/newmaa/othtech/utils/ShapeLoaderCheck.java` is a standalone Java regression for production resources, exact whitespace/ragged arrays, independent cached results, and rejection of missing/corrupt resources. Run `./gradlew shapeLoaderCheck`; `./gradlew check` also runs this and `flightDynamicsCheck`. The dedicated regression source set compiles the checks and their two production classes with Java 8 language/API checking. These main-method programs are intentionally separate from JUnit's `test` source set.
