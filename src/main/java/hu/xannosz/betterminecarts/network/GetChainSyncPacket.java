package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.utils.Linkable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class GetChainSyncPacket {
	private final int entityId;

	public GetChainSyncPacket(int entityId) {
		this.entityId = entityId;
	}

	public GetChainSyncPacket(FriendlyByteBuf buf) {
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
			if (entity instanceof Linkable linkable) {
				linkable.updateChains();
			}
		});
	}
}
