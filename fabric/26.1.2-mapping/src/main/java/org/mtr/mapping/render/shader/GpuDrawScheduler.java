package org.mtr.mapping.render.shader;

import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Flushes GPU Optimized Renderer draws around translucent terrain.
 * <p>
 * Opaque/cutout must run <b>before</b> translucent blocks (glass), otherwise depth from
 * glass hides trains when looking through it. Translucent vehicle parts flush on END_MAIN
 * (AFTER_TRANSLUCENT_TERRAIN still sits inside an open RenderPass).
 */
public final class GpuDrawScheduler {

	private static final AtomicReference<Runnable> OPAQUE_FLUSHER = new AtomicReference<>();
	private static final AtomicReference<Runnable> TRANSLUCENT_FLUSHER = new AtomicReference<>();
	private static boolean registered;

	private GpuDrawScheduler() {
	}

	public static void setFlushers(Runnable opaqueFlusher, Runnable translucentFlusher) {
		OPAQUE_FLUSHER.set(opaqueFlusher);
		TRANSLUCENT_FLUSHER.set(translucentFlusher);
		ensureRegistered();
	}

	/** @deprecated use {@link #setFlushers} */
	@Deprecated
	public static void setFlusher(Runnable flusher) {
		setFlushers(flusher, flusher);
	}

	private static synchronized void ensureRegistered() {
		if (registered) {
			return;
		}
		registered = true;
		LevelRenderEvents.BEFORE_TRANSLUCENT_TERRAIN.register(context -> {
			final Runnable flusher = OPAQUE_FLUSHER.get();
			if (flusher != null) {
				flusher.run();
			}
		});
		LevelRenderEvents.END_MAIN.register(context -> {
			final Runnable flusher = TRANSLUCENT_FLUSHER.get();
			if (flusher != null) {
				flusher.run();
			}
		});
	}
}
