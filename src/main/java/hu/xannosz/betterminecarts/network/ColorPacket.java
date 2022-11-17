package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.entity.AbstractLocomotive;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ColorPacket {
	private final MinecartColor top;
	private final MinecartColor bottom;
	private final int entityId;

	public ColorPacket(MinecartColor top, MinecartColor bottom, int entityId) {
		this.top = top;
		this.bottom = bottom;
		this.entityId = entityId;
	}

	public ColorPacket(FriendlyByteBuf buf) {
		top = MinecartColor.getFromLabel(buf.readUtf());
		bottom = MinecartColor.getFromLabel(buf.readUtf());
		entityId = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeUtf(top.getLabel());
		buf.writeUtf(bottom.getLabel());
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
				abstractLocomotive.setTopFilter(top);
				abstractLocomotive.setBottomFilter(bottom);
				abstractLocomotive.setFilterUpdateDone(true);
			}
		});
	}
}
