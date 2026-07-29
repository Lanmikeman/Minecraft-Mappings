package org.mtr.mapping.render.batch;

import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.shader.ShaderManager;
import org.mtr.mapping.render.vertex.VertexAttributeState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class BatchManager {
	private final Map<MaterialProperties, List<ShaderManager.RenderCall>> opaqueBatches = new HashMap<>();
	private final Map<MaterialProperties, List<ShaderManager.RenderCall>> cutoutBatches = new HashMap<>();
	private final Map<MaterialProperties, List<ShaderManager.RenderCall>> translucentBatches = new HashMap<>();

	public void queue(VertexArray vertexArray, VertexAttributeState vertexAttributeState) {
		final MaterialProperties materialProperties = vertexArray.materialProperties;
		(materialProperties.translucent ? translucentBatches : materialProperties.cutoutHack ? cutoutBatches : opaqueBatches)
				.computeIfAbsent(materialProperties, key -> new ArrayList<>())
				.add(new ShaderManager.RenderCall(vertexArray, vertexAttributeState));
	}

	public void queue(List<VertexArray> vertexArrays, VertexAttributeState vertexAttributeState) {
		vertexArrays.forEach(vertexArray -> queue(vertexArray, vertexAttributeState));
	}

	public boolean drawOpaque(ShaderManager shaderManager) {
		if (!shaderManager.drawBatches(opaqueBatches)) {
			return false;
		}
		return shaderManager.drawBatches(cutoutBatches);
	}

	public void drawTranslucent(ShaderManager shaderManager) {
		shaderManager.drawBatches(translucentBatches);
	}

	public void discardTranslucent() {
		translucentBatches.clear();
	}

	public void drawAll(ShaderManager shaderManager, boolean renderTranslucent) {
		drawOpaque(shaderManager);
		if (renderTranslucent) {
			drawTranslucent(shaderManager);
		} else {
			discardTranslucent();
		}
	}
}
