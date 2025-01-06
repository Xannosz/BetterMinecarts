package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public class ModMessages {
	public static SimpleChannel INSTANCE;

	public static void setupMessages(final FMLCommonSetupEvent event) {
		INSTANCE = ChannelBuilder
				.named(ResourceLocation.fromNamespaceAndPath(BetterMinecarts.MOD_ID, "messages"))
				.networkProtocolVersion(1)
				.clientAcceptedVersions((s, v) -> true)
				.serverAcceptedVersions((s, v) -> true)
				.simpleChannel();
		INSTANCE.messageBuilder(ButtonClickedPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
				.decoder(ButtonClickedPacket::new)
				.encoder(ButtonClickedPacket::toBytes)
				.consumerMainThread(ButtonClickedPacket::handler)
				.add();
		INSTANCE.messageBuilder(PlaySoundPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
				.decoder(PlaySoundPacket::new)
				.encoder(PlaySoundPacket::toBytes)
				.consumerMainThread(PlaySoundPacket::handler)
				.add();
		INSTANCE.messageBuilder(KeyPressedPacket.class, 2, NetworkDirection.PLAY_TO_SERVER)
				.decoder(KeyPressedPacket::new)
				.encoder(KeyPressedPacket::toBytes)
				.consumerMainThread(KeyPressedPacket::handler)
				.add();
	}
}
