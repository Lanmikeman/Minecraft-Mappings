package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class ModelExtension extends ModelAbstractMapping {
	@MappedMethod
	public ModelExtension(ModelPart root) {
		super(root, id -> null);
	}
}
