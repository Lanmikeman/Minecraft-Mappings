package org.mtr.mapping.mapper;

import net.minecraft.nbt.CompoundTag;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.World;

public abstract class EntityExtension extends EntityAbstractMapping {

	@MappedMethod
	public EntityExtension(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Deprecated
	@Override
	protected final void readCustomDataFromNbt(CompoundTag nbt) {
	}

	@Deprecated
	@Override
	protected final void writeCustomDataToNbt(CompoundTag nbt) {
	}

	@MappedMethod
	public void setPosition2(double x, double y, double z) {
		super.setPosition(x, y, z);
	}
}
