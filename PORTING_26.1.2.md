# Port Minecraft-Mappings → 26.1.2 (Fabric)

## Status
- Modules scaffolded: `fabric/26.1.2-generator`, `fabric/26.1.2-mapping` (copied from 1.20.4)
- `BuildTools` calendar version + Java 25
- Yarn disabled for 26.x; official Mojang mappings hook added

## Next (blocking)
1. **ClassScannerTest** for 26.1.2 must use **Mojang official names** (unobfuscated), not Yarn:
   - `Identifier` → `ResourceLocation`
   - `BlockEntity` package paths, client GUI widgets renames, etc.
2. Run generator pipeline (`-Pgenerate=normal`) once Loom 1.15 + Java 25 env works.
3. Fix hand-written `mapper/`, `registry/`, `render/` (OpenGL/Blaze3D) for 26.1.
4. Publish jar to MTR `libs/Minecraft-Mappings-fabric-26.1.2-0.0.1-dev.jar`

## Build (WIP)
```bash
export JAVA_HOME=... # JDK 25
./gradlew :fabric:26.1.2-generator:test -Pgenerate=normal
./gradlew :fabric:26.1.2-mapping:build
```
