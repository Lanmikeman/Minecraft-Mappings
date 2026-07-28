package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class StairsBlockExtension extends StairsBlockAbstractMapping {
	@MappedMethod
	public StairsBlockExtension(BlockState baseBlockState, BlockSettings blockSettings) {
		super(baseBlockState, BlockHelper.applyPendingBlockId(blockSettings));
	}
}
