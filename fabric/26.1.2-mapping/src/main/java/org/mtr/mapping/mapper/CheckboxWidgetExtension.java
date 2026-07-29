package org.mtr.mapping.mapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import java.util.function.Consumer;

/**
 * Checkbox bridge for MC 26.1.2 — draws a visible box + label (vanilla Checkbox API
 * differs enough that a lightweight custom widget is more reliable for MTR screens).
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
		this.onPress = onPress == null ? ignored -> {
		} : onPress;
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
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		final GuiGraphicsExtractor drawContext = graphicsHolder.drawContext;
		if (drawContext == null || !visible) {
			return;
		}

		final int box = Math.min(getHeight2(), 20);
		final int x = getX2();
		final int y = getY2() + Math.max(0, (getHeight2() - box) / 2);
		final boolean hovered = mouseX >= getX2() && mouseY >= getY2() && mouseX < getX2() + getWidth2() && mouseY < getY2() + getHeight2();

		drawContext.fill(x, y, x + box, y + box, 0xFF000000);
		drawContext.fill(x + 1, y + 1, x + box - 1, y + box - 1, hovered ? 0xFFE0E0E0 : 0xFFFFFFFF);
		if (checked) {
			drawContext.fill(x + 4, y + 4, x + box - 4, y + box - 4, 0xFF222222);
		}

		if (showMessage) {
			final Font font = Minecraft.getInstance().font;
			final Component message = getMessage();
			drawContext.text(font, message, x + box + 4, y + (box - 8) / 2, 0xFFFFFF);
		}
	}

	@Override
	@MappedMethod
	public boolean mouseClicked2(double mouseX, double mouseY, int button) {
		if (!visible || !active || button != 0) {
			return false;
		}
		if (mouseX >= getX2() && mouseY >= getY2() && mouseX < getX2() + getWidth2() && mouseY < getY2() + getHeight2()) {
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
