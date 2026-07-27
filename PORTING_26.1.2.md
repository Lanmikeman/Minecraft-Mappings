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

## Progress 2026-07-27 (session)

### Toolchain green
- Gradle **9.6.1**, Loom **`net.fabricmc.fabric-loom` 1.17.x** (unobfuscated)
- Java **25** for Gradle + MC
- Access widener header: `accessWidener v2 official`
- Forge modules disabled in settings (ForgeGradle ⊄ Gradle 9)
- `./gradlew :fabric:26.1.2-generator:compileJava` **SUCCESS**
- Minimal Mojang `ClassScannerTest` with ~16 seed classes (yarn full list in `.yarn-1.20.4.bak`)

### Known Mojang package layout (26.1.2)
| MTR / Yarn-ish | Mojang 26.1.2 |
|----------------|---------------|
| `net.minecraft.block.Block` | `net.minecraft.world.level.block.Block` |
| `net.minecraft.util.math.BlockPos` | `net.minecraft.core.BlockPos` |
| `net.minecraft.world.World` | `net.minecraft.world.level.Level` |
| `net.minecraft.server.world.ServerWorld` | `net.minecraft.server.level.ServerLevel` |
| `net.minecraft.server.network.ServerPlayerEntity` | `net.minecraft.server.level.ServerPlayer` |
| `net.minecraft.entity.player.PlayerEntity` | `net.minecraft.world.entity.player.Player` |
| `net.minecraft.util.Identifier` (yarn) | `net.minecraft.resources.Identifier` |
| `net.minecraft.util.ActionResult` | `net.minecraft.world.InteractionResult` |
| `net.minecraft.client.MinecraftClient` | `net.minecraft.client.Minecraft` |
| `ItemStack` | `net.minecraft.world.item.ItemStack` (+ `ItemStackTemplate`) |

### Next
1. Expand ClassScannerTest (restore full put list with Mojang types)
2. Run `-Pgenerate=normal` generator to emit holders
3. Port handwritten mapper/registry/render off Yarn packages
4. Produce `Minecraft-Mappings-fabric-26.1.2-0.0.1-dev.jar` → MTR `libs/`
