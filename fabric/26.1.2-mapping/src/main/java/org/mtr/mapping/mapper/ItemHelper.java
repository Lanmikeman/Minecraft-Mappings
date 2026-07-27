package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.holder.Text;
import org.mtr.mapping.holder.World;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.util.List;

public interface ItemHelper {
	@MappedMethod
	default void appendTooltipHelper(ItemStack stack, @Nullable World world, List<Text> tooltipList, Object tooltipContext) {
		// Tooltip pipeline changed in 26.1; no-op bridge for now.
	}
}
