package org.mtr.mapping.render.vertex;

public final class VertexAttributeMapping {
	public final VertexAttributeType type;
	public final VertexAttributeSource source;
	public VertexAttributeMapping(VertexAttributeType type, VertexAttributeSource source) {
		this.type = type; this.source = source;
	}
}
