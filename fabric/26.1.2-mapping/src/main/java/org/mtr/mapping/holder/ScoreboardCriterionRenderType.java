package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;

import javax.annotation.Nullable;

public enum ScoreboardCriterionRenderType {
	INTEGER(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.INTEGER),
	HEARTS(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType.HEARTS);

	public final net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType data;

	ScoreboardCriterionRenderType(net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType data) {
		this.data = data;
	}

	@Nullable
	@MappedMethod
	public static ScoreboardCriterionRenderType convert(
			@Nullable net.minecraft.world.scores.criteria.ObjectiveCriteria.RenderType data
	) {
		if (data == null) {
			return null;
		}
		for (final ScoreboardCriterionRenderType value : values()) {
			if (value.data == data) {
				return value;
			}
		}
		return null;
	}
}
