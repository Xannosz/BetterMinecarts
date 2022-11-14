package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LampSetPacket {
	private final boolean lampStatus;
	private final int entityId;

	public LampSetPacket(boolean lampStatus, int entityId) {
		this.lampStatus = lampStatus;
		this.entityId = entityId;
	}

	public LampSetPacket(FriendlyByteBuf buf) {
		lampStatus = buf.readBoolean();
		entityId = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeBoolean(lampStatus);
		buf.writeInt(entityId);
	}

	public void handler(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			// CLIENT SITE
			ClientLevel world = Minecraft.getInstance().level;

			if (world == null) {
				return;
			}

			Entity entity = world.getEntity(entityId);
			if (entity instanceof AbstractLocomotive abstractLocomotive) {
				abstractLocomotive.setLampOn(lampStatus);
			}
		});
	}
}
