package org.mtr.mapping.registry;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.resources.Identifier;

public final class CreativeModeTabHolder {

	public final ItemGroup creativeModeTab;
	public final Identifier identifier;

	public CreativeModeTabHolder(ItemGroup creativeModeTab, Identifier identifier) {
		this.creativeModeTab = creativeModeTab;
		this.identifier = identifier;
	}
}
