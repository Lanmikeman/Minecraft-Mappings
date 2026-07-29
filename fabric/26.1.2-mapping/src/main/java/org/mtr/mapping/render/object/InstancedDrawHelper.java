package org.mtr.mapping.render.object;

import org.mtr.mapping.render.vertex.VertexAttributeMapping;
import org.mtr.mapping.render.vertex.VertexAttributeType;

import java.nio.ByteBuffer;

/** Legacy GL instancing helper — not used on the 26.1 RenderPass path. */
public final class InstancedDrawHelper {

	public static boolean setupInstanceAttributes(VertexArray vertexArray, VertexBuffer instanceBuffer, VertexAttributeMapping mapping, VertexAttributeType... types) {
		return false;
	}

	public static void setupInstanceAttributePointers(VertexBuffer instanceBuffer, VertexAttributeMapping mapping, int offset, VertexAttributeType... types) {
	}

	public static void uploadInstances(VertexBuffer instanceBuffer, ByteBuffer buffer, int size) {
	}

	public static void drawElementsInstanced(VertexArray vertexArray, int instanceCount) {
		throw new UnsupportedOperationException("Instanced draw is not available on the 26.1 GpuDevice path");
	}

	private InstancedDrawHelper() {
	}
}
