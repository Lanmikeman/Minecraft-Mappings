package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public final class TooltipContext extends HolderBase<net.minecraft.world.item.Item.TooltipContext> {

	public TooltipContext(net.minecraft.world.item.Item.TooltipContext data) {
		super(data);
	}

	@MappedMethod
	public static TooltipContext cast(HolderBase<?> data) {
		return new TooltipContext((net.minecraft.world.item.Item.TooltipContext) data.data);
	}

	@MappedMethod
	public static boolean isInstance(@Nullable HolderBase<?> data) {
		return data != null && data.data instanceof net.minecraft.world.item.Item.TooltipContext;
	}
}
