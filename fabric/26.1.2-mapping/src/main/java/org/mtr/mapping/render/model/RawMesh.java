package org.mtr.mapping.render.model;

import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.vertex.Vertex;

import java.util.ArrayList;
import java.util.List;

public final class RawMesh {
	public final MaterialProperties materialProperties;
	public final List<Vertex> vertices = new ArrayList<>();
	public final List<Face> faces = new ArrayList<>();
	public RawMesh(MaterialProperties materialProperties) { this.materialProperties = materialProperties; }
	public void addVertex(Vertex vertex) { vertices.add(vertex); }
	public void generateNormals() {}
	public void upload() {}
}
