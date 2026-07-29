package org.mtr.mapping.render.vertex;

import org.lwjgl.opengl.GL33;
import org.mtr.mapping.render.object.VertexBuffer;

import java.util.HashMap;
import java.util.Map;

public final class VertexAttributeMapping {
	public final Map<VertexAttributeType, VertexAttributeSource> sources;
	public final Map<VertexAttributeType, Integer> pointers = new HashMap<>();
	public final int strideVertex, strideInstance, paddingVertex, paddingInstance;

	private VertexAttributeMapping(Map<VertexAttributeType, VertexAttributeSource> sources) {
		this.sources = sources;
		int vertexStride = 0, instanceStride = 0;
		for (final VertexAttributeType type : VertexAttributeType.values()) {
			switch (sources.get(type)) {
				case VERTEX_BUFFER -> {
					pointers.put(type, vertexStride);
					vertexStride += type.byteSize;
				}
				case INSTANCE_BUFFER -> {
					pointers.put(type, instanceStride);
					instanceStride += type.byteSize;
				}
			}
		}
		paddingVertex = vertexStride % 2;
		paddingInstance = instanceStride % 2;
		strideVertex = vertexStride + paddingVertex;
		strideInstance = instanceStride + paddingInstance;
	}

	public void setupAttributesToVao(VertexBuffer vertexBuffer) {
		for (final VertexAttributeType type : VertexAttributeType.values()) {
			switch (sources.get(type)) {
				case GLOBAL -> type.toggleAttributeArray(false);
				case VERTEX_BUFFER -> {
					type.toggleAttributeArray(true);
					vertexBuffer.bind(GL33.GL_ARRAY_BUFFER);
					type.setupAttributePointer(strideVertex, pointers.get(type));
					type.setAttributeDivisor(0);
				}
			}
		}
	}

	public static class Builder {
		private final HashMap<VertexAttributeType, VertexAttributeSource> sources = new HashMap<>(VertexAttributeType.values().length);

		public Builder() {
			for (final VertexAttributeType type : VertexAttributeType.values()) sources.put(type, VertexAttributeSource.VERTEX_BUFFER);
		}

		public Builder set(VertexAttributeType type, VertexAttributeSource source) {
			sources.put(type, source);
			return this;
		}

		public VertexAttributeMapping build() {
			return new VertexAttributeMapping(sources);
		}
	}
}
