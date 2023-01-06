package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import hu.xannosz.betterminecarts.entity.SteamLocomotive;
import hu.xannosz.betterminecarts.utils.Linkable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ClientPacketHandlerClass {
	public static void handleColorPacket(ColorPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ClientLevel world = Minecraft.getInstance().level;

		if (world == null) {
			return;
		}

		Entity entity = world.getEntity(msg.getEntityId());
		if (entity instanceof AbstractLocomotive abstractLocomotive) {
			abstractLocomotive.setTopFilter(msg.getTop());
			abstractLocomotive.setBottomFilter(msg.getBottom());
			abstractLocomotive.setFilterUpdateDone(true);
		}
	}

	public static void handlePlaySoundPacket(PlaySoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
		Objects.requireNonNull(Minecraft.getInstance().level).playLocalSound(
				(double) msg.getPosition().getX() + 0.5D,
				(double) msg.getPosition().getY() + 0.5D,
				(double) msg.getPosition().getZ() + 0.5D,
				msg.isSteam() ? BetterMinecarts.STEAM_WHISTLE.get() : SoundEvents.BELL_BLOCK,
				SoundSource.BLOCKS, 5F, 5F, false);
	}

	public static void handleBurnTimePacket(BurnTimePacket msg, Supplier<NetworkEvent.Context> ctx) {
		ClientLevel world = Minecraft.getInstance().level;

		if (world == null) {
			return;
		}

		Entity entity = world.getEntity(msg.getEntityId());
		if (entity instanceof SteamLocomotive steamLocomotive) {
			steamLocomotive.setBurn(msg.isBurnTime());
		}
	}

	public static void handleLampSetPacket(LampSetPacket msg, Supplier<NetworkEvent.Context> ctx) {
		ClientLevel world = Minecraft.getInstance().level;

		if (world == null) {
			return;
		}

		Entity entity = world.getEntity(msg.getEntityId());
		if (entity instanceof AbstractLocomotive abstractLocomotive) {
			abstractLocomotive.setLampOn(msg.isLampStatus());
		}
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
