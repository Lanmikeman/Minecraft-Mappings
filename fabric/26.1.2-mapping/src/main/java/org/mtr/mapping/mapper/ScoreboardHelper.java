package org.mtr.mapping.mapper;

import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.network.chat.numbers.BlankFormat;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;

public final class ScoreboardHelper extends DummyClass {

	@Nullable
	@MappedMethod
	public static ScoreboardObjective getScoreboardObjective(Scoreboard scoreboard, String name) {
		final net.minecraft.world.scores.ScoreboardObjective scoreboardObjective = scoreboard.data.getNullableObjective(name);
		return scoreboardObjective == null ? null : new ScoreboardObjective(scoreboardObjective);
	}

	@MappedMethod
	public static ScoreboardObjective addObjective(Scoreboard scoreboard, String name, ScoreboardCriterion scoreboardCriterion, Text displayName, ScoreboardCriterionRenderType scoreboardCriterionRenderType) {
		return new ScoreboardObjective(scoreboard.data.addObjective(name, scoreboardCriterion.data, displayName.data, scoreboardCriterionRenderType.data, true, BlankFormat.INSTANCE));
	}

	@MappedMethod
	public static int getPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective) {
		return getOrCreateScore(scoreboard, playerName, scoreboardObjective).getScore();
	}

	@MappedMethod
	public static void setPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective, int amount) {
		getOrCreateScore(scoreboard, playerName, scoreboardObjective).setScore(amount);
	}

	@MappedMethod
	public static void incrementPlayerScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective, int amount) {
		getOrCreateScore(scoreboard, playerName, scoreboardObjective).incrementScore(amount);
	}

	private static ScoreAccess getOrCreateScore(Scoreboard scoreboard, String playerName, ScoreboardObjective scoreboardObjective) {
		return scoreboard.data.getOrCreateScore(ScoreHolder.fromName(playerName), scoreboardObjective.data);
	}
}
