package hu.xannosz.betterminecarts.entity;

import hu.xannosz.betterminecarts.utils.MinecartColor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LocomotiveType {
	ELECTRIC("electric_locomotive",MinecartColor.YELLOW,MinecartColor.BROWN),
	STEAM("steam_locomotive",MinecartColor.LIGHT_GRAY,MinecartColor.GRAY),
	DIESEL("diesel_locomotive",MinecartColor.CYAN,MinecartColor.RED);

	private final String name;
	private final MinecartColor topColor;
	private final MinecartColor bottomColor;
}
