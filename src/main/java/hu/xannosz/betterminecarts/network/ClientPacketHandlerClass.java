package hu.xannosz.betterminecarts.network;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

public class ClientPacketHandlerClass {
	public static void handlePlaySoundPacket(PlaySoundPacket packet, CustomPayloadEvent.Context context) {
		Objects.requireNonNull(Minecraft.getInstance().level).playLocalSound(
				(double) packet.getPosition().getX() + 0.5D,
				(double) packet.getPosition().getY() + 0.5D,
				(double) packet.getPosition().getZ() + 0.5D,
				packet.getType().getWhistle(),
				SoundSource.BLOCKS, 5F, 5F, true);
	}
}
