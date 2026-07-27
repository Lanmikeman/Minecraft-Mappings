package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.PackType;
import net.minecraft.resources.Identifier;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ClientWorld;
import org.mtr.mapping.holder.WorldChunk;
import org.mtr.mapping.mapper.GraphicsHolder;
import org.mtr.mapping.tool.DummyClass;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventRegistryClient extends DummyClass {

	@MappedMethod
	public void registerStartClientTick(Runnable runnable) {
		ClientTickEvents.START_CLIENT_TICK.register(minecraftServer -> runnable.run());
	}

	@MappedMethod
	public void registerEndClientTick(Runnable runnable) {
		ClientTickEvents.END_CLIENT_TICK.register(minecraftServer -> runnable.run());
	}

	@MappedMethod
	public void registerStartWorldTick(Consumer<ClientLevel> consumer) {
		ClientTickEvents.START_WORLD_TICK.register(clientWorld -> consumer.accept(new ClientLevel(clientWorld)));
	}

	@MappedMethod
	public void registerEndWorldTick(Consumer<ClientLevel> consumer) {
		ClientTickEvents.END_WORLD_TICK.register(clientWorld -> consumer.accept(new ClientLevel(clientWorld)));
	}

	@MappedMethod
	public void registerClientJoin(Runnable runnable) {
		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> runnable.run());
	}

	@MappedMethod
	public void registerClientDisconnect(Runnable runnable) {
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> runnable.run());
	}

	@MappedMethod
	public void registerChunkLoad(BiConsumer<ClientLevel, WorldChunk> consumer) {
		ClientChunkEvents.CHUNK_LOAD.register((clientWorld, worldChunk) -> consumer.accept(new ClientLevel(clientWorld), new WorldChunk(worldChunk)));
	}

	@MappedMethod
	public void registerChunkUnload(BiConsumer<ClientLevel, WorldChunk> consumer) {
		ClientChunkEvents.CHUNK_UNLOAD.register((clientWorld, worldChunk) -> consumer.accept(new ClientLevel(clientWorld), new WorldChunk(worldChunk)));
	}

	@MappedMethod
	public void registerGuiRendering(Consumer<GraphicsHolder> consumer) {
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> GraphicsHolder.createInstanceSafe(drawContext, consumer));
	}

	@MappedMethod
	public void registerResourceReloadEvent(Runnable runnable) {
		final Identifier identifier = new Identifier(Integer.toHexString(new Random().nextInt()), "resource");
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Deprecated
			@Override
			public final Identifier getFabricId() {
				return identifier;
			}

			@Deprecated
			@Override
			public final void reload(ResourceManager manager) {
				runnable.run();
			}
		});
	}
}
