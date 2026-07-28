package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Yarn {@code ChunkManager} ≈ Mojmap {@code ChunkSource}.
 * Regenerated holder pointed at ChunkMap; rewritten for MTR smoke against ChunkSource.
 */
@SuppressWarnings({"deprecation", "unused"})
public final class ChunkManager extends HolderBase<net.minecraft.world.level.chunk.ChunkSource> {

	public ChunkManager(net.minecraft.world.level.chunk.ChunkSource data) {
		super(data);
	}

	@MappedMethod
	public static ChunkManager cast(HolderBase<?> data) {
		return new ChunkManager((net.minecraft.world.level.chunk.ChunkSource) data.data);
	}

	@MappedMethod
	public static boolean isInstance(@Nullable HolderBase<?> data) {
		return data != null && data.data instanceof net.minecraft.world.level.chunk.ChunkSource;
	}

	@MappedMethod
	public boolean isChunkLoaded(int chunkX, int chunkZ) {
		return this.data.hasChunk(chunkX, chunkZ);
	}

	@Nullable
	@MappedMethod
	public WorldChunk getWorldChunk(int chunkX, int chunkZ) {
		final net.minecraft.world.level.chunk.LevelChunk chunk = this.data.getChunk(chunkX, chunkZ, false);
		return chunk == null ? null : new WorldChunk(chunk);
	}

	@MappedMethod
	public boolean hasChunk(int chunkX, int chunkZ) {
		return this.data.hasChunk(chunkX, chunkZ);
	}
}
