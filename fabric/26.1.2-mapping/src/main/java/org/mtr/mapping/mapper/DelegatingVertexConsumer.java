package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Forwards vertex writes without implementing Sodium's {@code VertexBufferWriter}.
 * Forces ModelPart through vanilla {@code addVertex} so Iris can fill extended attributes
 * (iris_Entity / midTexCoord / tangent) instead of Sodium's EntityVertex bulk copy.
 */
final class DelegatingVertexConsumer implements VertexConsumer {

	private final VertexConsumer delegate;

	DelegatingVertexConsumer(VertexConsumer delegate) {
		this.delegate = delegate;
	}

	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		return delegate.addVertex(x, y, z);
	}

	@Override
	public VertexConsumer setColor(int r, int g, int b, int a) {
		return delegate.setColor(r, g, b, a);
	}

	@Override
	public VertexConsumer setColor(int color) {
		return delegate.setColor(color);
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		return delegate.setUv(u, v);
	}

	@Override
	public VertexConsumer setUv1(int u, int v) {
		return delegate.setUv1(u, v);
	}

	@Override
	public VertexConsumer setUv2(int u, int v) {
		return delegate.setUv2(u, v);
	}

	@Override
	public VertexConsumer setNormal(float x, float y, float z) {
		return delegate.setNormal(x, y, z);
	}

	@Override
	public VertexConsumer setLineWidth(float width) {
		return delegate.setLineWidth(width);
	}
}
