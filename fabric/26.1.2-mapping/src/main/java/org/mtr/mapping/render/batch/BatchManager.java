package org.mtr.mapping.render.batch;

import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BatchManager {
	private final Map<MaterialProperties, List<RenderCall>> opaque = new HashMap<>();
	private final Map<MaterialProperties, List<RenderCall>> translucent = new HashMap<>();

	public void queue(VertexArray vertexArray, VertexAttributeState state) {
		(vertexArray.materialProperties != null && vertexArray.materialProperties.translucent ? translucent : opaque)
			.computeIfAbsent(vertexArray.materialProperties, k -> new ArrayList<>())
			.add(new RenderCall(vertexArray, state));
	}

	public void queue(List<VertexArray> arrays, VertexAttributeState state) {
		arrays.forEach(a -> queue(a, state));
	}

	public void drawAll(ShaderManager shaderManager) {
		draw(opaque, shaderManager);
		draw(translucent, shaderManager);
		opaque.clear(); translucent.clear();
	}

	private void draw(Map<MaterialProperties, List<RenderCall>> batches, ShaderManager shaderManager) {
		batches.forEach((mat, calls) -> {
			shaderManager.setupShaderBatch(mat);
			mat.setupGlState();
			for (RenderCall call : calls) {
				call.state.apply();
				call.vertexArray.bind();
				call.vertexArray.draw();
			}
			mat.cleanupGlState();
			shaderManager.finish();
		});
	}

	private record RenderCall(VertexArray vertexArray, VertexAttributeState state) {}
}
