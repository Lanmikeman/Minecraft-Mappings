package org.mtr.mapping.render.obj;

import org.mtr.mapping.holder.Identifier;

public final class AtlasManager {
	public AtlasSprite getAtlasSprite(Identifier identifier) { return new AtlasSprite(identifier); }
}
