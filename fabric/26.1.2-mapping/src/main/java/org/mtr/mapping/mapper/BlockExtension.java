package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockAbstractMapping;
import org.mtr.mapping.holder.BlockSettings;

public abstract class BlockExtension extends BlockAbstractMapping {
	@MappedMethod
	public BlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}
}
