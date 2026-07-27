package org.mtr.mapping.mapper;

import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.scoreboard.number.BlankNumberFormat;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;

public final class ScoreboardHelper extends DummyClass {

	@Nullable
	@MappedMethod
	public static Objective getScoreboardObjective(Scoreboard scoreboard, String name) {
		final net.minecraft.world.scores.Objective scoreboardObjective = scoreboard.data.getNullableObjective(name);
		return scoreboardObjective == null ? null : new Objective(scoreboardObjective);
	}

	@MappedMethod
	public static Objective addObjective(Scoreboard scoreboard, String name, ObjectiveCriteria scoreboardCriterion, Component displayName, ScoreboardCriterionRenderType scoreboardCriterionRenderType) {
		return new Objective(scoreboard.data.addObjective(name, scoreboardCriterion.data, displayName.data, scoreboardCriterionRenderType.data, true, BlankNumberFormat.INSTANCE));
	}

	@MappedMethod
	public static int getPlayerScore(Scoreboard scoreboard, String playerName, Objective scoreboardObjective) {
		return getOrCreateScore(scoreboard, playerName, scoreboardObjective).getScore();
	}

	@MappedMethod
	public static void setPlayerScore(Scoreboard scoreboard, String playerName, Objective scoreboardObjective, int amount) {
		getOrCreateScore(scoreboard, playerName, scoreboardObjective).setScore(amount);
	}

	@MappedMethod
	public static void incrementPlayerScore(Scoreboard scoreboard, String playerName, Objective scoreboardObjective, int amount) {
		getOrCreateScore(scoreboard, playerName, scoreboardObjective).incrementScore(amount);
	}

	private static ScoreAccess getOrCreateScore(Scoreboard scoreboard, String playerName, Objective scoreboardObjective) {
		return scoreboard.data.getOrCreateScore(ScoreHolder.fromName(playerName), scoreboardObjective.data);
	}
}
