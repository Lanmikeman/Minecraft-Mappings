package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public class ItemExtension extends ItemAbstractMapping {
	@MappedMethod
	public ItemExtension(ItemSettings itemSettings) {
		super(itemSettings);
	}

	@MappedMethod
	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}
}
