package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.tool.DummyClass;

import java.io.InputStream;
import java.util.function.Consumer;

public final class ResourceManagerHelper extends DummyClass {

	@MappedMethod
	public static void readResource(Identifier identifier, Consumer<InputStream> consumer) {
		MinecraftClient.getInstance().data.getResourceManager().getResource(identifier.data).ifPresent(resource -> {
			try (InputStream in = resource.open()) {
				consumer.accept(in);
			} catch (Exception e) {
				logException(e);
			}
		});
	}

	@MappedMethod
	public static String readResource(Identifier identifier) {
		final StringBuilder sb = new StringBuilder();
		readResource(identifier, in -> {
			try {
				sb.append(new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
			} catch (Exception e) {
				logException(e);
			}
		});
		return sb.toString();
	}

	@MappedMethod
	public static void readAllResources(Identifier identifier, Consumer<InputStream> consumer) {
		try {
			for (final var resource : MinecraftClient.getInstance().data.getResourceManager().getResourceStack(identifier.data)) {
				try (InputStream in = resource.open()) {
					consumer.accept(in);
				}
			}
		} catch (Exception e) {
			logException(e);
		}
	}

	@MappedMethod
	public static int getResourcePackVersion() {
		return 0;
	}

	@MappedMethod
	public static int getDataPackVersion() {
		return 0;
	}

	@MappedMethod
	public static void readDirectory(String directory, java.util.function.BiConsumer<Identifier, InputStream> consumer) {
		try {
			final var resourceManager = MinecraftClient.getInstance().data.getResourceManager();
			final var locations = resourceManager.listResources(directory, path -> true);
			for (final var entry : locations.entrySet()) {
				try (InputStream in = entry.getValue().open()) {
					consumer.accept(new Identifier(entry.getKey()), in);
				} catch (Exception e) {
					logException(e);
				}
			}
		} catch (Exception e) {
			logException(e);
		}
	}
}
