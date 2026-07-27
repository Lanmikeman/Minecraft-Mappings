package org.mtr.mapping.mapper;

import net.minecraft.world.level.gamerules.GameRules;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MinecraftServer;

import javax.annotation.Nullable;

/**
 * Stable MTR enum names mapped onto Minecraft 26.1.2 GameRules constants.
 * Boolean/Integer distinction is preserved for MappedMethod callers.
 */
public enum GameRule {

	ANNOUNCE_ADVANCEMENTS(GameRules.SHOW_ADVANCEMENT_MESSAGES, null),
	BLOCK_EXPLOSION_DROP_DECAY(GameRules.BLOCK_EXPLOSION_DROP_DECAY, null),
	COMMAND_BLOCK_OUTPUT(GameRules.COMMAND_BLOCK_OUTPUT, null),
	DISABLE_ELYTRA_MOVEMENT_CHECK(GameRules.ELYTRA_MOVEMENT_CHECK, null),
	DISABLE_RAIDS(GameRules.RAIDS, null),
	DO_DAYLIGHT_CYCLE(GameRules.ADVANCE_TIME, null),
	DO_ENTITY_DROPS(GameRules.ENTITY_DROPS, null),
	DO_FIRE_TICK(null, GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER),
	DO_IMMEDIATE_RESPAWN(GameRules.IMMEDIATE_RESPAWN, null),
	DO_INSOMNIA(GameRules.SPAWN_PHANTOMS, null),
	DO_LIMITED_CRAFTING(GameRules.LIMITED_CRAFTING, null),
	DO_MOB_GRIEFING(GameRules.MOB_GRIEFING, null),
	DO_MOB_LOOT(GameRules.MOB_DROPS, null),
	DO_MOB_SPAWNING(GameRules.SPAWN_MOBS, null),
	DO_PATROL_SPAWNING(GameRules.SPAWN_PATROLS, null),
	DO_TILE_DROPS(GameRules.BLOCK_DROPS, null),
	DO_TRADER_SPAWNING(GameRules.SPAWN_WANDERING_TRADERS, null),
	DO_VINES_SPREAD(GameRules.SPREAD_VINES, null),
	DO_WARDEN_SPAWNING(GameRules.SPAWN_WARDENS, null),
	DO_WEATHER_CYCLE(GameRules.ADVANCE_WEATHER, null),
	DROWNING_DAMAGE(GameRules.DROWNING_DAMAGE, null),
	ENDER_PEARLS_VANISH_ON_DEATH(GameRules.ENDER_PEARLS_VANISH_ON_DEATH, null),
	FALL_DAMAGE(GameRules.FALL_DAMAGE, null),
	FIRE_DAMAGE(GameRules.FIRE_DAMAGE, null),
	FORGIVE_DEAD_PLAYERS(GameRules.FORGIVE_DEAD_PLAYERS, null),
	FREEZE_DAMAGE(GameRules.FREEZE_DAMAGE, null),
	GLOBAL_SOUND_EVENTS(GameRules.GLOBAL_SOUND_EVENTS, null),
	KEEP_INVENTORY(GameRules.KEEP_INVENTORY, null),
	LAVA_SOURCE_CONVERSION(GameRules.LAVA_SOURCE_CONVERSION, null),
	LOG_ADMIN_COMMANDS(GameRules.LOG_ADMIN_COMMANDS, null),
	MOB_EXPLOSION_DROP_DECAY(GameRules.MOB_EXPLOSION_DROP_DECAY, null),
	NATURAL_REGENERATION(GameRules.NATURAL_HEALTH_REGENERATION, null),
	PROJECTILES_CAN_BREAK_BLOCKS(GameRules.PROJECTILES_CAN_BREAK_BLOCKS, null),
	REDUCED_DEBUG_INFO(GameRules.REDUCED_DEBUG_INFO, null),
	SEND_COMMAND_FEEDBACK(GameRules.SEND_COMMAND_FEEDBACK, null),
	SHOW_DEATH_MESSAGES(GameRules.SHOW_DEATH_MESSAGES, null),
	SPECTATORS_GENERATE_CHUNKS(GameRules.SPECTATORS_GENERATE_CHUNKS, null),
	TNT_EXPLOSION_DROP_DECAY(GameRules.TNT_EXPLOSION_DROP_DECAY, null),
	UNIVERSAL_ANGER(GameRules.UNIVERSAL_ANGER, null),
	WATER_SOURCE_CONVERSION(GameRules.WATER_SOURCE_CONVERSION, null),
	COMMAND_MODIFICATION_BLOCK_LIMIT(null, GameRules.MAX_BLOCK_MODIFICATIONS),
	MAX_COMMAND_CHAIN_LENGTH(null, GameRules.MAX_COMMAND_SEQUENCE_LENGTH),
	MAX_COMMAND_FORK_COUNT(null, GameRules.MAX_COMMAND_FORKS),
	MAX_ENTITY_CRAMMING(null, GameRules.MAX_ENTITY_CRAMMING),
	PLAYERS_NETHER_PORTAL_CREATIVE_DELAY(null, GameRules.PLAYERS_NETHER_PORTAL_CREATIVE_DELAY),
	PLAYERS_NETHER_PORTAL_DEFAULT_DELAY(null, GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY),
	PLAYERS_SLEEPING_PERCENTAGE(null, GameRules.PLAYERS_SLEEPING_PERCENTAGE),
	RANDOM_TICK_SPEED(null, GameRules.RANDOM_TICK_SPEED),
	SNOW_ACCUMULATION_HEIGHT(null, GameRules.MAX_SNOW_ACCUMULATION_HEIGHT),
	SPAWN_RADIUS(null, GameRules.RESPAWN_RADIUS);

	@Nullable
	private final net.minecraft.world.level.gamerules.GameRule<Boolean> gameRuleBoolean;
	@Nullable
	private final net.minecraft.world.level.gamerules.GameRule<Integer> gameRuleInteger;

	@Deprecated
	GameRule(@Nullable net.minecraft.world.level.gamerules.GameRule<Boolean> gameRuleBoolean, @Nullable net.minecraft.world.level.gamerules.GameRule<Integer> gameRuleInteger) {
		this.gameRuleBoolean = gameRuleBoolean;
		this.gameRuleInteger = gameRuleInteger;
	}

	@MappedMethod
	public boolean getBooleanGameRule(MinecraftServer minecraftServer) {
		return gameRuleBoolean != null && Boolean.TRUE.equals(minecraftServer.data.getGameRules().get(gameRuleBoolean));
	}

	@MappedMethod
	public int getIntegerGameRule(MinecraftServer minecraftServer) {
		if (gameRuleInteger == null) {
			return 0;
		}
		final Integer value = minecraftServer.data.getGameRules().get(gameRuleInteger);
		return value == null ? 0 : value;
	}

	@MappedMethod
	public boolean hasBooleanGameRule() {
		return gameRuleBoolean != null;
	}

	@MappedMethod
	public boolean hasIntegerGameRule() {
		return gameRuleInteger != null;
	}
}