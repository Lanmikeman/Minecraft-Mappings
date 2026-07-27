package org.mtr.mapping.render.batch;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Properties regarding material. Set during model loading. Affects batching.
 * GL/setup temporarily stubbed for MC 26.1.2 port.
 */
public final class MaterialProperties {

	private Identifier texture;
	public final OptimizedModel.ShaderType shaderType;
	public final VertexAttributeState vertexAttributeState;
	public final boolean translucent;
	public final boolean writeDepthBuf;
	public final boolean cutoutHack;

	private static final Function<Identifier, RenderLayer> ENTITY_TRANSLUCENT_CULL = texture -> null;
	private static final BiFunction<Identifier, Boolean, RenderLayer> BEACON_BEAM = (texture, translucent) -> null;
	private static final Function<Identifier, RenderLayer> ENTITY_CUTOUT = texture -> null;

	public MaterialProperties(OptimizedModel.ShaderType shaderType, Identifier texture, @Nullable Integer color) {
		this.shaderType = shaderType;
		this.texture = texture;
		switch (shaderType) {
			default:
				translucent = false;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
			case TRANSLUCENT:
			case TRANSLUCENT_BRIGHT:
				translucent = true;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
			case CUTOUT:
			case CUTOUT_BRIGHT:
				translucent = false;
				writeDepthBuf = true;
				cutoutHack = true;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
					}
	}

	public void setTexture(Identifier texture) { this.texture = texture; }
	public Identifier getTexture() { return texture; }

	public RenderLayer getRenderLayer() {
		if (texture == null) return null;
		switch (shaderType) {
			case TRANSLUCENT:
			case TRANSLUCENT_BRIGHT:
				return ENTITY_TRANSLUCENT_CULL.apply(texture);
						default:
				return ENTITY_CUTOUT.apply(texture);
		}
	}

	public void setupGlState() {
		// TODO 26.1 RenderSystem/GlState API
	}

	public void cleanupGlState() {
		// TODO 26.1 RenderSystem/GlState API
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof MaterialProperties other)) return false;
		return shaderType == other.shaderType && Objects.equals(texture, other.texture) && translucent == other.translucent;
	}

	@Override
	public int hashCode() {
		return Objects.hash(shaderType, texture, translucent);
	}
}
