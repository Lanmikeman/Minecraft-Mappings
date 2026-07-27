package org.mtr.mapping.render.model;

import org.mtr.mapping.render.batch.MaterialProperties;

import java.util.HashMap;
import java.util.Map;

public final class RawModel {
	private final Map<MaterialProperties, RawMesh> meshes = new HashMap<>();
	public RawMesh getRawMeshMaterial(MaterialProperties materialProperties) {
		return meshes.computeIfAbsent(materialProperties, RawMesh::new);
	}
	public Map<MaterialProperties, RawMesh> getRawMeshes() { return meshes; }
	public void generateNormals() { meshes.values().forEach(RawMesh::generateNormals); }
	public void upload() { meshes.values().forEach(RawMesh::upload); }
}
