package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import java.util.function.Consumer;

/**
 * Checkbox bridge for MC 26.1.2 — CheckboxWidgetAbstractMapping not regenerated yet,
 * so this extends {@link ClickableWidgetExtension} with the yarn-facing API MTR uses.
 */
public class CheckboxWidgetExtension extends ClickableWidgetExtension {

	private final Consumer<Boolean> onPress;
	private final boolean showMessage;
	private boolean checked;

	@MappedMethod
	public CheckboxWidgetExtension(int x, int y, int width, int height, boolean showMessage, Consumer<Boolean> onPress) {
		this(x, y, width, height, "", showMessage, onPress);
	}

	@MappedMethod
	public CheckboxWidgetExtension(int x, int y, int width, int height, String message, boolean showMessage, Consumer<Boolean> onPress) {
		this(x, y, width, height, TextHelper.literal(message), showMessage, onPress);
	}

	@MappedMethod
	public CheckboxWidgetExtension(int x, int y, int width, int height, MutableText message, boolean showMessage, Consumer<Boolean> onPress) {
		super(x, y, width, height, showMessage ? new Text(message.data) : new Text(TextHelper.literal("").data));
		this.showMessage = showMessage;
		this.onPress = onPress == null ? ignored -> {} : onPress;
		this.checked = false;
	}

	@MappedMethod
	public boolean isChecked() {
		return checked;
	}

	@MappedMethod
	public boolean isChecked2() {
		return checked;
	}

	@MappedMethod
	public final void setChecked(boolean checked) {
		if (checked != this.checked) {
			this.checked = checked;
			onPress.accept(this.checked);
		}
	}

	@MappedMethod
	public void renderButton(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		render(graphicsHolder, mouseX, mouseY, delta);
	}

	@Override
	@MappedMethod
	public boolean mouseClicked2(double mouseX, double mouseY, int button) {
		if (button == 0) {
			checked = !checked;
			onPress.accept(checked);
			return true;
		}
		return false;
	}

	@Override
	protected void updateWidgetNarration2(net.minecraft.client.gui.narration.NarrationElementOutput output) {
		defaultButtonNarrationText2(output);
	}
}
