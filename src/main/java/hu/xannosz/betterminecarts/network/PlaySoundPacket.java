package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.entity.LocomotiveType;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

@Getter
public class PlaySoundPacket {
	private final BlockPos position;
	private final LocomotiveType type;

	public PlaySoundPacket(BlockPos position, LocomotiveType type) {
		this.position = position;
		this.type = type;
	}

	public PlaySoundPacket(FriendlyByteBuf buf) {
		position = buf.readBlockPos();
		type = buf.readEnum(LocomotiveType.class);
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeBlockPos(position);
		buf.writeEnum(type);
	}

	public static void handler(PlaySoundPacket packet, CustomPayloadEvent.Context context) {
		context.enqueueWork(() ->
				// CLIENT SITE
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
						ClientPacketHandlerClass.handlePlaySoundPacket(packet, context))
		);
	}
}
