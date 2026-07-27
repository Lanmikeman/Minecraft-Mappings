package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

import java.util.List;
import java.util.function.Consumer;

public final class BlockHelper extends DummyClass {

	@MappedMethod
	public static void addBlockTooltip(List<org.mtr.mapping.holder.Text> tooltipList, Consumer<List<MutableText>> consumer) {
		final java.util.ArrayList<MutableText> newTooltipList = new java.util.ArrayList<>();
		consumer.accept(newTooltipList);
		newTooltipList.forEach(mutableText -> tooltipList.add(new Text(mutableText.data)));
	}

	@MappedMethod
	public static BlockSettings setLuminance(BlockSettings blockSettings, java.util.function.ToIntFunction<BlockState> luminanceFunction) {
		return new BlockSettings(blockSettings.data.lightLevel(state -> luminanceFunction.applyAsInt(new BlockState(state))));
	}

	@MappedMethod
	public static VoxelShape union(VoxelShape... shapes) {
		net.minecraft.world.phys.shapes.VoxelShape result = net.minecraft.world.phys.shapes.Shapes.empty();
		for (final VoxelShape shape : shapes) {
			if (shape != null && shape.data != null) {
				result = net.minecraft.world.phys.shapes.Shapes.or(result, shape.data);
			}
		}
		return new VoxelShape(result);
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.data.scheduleTick(pos.data, block.data, ticks);
	}
}
