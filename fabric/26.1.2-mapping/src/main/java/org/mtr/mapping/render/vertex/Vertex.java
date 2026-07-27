package org.mtr.mapping.render.vertex;

import org.mtr.mapping.holder.Vector3f;

public final class Vertex {
	public Vector3f position = new Vector3f(new org.joml.Vector3f());
	public Vector3f normal = new Vector3f(new org.joml.Vector3f());
	public float u, v;
	public int color = 0xFFFFFFFF;
	public int light;
}
