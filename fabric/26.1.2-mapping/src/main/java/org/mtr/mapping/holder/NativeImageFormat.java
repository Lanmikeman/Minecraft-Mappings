package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;

import javax.annotation.Nullable;

public enum NativeImageFormat {
	RGBA(com.mojang.blaze3d.platform.NativeImage.Format.RGBA),
	RGB(com.mojang.blaze3d.platform.NativeImage.Format.RGB),
	LUMINANCE_ALPHA(com.mojang.blaze3d.platform.NativeImage.Format.LUMINANCE_ALPHA),
	LUMINANCE(com.mojang.blaze3d.platform.NativeImage.Format.LUMINANCE);

	public final com.mojang.blaze3d.platform.NativeImage.Format data;

	NativeImageFormat(com.mojang.blaze3d.platform.NativeImage.Format data) {
		this.data = data;
	}

	@Nullable
	@MappedMethod
	public static NativeImageFormat convert(@Nullable com.mojang.blaze3d.platform.NativeImage.Format data) {
		if (data == null) {
			return null;
		}
		for (final NativeImageFormat value : values()) {
			if (value.data == data) {
				return value;
			}
		}
		return null;
	}

	@MappedMethod
	public static NativeImageFormat getRGBAMapped() {
		return RGBA;
	}

	@MappedMethod
	public static NativeImageFormat getAbgrMapped() {
		return RGBA;
	}
}
