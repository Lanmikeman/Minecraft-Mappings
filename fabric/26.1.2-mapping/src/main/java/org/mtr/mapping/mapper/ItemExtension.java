package org.mtr.mapping.mapper;

import net.minecraft.network.chat.Component;
import net.minecraft.util.TypedActionResult;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;

import javax.annotation.Nullable;
import java.util.List;

public class ItemExtension extends ItemAbstractMapping implements ItemHelper {

	public ItemExtension(ItemSettings itemSettings) {
		super(itemSettings);
	}

	@Deprecated
	@Override
	public final TypedActionResult<net.minecraft.world.item.ItemStack> use(net.minecraft.world.level.Level world, net.minecraft.world.entity.player.Player user, net.minecraft.world.InteractionHand hand) {
		useWithoutResult(new Level(world), new Player(user), InteractionHand.convert(hand));
		return super.use(world, user, hand);
	}

	@Deprecated
	@Override
	public final void appendTooltip(net.minecraft.world.item.ItemStack stack, @Nullable net.minecraft.world.level.Level world, List<Component> tooltip, net.minecraft.world.item.Item$TooltipContext context) {
		appendTooltipHelper(new ItemStack(stack), world == null ? null : new Level(world), tooltip, new TooltipContext(context));
	}

	@MappedMethod
	public void useWithoutResult(Level world, Player user, InteractionHand hand) {
	}
}
