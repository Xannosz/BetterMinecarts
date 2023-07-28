package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.BetterMinecarts;
import hu.xannosz.betterminecarts.utils.MinecartColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

@Getter
@RequiredArgsConstructor
public enum LocomotiveType {
	ELECTRIC("electric_locomotive",MinecartColor.YELLOW,MinecartColor.BROWN, SoundEvents.BELL_BLOCK),
	STEAM("steam_locomotive",MinecartColor.LIGHT_GRAY,MinecartColor.GRAY, BetterMinecarts.STEAM_WHISTLE.get()),
	DIESEL("diesel_locomotive",MinecartColor.CYAN,MinecartColor.RED,BetterMinecarts.DIESEL_WHISTLE.get());

	private final String name;
	private final MinecartColor topColor;
	private final MinecartColor bottomColor;
	private final SoundEvent whistle;
}
