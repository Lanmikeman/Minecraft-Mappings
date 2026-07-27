package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ModelPart;
import org.mtr.mapping.tool.DummyClass;

public class ModelPartExtension extends DummyClass {
	public final ModelPart modelPart;

	@MappedMethod
	public ModelPartExtension(ModelPart modelPart) {
		this.modelPart = modelPart;
	}

	@MappedMethod
	public void setPivot(float x, float y, float z) {
		modelPart.data.setPos(x, y, z);
	}

	@MappedMethod
	public void setRotation(float x, float y, float z) {
		modelPart.data.xRot = x;
		modelPart.data.yRot = y;
		modelPart.data.zRot = z;
	}
}
