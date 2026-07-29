package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.model.RawModel;
import org.mtr.mapping.render.obj.AtlasManager;
import org.mtr.mapping.render.obj.ObjModelLoader;
import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.tool.Utilities;
import org.mtr.mapping.render.vertex.CapturingVertexConsumer;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;
import org.mtr.mapping.render.vertex.VertexAttributeSource;
import org.mtr.mapping.render.vertex.VertexAttributeType;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public final class OptimizedModel extends DummyClass {

	public enum ShaderType {
		CUTOUT, TRANSLUCENT,
		CUTOUT_BRIGHT, TRANSLUCENT_BRIGHT,
		CUTOUT_GLOWING, TRANSLUCENT_GLOWING
	}

	/** GPU-uploaded VAOs (preferred path). */
	final List<VertexArray> uploadedParts;
	/** CPU meshes kept for Iris / soft-path fallback. */
	final List<RawMesh> softMeshes;

	private static final AtlasManager ATLAS_MANAGER = new AtlasManager();

	private static final VertexAttributeMapping DEFAULT_MAPPING = new VertexAttributeMapping.Builder()
			.set(VertexAttributeType.POSITION, VertexAttributeSource.VERTEX_BUFFER)
			.set(VertexAttributeType.COLOR, VertexAttributeSource.GLOBAL)
			.set(VertexAttributeType.UV_TEXTURE, VertexAttributeSource.VERTEX_BUFFER)
			.set(VertexAttributeType.UV_OVERLAY, VertexAttributeSource.GLOBAL)
			.set(VertexAttributeType.UV_LIGHTMAP, VertexAttributeSource.GLOBAL)
			.set(VertexAttributeType.NORMAL, VertexAttributeSource.VERTEX_BUFFER)
			.set(VertexAttributeType.MATRIX_MODEL, VertexAttributeSource.GLOBAL)
			.build();

	@MappedMethod
	public static OptimizedModel fromMaterialGroups(Collection<MaterialGroup> materialGroups) {
		final CapturingVertexConsumer cvc = new CapturingVertexConsumer();
		materialGroups.forEach(materialGroup -> {
			cvc.beginStage(materialGroup.materialProperties);
			materialGroup.modelPartConsumers.forEach(c -> c.accept(cvc));
		});
		cvc.flushCurrent();

		cvc.rawModel.triangulate();
		final RawModel rawModel = new RawModel();
		cvc.rawModel.iterateRawMeshList(rawModel::append);
		rawModel.distinct();
		return fromRawModel(rawModel);
	}

	@MappedMethod
	public static OptimizedModel fromObjModels(Collection<ObjModel> objModels) {
		final RawModel rawModel = new RawModel();
		// Append copies first — never mutate ObjModel.rawModel in place. generateNormals()
		// rewrites the vertex list; running it on a shared source across cache rebuilds
		// corrupts later soft-path draws.
		objModels.forEach(objModel -> rawModel.append(objModel.rawModel));
		rawModel.generateNormals();
		rawModel.distinct();
		rawModel.triangulate();
		return fromRawModel(rawModel);
	}

	private static OptimizedModel fromRawModel(RawModel rawModel) {
		final List<RawMesh> meshes = new ArrayList<>();
		rawModel.iterateRawMeshList(meshes::add);
		List<VertexArray> uploaded = Collections.emptyList();
		try {
			if (Utilities.canUseCustomShader()) {
				uploaded = rawModel.upload(DEFAULT_MAPPING);
			}
		} catch (Exception e) {
			DummyClass.logException(e);
			uploaded = Collections.emptyList();
		}
		return new OptimizedModel(uploaded, meshes);
	}

	public OptimizedModel(List<VertexArray> uploadedParts, List<RawMesh> softMeshes) {
		this.uploadedParts = uploadedParts;
		this.softMeshes = softMeshes;
	}

	/** Soft-only constructor (legacy / fallback). */
	public OptimizedModel(List<RawMesh> softMeshes) {
		this(Collections.emptyList(), softMeshes);
	}

	@MappedMethod
	public OptimizedModel(OptimizedModel... optimizedModels) {
		uploadedParts = new ArrayList<>();
		softMeshes = new ArrayList<>();
		for (final OptimizedModel optimizedModel : optimizedModels) {
			if (optimizedModel != null) {
				uploadedParts.addAll(optimizedModel.uploadedParts);
				softMeshes.addAll(optimizedModel.softMeshes);
			}
		}
	}

	@MappedMethod
	public OptimizedModel() {
		this(Collections.emptyList(), Collections.emptyList());
	}

	@MappedMethod
	public OptimizedModel(RawModel rawModel) {
		final OptimizedModel built = fromRawModel(rawModel);
		this.uploadedParts = built.uploadedParts;
		this.softMeshes = built.softMeshes;
	}

	@MappedMethod
	public void close() {
		uploadedParts.forEach(VertexArray::close);
	}

	@MappedMethod
	public List<VertexArray> getVertexArrays() {
		return uploadedParts;
	}

	public boolean hasGpuParts() {
		return !uploadedParts.isEmpty();
	}

	/** Soft MultiBufferSource path only — used for rails where GPU draws are unreliable. */
	@MappedMethod
	public OptimizedModel softOnlyCopy() {
		return new OptimizedModel(softMeshes);
	}

	// ──────────────────────────────────────────────────────────────────────────
	// MaterialGroup — captures ModelPart cubes for fromMaterialGroups
	// ──────────────────────────────────────────────────────────────────────────

	public static final class MaterialGroup {

		final MaterialProperties materialProperties;
		final List<Consumer<CapturingVertexConsumer>> modelPartConsumers = new ArrayList<>();

		@MappedMethod
		public MaterialGroup(ShaderType shaderType, Identifier texture) {
			materialProperties = new MaterialProperties(shaderType, texture, null);
		}

		@MappedMethod
		public void addCube(ModelPartExtension modelPart, double x, double y, double z, boolean flipped, int light) {
			if (modelPart.modelPart != null) {
				modelPartConsumers.add(cvc -> {
					final PoseStack poseStack = new PoseStack();
					poseStack.translate(x, y, z);
					if (flipped) {
						poseStack.mulPose(Axis.YP.rotationDegrees(180));
					}
					modelPart.modelPart.render(poseStack, cvc, light, OverlayTexture.NO_OVERLAY);
				});
			}
		}
	}

	// ──────────────────────────────────────────────────────────────────────────
	// ObjModel — wraps per-group raw meshes loaded from OBJ; transformed and
	// accumulated into rawModel before fromObjModels is called.
	// ──────────────────────────────────────────────────────────────────────────

	public static final class ObjModel {

		private final float minX;
		private final float minY;
		private final float minZ;
		private final float maxX;
		private final float maxY;
		private final float maxZ;
		private final List<RawMesh> rawMeshes;
		final RawModel rawModel = new RawModel();

		private ObjModel(
				List<RawMesh> rawMeshes,
				float minX, float minY, float minZ,
				float maxX, float maxY, float maxZ
		) {
			this.rawMeshes = rawMeshes;
			this.minX = minX;
			this.minY = minY;
			this.minZ = minZ;
			this.maxX = maxX;
			this.maxY = maxY;
			this.maxZ = maxZ;
		}

		/**
		 * @param objString        OBJ file content
		 * @param mtlResolver      maps MTL filename → MTL content
		 * @param textureResolver  maps texture filename → Identifier
		 * @param atlasIndex       atlas JSON Identifier, or null
		 * @param splitModel       true → split by OBJ groups
		 * @param flipTextureV     true → flip V coordinate
		 */
		@MappedMethod
		public static Map<String, ObjModel> loadModel(
				String objString,
				Function<String, String> mtlResolver,
				Function<String, Identifier> textureResolver,
				@Nullable Identifier atlasIndex,
				boolean splitModel,
				boolean flipTextureV
		) {
			if (atlasIndex != null) {
				ATLAS_MANAGER.load(atlasIndex);
			}

			final Map<String, ObjModel> objModels = new HashMap<>();
			ObjModelLoader.loadModel(objString, mtlResolver, textureResolver, ATLAS_MANAGER, splitModel).forEach((key, rawMeshes) -> {
				prepareRawMeshes(rawMeshes, flipTextureV);
				final float[] bounds = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
				rawMeshes.forEach(rawMesh -> rawMesh.vertices.forEach(vertex -> {
					final float x = vertex.position.data.x;
					final float y = vertex.position.data.y;
					final float z = vertex.position.data.z;
					bounds[0] = Math.min(bounds[0], x);
					bounds[1] = Math.min(bounds[1], y);
					bounds[2] = Math.min(bounds[2], z);
					bounds[3] = Math.max(bounds[3], x);
					bounds[4] = Math.max(bounds[4], y);
					bounds[5] = Math.max(bounds[5], z);
				}));
				objModels.put(key, new ObjModel(rawMeshes, bounds[0], bounds[1], bounds[2], bounds[3], bounds[4], bounds[5]));
			});

			return objModels;
		}

		@MappedMethod
		public void addTransformation(ShaderType shaderType, double x, double y, double z, boolean flipped) {
			rawMeshes.forEach(rawMesh -> {
				final RawMesh newRawMesh = new RawMesh(shaderType, rawMesh);
				newRawMesh.applyTranslation((float) x, (float) y, (float) z);
				if (flipped) {
					newRawMesh.applyRotation(new Vector3f(0, 1, 0), 180);
				}
				rawModel.append(newRawMesh);
			});
		}

		@MappedMethod
		public void applyTranslation(double x, double y, double z) {
			rawMeshes.forEach(rawMesh -> rawMesh.applyTranslation((float) x, (float) y, (float) z));
		}

		@MappedMethod
		public void applyRotation(double x, double y, double z) {
			rawMeshes.forEach(rawMesh -> {
				rawMesh.applyRotation(new Vector3f(1, 0, 0), (float) x);
				rawMesh.applyRotation(new Vector3f(0, 1, 0), (float) y);
				rawMesh.applyRotation(new Vector3f(0, 0, 1), (float) z);
			});
		}

		@MappedMethod
		public void applyScale(double x, double y, double z) {
			rawMeshes.forEach(rawMesh -> rawMesh.applyScale((float) x, (float) y, (float) z));
		}

		@MappedMethod
		public void applyMirror(boolean x, boolean y, boolean z) {
			rawMeshes.forEach(rawMesh -> rawMesh.applyMirror(x, y, z, x, y, z));
		}

		@MappedMethod
		public float getMinX() {
			return minX;
		}

		@MappedMethod
		public float getMinY() {
			return minY;
		}

		@MappedMethod
		public float getMinZ() {
			return minZ;
		}

		@MappedMethod
		public float getMaxX() {
			return maxX;
		}

		@MappedMethod
		public float getMaxY() {
			return maxY;
		}

		@MappedMethod
		public float getMaxZ() {
			return maxZ;
		}

		private static void prepareRawMeshes(List<RawMesh> rawMeshes, boolean flipTextureV) {
			rawMeshes.forEach(rawMesh -> {
				rawMesh.applyRotation(new Vector3f(1, 0, 0), 180);
				if (flipTextureV) {
					rawMesh.applyUVMirror(false, true);
				}
			});
		}
	}
}
