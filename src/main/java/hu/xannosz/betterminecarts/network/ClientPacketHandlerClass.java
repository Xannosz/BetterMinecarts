package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.utils.Linkable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
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
				SoundSource.BLOCKS, 5F, 5F, false);
	}

	public static void handleSyncChainedMinecartPacket(SyncChainedMinecartPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ClientLevel world = Minecraft.getInstance().level;

		if (world == null) {
			return;
		}

		if (world.getEntity(msg.getChildId()) instanceof Linkable linkable) {
			if (msg.isParentExists() && world.getEntity(msg.getParentId()) instanceof AbstractMinecart parent)
				linkable.setLinkedParent(parent);
			else
				linkable.setLinkedParent(null);
			linkable.setUpdated();
		}
	}
}
