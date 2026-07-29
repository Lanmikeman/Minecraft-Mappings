package org.mtr.mapping.render.vertex;

import org.mtr.mapping.holder.Vector3f;

import java.util.Objects;

public final class Vertex {

	public Vector3f position = new Vector3f(new org.joml.Vector3f());
	public Vector3f normal = new Vector3f(new org.joml.Vector3f());
	public float u, v;
	public int color = 0xFFFFFFFF;
	public int light;

	public Vertex() {
	}

	public Vertex(Vertex vertex) {
		position = new Vector3f(new org.joml.Vector3f(vertex.position.data));
		normal = new Vector3f(new org.joml.Vector3f(vertex.normal.data));
		u = vertex.u;
		v = vertex.v;
		color = vertex.color;
		light = vertex.light;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Vertex vertex = (Vertex) o;
		return Float.compare(vertex.u, u) == 0
				&& Float.compare(vertex.v, v) == 0
				&& position.data.equals(vertex.position.data)
				&& normal.data.equals(vertex.normal.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(position.data, normal.data, u, v);
	}
}
