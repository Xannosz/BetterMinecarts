package hu.xannosz.betterminecarts.network;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
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
		context.enqueueWork(() ->
				// CLIENT SITE
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
						ClientPacketHandlerClass.handleBurnTimePacket(this, supplier))
		);
	}
}
