package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.TickableSoundInstance;

public interface TickableSoundInstanceExtension extends TickableSoundInstance {

	/** Yarn-era name; MC 26 uses {@link #isStopped2()}. */
	@MappedMethod
	boolean isDone();

	@MappedMethod
	@Override
	default boolean isStopped2() {
		return isDone();
	}
}
