package org.mtr.mapping.render.tool;

import com.mojang.blaze3d.opengl.GlStateManager;
import org.lwjgl.opengl.GL33;
import org.mtr.mapping.tool.DummyClass;

import java.util.Locale;

public final class GlStateTracker {
	private static boolean supportVertexAttributeDivisor;
	private static boolean isGl4ES;
	private static final int SHADER_TEXTURE_COUNT = 8;
	private static int vertArrayBinding, arrayBufBinding, elementBufBinding, activeTexture;
	private static final int[] shaderTextures = new int[SHADER_TEXTURE_COUNT];
	private static boolean blendEnabled, depthTestEnabled, cullEnabled, depthMask, isStateProtected;
	private static int depthFunc, blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha, blendEquationRgb, blendEquationAlpha;

	public static void capture() {
		final int contextVersion = GL33.glGetInteger(GL33.GL_MAJOR_VERSION) * 10 + GL33.glGetInteger(GL33.GL_MINOR_VERSION);
		supportVertexAttributeDivisor = contextVersion >= 33;
		final String version = GL33.glGetString(GL33.GL_VERSION);
		isGl4ES = version != null && version.toLowerCase(Locale.ENGLISH).contains("gl4es");
		if (isStateProtected) return;

		vertArrayBinding = GL33.glGetInteger(GL33.GL_VERTEX_ARRAY_BINDING);
		arrayBufBinding = GL33.glGetInteger(GL33.GL_ARRAY_BUFFER_BINDING);
		elementBufBinding = GL33.glGetInteger(GL33.GL_ELEMENT_ARRAY_BUFFER_BINDING);
		activeTexture = GL33.glGetInteger(GL33.GL_ACTIVE_TEXTURE);
		for (int i = 0; i < SHADER_TEXTURE_COUNT; i++) {
			GL33.glActiveTexture(GL33.GL_TEXTURE0 + i);
			shaderTextures[i] = GL33.glGetInteger(GL33.GL_TEXTURE_BINDING_2D);
		}
		GL33.glActiveTexture(activeTexture);
		blendEnabled = GL33.glIsEnabled(GL33.GL_BLEND);
		depthTestEnabled = GL33.glIsEnabled(GL33.GL_DEPTH_TEST);
		cullEnabled = GL33.glIsEnabled(GL33.GL_CULL_FACE);
		depthMask = GL33.glGetBoolean(GL33.GL_DEPTH_WRITEMASK);
		depthFunc = GL33.glGetInteger(GL33.GL_DEPTH_FUNC);
		blendSrcRgb = GL33.glGetInteger(GL33.GL_BLEND_SRC_RGB);
		blendDstRgb = GL33.glGetInteger(GL33.GL_BLEND_DST_RGB);
		blendSrcAlpha = GL33.glGetInteger(GL33.GL_BLEND_SRC_ALPHA);
		blendDstAlpha = GL33.glGetInteger(GL33.GL_BLEND_DST_ALPHA);
		blendEquationRgb = GL33.glGetInteger(GL33.GL_BLEND_EQUATION_RGB);
		blendEquationAlpha = GL33.glGetInteger(GL33.GL_BLEND_EQUATION_ALPHA);
		isStateProtected = true;
	}

	public static void restore() {
		if (!isStateProtected) {
			final IllegalStateException exception = new IllegalStateException("GlStateTracker: Not captured");
			DummyClass.logException(exception);
			throw exception;
		}
		GL33.glBindVertexArray(vertArrayBinding);
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, arrayBufBinding);
		GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, elementBufBinding);
		for (int i = 0; i < SHADER_TEXTURE_COUNT; i++) {
			GlStateManager._activeTexture(GL33.GL_TEXTURE0 + i);
			GlStateManager._bindTexture(shaderTextures[i]);
		}
		GlStateManager._activeTexture(activeTexture);
		setBlend(blendEnabled);
		setDepthTest(depthTestEnabled);
		setCull(cullEnabled);
		GlStateManager._depthMask(depthMask);
		GlStateManager._depthFunc(depthFunc);
		GL33.glBlendFuncSeparate(blendSrcRgb, blendDstRgb, blendSrcAlpha, blendDstAlpha);
		GL33.glBlendEquationSeparate(blendEquationRgb, blendEquationAlpha);
		isStateProtected = false;
	}

	private static void setBlend(boolean enabled) { if (enabled) GlStateManager._enableBlend(); else GlStateManager._disableBlend(); }
	private static void setDepthTest(boolean enabled) { if (enabled) GlStateManager._enableDepthTest(); else GlStateManager._disableDepthTest(); }
	private static void setCull(boolean enabled) { if (enabled) GlStateManager._enableCull(); else GlStateManager._disableCull(); }

	public static void assertProtected() {
		if (!isStateProtected) {
			final IllegalStateException exception = new IllegalStateException("GlStateTracker: Not protected");
			DummyClass.logException(exception);
			throw exception;
		}
	}

	public static boolean supportVertexAttributeDivisor() { return supportVertexAttributeDivisor; }
	public static boolean isGl4ES() { return isGl4ES; }
	public static boolean isStateDirty() { return isStateProtected; }
}
