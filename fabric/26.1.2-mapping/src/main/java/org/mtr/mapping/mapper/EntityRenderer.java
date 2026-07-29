package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Frustum;
import org.mtr.mapping.holder.Identifier;

public abstract class EntityRenderer<T extends EntityExtension> extends net.minecraft.client.renderer.entity.EntityRenderer<T, EntityRenderer.MtrEntityRenderState<T>> {

	@MappedMethod
	public EntityRenderer(Argument argument) {
		super(argument.data);
	}

	@Deprecated
	@Override
	public MtrEntityRenderState<T> createRenderState() {
		return new MtrEntityRenderState<>();
	}

	@Deprecated
	@Override
	public void extractRenderState(T entity, MtrEntityRenderState<T> state, float tickDelta) {
		super.extractRenderState(entity, state, tickDelta);
		state.entity = entity;
		state.tickDelta = tickDelta;
		state.yaw = entity.getYRot();
	}

	@Deprecated
	@Override
	public void submit(MtrEntityRenderState<T> state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
		if (state.entity == null) {
			return;
		}
		// Iris shadow pass uses light-space matrices; player-camera lag correction creates ghosts.
		if (OptimizedRenderer.renderingShadows()) {
			return;
		}
		// Cancel any residual entity-vs-camera lag in the PoseStack so world-anchored
		// geometry (rails/vehicles) stays locked to the live camera origin.
		final net.minecraft.world.phys.Vec3 cameraPos = camera.pos;
		matrices.pushPose();
		matrices.translate(cameraPos.x - state.x, cameraPos.y - state.y, cameraPos.z - state.z);
		try {
			// Do not endBatch here — flushing mid entity/shadow pass causes z-fight/artifacts with Iris/Sodium.
			final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
			GraphicsHolder.createInstanceSafe(matrices, bufferSource, graphicsHolder -> render(state.entity, state.yaw, state.tickDelta, graphicsHolder, state.lightCoords));
		} finally {
			matrices.popPose();
		}
	}

	@MappedMethod
	public abstract void render(T entity, float yaw, float tickDelta, GraphicsHolder graphicsHolder, int light);

	@MappedMethod
	public abstract Identifier getTexture2(T entity);

	@Deprecated
	@Override
	public boolean shouldRender(T entity, net.minecraft.client.renderer.culling.Frustum frustum, double x, double y, double z) {
		return shouldRender2(entity, new Frustum(frustum), x, y, z);
	}

	@MappedMethod
	public boolean shouldRender2(T entity, Frustum frustum, double x, double y, double z) {
		return super.shouldRender(entity, frustum.data, x, y, z);
	}

	@Deprecated
	public static final class Argument {
		final EntityRendererProvider.Context data;

		public Argument(EntityRendererProvider.Context data) {
			this.data = data;
		}
	}

	@Deprecated
	public static final class MtrEntityRenderState<T extends EntityExtension> extends EntityRenderState {
		public T entity;
		public float tickDelta;
		public float yaw;
	}
}
