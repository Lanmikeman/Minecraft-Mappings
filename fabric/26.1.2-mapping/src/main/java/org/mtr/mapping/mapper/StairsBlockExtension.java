package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import java.util.List;

public class StairsBlockExtension extends StairsBlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public StairsBlockExtension(BlockState baseBlockState, BlockSettings blockSettings) {
		super(baseBlockState, BlockHelper.applyPendingBlockId(blockSettings));
	}

	@MappedMethod
	@Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
		properties.add(new DirectionProperty(net.minecraft.world.level.block.StairBlock.FACING));
		properties.add(new EnumProperty<>(net.minecraft.world.level.block.StairBlock.HALF));
		properties.add(new EnumProperty<>(net.minecraft.world.level.block.StairBlock.SHAPE));
		properties.add(new BooleanProperty(net.minecraft.world.level.block.StairBlock.WATERLOGGED));
	}

	@Deprecated
	@Override
	protected void createBlockStateDefinition2(net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.state.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@MappedMethod
	public static StairShape getType(BlockState state) {
		return StairShape.convert(state.data.getValue(net.minecraft.world.level.block.StairBlock.SHAPE));
	}
}
