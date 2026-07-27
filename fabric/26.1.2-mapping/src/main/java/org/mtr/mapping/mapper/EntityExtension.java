package org.mtr.mapping.mapper;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.holder.EntityType;
import org.mtr.mapping.holder.Vector3d;
import org.mtr.mapping.holder.World;

import java.util.UUID;

public abstract class EntityExtension extends EntityAbstractMapping {

	@MappedMethod
	public EntityExtension(EntityType<?> type, World world) {
		super(type, world);
	}

	@Deprecated
	@Override
	protected final void readAdditionalSaveData2(ValueInput input) {
	}

	@Deprecated
	@Override
	protected final void addAdditionalSaveData2(ValueOutput output) {
	}

	@MappedMethod
	public void setPosition2(double x, double y, double z) {
		super.setPos(x, y, z);
	}

	/** Yarn Entity#getPos -> official position() */
	@MappedMethod
	public Vector3d getPos2() {
		return new Vector3d(position());
	}

	/** Yarn Entity#getWorld -> official level() */
	@MappedMethod
	public World getEntityWorld2() {
		return new World(level());
	}

	@MappedMethod
	public UUID getUuid() {
		return getUUID();
	}

	@MappedMethod
	public void setUuid2(UUID uuid) {
		setUUID(uuid);
	}

	@MappedMethod
	@Override
	public boolean hurtServer2(org.mtr.mapping.holder.ServerWorld level, net.minecraft.world.damagesource.DamageSource source, float damage) {
		return false;
	}

	@MappedMethod
	public void kill2() {
		this.discard();
	}

	/** Subclasses (MTR) still override this yarn-era hook. */
	@MappedMethod
	protected void initDataTracker2() {
	}

	@Deprecated
	@Override
	protected final void defineSynchedData2(net.minecraft.network.syncher.SynchedEntityData.Builder entityData) {
		initDataTracker2();
	}
}
