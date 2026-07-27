package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class EntityModelExtension<T> extends EntityModelAbstractMapping {
	@MappedMethod
	public EntityModelExtension(ModelPart root) {
		super(root);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int light, int overlay, int color) {}
}
