package org.mtr.mapping.render.shader;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.render.batch.MaterialProperties;
import org.mtr.mapping.tool.DummyClass;

/** Temporary 26.1.2 stub — full GL/shader pipeline pending port. */
public class ShaderManager extends DummyClass {
	@MappedMethod public void reload() {}
	@MappedMethod public boolean isReady() { return false; }
	@MappedMethod public void setupShaderBatch(MaterialProperties materialProperties) {}
	@MappedMethod public void finish() {}
}
