package org.mtr.mapping.mapper;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.DummyClass;

public class GuiDrawing extends DummyClass {

	private final GuiGraphicsExtractor drawContext;
	private Identifier texture;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
		this.drawContext = graphicsHolder.drawContext;
	}

	@Deprecated
	public GuiDrawing(GuiGraphicsExtractor drawContext) {
		this.drawContext = drawContext;
	}

	@MappedMethod
	public void beginDrawingRectangle() {
	}

	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (drawContext != null) {
			// Color.HSBtoRGB / RGB-only values have alpha 0; 26.1 fill treats that as invisible.
			drawContext.fill((int) x1, (int) y1, (int) x2, (int) y2, withOpaqueAlpha(color));
		}
	}

	/**
	 * Vertical gradient (colorTop at y1 → colorBottom at y2). Prefer this over per-pixel
	 * {@link #drawRectangle} for large gradients (e.g. color picker).
	 */
	@MappedMethod
	public void drawRectangleGradient(double x1, double y1, double x2, double y2, int colorTop, int colorBottom) {
		if (drawContext != null) {
			drawContext.fillGradient((int) x1, (int) y1, (int) x2, (int) y2, withOpaqueAlpha(colorTop), withOpaqueAlpha(colorBottom));
		}
	}

	/** Quad with four corners (used by driving HUD). Approximate as axis-aligned bounds for now. */
	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, int color) {
		final double minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
		final double maxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
		final double minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
		final double maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
		drawRectangle(minX, minY, maxX, maxY, color);
	}

	private static int withOpaqueAlpha(int color) {
		return (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;
	}

	@MappedMethod
	public void finishDrawingRectangle() {
	}

	@MappedMethod
	public void beginDrawingTexture(Identifier identifier) {
		this.texture = identifier;
	}

	@MappedMethod
	public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {
		if (drawContext != null && texture != null) {
			drawContext.blit(texture.data, (int) x1, (int) y1, (int) x2, (int) y2, u1, v1, u2, v2);
		}
	}

	@MappedMethod
	public void finishDrawingTexture() {
		texture = null;
	}
}
