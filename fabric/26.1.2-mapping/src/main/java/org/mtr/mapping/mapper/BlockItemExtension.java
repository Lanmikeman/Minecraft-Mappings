package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import javax.annotation.Nonnull;

public class BlockItemExtension extends BlockItemAbstractMapping {

	@MappedMethod
	public BlockItemExtension(Block block, ItemSettings itemSettings) {
		super(block, itemSettings);
	}

	@MappedMethod
	public void useWithoutResult(World world, PlayerEntity user, Hand hand) {
	}

	@Deprecated
	@Override
	public final ActionResult use2(World world, PlayerEntity user, Hand hand) {
		useWithoutResult(world, user, hand);
		return super.use2(world, user, hand);
	}

	@MappedMethod
	public boolean hasGlint2(ItemStack stack) {
		return super.isFoil2(stack);
	}

	@Deprecated
	@Override
	public boolean isFoil2(ItemStack stack) {
		return hasGlint2(stack);
	}

	@Nonnull
	@MappedMethod
	public ActionResult useOnBlock2(ItemUsageContext context) {
		return super.useOn2(context);
	}

	@Deprecated
	@Override
	public ActionResult useOn2(ItemUsageContext context) {
		return useOnBlock2(context);
	}
}
