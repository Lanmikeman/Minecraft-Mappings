package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.DummyInterface;

import javax.annotation.Nullable;
import java.util.List;

public interface ItemHelper extends DummyInterface {

	@MappedMethod
	default void addTooltips(ItemStack stack, @Nullable World world, List<MutableText> tooltip, TooltipContext options) {
	}
}
