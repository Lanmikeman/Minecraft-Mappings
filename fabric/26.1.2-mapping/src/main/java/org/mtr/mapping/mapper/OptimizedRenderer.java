package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.render.batch.BatchManager;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.vertex.VertexAttributeState;
import org.mtr.mapping.tool.DummyClass;

public final class OptimizedRenderer extends DummyClass {
	private final BatchManager batchManager = new BatchManager();
	private final ShaderManager shaderManager = new ShaderManager();

	@MappedMethod public OptimizedRenderer() {}
	@MappedMethod public void beginReload() { shaderManager.reload(); }
	@MappedMethod public void finishReload() {}
	@MappedMethod public void queue(OptimizedModel model, VertexAttributeState state) {
		batchManager.queue(model.getVertexArrays(), state);
	}
	@MappedMethod public void drawAll() { batchManager.drawAll(shaderManager); }

	@MappedMethod
	public static boolean hasOptimizedRendering() {
		return false; // TODO GL pipeline
	}

}
