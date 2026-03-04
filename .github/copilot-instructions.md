# Copilot Instructions for 123Technology

## Project Overview

**123Technology** is a Minecraft 1.7.10 mod for the [GregTech: New Horizons (GTNH)](https://github.com/GTNewHorizons) modpack. It adds custom multiblock machines, items, fluids, dimensions, and recipes to the GTNH experience.

> **Naming note:** The mod ID is `123Technology` but the compiled jar and many internal identifiers use `OTHTechnology` as the base name. This is intentional — Forge has quirks with mod IDs that start with a digit, so the file/archive name was changed to `OTHTechnology` while the mod ID (`123Technology`) is preserved. Do **not** rename these.

- **Mod ID:** `123Technology`
- **Archive/file base name:** `OTHTechnology`
- **Root package:** `com.newmaa.othtech`
- **GTNH target version:** 2.8.0
- **Minecraft version:** 1.7.10 / Forge 10.13.4.1614

## Repository Structure

```
src/main/java/com/newmaa/othtech/
├── OTHTechnology.java       # Main @Mod entry point (FML lifecycle hooks)
├── CommonProxy.java / ClientProxy.java
├── Config.java              # Forge config handling
├── machine/                 # All multiblock machine tile entity classes (OTE* prefix)
│   ├── MachineLoader.java   # Registers all machines
│   ├── machineclass/        # Shared base/helper classes for machines
│   └── hatch/               # Custom hatch tile entities
├── recipe/                  # Recipe registration (RecipeLoader + per-machine files)
├── common/                  # Items, blocks, fluids, materials, dimensions, recipe maps
│   ├── OTHItemList.java     # Enum of all mod items/machines
│   ├── recipemap/           # NEI recipe map registrations
│   └── ...
├── event/                   # Forge event handlers
├── utils/                   # Shared utilities
└── Mixins/                  # Mixin classes — PascalCase matches the Java package name required by UniMixins/SpongeMixins; must match mixinsPackage in gradle.properties
```

## Build & Test

```bash
# Build the mod jar
./gradlew build

# Run the Minecraft client (dev environment)
./gradlew runClient

# Run the Minecraft server (dev environment)
./gradlew runServer
```

CI runs automatically on pushes and PRs to `master`/`main` via `.github/workflows/build-and-test.yml`.

## Coding Conventions

- **Machine classes** are prefixed with `OTE` (e.g., `OTEMegaQFT`, `OTESINOPEC`). Each machine extends a GTNH multiblock base class (e.g., `GT_MetaTileEntity_ExtendedPowerMultiBlockBase`, `GT_MetaTileEntity_MultiBlockBase`).
- **New machines** must be registered in `MachineLoader.java` and added to `OTHItemList`.
- **Recipe pools** (recipe maps) are registered in `common/recipemap/` and use GTNH's `GT_Recipe.GT_Recipe_Map` system.
- **Recipes** are loaded in `RecipeLoader` during `FMLLoadCompleteEvent`.
- **Items and blocks** are declared in `common/OTHItemList.java` as enum entries.
- The project uses **modern Java syntax** (Java 8–17 via [Jabel](https://github.com/bsideup/jabel)) while targeting JVM 8 bytecode.
- **Mixins** must reside in the `com.newmaa.othtech.Mixins` package.
- Spotless (Google Java Format) and Checkstyle (no wildcard imports) are enforced — run `./gradlew spotlessApply` to auto-format before committing.

## Key Dependencies

Declared in `dependencies.gradle` using `elytraModpackVersion` helper:

- `GT5-Unofficial` — GregTech 5 (core recipes, machines, materials)
- `NewHorizonsCoreMod` — GTNH core mod (structures, utilities)
- `StructureLib` — multiblock structure definition
- `Avaritia`, `EnderIO`, `Botania`, `Thaumcraft`, `ForestryMC`, `BloodMagic`, and many other GTNH companion mods

## Notes

- Lang files (`src/main/resources/assets/123technology/lang/`) **must** be kept up to date when adding new items or machines, otherwise tooltips will be broken.
- Config options (e.g., machine explosion toggles) live in `Config.java` and are exposed through the standard Forge config system.
