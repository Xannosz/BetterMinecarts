package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.config.BetterMinecartsConfig;
import hu.xannosz.betterminecarts.utils.KeyId;
import hu.xannosz.betterminecarts.utils.KeyUser;
import hu.xannosz.betterminecarts.utils.Linkable;
import hu.xannosz.betterminecarts.utils.TrainUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Objects;

public class KeyPressedPacket {
	private final KeyId keyId;
	private final int entityId;

	public KeyPressedPacket(KeyId keyId, int entityId) {
		this.keyId = keyId;
		this.entityId = entityId;
	}

	public KeyPressedPacket(FriendlyByteBuf buf) {
		keyId = buf.readEnum(KeyId.class);
		entityId = buf.readInt();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeEnum(keyId);
		buf.writeInt(entityId);
	}

	public static void handler(KeyPressedPacket packet, CustomPayloadEvent.Context context) {
		context.enqueueWork(() -> {
			// SERVER SITE
			Entity entity = Objects.requireNonNull(context.getSender()).level().getEntity(packet.entityId);
			if (entity instanceof Linkable linkable) {
				Linkable head;
				if (BetterMinecartsConfig.KEY_CONTROL_FROM_THE_WHOLE_TRAIN.get()) {
					head = TrainUtil.getHeadOfTrain(linkable);
				} else if (linkable.getLinkedParent() == null) {
					head = linkable;
				} else {
					head = (Linkable) linkable.getLinkedParent();
				}
				if (head instanceof KeyUser) {
					((KeyUser) head).executeKeyPress(packet.keyId, context.getSender());
				}
			}
		});
	}
}
