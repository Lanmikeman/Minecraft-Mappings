package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class DoorBlockExtension extends DoorBlockAbstractMapping {
	@MappedMethod
	public DoorBlockExtension(net.minecraft.world.level.block.state.properties.BlockSetType type, BlockSettings blockSettings) {
		super(type, BlockHelper.applyPendingBlockId(blockSettings));
	}
}
