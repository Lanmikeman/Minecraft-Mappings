package org.mtr.mapping.render.model;

import org.mtr.mapping.holder.Matrix4f;
import org.mtr.mapping.holder.Vector3f;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.render.object.VertexArray;
import org.mtr.mapping.render.vertex.VertexAttributeMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public final class RawModel {

	private final Map<MaterialProperties, RawMesh> materialPropertiesToRawMesh = new HashMap<>();

	public List<VertexArray> upload(VertexAttributeMapping mapping) {
		final List<VertexArray> vertexArrays = new ArrayList<>();
		iterateRawMeshList(rawMesh -> {
			if (rawMesh.hasFaces()) {
				vertexArrays.add(VertexArray.upload(rawMesh));
			}
		});
		return vertexArrays;
	}

	public void append(RawMesh nextMesh) {
		if (materialPropertiesToRawMesh.containsKey(nextMesh.materialProperties)) {
			materialPropertiesToRawMesh.get(nextMesh.materialProperties).append(nextMesh);
		} else {
			final RawMesh newMesh = new RawMesh(nextMesh.materialProperties);
			materialPropertiesToRawMesh.put(nextMesh.materialProperties, newMesh);
			newMesh.append(nextMesh);
		}
	}

	public RawMesh getRawMesh(MaterialProperties materialProperties) {
		return materialPropertiesToRawMesh.computeIfAbsent(materialProperties, RawMesh::new);
	}

	/** @deprecated kept for existing call sites; use {@link #getRawMesh(MaterialProperties)} */
	@Deprecated
	public RawMesh getRawMeshMaterial(MaterialProperties materialProperties) {
		return getRawMesh(materialProperties);
	}

	public void iterateRawMeshList(Consumer<RawMesh> consumer) {
		materialPropertiesToRawMesh.values().forEach(consumer);
	}

	public void append(Collection<RawMesh> meshes) {
		meshes.forEach(this::append);
	}

	public void append(RawModel nextModel) {
		append(nextModel.materialPropertiesToRawMesh.values());
	}

	public List<RawMesh> toList() {
		return new ArrayList<>(materialPropertiesToRawMesh.values());
	}

	public void applyMatrix(Matrix4f matrix4f) {
		iterateRawMeshList(rawMesh -> rawMesh.applyMatrix(matrix4f));
	}

	public void applyTranslation(float x, float y, float z) {
		iterateRawMeshList(rawMesh -> rawMesh.applyTranslation(x, y, z));
	}

	public void applyRotation(Vector3f axis, float angle) {
		iterateRawMeshList(rawMesh -> rawMesh.applyRotation(axis, angle));
	}

	public void applyScale(float x, float y, float z) {
		iterateRawMeshList(rawMesh -> rawMesh.applyScale(x, y, z));
	}

	public void applyMirror(boolean vx, boolean vy, boolean vz, boolean nx, boolean ny, boolean nz) {
		iterateRawMeshList(rawMesh -> rawMesh.applyMirror(vx, vy, vz, nx, ny, nz));
	}

	public void applyUVMirror(boolean u, boolean v) {
		iterateRawMeshList(rawMesh -> rawMesh.applyUVMirror(u, v));
	}

	public void generateNormals() {
		iterateRawMeshList(RawMesh::generateNormals);
	}

	public void distinct() {
		iterateRawMeshList(RawMesh::distinct);
	}

	public void triangulate() {
		iterateRawMeshList(RawMesh::triangulate);
	}
}
