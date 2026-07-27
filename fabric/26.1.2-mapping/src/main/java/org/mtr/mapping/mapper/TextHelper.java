package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.MutableText;
import org.mtr.mapping.holder.OrderedText;
import org.mtr.mapping.holder.Style;
import org.mtr.mapping.tool.DummyClass;

public final class TextHelper extends DummyClass {

	@MappedMethod
	public static MutableComponent translatable(String key, Object... arguments) {
		return new MutableComponent(Component.translatable(key, arguments));
	}

	@MappedMethod
	public static MutableComponent literal(String key) {
		return new MutableComponent(Component.literal(key));
	}

	@MappedMethod
	public static MutableComponent setStyle(MutableComponent mutableText, Style style) {
		return new MutableComponent(mutableText.data.setStyle(style.data));
	}

	@MappedMethod
	public static FormattedCharSequence mutableTextToOrderedText(MutableComponent mutableText) {
		return new FormattedCharSequence(mutableText.data.asOrderedText());
	}

	@MappedMethod
	public static MutableComponent append(MutableComponent baseText, MutableComponent... moreText) {
		net.minecraft.network.chat.MutableComponent result = baseText.data;
		for (final MutableComponent mutableText : moreText) {
			result = result.append(mutableText.data);
		}
		return new MutableComponent(result);
	}
}
