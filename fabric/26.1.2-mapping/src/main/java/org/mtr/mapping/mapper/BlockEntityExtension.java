package org.mtr.mapping.mapper;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import java.lang.reflect.Field;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping {

	private static final Field TAG_VALUE_INPUT_TAG;

	static {
		Field field;
		try {
			field = TagValueInput.class.getDeclaredField("input");
			field.setAccessible(true);
		} catch (ReflectiveOperationException e) {
			field = null;
		}
		TAG_VALUE_INPUT_TAG = field;
	}

	@MappedMethod
	public BlockEntityExtension(BlockEntityType<?> type, BlockPos blockPos, BlockState blockState) {
		super(type, blockPos, blockState);
	}

	@Deprecated
	@Override
	protected void saveAdditional2(ValueOutput output) {
		super.saveAdditional2(output);
		final CompoundTag tag = new CompoundTag();
		writeCompoundTag(new org.mtr.mapping.holder.CompoundTag(tag));
		if (!tag.isEmpty()) {
			if (output instanceof TagValueOutput tagValueOutput) {
				tagValueOutput.buildResult().merge(tag);
			} else {
				output.store("mtr", CompoundTag.CODEC, tag);
			}
		}
	}

	@Deprecated
	@Override
	protected void loadAdditional2(ValueInput input) {
		super.loadAdditional2(input);
		readCompoundTag(new org.mtr.mapping.holder.CompoundTag(readCustomTag(input)));
	}

	@MappedMethod
	public void writeCompoundTag(org.mtr.mapping.holder.CompoundTag compoundTag) {
	}

	@MappedMethod
	public void readCompoundTag(org.mtr.mapping.holder.CompoundTag compoundTag) {
	}

	/**
	 * MC 26.1's default {@code getUpdateTag} returns an empty tag, so clients never see
	 * custom PIDS/sign/etc. data after {@code setData}. Use the same payload as chunk save.
	 */
	@Deprecated
	@Override
	public org.mtr.mapping.holder.CompoundTag getUpdateTag2(HolderLookup.Provider registries) {
		return saveCustomOnly2(registries);
	}

	@Deprecated
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket2() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@MappedMethod
	public void blockEntityTick() {
	}

	/** Yarn BlockEntity#getPos -> official getBlockPos() */
	@MappedMethod
	public BlockPos getPos2() {
		return new BlockPos(getBlockPos());
	}

	@MappedMethod
	public World getWorld2() {
		final net.minecraft.world.level.Level level = getLevel();
		return level == null ? null : new World(level);
	}

	@MappedMethod
	public BlockState getCachedState2() {
		return new BlockState(getBlockState());
	}

	@MappedMethod
	public void markDirty2() {
		setChanged();
		final net.minecraft.world.level.Level level = getLevel();
		final net.minecraft.world.level.block.state.BlockState blockState = getBlockState();
		if (level != null && !level.isClientSide() && blockState != null) {
			level.sendBlockUpdated(getBlockPos(), blockState, blockState, Block.UPDATE_CLIENTS);
		}
	}

	@MappedMethod
	public double getRenderDistance2() {
		return 0;
	}

	@MappedMethod
	public void markRemoved2() {
		setRemoved();
	}

	private static CompoundTag readCustomTag(ValueInput input) {
		if (TAG_VALUE_INPUT_TAG != null && input instanceof TagValueInput) {
			try {
				final Object value = TAG_VALUE_INPUT_TAG.get(input);
				if (value instanceof CompoundTag compoundTag) {
					return compoundTag;
				}
			} catch (ReflectiveOperationException ignored) {
			}
		}
		return input.read("mtr", CompoundTag.CODEC).orElseGet(CompoundTag::new);
	}
}
