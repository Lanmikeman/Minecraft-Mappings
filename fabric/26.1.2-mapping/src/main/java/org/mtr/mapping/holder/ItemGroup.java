package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public final class ItemGroup extends HolderBase<net.minecraft.world.item.CreativeModeTab> {

	public ItemGroup(net.minecraft.world.item.CreativeModeTab data) {
		super(data);
	}

	@MappedMethod
	public static ItemGroup cast(HolderBase<?> data) {
		return new ItemGroup((net.minecraft.world.item.CreativeModeTab) data.data);
	}

	@MappedMethod
	public static boolean isInstance(@Nullable HolderBase<?> data) {
		return data != null && data.data instanceof net.minecraft.world.item.CreativeModeTab;
	}
}
