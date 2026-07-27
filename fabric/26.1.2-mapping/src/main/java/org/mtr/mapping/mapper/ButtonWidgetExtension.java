package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class ButtonWidgetExtension extends ButtonWidgetAbstractMapping {
	@MappedMethod
	public ButtonWidgetExtension(int x, int y, int width, int height, Text message, net.minecraft.client.gui.components.Button.OnPress onPress) {
		super(x, y, width, height, message, onPress, net.minecraft.client.gui.components.Button.DEFAULT_NARRATION);
	}

	@MappedMethod
	public void renderButton(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {}
}
