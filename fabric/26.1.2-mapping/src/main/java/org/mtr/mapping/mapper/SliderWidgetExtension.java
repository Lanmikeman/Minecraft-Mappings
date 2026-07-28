package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class SliderWidgetExtension extends SliderWidgetAbstractMapping {

	@MappedMethod
	public SliderWidgetExtension(int x, int y, int width, int height) {
		this(x, y, width, height, new Text(TextHelper.literal("").data), 0);
	}

	@MappedMethod
	public SliderWidgetExtension(int x, int y, int width, int height, Text message, double initialValue) {
		super(x, y, width, height, message, initialValue);
	}

	@MappedMethod
	protected void updateMessage2() {
	}

	@MappedMethod
	protected void applyValue2() {
	}

	/** Yarn-era mouse hooks used by MTR; 26.1 uses MouseButtonEvent on the abstract mapping. */
	@MappedMethod
	public void onClick2(double mouseX, double mouseY) {
	}

	@MappedMethod
	protected void onDrag2(double mouseX, double mouseY, double dx, double dy) {
	}

	@Deprecated
	@Override
	public void onClick2(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
		onClick2(event.x(), event.y());
		super.onClick2(event, doubleClick);
	}

	@Deprecated
	@Override
	protected void onDrag2(net.minecraft.client.input.MouseButtonEvent event, double dx, double dy) {
		onDrag2(event.x(), event.y(), dx, dy);
		super.onDrag2(event, dx, dy);
	}

	@MappedMethod
	public final void setX2(int x) {
		super.setX2(x);
	}

	@MappedMethod
	public final void setY2(int y) {
		super.setY2(y);
	}

	@MappedMethod
	public void setWidth2(int width) {
		super.setWidth2(width);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.drawContext != null) {
			super.extractWidgetRenderState2(graphicsHolder.drawContext, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public void extractWidgetRenderState2(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(graphics, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}
}
