package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.utils.ButtonId;
import hu.xannosz.betterminecarts.utils.ButtonUser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ButtonClickedPacket {

	private final ButtonId buttonId;
	private final int entityId;

	public ButtonClickedPacket(ButtonId buttonId, int entityId) {
		this.buttonId = buttonId;
		this.entityId = entityId;
	}

	public ButtonClickedPacket(FriendlyByteBuf buf) {
		buttonId = buf.readEnum(ButtonId.class);
		entityId = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeEnum(buttonId);
		buf.writeInt(entityId);
	}

	public void handler(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			// SERVER SITE
			Entity entity = Objects.requireNonNull(context.getSender()).level().getEntity(entityId);
			if (entity instanceof ButtonUser) {
				((ButtonUser) entity).executeButtonClick(buttonId);
			}
		});
	}
}
