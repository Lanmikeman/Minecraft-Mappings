package org.mtr.mapping.render.object;

import org.mtr.mapping.render.batch.MaterialProperties;

public final class VertexArray implements AutoCloseable {
	public MaterialProperties materialProperties;
	public void bind() {}
	public void draw() {}
	@Override public void close() {}
}
