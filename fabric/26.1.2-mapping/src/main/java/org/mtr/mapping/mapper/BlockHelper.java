package org.mtr.mapping.mapper;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyInterface;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

public interface BlockHelper extends DummyInterface {

	@MappedMethod
	default void addBlockProperties(List<HolderBase<?>> properties) {
	}

	@Deprecated
	default void appendPropertiesHelper(StateDefinition.Builder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.state.BlockState> builder) {
		final List<HolderBase<?>> properties = new ArrayList<>();
		addBlockProperties(properties);
		if (!properties.isEmpty()) {
			final Property<?>[] newProperties = new Property[properties.size()];
			for (int i = 0; i < properties.size(); i++) {
				final Object data = properties.get(i).data;
				if (data instanceof Property) {
					newProperties[i] = (Property<?>) data;
				}
			}
			builder.add(newProperties);
		}
	}

	@MappedMethod
	default void addTooltips(ItemStack stack, @Nullable BlockView world, List<MutableText> tooltip, TooltipContext options) {
	}

	@MappedMethod
	static BlockSettings setLuminance(BlockSettings blockSettings, ToIntFunction<BlockState> luminanceFunction) {
		return new BlockSettings(blockSettings.data.lightLevel(state -> luminanceFunction.applyAsInt(new BlockState(state))));
	}

	@MappedMethod
	static VoxelShape union(VoxelShape... shapes) {
		net.minecraft.world.phys.shapes.VoxelShape result = net.minecraft.world.phys.shapes.Shapes.empty();
		for (final VoxelShape shape : shapes) {
			if (shape != null && shape.data != null) {
				result = net.minecraft.world.phys.shapes.Shapes.or(result, shape.data);
			}
		}
		return new VoxelShape(result);
	}

	@MappedMethod
	static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.data.scheduleTick(pos.data, block.data, ticks);
	}

	@MappedMethod
	static VoxelShape shapeUnion(VoxelShape voxelShape, VoxelShape... voxelShapes) {
		VoxelShape result = voxelShape;
		for (final VoxelShape additionalShape : voxelShapes) {
			result = VoxelShapes.union(result, additionalShape);
		}
		return result;
	}

	@MappedMethod
	static BlockSettings createBlockSettings(boolean blockPiston, boolean forceSolid) {
		net.minecraft.world.level.block.state.BlockBehaviour.Properties settings = net.minecraft.world.level.block.state.BlockBehaviour.Properties.of();
		if (blockPiston) {
			settings = settings.pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK);
		}
		if (forceSolid) {
			settings = settings.forceSolidOn();
		}
		return new BlockSettings(settings);
	}

	@MappedMethod
	static BlockSettings createBlockSettings(boolean blockPiston, boolean forceSolid, java.util.function.ToIntFunction<BlockState> luminanceFunction) {
		return setLuminance(createBlockSettings(blockPiston, forceSolid), luminanceFunction);
	}

	/** Apply pending registry id from {@link org.mtr.mapping.registry.Registry} (MC 26.1+). */
	@MappedMethod
	static BlockSettings applyPendingBlockId(BlockSettings blockSettings) {
		final Identifier pending = org.mtr.mapping.registry.Registry.peekPendingBlockId();
		if (pending != null) {
			return new BlockSettings(blockSettings.data.setId(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, pending.data)));
		}
		return blockSettings;
	}
}
