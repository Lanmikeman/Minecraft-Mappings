package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.WorldChunk;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventRegistry extends DummyClass {

	@MappedMethod
	public void registerServerStarting(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> consumer.accept(new MinecraftServer(minecraftServer)));
	}

	@MappedMethod
	public void registerServerStarted(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> consumer.accept(new MinecraftServer(minecraftServer)));
	}

	@MappedMethod
	public void registerServerStopping(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> consumer.accept(new MinecraftServer(minecraftServer)));
	}

	@MappedMethod
	public void registerServerStopped(Consumer<MinecraftServer> consumer) {
		ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> consumer.accept(new MinecraftServer(minecraftServer)));
	}

	@MappedMethod
	public void registerStartServerTick(Runnable runnable) {
		ServerTickEvents.START_SERVER_TICK.register(minecraftServer -> runnable.run());
	}

	@MappedMethod
	public void registerEndServerTick(Runnable runnable) {
		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> runnable.run());
	}

	@MappedMethod
	public void registerStartWorldTick(Consumer<ServerLevel> consumer) {
		ServerTickEvents.START_WORLD_TICK.register(serverWorld -> consumer.accept(new ServerLevel(serverWorld)));
	}

	@MappedMethod
	public void registerEndWorldTick(Consumer<ServerLevel> consumer) {
		ServerTickEvents.END_WORLD_TICK.register(serverWorld -> consumer.accept(new ServerLevel(serverWorld)));
	}

	@MappedMethod
	public void registerPlayerJoin(BiConsumer<MinecraftServer, ServerPlayer> consumer) {
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> consumer.accept(new MinecraftServer(server), new ServerPlayer(handler.player)));
	}

	@MappedMethod
	public void registerPlayerDisconnect(BiConsumer<MinecraftServer, ServerPlayer> consumer) {
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> consumer.accept(new MinecraftServer(server), new ServerPlayer(handler.player)));
	}

	@MappedMethod
	public void registerChunkLoad(BiConsumer<ServerLevel, WorldChunk> consumer) {
		ServerChunkEvents.CHUNK_LOAD.register((serverWorld, worldChunk) -> consumer.accept(new ServerLevel(serverWorld), new WorldChunk(worldChunk)));
	}

	@MappedMethod
	public void registerChunkUnload(BiConsumer<ServerLevel, WorldChunk> consumer) {
		ServerChunkEvents.CHUNK_UNLOAD.register((serverWorld, worldChunk) -> consumer.accept(new ServerLevel(serverWorld), new WorldChunk(worldChunk)));
	}
}
