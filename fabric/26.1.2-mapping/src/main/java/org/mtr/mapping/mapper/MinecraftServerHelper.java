package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class MinecraftServerHelper extends DummyClass {

	@MappedMethod
	public static void iterateWorlds(MinecraftServer minecraftServer, Consumer<ServerLevel> consumer) {
		minecraftServer.data.getWorlds().forEach(serverWorld -> consumer.accept(new ServerLevel(serverWorld)));
	}

	@MappedMethod
	public static void iteratePlayers(MinecraftServer minecraftServer, Consumer<ServerPlayer> consumer) {
		minecraftServer.data.getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> consumer.accept(new ServerPlayer(serverPlayerEntity)));
	}

	@MappedMethod
	public static void iteratePlayers(ServerLevel serverWorld, Consumer<ServerPlayer> consumer) {
		serverWorld.data.getPlayers().forEach(serverPlayerEntity -> consumer.accept(new ServerPlayer(serverPlayerEntity)));
	}

	@MappedMethod
	public static void iteratePlayers(ServerLevel serverWorld, Predicate<ServerPlayer> predicate, Consumer<ServerPlayer> consumer) {
		serverWorld.data.getPlayers(serverPlayerEntity -> predicate.test(new ServerPlayer(serverPlayerEntity))).forEach(serverPlayerEntity -> consumer.accept(new ServerPlayer(serverPlayerEntity)));
	}

	@MappedMethod
	public static Identifier getWorldId(Level world) {
		return new Identifier(world.data.getResourceKey().getValue());
	}
}
