package org.mtr.mapping.holder;

import org.mtr.mapping.tool.HolderBase;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation","unused"})
public final class ModelData extends HolderBase<net.minecraft.client.model.geom.builders.MeshDefinition> {
	public ModelData(net.minecraft.client.model.geom.builders.MeshDefinition data) { super(data); }
	public ModelData() { super(new net.minecraft.client.model.geom.builders.MeshDefinition()); }
}
