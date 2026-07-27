package org.mtr.mapping.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Entity;
import org.mtr.mapping.holder.EntityAbstractMapping;
import org.mtr.mapping.tool.DummyClass;

import java.io.File;
import java.util.function.Consumer;

public final class MinecraftClientHelper extends DummyClass {

	@MappedMethod
	public static int getRenderDistance() {
		return Minecraft.getInstance().options.getViewDistance().getValue();
	}

	@MappedMethod
	public static File getResourcePackDirectory() {
		return Minecraft.getInstance().getResourcePackDir().toFile();
	}

	@MappedMethod
	public static void getEntities(Consumer<Entity> consumer) {
		final ClientLevel clientWorld = Minecraft.getInstance().world;
		if (clientWorld != null) {
			clientWorld.getEntities().forEach(entity -> consumer.accept(new Entity(entity)));
		}
	}

	@MappedMethod
	public static void addEntity(EntityAbstractMapping entity) {
		final ClientLevel clientWorld = Minecraft.getInstance().world;
		if (clientWorld != null) {
			clientWorld.addEntity(entity);
		}
	}
}
