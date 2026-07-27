package org.mtr.mapping.mapper;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Vector3d;

public abstract class BlockEntityRenderer<T extends BlockEntityExtension> implements net.minecraft.client.renderer.blockentity.BlockEntityRenderer<T, BlockEntityRenderState> {

	@MappedMethod
	public BlockEntityRenderer(Argument argument) {
	}

	@Deprecated
	@Override
	public BlockEntityRenderState createRenderState() {
		return new BlockEntityRenderState();
	}

	@Deprecated
	@Override
	public void submit(BlockEntityRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState camera) {
		// TODO 26.1: bridge to GraphicsHolder-based render(entity, ...)
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
}
