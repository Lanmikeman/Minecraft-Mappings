package org.mtr.mapping.registry;

import org.mtr.mapping.holder.Identifier;
import org.mtr.mapping.holder.ItemGroup;

public final class CreativeModeTabHolder {

	public final ItemGroup creativeModeTab;
	public final Identifier identifier;

	public CreativeModeTabHolder(ItemGroup creativeModeTab, Identifier identifier) {
		this.creativeModeTab = creativeModeTab;
		this.identifier = identifier;
	}
}
