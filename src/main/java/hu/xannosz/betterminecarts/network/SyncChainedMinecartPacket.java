package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.utils.Linkable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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
		context.enqueueWork(() -> {
			// CLIENT SITE
			ClientLevel world = Minecraft.getInstance().level;

			if (world == null) {
				return;
			}

			if (world.getEntity(childId) instanceof Linkable linkable) {
				if (parentExists && world.getEntity(parentId) instanceof AbstractMinecart parent)
					linkable.setLinkedParent(parent);
				else
					linkable.setLinkedParent(null);
			}
		});
	}
}
