package org.mtr.mapping.render.obj;

import org.mtr.mapping.holder.Identifier;

public final class AtlasSprite {
	public final Identifier identifier;
	public AtlasSprite(Identifier identifier) { this.identifier = identifier; }
	public float getU(float u) { return u; }
	public float getV(float v) { return v; }
}
