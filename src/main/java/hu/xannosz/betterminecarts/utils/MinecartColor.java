package hu.xannosz.betterminecarts.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

@Getter
@AllArgsConstructor
public enum MinecartColor {
	YELLOW(new Vec3(90.6,90.6,16.5),ChatFormatting.YELLOW,"yellow"),
	BROWN(new Vec3(38.8,17.3,1.6),ChatFormatting.DARK_RED,"brown");

	private final Vec3 filter;
	private final ChatFormatting labelColor;
	private final String label;
}
