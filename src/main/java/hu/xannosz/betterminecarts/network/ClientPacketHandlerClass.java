package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ClientPacketHandlerClass {
	public static void handlePlaySoundPacket(PlaySoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
		Objects.requireNonNull(Minecraft.getInstance().level).playLocalSound(
				(double) msg.getPosition().getX() + 0.5D,
				(double) msg.getPosition().getY() + 0.5D,
				(double) msg.getPosition().getZ() + 0.5D,
				msg.isSteam() ? BetterMinecarts.STEAM_WHISTLE.get() : SoundEvents.BELL_BLOCK,
				SoundSource.BLOCKS, 5F, 5F, true);
	}
}
