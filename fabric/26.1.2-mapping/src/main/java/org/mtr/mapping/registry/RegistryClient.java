package org.mtr.mapping.registry;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.*;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class RegistryClient extends DummyClass {

	public static Function<World, ? extends EntityExtension> worldRenderingEntity;
	public final EventRegistryClient eventRegistryClient = new EventRegistryClient();
	private final Registry registry;
	private final List<Runnable> objectsToRegister = new ArrayList<>();

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
		// TODO 26.1 particle factory registry
	}

	@MappedMethod
	public void registerBlockRenderType(RenderLayer renderLayer, BlockRegistryObject block) {
		objectsToRegister.add(() -> { /* TODO 26.1: BlockRenderLayerMap removed */ });
	}

	@MappedMethod
	public KeyBinding registerKeyBinding(String translationKey, int key, String categoryKey) {
		final KeyMapping.Category category = new KeyMapping.Category(Identifier.parse(categoryKey.contains(":") ? categoryKey : "mtr:" + categoryKey));
		return new KeyBinding(KeyMappingHelper.registerKeyMapping(new KeyMapping(translationKey, InputConstants.Type.KEYSYM, key, category)));
	}

	@MappedMethod
	public void registerBlockColors(Object blockColorProvider, BlockRegistryObject... blocks) {
		// TODO 26.1 block colors
	}

	@MappedMethod
	public void registerItemColors(Object itemColorProvider, ItemRegistryObject... items) {
		// TODO 26.1 item colors
	}

	@MappedMethod
	public void registerItemModelPredicate(ItemRegistryObject item, org.mtr.mapping.holder.Identifier identifier, ModelPredicateProvider modelPredicateProvider) {
		/* TODO 26.1 item model predicates */
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

	@FunctionalInterface
	public interface ModelPredicateProvider {
		@MappedMethod
		float call(ItemStack itemStack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity);
	}
}
