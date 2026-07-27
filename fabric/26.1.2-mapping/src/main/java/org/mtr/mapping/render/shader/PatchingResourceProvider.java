package org.mtr.mapping.render.shader;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.util.Optional;

public final class PatchingResourceProvider implements ResourceProvider {
	private final ResourceProvider upstream;
	public PatchingResourceProvider(ResourceProvider upstream) { this.upstream = upstream; }
	@Override public Optional<Resource> getResource(Identifier id) { return upstream.getResource(id); }
}
