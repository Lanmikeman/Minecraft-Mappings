package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Direction;
import org.mtr.mapping.tool.DummyClass;

public final class DirectionHelper extends DummyClass {
	@MappedMethod
	public static Direction rotateYClockwise(Direction direction) {
		return Direction.convert(direction.data.getClockWise());
	}

	@MappedMethod
	public static Direction rotateYCounterclockwise(Direction direction) {
		return Direction.convert(direction.data.getCounterClockWise());
	}
}
