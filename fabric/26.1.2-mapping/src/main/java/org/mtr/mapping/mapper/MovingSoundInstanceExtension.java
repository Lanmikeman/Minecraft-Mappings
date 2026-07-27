package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class MovingSoundInstanceExtension extends MovingSoundInstanceAbstractMapping {
	@MappedMethod
	public MovingSoundInstanceExtension(SoundEvent sound, SoundCategory category) {
		super(sound, category, new Random(net.minecraft.util.RandomSource.create()));
	}
}
