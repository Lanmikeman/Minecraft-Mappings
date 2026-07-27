package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class SlabBlockExtension extends SlabBlockAbstractMapping {

	/** Alias used by MTR; same field as {@link net.minecraft.world.level.block.SlabBlock#TYPE}. */
	public static final EnumProperty<net.minecraft.world.level.block.state.properties.SlabType> TYPE = getTypeMapped();

	@MappedMethod
	public SlabBlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}

	@MappedMethod
	public static SlabType getType(BlockState state) {
		return SlabType.convert(state.data.getValue(net.minecraft.world.level.block.SlabBlock.TYPE));
	}
}
