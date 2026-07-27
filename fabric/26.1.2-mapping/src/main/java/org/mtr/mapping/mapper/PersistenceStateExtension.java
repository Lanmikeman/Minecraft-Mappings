package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.Supplier;

/** SavedData API changed in 26.1; bridge stub with stable MappedMethod surface. */
public abstract class PersistenceStateExtension extends DummyClass {
	@MappedMethod
	public PersistenceStateExtension() {}

	@MappedMethod
	public abstract void readNbt(org.mtr.mapping.holder.CompoundTag compoundTag);

	@MappedMethod
	public abstract org.mtr.mapping.holder.CompoundTag writeNbt();

	@MappedMethod
	public static <T extends PersistenceStateExtension> T register(ServerWorld serverWorld, Supplier<T> supplier, String id) {
		return supplier.get();
	}
}
