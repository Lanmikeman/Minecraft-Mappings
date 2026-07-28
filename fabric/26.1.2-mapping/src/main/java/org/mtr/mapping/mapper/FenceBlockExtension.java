package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class FenceBlockExtension extends FenceBlockAbstractMapping {
	@MappedMethod
	public FenceBlockExtension(BlockSettings blockSettings) {
		super(BlockHelper.applyPendingBlockId(blockSettings));
	}
}
