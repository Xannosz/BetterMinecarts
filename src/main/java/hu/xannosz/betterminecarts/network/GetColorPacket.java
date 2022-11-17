package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Objects;
import java.util.function.Supplier;

public class GetColorPacket {
	private final int entityId;

	public GetColorPacket(int entityId) {
		this.entityId = entityId;
	}

	public GetColorPacket(FriendlyByteBuf buf) {
		entityId = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(entityId);
	}

	public void handler(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			// SERVER SITE
			Entity entity = Objects.requireNonNull(context.getSender()).getLevel().getEntity(entityId);
			if (entity instanceof AbstractLocomotive abstractLocomotive) {
				BetterMinecarts.INSTANCE.send(PacketDistributor.PLAYER.with(context::getSender),
						new ColorPacket(abstractLocomotive.getTopFilter(), abstractLocomotive.getBottomFilter(), abstractLocomotive.getId()));
			}
		});
	}
}
