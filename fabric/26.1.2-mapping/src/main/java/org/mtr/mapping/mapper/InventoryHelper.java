package org.mtr.mapping.mapper;

import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.Inventory;
import org.mtr.mapping.holder.ItemStack;
import org.mtr.mapping.tool.DummyClass;

import java.util.List;
import java.util.function.Predicate;

public final class InventoryHelper extends DummyClass {

	@MappedMethod
	public static ItemStack splitStack(List<ItemStack> stacks, int slot, int amount) {
		if (slot < 0 || slot >= stacks.size()) {
			return new ItemStack(net.minecraft.world.item.ItemStack.EMPTY);
		}
		return new ItemStack(stacks.get(slot).data.split(amount));
	}

	@MappedMethod
	public static ItemStack removeStack(List<ItemStack> stacks, int slot) {
		if (slot < 0 || slot >= stacks.size()) {
			return new ItemStack(net.minecraft.world.item.ItemStack.EMPTY);
		}
		final ItemStack stack = stacks.get(slot);
		stacks.set(slot, new ItemStack(net.minecraft.world.item.ItemStack.EMPTY));
		return stack;
	}

	@MappedMethod
	public static int remove(Inventory inventory, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun) {
		return 0; // TODO container remove API
	}

	@MappedMethod
	public static int remove(ItemStack stack, Predicate<ItemStack> shouldRemove, int maxCount, boolean dryRun) {
		return 0;
	}
}
