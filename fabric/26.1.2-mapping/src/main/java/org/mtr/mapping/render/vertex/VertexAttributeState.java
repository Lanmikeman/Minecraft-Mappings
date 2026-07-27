package org.mtr.mapping.render.vertex;

import javax.annotation.Nullable;

public final class VertexAttributeState {
	@Nullable public final Integer color;
	@Nullable public final Integer light;
	public VertexAttributeState() { this(null, null); }
	public VertexAttributeState(@Nullable Integer color, @Nullable Integer light) {
		this.color = color; this.light = light;
	}
	public void apply() {}
	public void bind() {}
}
