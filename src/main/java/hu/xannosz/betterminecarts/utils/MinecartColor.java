package hu.xannosz.betterminecarts.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

@Getter
@AllArgsConstructor
public enum MinecartColor {
	YELLOW(new Vec3(90.6,90.6,16.5),ChatFormatting.YELLOW,"yellow"),
	BROWN(new Vec3(38.8,17.3,1.6),ChatFormatting.DARK_RED,"brown"),
	GRAY(new Vec3(77.6,77.6,77.6),ChatFormatting.GRAY,"gray"),
	DARK_GRAY(new Vec3(54.5,54.5,54.5),ChatFormatting.DARK_GRAY,"dark_gray");

	private final Vec3 filter;
	private final ChatFormatting labelColor;
	private final String label;
}
