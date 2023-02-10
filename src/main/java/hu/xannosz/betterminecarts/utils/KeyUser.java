package hu.xannosz.betterminecarts.utils;

import net.minecraft.world.entity.player.Player;

public interface KeyUser {
	void executeKeyPress(KeyId keyId, Player player);
}
