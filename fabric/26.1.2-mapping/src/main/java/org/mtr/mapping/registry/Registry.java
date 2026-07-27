package org.mtr.mapping.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import org.mtr.mapping.annotation.MappedMethod;
import org.mtr.mapping.holder.*;
import org.mtr.mapping.mapper.BlockEntityExtension;
import org.mtr.mapping.mapper.BlockItemExtension;
import org.mtr.mapping.mapper.EntityExtension;
import org.mtr.mapping.tool.DummyClass;
import org.mtr.mapping.tool.HolderBase;
import org.mtr.mapping.tool.PacketBufferReceiver;
import org.mtr.mapping.tool.PacketBufferSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Registry extends DummyClass {

	Identifier packetsIdentifier;
	CustomPacketPayload.Type<MtrPayload> payloadType;
	final Map<String, Function<PacketBufferReceiver, ? extends PacketHandler>> packets = new HashMap<>();
	public final EventRegistry eventRegistry = new EventRegistry();
	private final List<Runnable> objectsToRegister = new ArrayList<>();
	private final List<Consumer<CommandDispatcher<CommandSourceStack>>> commandsToRegister = new ArrayList<>();
	private boolean payloadRegistered;

	@MappedMethod
	public void init() {
		objectsToRegister.forEach(Runnable::run);
		CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, environment) -> commandsToRegister.forEach(consumer -> consumer.accept(dispatcher)));
	}

	@MappedMethod
	public BlockRegistryObject registerBlock(Identifier identifier, Supplier<Block> supplier) {
		final Block block = supplier.get();
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, identifier.data, block.data));
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, CreativeModeTabHolder... creativeModeTabHolders) {
		return registerBlockWithBlockItem(identifier, supplier, BlockItemExtension::new, creativeModeTabHolders);
	}

	@MappedMethod
	public BlockRegistryObject registerBlockWithBlockItem(Identifier identifier, Supplier<Block> supplier, BiFunction<Block, ItemSettings, BlockItemExtension> function, CreativeModeTabHolder... creativeModeTabHolders) {
		final Block block = supplier.get();
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, identifier.data, block.data));
		final BlockItemExtension blockItemExtension = function.apply(block, new ItemSettings());
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, identifier.data, blockItemExtension));
		for (final CreativeModeTabHolder creativeModeTabHolder : creativeModeTabHolders) {
			// TODO 26.1 creative tab entries
		}
		return new BlockRegistryObject(block);
	}

	@MappedMethod
	public ItemRegistryObject registerItem(Identifier identifier, Function<ItemSettings, Item> function, CreativeModeTabHolder... creativeModeTabHolders) {
		final Item item = function.apply(new ItemSettings());
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, identifier.data, item.data));
		for (final CreativeModeTabHolder creativeModeTabHolder : creativeModeTabHolders) {
			// TODO 26.1 creative tab entries
		}
		return new ItemRegistryObject(item);
	}

	@MappedMethod
	@SafeVarargs
	public final <T extends BlockEntityExtension> BlockEntityTypeRegistryObject<T> registerBlockEntityType(Identifier identifier, BiFunction<BlockPos, BlockState, T> function, Supplier<Block>... blockSuppliers) {
		final net.minecraft.world.level.block.entity.BlockEntityType<T> blockEntityType = FabricBlockEntityTypeBuilder.create((pos, state) -> function.apply(new BlockPos(pos), new BlockState(state)), HolderBase.convertArray(blockSuppliers, net.minecraft.world.level.block.Block[]::new)).build();
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, identifier.data, blockEntityType));
		return new BlockEntityTypeRegistryObject<>(new BlockEntityType<>(blockEntityType));
	}

	@MappedMethod
	public <T extends EntityExtension> EntityTypeRegistryObject<T> registerEntityType(Identifier identifier, BiFunction<EntityType<?>, World, T> function, float width, float height) {
		final net.minecraft.world.entity.EntityType<T> entityType = net.minecraft.world.entity.EntityType.Builder.<T>of(getEntityFactory(function), MobCategory.MISC).sized(width, height).build(net.minecraft.resources.ResourceKey.create(Registries.ENTITY_TYPE, identifier.data));
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.ENTITY_TYPE, identifier.data, entityType));
		return new EntityTypeRegistryObject<>(new EntityType<>(entityType));
	}

	@SuppressWarnings("unchecked")
	private <T extends EntityExtension> net.minecraft.world.entity.EntityType.EntityFactory<T> getEntityFactory(BiFunction<EntityType<?>, World, T> function) {
		return (entityType, world) -> function.apply(new EntityType<>(entityType), new World(world));
	}

	@MappedMethod
	public ParticleTypeRegistryObject registerParticleType(Identifier identifier) {
		return registerParticleType(identifier, false);
	}

	@MappedMethod
	public ParticleTypeRegistryObject registerParticleType(Identifier identifier, boolean alwaysSpawn) {
		final DefaultParticleType defaultParticleType = new DefaultParticleType(FabricParticleTypes.simple(alwaysSpawn));
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier.data, defaultParticleType.data));
		return new ParticleTypeRegistryObject(defaultParticleType, identifier);
	}

	@MappedMethod
	public CreativeModeTabHolder createCreativeModeTabHolder(Identifier identifier, Supplier<ItemStack> iconSupplier) {
		final CreativeModeTab itemGroup = FabricCreativeModeTab.builder().icon(() -> iconSupplier.get().data).title(Component.translatable(String.format("itemGroup.%s.%s", identifier.data.getNamespace(), identifier.data.getPath()))).build();
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, identifier.data, itemGroup));
		return new CreativeModeTabHolder(new ItemGroup(itemGroup), identifier);
	}

	@MappedMethod
	public SoundEventRegistryObject registerSoundEvent(Identifier identifier) {
		final net.minecraft.sounds.SoundEvent soundEvent = net.minecraft.sounds.SoundEvent.createVariableRangeEvent(identifier.data);
		objectsToRegister.add(() -> net.minecraft.core.Registry.register(BuiltInRegistries.SOUND_EVENT, identifier.data, soundEvent));
		return new SoundEventRegistryObject(new SoundEvent(soundEvent));
	}

	@MappedMethod
	public void registerCommand(String command, Consumer<CommandBuilder<?>> buildCommand, String... redirects) {
		commandsToRegister.add(dispatcher -> {
			final CommandBuilder<LiteralArgumentBuilder<CommandSourceStack>> commandBuilder = new CommandBuilder<>(Commands.literal(command));
			buildCommand.accept(commandBuilder);
			final LiteralCommandNode<CommandSourceStack> literalCommandNode = dispatcher.register(commandBuilder.argumentBuilder);
			for (final String redirect : redirects) {
				dispatcher.register(Commands.literal(redirect).redirect(literalCommandNode));
			}
		});
	}

	@MappedMethod
	public void setupPackets(Identifier identifier) {
		packetsIdentifier = identifier;
		payloadType = new CustomPacketPayload.Type<>(identifier.data);
		if (!payloadRegistered) {
			final StreamCodec<RegistryFriendlyByteBuf, MtrPayload> codec = StreamCodec.of(
					(buf, payload) -> buf.writeBytes(payload.bytes()),
					buf -> {
						final byte[] bytes = new byte[buf.readableBytes()];
						buf.readBytes(bytes);
						return new MtrPayload(payloadType, bytes);
					}
			);
			PayloadTypeRegistry.serverboundPlay().register(payloadType, codec);
			PayloadTypeRegistry.clientboundPlay().register(payloadType, codec);
			payloadRegistered = true;
		}
		ServerPlayNetworking.registerGlobalReceiver(payloadType, (payload, context) -> {
			final net.minecraft.network.FriendlyByteBuf buf = new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.wrappedBuffer(payload.bytes()));
			PacketBufferReceiver.receive(buf, packetBufferReceiver -> {
				final Function<PacketBufferReceiver, ? extends PacketHandler> getInstance = packets.get(packetBufferReceiver.readString());
				if (getInstance != null) {
					getInstance.apply(packetBufferReceiver).runServer(new MinecraftServer(context.server()), new ServerPlayerEntity(context.player()));
				}
			}, context.server()::execute);
		});
	}

	@MappedMethod
	public <T extends PacketHandler> void registerPacket(Class<T> classObject, Function<PacketBufferReceiver, T> getInstance) {
		packets.put(classObject.getName(), getInstance);
	}

	@MappedMethod
	public <T extends PacketHandler> void sendPacketToClient(ServerPlayerEntity serverPlayerEntity, T data) {
		if (packetsIdentifier != null && payloadType != null) {
			final PacketBufferSender packetBufferSender = new PacketBufferSender(() -> new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer()));
			packetBufferSender.writeString(data.getClass().getName());
			data.write(packetBufferSender);
			packetBufferSender.send(byteBuf -> {
				final byte[] bytes = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(bytes);
				ServerPlayNetworking.send(serverPlayerEntity.data, new MtrPayload(payloadType, bytes));
			}, serverPlayerEntity.data.level().getServer()::execute);
		}
	}

	public static final class MtrPayload implements CustomPacketPayload {
		private final CustomPacketPayload.Type<MtrPayload> type;
		private final byte[] bytes;
		public MtrPayload(CustomPacketPayload.Type<MtrPayload> type, byte[] bytes) {
			this.type = type;
			this.bytes = bytes;
		}
		public byte[] bytes() { return bytes; }
		@Override
		public Type<? extends CustomPacketPayload> type() { return type; }
	}
}
