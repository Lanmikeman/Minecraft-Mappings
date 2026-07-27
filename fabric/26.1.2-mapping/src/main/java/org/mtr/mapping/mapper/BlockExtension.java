package org.mtr.mapping.mapper;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class BlockExtension extends BlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public BlockExtension(BlockSettings blockSettings) {
		super(blockSettings);
	}

	@MappedMethod
	public void onBreak2(Level world, BlockPos pos, BlockState state, Player player) {
		super.onBreak(world.data, pos.data, state.data, player.data);
	}

	@Deprecated
	@Override
	public final net.minecraft.world.level.block.state.BlockState onBreak(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.entity.player.Player player) {
		onBreak2(new Level(world), new BlockPos(pos), new BlockState(state), new Player(player));
		return state;
	}

	@Nonnull
	@MappedMethod
	public ItemStack getPickStack2(BlockGetter world, BlockPos pos, BlockState state) {
		return world.data instanceof LevelReader ? new ItemStack(super.getPickStack((LevelReader) world.data, pos.data, state.data)) : ItemStack.getEmptyMapped();
	}

	@Nonnull
	@Deprecated
	@Override
	public final net.minecraft.world.item.ItemStack getPickStack(LevelReader world, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
		return getPickStack2(new BlockGetter(world), new BlockPos(pos), new BlockState(state)).data;
	}

	@Deprecated
	@Override
	protected final void appendProperties(StateDefinition.Builder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.state.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@Deprecated
	@Override
	public final void appendTooltip(net.minecraft.world.item.ItemStack stack, @Nullable net.minecraft.world.level.BlockGetter world, List<Component> tooltip, net.minecraft.world.item.Item$TooltipContext options) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new BlockGetter(world), tooltip, new TooltipContext(options));
	}

	@MappedMethod
	public static void scheduleBlockTick(Level world, BlockPos pos, Block block, int ticks) {
		world.data.scheduleBlockTick(pos.data, block.data, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledBlockTick(Level world, BlockPos pos, Block block) {
		return world.data.getBlockTickScheduler().isQueued(pos.data, block.data);
	}

	@MappedMethod
	public static void scheduleFluidTick(Level world, BlockPos pos, Fluid fluid, int ticks) {
		world.data.scheduleFluidTick(pos.data, fluid.data, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledFluidTick(Level world, BlockPos pos, Fluid fluid) {
		return world.data.getFluidTickScheduler().isQueued(pos.data, fluid.data);
	}
}
