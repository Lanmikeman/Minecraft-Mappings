package org.mtr.mapping.mapper;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.ColorHelper;
import org.mtr.mapping.tool.DummyClass;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class GraphicsHolder extends DummyClass {

	VertexConsumer vertexConsumer;
	private int matrixPushes;

	@Nullable
	final PoseStack matrixStack;
	@Nullable
	final MultiBufferSource vertexConsumerProvider;
	@Nullable
	final GuiGraphicsExtractor drawContext;

	@MappedMethod
	public static int getDefaultLight() {
		return 0xF000F0;
	}

	@Deprecated
	public static void createInstanceSafe(@Nullable PoseStack matrixStack, @Nullable MultiBufferSource vertexConsumerProvider, Consumer<GraphicsHolder> consumer) {
		final int poseDepth = getPoseDepth(matrixStack);
		createInstanceSafe(new GraphicsHolder(matrixStack, vertexConsumerProvider), consumer, poseDepth);
	}

	@Deprecated
	public static void createInstanceSafe(GuiGraphicsExtractor drawContext, Consumer<GraphicsHolder> consumer) {
		createInstanceSafe(new GraphicsHolder(drawContext), consumer, 0);
	}

	private static void createInstanceSafe(GraphicsHolder graphicsHolder, Consumer<GraphicsHolder> consumer, int poseDepth) {
		try {
			consumer.accept(graphicsHolder);
		} catch (Exception e) {
			logException(e);
		} finally {
			while (graphicsHolder.matrixPushes > 0) {
				graphicsHolder.pop();
			}
			// ModelPart.pushPose has no try/finally — restore after Sodium/Iris vertex errors
			restorePoseDepth(graphicsHolder.matrixStack, poseDepth);
		}
	}

	private static int getPoseDepth(@Nullable PoseStack poseStack) {
		if (poseStack == null) {
			return 0;
		}
		try {
			final Field lastIndex = PoseStack.class.getDeclaredField("lastIndex");
			lastIndex.setAccessible(true);
			return lastIndex.getInt(poseStack);
		} catch (ReflectiveOperationException e) {
			return poseStack.isEmpty() ? 0 : -1;
		}
	}

	private static void restorePoseDepth(@Nullable PoseStack poseStack, int targetDepth) {
		if (poseStack == null || targetDepth < 0) {
			return;
		}
		int depth = getPoseDepth(poseStack);
		while (depth > targetDepth) {
			poseStack.popPose();
			depth--;
		}
	}

	private GraphicsHolder(@Nullable PoseStack matrixStack, @Nullable MultiBufferSource vertexConsumerProvider) {
		this.matrixStack = matrixStack;
		this.vertexConsumerProvider = vertexConsumerProvider;
		this.drawContext = null;
		push();
	}

	private GraphicsHolder(GuiGraphicsExtractor drawContext) {
		// 26.1 GUI pose is Matrix3x2fStack, not PoseStack — keep 3D stack null for HUD path
		this.matrixStack = null;
		this.vertexConsumerProvider = null;
		this.drawContext = drawContext;
		push();
	}

	@MappedMethod
	public void push() {
		if (drawContext != null) {
			drawContext.pose().pushMatrix();
			matrixPushes++;
		} else if (matrixStack != null) {
			matrixStack.pushPose();
			matrixPushes++;
		}
	}

	@MappedMethod
	public void pop() {
		if (matrixPushes > 0) {
			if (drawContext != null) {
				drawContext.pose().popMatrix();
				matrixPushes--;
			} else if (matrixStack != null) {
				matrixStack.popPose();
				matrixPushes--;
			}
		}
	}

	@MappedMethod
	public void translate(double x, double y, double z) {
		if (drawContext != null) {
			drawContext.pose().translate((float) x, (float) y);
		} else if (matrixStack != null) {
			matrixStack.translate(x, y, z);
		}
	}

	@MappedMethod
	public void scale(float x, float y, float z) {
		if (drawContext != null) {
			drawContext.pose().scale(x, y);
		} else if (matrixStack != null) {
			matrixStack.scale(x, y, z);
		}
	}

	@MappedMethod
	public void rotateXRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.XP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateYRadians(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.YP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateZRadians(float angle) {
		if (drawContext != null) {
			drawContext.pose().rotate(angle);
		} else if (matrixStack != null) {
			matrixStack.mulPose(Axis.ZP.rotation(angle));
		}
	}

	@MappedMethod
	public void rotateXDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.XP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void rotateYDegrees(float angle) {
		if (matrixStack != null) {
			matrixStack.mulPose(Axis.YP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public void rotateZDegrees(float angle) {
		if (drawContext != null) {
			drawContext.pose().rotate((float) Math.toRadians(angle));
		} else if (matrixStack != null) {
			matrixStack.mulPose(Axis.ZP.rotationDegrees(angle));
		}
	}

	@MappedMethod
	public org.mtr.mapping.holder.Matrix4f copyPositionMatrix() {
		return matrixStack == null ? new org.mtr.mapping.holder.Matrix4f() : new org.mtr.mapping.holder.Matrix4f(matrixStack.last().pose());
	}

	@MappedMethod
	public void drawText(MutableText mutableText, int x, int y, int color, boolean shadow, int light) {
		if (drawContext != null) {
			drawContext.text(font(), mutableText.data, x, y, color, shadow);
		} else if (matrixStack != null && vertexConsumerProvider != null) {
			font().drawInBatch(mutableText.data, x, y, color, shadow, matrixStack.last().pose(), vertexConsumerProvider, Font.DisplayMode.NORMAL, 0, light);
		}
	}

	@MappedMethod
	public void drawText(OrderedText orderedText, int x, int y, int color, boolean shadow, int light) {
		if (drawContext != null) {
			drawContext.text(font(), orderedText.data, x, y, color, shadow);
		} else if (matrixStack != null && vertexConsumerProvider != null) {
			font().drawInBatch(orderedText.data, x, y, color, shadow, matrixStack.last().pose(), vertexConsumerProvider, Font.DisplayMode.NORMAL, 0, light);
		}
	}

	@MappedMethod
	public void drawText(String text, int x, int y, int color, boolean shadow, int light) {
		if (drawContext != null) {
			drawContext.text(font(), text, x, y, color, shadow);
		} else if (matrixStack != null && vertexConsumerProvider != null) {
			font().drawInBatch(text, x, y, color, shadow, matrixStack.last().pose(), vertexConsumerProvider, Font.DisplayMode.NORMAL, 0, light);
		}
	}

	@MappedMethod
	public void drawCenteredText(String text, int centerX, int y, int color) {
		if (drawContext != null) {
			drawContext.centeredText(font(), text, centerX, y, color);
		}
	}

	@MappedMethod
	public void drawCenteredText(MutableText text, int centerX, int y, int color) {
		if (drawContext != null) {
			drawContext.centeredText(font(), text.data, centerX, y, color);
		}
	}

	@MappedMethod
	public static int getTextWidth(MutableText mutableText) {
		return font().width(mutableText.data);
	}

	@MappedMethod
	public static int getTextWidth(OrderedText orderedText) {
		return font().width(orderedText.data);
	}

	@MappedMethod
	public static int getTextWidth(String text) {
		return font().width(text);
	}

	@MappedMethod
	public static List<OrderedText> wrapLines(MutableText mutableText, int width) {
		return font().split(mutableText.data, width).stream().map(OrderedText::new).collect(Collectors.toList());
	}

	private static Font font() {
		return MinecraftClient.getInstance().data.font;
	}

	private static MinecraftClient getInstance() {
		return MinecraftClient.getInstance();
	}

	@MappedMethod
	public void createVertexConsumer(RenderLayer renderLayer) {
		if (vertexConsumerProvider != null) {
			vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer.data);
		}
	}

	@MappedMethod
	public void drawLineInWorld(float x1, float y1, float z1, float x2, float y2, float z2, int color) {
		if (matrixStack != null && vertexConsumer != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				final PoseStack.Pose entry = matrixStack.last();
				// MC 26.1 line formats include LINE_WIDTH (see ShapeRenderer / POSITION_COLOR_NORMAL_LINE_WIDTH)
				vertexConsumer.addVertex(entry.pose(), x1, y1, z1).setColor(r, g, b, a).setNormal(entry, 0, 1, 0).setLineWidth(2.0F);
				vertexConsumer.addVertex(entry.pose(), x2, y2, z2).setColor(r, g, b, a).setNormal(entry, 0, 1, 0).setLineWidth(2.0F);
			});
		}
	}

	@MappedMethod
	public void drawTextureInWorld(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, Direction facing, int color, int light) {
		if (matrixStack != null && vertexConsumer != null) {
			ColorHelper.unpackColor(color, (a, r, g, b) -> {
				// Prefer geometric normal — gangways/barriers pass Direction.UP for vertical
				// faces, and 26.1 entity cardinal lighting turns those faces black.
				float nx = (y2 - y1) * (z3 - z1) - (z2 - z1) * (y3 - y1);
				float ny = (z2 - z1) * (x3 - x1) - (x2 - x1) * (z3 - z1);
				float nz = (x2 - x1) * (y3 - y1) - (y2 - y1) * (x3 - x1);
				final float lenSq = nx * nx + ny * ny + nz * nz;
				if (lenSq > 1.0E-12F) {
					final float inv = 1.0F / (float) Math.sqrt(lenSq);
					nx *= inv;
					ny *= inv;
					nz *= inv;
				} else {
					final Vector3i vector3i = facing.getVector();
					nx = vector3i.getX();
					ny = vector3i.getY();
					nz = vector3i.getZ();
				}
				final PoseStack.Pose entry = matrixStack.last();
				final Matrix4f matrix4f = entry.pose();
				vertexConsumer.addVertex(matrix4f, x1, y1, z1).setColor(r, g, b, a).setUv(u1, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, nx, ny, nz);
				vertexConsumer.addVertex(matrix4f, x2, y2, z2).setColor(r, g, b, a).setUv(u2, v2).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, nx, ny, nz);
				vertexConsumer.addVertex(matrix4f, x3, y3, z3).setColor(r, g, b, a).setUv(u2, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, nx, ny, nz);
				vertexConsumer.addVertex(matrix4f, x4, y4, z4).setColor(r, g, b, a).setUv(u1, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(entry, nx, ny, nz);
			});
		}
	}

	@MappedMethod
	public void renderEntity(Entity entity, double x, double y, double z, float yaw, float tickDelta, int light) {
		if (matrixStack == null || entity == null || entity.data == null) {
			return;
		}
		try {
			final MinecraftClient minecraft = MinecraftClient.getInstance();
			final net.minecraft.client.Minecraft mc = minecraft.data;
			final net.minecraft.client.renderer.entity.EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
			final net.minecraft.client.renderer.entity.state.EntityRenderState state = dispatcher.extractEntity(entity.data, tickDelta);
			final net.minecraft.client.renderer.state.level.CameraRenderState camera =
					mc.gameRenderer.getGameRenderState().levelRenderState.cameraRenderState;
			final net.minecraft.client.renderer.SubmitNodeCollector collector = getSubmitNodeCollector(mc.levelRenderer);
			if (collector == null) {
				return;
			}
			dispatcher.submit(state, camera, x, y, z, matrixStack, collector);
		} catch (Exception e) {
			logException(e);
		}
	}

	@Nullable
	private static net.minecraft.client.renderer.SubmitNodeCollector getSubmitNodeCollector(net.minecraft.client.renderer.LevelRenderer levelRenderer) {
		try {
			final java.lang.reflect.Field field = net.minecraft.client.renderer.LevelRenderer.class.getDeclaredField("submitNodeStorage");
			field.setAccessible(true);
			return (net.minecraft.client.renderer.SubmitNodeCollector) field.get(levelRenderer);
		} catch (ReflectiveOperationException e) {
			logException(e);
			return null;
		}
	}
}
