package hu.xannosz.betterminecarts.event;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.network.KeyPressedPacket;
import hu.xannosz.betterminecarts.network.ModMessages;
import hu.xannosz.betterminecarts.utils.KeyBinding;
import hu.xannosz.betterminecarts.utils.KeyId;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ClientEvents {
	@Mod.EventBusSubscriber(modid = BetterMinecarts.MOD_ID, value = Dist.CLIENT)
	public static class ClientForgeEvents {
		@SubscribeEvent
		public static void onKeyInput(InputEvent.Key event) {
			if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.isPassenger() &&
					Minecraft.getInstance().player.getRootVehicle() instanceof AbstractMinecart minecart) {
				if (KeyBinding.INCREASE_KEY.consumeClick()) {
					ModMessages.INSTANCE.sendToServer(new KeyPressedPacket(KeyId.INCREASE, minecart.getId()));
				}
				if (KeyBinding.DECREASE_KEY.consumeClick()) {
					ModMessages.INSTANCE.sendToServer(new KeyPressedPacket(KeyId.DECREASE, minecart.getId()));
				}
				if (KeyBinding.LAMP_KEY.consumeClick()) {
					ModMessages.INSTANCE.sendToServer(new KeyPressedPacket(KeyId.LAMP, minecart.getId()));
				}
				if (KeyBinding.WHISTLE_KEY.consumeClick()) {
					ModMessages.INSTANCE.sendToServer(new KeyPressedPacket(KeyId.WHISTLE, minecart.getId()));
				}
				if (KeyBinding.REDSTONE_KEY.consumeClick()) {
					ModMessages.INSTANCE.sendToServer(new KeyPressedPacket(KeyId.REDSTONE, minecart.getId()));
				}
				if (KeyBinding.DATA_KEY.consumeClick()) {
					ModMessages.INSTANCE.sendToServer(new KeyPressedPacket(KeyId.DATA, minecart.getId()));
				}
				return;
			}
			KeyBinding.INCREASE_KEY.consumeClick();
			KeyBinding.DECREASE_KEY.consumeClick();
			KeyBinding.LAMP_KEY.consumeClick();
			KeyBinding.WHISTLE_KEY.consumeClick();
			KeyBinding.REDSTONE_KEY.consumeClick();
			KeyBinding.DATA_KEY.consumeClick();
		}
	}

	@Mod.EventBusSubscriber(modid = BetterMinecarts.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ClientModBusEvents {
		@SubscribeEvent
		public static void onKeyRegister(RegisterKeyMappingsEvent event) {
			event.register(KeyBinding.INCREASE_KEY);
			event.register(KeyBinding.DECREASE_KEY);
			event.register(KeyBinding.LAMP_KEY);
			event.register(KeyBinding.WHISTLE_KEY);
			event.register(KeyBinding.REDSTONE_KEY);
			event.register(KeyBinding.DATA_KEY);
		}
	}
}
