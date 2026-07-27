package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.BlockEntity;
import org.mtr.mapping.holder.BlockPos;
import org.mtr.mapping.holder.BlockState;

public interface BlockWithEntity {
	@MappedMethod
	BlockEntity create(BlockPos blockPos, BlockState blockState);
}
