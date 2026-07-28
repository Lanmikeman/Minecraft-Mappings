package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class MovingSoundInstanceExtension extends MovingSoundInstanceAbstractMapping {

	@MappedMethod
	public MovingSoundInstanceExtension(SoundEvent sound, SoundCategory category) {
		super(sound, category, new Random(net.minecraft.util.RandomSource.create()));
	}

	@MappedMethod
	protected void setIsRelativeMapped(boolean isRelative) {
		relative = isRelative;
	}

	@MappedMethod
	protected void setIsRepeatableMapped(boolean isRepeatable) {
		looping = isRepeatable;
	}

	@MappedMethod
	protected void setVolume(float volume) {
		this.volume = volume;
	}

	@MappedMethod
	protected void setVolume(int volume) {
		this.volume = volume;
	}

	@MappedMethod
	protected void setPitch(float pitch) {
		this.pitch = pitch;
	}

	@MappedMethod
	protected void setPitch(int pitch) {
		this.pitch = pitch;
	}

	@MappedMethod
	protected void setRepeatDelay(int repeatDelay) {
		this.delay = repeatDelay;
	}

	@MappedMethod
	protected void setX(double x) {
		this.x = x;
	}

	@MappedMethod
	protected void setY(double y) {
		this.y = y;
	}

	@MappedMethod
	protected void setZ(double z) {
		this.z = z;
	}

	@MappedMethod
	protected void setX(int x) {
		this.x = x;
	}

	@MappedMethod
	protected void setY(int y) {
		this.y = y;
	}

	@MappedMethod
	protected void setZ(int z) {
		this.z = z;
	}

	@MappedMethod
	protected void setDone2() {
		stop();
	}

	/** Yarn-era hooks still overridden by MTR (may not exist on 26.1 AbstractTickableSoundInstance). */
	@MappedMethod
	public boolean shouldAlwaysPlay2() {
		return false;
	}

	@MappedMethod
	public boolean canPlay2() {
		return canPlaySound2();
	}
}
