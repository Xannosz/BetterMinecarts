package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.entity.SteamLocomotive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BurnTimePacket {
	private final boolean burnTime;
	private final int entityId;

	public BurnTimePacket(boolean burnTime, int entityId) {
		this.burnTime = burnTime;
		this.entityId = entityId;
	}

	public BurnTimePacket(FriendlyByteBuf buf) {
		burnTime = buf.readBoolean();
		entityId = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeBoolean(burnTime);
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
			if (entity instanceof SteamLocomotive steamLocomotive) {
				steamLocomotive.setBurn(burnTime);
			}
		});
	}
}
