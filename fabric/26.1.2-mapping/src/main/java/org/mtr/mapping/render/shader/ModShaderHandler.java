package org.mtr.mapping.render.shader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

public final class ModShaderHandler {

	private static InternalHandler internalHandler;
	private static BooleanSupplier irisShadowPassSupplier;

	private static final String IRIS_PREFIX = "net.irisshaders";
	private static final String IRIS_CLASS = IRIS_PREFIX + ".iris.api.v0.IrisApi";
	private static final String IRIS_SHADOW_STATE = IRIS_PREFIX + ".iris.shadows.ShadowRenderingState";
	private static final String IRIS_SHADOW_RENDERER = IRIS_PREFIX + ".iris.shadows.ShadowRenderer";

	private static final String OPTIFINE_PREFIX = "net.optifine";
	private static final String OPTIFINE_CLASS = OPTIFINE_PREFIX + ".shaders.Shaders";

	public static InternalHandler getInternalHandler() {
		if (internalHandler == null) {
			internalHandler = new InternalHandler();

			try {
				final Class<?> ignored = Class.forName(IRIS_CLASS);
				internalHandler = new Iris();
			} catch (Exception ignored) {
			}

			try {
				final Class<?> ignored = Class.forName(OPTIFINE_CLASS);
				internalHandler = new Optifine();
			} catch (Exception ignored) {
			}
		}

		return internalHandler;
	}

	/**
	 * True only during an actual shadow-map pass.
	 * <p>
	 * Important: with Iris installed, Iris classes appear on nearly every world-render
	 * stack frame. Matching {@code net.irisshaders.*} alone made every frame look like a
	 * shadow pass, forcing {@code millisElapsed = 0} and causing jittery trains/rails.
	 */
	public static boolean renderingShadows() {
		final BooleanSupplier iris = getIrisShadowPassSupplier();
		if (iris != null) {
			try {
				return iris.getAsBoolean();
			} catch (Exception ignored) {
			}
		}

		for (final StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
			final String className = stackTraceElement.getClassName();
			final String lower = className.toLowerCase();
			if ((className.startsWith(IRIS_PREFIX) || className.startsWith(OPTIFINE_PREFIX))
					&& (lower.contains("shadow") || lower.contains("shadowmap"))) {
				return true;
			}
		}
		return false;
	}

	private static BooleanSupplier getIrisShadowPassSupplier() {
		if (irisShadowPassSupplier != null) {
			return irisShadowPassSupplier;
		}
		synchronized (ModShaderHandler.class) {
			if (irisShadowPassSupplier != null) {
				return irisShadowPassSupplier;
			}
			irisShadowPassSupplier = createIrisShadowPassSupplier();
			return irisShadowPassSupplier;
		}
	}

	private static BooleanSupplier createIrisShadowPassSupplier() {
		try {
			final Class<?> stateClass = Class.forName(IRIS_SHADOW_STATE);
			final Method method = stateClass.getMethod("areShadowsCurrentlyBeingRendered");
			return () -> {
				try {
					return Boolean.TRUE.equals(method.invoke(null));
				} catch (Exception ignored) {
					return false;
				}
			};
		} catch (Exception ignored) {
		}

		try {
			final Class<?> rendererClass = Class.forName(IRIS_SHADOW_RENDERER);
			final Field active = rendererClass.getField("ACTIVE");
			return () -> {
				try {
					return active.getBoolean(null);
				} catch (Exception ignored) {
					return false;
				}
			};
		} catch (Exception ignored) {
		}

		return null;
	}

	public static class InternalHandler {

		public boolean noShaderPackInUse() {
			return true;
		}
	}

	private static class Iris extends InternalHandler {

		private final BooleanSupplier shadersEnabledSupplier;

		private Iris() {
			shadersEnabledSupplier = createShadersEnabledSupplier();
		}

		@Override
		public boolean noShaderPackInUse() {
			return !shadersEnabledSupplier.getAsBoolean();
		}

		private static BooleanSupplier createShadersEnabledSupplier() {
			try {
				Class<?> irisApiClass = Class.forName(IRIS_CLASS);
				Object irisApiInstance = irisApiClass.getMethod("getInstance").invoke(null);
				Method fnIsShaderPackInUse = irisApiClass.getMethod("isShaderPackInUse");
				return () -> {
					try {
						return (Boolean) fnIsShaderPackInUse.invoke(irisApiInstance);
					} catch (Exception ignored) {
						return false;
					}
				};
			} catch (Exception ignored) {
				return () -> false;
			}
		}
	}

	private static class Optifine extends InternalHandler {

		private final BooleanSupplier shadersEnabledSupplier;

		private Optifine() {
			shadersEnabledSupplier = createShadersEnabledSupplier();
		}

		@Override
		public boolean noShaderPackInUse() {
			return !shadersEnabledSupplier.getAsBoolean();
		}

		private static BooleanSupplier createShadersEnabledSupplier() {
			try {
				Class<?> ofShaders = Class.forName(OPTIFINE_CLASS);
				Field field = ofShaders.getDeclaredField("activeProgramID");
				return () -> {
					try {
						return (int) field.get(null) != 0;
					} catch (IllegalAccessException ignored) {
						return false;
					}
				};
			} catch (Exception ignored) {
				return () -> false;
			}
		}
	}
}
