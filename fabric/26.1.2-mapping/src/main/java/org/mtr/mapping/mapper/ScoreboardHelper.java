package org.mtr.mapping.mapper;

import net.minecraft.network.chat.numbers.BlankFormat;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;

public final class ScoreboardHelper extends DummyClass {

	@Nullable
	@MappedMethod
	public static ScoreboardObjective getScoreboardObjective(Scoreboard scoreboard, String name) {
		final net.minecraft.world.scores.Objective objective = scoreboard.data.getObjective(name);
		return objective == null ? null : new ScoreboardObjective(objective);
	}

	@MappedMethod
	public static ScoreboardObjective addObjective(Scoreboard scoreboard, String name, ScoreboardCriterion criterion, MutableText displayName, ScoreboardCriterionRenderType renderType) {
		return new ScoreboardObjective(scoreboard.data.addObjective(
				name,
				criterion.data,
				displayName.data,
				renderType.data,
				true,
				BlankFormat.INSTANCE
		));
	}

	@MappedMethod
	public static int getPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective objective) {
		return getOrCreateScore(scoreboard, playerName, objective).get();
	}

	@MappedMethod
	public static void setPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective objective, int amount) {
		getOrCreateScore(scoreboard, playerName, objective).set(amount);
	}

	@MappedMethod
	public static void incrementPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective objective, int amount) {
		getOrCreateScore(scoreboard, playerName, objective).add(amount);
	}

	private static ScoreAccess getOrCreateScore(Scoreboard scoreboard, String playerName, ScoreboardObjective objective) {
		return scoreboard.data.getOrCreatePlayerScore(ScoreHolder.forNameOnly(playerName), objective.data);
	}
}
