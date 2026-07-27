package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class TexturedButtonWidgetExtension extends TexturedButtonWidgetAbstractMapping {
	@MappedMethod
	public TexturedButtonWidgetExtension(int x, int y, int width, int height, net.minecraft.client.gui.components.WidgetSprites sprites, net.minecraft.client.gui.components.Button.OnPress onPress, Text message) {
		super(x, y, width, height, sprites, onPress, message);
	}
}
