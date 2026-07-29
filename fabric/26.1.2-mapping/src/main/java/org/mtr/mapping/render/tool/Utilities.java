package org.mtr.mapping.render.tool;

import com.mojang.blaze3d.systems.RenderSystem;
import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.shader.ModShaderHandler;

import java.nio.FloatBuffer;

public final class Utilities {

	/**
	 * Set when a GpuDevice draw attempt fails mid-frame.
	 * Forces soft MultiBufferSource path for the rest of the session.
	 */
	private static volatile boolean gpuPathBlocked;

	public static int exchangeLightmapUVBits(int light) {
		return (light >>> 16) | (((short) light) << 16);
	}

	public static float[] copyArray(float[] src) {
		return src == null ? null : src.clone();
	}

	public static Vector3f copy(Vector3f vector3f) {
		return new Vector3f(new org.joml.Vector3f(vector3f.data));
	}

	public static Matrix4f copy(Matrix4f matrix4f) {
		return new Matrix4f(new org.joml.Matrix4f(matrix4f.data));
	}

	public static Matrix4f create() {
		final Matrix4f matrix4f = new Matrix4f();
		matrix4f.data.identity();
		return matrix4f;
	}

	public static void store(Matrix4f matrix4f, FloatBuffer buffer) {
		buffer.put(0, matrix4f.data.m00()).put(1, matrix4f.data.m01()).put(2, matrix4f.data.m02()).put(3, matrix4f.data.m03())
				.put(4, matrix4f.data.m10()).put(5, matrix4f.data.m11()).put(6, matrix4f.data.m12()).put(7, matrix4f.data.m13())
				.put(8, matrix4f.data.m20()).put(9, matrix4f.data.m21()).put(10, matrix4f.data.m22()).put(11, matrix4f.data.m23())
				.put(12, matrix4f.data.m30()).put(13, matrix4f.data.m31()).put(14, matrix4f.data.m32()).put(15, matrix4f.data.m33());
	}

	public static Vector3f transformPosition(Matrix4f matrix4f, Vector3f position) {
		final org.joml.Vector4f r = new org.joml.Vector4f(position.data, 1.0f).mul(matrix4f.data);
		return new Vector3f(r.x, r.y, r.z);
	}

	public static Vector3f transformDirection(Matrix4f matrix4f, Vector3f direction) {
		final org.joml.Vector3f r = new org.joml.Vector3f(direction.data).mulDirection(matrix4f.data);
		return new Vector3f(r);
	}

	public static void blockGpuPath(String reason) {
		if (!gpuPathBlocked) {
			gpuPathBlocked = true;
			System.out.println("[MTR] GPU Optimized Renderer disabled for this session: " + reason + " (soft-path fallback)");
		}
	}

	public static boolean isGpuPathBlocked() {
		return gpuPathBlocked;
	}

	/**
	 * GPU Optimized Renderer via GpuDevice/RenderPass when Iris pack is off.
	 * Draws are deferred to {@code LevelRenderEvents.END_MAIN} with transforms
	 * prepared before createRenderPass. On failure, soft path takes over.
	 */
	public static boolean canUseCustomShader() {
		if (gpuPathBlocked) {
			return false;
		}
		return ModShaderHandler.getInternalHandler().noShaderPackInUse()
				&& !GlStateTracker.isGl4ES()
				&& RenderSystem.tryGetDevice() != null;
	}
}
