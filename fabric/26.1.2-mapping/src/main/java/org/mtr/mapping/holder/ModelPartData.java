package org.mtr.mapping.holder;

import org.mtr.mapping.tool.HolderBase;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation","unused"})
public final class ModelPartData extends HolderBase<net.minecraft.client.model.geom.builders.PartDefinition> {
	public ModelPartData(net.minecraft.client.model.geom.builders.PartDefinition data) { super(data); }
}
