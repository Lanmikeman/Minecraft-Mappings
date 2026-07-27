package org.mtr.mapping.mapper;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.holder.EntityModelAbstractMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class EntityModelExtension<T extends EntityAbstractMapping> extends EntityModelAbstractMapping implements ModelHelper {

	private final int textureWidth;
	private final int textureHeight;
	private final MeshDefinition modelData = new MeshDefinition();
	private final PartDefinition modelPartData = modelData.getRoot();
	private final List<ModelPartExtension> modelPartExtensions = new ArrayList<>();

	private static ModelPart emptyRoot() {
		return new ModelPart(List.of(), Map.of());
	}

	@MappedMethod
	public EntityModelExtension(int textureWidth, int textureHeight) {
		super(new org.mtr.mapping.holder.ModelPart(emptyRoot()));
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	@MappedMethod
	public final ModelPartExtension createModelPart() {
		final ModelPartExtension modelPartExtension = new ModelPartExtension(modelPartData);
		modelPartExtensions.add(modelPartExtension);
		return modelPartExtension;
	}

	@MappedMethod
	public final void buildModel() {
		final ModelPart modelPart = LayerDefinition.create(modelData, textureWidth, textureHeight).bakeRoot();
		modelPartExtensions.forEach(modelPartExtension -> modelPartExtension.setModelPart(modelPart));
	}

	@MappedMethod
	@Override
	public void render(GraphicsHolder graphicsHolder, int light, int overlay, float red, float green, float blue, float alpha) {
	}
}
