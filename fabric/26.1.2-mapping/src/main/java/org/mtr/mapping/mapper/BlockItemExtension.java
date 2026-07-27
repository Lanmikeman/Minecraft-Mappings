package org.mtr.mapping.mapper;

import net.minecraft.world.InteractionResult;

import net.minecraft.world.item.ItemStack;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

public class BlockItemExtension extends BlockItemAbstractMapping {

	@MappedMethod
	public BlockItemExtension(Block block, ItemSettings itemSettings) {
		super(block, itemSettings);
	}

	
	@MappedMethod
	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}
}
