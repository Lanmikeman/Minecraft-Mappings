package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Yarn-facing block hooks for MTR. Mojmap MC methods are bridged to the *2 names used by the mod.
 */
public class BlockExtension extends BlockAbstractMapping implements BlockHelper {

	@MappedMethod
	public BlockExtension(BlockSettings blockSettings) {
		super(BlockHelper.applyPendingBlockId(blockSettings));
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

	// --- placement / break (rename bridges over generated *2) ---

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

	@MappedMethod
	public void onPlaced2(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
		super.setPlacedBy2(world, pos, state, placer, itemStack);
	}

	@Deprecated
	@Override
	public void setPlacedBy2(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		onPlaced2(world, pos, state, placer, itemStack);
	}

	@MappedMethod
	public void onBreak2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		super.playerWillDestroy2(world, pos, state, player);
	}

	@Deprecated
	@Override
	public BlockState playerWillDestroy2(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		onBreak2(world, pos, state, player);
		return state;
	}

	// --- neighbor update (26.1 signature differs from Yarn) ---

	@Nonnull
	@MappedMethod
	public BlockState getStateForNeighborUpdate2(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return state;
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.level.block.state.BlockState updateShape(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.LevelReader level,
			net.minecraft.world.level.ScheduledTickAccess ticks,
			net.minecraft.core.BlockPos pos,
			net.minecraft.core.Direction direction,
			net.minecraft.core.BlockPos neighborPos,
			net.minecraft.world.level.block.state.BlockState neighborState,
			net.minecraft.util.RandomSource random
	) {
		final WorldAccess worldAccess;
		if (level instanceof net.minecraft.world.level.LevelAccessor accessor) {
			worldAccess = new WorldAccess(accessor);
		} else {
			return state;
		}
		final BlockState result = getStateForNeighborUpdate2(
				new BlockState(state),
				Direction.convert(direction),
				new BlockState(neighborState),
				worldAccess,
				new BlockPos(pos),
				new BlockPos(neighborPos)
		);
		return result == null ? state : result.data;
	}

	// --- shapes ---

	@Nonnull
	@MappedMethod
	public VoxelShape getOutlineShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return new VoxelShape(super.getShape(state.data, world.data, pos.data, context.data));
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.phys.shapes.VoxelShape getShape(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.BlockGetter world,
			net.minecraft.core.BlockPos pos,
			net.minecraft.world.phys.shapes.CollisionContext context
	) {
		return getOutlineShape2(new BlockState(state), new BlockView(world), new BlockPos(pos), new ShapeContext(context)).data;
	}

	@Nonnull
	@MappedMethod
	public VoxelShape getCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return new VoxelShape(super.getCollisionShape(state.data, world.data, pos.data, context.data));
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.phys.shapes.VoxelShape getCollisionShape(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.BlockGetter world,
			net.minecraft.core.BlockPos pos,
			net.minecraft.world.phys.shapes.CollisionContext context
	) {
		return getCollisionShape2(new BlockState(state), new BlockView(world), new BlockPos(pos), new ShapeContext(context)).data;
	}

	@Nonnull
	@MappedMethod
	public VoxelShape getCameraCollisionShape2(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getOutlineShape2(state, world, pos, context);
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.phys.shapes.VoxelShape getVisualShape(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.BlockGetter world,
			net.minecraft.core.BlockPos pos,
			net.minecraft.world.phys.shapes.CollisionContext context
	) {
		return getCameraCollisionShape2(new BlockState(state), new BlockView(world), new BlockPos(pos), new ShapeContext(context)).data;
	}

	@Nonnull
	@MappedMethod
	public VoxelShape getCullingShape2(BlockState state, BlockView world, BlockPos pos) {
		return new VoxelShape(super.getOcclusionShape(state.data));
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.phys.shapes.VoxelShape getOcclusionShape(net.minecraft.world.level.block.state.BlockState state) {
		// pos/world not available in 26.1 occlusion hook; pass zeros for yarn-shaped overrides that ignore them
		return getCullingShape2(new BlockState(state), new BlockView(net.minecraft.world.level.EmptyBlockGetter.INSTANCE), new BlockPos(net.minecraft.core.BlockPos.ZERO)).data;
	}

	// --- interaction ---

	@Nonnull
	@MappedMethod
	public ActionResult onUse2(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return ActionResult.PASS;
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.InteractionResult useWithoutItem(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.Level level,
			net.minecraft.core.BlockPos pos,
			net.minecraft.world.entity.player.Player player,
			net.minecraft.world.phys.BlockHitResult hit
	) {
		return onUse2(new BlockState(state), new World(level), new BlockPos(pos), new PlayerEntity(player), Hand.MAIN_HAND, new BlockHitResult(hit)).data;
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.InteractionResult useItemOn(
			net.minecraft.world.item.ItemStack stack,
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.Level level,
			net.minecraft.core.BlockPos pos,
			net.minecraft.world.entity.player.Player player,
			net.minecraft.world.InteractionHand hand,
			net.minecraft.world.phys.BlockHitResult hit
	) {
		return onUse2(new BlockState(state), new World(level), new BlockPos(pos), new PlayerEntity(player), Hand.convert(hand), new BlockHitResult(hit)).data;
	}

	// --- ticking ---

	@MappedMethod
	public void scheduledTick2(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.tick(state.data, world.data, pos.data, random.data);
	}

	@Deprecated
	@Override
	protected final void tick(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.server.level.ServerLevel level,
			net.minecraft.core.BlockPos pos,
			net.minecraft.util.RandomSource random
	) {
		scheduledTick2(new BlockState(state), new ServerWorld(level), new BlockPos(pos), new Random(random));
	}

	@MappedMethod
	public void randomTick2(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.randomTick(state.data, world.data, pos.data, random.data);
	}

	@Deprecated
	@Override
	protected final void randomTick(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.server.level.ServerLevel level,
			net.minecraft.core.BlockPos pos,
			net.minecraft.util.RandomSource random
	) {
		randomTick2(new BlockState(state), new ServerWorld(level), new BlockPos(pos), new Random(random));
	}

	@MappedMethod
	public void randomDisplayTick2(BlockState state, World world, BlockPos pos, Random random) {
		super.animateTick2(state, world, pos, random);
	}

	@Deprecated
	@Override
	public void animateTick2(BlockState state, World world, BlockPos pos, Random random) {
		randomDisplayTick2(state, world, pos, random);
	}

	@MappedMethod
	public boolean hasRandomTicks2(BlockState state) {
		return super.isRandomlyTicking(state.data);
	}

	@Deprecated
	@Override
	protected final boolean isRandomlyTicking(net.minecraft.world.level.block.state.BlockState state) {
		return hasRandomTicks2(new BlockState(state));
	}

	// --- fluids / render / redstone / entity ---

	@Nonnull
	@MappedMethod
	public FluidState getFluidState2(BlockState state) {
		return new FluidState(super.getFluidState(state.data));
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.level.material.FluidState getFluidState(net.minecraft.world.level.block.state.BlockState state) {
		return getFluidState2(new BlockState(state)).data;
	}

	@Nonnull
	@MappedMethod
	public BlockRenderType getRenderType2(BlockState state) {
		return BlockRenderType.convert(super.getRenderShape(state.data));
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.level.block.RenderShape getRenderShape(net.minecraft.world.level.block.state.BlockState state) {
		return getRenderType2(new BlockState(state)).data;
	}

	@MappedMethod
	public boolean emitsRedstonePower2(BlockState state) {
		return super.isSignalSource(state.data);
	}

	@Deprecated
	@Override
	protected final boolean isSignalSource(net.minecraft.world.level.block.state.BlockState state) {
		return emitsRedstonePower2(new BlockState(state));
	}

	@MappedMethod
	public int getWeakRedstonePower2(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return super.getSignal(state.data, world.data, pos.data, direction.data);
	}

	@Deprecated
	@Override
	protected final int getSignal(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.BlockGetter world,
			net.minecraft.core.BlockPos pos,
			net.minecraft.core.Direction direction
	) {
		return getWeakRedstonePower2(new BlockState(state), new BlockView(world), new BlockPos(pos), Direction.convert(direction));
	}

	@MappedMethod
	public void onEntityCollision2(BlockState state, World world, BlockPos pos, Entity entity) {
	}

	@Deprecated
	@Override
	protected final void entityInside(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.Level level,
			net.minecraft.core.BlockPos pos,
			net.minecraft.world.entity.Entity entity,
			net.minecraft.world.entity.InsideBlockEffectApplier effectApplier,
			boolean stillColliding
	) {
		onEntityCollision2(new BlockState(state), new World(level), new BlockPos(pos), new Entity(entity));
	}

	@MappedMethod
	public boolean isSideInvisible2(BlockState state, BlockState neighborState, Direction direction) {
		return super.skipRendering(state.data, neighborState.data, direction.data);
	}

	@Deprecated
	@Override
	protected final boolean skipRendering(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.block.state.BlockState neighborState,
			net.minecraft.core.Direction direction
	) {
		return isSideInvisible2(new BlockState(state), new BlockState(neighborState), Direction.convert(direction));
	}

	@MappedMethod
	public float getAmbientOcclusionLightLevel2(BlockState state, BlockView world, BlockPos pos) {
		return super.getShadeBrightness(state.data, world.data, pos.data);
	}

	@Deprecated
	@Override
	protected final float getShadeBrightness(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.BlockGetter world,
			net.minecraft.core.BlockPos pos
	) {
		return getAmbientOcclusionLightLevel2(new BlockState(state), new BlockView(world), new BlockPos(pos));
	}

	@MappedMethod
	public boolean isTranslucent2(BlockState state, BlockView world, BlockPos pos) {
		return super.propagatesSkylightDown(state.data);
	}

	@Deprecated
	@Override
	protected final boolean propagatesSkylightDown(net.minecraft.world.level.block.state.BlockState state) {
		return isTranslucent2(new BlockState(state), new BlockView(net.minecraft.world.level.EmptyBlockGetter.INSTANCE), new BlockPos(net.minecraft.core.BlockPos.ZERO));
	}

	@Nonnull
	@MappedMethod
	public ItemStack getPickStack2(BlockView world, BlockPos pos, BlockState state) {
		if (world.data instanceof net.minecraft.world.level.LevelReader reader) {
			return new ItemStack(super.getCloneItemStack(reader, pos.data, state.data, false));
		}
		return ItemStack.getEmptyMapped();
	}

	@Deprecated
	@Override
	protected final net.minecraft.world.item.ItemStack getCloneItemStack(
			net.minecraft.world.level.LevelReader world,
			net.minecraft.core.BlockPos pos,
			net.minecraft.world.level.block.state.BlockState state,
			boolean includeData
	) {
		return getPickStack2(new BlockView(world), new BlockPos(pos), new BlockState(state)).data;
	}

	@MappedMethod
	public static void scheduleBlockTick(World world, BlockPos pos, Block block, int ticks) {
		world.data.scheduleTick(pos.data, block.data, ticks);
	}

	@MappedMethod
	public static void scheduleFluidTick(World world, BlockPos pos, Fluid fluid, int ticks) {
		world.data.scheduleTick(pos.data, fluid.data, ticks);
	}

	@MappedMethod
	public static boolean hasScheduledBlockTick(World world, BlockPos pos, Block block) {
		return world.data.getBlockTicks().hasScheduledTick(pos.data, block.data);
	}

	@MappedMethod
	public void neighborUpdate2(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
		super.neighborChanged(state.data, world.data, pos.data, sourceBlock.data, null, notify);
	}

	@Deprecated
	@Override
	protected final void neighborChanged(
			net.minecraft.world.level.block.state.BlockState state,
			net.minecraft.world.level.Level level,
			net.minecraft.core.BlockPos pos,
			net.minecraft.world.level.block.Block sourceBlock,
			net.minecraft.world.level.redstone.Orientation orientation,
			boolean notify
	) {
		neighborUpdate2(new BlockState(state), new World(level), new BlockPos(pos), new Block(sourceBlock), new BlockPos(pos), notify);
	}
}
