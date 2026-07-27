package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.RawModel;
import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.tool.DummyClass;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class OptimizedModel extends DummyClass {
	public enum ShaderType {
		CUTOUT, TRANSLUCENT, CUTOUT_BRIGHT, TRANSLUCENT_BRIGHT, CUTOUT_GLOWING, TRANSLUCENT_GLOWING
	}

	private final List<VertexArray> vertexArrays;

	@MappedMethod
	public OptimizedModel() {
		this.vertexArrays = Collections.emptyList();
	}

	@MappedMethod
	public OptimizedModel(RawModel rawModel) {
		rawModel.generateNormals();
		rawModel.upload();
		this.vertexArrays = Collections.emptyList(); // TODO upload to VAO
	}

	@MappedMethod
	public List<VertexArray> getVertexArrays() { return vertexArrays; }

	@MappedMethod
	public static OptimizedModel fromObj(org.mtr.mapping.holder.Identifier id) {
		return new OptimizedModel();
	}
}
