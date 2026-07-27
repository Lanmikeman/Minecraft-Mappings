package org.mtr.mapping.holder;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.tool.DummyClass;

public final class Util extends DummyClass {

	private Util() {
	}

	@MappedMethod
	public static OperatingSystem getOperatingSystem() {
		return OperatingSystem.convert(net.minecraft.util.Util.getPlatform());
	}
}
