package org.mtr.mapping.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.model.RawMesh;
import org.mtr.mapping.render.model.RawModel;

/**
 * Captures vanilla VertexConsumer calls into a CPU-side RawModel.
 * Used by OptimizedModel.fromMaterialGroups to build soft-path meshes.
 *
 * In MC 26.1, addVertex(x,y,z) starts a new vertex; subsequent set* calls fill it.
 * The vertex is committed when the next addVertex is called, or when flushCurrent() is invoked.
 */
public final class CapturingVertexConsumer implements VertexConsumer {

	public final RawModel rawModel = new RawModel();

	private RawMesh mesh;
	private boolean pendingVertex = false;
	private float px, py, pz;
	private float nx, ny, nz;
	private float tu, tv;
	private int vcolor = 0xFFFFFFFF;
	private int vlight;

	public void beginStage(MaterialProperties materialProperties) {
		flushCurrent();
		mesh = rawModel.getRawMesh(materialProperties);
	}

	public void flushCurrent() {
		if (pendingVertex && mesh != null) {
			final Vertex v = new Vertex();
			v.position = new Vector3f(px, py, pz);
			v.normal = new Vector3f(nx, ny, nz);
			v.u = tu;
			v.v = tv;
			v.color = vcolor;
			v.light = vlight;
			mesh.addVertex(v);
			pendingVertex = false;
		}
	}

	@Override
	public VertexConsumer addVertex(float x, float y, float z) {
		flushCurrent();
		px = x;
		py = y;
		pz = z;
		nx = 0;
		ny = 1;
		nz = 0;
		tu = 0;
		tv = 0;
		vcolor = 0xFFFFFFFF;
		vlight = 0;
		pendingVertex = true;
		return this;
	}

	@Override
	public VertexConsumer setColor(int r, int g, int b, int a) {
		vcolor = (r << 24) | (g << 16) | (b << 8) | a;
		return this;
	}

	@Override
	public VertexConsumer setColor(int color) {
		vcolor = color;
		return this;
	}

	@Override
	public VertexConsumer setUv(float u, float v) {
		tu = u;
		tv = v;
		return this;
	}

	@Override
	public VertexConsumer setUv1(int u, int v) {
		return this;
	}

	@Override
	public VertexConsumer setUv2(int u, int v) {
		vlight = (v << 16) | u;
		return this;
	}

	@Override
	public VertexConsumer setNormal(float x, float y, float z) {
		nx = x;
		ny = y;
		nz = z;
		return this;
	}

	@Override
	public VertexConsumer setLineWidth(float width) {
		return this;
	}
}
