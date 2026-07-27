package org.mtr.mapping.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.resources.Identifier;

public final class CreativeModeTabHolder {

	public final CreativeModeTab creativeModeTab;
	public final Identifier identifier;

	public CreativeModeTabHolder(CreativeModeTab creativeModeTab, Identifier identifier) {
		this.creativeModeTab = creativeModeTab;
		this.identifier = identifier;
	}
}
