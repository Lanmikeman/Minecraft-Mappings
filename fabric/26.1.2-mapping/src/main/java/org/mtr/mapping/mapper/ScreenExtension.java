package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public abstract class ScreenExtension extends ScreenAbstractMapping {

	@MappedMethod
	protected ScreenExtension() {
		this(new Text(TextHelper.literal("").data));
	}

	@MappedMethod
	protected ScreenExtension(String title) {
		this(new Text(TextHelper.literal(title).data));
	}

	@MappedMethod
	protected ScreenExtension(MutableText title) {
		this(new Text(title.data));
	}

	@MappedMethod
	protected ScreenExtension(Text title) {
		super(title);
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
		if (graphicsHolder.drawContext != null) {
			super.extractRenderState2(graphicsHolder.drawContext, mouseX, mouseY, delta);
		}
	}

	@Deprecated
	@Override
	public void extractRenderState2(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		GraphicsHolder.createInstanceSafe(graphics, graphicsHolder -> render(graphicsHolder, mouseX, mouseY, delta));
	}

	@MappedMethod
	public final void addChild(ClickableWidget child) {
		addRenderableWidget(child.data);
	}

	@MappedMethod
	public final void addSelectableChild(ClickableWidget child) {
		addWidget(child.data);
	}

	@MappedMethod
	public final void renderBackground(GraphicsHolder graphicsHolder) {
		// In 26.1, Screen.extractRenderStateWithTooltipAndSubtitles already calls
		// extractBackground (blur) before extractRenderState. Calling it again throws
		// "Can only blur once per frame" and aborts the rest of MTR custom drawing.
	}

	@MappedMethod
	public void tick2() {
		super.tick2();
	}

	@MappedMethod
	public void resize2(MinecraftClient client, int width, int height) {
		super.resize2(width, height);
	}

	/**
	 * Yarn-era hook. Default: not handled — event bridge falls through to {@link ScreenAbstractMapping}.
	 */
	@MappedMethod
	public boolean mouseScrolled2(double mouseX, double mouseY, double amount) {
		return false;
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
}
