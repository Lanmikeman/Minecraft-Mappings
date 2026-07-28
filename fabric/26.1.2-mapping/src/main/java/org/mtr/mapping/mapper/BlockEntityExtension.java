package org.mtr.mapping.mapper;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class BlockEntityExtension extends BlockEntityAbstractMapping {

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
	}

	@Deprecated
	@Override
	protected void loadAdditional2(ValueInput input) {
		super.loadAdditional2(input);
		readCompoundTag(new org.mtr.mapping.holder.CompoundTag(new CompoundTag()));
	}

	@MappedMethod
	public void writeCompoundTag(org.mtr.mapping.holder.CompoundTag compoundTag) {
	}

	@MappedMethod
	public void readCompoundTag(org.mtr.mapping.holder.CompoundTag compoundTag) {
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
	}

	@MappedMethod
	public void markRemoved2() {
		setRemoved();
	}
}
