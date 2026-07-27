package org.mtr.mapping.mapper;

import org.mtr.mapping.holder.ScoreboardCriterion;
import org.mtr.mapping.tool.HolderBase;

public interface ScoreboardCriteria {

	ObjectiveCriteria DUMMY = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.DUMMY);
	ObjectiveCriteria TRIGGER = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.TRIGGER);
	ObjectiveCriteria DEATH_COUNT = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.DEATH_COUNT);
	ObjectiveCriteria PLAYER_KILL_COUNT = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.PLAYER_KILL_COUNT);
	ObjectiveCriteria TOTAL_KILL_COUNT = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.TOTAL_KILL_COUNT);
	ObjectiveCriteria HEALTH = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.HEALTH);
	ObjectiveCriteria FOOD = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.FOOD);
	ObjectiveCriteria AIR = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.AIR);
	ObjectiveCriteria ARMOR = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.ARMOR);
	ObjectiveCriteria XP = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.XP);
	ObjectiveCriteria LEVEL = new ObjectiveCriteria(net.minecraft.scoreboard.ScoreboardCriterion.LEVEL);
	ObjectiveCriteria[] TEAM_KILLS = HolderBase.convertArray(net.minecraft.scoreboard.ScoreboardCriterion.TEAM_KILLS, ObjectiveCriteria[]::new, ObjectiveCriteria::new);
	ObjectiveCriteria[] KILLED_BY_TEAMS = HolderBase.convertArray(net.minecraft.scoreboard.ScoreboardCriterion.KILLED_BY_TEAMS, ObjectiveCriteria[]::new, ObjectiveCriteria::new);
}
