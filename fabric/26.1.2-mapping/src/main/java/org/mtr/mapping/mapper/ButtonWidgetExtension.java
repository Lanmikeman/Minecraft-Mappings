package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public class ButtonWidgetExtension extends ButtonWidgetAbstractMapping {

	@MappedMethod
	public ButtonWidgetExtension(int x, int y, int width, int height, PressAction onPress) {
		this(x, y, width, height, TextHelper.literal(""), onPress);
	}

	@MappedMethod
	public ButtonWidgetExtension(int x, int y, int width, int height, String message, PressAction onPress) {
		this(x, y, width, height, TextHelper.literal(message), onPress);
	}

	@MappedMethod
	public ButtonWidgetExtension(int x, int y, int width, int height, MutableText message, PressAction onPress) {
		this(x, y, width, height, new Text(message.data), onPress);
	}

	@MappedMethod
	public ButtonWidgetExtension(int x, int y, int width, int height, Text message, PressAction onPress) {
		super(x, y, width, height, message, onPress, net.minecraft.client.gui.components.Button.DEFAULT_NARRATION);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.drawContext != null) {
			extractDefaultSprite(graphicsHolder.drawContext);
			extractDefaultLabel(graphicsHolder.drawContext.textRendererForWidget(this, net.minecraft.client.gui.GuiGraphicsExtractor.HoveredTextEffects.NONE));
		}
	}

	@MappedMethod
	public void renderButton(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		render(graphicsHolder, mouseX, mouseY, delta);
	}

	/**
	 * Default: run the {@link PressAction} from the constructor.
	 * Subclasses (e.g. WidgetColorSelector) may override this instead.
	 */
	@MappedMethod
	public void onPress2() {
		getOnPressMapped().onPress(this);
	}

	@Deprecated
	@Override
	public void onPress2(net.minecraft.client.input.InputWithModifiers input) {
		onPress2();
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
	public void setMessage2(Text message) {
		super.setMessage2(message);
	}

	@MappedMethod
	public void setMessage2(MutableText message) {
		super.setMessage2(message == null ? new Text(net.minecraft.network.chat.Component.empty()) : new Text(message.data));
	}

	@Deprecated
	@Override
	protected void extractContents(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(graphics, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}
}
