package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class SliderWidgetExtension extends SliderWidgetAbstractMapping {
	@MappedMethod
	public SliderWidgetExtension(int x, int y, int width, int height, Text message, double initialValue) {
		super(x, y, width, height, message, initialValue);
	}

	@MappedMethod
	public void updateMessage2() {}

	@MappedMethod
	public void applyValue2() {}
}
