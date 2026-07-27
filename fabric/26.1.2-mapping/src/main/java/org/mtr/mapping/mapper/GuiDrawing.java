package org.mtr.mapping.mapper;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.tool.DummyClass;

public class GuiDrawing extends DummyClass {
	private final GuiGraphicsExtractor drawContext;

	@MappedMethod
	public GuiDrawing(GraphicsHolder graphicsHolder) {
		this.drawContext = graphicsHolder.drawContext;
	}

	@Deprecated
	public GuiDrawing(GuiGraphicsExtractor drawContext) {
		this.drawContext = drawContext;
	}

	@MappedMethod public void beginDrawingRectangle() {}
	@MappedMethod public void drawRectangle(double x1, double y1, double x2, double y2, int color) {
		if (drawContext != null) drawContext.fill((int)x1,(int)y1,(int)x2,(int)y2,color);
	}
	@MappedMethod public void finishDrawingRectangle() {}
	@MappedMethod public void beginDrawingTexture(Identifier identifier) {}
	@MappedMethod public void drawTexture(double x1, double y1, double x2, double y2, float u1, float v1, float u2, float v2) {}
	@MappedMethod public void finishDrawingTexture() {}
}
