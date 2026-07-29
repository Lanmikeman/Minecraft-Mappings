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

	/** Quad with four corners (used by driving HUD). Draw as two triangles via GUI fill strips when axis-aligned; otherwise approximate with AABB fill + note that 26.1 GuiGraphicsExtractor has no free quads. */
	@MappedMethod
	public void drawRectangle(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, int color) {
		if (drawContext == null) {
			return;
		}
		// Fast path: axis-aligned rectangle
		if (almostEqual(y1, y2) && almostEqual(y3, y4) && almostEqual(x1, x4) && almostEqual(x2, x3)) {
			drawRectangle(Math.min(x1, x2), Math.min(y1, y3), Math.max(x1, x2), Math.max(y1, y3), color);
			return;
		}
		if (almostEqual(x1, x2) && almostEqual(x3, x4) && almostEqual(y1, y4) && almostEqual(y2, y3)) {
			drawRectangle(Math.min(x1, x3), Math.min(y1, y2), Math.max(x1, x3), Math.max(y1, y2), color);
			return;
		}
		// Skewed quad: scanline-fill between edges (good enough for driving HUD wedges)
		final double minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
		final double maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
		final int opaque = withOpaqueAlpha(color);
		final int yStart = (int) Math.floor(minY);
		final int yEnd = (int) Math.ceil(maxY);
		for (int y = yStart; y < yEnd; y++) {
			final double yt = y + 0.5;
			double xMin = Double.POSITIVE_INFINITY;
			double xMax = Double.NEGATIVE_INFINITY;
			xMin = updateMin(xMin, edgeX(x1, y1, x2, y2, yt));
			xMax = updateMax(xMax, edgeX(x1, y1, x2, y2, yt));
			xMin = updateMin(xMin, edgeX(x2, y2, x3, y3, yt));
			xMax = updateMax(xMax, edgeX(x2, y2, x3, y3, yt));
			xMin = updateMin(xMin, edgeX(x3, y3, x4, y4, yt));
			xMax = updateMax(xMax, edgeX(x3, y3, x4, y4, yt));
			xMin = updateMin(xMin, edgeX(x4, y4, x1, y1, yt));
			xMax = updateMax(xMax, edgeX(x4, y4, x1, y1, yt));
			if (xMin < xMax) {
				drawContext.fill((int) Math.floor(xMin), y, (int) Math.ceil(xMax), y + 1, opaque);
			}
		}
	}

	private static boolean almostEqual(double a, double b) {
		return Math.abs(a - b) < 0.01;
	}

	private static double updateMin(double current, double value) {
		return Double.isFinite(value) ? Math.min(current, value) : current;
	}

	private static double updateMax(double current, double value) {
		return Double.isFinite(value) ? Math.max(current, value) : current;
	}

	/** Intersection X of horizontal line y=yt with segment (xA,yA)-(xB,yB), or NaN if none. */
	private static double edgeX(double xA, double yA, double xB, double yB, double yt) {
		if ((yA > yt && yB > yt) || (yA < yt && yB < yt) || Math.abs(yA - yB) < 1.0E-6) {
			return Double.NaN;
		}
		final double t = (yt - yA) / (yB - yA);
		if (t < 0 || t > 1) {
			return Double.NaN;
		}
		return xA + t * (xB - xA);
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
