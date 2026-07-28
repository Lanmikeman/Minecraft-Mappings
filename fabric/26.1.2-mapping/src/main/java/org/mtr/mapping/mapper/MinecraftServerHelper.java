package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.MinecraftServer;
import org.mtr.mapping.holder.ServerPlayerEntity;
import org.mtr.mapping.holder.ServerWorld;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.tool.DummyClass;

import java.util.function.Consumer;

public final class MinecraftServerHelper extends DummyClass {

	@MappedMethod
	public static void iterateWorlds(MinecraftServer minecraftServer, Consumer<ServerWorld> consumer) {
		minecraftServer.data.getAllLevels().forEach(serverWorld -> consumer.accept(new ServerWorld(serverWorld)));
	}

	@MappedMethod
	public static void iteratePlayers(MinecraftServer minecraftServer, Consumer<ServerPlayerEntity> consumer) {
		minecraftServer.data.getPlayerList().getPlayers().forEach(p -> consumer.accept(new ServerPlayerEntity(p)));
	}

	@MappedMethod
	public static void iteratePlayers(ServerWorld serverWorld, Consumer<ServerPlayerEntity> consumer) {
		serverWorld.data.players().forEach(p -> consumer.accept(new ServerPlayerEntity(p)));
	}

	@MappedMethod
	public static Identifier getWorldId(ServerWorld world) {
		return new Identifier(world.data.dimension().identifier());
	}

	@MappedMethod
	public static Identifier getWorldId(World world) {
		return new Identifier(world.data.dimension().identifier());
	}
}
