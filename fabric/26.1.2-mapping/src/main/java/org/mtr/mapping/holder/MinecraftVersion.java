package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public final class MinecraftVersion extends HolderBase<net.minecraft.WorldVersion> {

	public MinecraftVersion(net.minecraft.WorldVersion data) {
		super(data);
	}

	@MappedMethod
	public static MinecraftVersion cast(HolderBase<?> data) {
		return new MinecraftVersion((net.minecraft.WorldVersion) data.data);
	}

	@MappedMethod
	public static boolean isInstance(@Nullable HolderBase<?> data) {
		return data != null && data.data instanceof net.minecraft.WorldVersion;
	}
}
