package hu.xannosz.betterminecarts;

import hu.xannosz.betterminecarts.blocks.CrossedRailBlock;
import hu.xannosz.betterminecarts.client.models.ElectricLocomotiveModel;
import hu.xannosz.betterminecarts.client.renderer.ElectricLocomotiveRenderer;
import hu.xannosz.betterminecarts.entity.ElectricLocomotive;
import hu.xannosz.betterminecarts.integration.MinecartTweaksConfig;
import hu.xannosz.betterminecarts.item.ElectricLocomotiveItem;
import hu.xannosz.betterminecarts.network.ButtonClickedPacket;
import hu.xannosz.betterminecarts.network.SyncChainedMinecartPacket;
import hu.xannosz.betterminecarts.screen.ElectricLocomotiveMenu;
import hu.xannosz.betterminecarts.screen.ElectricLocomotiveScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@Mod(BetterMinecarts.MOD_ID)
public class BetterMinecarts {
	public static final String MOD_ID = "betterminecarts";

	public static SimpleChannel INSTANCE;
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BetterMinecarts.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BetterMinecarts.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, BetterMinecarts.MOD_ID);
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, BetterMinecarts.MOD_ID);

	public static final RegistryObject<Block> CROSSED_RAIL = registerBlock("crossed_rail",
			CrossedRailBlock::new
	);

	public static final RegistryObject<Item> ELECTRIC_LOCOMOTIVE_ITEM = ITEMS.register("electric_locomotive_item",
			() -> new ElectricLocomotiveItem( new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));

	public static final RegistryObject<EntityType<ElectricLocomotive>> ELECTRIC_LOCOMOTIVE = ENTITIES.register("electric_locomotive",
			() -> EntityType.Builder.<ElectricLocomotive>of(ElectricLocomotive::new, MobCategory.MISC).sized(1.0f, 1.0f).build(BetterMinecarts.MOD_ID + ":electric_locomotive"));

	public static final RegistryObject<MenuType<ElectricLocomotiveMenu>> ELECTRIC_LOCOMOTIVE_MENU =
			registerMenuType(ElectricLocomotiveMenu::new, "rune_menu");

	public BetterMinecarts() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setupMessages);
		BLOCKS.register(modEventBus);
		ITEMS.register(modEventBus);
		ENTITIES.register(modEventBus);
		MENUS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static MinecartTweaksConfig getConfig() {
		return new MinecartTweaksConfig();
	}

	public static DamageSource minecart(Entity entity) {
		return new EntityDamageSource(MOD_ID + ".minecart", entity);
	}

	private void setupMessages(final FMLCommonSetupEvent event) {
		INSTANCE = NetworkRegistry.ChannelBuilder
				.named(new ResourceLocation(BetterMinecarts.MOD_ID, "messages"))
				.networkProtocolVersion(() -> "1.0")
				.clientAcceptedVersions(s -> true)
				.serverAcceptedVersions(s -> true)
				.simpleChannel();
		INSTANCE.messageBuilder(SyncChainedMinecartPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
				.decoder(SyncChainedMinecartPacket::new)
				.encoder(SyncChainedMinecartPacket::toBytes)
				.consumerMainThread(SyncChainedMinecartPacket::handler)
				.add();
		INSTANCE.messageBuilder(ButtonClickedPacket.class, 1, NetworkDirection.PLAY_TO_SERVER)
				.decoder(ButtonClickedPacket::new)
				.encoder(ButtonClickedPacket::toBytes)
				.consumerMainThread(ButtonClickedPacket::handler)
				.add();
	}

	private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> blockCreator) {
		RegistryObject<T> block = BLOCKS.register(name, blockCreator);
		registerBlockItem(name, block);
		return block;
	}

	private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block) {
		ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));
	}

	private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
																								  String name) {
		return MENUS.register(name, () -> IForgeMenuType.create(factory));
	}

	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(ELECTRIC_LOCOMOTIVE.get(), ElectricLocomotiveRenderer::new);
		}

		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
			MenuScreens.register(ELECTRIC_LOCOMOTIVE_MENU.get(), ElectricLocomotiveScreen::new);
		}

		@SubscribeEvent
		public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(ElectricLocomotiveModel.LAYER_LOCATION,
					ElectricLocomotiveModel::createBodyLayer);
		}
	}
}
