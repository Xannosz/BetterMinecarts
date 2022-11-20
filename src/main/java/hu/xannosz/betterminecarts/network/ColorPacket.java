package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.utils.MinecartColor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
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
		context.enqueueWork(() ->
				// CLIENT SITE
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
						ClientPacketHandlerClass.handleColorPacket(this, supplier))
		);
	}
}
