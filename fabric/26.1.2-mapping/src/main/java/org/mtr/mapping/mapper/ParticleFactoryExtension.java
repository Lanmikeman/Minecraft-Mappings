package org.mtr.mapping.mapper;
import net.minecraft.util.RandomSource;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.Particle;
import org.mtr.mapping.holder.BillboardParticle;
import org.mtr.mapping.holder.SpriteProvider;
import org.mtr.mapping.tool.DummyClass;

public abstract class ParticleFactoryExtension implements ParticleProvider<SimpleParticleType> {

	private final CreateParticle createParticle;
	private final CreateBillboardParticle createBillboardParticle;
	private final SpriteProvider spriteProvider;

	@MappedMethod
	public ParticleFactoryExtension(CreateParticle createParticle, SpriteProvider spriteProvider) {
		this.createParticle = createParticle;
		createBillboardParticle = null;
		this.spriteProvider = spriteProvider;
	}

	@MappedMethod
	public ParticleFactoryExtension(CreateBillboardParticle createBillboardParticle, SpriteProvider spriteProvider) {
		createParticle = null;
		this.createBillboardParticle = createBillboardParticle;
		this.spriteProvider = spriteProvider;
	}

	@Deprecated
	public final net.minecraft.client.particle.Particle createParticle(SimpleParticleType defaultParticleType, net.minecraft.client.multiplayer.ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ, net.minecraft.util.RandomSource random) {
		if (createParticle != null) {
			return createParticle.create(new ClientWorld(clientWorld), x, y, z, velocityX, velocityY, velocityZ).data;
		} else if (createBillboardParticle != null) {
			final BillboardParticle spriteBillboardParticle = createBillboardParticle.create(new ClientWorld(clientWorld), x, y, z, velocityX, velocityY, velocityZ);
			// sprite set TODO
			return spriteBillboardParticle.data;
		} else {
			final NullPointerException nullPointerException = new NullPointerException("Both createParticle and createBillboardParticle are null!");
			DummyClass.logException(nullPointerException);
			throw nullPointerException;
		}
	}

	@FunctionalInterface
	public interface CreateParticle {
		@MappedMethod
		Particle create(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
	}

	@FunctionalInterface
	public interface CreateBillboardParticle {
		@MappedMethod
		BillboardParticle create(ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ);
	}
}
