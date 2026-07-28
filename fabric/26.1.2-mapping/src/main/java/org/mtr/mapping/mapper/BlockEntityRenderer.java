package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Vector3d;

public abstract class BlockEntityRenderer<T extends BlockEntityExtension> implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<T, BlockEntityRenderer.MtrBlockEntityRenderState<T>> {

	@MappedMethod
	public BlockEntityRenderer(Argument argument) {
	}

	@Deprecated
	@Override
	public MtrBlockEntityRenderState<T> createRenderState() {
		return new MtrBlockEntityRenderState<>();
	}

	@Deprecated
	@Override
	public void extractRenderState(T entity, MtrBlockEntityRenderState<T> state, float tickDelta, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		net.minecraft.client.renderer.blockentity.BlockEntityRenderer.super.extractRenderState(entity, state, tickDelta, cameraPos, crumblingOverlay);
		state.entity = entity;
		state.tickDelta = tickDelta;
	}

	@Deprecated
	@Override
	public void submit(MtrBlockEntityRenderState<T> state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
		if (state.entity == null) {
			return;
		}
		// Do not endBatch here — flushing mid pass causes z-fight/artifacts with Iris/Sodium.
		final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		GraphicsHolder.createInstanceSafe(matrices, bufferSource, graphicsHolder -> render(state.entity, state.tickDelta, graphicsHolder, state.lightCoords, OverlayTexture.NO_OVERLAY));
	}

	@MappedMethod
	public abstract void render(T entity, float tickDelta, GraphicsHolder graphicsHolder, int light, int overlay);

	@MappedMethod
	public boolean rendersOutsideBoundingBox2(T blockEntity) {
		return shouldRenderOffScreen();
	}

	@MappedMethod
	public int getRenderDistance2() {
		return getViewDistance();
	}

	@MappedMethod
	public boolean isInRenderDistance(T blockEntity, Vector3d position) {
		return shouldRender(blockEntity, position.data);
	}

	@Deprecated
	public static final class Argument {
		final BlockEntityRendererProvider.Context data;

		public Argument(BlockEntityRendererProvider.Context data) {
			this.data = data;
		}
	}

	@Deprecated
	public static final class MtrBlockEntityRenderState<T extends BlockEntityExtension> extends BlockEntityRenderState {
		T entity;
		float tickDelta;
	}
}
