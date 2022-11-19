package hu.xannosz.betterminecarts.network;

import hu.xannosz.betterminecarts.BetterMinecarts;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class PlaySoundPacket {
	private final BlockPos position;
	private final boolean isSteam;

	public PlaySoundPacket(BlockPos position, boolean isSteam) {
		this.position = position;
		this.isSteam = isSteam;
	}

	public PlaySoundPacket(FriendlyByteBuf buf) {
		position = buf.readBlockPos();
		isSteam = buf.readBoolean();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeBlockPos(position);
		buf.writeBoolean(isSteam);
	}

	public void handler(Supplier<NetworkEvent.Context> supplier) {
		NetworkEvent.Context context = supplier.get();
		context.enqueueWork(() -> {
			// CLIENT SITE
			Objects.requireNonNull(Minecraft.getInstance().level).playLocalSound(
					(double) position.getX() + 0.5D, (double) position.getY() + 0.5D, (double) position.getZ() + 0.5D,
					isSteam ? BetterMinecarts.STEAM_WHISTLE.get() : SoundEvents.BELL_BLOCK,
					SoundSource.BLOCKS, 5F, 5F, false);
		});
	}
}
