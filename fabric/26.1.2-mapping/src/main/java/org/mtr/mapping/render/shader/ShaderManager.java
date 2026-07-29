package org.mtr.mapping.render.shader;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Lightmap;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.attribute.EnvironmentAttributes;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.VertexAttributeState;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Draws uploaded {@link VertexArray} meshes through Minecraft 26.1 {@link RenderPass}.
 * All {@link net.minecraft.client.renderer.DynamicUniforms#writeTransform} calls must happen
 * BEFORE {@code createRenderPass} — mapBuffer is illegal while a pass is open.
 * <p>
 * Entity pipelines sample Sampler1 (overlay) + Sampler2 (lightmap). Missing those made
 * meshes pure-black / “shadow-only”. World projection is restored if END_MAIN ran after
 * GameRenderer swapped it.
 */
public final class ShaderManager {

	private static final Vector3f MODEL_OFFSET = new Vector3f();
	private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
	private static final Vector4f COLOR_SCRATCH = new Vector4f(1f, 1f, 1f, 1f);
	private static final Matrix4f MODEL_VIEW_SCRATCH = new Matrix4f();
	private static boolean loggedPassFailure;

	@Nullable
	private static GpuBufferSlice capturedProjection;
	@Nullable
	private static com.mojang.blaze3d.ProjectionType capturedProjectionType;

	private boolean ready;

	public boolean isReady() {
		return ready;
	}

	private static RenderPipeline pipelineCutout;
	private static RenderPipeline pipelineTranslucent;
	private static RenderPipeline pipelineGlowingCutout;
	private static RenderPipeline pipelineGlowingTranslucent;
	private static boolean pipelinesReady;

	public void reloadShaders() {
		ready = RenderSystem.tryGetDevice() != null;
		ensurePipelines();
	}

	private static void ensurePipelines() {
		if (pipelinesReady) {
			return;
		}
		try {
			final java.lang.reflect.Field snippetField = RenderPipelines.class.getDeclaredField("ENTITY_SNIPPET");
			snippetField.setAccessible(true);
			final RenderPipeline.Snippet entitySnippet = (RenderPipeline.Snippet) snippetField.get(null);

			final java.lang.reflect.Field emissiveField = RenderPipelines.class.getDeclaredField("ENTITY_EMISSIVE_SNIPPET");
			emissiveField.setAccessible(true);
			final RenderPipeline.Snippet emissiveSnippet = (RenderPipeline.Snippet) emissiveField.get(null);

			// Match soft entityCutout (no cull). Cull hid 3D rail meshes with mixed winding
			// and made bogies look sunk into the flat 2D rail strip. Door double-face on
			// some packs is a separate model issue.
			pipelineCutout = RenderPipeline.builder(entitySnippet)
					.withLocation("mtr/optimized_entity_cutout")
					.withShaderDefine("ALPHA_CUTOUT", 0.1f)
					.withShaderDefine("NO_CARDINAL_LIGHTING")
					.withCull(false)
					.build();
			pipelineTranslucent = RenderPipeline.builder(entitySnippet)
					.withLocation("mtr/optimized_entity_translucent")
					.withShaderDefine("NO_CARDINAL_LIGHTING")
					.withColorTargetState(new com.mojang.blaze3d.pipeline.ColorTargetState(
							com.mojang.blaze3d.pipeline.BlendFunction.TRANSLUCENT))
					.withCull(false)
					.build();
			// Glowing must stay on ENTITY format — BEACON_BEAM uses a different vertex layout
			// and turns light meshes into black "tubes" from fixtures.
			pipelineGlowingCutout = RenderPipeline.builder(emissiveSnippet)
					.withLocation("mtr/optimized_entity_glowing_cutout")
					.withShaderDefine("ALPHA_CUTOUT", 0.1f)
					.withShaderDefine("NO_CARDINAL_LIGHTING")
					.withCull(false)
					.build();
			pipelineGlowingTranslucent = RenderPipeline.builder(emissiveSnippet)
					.withLocation("mtr/optimized_entity_glowing_translucent")
					.withShaderDefine("NO_CARDINAL_LIGHTING")
					.withColorTargetState(new com.mojang.blaze3d.pipeline.ColorTargetState(
							com.mojang.blaze3d.pipeline.BlendFunction.TRANSLUCENT))
					.withCull(false)
					.build();
			pipelinesReady = true;
		} catch (Exception e) {
			DummyClass.logException(e);
			pipelineCutout = RenderPipelines.ENTITY_CUTOUT;
			pipelineTranslucent = RenderPipelines.ENTITY_TRANSLUCENT_CULL;
			pipelineGlowingCutout = RenderPipelines.ENTITY_CUTOUT;
			pipelineGlowingTranslucent = RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE;
			pipelinesReady = true;
		}
	}

	private static RenderPipeline pipelineFor(MaterialProperties materialProperties) {
		ensurePipelines();
		switch (materialProperties.shaderType) {
			case TRANSLUCENT:
			case TRANSLUCENT_BRIGHT:
				return pipelineTranslucent;
			case TRANSLUCENT_GLOWING:
				return pipelineGlowingTranslucent;
			case CUTOUT_GLOWING:
				return pipelineGlowingCutout;
			case CUTOUT_BRIGHT:
			case CUTOUT:
			default:
				return pipelineCutout;
		}
	}

	/** Call while world projection is still active (MainRenderer / entity submit). */
	public static void captureWorldProjection() {
		capturedProjection = RenderSystem.getProjectionMatrixBuffer();
		capturedProjectionType = RenderSystem.getProjectionType();
	}

	public void setupShaderBatchState(MaterialProperties materialProperties) {
	}

	public void cleanupShaderBatchState() {
	}

	/**
	 * @return {@code true} if batches were consumed; {@code false} if the draw must be
	 * retried later (nested RenderPass / mapBuffer) — batches are left intact.
	 */
	public boolean drawBatches(Map<MaterialProperties, List<RenderCall>> batches) {
		if (!ready || batches.isEmpty()) {
			batches.clear();
			return true;
		}

		final Minecraft client = Minecraft.getInstance();
		final RenderTarget target = client.getMainRenderTarget();
		final GpuTextureView colorTexture = target.getColorTextureView();
		final GpuTextureView depthTexture = target.getDepthTextureView();
		if (colorTexture == null || depthTexture == null) {
			batches.clear();
			return true;
		}

		final AuxTextures aux = resolveAuxTextures(client);
		if (aux == null) {
			batches.clear();
			return true;
		}

		final Map<MaterialProperties, BoundTexture> bound = new HashMap<>();
		BoundTexture missing = null;
		for (final MaterialProperties materialProperties : batches.keySet()) {
			BoundTexture texture = resolveTexture(materialProperties.getTexture());
			if (texture == null) {
				// Never drop a batch — missing coupler/rail textures became pure-black holes.
				if (missing == null) {
					missing = resolveTexture(new Identifier("minecraft", "textures/misc/unknown_pack.png"));
					if (missing == null) {
						missing = resolveTexture(new Identifier("minecraft", "textures/block/white_concrete.png"));
					}
				}
				texture = missing;
			}
			if (texture != null) {
				bound.put(materialProperties, texture);
			}
		}

		final boolean restoreProjection = capturedProjection != null && capturedProjectionType != null;
		if (restoreProjection) {
			RenderSystem.backupProjectionMatrix();
			RenderSystem.setProjectionMatrix(capturedProjection, capturedProjectionType);
		}

		try {
			// Prepare DynamicTransforms OUTSIDE any RenderPass (mapBuffer requirement).
			final List<PreparedDraw> prepared = new ArrayList<>();
			try {
				batches.forEach((materialProperties, renderCalls) -> {
					if (renderCalls == null || renderCalls.isEmpty()) {
						return;
					}
					final BoundTexture texture = bound.get(materialProperties);
					if (texture == null) {
						return;
					}
					final RenderPipeline pipeline = pipelineFor(materialProperties);
					for (final RenderCall call : renderCalls) {
						final GpuBufferSlice transform = prepareTransform(call, materialProperties);
						if (transform != null) {
							prepared.add(new PreparedDraw(pipeline, texture, call.vertexArray, transform));
						}
					}
				});
			} catch (IllegalStateException e) {
				return false;
			}

			if (prepared.isEmpty()) {
				batches.clear();
				return true;
			}

			try (RenderPass renderPass = RenderSystem.getDevice()
					.createCommandEncoder()
					.createRenderPass(() -> "mtr_optimized_renderer", colorTexture, OptionalInt.empty(), depthTexture, OptionalDouble.empty())) {
				RenderSystem.bindDefaultUniforms(renderPass);
				RenderPipeline lastPipeline = null;
				BoundTexture lastTexture = null;
				boolean lastBoundAux = false;
				for (final PreparedDraw draw : prepared) {
					if (draw.pipeline != lastPipeline) {
						renderPass.setPipeline(draw.pipeline);
						lastPipeline = draw.pipeline;
						lastBoundAux = false;
					}
					if (!lastBoundAux) {
						final java.util.List<String> samplers = draw.pipeline.getSamplers();
						if (samplers.contains("Sampler1")) {
							renderPass.bindTexture("Sampler1", aux.overlay, aux.sampler);
						}
						if (samplers.contains("Sampler2")) {
							renderPass.bindTexture("Sampler2", aux.lightmap, aux.sampler);
						}
						lastBoundAux = true;
					}
					if (draw.texture != lastTexture) {
						renderPass.bindTexture("Sampler0", draw.texture.view, draw.texture.sampler);
						lastTexture = draw.texture;
					}
					renderPass.setUniform("DynamicTransforms", draw.transform);
					renderPass.setVertexBuffer(0, draw.vertexArray.vertexBuffer);
					final GpuBuffer indexBuffer;
					final VertexFormat.IndexType indexType;
					if (draw.vertexArray.sequentialQuads) {
						final RenderSystem.AutoStorageIndexBuffer sequential =
								RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
						indexBuffer = sequential.getBuffer(draw.vertexArray.indexCount);
						indexType = sequential.type();
					} else {
						indexBuffer = draw.vertexArray.indexBuffer;
						indexType = draw.vertexArray.indexType;
					}
					renderPass.setIndexBuffer(indexBuffer, indexType);
					renderPass.drawIndexed(0, 0, draw.vertexArray.indexCount, 1);
				}
			} catch (IllegalStateException e) {
				return false;
			} catch (Exception e) {
				DummyClass.logException(e);
				failGpu(new IllegalStateException(e));
				batches.clear();
				return true;
			}
			batches.clear();
			return true;
		} finally {
			if (restoreProjection) {
				try {
					RenderSystem.restoreProjectionMatrix();
				} catch (Exception ignored) {
				}
			}
		}
	}

	private static GpuBufferSlice prepareTransform(RenderCall call, MaterialProperties materialProperties) {
		final VertexAttributeState state = call.vertexAttributeState;
		if (state.matrix4f == null) {
			return null;
		}
		MODEL_VIEW_SCRATCH.set(state.matrix4f.data);

		final int color = effectiveColor(materialProperties, state);
		final float a = alphaOf(color);
		float r = ((color >>> 16) & 0xFF) / 255f;
		float g = ((color >>> 8) & 0xFF) / 255f;
		float b = (color & 0xFF) / 255f;

		final float brightness = lightBrightness(materialProperties, state);
		final Identifier texture = materialProperties.getTexture();
		final boolean coupling = texture != null && texture.data.getPath().contains("coupling");
		// Couplers use a dark albedo; ColorModulator * MTL made them pitch-black. Force full tint.
		if (coupling) {
			COLOR_SCRATCH.set(1f, 1f, 1f, a);
		} else {
			COLOR_SCRATCH.set(r * brightness, g * brightness, b * brightness, a);
		}

		return RenderSystem.getDynamicUniforms()
				.writeTransform(MODEL_VIEW_SCRATCH, COLOR_SCRATCH, MODEL_OFFSET, TEXTURE_MATRIX);
	}

	private static void failGpu(IllegalStateException e) {
		Utilities.blockGpuPath(e.getMessage());
		if (!loggedPassFailure) {
			loggedPassFailure = true;
			DummyClass.logException(e);
		}
	}

	private static int effectiveColor(MaterialProperties materialProperties, VertexAttributeState state) {
		// Textured OBJ meshes: MTL Kd is already baked into the albedo. Multiplying it again
		// via ColorModulator crushed dark metals (couplers) to near-black on the GPU path.
		final Identifier texture = materialProperties.getTexture();
		if (texture != null) {
			final String path = texture.data.getPath();
			if (!path.isEmpty() && !path.endsWith("white.png") && !path.endsWith("transparent.png")) {
				if (state.color != null) {
					return state.color;
				}
				return 0xFFFFFFFF;
			}
		}
		if (materialProperties.vertexAttributeState.color != null) {
			return materialProperties.vertexAttributeState.color;
		}
		if (state.color != null) {
			return state.color;
		}
		return 0xFFFFFFFF;
	}

	private static float alphaOf(int argb) {
		final int a = (argb >>> 24) & 0xFF;
		return a == 0 ? 1f : a / 255f;
	}

	private static float lightBrightness(MaterialProperties materialProperties, VertexAttributeState state) {
		if (materialProperties.vertexAttributeState.lightmapUV != null) {
			return 1f;
		}
		switch (materialProperties.shaderType) {
			case CUTOUT_BRIGHT:
			case TRANSLUCENT_BRIGHT:
			case CUTOUT_GLOWING:
			case TRANSLUCENT_GLOWING:
				return 1f;
			default:
				break;
		}
		if (state.lightmapUV == null) {
			return 1f;
		}
		// state.lightmapUV was exchanged once in VertexAttributeState; exchange again to
		// recover Minecraft's packed block/sky format.
		final int light = Utilities.exchangeLightmapUVBits(state.lightmapUV);
		final int block = (light >>> 4) & 0xF;
		final int sky = (light >>> 20) & 0xF;
		// VBOs bake fullbright lightmap UVs, so ColorModulator approximates the lightmap
		// sample. Do NOT use Level.getSkyDarken() — in 26.1 that int is 15-SKY_LIGHT_LEVEL
		// and subtracting it from packed sky made daytime exteriors/couplers near-black.
		// SKY_LIGHT_FACTOR is what the lightmap itself uses for time-of-day.
		final Minecraft client = Minecraft.getInstance();
		if (client.level == null) {
			return Math.max(block, sky) / 15f;
		}
		final float skyFactor = skyLightFactor(client);
		final float blockB = Lightmap.getBrightness(client.level.dimensionType(), block);
		final float skyB = Lightmap.getBrightness(client.level.dimensionType(), sky) * skyFactor;
		// Floor: VBO is fullbright; ColorModulator must not crush textured parts to black.
		return Math.max(0.4f, Math.max(blockB, skyB));
	}

	private static float skyLightFactor(Minecraft client) {
		try {
			final Float factor = client.level.environmentAttributes()
					.getDimensionValue(EnvironmentAttributes.SKY_LIGHT_FACTOR);
			if (factor != null && factor >= 0f && factor <= 1.5f) {
				return factor;
			}
		} catch (Throwable ignored) {
		}
		return 1f;
	}

	@Nullable
	private static AuxTextures resolveAuxTextures(Minecraft client) {
		try {
			final GameRenderer gameRenderer = client.gameRenderer;
			final OverlayTexture overlayTexture = gameRenderer.overlayTexture();
			final GpuTextureView overlay = overlayTexture.getTextureView();
			final GpuTextureView lightmap = gameRenderer.levelLightmap();
			if (overlay == null || lightmap == null) {
				return null;
			}
			final GpuSampler sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR);
			return new AuxTextures(overlay, lightmap, sampler);
		} catch (Exception e) {
			DummyClass.logException(e);
			return null;
		}
	}

	private static BoundTexture resolveTexture(Identifier texture) {
		if (texture == null) {
			return null;
		}
		try {
			final AbstractTexture abstractTexture = Minecraft.getInstance().getTextureManager().getTexture(texture.data);
			if (abstractTexture == null) {
				return null;
			}
			final GpuTextureView view = abstractTexture.getTextureView();
			final GpuSampler sampler = abstractTexture.getSampler();
			if (view == null || sampler == null) {
				return null;
			}
			return new BoundTexture(view, sampler);
		} catch (Exception e) {
			DummyClass.logException(e);
			return null;
		}
	}

	public static int resolveGlTextureId(Identifier texture) {
		return 0;
	}

	private record BoundTexture(GpuTextureView view, GpuSampler sampler) {
	}

	private record AuxTextures(GpuTextureView overlay, GpuTextureView lightmap, GpuSampler sampler) {
	}

	private record PreparedDraw(RenderPipeline pipeline, BoundTexture texture, VertexArray vertexArray, GpuBufferSlice transform) {
	}

	public static final class RenderCall {
		public final VertexArray vertexArray;
		public final VertexAttributeState vertexAttributeState;

		public RenderCall(VertexArray vertexArray, VertexAttributeState vertexAttributeState) {
			this.vertexArray = vertexArray;
			this.vertexAttributeState = vertexAttributeState;
		}
	}
}
