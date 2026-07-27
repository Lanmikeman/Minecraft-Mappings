package org.mtr.mapping.render.model;

import org.mtr.mapping.render.batch.MaterialProperties;

public final class Mesh {
	public final MaterialProperties materialProperties;
	public Mesh(MaterialProperties materialProperties) { this.materialProperties = materialProperties; }
	public void draw() {}
}
