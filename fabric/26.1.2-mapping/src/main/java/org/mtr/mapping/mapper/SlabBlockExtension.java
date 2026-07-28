package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Yarn-facing slab hooks. SlabBlockAbstractMapping already exposes Mojmap *2 methods;
 * this layer renames/adapts them to the Yarn names used by MTR.
 */
public class SlabBlockExtension extends SlabBlockAbstractMapping implements BlockHelper {

	/** Alias used by MTR; same field as {@link net.minecraft.world.level.block.SlabBlock#TYPE}. */
	public static final EnumProperty<net.minecraft.world.level.block.state.properties.SlabType> TYPE = getTypeMapped();

	/** Waterlogged flag shared by slab-like MTR blocks. */
	public static final BooleanProperty WATERLOGGED = new BooleanProperty(net.minecraft.world.level.block.SlabBlock.WATERLOGGED);

	@MappedMethod
	public SlabBlockExtension(BlockSettings blockSettings) {
		super(BlockHelper.applyPendingBlockId(blockSettings));
		registerDefaultState(defaultBlockState()
				.setValue(net.minecraft.world.level.block.SlabBlock.TYPE, net.minecraft.world.level.block.state.properties.SlabType.BOTTOM)
				.setValue(net.minecraft.world.level.block.SlabBlock.WATERLOGGED, false));
	}

	@MappedMethod
	@Override
	public void addBlockProperties(List<HolderBase<?>> properties) {
		properties.add(TYPE);
		properties.add(WATERLOGGED);
	}

	@Nonnull
	@MappedMethod
	public BlockState getDefaultState2() {
		return new BlockState(defaultBlockState());
	}

	@MappedMethod
	public void setDefaultState2(BlockState state) {
		registerDefaultState(state.data);
	}

	@MappedMethod
	public static SlabType getType(BlockState state) {
		return SlabType.convert(state.data.getValue(net.minecraft.world.level.block.SlabBlock.TYPE));
	}

	@Deprecated
	@Override
	protected void createBlockStateDefinition2(net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block, net.minecraft.world.level.block.state.BlockState> builder) {
		appendPropertiesHelper(builder);
	}

	@Nonnull
	@MappedMethod
	public String getTranslationKey2() {
		return getDescriptionId2();
	}

	@Nonnull
	@MappedMethod
	public BlockState getPlacementState2(ItemPlacementContext context) {
		return super.getStateForPlacement2(context);
	}

	@Deprecated
	@Override
	public BlockState getStateForPlacement2(ItemPlacementContext context) {
		final BlockState state = getPlacementState2(context);
		return state == null ? null : state;
	}

	@Nonnull
	@MappedMethod
	public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return state;
	}

	@Deprecated
	@Override
	protected BlockState updateShape2(
			BlockState state,
			net.minecraft.world.level.LevelReader level,
			net.minecraft.world.level.ScheduledTickAccess ticks,
			BlockPos pos,
			Direction directionToNeighbour,
			BlockPos neighbourPos,
			BlockState neighbourState,
			Random random
	) {
		final WorldAccess worldAccess;
		if (level instanceof net.minecraft.world.level.LevelAccessor accessor) {
			worldAccess = new WorldAccess(accessor);
		} else {
			return state;
		}
		return getStateForNeighborUpdate2(state, directionToNeighbour, neighbourState, worldAccess, pos, neighbourPos);
	}

	@Nonnull
	@MappedMethod
	public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return super.getShape2(state, world, pos, context);
	}

	@Deprecated
	@Override
	protected VoxelShape getShape2(BlockState state, BlockView level, BlockPos pos, ShapeContext context) {
		return getOutlineShape2(state, level, pos, context);
	}

	@MappedMethod
	public boolean isSideInvisible2(BlockState state, BlockState neighborState, Direction direction) {
		return false;
	}

	@Deprecated
	@Override
	protected boolean skipRendering(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.block.state.BlockState neighborState, net.minecraft.core.Direction direction) {
		return isSideInvisible2(new BlockState(state), new BlockState(neighborState), Direction.convert(direction));
	}

	@MappedMethod
	public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
		return 1.0f;
	}

	@MappedMethod
	public boolean isTranslucent2(BlockState state, BlockView world, BlockPos pos) {
		return false;
	}
}
