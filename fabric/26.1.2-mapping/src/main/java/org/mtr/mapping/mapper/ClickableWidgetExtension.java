package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

/**
 * Yarn-facing mouse/render hooks. MC 26.1 uses {@link net.minecraft.client.input.MouseButtonEvent};
 * MTR still overrides the older double/int signatures.
 */
public abstract class ClickableWidgetExtension extends ClickableWidgetAbstractMapping {

	@MappedMethod
	public ClickableWidgetExtension(int x, int y, int width, int height) {
		this(x, y, width, height, TextHelper.literal(""));
	}

	@MappedMethod
	public ClickableWidgetExtension(int x, int y, int width, int height, Text message) {
		super(x, y, width, height, message);
	}

	@MappedMethod
	public ClickableWidgetExtension(int x, int y, int width, int height, MutableText message) {
		super(x, y, width, height, new Text(message.data));
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
	}

	@MappedMethod
	public void renderButton(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		render(graphicsHolder, mouseX, mouseY, delta);
	}

	@MappedMethod
	public boolean mouseClicked2(double mouseX, double mouseY, int button) {
		return false;
	}

	@MappedMethod
	public boolean mouseReleased2(double mouseX, double mouseY, int button) {
		return false;
	}

	@MappedMethod
	public boolean mouseDragged2(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return false;
	}

	@MappedMethod
	public boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
		return false;
	}

	@Deprecated
	@Override
	public boolean mouseClicked2(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
		if (mouseClicked2(event.x(), event.y(), event.button())) {
			return true;
		}
		return super.mouseClicked2(event, doubleClick);
	}

	@Deprecated
	@Override
	public boolean mouseReleased2(net.minecraft.client.input.MouseButtonEvent event) {
		if (mouseReleased2(event.x(), event.y(), event.button())) {
			return true;
		}
		return super.mouseReleased2(event);
	}

	@Deprecated
	@Override
	public boolean mouseDragged2(net.minecraft.client.input.MouseButtonEvent event, double dx, double dy) {
		if (mouseDragged2(event.x(), event.y(), event.button(), dx, dy)) {
			return true;
		}
		return super.mouseDragged2(event, dx, dy);
	}

	@Deprecated
	@Override
	public boolean mouseScrolled2(double x, double y, double scrollX, double scrollY) {
		if (mouseScrolled2(x, y, scrollY)) {
			return true;
		}
		return super.mouseScrolled2(x, y, scrollX, scrollY);
	}

	@MappedMethod
	public final int getX2() {
		return super.getX2();
	}

	@MappedMethod
	public final int getY2() {
		return super.getY2();
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
	public final void setWidth2(int width) {
		super.setWidth2(width);
	}

	@MappedMethod
	public final void setHeight2(int height) {
		super.setHeight2(height);
	}

	@MappedMethod
	public void setMessage2(Text message) {
		super.setMessage2(message);
	}

	@MappedMethod
	public void setMessage2(MutableText message) {
		super.setMessage2(message == null ? new Text(net.minecraft.network.chat.Component.empty()) : new Text(message.data));
	}

	@Override
	protected void updateWidgetNarration2(net.minecraft.client.gui.narration.NarrationElementOutput output) {
		defaultButtonNarrationText2(output);
	}

	@Override
	protected void extractWidgetRenderState2(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(graphics, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}
}
