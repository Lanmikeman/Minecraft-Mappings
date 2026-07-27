package org.mtr.mapping.mapper;

import org.mtr.mapping.holder.ScoreboardCriterion;
import org.mtr.mapping.tool.HolderBase;

public interface ScoreboardCriteria {

	ScoreboardCriterion DUMMY = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.DUMMY);
	ScoreboardCriterion TRIGGER = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.TRIGGER);
	ScoreboardCriterion DEATH_COUNT = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.DEATH_COUNT);
	ScoreboardCriterion PLAYER_KILL_COUNT = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.PLAYER_KILL_COUNT);
	ScoreboardCriterion TOTAL_KILL_COUNT = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.TOTAL_KILL_COUNT);
	ScoreboardCriterion HEALTH = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.HEALTH);
	ScoreboardCriterion FOOD = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.FOOD);
	ScoreboardCriterion AIR = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.AIR);
	ScoreboardCriterion ARMOR = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.ARMOR);
	ScoreboardCriterion XP = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.XP);
	ScoreboardCriterion LEVEL = new ScoreboardCriterion(net.minecraft.world.scores.ScoreboardCriterion.LEVEL);
	ScoreboardCriterion[] TEAM_KILLS = HolderBase.convertArray(net.minecraft.world.scores.ScoreboardCriterion.TEAM_KILLS, ScoreboardCriterion[]::new, ScoreboardCriterion::new);
	ScoreboardCriterion[] KILLED_BY_TEAMS = HolderBase.convertArray(net.minecraft.world.scores.ScoreboardCriterion.KILLED_BY_TEAMS, ScoreboardCriterion[]::new, ScoreboardCriterion::new);
}
