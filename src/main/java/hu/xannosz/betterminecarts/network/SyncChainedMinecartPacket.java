package hu.xannosz.betterminecarts.network;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
public class SyncChainedMinecartPacket {
	private final int parentId;
	private final int childId;
	private final boolean parentExists;

	public SyncChainedMinecartPacket(AbstractMinecart parent, AbstractMinecart child) {
		parentExists = parent != null;
		if (parent != null) {
			this.parentId = parent.getId();
		} else {
			this.parentId = -1;
		}
		this.childId = child.getId();
	}

	public SyncChainedMinecartPacket(FriendlyByteBuf buf) {
		parentExists = buf.readBoolean();
		parentId = buf.readInt();
		childId = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeBoolean(parentId != -1);
		buf.writeInt(parentId);
		buf.writeInt(childId);
	}

	public void handler(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() ->
				// CLIENT SITE
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
						ClientPacketHandlerClass.handleSyncChainedMinecartPacket(this, supplier))
		);
	}
}
