package hu.xannosz.betterminecarts;

import hu.xannosz.betterminecarts.client.models.DieselLocomotiveModel;
import hu.xannosz.betterminecarts.client.models.ElectricLocomotiveModel;
import hu.xannosz.betterminecarts.client.models.SteamLocomotiveModel;
import hu.xannosz.betterminecarts.client.renderer.LocomotiveRenderer;
import hu.xannosz.betterminecarts.config.BetterMinecartsConfig;
import hu.xannosz.betterminecarts.item.ModItems;
import hu.xannosz.betterminecarts.network.ModMessages;
import hu.xannosz.betterminecarts.screen.DieselLocomotiveScreen;
import hu.xannosz.betterminecarts.screen.ElectricLocomotiveScreen;
import hu.xannosz.betterminecarts.screen.SteamLocomotiveScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static hu.xannosz.betterminecarts.blockentity.ModBlockEntities.BLOCK_ENTITIES;
import static hu.xannosz.betterminecarts.blocks.ModBlocks.BLOCKS;
import static hu.xannosz.betterminecarts.component.ModComponentTypes.DATA_COMPONENT_TYPES;
import static hu.xannosz.betterminecarts.entity.ModEntities.*;
import static hu.xannosz.betterminecarts.item.ModItems.ITEMS;
import static hu.xannosz.betterminecarts.screen.ModMenus.*;

@Mod(BetterMinecarts.MOD_ID)
public class BetterMinecarts {
	public static final String MOD_ID = "betterminecarts";

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BetterMinecarts.MOD_ID);
	public static RegistryObject<SoundEvent> STEAM_WHISTLE = SOUND_EVENTS.register("steam_whistle",
			() -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath(BetterMinecarts.MOD_ID, "steam_whistle"),
					16f));
	public static RegistryObject<SoundEvent> DIESEL_WHISTLE = SOUND_EVENTS.register("diesel_whistle",
			() -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath(BetterMinecarts.MOD_ID, "diesel_whistle"),
					16f));

	public static final ResourceKey<DamageType> MINECART_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, "minecart"));

	public BetterMinecarts() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(ModMessages::setupMessages);
		BLOCKS.register(modEventBus);
		BLOCK_ENTITIES.register(modEventBus);
		ITEMS.register(modEventBus);
		ENTITIES.register(modEventBus);
		MENUS.register(modEventBus);
		SOUND_EVENTS.register(modEventBus);
		DATA_COMPONENT_TYPES.register(modEventBus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BetterMinecartsConfig.SPEC, MOD_ID + ".toml");

		MinecraftForge.EVENT_BUS.register(this);

		modEventBus.addListener(this::addCreative);
	}

	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS || event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(ModItems.CROWBAR);
			event.accept(ModItems.CRAFTING_MINECART_ITEM);
			event.accept(ModItems.ELECTRIC_LOCOMOTIVE);
			event.accept(ModItems.STEAM_LOCOMOTIVE);
			event.accept(ModItems.DIESEL_LOCOMOTIVE);
			event.accept(ModItems.BIO_DIESEL_FUEL);
			ModItems.BLOCK_ITEMS.forEach(event::accept);
		}
	}

	@SuppressWarnings("unused")
	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void entityRenderers(EntityRenderersEvent.RegisterRenderers event) {
			event.registerEntityRenderer(ELECTRIC_LOCOMOTIVE.get(), context ->
					new LocomotiveRenderer(context,
							new ElectricLocomotiveModel(context.bakeLayer(ElectricLocomotiveModel.LAYER_LOCATION))));
			event.registerEntityRenderer(STEAM_LOCOMOTIVE.get(), context ->
					new LocomotiveRenderer(context,
							new SteamLocomotiveModel(context.bakeLayer(SteamLocomotiveModel.LAYER_LOCATION))));
			event.registerEntityRenderer(DIESEL_LOCOMOTIVE.get(), context ->
					new LocomotiveRenderer(context,
							new DieselLocomotiveModel(context.bakeLayer(DieselLocomotiveModel.LAYER_LOCATION))));
			event.registerEntityRenderer(CRAFTING_MINECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.FURNACE_MINECART));
		}

		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void onClientSetup(FMLClientSetupEvent event) {
			MenuScreens.register(ELECTRIC_LOCOMOTIVE_MENU.get(), ElectricLocomotiveScreen::new);
			MenuScreens.register(STEAM_LOCOMOTIVE_MENU.get(), SteamLocomotiveScreen::new);
			MenuScreens.register(DIESEL_LOCOMOTIVE_MENU.get(), DieselLocomotiveScreen::new);
		}

		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(ElectricLocomotiveModel.LAYER_LOCATION,
					ElectricLocomotiveModel::createBodyLayer);
			event.registerLayerDefinition(SteamLocomotiveModel.LAYER_LOCATION,
					SteamLocomotiveModel::createBodyLayer);
			event.registerLayerDefinition(DieselLocomotiveModel.LAYER_LOCATION,
					DieselLocomotiveModel::createBodyLayer);
		}
	}
}
