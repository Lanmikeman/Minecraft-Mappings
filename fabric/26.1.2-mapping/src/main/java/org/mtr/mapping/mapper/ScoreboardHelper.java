package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

public final class ScoreboardHelper extends DummyClass {
	@MappedMethod
	public static ScoreboardObjective getScoreboardObjective(Scoreboard scoreboard, String name) {
		final net.minecraft.world.scores.Objective obj = scoreboard.data.getObjective(name);
		return obj == null ? null : new ScoreboardObjective(obj);
	}

	@MappedMethod
	public static ScoreboardObjective addObjective(Scoreboard scoreboard, String name, ScoreboardCriterion criterion, MutableText displayName, Object renderType) {
		return null; // TODO scores API
	}

	@MappedMethod
	public static int getPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective objective) {
		return 0;
	}

	@MappedMethod
	public static void setPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective objective, int amount) {}

	@MappedMethod
	public static void incrementPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective objective, int amount) {}
}
