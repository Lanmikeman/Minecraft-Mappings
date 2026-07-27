package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class ClickableWidgetExtension extends ClickableWidgetAbstractMapping {
	@MappedMethod
	public ClickableWidgetExtension(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}

	@MappedMethod
	public void renderButton(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {}
}
