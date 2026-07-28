package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.tool.TextCase;

import javax.annotation.Nullable;
import javax.annotation.RegEx;
import java.util.function.Consumer;

public class TextFieldWidgetExtension extends TextFieldWidgetAbstractMapping {

	private final int maxLength;
	private final TextCase textCase;
	private final String filter;
	private final String suggestion;

	@MappedMethod
	public TextFieldWidgetExtension(int x, int y, int width, int height, int maxLength, TextCase textCase, @RegEx @Nullable String filter, @Nullable String suggestion) {
		this(x, y, width, height, "", maxLength, textCase, filter, suggestion);
	}

	@MappedMethod
	public TextFieldWidgetExtension(int x, int y, int width, int height, String message, int maxLength, TextCase textCase, @RegEx @Nullable String filter, @Nullable String suggestion) {
		this(x, y, width, height, TextHelper.literal(message), maxLength, textCase, filter, suggestion);
	}

	@MappedMethod
	public TextFieldWidgetExtension(int x, int y, int width, int height, MutableText text, int maxLength, TextCase textCase, @RegEx @Nullable String filter, @Nullable String suggestion) {
		super(new TextRenderer(MinecraftClient.getInstance().data.font), x, y, width, height, new Text(text.data));
		this.maxLength = maxLength;
		this.textCase = textCase;
		this.filter = filter;
		this.suggestion = suggestion;
		setChangedListener2(value -> {
		});
		setMaxLength2(0);
	}

	@MappedMethod
	public TextFieldWidgetExtension(int x, int y, int width, int height, Text message) {
		this(x, y, width, height, message == null ? "" : message.getString(), 256, TextCase.DEFAULT, null, null);
	}

	@MappedMethod
	public String getText2() {
		return super.getValue2();
	}

	@MappedMethod
	public void setText2(String text) {
		super.setValue2(text == null ? "" : text);
	}

	@MappedMethod
	public void setChangedListener2(Consumer<String> changedListener) {
		super.setResponder2(text -> {
			final String newText;
			if (filter == null || filter.isEmpty()) {
				newText = trySetLength(textCase.convert.apply(text));
			} else {
				newText = trySetLength(textCase.convert.apply(text).replaceAll(filter, ""));
				if (!newText.equals(text)) {
					setText2(newText);
				}
			}
			setSuggestion2(newText.isEmpty() && suggestion != null ? suggestion : "");
			if (changedListener != null) {
				changedListener.accept(newText);
			}
		});
	}

	/**
	 * AbstractMapping: {@code setMaxLength} → {@code setMaxLength2}. Must call {@code super.setMaxLength2}, not {@code setMaxLength}.
	 */
	@Deprecated
	@Override
	public final void setMaxLength2(int maxLength) {
		super.setMaxLength2(Integer.MAX_VALUE);
	}

	@MappedMethod
	public void tick2() {
		// EditBox tick removed / no-op in 26.x UI pipeline
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

	private String trySetLength(String text) {
		return text.isEmpty() ? "" : text.substring(0, Math.min(this.maxLength <= 0 ? Integer.MAX_VALUE : this.maxLength, text.length()));
	}
}
