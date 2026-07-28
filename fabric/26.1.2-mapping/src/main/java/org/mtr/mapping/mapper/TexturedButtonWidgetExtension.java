package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

/**
 * Yarn-facing textured button API for MC 26.1 (WidgetSprites / ImageButton).
 */
public class TexturedButtonWidgetExtension extends TexturedButtonWidgetAbstractMapping {

	@MappedMethod
	public TexturedButtonWidgetExtension(int x, int y, int width, int height, Identifier normalTexture, Identifier highlightedTexture, Identifier disabledTexture, PressAction onPress) {
		this(x, y, width, height, normalTexture, highlightedTexture, disabledTexture, onPress, "");
	}

	@MappedMethod
	public TexturedButtonWidgetExtension(int x, int y, int width, int height, Identifier normalTexture, Identifier highlightedTexture, Identifier disabledTexture, PressAction onPress, String message) {
		this(x, y, width, height, normalTexture, highlightedTexture, disabledTexture, onPress, TextHelper.literal(message));
	}

	@MappedMethod
	public TexturedButtonWidgetExtension(int x, int y, int width, int height, Identifier normalTexture, Identifier highlightedTexture, Identifier disabledTexture, PressAction onPress, MutableText message) {
		super(x, y, width, height, spritesOf(normalTexture, highlightedTexture, disabledTexture), onPress, new Text(message.data));
	}

	@MappedMethod
	public void render(GraphicsHolder graphicsHolder, int mouseX, int mouseY, float delta) {
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

	private static net.minecraft.client.gui.components.WidgetSprites spritesOf(Identifier normal, Identifier highlighted, Identifier disabled) {
		return new net.minecraft.client.gui.components.WidgetSprites(format(normal), format(disabled), format(highlighted));
	}

	private static net.minecraft.resources.Identifier format(Identifier identifier) {
		final String beginning = "textures/gui/sprites/";
		final String path = identifier.data.getPath();
		final String trimmed = path.startsWith(beginning) ? path.substring(beginning.length()) : path;
		final String noExt = trimmed.endsWith(".png") ? trimmed.substring(0, trimmed.length() - 4) : trimmed;
		return net.minecraft.resources.Identifier.fromNamespaceAndPath(identifier.data.getNamespace(), noExt);
	}
}
