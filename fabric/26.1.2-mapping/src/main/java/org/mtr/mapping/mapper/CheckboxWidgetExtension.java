package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;

/** Checkbox abstract holder pending regen — keep stable MappedMethod surface. */
public class CheckboxWidgetExtension {
	private boolean checked;
	@MappedMethod
	public CheckboxWidgetExtension(int x, int y, int width, int height, MutableText message, boolean checked) {
		this.checked = checked;
	}
	@MappedMethod public boolean isChecked() { return checked; }
	@MappedMethod public void setChecked(boolean checked) { this.checked = checked; }
	@MappedMethod public void renderButton(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {}
}
