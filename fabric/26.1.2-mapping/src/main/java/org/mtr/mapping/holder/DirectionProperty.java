package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public final class DirectionProperty extends HolderBase<net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction>> {

	public DirectionProperty(net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction> data) {
		super(data);
	}

	@MappedMethod
	public static DirectionProperty cast(HolderBase<?> data) {
		return new DirectionProperty((net.minecraft.world.level.block.state.properties.EnumProperty<net.minecraft.core.Direction>) data.data);
	}

	@MappedMethod
	public static boolean isInstance(@Nullable HolderBase<?> data) {
		return data != null && data.data instanceof net.minecraft.world.level.block.state.properties.EnumProperty;
	}
}
