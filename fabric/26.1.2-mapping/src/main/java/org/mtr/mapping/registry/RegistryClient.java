package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class RegistryClient extends DummyClass {

	public static Function<World, ? extends EntityExtension> worldRenderingEntity;
	public final EventRegistryClient eventRegistryClient = new EventRegistryClient();
	private final Registry registry;
	private final List<Runnable> objectsToRegister = new ArrayList<>();

	/** Blocks that need translucent chunk section rendering in 26.1 (data-driven via model textures). */
	private static final Set<Block> TRANSLUCENT_BLOCKS = ConcurrentHashMap.newKeySet();

	@MappedMethod
	public RegistryClient(Registry registry) {
		this.registry = registry;
	}

	@MappedMethod
	public void init() {
		objectsToRegister.forEach(Runnable::run);
	}

	@MappedMethod
	public <T extends BlockEntityTypeRegistryObject<U>, U extends BlockEntityExtension> void registerBlockEntityRenderer(T blockEntityType, Function<BlockEntityRenderer.Argument, BlockEntityRenderer<U>> rendererInstance) {
		objectsToRegister.add(() -> net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(blockEntityType.get().data, context -> rendererInstance.apply(new BlockEntityRenderer.Argument(context))));
	}

	@MappedMethod
	public <T extends EntityTypeRegistryObject<U>, U extends EntityExtension> void registerEntityRenderer(T entityType, Function<EntityRenderer.Argument, EntityRenderer<U>> rendererInstance) {
		objectsToRegister.add(() -> EntityRendererRegistry.register(entityType.get().data, dispatcher -> rendererInstance.apply(new EntityRenderer.Argument(dispatcher))));
	}

	@MappedMethod
	public void registerParticleRenderer(ParticleTypeRegistryObject particleTypeRegistryObject, Function<SpriteProvider, ParticleFactoryExtension> factory) {
		objectsToRegister.add(() -> ParticleProviderRegistry.getInstance().register(
				particleTypeRegistryObject.get().data,
				spriteSet -> factory.apply(new SpriteProvider(spriteSet))
		));
	}

	@MappedMethod
	public void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject block) {
		objectsToRegister.add(() -> {
			// MC 26.1 dropped BlockRenderLayerMap. Cutout is automatic for alpha textures.
			// Translucent needs force_translucent on model textures — track blocks for tooling/debug.
			if (isTranslucent(renderLayer)) {
				TRANSLUCENT_BLOCKS.add(block.get().data);
			}
		});
	}

	@MappedMethod
	public KeyBinding registerKeyBinding(String translationKey, int key, String categoryKey) {
		final KeyMapping.Category category = new KeyMapping.Category(Identifier.parse(categoryKey.contains(":") ? categoryKey : "mtr:" + categoryKey));
		return new KeyBinding(KeyMappingHelper.registerKeyMapping(new KeyMapping(translationKey, InputConstants.Type.KEYSYM, key, category)));
	}

	@MappedMethod
	public void registerBlockColors(BlockColorProvider blockColorProvider, BlockRegistryObject... blocks) {
		final Block[] nativeBlocks = new Block[blocks.length];
		for (int i = 0; i < blocks.length; i++) {
			nativeBlocks[i] = blocks[i].get().data;
		}
		BlockColorRegistry.register((state, getter, pos, colors) -> {
			// Collect tint indices 0..n until provider returns a sentinel or we fill a small range.
			for (int tintIndex = 0; tintIndex < 8; tintIndex++) {
				final int color = blockColorProvider.getColor2(
						new BlockState(state),
						getter == null ? null : new BlockRenderView(getter),
						pos == null ? null : new BlockPos(pos),
						tintIndex
				);
				colors.add(color);
			}
		}, nativeBlocks);
	}

	@MappedMethod
	public void registerItemColors(ItemColorProvider itemColorProvider, ItemRegistryObject... items) {
		// MC 26.1 item tinting is codec/JSON ItemTintSource based — no runtime ItemColor registry.
		// Keep API for MTR compile; item model JSON tint sources needed for visual parity.
	}

	@MappedMethod
	public void registerItemModelPredicate(ItemRegistryObject item, org.mtr.mapping.holder.Identifier identifier, ModelPredicateProvider modelPredicateProvider) {
		// MC 26.1 replaced ModelPredicateProviderRegistry with item model select/range_dispatch JSON.
		// Keep API for MTR compile; migrate connector items to range_select models separately.
	}

	@MappedMethod
	public void setupPackets(org.mtr.mapping.holder.Identifier identifier) {
		if (registry.payloadType == null) {
			registry.setupPackets(identifier);
		}
		ClientPlayNetworking.registerGlobalReceiver(registry.payloadType, (payload, context) -> {
			final FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(payload.bytes()));
			PacketBufferReceiver.receive(buf, packetBufferReceiver -> {
				final Function<PacketBufferReceiver, ? extends PacketHandler> getInstance = registry.packets.get(packetBufferReceiver.readString());
				if (getInstance != null) {
					getInstance.apply(packetBufferReceiver).runClient();
				}
			}, context.client()::execute);
		});
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToServer(T data) {
		if (registry.packetsIdentifier != null && registry.payloadType != null) {
			final PacketBufferSender packetBufferSender = new PacketBufferSender(() -> new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer()));
			packetBufferSender.writeString(data.getClass().getName());
			data.write(packetBufferSender);
			packetBufferSender.send(byteBuf -> {
				final byte[] bytes = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(bytes);
				ClientPlayNetworking.send(new Registry.MtrPayload(registry.payloadType, bytes));
			}, MinecraftClient.getInstance()::execute);
		}
	}

	/** Exposed for resource tooling / tests. */
	public static Set<Block> getTranslucentBlocks() {
		return TRANSLUCENT_BLOCKS;
	}

	private static boolean isTranslucent(RenderLayer renderLayer) {
		try {
			return renderLayer.data.hasBlending() || renderLayer.data.toString().toLowerCase().contains("translucent");
		} catch (Exception e) {
			return false;
		}
	}

	@FunctionalInterface
	public interface ModelPredicateProvider {
		@MappedMethod
		float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity);
	}
}
