package hu.xannosz.betterminecarts.network;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
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
		context.enqueueWork(() ->
				// CLIENT SITE
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
						ClientPacketHandlerClass.handleLampSetPacket(this, supplier))
		);
	}
}
