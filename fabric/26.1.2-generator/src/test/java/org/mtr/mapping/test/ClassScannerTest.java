package org.mtr.mapping.test;

// Minimal scanner for Minecraft 26.1.2 (official/unobfuscated names).
// Full Yarn-based scanner saved as ClassScannerTest.java.yarn-1.20.4.bak
// Expand put() list incrementally as generator pipeline is restored.

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.junit.jupiter.api.Test;

public final class ClassScannerTest {

	@Test
	public void scan() {
		final ClassScannerBase scanner = ClassScannerBase.getInstance();
		// mappedName (stable MTR API) -> Mojang class
		scanner.put("MinecraftClient", Minecraft.class);
		scanner.put("MinecraftServer", MinecraftServer.class);
		scanner.put("BlockPos", BlockPos.class);
		scanner.put("Direction", Direction.class);
		scanner.put("Identifier", Identifier.class);
		scanner.put("ServerWorld", ServerLevel.class);
		scanner.put("ServerPlayerEntity", ServerPlayer.class);
		scanner.put("ActionResult", InteractionResult.class);
		scanner.put("Entity", Entity.class);
		scanner.put("PlayerEntity", Player.class);
		scanner.put("Item", Item.class);
		scanner.put("ItemStack", ItemStack.class);
		scanner.put("World", Level.class);
		scanner.put("Block", Block.class);
		scanner.put("Blocks", Blocks.class);
		scanner.put("BlockState", BlockState.class);
		scanner.generate();
	}
}
