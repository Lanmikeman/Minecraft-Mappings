package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class ScreenExtension extends ScreenAbstractMapping {
	@MappedMethod
	public ScreenExtension(Text title) {
		super(title);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {}

	@MappedMethod
	public void init2() {}
}
