package org.mtr.mapping.registry;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.DefaultParticleType;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.RegistryObject;

import java.util.function.Consumer;

public final class ParticleTypeRegistryObject extends RegistryObject<SimpleParticleType> {

	final Identifier identifier;
	private final SimpleParticleType defaultParticleType;

	ParticleTypeRegistryObject(SimpleParticleType defaultParticleType, Identifier identifier) {
		this.identifier = identifier;
		this.defaultParticleType = defaultParticleType;
	}

	@MappedMethod
	@Override
	public SimpleParticleType get() {
		return defaultParticleType;
	}

	@MappedMethod
	@Override
	public boolean isPresent() {
		return true;
	}

	@MappedMethod
	@Override
	public void ifPresent(Consumer<SimpleParticleType> consumer) {
		consumer.accept(defaultParticleType);
	}
}
