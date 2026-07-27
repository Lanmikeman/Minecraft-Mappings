package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.holder.MinecraftClient;
import org.mtr.mapping.tool.DummyClass;

import java.io.File;
import java.util.function.Consumer;

public final class MinecraftClientHelper extends DummyClass {

	@MappedMethod
	public static int getRenderDistance() {
		return MinecraftClient.getInstance().data.options.getEffectiveRenderDistance();
	}

	@MappedMethod
	public static File getResourcePackDirectory() {
		return MinecraftClient.getInstance().data.getResourcePackDirectory().toFile();
	}

	@MappedMethod
	public static void getWorlds(Consumer<ClientWorld> consumer) {
		final net.minecraft.client.multiplayer.ClientLevel level = MinecraftClient.getInstance().data.level;
		if (level != null) {
			consumer.accept(new ClientWorld(level));
		}
	}

	@MappedMethod
	public static void getEntities(Consumer<Entity> consumer) {
		final net.minecraft.client.multiplayer.ClientLevel level = MinecraftClient.getInstance().data.level;
		if (level != null) {
			level.entitiesForRendering().forEach(entity -> consumer.accept(new Entity(entity)));
		}
	}

	@MappedMethod
	public static void addEntity(EntityAbstractMapping entity) {
		final net.minecraft.client.multiplayer.ClientLevel level = MinecraftClient.getInstance().data.level;
		if (level != null) {
			level.addEntity(entity);
		}
	}
}
