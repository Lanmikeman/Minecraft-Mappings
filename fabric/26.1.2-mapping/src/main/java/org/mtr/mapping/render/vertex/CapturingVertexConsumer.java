package org.mtr.mapping.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.model.RawModel;

public final class CapturingVertexConsumer implements VertexConsumer {
	public final RawModel rawModel = new RawModel();
	private RawMesh mesh;
	private final Vertex current = new Vertex();

	public void startSource(MaterialProperties materialProperties) {
		mesh = rawModel.getRawMeshMaterial(materialProperties);
	}

	@Override public VertexConsumer addVertex(float x, float y, float z) {
		current.position = new org.mtr.mapping.holder.Vector3f(new org.joml.Vector3f(x,y,z));
		if (mesh != null) {
			final Vertex v = new Vertex();
			v.position = current.position; v.normal = current.normal; v.u = current.u; v.v = current.v; v.color = current.color; v.light = current.light;
			mesh.addVertex(v);
		}
		return this;
	}
	@Override public VertexConsumer setColor(int color) { current.color = color; return this; }
	@Override public VertexConsumer setColor(int r, int g, int b, int a) { current.color = (a<<24)|(r<<16)|(g<<8)|b; return this; }
	@Override public VertexConsumer setUv(float u, float v) { current.u=u; current.v=v; return this; }
	@Override public VertexConsumer setUv1(int u, int v) { return this; }
	@Override public VertexConsumer setUv2(int u, int v) { current.light=(v<<16)|u; return this; }
	@Override public VertexConsumer setNormal(float x, float y, float z) { current.normal = new org.mtr.mapping.holder.Vector3f(new org.joml.Vector3f(x,y,z)); return this; }
	@Override public VertexConsumer setLineWidth(float width) { return this; }
}
