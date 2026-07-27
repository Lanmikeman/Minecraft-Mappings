package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public class TextFieldWidgetExtension extends TextFieldWidgetAbstractMapping {
	@MappedMethod
	public TextFieldWidgetExtension(int x, int y, int width, int height, Text message) {
		super(new TextRenderer(MinecraftClient.getInstance().data.font), x, y, width, height, message);
	}

	@MappedMethod
	public String getText2() { return getValue(); }

	@MappedMethod
	public void setText2(String text) { setValue(text); }
}
