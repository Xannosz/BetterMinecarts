package hu.xannosz.betterminecarts;

import hu.xannosz.betterminecarts.client.models.ElectricLocomotiveModel;
import hu.xannosz.betterminecarts.client.models.SteamLocomotiveModel;
import hu.xannosz.betterminecarts.client.renderer.LocomotiveRenderer;
import hu.xannosz.betterminecarts.config.BetterMinecartsConfig;
import hu.xannosz.betterminecarts.network.ModMessages;
import hu.xannosz.betterminecarts.screen.ElectricLocomotiveScreen;
import hu.xannosz.betterminecarts.screen.SteamLocomotiveScreen;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
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
import static hu.xannosz.betterminecarts.entity.ModEntities.*;
import static hu.xannosz.betterminecarts.item.ModItems.ITEMS;
import static hu.xannosz.betterminecarts.screen.ModMenus.*;

@Mod(BetterMinecarts.MOD_ID)
public class BetterMinecarts {
	public static final String MOD_ID = "betterminecarts";

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, BetterMinecarts.MOD_ID);
	public static RegistryObject<SoundEvent> STEAM_WHISTLE = SOUND_EVENTS.register("steam_whistle",
			() -> new SoundEvent(new ResourceLocation(BetterMinecarts.MOD_ID, "steam_whistle")));

	public BetterMinecarts() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(ModMessages::setupMessages);
		BLOCKS.register(modEventBus);
		BLOCK_ENTITIES.register(modEventBus);
		ITEMS.register(modEventBus);
		ENTITIES.register(modEventBus);
		MENUS.register(modEventBus);
		SOUND_EVENTS.register(modEventBus);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BetterMinecartsConfig.SPEC, MOD_ID + ".toml");

		MinecraftForge.EVENT_BUS.register(this);
	}

	public static DamageSource minecart(Entity entity) {
		return new EntityDamageSource(MOD_ID + ".minecart", entity);
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
			event.registerEntityRenderer(CRAFTING_MINECART.get(), context -> new MinecartRenderer<>(context, ModelLayers.FURNACE_MINECART));
		}

		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void onClientSetup(FMLClientSetupEvent event) {
			MenuScreens.register(ELECTRIC_LOCOMOTIVE_MENU.get(), ElectricLocomotiveScreen::new);
			MenuScreens.register(STEAM_LOCOMOTIVE_MENU.get(), SteamLocomotiveScreen::new);
		}

		@SubscribeEvent
		@SuppressWarnings("unused")
		public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
			event.registerLayerDefinition(ElectricLocomotiveModel.LAYER_LOCATION,
					ElectricLocomotiveModel::createBodyLayer);
			event.registerLayerDefinition(SteamLocomotiveModel.LAYER_LOCATION,
					SteamLocomotiveModel::createBodyLayer);
		}
	}

	public static String generateNameFromData(MinecartColor topColor, MinecartColor bottomColor, boolean isSteam) {
		if (isSteam) {
			return "steam_locomotive_item_" + topColor.getLabel() + "_" + bottomColor.getLabel();
		} else {
			return "electric_locomotive_item_" + topColor.getLabel() + "_" + bottomColor.getLabel();
		}
	}
}
