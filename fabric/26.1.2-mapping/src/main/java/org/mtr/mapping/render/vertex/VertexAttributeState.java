package org.mtr.mapping.render.vertex;

import org.lwjgl.opengl.GL33;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.OverlayTexture;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.tool.GlStateTracker;
import org.mtr.mapping.render.tool.Utilities;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

public final class VertexAttributeState {
	public final Integer color, lightmapUV, overlayUV;
	public final Vector3f position, normal;
	public final Float textureU, textureV;
	public final Matrix4f matrix4f;

	public VertexAttributeState(int color, int lightmapUV, Matrix4f matrix4f) {
		this.color = color;
		this.lightmapUV = Utilities.exchangeLightmapUVBits(lightmapUV);
		position = null; textureU = null; textureV = null;
		overlayUV = Utilities.exchangeLightmapUVBits(OverlayTexture.getDefaultUvMapped());
		normal = null; this.matrix4f = matrix4f;
	}

	public VertexAttributeState(@Nullable Integer color, @Nullable Integer lightmapUV) {
		this.color = color; this.lightmapUV = lightmapUV;
		position = null; textureU = null; textureV = null; overlayUV = null; normal = null; matrix4f = null;
	}

	public void apply() {
		if (position != null) GL33.glVertexAttrib3f(VertexAttributeType.POSITION.location, position.data.x, position.data.y, position.data.z);
		if (color != null) GL33.glVertexAttrib4f(VertexAttributeType.COLOR.location, ((color >>> 24) & 0xFF) / 255F, ((color >>> 16) & 0xFF) / 255F, ((color >>> 8) & 0xFF) / 255F, (color & 0xFF) / 255F);
		if (textureU != null && textureV != null) GL33.glVertexAttrib2f(VertexAttributeType.UV_TEXTURE.location, textureU, textureV);
		applyUv(VertexAttributeType.UV_OVERLAY, overlayUV);
		applyUv(VertexAttributeType.UV_LIGHTMAP, lightmapUV);
		if (normal != null) GL33.glVertexAttrib3f(VertexAttributeType.NORMAL.location, normal.data.x, normal.data.y, normal.data.z);
		if (matrix4f != null) {
			final FloatBuffer buffer = ByteBuffer.allocate(64).asFloatBuffer();
			Utilities.store(matrix4f, buffer);
			for (int i = 0; i < 4; i++) {
				final int offset = i * 4;
				GL33.glVertexAttrib4f(VertexAttributeType.MATRIX_MODEL.location + i, buffer.get(offset), buffer.get(offset + 1), buffer.get(offset + 2), buffer.get(offset + 3));
			}
		}
	}

	private static void applyUv(VertexAttributeType type, Integer uv) {
		if (uv == null) return;
		if (GlStateTracker.isGl4ES()) GL33.glVertexAttrib2f(type.location, (short) (uv >>> 16), (short) (int) uv);
		else GL33.glVertexAttribI2i(type.location, (short) (uv >>> 16), (short) (int) uv);
	}

	@Override public boolean equals(Object other) {
		if (!(other instanceof VertexAttributeState state)) return false;
		return Objects.equals(color, state.color) && Objects.equals(lightmapUV, state.lightmapUV) && Objects.equals(position, state.position) && Objects.equals(textureU, state.textureU) && Objects.equals(textureV, state.textureV) && Objects.equals(overlayUV, state.overlayUV) && Objects.equals(normal, state.normal) && Objects.equals(matrix4f, state.matrix4f);
	}

	@Override public int hashCode() {
		return Objects.hash(position, color, textureU, textureV, lightmapUV, normal, overlayUV, matrix4f);
	}
}
