package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.HolderBase;

import javax.annotation.Nonnull;

/**
 * Yarn name {@code RenderLayer} maps to Mojang {@code RenderType}.
 * MC 26 moved factories to {@link net.minecraft.client.renderer.rendertype.RenderTypes}.
 */
@SuppressWarnings({"deprecation", "unused"})
public final class RenderLayer extends HolderBase<net.minecraft.client.renderer.rendertype.RenderType> {

	public RenderLayer(net.minecraft.client.renderer.rendertype.RenderType data) {
		super(data);
	}

	@MappedMethod
	public static RenderLayer cast(org.mtr.mapping.tool.HolderBase<?> data) {
		return new RenderLayer((net.minecraft.client.renderer.rendertype.RenderType) data.data);
	}

	@MappedMethod
	public static boolean isInstance(org.mtr.mapping.tool.HolderBase<?> data) {
		return data != null && data.data instanceof net.minecraft.client.renderer.rendertype.RenderType;
	}

	@Nonnull
	@MappedMethod
	public static RenderLayer getCutout() {
		return new RenderLayer(net.minecraft.client.renderer.rendertype.RenderTypes.cutoutMovingBlock());
	}

	@Nonnull
	@MappedMethod
	public static RenderLayer getTranslucent() {
		return new RenderLayer(net.minecraft.client.renderer.rendertype.RenderTypes.translucentMovingBlock());
	}

	@Nonnull
	@MappedMethod
	public static RenderLayer getSolid() {
		return new RenderLayer(net.minecraft.client.renderer.rendertype.RenderTypes.solidMovingBlock());
	}

	@Nonnull
	@MappedMethod
	public static RenderLayer getCutoutMipped() {
		return getCutout();
	}
}
