package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.RenderLayer;
import org.mtr.mapping.render.batch.BatchManager;
import org.mtr.mapping.render.model.Face;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.shader.GpuDrawScheduler;
import org.mtr.mapping.render.shader.ModShaderHandler;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.Vertex;
import org.mtr.mapping.render.vertex.VertexAttributeState;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public final class OptimizedRenderer extends DummyClass {

	private final BatchManager batchManager = new BatchManager();
	private final ShaderManager shaderManager = new ShaderManager();
	private int reloadDepth;
	private boolean pendingOpaqueFlush;
	private boolean pendingTranslucentFlush;
	/** When BEFORE_TRANSLUCENT cannot open a pass, opaque draws retry on END_MAIN. */
	private boolean opaqueNeedsEndMainRetry;

	// Soft-path fallback state
	private final List<SoftDrawCall> pendingSoft = new ArrayList<>();
	@Nullable
	private MultiBufferSource softBufferSource;

	@MappedMethod
	public OptimizedRenderer() {
		GpuDrawScheduler.setFlushers(this::flushGpuOpaque, this::flushGpuTranslucent);
	}

	@MappedMethod
	public void beginReload() {
		if (reloadDepth++ == 0) {
			shaderManager.reloadShaders();
		}
		pendingSoft.clear();
		softBufferSource = null;
	}

	@MappedMethod
	public void finishReload() {
		if (reloadDepth > 0) {
			reloadDepth--;
		}
	}

	@MappedMethod
	public void runWithProtectedState(Runnable runnable) {
		runnable.run();
	}

	@MappedMethod
	public <T> T runWithProtectedState(Supplier<T> supplier) {
		return supplier.get();
	}

	@MappedMethod
	public void queue(OptimizedModel optimizedModel, GraphicsHolder graphicsHolder, int color, int light) {
		queue(optimizedModel, graphicsHolder, color, light, false);
	}

	/** Force soft MultiBufferSource path (rails). */
	@MappedMethod
	public void queueSoft(OptimizedModel optimizedModel, GraphicsHolder graphicsHolder, int color, int light) {
		queue(optimizedModel, graphicsHolder, color, light, true);
	}

	private void queue(OptimizedModel optimizedModel, GraphicsHolder graphicsHolder, int color, int light, boolean forceSoft) {
		if (graphicsHolder.matrixStack == null) {
			return;
		}

		final boolean useGpu = !forceSoft && Utilities.canUseCustomShader() && shaderManager.isReady() && optimizedModel.hasGpuParts();
		if (useGpu) {
			// Entity PoseStack is camera-relative translation only (no view rotation). Soft path
			// bakes that pose into verts; RenderType.draw then applies getModelViewMatrix()
			// (camera view rotation). GPU keeps local verts — multiply view*pose here.
			final Matrix4f modelView = new Matrix4f();
			net.minecraft.client.Minecraft.getInstance().gameRenderer.getMainCamera().getViewRotationMatrix(modelView);
			final Matrix4f rsView = com.mojang.blaze3d.systems.RenderSystem.getModelViewMatrix();
			// If RenderSystem already has a non-identity view (bobbing etc.), prefer it.
			if (Math.abs(rsView.m00() - 1f) > 1e-5f || Math.abs(rsView.m11() - 1f) > 1e-5f
					|| Math.abs(rsView.m22() - 1f) > 1e-5f || Math.abs(rsView.m30()) > 1e-5f
					|| Math.abs(rsView.m31()) > 1e-5f || Math.abs(rsView.m32()) > 1e-5f) {
				modelView.set(rsView);
			}
			modelView.mul(graphicsHolder.matrixStack.last().pose());
			final org.mtr.mapping.holder.Matrix4f modelMat = new org.mtr.mapping.holder.Matrix4f(modelView);
			batchManager.queue(optimizedModel.uploadedParts, new VertexAttributeState(color, light, modelMat));
			return;
		}

		// Soft-path fallback (Iris / upload failed / shaders not ready / forced)
		if (graphicsHolder.vertexConsumerProvider == null || optimizedModel.softMeshes.isEmpty()) {
			return;
		}
		softBufferSource = graphicsHolder.vertexConsumerProvider;
		final PoseStack.Pose entry = graphicsHolder.matrixStack.last();
		pendingSoft.add(new SoftDrawCall(optimizedModel.softMeshes, entry.pose(), entry.normal(), color, light));
	}

	@MappedMethod
	public void render(boolean renderTranslucent) {
		// GPU draws must NOT run during MainRenderer (inside LevelRenderer RenderPass).
		// Opaque → BEFORE_TRANSLUCENT (visible through glass); translucent → END_MAIN.
		if (Utilities.canUseCustomShader() && shaderManager.isReady()) {
			ShaderManager.captureWorldProjection();
			pendingOpaqueFlush = true;
			pendingTranslucentFlush = renderTranslucent;
			opaqueNeedsEndMainRetry = false;
		} else {
			pendingOpaqueFlush = false;
			pendingTranslucentFlush = false;
			opaqueNeedsEndMainRetry = false;
		}
		flushSoft(renderTranslucent);
	}

	private void flushGpuOpaque() {
		if (!pendingOpaqueFlush) {
			return;
		}
		pendingOpaqueFlush = false;
		if (!Utilities.canUseCustomShader() || !shaderManager.isReady()) {
			batchManager.drawAll(shaderManager, false);
			return;
		}
		if (!batchManager.drawOpaque(shaderManager)) {
			// Pass still open — keep remaining batches and finish on END_MAIN.
			opaqueNeedsEndMainRetry = true;
			pendingOpaqueFlush = true;
		}
	}

	private void flushGpuTranslucent() {
		if (opaqueNeedsEndMainRetry || pendingOpaqueFlush) {
			opaqueNeedsEndMainRetry = false;
			pendingOpaqueFlush = false;
			if (Utilities.canUseCustomShader() && shaderManager.isReady()) {
				batchManager.drawOpaque(shaderManager);
			}
		}
		if (!pendingTranslucentFlush) {
			batchManager.discardTranslucent();
			return;
		}
		pendingTranslucentFlush = false;
		if (!Utilities.canUseCustomShader() || !shaderManager.isReady()) {
			return;
		}
		batchManager.drawTranslucent(shaderManager);
	}

	@MappedMethod
	public void drawAll() {
	}

	@MappedMethod
	public static boolean hasOptimizedRendering() {
		return true;
	}

	@MappedMethod
	public static boolean renderingShadows() {
		return ModShaderHandler.renderingShadows();
	}

	private void flushSoft(boolean renderTranslucent) {
		if (softBufferSource == null || pendingSoft.isEmpty()) {
			pendingSoft.clear();
			return;
		}
		final List<SoftEmit> emits = new ArrayList<>(pendingSoft.size() * 4);
		for (final SoftDrawCall call : pendingSoft) {
			for (final RawMesh mesh : call.meshes) {
				if (!renderTranslucent && mesh.materialProperties.translucent) {
					continue;
				}
				emits.add(new SoftEmit(mesh, call));
			}
		}
		pendingSoft.clear();
		emits.sort(SOFT_EMIT_ORDER);
		final MultiBufferSource source = softBufferSource;
		softBufferSource = null;
		for (final SoftEmit emit : emits) {
			emitSoftMesh(emit.mesh, emit.call.pose, emit.call.normal, source, emit.call.color, emit.call.light);
		}
	}

	private static final Comparator<SoftEmit> SOFT_EMIT_ORDER = (a, b) -> {
		final int translucentCmp = Boolean.compare(a.mesh.materialProperties.translucent, b.mesh.materialProperties.translucent);
		if (translucentCmp != 0) {
			return translucentCmp;
		}
		final RenderLayer layerA = a.mesh.materialProperties.getRenderLayer();
		final RenderLayer layerB = b.mesh.materialProperties.getRenderLayer();
		if (layerA == layerB) {
			return 0;
		}
		if (layerA == null) {
			return -1;
		}
		if (layerB == null) {
			return 1;
		}
		return Integer.compare(System.identityHashCode(layerA.data), System.identityHashCode(layerB.data));
	};

	private static void emitSoftMesh(RawMesh mesh, Matrix4f posMatrix, Matrix3f normalMatrix, MultiBufferSource bufferSource, int color, int passedLight) {
		final RenderLayer renderLayer = mesh.materialProperties.getRenderLayer();
		if (renderLayer == null) {
			return;
		}
		final com.mojang.blaze3d.vertex.VertexConsumer consumer = bufferSource.getBuffer(renderLayer.data);
		final int effectiveColor = mesh.materialProperties.vertexAttributeState.color != null
				? mesh.materialProperties.vertexAttributeState.color : color;
		final int ca = (effectiveColor >> 24) & 0xFF;
		final int cr = (effectiveColor >> 16) & 0xFF;
		final int cg = (effectiveColor >> 8) & 0xFF;
		final int cb = effectiveColor & 0xFF;
		final int alpha = ca == 0 ? 255 : ca;
		final int effectiveLight = mesh.materialProperties.vertexAttributeState.lightmapUV != null
				? mesh.materialProperties.vertexAttributeState.lightmapUV
				: passedLight;
		final Vector3f norm = new Vector3f();
		for (final Face face : mesh.faces) {
			final int[] indices = face.vertices;
			if (indices.length < 3) {
				continue;
			}
			// Entity RenderLayers on 26.1 use VertexFormat.Mode.QUADS (ENTITY_SNIPPET).
			// OBJ soft meshes are triangles — emit as degenerate quads (v0,v1,v2,v2).
			// Emitting 3 verts into QUADS mode remaps across triangle boundaries → shredded meshes.
			if (indices.length == 3) {
				emitSoftVertex(consumer, mesh, indices[0], posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
				emitSoftVertex(consumer, mesh, indices[1], posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
				emitSoftVertex(consumer, mesh, indices[2], posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
				emitSoftVertex(consumer, mesh, indices[2], posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
			} else if (indices.length == 4) {
				for (final int index : indices) {
					emitSoftVertex(consumer, mesh, index, posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
				}
			} else {
				for (int i = 2; i < indices.length; i++) {
					emitSoftVertex(consumer, mesh, indices[0], posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
					emitSoftVertex(consumer, mesh, indices[i - 1], posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
					emitSoftVertex(consumer, mesh, indices[i], posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
					emitSoftVertex(consumer, mesh, indices[i], posMatrix, normalMatrix, cr, cg, cb, alpha, effectiveLight, norm);
				}
			}
		}
	}

	private static void emitSoftVertex(
			com.mojang.blaze3d.vertex.VertexConsumer consumer,
			RawMesh mesh,
			int index,
			Matrix4f posMatrix,
			Matrix3f normalMatrix,
			int cr, int cg, int cb, int alpha,
			int light,
			Vector3f norm
	) {
		final Vertex vertex = mesh.vertices.get(index);
		norm.set(vertex.normal.data);
		norm.mul(normalMatrix);
		if (norm.lengthSquared() > 1.0E-6F) {
			norm.normalize();
		} else {
			norm.set(0, 1, 0);
		}
		consumer.addVertex(posMatrix, vertex.position.data.x, vertex.position.data.y, vertex.position.data.z)
				.setColor(cr, cg, cb, alpha)
				.setUv(vertex.u, vertex.v)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(norm.x, norm.y, norm.z);
	}

	private static final class SoftDrawCall {
		final List<RawMesh> meshes;
		final Matrix4f pose;
		final Matrix3f normal;
		final int color;
		final int light;

		SoftDrawCall(List<RawMesh> meshes, Matrix4f pose, Matrix3f normal, int color, int light) {
			this.meshes = meshes;
			this.pose = new Matrix4f(pose);
			this.normal = new Matrix3f(normal);
			this.color = color;
			this.light = light;
		}
	}

	private static final class SoftEmit {
		final RawMesh mesh;
		final SoftDrawCall call;

		SoftEmit(RawMesh mesh, SoftDrawCall call) {
			this.mesh = mesh;
			this.call = call;
		}
	}
}
