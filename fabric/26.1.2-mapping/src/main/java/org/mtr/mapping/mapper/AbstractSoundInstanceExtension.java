package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class AbstractSoundInstanceExtension extends AbstractSoundInstanceAbstractMapping {
	@MappedMethod
	public AbstractSoundInstanceExtension(SoundEvent sound, SoundCategory category) {
		super(sound, category, new Random(net.minecraft.util.RandomSource.create()));
	}

	@MappedMethod
	public void setVolume2(float volume) { this.volume = volume; }

	@MappedMethod
	public void setPitch2(float pitch) { this.pitch = pitch; }
}
