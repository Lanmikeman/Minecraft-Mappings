package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.render.model.RawModel;
import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class OptimizedModel extends DummyClass {

	public enum ShaderType {
		CUTOUT, TRANSLUCENT, CUTOUT_BRIGHT, TRANSLUCENT_BRIGHT, CUTOUT_GLOWING, TRANSLUCENT_GLOWING
	}

	final List<VertexArray> uploadedParts;

	@MappedMethod
	public static OptimizedModel fromMaterialGroups(Collection<MaterialGroup> materialGroups) {
		return new OptimizedModel(Collections.emptyList());
	}

	@MappedMethod
	public static OptimizedModel fromObjModels(Collection<ObjModel> objModels) {
		return new OptimizedModel(Collections.emptyList());
	}

	public OptimizedModel(List<VertexArray> uploadedParts) {
		this.uploadedParts = uploadedParts;
	}

	@MappedMethod
	public OptimizedModel(OptimizedModel... optimizedModels) {
		uploadedParts = new ArrayList<>();
		for (final OptimizedModel optimizedModel : optimizedModels) {
			if (optimizedModel != null) {
				uploadedParts.addAll(optimizedModel.uploadedParts);
			}
		}
	}

	@MappedMethod
	public OptimizedModel() {
		this(Collections.emptyList());
	}

	@MappedMethod
	public OptimizedModel(RawModel rawModel) {
		this(Collections.emptyList());
	}

	@MappedMethod
	public void close() {
	}

	@MappedMethod
	public List<VertexArray> getVertexArrays() {
		return uploadedParts;
	}

	public static final class MaterialGroup {
		@MappedMethod
		public MaterialGroup(ShaderType shaderType, Identifier texture) {
		}

		@MappedMethod
		public void addCube(ModelPartExtension modelPart, double x, double y, double z, boolean flipped, int light) {
		}
	}

	public static final class ObjModel {
		@Nullable
		public RawModel rawModel;

		@MappedMethod
		public static Map<String, ObjModel> loadModel(String content, Function<String, String> materialNameToTexture, Function<String, Identifier> materialNameToIdentifier, Identifier atlasId, boolean flipV, boolean splitModel) {
			return Collections.emptyMap();
		}

		@MappedMethod
		public void addTransformation(ShaderType shaderType, double x, double y, double z, boolean flipped) {
		}

		@MappedMethod
		public void applyTranslation(double x, double y, double z) {
		}

		@MappedMethod
		public void applyRotation(double x, double y, double z) {
		}

		@MappedMethod
		public void applyScale(double x, double y, double z) {
		}

		@MappedMethod
		public void applyMirror(boolean x, boolean y, boolean z) {
		}

		@MappedMethod
		public float getMinX() { return 0; }

		@MappedMethod
		public float getMinY() { return 0; }

		@MappedMethod
		public float getMinZ() { return 0; }

		@MappedMethod
		public float getMaxX() { return 0; }

		@MappedMethod
		public float getMaxY() { return 0; }

		@MappedMethod
		public float getMaxZ() { return 0; }
	}
}
