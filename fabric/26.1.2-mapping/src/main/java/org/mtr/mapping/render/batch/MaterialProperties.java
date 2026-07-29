package org.mtr.mapping.render.batch;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL33;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.mapper.OptimizedModel;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Properties regarding material. Set during model loading. Affects batching.
 */
public final class MaterialProperties {

	private Identifier texture;
	public final OptimizedModel.ShaderType shaderType;
	public final VertexAttributeState vertexAttributeState;
	public final boolean translucent;
	public final boolean writeDepthBuf;
	public final boolean cutoutHack;

	public MaterialProperties(OptimizedModel.ShaderType shaderType, Identifier texture, @Nullable Integer color) {
		this.shaderType = shaderType;
		this.texture = texture;
		switch (shaderType) {
			default:
			case CUTOUT:
				translucent = false;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
			case TRANSLUCENT:
				translucent = true;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
			case CUTOUT_BRIGHT:
				translucent = false;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, 15 << 4 | 15 << 20);
				break;
			case TRANSLUCENT_BRIGHT:
				translucent = true;
				writeDepthBuf = true;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, 15 << 4 | 15 << 20);
				break;
			case CUTOUT_GLOWING:
				translucent = false;
				writeDepthBuf = true;
				cutoutHack = true;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
			case TRANSLUCENT_GLOWING:
				translucent = true;
				writeDepthBuf = false;
				cutoutHack = false;
				vertexAttributeState = new VertexAttributeState(color, null);
				break;
		}
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}

	public Identifier getTexture() {
		return texture;
	}

	/**
	 * Soft-path RenderLayer (used when GPU custom shaders are unavailable).
	 */
	public RenderLayer getRenderLayer() {
		if (texture == null) {
			return RenderLayer.getCutout();
		}
		switch (shaderType) {
			case TRANSLUCENT:
			case TRANSLUCENT_BRIGHT:
				return RenderLayer.getEntityTranslucentCull(texture);
			case CUTOUT_GLOWING:
			case TRANSLUCENT_GLOWING:
				return RenderLayer.getBeaconBeam(texture, translucent);
			default:
				return RenderLayer.getEntityCutout(texture);
		}
	}

	public void setupCompositeState() {
		final int glId = ShaderManager.resolveGlTextureId(texture);
		GlStateManager._activeTexture(GL33.GL_TEXTURE0);
		GlStateManager._bindTexture(glId);

		if (translucent || cutoutHack) {
			GlStateManager._enableBlend();
			GlStateManager._blendFuncSeparate(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA, GL33.GL_ONE, GL33.GL_ONE_MINUS_SRC_ALPHA);
		} else {
			GlStateManager._disableBlend();
		}

		GlStateManager._enableDepthTest();
		GlStateManager._depthFunc(GL33.GL_LEQUAL);
		GlStateManager._enableCull();
		GlStateManager._depthMask(writeDepthBuf);
	}

	public void setupGlState() {
		setupCompositeState();
	}

	public void cleanupGlState() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof MaterialProperties)) {
			return false;
		}
		final MaterialProperties other = (MaterialProperties) obj;
		return shaderType == other.shaderType
				&& Objects.equals(texture, other.texture)
				&& Objects.equals(vertexAttributeState, other.vertexAttributeState)
				&& translucent == other.translucent
				&& writeDepthBuf == other.writeDepthBuf
				&& cutoutHack == other.cutoutHack;
	}

	@Override
	public int hashCode() {
		return Objects.hash(shaderType, texture, vertexAttributeState, translucent, writeDepthBuf, cutoutHack);
	}
}
